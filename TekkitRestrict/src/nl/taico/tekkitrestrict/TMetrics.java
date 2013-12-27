package nl.taico.tekkitrestrict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TMetrics {
	public TMetrics(final JavaPlugin plugin, boolean showWarnings){
		this.plugin = plugin;
		this.showWarnings = showWarnings;
	}

	private static final String BASE_URL = "http://metrics.taico.nl/";
	private static final String REPORT_URL = "tekkitrestrict.php";
	private static final int PING_INTERVAL = 15;

	public int uid = 0;
	private int savedId = 0;
	private boolean first = true;
	private boolean logged = false;
	private final JavaPlugin plugin;
	private int taskId = -1;
	private boolean showWarnings;

	public boolean start(){
		if (!tekkitrestrict.useTMetrics){
			stop();
			return false;
		}
		
		if (taskId >= 0) {
            return true;
        }
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run() {
				taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
		            public void run() {
		                try {
		                    postPlugin();
		                } catch (TMetricsException ex) {
		                    if (showWarnings) {
		                        Bukkit.getLogger().warning("[TMetrics] Error: " + ex.toString());
		                    }
		                }
		            }
		        }, 0, PING_INTERVAL * 1200);
			}
		}, 20);//Execute 20 ticks after all plugins have loaded
		

        return true;
	}
	
	public void stop(){
		if (taskId > 0) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
	}

	private void postPlugin() throws TMetricsException {
		if (plugin == null) throw new TMetricsException("The developer forgot to initiate the metrics. Please report this bug if you see this message.");
		File file = new File(plugin.getDataFolder()+File.separator+"uid");
		if (uid == 0){
			if (file.exists()) savedId = uid = readUIDFile(file);
		}

		int playersOnline = Bukkit.getServer().getOnlinePlayers().length;
		//String params = "";
		String shortparams = "";
		if (uid == 0 || first){
			int onlineMode = Bukkit.getServer().getOnlineMode() ? 1 : 0;
			String pluginVersion = tekkitrestrict.version.fullVer;
			String serverVersion = Bukkit.getVersion();
	
			String osname = System.getProperty("os.name");
			String osarch = System.getProperty("os.arch");
			String osversion = System.getProperty("os.version");
			String java_version = System.getProperty("java.version");
			int coreCount = Runtime.getRuntime().availableProcessors();
			long memory = Runtime.getRuntime().maxMemory();
			if (memory == Long.MAX_VALUE) memory = Runtime.getRuntime().totalMemory();
			memory = Math.round(memory/(1024*1024));
	
			// normalize os arch .. amd64 -> x86_64
			if (osarch.equals("amd64")) osarch = "x86_64";
			
			int arch = 0;
			if (osarch.equals("x86_64")) arch = 1;
			int eepatch = tekkitrestrict.linkEEPatch()?1:0;
	
			/*
			params =  "id="+uid+"&"
					+ "pver="+pluginVersion+"&"
					+ "sver="+serverVersion+"&"
					+ "online="+onlineMode+"&"
					+ "players="+playersOnline+"&"
					+ "osname="+osname+"&"
					+ "osarch="+arch+"&"
					+ "osver="+osversion+"&"
					+ "jver="+java_version+"&"
					+ "cores="+coreCount+"&"
					+ "memory="+memory+"&"
					+ "eepatch="+eepatch;
			*/
			
			shortparams =
					  "id="+uid+"&"
					+ "a="+pluginVersion+"&"
					+ "b="+serverVersion+"&"
					+ "c="+onlineMode+"&"
					+ "z="+playersOnline+"&"
					+ "d="+osname+"&"
					+ "e="+arch+"&"
					+ "f="+osversion+"&"
					+ "g="+java_version+"&"
					+ "h="+coreCount+"&"
					+ "i="+memory+"&"
					+ "j="+eepatch;
		} else {
			/*
			params =  "id="+uid+"&"
					+ "players="+playersOnline;
			*/
			
			shortparams =
					  "id="+uid+"&"
					+ "z="+playersOnline;
		}
		//String request = "http://metrics.taico.nl/tekkitrestrict.php";
		String request = BASE_URL + REPORT_URL;
		if (first){
			request += "?first=1";
			first = false;
		}
		URL url; 
		HttpURLConnection connection = null;
		try {
			url = new URL(request);
			connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", "TMetrics/1");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(shortparams.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(shortparams);
			wr.flush();
			wr.close();

			int responseCode = connection.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			ArrayList<String> response = new ArrayList<String>();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.add(inputLine);
			}

			in.close();
			connection.disconnect();

			if (responseCode != 200){
				throw new TMetricsException("Unable to send statistics! Response code: "+responseCode);
			} else if (response.size() == 0){
				throw new TMetricsException("An error occured while trying to send statistics: No response from server.");
			}

			for (String s : response){
				if (s == null || s.equals("")) continue;
				if (s.contains("id=")){
					try {
						uid = Integer.parseInt(s.replace("id=", ""));
					} catch (NumberFormatException ex) {
						if (showWarnings) Bukkit.getLogger().warning("[TMetrics] Invalid response from server!");
					}
				} else if (s.contains("OK:")){
					continue;
				/*
				} else if (s.contains("IMPO/RTANT: ")) {
					if (showWarnings) scheduleRepeatingMSG(s.replace("IMPOR/TANT: ", ""));
					Bukkit.getLogger().warning("[TekkitRestrict] Important Message: "+s.replace("IMPOR/TANT: ", ""));
				} else if (s.contains("MSG: ")){
					if (showWarnings) Bukkit.getLogger().info("[TMetrics] Message from server: "+s.replace("MSG: ", ""));
				*/
				} else if (s.contains("ERROR: ")){
					if (showWarnings){
						int error = 0;
						try {
							error = Integer.parseInt(s.replace("ERROR: ", ""));
							String msg = null;
							if (error == 1 || error == 3 || error == 6)
								msg = "Unable to save statistics!";
							else if (error == 2)
								msg = "No ID given!";
							else if (error == 4)
								msg = "Unable to get UID!";
							else if (error > 100)
								msg = "The version of TekkitRestrict you are using has a serious bug. It is highly recommended to update to TekkitRestrict 1."+(error-100)+" or a higher version.\n";
							else
								msg = "Unknown error: " + error;
							Bukkit.getLogger().warning("[TMetrics] Statistics server returned: " + msg);
						} catch (Exception ex){
							Bukkit.getLogger().warning("[TMetrics] Statistics server returned an invalid response!");
						}
					}
				}
			}

			if (savedId != uid || savedId == 0) generateUIDFile(file);
		} catch (Exception ex){
			if (connection != null){
				connection.disconnect();
			}
			if (!logged){
				logged = true;
				throw new TMetricsException("An error occured while trying to send statistics: "+ex.toString()+"\n"+"This error will only be logged once.");
			}
		}
	}
	
	/*
	private void scheduleRepeatingMSG(final String message){
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				Bukkit.getLogger().warning("[TekkitRestrict] Important Message: "+message);
				for (Player player : Bukkit.getOnlinePlayers()){
					if (player.isOp()) player.sendMessage(ChatColor.BLUE+"[TekkitRestrict] Important Message: " + message);
				}
			}
		}, 20*60*10, 20*60*60);
	}*/

	private void generateUIDFile(File file) {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			writer.write(""+uid);
		} catch (IOException ex){
			if (showWarnings) Bukkit.getLogger().warning("[TMetrics] Unable to write UID to file!");
		} finally {
			try {if (writer != null) writer.close();} catch (IOException ex) {}
		}
	}

	private int readUIDFile(File file){
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			return 0;
		}

		String line = "0";
		try {
			line = input.readLine();
		} catch (IOException ex) {
			if (showWarnings) Bukkit.getLogger().warning("[TMetrics] The UID file for TekkitRestrict cannot be read!");
		}

		try {
			input.close();
		} catch (IOException e) {
			if (showWarnings) Bukkit.getLogger().warning("[TMetrics] Unknown error occured when reading UID for TekkitRestrict!");
		}

		try {
			return Integer.parseInt(line);
		} catch (NumberFormatException ex){
			if (showWarnings) Bukkit.getLogger().warning("[TMetrics] The UID file for TekkitRestrict is malformed!");
		}

		return 0;
	}
}
