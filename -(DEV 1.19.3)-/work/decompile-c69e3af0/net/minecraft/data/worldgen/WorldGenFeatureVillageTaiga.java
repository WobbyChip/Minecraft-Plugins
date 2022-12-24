package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolStructure;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;

public class WorldGenFeatureVillageTaiga {

    public static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> START = WorldGenFeaturePieces.createKey("village/taiga/town_centers");
    private static final ResourceKey<WorldGenFeatureDefinedStructurePoolTemplate> TERMINATORS_KEY = WorldGenFeaturePieces.createKey("village/taiga/terminators");

    public WorldGenFeatureVillageTaiga() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureDefinedStructurePoolTemplate> bootstapcontext) {
        HolderGetter<PlacedFeature> holdergetter = bootstapcontext.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> holder = holdergetter.getOrThrow(VillagePlacements.SPRUCE_VILLAGE);
        Holder<PlacedFeature> holder1 = holdergetter.getOrThrow(VillagePlacements.PINE_VILLAGE);
        Holder<PlacedFeature> holder2 = holdergetter.getOrThrow(VillagePlacements.PILE_PUMPKIN_VILLAGE);
        Holder<PlacedFeature> holder3 = holdergetter.getOrThrow(VillagePlacements.PATCH_TAIGA_GRASS_VILLAGE);
        Holder<PlacedFeature> holder4 = holdergetter.getOrThrow(VillagePlacements.PATCH_BERRY_BUSH_VILLAGE);
        HolderGetter<ProcessorList> holdergetter1 = bootstapcontext.lookup(Registries.PROCESSOR_LIST);
        Holder<ProcessorList> holder5 = holdergetter1.getOrThrow(ProcessorLists.MOSSIFY_10_PERCENT);
        Holder<ProcessorList> holder6 = holdergetter1.getOrThrow(ProcessorLists.ZOMBIE_TAIGA);
        Holder<ProcessorList> holder7 = holdergetter1.getOrThrow(ProcessorLists.STREET_SNOWY_OR_TAIGA);
        Holder<ProcessorList> holder8 = holdergetter1.getOrThrow(ProcessorLists.FARM_TAIGA);
        HolderGetter<WorldGenFeatureDefinedStructurePoolTemplate> holdergetter2 = bootstapcontext.lookup(Registries.TEMPLATE_POOL);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder9 = holdergetter2.getOrThrow(WorldGenFeaturePieces.EMPTY);
        Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder10 = holdergetter2.getOrThrow(WorldGenFeatureVillageTaiga.TERMINATORS_KEY);

        bootstapcontext.register(WorldGenFeatureVillageTaiga.START, new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/town_centers/taiga_meeting_point_1", holder5), 49), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/town_centers/taiga_meeting_point_2", holder5), 49), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/town_centers/taiga_meeting_point_1", holder6), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/town_centers/taiga_meeting_point_2", holder6), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/streets", new WorldGenFeatureDefinedStructurePoolTemplate(holder10, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/corner_01", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/corner_02", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/corner_03", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_01", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_02", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_03", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_04", holder7), 7), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_05", holder7), 7), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/straight_06", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_01", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_02", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_03", holder7), 2), new Pair[]{Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_04", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_05", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/crossroad_06", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/streets/turn_01", holder7), 3)}), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/zombie/streets", new WorldGenFeatureDefinedStructurePoolTemplate(holder10, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/corner_01", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/corner_02", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/corner_03", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_01", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_02", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_03", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_04", holder7), 7), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_05", holder7), 7), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/straight_06", holder7), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_01", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_02", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_03", holder7), 2), new Pair[]{Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_04", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_05", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/crossroad_06", holder7), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/streets/turn_01", holder7), 3)}), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/houses", new WorldGenFeatureDefinedStructurePoolTemplate(holder10, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_house_1", holder5), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_house_2", holder5), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_house_3", holder5), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_house_4", holder5), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_house_5", holder5), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_medium_house_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_medium_house_2", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_medium_house_3", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_medium_house_4", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_butcher_shop_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_tool_smith_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_fletcher_house_1", holder5), 2), new Pair[]{Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_shepherds_house_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_armorer_house_1", holder5), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_armorer_2", holder5), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_fisher_cottage_1", holder5), 3), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_tannery_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_cartographer_house_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_library_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_masons_house_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_weaponsmith_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_weaponsmith_2", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_temple_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_large_farm_1", holder8), 6), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_large_farm_2", holder8), 6), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_farm_1", holder5), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_animal_pen_1", holder5), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 6)}), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/zombie/houses", new WorldGenFeatureDefinedStructurePoolTemplate(holder10, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_small_house_1", holder6), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_small_house_2", holder6), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_small_house_3", holder6), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_small_house_4", holder6), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_small_house_5", holder6), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_medium_house_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_medium_house_2", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_medium_house_3", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_medium_house_4", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_butcher_shop_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_tool_smith_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_fletcher_house_1", holder6), 2), new Pair[]{Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_shepherds_house_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_armorer_house_1", holder6), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_fisher_cottage_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_tannery_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_cartographer_house_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_library_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_masons_house_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_weaponsmith_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_weaponsmith_2", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_temple_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_large_farm_1", holder6), 6), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/houses/taiga_large_farm_2", holder6), 6), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_small_farm_1", holder6), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/houses/taiga_animal_pen_1", holder6), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 6)}), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        bootstapcontext.register(WorldGenFeatureVillageTaiga.TERMINATORS_KEY, new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/plains/terminators/terminator_01", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/plains/terminators/terminator_02", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/plains/terminators/terminator_03", holder7), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/plains/terminators/terminator_04", holder7), 1)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.TERRAIN_MATCHING));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/decor", new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_lamp_post_1"), 10), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_1"), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_3"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_4"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_5"), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_6"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder1), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder2), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder3), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder4), 1), new Pair[]{Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 4)}), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/zombie/decor", new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_1"), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_2"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_3"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/taiga_decoration_4"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder1), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder2), 2), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder3), 4), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.feature(holder4), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.empty(), 4)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/villagers", new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/villagers/nitwit"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/villagers/baby"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/villagers/unemployed"), 10)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
        WorldGenFeaturePieces.register(bootstapcontext, "village/taiga/zombie/villagers", new WorldGenFeatureDefinedStructurePoolTemplate(holder9, ImmutableList.of(Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/villagers/nitwit"), 1), Pair.of(WorldGenFeatureDefinedStructurePoolStructure.legacy("village/taiga/zombie/villagers/unemployed"), 10)), WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID));
    }
}
