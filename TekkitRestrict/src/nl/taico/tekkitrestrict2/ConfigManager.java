package nl.taico.tekkitrestrict2;

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

import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.tekkitrestrict;

public class ConfigManager {
	
	/**
	 * Get new configuration with header
	 * @param filePath - Path to file
	 * @return - New SimpleConfig
	 * @throws TRException 
	 */
	public TRConfig getNewConfig(String filePath, String[] header, int length) {
		final File file = this.getConfigFile(filePath);

		if (!file.exists()) {
			this.prepareFile(filePath);

			if (header != null && header.length != 0) {
				this.setHeader(file, header, length);
			}
		}

		return new TRConfig(this.getConfigContent(filePath), file, this.getCommentsNum(file), length);
	}

	/**
	 * Get new configuration
	 * @param filePath - Path to file
	 * @return - New SimpleConfig
	 * @throws TRException 
	 */
	public TRConfig getNewConfig(String filePath) {
		return this.getNewConfig(filePath, null, 93);
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
				configFile = new File(tekkitrestrict.getInstance().getDataFolder() + file.replace("/", File.separator));
			} else {
				configFile = new File(tekkitrestrict.getInstance().getDataFolder() + File.separator + file.replace("/", File.separator));
			}
		} else {
			configFile = new File(tekkitrestrict.getInstance().getDataFolder(), file);
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
				this.copyResource(tekkitrestrict.getInstance().getResource(resource), file);
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
		if (length < 23) length = 23;

		try {
			String currentLine;
			StringBuilder config = new StringBuilder();
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
			}
			
			config.append(base).append("\n");
			
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

			//StringBuilder addLine = new StringBuilder();
			String currentLine;

			StringBuilder whole = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.startsWith("#") && !currentLine.equals("#")){
					whole.append("TR_COMMENT_")
						   .append(commentNum)
						   .append(": \"")
						   .append(
								currentLine.replace("\"", "{DQUOTE}")
										   .replace("'", "{SQUOTE}")
										   .replace("\\n", "{NEWLINE}")
								  )
						   .append("\"\n");
					commentNum++;
				} else {
					whole.append(currentLine).append("\n");
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
		final String[] lines = configString.split("\n");
		StringBuilder config = new StringBuilder();

		for (String line : lines) {
			//TR_COMMENT_1: "# hoi"
			//TR_COMMENT_2: '########################'
			if (!line.startsWith("TR_COMMENT")){
				config.append(line).append("\n");
			} else {
				String c = line.substring(line.indexOf(":") + 1).trim();
				if ((c.startsWith("\"") && c.endsWith("\"")) || (c.startsWith("'") && c.endsWith("'"))){
					c = c.substring(1, c.length()-1);
				}
				config.append(
						c.replace("{DQUOTE}", "\"")
						 .replace("{SQUOTE}", "'")
						 .replace("{NEWLINE}", "\\n")
						).append("\n");
				//# hoi
				//######################
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
