package nl.taico.tekkitrestrict.functions;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class TRErrorFixer {
	public static void addFix(){
		Bukkit.getServer().getLogger().addHandler(new Handler(){
			@Override
			public void close() throws SecurityException {}

			@Override
			public void flush() {}

			@Override
			public void publish(LogRecord r) {
				if (r.getMessage().contains("[BuildCraft] Pipe failed to load from NBT at ")){
					final String[] msg = r.getMessage().replace("[BuildCraft] Pipe failed to load from NBT at ", "").split(",");
					for (final World world : Bukkit.getWorlds()){
						final Block block = new Location(world, Integer.parseInt(msg[0]), Integer.parseInt(msg[1]), Integer.parseInt(msg[2])).getBlock();
						if (block.getTypeId() == 166){
							block.breakNaturally();
							return;
						}
					}
				} else if (r.getMessage().trim().matches("Attempted to place a tile entity \\(.*\\) at (.*) \\(AIR\\) where there was no entity tile!")){
					Pattern pat = Pattern.compile("(-?\\d+,-?\\d+,-?\\d+)");
					Matcher mat = pat.matcher(r.getMessage());
					if (mat.find()){
						final String[] msg = mat.group().split(",");
						for (final World world : Bukkit.getWorlds()){
							final Block block = new Location(world, Integer.parseInt(msg[0]), Integer.parseInt(msg[1]), Integer.parseInt(msg[2])).getBlock();
							if (block.isEmpty()){
								block.setTypeId(1);
								block.breakNaturally();
							}
						}
					}
				}
			}
			
		});
	}
}
