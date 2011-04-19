package dk.earthgame.TAT.PlayersOnline;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayersOnlineDisabled implements CommandExecutor {
	public PlayersOnlineDisabled(PlayersOnline instance) {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
    }
}
