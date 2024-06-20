package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class AttributeModifierIdFix extends DataFix {

    private static final Map<UUID, String> ID_MAP = ImmutableMap.builder().put(UUID.fromString("736565d2-e1a7-403d-a3f8-1aeb3e302542"), "minecraft:creative_mode_block_range").put(UUID.fromString("98491ef6-97b1-4584-ae82-71a8cc85cf73"), "minecraft:creative_mode_entity_range").put(UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635"), "minecraft:effect.speed").put(UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890"), "minecraft:effect.slowness").put(UUID.fromString("AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3"), "minecraft:effect.haste").put(UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386"), "minecraft:effect.mining_fatigue").put(UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"), "minecraft:effect.strength").put(UUID.fromString("C0105BF3-AEF8-46B0-9EBC-92943757CCBE"), "minecraft:effect.jump_boost").put(UUID.fromString("22653B89-116E-49DC-9B6B-9971489B5BE5"), "minecraft:effect.weakness").put(UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC"), "minecraft:effect.health_boost").put(UUID.fromString("EAE29CF0-701E-4ED6-883A-96F798F3DAB5"), "minecraft:effect.absorption").put(UUID.fromString("03C3C89D-7037-4B42-869F-B146BCB64D2E"), "minecraft:effect.luck").put(UUID.fromString("CC5AF142-2BD2-4215-B636-2605AED11727"), "minecraft:effect.unluck").put(UUID.fromString("6555be74-63b3-41f1-a245-77833b3c2562"), "minecraft:evil").put(UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce"), "minecraft:powder_snow").put(UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D"), "minecraft:sprinting").put(UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0"), "minecraft:attacking").put(UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667"), "minecraft:baby").put(UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F"), "minecraft:covered").put(UUID.fromString("9e362924-01de-4ddd-a2b2-d0f7a405a174"), "minecraft:suffocating").put(UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E"), "minecraft:drinking").put(UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836"), "minecraft:baby").put(UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718"), "minecraft:attacking").put(UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), "minecraft:armor.boots").put(UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), "minecraft:armor.leggings").put(UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), "minecraft:armor.chestplate").put(UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"), "minecraft:armor.helmet").put(UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"), "minecraft:armor.body").put(UUID.fromString("b572ecd2-ac0c-4071-abde-9594af072a37"), "minecraft:enchantment.fire_protection").put(UUID.fromString("40a9968f-5c66-4e2f-b7f4-2ec2f4b3e450"), "minecraft:enchantment.blast_protection").put(UUID.fromString("07a65791-f64d-4e79-86c7-f83932f007ec"), "minecraft:enchantment.respiration").put(UUID.fromString("60b1b7db-fffd-4ad0-817c-d6c6a93d8a45"), "minecraft:enchantment.aqua_affinity").put(UUID.fromString("11dc269a-4476-46c0-aff3-9e17d7eb6801"), "minecraft:enchantment.depth_strider").put(UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038"), "minecraft:enchantment.soul_speed").put(UUID.fromString("b9716dbd-50df-4080-850e-70347d24e687"), "minecraft:enchantment.soul_speed").put(UUID.fromString("92437d00-c3a7-4f2e-8f6c-1f21585d5dd0"), "minecraft:enchantment.swift_sneak").put(UUID.fromString("5d3d087b-debe-4037-b53e-d84f3ff51f17"), "minecraft:enchantment.sweeping_edge").put(UUID.fromString("3ceb37c0-db62-46b5-bd02-785457b01d96"), "minecraft:enchantment.efficiency").put(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "minecraft:base_attack_damage").put(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "minecraft:base_attack_speed").build();
    private static final Map<String, String> NAME_MAP = Map.of("Random spawn bonus", "minecraft:random_spawn_bonus", "Random zombie-spawn bonus", "minecraft:zombie_random_spawn_bonus", "Leader zombie bonus", "minecraft:leader_zombie_bonus", "Zombie reinforcement callee charge", "minecraft:reinforcement_callee_charge", "Zombie reinforcement caller charge", "minecraft:reinforcement_caller_charge");

    public AttributeModifierIdFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("components");

        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("AttributeIdFix (ItemStack)", type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), AttributeModifierIdFix::fixItemStackComponents);
            });
        }), new TypeRewriteRule[]{this.fixTypeEverywhereTyped("AttributeIdFix (Entity)", this.getInputSchema().getType(DataConverterTypes.ENTITY), AttributeModifierIdFix::fixEntity), this.fixTypeEverywhereTyped("AttributeIdFix (Player)", this.getInputSchema().getType(DataConverterTypes.PLAYER), AttributeModifierIdFix::fixEntity)});
    }

    private static Stream<Dynamic<?>> fixModifiersTypeWrapper(Stream<?> stream) {
        return fixModifiers(stream);
    }

    private static Stream<Dynamic<?>> fixModifiers(Stream<Dynamic<?>> stream) {
        Map<String, Dynamic<?>> map = new Object2ObjectArrayMap();

        stream.forEach((dynamic) -> {
            UUID uuid = uuidFromIntArray(dynamic.get("uuid").asIntStream().toArray());
            String s = dynamic.get("name").asString("");
            String s1 = uuid != null ? (String) AttributeModifierIdFix.ID_MAP.get(uuid) : null;
            String s2 = (String) AttributeModifierIdFix.NAME_MAP.get(s);

            if (s1 != null) {
                dynamic = dynamic.set("id", dynamic.createString(s1));
                map.put(s1, dynamic.remove("uuid").remove("name"));
            } else if (s2 != null) {
                Dynamic<?> dynamic1 = (Dynamic) map.get(s2);

                if (dynamic1 == null) {
                    dynamic = dynamic.set("id", dynamic.createString(s2));
                    map.put(s2, dynamic.remove("uuid").remove("name"));
                } else {
                    double d0 = dynamic1.get("amount").asDouble(0.0D);
                    double d1 = dynamic.get("amount").asDouble(0.0D);

                    map.put(s2, dynamic1.set("amount", dynamic.createDouble(d0 + d1)));
                }
            } else {
                String s3 = uuid != null ? uuid.toString().toLowerCase(Locale.ROOT) : "unknown";
                String s4 = "minecraft:" + s3;

                dynamic = dynamic.set("id", dynamic.createString(s4));
                map.put(s4, dynamic.remove("uuid").remove("name"));
            }

        });
        return map.values().stream();
    }

    private static Dynamic<?> convertModifierForEntity(Dynamic<?> dynamic) {
        return dynamic.renameField("UUID", "uuid").renameField("Name", "name").renameField("Amount", "amount").renameAndFixField("Operation", "operation", (dynamic1) -> {
            String s;

            switch (dynamic1.asInt(0)) {
                case 0:
                    s = "add_value";
                    break;
                case 1:
                    s = "add_multiplied_base";
                    break;
                case 2:
                    s = "add_multiplied_total";
                    break;
                default:
                    s = "invalid";
            }

            return dynamic1.createString(s);
        });
    }

    private static Dynamic<?> fixItemStackComponents(Dynamic<?> dynamic) {
        return dynamic.update("minecraft:attribute_modifiers", (dynamic1) -> {
            return dynamic1.update("modifiers", (dynamic2) -> {
                Optional optional = dynamic2.asStreamOpt().result().map(AttributeModifierIdFix::fixModifiersTypeWrapper);

                Objects.requireNonNull(dynamic2);
                return (Dynamic) DataFixUtils.orElse(optional.map(dynamic2::createList), dynamic2);
            });
        });
    }

    private static Dynamic<?> fixAttribute(Dynamic<?> dynamic) {
        return dynamic.renameField("Name", "id").renameField("Base", "base").renameAndFixField("Modifiers", "modifiers", (dynamic1) -> {
            Optional optional = dynamic1.asStreamOpt().result().map((stream) -> {
                return stream.map(AttributeModifierIdFix::convertModifierForEntity);
            }).map(AttributeModifierIdFix::fixModifiersTypeWrapper);

            Objects.requireNonNull(dynamic);
            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic::createList), dynamic1);
        });
    }

    private static Typed<?> fixEntity(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.renameAndFixField("Attributes", "attributes", (dynamic1) -> {
                Optional optional = dynamic1.asStreamOpt().result().map((stream) -> {
                    return stream.map(AttributeModifierIdFix::fixAttribute);
                });

                Objects.requireNonNull(dynamic1);
                return (Dynamic) DataFixUtils.orElse(optional.map(dynamic1::createList), dynamic1);
            });
        });
    }

    @Nullable
    public static UUID uuidFromIntArray(int[] aint) {
        return aint.length != 4 ? null : new UUID((long) aint[0] << 32 | (long) aint[1] & 4294967295L, (long) aint[2] << 32 | (long) aint[3] & 4294967295L);
    }
}
