package de.pesacraft.cannonfight.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pesacraft.cannonfight.Language;
import de.pesacraft.cannonfight.data.players.CannonFighter;
import de.pesacraft.cannonfight.game.Arenas;
import de.pesacraft.cannonfight.game.GameManager;

public class ForceStartCommand {

	public static boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				// only players can force without given command
				sender.sendMessage(Language.get("command.force-only-player-without-arg")); // ChatColor.RED + "Only players can force start without a given arena!"
				return true;
			}
			if (!sender.hasPermission("cannonfight.command.force") && !sender.hasPermission("cannonfight.command.*")) {
				sender.sendMessage(Language.get("error.no-permission"));
				return true;
			}
			
			CannonFighter c = CannonFighter.get((Player) sender);
			
			if (!c.isInQueue()) {
				// wartet nicht auf spielstart
				sender.sendMessage(Language.get("command.force-not-in-queue"));
				return true;
			}
			
			// spieler in queue
			if (GameManager.getForArena(c.getArenaQueuing()).startGame(true)) {
				// Spiel gestartet
				sender.sendMessage(Language.get("command.force-own-successful"));
			}
			else {
				// spiel konnte nicht gestartet werden
				sender.sendMessage(Language.get("command.force-own-failed"));
			}
		}
		else if (args.length == 1) {
			if (!sender.hasPermission("cannonfight.command.force.specific") && !sender.hasPermission("cannonfight.command.*")) {
				sender.sendMessage(Language.get("error.no-permission"));
				return true;
			}
			
			if (GameManager.getForArena(Arenas.getArena(args[0])).startGame(true)) {
				// Spiel gestartet
				sender.sendMessage(Language.get("command.force-specific-successful"));
			}
			else {
				// spiel konnte nicht gestartet werden
				sender.sendMessage(Language.get("command.force-specific-failed"));
			}
		}
		else 
			return false;
		return true;
	}

}
