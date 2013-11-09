package nl.taico.tekkitrestrict.functions;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.FurnaceRecipes;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRRecipeBlock {
	public static void reload() {
		blockConfigRecipes();
	}

	public static void blockConfigRecipes() {
		List<String> ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeBlock");
		for (String s : ssr) {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in RecipeBlock:");
				Warning.config(ex.getMessage());
				continue;
			}
			for (TRItem ir : iss) {
				try {
					blockRecipeVanilla(ir.id, ir.data);
				} catch (Exception e) {
					Warning.other("Error! [TRRecipe-RecipeBlockVanilla] " + e.getMessage());
				}
				try {
					blockRecipeForge(ir.id, ir.data);
				} catch (Exception e) {
					Warning.other("Error! [TRRecipe-RecipeBlockForge] " + e.getMessage());
				}
			}
		}

		ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeFurnaceBlock");
		for (String s : ssr) {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in RecipeFurnaceBlock:");
				Warning.config(ex.getMessage());
				continue;
			}
			for (TRItem ir : iss) {
				try {
					blockFurnaceRecipe(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Error! [TRRecipe-Furnace Block] " + ex.getMessage());
				}
			}
		}
	}

	public static boolean blockRecipeVanilla(int id, int data) {
		boolean status = false;
		Iterator<Recipe> recipes = Bukkit.recipeIterator();
		Recipe recipe;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) != null) {
				int tid = recipe.getResult().getTypeId();//was .getData().getItemTypeId();
				int tdata = recipe.getResult().getDurability();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove();
					status = true;
				}
			}
		}

		return status;
	}

	public static boolean blockRecipeForge(int id, int data) {
		boolean status = false;
		// loop through recipes...
		List<Object> recipes = CraftingManager.getInstance().recipies;

		for (int i = 0; i < recipes.size(); i++) {
			Object r = recipes.get(i);
			if (r instanceof ShapedRecipe) {
				ShapedRecipe recipe = (ShapedRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getDurability();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove(i);
					i--;
					status = true;
				}
			}
			if (r instanceof ShapelessRecipe) {
				ShapelessRecipe recipe = (ShapelessRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getDurability();
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
