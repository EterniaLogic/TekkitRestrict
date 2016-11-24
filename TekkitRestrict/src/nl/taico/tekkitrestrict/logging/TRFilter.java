package nl.taico.tekkitrestrict.logging;

import java.util.HashSet;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TRFilter implements Filter {
	public enum Priority {
		/**
		 * Gets it first
		 */
		LOW,
		/**
		 * Gets it second
		 */
		NORMAL,
		/**
		 * Gets it last
		 */
		HIGH
	}
	
	public TRFilter(){
		l = false;
		n = false;
		h = false;
	}
	
	public TRFilter(Filter old){
		if (old != null) {
			addFilter(old, Priority.LOW);
		} else {
			l = false;
			n = false;
			h = false;
		}
	}
	
	public TRFilter(Filter old, Priority priority){
		if (old != null) {
			addFilter(old, priority);
		} else {
			l = false;
			n = false;
			h = false;
		}
	}
	
	private HashSet<Filter> filters_l = new HashSet<>(2);
	private HashSet<Filter> filters_n = new HashSet<>(2);
	private HashSet<Filter> filters_h = new HashSet<>(2);
	
	private boolean l, n, h;
	
	protected void recalculate(){
		l = filters_l.isEmpty();
		n = filters_n.isEmpty();
		h = filters_h.isEmpty();
	}
	
	public void addFilter(Filter filter, Priority priority){
		removeFilter(filter);
		switch (priority){
			case LOW:
				filters_l.add(filter);
				break;
			case NORMAL:
				filters_n.add(filter);
				break;
			case HIGH:
				filters_h.add(filter);
				break;
			default:
				return;
		}
		recalculate();
	}
	
	public void removeAndConvert(Handler handler, Filter... filters){
		for (Filter f : filters) removeFilter(f);
		convert(handler);
	}
	
	public void removeFilter(Filter filter){
		boolean a = false;
		a |= filters_l.remove(filter);
		a |= filters_n.remove(filter);
		a |= filters_h.remove(filter);
		if (a) recalculate();
	}
	
	public void removeFilter(Filter filter, Priority priority){
		switch (priority){
			case LOW:
				if (!filters_l.remove(filter)) return;
				break;
			case NORMAL:
				if (!filters_n.remove(filter)) return;
				break;
			case HIGH:
				if (!filters_h.remove(filter)) return;
				break;
			default:
				return;
		}
		
		recalculate();
	}
	
	public boolean canConvert(){
		if (l && n) return filters_h.size() <= 1;
		if (l && h) return filters_n.size() <= 1;
		if (n && h) return filters_l.size() <= 1;
		return false;
	}
	
	public void convert(Handler handler){
		if (l && n && h) {
			handler.setFilter(null);
			return;
		} else if (l && n && filters_h.size() == 1){
			handler.setFilter(filters_h.iterator().next());
		} else if (l && h && filters_n.size() == 1){
			handler.setFilter(filters_n.iterator().next());
		} else if (n && h && filters_l.size() == 1){
			handler.setFilter(filters_l.iterator().next());
		} else {
			return;
		}
	}
	
	@Override
	public boolean isLoggable(LogRecord record) {
		if (!l){
			for (Filter f : filters_l){
				if (!f.isLoggable(record)) return false;
			}
		}
		
		if (!n){
			for (Filter f : filters_n){
				if (!f.isLoggable(record)) return false;
			}
		}
		
		if (!h){
			for (Filter f : filters_h){
				if (!f.isLoggable(record)) return false;
			}
		}
		
		return true;
	}
}
