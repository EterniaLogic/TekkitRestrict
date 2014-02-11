package nl.taico.tekkitrestrict2;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import nl.taico.tekkitrestrict.newconfig.*;

public class TRConfig {
	private int comments;
	private ConfigManager manager;

	private File file;
	private FileConfiguration config;
	private int linelength;

	public TRConfig(InputStream configStream, File configFile, int comments, int linelength) {
		this.comments = comments;
		this.manager = new ConfigManager();

		this.file = configFile;
		this.config = YamlConfiguration.loadConfiguration(configStream);
		this.linelength = linelength+3;
	}

	public Object get(String path) {
		return this.config.get(path);
	}

	public Object get(String path, Object def) {
		return this.config.get(path, def);
	}

	public String getString(String path) {
		return this.config.getString(path);
	}

	public String getString(String path, String def) {
		return this.config.getString(path, def);
	}

	public int getInt(String path) {
		return this.config.getInt(path);
	}

	public int getInt(String path, int def) {
		return this.config.getInt(path, def);
	}

	public boolean getBoolean(String path) {
		return this.config.getBoolean(path);
	}

	public boolean getBoolean(String path, boolean def) {
		return this.config.getBoolean(path, def);
	}

	public void createSection(String path) {
		this.config.createSection(path);
	}

	public ConfigurationSection getConfigurationSection(String path) {
		return this.config.getConfigurationSection(path);
	}

	public double getDouble(String path) {
		return this.config.getDouble(path);
	}

	public double getDouble(String path, double def) {
		return this.config.getDouble(path, def);
	}

	public List<?> getList(String path) {
		return this.config.getList(path);
	}

	public List<?> getList(String path, List<?> def) {
		return this.config.getList(path, def);
	}

