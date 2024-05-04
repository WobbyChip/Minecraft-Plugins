package net.minecraft.world.item.enchantment;

import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class BreachEnchantment extends Enchantment {

    public BreachEnchantment() {
        super(Enchantment.definition(TagsItem.MACE_ENCHANTABLE, 2, 4, Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4, FeatureFlagSet.of(FeatureFlags.UPDATE_1_21), EnumItemSlot.MAINHAND));
    }

    public static float calculateArmorBreach(float f, float f1) {
        return MathHelper.clamp(f1 - 0.15F * f, 0.0F, 1.0F);
    }
}
