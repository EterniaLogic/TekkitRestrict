package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.SQLite;

public class TRSQLDB {
	private SQLite db=null;
	private ExecutorService s = Executors.newFixedThreadPool(5);
	private Queue<String> qs = new java.util.concurrent.ConcurrentLinkedQueue<String>();
	public TRSQLDB(Logger log, String schema, String Table, String file){
		db = new SQLite(log, schema, Table, file);
	}
	
	ResultSet prev = null;
	public ResultSet query(String ins) throws SQLException{
		if(prev != null){
			if(!prev.isClosed())
				prev.close();
		}
		dbCall dc = new dbCall();
		dc.tocall = ins;
		Future<ResultSet> r = s.submit(dc);
		return db.query(ins);
	}
	
	public void open(){
		db.open();
	}
	
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
