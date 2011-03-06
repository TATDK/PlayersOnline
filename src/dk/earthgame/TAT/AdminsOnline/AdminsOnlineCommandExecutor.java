package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handle events for Player commands
 * @author TAT
 */
public class AdminsOnlineCommandExecutor implements CommandExecutor {
	private AdminsOnline plugin;
	private AdminsOnlineWorker worker;

	public AdminsOnlineCommandExecutor(AdminsOnline instance,AdminsOnlineWorker worker) {
		plugin = instance;
		this.worker = worker;
	}

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
  		if (command.getName().equalsIgnoreCase("adminsonline")) {
			if (args.length > 0) {
				int adminStatus = plugin.playerIsAdmin(world,args[0]);
				if (adminStatus > 0) {
  					if (plugin.getServer().getPlayer(args[0]) != null) {
  						if (plugin.getServer().getPlayer(args[0]).isOnline()) {
  							sender.sendMessage(ChatColor.GREEN + args[0] + " is online!");
  	  						if (adminStatus == 2) {
  	  							sender.sendMessage(ChatColor.GREEN + "Group: " + plugin.playerColor(world,plugin.getServer().getPlayer(args[0]).getName()) + plugin.playerGroup(world,plugin.getServer().getPlayer(args[0])));
  	  						}
  	  						return true;
  						} else {
  							sender.sendMessage(ChatColor.RED + args[0] + " isn't online!");
  				  			return true;
  						}
  					} else {
  						sender.sendMessage(ChatColor.RED + args[0] + " isn't online!");
  			  			return true;
  					}
  				} else {
  					sender.sendMessage(ChatColor.DARK_RED + args[0] + " isn't an admin!");
  		  			return true;
  				}
  			} else {
  				worker.showAdminsOnline(world, sender);
  	  			return true;
  			}
  		}
		return false;
	}

}
