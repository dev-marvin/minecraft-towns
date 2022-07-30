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

public class DeclineCommand {

    private final String usage, unknown, error, log, success;
    private final TownCache townCache;
    private final TownController townController;

    public DeclineCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("decline-usage"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("decline-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("decline-log"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("decline-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.decline",
            aliases = "deny",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        Player player = context.getSender();
        Town town = townCache.get(context.getArg(0));
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        if (town.getMemberRank(player.getUniqueId()) != Rank.PENDING_MEMBER) {
            context.sendMessage(error);
            return;
        }

        town.removeMember(player.getUniqueId());
        town.addLog(log.replace("{player}", player.getName()).replace("{date}", DateUtil.now()));
        townController.updateMembers(town);
        townController.updateLogs(town);

        context.sendMessage(success.replace("{town-name}", town.getName()));
    }

}
