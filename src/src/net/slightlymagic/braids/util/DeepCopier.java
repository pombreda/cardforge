package net.slightlymagic.braids.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Creates a deep copy of any Serializable object.
 */
public class DeepCopier<T extends Serializable> { 
	
	public static final MessageDigest SHA_256;
	static {
		try {
			SHA_256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public DeepCopier() { ; }

	/**
	 * Static version of copy.
	 * @param obj the object to copy
	 * @return a deep copy of obj
	 * @see #copy
	 */
	public static Object staticCopy(Serializable obj) {
		return staticCopyHelper(null, obj);
	}
	
	/**
	 * Like staticCopy, but also provides an SHA-256 digest.  Example usage:
	 * 
 	 * <pre>
	 * byte[][] digestOut = new byte[1][];
	 * Object clone = staticCopyWithDigest(digestOut, obj);
	 * byte[] digest = digestOut[0];
	 * </pre>
	 *
	 * @param digestOut  output parameter; an array of length 1.
	 * 
	 * @param obj the object to copy
	 * 
	 * @return a deep copy of obj
	 */
	public static Object staticCopyWithDigest(byte[][] digestOut, 
			Serializable obj) 
	{
		return staticCopyHelper(digestOut, obj);
	}
	
	protected static Object staticCopyHelper(byte[][] digestOut, 
			Serializable obj) 
	{

		ByteArrayOutputStream byteArrayOutStream = null; 
		ObjectOutputStream objectOutStream = null; 
		ByteArrayInputStream byteArrayInStream = null; 
		ObjectInputStream objectInStream = null; 
		Object result = null;

		try {
			byteArrayOutStream = new ByteArrayOutputStream(); 
			objectOutStream = new ObjectOutputStream(byteArrayOutStream); 
			objectOutStream.writeObject(obj); 
			objectOutStream.close(); 
			byte[] byteArray = byteArrayOutStream.toByteArray();

			if (digestOut != null) {
				if (digestOut.length != 1) {
					throw new IllegalArgumentException("digestOut's length must be 1");
				}
				
				digestOut[0] = SHA_256.digest(byteArray);
			}
			
			byteArrayInStream = new ByteArrayInputStream(byteArray); 
			objectInStream = new ObjectInputStream(byteArrayInStream);
			result = objectInStream.readObject();
		}
		catch (IOException exn) {
			// This is very rare. I am not sure if the above code throws this
			// if it runs out of memory; I would hope it to throw 
			// OutOfMemoryError instead.			
			throw new RuntimeException(exn);
			
		} catch (ClassNotFoundException exn) {
			// I think this is actually impossible, given that all the classes
			// in obj already exist.
			throw new RuntimeException(exn);
		}
		finally {
			// I'm not sure if I need to do this, since none of these have
			// external resources (i.e., no file-handles), but it's a habit.
			try { byteArrayOutStream.close(); } catch (Exception ignored) { ; }
			try { objectOutStream.close(); } catch (Exception ignored) { ; }
			try { byteArrayInStream.close(); } catch (Exception ignored) { ; }
			try { objectInStream.close(); } catch (Exception ignored) { ; }
		}

		return result;
	}

	/**
	 * Slow, but effective means of deeply copying an object that preserves
	 * duplicate and circular references.
	 * 
	 * @param obj  the object to copy
	 * @return an object guaranteed to have the same type as obj
	 */
	@SuppressWarnings("unchecked")
	public T copy(T obj) 
	{ 
		return (T) DeepCopier.staticCopy(obj);
	}
	
	
	/**
	 * @see #staticCopyWithDigest(byte[][], Serializable)
	 */
	@SuppressWarnings("unchecked")
	public T copy(byte[][] digestOut, T obj) {
		return (T) DeepCopier.staticCopyWithDigest(digestOut, obj);
	}
	
}
