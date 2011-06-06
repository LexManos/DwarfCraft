package com.smartaleq.bukkit.dwarfcraft.events;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;

import com.smartaleq.bukkit.dwarfcraft.DCCraftSchedule;
import com.smartaleq.bukkit.dwarfcraft.DCPlayer;
import com.smartaleq.bukkit.dwarfcraft.DataManager;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.Effect;
import com.smartaleq.bukkit.dwarfcraft.EffectType;
import com.smartaleq.bukkit.dwarfcraft.Skill;
import com.smartaleq.bukkit.dwarfcraft.Util;

public class DCPlayerListener extends PlayerListener {
	private final DwarfCraft plugin;

	public DCPlayerListener(final DwarfCraft plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){}

	/**
	 * When a player joins the server this initialized their data from the
	 * database or creates new info for them.
	 * 
	 * also broadcasts a welcome "player" message
	 */
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		DataManager dm     = plugin.getDataManager();
		Player      player = event.getPlayer();
		DCPlayer    data   = dm.find(player);
		
		if (data == null)           data = dm.createDwarf(player);
		if (!dm.getDwarfData(data)) dm.createDwarfData(data);
			
		plugin.getOut().welcome(plugin.getServer(), data);
	}
	
    /**	
	 * Called when a player interacts	
     *	
	 * @param event Relevant event details	
     */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		//Crafting changes
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WORKBENCH){
			DCCraftSchedule sched = new DCCraftSchedule(plugin, plugin.getDataManager().find(event.getPlayer()));
			int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sched, 0, 2);
			sched.setID(id);
		}
		
		Player   player = event.getPlayer();
		DCPlayer dcPlayer = plugin.getDataManager().find(player);
		HashMap<Integer, Skill> skills = dcPlayer.getSkills();
		
		ItemStack item = player.getItemInHand();
				
		//EffectType.PLOWDURABILITY
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK){			
			Block    block    = event.getClickedBlock();
			Material material = block.getType();
			
			if (material == Material.DIRT || material == Material.GRASS){			
				for (Skill s : skills.values()) {
					for (Effect effect : s.getEffects()) {
						if (effect.getEffectType() == EffectType.PLOWDURABILITY && effect.checkTool(item)) {
							effect.damageTool(dcPlayer, 1, item);
							//block.setTypeId(60);
						}
					}
				}
			}
		}

		//EffectType.EAT
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){	
			for (Skill s : skills.values()) {
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.EAT && e.checkInitiator(item)) {
						int health = Util.randomAmount(e.getEffectAmount(dcPlayer));
						
						if (DwarfCraft.debugMessagesThreshold < 8)
							System.out.println(String.format("DC8: Are Food: \"%s\" for %d health", Util.getCleanName(item), health));
						
						
						player.setHealth(Math.min((int)(player.getHealth() + health), 20));
						
						item.setAmount(item.getAmount() - 1);
						if (item.getAmount() <= 0)
							item = null;
						
						player.setItemInHand(item);
						event.setCancelled(true);						
					}
				}
			}
		}
	}
	
	/**
     * Called when a player opens an inventory
     *
     * @param event Relevant event details
     */
	@Override
    public void onInventoryOpen(PlayerInventoryEvent event) {
		DCCraftSchedule sched = new DCCraftSchedule(plugin, plugin.getDataManager().find(event.getPlayer()));
		int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sched, 0, 2);
		sched.setID(id);
    }
}