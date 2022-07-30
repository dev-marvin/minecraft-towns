package me.tuskdev.towns.cache;

import me.tuskdev.towns.controller.TownController;
import me.tuskdev.towns.model.Town;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownCache {

    private final Map<UUID, String> PLAYER_TOWN = new HashMap<>();
    private final Map<String, Town> CACHE = new HashMap<>();

    public Town get(UUID uuid) {
        return CACHE.get(PLAYER_TOWN.getOrDefault(uuid, "").toUpperCase());
    }

    public void set(UUID uuid, String town) {
        if (town == null) PLAYER_TOWN.remove(uuid);
        else PLAYER_TOWN.put(uuid, town.toUpperCase());
    }

    public Town get(String name) {
        return CACHE.get(name.toUpperCase());
    }

    public Town get(long claimId) {
        return CACHE.values().stream().filter(town -> town.getClaimId() == claimId).findFirst().orElse(null);
    }

    public void add(Town town) {
        CACHE.put(town.getName().toUpperCase(), town);
    }

    public void remove(String name) {
        CACHE.remove(name.toUpperCase());
    }

    public Collection<Town> all() {
        return CACHE.values();
    }

    public synchronized void load(TownController townController) {
        townController.load().whenComplete((towns, throwable) -> {
            towns.forEach((key, value) -> value.getMembers().forEach(member -> PLAYER_TOWN.put(member, key)));
            CACHE.putAll(towns);
        });
    }

}
