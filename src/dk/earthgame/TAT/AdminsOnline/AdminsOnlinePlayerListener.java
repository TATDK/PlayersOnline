package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author TAT
 */
public class AdminsOnlinePlayerListener extends PlayerListener {
	private final AdminsOnline plugin;

	public int playerCount(){
		Player players[] = plugin.getServer().getOnlinePlayers();
		int x = 0;
		for(Player hurrdurr: players){
			if (plugin.playerIsAdmin(hurrdurr.getName()) > 0) {
				x++;
			}
		}
		return x;
	}
	 
	public AdminsOnlinePlayerListener(AdminsOnline instance) {
		plugin = instance;
	}

	public void onPlayerCommand(PlayerChatEvent event) {
  		Player player = event.getPlayer();
  		String[] message = event.getMessage().split(" ");
  		if (message[0].equalsIgnoreCase("/reloadao")) {
  			event.setCancelled(true);
  			if (plugin.playerIsAdmin(player.getName()) > 0) {
	            try {
	            	plugin.loadConfiguration();
	                player.sendMessage(ChatColor.WHITE + plugin.pdfFile.getName() + ChatColor.GREEN +": Configuration reloaded.");
	            } catch (Throwable t) {
	                player.sendMessage(ChatColor.WHITE + plugin.pdfFile.getName() + ChatColor.RED + "Error while reloading: " + t.getMessage());
	            }
  			} else {
  				player.sendMessage(ChatColor.RED + "You don't have access to this command!");
  			}
  		}
  		if (message[0].equalsIgnoreCase("/adminsonline") || message[0].equalsIgnoreCase(AdminsOnline.ShortCommand)) {
			event.setCancelled(true);
			if (message.length > 1) {
				Integer adminStatus = plugin.playerIsAdmin(message[1]);
				if (adminStatus > 0) {
  					if (plugin.getServer().getPlayer(message[1]) != null) {
  						if (plugin.getServer().getPlayer(message[1]).isOnline()) {
  							player.sendMessage(ChatColor.GREEN + message[1] + " is online!");
  	  						if (adminStatus == 2) {
  	  							player.sendMessage(ChatColor.GREEN + "Group: " + plugin.playerColor(plugin.getServer().getPlayer(message[1]).getName()) + plugin.playerGroup(plugin.getServer().getPlayer(message[1])));
  	  						}
  						} else {
  							player.sendMessage(ChatColor.RED + message[1] + " isn't online!");
  						}
  					} else {
  						player.sendMessage(ChatColor.RED + message[1] + " isn't online!");
  					}
  				} else {
  					player.sendMessage(ChatColor.DARK_RED + message[1] + " isn't an admin!");
  				}
  			} else {
				String tempList = "";
				int x = 0;
	
				for(Player p : plugin.getServer().getOnlinePlayers())
				{
					if(p != null && plugin.playerIsAdmin(p.getName()) > 0 && x+1 == playerCount()){
						if (plugin.playerIsAdmin(p.getName()) == 2) {
							tempList+= plugin.playerColor(p.getName()) + p.getName();
						} else {
							tempList+= p.getName();
						}
						x++;
					}
					if(p != null && plugin.playerIsAdmin(p.getName()) > 0 && x < playerCount()){
						if (plugin.playerIsAdmin(p.getName()) == 2) {
							tempList+= plugin.playerColor(p.getName()) + p.getName() + ", ";
						} else {
							tempList+= p.getName() + ", ";
						}
						x++;
					}
				}
				if (tempList == "") {
					player.sendMessage(ChatColor.RED + "No admins online");
				} else {
					if (playerCount() > 1) {
						player.sendMessage(ChatColor.WHITE + "" + x + ChatColor.RED + " Online Admins:");
					} else {
						player.sendMessage(ChatColor.WHITE + "" + x + ChatColor.RED + " Online Admin:");
					}
					player.sendMessage(tempList);
				}
  			}
  		}
	 }
}