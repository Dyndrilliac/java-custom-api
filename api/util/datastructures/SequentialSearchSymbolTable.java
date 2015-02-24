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
	private Node	first	= null;	// The head to the linked list of key-value pairs.
	private Node	last	= null;	// The tail to the linked list of key-value pairs.
	private int		size	= 0;	// Number of key-value pairs.

	// Is the key in the symbol table?
	public final boolean contains(final K key)
	{
		return (this.get(key) != null);
	}

	public final void delete(final K key)
	{
		this.setFirst(this.delete(this.getFirst(), key));
	}

	// Delete key (and associated value) if key is in the table.
	@SuppressWarnings("unchecked")
	protected final Node delete(final Node node, final K key)
	{
		if (node == null)
		{
			return null;
		}

		if (key.equals(((KeyValueNode<K,V>)node).getKey()))
		{
			this.setSize(this.getSize() - 1);
			return node.getNext();
		}

		node.setNext(this.delete(node.getNext(), key));
		return node;
	}

	// Return value associated with key, null if no such key.
	@SuppressWarnings("unchecked")
	public final V get(final K key)
	{
		for (Node x = this.getFirst(); x != null; x = x.getNext())
		{
			if (key.equals(((KeyValueNode<K,V>)x).getKey()))
			{
				return ((KeyValueNode<K,V>)x).getValue();
			}
		}

		return null;
	}

	public final Node getFirst()
	{
		return this.first;
	}

	public final Node getLast()
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
	@SuppressWarnings("unchecked")
	public final Iterable<K> keys()
	{
		LinkedList<K> queue = new LinkedList<K>();

		for (Node x = this.getFirst(); x != null; x = x.getNext())
		{
			queue.add(((KeyValueNode<K,V>)x).getKey());
		}

		return queue;
	}

	// Insert key-value pair into the table.
	@SuppressWarnings("unchecked")
	public final void put(final K key, final V value)
	{
		if (value == null)
		{
			this.delete(key);
			return;
		}

		for (Node x = this.getFirst(); x != null; x = x.getNext())
		{
			if (key.equals(((KeyValueNode<K,V>)x).getKey()))
			{
				((KeyValueNode<K,V>)x).setValue(value);
				return;
			}
		}

		this.setFirst(new KeyValueNode<K,V>(key, value, this.getFirst(), null));
		this.setSize(this.getSize() + 1);
	}

	protected final void setFirst(final Node first)
	{
		this.first = first;
	}

	protected final void setLast(final Node last)
	{
		this.last = last;
	}

	protected final void setSize(final int size)
	{
		this.size = size;
	}
}