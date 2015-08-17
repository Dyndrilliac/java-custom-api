/*
 * Title: Tree
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * A minimalist generic binary tree data structure.
 */

package api.util.datastructures;

import api.util.stdlib.StdOut;

public class Tree<T>
{
	private TreeNode<T>	root	= null;
	private int			size	= 0;
	
	public Tree()
	{
	}
	
	@SafeVarargs
	public Tree(final T... args)
	{
		for (T arg: args)
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
		return (iterator.delete(data));
	}
	
	public final TreeNode<T> find(final T data)
	{
		TreeIterator<T> iterator = this.getIterator();
		return (iterator.find(data));
	}
	
	public final int getDepth()
	{
		if (this.isEmpty())
		{
			return -1;
		}
		else
		{
			return this.getRoot().getDepth();
		}
	}
	
	public final int getHeight()
	{
		if (this.isEmpty())
		{
			return -1;
		}
		else
		{
			return this.getRoot().getHeight();
		}
	}
	
	public final TreeIterator<T> getIterator()
	{
		return (new TreeIterator<T>(this));
	}
	
	public final TreeNode<T> getRoot()
	{
		return this.root;
	}
	
	public final int getSize()
	{
		return this.size;
	}
	
	public final void insert(final T data)
	{
		TreeIterator<T> iterator = this.getIterator();
		iterator.insert(data);
	}
	
	public final boolean isEmpty()
	{
		return (this.getSize() == 0);
	}
	
	public final void setRoot(final TreeNode<T> root)
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
		String retVal = "";
		
		// TODO: Needs revision.
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public void traversal_inOrder(final TreeNode<T> root)
	{
		if (root != null)
		{
			this.traversal_preOrder((TreeNode<T>)root.getLeft());
			StdOut.print(root.toString() + "\n");
			this.traversal_preOrder((TreeNode<T>)root.getRight());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void traversal_postOrder(final TreeNode<T> root)
	{
		if (root != null)
		{
			this.traversal_preOrder((TreeNode<T>)root.getLeft());
			this.traversal_preOrder((TreeNode<T>)root.getRight());
			StdOut.print(root.toString() + "\n");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void traversal_preOrder(final TreeNode<T> root)
	{
		if (root != null)
		{
			StdOut.print(root.toString() + "\n");
			this.traversal_preOrder((TreeNode<T>)root.getLeft());
			this.traversal_preOrder((TreeNode<T>)root.getRight());
		}
	}
}