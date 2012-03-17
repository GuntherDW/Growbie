package com.afforess.growbie;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * A utility class for handling Growbie events
 */
public abstract class Gardener {
	
	public static boolean isBonemeal(ItemStack item) {
		return item.getType() == Material.INK_SACK && item.getDurability() == 15;
	}
    
    public static boolean spreadPlants(Block block, Player p) {
        boolean didGrow = false;
        if (GrowbieConfiguration.isSpreadablePlantSourceBlock(block.getType())) {
            Material whattogrow = GrowbieConfiguration.getSpreadableMaterial(block.getType());
            int plantsToGrow = 3;


            //Populate list of suitable blocks adjacent to us
            ArrayList<Block> growInBlocks = new ArrayList<Block>(27);
            int range = 2;
            for (int dx = -(range); dx <= range; dx++){
                for (int dy = -(range); dy <= range; dy++){
                    for (int dz = -(range); dz <= range; dz++){
                        growInBlocks.add(block.getRelative(dx, dy, dz));
                    }
                }
            }

            while (plantsToGrow > 0 && !growInBlocks.isEmpty()) {
                // get a random block from the list
                int i = Math.round((float)Math.random() * (growInBlocks.size()-1));
                Block growBlock = growInBlocks.get(i);
                growInBlocks.remove(i);

                if (GrowbieConfiguration.canGrowPlantOnBlock(growBlock, true)) {
                    // grow plant
                    BlockState state = growBlock.getState();
                    growBlock.setType(whattogrow);
                    didGrow = true;
                    Growbie.instance.consumerLog(p, state, growBlock.getState());
                    plantsToGrow--;
                }
            }
        }
        return didGrow;
    }
	
