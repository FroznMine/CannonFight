package de.pesacraft.cannonfight.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pesacraft.cannonfight.Language;
import de.pesacraft.cannonfight.data.players.CannonFighter;
import de.pesacraft.cannonfight.game.Arena;
import de.pesacraft.cannonfight.game.Arenas;
import de.pesacraft.cannonfight.game.GameManager;

public class JoinCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {		
		if (args.length == 0) {
			// zufällige Arena
			
			if (!(sender instanceof Player)) {
				// only players can join
				sender.sendMessage(Language.get("command.join-only-players")); 
				return true;
			}
			
			if (!sender.hasPermission("cannonfight.command.join.random")) {
				sender.sendMessage(Language.get("error.no-permission"));
				return true;
			}
			
			CannonFighter c = CannonFighter.get((Player) sender);
			Arena a;
			
			if (c.isInGame() || c.isInQueue()) {
				c.sendMessage(Language.get("error.has-to-leave-before-join"));
				return true;
			}
			a = Arenas.random();
			
			join(c, a);
			return true;
		}
		
		if (args.length == 1) {
			// angegebene Arena
			
			if (!(sender instanceof Player)) {
				// only players can join
				sender.sendMessage(Language.get("command.join-only-players")); 
				return true;
			}
			
			if (!sender.hasPermission("cannonfight.command.join")) {
				sender.sendMessage(Language.get("error.no-permission"));
				return true;
			}
			
			CannonFighter c = CannonFighter.get((Player) sender);
			
			if (c.isInGame() || c.isInQueue()) {
				c.sendMessage(Language.get("error.has-to-leave-before-join"));
				return true;
			}
			
			Arena a = Arenas.getArena(args[0]);
			
			join(c, a);
			return true;
		}
		
		if (args.length == 2) {
			// anderen in eine arena
			if (!sender.hasPermission("cannonfight.command.join.other")) {
				sender.sendMessage(Language.get("error.no-permission"));
				return true;
			}
			
			Player p = Bukkit.getPlayer(args[1]);
			
			if (p == null) {
				sender.sendMessage(Language.get("error.player-not-online").replaceAll("%player%", args[1]));
				return true;
			}
			
			CannonFighter c = CannonFighter.get(p);
			Arena a = Arenas.getArena(args[0]);
			
			if (c.isInGame() || c.isInQueue()) {
				c.sendMessage(Language.get("error.has-to-leave-before-join-other"));
				return true;
			}
			
			if (join(c, a))
				sender.sendMessage(Language.get("command.join-successful-other")); 
			else
				sender.sendMessage(Language.get("command.join-failed-other")); 
			
			return true;
		}
		
		sender.sendMessage(Language.get("error.wrong-usage").replaceAll("%command%", "/" + label + " [arena] [player]"));
		return true;
	}

	
	public static boolean join(CannonFighter c, Arena a) {
		GameManager g = GameManager.getForArena(a);
		
		if (g.isGameRunning()) {
			// Spiel läuft -> hinzufügen
			if (g.addPlayer(c)) {
				// konnte rein
				c.sendMessage(Language.get("command.join-successful").replaceAll("%game%", a.getName()));
				return true;
			}
		}
		
		// kein Spiel läuft oder konnte nicht joinen -> zur queue
		if (g.addToQueue(c)) {
			// kann in queue
			c.sendMessage(Language.get("command.join-queue-succesful").replaceAll("%arena%", a.getName()));
			return true;
		}
		
		// kann nicht in queue
		c.sendMessage(Language.get("command.join-queue-failed"));
		return false;
	}
}
