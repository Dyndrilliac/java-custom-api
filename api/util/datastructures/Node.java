/*
 * Title: Node
 * Author: Matthew Boyette
 * Date: 2/11/2015
 *
 * A minimalist generic node data structure.
 */

package api.util.datastructures;

public final class Node<K,V>
{
	private K			key			= null;
	private Node<K,V>	next		= null;
	private Node<K,V>	previous	= null;
	private V			value		= null;

	public Node(final K key, final V value, final Node<K,V> next, final Node<K,V> previous)
	{
		this.key = key;
		this.value = value;
		this.next = next;
		this.previous = previous;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof Node))
		{
			return false;
		}

		Node<?,?> other = (Node<?,?>)obj;

		if (this.key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		else
			if (!this.key.equals(other.key))
			{
				return false;
			}

		if (this.next == null)
		{
			if (other.next != null)
			{
				return false;
			}
		}
		else
			if (!this.next.equals(other.next))
			{
				return false;
			}

		if (this.previous == null)
		{
			if (other.previous != null)
			{
				return false;
			}
		}
		else
			if (!this.previous.equals(other.previous))
			{
				return false;
			}

		if (this.value == null)
		{
			if (other.value != null)
			{
				return false;
			}
		}
		else
			if (!this.value.equals(other.value))
			{
				return false;
			}

		return true;
	}

	public final K getKey()
	{
		return this.key;
	}

	public final Node<K,V> getNext()
	{
		return this.next;
	}

	public final Node<K,V> getPrevious()
	{
		return this.previous;
	}

	public final V getValue()
	{
		return this.value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;

		result = (prime * result) + ((this.key == null) ? 0 : this.key.hashCode());
		result = (prime * result) + ((this.next == null) ? 0 : this.next.hashCode());
		result = (prime * result) + ((this.previous == null) ? 0 : this.previous.hashCode());
		result = (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());

		return result;
	}

	public final void setKey(final K key)
	{
		this.key = key;
	}

	public final void setNext(final Node<K,V> next)
	{
		this.next = next;
	}

	public final void setPrevious(final Node<K,V> previous)
	{
		this.previous = previous;
	}

	public final void setValue(final V value)
	{
		this.value = value;
	}
}