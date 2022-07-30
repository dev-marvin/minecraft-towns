package me.tuskdev.towns.controller;

import me.tuskdev.towns.PooledConnection;
import me.tuskdev.towns.model.Town;
import me.tuskdev.towns.util.GsonUtil;
import me.tuskdev.towns.util.TownLoader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TownController {

    private static final String QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `towns` (`name` VARCHAR(16) NOT NULL, `claim_id` LONG NOT NULL, `members` TEXT NOT NULL, `logs` TEXT NOT NULL, `world_name` VARCHAR(16) NOT NULL, `block_x` INT NOT NULL, `block_y` INT NOT NULL, `block_z` INT NOT NULL, `balance` DOUBLE DEFAULT 0, `enable_chat` BOOLEAN DEFAULT false)";
    private static final String QUERY_SELECT = "SELECT * FROM `towns`";
    private static final String QUERY_INSERT_TOWN = "INSERT INTO `towns` (`name`, `claim_id`, `members`, `logs`, `world_name`, `block_x`, `block_y`, `block_z`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_UPDATE_NAME = "UPDATE `towns` SET `name` = ? WHERE `name` = ?";
    private static final String QUERY_UPDATE_MEMBERS = "UPDATE `towns` SET `members` = ? WHERE `name` = ?";
    private static final String QUERY_UPDATE_LOGS = "UPDATE `towns` SET `logs` = ? WHERE `name` = ?";
    private static final String QUERY_UPDATE_COORDINATES = "UPDATE `towns` SET `block_x` = ?, `block_y` = ?, `block_z` = ? WHERE `name` = ?";
    private static final String QUERY_UPDATE_BALANCE = "UPDATE `towns` SET `balance` = ? WHERE `name` = ?";
    private static final String QUERY_UPDATE_ENABLE_CHAT = "UPDATE `towns` SET `enable_chat` = ? WHERE `name` = ?";
    private static final String QUERY_DELETE_TOWN = "DELETE FROM `towns` WHERE `name` = ?";

    private final PooledConnection pooledConnection;

    public TownController(PooledConnection pooledConnection) {
        this.pooledConnection = pooledConnection;

        init();
    }

    void init() {
        pooledConnection.submit(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute(QUERY_CREATE_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Map<String, Town>> load() {
        CompletableFuture<Map<String, Town>> completableFuture = new CompletableFuture<>();

        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_SELECT)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                Map<String, Town> map = new HashMap<>();
                while (resultSet.next())
                    map.put(resultSet.getString("name").toUpperCase(), TownLoader.load(resultSet));

                completableFuture.complete(map);

                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return completableFuture;
    }

    public void insert(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_INSERT_TOWN)) {
                preparedStatement.setString(1, town.getName());
                preparedStatement.setLong(2, town.getClaimId());
                preparedStatement.setString(3, GsonUtil.toJson(town.getMembersMap()));
                preparedStatement.setString(4, GsonUtil.toJson(town.getLogs()));
                preparedStatement.setString(5, town.getCoordinates().worldName());
                preparedStatement.setInt(6, town.getCoordinates().blockX());
                preparedStatement.setInt(7, town.getCoordinates().blockY());
                preparedStatement.setInt(8, town.getCoordinates().blockZ());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateName(String oldName, String newName) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_NAME)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, oldName);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateMembers(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_MEMBERS)) {
                preparedStatement.setString(1, GsonUtil.toJson(town.getMembersMap()));
                preparedStatement.setString(2, town.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateLogs(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_LOGS)) {
                preparedStatement.setString(1, GsonUtil.toJson(town.getLogs()));
                preparedStatement.setString(2, town.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateCoordinates(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_COORDINATES)) {
                preparedStatement.setInt(1, town.getCoordinates().blockX());
                preparedStatement.setInt(2, town.getCoordinates().blockY());
                preparedStatement.setInt(3, town.getCoordinates().blockZ());
                preparedStatement.setString(4, town.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateBalance(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_BALANCE)) {
                preparedStatement.setDouble(1, town.getBalance());
                preparedStatement.setString(2, town.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateEnableChat(Town town) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_ENABLE_CHAT)) {
                preparedStatement.setBoolean(1, town.isEnableChat());
                preparedStatement.setString(2, town.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void delete(String townName) {
        pooledConnection.submit(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_TOWN)) {
                preparedStatement.setString(1, townName);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
