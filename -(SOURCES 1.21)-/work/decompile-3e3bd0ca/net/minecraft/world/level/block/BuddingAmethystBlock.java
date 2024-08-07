package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidTypes;

public class BuddingAmethystBlock extends AmethystBlock {

    public static final MapCodec<BuddingAmethystBlock> CODEC = simpleCodec(BuddingAmethystBlock::new);
    public static final int GROWTH_CHANCE = 5;
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();

    @Override
    public MapCodec<BuddingAmethystBlock> codec() {
        return BuddingAmethystBlock.CODEC;
    }

    public BuddingAmethystBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    protected void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (randomsource.nextInt(5) == 0) {
            EnumDirection enumdirection = BuddingAmethystBlock.DIRECTIONS[randomsource.nextInt(BuddingAmethystBlock.DIRECTIONS.length)];
            BlockPosition blockposition1 = blockposition.relative(enumdirection);
            IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);
            Block block = null;

            if (canClusterGrowAtState(iblockdata1)) {
                block = Blocks.SMALL_AMETHYST_BUD;
            } else if (iblockdata1.is(Blocks.SMALL_AMETHYST_BUD) && iblockdata1.getValue(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.MEDIUM_AMETHYST_BUD;
            } else if (iblockdata1.is(Blocks.MEDIUM_AMETHYST_BUD) && iblockdata1.getValue(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.LARGE_AMETHYST_BUD;
            } else if (iblockdata1.is(Blocks.LARGE_AMETHYST_BUD) && iblockdata1.getValue(AmethystClusterBlock.FACING) == enumdirection) {
                block = Blocks.AMETHYST_CLUSTER;
            }

            if (block != null) {
                IBlockData iblockdata2 = (IBlockData) ((IBlockData) block.defaultBlockState().setValue(AmethystClusterBlock.FACING, enumdirection)).setValue(AmethystClusterBlock.WATERLOGGED, iblockdata1.getFluidState().getType() == FluidTypes.WATER);

                worldserver.setBlockAndUpdate(blockposition1, iblockdata2);
            }

        }
    }

    public static boolean canClusterGrowAtState(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is(Blocks.WATER) && iblockdata.getFluidState().getAmount() == 8;
    }
}
