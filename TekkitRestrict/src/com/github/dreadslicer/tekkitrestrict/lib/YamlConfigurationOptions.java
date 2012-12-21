package com.github.dreadslicer.tekkitrestrict.lib;

import org.apache.commons.lang.Validate;

//Referenced classes of package org.bukkit.configuration.file:
//         FileConfigurationOptions, YamlConfiguration, FileConfiguration

public class YamlConfigurationOptions extends FileConfigurationOptions {

	public YamlConfigurationOptions(TRFileConfiguration trFileConfiguration) {
		super(trFileConfiguration);
		indent = 2;
	}

	public YamlConfigurationOptions(YamlConfiguration yamlConfiguration) {
		super(yamlConfiguration);
		indent = 2;
	}

	@Override
	public YamlConfiguration configuration() {
		return (YamlConfiguration) super.configuration();
	}

	@Override
	public YamlConfigurationOptions copyDefaults(boolean value) {
		super.copyDefaults(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions pathSeparator(char value) {
		super.pathSeparator(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions header(String value) {
		super.header(value);
		return this;
	}

	@Override
	public YamlConfigurationOptions copyHeader(boolean value) {
		super.copyHeader(value);
		return this;
	}

	public int indent() {
		return indent;
	}

	public YamlConfigurationOptions indent(int value) {
		Validate.isTrue(value >= 2, "Indent must be at least 2 characters");
		Validate.isTrue(value <= 9,
				"Indent cannot be greater than 9 characters");
		indent = value;
		return this;
	}

	private int indent;
}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from: /home/dread/tekkit_server/Tekkit_.jar Total time: 32 ms Jad
 * reported messages/errors: The class file version is 49.0 (only 45.3, 46.0 and
 * 47.0 are supported) Exit status: 0 Caught exceptions:
 */
