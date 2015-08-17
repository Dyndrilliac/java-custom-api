/*
 * Title: DoubleLinkedList
 * Author: Matthew Boyette
 * Date: 5/24/2013
 * 
 * An iterator for a minimalist generic double linked-list data structure.
 */

package api.util.datastructures;

public class DoubleLinkedListIterator<T>
{
	private Node				current		= null;
	private DoubleLinkedList<T>	list		= null;
	private Node				next		= null;
	private Node				previous	= null;
	
	public DoubleLinkedListIterator(final DoubleLinkedList<T> list)
	{
		this.setList(list);
	}
	
	public boolean atHead()
	{
		return (this.getCurrent() == this.getList().getHead());
	}
	
	public boolean atTail()
	{
		return (this.getCurrent() == this.getList().getTail());
	}
	
	@SuppressWarnings("unchecked")
	public T deleteCurrent()
	{
		T retVal = ((DataNode<T>)this.getCurrent()).getData();
		
		if (this.atHead())
		{
			this.getList().setHead(this.getNext());
			this.getList().getHead().setPrevious(null);
			this.resetHead();
		}
		else
			if (this.atTail())
			{
				
				this.getList().setTail(this.getPrevious());
				this.getList().getTail().setNext(null);
				this.resetTail();
				this.nextNode();
			}
			else
			{
				this.getPrevious().setNext(this.getNext());
				this.getNext().setPrevious(this.getPrevious());
				this.setCurrent(this.getNext());
				this.setNext(this.getCurrent().getNext());
				this.setPrevious(this.getCurrent().getPrevious());
			}
		
		int newSize = (this.getList().getSize() - 1);
		this.getList().setSize(newSize);
		return retVal;
	}
	
	public Node getCurrent()
	{
		return this.current;
	}
	
	public final DoubleLinkedList<T> getList()
	{
		return this.list;
	}
	
	public final Node getNext()
	{
		return this.next;
	}
	
	public final Node getPrevious()
	{
		return this.previous;
	}
	
	public void insertAfter(final T data)
	{
		Node newNode = new DataNode<T>(data, null, null);
		
		if (this.getList().isEmpty())
		{
			this.getList().setHead(newNode);
			this.getList().setTail(newNode);
			this.setCurrent(newNode);
			this.resetHead();
		}
		else
		{
			newNode.setPrevious(this.getCurrent());
			this.getCurrent().setNext(newNode);
			this.getList().setTail(newNode);
			this.nextNode();
		}
		
		int newSize = (this.getList().getSize() + 1);
		this.getList().setSize(newSize);
	}
	
	public void insertBefore(final T data)
	{
		Node newNode = new DataNode<T>(data, null, null);
		
		if (this.getList().isEmpty())
		{
			this.getList().setHead(newNode);
			this.getList().setTail(newNode);
			this.setCurrent(newNode);
			this.resetHead();
		}
		else
			if (this.atHead())
			{
				if (this.getCurrent() != null)
				{
					this.getCurrent().setPrevious(newNode);
				}
				
				newNode.setNext(this.getCurrent());
				this.getList().setHead(newNode);
				this.resetHead();
			}
			else
			{
				newNode.setNext(this.getCurrent());
				this.getCurrent().setPrevious(newNode);
				this.setCurrent(newNode);
			}
		
		int newSize = (this.getList().getSize() + 1);
		this.getList().setSize(newSize);
	}
	
	public void nextNode()
	{
		if (this.atTail())
		{
			this.resetHead();
		}
		else
		{
			this.setPrevious(this.getCurrent());
			this.setCurrent(this.getNext());
			
			if (this.getCurrent() != null)
			{
				this.setNext(this.getCurrent().getNext());
			}
			else
			{
				this.setNext(null);
			}
		}
	}
	
	public void prevNode()
	{
		if (this.atHead())
		{
			this.resetTail();
		}
		else
		{
			this.setNext(this.getCurrent());
			this.setCurrent(this.getPrevious());
			
			if (this.getCurrent() != null)
			{
				this.setPrevious(this.getCurrent().getPrevious());
			}
			else
			{
				this.setPrevious(null);
			}
		}
	}
	
	public void resetHead()
	{
		if (this.getList().getHead() != null)
		{
			this.setPrevious(this.getList().getHead().getPrevious());
			this.setCurrent(this.getList().getHead());
			this.setNext(this.getList().getHead().getNext());
		}
		else
		{
			this.setPrevious(null);
			this.setCurrent(null);
			this.setNext(null);
		}
	}
	
	public void resetTail()
	{
		if (this.getList().getTail() != null)
		{
			this.setPrevious(this.getList().getTail().getPrevious());
			this.setCurrent(this.getList().getTail());
			this.setNext(this.getList().getTail().getNext());
		}
		else
		{
			this.setPrevious(null);
			this.setCurrent(null);
			this.setNext(null);
		}
	}
	
	public final void setCurrent(final Node current)
	{
		this.current = current;
	}
	
	public final void setList(final DoubleLinkedList<T> list)
	{
		this.list = list;
	}
	
	public final void setNext(final Node next)
	{
		this.next = next;
	}
	
	public final void setPrevious(final Node previous)
	{
		this.previous = previous;
	}
}