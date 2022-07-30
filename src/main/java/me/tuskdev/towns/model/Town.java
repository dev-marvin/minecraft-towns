package me.tuskdev.towns.model;

import com.google.common.collect.ImmutableSet;
import me.tuskdev.towns.enums.Rank;
import me.tuskdev.towns.util.Coordinates;

import java.util.*;

public class Town {

    private final long claimId;
    private final Map<UUID, Rank> members; // members of town and their rank
    private final List<String> logs;
    private String name;
    private Coordinates coordinates; // spawn of town
    private double balance = 0;
    private boolean enableChat = false;

    public Town(String name, long claimId, Coordinates coordinates) {
        this.name = name;
        this.claimId = claimId;
        this.coordinates = coordinates;
        this.members = new HashMap<>();
        this.logs = new ArrayList<>();
    }

    public Town(String name, long claimId, Map<UUID, Rank> members, List<String> logs, Coordinates coordinates, double balance, boolean enableChat) {
        this.name = name;
        this.claimId = claimId;
        this.members = members;
        this.logs = logs;
        this.coordinates = coordinates;
        this.balance = balance;
        this.enableChat = enableChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getClaimId() {
        return claimId;
    }

    public Map<UUID, Rank> getMembersMap() {
        return members;
    }

    public Set<UUID> getMembers() {
        return ImmutableSet.copyOf(members.keySet());
    }

    public void setMember(UUID uuid, Rank rank) {
        members.put(uuid, rank);
    }

    public Rank getMemberRank(UUID uuid) {
        return members.get(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public List<String> getLogs() {
        return logs;
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isEnableChat() {
        return enableChat;
    }

    public void setEnableChat(boolean enableChat) {
        this.enableChat = enableChat;
    }
}
