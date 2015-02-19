/*
 * Title: SequentialSearchSymbolTable
 * Author: Matthew Boyette
 * Date: 2/11/2015
 *
 * A minimalist generic sequential search table data structure.
 * TODO: Finish double-linked list functionality. Currently only supports single-linked list functionality.
 */

package api.util.datastructures;

import java.util.LinkedList;

public class SequentialSearchSymbolTable<K,V>
{
	private Node<K,V>	first	= null;	// The head to the linked list of key-value pairs.
	private Node<K,V>	last	= null;	// The tail to the linked list of key-value pairs.
	private int			size	= 0;	// Number of key-value pairs.

	// Is the key in the symbol table?
	public final boolean contains(final K key)
	{
		return (this.get(key) != null);
	}

	public final void delete(final K key)
	{
		this.first = this.delete(this.first, key);
	}

	// Delete key (and associated value) if key is in the table.
	protected final Node<K,V> delete(final Node<K,V> node, final K key)
	{
		if (node == null)
		{
			return null;
		}

		if (key.equals(node.getKey()))
		{
			this.size--;
			return node.getNext();
		}

		node.setNext(this.delete(node.getNext(), key));
		return node;
	}

	// Return value associated with key, null if no such key.
	public final V get(final K key)
	{
		for (Node<K,V> x = this.first; x != null; x = x.getNext())
		{
			if (key.equals(x.getKey()))
			{
				return x.getValue();
			}
		}

		return null;
	}

	public final Node<K,V> getFirst()
	{
		return this.first;
	}

	public final Node<K,V> getLast()
	{
		return this.last;
	}

	// Return the size of the symbol table.
	public final int getSize()
	{
		return this.size;
	}

	// Is the symbol table empty?
	public final boolean isEmpty()
	{
		return (this.getSize() == 0);
	}

	// Return keys in symbol table as an Iterable.
	public final Iterable<K> keys()
	{
		LinkedList<K> queue = new LinkedList<K>();

		for (Node<K,V> x = this.first; x != null; x = x.getNext())
		{
			queue.add(x.getKey());
		}

		return queue;
	}

	// Insert key-value pair into the table.
	public final void put(final K key, final V value)
	{
		if (value == null)
		{
			this.delete(key);
			return;
		}

		for (Node<K,V> x = this.first; x != null; x = x.getNext())
		{
			if (key.equals(x.getKey()))
			{
				x.setValue(value);
				return;
			}
		}

		this.first = new Node<K,V>(key, value, this.first, null);
		this.size++;
	}

	protected final void setFirst(final Node<K,V> first)
	{
		this.first = first;
	}

	protected final void setLast(final Node<K,V> last)
	{
		this.last = last;
	}

	protected final void setSize(final int size)
	{
		this.size = size;
	}
}