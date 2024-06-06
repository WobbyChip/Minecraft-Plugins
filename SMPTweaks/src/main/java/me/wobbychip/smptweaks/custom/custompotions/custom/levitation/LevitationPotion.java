package me.wobbychip.smptweaks.custom.custompotions.custom.levitation;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class LevitationPotion extends CustomPotion {
	public LevitationPotion() {
		super(PotionType.AWKWARD, Material.SHULKER_SHELL, "levitation", Color.fromRGB(206, 255, 255));
		this.addPotionEffect(PotionEffectType.LEVITATION, 900, 0);
		this.setDisplayName("§r§fPotion of Levitation");
		this.setTippedArrow(true, "§r§fArrow of Levitation");
		this.setAllowVillagerTrades(true);
	}
}
