package me.wobbychip.smptweaks.custom.custompotions.custom;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.List;

public class AmethystPotion extends CustomPotion {
	public AmethystPotion() {
		super(PotionType.MUNDANE, Material.AMETHYST_SHARD, "amethyst", Color.fromRGB(153, 102, 204));
		this.setDisplayName("§r§fPotion of Amethyst");
		this.setLore(List.of("§9Used as base for other potions"));
		this.setTippedArrow(false, "§r§fArrow of Amethyst");
		this.setAllowVillagerTrades(true);
	}
}
