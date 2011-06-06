package dk.earthgame.TAT.PlayersOnline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * PlayersOnline for Bukkit
 *
 * @author TAT
 * @version 1.0
 */
public class PlayersOnline extends JavaPlugin {
	Permissions Permissions = null;
	boolean UsePermissions = false;
	PluginDescriptionFile pdfFile;
	Configuration config;
	boolean ShowInfo = true;
	boolean UseOP = true;
	boolean ShowOnLogin = true;
	boolean Multiworld = true;
	String msg_Admin;
	String msg_Admins;
	String msg_NoAdmins;
	String msg_Player;
	String msg_Players;
	String msg_Online;
	String msg_Offline;
	String msg_NotAdmin;
	String msg_Group;
	String msg_World;
	
	PlayersOnlineWorker worker = new PlayersOnlineWorker(this);
	List<String> ShortCommand = new ArrayList<String>();
	//AdminGroups = Groupname, Shown Groupname
	Map<String, String> AdminGroups = new HashMap<String, String>();
	//GroupColors = Groupname, Groupcolor
	Map<String, String> GroupColors = new HashMap<String, String>();
	//Player = Groupname, Shown Groupname
	Map<String, String> PlayerGroups = new HashMap<String, String>();

	private PlayersOnlineJoinExecutor joinExecutor;
	
	protected static final Logger log = Logger.getLogger("Minecraft.AdminsOnline");
	
	//----------------------------------------------------------
	
	public PlayersOnline() {
		joinExecutor = new PlayersOnlineJoinExecutor(worker, this);
	}

	public void onEnable() {
		//Create folder for config
		this.getDataFolder().mkdir();
		
		// Register our events
		getCommand("reloadao").setExecutor(new PlayersOnlineCommandExecutor(this,worker));
		getCommand("admins").setExecutor(new PlayersOnlineCommandExecutor(this,worker));
		getCommand("players").setExecutor(new PlayersOnlineCommandExecutor(this,worker));
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, joinExecutor, Priority.Normal, this);
		
