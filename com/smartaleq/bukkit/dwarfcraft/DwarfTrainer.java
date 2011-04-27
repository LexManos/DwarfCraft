package com.smartaleq.bukkit.dwarfcraft;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.World;

//import redecouverte.npcspawner.*;
import org.martin.bukkit.npclib.*;

public final class DwarfTrainer {
	private NPCEntity mEntity;
	private Integer   mSkillID;
	private Integer   mMaxLevel;
	private boolean   mIsGreeter;
	private String    mMsgID;
	private World     mWorld;
	private Material  mHeldItem;
	private String    mName;
	private String    mID;
	private final DwarfCraft plugin;

	public DwarfTrainer(final DwarfCraft plugin, Location location,
			String uniqueId, String name, Integer skillId, Integer maxSkill,
			String greeterMessage, boolean isGreeter) {
		
		this.plugin     = plugin;
		this.mSkillID   = skillId;
		this.mMaxLevel  = maxSkill;
		this.mMsgID     = greeterMessage;
		this.mIsGreeter = isGreeter;
		this.mWorld     = location.getWorld();
		this.mName      = name;
		this.mID        = uniqueId;
		this.mEntity    = plugin.getNPCManager().spawnNPC(mName, location, uniqueId);

		if (mIsGreeter)
			mHeldItem = Material.AIR;
		else
			mHeldItem = plugin.getConfigManager().getGenericSkill(skillId).getTrainerHeldMaterial();
		
		assert (mHeldItem != null);
		
		if (mHeldItem != Material.AIR)
			mEntity.setItemInHand(mHeldItem);
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) 
			return true;
		else if (that instanceof HumanEntity) 
			return (mEntity.getBukkitEntity().getEntityId() == ((HumanEntity)that).getEntityId());		
		return false;
	}

	public NPCEntity getBasicHumanNpc() {
		return mEntity;
	}

	protected Location getLocation() {
		return mEntity.getBukkitEntity().getLocation();
	}

	protected int getMaterial() {
		if (mHeldItem != null)
			return mHeldItem.getId();
		else
			return (Material.AIR.getId());
	}

	public Integer getMaxSkill() {
		return mMaxLevel;
	}

	protected String getMessage() {
		return mMsgID;
	}

	protected String getName() {
		return mEntity.getName();
	}

	public Integer getSkillTrained() {
		return mSkillID;
	}

	public String getUniqueId() {
		return mID;
	}

	protected World getWorld() {
		return mWorld;
	}

	public boolean isGreeter() {
		return mIsGreeter;
	}

	public void lookAt(Entity target) {
		assert (target != null);
		Location l;
		l = target.getLocation().clone();
		if (target instanceof Player) {
			l.setY(l.getY() + ((Player) target).getEyeHeight());
		}
		this.lookAt(l);
		return;
	}

	protected void lookAt(Location l) {
		assert (l != null);
		return;
	}

	public void printLeftClick(Player player) {
		GreeterMessage msg = plugin.getDataManager().getGreeterMessage(mMsgID);
		if (msg != null) {
			plugin.getOut().sendMessage(player, msg.getLeftClickMessage());
		} else {
			System.out.println(String.format(
					"[DC] Error: Greeter %s has no left click message. Check your configuration file for message ID %d", getUniqueId(), mMsgID));
		}
		return;
	}

	public void printRightClick(Player player) {
		GreeterMessage msg = plugin.getDataManager().getGreeterMessage(mMsgID);
		if (msg != null) {
			plugin.getOut().sendMessage(player, msg.getRightClickMessage());
		}
		return;
	}

	@SuppressWarnings("unused")
	public void trainSkill(DCPlayer dCPlayer) {
		Skill  skill  = dCPlayer.getSkill(mSkillID);
		Player player = dCPlayer.getPlayer();
		String tag    = String.format("&6[Train &b%d&6] ", skill.getId());

		if (skill == null) {
			plugin.getOut().sendMessage(player, "&cYour race doesn't have this skill!", tag);
			return;
		}
		
		List<ItemStack> trainingCosts = dCPlayer.calculateTrainingCost(skill);

		if (skill.getLevel() >= 30) {
			plugin.getOut().sendMessage(player, "&cYour skill is max level (30)!", tag);
			return;
		}
		
		if (skill.getLevel() >= mMaxLevel) {
			plugin.getOut().sendMessage(player, "&cI can't teach you any more, find a higher level trainer", tag);
			return;
		}

		boolean hasMats = true;
		for (ItemStack itemStack : trainingCosts) {
			if (itemStack == null)
				continue;
			if (itemStack.getAmount() == 0)
				continue;

			if (!player.getInventory().contains(itemStack.getTypeId(), itemStack.getAmount())){
				plugin.getOut().sendMessage(player,
						String.format("&cYou do not have the &2%d %s &crequired", itemStack.getAmount(), itemStack.getType()), tag);
				hasMats = false;
			} else
				plugin.getOut().sendMessage(player,
						String.format("&aYou have the &2%d %s &arequired", itemStack.getAmount(), itemStack.getType()), tag);

		}
		if(!hasMats)
			return;
		
		skill.setLevel(skill.getLevel() + 1);
		for (ItemStack itemStack : trainingCosts)
			player.getInventory().removeItem(itemStack);
		
		plugin.getOut().sendMessage(player, "&6Training Successful!", tag);
		plugin.getDataManager().saveDwarfData(dCPlayer);
	}
}