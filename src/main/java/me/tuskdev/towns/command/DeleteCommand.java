package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.VaultHook;
import me.tuskdev.towns.model.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DeleteCommand {

    private static final Map<UUID, String> CONFIRM = new HashMap<>();

    private final String usage, unknown, permission, confirm, error, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public DeleteCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("delete-usage"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.confirm = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("delete-confirm"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("delete-error"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("delete-complete"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("delete-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.delete",
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

        if (town.getMemberRank(player.getUniqueId()) != Rank.LEADER && !player.isOp()) {
            context.sendMessage(permission);
            return;
        }

        CONFIRM.put(player.getUniqueId(), town.getName());

        player.sendMessage(confirm.replace("{town-name}", town.getName()));
    }

    @Command(
            name = "town.delete.confirm",
            target = CommandTarget.PLAYER
    )
    public void handleConfirmCommand(Context<Player> context) {
        UUID uuid = context.getSender().getUniqueId();

        String townName = CONFIRM.get(uuid);
        if (townName == null) {
            context.sendMessage(error);
            return;
        }

        Town town = townCache.get(townName);
        town.getMembers().forEach(member -> townCache.set(member, null));
        VaultHook.depositPlayer(context.getSender(), town.getBalance());
        townCache.remove(townName);
        townController.delete(townName);

        CONFIRM.remove(uuid);
        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> member.sendMessage(complete.replace("{town-name}", townName)));
        context.sendMessage(success.replace("{town-name}", townName));
    }

}
