package dk.earthgame.TAT.AdminsOnline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

/**
 * AdminsOnline for Bukkit
 *
 * @author TAT
 */
public class AdminsOnline extends JavaPlugin {
	public static PermissionHandler Permissions = null;
    public boolean usePermissions = false;
	public PluginDescriptionFile pdfFile = this.getDescription();
    public Configuration config;
    public static Boolean ShowInfo = true;
    public static Boolean UseOP;
    public static Boolean UsePermissions;
    public static String ShortCommand = "/adminsonline";
    //PermissionGroups = Groupname, Shown Groupname
    public static Map<String, String> PermissionGroups = new HashMap<String, String>();
    //PermissionColors = Groupname, Groupcolor
    public static Map<String, String> PermissionColors = new HashMap<String, String>();

	private final AdminsOnlinePlayerListener playerListener = new AdminsOnlinePlayerListener(this);
	
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	//----------------------------------------------------------
	
    public AdminsOnline(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        folder.mkdirs();
        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }
    
    public void setupPermissions() {
    	// Initialize permissions system
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

    	if(AdminsOnline.Permissions == null) {
		    if(test != null) {
    			AdminsOnline.Permissions = ((Permissions)test).getHandler();
    	    	output("Permission system found.");
		    } else {
    	    	output("Permission system not found.");
		    }
    	}
    }

    public void onEnable() {
        // Register our events
    	PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);

        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");        
        createDefaultConfiguration();
    	loadConfiguration();
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
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
    	if (UseOP) {
        	output("Using OP");
        } else {
        	output("Not using OP!");
        }
    	if (UsePermissions) {
    		output("Searching for Permissions");
    		setupPermissions();
        }
    	if (config.getString("ShortCommand") != null && config.getString("ShortCommand") != "") {
        	ShortCommand = config.getString("ShortCommand");
        	output("Added alias " + config.getString("ShortCommand"));
        } else {
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
    
    public ChatColor playerColor(String player) {
    	String color = PermissionColors.get(Permissions.getGroup(player));
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
    	return ChatColor.WHITE;
    }
    
    public String playerGroup(Player player) {
    	if (UsePermissions) {
			if (PermissionGroups.get(Permissions.getGroup(player.getName())) != null) {
				return PermissionGroups.get(Permissions.getGroup(player.getName()));
			} else {
				return "";
			}
		} else {
			return "";
		}
    }
    
    public Integer playerIsAdmin(String player) {
    	if (player != null) {
    		if (UsePermissions) {
				if (PermissionGroups.get(Permissions.getGroup(player)) != null) {
					return 2;
				}
    		}
			if (UseOP) {
				if (getServer().getPlayer(player).isOp()) {
					return 1;
				} else {
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