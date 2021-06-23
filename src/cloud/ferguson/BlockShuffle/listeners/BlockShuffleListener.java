/**
 * Author: Oats ©2021
 * Project: BlockShuffle
 */

package cloud.ferguson.BlockShuffle.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import cloud.ferguson.BlockShuffle.Main;

public class BlockShuffleListener implements Listener {
  private Main plugin;

  public BlockShuffleListener(Main plugin) {
    this.plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, this.plugin);
  }

  @EventHandler
  public void blockStepEvent(PlayerMoveEvent a_event) {
    Player p = a_event.getPlayer();
    Location playerLoc = p.getLocation();
    Location standingLoc = playerLoc.subtract(0, 1, 0);
    Block standingBlock = standingLoc.getBlock();
    if (standingBlock != null) {
      if (plugin.playerBlockTargets.containsKey(p)) {
        // Check if the player has found their block
        if (plugin.playerBlockTargets.get(p) == standingBlock.getType()) {
          plugin.onPlayerFindBlock(p, standingBlock);
        }
      } else {
        plugin.welcomePlayer(p);
      }
    }
  }
}
