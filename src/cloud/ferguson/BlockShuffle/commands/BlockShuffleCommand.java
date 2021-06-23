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

public class BlockShuffleCommand implements CommandExecutor {
  private Main plugin;

  public BlockShuffleCommand(Main plugin) {
    this.plugin = plugin;
    this.plugin.getCommand("shuffleblock").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;

      if (plugin.isCoop) {
        Bukkit.broadcastMessage(plugin.pluginChatPrefix + "§l" + p.getDisplayName()
            + "§r rerolled all blocks for the team!");
        plugin.shuffleAllBlocks(p);
      } else {
        Bukkit
            .broadcastMessage(plugin.pluginChatPrefix + "Rerolling block for player: §l" + p.getDisplayName() + "§r!");
        plugin.shuffleBlock(p);
      }
    } else {
      // Running from the server, re-shuffle all players
      Bukkit.broadcastMessage(plugin.pluginChatPrefix + "§lServer Console§r rerolled all blocks for the players!");
      plugin.shuffleAllBlocks(null);
    }

    return false;
  }
}
