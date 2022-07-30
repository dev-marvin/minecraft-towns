package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.hook.VaultHook;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.DateUtil;
import me.tuskdev.towns.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DepositCommand {

    private final String usage, number, money, unknown, permission, log, complete, success;
    private final TownCache townCache;
    private final TownController townController;

    public DepositCommand(TownCache townCache, TownController townController, FileConfiguration fileConfiguration) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("deposit-usage"));
        this.number = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invalid-number"));
        this.money = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-money"));
        this.unknown = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("town-not-exists"));
        this.permission = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("no-permission"));
        this.log = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("deposit-log"));
        this.complete = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("deposit-target"));
        this.success = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("deposit-success"));

        this.townCache = townCache;
        this.townController = townController;
    }

    @Command(
            name = "town.deposit",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        int amount = NumberUtil.tryParseInt(context.getArg(0));
        if (amount <= 0) {
            context.sendMessage(number);
            return;
        }

        Player player = context.getSender();
        if (VaultHook.getBalance(player) < amount) {
            context.sendMessage(money);
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

        VaultHook.withdrawPlayer(player, amount);

        town.setBalance(town.getBalance() + amount);
        town.addLog(log.replace("{player}", player.getName()).replace("{amount}", amount + "").replace("{date}", DateUtil.now()));
        townController.updateBalance(town);
        townController.updateLogs(town);

        town.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(member -> member.sendMessage(complete.replace("{player}", player.getName()).replace("{amount}", amount + "")));
        context.sendMessage(success.replace("{amount}", amount + ""));
    }
}
