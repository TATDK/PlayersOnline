package dk.earthgame.TAT.PlayersOnline;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for Player join
 * @author TAT
 */
public class PlayersOnlineJoinExecutor extends PlayerListener {
	private PlayersOnlineWorker worker;
	private PlayersOnline plugin;
	
	public PlayersOnlineJoinExecutor(PlayersOnlineWorker worker, PlayersOnline plugin) {
		this.worker = worker;
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.ShowOnLogin) {
			Player player = event.getPlayer();
			worker.showTotalAdminsOnline(player);
			if (plugin.Multiworld) {
				for (World curWorld : plugin.getServer().getWorlds()) {
					worker.showAdminsOnline(curWorld.getName(), player);
				}
			} else {
				worker.showAdminsOnline(player.getWorld().getName(), player);
			}
		}
	}
}