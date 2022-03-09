package me.wobbychip.custompotions.custom;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionType;

import me.wobbychip.custompotions.potions.CustomPotion;
import me.wobbychip.custompotions.potions.PotionManager;

public class FirePotion extends CustomPotion {
	public FirePotion() {
		super(PotionManager.getPotion(PotionType.AWKWARD, false, false), Material.BLAZE_ROD, "fire", Color.fromRGB(226, 88, 34));
		this.setDisplayName("�r�fPotion of Fire");
		this.setLore(Arrays.asList("�9Fire In The Hole"));
		this.setTippedArrow(true, "�r�fArrow of Fire");
	}

	public void onPotionConsume(PlayerItemConsumeEvent event) {
		event.getPlayer().setFireTicks(20*10);
	}

	public void onPotionSplash(PotionSplashEvent event) {
		for (LivingEntity livingEntity : event.getAffectedEntities()) {
			livingEntity.setFireTicks(20*5);
		}
	}

	public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
		for (LivingEntity livingEntity : event.getAffectedEntities()) {
			livingEntity.setFireTicks(20*1);
		}
	}

	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			if (event.getHitEntity() != null) {
				event.getHitEntity().setFireTicks(20*5);
			}
		}
	}
}
