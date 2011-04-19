package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminsOnlineWorker {
	private final AdminsOnline plugin;
	
	public AdminsOnlineWorker(AdminsOnline instance) {
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
	
	public void showAdminsOnline(String world,CommandSender sender) {
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
			sender.sendMessage(ChatColor.RED + "No admins online");
		} else {
			if (playerCount() > 1) {
				sender.sendMessage(ChatColor.WHITE + "" + x + ChatColor.RED + " Online Admins:");
			} else {
				sender.sendMessage(ChatColor.WHITE + "" + x + ChatColor.RED + " Online Admin:");
			}
			sender.sendMessage(tempList);
		}
	}
}
