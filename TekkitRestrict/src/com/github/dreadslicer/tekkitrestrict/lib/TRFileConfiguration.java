package com.github.dreadslicer.tekkitrestrict.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfigurationOptions;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

@SuppressWarnings("rawtypes")
public class TRFileConfiguration extends FileConfiguration {
	public TRFileConfiguration() {
		super();
	}

	@Override
	public Object getDefault(String path) {
		for (YamlConfiguration conf : tekkitrestrict.configList) {
			Object j;
			if ((j = conf.getDefault(path)) != null) {
				return j;
			}
		}
		return null;
	}

	@Override
	public Object get(String path) {
		return get(path, getDefault(path));
	}

	@Override
	public Object get(String path, Object def) {
		for (YamlConfiguration conf : tekkitrestrict.configList) {
			Object j;
			if ((j = conf.get(path, def)) != def) {
				return j;
			}
		}
		return def;
	}

	@Override
	public String getString(String path) {
		Object def = getDefault(path);
		return getString(path, def == null ? null : def.toString());
	}

	@Override
	public String getString(String path, String def) {
		Object val = get(path, def);
		return val == null ? def : val.toString();
	}

	@Override
	public boolean isString(String path) {
		Object val = get(path);
		return val instanceof String;
	}

	@Override
	public int getInt(String path) {
		Object def = getDefault(path);
		return getInt(path,
				(def instanceof Number) ? NumberConversions.toInt(def) : 0);
	}

	@Override
	public int getInt(String path, int def) {
		Object val = get(path, Integer.valueOf(def));
		return (val instanceof Number) ? NumberConversions.toInt(val) : def;
	}

	@Override
	public boolean isInt(String path) {
		Object val = get(path);
		return val instanceof Integer;
	}

	@Override
	public boolean getBoolean(String path) {
		Object def = getDefault(path);
		return getBoolean(path,
				(def instanceof Boolean) ? ((Boolean) def).booleanValue()
						: false);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		Object val = get(path, Boolean.valueOf(def));
		return (val instanceof Boolean) ? ((Boolean) val).booleanValue() : def;
	}

	@Override
	public boolean isBoolean(String path) {
		Object val = get(path);
		return val instanceof Boolean;
	}

	@Override
	public double getDouble(String path) {
		Object def = getDefault(path);
		return getDouble(path,
				(def instanceof Number) ? NumberConversions.toDouble(def)
						: 0.0D);
	}

	@Override
	public double getDouble(String path, double def) {
		Object val = get(path, Double.valueOf(def));
		return (val instanceof Number) ? NumberConversions.toDouble(val) : def;
	}

	@Override
	public boolean isDouble(String path) {
		Object val = get(path);
		return val instanceof Double;
	}

	@Override
	public long getLong(String path) {
		Object def = getDefault(path);
		return getLong(path,
				(def instanceof Number) ? NumberConversions.toLong(def) : 0L);
	}

	@Override
	public long getLong(String path, long def) {
		Object val = get(path, Long.valueOf(def));
		return (val instanceof Number) ? NumberConversions.toLong(val) : def;
	}

	@Override
	public boolean isLong(String path) {
		Object val = get(path);
		return val instanceof Long;
	}

	@Override
	public List<Object> getList(String path) {
		Object def = getDefault(path);
		return getList(path, (def instanceof List) ? (List) def : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getList(String path, List def) {
		Object val = get(path, def);
		return (List<Object>) ((val instanceof List) ? val : def);
	}

	@Override
	public boolean isList(String path) {
		Object val = get(path);
		return val instanceof List;
	}

	@Override
	public List<String> getStringList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<String>(0);
		}
		List<String> result = new ArrayList<String>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if ((object instanceof String) || isPrimitiveWrapper(object)) {
				result.add(String.valueOf(object));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Integer>(0);
		}
		List<Integer> result = new ArrayList<Integer>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Integer) {
				result.add((Integer) object);
			} else if (object instanceof String) {
				try {
					result.add(Integer.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Integer.valueOf(((Character) object).charValue()));
			} else if (object instanceof Number) {
				result.add(Integer.valueOf(((Number) object).intValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Boolean> getBooleanList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Boolean>(0);
		}
		List<Boolean> result = new ArrayList<Boolean>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Boolean) {
				result.add((Boolean) object);
			} else if (object instanceof String) {
				if (Boolean.TRUE.toString().equals(object)) {
					result.add(Boolean.valueOf(true));
				} else if (Boolean.FALSE.toString().equals(object)) {
					result.add(Boolean.valueOf(false));
				}
			}
		} while (true);
		return result;
	}

