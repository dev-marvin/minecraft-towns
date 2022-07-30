package me.tuskdev.towns.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigFile {

	private final File file;
	private final FileConfiguration fileConfiguration;

	public ConfigFile(Plugin plugin, String fileName) {
		file = new File(plugin.getDataFolder(), fileName);

		if (!file.exists()) plugin.saveResource(fileName, true);

		fileConfiguration = YamlConfiguration.loadConfiguration(file);
	}
	public ConfigFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.file = file;
		fileConfiguration = YamlConfiguration.loadConfiguration(file);
	}
	
	public boolean save() {
		try {
			fileConfiguration.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}
}
