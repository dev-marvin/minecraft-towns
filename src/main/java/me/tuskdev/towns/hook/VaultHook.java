package me.tuskdev.towns.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;
import java.util.UUID;

public class VaultHook {

    public static final Economy ECONOMY;

    static {
        RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        ECONOMY = registeredServiceProvider != null ? registeredServiceProvider.getProvider() : null;
    }

    public static double getBalance(Player player) {
        return ECONOMY != null ? ECONOMY.getBalance(player) : 0;
    }

    public static void withdrawPlayer(Player player, double value) {
        if (ECONOMY == null) return;

        ECONOMY.withdrawPlayer(player, value);
    }

    public static void depositPlayer(Player player, double value) {
        if (ECONOMY == null) return;

        ECONOMY.depositPlayer(player, value);
    }


    public static void depositPlayer(UUID uuid, double value) {
        if (ECONOMY == null) return;

        ECONOMY.depositPlayer(Bukkit.getOfflinePlayer(uuid), value);
    }

}
