package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.World;

public class CriterionTriggerChangedDimension extends CriterionTriggerAbstract<CriterionTriggerChangedDimension.a> {

    public CriterionTriggerChangedDimension() {}

    @Override
    public CriterionTriggerChangedDimension.a createInstance(JsonObject jsonobject, Optional<ContextAwarePredicate> optional, LootDeserializationContext lootdeserializationcontext) {
        ResourceKey<World> resourcekey = jsonobject.has("from") ? ResourceKey.create(Registries.DIMENSION, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "from"))) : null;
        ResourceKey<World> resourcekey1 = jsonobject.has("to") ? ResourceKey.create(Registries.DIMENSION, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(optional, resourcekey, resourcekey1);
    }

    public void trigger(EntityPlayer entityplayer, ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
        this.trigger(entityplayer, (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.matches(resourcekey, resourcekey1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final ResourceKey<World> from;
        @Nullable
        private final ResourceKey<World> to;

        public a(Optional<ContextAwarePredicate> optional, @Nullable ResourceKey<World> resourcekey, @Nullable ResourceKey<World> resourcekey1) {
            super(optional);
            this.from = resourcekey;
            this.to = resourcekey1;
        }

        public static Criterion<CriterionTriggerChangedDimension.a> changedDimension() {
            return CriterionTriggers.CHANGED_DIMENSION.createCriterion(new CriterionTriggerChangedDimension.a(Optional.empty(), (ResourceKey) null, (ResourceKey) null));
        }

        public static Criterion<CriterionTriggerChangedDimension.a> changedDimension(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return CriterionTriggers.CHANGED_DIMENSION.createCriterion(new CriterionTriggerChangedDimension.a(Optional.empty(), resourcekey, resourcekey1));
        }

        public static Criterion<CriterionTriggerChangedDimension.a> changedDimensionTo(ResourceKey<World> resourcekey) {
            return CriterionTriggers.CHANGED_DIMENSION.createCriterion(new CriterionTriggerChangedDimension.a(Optional.empty(), (ResourceKey) null, resourcekey));
        }

        public static Criterion<CriterionTriggerChangedDimension.a> changedDimensionFrom(ResourceKey<World> resourcekey) {
            return CriterionTriggers.CHANGED_DIMENSION.createCriterion(new CriterionTriggerChangedDimension.a(Optional.empty(), resourcekey, (ResourceKey) null));
        }

        public boolean matches(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return this.from != null && this.from != resourcekey ? false : this.to == null || this.to == resourcekey1;
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonobject = super.serializeToJson();

            if (this.from != null) {
                jsonobject.addProperty("from", this.from.location().toString());
            }

            if (this.to != null) {
                jsonobject.addProperty("to", this.to.location().toString());
            }

            return jsonobject;
        }
    }
}