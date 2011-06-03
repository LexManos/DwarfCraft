package com.smartaleq.bukkit.dwarfcraft.events;

import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.Location;

import com.smartaleq.bukkit.dwarfcraft.DCPlayer;
import com.smartaleq.bukkit.dwarfcraft.DwarfCraft;
import com.smartaleq.bukkit.dwarfcraft.DwarfVehicle;
import com.smartaleq.bukkit.dwarfcraft.Effect;
import com.smartaleq.bukkit.dwarfcraft.EffectType;
import com.smartaleq.bukkit.dwarfcraft.Skill;

public class DCVehicleListener extends VehicleListener {
	private final DwarfCraft plugin;

	public DCVehicleListener(final DwarfCraft plugin) {
		this.plugin = plugin;
	}
	
	/**
     * Called when a vehicle is destroyed
     *
     * @param event
     */
    @Override
    public void onVehicleDestroy(VehicleDestroyEvent event) {
    	boolean dropChange = false;
    	
    	if (event.getVehicle() instanceof Boat &&
    		event.getAttacker() instanceof Player){
    		
    		Player player = (Player)event.getAttacker();
    		DCPlayer dcPlayer = plugin.getDataManager().find(player);
    		Location loc = event.getVehicle().getLocation();

    		for (Skill skill : dcPlayer.getSkills().values()) {
    			for (Effect effect : skill.getEffects()){
    				if (effect.getEffectType() == EffectType.VEHICLEDROP){
    					ItemStack drop = effect.getOutput(dcPlayer);
    					
    					if (DwarfCraft.debugMessagesThreshold < 6) 
                            System.out.println("Debug: dropped " + drop.toString());
    					
    					if (drop.getAmount() > 0)
    						loc.getWorld().dropItemNaturally(loc, drop);
    					
    					dropChange = true;
    				}
    			}
    		}
    	}
    	
    	if (dropChange){
    		event.getVehicle().remove();
    		event.setCancelled(true);
    	}
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
