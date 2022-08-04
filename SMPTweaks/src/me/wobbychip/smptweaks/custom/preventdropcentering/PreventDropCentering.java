package me.wobbychip.smptweaks.custom.preventdropcentering;

import org.bukkit.Bukkit;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.tweaks.CustomTweak;

public class PreventDropCentering extends CustomTweak {
	public PreventDropCentering() {
		super(PreventDropCentering.class.getSimpleName(), true);
	}

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new Events(), Main.plugin);
	}
}
