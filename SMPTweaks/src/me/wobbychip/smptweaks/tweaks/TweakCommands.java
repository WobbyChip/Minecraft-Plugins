package me.wobbychip.smptweaks.tweaks;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.wobbychip.smptweaks.Main;

public class TweakCommands {
	private String command;
	private List<String> arguments;
	private String usage;

	public TweakCommands(String command, List<String> arguments) {
		this.command = command;
		this.arguments = arguments;
		this.usage = Main.color + "Usage /smptweaks execute " + command;
	}

	public String getCommand() {
		return command;
	}

	public String getUsage() {
		return usage;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public boolean onTweakCommand(CustomTweak tweak, final CommandSender sender, final Command command, final String label, final String[] args) {
		return true;
	}

	public List<String> onTweakTabComplete(CustomTweak tweak, CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}
}
