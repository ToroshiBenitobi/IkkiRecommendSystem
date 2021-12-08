package com.tqz.business.service;

import com.tqz.business.model.domain.Rating;
import com.tqz.business.model.request.GetRatingsRequest;
import com.tqz.business.model.request.ProductRatingRequest;
import com.tqz.business.utils.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jedis jedis;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    private MongoCollection<Document> ratingCollection;

    private MongoCollection<Document> getRatingCollection() {
        if (null == ratingCollection)
            ratingCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return ratingCollection;
    }

    public Rating documentToRating(Document document) {
        Rating rating = null;
        try {
            rating = objectMapper.readValue(JSON.serialize(document), Rating.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rating;

    }

    public boolean productRating(ProductRatingRequest request) {
        Rating rating = new Rating(request.getUserId(), request.getProductId(), request.getScore());
        updateRedis(rating);
        if (null == userService.findByUserId(request.getUserId())) return false;
        if (null == productService.findByProductId(request.getProductId())) return false;
        if (request.getScore() > 5 || request.getScore() < 1) return false;
        if (ratingExist(rating.getUserId(), rating.getProductId())) {
            return updateRating(rating);
        } else {
            return newRating(rating);
        }
    }

    private void updateRedis(Rating rating) {
        if (jedis.exists("userId:" + rating.getUserId()) && jedis.llen("userId:" + rating.getUserId()) >= Constant.REDIS_PRODUCT_RATING_QUEUE_SIZE) {
            jedis.rpop("userId:" + rating.getUserId());
        }
        jedis.lpush("userId:" + rating.getUserId(), rating.getProductId() + ":" + rating.getScore());
    }

    private boolean newRating(Rating rating) {
        try {
            getRatingCollection().insertOne(Document.parse(objectMapper.writeValueAsString(rating)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean ratingExist(int userId, int productId) {
        return null != findRating(userId, productId);
    }

    private boolean updateRating(Rating rating) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("userId", rating.getUserId());
        basicDBObject.append("productId", rating.getProductId());
        getRatingCollection().updateOne(basicDBObject,
                new Document().append("$set", new Document("score", rating.getScore())));
        return true;
    }

    public Rating findRating(int userId, int productId) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("userId", userId);
        basicDBObject.append("productId", productId);
        FindIterable<Document> documents = getRatingCollection().find(basicDBObject);
        if (documents.first() == null)
            return null;
        return documentToRating(documents.first());
    }

    public List<Rating> findRatingsByProductId(GetRatingsRequest request) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("productId", request.getProductId());
        FindIterable<Document> documents = getRatingCollection().find(basicDBObject);
        if (documents.first() == null) {
            System.out.println("null");
            return null;
        }
        List<Rating> ratings = new ArrayList<>();
        for (Document document : documents) {
            ratings.add(documentToRating(document));
        }
        return ratings;
    }

}
