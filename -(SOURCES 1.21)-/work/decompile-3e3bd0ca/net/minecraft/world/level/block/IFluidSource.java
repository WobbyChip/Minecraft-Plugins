package net.minecraft.world.level.block;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;

public interface IFluidSource {

    ItemStack pickupBlock(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata);

    Optional<SoundEffect> getPickupSound();
}
