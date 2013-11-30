package nl.taico.tekkitrestrict.functions;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.FileLog;
import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.tekkitrestrict;

import com.earth2me.essentials.Essentials;

public class TRLogFilter implements Filter {
private static boolean disabled = false;
	
	//private static List<String> SplitCI = new LinkedList<String>();
	//private static List<String> SplitCIPlayer = new LinkedList<String>();
	
	private static Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
	
	private static FileLog chat, info, login, command, spawnitem, privatechat, banskicks;
	
	public TRLogFilter(){
		chat = new FileLog("Chat");
		info = new FileLog("Info");
	}
	
	public static void disable(){
		disabled = true;
		essentials = null;
		chat = null;
		login = null;
		command = null;
		info = null;
		spawnitem = null;
		privatechat = null;
		banskicks = null;
	}
	
	/*
	public static void loadConfig() {
		if (!TRConfigCache.LogFilter.Split.Enabled) {
			TRConfigCache.LogFilter.Split.FilterOut = new ArrayList<String>();
			TRConfigCache.LogFilter.Split.FilterOutWithPlayer = new ArrayList<String>();
		}
	}*/
	
	/** @return If the message contains the name of the given player. It also checks nicknames if essentials is enabled. */
	private boolean containsPlayer(Player player, String message){
		message = message.toLowerCase();
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

	@Override
	public boolean isLoggable(LogRecord record) {
		try {
			if (record.getMessage() == null) return true;
			if (disabled || tekkitrestrict.disable) return true;
			
			String a = record.getMessage();
			String b = record.getMessage().toLowerCase();
			
			if (TRConfigCache.LogFilter.splitLogs) SplitLog(record, a, b);
			
			if (TRConfigCache.LogFilter.filterLogs){
				String levelname = record.getLevel().getName().toLowerCase();
				for (String filtera : TRConfigCache.LogFilter.replaceList) {
					filtera = filtera.toLowerCase();
					if (levelname.equals(filtera)) return false;
					else if (b.contains(filtera)) return false;
				}
			}
			
			return true;
		} catch (Exception ex){
			disabled = true;
			tekkitrestrict.log.warning("Logfilter disabled.");
			tekkitrestrict.log.warning("Exception in isLoggable: " + ex.toString());
			return false;
		}
	}
	
	/** @return Whether string b contains string needle. */
	private boolean c(String b, String needle){
		return b.contains(needle);
	}
	
	private void SplitLog(LogRecord record, String a, String b){
		try {
		//boolean LogSplitOccuredM = false, LogSplitOccuredP = false;
		if (c(b, " disconnected: ") || c(b, "logged in with") || c(b, "joined with: [") || c(b, "sending serverside check to")){
			if (login == null) login = FileLog.getLogOrMake("Login");
			login.log(a);
		} else if (c(b, "player_command") || record.getLevel() == Level.parse("Command")) {
			if (command == null) command = FileLog.getLogOrMake("Command");
			command.log(a);
			
			if (c(b, "/i ") || c(b, "/give ") || c(b, "/more ") || c(b, "/unlimited ")){
				if (spawnitem == null) spawnitem = FileLog.getLogOrMake("SpawnItem");
				spawnitem.log(a);
			} else if (c(b, "/msg ") || c(b, "/emsg ") || c(b, "/m ") || c(b, "/tell ") || c(b, "/etell ") || c(b, "/whisper ") || c(b, "/ewhisper ") ||
					   c(b, "/r ") || c(b, "/er ") || c(b, "/reply ") || c(b, "/ereply ") || c(b, "/mail ") || c(b, "/email ")) {
				if (privatechat == null) privatechat = FileLog.getLogOrMake("PrivateChat");
				privatechat.log(a);
			} else if (c(b, "/kick ") || c(b, "/ekick ") || c(b, "/bmkick ") ||
					   c(b, "/ban ") || c(b, "/eban ") || c(b, "/bmban ") ||
					   c(b, "/tempban ") || c(b, "/etempban ") || c(b, "/bmtempban ") ||
					   c(b, "/banip ") || c(b, "/ebanip ") || c(b, "/bmbanip ") ||
					   c(b, "/unban ") || c(b, "/eunban ") || c(b, "/bmunban ") || c(b, "/pardon ") || c(b, "/epardon ") ||
					   c(b, "/unbanip ") || c(b, "/eunbanip ") || c(b, "/bmunbanip ") || c(b, "/pardonip ") || c(b, "/epardonip ")){
				if (banskicks == null) banskicks = FileLog.getLogOrMake("BansKicks");
				banskicks.log(a);
			}
		} else {
			
			/*
			for (String c : TRConfigCache.LogFilter.Split.FilterOut) {
				String[] LogSplit = c.split(";~;");
				String LogSearch = LogSplit[0].toLowerCase();
				String LogSplitLoc = LogSplit[1].replace(" ", "_");
				if (b.contains(LogSearch)) {
					FileLog.getLogOrMake(LogSplitLoc).log("[" + record.getLevel().getName() + "] " + a);
					LogSplitOccuredM=true;
				}
			}*/
			
			//if (LogSplitOccuredM) return;
				
			Player[] players = Bukkit.getOnlinePlayers();
			for (Player current : players) {
				if (!containsPlayer(current, b)) continue;
				
				if (b.contains("[34;1mgiving")) {
					if (spawnitem == null) spawnitem = FileLog.getLogOrMake("SpawnItem");
					spawnitem.log("[CONSOLE] " + a);
					return;
				} else if ((b.contains("giving " + current.getName().toLowerCase())
						|| b.contains("giving " + current.getDisplayName().toLowerCase())
						|| b.contains("giving " + current.getPlayerListName().toLowerCase())) && b.contains(" of ")) {
					if (spawnitem == null) spawnitem = FileLog.getLogOrMake("SpawnItem");
					spawnitem.log("[NEI] " + a);
					return;					
				} else {
					chat.log(a);
					return;
					/*
					for (String c : TRConfigCache.LogFilter.Split.FilterOutWithPlayer) {
						String[] LogSplit = c.split(";~;");
						String LogSearch = LogSplit[0].toLowerCase();
						String LogSplitLoc = LogSplit[1].replace(" ", "_");
						if (LogSplit.length >= 3) {
							if (LogSplit[2].equalsIgnoreCase("true")) cc = true;
						}
						
						if (c(b, LogSearch)) {
							FileLog.getLogOrMake(LogSplitLoc).log("[" + record.getLevel().getName() + "] " + a);
							LogSplitOccuredP=true;
						}
					}
					
					if (!LogSplitOccuredP) {
						chat.log(a);
						break;
					} else {
						LogSplitOccuredP=false;
					}*/
				}
			}
			info.log("[" + record.getLevel().getName() + "] " + a);
		}
		} catch (Exception ex){
			disabled = true;
			tekkitrestrict.log.warning("Logfilter disabled.");
			tekkitrestrict.log.warning("Exception in splitlog: ");
			ex.printStackTrace();
		}
	}
	
/*	private static List<String> toFilter = new LinkedList<String>();
	private static Boolean logConsole;

	public static void reload() {
		toFilter = tekkitrestrict.config.getStringList("LogFilter");
		logConsole = tekkitrestrict.config.getBoolean("LogConsole");
		
		 * for(int i=0;i<toFilter.size();i++){
		 * tekkitrestrict.log.info(toFilter.get(i)); }
		 
	}

	@Override
	public boolean isLoggable(LogRecord record) {

		if (logConsole) {
			// okay, so here we will split up the server.log

			// Login ---- logged in with
			// Logout --- lost connection
			// Warning -- WARNING
			// Error ---- SEVERE
			// Chat ----- PlayerName
			// Command -- PLAYER_COMMAND
			if (record.getMessage() != null) {
				Player[] pl = tekkitrestrict.getInstance().getServer().getOnlinePlayers();
				String a = record.getMessage();
				String b = record.getMessage().toLowerCase();
				boolean lc = false;
				for (int i = 0; i < pl.length; i++) {
					if (b.contains(pl[i].getName().toLowerCase()
							+ " lost connection")) {
						TRLogger.LogConsole("Login", a);
						lc = true;
					}
				}

				if (lc) {
				} else if (b.contains("logged in with")) {
					TRLogger.LogConsole("Login", a);
				} else if (record.getLevel().equals(Level.WARNING)) {
					TRLogger.LogConsole("Warning", a);
				} else if (record.getLevel().equals(Level.SEVERE)) {
					TRLogger.LogConsole("Error", a);
				} else if (b.contains("player_command")) {
					TRLogger.LogConsole("Command", a);
					if (b.contains("/i ")) {
						TRLogger.LogConsole("SpawnItem", a);
					} else if (b.contains("/give ")) {
						TRLogger.LogConsole("SpawnItem", a);
					}
				} else {
					boolean cc = false;

					for (int i = 0; i < pl.length; i++) {
						String chatline = a;
						if (b.contains("sending serverside check to")) {
						} else if (b.contains(pl[i].getName().toLowerCase())
								|| b.contains(pl[i].getDisplayName()
										.toLowerCase())
								|| b.contains(pl[i].getPlayerListName()
										.toLowerCase())) {
							if (b.contains("[34;1mgiving")) {
								TRLogger.LogConsole("SpawnItem", "[CONSOLE] " + a);
								cc = true;
							} else if ((b.contains("giving " + pl[i].getName().toLowerCase())
									|| b.contains("giving " + pl[i].getDisplayName().toLowerCase())
									|| b.contains("giving " + pl[i].getPlayerListName().toLowerCase()))
									&& b.contains(" of ")) {
								//Extensive check to reduce the chance of someone saying something triggering this.
								TRLogger.LogConsole("SpawnItem", "[NEI] " + a);
								cc = true;
							} else {
								TRLogger.LogConsole("Chat", chatline);
								cc = true;
							}
						}
					}
					if (!cc) {
						TRLogger.LogConsole("Info", a);
					}
				}
			}
		}

		if (record.getMessage() != null) {
			String a = record.getMessage();
			boolean c = false;
			for (int i = 0; i < toFilter.size(); i++) {
				String filtera = toFilter.get(i).toLowerCase();
				if (record.getLevel().getName().toLowerCase().equals(filtera)) {
					c = true;
				} else if (a.toLowerCase().contains(filtera)) {
					c = true;
				}
			}
			return !c;
		}

		return true;
	}
*/
}
