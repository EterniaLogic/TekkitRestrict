package nl.taico.tekkitrestrict.newconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.bukkit.plugin.java.JavaPlugin;

import nl.taico.tekkitrestrict.TRException;

public class SimpleConfigManager {

	private JavaPlugin plugin;

	/**
	 * Manage custom configurations and files
	 */
	public SimpleConfigManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get new configuration with header
	 * @param filePath - Path to file
	 * @return - New SimpleConfig
	 * @throws TRException 
	 */
	public SimpleConfig getNewConfig(String filePath, String[] header, int length) {
		File file = this.getConfigFile(filePath);

		if (!file.exists()) {
			this.prepareFile(filePath);

			if (header != null && header.length != 0) {
				this.setHeader(file, header, length);
			}

		}

		SimpleConfig config = new SimpleConfig(this.getConfigContent(filePath), file, this.getCommentsNum(file), plugin, length);
		return config;
	}

	/**
	 * Get new configuration
	 * @param filePath - Path to file
	 * @return - New SimpleConfig
	 * @throws TRException 
	 */
	public SimpleConfig getNewConfig(String filePath) {
		return this.getNewConfig(filePath, null, 84);
	}

	/**
	 * Get configuration file from string
	 * @param file - File path
	 * @return - New file object
	 */
	private File getConfigFile(String file) {
		if (file == null || file.isEmpty()) {
			return null;
		}

		File configFile;

		if (file.contains("/")) {
			if (file.startsWith("/")) {
				configFile = new File(plugin.getDataFolder() + file.replace("/", File.separator));
			} else {
				configFile = new File(plugin.getDataFolder() + File.separator + file.replace("/", File.separator));
			}
		} else {
			configFile = new File(plugin.getDataFolder(), file);
		}

		return configFile;
	}

