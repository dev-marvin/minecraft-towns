package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.model.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LeaveCommand {

    private final String unknown, leader, error, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public LeaveCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.leader = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("leave-leader"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("leave-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("leave-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("leave-complete"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("leave-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.leave",
            aliases = "exit",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        Player player = context.getSender();
        Town town = townCache.get(player.getUniqueId());
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        if (town.getMemberRank(player.getUniqueId()) == Rank.LEADER) {
            context.sendMessage(leader);
            return;
        }

        if (town.getMemberRank(player.getUniqueId()) == null || town.getMemberRank(player.getUniqueId()) == Rank.PENDING_MEMBER) {
            context.sendMessage(error);
            return;
        }

        town.removeMember(player.getUniqueId());
        town.addLog(log.replace("{player}", player.getName()));
        townController.updateMembers(town);
        townController.updateLogs(town);

        townCache.set(player.getUniqueId(), null);

        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> member.sendMessage(complete.replace("{player", player.getName())));
        context.sendMessage(success);
    }
}
