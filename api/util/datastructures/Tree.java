/*
 * Title: Tree
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * A minimalist generic binary tree data structure.
 */

package api.util.datastructures;

import api.util.datastructures.TreeNode.TRAVERSAL_ORDER;

public class Tree<T extends Comparable<? super T>>
{
    private Node root = null;
    private int  size = 0;

    public Tree()
    {
        this.clear();
    }

    @SafeVarargs
    public Tree(final T... args)
    {
        super();

        for ( T arg : args )
        {
            this.insert(arg);
        }
    }

    public final void clear()
    {
        this.setRoot(null);
        this.setSize(0);
    }

    public final boolean delete(final T data)
    {
        TreeIterator<T> iterator = this.getIterator();
        return ( iterator.find(data, true) );
    }

    public final boolean find(final T data)
    {
        TreeIterator<T> iterator = this.getIterator();
        return ( iterator.find(data, false) );
    }

    @SuppressWarnings("unchecked")
    public final int getDepth()
    {
        if ( this.isEmpty() )
        {
            return -1;
        }
        else
        {
            return ( (TreeNode<T>) this.getRoot() ).getDepth();
        }
    }

    @SuppressWarnings("unchecked")
    public final int getHeight()
    {
        if ( this.isEmpty() )
        {
            return -1;
        }
        else
        {
            return ( (TreeNode<T>) this.getRoot() ).getHeight();
        }
    }

    public final TreeIterator<T> getIterator()
    {
        return ( new TreeIterator<T>(this) );
    }

    public final Node getRoot()
    {
        return this.root;
    }

    public final int getSize()
    {
        return this.size;
    }

    public final boolean insert(final T data)
    {
        TreeIterator<T> iterator = this.getIterator();
        return ( iterator.insert(data) );
    }

    public final boolean isEmpty()
    {
        return ( this.getSize() == 0 );
    }

    public final void setRoot(final Node root)
    {
        this.root = root;
    }

    public final void setSize(final int size)
    {
        this.size = size;
    }

    @Override
    public String toString()
    {
        return this.toString(this.getRoot(), TRAVERSAL_ORDER.DEFAULT, 15);
    }

    public String toString(final Node root)
    {
        return this.toString(root, TRAVERSAL_ORDER.DEFAULT, 15);
    }

    @SuppressWarnings("unchecked")
    public String toString(final Node root, final TRAVERSAL_ORDER travOrder, final int minWidth)
    {
        if ( root != null ) { return ( (TreeNode<T>) root ).toString(travOrder, minWidth); }

        return "";
    }

    public String toString(final TRAVERSAL_ORDER travOrder)
    {
        return this.toString(this.getRoot(), travOrder, 15);
    }

    public String toString(final TRAVERSAL_ORDER travOrder, final int minWidth)
    {
        return this.toString(this.getRoot(), travOrder, minWidth);
    }
}
