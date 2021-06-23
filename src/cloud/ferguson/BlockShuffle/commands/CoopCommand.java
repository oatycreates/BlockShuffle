/**
 * Author: Oats ©2021
 * Project: BlockShuffle
 */

package cloud.ferguson.BlockShuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cloud.ferguson.BlockShuffle.Main;

public class CoopCommand implements CommandExecutor {
  private Main plugin;

  public CoopCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("blockshufflecoop").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player p = sender instanceof Player ? (Player) sender : null;
    // Toggle cooperative mode and re-shuffle blocks
    plugin.isCoop = !plugin.isCoop;
    if (plugin.isCoop) {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Switching to §3Coop§r mode, have fun!");
    } else {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Switching to §9Competitive§r mode, have fun!");
    }
    // Reset score
    plugin.resetPlayerScores();
    plugin.shuffleAllBlocks(p);
    return false;
  }
}
