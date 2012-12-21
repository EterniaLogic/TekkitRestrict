package com.github.dreadslicer.tekkitrestrict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedList;
import java.util.List;

public class TRPerformance {
	public static void reload() {
		ServerThreads = tekkitrestrict.config.getInt("ServerThreads");
		MaxTicks = tekkitrestrict.config.getInt("MaxTicks");
	}

	public static void getThreadLag(org.bukkit.entity.Player p) {
		// java.lang.management.ThreadInfo ti = new ThreadInfo(null, 0, ti,
		// null, 0, 0, 0, 0, null);
		ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
		long[] threadIds = mxBean.getAllThreadIds();
		ThreadInfo[] threadInfos = mxBean.getThreadInfo(threadIds);

		File fss = new File("threadinfo.txt");
		try {
			fss.createNewFile();
			FileWriter fstream = new FileWriter(fss);
			BufferedWriter out = new BufferedWriter(fstream);

			float cputotal = 0;
			List<ThreadInfo> threadInfot = new LinkedList<ThreadInfo>();
			for (ThreadInfo threadInfo : threadInfos) {
				long cputime = ManagementFactory.getThreadMXBean()
						.getThreadCpuTime(threadInfo.getThreadId());
				cputotal += cputime;
				threadInfot.add(threadInfo);
			}

			// order threads manually
			boolean done = false;

			List<ThreadInfo> threadInfoz = new LinkedList<ThreadInfo>();
			while (!done) {
				if (threadInfot.size() == 0) {
					break;
				}
				ThreadInfo max = null;
				float maxcputime = 0;
				for (ThreadInfo threadInfo : threadInfot) {
					float cputime = ManagementFactory.getThreadMXBean()
							.getThreadCpuTime(threadInfo.getThreadId());
					if (max == null) {
						max = threadInfo;
						maxcputime = cputime;
					} else {
						if (cputime >= maxcputime) {
							max = threadInfo;
							maxcputime = cputime;
						}
					}
				}
				threadInfot.remove(max);
				threadInfoz.add(max);
			}

			for (ThreadInfo threadInfo : threadInfoz) {
				float cputime = ManagementFactory.getThreadMXBean()
						.getThreadCpuTime(threadInfo.getThreadId());
				// long cputime = ManagementFactory.getThreadMXBean().getT
				/*
				 * if(cputime > 0){
				 * p.sendRawMessage(threadInfo.getThreadName()+"-T:"
				 * +threadInfo.getBlockedTime()+"ms-CPU:"+cputime);
				 * 
				 * }
				 */
				if (cputime > 0) {
					// dump the rest into a file...
					out.write("Thread [" + threadInfo.getThreadId() + "] \""
							+ threadInfo.getThreadName() + "\"\n");
					out.write("  Blocket Count: "
							+ threadInfo.getBlockedCount() + " time:"
							+ threadInfo.getBlockedTime() + "\n");
					out.write("  CPU time: "
							+ String.format("%.2f",
									100.0f * (cputime / cputotal)) + "%\n");

					for (Thread t : Thread.getAllStackTraces().keySet()) {
						if (t.getId() == threadInfo.getThreadId()) {
							for (StackTraceElement eee : t.getStackTrace()) {
								out.write("    " + eee.toString() + "\n");
							}
						}
					}

				}
			}
			out.close();
			if (p != null) {
				p.sendRawMessage("File 'threadinfo.txt' generated at serverdir");
			} else {
				tekkitrestrict.log
						.info("File 'threadinfo.txt' generated at serverdir");
			}
		} catch (Exception e) {

		}
	}

	public static double getThreadTimeRatio() {
		return new Double(MaxTicks) / new Double(ServerThreads);
	}

	public static int ServerThreads, MaxTicks;
	public static int x, z;
	public static net.minecraft.server.Chunk chunk;
	public static net.minecraft.server.WorldServer wo;
	public static boolean didInit = false;
}