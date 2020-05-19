package com.miketheshadow.complexores.listener;

import com.miketheshadow.complexores.ComplexOres;
import com.miketheshadow.complexores.dbhandler.OreDBHandler;
import com.miketheshadow.complexores.dbhandler.OreReferenceDBHandler;
import com.miketheshadow.complexores.dbhandler.OreStorageDBHandler;
import com.miketheshadow.complexores.util.CustomOre;
import com.miketheshadow.complexproficiencies.api.ProficiencyAPI;
import com.miketheshadow.complexproficiencies.api.UserAPI;
import com.miketheshadow.complexproficiencies.utils.CustomUser;
import com.mongodb.BasicDBObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerBreakBlockEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakEvent(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        final CustomOre ore = OreDBHandler.getOre(event.getBlock().getType().toString());
        if(ore == null) {
            return;
        }
        event.setCancelled(true);
        if(player.getGameMode() == GameMode.CREATIVE) {
            if(OreReferenceDBHandler.removeReferenceOre(event.getBlock().getLocation())) {
                player.sendMessage(ChatColor.RED + "YOU REMOVED A REFERENCE ORE OF TYPE: " + event.getBlock().getType() +" THIS WAS A MISTAKE PLEASE REPLACE IT AND SET IT AS SUCH!");
            } else if(OreStorageDBHandler.removeOre(event.getBlock().getLocation())) {
                player.sendMessage(ChatColor.RED + "Ore removed from DB!");
            }
            else player.sendMessage(ChatColor.RED + "Ore was not in DB!");
            event.setCancelled(false);
            return;
        }
        if(!OreStorageDBHandler.getOreFromLocation(event.getBlock().getLocation())) {
            player.sendMessage(ChatColor.RED + "ORE EXCEPTION! THIS ORE IS INVALID! PLEASE REPORT IT WITH A SCREENSHOT OF THE TEXT BELOW!");
            player.sendMessage(ChatColor.RED + event.getBlock().getLocation().toString());
            return;
        }
        if(OreReferenceDBHandler.removeReferenceOre(event.getBlock().getLocation())) {
            player.sendMessage(ChatColor.RED + "REMOVED A REFERENCE ORE PLEASE NOTE THAT A REFERENCE ORE IS ESSENTIAL FOR THE ORE PLUGIN TO WORK!!!");
            player.sendMessage(ChatColor.RED + "THE BLOCK WAS OF TYPE: " + event.getBlock().getType() + " REPLACE IT AND RUN THE setreferenceore ON IT TO RESTORE");
            return;
        }
        int level = ProficiencyAPI.getProfLevel(player,"mining");
        if(level < ore.getLevelReq()) {
            player.sendMessage(ChatColor.RED + "You aren't high enough level to mine this. Current: " + level + " Required: " + ore.getLevelReq());
            return;
        }
        if(!UserAPI.userHasLabor(player,ore.getLaborCost())) {
            player.sendMessage(ChatColor.RED + "You don't have enough labor to mine this! Required: " + ore.getLaborCost());
            return;
        }
        //we check if the player has the inventory space here
        int freeSlots = 0;
        for(ItemStack item : player.getInventory()) {
            if(item == null) freeSlots++;
            else if(item.getType() == Material.AIR)freeSlots++;
        }

        //calculate item total here
        int addSize = 0;
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(ore.getMainDropAsItemStack());
        if(!ore.getDropList().isEmpty()) {
            for(Map.Entry<String,Integer> map : ore.getDropList().entrySet()) {
                Random random = new Random();
                int chance = random.nextInt(100);
                if(map.getValue() > chance) {
                    drops.add(NBTItem.convertNBTtoItem(new NBTContainer(map.getKey())));
                    addSize++;
                }
            }
        }
        if(freeSlots < addSize) {
            player.sendMessage(ChatColor.RED + "You don't have enough inventory space for this!");
            return;
        }
        ChatColor dropColor = ChatColor.AQUA;
        player.sendMessage("");
        for(ItemStack item : drops) {
            player.sendMessage(ChatColor.GOLD + "Received: " + dropColor + item.getItemMeta().getDisplayName());
            dropColor = ChatColor.LIGHT_PURPLE;
            player.getInventory().addItem(item);
        }
        event.getBlock().setType(Material.AIR);
        UserAPI.updateUserProf(player,"mining",ore.getLaborCost());
        long time = (ore.getRespawnTime() * 60) * 20;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ComplexOres.getPlugin(ComplexOres.class),new Runnable() {
            @Override
            public void run() {
                String mat = ore.getMaterial();
                Material material;
                if(OreReferenceDBHandler.getReferenceOre(mat) != null) {
                    material = OreReferenceDBHandler.getReferenceOre(mat).getBlock().getType();
                } else {
                    material = Material.valueOf(ore.getMaterial());
                }
                event.getBlock().setType(material);

            }
        },time);

    }

}
