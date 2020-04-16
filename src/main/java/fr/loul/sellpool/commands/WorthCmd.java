package fr.loul.sellpool.commands;

import fr.loul.sellpool.ItemPool;
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
			if (SellPool.getInstance().getMapPool().containsKey(m)) {
				ItemPool item = SellPool.getInstance().getMapPool().get(m);
				item.updatePrice();
				float pr = item.getActualPrice();
				int pri = (int) (pr * 100);
				float price = (float) pri / 100;
				p.sendMessage(SellPool.getCfgStr("Worth")
						.replace("%price%", String.valueOf(price))
						.replace("%item%", item.getName()));
			} else {
				p.sendMessage(SellPool.getCfgStr("ItemNotListed"));
			}
		} else {
			p.sendMessage(SellPool.getCfgStr("NoPermission"));
		}
		return true;
	}
}
