package nl.taico.tekkitrestrict.objects.itemprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.taico.tekkitrestrict.objects.TRItem;

public class TRMod {
	public String mainName;
	public List<String> names;
	private List<TRItem> items;
	public TRMod(List<String> names, List<Integer> items){
		this.mainName = names.get(0);
		this.names = names;
		this.items = parseItems(items);
	}
	
	public TRMod(String[] names, List<Integer> items){
		this.mainName = names[0];
		this.names = Arrays.asList(names);
		this.items = parseItems(items);
	}
	
	public TRMod(String name, List<TRItem> items){
		this.mainName = name;
		this.names = new ArrayList<String>(1);
		this.names.add(name);
		this.items = items;
	}
	
	public List<TRItem> parseItems(List<Integer> ints){
		final List<TRItem> tbr = new ArrayList<TRItem>(ints.size());
		for (Integer i : ints) tbr.add(new TRItem(i));
		return tbr;
	}
	
	public boolean is(String mod){
		for (String name : names) if (name.equalsIgnoreCase(mod)) return true;
		
		return false;
	}
	
	public List<TRItem> getItemsNoCopy(){
		return items;
	}
	
	public List<TRItem> getItems(){
		final List<TRItem> tbr = new ArrayList<TRItem>(items.size());
		for (TRItem i : items) tbr.add(i.clone());
		return tbr;
	}
	
	public List<TRItem> getItems(String msg){
		final List<TRItem> tbr = new ArrayList<TRItem>(items.size());
		for (TRItem i : items) tbr.add(i.cloneAndSetMsg(msg));
		return tbr;
	}
}
