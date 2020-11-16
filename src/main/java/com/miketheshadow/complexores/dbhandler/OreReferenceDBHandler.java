package com.miketheshadow.complexores.dbhandler;

import com.miketheshadow.complexproficiencies.api.DatabaseAPI;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;

import javax.print.Doc;
import java.util.HashMap;

public class OreReferenceDBHandler {

    private static MongoCollection<Document> collection = init();

    public static void addReferenceOre(String material, Location location) {
        Document document = new Document();
        document.append("location",location.serialize());
        document.append("material",material);
        FindIterable<Document> cursor = collection.find(new BasicDBObject("material", material));
        Document doc = cursor.first();
        if (doc == null) {
            collection.insertOne(document);
            ConsoleCommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage(ChatColor.GREEN + "Debug: " + "adding ore reference for " + material);
        } else {
            collection.replaceOne(doc,document);
            ConsoleCommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage(ChatColor.GREEN + "Debug: " + "replacing ore with " + material);
        }
    }

    public static Location getReferenceOre(String material) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("material",material));
        Document document = cursor.first();
        if(document == null) return null;
        BasicDBObject dropObject = new BasicDBObject((Document)document.get("location"));
        HashMap<String,Object> map = (HashMap<String, Object>) dropObject.toMap();
        return Location.deserialize(map);
    }
    public static boolean removeReferenceOre(Location location) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("location", location.serialize()));
        Document document = cursor.first();
        if(document == null) return false;
        collection.deleteOne(document);
        return true;
    }

    public static void checkAllReferenceOres() {

    }

    public static MongoCollection<Document> init() {
        if(collection == null)
        {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(DatabaseAPI.getDatabaseConnection().getConnectionString()));
            MongoDatabase database = mongoClient.getDatabase("ComplexOres");
            return database.getCollection("OreReferences");
        }
        return collection;
    }


}
