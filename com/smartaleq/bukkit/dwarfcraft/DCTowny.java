package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import ca.xshade.bukkit.towny.IConomyException;
import ca.xshade.bukkit.towny.Towny;
import ca.xshade.bukkit.towny.TownyException;
import ca.xshade.bukkit.towny.TownySettings;
import ca.xshade.bukkit.towny.event.TownyPlayerListener;
import ca.xshade.bukkit.towny.object.Resident;
import ca.xshade.bukkit.towny.object.Town;
import ca.xshade.bukkit.towny.object.TownBlockOwner;
import ca.xshade.bukkit.towny.object.TownyIConomyObject;
import ca.xshade.bukkit.towny.object.WorldCoord;

class DCTowny extends TownyPlayerListener {

	private final Towny townyPlugin;
	private final DwarfCraft dwarfCraftPlugin;

	protected DCTowny(final Towny townyPlugin, final DwarfCraft dwarfCraftPlugin) {
		super(townyPlugin);
		this.townyPlugin = townyPlugin;
		this.dwarfCraftPlugin = dwarfCraftPlugin;
	}

	 public void checkIfSelectionIsValid(TownBlockOwner owner,
			List<WorldCoord> selection, boolean attachedToEdge, int blockCost,
			boolean force) throws TownyException {
		if (force)
			return;

		if (attachedToEdge && !isEdgeBlock(owner, selection))
			throw new TownyException("Selected area not attached to edge.");

		if (owner instanceof Town) {
			Town town = (Town) owner;
			int available = getDCMaxTownBlocks(town) - town.getTownBlocks().size();
			townyPlugin.sendDebugMsg("Claim Check Available: " + available);
			if (available - selection.size() < 0)
				throw new TownyException("Not enough available town blocks to claim this selection.");
		}

		try {
			int cost = blockCost * selection.size();
			if (TownySettings.isUsingIConomy() && !owner.canPay(cost))
				throw new TownyException("Town cannot afford to claim " + selection.size() + " town blocks costing " + cost + TownyIConomyObject.getIConomyCurrency());
		} catch (IConomyException e1) {
			throw new TownyException("Iconomy Error");
		}
	}

	private int getDCMaxTownBlocks(Town town) {
		int residentTotal = 0;
		int mayorMax = 5;
		Resident mayor = town.getMayor();
		DCPlayer mayorDwarf = dwarfCraftPlugin.getDataManager().findOffline(mayor.getName());
		mayorMax = (int) mayorDwarf.getEffect(920).getEffectAmount(mayorDwarf);
		List<Resident> residentList = town.getResidents();
		for (Resident r : residentList) {
			String residentName = r.getName();
			DCPlayer dCPlayer = dwarfCraftPlugin.getDataManager().findOffline(residentName);
			residentTotal += dCPlayer.getEffect(910).getEffectAmount(dCPlayer);
		}
		System.out.println("DC: got town max blocks");
		return Math.min(Math.max(residentTotal, 5), mayorMax);
	}
}
