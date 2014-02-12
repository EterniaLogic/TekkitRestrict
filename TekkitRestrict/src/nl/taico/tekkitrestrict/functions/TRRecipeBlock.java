package nl.taico.tekkitrestrict.functions;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.FurnaceRecipes;

import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRRecipeBlock {
	public static int recipesSize, furnaceSize;
	public static void reload() {
		blockConfigRecipes();
	}

	public static void blockConfigRecipes() {
		recipesSize = 0;
		List<String> ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeBlock");
		for (final String s : ssr) {
			final List<TRItem> iss;
			try {
				iss = TRItemProcessor2.processString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in RecipeBlock:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
			for (final TRItem ir : iss) {
				recipesSize++;
				try {
					blockCraftingRecipe(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Exception in TRRecipeBLock.blockRecipeForge! Error: " + ex.toString(), false);
				}
			}
		}

		ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeFurnaceBlock");
		for (final String s : ssr) {
			final List<TRItem> iss;
			try {
				iss = TRItemProcessor2.processString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in RecipeFurnaceBlock:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
			for (final TRItem ir : iss) {
				furnaceSize++;
				try {
					blockFurnaceRecipe(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Exception in TRRecipeBLock.blockFurnaceRecipe(id:int, data:int)! Error: " + ex.toString(), false);
				}
			}
		}
	}

	public static boolean blockCraftingRecipe(int id, int data) {
		boolean status = false;
		// loop through recipes...
		final Iterator<CraftingRecipe> recipes = CraftingManager.getInstance().getRecipies().iterator();

		while (recipes.hasNext()) {
			final CraftingRecipe recipe = recipes.next();
			final net.minecraft.server.ItemStack result;
			if ((result = recipe.b()).id == id && (result.getData() == data || data == 0)) {
				recipes.remove();
				status = true;
			}
		}
		return status;
	}

	public static boolean blockFurnaceRecipe(int id, int data) {
		FurnaceRecipes.getInstance().addSmelting(id, data, null);
		FurnaceRecipes.getInstance().registerRecipe(id, null);
		return true;
	}
}
