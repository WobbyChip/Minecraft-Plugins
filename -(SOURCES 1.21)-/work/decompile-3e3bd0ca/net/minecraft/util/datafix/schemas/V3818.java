package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class V3818 extends DataConverterSchemaNamed {

    public V3818(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

        schema.register(map, "minecraft:beehive", () -> {
            return DSL.optionalFields("bees", DSL.list(DSL.optionalFields("entity_data", DataConverterTypes.ENTITY_TREE.in(schema))));
        });
        return map;
    }
}
