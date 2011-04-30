package com.smartaleq.bukkit.dwarfcraft.events;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

import com.smartaleq.bukkit.dwarfcraft.DCPlayer;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.Effect;
import com.smartaleq.bukkit.dwarfcraft.EffectType;
import com.smartaleq.bukkit.dwarfcraft.Skill;
import com.smartaleq.bukkit.dwarfcraft.Util;

/**
 * This watches for broken blocks and reacts
 * 
 */
public class DCBlockListener extends BlockListener {
	private final DwarfCraft plugin;

	public DCBlockListener(final DwarfCraft plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a block is destroyed by a player.
	 * 
	 * @param event
	 *            Relevant event details
	 */
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();		
		DCPlayer dCPlayer = plugin.getDataManager().find(player);
		HashMap<Integer, Skill> skills = dCPlayer.getSkills();
		
		ItemStack tool = player.getItemInHand();
		int toolId = -1;
		short durability = 0;
		
		if (tool != null) {
			toolId = tool.getTypeId();
			durability = tool.getDurability();
		}
		
		Location loc = event.getBlock().getLocation();
		int materialId = event.getBlock().getTypeId();
		byte meta = event.getBlock().getData();

		boolean blockDropChange = false;
		for (Skill s : skills.values()) {
			for (Effect effect : s.getEffects()) {
				if (effect.getEffectType() == EffectType.BLOCKDROP && effect.getInitiatorId() == materialId) {
					
					// Crops special line:
					if (effect.getInitiatorId() == 59){
						if (meta != 7)
							continue;
					}
					
					if (DwarfCraft.debugMessagesThreshold < 4)
						System.out.println("DC4: Effect: " + effect.getId() + " tool: " + toolId + " and toolRequired: " + effect.getToolRequired());
					
					if (effect.checkTool(toolId) || !effect.getToolRequired()) {
						Util.dropBlockEffect(loc, effect, effect.getEffectAmount(dCPlayer), true, meta);
						blockDropChange = true;
					}
				}
				if (effect.getEffectType() == EffectType.TOOLDURABILITY && durability != -1) {
					if (effect.checkTool(toolId)) {
						double effectAmount = effect.getEffectAmount(dCPlayer);
						if (DwarfCraft.debugMessagesThreshold < 3)
							System.out.println("DC2: affected durability of a tool - old:" + durability);
						tool.setDurability((short)(durability + Util.randomAmount(effectAmount)));
						
						// if you use the tool on a non-dropping block it
						// doesn't take special durability damage
						if (DwarfCraft.debugMessagesThreshold < 3)
							System.out.println("DC3: affected durability of a tool - new:" + tool.getDurability());
						
						if (tool.getDurability() >= tool.getType().getMaxDurability()){
							if (tool.getTypeId() == 267 && tool.getDurability() < 250)
								continue;
							player.setItemInHand(null);
						}
					}
				}
				if (tool != null){
					if (effect.getEffectType() == EffectType.SWORDDURABILITY && (effect.checkTool(toolId) || !effect.getToolRequired())) {
						if (DwarfCraft.debugMessagesThreshold < 2)
							System.out.println("DC2: affected durability of a sword - old:" + durability + " effect called: " + effect.getId());
						
						tool.setDurability((short)(durability + (Util.randomAmount(effect.getEffectAmount(dCPlayer) * 2))));
						
						if (DwarfCraft.debugMessagesThreshold < 3)
							System.out.println("DC3: affected durability of a sword - new:" + tool.getDurability());

						if (tool.getDurability() >= tool.getType().getMaxDurability()){
							if (tool.getTypeId() == 267 && tool.getDurability() < 250)
								continue;
							player.setItemInHand(null);
						}
					}
				}
			}
		}

		if (blockDropChange) {
			event.getBlock().setTypeId(0);
			event.setCancelled(true);
		}
	}

	/**
	 * onBlockDamage used to accelerate how quickly blocks are destroyed.
	 * setDamage() not implemented yet
	 */
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		DCPlayer dCPlayer = plugin.getDataManager().find(player);
		HashMap<Integer, Skill> skills = dCPlayer.getSkills();

		// Effect Specific information
		ItemStack tool = player.getItemInHand();
		int toolId = -1;
		if (tool != null) 
			toolId = tool.getTypeId();
		int materialId = event.getBlock().getTypeId();
		
		//if (event.getDamageLevel() != BlockDamageLevel.STARTED)
		//	return;
				
		for (Skill s : skills.values()) {
			for (Effect e : s.getEffects()) {
				if (e.getEffectType() == EffectType.DIGTIME && e.getInitiatorId() == materialId) {
					if (DwarfCraft.debugMessagesThreshold < 2)
						System.out.println("DC2: started instamine check");
					
					if (e.checkTool(toolId) || !e.getToolRequired()) {
						if (Util.randomAmount(e.getEffectAmount(dCPlayer)) == 0)
							return;
						
						if (DwarfCraft.debugMessagesThreshold < 3)
							System.out.println("DC3: Insta-mine occured. Block: " + materialId);
						
						event.setInstaBreak(true);						
					}
				}
			}
		}
	}
	

}
