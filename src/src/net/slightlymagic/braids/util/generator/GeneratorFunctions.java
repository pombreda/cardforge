/** Licensed under both the GPL and the Apache 2.0 License. */
package net.slightlymagic.braids.util.generator;

import net.slightlymagic.braids.util.lambda.Lambda1;

import com.google.code.jyield.Generator;
import com.google.code.jyield.YieldUtils;
import com.google.code.jyield.Yieldable;

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

	/**
	 * Highly efficient means of filtering a long or infinite sequence.
	 * 
	 * @param <T> any type
	 * 
	 * @param predicate a Lambda (function) whose apply method takes an object 
	 * of type <T> and returns a Boolean. If it returns false or null, the 
	 * item from the inputGenerator is not yielded by this Generator;
	 * if predicate.apply returns true, then this Generator <i>does</i>
	 * yield the value. 
	 * 
	 * @param inputGenerator the sequence upon which we operate
	 * 
	 * @return a generator which produces a subset <= the inputGenerator
	 */
	public static <T> Generator<T> filterGenerator(
			final Lambda1<Boolean,T> predicate, final Generator<T> inputGenerator) 
	{
		Generator<T> result = new Generator<T>() {

			@Override
			public void generate(final Yieldable<T> outputYield) {

				Yieldable<T> inputYield = new Yieldable<T>() {
					Boolean pResult;
					
					@Override
					public void yield(T input) {
						pResult = predicate.apply(input); 
						if (pResult != null && pResult) {
							outputYield.yield(input);
						}
					}
				};
				
				inputGenerator.generate(inputYield);
			}
			
		};
		
		return result;
	}

	/**
	 * Highly efficient means of applying a transform to a long or infinite 
	 * sequence.
	 * 
	 * @param <T> any type
	 * 
	 * @param transform a Lambda (function) whose apply method takes an object 
	 * of type <T> and returns an object of the same type. This transforms
	 * the values from the inputGenerator into this Generator.
	 * 
	 * @param inputGenerator the sequence upon which we operate
	 * 
	 * @return a generator that yields transform.apply's return value for
	 * each item in the inputGenerator
	 */
	public static <T> Generator<T> transformGenerator(
			final Lambda1<T,T> transform, final Generator<T> inputGenerator) 
	{
		Generator<T> result = new Generator<T>() {

			@Override
			public void generate(final Yieldable<T> outputYield) {

				Yieldable<T> inputYield = new Yieldable<T>() {
					@Override
					public void yield(T input) {
						outputYield.yield(transform.apply(input));
					}
				};
				
				inputGenerator.generate(inputYield);
			}
			
		};
		
		return result;
	}
}
