package me.wobbychip.smptweaks.custom.preventdropcentering;

import org.bukkit.Bukkit;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.tweaks.CustomTweak;

public class PreventDropCentering extends CustomTweak {
	public PreventDropCentering() {
		super("PreventDropCentering");

		if (this.isEnabled()) {
			Bukkit.getPluginManager().registerEvents(new Events(), Main.plugin);
			this.printEnabled();
		} else {
			this.printDisabled();
		}
	}
}
