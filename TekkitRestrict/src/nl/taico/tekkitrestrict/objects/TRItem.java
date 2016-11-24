package nl.taico.tekkitrestrict.objects;

import javax.annotation.Nullable;

import lombok.NonNull;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TRItem {
	private static String DEFAULT = ChatColor.RED +  "You are not allowed to modify/obtain this item!";
	//IMPORTANT does not check messages!
	public static boolean compare(final int id, final int data, final int mainId, final int mainData){
		return (id == mainId) && ((data == mainData) || (mainData == -1));// || (data == 0 && mainData == -10)); TODO change -10
	}
	//IMPORTANT does not check messages!
	public static boolean compare(final int id, final int data, @NonNull final TRItem mainItem){
		return (id == mainItem.id) && ((data == mainItem.data) || (mainItem.data == -1));// || (data == 0 && mainItem.data == -10)); TODO change -10
	}

	public static boolean compareNP(final TRItem item, final TRItem np){
		if (item.id != np.id) return false;

		return (item.data == np.data) || ((item.data == -10) && (np.data == 0)) || (np.data == -1);//:0 = :0, :-1 = :-1.
	}

	public static String defaultMessage(){
		return DEFAULT;
	}

	@NonNull public static TRItem parseItem(final int id, final int data) {
		return new TRItem(id, data);
	}

	@NonNull public static TRItem parseItem(final int id, final int data, @Nullable final String msg) {
		return new TRItem(id, data, msg);
	}

	public static void sendBannedMessage(@NonNull final Player player, @NonNull final String message){
		if (message.contains("\n")){
			final String temp[] = message.split("\n");
			for (String msg : temp) player.sendMessage(msg);
		} else {
			player.sendMessage(message);
		}
	}

	public int id;

	public int data;

	public String msg;

	public TRItem(){
		this.msg = "";
	}

	/*
	@Override
	public Object clone(){
		final TRItem ti = new TRItem();
		ti.id = this.id;
		ti.data = this.data;
		ti.msg = this.msg;
		return ti;
	}*/

	public TRItem(final int id){
		this.id = id;
		this.data = -1;
		this.msg = "";
	}

	public TRItem(final int id, final int data){
		this.id = id;
		this.data = data;
		this.msg = "";
	}

	public TRItem(final int id, final int data, final String msg){
		this.id = id;
		this.data = data;
		this.msg = msg;
	}

	@Override
	public TRItem clone(){
		return new TRItem(this.id, this.data, this.msg);
	}
	public TRItem cloneAndSetMsg(String msg){
		return new TRItem(this.id, this.data, msg);
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
	public boolean compare(final int id, final int data) {
		return (this.id == id) && ((this.data == data) || (this.data == -1));// || (data == 0 && this.data == -10)); TODO change -10
	}

	//Two TRItems equal if their id's and data values are equal.
	//Their message is ignored in this comparison.
	@Override
	public boolean equals(Object obj){
		if (obj == this) return false;
		if (!(obj instanceof TRItem)) return false;
		final TRItem tri = (TRItem) obj;

		return (tri.id == id) && (tri.data == data);
	}
	//This hashcode is best suited for blocks, as they have a max data value of 15.
	//This means that the chance that 2 different id:data combinations give the same
	//hashcode result, is smaller.
	@Override
	public int hashCode(){
		return (17 * (17 + id)) + data;
	}

	/** @return A string representation of this Cache Item: "id:data" */
	@Override
	public String toString() {
		return new StringBuilder(12).append(id).append(':').append(data).toString();
	}
}
