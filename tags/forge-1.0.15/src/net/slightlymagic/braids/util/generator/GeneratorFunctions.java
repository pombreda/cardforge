/** Licensed under both the GPL and the Apache 2.0 License. */
package net.slightlymagic.braids.util.generator;

import com.google.code.jyield.Generator;
import com.google.code.jyield.YieldUtils;

/**
 * For documentation on Java-Yield and its generators, see
 * {@link http://code.google.com/p/java-yield/}
 */
public final class GeneratorFunctions {

	/**
	 * Do not instantiate.
	 */
	private GeneratorFunctions() {
		;
	}

	/**
	 * Estimate the number of items in this generator by traversing all of its
	 * elements.
	 * 
	 * Note this only works on a generator that can be reinstantiated once it
	 * has been traversed.  This is only an estimate, because a generator's size 
	 * may vary been traversals. This is especially true if the generator 
	 * relies on external resources, such as a file system.
	 * 
	 * If you call this on an infinite generator, this method will never 
	 * return.
	 * 
	 * @return  the estimated number of items provided by this generator
	 */
	public static <T> long estimateSize(Generator<T> gen) {
		long result = 0;
		for (@SuppressWarnings("unused") T ignored : YieldUtils.toIterable(gen)) 
		{
			result++;
		}
		
		return result;
	}
}
