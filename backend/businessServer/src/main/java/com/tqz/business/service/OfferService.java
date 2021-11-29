package com.tqz.business.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import com.tqz.business.model.domain.Offer;
import com.tqz.business.model.domain.User;
import com.tqz.business.model.request.AddOfferRequest;
import com.tqz.business.model.request.FindOfferByProductRequestRequest;
import com.tqz.business.model.request.LoginUserRequest;
import com.tqz.business.model.request.RegisterUserRequest;
import com.tqz.business.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.mongodb.client.model.Filters.eq;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OfferService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> offerCollection;

    private MongoCollection<Document> getOfferCollection() {
        if (null == offerCollection)
            offerCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_OFFER_COLLECTION);
        return offerCollection;
    }

    public Boolean addOffer(AddOfferRequest request) {
        Offer offer = new Offer();

        offer.setCapacity(request.getCapacity());
        offer.setPrice(request.getPrice());
        offer.setProductId(request.getProductId());
        offer.setOwnerId(request.getOwnerId());
        offer.setStatus(0);
        offer.setTimestamp(System.currentTimeMillis());

        try {
            getOfferCollection().insertOne(Document.parse(objectMapper.writeValueAsString(offer)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Offer> findOfferByProductId(FindOfferByProductRequestRequest request) {
        List<Offer> offers = new ArrayList<>();
        FindIterable<Document> documents = getOfferCollection().find(eq("productId", request.getProductId()));
        int num = request.getNum();
        int i = 1;
        for (Document document: documents) {
            offers.add(documentToOffer(document));
            if (i >= num) {
                break;
            }
        }
        return offers;
    }

    private Offer documentToOffer(Document document) {
        try {
            return objectMapper.readValue(JSON.serialize(document), Offer.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addUserToOffer(Offer offer, User user) {
//        getOfferCollection().updateOne(Filters)
        return true;
    }

    public boolean updateOffer(Offer offer) {
        getOfferCollection().updateOne(Filters.eq("offerId", offer.getOfferId()), new Document().append("$set", new Document("status", offer.getStatus())));
//        getOfferCollection().updateOne(Filters.eq("offerId", offer.getOfferId()), new Document().append("$set", new Document("prefGenres", user.getPrefGenres())));
        return true;
    }

}
