package fr.loul.sellpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.loul.sellpool.commands.PoolCmd;
import fr.loul.sellpool.commands.SellCmd;
import fr.loul.sellpool.commands.WorthCmd;
import net.milkbowl.vault.economy.Economy;

public class SellPool extends JavaPlugin {

	private static final String PREFIX = "§7[§9SellPool§7] ";

	@Getter
	private static SellPool instance;
	@Getter
	private static Economy economy = null;
	private final ConsoleCommandSender console = getServer().getConsoleSender();
	private final PluginManager pl = getServer().getPluginManager();

	@Getter
	private Map<Material, ItemPool> mapPool;
	@Getter
	private ScheduledExecutorService executorService;

	@Override
	public void onEnable() {
		instance = this;
		this.mapPool = new HashMap<>();
		this.executorService = new ScheduledThreadPoolExecutor(1);

		saveDefaultConfig();
		registerEvents();
		mapInit();
		decreaseStart();
		if (!setupEconomy()) {
			console.sendMessage(PREFIX + "§cError! SellPool disabled due to no Vault dependency found!");
			pl.disablePlugin(this);
			return;
		}
		sendConsoleMessage(PREFIX + "§aLaunched successfully!");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelAllTasks();
		mapSave();
		sendConsoleMessage(PREFIX + "§aDisabled successfully!");
	}
	
	private void registerEvents() {
		pl.registerEvents(new SellCmd(), this);
		getCommand("worth").setExecutor(new WorthCmd());
		getCommand("pool").setExecutor(new PoolCmd());
	}
	
	private void decreaseStart() {
		this.executorService.scheduleAtFixedRate(() -> {
			ConfigurationSection items = getConfig().getConfigurationSection("items");
			for (String keys : items.getKeys(false)) {
				Material m = Material.getMaterial(keys);
				this.mapPool.get(m).poolDecr();
			}
		}, 1, 5, TimeUnit.SECONDS);

		this.executorService.scheduleAtFixedRate(this::mapSave, 5, 5, TimeUnit.MINUTES);
	}
	
	private void mapInit() {
		sendConsoleMessage(PREFIX + "§aLoading items...");
		for (String keys : getConfig().getConfigurationSection("items").getKeys(false)) {
			int max = getConfig().getInt("items." + keys + ".pool_size");
			double maxp = getConfig().getDouble("items." + keys + ".max_price");
			double minp = getConfig().getDouble("items." + keys + ".min_price");
			int act = getConfig().getInt("items." + keys + ".actual");
			int decr = getConfig().getInt("items." + keys + ".decrease");
			int decramount = getConfig().getInt("items." + keys + ".decr_amount");
			String n = getConfig().getString("items." + keys + ".name");
			if (max <= 0) {
				sendConsoleMessage(PREFIX + "§cError ! PoolSize of " + keys + " equals or lower than 0. Disabling plugin...");
				pl.disablePlugin(this);
				return;
			}
			this.mapPool.put(Material.getMaterial(keys), new ItemPool(max, maxp, minp, act, decr, decramount, n));
			sendConsoleMessage(PREFIX + "§a" + keys + " : Loaded.");
		}
	}
	
	private void mapSave() {
		sendConsoleMessage(PREFIX + "§aSaving items...");
		for (String keys : getConfig().getConfigurationSection("items").getKeys(false)) {
			getConfig().set("items." + keys + ".actual", this.mapPool.get(Material.getMaterial(keys)).getPool());
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
