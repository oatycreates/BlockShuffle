/**
 * Author: Oats ©2021
 * Project: BlockShuffle
 */

package cloud.ferguson.BlockShuffle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.ferguson.BlockShuffle.commands.BlockShuffleCommand;
import cloud.ferguson.BlockShuffle.commands.CoopCommand;
import cloud.ferguson.BlockShuffle.commands.ToggleDyedCommand;
import cloud.ferguson.BlockShuffle.commands.ToggleEndCommand;
import cloud.ferguson.BlockShuffle.commands.ToggleNetherCommand;
import cloud.ferguson.BlockShuffle.commands.ToggleOceanCommand;
import cloud.ferguson.BlockShuffle.listeners.BlockShuffleListener;

public class Main extends JavaPlugin {
  public String pluginChatPrefix = "§d[BlockShuffle]§r ";
  /**
   * When coop mode is set, all players will be given the same block to work on.
   * This will be reset when that block is found
   */
  public boolean isCoop = false;
  /**
   * Block players will be working towards in coop mode.
   */
  public Material coopTargetBlock = null;
  public HashMap<Player, Material> playerBlockTargets;
  private HashMap<Player, LocalDateTime> playerGoalAssignedAt;
  private HashMap<Player, Integer> playerGoalCompletions;
  public Random blockRandomiser;
  public ArrayList<Material> availableBlocks;
  public boolean allowNether = true;
  public boolean allowOcean = false;
  public boolean allowEnd = false;
  public boolean allowDyed = true;
  private List<Material> invalidBlocks = Arrays.asList(Material.END_PORTAL, Material.NETHER_PORTAL, Material.WATER,
      Material.BUBBLE_COLUMN, Material.LAVA, Material.SPAWNER, Material.BEACON, Material.WITHER_ROSE,
      Material.POTTED_WITHER_ROSE, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW,
      Material.BARRIER);
  private List<Material> netherBlocks = Arrays.asList(Material.ANCIENT_DEBRIS, Material.ENDER_CHEST, Material.BASALT,
      Material.GLOWSTONE, Material.SHROOMLIGHT, Material.TWISTING_VINES, Material.TWISTING_VINES_PLANT,
      Material.WEEPING_VINES, Material.WEEPING_VINES_PLANT, Material.DAYLIGHT_DETECTOR, Material.OBSERVER,
      Material.COMPARATOR, Material.REDSTONE_LAMP);
  private List<Material> oceanBlocks = Arrays.asList(Material.SEA_LANTERN, Material.SEA_PICKLE, Material.SEAGRASS,
      Material.TALL_SEAGRASS, Material.KELP, Material.KELP_PLANT, Material.DRIED_KELP_BLOCK, Material.SPONGE,
      Material.WET_SPONGE);
  private List<Material> flowersList = Arrays.asList(Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID,
      Material.ALLIUM, Material.AZURE_BLUET, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
      Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY);

  @Override
  public void onEnable() {
    super.onEnable();

    // Default to competitive mode
    isCoop = false;

    playerBlockTargets = new HashMap<Player, Material>();
    playerGoalAssignedAt = new HashMap<Player, LocalDateTime>();
    playerGoalCompletions = new HashMap<Player, Integer>();
    coopTargetBlock = null;
    blockRandomiser = new Random();
    refreshBlockPool();

    new BlockShuffleListener(this);
    new BlockShuffleCommand(this);
    new ToggleNetherCommand(this);
    new ToggleEndCommand(this);
    new ToggleOceanCommand(this);
    new ToggleDyedCommand(this);
    new CoopCommand(this);
  }

  /**
   * Shuffles the target block for the specified player. Will be set to the same
   * block for everyone if coop mode is set.
   */
  public void shuffleBlock(Player a_targetPlayer) {
    Material targetBlock;
    if (isCoop) {
      if (coopTargetBlock == null) {
        coopTargetBlock = chooseRandomBlock();

        // Apply target block to all other players in coop
        for (Player player : playerBlockTargets.keySet()) {
          if (player != a_targetPlayer) {
            shuffleBlock(player);
          }
        }
      }

      targetBlock = coopTargetBlock;
    } else {
      targetBlock = chooseRandomBlock();
    }
    playerBlockTargets.replace(a_targetPlayer, targetBlock);
    playerGoalAssignedAt.replace(a_targetPlayer, LocalDateTime.now());
    if (isCoop) {
      a_targetPlayer.sendMessage(pluginChatPrefix + "Your team's target block is: §6" + targetBlock.name() + "§r");
    } else {
      a_targetPlayer.sendMessage(pluginChatPrefix + "Your target block is: §6" + targetBlock.name() + "§r");
    }
  }

