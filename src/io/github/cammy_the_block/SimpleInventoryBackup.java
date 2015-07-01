package io.github.cammy_the_block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleInventoryBackup  extends JavaPlugin {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new TheListener(), this);
	}
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(new TheListener());
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("backupInventory")) { // If the player typed /basic then do the following...
			return backupInventory(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("restoreInventory")) { // If the player typed /basic then do the following...
			return restoreInventory(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("requestInventoryRestore")) { // If the player typed /basic then do the following...
			return requestInventoryRestore(sender, cmd, label, args);
		}
		return false; 
	}
	private boolean requestInventoryRestore(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be run by a player.");
			return true;
		} 
		else {
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.hasPermission("simpleinventorybackup.seerequest")){
					p.sendMessage(sender.getName() + " has requested a inventory restore. To do so type /restoreinventroy " + sender.getName());
				}
			}
			return true;
		}
	}
	private boolean restoreInventory(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("all")){
				sender.sendMessage("Restoring all inventories.");
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					List<ItemStack> items = new ArrayList<ItemStack>();
					for(int x = 0; x < 36; x++){
						items.add(getConfig().getItemStack(p.getName() + x));
					}
					p.getInventory().setContents(items.toArray(new ItemStack[items.size()]));

					List<ItemStack> armorItems = new ArrayList<ItemStack>();
					for(int x = 0; x < 4; x++){
						armorItems.add(getConfig().getItemStack(p.getName() + "a" + x));
					}
					p.getInventory().setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));	
				}
				sender.sendMessage("All inventories have been restored.");
				return true;
			}
			else {
				Player p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					List<ItemStack> items = new ArrayList<ItemStack>();
					for(int x = 0; x < 36; x++){
						items.add(getConfig().getItemStack(p.getName() + x));
					}
					p.getInventory().setContents(items.toArray(new ItemStack[items.size()]));

					List<ItemStack> armorItems = new ArrayList<ItemStack>();
					for(int x = 0; x < 4; x++){
						armorItems.add(getConfig().getItemStack(p.getName() + "a" + x));
					}
					p.getInventory().setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));
					sender.sendMessage(p.getName()+"'s inventory has been restored up.");
					return true;
				} else {
					sender.sendMessage(args[0] + " is offline or does not exist. Sorry");
					return true;
				}

			}
		}
		return false;
	}
	public boolean backupInventory(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if(args[0].equalsIgnoreCase("all")){
				sender.sendMessage("Backing up all inventories");
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					for (int x = 0; x < p.getInventory().getContents().length; x++){
						getConfig().set(p.getName() + x, p.getInventory().getContents()[x]);
					}
					for (int x = 0; x < p.getInventory().getArmorContents().length; x++){
						getConfig().set(p.getName() + "a" + x, p.getInventory().getArmorContents()[x]);
					}
				}
				saveConfig();
				sender.sendMessage("Done backing up all inventories.");
				return true;
			}
			else {
				Player p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					for (int x = 0; x < p.getInventory().getContents().length; x++){
						getConfig().set(p.getName() + x, p.getInventory().getContents()[x]);
					}
					for (int x = 0; x < p.getInventory().getArmorContents().length; x++){
						getConfig().set(p.getName() + "a" + x, p.getInventory().getArmorContents()[x]);
					}
					saveConfig();
					sender.sendMessage(p.getName() + "'s inventory is backed up.");
					return true;
				} else {
					sender.sendMessage(args[0] + " is offline or does not exist. Sorry");
					return true;
				}

			}
		}
		return false;
	}
}
class TheListener implements Listener {

}