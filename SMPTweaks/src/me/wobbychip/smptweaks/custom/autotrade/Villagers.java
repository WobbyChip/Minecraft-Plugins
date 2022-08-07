package me.wobbychip.smptweaks.custom.autotrade;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import me.wobbychip.smptweaks.utils.PersistentUtils;
import me.wobbychip.smptweaks.utils.ReflectionUtils;
import net.minecraft.network.protocol.game.PacketPlayInTrSel;

public class Villagers {
	public static boolean tradeVillager(Block trader, Player player, Villager villager, int trade) {
		if (!canBuy(player, villager, trade)) { return false; }
		player.openMerchant(villager, true);

		for (ItemStack item : villager.getRecipes().get(trade).getIngredients()) {
			item.setAmount(item.getMaxStackSize());
			player.getInventory().addItem(item);
		}

		ReflectionUtils.handlePacket(player, new PacketPlayInTrSel(trade));
		ReflectionUtils.quickMoveStack(player, 2);

		player.closeInventory();
		player.getInventory().clear();

		//FUCK THE BUKKIT AGAIN, EntitySpawnEvent not working with xp
		for (Entity entity : villager.getLocation().getWorld().getNearbyEntities(villager.getLocation(), 1, 1, 1)) {
			if (!(entity instanceof ExperienceOrb)) { continue; }
			if (storeOrb((ExperienceOrb) entity, villager, trader)) { break; }
		}

		return true;
	}

	public static boolean canBuy(Player player, Villager villager, int trade) {
		player.openMerchant(villager, true);

		TradeSelectEvent event = new TradeSelectEvent(player.getOpenInventory(), trade);
		Bukkit.getPluginManager().callEvent(event);

		player.closeInventory();
		return (event.getResult() != Result.DENY);
	}

	public static boolean storeOrb(ExperienceOrb orb, Villager villager, Block block) {
		for (Entity entity : orb.getLocation().getWorld().getNearbyEntities(orb.getLocation(), 0.01, 0.01, 0.01)) {
			if (!entity.getUniqueId().equals(villager.getUniqueId())) { continue; }
			storeXp(block, orb.getExperience());
			orb.remove();
			return true;
		}

		return false;
	}

	public static void storeXp(Block block, int xp) {
		int amountXP = 0;

		if (PersistentUtils.hasPersistentDataInteger(block, AutoTrade.isAutoTrade)) {
			amountXP = PersistentUtils.getPersistentDataInteger(block, AutoTrade.isAutoTrade);
		}

		PersistentUtils.setPersistentDataInteger(block, AutoTrade.isAutoTrade, amountXP+xp);
	}

	public static void releaseXp(Block block, Location location) {
		if (!PersistentUtils.hasPersistentDataInteger(block, AutoTrade.isAutoTrade)) { return; }
		Integer amountXP = PersistentUtils.getPersistentDataInteger(block, AutoTrade.isAutoTrade);
		PersistentUtils.setPersistentDataInteger(block, AutoTrade.isAutoTrade, 0);
		if (amountXP <= 0) { return; }

		ExperienceOrb orb = location.getWorld().spawn(location, ExperienceOrb.class);
		orb.setExperience(amountXP);
	}

	public static ItemStack adjustItem(Villager villager, MerchantRecipe recipe, ItemStack item) {
		int reputation = ReflectionUtils.getPlayerReputation(villager, AutoTrade.fakePlayer);
		float f = (float) reputation * recipe.getPriceMultiplier();
        int i = (int) f;

		recipe.setSpecialPrice(-(f < (float) i ? i - 1 : i));
		recipe.adjust(item);
		recipe.setSpecialPrice(0);
		return item;
	}
}