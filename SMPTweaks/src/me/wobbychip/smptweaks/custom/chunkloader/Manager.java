package me.wobbychip.smptweaks.custom.chunkloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.wobbychip.smptweaks.Config;
import me.wobbychip.smptweaks.Main;
import me.wobbychip.smptweaks.utils.Utils;

public class Manager {
	public Config config;
	public int taskId;
	public Map<String, Loader> loaders = new HashMap<>();

	public Manager(Config config) {
		this.config = config;

		for (String loader : config.getConfig().getStringList("chunkloaders")) {
			Location location = Utils.stringToLocation(loader);
			String message = String.format("Loading at %s: X: %s Y: %s Z: %s", location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
			ChunkLoader.tweak.printMessage(message, true);
			addLoader(location.getBlock(), false);
		}

		taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				updateAll();
			}
		}, 5L, 5L);
	}

	public Border getBorder(Player player) {
		for (Loader loader : loaders.values()) {
			if (loader.getBorder().containsPlayer(player)) { return loader.getBorder(); }
		}

		return null;
	}

	public void addLoader(Block block, boolean doSave) {
		if (block.getType() != Material.LODESTONE) { return; }
		String location = Utils.locationToString(block.getLocation());
		if (loaders.containsKey(location)) { return; }
		if (!block.getChunk().isLoaded()) { block.getChunk().load(); }
		loaders.put(location, new Loader(block));
		if (doSave) { saveAll(); }
	}

	public void removeLoader(Block block) {
		String location = Utils.locationToString(block.getLocation());
		if (!loaders.containsKey(location)) { return; }
		loaders.remove(location).remove(false);
		saveAll();
	}

	public Loader getLoader(Block block) {
		String location = Utils.locationToString(block.getLocation());
		if (loaders.containsKey(location)) { return loaders.get(location); }
		return null;
	}

	public void updateLoader(Block block) {
		if (block.getType() != Material.LODESTONE) { return; }
		String location = Utils.locationToString(block.getLocation());
		if (loaders.containsKey(location)) { loaders.get(location).update(false); }
	}

	public void updateAll() {
		List<Block> remove = new ArrayList<>();
		
		for (Loader loader : loaders.values()) {
			if (!loader.getLocation().getChunk().isEntitiesLoaded()) { continue; }
			if (!loader.isLoader()) { remove.add(loader.getLocation().getBlock()); }
		}

		for (Block block : remove) {
			removeLoader(block);
		}
	}

	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(taskId);

		for (Loader loader : loaders.values()) {
			loader.remove(true);
		}
	}

	public void saveAll() {
		config.getConfig().set("chunkloaders", new ArrayList<>(loaders.keySet()));
		config.Save();
	}
}
