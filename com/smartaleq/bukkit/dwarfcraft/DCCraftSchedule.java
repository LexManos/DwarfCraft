package com.smartaleq.bukkit.dwarfcraft;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.server.ContainerWorkbench;

public class DCCraftSchedule implements Runnable {
	private final DCPlayer dCPlayer;
	private final DwarfCraft plugin;
	private final EntityPlayer entityPlayer;
	private ContainerWorkbench workBench;

	public DCCraftSchedule(DwarfCraft newPlugin, DCPlayer newDwarf) {
		this.dCPlayer = newDwarf;
		this.plugin = newPlugin;
		this.entityPlayer = ((CraftPlayer) (dCPlayer.getPlayer())).getHandle();
	}

	@Override
	public void run() {
		// in this task we need to check to see if they are still using a
		// craftbench. if so, continue the task.
		if (entityPlayer == null)
			return;
		if (entityPlayer.activeContainer == entityPlayer.defaultContainer)
			return;
		try {
			workBench = (ContainerWorkbench) (entityPlayer.activeContainer);
		} catch (Exception e) {
			return;
		}

		ItemStack outputStack = CraftingManager.a().a(workBench.a);
		if (outputStack != null) {
			int materialId = outputStack.id;
			int damage = outputStack.damage;
			for (Skill s : dCPlayer.getSkills().values()) {
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.CRAFT && materialId == e.getOutputId() && damage == e.getInitiatorId()) {
						outputStack.count = (int) e.getEffectAmount(dCPlayer);
						// TODO: need code to check max stack size and if amount
						// created > max stack size drop all count above 1 to
						// ground/inventory.
						// I'm not sure what the server ItemStack method is for
						// is.getMaxStackSize()
					}
				}
				workBench.b.a(0, outputStack); // set the output stack on the
												// crafting bench
			}
		}
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DCCraftSchedule(plugin, dCPlayer), 2);
	}
}