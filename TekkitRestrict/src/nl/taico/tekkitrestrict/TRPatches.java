package nl.taico.tekkitrestrict;

import ic2.api.Ic2Recipes;
import ic2.common.EntityMiningLaser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.lib.RandomString;

import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RedPowerMachine;

public class TRPatches {
	public static boolean patchMiningLaser(){
		Log.trace("TRPatches - Patching Mining Laser...");
		try {
			final ArrayList<Block> miningLaser = new ArrayList<Block>();
			for (final Block block : EntityMiningLaser.unmineableBlocks) miningLaser.add(block);
			
			miningLaser.add(Block.byId[194]);
			EntityMiningLaser.unmineableBlocks = miningLaser.toArray(new Block[miningLaser.size()]);
			return true;
		} catch (Exception ex){
			Log.debugEx(ex);
			return false;
		}
	}
	
	public static boolean patchDeployer(){
		Log.trace("TRPatches - Patching Deployer...");
		try {			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6362));//REP
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6359));//Wireless sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6363));//Private sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27562));//Alcbag
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27585));//Divining ROd
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(30122));//Cropnalyser
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(30104));//Debug item
			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27592));//transtablet
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(7493));//Ender pouch
			return true;
		} catch (Exception ex){
			Log.debugEx(ex);
			return false;
		}
	}
	
	public static boolean patchBlockBreaker(){
		Log.trace("TRPatches - Patching Block Breaker...");
		try {
			//.add(dmg << 15 | id)
			RedPowerMachine.breakerBlacklist.add(Integer.valueOf(-1 << 15 | 194));
			return true;
		} catch (Exception ex){
			Log.debugEx(ex);
			return false;
		}
	}
	
	public static boolean addNetherOresRecipes(){
		Log.trace("TRPatches - Adding Nether Ore Macerator Recipes...");
		try {
			Ic2Recipes.addMaceratorRecipe(new ItemStack(135, 1, 2), new ItemStack(30254, 4, 0));
			Ic2Recipes.addMaceratorRecipe(new ItemStack(135, 1, 3), new ItemStack(30255, 4, 0));
			return true;
		} catch (Exception ex){
			Log.debugEx(ex);
			return false;
		}
	}
	
	public static void patchCC(){
		final String s = File.separator;
		Character nul = '\000';//not final to make sure this gets compiled correctly
		final String path = "mods"+s+"ComputerCraft"+s+"lua"+s+"rom"+s;
		final File patched = new File(path+"patched3"+s);
		if (patched.exists()) return;
		
		Log.trace("TRPatches - Patching ComputerCraft...");
		
		BufferedReader input = null;
		final File file = new File(path+"startup"+s);
		if (!file.exists()){
			Warning.load("[CCPatch] ComputerCraft file cannot be found! (" + file.getPath() + ")", false);
			return;
		}
		
		try {
			input = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			Warning.load("[CCPatch] ComputerCraft file cannot be found! (" + file.getPath() + ")", false);
			return;
		}
		
		final LinkedList<String> lines = new LinkedList<String>();
		try {
			String line;
			while ((line = input.readLine()) != null){
				lines.add(line);
			}
			input.close();
		} catch (IOException e) {
			Warning.load("[CCPatch] Cannot read ComputerCraft file! (" + file.getAbsolutePath() + ")", false);
			try {
				input.close();
			} catch (IOException e1) {}
			return;
		}
		boolean rebootPatch = true, rsCrashPatch = true, nulPatch = true;
		for (final String curline : lines){
			if (curline == null) continue;
			if (curline.contains("os.reboot = nil") || curline.contains("os.reboot=nil")) rebootPatch = false;
			else if (curline.contains("bypassAntiRedstoneCrashBug = rs.setOutput") || curline.contains("rs.setOutput = function(side, bool)")) rsCrashPatch = false;
			
			if (!rebootPatch && !rsCrashPatch) break;
		}
		
		if (!rebootPatch && !rsCrashPatch && !nulPatch){
			try {
				patched.createNewFile();
			} catch (IOException e) {
				Warning.load("[CCPatch] Unable to write patched file!", false);
				return;
			}
			return;
		}

		if (rebootPatch){
			Log.info("[CCPatch] Adding reboot patch...");
			lines.add(0, "os.reboot = nil");
			Log.info("[CCPatch] Reboot patch added.");
		} else {
			Log.info("[CCPatch] Reboot patch already found, skipping reboot patch...");
		}
		
		if (rsCrashPatch) {
			final String extra = new RandomString(10).nextString();
			Log.info("[CCPatch] Adding redstone crash patch...");
			lines.add(extra+"bypassAntiRedstoneCrashBug = rs.setOutput");
			lines.add("rs.setOutput = function(side, bool)");
			lines.add("    sleep(0.05)");
			lines.add("    "+extra+"bypassAntiRedstoneCrashBug(side, bool)");
			lines.add("end");
			Log.info("[CCPatch] Redstone crash patch added.");
		} else {
			Log.info("[CCPatch] Redstone crash patch already found, skipping redstone crash patch...");
		}
		
		if (nulPatch){
			
			boolean corrupt = false;
			final char d = RandomString.randomChar();
			for (int i = 0;i<lines.size();i++){
				final String l = lines.get(i);
				final String l2 = l.replace(nul, d);
				if (!l.equals(l2)){
					corrupt = true;
					lines.set(i, l2);
				}
			}
			
			if (corrupt){
				Log.info("[CCPatch] Your Computers startupfile was corrupt and has been repaired!");
			}
			
		}
		
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			for (final String line2 : lines){
				output.append(line2);
				output.newLine();
			}
			output.close();
		} catch (IOException e) {
			Warning.load("[CCPatch] Unable to write changes to file!", false);
			return;
		}
		
		try {
			patched.createNewFile();
		} catch (IOException e) {
			Warning.other("[CCPatch] Unable to write patched file!", false);
			return;
		}
		Log.info("[CCPatch] Patching completed!");
		return;
	}
}
