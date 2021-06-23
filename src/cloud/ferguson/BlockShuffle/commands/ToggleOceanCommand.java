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

public class ToggleOceanCommand implements CommandExecutor {
  private Main plugin;

  public ToggleOceanCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("blockshuffleocean").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    // Toggle inclusion of Ocean blocks
    plugin.allowOcean = !plugin.allowOcean;
    if (plugin.allowOcean) {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Including §9Ocean§r blocks, good luck!");
    } else {
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "Removing §9Ocean§r blocks from pool");
    }
    plugin.refreshBlockPool();
    return false;
  }
}
