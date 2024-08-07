package me.wobbychip.smptweaks.custom.anticreepergrief;

import org.bukkit.Bukkit;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.tweaks.CustomTweak;

public class AntiCreeperGrief extends CustomTweak {
	public static CustomTweak tweak;

	public AntiCreeperGrief() {
		super(AntiCreeperGrief.class, false, false);
		this.setGameRule("doCreeperGrief", false, false);
		this.setDescription("Prevent creepers from exploding blocks with gamerule.");
		AntiCreeperGrief.tweak = this;
	}

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new Events(), Main.plugin);
	}
}
