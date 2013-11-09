package com.github.dreadslicer.tekkitrestrict;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import nl.taico.tekkitrestrict.lib.RandomString;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;

public class PatchCC {
	static final String s = File.separator;
	static void start(){
		File patched = new File("mods"+s+"ComputerCraft"+s+"lua"+s+"rom"+s+"patched2"+s);
		if (patched.exists()) return;
		
		BufferedReader input = null;
		File file = new File("mods"+s+"ComputerCraft"+s+"lua"+s+"rom"+s+"startup"+s);
		if (!file.exists()){
			tekkitrestrict.loadWarning("[CCPatch] ComputerCraft file cannot be found! (" + file.getAbsolutePath() + ")");
			return;
		}
		
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			tekkitrestrict.loadWarning("[CCPatch] ComputerCraft file cannot be found! (" + file.getAbsolutePath() + ")");
			return;
		}
		
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line;
			while ((line = input.readLine()) != null){
				lines.add(line);
			}
			input.close();
		} catch (IOException e) {
			tekkitrestrict.loadWarning("[CCPatch] Cannot read ComputerCraft file! (" + file.getAbsolutePath() + ")");
			try {
				input.close();
			} catch (IOException e1) {}
			return;
		}
		boolean rebootPatch = true, rsCrashPatch = true, nulPatch = false;
		for (String curline : lines){
			if (curline == null) continue;
			if (curline.contains("os.reboot = nil") || curline.contains("os.reboot=nil")) rebootPatch = false;
			else if (curline.contains("bypassAntiRedstoneCrashBug = rs.setOutput") || curline.contains("rs.setOutput = function(side, bool)")) rsCrashPatch = false;
			else if (curline.contains("\000")) nulPatch = true;
			
			if (!rebootPatch && !rsCrashPatch && nulPatch) break;
		}
		
		if (!rebootPatch && !rsCrashPatch && !nulPatch){
			try {
				patched.createNewFile();
			} catch (IOException e) {
				tekkitrestrict.loadWarning("[CCPatch] Unable to write patched file!");
				return;
			}
			return;
		}

		if (rebootPatch){
			tekkitrestrict.log.info("[CCPatch] Adding reboot patch...");
			lines.add(0, "os.reboot = nil");
			tekkitrestrict.log.info("[CCPatch] Reboot patch added.");
		} else {
			tekkitrestrict.log.info("[CCPatch] Reboot patch already found, skipping reboot patch...");
		}
		
		if (rsCrashPatch) {
			RandomString ran = new RandomString(10);
			String extra = ran.nextString();
			tekkitrestrict.log.info("[CCPatch] Adding redstone crash patch...");
			lines.add(extra+"bypassAntiRedstoneCrashBug = rs.setOutput");
			lines.add("rs.setOutput = function(side, bool)");
			lines.add("    sleep(0.05)");
			lines.add("    "+extra+"bypassAntiRedstoneCrashBug(side, bool)");
			lines.add("end");
			tekkitrestrict.log.info("[CCPatch] Redstone crash patch added.");
		} else {
			tekkitrestrict.log.info("[CCPatch] Redstone crash patch already found, skipping redstone crash patch...");
		}
		
		if (nulPatch){
			tekkitrestrict.log.info("[CCPatch] Your Computers startupfile is corrupt! Repairing...");
			char c = RandomString.randomChar();
			for (String line : lines){
				line.replace('\000', c);
			}
			tekkitrestrict.log.info("[CCPatch] Repair complete!");
		}
		
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			for (String line2 : lines){
				output.append(line2);
				output.newLine();
			}
			output.close();
		} catch (IOException e) {
			tekkitrestrict.loadWarning("[CCPatch] Unable to write changes to file!");
			return;
		}
		
		try {
			patched.createNewFile();
		} catch (IOException e) {
			Warning.other("[CCPatch] Unable to write patched file!");
			return;
		}
		tekkitrestrict.log.info("[CCPatch] Patching completed!");
		return;
	}
}