  /**
   * Shuffles the target block for all known players.
   * 
   * @param a_callingPlayer Player executing this command (or null if system
   *                        call).
   */
  public void shuffleAllBlocks(Player a_callingPlayer) {
    if (isCoop) {
      coopTargetBlock = null;
      if (a_callingPlayer != null) {
        // Re-roll will share to all other players
        shuffleBlock(a_callingPlayer);
      } else if (playerBlockTargets.size() > 0) {
        // Use the first player to pass the shuffle onto others
        shuffleBlock((Player) playerBlockTargets.keySet().toArray()[0]);
      }
    } else {
      // Re-roll for all players individually
      for (Player player : playerBlockTargets.keySet()) {
        shuffleBlock(player);
      }
    }
  }

  /**
   * Welcomes the player to the game mode and gives them a goal block.
   * 
   * @param a_player Player that needs a starting block.
   */
  public void welcomePlayer(Player a_player) {
    // If the player hasn't been registered yet, roll them a block
    a_player
        .sendMessage(pluginChatPrefix + "Welcome! See §l/blockshuffle§r for commands. Blocks available - §4Nether§r: "
            + (allowNether ? "§2Yes§r" : "§4No§r") + ", §9Ocean§r: " + (allowOcean ? "§2Yes§r" : "§4No§r")
            + ", §8End§r: " + (allowEnd ? "§2Yes§r" : "§4No§r") + ", §5Dyed§r: " + (allowDyed ? "§2Yes§r" : "§4No§r"));
    playerBlockTargets.put(a_player, Material.BEDROCK);
    playerGoalAssignedAt.put(a_player, LocalDateTime.now());
    shuffleBlock(a_player);
  }

  /**
   * Called when the player finds their target block.
   * 
   * @param a_player      Player finding the block.
   * @param a_targetBlock Target block they were aiming for.
   */
  public void onPlayerFindBlock(Player a_player, Block a_targetBlock) {
    LocalDateTime startTime = playerGoalAssignedAt.get(a_player);
    Duration timeTaken = Duration.between(startTime, LocalDateTime.now());
    // Add in leading zeros to clarify the time display
    long timeTakenSeconds = timeTaken.getSeconds() % 60;
    long timeTakenMinutes = timeTaken.toMinutes();
    String formattedTimeTakenSeconds = timeTakenSeconds < 10 ? "0" + timeTakenSeconds : Long.toString(timeTakenSeconds);
    String formattedTimeTakenMinutes = timeTakenMinutes < 10 ? "0" + timeTakenMinutes : Long.toString(timeTakenMinutes);
    String formattedTimeTaken = formattedTimeTakenMinutes + ":" + formattedTimeTakenSeconds;

    if (isCoop) {
      if (!playerGoalCompletions.containsKey(null)) {
        playerGoalCompletions.put(null, 0);
      }
      int newScore = playerGoalCompletions.get(null) + 1;
      playerGoalCompletions.replace(null, newScore);

      Bukkit.broadcastMessage(pluginChatPrefix + "§6Congratulations players!§r Your block §6" + a_targetBlock.getType()
          + "§r was found by §6" + a_player.getDisplayName() + "§r! Score: " + newScore + ", time taken: "
          + formattedTimeTaken);
      shuffleAllBlocks(a_player);
    } else {
      if (!playerGoalCompletions.containsKey(a_player)) {
        playerGoalCompletions.put(a_player, 0);
      }
      int newScore = playerGoalCompletions.get(a_player) + 1;
      playerGoalCompletions.replace(a_player, newScore);

      Bukkit.broadcastMessage(
          pluginChatPrefix + "§6Congratulations! " + a_player.getDisplayName() + "§r has found their block: §6"
              + a_targetBlock.getType() + "§r! Score: " + newScore + ", time taken: " + formattedTimeTaken);
      shuffleBlock(a_player);
    }
  }

