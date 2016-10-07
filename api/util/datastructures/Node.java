/*
 * Title: Node
 * Author: Matthew Boyette
 * Date: 5/24/2013
 * 
 * A minimalist abstract node data structure.
 */

package api.util.datastructures;

public abstract class Node
{
    private Node next     = null;
    private Node previous = null;

    public Node(final Node next, final Node previous)
    {
        this.setNext(next);
        this.setPrevious(previous);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if ( this == obj ) { return true; }

        if ( obj == null ) { return false; }

        if ( !( obj instanceof Node ) ) { return false; }

        if ( this.hashCode() != obj.hashCode() ) { return false; }

        return true;
    }

    public final Node getNext()
    {
        return this.next;
    }

    public final Node getPrevious()
    {
        return this.previous;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    public final void setNext(final Node next)
    {
        this.next = next;
    }

    public final void setPrevious(final Node previous)
    {
        this.previous = previous;
    }

    @Override
    public String toString()
    {
        return "Node@" + this.hashCode();
    }
}
