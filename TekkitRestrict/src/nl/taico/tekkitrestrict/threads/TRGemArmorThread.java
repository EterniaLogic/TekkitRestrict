package nl.taico.tekkitrestrict.threads;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.minecraft.server.EntityHuman;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import ee.EEBase;

public class TRGemArmorThread extends Thread {
	@Override
	public void run() {
		if (!tekkitrestrict.config.getBoolean(ConfigFile.EEPatch, "Actions.Armor.Movement.Activate", true) && !Threads.GAMovement)
			Threads.GAMovement = true;
		if (!tekkitrestrict.config.getBoolean(ConfigFile.EEPatch, "Actions.Armor.Offensive.Activate", true) && !Threads.GAOffensive)
			Threads.GAOffensive = true;
		
		int errors = 0;
		while (true) {
			try {
				if (!tekkitrestrict.EEEnabled){
					Warning.other("The GemArmorDisabler thread has stopped because EE is disabled.", false);
					break; //If ee is disabled, stop the thread.
				}
				GemArmorDisabler();
			} catch (Exception ex) {
				errors++;
				Warning.other("Error: [GemArmor thread] " + ex.toString(), false);
				if (errors < 2){
					Log.Exception(ex, true);
				}
				
				if (errors > 50){
					Warning.other("The GemArmorDisabler thread has errored for more than 50 time now. It will now be disabled.", true);
					break;
				}
			}
			
			try {
				if (!Threads.GAMovement && !Threads.GAOffensive)
					Thread.sleep(Threads.gemArmorSpeed*25);
				else
					Thread.sleep(Threads.gemArmorSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The gemarmor thread shouldn't trigger again.
			}
		}
	}

	private void GemArmorDisabler() throws Exception {
		//TODO Change this one day
		try {
			if (!Threads.GAMovement) {
				synchronized (EEBase.playerArmorMovementToggle) {
					Iterator<EntityHuman> it = EEBase.playerArmorMovementToggle.keySet().iterator();
					//ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = it.next();
						Player player = (Player) human.getBukkitEntity();
						if (player.hasPermission("tekkitrestrict.bypass.gemarmor.defensive")) continue;
						player.sendMessage(ChatColor.RED + "You are not allowed to use GemArmor Movement Powers!");
						it.remove();
						//toremove.add(human);
					}
					
					//for (EntityHuman current : toremove){
					//	EEBase.playerArmorMovementToggle.remove(current);
					//}
				}
			}
			
			if (!Threads.GAOffensive) {
				synchronized (EEBase.playerArmorOffensiveToggle) {
					Iterator<EntityHuman> it = EEBase.playerArmorOffensiveToggle.keySet().iterator();
					//ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = it.next();
						Player player = (Player) human.getBukkitEntity();
						if (player.hasPermission("tekkitrestrict.bypass.gemarmor.offensive")) continue;
						player.sendMessage(ChatColor.RED + "You are not allowed to use GemArmor Offensive Powers!");
						it.remove();
						//toremove.add(human);
					}
					
					//for (EntityHuman current : toremove){
					//	EEBase.playerArmorOffensiveToggle.remove(current);
					//}
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
	}
}
