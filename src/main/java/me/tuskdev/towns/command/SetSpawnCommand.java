package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.Coordinates;
import me.tuskdev.towns.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    private final String unknown, permission, log, success;
    private final TownCache townCache;
    private final TownController townController;

    public SetSpawnCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("set-spawn-log"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("set-spawn-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.setspawn",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        Player player = context.getSender();
        Town town = townCache.get(player.getUniqueId());
        if (town == null) {
            context.sendMessage(unknown);
            return;
        }

        Rank playerRank = town.getMemberRank(player.getUniqueId());
        if (playerRank != Rank.LEADER && playerRank != Rank.OFFICE && !player.isOp()) {
            context.sendMessage(permission);
            return;
        }

        town.setCoordinates(Coordinates.of(player));
        town.addLog(log.replace("{player}", player.getName()).replace("{date}", DateUtil.now()));
        townController.updateCoordinates(town);
        townController.updateLogs(town);

        context.sendMessage(success);
    }

}
