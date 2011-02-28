package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author TAT
 */
public class AdminsOnlinePlayerListener extends PlayerListener {
	private final AdminsOnline plugin;
	 
	public AdminsOnlinePlayerListener(AdminsOnline instance) {
		plugin = instance;
	}

	public int playerCount(){
		Player players[] = plugin.getServer().getOnlinePlayers();
		int x = 0;
		for(Player hurrdurr: players){
			if (plugin.playerIsAdmin(hurrdurr.getWorld().getName().toString(),hurrdurr.getName()) > 0) {
				x++;
			}
		}
		return x;
	}
	
	private void showAdminsOnline(String world,Player player) {
		String tempList = "";
		int x = 0;

		for(Player p : plugin.getServer().getOnlinePlayers())
		{
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x+1 == playerCount()){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName();
				} else {
					tempList+= p.getName();
				}
				x++;
			}
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x < playerCount()){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName() + ", ";
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
	
	public void onPlayerJoin(PlayerEvent event) {
		if (AdminsOnline.ShowOnLogin) {
			Player player = event.getPlayer();
			String world = player.getWorld().getName().toString();
			showAdminsOnline(world,player);
		}
	}

	public void onPlayerCommand(PlayerChatEvent event) {
  		Player player = event.getPlayer();
  		String world = player.getWorld().getName().toString();
  		String[] message = event.getMessage().split(" ");
  		if (message[0].equalsIgnoreCase("/reloadao")) {
  			event.setCancelled(true);
  			if (plugin.playerIsAdmin(world,player.getName()) > 0) {
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
				Integer adminStatus = plugin.playerIsAdmin(world,message[1]);
				if (adminStatus > 0) {
  					if (plugin.getServer().getPlayer(message[1]) != null) {
  						if (plugin.getServer().getPlayer(message[1]).isOnline()) {
  							player.sendMessage(ChatColor.GREEN + message[1] + " is online!");
  	  						if (adminStatus == 2) {
  	  							player.sendMessage(ChatColor.GREEN + "Group: " + plugin.playerColor(world,plugin.getServer().getPlayer(message[1]).getName()) + plugin.playerGroup(world,plugin.getServer().getPlayer(message[1])));
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
  				showAdminsOnline(world, player);
  			}
  		}
	 }
}