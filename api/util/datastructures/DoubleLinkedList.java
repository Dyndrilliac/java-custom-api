/*
 * Title: DoubleLinkedList
 * Author: Matthew Boyette
 * Date: 5/24/2013
 *
 * A minimalist generic double linked-list data structure.
 */

package api.util.datastructures;

public class DoubleLinkedList<T>
{
	private Node	head	= null;
	private int		size	= 0;
	private Node	tail	= null;

	public DoubleLinkedList()
	{
	}

	@SafeVarargs
	public DoubleLinkedList(final T... args)
	{
		for (T arg: args)
		{
			this.insertTail(arg);
		}
	}

	public void clear()
	{
		this.setSize(0);
		this.setHead(null);
		this.setTail(null);
	}

	public T deleteHead()
	{
		T retVal = null;
		DoubleLinkedListIterator<T> iterator = this.getIterator();
		iterator.resetHead();
		retVal = iterator.deleteCurrent();
		return retVal;
	}

	public T deleteTail()
	{
		T retVal = null;
		DoubleLinkedListIterator<T> iterator = this.getIterator();
		iterator.resetTail();
		retVal = iterator.deleteCurrent();
		return retVal;
	}

	public Node getHead()
	{
		return this.head;
	}

	public DoubleLinkedListIterator<T> getIterator()
	{
		return new DoubleLinkedListIterator<T>(this);
	}

	public int getSize()
	{
		return this.size;
	}

	public Node getTail()
	{
		return this.tail;
	}

	public void insertHead(final T data)
	{
		DoubleLinkedListIterator<T> iterator = this.getIterator();
		iterator.resetHead();
		iterator.insertBefore(data);
	}

	public void insertTail(final T data)
	{
		DoubleLinkedListIterator<T> iterator = this.getIterator();
		iterator.resetTail();
		iterator.insertAfter(data);
	}

	public boolean isEmpty()
	{
		return (this.getSize() <= 0);
	}

	public void setHead(final Node head)
	{
		this.head = head;
	}

	public void setSize(final int size)
	{
		this.size = size;
	}

	public void setTail(final Node tail)
	{
		this.tail = tail;
	}

	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		DoubleLinkedListIterator<T> iterator = this.getIterator();
		iterator.resetHead();

		for (int i = 0; i < this.getSize(); i++)
		{
			output.append(iterator.getCurrent().toString() + "\n");
			iterator.nextNode();
		}

		return output.toString();
	}
}