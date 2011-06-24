package net.slightlymagic.braids.util.lambda;

/**
 * This embodies a promise to invoke a certain method at a later time; the 
 * FrozenCall remembers the arguments to use.
 * 
 * @deprecated
 * @see FrozenCall
 */
public class FrozenCallVoid implements ThunkVoid {
	private LambdaVoid proc;
	private Object[] args;

	public FrozenCallVoid(LambdaVoid proc, Object[] args) {
		this.proc = proc;
		this.args = args;
	}
	
	public void apply() {
		proc.apply(args);
	}
}
