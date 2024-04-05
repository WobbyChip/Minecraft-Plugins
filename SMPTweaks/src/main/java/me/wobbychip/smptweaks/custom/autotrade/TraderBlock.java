package me.wobbychip.smptweaks.custom.autotrade;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.library.customblocks.blocks.CustomBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class TraderBlock extends CustomBlock {
    public TraderBlock() {
        super("trader_block", Material.DISPENSER);
        this.setCustomModel(1000210000, 1000220000);
        this.setCustomName(Main.SYM_COLOR + "rAuto Trader");
        this.setCustomTitle("Auto Trader");
        this.setDispensable(Dispensable.CUSTOM);
        this.setComparable(Comparable.IGNORE);
    }

    @Override
    public Recipe prepareRecipe(NamespacedKey key, ItemStack itemStack) {
        ShapedRecipe recipe = new ShapedRecipe(key, itemStack);
        recipe.shape("EEE", "EDE", "RNR");
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('D', Material.DISPENSER);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('N', Material.NETHER_STAR);
        return recipe;
    }

    @Override
    public boolean prepareDispense(Block block, HashMap<ItemStack, Map.Entry<ItemStack, Integer>> dispense) {
        if (!AutoTrade.tweak.getGameRuleBoolean((block.getWorld()))) { return false; }
        Traders.handleTrader(block).forEach(e -> dispense.put(e, Map.entry(new ItemStack(Material.AIR), -1)));
        return !dispense.isEmpty();
    }

    @Override
    public void remove(Block block) {
        Villagers.releaseXp(block, block.getLocation().clone().add(0.5, 0.5, 0.5));
    }
}
