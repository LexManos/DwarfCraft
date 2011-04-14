package com.smartaleq.bukkit.dwarfcraft;

import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Location;

class DCVehicleListener extends VehicleListener {
	private final DwarfCraft plugin;

	protected DCVehicleListener(final DwarfCraft plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a vehicle is damaged by the player.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleDamage(VehicleDamageEvent event) {
		// if(event.getDamage()>event.getVehicle().getHealth()) dropstuff
		event.getAttacker();
	}

	@Override
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!(event.getVehicle() instanceof CraftBoat)) return;
		plugin.getDataManager().addVehicle(new DwarfVehicle(event.getVehicle()));
		if (DwarfCraft.debugMessagesThreshold < 6)
			System.out.println("DC6:Added DwarfVehicle to vehicleList");
	}

	@Override
	public void onVehicleExit(VehicleExitEvent event) {
		if (!(event.getVehicle() instanceof CraftBoat)) return;
		plugin.getDataManager().removeVehicle(event.getVehicle());
	}

//	 public void onVehicleDestroyed(VehicleDestroyedEvent event)

	/**
	 * Called when a vehicle moves.
	 * 
	 * @param event
	 */
	@Override
	public void onVehicleMove(VehicleMoveEvent event) {
		if (event.getVehicle().getPassenger() == null) return;
		if (!(event.getVehicle() instanceof CraftBoat)) return;
		if (!(event.getVehicle().getPassenger() instanceof Player)) return;
		
		DCPlayer dCPlayer = plugin.getDataManager().find((Player)event.getVehicle().getPassenger()); 
		double effectAmount = 1.0;

		for (Skill s : dCPlayer.getSkills().values()) {
			for (Effect e : s.getEffects()) {
				if (e.getEffectType() == EffectType.VEHICLEMOVE) {
					effectAmount = e.getEffectAmount(dCPlayer);
				}
			}
		}

		DwarfVehicle dv = plugin.getDataManager().getVehicle(event.getVehicle());
		if (dv != null) {
			Location oldLoc = event.getVehicle().getLocation();
			Vector vel = event.getVehicle().getVelocity().multiply(effectAmount);
			Location location = new Location(event.getVehicle().getWorld(), oldLoc.getX(), oldLoc.getY(), oldLoc.getZ());
			location.setX(location.getX() + vel.getX());
			location.setZ(location.getZ() + vel.getZ());
			event.getVehicle().teleport(location);
		}
	}
}
