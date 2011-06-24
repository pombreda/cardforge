package net.slightlymagic.braids.util;

import static org.junit.Assert.*;

import static net.slightlymagic.braids.util.UtilFunctions.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

public class UtilFunctionsTest {

	@Test 
	public void test_checkNotNull() {
		String paramName = null;
		Object param = 1;
		
		checkNotNull(paramName, param);
		
		paramName = "parameter_name";
		checkNotNull(paramName, param);
	}

	@Test 
	public void test_checkNotNull_null_null() {
		NullPointerException actual = null;
		try {
			checkNotNull(null, null);
		}
		catch (NullPointerException exn) {
			actual = exn;
		}
		
		assertNotNull(actual);
		StackTraceElement elt = actual.getStackTrace()[0];
		assertEquals("Exception appears to come from this class", 
				elt.getClassName(), this.getClass().getName());
	}
	
	@Test 
	public void test_checkNotNull_String_null() {
		NullPointerException actual = null;
		try {
			checkNotNull("parameter_name", null);
		}
		catch (NullPointerException exn) {
			actual = exn;
		}
		
		assertNotNull(actual);
		StackTraceElement elt = actual.getStackTrace()[0];
		assertEquals("Exception appears to come from this class", 
				elt.getClassName(), this.getClass().getName());
	}
	
	@Test
	public void test_slice3() {
		String[] input = new String[] { "zero", "one", "two", "three", "four" };
		String message;
		String[] expected;
		int startIndex;
		
		expected = new String[] { "one", "two", "three", "four" };
		startIndex = 1;
		message = "positive index";
		helper_slice3(message, expected, input, startIndex);

		expected = new String[] { "four" };
		startIndex = -1;
		message = "negative index";
		helper_slice3(message, expected, input, startIndex);
		
		expected = new String[0];
		startIndex = 5;
		message = "positive after end";
		helper_slice3(message, expected, input, startIndex);
		
		
		expected = new String[] { "zero", "one", "two", "three", "four" };
		startIndex = -15;
		message = "negative before beginning";
		helper_slice3(message, expected, input, startIndex);
		
	}

	public void helper_slice3(String message, String[] expected,
			String[] input, int startIndex) throws ArrayComparisonFailure {
		String[] actual;
		actual = new String[getSliceLength(input, startIndex)];
		slice(actual, input, startIndex);
		assertArrayEquals(message, expected, actual);
	}
	
	@Test
	public void test_smartRemoveDuplicatesAndNulls_ArrayList() {
		System.out.println("Running test testSmartRemoveDuplicatesAndNulls_ArrayList:");
		ArrayList<Integer> actual = new ArrayList<Integer>(15);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(new Integer(9));
		actual.add(new Integer(2));
		actual.add(new Integer(3));
		actual.add(new Integer(4)); 
		actual.add(new Integer(4));
		actual.add(null);
		actual.add(new Integer(4));
		actual.add(new Integer(8));
		actual.add(new Integer(9));

		
		smartRemoveDuplicatesAndNulls(actual);
		
		ArrayList<Integer> expected = new ArrayList<Integer>(10);
		expected.add(new Integer(2));
		expected.add(new Integer(3));
		expected.add(new Integer(4));
		expected.add(new Integer(8));
		expected.add(new Integer(9));

		assertEquals("length", expected.size(), actual.size());
		
		for (Object obj : expected) {
			String message = "contains " + obj;
			assertTrue(message, actual.contains(obj));
			System.out.println(message);
		}
		
		String message2 = "does not contain null";
		assertFalse(message2, actual.contains(null));
		System.out.println(message2);
	}

	@Test
	public void test_smartRemoveDuplicatesAndNulls_LinkedList() {
		System.out.println("Running test testSmartRemoveDuplicatesAndNulls_LinkedList");
		LinkedList<Integer> actual = new LinkedList<Integer>();
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(null);
		actual.add(new Integer(9));
		actual.add(new Integer(2));
		actual.add(new Integer(3));
		actual.add(new Integer(4)); 
		actual.add(new Integer(4));
		actual.add(null);
		actual.add(new Integer(4));
		actual.add(new Integer(8));
		actual.add(new Integer(9));

		
		smartRemoveDuplicatesAndNulls(actual);
		
		LinkedList<Integer> expected = new LinkedList<Integer>();
		expected.add(new Integer(2));
		expected.add(new Integer(3));
		expected.add(new Integer(4));
		expected.add(new Integer(8));
		expected.add(new Integer(9));

		assertEquals("length", expected.size(), actual.size());
		
		for (Object obj : expected) {
			String message = "contains " + obj;
			assertTrue(message, actual.contains(obj));
			System.out.println(message);
		}
		
		String message2 = "does not contain null";
		assertFalse(message2, actual.contains(null));
		System.out.println(message2);
	}
}
