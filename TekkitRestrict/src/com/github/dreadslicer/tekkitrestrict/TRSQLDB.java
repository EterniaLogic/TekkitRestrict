package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.dreadslicer.tekkitrestrict.database.SQLite;

public class TRSQLDB {
	private SQLite db = null;
	private ExecutorService s = Executors.newFixedThreadPool(5);
	
	public TRSQLDB(String name, String location){
		db = new SQLite(name, location);
	}
	
	public ResultSet query(String query) throws SQLException{
		return db.query(query);
	}
	
	//ResultSet prev = null;
	public ResultSet query_future(String ins) throws SQLException{
		//if(prev != null && !prev.isClosed()){
		//	prev.close();
		//}
		dbCall dc = new dbCall();
		dc.tocall = ins;
		@SuppressWarnings({ "unused", "unchecked" })
		Future<ResultSet> r = s.submit(dc);
		return db.query(ins);
	}
	
	public void open(){
		db.open();
	}
	
	@SuppressWarnings("rawtypes")
	public class dbCall implements Callable {
		/**
		 * @return future called object.
		 */
		public ResultSet call() throws Exception {
			return db.query(tocall);
		}
		public String tocall;
	}
}
