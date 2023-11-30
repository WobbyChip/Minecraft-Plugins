package me.wobbychip.smptweaks.custom.holograms;

import me.wobbychip.smptweaks.utils.PersistentUtils;
import me.wobbychip.smptweaks.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.*;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.UUID;

public class Hologram {
	public static String TAG_IS_HOLOGRAM = "tag_is_hologram";
	public static String TAG_POSITION = "tag_hologram_position";
	public static String TAG_DISPLAY = "tag_hologram_display";
	public TextDisplay display;
	public Interaction interaction;
	public int y;

	private Hologram(TextDisplay display, Interaction interaction, int y) {
		this.display = display;
		this.interaction = interaction;
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public UUID getUniqueId() {
		return interaction.getUniqueId();
	}

	public Location getLocation() {
		int x = interaction.getLocation().getBlockX();
		int z = interaction.getLocation().getBlockZ();
		return new Location(interaction.getLocation().getWorld(), x, y, z);
	}

	public boolean getSeeThrough() {
		return display.isSeeThrough();
	}

	public void setSeeThrough(boolean seeThrough) {
		display.setSeeThrough(seeThrough);
	}

	public Billboard getBillboard() {
		return display.getBillboard();
	}

	public void setBillboard(Billboard billboard) {
		display.setBillboard(billboard);
	}

	public String getText() {
		return display.getText();
	}

	public void setText(String text) {
		float yaw = display.getLocation().getYaw();
		display.setText(text);
		teleport(getLocation());

		Location location = display.getLocation();
		location.setYaw(yaw);
		display.teleport(location);
	}

	public TextDisplay getDisplay() {
		return display;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public ItemStack getBook() {
		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		PersistentUtils.setPersistentDataBoolean(book, TAG_IS_HOLOGRAM, true);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		String location = Utils.locationToString(this.getLocation());
		bookMeta.setDisplayName("§bHologram (" + location + ")");
		bookMeta.setAuthor(getUniqueId().toString());
		bookMeta.setPages(this.getText());
		bookMeta.addEnchant(Enchantment.DURABILITY, 1, true);
		bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		book.setItemMeta(bookMeta);
		return book;
	}

	public static Hologram get(UUID uuid) {
		return get((Interaction) Utils.getEntityByUniqueId(uuid));
	}

	public static Hologram get(Interaction interaction) {
		if (interaction == null) { return null; }
		if (!PersistentUtils.hasPersistentDataBoolean(interaction, TAG_IS_HOLOGRAM)) { return null; }
		if (!PersistentUtils.hasPersistentDataInteger(interaction, TAG_POSITION)) { return null; }
		if (!PersistentUtils.hasPersistentDataString(interaction, TAG_DISPLAY)) { return null; }

		UUID uuid = UUID.fromString(PersistentUtils.getPersistentDataString(interaction, TAG_DISPLAY));
		Entity entity = Utils.getEntityByUniqueId(uuid);
		if ((entity == null) || (entity.getType() != EntityType.TEXT_DISPLAY)) { return null; }

		int y = PersistentUtils.getPersistentDataInteger(interaction, TAG_POSITION);
		return new Hologram((TextDisplay) entity, interaction, y);
	}

	public static Hologram create(Location location, String text) {
		location = location.getBlock().getLocation().add(0.5, 0, 0.5);
		TextDisplay display = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
		display.setInvulnerable(true);
		display.setSeeThrough(true);
		display.setLineWidth(Integer.MAX_VALUE);
		display.setBillboard(Billboard.VERTICAL);
		display.setAlignment(TextAlignment.CENTER);
		display.setText(text);

		Interaction interaction = (Interaction) location.getWorld().spawnEntity(location, EntityType.INTERACTION);
		PersistentUtils.setPersistentDataBoolean(interaction, TAG_IS_HOLOGRAM, true);
		PersistentUtils.setPersistentDataInteger(interaction, TAG_POSITION, location.getBlockY());
		PersistentUtils.setPersistentDataString(interaction, TAG_DISPLAY, display.getUniqueId().toString());

		Hologram hologram = new Hologram(display, interaction, location.getBlockY());
		hologram.teleport(hologram.getLocation());
		return hologram;
	}

	public void remove() {
		display.remove();
		interaction.remove();
	}

	public void teleport(Location location) {
		//By default 4 lines can fit in 1 block
		location = location.getBlock().getLocation();
		float height = (getText().split("\r\n|\r|\n").length * 0.25f);
		interaction.setInteractionHeight((height > 1f) ? height : 1f);
		display.setViewRange(getText().isBlank() ? 0f : 1f);

		this.y = location.getBlockY();
		PersistentUtils.setPersistentDataInteger(interaction, TAG_POSITION, this.y);

		interaction.teleport(location.clone().add(0.5, (0.5-(interaction.getInteractionHeight()/2)), 0.5));
		display.teleport(location.clone().add(0.5, (0.5-(height/2)), 0.5));
	}

	public void updateRotation(float angle) {
		float yaw = display.getLocation().getYaw();
		yaw = Math.round(yaw / angle) * angle;
		yaw = (yaw + angle) % 360;

		Location location = display.getLocation();
		location.setYaw(yaw);
		display.teleport(location);
	}

	public void updateRotation(Player player) {
		float yaw = player.getLocation().getYaw();
		yaw = Math.round(yaw / 90) * 90;
		yaw = (yaw % 360 + 360) % 360;
		yaw = (yaw + 180) % 360;

		Location location = display.getLocation();
		location.setYaw(yaw);
		display.teleport(location);
	}
}
