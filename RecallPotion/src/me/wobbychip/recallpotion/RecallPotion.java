package me.wobbychip.recallpotion;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionType;

import me.wobbychip.recallpotion.potions.CustomPotion;
import me.wobbychip.recallpotion.potions.PotionManager;
import me.wobbychip.recallpotion.utils.Utils;

public class RecallPotion extends CustomPotion {
	public RecallPotion() {
		super(PotionManager.getPotion(PotionType.AWKWARD, false, false), Material.CHORUS_FRUIT, "recall", Color.fromRGB(23, 193, 224));
		this.setDisplayName("�r�fPotion of Recalling");
		this.setLore(Arrays.asList("�9Teleport to Spawnpoint"));
		this.setTippedArrow(true, "�r�fArrow of Recalling");
	}

	@Override
	public void onPotionConsume(PlayerItemConsumeEvent event) {
		Utils.respawnPlayer(event.getPlayer());
	}

	@Override
	public void onPotionSplash(PotionSplashEvent event) {
		for (LivingEntity livingEntity : event.getAffectedEntities()) {
			if (livingEntity instanceof Player) { Utils.respawnPlayer((Player) livingEntity); }
		}
	}

	@Override
	public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
		for (LivingEntity livingEntity : event.getAffectedEntities()) {
			if (livingEntity instanceof Player) { Utils.respawnPlayer((Player) livingEntity); }
		}
	}

	@Override
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getHitEntity() instanceof Player) {
			Utils.respawnPlayer((Player) event.getHitEntity());
		}
	}
}
