package net.slightlymagic.braids.util.lambda;

/**
 * @deprecated
 * @see Lamdba2
 */
public abstract class LambdaVoid2<A1,A2> implements LambdaVoid {

	public abstract void apply(A1 arg1, A2 arg2);

	@SuppressWarnings("unchecked")
	//TODO @Override
	public void apply(Object[] args) {
		apply((A1) args[0], (A2) args[1]);
	}

}
