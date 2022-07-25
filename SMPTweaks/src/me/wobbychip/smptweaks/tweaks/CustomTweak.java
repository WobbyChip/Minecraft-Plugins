package me.wobbychip.smptweaks.tweaks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.utils.Utils;

public class CustomTweak {
	private String name;
	private boolean enabled = true;
	
	public CustomTweak(String name) {
		if (!Main.plugin.getConfig().contains(name.toUpperCase())) {
			Main.plugin.getConfig().set(name.toUpperCase(), enabled);
			Main.plugin.saveConfig();
		} else {
			this.enabled = Main.plugin.getConfig().getBoolean(name.toUpperCase());
		}

		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void printMessage(String message) {
		Utils.sendMessage(String.format("&9[SMPTweaks] %s ", message));
	}

	public void printEnabled() {
		Utils.sendMessage(String.format("&9[SMPTweaks] %s has loaded.", this.getName()));
	}

	public void printDisabled() {
		Utils.sendMessage(String.format("&9[SMPTweaks] %s is set to disabled.", this.getName()));
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) { return true; }
	public void onDisable() {}
}
