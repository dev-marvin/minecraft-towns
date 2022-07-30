package me.tuskdev.towns;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class PooledConnection {

    private static final String CONNECTION_URL = "jdbc:mysql://%s:%s/%s";

    private final Executor EXECUTOR = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    private final HikariDataSource dataSource;

    PooledConnection(ConfigurationSection configurationSection) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format(CONNECTION_URL, configurationSection.getString("hostname"), configurationSection.getString("port"), configurationSection.getString("database")));
        hikariConfig.setUsername(configurationSection.getString("username"));
        hikariConfig.setPassword(configurationSection.getString("password"));

        ConfigurationSection dataSourceProperties = configurationSection.getConfigurationSection("dataSourceProperties");
        dataSourceProperties.getKeys(false).forEach(key -> hikariConfig.addDataSourceProperty(key, dataSourceProperties.get(key)));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public void submit(Consumer<Connection> consumer) {
        EXECUTOR.execute(() -> {
            try (Connection connection = dataSource.getConnection()) {
                consumer.accept(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    void close() {
        dataSource.close();
    }

}
