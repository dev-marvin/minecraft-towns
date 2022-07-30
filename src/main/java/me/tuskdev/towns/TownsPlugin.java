package me.tuskdev.towns;

import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.command.CommandInfo;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import me.tuskdev.towns.cache.TownCache;
import me.tuskdev.towns.command.*;
import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.hook.PlaceholderAPIHook;
import me.tuskdev.towns.listener.ChatListener;
import me.tuskdev.towns.listener.ClaimListener;
import me.tuskdev.towns.listener.PlayerMoveListener;
import me.tuskdev.towns.util.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TownsPlugin extends JavaPlugin {

    private PooledConnection pooledConnection;

    @Override
    public void onLoad() {
        saveDefaultConfig();

        pooledConnection = new PooledConnection(getConfig().getConfigurationSection("database"));
    }

    @Override
    public void onEnable() {
        TownCache townCache = new TownCache();
        TownController townController = new TownController(pooledConnection);
        townCache.load(townController);

        FileConfiguration fileConfiguration = new ConfigFile(this, "messages.yml").getFileConfiguration();

        BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommand(CommandInfo.builder().name("town").target(CommandTarget.PLAYER).build(), (context) -> ((Player) context.getSender()).performCommand("town info"));

        bukkitFrame.registerCommands(
                new AcceptCommand(townCache, townController, fileConfiguration),
                new ChatCommand(townCache, townController, fileConfiguration),
                new CreateCommand(getConfig().getDouble("price-to-create-town"), townCache, townController, fileConfiguration),
                new DeclineCommand(townCache, townController, fileConfiguration),
                new DeleteCommand(townCache, townController, fileConfiguration),
                new DemoteCommand(townCache, townController, fileConfiguration),
                new DepositCommand(townCache, townController, fileConfiguration),
                new InfoCommand(townCache, fileConfiguration),
                new InviteCommand(townCache, townController, fileConfiguration),
                new KickCommand(townCache, townController, fileConfiguration),
                new LeaveCommand(townCache, townController, fileConfiguration),
                new LogsCommand(townCache, fileConfiguration),
                new PromoteCommand(townCache, townController, fileConfiguration),
                new RenameCommand(townCache, townController, fileConfiguration),
                new SetSpawnCommand(townCache, townController, fileConfiguration),
                new TeleportCommand(townCache, fileConfiguration),
                new TopCommand(townCache, fileConfiguration, this),
                new WithdrawCommand(townCache, townController, fileConfiguration)
        );

        getServer().getPluginManager().registerEvents(new ChatListener(townCache, fileConfiguration), this);
        getServer().getPluginManager().registerEvents(new ClaimListener(townCache, townController, fileConfiguration), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(townCache, fileConfiguration), this);

        new PlaceholderAPIHook(townCache).register();
    }

    @Override
    public void onDisable() {
        pooledConnection.close();
    }
}