	/**
	 * Create new file for config and copy resource into it
	 * @param file - Path to file
	 * @param resource - Resource to copy
	 */
	public void prepareFile(String filePath, String resource) {
		File file = this.getConfigFile(filePath);
		if (file == null){
			return;
		}
		if (file.exists()) {
			return;
		}

		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			if (resource != null && !resource.isEmpty()) {
				this.copyResource(plugin.getResource(resource), file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create new file for config without resource
	 * @param file - File to create
	 */
	public void prepareFile(String filePath) {
		this.prepareFile(filePath, null);
	}

	/**
	 * Adds header block to config
	 * @param file - Config file
	 * @param header - Header lines
	 * @throws TRException if the length is lower than 10
	 */
	public void setHeader(File file, String[] header, int length) {
		if (!file.exists()) return;
		if (length < 20) length = 20;

		try {
			String currentLine;
			StringBuilder config = new StringBuilder("");
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {
				config.append(currentLine).append("\n");
			}

			reader.close();

			StringBuilder base = new StringBuilder(length+6);
			for (int i = 0;i<(length+6);i++) base.append("#");
			
			config.append(base).append("\n");
			
			for (String line : header) {
				if (line.length() <= length){//20 max
					
					StringBuilder ll = new StringBuilder(line);
					while (ll.length() <= length) ll.append(" ");//make it full length
					
					config.append("## ").append(ll).append("##\n");
				} else {
					String[] temp = line.split(" ");
					
					StringBuilder l0 = new StringBuilder(length+1);
					for (int i = 0; i < temp.length; i++){
						if (temp[i].isEmpty()) continue;
						if (l0.length()+temp[i].length() > length){//123_56_ + 89 > 8
							
							StringBuilder ll = new StringBuilder(l0);
							while (ll.length() <= length) ll.append(" ");
							
							config.append("## ").append(ll).append("##\n");
							l0 = new StringBuilder(length+1);
						}
						
						if (temp[i].length()<length){
							l0.append(temp[i]).append(" ");
						} else {
							config.append("## ").append(temp[i]).append(" ##\n");
						}
					}
					StringBuilder ll = new StringBuilder(l0);
					while (ll.length() <= length) ll.append(" ");
					config.append("## ").append(ll).append("##\n");
				}
				/*
				if (line.length() > 50) {
					continue;
				}

				int length = (50 - line.length()) / 2;
				StringBuilder finalLine = new StringBuilder(line);

				for (int i = 0; i < length; i++) {
					finalLine.append(" ");
					finalLine.reverse();
					finalLine.append(" ");
					finalLine.reverse();
				}

				if (line.length() % 2 != 0) {
					finalLine.append(" ");
				}
				config.append("# " + finalLine.toString());
				*/
				//config.append("# < " + finalLine.toString() + " > #\n");
			}
			
			config.append(base);

			//config.append("# +----------------------------------------------------+ #");

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(this.prepareConfigString(config.toString()));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read file and make comments SnakeYAML friendly
	 * @param filePath - Path to file
	 * @return - File as Input Stream
	 */
	public InputStream getConfigContent(File file) {
		if (!file.exists()) {
			return null;
		}

		try {
			int commentNum = 0;

			String addLine;
			String currentLine;
			String pluginName = this.getPluginName();

			StringBuilder whole = new StringBuilder("");
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {

				if (currentLine.startsWith("#") && !currentLine.startsWith("##")) {
					addLine = pluginName+"_COMMENT_"+commentNum+": \"" + currentLine.substring(1).replace("\\\"", "\"").replace("\"", "\\\"") + "\"";
					//addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
					whole.append(addLine + "\n");
					commentNum++;
				} else {
					whole.append(currentLine + "\n");
				}

			}

			String config = whole.toString();
			InputStream configStream = new ByteArrayInputStream(config.getBytes(Charset.forName("UTF-8")));

			reader.close();
			return configStream;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get comments from file
	 * @param file - File
	 * @return - Comments number
	 */
	private int getCommentsNum(File file) {
		if (!file.exists()) {
			return 0;
		}

		try {
			int comments = 0;
			String currentLine;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.startsWith("#")) {
					comments++;
				}
			}

			reader.close();
			return comments;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Get config content from file
	 * @param filePath - Path to file
	 * @return - readied file
	 */
	public InputStream getConfigContent(String filePath) {
		return this.getConfigContent(this.getConfigFile(filePath));
	}

	private String prepareConfigString(String configString) {
		int lastLine = 0;

		String[] lines = configString.split("\n");
		StringBuilder config = new StringBuilder("");

		for (String line : lines) {
			System.out.println(line);
			if (line.startsWith(this.getPluginName() + "_COMMENT")) {
				String comment = "#" + line.trim().substring(line.indexOf(":") + 1);

				if (comment.startsWith("####") && comment.endsWith("####")) {
					lastLine = 0;

					config.append(comment).append("\n");
				} else {

					/*
					 * Last line = 0 - Comment
					 * Last line = 1 - Normal path
					 */

					String normalComment;

					if (comment.startsWith("# '")) {
						normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# '", "# ");
					} else {
						normalComment = comment;
					}

					if (lastLine == 0) {
						config.append(normalComment + "\n");
					} else if (lastLine == 1) {//start new comment
						config.append("\n" + normalComment + "\n");
					}

					lastLine = 0;

				}

			} else {
				config.append(line + "\n");
				lastLine = 1;
			}

		}

		return config.toString();

	}

	/**
	 * Saves configuration to file
	 * @param configString - Config string
	 * @param file - Config file
	 */
	public void saveConfig(String configString, File file) {
		String configuration = this.prepareConfigString(configString);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(configuration);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getPluginName() {
		return plugin.getDescription().getName();
	}

	/**
	 * Copy resource from Input Stream to file
	 * @param resource - Resource from .jar
	 * @param file - File to write
	 */
	private void copyResource(InputStream resource, File file) {

		try {
			OutputStream out = new FileOutputStream(file);

			int lenght;
			byte[] buf = new byte[1024];

			while ((lenght = resource.read(buf)) > 0) {
				out.write(buf, 0, lenght);
			}

			out.close();
			resource.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
