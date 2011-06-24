package net.slightlymagic.braids.util;

/**
 * Can be used as keys in a Map; has very, very low overhead compared to
 * List<Byte>.
 * 
 * I tried templating this, but the only way was to have the array's type 
 * be Byte[] instead of byte[].  I think that takes up a lot more space.
 * 
 * This also has a hashCode function that relies only on the contents of the
 * array (instead of the array's location in memory). 
 */
public final class ComparableByteArray 
	implements java.lang.Comparable<ComparableByteArray> 
{
	
	private byte[] array;

	public ComparableByteArray(byte[] contents) {
		this.setArray(contents);
	}

	public void setArray(byte[] array) {
		this.array = array;
	}

	public byte[] getArray() {
		return array;
	}
	
	public int hashCode() {
		if (array != null && array.length > 1) {
			int result = 0;
			for (int i = 0; i < array.length; i++) {

				result = Integer.rotateLeft(result, 8);
				result ^= array[i];
			}
			
			return result;
		}
		else {
			return 0;
		}
	}
	
	public boolean equals(ComparableByteArray that) {
		return (compareTo(that) == 0);
	}
	
	public boolean equals(byte[] otherArray) {
		ComparableByteArray that = new ComparableByteArray(otherArray);
		return (compareTo(that) == 0);
	}

	//TODO @Override
	/**
	 * This is not in lexicographic order (EXCEPT for arrays of equal length),
	 * but it is consistent enough for use as keys in Map instances.
	 * 
	 * @return a negative number if this < that, 0 if they are equal, and
	 *  a positive number if this > that.
	 */
	public int compareTo(ComparableByteArray that) {
		if (that == null)  return +1;
		else if (this == that)  return 0;
		else if (this.array == that.array) return 0;
		else if (that.array == null)  return +1;
		else if (this.array.length < that.array.length) return -1;
		else if (this.array.length > that.array.length) return +1;
		
		for (int i = 0; i < this.array.length; i++) {
			if (this.array[i] < that.array[i]) return -1;
			if (this.array[i] > that.array[i]) return 1;
		}
		
		return 0;
	}
	
}
