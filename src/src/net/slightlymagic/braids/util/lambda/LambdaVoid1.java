package net.slightlymagic.braids.util.lambda;

/**
 * @deprecated
 * @see Lamdba1
 */
public abstract class LambdaVoid1<A1> implements LambdaVoid {

	public abstract void apply(A1 arg1);

	@SuppressWarnings("unchecked")
	//TODO @Override
	public void apply(Object[] args) {
		apply((A1) args[0]);
	}

}
