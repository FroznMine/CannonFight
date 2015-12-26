package de.pesacraft.cannonfight.game;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import de.pesacraft.cannonfight.util.game.blockrestore.ModifiedBlock;

public class BlockChange extends BukkitRunnable {
	private ModifiedBlock block;
	
	public BlockChange(Block block, Material newMaterial, byte newData) {
		this.block = new ModifiedBlock(block);
	}

	@Override
	public void run() {
		this.block.restore();
	}
	
	public ModifiedBlock getBlock() {
		return block;
	}
}