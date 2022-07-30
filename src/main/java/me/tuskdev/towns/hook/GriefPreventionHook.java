package me.tuskdev.towns.hook;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook {

    private static final GriefPrevention INSTANCE = GriefPrevention.instance;

    public static Claim getClaimAt(Player player) {
        return getClaimAt(player.getLocation());
    }

    public static Claim getClaimAt(Location location) {
        return INSTANCE.dataStore.getClaimAt(location, true, true, null);
    }

    public static Claim getClaimById(long id) {
        return INSTANCE.dataStore.getClaim(id);
    }

    public static void addTrust(Player player, long claimId) {
        Claim claim = getClaimById(claimId);
        if (claim == null) return;

        claim.setPermission(player.getUniqueId().toString(), ClaimPermission.Build);
    }

}
