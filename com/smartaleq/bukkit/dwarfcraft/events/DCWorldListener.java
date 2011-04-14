package com.smartaleq.bukkit.dwarfcraft.events;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.Chunk;

import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;

public class DCWorldListener extends WorldListener {
	private final DwarfCraft plugin;

	public DCWorldListener(final DwarfCraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		event.setCancelled(plugin.getDataManager().checkTrainersInChunk(chunk));
	}
}