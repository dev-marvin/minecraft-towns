package me.tuskdev.towns.command;

import me.ryanhamshire.GriefPrevention.Claim;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.GriefPreventionHook;
import me.tuskdev.towns.hook.VaultHook;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.Coordinates;
import me.tuskdev.towns.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CreateCommand {

    private final String usage, exists, error, invalid, owner, member, money, log, complete, success;
    private final double priceToCreateTown;
    private final TownCache townCache;
    private final TownController townController;

    public CreateCommand(double priceToCreateTown, TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-usage"));
        this.exists = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-already-exists"));
        this.error = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-error"));
        this.member = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-already-member"));
        this.invalid = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-area-claim"));
        this.owner = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-owner-claim"));
        this.money = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-money-error"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("create-success"));

        this.priceToCreateTown = priceToCreateTown;
        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.create",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        String townName = context.getArg(0);
        if (townCache.get(townName) != null) {
            context.sendMessage(exists);
            return;
        }

        Player player = context.getSender();
        if (townName.length() > (player.hasPermission("mcpartytowns.extended") ? 16 : 10)) {
            context.sendMessage(error);
            return;
        }

        if (townCache.get(player.getUniqueId()) != null) {
            context.sendMessage(member);
            return;
        }

        Claim claim = GriefPreventionHook.getClaimAt(player);
        if (claim == null) {
            context.sendMessage(invalid);
            return;
        }

        if (!claim.getOwnerID().equals(player.getUniqueId())) {
            context.sendMessage(owner);
            return;
        }

        if (VaultHook.getBalance(player) < priceToCreateTown) {
            context.sendMessage(money);
            return;
        }

        VaultHook.withdrawPlayer(player, priceToCreateTown);

        Town town = new Town(townName, claim.getID(), Coordinates.of(player));
        town.setMember(player.getUniqueId(), Rank.LEADER);
        town.addLog(log.replace("{player}", player.getName()).replace("{date}", DateUtil.now()));

        townCache.set(player.getUniqueId(), townName);
        townCache.add(town);
        townController.insert(town);

        context.sendMessage(success.replace("{town-name}", townName));

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(complete.replace("{town-name}", townName).replace("{player}", player.getName()));
        Bukkit.broadcastMessage("");
    }

}
