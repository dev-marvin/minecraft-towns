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

public class InviteCommand {

    private final String usage, citizen, unknown, permission, pending, already, member, self, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public InviteCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-usage"));
        this.citizen = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-citizen"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.pending = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-pending"));
        this.already = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-member"));
        this.member = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-player"));
        this.self = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-self"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invite-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.invite",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        Player target = Bukkit.getPlayerExact(context.getArg(0));
        if (target == null) {
            context.sendMessage(member);
            return;
        }

        if (townCache.get(target.getUniqueId()) != null) {
            context.sendMessage(citizen);
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

        Rank rank = town.getMemberRank(target.getUniqueId());
        if (rank != null) {
            context.sendMessage(rank == Rank.PENDING_MEMBER ?
                    pending :
                    already
            );
            return;
        }

        town.setMember(target.getUniqueId(), Rank.PENDING_MEMBER);
        town.addLog(log.replace("{player}", player.getName()).replace("{target}", target.getName()).replace("{date}", DateUtil.now()));
        townController.updateMembers(town);
        townController.updateLogs(town);

        target.sendMessage(complete.replace("{town-name}", town.getName()));
        context.sendMessage(success.replace("{target}", target.getName()));
    }

}
