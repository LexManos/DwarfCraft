package com.smartaleq.bukkit.dwarfcraft;

import java.util.HashMap;
import java.util.List;

import org.bukkit.craftbukkit.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

import com.smartaleq.bukkit.dwarfcraft.DCPlayer.ArmorType;

import redecouverte.npcspawner.NpcEntityTargetEvent;
import redecouverte.npcspawner.NpcEntityTargetEvent.NpcTargetReason;

class DCEntityListener extends EntityListener {
	private final DwarfCraft plugin;
	private HashMap<Entity, DCPlayer> killMap;

	protected DCEntityListener(DwarfCraft plugin) {
		this.plugin = plugin;
		killMap = new HashMap<Entity, DCPlayer>();
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			if (event.getEntity() instanceof HumanEntity) {
				if (checkDwarfTrainer((EntityDamageByEntityEvent) event)) { 
					event.setCancelled(true);
					return;
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
			EntityDamageByProjectileEvent sub = (EntityDamageByProjectileEvent) event;
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC4: Damage Event: projectile");
			onEntityDamageByProjectile(sub);
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
			if (DwarfCraft.debugMessagesThreshold < 2)
				System.out.println("DC4: Damage Event: entity by entity");
			onEntityAttack(sub);
		} 
	}
	
	private boolean checkDwarfTrainer(EntityDamageByEntityEvent event) {
		// all we know right now is that event.entity instanceof HumanEntity
		DwarfTrainer trainer = plugin.getDataManager().getTrainer(
				event.getEntity());
		if (trainer != null) {
			if (event.getDamager() instanceof Player) {
				if (plugin.getDataManager().getTrainerRemove().contains(event.getDamager())){
					plugin.getDataManager().removeTrainer(trainer.getUniqueId());
					plugin.getDataManager().getTrainerRemove().remove(event.getDamager());
				}
				// in business, left click
				if (trainer.isGreeter()) {
					trainer.printLeftClick((Player) (event.getDamager()));
				} else {
					trainer.lookAt(event.getDamager());
					Player player = (Player) event.getDamager();
					DCPlayer dCPlayer = plugin.getDataManager().find(player);
					Skill skill = dCPlayer.getSkill(trainer.getSkillTrained());
					int maxSkill = trainer.getMaxSkill();
					plugin.getOut().printSkillInfo(player, skill, dCPlayer,
							maxSkill);
				}
			}
			return true;
		}
		return false;
	}

	private boolean checkDwarfTrainer(NpcEntityTargetEvent event) {
		try {
			DCPlayer dCPlayer = plugin.getDataManager().find(
					((Player) event.getTarget()));
			DwarfTrainer trainer = plugin.getDataManager().getTrainer(
					event.getEntity());
			if (trainer != null) {
				if (event.getTarget() instanceof Player) {
					if (event.getNpcReason() == NpcTargetReason.CLOSEST_PLAYER) {
					} else if (event.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED) {
						if (trainer.isGreeter()) {
							trainer.printRightClick((Player) (event.getTarget()));
						} else {
							trainer.lookAt(event.getTarget());
							trainer.getBasicHumanNpc().animateArmSwing();
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
			victim = (LivingEntity) event.getEntity();
			if (DwarfCraft.debugMessagesThreshold < 0)System.out.println("DC0: victim is living ");}
		else{
			if (DwarfCraft.debugMessagesThreshold < 0)System.out.println("DC0: victim is unliving ");
			return;}
		boolean isPVP = false;
		DCPlayer attacker = null;
		if (victim instanceof Player) {
			isPVP = true;
			if (DwarfCraft.debugMessagesThreshold < 1)System.out.println("DC1: EDBE is PVP");
		}
		int damage = event.getDamage();
		int hp = victim.getHealth();
		if (damager instanceof Player){
			attacker = plugin.getDataManager().find((Player) damager);
			assert(event.getDamager() == attacker.getPlayer());
			assert(attacker!=null);
		}
		// EvP no effects, EvE no effects
		else {
			if (DwarfCraft.debugMessagesThreshold < 4)
				System.out.println("DC4: EVP "
						+ damager.getClass().getSimpleName() + " attacked "
						+ victim.getClass().getSimpleName() + " for " + damage
						+ " of " + hp);
			return;
		}
		ItemStack tool = attacker.getPlayer().getItemInHand();
		int toolId = -1;
		short durability = 0;
		boolean sword = false;
		if (tool != null) {
			toolId = tool.getTypeId();
			durability = tool.getDurability();
			if (toolId == 268||toolId ==272||toolId ==267||toolId ==283||toolId ==276) sword = true;
		}
		
		HashMap<Integer, Skill> skills = attacker.getSkills();
		for (Skill s : skills.values()) {
			for (Effect e : s.getEffects()) {
				if (e.getEffectType() == EffectType.SWORDDURABILITY && sword) {
//					double effectAmount = e.getEffectAmount(attacker);
					if (DwarfCraft.debugMessagesThreshold < 2)
						System.out
								.println("DC2: affected durability of a sword - old:"
										+ durability
										+ " effect called:" + e.getId());
//					tool.setDurability((short) (durability + Util
//							.randomAmount(effectAmount)));
					if (DwarfCraft.debugMessagesThreshold < 3)
						System.out
								.println("DC3: affected durability of a sword - new:"
										+ tool.getDurability());
					boolean brokentool = Util.toolChecker((Player) damager);
					if (DwarfCraft.debugMessagesThreshold < 2)
						System.out
								.println("DC2: sword broken after durability check:"
										+ brokentool);
				}
				if (DwarfCraft.debugMessagesThreshold < 1)System.out.println("DC1: effect:"+e.getId()+e.getEffectType()+sword);
					if (e.getEffectType() == EffectType.PVEDAMAGE && !isPVP
							&& sword) {
						if (hp <= 0) {
							event.setCancelled(true);
							return;
						}
						damage = Util.randomAmount((e.getEffectAmount(attacker))
								* damage);
						if (damage >= hp && !killMap.containsKey(victim)){
			                killMap.put(victim, attacker);
						}
						event.setDamage(damage);
						if (DwarfCraft.debugMessagesThreshold < 6)
							System.out.println("DC6: PVE "
									+ attacker.getPlayer().getName() + " attacked "
									+ victim.getClass().getSimpleName() + " for "
									+ e.getEffectAmount(attacker) + " of "
									+ event.getDamage() + " doing " + damage
									+ " dmg of " + hp + "hp" + " effect called:"
									+ e.getId());
					}
					if (e.getEffectType() == EffectType.PVPDAMAGE && isPVP && sword) {
						damage = Util.randomAmount((e.getEffectAmount(attacker))
								* damage);
						event.setDamage(damage);
						if (DwarfCraft.debugMessagesThreshold < 6)
							System.out.println("DC6: PVP "
									+ attacker.getPlayer().getName() + " attacked "
									+ ((Player) victim).getName() + " for "
									+ e.getEffectAmount(attacker) + " of "
									+ event.getDamage() + " doing " + damage
									+ " dmg of " + hp + "hp" + " effect called:"
									+ e.getId());
				}
			}
		}
	}

	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		LivingEntity attacker = (LivingEntity) event.getDamager();
		LivingEntity hitThing = ((LivingEntity) event.getEntity());
		int hp = hitThing.getHealth();
		if (hp <= 0) {
			event.setCancelled(true);
			return;
		}
		double damage=event.getDamage();
		double mitigation = 1;
		DCPlayer attackDwarf = null;
		DCPlayer defendDwarf = null;
		if(attacker instanceof Player){
			attackDwarf = plugin.getDataManager().find((Player) attacker);
			for (Skill s : attackDwarf.getSkills().values()) {
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.BOWATTACK) {
						damage = e.getEffectAmount(attackDwarf);
					}
				}
			}
		}
		if(hitThing instanceof Player){
			defendDwarf = plugin.getDataManager().find((Player) hitThing);
			for (Skill s : defendDwarf.getSkills().values()) {
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.BOWDEFEND) {
						mitigation = armorMitigation(EffectType.BOWDEFEND, defendDwarf);
					}
				}
			}
		}
		
		damage = Util.randomAmount(damage*mitigation);
		event.setDamage((int) damage);
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
				if (e.getEffectType() == EffectType.FALLDAMAGE && event
						.getCause() == DamageCause.FALL){
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				}
				else if(e.getEffectType() == EffectType.SUFFOCATEDEFEND && event
								.getCause() == DamageCause.SUFFOCATION) {
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				}
				else if	(e.getEffectType() == EffectType.FIREDAMAGE && event
							.getCause() == DamageCause.FIRE){
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				}
				else if		(e.getEffectType() == EffectType.FIREDAMAGE && event
						.getCause() == DamageCause.FIRE_TICK){
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				}
				else if	(e.getEffectType() == EffectType.EXPLOSIONDAMAGE && event
					.getCause() == DamageCause.ENTITY_EXPLOSION){
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				}
				else if	(e.getEffectType() == EffectType.EXPLOSIONDAMAGE && event
				.getCause() == DamageCause.BLOCK_EXPLOSION) {
					damage = Util.randomAmount(e.getEffectAmount(dCPlayer) * damage);
				}
				else if	(e.getEffectType() == EffectType.LAVADEFEND && event
						.getCause() == DamageCause.LAVA) {
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				}
				else if	(e.getEffectType() == EffectType.DROWNDEFEND && event
						.getCause() == DamageCause.DROWNING) {
					damage = Util.randomAmount(damage * armorMitigation(e.getEffectType(), dCPlayer));
				}
				
				}
			if (event.getCause() == DamageCause.FALL){
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.FALLTHRESHOLD){
						if(event.getDamage() <= e.getEffectAmount(dCPlayer)){
							if (DwarfCraft.debugMessagesThreshold < 1)
								System.out.println("DC1: Damage less than fall threshold");
							event.setCancelled(true);
						}
					}
				}
			}
		}
		if (DwarfCraft.debugMessagesThreshold < 1)
					System.out.println("DC1: environment damage type:"
							+ event.getCause() + " base damage:"
							+ event.getDamage() + " new damage:" + damage);
		event.setDamage((int) damage);
		if (damage==0) event.setCancelled(true);
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
			for (Skill s : killer.getSkills().values()) {
				for (Effect e : s.getEffects()) {
					if (e.getEffectType() == EffectType.MOBDROP) {
						if (
								   (e.getId() == 810 && (deadThing instanceof CraftPig))
								|| (e.getId() == 811 && (deadThing instanceof CraftCow))
								|| (e.getId() == 812 && (deadThing instanceof CraftSheep))
								|| (e.getId() == 813 && (deadThing instanceof CraftChicken))
								|| (e.getId() == 820 && (deadThing instanceof CraftCreeper))
								|| (e.getId() == 823 && (deadThing instanceof CraftSpider))
								|| (e.getId() == 821 && (deadThing instanceof CraftSkeleton))
								|| (e.getId() == 822 && (deadThing instanceof CraftSkeleton))
								|| (e.getId() == 850 && (deadThing instanceof CraftZombie))
								|| (e.getId() == 851 && (deadThing instanceof CraftZombie))	) {

							if (DwarfCraft.debugMessagesThreshold < 5)
								System.out.println("DC5: killed a "
										+ deadThing.getClass().getSimpleName()
										+ " effect called:" + e.getId());
							byte i = 0;
							short j = 0;
							items.add(new ItemStack(e.getOutputId(),Util.randomAmount(e.getEffectAmount(killer)),j,i));
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
		return;
	}

	protected double armorMitigation(EffectType type, DCPlayer defendDwarf ){
		double multiplier = 1;
		DCPlayer dCPlayer = plugin.getDataManager().find(defendDwarf.getPlayer());
		for (Skill s:dCPlayer.getSkills().values()){
			for (Effect e: s.getEffects()){
				if (e.getEffectType()==type){
					if (type == EffectType.BOWDEFEND)
						multiplier -=  dCPlayer.countArmorPieces(ArmorType.LEATHER)/4 * e.getEffectAmount(dCPlayer);
					if (type == EffectType.SUFFOCATEDEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.IRON)/4 * e.getEffectAmount(dCPlayer);
					if (type == EffectType.LAVADEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.GOLD)/4 * e.getEffectAmount(dCPlayer);
					if (type == EffectType.DROWNDEFEND)
						multiplier -= dCPlayer.countArmorPieces(ArmorType.DIAMOND)/4 * e.getEffectAmount(dCPlayer);
				}
			}
		}
		assert(multiplier <= 1 && multiplier >= 0);
		return multiplier;
	}
	
	
	

}

