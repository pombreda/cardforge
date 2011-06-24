package net.slightlymagic.braids.util;

import java.util.Comparator;

public class HashCodeComparator<T extends Object> implements Comparator<T> {

	// Default constructor omitted.
	
	//TODO @Override
	public int compare(T arg0, T arg1) {
		if (arg0 == null) {
			if (arg1 == null)  return 0;
			else  return +1;
		}
		if (arg1 == null)  return -1;  // because arg0 != null.
		
		int arg0Hash = arg0.hashCode();
		int arg1Hash = arg1.hashCode();

		if (arg0Hash < arg1Hash)  return -1;
		else if (arg0Hash > arg1Hash)  return +1;

		return 0;
	}

}
