package me.wobbychip.custompotions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.wobbychip.custompotions.Main;
import me.wobbychip.custompotions.potions.CustomPotion;
import me.wobbychip.custompotions.utils.Utils;

public class GetCommand {
	public static List<String> arguments = Arrays.asList("potion", "splash", "lingering", "arrow");
	public static String USAGE_MESSAGE = "&9Usage /cpotions get <potion_name> [potion | splash | lingering | arrow]";
	public static String NO_CONSOLE = "&9This command can only be executed by the player!";

	public static boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		boolean isCreative = ((sender instanceof Player) && (((Player) sender).getGameMode() == GameMode.CREATIVE));

    	if (!(sender instanceof Player)) {
			Utils.sendMessage(sender, NO_CONSOLE);
			return true;
		}

    	if (!Utils.hasPermissions(sender, "cpotions.get") && !isCreative) {
			Utils.sendMessage(sender, Commands.NO_PERMISSIONS);
			return true;
    	}

    	if (args.length < 2) {
			Utils.sendMessage(sender, USAGE_MESSAGE);
			return true;
		}

		ItemStack item = new ItemStack(Material.AIR);
		CustomPotion potion = Main.manager.getCustomPotion(args[0].toLowerCase());

		if (potion == null) {
			Utils.sendMessage(sender, Commands.NO_POTION);
			return true;
		}

		switch (args[1].toLowerCase()) {
			case "potion":
				item.setType(Material.POTION);
				item = potion.setProperties(item);
				break;
			case "splash":
				item.setType(Material.SPLASH_POTION);
				item = potion.setProperties(item);
				break;
			case "lingering":
				item.setType(Material.LINGERING_POTION);
				item = potion.setProperties(item);
				break;
			case "arrow":
				item = potion.getTippedArrow(false, 64);
				break;
			default:
				Utils.sendMessage(sender, USAGE_MESSAGE);
				return true;
		}

		((Player) sender).getInventory().addItem(item);
		return true;
	}

	public static List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		boolean isCreative = ((sender instanceof Player) && (((Player) sender).getGameMode() == GameMode.CREATIVE));

    	if (!Utils.hasPermissions(sender, "cpotions.get") && !isCreative) {
			return null;
    	}

		if (args.length == 2) {
			return new ArrayList<String>(Main.manager.getPotionSet());
		}

		if (args.length == 3) {
			return arguments;
		}

		return null;
	}
}