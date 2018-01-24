package eSChest.plugin;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ESChest extends JavaPlugin implements Listener {

	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
		getLogger().info("プラグインが停止しました");
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("プラグインが読み込まれました");
	}

	@EventHandler
	public void BlockDamage(BlockDamageEvent e) {

		Block block = e.getBlock();

		if (block.getType() != Material.CHEST) {
			return;
		} else if (isLargeChest(block)) {
			return;
		}

		Chest chest = (Chest) block.getState();
		Inventory oldInv = chest.getInventory();

		HashMap<Integer, ItemStack> items = new HashMap<>();

		for (int i = 0, size = oldInv.getSize(); i < size; i++) {
			ItemStack item = oldInv.getItem(i);
			if (item == null) {
				continue;
			}

			items.put(i, item);
		}

		oldInv.clear();

		block.setType(Material.BLUE_SHULKER_BOX);
		ShulkerBox box = (ShulkerBox) block.getState();
		Inventory newInv = box.getInventory();

		for (Entry<Integer, ItemStack> item : items.entrySet()) {
			newInv.setItem(item.getKey(), item.getValue());
		}

		Location boxLoc = block.getLocation().clone();
		World world = boxLoc.getWorld();

		boxLoc.setY(boxLoc.getY() + 1);
		world.createExplosion(boxLoc, 0);
		world.strikeLightningEffect(boxLoc);
		world.spawnParticle(Particle.VILLAGER_HAPPY, boxLoc, 20, 1, 1, 1);
		world.playSound(boxLoc, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

	}

	private boolean isLargeChest(Block block) {
		if (block.getType() != Material.CHEST) {
			return false;
		}
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, }) {
			Block relative = block.getRelative(face);
			if (relative.getType() == Material.CHEST) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			if (sender instanceof Player) {
				if (args[0].equalsIgnoreCase("on")) {
					getServer().getPluginManager().registerEvents(this, this);
					sender.sendMessage("§a[eChest]プラグインが有効になりました");
					return true;
				} else if (args[0].equalsIgnoreCase("off")) {
					HandlerList.unregisterAll();
					sender.sendMessage("§a[eChest]プラグインが無効になりました");
					return true;
				}
			}
		}

		sender.sendMessage("§c[eChest]使い方： /eschest on または off");
		return false;
	}

}
