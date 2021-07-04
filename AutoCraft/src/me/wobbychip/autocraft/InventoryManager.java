package me.wobbychip.autocraft;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryManager implements InventoryHolder {
	public boolean isDestroyed = false;
	private Inventory inv;
	private InventoryView playerInv;
	private Location location;
	private File file;

	public InventoryManager(String locationString) {
		location = Utilities.StringToLocation(locationString);
		inv = Bukkit.createInventory(null, InventoryType.WORKBENCH);
		file = new File(Main.plugin.getDataFolder(), "craftingtables/" + locationString + ".bin");

		if (!file.exists()) {
        	file.getParentFile().mkdirs();
        	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
        	Load();
        }

		Main.inventoryManagers.put(locationString, this);
	}

	public void Save() {
		if (playerInv != null) {
			inv.setStorageContents(playerInv.getTopInventory().getStorageContents());
			byte[] bytes = Utilities.inventoryToByteArray(inv);
			Utilities.writeFile(file, bytes);
		}
	}

	public void Load() {
		byte[] bytes = Utilities.readFile(file);
		if (bytes.length <= 0) { return; }

		try {
			Utilities.inventoryFromByteArray(inv, bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getViewers() {
		if (playerInv != null) {
			return playerInv.getTopInventory().getViewers().size();
		} else {
			return 0;
		}
	}

	//Fuck the bukkit for garbage inventory, just why tf createInventory type workbench not handling most of the stuff
	public void openInventory(Player player) {
		if (getViewers() == 0) {
			playerInv = player.openWorkbench(location, true);
			//playerInv.getTopInventory().setStorageContents(inv.getStorageContents());
		} else {
			player.openInventory(playerInv.getTopInventory()); //Not working
		}
	}

	public void closeInventory() {
		List<HumanEntity> humanEntityList  = inv.getViewers();
		//Fix this not all inventories closing

		for (int i = 0; i < humanEntityList.size(); i++) {
			HumanEntity humanEntity = humanEntityList.get(i);
			if (humanEntity != null) { humanEntity.closeInventory(); }
		}
	}

	public void dropInventory() {
		for (ItemStack itemStack : inv.getContents()) {
			if (itemStack != null) {
				location.getWorld().dropItem(location, itemStack);
			}
		}
	}

	public void destroyInventory() {
		this.isDestroyed = true;

		Utilities.DebugInfo("1");
		closeInventory();
		Utilities.DebugInfo("2");
		dropInventory();
		Utilities.DebugInfo("3");
		file.delete();
		Utilities.DebugInfo("4");

		String locationString = Utilities.LocationToString(location);
		for (String key : Main.inventoryManagers.keySet()) {
		    if (key.contains(locationString)) {
		    	Main.inventoryManagers.remove(key);
		    }
		}

		Utilities.DebugInfo("5");
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
}
