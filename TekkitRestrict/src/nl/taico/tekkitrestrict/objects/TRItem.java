package nl.taico.tekkitrestrict.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.annotations.Safe;

public class TRItem {
	public int id;
	public int data;
	public String msg = "";
	
	@NonNull public static TRItem parseItem(int id, int data, @Nullable String msg) {
		TRItem item = new TRItem();
		item.id = id;
		item.data = data;
		item.msg = msg;
		return item;
	}
	
	@NonNull public static TRItem parseItem(int id, int data) {
		TRItem item = new TRItem();
		item.id = id;
		item.data = data;
		return item;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (!(obj instanceof TRItem)) return false;
		TRItem tri = (TRItem) obj;
		if (tri.id == id && tri.data == data) return true;
		return false;
	}
	
	/** @return A string representation of this Cache Item: "id:data" */
	@Override
	public String toString() {
		return new StringBuilder(12).append(id).append(":").append(data).toString();
	}
	
	@Override
	public Object clone(){
		TRItem ti = new TRItem();
		ti.id = this.id;
		ti.data = this.data;
		ti.msg = this.msg;
		return ti;
	}
	
	public static boolean compareNP(@NonNull TRItem item, @NonNull TRItem np){
		if (item.id != np.id) return false;
		
		if (item.data == np.data || (item.data == -10 && np.data == 0)) return true;//:0 = :0, :-1 = :-1.
		if (np.data == -1) return true;
		
		return false;
	}
	
	/**
	 * Compare this TRItem with the given id and data
	 * @return True if:<br>
	 * <ul>
	 * <li>this.id == id AND this.data == data</li>
	 * <li>this.id == id AND this.data == -1</li>
	 * <li>this.id == id AND data == 0 AND this.data == -10</li>
	 * </ul>
	 */
	@Safe
	public boolean compare(int id, int data) {
		return this.id == id && (this.data == data || this.data == -1 || (data == 0 && this.data == -10));
	}
	//IMPORTANT does not check messages!
	public static boolean compare(int id, int data, @NonNull TRItem mainItem){
		return id == mainItem.id && (data == mainItem.data || mainItem.data == -1 || (data == 0 && mainItem.data == -10));
	}
	//IMPORTANT does not check messages!
	public static boolean compare(int id, int data, int mainId, int mainData){
		return id == mainId && (data == mainData || mainData == -1 || (data == 0 && mainData == -10));
	}
	
	@NonNull public static String defaultMessage(){
		return ChatColor.RED + "You are not allowed to modify/obtain this item!";
	}
	
	public static void sendBannedMessage(@NonNull Player player, @NonNull String message){
		if (message.contains("\n")){
			String temp[] = message.split("\n");
			for (String msg : temp) player.sendMessage(msg);
		} else {
			player.sendMessage(message);
		}
	}
}
