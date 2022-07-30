package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DemoteCommand {

    private final String usage, online, self, unknown, permission, member, error, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public DemoteCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("demote-usage"));
        this.online = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-player"));
        this.self = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-self"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.member = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-member"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("demote-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("demote-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("demote-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("demote-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.demote",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        Player target = Bukkit.getPlayerExact(context.getArg(0));
        if (target == null) {
            context.sendMessage(online);
            return;
        }

        Player player = context.getSender();
        if (player.getName().equalsIgnoreCase(target.getName())) {
            context.sendMessage(self);
            return;
        }

        Town town = townCache.get(player.getUniqueId());
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        Rank playerRank = town.getMemberRank(player.getUniqueId());
        if (playerRank != Rank.LEADER && town.getMemberRank(player.getUniqueId()) != Rank.OFFICE && !player.isOp()) {
            context.sendMessage(permission);
            return;
        }

        Rank targetRank = town.getMemberRank(target.getUniqueId());
        if (targetRank == null || targetRank == Rank.PENDING_MEMBER) {
            context.sendMessage(member);
            return;
        }

        if (targetRank == Rank.MEMBER) {
            context.sendMessage(error);
            return;
        }

        town.setMember(target.getUniqueId(), Rank.MEMBER);
        town.addLog(log.replace("{player}", player.getName()).replace("{target}", target.getName()).replace("{date}", DateUtil.now()));
        townController.updateMembers(town);
        townController.updateLogs(town);

        target.sendMessage(complete.replace("{town-name}", town.getName()));
        context.sendMessage(success.replace("{target}", target.getName()));
    }

}
