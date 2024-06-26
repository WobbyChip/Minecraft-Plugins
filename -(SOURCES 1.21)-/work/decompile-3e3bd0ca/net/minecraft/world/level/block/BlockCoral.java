package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.Fluid;

public class BlockCoral extends Block {

    public static final MapCodec<Block> DEAD_CORAL_FIELD = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("dead");
    public static final MapCodec<BlockCoral> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(BlockCoral.DEAD_CORAL_FIELD.forGetter((blockcoral) -> {
            return blockcoral.deadBlock;
        }), propertiesCodec()).apply(instance, BlockCoral::new);
    });
    private final Block deadBlock;

    public BlockCoral(Block block, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.deadBlock = block;
    }

    @Override
    public MapCodec<BlockCoral> codec() {
        return BlockCoral.CODEC;
    }

    @Override
    protected void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (!this.scanForWater(worldserver, blockposition)) {
            worldserver.setBlock(blockposition, this.deadBlock.defaultBlockState(), 2);
        }

    }

    @Override
    protected IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!this.scanForWater(generatoraccess, blockposition)) {
            generatoraccess.scheduleTick(blockposition, (Block) this, 60 + generatoraccess.getRandom().nextInt(40));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    protected boolean scanForWater(IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            Fluid fluid = iblockaccess.getFluidState(blockposition.relative(enumdirection));

            if (fluid.is(TagsFluid.WATER)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        if (!this.scanForWater(blockactioncontext.getLevel(), blockactioncontext.getClickedPos())) {
            blockactioncontext.getLevel().scheduleTick(blockactioncontext.getClickedPos(), (Block) this, 60 + blockactioncontext.getLevel().getRandom().nextInt(40));
        }

        return this.defaultBlockState();
    }
}
