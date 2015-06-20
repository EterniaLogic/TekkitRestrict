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
				Warning.config("You have an error in your Advanced.config.yml in RecipeBlock:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
			for (TRItem ir : iss) {
				try {
					blockRecipeVanilla(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Exception in '+TRRecipeBLock.blockRecipeVanilla(id:int, data:int):boolean'! Error: " + ex.toString(), false);
				}
				try {
					blockRecipeForge(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Exception in '+TRRecipeBLock.blockRecipeForge(id:int, data:int):boolean'! Error: " + ex.toString(), false);
				}
			}
		}

		ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeFurnaceBlock");
		for (String s : ssr) {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in RecipeFurnaceBlock:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
			for (TRItem ir : iss) {
				try {
					blockFurnaceRecipe(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Exception in '+TRRecipeBLock.blockFurnaceRecipe(id:int, data:int):boolean'! Error: " + ex.toString(), false);
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
		@SuppressWarnings("unchecked")
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
