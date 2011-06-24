package net.slightlymagic.braids.util;

import java.util.Comparator;

public class ToStringComparator<T extends Object> implements Comparator<T> {

	// Default constructor omitted.
	
	//TODO @Override
	public int compare(T arg0, T arg1) {
		if (arg0 == null) {
			if (arg1 == null)  return 0;
			else  return +1;
		}
		if (arg1 == null)  return -1;  // because arg0 != null.
		
		String arg0String = arg0.toString();
		String arg1String = arg1.toString();

		if (arg0String == null) {
			if (arg1String == null)  return 0;
			else  return +1;
		}
		if (arg1String == null)  return -1;  // because arg0String != null.
		
		return arg0String.compareTo(arg1String);
	}
}
