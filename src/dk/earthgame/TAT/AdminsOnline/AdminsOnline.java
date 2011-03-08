package dk.earthgame.TAT.AdminsOnline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
 * AdminsOnline for Bukkit
 *
 * @author TAT
 */
public class AdminsOnline extends JavaPlugin {
	public Permissions Permissions = null;
    public boolean usePermissions = false;
	public PluginDescriptionFile pdfFile;
    public Configuration config;
    public static Boolean ShowInfo = true;
    public static Boolean UseOP = true;
    public static Boolean UsePermissions = true;
    public static Boolean ShowOnLogin = true;
	public AdminsOnlineWorker worker = new AdminsOnlineWorker(this);
    public static List<String> ShortCommand;
    //PermissionGroups = Groupname, Shown Groupname
    public static Map<String, String> PermissionGroups = new HashMap<String, String>();
    //PermissionColors = Groupname, Groupcolor
    public static Map<String, String> PermissionColors = new HashMap<String, String>();

	private AdminsOnlineJoinExecutor joinExecutor;
	
	protected static final Logger log = Logger.getLogger("Minecraft.AdminsOnline");
	
	//----------------------------------------------------------
	
	public AdminsOnline() {
    	joinExecutor = new AdminsOnlineJoinExecutor(worker);
	}

    public void onEnable() {
        // Register our events
    	getCommand("reloadao").setExecutor(new AdminsOnlineCommandExecutor(this,worker));
    	getCommand("adminsonline").setExecutor(new AdminsOnlineCommandExecutor(this,worker));
    	
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, joinExecutor, Priority.Normal, this);
        
        pdfFile = this.getDescription();
        
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");        
        createDefaultConfiguration();
    	loadConfiguration();
    }
    
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	getCommand("reloadao").setExecutor(new AdminsOnlineDisabled(this));
    	getCommand("adminsonline").setExecutor(new AdminsOnlineDisabled(this));
    	ShortCommand.clear();
    	getCommand("adminsonline").setAliases(ShortCommand);
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
         	ShortCommand.add(config.getString("ShortCommand"));
        	getCommand("adminsonline").setAliases(ShortCommand);
        	output("Added alias " + config.getString("ShortCommand"));
        } else {
        	ShortCommand.clear();
        	output("No alias added!");
        }
		PermissionGroups = (Map<String, String>)config.getProperty("PermissionGroups");
		PermissionColors = (Map<String, String>)config.getProperty("PermissionColors");
    }
    
    private void createDefaultConfiguration() {
    	String name = "config.yml";
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()) {
            InputStream input = AdminsOnline.class.getResourceAsStream("/config/" + name);
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
    
    public ChatColor playerColor(String world,String player) {
    	String color = PermissionColors.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player));
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
    
    public String playerGroup(String world,Player player) {
    	if (UsePermissions) {
			if (PermissionGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName())) != null) {
				return PermissionGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player.getName()));
			} else {
				return "";
			}
		} else {
			return "";
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
    
    public Integer playerIsAdmin(String world,String player) {
    	if (player != null) {
    		if (UsePermissions) {
    			if (com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world, player) != null) {
    				if (PermissionGroups.get(com.nijikokun.bukkit.Permissions.Permissions.Security.getGroup(world,player)) != null) {
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