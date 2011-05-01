package com.smartaleq.bukkit.dwarfcraft.events;

import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;

import com.smartaleq.bukkit.dwarfcraft.*;
import com.smartaleq.bukkit.dwarfcraft.DCPlayer.ArmorType;

import org.martin.bukkit.npclib.*;
import org.martin.bukkit.npclib.NpcEntityTargetEvent.NpcTargetReason;

public class DCEntityListener extends EntityListener {
	private final DwarfCraft plugin;
	private HashMap<Entity, DCPlayer> killMap;

	public DCEntityListener(DwarfCraft plugin) {
		this.plugin = plugin;
		killMap = new HashMap<Entity, DCPlayer>();
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			if (event.getEntity() instanceof HumanEntity) {
				if (!(event instanceof EntityDamageByProjectileEvent)){
					if (checkTrainerLeftClick((EntityDamageByEntityEvent) event)) { 
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		
		if (event.isCancelled())
			return;
		
		if (event.getCause() == DamageCause.BLOCK_EXPLOSION
			|| event.getCause() == DamageCause.ENTITY_EXPLOSION
			|| event.getCause() == DamageCause.FALL
			|| event.getCause() == DamageCause.SUFFOCATION
			|| event.getCause() == DamageCause.FIRE
			|| event.getCause() == DamageCause.FIRE_TICK
			|| event.getCause() == DamageCause.LAVA
			|| event.getCause() == DamageCause.DROWNING) {
			
			if (DwarfCraft.debugMessagesThreshold < -1)
				System.out.println("DC-1: Damage Event: "+event.getCause());
			
			onEntityDamagedByEnvirons(event);

		} else if (event instanceof EntityDamageByProjectileEvent) {
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC4: Damage Event: projectile");
			onEntityDamageByProjectile((EntityDamageByProjectileEvent) event);
			
		} else if (event instanceof EntityDamageByEntityEvent) {			
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC4: Damage Event: entity by entity");
			onEntityAttack((EntityDamageByEntityEvent) event);
		} 
	}
	
	private boolean checkTrainerLeftClick(EntityDamageByEntityEvent event) {
		DwarfTrainer trainer = plugin.getDataManager().getTrainer(event.getEntity());
		if (trainer != null) {
			if (event.getDamager() instanceof Player) {
				if (plugin.getDataManager().getTrainerRemove().contains(event.getDamager())){
					plugin.getDataManager().removeTrainer(trainer.getUniqueId());
					plugin.getDataManager().getTrainerRemove().remove(event.getDamager());
				}else{
					//in business, left click
					if (trainer.isGreeter()) {
						trainer.printLeftClick((Player) (event.getDamager()));
					} else {
						trainer.lookAt(event.getDamager());
						Player   player   = (Player)event.getDamager();
						DCPlayer dCPlayer = plugin.getDataManager().find(player);
						Skill    skill    = dCPlayer.getSkill(trainer.getSkillTrained());
						plugin.getOut().printSkillInfo(player, skill, dCPlayer, trainer.getMaxSkill());
					}
				}
			}
			return true;
		}
		return false;
	}

	private boolean checkDwarfTrainer(NpcEntityTargetEvent event) {
		try {
			DCPlayer dCPlayer = plugin.getDataManager().find(((Player) event.getTarget()));
			DwarfTrainer trainer = plugin.getDataManager().getTrainer(event.getEntity());
			if (trainer != null) {
				if (event.getTarget() instanceof Player) {
					if (event.getNpcReason() == NpcTargetReason.CLOSEST_PLAYER) {
					} else if (event.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED) {
						if (trainer.isGreeter()) {
							trainer.printRightClick((Player) (event.getTarget()));
						} else {
							trainer.lookAt(event.getTarget());
							trainer.getEntity().animateArmSwing();
							trainer.trainSkill(dCPlayer);
						}
					} else if (event.getNpcReason() == NpcTargetReason.NPC_BOUNCED) {
						// player collided with mob
						// doesn't seem to work
					}
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void onEntityAttack(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		LivingEntity victim;
		
		if (event.getEntity() instanceof LivingEntity){
			victim = (LivingEntity)event.getEntity();
			if (DwarfCraft.debugMessagesThreshold < 0)
				System.out.println("DC0: victim is living ");
		}else{
			if (DwarfCraft.debugMessagesThreshold < 0)
				System.out.println("DC0: victim is unliving ");
			return;
		}
		
		boolean isPVP = false;
		DCPlayer attacker = null;
		
		if (victim instanceof Player) {
			isPVP = true;
			if (DwarfCraft.debugMessagesThreshold < 1)
				System.out.println("DC1: EDBE is PVP");
		}
		
		int damage = event.getDamage();
		int hp = victim.getHealth();
		if (damager instanceof Player){
			attacker = plugin.getDataManager().find((Player) damager);
			assert(event.getDamager() == attacker.getPlayer());
			assert(attacker != null);
		} else {// EvP no effects, EvE no effects
			if (DwarfCraft.debugMessagesThreshold < 4)
				System.out.println(String.format("DC4: EVP %s attacked %s for %d of %d\r\n", 
						damager.getClass().getSimpleName(), victim.getClass().getSimpleName(), damage, hp));
			return;
		}
		
		ItemStack tool = attacker.getPlayer().getItemInHand();
		int toolId = -1;
		short durability = 0;
		
		if (tool != null) {
			toolId = tool.getTypeId();
			durability = tool.getDurability();
		}
		
		HashMap<Integer, Skill> skills = attacker.getSkills();
		for (Skill s : skills.values()) {
			for (Effect e : s.getEffects()) {
				if (tool != null){
					if (e.getEffectType() == EffectType.SWORDDURABILITY && e.checkTool(toolId)) {
						if (DwarfCraft.debugMessagesThreshold < 2)
							System.out.println("DC2: affected durability of a sword - old:" + durability + " effect called: " + e.getId());
						
						tool.setDurability((short)(durability + Util.randomAmount(e.getEffectAmount(attacker))));
						
						if (DwarfCraft.debugMessagesThreshold < 3)
							System.out.println("DC3: affected durability of a sword - new:" + tool.getDurability());

						if (tool.getDurability() >= tool.getType().getMaxDurability()){
							if (tool.getTypeId() == 267 && tool.getDurability() < 250)
								continue;
							attacker.getPlayer().setItemInHand(null);
						}
					}
				}
				
				if (e.getEffectType() == EffectType.PVEDAMAGE && !isPVP && e.checkTool(toolId)) {
						if (hp <= 0) {
							event.setCancelled(true);
							return;
						}
						damage = Util.randomAmount((e.getEffectAmount(attacker)) * damage);
						if (damage >= hp && !killMap.containsKey(victim)){
			                killMap.put(victim, attacker);
						}
						event.setDamage(damage);
						if (DwarfCraft.debugMessagesThreshold < 6){
							System.out.println(String.format("DC6: PVE %s attacked %s for %.2f of %d doing %d dmg of %d hp" + " effect called: %d",
									attacker.getPlayer().getName(), victim.getClass().getSimpleName(), e.getEffectAmount(attacker),
									event.getDamage(), damage, hp, e.getId()));
						}
				}
				if (e.getEffectType() == EffectType.PVPDAMAGE && isPVP && e.checkTool(toolId)) {
					damage = Util.randomAmount((e.getEffectAmount(attacker)) * damage);
					event.setDamage(damage);
					if (DwarfCraft.debugMessagesThreshold < 6){
						System.out.println(String.format("DC6: PVP %s attacked %s for %.2f of %d doing %d dmg of %d hp" + " effect called: %d",
							attacker.getPlayer().getName(), ((Player)victim).getName(), e.getEffectAmount(attacker),
							event.getDamage(), damage, hp, e.getId()));
					}
				}
			}
		}
	}

	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		LivingEntity attacker = (LivingEntity)event.getDamager();
		LivingEntity hitThing = (LivingEntity)event.getEntity();
		
		int hp = hitThing.getHealth();
		if (hp <= 0) {
			event.setCancelled(true);
			return;
		}
		double damage = event.getDamage();
		double mitigation = 1;

		DCPlayer attackDwarf = null;
		DCPlayer defendDwarf = null;
		
		if(attacker instanceof Player){
			attackDwarf = plugin.getDataManager().find((Player)attacker);
			for (Skill skill : attackDwarf.getSkills().values()) {
				for (Effect effect : skill.getEffects()) {
					if (effect.getEffectType() == EffectType.BOWATTACK) 
						damage = effect.getEffectAmount(attackDwarf);
				}
			}
		}
		
		if(hitThing instanceof Player){
			defendDwarf = plugin.getDataManager().find((Player)hitThing);
			if (defendDwarf == null){ //its a NPC				
			}else{
				for (Skill skill : defendDwarf.getSkills().values()) {
					for (Effect effect : skill.getEffects()) {
						if (effect.getEffectType() == EffectType.BOWDEFEND) 
							mitigation = armorMitigation(EffectType.BOWDEFEND, defendDwarf);
					}
				}
			}
		}
		
		damage = Util.randomAmount(damage * mitigation);
		event.setDamage((int)damage);
		if (damage >= hp && attacker instanceof Player && !killMap.containsKey(hitThing) && !(hitThing instanceof Player)){
	        killMap.put(hitThing, attackDwarf);
		}
	}

	public void onEntityDamagedByEnvirons(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		DCPlayer dCPlayer = plugin.getDataManager().find((Player) event.getEntity());
		double damage = event.getDamage();
		for (Skill s : dCPlayer.getSkills().values()) {
			for (Effect e : s.getEffects()) {
				if      (e.getEffectType() == EffectType.FALLDAMAGE      && event.getCause() == DamageCause.FALL) 
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				else if (e.getEffectType() == EffectType.FIREDAMAGE      && event.getCause() == DamageCause.FIRE) 
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				else if (e.getEffectType() == EffectType.FIREDAMAGE      && event.getCause() == DamageCause.FIRE_TICK)
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				else if (e.getEffectType() == EffectType.EXPLOSIONDAMAGE && event.getCause() == DamageCause.ENTITY_EXPLOSION)
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				else if (e.getEffectType() == EffectType.EXPLOSIONDAMAGE && event.getCause() == DamageCause.BLOCK_EXPLOSION) 
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				else if (e.getEffectType() == EffectType.LAVADEFEND      && event.getCause() == DamageCause.LAVA) 
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				else if (e.getEffectType() == EffectType.DROWNDEFEND     && event.getCause() == DamageCause.DROWNING) 
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				else if (e.getEffectType() == EffectType.SUFFOCATEDEFEND && event.getCause() == DamageCause.SUFFOCATION) 
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				
				if (e.getEffectType() == EffectType.FALLTHRESHOLD && event.getCause() == DamageCause.FALL){
					if(event.getDamage() <= e.getEffectAmount(dCPlayer)){
						if (DwarfCraft.debugMessagesThreshold < 1)
							System.out.println("DC1: Damage less than fall threshold");
						event.setCancelled(true);
					}
				}				  
			}
		}
		if (DwarfCraft.debugMessagesThreshold < 1){
			System.out.println(String.format("DC1: environment damage type: %s base damage: %d new damage: %.2f\r\n",
					event.getCause(), event.getDamage(), damage));
		}
		event.setDamage((int)damage);
		if (damage == 0) 
			event.setCancelled(true);
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity deadThing = event.getEntity();
		if (deadThing instanceof Player)
			return;			
		
		List<ItemStack> items = event.getDrops();
		items.clear();
		
		if (killMap.containsKey(event.getEntity())){
			DCPlayer killer = killMap.get(deadThing);
			for (Skill skill : killer.getSkills().values()) {
				for (Effect effect : skill.getEffects()) {
					if (effect.getEffectType() == EffectType.MOBDROP) {
						
						if (
								   (effect.getId() == 810 && (deadThing instanceof CraftPig))     //Animal Hunter
								|| (effect.getId() == 811 && (deadThing instanceof CraftCow))
								|| (effect.getId() == 812 && (deadThing instanceof CraftSheep))
								|| (effect.getId() == 813 && (deadThing instanceof CraftChicken))
								
								|| (effect.getId() == 820 && (deadThing instanceof CraftCreeper)) //Monster Hunter
								|| (effect.getId() == 823 && (deadThing instanceof CraftSpider))
								|| (effect.getId() == 821 && (deadThing instanceof CraftSkeleton))
								|| (effect.getId() == 822 && (deadThing instanceof CraftSkeleton))
								
								|| (effect.getId() == 850 && (deadThing instanceof CraftZombie))  //Archer
								|| (effect.getId() == 851 && (deadThing instanceof CraftZombie))	) {

							
							int count = Util.randomAmount(effect.getEffectAmount(killer));
							if (DwarfCraft.debugMessagesThreshold < 5){
								System.out.println(String.format("DC5: killed a %s effect called: %d created %d of %s\r\n",
									deadThing.getClass().getSimpleName(), effect.getId(), count, 
									org.bukkit.Material.getMaterial(effect.getOutputId()).name()));
							}
							items.add(effect.getOutput());
						}
					}
				}
			}
		}	
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event instanceof NpcEntityTargetEvent) {
			checkDwarfTrainer((NpcEntityTargetEvent) event);
			event.setCancelled(true);
		}
		if (event.getReason() == TargetReason.CLOSEST_PLAYER){
			if (event.getTarget() instanceof CraftPlayer){
				if (((CraftPlayer)event.getTarget()).getHandle().netServerHandler instanceof NPCNetHandler){
					event.setCancelled(true);
				}
			}			
		}
	}

	protected double armorMitigation(EffectType type, DCPlayer defendDwarf ){
		double multiplier = 1;
		DCPlayer dCPlayer = plugin.getDataManager().find(defendDwarf.getPlayer());
		for (Skill skill : dCPlayer.getSkills().values()){
			for (Effect effect : skill.getEffects()){
				if (effect.getEffectType() == type){
					if (type == EffectType.BOWDEFEND)
						multiplier -=  dCPlayer.countArmorPieces(ArmorType.LEATHER)/4 * effect.getEffectAmount(dCPlayer);
					if (type == EffectType.SUFFOCATEDEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.IRON)/4 * effect.getEffectAmount(dCPlayer);
					if (type == EffectType.LAVADEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.GOLD)/4 * effect.getEffectAmount(dCPlayer);
					if (type == EffectType.DROWNDEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.DIAMOND)/4 * effect.getEffectAmount(dCPlayer);
				}
			}
		}
		assert(multiplier <= 1 && multiplier >= 0);
		return multiplier;
	}
}

