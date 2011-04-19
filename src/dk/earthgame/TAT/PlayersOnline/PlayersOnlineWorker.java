package dk.earthgame.TAT.PlayersOnline;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayersOnlineWorker {
	private final PlayersOnline plugin;
	
	public PlayersOnlineWorker(PlayersOnline instance) {
		plugin = instance;
	}

	int adminsCount(){
		Player players[] = plugin.getServer().getOnlinePlayers();
		int x = 0;
		for(Player p: players){
			if (plugin.playerIsAdmin(p.getWorld().getName().toString(),p.getName()) > 0) {
				x++;
			}
		}
		return x;
	}
	
	int adminsCount(String world){
		List<Player> players = plugin.getServer().getWorld(world).getPlayers();
		int x = 0;
		for(Player p: players){
			if (plugin.playerIsAdmin(world,p.getName()) > 0) {
				x++;
			}
		}
		return x;
	}
	
	int playersCount() {
		return plugin.getServer().getOnlinePlayers().length;
	}
	
	int playersCount(String world) {
		return plugin.getServer().getWorld(world).getPlayers().size();
	}
	
	void showTotalAdminsOnline(String world,CommandSender sender) {
		int adminsOnline = adminsCount(world);
		if (adminsOnline == 0) {
			sender.sendMessage(ChatColor.RED + "No admins online");
		} else {
			if (adminsOnline > 1) {
				sender.sendMessage(ChatColor.WHITE + "" + adminsOnline + ChatColor.RED + " Online Admins");
			} else {
				sender.sendMessage(ChatColor.WHITE + "" + adminsOnline + ChatColor.RED + " Online Admin");
			}
		}
	}
	
	void showTotalAdminsOnline(CommandSender sender) {
		int adminsOnline = adminsCount();
		if (adminsOnline == 0) {
			sender.sendMessage(ChatColor.RED + "No admins online");
		} else {
			if (adminsOnline > 1) {
				sender.sendMessage(ChatColor.WHITE + "" + adminsOnline + ChatColor.RED + " Online Admins");
			} else {
				sender.sendMessage(ChatColor.WHITE + "" + adminsOnline + ChatColor.RED + " Online Admin");
			}
		}
	}
	
	void showAdminsOnline(String world,CommandSender sender) {
		String tempList = "";
		int x = 0;
		int adminsOnline = 0;
		if (plugin.Multiworld) {
			adminsOnline = adminsCount(world);
		} else {
			adminsOnline = adminsCount();
		}
		
		for(Player p : plugin.getServer().getWorld(world).getPlayers())
		{
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x+1 == adminsOnline){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName();
				} else {
					tempList+= p.getName();
				}
				x++;
			}
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x < adminsOnline){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName() + ", ";
				} else {
					tempList+= p.getName() + ", ";
				}
				x++;
			}
		}
		if (tempList != "") {
			if (plugin.Multiworld) {
				sender.sendMessage(ChatColor.GREEN + world + ": " + tempList);
			} else {
				sender.sendMessage(tempList);
			}
		}
	}

	void showTotalPlayersOnline(CommandSender sender) {
		int playersOnline = playersCount();
		if (playersOnline > 1) {
			sender.sendMessage(ChatColor.WHITE + "" + playersOnline + ChatColor.RED + " Online Players");
		} else {
			sender.sendMessage(ChatColor.WHITE + "" + playersOnline + ChatColor.RED + " Online Player");
		}
	}

	void showTotalPlayersOnline(String world,CommandSender sender) {
		int playersOnline = playersCount(world);
		if (playersOnline > 1) {
			sender.sendMessage(ChatColor.WHITE + "" + playersOnline + ChatColor.RED + " Online Players");
		} else {
			sender.sendMessage(ChatColor.WHITE + "" + playersOnline + ChatColor.RED + " Online Player");
		}
	}
	
	void showPlayersOnline(String world,CommandSender sender) {
		String tempList = "";
		int x = 0;
		int playersOnline = 0;
		if (plugin.Multiworld) {
			playersOnline = playersCount(world);
		} else {
			playersOnline = playersCount();
		}
		
		for(Player p : plugin.getServer().getWorld(world).getPlayers())
		{
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x+1 == playersOnline){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName();
				} else {
					tempList+= p.getName();
				}
				x++;
			}
			if(p != null && plugin.playerIsAdmin(world,p.getName()) > 0 && x < playersOnline){
				if (plugin.playerIsAdmin(world,p.getName()) == 2) {
					tempList+= plugin.playerColor(world,p.getName()) + p.getName() + ", ";
				} else {
					tempList+= p.getName() + ", ";
				}
				x++;
			}
		}
		if (tempList != "") {
			if (plugin.Multiworld) {
				sender.sendMessage(ChatColor.GREEN + world + ": " + tempList);
			} else {
				sender.sendMessage(tempList);
			}
		}
	}
}
