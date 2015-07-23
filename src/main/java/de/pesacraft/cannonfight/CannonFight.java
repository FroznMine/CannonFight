package de.pesacraft.cannonfight;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.pesacraft.cannonfight.commands.JoinCommand;
import de.pesacraft.cannonfight.game.Game;
import de.pesacraft.cannonfight.util.money.CraftConomyMoney;
import de.pesacraft.cannonfight.util.money.DatabaseMoney;
import de.pesacraft.cannonfight.util.money.Money;
import de.pesacraft.lobbysystem.LobbySystem;

public class CannonFight extends JavaPlugin {
	public static Logger LOGGER;
	public static CannonFight PLUGIN;
	public static Money MONEY;
	
	public void onEnable() { 
		PLUGIN = this;
		LOGGER = this.getLogger();
		
		if (!new File(this.getDataFolder() + "config.yml").exists()) this.saveDefaultConfig();
		
		registerSerializables();
		
		loadConfig();
		loadArenas();
		loadCannons();
		loadMoney();
		LobbySystem.registerGame(this, Game.class);
	}
	 
	private void loadMoney() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Craftconomy3");
		
		if (plugin != null) {
			MONEY = new CraftConomyMoney(plugin);
		}
		else {
			MONEY = new DatabaseMoney();
		}
	}

	private void loadCannons() {
		// TODO Auto-generated method stub
		
	}

	private void registerSerializables() {
		// TODO Auto-generated method stub
		
	}

	private void loadArenas() {
		// TODO Auto-generated method stub
		
	}

	private void loadConfig() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length >= 1) {
			String subcommand = args[0];
			
			if (subcommand.equalsIgnoreCase("join")) {
				return JoinCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length - 1));
			}
		}
		return super.onCommand(sender, command, label, args);
	}

	public void onDisable() { 
	 
	}
}