  /**
   * Clears all player scores.
   */
  public void resetPlayerScores() {
    Bukkit.broadcastMessage(pluginChatPrefix + "Reset player scores");
    playerGoalCompletions.clear();
  }

  /**
   * Picks a random goal block type from the valid blocks list.
   * 
   * @return Chosen valid block.
   */
  private Material chooseRandomBlock() {
    int chosenBlock = blockRandomiser.nextInt(availableBlocks.size());
    Material targetBlock = availableBlocks.get(chosenBlock);
    return targetBlock;
  }

  /**
   * Refreshes the block pool and removes any invalid picks.
   */
  @SuppressWarnings("deprecation")
  public void refreshBlockPool() {
    Material[] blocks = Material.values();
    availableBlocks = new ArrayList<Material>();
    Collections.addAll(availableBlocks, blocks);
    // Remove invalid blocks
    availableBlocks.removeAll(invalidBlocks);
    availableBlocks
        .removeIf((Material mat) -> !mat.isBlock() || mat.isLegacy() || mat.isAir() || mat.name().contains("_SKULL")
            || mat.name().contains("_HEAD") || mat.name().contains("_EGG") || mat.name().contains("COMMAND_BLOCK")
            || mat.name().contains("INFESTED_"));

    // Remove any aged versions of blocks that age over time so the player doesn't have to wait for them
    availableBlocks .removeIf((Material mat) -> mat.name().contains("EXPOSED_") || mat.name().contains("WEATHERED_") ||
        mat.name().contains("OXIDIZED_"));

    if (!allowNether) {
      Bukkit.broadcastMessage(
          pluginChatPrefix + "Removing §4Nether§r blocks from random pool, toggle with §l/bsnether§r");
      availableBlocks.removeAll(netherBlocks);
      availableBlocks.removeIf((Material mat) -> mat.name().contains("NETHER") || mat.name().contains("BLACKSTONE")
          || mat.name().contains("SOUL_") || mat.name().contains("WARPED_") || mat.name().contains("CRIMSON_")
          || mat.name().contains("QUARTZ"));
    }

    if (!allowOcean) {
      Bukkit
          .broadcastMessage(pluginChatPrefix + "Removing §9Ocean§r blocks from random pool, toggle with §l/bsocean§r");
      availableBlocks.removeAll(oceanBlocks);
      availableBlocks.removeIf((Material mat) -> mat.name().contains("PRISMARINE") || mat.name().contains("CORAL"));
    }

    if (!allowEnd) {
      Bukkit.broadcastMessage(pluginChatPrefix + "Removing §8End§r blocks from random pool, toggle with §l/bsend§r");
      availableBlocks.removeIf((Material mat) -> mat.name().contains("DRAGON_") || mat.name().contains("END_")
          || mat.name().contains("PURPUR_") || mat.name().contains("SHULKER_") || mat.name().contains("CHORUS"));
    }

    if (!allowDyed) {
      Bukkit.broadcastMessage(
          pluginChatPrefix + "Removing §5Dyed§r blocks and Flowers from random pool, toggle with §l/bsdyed§r");
      availableBlocks.removeAll(flowersList);
      // See: https://minecraft.gamepedia.com/Wool for colours
      // Leave White for basic craft option
      availableBlocks.removeIf((Material mat) -> mat.name().contains("POTTED_") || mat.name().contains("_TULIP")
          || mat.name().contains("_STAINED") || mat.name().contains("BLACK_") || mat.name().contains("BLUE_")
          || mat.name().contains("BROWN_") || mat.name().contains("CYAN_") || mat.name().contains("GRAY_")
          || mat.name().contains("GREEN_") || mat.name().contains("LIGHT_BLUE_") || mat.name().contains("LIGHT_GRAY_")
          || mat.name().contains("LIME_") || mat.name().contains("MAGENTA_") || mat.name().contains("ORANGE_")
          || mat.name().contains("PINK_") || mat.name().contains("PURPLE_") || mat.name().contains("RED_")
          || mat.name().contains("YELLOW_")/* || mat.name().contains("WHITE_") */);
    }

    Bukkit.broadcastMessage(pluginChatPrefix + "Finished refreshing pool! Total blocks: " + availableBlocks.size());

    // Reset score and pick new blocks
    resetPlayerScores();
    shuffleAllBlocks(null);
  }
}
