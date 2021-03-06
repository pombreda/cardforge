package net.slightlymagic.braids.util.generator;

import com.google.code.jyield.Generator;
import com.google.code.jyield.Yieldable;

/**
 * Creates a Generator from an array; generators are a handy 
 * substitute for passing around and creating temporary
 * lists, collections, and arrays.
 * 
 * @see http://code.google.com/p/jyield/
 */
public class GeneratorFromArray<T> implements Generator<T> {
	private T[] array;

	/**
	 * Create a Generator from an array
	 * 
	 * @param array  from which to generate items
	 */
	public GeneratorFromArray(T[] array) {
		this.array = array;
	}

	@Override
	/**
	 * Submits all of the array's elements to the yieldable.
	 * 
	 * @param yy  the yieldable which receives the elements
	 */
	public void generate(Yieldable<T> yy) {
		for (T item : array) {
			yy.yield(item);
		}
	}
}
