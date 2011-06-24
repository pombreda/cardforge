package net.slightlymagic.braids.util.lambda;

/**
 * @deprecated
 * @see Lamdba3
 */
public abstract class LambdaVoid3<A1,A2,A3> implements LambdaVoid {

	public abstract void apply(A1 arg1, A2 arg2, A3 arg3);

	@SuppressWarnings("unchecked")
	//TODO @Override
	public void apply(Object[] args) {
		apply((A1) args[0], (A2) args[1], (A3) args[2]);
	}

}
