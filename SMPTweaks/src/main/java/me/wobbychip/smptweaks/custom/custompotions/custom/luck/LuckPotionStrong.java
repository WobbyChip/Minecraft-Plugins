package me.wobbychip.smptweaks.custom.custompotions.custom.luck;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public class LuckPotionStrong extends CustomPotion {
	public LuckPotionStrong() {
		super("luck", Material.GLOWSTONE_DUST, "strong_luck", Color.fromRGB(89, 193, 6));
		this.addPotionEffect(PotionEffectType.LUCK, 1800, 1);
		this.setDisplayName("§r§fPotion of Luck");
		this.setTippedArrow(true, "§r§fArrow of Luck");
		this.setAllowVillagerTrades(false);
	}
}
