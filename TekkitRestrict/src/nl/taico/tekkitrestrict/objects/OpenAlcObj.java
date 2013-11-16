package nl.taico.tekkitrestrict.objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ee.AlchemyBagData;

public class OpenAlcObj {
	private AlchemyBagData bag;
	private Player bagOwner;
	private Player viewer;
	private String viewerName;
	private String bagOwnerName;
	private static ArrayList<OpenAlcObj> allOpenAlcs = new ArrayList<OpenAlcObj>();
	
	public OpenAlcObj(@NonNull AlchemyBagData bag, @NonNull Player bagOwner, @NonNull Player viewer){
		this.bag = bag;
		this.bagOwner = bagOwner;
		this.bagOwnerName = bagOwner.getName();
		this.viewer = viewer;
		this.viewerName = viewer.getName();
		
		allOpenAlcs.add(this);
	}
	
	@NonNull public String getBagOwnerName(){
		return bagOwnerName;
	}
	@NonNull public String getViewerName(){
		return viewerName;
	}
	@NonNull public Player getViewer(){
		return viewer;
	}
	@NonNull public Player getBagOwner(){
		return bagOwner;
	}
	@NonNull public AlchemyBagData getBag(){
		return bag;
	}
	
	@Nullable public static OpenAlcObj getOpenAlcByOwner(@NonNull String owner){
		for (OpenAlcObj current : allOpenAlcs){
			if (current.bagOwnerName.equalsIgnoreCase(owner)) return current;
		}
		return null;
	}
	
	@Nullable public static OpenAlcObj getOpenAlcByViewer(@NonNull String viewer){
		for (OpenAlcObj current : allOpenAlcs){
			if (current.viewerName.equalsIgnoreCase(viewer)) return current;
		}
		return null;
	}
	
	public static boolean isViewing(@NonNull String player){
		if (getOpenAlcByViewer(player) != null) return true;
		return false;
	}
	
	public static boolean isViewed(@NonNull String player){
		if (getOpenAlcByOwner(player) != null) return true;
		return false;
	}

	
	public void delete() {
		allOpenAlcs.remove(this);
		this.viewer = null;
		this.bagOwner = null;
		this.bag = null;
	}
}
