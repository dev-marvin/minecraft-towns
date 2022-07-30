package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.GriefPreventionHook;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AcceptCommand {

    private final String usage, unknown, error, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public AcceptCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("accept-usage"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("accept-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("accept-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("accept-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("accept-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.accept",
            aliases = "join",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        Town town = townCache.get(context.getArg(0));
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        Player player = context.getSender();
        if (town.getMemberRank(player.getUniqueId()) != Rank.PENDING_MEMBER) {
            context.sendMessage(error);
            return;
        }

        town.setMember(player.getUniqueId(), Rank.MEMBER);
        town.addLog(log.replace("{player}", player.getName()).replace("{date}", DateUtil.now()));
        townController.updateMembers(town);
        townController.updateLogs(town);

        townCache.set(player.getUniqueId(), town.getName());

        GriefPreventionHook.addTrust(player, town.getClaimId());

        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> member.sendMessage(complete.replace("{player}", player.getName())));
        context.sendMessage(success.replace("{town-name}", town.getName()));
    }

}
