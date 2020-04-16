package fr.loul.sellpool.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.loul.sellpool.SellPool;

public class WorthCmd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			SellPool.sendConsoleMessage(SellPool.getCfgStr("ConsoleCantSend"));
			return true;
		}
		Player p = (Player) sender;
		if (sender.hasPermission("sellpool.worth") || sender.hasPermission("sellpool.*") || sender.hasPermission("*")) {
			Material m = p.getInventory().getItemInMainHand().getType();
			if (SellPool.MapPool.containsKey(m)) {
				SellPool.MapPool.get(m).updatePrice();
				float pr = SellPool.MapPool.get(m).getActualPrice();
				int pri = (int) (pr * 100);
				float price = (float) pri / 100;
				p.sendMessage(SellPool.getCfgStr("Worth")
						.replace("%price%", String.valueOf(price))
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
