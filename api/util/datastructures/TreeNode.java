/*
 * Title: TreeNode
 * Author: Matthew Boyette
 * Date: 6/5/2013
 *
 * A minimalist generic node data structure which stores raw data.
 * This type of node has methods that are helpful when stored in a tree structure.
 */

package api.util.datastructures;

public class TreeNode<T> extends DataNode<T>
{
	private Node	parent	= null;

	public TreeNode(final T data, final Node parent, final Node right, final Node left)
	{
		super(data, right, left);
		this.setParent(parent);
	}

	@SuppressWarnings("unchecked")
	public int getDepth()
	{
		if (this.isRoot())
		{
			return 0;
		}
		else
		{
			return (((TreeNode<T>)this.getParent()).getDepth() + 1);
		}
	}

	@SuppressWarnings("unchecked")
	public int getHeight()
	{
		if (this.isLeaf())
		{
			return 0;
		}
		else
		{
			return (Math.max((((TreeNode<T>)this.getLeft()).getHeight() + 1),
				(((TreeNode<T>)this.getRight()).getHeight() + 1)));
		}
	}

	public final Node getLeft()
	{
		return this.getPrevious();
	}

	public final Node getParent()
	{
		return this.parent;
	}

	public final Node getRight()
	{
		return this.getNext();
	}

	public boolean isLeaf()
	{
		return ((this.getLeft() == null) && (this.getRight() == null));
	}

	@SuppressWarnings("unchecked")
	public boolean isLeft()
	{
		if (this.isRoot() == false)
		{
			if (this == ((TreeNode<T>)this.getParent()).getLeft())
			{
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isRight()
	{
		if (this.isRoot() == false)
		{
			if (this == ((TreeNode<T>)this.getParent()).getRight())
			{
				return true;
			}
		}

		return false;
	}

	public boolean isRoot()
	{
		return (this.getParent() == null);
	}

	public final void setLeft(final Node left)
	{
		this.setPrevious(left);
	}

	public final void setParent(final Node parent)
	{
		this.parent = parent;
	}

	public final void setRight(final Node right)
	{
		this.setNext(right);
	}
}