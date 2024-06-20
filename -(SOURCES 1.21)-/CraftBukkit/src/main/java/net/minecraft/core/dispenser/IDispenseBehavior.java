package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseChestedAbstract;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemBoneMeal;
import net.minecraft.world.item.ItemMonsterEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBeehive;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.BlockPumpkinCarved;
import net.minecraft.world.level.block.BlockRespawnAnchor;
import net.minecraft.world.level.block.BlockShulkerBox;
import net.minecraft.world.level.block.BlockSkull;
import net.minecraft.world.level.block.BlockTNT;
import net.minecraft.world.level.block.BlockWitherSkull;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.IFluidSource;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBeehive;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.slf4j.Logger;

// CraftBukkit start
import net.minecraft.world.item.ItemBucket;
import net.minecraft.world.level.block.BlockSapling;
import net.minecraft.world.level.block.IFluidContainer;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.world.StructureGrowEvent;
// CraftBukkit end

public interface IDispenseBehavior {

    Logger LOGGER = LogUtils.getLogger();
    IDispenseBehavior NOOP = (sourceblock, itemstack) -> {
        return itemstack;
    };

    ItemStack dispense(SourceBlock sourceblock, ItemStack itemstack);

    static void bootStrap() {
        BlockDispenser.registerProjectileBehavior(Items.ARROW);
        BlockDispenser.registerProjectileBehavior(Items.TIPPED_ARROW);
        BlockDispenser.registerProjectileBehavior(Items.SPECTRAL_ARROW);
        BlockDispenser.registerProjectileBehavior(Items.EGG);
        BlockDispenser.registerProjectileBehavior(Items.SNOWBALL);
        BlockDispenser.registerProjectileBehavior(Items.EXPERIENCE_BOTTLE);
        BlockDispenser.registerProjectileBehavior(Items.SPLASH_POTION);
        BlockDispenser.registerProjectileBehavior(Items.LINGERING_POTION);
        BlockDispenser.registerProjectileBehavior(Items.FIREWORK_ROCKET);
        BlockDispenser.registerProjectileBehavior(Items.FIRE_CHARGE);
        BlockDispenser.registerProjectileBehavior(Items.WIND_CHARGE);
        DispenseBehaviorItem dispensebehavioritem = new DispenseBehaviorItem() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
                EntityTypes<?> entitytypes = ((ItemMonsterEgg) itemstack.getItem()).getType(itemstack);

                // CraftBukkit start
                WorldServer worldserver = sourceblock.level();
                ItemStack itemstack1 = itemstack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.grow(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.grow(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }

                try {
                    entitytypes.spawn(sourceblock.level(), itemstack, (EntityHuman) null, sourceblock.pos().relative(enumdirection), EnumMobSpawn.DISPENSER, enumdirection != EnumDirection.UP, false);
                } catch (Exception exception) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", sourceblock.pos(), exception); // CraftBukkit - decompile error
                    return ItemStack.EMPTY;
                }

                // itemstack.shrink(1); // Handled during event processing
                // CraftBukkit end
                sourceblock.level().gameEvent((Entity) null, (Holder) GameEvent.ENTITY_PLACE, sourceblock.pos());
                return itemstack;
            }
        };
        Iterator iterator = ItemMonsterEgg.eggs().iterator();

        while (iterator.hasNext()) {
            ItemMonsterEgg itemmonsteregg = (ItemMonsterEgg) iterator.next();

            BlockDispenser.registerBehavior(itemmonsteregg, dispensebehavioritem);
        }

        BlockDispenser.registerBehavior(Items.ARMOR_STAND, new DispenseBehaviorItem() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
                BlockPosition blockposition = sourceblock.pos().relative(enumdirection);
                WorldServer worldserver = sourceblock.level();

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.grow(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.grow(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                Consumer<EntityArmorStand> consumer = EntityTypes.appendDefaultStackConfig((entityarmorstand) -> {
                    entityarmorstand.setYRot(enumdirection.toYRot());
                }, worldserver, itemstack, (EntityHuman) null);
                EntityArmorStand entityarmorstand = (EntityArmorStand) EntityTypes.ARMOR_STAND.spawn(worldserver, consumer, blockposition, EnumMobSpawn.DISPENSER, false, false);

                if (entityarmorstand != null) {
                    // itemstack.shrink(1); // CraftBukkit - Handled during event processing
                }

                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Items.SADDLE, new DispenseBehaviorMaybe() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                List<EntityLiving> list = sourceblock.level().getEntitiesOfClass(EntityLiving.class, new AxisAlignedBB(blockposition), (entityliving) -> {
                    if (!(entityliving instanceof ISaddleable isaddleable)) {
                        return false;
                    } else {
                        return !isaddleable.isSaddled() && isaddleable.isSaddleable();
                    }
                });

                if (!list.isEmpty()) {
                    // CraftBukkit start
                    ItemStack itemstack1 = itemstack.split(1);
                    WorldServer world = sourceblock.level();
                    org.bukkit.block.Block block = CraftBlock.at(world, sourceblock.pos());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                    BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.entity.CraftLivingEntity) list.get(0).getBukkitEntity());
                    if (!BlockDispenser.eventFired) {
                        world.getCraftServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        itemstack.grow(1);
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        itemstack.grow(1);
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != ItemArmor.DISPENSE_ITEM_BEHAVIOR) {
                            idispensebehavior.dispense(sourceblock, eventStack);
                            return itemstack;
                        }
                    }
                    ((ISaddleable) list.get(0)).equipSaddle(itemstack1, SoundCategory.BLOCKS);
                    // CraftBukkit end
                    this.setSuccess(true);
                    return itemstack;
                } else {
                    return super.execute(sourceblock, itemstack);
                }
            }
        });
        DispenseBehaviorMaybe dispensebehaviormaybe = new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                List<EntityHorseAbstract> list = sourceblock.level().getEntitiesOfClass(EntityHorseAbstract.class, new AxisAlignedBB(blockposition), (entityhorseabstract) -> {
                    return entityhorseabstract.isAlive() && entityhorseabstract.canUseSlot(EnumItemSlot.BODY);
                });
                Iterator iterator1 = list.iterator();

                EntityHorseAbstract entityhorseabstract;

                do {
                    if (!iterator1.hasNext()) {
                        return super.execute(sourceblock, itemstack);
                    }

                    entityhorseabstract = (EntityHorseAbstract) iterator1.next();
                } while (!entityhorseabstract.isBodyArmorItem(itemstack) || entityhorseabstract.isWearingBodyArmor() || !entityhorseabstract.isTamed());

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.split(1);
                WorldServer world = sourceblock.level();
                org.bukkit.block.Block block = CraftBlock.at(world, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.entity.CraftLivingEntity) entityhorseabstract.getBukkitEntity());
                if (!BlockDispenser.eventFired) {
                    world.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.grow(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.grow(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != ItemArmor.DISPENSE_ITEM_BEHAVIOR) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }

                entityhorseabstract.setBodyArmorItem(CraftItemStack.asNMSCopy(event.getItem()));
                // CraftBukkit end
                this.setSuccess(true);
                return itemstack;
            }
        };

        BlockDispenser.registerBehavior(Items.LEATHER_HORSE_ARMOR, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.IRON_HORSE_ARMOR, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.GOLDEN_HORSE_ARMOR, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.DIAMOND_HORSE_ARMOR, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.WHITE_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.ORANGE_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.CYAN_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.BLUE_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.BROWN_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.BLACK_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.GRAY_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.GREEN_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.LIGHT_BLUE_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.LIGHT_GRAY_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.LIME_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.MAGENTA_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.PINK_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.PURPLE_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.RED_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.YELLOW_CARPET, dispensebehaviormaybe);
        BlockDispenser.registerBehavior(Items.CHEST, new DispenseBehaviorMaybe() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                List<EntityHorseChestedAbstract> list = sourceblock.level().getEntitiesOfClass(EntityHorseChestedAbstract.class, new AxisAlignedBB(blockposition), (entityhorsechestedabstract) -> {
                    return entityhorsechestedabstract.isAlive() && !entityhorsechestedabstract.hasChest();
                });
                Iterator iterator1 = list.iterator();

                EntityHorseChestedAbstract entityhorsechestedabstract;

                do {
                    if (!iterator1.hasNext()) {
                        return super.execute(sourceblock, itemstack);
                    }

                    entityhorsechestedabstract = (EntityHorseChestedAbstract) iterator1.next();
                    // CraftBukkit start
                } while (!entityhorsechestedabstract.isTamed());
                ItemStack itemstack1 = itemstack.split(1);
                WorldServer world = sourceblock.level();
                org.bukkit.block.Block block = CraftBlock.at(world, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseArmorEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.entity.CraftLivingEntity) entityhorsechestedabstract.getBukkitEntity());
                if (!BlockDispenser.eventFired) {
                    world.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != ItemArmor.DISPENSE_ITEM_BEHAVIOR) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                entityhorsechestedabstract.getSlot(499).set(CraftItemStack.asNMSCopy(event.getItem()));
                // CraftBukkit end

                // itemstack.shrink(1); // CraftBukkit - handled above
                this.setSuccess(true);
                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Items.OAK_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.OAK));
        BlockDispenser.registerBehavior(Items.SPRUCE_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.SPRUCE));
        BlockDispenser.registerBehavior(Items.BIRCH_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.BIRCH));
        BlockDispenser.registerBehavior(Items.JUNGLE_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.JUNGLE));
        BlockDispenser.registerBehavior(Items.DARK_OAK_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.DARK_OAK));
        BlockDispenser.registerBehavior(Items.ACACIA_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.ACACIA));
        BlockDispenser.registerBehavior(Items.CHERRY_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.CHERRY));
        BlockDispenser.registerBehavior(Items.MANGROVE_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.MANGROVE));
        BlockDispenser.registerBehavior(Items.BAMBOO_RAFT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.BAMBOO));
        BlockDispenser.registerBehavior(Items.OAK_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.OAK, true));
        BlockDispenser.registerBehavior(Items.SPRUCE_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.SPRUCE, true));
        BlockDispenser.registerBehavior(Items.BIRCH_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.BIRCH, true));
        BlockDispenser.registerBehavior(Items.JUNGLE_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.JUNGLE, true));
        BlockDispenser.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.DARK_OAK, true));
        BlockDispenser.registerBehavior(Items.ACACIA_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.ACACIA, true));
        BlockDispenser.registerBehavior(Items.CHERRY_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.CHERRY, true));
        BlockDispenser.registerBehavior(Items.MANGROVE_CHEST_BOAT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.MANGROVE, true));
        BlockDispenser.registerBehavior(Items.BAMBOO_CHEST_RAFT, new DispenseBehaviorBoat(EntityBoat.EnumBoatType.BAMBOO, true));
        DispenseBehaviorItem dispensebehavioritem1 = new DispenseBehaviorItem() {
            private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();

            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem) itemstack.getItem();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                WorldServer worldserver = sourceblock.level();

                // CraftBukkit start
                int x = blockposition.getX();
                int y = blockposition.getY();
                int z = blockposition.getZ();
                IBlockData iblockdata = worldserver.getBlockState(blockposition);
                if (iblockdata.isAir() || iblockdata.canBeReplaced() || (dispensiblecontaineritem instanceof ItemBucket && iblockdata.getBlock() instanceof IFluidContainer && ((IFluidContainer) iblockdata.getBlock()).canPlaceLiquid((EntityHuman) null, worldserver, blockposition, iblockdata, ((ItemBucket) dispensiblecontaineritem).content))) {
                    org.bukkit.block.Block block = CraftBlock.at(worldserver, sourceblock.pos());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                    BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(x, y, z));
                    if (!BlockDispenser.eventFired) {
                        worldserver.getCraftServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                            idispensebehavior.dispense(sourceblock, eventStack);
                            return itemstack;
                        }
                    }

                    dispensiblecontaineritem = (DispensibleContainerItem) CraftItemStack.asNMSCopy(event.getItem()).getItem();
                }
                // CraftBukkit end

                if (dispensiblecontaineritem.emptyContents((EntityHuman) null, worldserver, blockposition, (MovingObjectPositionBlock) null)) {
                    dispensiblecontaineritem.checkExtraContent((EntityHuman) null, worldserver, itemstack, blockposition);
                    return this.consumeWithRemainder(sourceblock, itemstack, new ItemStack(Items.BUCKET));
                } else {
                    return this.defaultDispenseItemBehavior.dispense(sourceblock, itemstack);
                }
            }
        };

        BlockDispenser.registerBehavior(Items.LAVA_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.WATER_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.POWDER_SNOW_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.SALMON_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.COD_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.PUFFERFISH_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.AXOLOTL_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.TADPOLE_BUCKET, dispensebehavioritem1);
        BlockDispenser.registerBehavior(Items.BUCKET, new DispenseBehaviorItem() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                IBlockData iblockdata = worldserver.getBlockState(blockposition);
                Block block = iblockdata.getBlock();

                if (block instanceof IFluidSource ifluidsource) {
                    ItemStack itemstack1 = ifluidsource.pickupBlock((EntityHuman) null, DummyGeneratorAccess.INSTANCE, blockposition, iblockdata); // CraftBukkit

                    if (itemstack1.isEmpty()) {
                        return super.execute(sourceblock, itemstack);
                    } else {
                        worldserver.gameEvent((Entity) null, (Holder) GameEvent.FLUID_PICKUP, blockposition);
                        Item item = itemstack1.getItem();

                        // CraftBukkit start
                        org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, sourceblock.pos());
                        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                        if (!BlockDispenser.eventFired) {
                            worldserver.getCraftServer().getPluginManager().callEvent(event);
                        }

                        if (event.isCancelled()) {
                            return itemstack;
                        }

                        if (!event.getItem().equals(craftItem)) {
                            // Chain to handler for new item
                            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                            IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                            if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                                idispensebehavior.dispense(sourceblock, eventStack);
                                return itemstack;
                            }
                        }

                        itemstack1 = ifluidsource.pickupBlock((EntityHuman) null, worldserver, blockposition, iblockdata); // From above
                        // CraftBukkit end

                        return this.consumeWithRemainder(sourceblock, itemstack, new ItemStack(item));
                    }
                } else {
                    return super.execute(sourceblock, itemstack);
                }
            }
        });
        BlockDispenser.registerBehavior(Items.FLINT_AND_STEEL, new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                this.setSuccess(true);
                EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
                BlockPosition blockposition = sourceblock.pos().relative(enumdirection);
                IBlockData iblockdata = worldserver.getBlockState(blockposition);

                if (BlockFireAbstract.canBePlacedAt(worldserver, blockposition, enumdirection)) {
                    // CraftBukkit start - Ignition by dispensing flint and steel
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(worldserver, blockposition, sourceblock.pos()).isCancelled()) {
                        worldserver.setBlockAndUpdate(blockposition, BlockFireAbstract.getState(worldserver, blockposition));
                        worldserver.gameEvent((Entity) null, (Holder) GameEvent.BLOCK_PLACE, blockposition);
                    }
                    // CraftBukkit end
                } else if (!BlockCampfire.canLight(iblockdata) && !CandleBlock.canLight(iblockdata) && !CandleCakeBlock.canLight(iblockdata)) {
                    if (iblockdata.getBlock() instanceof BlockTNT && org.bukkit.craftbukkit.event.CraftEventFactory.callTNTPrimeEvent(worldserver, blockposition, org.bukkit.event.block.TNTPrimeEvent.PrimeCause.DISPENSER, null, sourceblock.pos())) { // CraftBukkit - TNTPrimeEvent
                        BlockTNT.explode(worldserver, blockposition);
                        worldserver.removeBlock(blockposition, false);
                    } else {
                        this.setSuccess(false);
                    }
                } else {
                    worldserver.setBlockAndUpdate(blockposition, (IBlockData) iblockdata.setValue(BlockProperties.LIT, true));
                    worldserver.gameEvent((Entity) null, (Holder) GameEvent.BLOCK_CHANGE, blockposition);
                }

                if (this.isSuccess()) {
                    itemstack.hurtAndBreak(1, worldserver, (EntityPlayer) null, (item) -> {
                    });
                }

                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Items.BONE_MEAL, new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                this.setSuccess(true);
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                // CraftBukkit start
                org.bukkit.block.Block block = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }

                worldserver.captureTreeGeneration = true;
                // CraftBukkit end

                if (!ItemBoneMeal.growCrop(itemstack, worldserver, blockposition) && !ItemBoneMeal.growWaterPlant(itemstack, worldserver, blockposition, (EnumDirection) null)) {
                    this.setSuccess(false);
                } else if (!worldserver.isClientSide) {
                    worldserver.levelEvent(1505, blockposition, 15);
                }
                // CraftBukkit start
                worldserver.captureTreeGeneration = false;
                if (worldserver.capturedBlockStates.size() > 0) {
                    TreeType treeType = BlockSapling.treeType;
                    BlockSapling.treeType = null;
                    Location location = CraftLocation.toBukkit(blockposition, worldserver.getWorld());
                    List<org.bukkit.block.BlockState> blocks = new java.util.ArrayList<>(worldserver.capturedBlockStates.values());
                    worldserver.capturedBlockStates.clear();
                    StructureGrowEvent structureEvent = null;
                    if (treeType != null) {
                        structureEvent = new StructureGrowEvent(location, treeType, false, null, blocks);
                        org.bukkit.Bukkit.getPluginManager().callEvent(structureEvent);
                    }

                    BlockFertilizeEvent fertilizeEvent = new BlockFertilizeEvent(location.getBlock(), null, blocks);
                    fertilizeEvent.setCancelled(structureEvent != null && structureEvent.isCancelled());
                    org.bukkit.Bukkit.getPluginManager().callEvent(fertilizeEvent);

                    if (!fertilizeEvent.isCancelled()) {
                        for (org.bukkit.block.BlockState blockstate : blocks) {
                            blockstate.update(true);
                        }
                    }
                }
                // CraftBukkit end

                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Blocks.TNT, new DispenseBehaviorItem() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                // CraftBukkit start
                // EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (EntityLiving) null);

                ItemStack itemstack1 = itemstack.split(1);
                org.bukkit.block.Block block = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D));
                if (!BlockDispenser.eventFired) {
                   worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.grow(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.grow(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(worldserver, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), (EntityLiving) null);
                // CraftBukkit end

                worldserver.addFreshEntity(entitytntprimed);
                worldserver.playSound((EntityHuman) null, entitytntprimed.getX(), entitytntprimed.getY(), entitytntprimed.getZ(), SoundEffects.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldserver.gameEvent((Entity) null, (Holder) GameEvent.ENTITY_PLACE, blockposition);
                // itemstack.shrink(1); // CraftBukkit - handled above
                return itemstack;
            }
        });
        DispenseBehaviorMaybe dispensebehaviormaybe1 = new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                this.setSuccess(ItemArmor.dispenseArmor(sourceblock, itemstack));
                return itemstack;
            }
        };

        BlockDispenser.registerBehavior(Items.CREEPER_HEAD, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.ZOMBIE_HEAD, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.DRAGON_HEAD, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.SKELETON_SKULL, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.PIGLIN_HEAD, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.PLAYER_HEAD, dispensebehaviormaybe1);
        BlockDispenser.registerBehavior(Items.WITHER_SKELETON_SKULL, new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();
                EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
                BlockPosition blockposition = sourceblock.pos().relative(enumdirection);

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                if (worldserver.isEmptyBlock(blockposition) && BlockWitherSkull.canSpawnMob(worldserver, blockposition, itemstack)) {
                    worldserver.setBlock(blockposition, (IBlockData) Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(BlockSkull.ROTATION, RotationSegment.convertToSegment(enumdirection)), 3);
                    worldserver.gameEvent((Entity) null, (Holder) GameEvent.BLOCK_PLACE, blockposition);
                    TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                    if (tileentity instanceof TileEntitySkull) {
                        BlockWitherSkull.checkSpawn(worldserver, blockposition, (TileEntitySkull) tileentity);
                    }

                    itemstack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ItemArmor.dispenseArmor(sourceblock, itemstack));
                }

                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Blocks.CARVED_PUMPKIN, new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                BlockPumpkinCarved blockpumpkincarved = (BlockPumpkinCarved) Blocks.CARVED_PUMPKIN;

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                if (worldserver.isEmptyBlock(blockposition) && blockpumpkincarved.canSpawnGolem(worldserver, blockposition)) {
                    if (!worldserver.isClientSide) {
                        worldserver.setBlock(blockposition, blockpumpkincarved.defaultBlockState(), 3);
                        worldserver.gameEvent((Entity) null, (Holder) GameEvent.BLOCK_PLACE, blockposition);
                    }

                    itemstack.shrink(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ItemArmor.dispenseArmor(sourceblock, itemstack));
                }

                return itemstack;
            }
        });
        BlockDispenser.registerBehavior(Blocks.SHULKER_BOX.asItem(), new DispenseBehaviorShulkerBox());
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            BlockDispenser.registerBehavior(BlockShulkerBox.getBlockByColor(enumcolor).asItem(), new DispenseBehaviorShulkerBox());
        }

        BlockDispenser.registerBehavior(Items.GLASS_BOTTLE.asItem(), new DispenseBehaviorMaybe() {
            private ItemStack takeLiquid(SourceBlock sourceblock, ItemStack itemstack, ItemStack itemstack1) {
                sourceblock.level().gameEvent((Entity) null, (Holder) GameEvent.FLUID_PICKUP, sourceblock.pos());
                return this.consumeWithRemainder(sourceblock, itemstack, itemstack1);
            }

            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                this.setSuccess(false);
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                IBlockData iblockdata = worldserver.getBlockState(blockposition);

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, sourceblock.pos());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    worldserver.getCraftServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != this) {
                        idispensebehavior.dispense(sourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                if (iblockdata.is(TagsBlock.BEEHIVES, (blockbase_blockdata) -> {
                    return blockbase_blockdata.hasProperty(BlockBeehive.HONEY_LEVEL) && blockbase_blockdata.getBlock() instanceof BlockBeehive;
                }) && (Integer) iblockdata.getValue(BlockBeehive.HONEY_LEVEL) >= 5) {
                    ((BlockBeehive) iblockdata.getBlock()).releaseBeesAndResetHoneyLevel(worldserver, iblockdata, blockposition, (EntityHuman) null, TileEntityBeehive.ReleaseStatus.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.takeLiquid(sourceblock, itemstack, new ItemStack(Items.HONEY_BOTTLE));
                } else if (worldserver.getFluidState(blockposition).is(TagsFluid.WATER)) {
                    this.setSuccess(true);
                    return this.takeLiquid(sourceblock, itemstack, PotionContents.createItemStack(Items.POTION, Potions.WATER));
                } else {
                    return super.execute(sourceblock, itemstack);
                }
            }
        });
        BlockDispenser.registerBehavior(Items.GLOWSTONE, new DispenseBehaviorMaybe() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING);
                BlockPosition blockposition = sourceblock.pos().relative(enumdirection);
                WorldServer worldserver = sourceblock.level();
                IBlockData iblockdata = worldserver.getBlockState(blockposition);

                this.setSuccess(true);
                if (iblockdata.is(Blocks.RESPAWN_ANCHOR)) {
                    if ((Integer) iblockdata.getValue(BlockRespawnAnchor.CHARGE) != 4) {
                        BlockRespawnAnchor.charge((Entity) null, worldserver, blockposition, iblockdata);
                        itemstack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }

                    return itemstack;
                } else {
                    return super.execute(sourceblock, itemstack);
                }
            }
        });
        BlockDispenser.registerBehavior(Items.SHEARS.asItem(), new DispenseBehaviorShears());
        BlockDispenser.registerBehavior(Items.BRUSH.asItem(), new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                WorldServer worldserver = sourceblock.level();
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                List<Armadillo> list = worldserver.getEntitiesOfClass(Armadillo.class, new AxisAlignedBB(blockposition), IEntitySelector.NO_SPECTATORS);

                if (list.isEmpty()) {
                    this.setSuccess(false);
                    return itemstack;
                } else {
                    // CraftBukkit start
                    ItemStack itemstack1 = itemstack;
                    WorldServer world = sourceblock.level();
                    org.bukkit.block.Block block = CraftBlock.at(world, sourceblock.pos());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                    BlockDispenseEvent event = new BlockDispenseArmorEvent(block, craftItem.clone(), (org.bukkit.craftbukkit.entity.CraftLivingEntity) list.get(0).getBukkitEntity());
                    if (!BlockDispenser.eventFired) {
                        world.getCraftServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.DISPENSER_REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NOOP && idispensebehavior != ItemArmor.DISPENSE_ITEM_BEHAVIOR) {
                            idispensebehavior.dispense(sourceblock, eventStack);
                            return itemstack;
                        }
                    }
                    // CraftBukkit end
                    Iterator iterator1 = list.iterator();

                    Armadillo armadillo;

                    do {
                        if (!iterator1.hasNext()) {
                            this.setSuccess(false);
                            return itemstack;
                        }

                        armadillo = (Armadillo) iterator1.next();
                    } while (!armadillo.brushOffScute());

                    itemstack.hurtAndBreak(16, worldserver, (EntityPlayer) null, (item) -> {
                    });
                    return itemstack;
                }
            }
        });
        BlockDispenser.registerBehavior(Items.HONEYCOMB, new DispenseBehaviorMaybe() {
            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                BlockPosition blockposition = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));
                WorldServer worldserver = sourceblock.level();
                IBlockData iblockdata = worldserver.getBlockState(blockposition);
                Optional<IBlockData> optional = HoneycombItem.getWaxed(iblockdata);

                if (optional.isPresent()) {
                    worldserver.setBlockAndUpdate(blockposition, (IBlockData) optional.get());
                    worldserver.levelEvent(3003, blockposition, 0);
                    itemstack.shrink(1);
                    this.setSuccess(true);
                    return itemstack;
                } else {
                    return super.execute(sourceblock, itemstack);
                }
            }
        });
        BlockDispenser.registerBehavior(Items.POTION, new DispenseBehaviorItem() {
            private final DispenseBehaviorItem defaultDispenseItemBehavior = new DispenseBehaviorItem();

            @Override
            public ItemStack execute(SourceBlock sourceblock, ItemStack itemstack) {
                PotionContents potioncontents = (PotionContents) itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

                if (!potioncontents.is(Potions.WATER)) {
                    return this.defaultDispenseItemBehavior.dispense(sourceblock, itemstack);
                } else {
                    WorldServer worldserver = sourceblock.level();
                    BlockPosition blockposition = sourceblock.pos();
                    BlockPosition blockposition1 = sourceblock.pos().relative((EnumDirection) sourceblock.state().getValue(BlockDispenser.FACING));

                    if (!worldserver.getBlockState(blockposition1).is(TagsBlock.CONVERTABLE_TO_MUD)) {
                        return this.defaultDispenseItemBehavior.dispense(sourceblock, itemstack);
                    } else {
                        if (!worldserver.isClientSide) {
                            for (int k = 0; k < 5; ++k) {
                                worldserver.sendParticles(Particles.SPLASH, (double) blockposition.getX() + worldserver.random.nextDouble(), (double) (blockposition.getY() + 1), (double) blockposition.getZ() + worldserver.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                            }
                        }

                        worldserver.playSound((EntityHuman) null, blockposition, SoundEffects.BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        worldserver.gameEvent((Entity) null, (Holder) GameEvent.FLUID_PLACE, blockposition);
                        worldserver.setBlockAndUpdate(blockposition1, Blocks.MUD.defaultBlockState());
                        return this.consumeWithRemainder(sourceblock, itemstack, new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
            }
        });
    }
}
