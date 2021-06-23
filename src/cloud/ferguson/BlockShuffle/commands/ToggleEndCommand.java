/**
 * Author: Oats ©2021
 * Project: BlockShuffle
 */

package cloud.ferguson.BlockShuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import cloud.ferguson.BlockShuffle.Main;

public class ToggleEndCommand implements CommandExecutor {
  private Main plugin;

  public ToggleEndCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("blockshuffleend").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // Toggle inclusion of End blocks
    plugin.allowEnd = !plugin.allowEnd;
    if (plugin.allowEnd) {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Including §8End§r blocks, good luck!");
    } else {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Removing §8End§r blocks from pool");
    }
    plugin.refreshBlockPool();
    return false;
  }
}