		pdfFile = this.getDescription();
		
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");		
		createDefaultConfiguration();
		loadConfiguration();
	}
	
	public void onDisable() {
		// reloadao
		getCommand("reloadao").setExecutor(new PlayersOnlineDisabled(this));
		getCommand("reloadao").setUsage(ChatColor.RED + "PlayersOnline is disabled");
		// admins
		getCommand("admins").setExecutor(new PlayersOnlineDisabled(this));
		getCommand("admins").setUsage(ChatColor.RED + "PlayersOnline is disabled");
		// players
		getCommand("players").setExecutor(new PlayersOnlineDisabled(this));
		getCommand("players").setUsage(ChatColor.RED + "PlayersOnline is disabled");
		// ShortCommand
		ShortCommand.clear();
		getCommand("admins").setAliases(ShortCommand);
		// Console message
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}
	
	public void setupPermissions() {
		// Initialize permissions system
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if(Permissions == null) {
			if(test != null) {
				Permissions = (Permissions)test;
				output("Permission system found.");
			} else {
				output("Permission system not found.");
			}
		}
	}
	
	public void output(String msg) {
		if (ShowInfo) {
			log.info(pdfFile.getName() + ": " + msg);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadConfiguration() {
		config = new Configuration(new File(this.getDataFolder(), "config.yml"));
		config.load();
		ShowInfo = config.getBoolean("ShowInfo",true);
		UseOP = config.getBoolean("UseOP",true);
		UsePermissions = config.getBoolean("UsePermissions",false);
		ShowOnLogin = config.getBoolean("ShowOnLogin",true);
		Multiworld = config.getBoolean("Multiworld",true);
		if (UseOP) {
			output("Using OP");
		} else {
			output("Not using OP!");
		}
		if (UsePermissions && Permissions == null) {
			output("Searching for Permissions");
			setupPermissions();
		}
		if (ShowOnLogin) {
			output("Shows admins online on login");
		}
		if (config.getString("ShortCommand") != null && config.getString("ShortCommand") != "") {
			ShortCommand.clear();
		 	ShortCommand.add(config.getString("ShortCommand").replace("/", ""));
		 	getCommand("admins").setAliases(ShortCommand);
			//output("Added alias /" + config.getString("ShortCommand").replace("/", ""));
		} else {
			ShortCommand.clear();
			//output("No alias added!");
		}
		
		//Messages
		msg_Admin = config.getString("Messages.Admin", "%f Online Admin");
		msg_Admins = config.getString("Messages.Admins", "%f Online Admins");
		msg_NoAdmins = config.getString("Messages.NoAdmins", "No admins online");
		msg_Player = config.getString("Messages.Player", "%f Online Player");
		msg_Players = config.getString("Messages.Players", "%f Online Players");
		msg_Online = config.getString("Messages.Online", "%s is online!");
		msg_Offline = config.getString("Messages.Offline", "%s isn't online!");
		msg_NotAdmin = config.getString("Messages.NotAdmin", "%s isn't an admin!");
		msg_Group = config.getString("Messages.Group", "Group: %s");
		msg_World = config.getString("Messages.World", "World: %s");
		
		AdminGroups = (Map<String, String>)config.getProperty("AdminGroups");
		GroupColors = (Map<String, String>)config.getProperty("GroupColors");
	}
	
	private void createDefaultConfiguration() {
		String name = "config.yml";
		File actual = new File(getDataFolder(), name);
		if (!actual.exists()) {
			InputStream input = PlayersOnline.class.getResourceAsStream("/config/" + name);
			if (input != null) {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					
					log.info(pdfFile.getName() + ": Default config file created!");
				} catch (IOException e) {
					e.printStackTrace();
					log.info(pdfFile.getName() + ": Error creating config file!");
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (IOException e) {}

					try {
						if (output != null)
							output.close();
					} catch (IOException e) {}
				}
			}
		} else {
			output("Config file found!");
		}
	}
	
	/**
	 * 
	 * @deprecated
	 * @param world
	 * @param player
	 * @return
	 * @see #playerColor(String)
	 */
	public ChatColor playerColor(String world,String player) {
		String color = GroupColors.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player));
		if (color != null) {
			if (color.equalsIgnoreCase("aqua"))
				return ChatColor.AQUA;
			if (color.equalsIgnoreCase("black"))
				return ChatColor.BLACK;
			if (color.equalsIgnoreCase("blue"))
				return ChatColor.BLUE;
			if (color.equalsIgnoreCase("dark_aqua"))
				return ChatColor.DARK_AQUA;
			if (color.equalsIgnoreCase("dark_blue"))
				return ChatColor.DARK_BLUE;
			if (color.equalsIgnoreCase("dark_gray"))
				return ChatColor.DARK_GRAY;
			if (color.equalsIgnoreCase("dark_green"))
				return ChatColor.DARK_GREEN;
			if (color.equalsIgnoreCase("dark_purple"))
				return ChatColor.DARK_PURPLE;
			if (color.equalsIgnoreCase("dark_red"))
				return ChatColor.DARK_RED;
			if (color.equalsIgnoreCase("gold"))
				return ChatColor.GOLD;
			if (color.equalsIgnoreCase("gray"))
				return ChatColor.GRAY;
			if (color.equalsIgnoreCase("green"))
				return ChatColor.GREEN;
			if (color.equalsIgnoreCase("light_purple"))
				return ChatColor.LIGHT_PURPLE;
			if (color.equalsIgnoreCase("red"))
				return ChatColor.RED;
			if (color.equalsIgnoreCase("yellow"))
				return ChatColor.YELLOW;
		}
		return ChatColor.WHITE;
	}
	
	public ChatColor playerColor(String group) {
		String color = GroupColors.get(group);
		if (color != null) {
			if (color.equalsIgnoreCase("aqua"))
				return ChatColor.AQUA;
			if (color.equalsIgnoreCase("black"))
				return ChatColor.BLACK;
			if (color.equalsIgnoreCase("blue"))
				return ChatColor.BLUE;
			if (color.equalsIgnoreCase("dark_aqua"))
				return ChatColor.DARK_AQUA;
			if (color.equalsIgnoreCase("dark_blue"))
				return ChatColor.DARK_BLUE;
			if (color.equalsIgnoreCase("dark_gray"))
				return ChatColor.DARK_GRAY;
			if (color.equalsIgnoreCase("dark_green"))
				return ChatColor.DARK_GREEN;
			if (color.equalsIgnoreCase("dark_purple"))
				return ChatColor.DARK_PURPLE;
			if (color.equalsIgnoreCase("dark_red"))
				return ChatColor.DARK_RED;
			if (color.equalsIgnoreCase("gold"))
				return ChatColor.GOLD;
			if (color.equalsIgnoreCase("gray"))
				return ChatColor.GRAY;
			if (color.equalsIgnoreCase("green"))
				return ChatColor.GREEN;
			if (color.equalsIgnoreCase("light_purple"))
				return ChatColor.LIGHT_PURPLE;
			if (color.equalsIgnoreCase("red"))
				return ChatColor.RED;
			if (color.equalsIgnoreCase("yellow"))
				return ChatColor.YELLOW;
		}
		return ChatColor.WHITE;
	}
	
	/**
	 * 
	 * @deprecated
	 * @param world
	 * @param player
	 * @return
	 * @see #getPlayerGroups(String, Player)
	 */
	public String playerGroup(String world,Player player) {
		if (UsePermissions) {
			if (AdminGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName())) != null) {
				return AdminGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName()));
			} else if (PlayerGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName())) != null) {
				return PlayerGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName()));
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	/**
	 * Get the groups the player is in
	 * 
	 * @param world
	 * @param player
	 * @since 0.5.1
	 * @return
	 */
	public String[] getPlayerGroups(String world,Player player) {
		if (UsePermissions) {
			if (Permissions.getHandler().getGroups(world,player.getName()) != null) {
				return Permissions.getHandler().getGroups(world,player.getName());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	boolean playerSaved(String player) {
		if (getServer().getPlayer(player) != null) {
			return true;
		}
		try {
			FileInputStream test = new FileInputStream("world/players/"+player+".dat");
		}
		catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Is the player an admin?
	 * 
	 * @param world
	 * @param player
	 * @return 2: Permissions admin - 1: OP - 0: not admin
	 */
	@SuppressWarnings("deprecation")
	public int playerIsAdmin(String world,String player) {
		if (player != null) {
			if (UsePermissions) {
				if (com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world, player) != null) {
					if (AdminGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player)) != null) {
						return 2;
					}
				}
			}
			if (UseOP) {
				if (playerSaved(player)) {
					try {
						if (getServer().getPlayer(player) != null) {
							if (getServer().getPlayer(player).isOp()) {
								return 1;
							} else {
								return 0;
							}
						}
						FileReader open = new FileReader("ops.txt");
						BufferedReader read = new BufferedReader(open);
						String OP = null;
						try {
							while ((OP = read.readLine()) != null) {
								if (OP.equalsIgnoreCase(player)) {
									return 1;
								}
							}
							read.close();
							open.close();
						} catch (IOException e) {
							log.warning(e.toString());
						}
					} catch (FileNotFoundException e) {
						log.warning("Couldn't get OPs");
					}
					return 0;
				} else {
					FileReader open;
					try {
						open = new FileReader("ops.txt");
						BufferedReader read = new BufferedReader(open);
						String OP = null;
						try {
							while ((OP = read.readLine()) != null) {
								if (OP.equalsIgnoreCase(player)) {
									return 1;
								}
							}
							read.close();
							open.close();
						} catch (IOException e) {
							log.warning(e.toString());
						}
					} catch (FileNotFoundException e1) {
						log.warning(e1.toString());
					}
					return 0;
				}
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}