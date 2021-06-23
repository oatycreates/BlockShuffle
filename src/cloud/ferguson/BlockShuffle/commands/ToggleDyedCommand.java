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

public class ToggleDyedCommand implements CommandExecutor {
  private Main plugin;

  public ToggleDyedCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("blockshuffledyed").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // Toggle inclusion of dyed blocks
    plugin.allowDyed = !plugin.allowDyed;
    if (plugin.allowDyed) {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Including §5Dyed§r blocks, good luck!");
    } else {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Removing §5Dyed§r blocks from pool");
    }
    plugin.refreshBlockPool();
    return false;
  }
}
