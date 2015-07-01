package io.github.cammy_the_block;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleInventoryBackup  extends JavaPlugin {
	
	List<World> worldsToInclude = new ArrayList<World>();
	List<World> worldsToExclude = new ArrayList<World>();
	boolean excludeAllWorlds = false;
	boolean includeAllWorlds = false;
	boolean noWorlds = false;
	boolean noExcludedWorlds = true;
	boolean individualPlayerOverride = false;
	File f = new File("plugins/SimpleInventoryBackup/playerInventoryBackups.yml");
	YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new TheListener(), this);
		getConfig().addDefault("individualplayeroverride", true);
		getConfig().addDefault("includedworlds", "all");
		getConfig().options().copyDefaults(true);
		saveConfig();
		readConfig();
	}
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(new TheListener());
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
	//
	//INVENTORY RESTORE METHODS
	//
	private boolean restoreInventory(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("all")){
				sender.sendMessage("Restoring all inventories acording.");
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					restoreInventoryIfSupposedTo(p);
				}
				sender.sendMessage("All inventories have been restored.");
				return true;
			}
			else {
				Player p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					if(individualPlayerOverride){
						restoreInventory(p);
						sender.sendMessage(p + "'s inventory has been restored.");
					}
					else {
						sender.sendMessage(restoreInventoryIfSupposedTo(p));
					}
					return true;
				} else {
					sender.sendMessage(args[0] + " is offline or does not exist. Sorry");
					return true;
				}

			}
		}
		return false;
	}
	public String restoreInventoryIfSupposedTo(Player p){ //Checks if config permits player's inventory to be backed up
		if (includeAllWorlds == true && !worldsToExclude.contains(p.getWorld())){
			restoreInventory(p);
			return p.getName() + "'s inventory has been restored.";
		}
		else if (worldsToInclude.contains(p.getWorld())){
			restoreInventory(p);
			return p.getName() + "'s inventory has been restored.";
		}
		else{
			return p.getName() +"'s inventory has not been restored due to config settings.";
		}
	}
	public void restoreInventory(Player p){
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(int x = 0; x < 36; x++){
			items.add(yaml.getItemStack(p.getName() + x));
		}
		p.getInventory().setContents(items.toArray(new ItemStack[items.size()]));

		List<ItemStack> armorItems = new ArrayList<ItemStack>();
		for(int x = 0; x < 4; x++){
			armorItems.add(yaml.getItemStack(p.getName() + "a" + x));
		}
		p.getInventory().setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));	
	}
	//
	//INVENTORY BACKUP METHODS
	//
	public boolean backupInventory(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if(args[0].equalsIgnoreCase("all")){
				sender.sendMessage("Backing up all inventories");
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					backupInventoryIfSupposedTo(p);
				}
				try {
					yaml.save(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sender.sendMessage("Done backing up all inventories.");
				return true;
			}
			else {
				Player p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					if(individualPlayerOverride){
						backupInventory(p);
						sender.sendMessage(p + "'s inventory has been backed up.");
					}
					else {
						sender.sendMessage(backupInventoryIfSupposedTo(p));
					}
					try {
						yaml.save(f);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					sender.sendMessage(args[0] + " is offline or does not exist. Sorry");
					return true;
				}

			}
		}
		return false;
	}
	public String backupInventoryIfSupposedTo(Player p){ //Checks if config permits player's inventory to be backed up
		if (includeAllWorlds == true && !worldsToExclude.contains(p.getWorld())){
			backupInventory(p);
			return p.getName() + "'s inventory has been backed up.";
		}
		else if (worldsToInclude.contains(p.getWorld())){
			backupInventory(p);
			return p.getName() + "'s inventory has been backed up.";
		}
		else{

			return p.getName() +"'s inventory has not been backed up due to config settings.";
		}
	}
	public void backupInventory(Player p){ //backs up player's inventory regardless of config
		for (int x = 0; x < p.getInventory().getContents().length; x++){
			yaml.set(p.getName() + x, p.getInventory().getContents()[x]);
		}
		for (int x = 0; x < p.getInventory().getArmorContents().length; x++){
			yaml.set(p.getName() + "a" + x, p.getInventory().getArmorContents()[x]);
		}
	}
	
	//
	//MISC METHODS
	//
	public void readConfig(){
		individualPlayerOverride = getConfig().getBoolean("individualplayeroverride");
		String worlds = getConfig().getString("includedworlds");
		if(worlds == null){
			noWorlds = true;
		}
		else if (worlds.equalsIgnoreCase("all")){
			includeAllWorlds = true;
		}
		else {
			for (String w: worlds.split(",")){
				worldsToInclude.add(Bukkit.getWorld(w));
			}
		}
		
		
		worlds = getConfig().getString("excludedworlds");
		if(worlds != null){
			for (String w: worlds.split(",")){
				worldsToExclude.add(Bukkit.getWorld(w.trim()));
			}
		}
		
	}
}
class TheListener implements Listener {

}