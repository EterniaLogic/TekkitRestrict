package nl.taico.tekkitrestrict.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.functions.TRSafeZone;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

public class TREntityRemoverThread extends Thread {
	private boolean err1;

	@SuppressWarnings("rawtypes")
	private void disableEntities() {
		if (!Threads.SSDisableEntities) return;

		List<World> worlds = TekkitRestrict.getInstance().getServer().getWorlds();

		//int range = Threads.SSDisableEntitiesRange;
		//if (range > 15) range = 15;
		try {
			for (World world : worlds) {
				try {
					Chunk[] chunks = world.getLoadedChunks();
					for (Chunk c : chunks){
						final ArrayList<Entity> tbr = new ArrayList<Entity>();
						Entity[] entities = c.getEntities();
						try {
							loop2: 
								for (Entity e : entities){
									if ((e instanceof org.bukkit.entity.Item) || (e instanceof Player) || (e instanceof ExperienceOrb) || (e instanceof FallingSand) || (e instanceof Painting)) continue;
									if ((e instanceof Vehicle) && !(e instanceof Pig)) continue;

									for (Class cl : TRConfigCache.Threads.SSClassBypasses){
										if (cl.isInstance(e)){
											continue loop2;
										}
									}
									tbr.add(e);
								}
						} catch (Exception ex){}

						Bukkit.getScheduler().scheduleSyncDelayedTask(TekkitRestrict.getInstance(), new Runnable(){
							@Override
							public void run(){
								try {
									Iterator<Entity> it = tbr.iterator();
									while (it.hasNext()){
										Entity e = it.next();
										if (e == null) continue;

										//int x = e.getLocation().getBlockX();
										//int z = e.getLocation().getBlockZ();
										//if (Math.abs(x-lastx)<=range && Math.abs(z-lastz)<=range){
										//	e.remove();
										//	it.remove();
										//} else {
										if (!"".equals(TRSafeZone.getSafeZoneByLocation(e.getLocation(), true))){
											//lastx = x;
											//lastz = z;
											e.remove();
											//it.remove();
										}
										//}
									}
								} catch (Exception ex){}
							}
						});

						//int lastx = 9999999, lastz = 9999999;


					}
					/*
					List<Entity> entities = world.getEntities();
					for (int i = 0;i<entities.size();i++){
						Entity e = entities.get(i);
						//e instanceof Vehicle = pig
						if (e instanceof org.bukkit.entity.Item || e instanceof Player || e instanceof ExperienceOrb || e instanceof FallingSand || e instanceof Painting) continue;
						if (e instanceof Vehicle && !(e instanceof Pig)) continue;
						boolean blocked = false;
						for (Class cl : TRConfigCache.Threads.SSClassBypasses){
							if (cl.isInstance(e)){
								blocked = true;
								break;
							}
						}
						if (blocked) continue;

						if (!TRSafeZone.getSafeZoneByLocation(e.getLocation(), true).isEmpty()) {
							tbr.add(e);
						}
					}
					 */
				} catch (Exception ex){
					if (!err1){
						Warning.other("An error occurred in the entities Disabler thread! (this error will only be logged once)", false);
						Log.Exception(ex, false);
						err1 = true;
					}
					//Entities list probably modified while iterating over it.
				}
			}
		} catch (Exception ex){}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(Threads.SSEntityRemoverSpeed);
		} catch (InterruptedException e) {
			if (TekkitRestrict.disable) return; //If plugin is disabling, then stop the thread. The EntityRemoveThread shouldn't trigger again.
		}
		while (true) {
			try {
				disableEntities();
			} catch (Exception ex) {
				Warning.other("An error occurred trying to disable entities!", false);
				Log.Exception(ex, false);
			}

			try {
				Thread.sleep(Threads.SSEntityRemoverSpeed);
			} catch (InterruptedException e) {
				if (TekkitRestrict.disable) break; //If plugin is disabling, then stop the thread. The EntityRemoveThread shouldn't trigger again.
			}
		}
	}
}
