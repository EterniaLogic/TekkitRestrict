package nl.taico.tekkitrestrict.logging;

import java.util.ArrayList;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.FileLog;
import nl.taico.tekkitrestrict.objects.TREnums.TRLogLevel;

import com.earth2me.essentials.Essentials;

public class TRSplitter {
	private String value;
	private String file;
	private FileLog filelog;
	private boolean cs;
	private boolean player;
	private TRLogLevel level;
	private static ArrayList<TRSplitter> splitters = new ArrayList<TRSplitter>();
	private static Plugin essentials = null;
	
	public TRSplitter(String value, String file){
		this(value, file, false, false, TRLogLevel.ALL);
	}
	
	public TRSplitter(String value, String file, TRLogLevel level){
		this(value, file, false, false, level);
	}
	
	public TRSplitter(String value, String file, boolean cs, boolean player){
		this(value, file, cs, player, TRLogLevel.ALL);
	}
	
	public TRSplitter(String value, String file, boolean cs, boolean player, TRLogLevel level){
		this.value = cs ? value : value.toLowerCase();
		this.file = file;
		this.cs = cs;
		this.player = player;
		this.level = level;
		splitters.add(this);
	}
	
	public String getValue(){
		return value;
	}
	
	public FileLog getFileLog(){
		if (filelog == null) filelog = FileLog.getLogOrMake(file, true);
		return filelog;
	}
	
	public String getFile(){
		return file;
	}
	
	public boolean isCaseSensitive(){
		return cs;
	}
	
	public boolean isForPlayer(){
		return player;
	}
	
	public TRLogLevel getLogLevel(){
		return level;
	}
	
	public ArrayList<TRSplitter> getFilters(){
		return splitters;
	}
	
	public static void setEssentials(Plugin essentials){
		TRSplitter.essentials = essentials;
	}
	
	public boolean matches(LogRecord record){
		if (!level.doesApply(record.getLevel())) return false;
		
		String message = record.getMessage();
		if (cs && !message.contains(value)) return false;
		
		String lmessage = record.getMessage().toLowerCase();
		if (!cs && !lmessage.contains(value)) return false;
		
		if (!player) return true;
		
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player : players){
			if (containsPlayer(player, lmessage)) return true;
		}
		return false;
	}
	
	public boolean matchesCmd(String cmd){
		if (level != TRLogLevel.ALL && level != TRLogLevel.PLAYER_COMMAND) return false;
		
		if (cs && !cmd.contains(value)) return false;
		
		if (!cs && !cmd.toLowerCase().contains(value)) return false;
		return true;
	}
	
	/** @return If the message contains the name of the given player. It also checks nicknames if essentials is enabled. */
	private boolean containsPlayer(Player player, String message){
		if (message.contains(player.getName().toLowerCase())) return true;
		if (message.contains(player.getDisplayName().toLowerCase())) return true;
		if (message.contains(player.getPlayerListName().toLowerCase())) return true;
		if (essentials != null){
			String nick = ((Essentials) essentials).getUser(player).getNickname();
			if (nick == null) return false;
			if (message.contains("~" + nick.toLowerCase())) return true;
		}
		return false;
	}
	
	public static void split(LogRecord record){
		for (TRSplitter s : splitters){
			if (s.matches(record)){
				s.getFileLog().log(record.getMessage(), record.getMillis());
				break;
			}
		}
	}
	
	public static void splitCmd(Player player, String cmd){
		for (TRSplitter s : splitters){
			if (s.matchesCmd(cmd)){
				s.getFileLog().log(player.getName() + " used " + cmd);
				break;
			}
		}
	}
	
	private static FileLog nei = null;
	public static void logNEIGive(String msg){
		if (true/*TRConfigCache.LogNEI*/){
			if (nei == null) nei = FileLog.getLogOrMake("SpawnItem", true);
			nei.log(msg);
		}
	}
}
