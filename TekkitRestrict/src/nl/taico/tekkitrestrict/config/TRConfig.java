package nl.taico.tekkitrestrict.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;

public abstract class TRConfig {
	protected static String s = File.separator;
	protected static boolean isPrimitive(Object input) {
	    return input instanceof Integer || input instanceof Boolean ||
	            input instanceof Character || input instanceof Byte ||
	            input instanceof Short || input instanceof Double ||
	            input instanceof Long || input instanceof Float;
	}
	
	protected static int toInt(Object object) {
	    if (object instanceof Number) {
	        return ((Number) object).intValue();
	    }

	    try {
	        return Integer.valueOf(object.toString());
	    } catch (NumberFormatException e) {
	    } catch (NullPointerException e) {
	    }
	    return 0;
	}
	
	protected static double toDouble(Object object) {
	    if (object instanceof Number) {
	        return ((Number) object).doubleValue();
	    }

	    try {
	        return Double.valueOf(object.toString());
	    } catch (NumberFormatException e) {
	    } catch (NullPointerException e) {
	    }
	    return 0;
	}
	
	protected static void upgradeFile(String name, ArrayList<String> content){
		tekkitrestrict.log.info("Upgrading "+name+".config.yml file.");
		File configFile = new File("plugins"+s+"tekkitrestrict"+s+name+".config.yml");
		if (configFile.exists()){
			File backupfile = new File("plugins"+s+"tekkitrestrict"+s+name+".config_backup.yml");
			if (backupfile.exists()) backupfile.delete();
			if (!configFile.renameTo(backupfile)){
				configFile = new File("plugins"+s+"tekkitrestrict"+s+name+".config.yml");
				configFile.delete();
			}
			configFile = new File("plugins"+s+"tekkitrestrict"+s+name+".config.yml");
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(configFile));
			for (int i = 0;i<content.size();i++){
				if (i != 0) output.newLine();
				output.append(content.get(i));
			}
			output.close();
		} catch (IOException e) {
			Warning.load("Unable to write changes to "+name+".config.yml!", false);
			try {if (output != null) output.close();} catch (IOException e1) {}
			return;
		}
		tekkitrestrict.log.info(name+".config.yml file was upgraded successfully!");
		Warning.loadWarnings.add(name+".config.yml file was upgraded! Please check the new/changed config settings!");
	}
	
	@SuppressWarnings("unchecked")
	protected static ArrayList<String> convertDefaults(ArrayList<String> defaults){
		int j = defaults.size();
		for (int i = 0;i<j;i++){
			String str = defaults.get(i);
			if (str.contains("#:-;-:#")){
				str = str.replace("#:-;-:# ", "");
				String nr = null;
				if (str.contains(" ")){
					nr = str.split(" ")[1];
					str = str.split(" ")[0];
				}
				final Object obj = tekkitrestrict.config.get(str, null);
				if (obj == null){
					defaults.remove(i);
					i--; j--;
					continue;
				}
				
				if (obj instanceof String){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": \""+obj.toString().replace("\\\"", "#;~;#").replace("\"", "\\\"").replace("#;~;#", "\\\"")+"\"");
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Integer){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toInt(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Double){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toDouble(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Boolean){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+((Boolean) obj).toString());
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof List){
					final List<Object> l = (List<Object>) obj;
					
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": " + (l.isEmpty() ? "[]" : ""));
					defaults.remove(i);//Remove posString, cursor is at first element of list
					if (nr != null){
						int k = Integer.parseInt(nr);
						while (k>0){
							defaults.remove(i-1+k);
							k--;
							j--;
						}
					}

					for (Object o : l){
						if (isPrimitive(o)){
							defaults.add(i, "- " + o.toString());//input element after posstring, default values are after this one.
						} else if (o instanceof String){
							defaults.add(i, "- \"" + o.toString().replace("\\\"", "#;~;#").replace("\"", "\\\"").replace("#;~;#", "\\\"") + "\"");//input element after posstring, default values are after this one.
						} else {
							tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, not Primitive or String");
							continue;
						}
						i++;//input next element after this one.
						j++;
					}
					i--; j--;
				} else {
					tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, obj is unknown object! Class: " + obj.getClass().getName());
					continue;
				}
			}
		}
		return defaults;
	}
	
	@SuppressWarnings("unchecked")
	protected static ArrayList<String> convertDefaultsShortList(ArrayList<String> defaults){
		int j = defaults.size();
		for (int i = 0;i<j;i++){
			String str = defaults.get(i);
			if (str.contains("#:-;-:#")){
				str = str.replace("#:-;-:# ", "");
				
				final Object obj = tekkitrestrict.config.get(str, null);
				if (obj == null){
					defaults.remove(i);
					i--; j--;
					continue;
				}
				
				if (obj instanceof String){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": \""+obj.toString().replace("\\\"", "#;~;#").replace("\"", "\\\"").replace("#;~;#", "\\\"")+"\"");
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Integer){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toInt(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Double){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toDouble(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Boolean){
					final String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+((Boolean) obj).toString());
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof List){
					final List<Object> l = (List<Object>) obj;
					
					final String str2 = defaults.get(i-1);//Method: "1"
					String toadd = "";
					for (Object o : l){
						if (isPrimitive(o) || o instanceof String){
							toadd += "\""+o.toString().replace("\\\"", "#;~;#").replace("\"", "\\\"").replace("#;~;#", "\\\"")+"\", ";
						} else {
							tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, not Primitive or String");
							continue;
						}
					}
					if (!toadd.equals("")){
						defaults.set(i-1, str2.split(":")[0] + ": [" + toadd.substring(0, toadd.length()-2) + "]");
					} else {
						defaults.set(i-1, str2.split(":")[0] + ": []");
					}
					
					defaults.remove(i);//Remove posString, cursor is at first element of list
					
					i--; j--;
				} else {
					tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, obj is unknown object! Class: " + obj.getClass().getName());
					defaults.remove(i);
					i--; j--;
					continue;
				}
			}
		}
		return defaults;
	}
	
	public static void upgradeAllConfig(){
		AdvancedConfig.upgradeFile();
		DatabaseConfig.upgradeFile();
		DisableClickConfig.upgradeFile();
		DisableItemsConfig.upgradeFile();
		GeneralConfig.upgradeFile();
		HackDupeConfig.upgradeFile();
	}
}
