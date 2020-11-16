package com.miketheshadow.complexores.dbhandler;

import com.miketheshadow.complexores.util.CustomOre;
import com.miketheshadow.complexproficiencies.api.DatabaseAPI;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

public class OreDBHandler {

    private static  MongoCollection<Document> collection = init();

    public static boolean addOre(CustomOre customOre) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("material", customOre.getMaterial()));
        if (cursor.first() == null) {
            collection.insertOne(customOre.toDocument());
            ConsoleCommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage(ChatColor.GREEN + "Adding new ore: " + customOre.getMaterial());
            sender.sendMessage(ChatColor.GREEN + "Drops: " + NBTItem.convertNBTtoItem(new NBTContainer(customOre.getMainDrop())).getType());
            return true;
        }
        return false;
    }

    public static CustomOre getOre(String material) {
        FindIterable<Document> cursor = collection.find(new BasicDBObject("material", material));
        if(cursor.first() == null) return null;
        return new CustomOre(cursor.first());
    }

    public static void updateOre(CustomOre customOre) {
        collection.replaceOne(new BasicDBObject("material", customOre), customOre.toDocument());
    }

    public static MongoCollection<Document> init() {
        if(collection == null) {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(DatabaseAPI.getDatabaseConnection().getConnectionString()));
            MongoDatabase database = mongoClient.getDatabase("ComplexOres");
            return database.getCollection("Ores");
        }
        return collection;
    }

    public static List<Document> getAllDocuments() {
        return null;
    }

    public static void updateDocument(Document document) {
    }
}