	@Override
	public List<Double> getDoubleList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Double>(0);
		}
		List<Double> result = new ArrayList<Double>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Double) {
				result.add((Double) object);
			} else if (object instanceof String) {
				try {
					result.add(Double.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Double.valueOf(((Character) object).charValue()));
			} else if (object instanceof Number) {
				result.add(Double.valueOf(((Number) object).doubleValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Float> getFloatList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Float>(0);
		}
		List<Float> result = new ArrayList<Float>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Float) {
				result.add((Float) object);
			} else if (object instanceof String) {
				try {
					result.add(Float.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Float.valueOf(((Character) object).charValue()));
			} else if (object instanceof Number) {
				result.add(Float.valueOf(((Number) object).floatValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Long> getLongList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Long>(0);
		}
		List<Long> result = new ArrayList<Long>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Long) {
				result.add((Long) object);
			} else if (object instanceof String) {
				try {
					result.add(Long.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Long.valueOf(((Character) object).charValue()));
			} else if (object instanceof Number) {
				result.add(Long.valueOf(((Number) object).longValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Byte> getByteList(String path) {
		List<Object> list = getList(path);
		if (list == null) {
			return new ArrayList<Byte>(0);
		}
		List<Byte> result = new ArrayList<Byte>();
		Iterator<Object> i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Byte) {
				result.add((Byte) object);
			} else if (object instanceof String) {
				try {
					result.add(Byte.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Byte.valueOf((byte) ((Character) object).charValue()));
			} else if (object instanceof Number) {
				result.add(Byte.valueOf(((Number) object).byteValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Character> getCharacterList(String path) {
		List list = getList(path);
		if (list == null) {
			return new ArrayList<Character>(0);
		}
		List<Character> result = new ArrayList<Character>();
		Iterator i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Character) {
				result.add((Character) object);
			} else if (object instanceof String) {
				String str = (String) object;
				if (str.length() == 1) {
					result.add(Character.valueOf(str.charAt(0)));
				}
			} else if (object instanceof Number) {
				result.add(Character.valueOf((char) ((Number) object)
						.intValue()));
			}
		} while (true);
		return result;
	}

	@Override
	public List<Short> getShortList(String path) {
		List list = getList(path);
		if (list == null) {
			return new ArrayList<Short>(0);
		}
		List<Short> result = new ArrayList<Short>();
		Iterator i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Short) {
				result.add((Short) object);
			} else if (object instanceof String) {
				try {
					result.add(Short.valueOf((String) object));
				} catch (Exception ex) {
				}
			} else if (object instanceof Character) {
				result.add(Short.valueOf((short) ((Character) object)
						.charValue()));
			} else if (object instanceof Number) {
				result.add(Short.valueOf(((Number) object).shortValue()));
			}
		} while (true);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getMapList(String path) {
		List list = getList(path);
		List<Map> result = new ArrayList<Map>();
		if (list == null) {
			return result;
		}
		Iterator i$ = list.iterator();
		do {
			if (!i$.hasNext()) {
				break;
			}
			Object object = i$.next();
			if (object instanceof Map) {
				result.add((Map) object);
			}
		} while (true);
		return result;
	}

	@Override
	public Vector getVector(String path) {
		Object def = getDefault(path);
		return getVector(path, (def instanceof Vector) ? (Vector) def : null);
	}

	@Override
	public Vector getVector(String path, Vector def) {
		Object val = get(path, def);
		return (val instanceof Vector) ? (Vector) val : def;
	}

	@Override
	public String buildHeader() {
		String header = options().header();
		if (options().copyHeader()) {
			org.bukkit.configuration.Configuration def = getDefaults();
			if (def != null && (def instanceof YamlConfiguration)) {
				YamlConfiguration filedefaults = (YamlConfiguration) def;
				String defaultsHeader = filedefaults.buildHeader();
				if (defaultsHeader != null && defaultsHeader.length() > 0) {
					return defaultsHeader;
				}
			}
		}
		if (header == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		String lines[] = header.split("\r?\n", -1);
		boolean startedHeader = false;
		for (int i = lines.length - 1; i >= 0; i--) {
			builder.insert(0, "\n");
			if (startedHeader || lines[i].length() != 0) {
				builder.insert(0, lines[i]);
				builder.insert(0, "# ");
				startedHeader = true;
			}
		}

		return builder.toString();
	}

	@Override
	public void loadFromString(String contents)
			throws InvalidConfigurationException {
		Validate.notNull(contents, "Contents cannot be null");
		Map input;
		try {
			input = (Map) yaml.load(contents);
		} catch (YAMLException e) {
			throw new InvalidConfigurationException(e);
		} catch (ClassCastException e) {
			throw new InvalidConfigurationException("Top level is not a Map.");
		}
		String header = parseHeader(contents);
		if (header.length() > 0) {
			options().header(header);
		}
		if (input != null) {
			convertMapsToSections(input, this);
		}
	}

	@Override
	public String saveToString() {
		yamlOptions.setIndent(options().indent());
		yamlOptions
				.setDefaultFlowStyle(org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK);
		yamlRepresenter
				.setDefaultFlowStyle(org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK);
		String header = buildHeader();
		String dump = yaml.dump(getValues(false));
		if (dump.equals("{}\n")) {
			dump = "";
		}
		return (new StringBuilder()).append(header).append(dump).toString();
	}

	protected void convertMapsToSections(Map input, ConfigurationSection section) {
		for (Iterator i$ = input.entrySet().iterator(); i$.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) i$.next();
			String key = entry.getKey().toString();
			Object value = entry.getValue();
			if (value instanceof Map) {
				convertMapsToSections((Map) value, section.createSection(key));
			} else {
				section.set(key, value);
			}
		}

	}

	protected String parseHeader(String input) {
		String lines[] = input.split("\r?\n", -1);
		StringBuilder result = new StringBuilder();
		boolean readingHeader = true;
		boolean foundHeader = false;
		for (int i = 0; i < lines.length && readingHeader; i++) {
			String line = lines[i];
			if (line.startsWith("# ")) {
				if (i > 0) {
					result.append("\n");
				}
				if (line.length() > "# ".length()) {
					result.append(line.substring("# ".length()));
				}
				foundHeader = true;
				continue;
			}
			if (foundHeader && line.length() == 0) {
				result.append("\n");
				continue;
			}
			if (foundHeader) {
				readingHeader = false;
			}
		}

		return result.toString();
	}

	public static YamlConfiguration loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(
					Level.SEVERE,
					(new StringBuilder()).append("Cannot load ").append(file)
							.toString(), ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(
					Level.SEVERE,
					(new StringBuilder()).append("Cannot load ").append(file)
							.toString(), ex);
		}
		return config;
	}

	public static YamlConfiguration loadConfiguration(InputStream stream) {
		Validate.notNull(stream, "Stream cannot be null");
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(stream);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Cannot load configuration from stream", ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Cannot load configuration from stream", ex);
		}
		return config;
	}

	@Override
	public YamlConfigurationOptions options() {
		if (options == null) {
			options = new YamlConfigurationOptions(this);
		}
		return (YamlConfigurationOptions) options;
	}

	protected static final String COMMENT_PREFIX = "# ";
	protected static final String BLANK_CONFIG = "{}\n";
	private final DumperOptions yamlOptions = new DumperOptions();
	private final Representer yamlRepresenter = new YamlRepresenter();
	private final Yaml yaml = new Yaml();
	protected Configuration defaults;
	protected MemoryConfigurationOptions options;
}
