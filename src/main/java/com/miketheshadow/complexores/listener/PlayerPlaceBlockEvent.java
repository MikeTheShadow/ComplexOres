package com.miketheshadow.complexores.listener;

import com.miketheshadow.complexores.dbhandler.OreDBHandler;
import com.miketheshadow.complexores.dbhandler.OreStorageDBHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockEvent implements Listener {

    @EventHandler
    public void onPlaceBlockEvent(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE)return;
        Block block = event.getBlock();
        if(OreDBHandler.getOre(block.getType().toString()) != null) {
            OreStorageDBHandler.addOre(event.getBlock().getType().toString(), event.getBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Ore successfully saved!");
        }
    }
}
