package me.wobbychip.smptweaks.custom.custompotions.custom;

import me.wobbychip.smptweaks.custom.custompotions.potions.CustomPotion;
import me.wobbychip.smptweaks.utils.ReflectionUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class EndRecallPotion extends CustomPotion {
	public EndRecallPotion() {
		super("nether_recall", Material.DRAGON_HEAD, "end_recall", Color.fromRGB(60, 0, 100));
		this.setDisplayName("§r§fPotion of End Recall");
		this.setLore(List.of("§9Teleports to The End"));
		this.setTippedArrow(true, "§r§fArrow of End Recall");
		this.setAllowVillagerTrades(false);
	}

	@Override
	public boolean onAffectPlayer(Player player, Event event) {
		ReflectionUtils.changeDimension(player, World.Environment.THE_END);
		return true;
	}
}
