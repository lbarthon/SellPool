package fr.loul.sellpool;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.loul.sellpool.commands.PoolCmd;
import fr.loul.sellpool.commands.SellCmd;
import fr.loul.sellpool.commands.WorthCmd;
import net.milkbowl.vault.economy.Economy;

public class SellPool extends JavaPlugin {

	@Getter
	private static JavaPlugin instance;
	@Getter
	private static Economy economy = null;
	private static String prefix = "§7[§9SellPool§7] ";
	private ConsoleCommandSender console = getServer().getConsoleSender();
	private PluginManager pl = getServer().getPluginManager();
	
	public static Map<Material, ItemPool> MapPool = new HashMap<>();
	
	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();
		registerEvents();
		mapInit();
		decreaseStart();
		if (!setupEconomy()) {
			console.sendMessage(prefix + "§cError! SellPool disabled due to no Vault dependency found!");
			pl.disablePlugin(this);
			return;
		}
		sendConsoleMessage(prefix + "§aLaunched successfully!");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelAllTasks();
		mapSave();
		sendConsoleMessage(prefix + "§aDisabled successfully!");
	}
	
	private void registerEvents() {
		pl.registerEvents(new SellCmd(), this);
		getCommand("worth").setExecutor(new WorthCmd());
		getCommand("pool").setExecutor(new PoolCmd());
	}
	
	private void decreaseStart() {
		new BukkitRunnable() {
			public void run() {
				ConfigurationSection items = getConfig().getConfigurationSection("items");
				for (String keys : items.getKeys(false)) {
					Material m = Material.getMaterial(keys);
					MapPool.get(m).poolDecr();
				}
			}
		}.runTaskTimer(this, 20 * 5, 20 * 5);
	}
	
	private void mapInit() {
		sendConsoleMessage(prefix + "§aLoading items...");
		for (String keys : getConfig().getConfigurationSection("items").getKeys(false)) {
			int max = getConfig().getInt("items." + keys + ".pool_size");
			double maxp = getConfig().getDouble("items." + keys + ".max_price");
			double minp = getConfig().getDouble("items." + keys + ".min_price");
			int act = getConfig().getInt("items." + keys + ".actual");
			int decr = getConfig().getInt("items." + keys + ".decrease");
			int decramount = getConfig().getInt("items." + keys + ".decr_amount");
			String n = getConfig().getString("items." + keys + ".name");
			if (max <= 0) {
				sendConsoleMessage(prefix + "§cError ! PoolSize of " + keys + " equals or lower than 0. Disabling plugin...");
				pl.disablePlugin(this);
				return;
			}
			MapPool.put(Material.getMaterial(keys), new ItemPool(max, maxp, minp, act, decr, decramount, n));
			sendConsoleMessage(prefix + "§a" + keys + " : Loaded.");
		}
	}
	
	private void mapSave() {
		sendConsoleMessage(prefix + "§aSaving items...");
		for (String keys : getConfig().getConfigurationSection("items").getKeys(false)) {
			getConfig().set("items." + keys + ".actual", MapPool.get(Material.getMaterial(keys)).getPool());
		}
		saveConfig();
	}
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
	public static String getCfgStr(String str) {
		return instance.getConfig().getString(str).replace("&", "§");
	}
	
	public static void sendConsoleMessage(String str) {
		instance.getServer().getConsoleSender().sendMessage(str.replace("&", "§"));
	}
}
