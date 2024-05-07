package me.wobbychip.smptweaks.custom.custompotions.custom;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class VoidPotion extends CustomPotion {
	public VoidPotion() {
		super("base", Material.CRYING_OBSIDIAN, "void", Color.fromRGB(0, 0, 0));
		this.setDisplayName("§r§fPotion of Void");
		this.setLore(List.of("§9Destroys bedrock"));
		this.setTippedArrow(true, "§r§fArrow of Void");
		this.setAllowVillagerTrades(false);
	}

	public void onPotionConsume(PlayerItemConsumeEvent event) {
		event.getPlayer().setHealth(0);
	}

	public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
		event.setCancelled(true);
	}

	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			if ((event.getHitBlock() != null) && destroyBlock(event.getHitBlock())) {
				event.getEntity().remove();
			}
		}

		if (event.getEntity() instanceof ThrownPotion) {
			if ((event.getHitBlock() != null)) {
				Block block = event.getHitBlock();
				destroyBlock(block);
				destroyBlock(block.getRelative(BlockFace.UP));
				destroyBlock(block.getRelative(BlockFace.DOWN));
				destroyBlock(block.getRelative(BlockFace.EAST));
				destroyBlock(block.getRelative(BlockFace.WEST));
				destroyBlock(block.getRelative(BlockFace.NORTH));
				destroyBlock(block.getRelative(BlockFace.SOUTH));
			}
		}
	}

	public boolean destroyBlock(Block block) {
		if (block.getType() == Material.BEDROCK) {
			Location loc = block.getLocation().clone().add(.5, .5, .5);
			block.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.5f);
			block.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5);
			block.setType(Material.AIR);
			return true;
		}

		return false;
	}
}
