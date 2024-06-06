package me.wobbychip.smptweaks.custom.custompotions.custom.saturation;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public class SaturationPotionStrong extends CustomPotion {
	public SaturationPotionStrong() {
		super("saturation", Material.GLOWSTONE_DUST, "strong_saturation", Color.fromRGB(248, 36, 36));
		this.addPotionEffect(PotionEffectType.SATURATION, 450, 1);
		this.setDisplayName("§r§fPotion of Saturation");
		this.setTippedArrow(true, "§r§fArrow of Saturation");
		this.setAllowVillagerTrades(false);
	}
}
