package me.tuskdev.towns.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public record Coordinates(String worldName, int blockX, int blockY, int blockZ) {

    public Location build() {
        return new Location(Bukkit.getWorld(worldName), blockX, blockY, blockZ);
    }

    public static Coordinates of(Location location) {
        return new Coordinates(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Coordinates of(Entity entity) {
        return of(entity.getLocation());
    }

    public static Coordinates of(Block block) {
        return of(block.getLocation());
    }

}
