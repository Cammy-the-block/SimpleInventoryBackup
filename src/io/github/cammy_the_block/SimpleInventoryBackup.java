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
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleInventoryBackup  extends JavaPlugin {
	
	static List<World> worldsToInclude = new ArrayList<World>();
	static List<World> worldsToExclude = new ArrayList<World>();
	boolean excludeAllWorlds = false;
	static boolean includeAllWorlds = false;
	boolean noWorlds = false;
	boolean noExcludedWorlds = true;
	static boolean backupEnderChests = true;
	boolean individualPlayerOverride = false;
	File f = new File("plugins/SimpleInventoryBackup/playerInventoryBackups.yml");
	static File tempf = new File("plugins/SimpleInventoryBackup/tempPlayerInventoryBackups.yml");
	YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
	static YamlConfiguration temp = YamlConfiguration.loadConfiguration(tempf);

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new TheListener(), this);
		getConfig().addDefault("individualplayeroverride", true);
		getConfig().addDefault("includedworlds", "all");
		getConfig().addDefault("backupenderchests", true);
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
				sender.sendMessage("Restoring all inventories acording to config.");
				for(Player p : Bukkit.getOnlinePlayers()) {
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
			items.add(yaml.getItemStack(p.getUniqueId().toString() + x));
		}
		if (items.isEmpty()){
			for(int x = 0; x < 36; x++){
				items.add(yaml.getItemStack(p.getName() + x));
			}
		}
		p.getInventory().setContents(items.toArray(new ItemStack[items.size()]));

		List<ItemStack> armorItems = new ArrayList<ItemStack>();
		for(int x = 0; x < 4; x++){
			armorItems.add(yaml.getItemStack(p.getUniqueId().toString() + "a" + x));
		}
		if (items.isEmpty()){
			for(int x = 0; x < 4; x++){
				armorItems.add(yaml.getItemStack(p.getName() + "a" + x));
			}
		}
		p.getInventory().setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));	
		
		if (backupEnderChests){
			List<ItemStack> enderChestItems = new ArrayList<ItemStack>();
			for(int x = 0; x < 27; x++){
				enderChestItems.add(yaml.getItemStack(p.getUniqueId().toString() + "e" + x));
			}
			p.getEnderChest().setContents(enderChestItems.toArray(new ItemStack[enderChestItems.size()]));	
		}
		
	}
	//
	//INVENTORY BACKUP METHODS
	//
	public boolean backupInventory(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1){
			if(args[0].equalsIgnoreCase("all")){
				try {
					yaml = new YamlConfiguration();
					yaml.loadFromString(temp.saveToString());
					yaml.save(f);
				} catch (InvalidConfigurationException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender.sendMessage("Backing up all inventories acording to config.");
				for(Player p : Bukkit.getOnlinePlayers()) {
					backupInventoryIfSupposedTo(p, yaml);
				}
				
				try {
					yaml.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender.sendMessage("Done backing up all inventories.");
				return true;
			}
			else {
				Player p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					if(individualPlayerOverride){
						backupInventory(p, yaml);
						sender.sendMessage(p + "'s inventory has been backed up.");
					}
					else {
						sender.sendMessage(backupInventoryIfSupposedTo(p, yaml));
					}
					try {
						yaml.save(f);
					} catch (IOException e) {
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
	public static String backupInventoryIfSupposedTo(Player p, YamlConfiguration yaml){ //Checks if config permits player's inventory to be backed up
		if (includeAllWorlds == true && !worldsToExclude.contains(p.getWorld())){
			backupInventory(p, yaml);
			return p.getName() + "'s inventory has been backed up.";
		}
		else if (worldsToInclude.contains(p.getWorld())){
			backupInventory(p, yaml);
			return p.getName() + "'s inventory has been backed up.";
		}
		else{

			return p.getName() +"'s inventory has not been backed up due to config settings.";
		}
	}
	public static void backupInventory(Player p, YamlConfiguration yaml){ //backs up player's inventory regardless of config
		for (int x = 0; x < p.getInventory().getContents().length; x++){
			yaml.set(p.getUniqueId().toString() + x, p.getInventory().getContents()[x]);
		}
		for (int x = 0; x < p.getInventory().getArmorContents().length; x++){
			yaml.set(p.getUniqueId().toString() + "a" + x, p.getInventory().getArmorContents()[x]);
		}
		if (backupEnderChests){
			for (int x = 0; x < p.getEnderChest().getContents().length; x++){
				yaml.set(p.getUniqueId().toString() + "e" + x, p.getEnderChest().getContents()[x]);
			}
		}
	}
	public void backupInventoriesFromTempYml(){
		
	}
	//
	//MISC METHODS
	//
	public void readConfig(){
		individualPlayerOverride = getConfig().getBoolean("individualplayeroverride");
		backupEnderChests = getConfig().getBoolean("backupenderchests");
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
		if (worlds != null){
			for (String w: worlds.split(",")){
				worldsToExclude.add(Bukkit.getWorld(w.trim()));
			}
		}
		
	}
}
class TheListener implements Listener {
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent event)
	{
		SimpleInventoryBackup.backupInventoryIfSupposedTo(event.getPlayer(), SimpleInventoryBackup.temp);
		try {
			SimpleInventoryBackup.temp.save(SimpleInventoryBackup.tempf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}