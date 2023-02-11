package me.wobbychip.smptweaks.custom.noendportal;

import org.bukkit.Bukkit;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.tweaks.CustomTweak;

public class NoEndPortal extends CustomTweak {
	public NoEndPortal() {
		super(NoEndPortal.class.getSimpleName(), false, true);
		this.setDescription("Disable end portal with custom gamerule (doEndPortal).");
	}

	public void onEnable() {
		Main.gameRules.addGameRule("doEndPortal", true);
		Bukkit.getPluginManager().registerEvents(new Events(), Main.plugin);
	}
}