	public List<String> getStringList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<String>(0);
		}

		List<String> result = new ArrayList<String>();
		for (Object object : list) {
			if ((object instanceof String) || (isPrimitiveWrapper(object))) {
				result.add(String.valueOf(object));
			}
		}

		return result;
	}

	public List<Integer> getIntegerList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Integer>(0);
		}

		List<Integer> result = new ArrayList<Integer>();
		for (Object object : list) {
			if (object instanceof Integer) {
				result.add((Integer) object);
			} else if (object instanceof String) {
				try {
					result.add(Integer.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((int) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).intValue());
			}
		}

		return result;
	}

	public List<Boolean> getBooleanList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Boolean>(0);
		}

		List<Boolean> result = new ArrayList<Boolean>();
		for (Object object : list) {
			if (object instanceof Boolean) {
				result.add((Boolean) object);
			} else if (object instanceof String) {
				if (Boolean.TRUE.toString().equals(object)) {
					result.add(true);
				} else if (Boolean.FALSE.toString().equals(object)) {
					result.add(false);
				}
			}
		}

		return result;
	}

	public List<Double> getDoubleList(String path) {
		List<?> list = getList(path);

		if (list == null) {
			return new ArrayList<Double>(0);
		}
		List<Double> result = new ArrayList<Double>();
		for (Object object : list) {
			if (object instanceof Double) {
				result.add((Double) object);
			} else if (object instanceof String) {
				try {
					result.add(Double.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((double) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).doubleValue());
			}
		}
		
		return result;
	}

	public List<Float> getFloatList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Float>(0);
		}

		List<Float> result = new ArrayList<Float>();
		for (Object object : list) {
			if (object instanceof Float) {
				result.add((Float) object);
			} else if (object instanceof String) {
				try {
					result.add(Float.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((float) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).floatValue());
			}
		}

		return result;
	}

	public List<Long> getLongList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Long>(0);
		}

		List<Long> result = new ArrayList<Long>();
		for (Object object : list) {
			if (object instanceof Long) {
				result.add((Long) object);
			} else if (object instanceof String) {
				try {
					result.add(Long.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((long) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).longValue());
			}
		}

		return result;
	}
	public List<Byte> getByteList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Byte>(0);
		}
		
		List<Byte> result = new ArrayList<Byte>();
		for (Object object : list) {
			if (object instanceof Byte) {
				result.add((Byte) object);
			} else if (object instanceof String) {
				try {
					result.add(Byte.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((byte) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).byteValue());
			}
		}

		return result;
	}

	public List<Character> getCharacterList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Character>(0);
		}

		List<Character> result = new ArrayList<Character>();
		for (Object object : list) {
			if (object instanceof Character) {

				result.add((Character) object);
			} else if (object instanceof String) {
				String str = (String) object;

				if (str.length() == 1) {
					result.add(str.charAt(0));
				}
			} else if (object instanceof Number) {
				result.add((char) ((Number) object).intValue());
			}
		}

		return result;
	}

	public List<Short> getShortList(String path) {
		List<?> list = getList(path);
		if (list == null) {
			return new ArrayList<Short>(0);
		}

		List<Short> result = new ArrayList<Short>();
		for (Object object : list) {
			if (object instanceof Short) {
				result.add((Short) object);
			} else if (object instanceof String) {
				try {
					result.add(Short.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add((short) ((Character) object).charValue());
			} else if (object instanceof Number) {
				result.add(((Number) object).shortValue());
			}
		}

		return result;
	}

	public List<Map<?, ?>> getMapList(String path) {
		List<?> list = getList(path);
		List<Map<?, ?>> result = new ArrayList<Map<?, ?>>();
		if (list == null) {
			return result;
		}

		for (Object object : list) {
			if (object instanceof Map) {
				result.add((Map<?, ?>) object);
			}
		}

		return result;
	}

	public boolean contains(String path) {
		return this.config.contains(path);
	}

	public void removeKey(String path) {
		this.config.set(path, null);
	}

	public void set(String path, Object value) {
		this.config.set(path, value);
	}

	public void set(String path, Object value, String comment) {
		if(!this.config.contains(path)) {
			this.config.set("TR_COMMENT_" + comments, "# ");
			comments++;
			for (String s : handleComment(comment)){
				this.config.set("TR_COMMENT_" + comments, s);
				comments++;
			}
		}

		this.config.set(path, value);
	}
	
	private String[] handleComment(String line){
		if (line.length() <= linelength || (line.startsWith("####") && line.endsWith("####"))){//20 max
			return new String[] {
					line.replace("\"", "{DQUOTE}")
						.replace("'", "{SQUOTE}")
						.replace("\\n", "{NEWLINE}")
						};
		} else {
			ArrayList<String> tbr = new ArrayList<String>();
			String[] temp = line.split(" ");
			
			StringBuilder l0 = new StringBuilder(linelength);
			for (int i = 0; i < temp.length; i++){
				if (temp[i].isEmpty()) continue;
				if (l0.length()+temp[i].length() > linelength){//123_56_ + 89 > 8
					tbr.add(l0.toString().trim());
					
					l0 = new StringBuilder(linelength);
				}
				
				if (temp[i].length()<linelength){
					l0.append(temp[i]).append(" ");
				} else {
					tbr.add(temp[i]);//TODO Change it so it breaks up the word instead.
				}
			}
			tbr.add(l0.toString()
					.trim()
					.replace("\"", "{DQUOTE}")
					.replace("'", "{SQUOTE}")
					.replace("\\n", "{NEWLINE}")
					);
			return tbr.toArray(new String[0]);
		}
	}

	public void set(String path, Object value, String[] comment) {
		if(!this.config.contains(path)) {
			this.config.set("TR_COMMENT_" + comments, "# ");
			comments++;
			for(String comm : comment) {
				for (String s : handleComment(comm)){
					if (!s.startsWith("#")) this.config.set("TR_COMMENT_" + comments, "# "+s);
					else this.config.set("TR_COMMENT_" + comments, s);
					comments++;
				}
			}
		}

		this.config.set(path, value);
	}

	public void setHeader(String[] header, int length) {
		manager.setHeader(this.file, header, length);
		//this.comments = header.length + 2;
		this.reloadConfig();
	}
	
	public void setHeader(String[] header) {
		manager.setHeader(this.file, header, linelength);
		//this.comments = header.length + 2;
		this.reloadConfig();
	}

	public void reloadConfig() {
		this.config = YamlConfiguration.loadConfiguration(manager.getConfigContent(file));
	}

	public void saveConfig() {
		String config = this.config.saveToString();
		manager.saveConfig(config, this.file);

	}

	public Set<String> getKeys() {
		return this.config.getKeys(false);
	}
	
	protected boolean isPrimitiveWrapper(Object input) {
		return input instanceof Integer || input instanceof Boolean ||
				input instanceof Character || input instanceof Byte ||
				input instanceof Short || input instanceof Double ||
				input instanceof Long || input instanceof Float;

	}
}