package com.github.dreadslicer.tekkitrestrict;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.FurnaceRecipes;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class TRRecipeBlock {
	public static void reload() {
		blockConfigRecipes();
	}

	public static void blockConfigRecipes() {
		// config
		List<String> ssr = tekkitrestrict.config.getStringList("RecipeBlock");
		for (String s : ssr) {
			List<TRCacheItem> iss = TRCacheItem.processItemString("", s, -1);
			for (TRCacheItem ir : iss) {
				try {
					blockRecipeVanilla(ir.id, ir.getData());
				} catch (Exception e) {
					TRLogger.Log(
							"debug",
							"Error! [TRRecipe-RecipeBlockVanilla] "
									+ e.getMessage());
				}
				try {
					blockRecipeForge(ir.id, ir.getData());
				} catch (Exception e) {
					TRLogger.Log("debug", "Error! [TRRecipe-RecipeBlockForge] "
							+ e.getMessage());
				}
			}
		}

		ssr = tekkitrestrict.config.getStringList("RecipeFurnaceBlock");
		for (String s : ssr) {
			List<TRCacheItem> iss = TRCacheItem.processItemString("", s, -1);
			for (TRCacheItem ir : iss) {
				try {
					blockFurnaceRecipe(ir.id, ir.getData());
				} catch (Exception e) {
					TRLogger.Log("debug", "Error! [TRRecipe-Furnace Block] "
							+ e.getMessage());
				}
			}
		}
	}

	public static boolean blockRecipeVanilla(int id, int data) {
		boolean status = false;
		Iterator<org.bukkit.inventory.Recipe> recipes = Bukkit.recipeIterator();
		org.bukkit.inventory.Recipe recipe;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) != null) {
				int tid = recipe.getResult().getData().getItemTypeId();
				int tdata = recipe.getResult().getData().getData();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove();
					status = true;
				}
			}
		}

		return status;
	}

	@SuppressWarnings("unchecked")
	public static boolean blockRecipeForge(int id, int data) {
		boolean status = false;
		// loop through recipes...
		List<Object> recipes = CraftingManager.getInstance().recipies;

		for (int i = 0; i < recipes.size(); i++) {
			Object r = recipes.get(i);
			if (r instanceof ShapedRecipe) {
				ShapedRecipe recipe = (ShapedRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getData().getData();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove(i);
					i--;
					status = true;
				}
			}
			if (r instanceof ShapelessRecipe) {
				ShapelessRecipe recipe = (ShapelessRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getData().getData();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove(i--);
					status = true;
				}
			}
		}
		return status;
	}

	public static boolean blockFurnaceRecipe(int id, int data) {
		boolean status = false;
		FurnaceRecipes.getInstance().addSmelting(id, data, null);
		FurnaceRecipes.getInstance().recipies.remove(id);
		return status;
	}
}
