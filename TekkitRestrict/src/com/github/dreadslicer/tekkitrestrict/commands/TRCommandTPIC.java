package com.github.dreadslicer.tekkitrestrict.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.dreadslicer.tekkitrestrict.TRLogger;
import com.github.dreadslicer.tekkitrestrict.TRPermHandler;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

public class TRCommandTPIC implements CommandExecutor {

	// private tekkitrestrict plugin; // pointer to your main class, unrequired
	// if you don't need methods from the main class

	public TRCommandTPIC(tekkitrestrict plugin) {
		// this.plugin = plugin;

	}

	// private static java.util.HashMap<String, Object[]> InvAlc = new
	// java.util.HashMap<String, Object[]>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		boolean admin = false;

		if (sender instanceof Player) {
			player = (Player) sender;
			try {
				if (TRPermHandler.hasPermission(player, "admin", "", "")) {
					admin = true;
				}
			} catch (Exception e) {
				if (player.isOp()) {
					admin = true;
				}
			}
		}
		List<String> message = new LinkedList<String>();
		boolean usemsg = true;
		if (cmd.getName().equalsIgnoreCase("tpic") && (admin)) {
			try {
				if (player != null) {
					int max = 0;
					if (args.length == 0) {
						max = 200;
					} else {
						try {
							max = Integer.valueOf(args[0]);
						} catch (Exception e) {
							message.add("This is not a number!");
						}
					}
					// ok, tp here.
					tpic(player, max);
				}
			} catch (Exception e) {
				message.add("An error has occured processing your command.");
				TRLogger.Log("debug", "TRCommandTPIC Error: " + e.getMessage());
				for (StackTraceElement ee : e.getStackTrace()) {
					TRLogger.Log("debug", "     " + ee.toString());
				}
			}
			if (usemsg) {
				sendMessage(player, message.toArray(new String[0]));
				message.clear();
			}
			return true;
		}
		// ?
		return false;
	}

	public static void tpic(Player player, int max) {
		// ok, so the first thing is...
		// loop through all entities in the world
		List<org.bukkit.World> ww = tekkitrestrict.getInstance().getServer()
				.getWorlds();
		for (int k = 0; k < ww.size(); k++) {
			org.bukkit.World w = ww.get(k);
			Object[] oo = w.getEntities().toArray();
			for (int i = 0; i < oo.length; i++) {

				if (oo[i] instanceof Item) {
					List<Item> nearby = new LinkedList<Item>();
					Item ei = (Item) oo[i];
					Vector V = ei.getLocation().toVector();
					// find nearby items...
					// tekkitrestrict.log.info("MAIN---");
					for (int j = 0; j < oo.length; j++) {
						if (oo[j] instanceof Item) {
							Item ej = (Item) oo[j];
							Vector Vj = ej.getLocation().toVector();
							if (V.distance(Vj) <= 16) {
								nearby.add(ej);
								// tekkitrestrict.log.info("link");
							}
						}
					}
					// tekkitrestrict.log.info(""+nearby.size());
					if (nearby.size() >= max) {
						player.sendMessage("Found (" + nearby.size()
								+ ") in this area!");
						player.teleport(ei.getLocation());
						return;
					}
				}
			}
		}
		player.sendMessage("There are no " + max + "-item chunks.");
	}

	public static void sendMessage(Player player, String[] message) {
		if (player != null) {
			for (int k = 0; k < message.length; k++) {
				player.sendRawMessage(message[k]);
			}
		} else {
			for (int k = 0; k < message.length; k++) {
				tekkitrestrict.log.log(Level.OFF, message[k]);
			}
		}
	}
}