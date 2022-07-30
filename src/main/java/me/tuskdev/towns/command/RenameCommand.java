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

import java.util.Objects;

public class RenameCommand {

    private final String usage, exists, error, unknown, permission, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public RenameCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("rename-usage"));
        this.exists = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-already-exists"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("rename-error"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("rename-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("rename-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("rename-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.rename",
            target = CommandTarget.PLAYER,
            permission = "mcpartytowns.rename"
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        String name = context.getArg(0);
        if (townCache.get(name) != null) {
            context.sendMessage(exists);
            return;
        }

        if (name.length() > 16) {
            context.sendMessage(error);
            return;
        }

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

        String oldName = town.getName();
        town.setName(name);
        town.addLog(log.replace("{player}", player.getName()).replace("{name}", name).replace("{old-name}", oldName).replace("{date}", DateUtil.now()));
        town.getMembers().forEach(uuid -> townCache.set(uuid, name));

        townCache.remove(oldName);
        townCache.add(town);
        townController.updateName(name, oldName);
        townController.updateLogs(town);

        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> member.sendMessage(success.replace("{player}", player.getName()).replace("{name}", name).replace("{old-name}", oldName)));
        context.sendMessage(success.replace("{name}", name).replace("{old-name}", oldName));
    }

}
