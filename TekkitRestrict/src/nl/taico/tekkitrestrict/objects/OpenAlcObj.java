package nl.taico.tekkitrestrict.objects;

import java.util.ArrayList;

import javax.annotation.Nullable;

import lombok.NonNull;

import org.bukkit.entity.Player;

import ee.AlchemyBagData;

public class OpenAlcObj {
	@Nullable public static OpenAlcObj getOpenAlcByViewer(@NonNull final String viewer){
		for (final OpenAlcObj current : allOpenAlcs){
			if (current.viewerName.equalsIgnoreCase(viewer)) return current;
		}
		return null;
	}
	public static boolean isViewed(@NonNull final String player, final int color){
		for (final OpenAlcObj current : allOpenAlcs){
			if (current.bagOwnerName.equalsIgnoreCase(player) && (current.color == color)) return true;
		}
		return false;
	}
	private AlchemyBagData bag;
	private Player bagOwner;
	private Player viewer;
	private String viewerName;
	private String bagOwnerName;

	private int color;

	private static ArrayList<OpenAlcObj> allOpenAlcs = new ArrayList<OpenAlcObj>();
	public static boolean isViewing(@NonNull final String player){
		if (getOpenAlcByViewer(player) != null) return true;
		return false;
	}
	public OpenAlcObj(@NonNull final AlchemyBagData bag, @NonNull final Player bagOwner, @NonNull final Player viewer, final int color){
		this.bag = bag;
		this.bagOwner = bagOwner;
		this.bagOwnerName = bagOwner.getName();
		this.viewer = viewer;
		this.viewerName = viewer.getName();
		this.color = color;
		allOpenAlcs.add(this);
	}
	public void delete() {
		allOpenAlcs.remove(this);
		this.viewer = null;
		this.bagOwner = null;
		this.bag = null;
	}
	@NonNull public AlchemyBagData getBag(){
		return bag;
	}
	@NonNull public Player getBagOwner(){
		return bagOwner;
	}

	/*
	@Nullable public static OpenAlcObj getOpenAlcByOwner(@NonNull final String owner){
		for (final OpenAlcObj current : allOpenAlcs){
			if (current.bagOwnerName.equalsIgnoreCase(owner)) return current;
		}
		return null;
	}*/

	@NonNull public String getBagOwnerName(){
		return bagOwnerName;
	}

	public int getColor(){
		return this.color;
	}

	@NonNull public Player getViewer(){
		return viewer;
	}


	@NonNull public String getViewerName(){
		return viewerName;
	}
}
