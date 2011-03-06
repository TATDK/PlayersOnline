package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminsOnlineDisabled implements CommandExecutor {
	public AdminsOnlineDisabled(AdminsOnline instance) {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
    }
}