	public static boolean growPlants(Block block, Player p) {
		boolean didGrow = false;
		if (GrowbieConfiguration.isGrowablePlant(block.getType())) {
			int plantsToGrow = GrowbieConfiguration.plantGrowthRate(block.getType());

			//Populate list of suitable blocks adjacent to us
			ArrayList<Block> growInBlocks = new ArrayList<Block>(27);
			int range = 2;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						growInBlocks.add(block.getRelative(dx, dy, dz));
					}
				}
			}

			while (plantsToGrow > 0 && !growInBlocks.isEmpty()) {
				// get a random block from the list
				int i = Math.round((float)Math.random() * (growInBlocks.size()-1));
				Block growBlock = growInBlocks.get(i);
				growInBlocks.remove(i);
				
				if (GrowbieConfiguration.canGrowPlantOnBlock(growBlock)) {
					// grow plant
                    BlockState state = growBlock.getState();
					growBlock.setType(block.getType());
                    Growbie.instance.consumerLog(p, state, growBlock.getState());
					didGrow = true;
					plantsToGrow--;
				}
			}
		}
		return didGrow;
	}
	
	public static boolean growBlocks(Block block, Player p) {
		boolean didGrow = false;
        // p.sendMessage("block.getType : "+block.getType()+" isGrowable? "+GrowbieConfiguration.isGrowableBlock(block.getType()));
		if (GrowbieConfiguration.isGrowableBlock(block.getType()) || (block.getType() == Material.SMOOTH_BRICK && block.getData() == (byte) 0)) {
			//Leaves is a special case (really just a special case of me abusing the config file, but whatever)
			if (block.getType().equals(Material.LOG) && GrowbieConfiguration.blockForGrowableBlock(block.getType()).equals(Material.LEAVES)) {
				int range = 1;
				for (int dx = -(range); dx <= range; dx++){
					for (int dy = -(range); dy <= range; dy++){
						for (int dz = -(range); dz <= range; dz++){
							Block loop = block.getRelative(dx, dy, dz);
                            if (loop.getTypeId() == Material.AIR.getId()) {
                                // loop.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
                                BlockState state = loop.getState();
                                Material newType = GrowbieConfiguration.blockForGrowableBlock(block.getType());
                                loop.setType(newType);
                                Growbie.instance.consumerLog(p, state, loop.getState());
								didGrow = true;
							}
						}
					}
				}
			}
			// if the target is grass and no air above, do not do
			else if(block.getRelative(BlockFace.UP).getType() == Material.AIR || GrowbieConfiguration.blockForGrowableBlock(block.getType()) != Material.GRASS) {
				// transform block
                BlockState state = block.getState();
                if(block.getType()==Material.SMOOTH_BRICK && block.getData() == (byte) 0) {
                    // BlockState state = Block
                    block.setTypeIdAndData(Material.SMOOTH_BRICK.getId(), (byte) 1, false);
                } else {
                    block.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
                }
                Growbie.instance.consumerLog(p, state, block.getState());
				didGrow = true;
			}
		}
		return didGrow;
	}
	
	public static boolean spreadBlocks(Block block, Player p) {
		boolean didGrow = false;
        boolean sbrick = (block.getType() == Material.SMOOTH_BRICK && block.getData() == (byte) 0);
		if (GrowbieConfiguration.isSpreadableBlock(block.getType()) || sbrick) {

			// Let's loop over three surrounding dimensions
			int range = 1;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						
						Block loop = block.getRelative(dx, dy, dz);
                        BlockState state = loop.getState();
						
						if ((sbrick && loop.getType() == Material.SMOOTH_BRICK && loop.getData() == (byte) 0)
                                || loop.getType() == GrowbieConfiguration.blockForSpreadableBlock(block.getType())) {
							// Special check for grass - only grow if air on block above
							if(block.getType() == Material.GRASS && loop.getRelative(BlockFace.UP).getType() != Material.AIR) {
								continue;
							}
							
							if(sbrick) loop.setTypeIdAndData(Material.SMOOTH_BRICK.getId(), (byte) 1, false);
                            else loop.setType(block.getType());
                            Growbie.instance.consumerLog(p, state, loop.getState());
							didGrow = true;
						}
					}
				}
			}
		}
		return didGrow;
	}
	
	public static boolean growTree(Block block, Player p) {
		if(GrowbieConfiguration.isSapling(block.getType())){

			// Biome data stolen from here
			// http://www.minecraftforum.net/viewtopic.php?f=1020&t=151067
			// Seems slightly incorrect though... definitely get some Birch in rainforests
			// May need to play with probabilities

			TreeType treeKind = TreeType.TREE;
			Double treeRoll = Math.random();
            BlockState state = block.getState();

			switch(block.getBiome()) {
			case RAINFOREST:
				if(treeRoll <= 0.33) { treeKind = TreeType.BIG_TREE; }
				break;
			case SWAMPLAND:
			case FOREST:
				if(treeRoll <= 0.20) { treeKind = TreeType.BIRCH; }
				else if(treeRoll <=  0.47) { treeKind = TreeType.BIG_TREE; }
				break;
			case TUNDRA:
			case TAIGA:
				if(treeRoll <= 0.33) { treeKind = TreeType.REDWOOD; }
				else { treeKind = TreeType.TALL_REDWOOD; }
				break;
			default:
				if(treeRoll <= 0.10) { treeKind = TreeType.BIG_TREE; }
				break;
			}
			
			block.setType(Material.AIR);

			if(!block.getWorld().generateTree(block.getLocation(), treeKind)) {
				block.setType(Material.SAPLING);
				// We do not need to use useItem() as this will pass
				// through to the Minecraft engine and also fail,
				// consuming the bonemeal itself
				return false;
			}
            Growbie.instance.consumerLog(p, state, block.getState());
			return true;
		}
		return false;
	}
	
	public static void useItem(Player player) {
		/* Make the bone meal decrease on use, as it normally would. */
		int amt = player.getItemInHand().getAmount();
		if (amt > 1) {
			--amt;
			player.getItemInHand().setAmount(amt);
		} else {
			player.getInventory().remove(player.getItemInHand());
		}
	}

}
