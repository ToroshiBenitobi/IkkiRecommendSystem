package com.tqz.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.tqz.business.model.domain.Offer;
import com.tqz.business.model.domain.Product;
import com.tqz.business.model.domain.Rating;
import com.tqz.business.model.domain.RatingInfo;
import com.tqz.business.model.recom.History;
import com.tqz.business.model.recom.Recommendation;
import com.tqz.business.model.request.AddBrowsingHistoryRequest;
import com.tqz.business.model.request.BrowsingHistoryRequest;
import com.tqz.business.model.request.GetRatingsRequest;
import com.tqz.business.utils.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.collection.script.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

@Service
public class ProductService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RatingService ratingService;

    private MongoCollection<Document> productCollection;
    private MongoCollection<Document> averageProductsScoreCollection;
    private MongoCollection<Document> browsingHistoryCollection;
    private MongoCollection<Document> offerCollection;
    private MongoCollection<Document> ratingCollection;

    private MongoCollection<Document> getRatingCollection() {
        if (null == ratingCollection)
            ratingCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return ratingCollection;
    }


    private MongoCollection<Document> getProductCollection() {
        if (null == productCollection)
            productCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_PRODUCT_COLLECTION);
        return productCollection;
    }

    private MongoCollection<Document> getAverageProductsScoreCollection() {
        if (null == averageProductsScoreCollection)
            averageProductsScoreCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_AVERAGE_PRODUCTS_SCORE_COLLECTION);
        return averageProductsScoreCollection;
    }

    private MongoCollection<Document> getBrowsingHistoryCollection() {
        if (null == browsingHistoryCollection)
            browsingHistoryCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.HISTORY_PRODUCT_COLLECTION);
        return browsingHistoryCollection;
    }

    private MongoCollection<Document> getOfferCollection() {
        if (null == offerCollection)
            offerCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_OFFER_COLLECTION);
        return offerCollection;
    }

    public List<Product> getRecommendProducts(List<Recommendation> recommendations) {
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec : recommendations) {
            ids.add(rec.getProductId());
        }
        return getProducts(ids);
    }

    private List<Product> getProducts(List<Integer> productIds) {
        FindIterable<Document> documents = getProductCollection().find(Filters.in("productId", productIds));
        List<Product> products = new ArrayList<>();
        for (Document document : documents) {
            products.add(documentToProduct(document));
        }
        return products;
    }

    private Product documentToProduct(Document document) {
        Product product = null;
        try {
            product = objectMapper.readValue(JSON.serialize(document), Product.class);
            Document score = getAverageProductsScoreCollection().find(eq("productId", product.getProductId())).first();
            if (null == score || score.isEmpty())
                product.setScore(0D);
            else
                product.setScore((Double) score.get("avg", 0D));

            FindIterable<Document> offers = getOfferCollection().find(Filters.and(eq("productId", product.getProductId()), eq("status", 0)));
            Double lowestPrice = product.getOriginalPrice();
            int offersNum = 0;
            if (null != offers) {
                for (Document offer : offers) {
                    lowestPrice = Math.min(lowestPrice, (Double) offer.get("price"));
                    ++offersNum;
                }
            }
            product.setLowestPrice(lowestPrice);
            product.setOffersNum(offersNum);

            RatingInfo ratingInfo = getRatingInfoByProductId(product.getProductId());
            ratingInfo.setAverageScore(product.getScore());
            product.setRatingInfo(ratingInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return product;
    }

    private List<History> documentToHistoryList(List<Document> documents) {
        List<History> historyList = new ArrayList<>();
        History history = null;
        try {
            for (Document document : documents) {
                history = objectMapper.readValue(JSON.serialize(document), History.class);
            }
            historyList.add(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    public Product findByProductId(int productId) {
        Document document = getProductCollection().find(new Document("productId", productId)).first();
        if (document == null || document.isEmpty())
            return null;
        return documentToProduct(document);
    }

    public List<Product> findByProductName(String name) {
//        FindIterable<Document> documents = getProductCollection().find(new Document("name", name));
        FindIterable<Document> documents = getProductCollection().find(Filters.regex("name", name));
        List<Product> products = new ArrayList<>();
        for (Document document : documents) {
            products.add(documentToProduct(document));
        }
        return products;
    }

    public List<Product> getBrowsingHistory(BrowsingHistoryRequest browsingHistoryRequest) {
        Document document = getBrowsingHistoryCollection().find(eq("userId", browsingHistoryRequest.getUserId())).first();
        List<Document> historyArrayList = (List<Document>) document.get("history");
        if (null == historyArrayList) {
            return null;
        }
        int num = Math.min(historyArrayList.size(), browsingHistoryRequest.getNumber());
        historyArrayList = historyArrayList.subList(0, num);
        List<Integer> products = new ArrayList<>();
        for (Document history : historyArrayList) {
            products.add((Integer) history.get("productId"));
        }
        return getProducts(products);
    }

    public Boolean addBrowsingHistory(AddBrowsingHistoryRequest request) {
        Document document = getBrowsingHistoryCollection().find(eq("userId", request.getUserId())).first();
        List<Document> historyList = new ArrayList<>();
        if (null == document) {
            document = new Document();
            document.append("userId", request.getUserId());
            document.append("history", historyList);
            getBrowsingHistoryCollection().insertOne(document);
        } else {
            historyList = (List<Document>) document.get("history");
//            historyList = documentToHistoryList((List<Document>) document.get("history"));
        }
//        History history = new History(request.getProductId(), System.currentTimeMillis());
        Document history = new Document();
        history.append("productId", request.getProductId()).append("timestamp", System.currentTimeMillis());

        int i;
        for (i = 0; i < historyList.size(); i++) {
            if ((int) historyList.get(i).get("productId") == request.getProductId()) {
                historyList.remove(i);
                break;
            }
        }


        historyList.add(history);

        try {
            getBrowsingHistoryCollection().updateOne(
                    eq("userId", request.getUserId()),
                    (Updates.set("history", historyList)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public RatingInfo getRatingInfoByProductId(int productId) {
        List<Rating> ratings = ratingService.findRatingsByProductId(new GetRatingsRequest(productId));
        RatingInfo ratingInfo = new RatingInfo();
        ratingInfo.setProductId(productId);
        int ratingSum = ratings.size();
        Integer[] ratingStarSum = {0, 0, 0, 0, 0};
        for (Rating rating : ratings) {
            int star = (int) rating.getScore() - 1;
            ++ratingStarSum[star];
        }
        ratingInfo.setRatingSum(ratingSum);
        ratingInfo.setRatingStarSum(ratingStarSum);
        double averageScore = (ratingSum == 0) ? 0 : (ratingStarSum[0] + 2 * ratingStarSum[1] +
                3 * ratingStarSum[2] + 4 * ratingStarSum[3] +
                5 * ratingStarSum[4]) / ratingSum * 1.0;
        ratingInfo.setAverageScore(averageScore);
        return ratingInfo;
    }

}
