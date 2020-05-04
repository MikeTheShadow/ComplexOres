package com.miketheshadow.complexores.dbhandler;

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
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;

public class OreStorageDBHandler {

    private static MongoCollection<Document> collection = init();
    public static boolean addOre(String material, Location location) {
        Document document = new Document();
        document.append("location",location.serialize());
        document.append("material",material);
        FindIterable<Document> cursor = collection.find(new BasicDBObject("location", location.serialize()));
        if (cursor.first() == null) {
            collection.insertOne(document);
            ConsoleCommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage(ChatColor.GREEN + "Debug: " + "adding ore at location" + location.toString());
            return true;
        }
        return false;
    }
    public static boolean getOreFromLocation(Location location) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("location", location.serialize()));
        return cursor.first() != null;
    }

    public static boolean removeOre(Location location) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("location", location.serialize()));
        Document document = cursor.first();
        if(document == null) return false;
        collection.deleteOne(document);
        return true;
    }

    public static MongoCollection<Document> init() {
        if(collection == null)
        {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            MongoDatabase database = mongoClient.getDatabase("ComplexOres");
            return database.getCollection("OreStorage");
        }
        return collection;
    }

    public static void restoreAllBlocks() {
        for(Document document : collection.find()) {
            Material material = Material.valueOf(document.getString("material"));
            BasicDBObject dropObject = new BasicDBObject((Document)document.get("location"));
            Location location = Location.deserialize( (HashMap<String, Object>) dropObject.toMap());
            location.getBlock().setType(material);
        }
    }

    public static void updateDocument(Document document) {

    }
}
