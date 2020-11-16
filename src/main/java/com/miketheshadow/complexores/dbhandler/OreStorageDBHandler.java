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
import org.bukkit.Material;

import java.util.HashMap;

public class OreStorageDBHandler {

    private static MongoCollection<Document> collection = init();
    public static void addOre(String material, Location location) {
        Document document = new Document();
        document.append("location",location.serialize());
        document.append("material",material);
        FindIterable<Document> cursor = collection.find(new BasicDBObject("location", location.serialize()));
        Document cursorDoc = cursor.first();
        if (cursorDoc == null) {
            collection.insertOne(document);
        } else {
            collection.replaceOne(cursorDoc,document);
        }
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
            MongoClient mongoClient = new MongoClient(new MongoClientURI(DatabaseAPI.getDatabaseConnection().getConnectionString()));
            MongoDatabase database = mongoClient.getDatabase("ComplexOres");
            return database.getCollection("OreStorage");
        }
        return collection;
    }

    public static void restoreAllBlocks() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "Debug: " + "Restoring all ores...");
        for(Document document : collection.find()) {
            Material material = Material.getMaterial(document.getString("material"));
            if(OreReferenceDBHandler.getReferenceOre(material.toString()) != null) {
                material = OreReferenceDBHandler.getReferenceOre(material.toString()).getBlock().getType();
            }
            BasicDBObject dropObject = new BasicDBObject((Document)document.get("location"));
            Location location = Location.deserialize( (HashMap<String, Object>) dropObject.toMap());
            location.getBlock().setType(material);
        }
    }
}
