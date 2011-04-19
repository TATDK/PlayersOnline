package dk.earthgame.TAT.AdminsOnline;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for Player join
 * @author TAT
 */
public class AdminsOnlineJoinExecutor extends PlayerListener {
	private AdminsOnlineWorker worker;
	
	public AdminsOnlineJoinExecutor(AdminsOnlineWorker worker) {
		this.worker = worker;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (AdminsOnline.ShowOnLogin) {
			Player player = event.getPlayer();
			String world = player.getWorld().getName().toString();
			worker.showAdminsOnline(world,player);
		}
	}
}