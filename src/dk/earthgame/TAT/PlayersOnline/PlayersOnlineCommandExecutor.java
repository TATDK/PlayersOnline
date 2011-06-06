package dk.earthgame.TAT.PlayersOnline;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handle events for Player commands
 * @author TAT
 */
public class PlayersOnlineCommandExecutor implements CommandExecutor {
	private PlayersOnline plugin;
	private PlayersOnlineWorker worker;

	public PlayersOnlineCommandExecutor(PlayersOnline instance,PlayersOnlineWorker worker) {
		plugin = instance;
		this.worker = worker;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String world = sender instanceof Player ? ((Player) sender).getWorld().getName().toString() : plugin.getServer().getWorlds().get(0).getName().toString();
		if (label.equalsIgnoreCase("reloadao")) {
  			if (plugin.playerIsAdmin(world,((Player)sender).getName()) > 0) {
  				try {
					plugin.loadConfiguration();
					sender.sendMessage(ChatColor.WHITE + plugin.pdfFile.getName() + ChatColor.GREEN +": Configuration reloaded.");
		  			return true;
				} catch (Throwable t) {
					sender.sendMessage(ChatColor.WHITE + plugin.pdfFile.getName() + ChatColor.RED + "Error while reloading: " + t.getMessage());
					return true;
				}
  			} else {
  				sender.sendMessage(ChatColor.RED + "You don't have access to this command!");
  				return true;
  			}
  		}
		//Admins only
  		if (command.getName().equalsIgnoreCase("admins")) {
			if (args.length > 0) {
				int adminStatus = plugin.playerIsAdmin(world,args[0]);
				if (adminStatus > 0) {
  					if (plugin.getServer().getPlayer(args[0]) != null) {
  						Player player = plugin.getServer().getPlayer(args[0]);
  						if (player.isOnline()) {
  							sender.sendMessage(ChatColor.GREEN + String.format(plugin.msg_Online, args[0]));
  							if (plugin.Multiworld) {
  								sender.sendMessage(ChatColor.GREEN + String.format(plugin.msg_World,player.getWorld().getName()));
  							}
  	  						if (adminStatus == 2) {
  	  							sender.sendMessage(ChatColor.GREEN + String.format(plugin.msg_Group, plugin.playerColor(world,player.getName()) + plugin.playerGroup(world,player) + ChatColor.GREEN));
  	  						}
  	  						return true;
  						} else {
  							sender.sendMessage(ChatColor.RED + String.format(plugin.msg_Offline, args[0]));
  				  			return true;
  						}
  					} else {
  						sender.sendMessage(ChatColor.RED + String.format(plugin.msg_Offline, args[0]));
  			  			return true;
  					}
  				} else {
  					sender.sendMessage(ChatColor.DARK_RED + String.format(plugin.msg_NotAdmin, args[0]));
  		  			return true;
  				}
  			} else {
  				worker.showTotalAdminsOnline(sender);
  				if (plugin.Multiworld) {
  					for (World curWorld : plugin.getServer().getWorlds()) {
  						worker.showAdminsOnline(curWorld.getName(), sender);
  					}
  				} else {
  					worker.showAdminsOnline(world, sender);
  				}
  	  			return true;
  			}
  		}
  		//All players (non-admins + admins)
  		if (command.getName().equalsIgnoreCase("players")) {
			if (args.length > 0) {
				if (plugin.getServer().getPlayer(args[0]) != null) {
					Player player = plugin.getServer().getPlayer(args[0]);
					if (player.isOnline()) {
						sender.sendMessage(ChatColor.GREEN + "" + String.format(plugin.msg_Online, args[0]));
						if (plugin.Multiworld) {
							sender.sendMessage(ChatColor.GREEN + String.format(plugin.msg_World,player.getWorld().getName()));
						}
  						if (plugin.UsePermissions && plugin.playerGroup(world, player) != "") {
  							sender.sendMessage(ChatColor.GREEN + String.format(plugin.msg_Group, plugin.playerColor(world,player.getName()) + plugin.playerGroup(world,player) + ChatColor.GREEN));
  						}
  						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "" + String.format(plugin.msg_Offline, args[0]));
			  			return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "" + String.format(plugin.msg_Offline, args[0]));
		  			return true;
				}
  			} else {
  				worker.showTotalPlayersOnline(sender);
  				if (plugin.Multiworld) {
  					for (World curWorld : plugin.getServer().getWorlds()) {
  						worker.showPlayersOnline(curWorld.getName(), sender);
  					}
  				} else {
  					worker.showPlayersOnline(world, sender);
  				}
  	  			return true;
  			}
  		}
		return false;
	}

}
