package com.bot;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class MongoDB {

    private static MongoClient mongoClient;
    private final static String TELEGRAM_DATABASE = "telegram";
    private final static String CUSTOMERS_COLLECTION = "customers";
    public final static String ORDERS_STATE = "ORDERS_STATE";
    public final static String PIZZA_TYPE = "PIZZA_TYPE";
    public final static String PIZZA_SIZE = "PIZZA_SIZE";
    public final static String DRINK = "DRINK";
    public final static String ADDRESS = "ADDRESS";


    public static void connectToDatabase(){

        String url = "mongodb://localhost:27017";

        mongoClient = MongoClients.create(url);

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);

        try{
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = mongoDatabase.runCommand(command);
            System.out.println("Connected to the database");
        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    public static void insertNewUserId(String userId){

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);
        MongoCollection<Document> customersCollection = mongoDatabase.getCollection(CUSTOMERS_COLLECTION);

        try{
            if (!userExists(userId)){
                InsertOneResult result = customersCollection.insertOne(new Document().append("_id", userId).append(ORDERS_STATE,OrderState.PIZZA_SELECTION.toString()).append(PIZZA_TYPE,"").append(PIZZA_SIZE,"").append(DRINK,"").append(ADDRESS,""));
                System.out.println(result.getInsertedId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean userExists(String userId){

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);

        MongoCollection<Document> customersCollection = mongoDatabase.getCollection(CUSTOMERS_COLLECTION);

        Document userDoc = customersCollection.find(eq("_id", userId)).first();

        if (userDoc == null){
            return false;
        }

        return true;

    }

    public static boolean updateField(String fieldName,String newValue,String userChatId){

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);
        MongoCollection<Document> customersCollection = mongoDatabase.getCollection(CUSTOMERS_COLLECTION);

        UpdateResult result = customersCollection.updateOne(eq("_id", userChatId), new Document("$set", new Document(fieldName, newValue)));

        return result.wasAcknowledged() && result.getModifiedCount() == 1;
    }

    public static String getFieldValue(String fieldName,String userChatId){

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);
        MongoCollection<Document> customerCollection = mongoDatabase.getCollection(CUSTOMERS_COLLECTION);

        Document result = customerCollection.find(eq("_id", userChatId)).projection(fields(include(fieldName))).first();

        if (result != null && result.containsKey(fieldName)){
            return result.getString(fieldName);
        }else {
            return null;
        }
    }

    public static Document getCustomerAttributes(String userChatId){

        MongoDatabase mongoDatabase = mongoClient.getDatabase(TELEGRAM_DATABASE);
        MongoCollection<Document> customerCollection = mongoDatabase.getCollection(CUSTOMERS_COLLECTION);

        Document result = customerCollection.find(eq("_id",userChatId)).first();

        return result;
    }
}
