package nl.taico.tekkitrestrict.functions;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.ItemStack;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.objects.TRItem;

public class TRRecipeBlock {
	public static int recipesSize, furnaceSize;
	public static void reload() {
		blockConfigRecipes();
	}

	public static void blockConfigRecipes() {
		recipesSize = 0;
		List<String> ssr = SettingsStorage.advancedConfig.getStringList("RecipeBlock");
		Log.trace("Loading Disabled Recipes...");
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
					if (!blockCraftingRecipe(ir.id, ir.data)) Warning.other("Unable to block crafting recipe for "+ir+": There is no recipe for this item.", false);
				} catch (Exception ex) {
					Warning.other("Unable to block crafting recipe for "+ir+": Exception: " + ex.toString(), false);
				}
			}
		}

		ssr = SettingsStorage.advancedConfig.getStringList("RecipeFurnaceBlock");
		Log.trace("Loading Disabled Furnace Recipes...");
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
					if (!blockFurnaceRecipe(ir.id, ir.data)) Warning.other("Unable to block furnace recipe for "+ir+": There is no smelting recipe for this item.", false);
				} catch (Exception ex) {
					Warning.other("Exception in TRRecipeBlock.blockFurnaceRecipe! Error: " + ex.toString(), false);
				}
			}
		}
	}

	public static boolean blockCraftingRecipe(int id, int data) {
		Log.trace("Disabling recipes for "+id+":"+data+"...");
		boolean status = false;
		// loop through recipes...
		final Iterator<CraftingRecipe> recipes = CraftingManager.getInstance().getRecipies().iterator();

		while (recipes.hasNext()) {
			final CraftingRecipe recipe = recipes.next();
			if (recipe == null) continue;
			final net.minecraft.server.ItemStack result = recipe.b();
			if (result == null) continue;
			if (result.id == id && (data == -1 || result.getData() == data)){// || (data == -10 && result.getData() == 0))) { TODO change -10
				recipes.remove();
				status = true;
			}
		}
		return status;
	}
	private static Field meta;
	
	@SuppressWarnings("rawtypes")
	public static boolean blockFurnaceRecipe(int id, int data) {
		Log.trace("Disabling furnace recipes for "+id+":"+data+"...");
		if (meta == null){
			try {
				meta = FurnaceRecipes.class.getField("metaSmeltingList");
				if (meta == null) return false;
				
				if (!meta.isAccessible()) meta.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException ex) {
				return false;
			}
		}
		
		boolean a = FurnaceRecipes.getInstance().getRecipies().containsKey(id);
		boolean b = false;
		
		try {
			Object obj = meta.get(FurnaceRecipes.getInstance());
			if (obj instanceof Map){
				final Iterator<Entry<List<Integer>, net.minecraft.server.ItemStack>> it = ((Map) obj).entrySet().iterator();
				while (it.hasNext()){
					final Entry<List<Integer>, ItemStack> e = it.next();
					if (id != e.getKey().get(0).intValue()) continue;
					
					if (data == -1 || data == e.getKey().get(1)) {
						it.remove();
						b = true;
					}
				}
				
				if ((!b || data == -1) && a){
					FurnaceRecipes.getInstance().getRecipies().remove(id);
					b = true;
				}
				
				return b;
			} else {
				return false;
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			return false;
		}
	}
}
