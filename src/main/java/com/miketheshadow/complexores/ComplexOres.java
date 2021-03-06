package com.miketheshadow.complexores;

import com.miketheshadow.complexores.dbhandler.OreDBHandler;
import com.miketheshadow.complexores.dbhandler.OreReferenceDBHandler;
import com.miketheshadow.complexores.dbhandler.OreStorageDBHandler;
import com.miketheshadow.complexores.listener.PlayerBreakBlockEvent;
import com.miketheshadow.complexores.listener.PlayerPlaceBlockEvent;
import com.miketheshadow.complexores.util.CustomOre;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ComplexOres extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loading Complex Ores!");
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerBreakBlockEvent(),this);
        manager.registerEvents(new PlayerPlaceBlockEvent(),this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded ores!");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("material")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.sendMessage(ChatColor.GOLD + "MaterialID: " + player.getInventory().getItemInMainHand().getType());
                NBTCompound nbtItem = NBTItem.convertItemtoNBT(player.getInventory().getItemInMainHand());
                player.sendMessage(ChatColor.GOLD + "NBT Type:" + NBTItem.convertNBTtoItem(nbtItem).toString());
                return true;
            }
            return false;
        }
        if(cmd.getName().equalsIgnoreCase("setreferenceore")) {
            if(!instanceOfPlayer(sender))return false;
            Player player = (Player)sender;
            Block block = player.getTargetBlock(null,5);
            String  material = block.getType().toString();
            sender.sendMessage(material);
            OreReferenceDBHandler.addReferenceOre(material,block.getLocation());
            OreStorageDBHandler.removeOre(block.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Added reference ore for: " + block.getType().toString());
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("OreRegister")) {
            if(sender instanceof Player) {
                if(args.length == 0) return false;
                Player player = (Player)sender;
                Inventory inventory = player.getInventory();
                Material ore = inventory.getItem(0).getType();
                player.sendMessage(ChatColor.GREEN + "ORE: " + ore.toString());
                int respawnTime = Integer.parseInt(args[0]);
                int laborCost = Integer.parseInt(args[1]);
                int levelReq = Integer.parseInt(args[2]);
                String guaranteedItem = NBTItem.convertItemtoNBT(inventory.getItem(1)).toString();
                player.sendMessage(ChatColor.GREEN + "100% DROP: " + inventory.getItem(1).getType().toString());
                HashMap<String,Integer> map = new HashMap<String, Integer>();
                for(int i = 3; i < args.length;i++) {
                    map.put(NBTItem.convertItemtoNBT(inventory.getItem(i - 1)).toString(),Integer.parseInt(args[i]));
                    player.sendMessage(ChatColor.GREEN + "Chance drop: " + inventory.getItem(i - 1).getType().toString() + " with chance: " + args[i]);
                }
                CustomOre customOre = new CustomOre(ore.toString(),guaranteedItem,map,respawnTime,laborCost,levelReq);
                if(!OreDBHandler.addOre(customOre)) OreDBHandler.updateOre(customOre);
                return true;
            }
            return false;
        }
        if(cmd.getName().equalsIgnoreCase("OreRegen")) {
            sender.sendMessage(ChatColor.GREEN + "Restoring all ores...");
            OreStorageDBHandler.restoreAllBlocks();
            sender.sendMessage(ChatColor.GREEN + "Restored!");
            return false;
        }
        return false;
    }

    public boolean instanceOfPlayer(CommandSender sender) {return (sender instanceof Player);}
}
