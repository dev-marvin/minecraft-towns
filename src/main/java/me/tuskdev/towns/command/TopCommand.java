package me.tuskdev.towns.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.NumberUtil;
import me.tuskdev.towns.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TopCommand {

    private final List<Town> top = new ArrayList<>();
    private final String usage, number, title, message;

    public TopCommand(TownCache townCache, FileConfiguration fileConfiguration, Plugin plugin) {
        this.usage = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("top-usage"));
        this.number = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("invalid-number"));
        this.title = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("top-success"));
        this.message = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("top-message"));

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            top.clear();
            top.addAll(townCache.all().stream().sorted((town1, town2) -> Double.compare(town2.getBalance(), town1.getBalance())).toList());
        }, 0L, 500L);
    }

    @Command(
            name = "town.top",
            target = CommandTarget.PLAYER
    )
    public void handleCommand(Context<Player> context) {
        if (context.argsCount() <= 0) {
            context.sendMessage(usage);
            return;
        }

        int page = NumberUtil.tryParseInt(context.getArg(0));
        if (page <= 0) {
            context.sendMessage(number);
            return;
        }

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < top.size(); i++) {
            Town town = top.get(i);
            lines.add(message.replace("{rank}", String.valueOf(i + 1)).replace("{town-name}", town.getName()).replace("{balance}", String.valueOf(town.getBalance())));
        }

        TextUtil.getPage(lines, page, title, 20).forEach(context::sendMessage);
    }

}
