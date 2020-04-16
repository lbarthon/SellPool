package fr.loul.sellpool.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import fr.loul.sellpool.SellPool;

public class SellCmd implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (e.getPlayer() instanceof Player) {
			Player p = e.getPlayer();
			String str = e.getMessage().toLowerCase().replace(" ", "");
			if (!str.startsWith("/sell")) return;
			
			if (!str.equalsIgnoreCase("/sell")) {
				if (!str.equalsIgnoreCase("/sellall")) {
					if (!str.equalsIgnoreCase("/sellhand")) {
						e.setCancelled(true);
						p.sendMessage(SellPool.getCfgStr("ErrorMessage"));
						return;
					}
				}
			}
			
			if (p.hasPermission("sellpool.sell") || p.hasPermission("sellpool.*") || p.hasPermission("*")) {
				e.setCancelled(true);
				
				if (str.equalsIgnoreCase("/sellall")) {
					int sold = 0;
					float totalprice = 0;
					int nb = 0;
					for (ItemStack i : p.getInventory().getContents().clone()) {
						nb++;
						if (i == null) continue;
						if (i.getType() == null) continue;
						if (i.getType().equals(Material.AIR)) continue;
						if (nb > 36) break;
						
						Material m = i.getType();
						if (SellPool.MapPool.containsKey(m)) {
							sold++;
							float pr = SellPool.MapPool.get(m).getActualPrice();
							int pri = (int) (pr * 100);
							float price = (float) pri / 100;
							int nbitems = 0;
							int nbr = 0;
							for (ItemStack itemstack : p.getInventory().getContents().clone()) {
								nbr++;
								if (itemstack == null) continue;
								if (itemstack.getType() == null) continue;
								if (itemstack.getType().equals(Material.AIR)) continue;
								if (nbr > 36) break;
								
								if (itemstack.getType() == m) {
									while (itemstack.getAmount() > 0) {
										itemstack.setAmount(itemstack.getAmount() - 1);
										nbitems++;
									}
								}
							}
							SellPool.getEconomy().depositPlayer(p, price * nbitems);
							SellPool.MapPool.get(m).poolIncr(nbitems);
							SellPool.MapPool.get(m).updatePrice();
							totalprice = totalprice + (nbitems * price);
						}
					}
					if (sold > 0) p.sendMessage(SellPool.getCfgStr("SellAllSuccessfullySold").replace("%price%", String.valueOf(totalprice)));
					else p.sendMessage(SellPool.getCfgStr("SellAllNothingToSell"));
					return;
				}
				
				if (p.getInventory().getItemInMainHand() == null) {
					p.sendMessage(SellPool.getCfgStr("NoItemInHand"));
					return;
				}
				if (p.getInventory().getItemInMainHand().getType() == null) {
					p.sendMessage(SellPool.getCfgStr("NoItemInHand"));
					return;
				}
				if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					p.sendMessage(SellPool.getCfgStr("NoItemInHand"));
					return;
				}
				
				Material m = p.getInventory().getItemInMainHand().getType();
				if (SellPool.MapPool.containsKey(m)) {
					float pr = SellPool.MapPool.get(m).getActualPrice();
					int pri = (int) (pr * 100);
					float price = (float) pri / 100;
					int nbitems = 0;
					int nb = 0;
					for (ItemStack itemstack : p.getInventory().getContents().clone()) {
						nb++;
						if (itemstack == null) continue;
						if (itemstack.getType() == null) continue;
						if (itemstack.getType().equals(Material.AIR)) continue;
						if (nb > 36) break;
						
						if (itemstack.getType() == m) {
							while (itemstack.getAmount() > 0) {
								itemstack.setAmount(itemstack.getAmount() - 1);
								nbitems++;
							}
						}
					}
					SellPool.getEconomy().depositPlayer(p, price * nbitems);
					SellPool.MapPool.get(m).poolIncr(nbitems);
					SellPool.MapPool.get(m).updatePrice();
					p.sendMessage(SellPool.getCfgStr("SuccessfullySold")
							.replace("%price%", String.valueOf(price * nbitems))
							.replace("%amount%", String.valueOf(nbitems))
							.replace("%item%", SellPool.MapPool.get(m).getName()));
				} else {
					p.sendMessage(SellPool.getCfgStr("ItemNotListed"));
				}
			} else {
				p.sendMessage(SellPool.getCfgStr("NoPermission"));
			}
		}
	}
}
