package fr.loul.sellpool.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.loul.sellpool.SellPool;

public class PoolCmd implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			SellPool.sendConsoleMessage(SellPool.getCfgStr("ConsoleCantSend"));
			return true;
		}
		Player p = (Player) sender;
		
		if (sender.hasPermission("sellpool.pool") || sender.hasPermission("sellpool.*") || sender.hasPermission("*")) {
			Material m = p.getInventory().getItemInMainHand().getType();
			if (SellPool.MapPool.containsKey(m)) {
				p.sendMessage(SellPool.getCfgStr("Pool")
						.replace("%pool%", String.valueOf((int) (SellPool.MapPool.get(m).getPool())))
						.replace("%max%", String.valueOf(SellPool.MapPool.get(m).getMax()))
						.replace("%item%", SellPool.MapPool.get(m).getName()));
			} else {
				p.sendMessage(SellPool.getCfgStr("ItemNotListed"));
			}
		} else {
			p.sendMessage(SellPool.getCfgStr("NoPermission"));
		}
		return true;
	}
}
