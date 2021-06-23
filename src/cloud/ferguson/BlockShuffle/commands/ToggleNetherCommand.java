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

public class ToggleNetherCommand implements CommandExecutor {
  private Main plugin;

  public ToggleNetherCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("blockshufflenether").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // Toggle inclusion of Nether blocks
    plugin.allowNether = !plugin.allowNether;
    if (plugin.allowNether) {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Including §4Nether§r blocks, good luck!");
    } else {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Removing §4Nether§r blocks from pool");
    }
    plugin.refreshBlockPool();
    return false;
  }
}
