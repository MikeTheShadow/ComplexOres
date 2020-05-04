package com.miketheshadow.complexores.util;

import com.mongodb.BasicDBObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomOre {

    private final String material;
    private final String mainDrop;
    private final HashMap<String,Integer> dropList;
    private final int respawnTime;
    private final int laborCost;
    private final int levelReq;

    public CustomOre(String material, String mainDrop, HashMap<String,Integer> dropList, int respawnTime,int laborCost,int levelReq) {
        this.material = material;
        this.mainDrop = mainDrop;
        this.dropList = dropList;
        this.respawnTime = respawnTime;
        this.laborCost = laborCost;
        this.levelReq = levelReq;
    }

    public CustomOre(Document document) {
        this.material = document.getString("material");
        this.mainDrop = document.getString("mainDrop");
        BasicDBObject dropObject = new BasicDBObject((Document)document.get("dropList"));
        this.dropList = (HashMap<String, Integer>) dropObject.toMap();
        this.respawnTime = document.getInteger("respawnTime");
        this.laborCost = document.getInteger("laborCost");
        this.levelReq = document.getInteger("levelReq");
    }

    public Document toDocument() {
        Document document = new Document();
        document.append("material",this.material);
        document.append("mainDrop",this.mainDrop);
        document.append("dropList",this.dropList);
        document.append("respawnTime",this.respawnTime);
        document.append("laborCost",this.laborCost);
        document.append("levelReq",this.levelReq);
        return document;
    }

    public String getMainDrop() { return mainDrop; }
    public String getMaterial() { return material; }
    public HashMap<String,Integer> getDropList() { return dropList; }
    public int getRespawnTime() { return respawnTime; }
    public int getLaborCost() { return laborCost; }
    public int getLevelReq() { return levelReq; }

    //convert string to items
    public ItemStack getMainDropAsItemStack() {
        return NBTItem.convertNBTtoItem(new NBTContainer(this.mainDrop));
    }
}
