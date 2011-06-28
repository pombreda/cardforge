package net.slightlymagic.braids.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @deprecated
 */
public class CollectionFromArray<E> implements Collection<E> {

	private E[] array;
	
	public CollectionFromArray(E[] array) {
		this.array = array;
	}

	//TODO @Override
	public boolean add(E e) {
		throw new IllegalAccessError("This instance is immutable.");
	}

	//TODO @Override
	public boolean addAll(Collection<? extends E> c) {
		throw new IllegalAccessError("This instance is immutable.");
	}

	//TODO @Override
	public void clear() {
		throw new IllegalAccessError("This instance is immutable.");
	}
	
	//TODO @Override
	public boolean contains(Object o) {
		for (int i = 0; i < this.size(); i++) {
			if (array[i] == o) {
				return true;
			}
			else if (array[i] != null && array[i].equals(o)) {
				return true;
			}
		}

		return false;
	}

	//TODO @Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) {
				return false;
			}
		}
		
		return true;
	}

	//TODO @Override
	public boolean isEmpty() {
		return (this.size() == 0);
	}

	//TODO @Override
	public Iterator<E> iterator() {
		
		// TODO Auto-generated method stub
		return null;
	}

	//TODO @Override
	public boolean remove(Object o) {
		throw new IllegalAccessError("This instance is immutable.");
	}

	//TODO @Override
	public boolean removeAll(Collection<?> c) {
		throw new IllegalAccessError("This instance is immutable.");
	}

	//TODO @Override
	public boolean retainAll(Collection<?> c) {
		throw new IllegalAccessError("This instance is immutable.");
	}

	//TODO @Override
	public int size() {
		if (array == null) {
			return 0;
		}
		return array.length;
	}

	//TODO @Override
	public Object[] toArray() {
		return array;
	}

	@SuppressWarnings("unchecked")
	//TODO @Override
	public <T> T[] toArray(T[] a) {
		T[] result = a;
		if (a.length < this.size()) {
			result = (T[]) new Object[this.size()];
		}

		for (int i = 0; i < this.size(); i++) {
			result[i] = (T) array[i];
		}
		
		return result;
	}

}
