package me.tuskdev.towns.util;

import me.tuskdev.towns.model.Town;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TownLoader {

    public static Town load(ResultSet resultSet) throws SQLException {
        return new Town(
                resultSet.getString("name"),
                resultSet.getLong("claim_id"),
                GsonUtil.fromJsonMap(resultSet.getString("members")),
                GsonUtil.fromJsonList(resultSet.getString("logs")),
                new Coordinates(resultSet.getString("world_name"), resultSet.getInt("block_x"), resultSet.getInt("block_y"), resultSet.getInt("block_z")),
                resultSet.getDouble("balance"),
                resultSet.getBoolean("enable_chat")
        );
    }

}
