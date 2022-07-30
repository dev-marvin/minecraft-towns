package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatCommand {
    
    private final String unknown, permission, error, log, success;
    private final TownCache townCache;
    private final TownController townController;

    public ChatCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("chat-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("chat-log"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("chat-success"));
        
        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.chat",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        Player player = context.getSender();
        Town town = townCache.get(player.getUniqueId());
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }
        
        if (town.getMemberRank(player.getUniqueId()) != Rank.LEADER) {
            context.sendMessage(permission);
            return;
        }
        
        boolean enableChat = context.argsCount() <= 0 ? !town.isEnableChat() : context.getArg(0).equals("on");
        if (enableChat == town.isEnableChat()) {
            context.sendMessage(error);
            return;
        }
        
        town.setEnableChat(enableChat);
        town.addLog(log.replace("{player}", player.getName()).replace("{status}", enableChat ? "enabled" : "disabled").replace("{date}", DateUtil.now()));
        townController.updateEnableChat(town);
        townController.updateLogs(town);
        
        context.sendMessage(success.replace("{status}", enableChat ? "enabled" : "disabled"));
    }
    
}
