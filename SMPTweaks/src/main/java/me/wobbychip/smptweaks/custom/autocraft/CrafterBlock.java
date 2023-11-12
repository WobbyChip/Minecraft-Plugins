package me.wobbychip.smptweaks.custom.autocraft;

import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.library.customblocks.blocks.CustomBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CrafterBlock extends CustomBlock {
    public CrafterBlock() {
        super("crafter", Material.DISPENSER);
        this.setCustomMaterial(Material.PISTON);
        this.setCustomTitle("Crafter");
    }

    @Override
    public Recipe prepareRecipe(NamespacedKey key) {
        ShapedRecipe recipe = new ShapedRecipe(key, getDropItem());
        recipe.shape("III", "ICI", "RDR");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);
        recipe.setIngredient('D', Material.DISPENSER);
        recipe.setIngredient('R', Material.REDSTONE);
        return recipe;
    }

    @Override
    public ItemStack prepareDropItem() {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Main.sym_color + "dCrafter");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean prepareCraft(PrepareItemCraftEvent event, World world, ItemStack result) {
        return AutoCraft.tweak.getGameRuleBoolean(world);
    }
}