package me.wobbychip.smptweaks.library.customblocks.events;

import me.wobbychip.smptweaks.library.customblocks.CustomBlocks;
import me.wobbychip.smptweaks.library.customblocks.blocks.CustomMarker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

public class WorldEvents implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkLoadEvent(ChunkLoadEvent event) {
		CustomMarker.collectUnmarkedBlocks(event.getChunk());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntitiesLoadEvent(EntitiesLoadEvent event) {
		CustomMarker.collectUnmarkedBlocks(event.getEntities());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (CustomBlocks.getSize() == 0) { return; }
		event.getPlayer().addResourcePack(CustomBlocks.RESOURCE_PACK_UUID, CustomBlocks.RESOURCE_PACK_URL, CustomBlocks.RESOURCE_PACK_HASH, CustomBlocks.RESOURCE_PACK_PROMPT, true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTeleportEvent(EntityTeleportEvent event) {
		if (!CustomMarker.isMarkerEntity(event.getEntity())) { return; }
		event.setTo(event.getFrom());
		event.setCancelled(false);
	}
}