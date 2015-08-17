/*
 * Title: TreeIterator
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * An iterator for a minimalist generic binary tree data structure.
 */

package api.util.datastructures;

public class TreeIterator<T>
{
	private TreeNode<T>	current	= null;
	private TreeNode<T>	left	= null;
	private TreeNode<T>	parent	= null;
	private TreeNode<T>	right	= null;
	private Tree<T>		tree	= null;
	
	public TreeIterator(final Tree<T> tree)
	{
		this.setTree(tree);
		this.reset();
	}
	
	public boolean delete(final T data)
	{
		TreeNode<T> TreeNodeDeleted = this.find(data);
		boolean retVal = false;
		int newSize = this.getTree().getSize();
		
		if (TreeNodeDeleted != null)
		{
			// TODO: Needs revision.
			newSize--;
			retVal = true;
		}
		
		this.getTree().setSize(newSize);
		this.reset();
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public TreeNode<T> find(final T data)
	{
		while (this.getCurrent().getData().equals(data) == false)
		{
			boolean goLeft = true; // TODO: Needs revision.
			
			if (goLeft)
			{
				this.setCurrent((TreeNode<T>)this.getCurrent().getLeft());
			}
			else
			{
				this.setCurrent((TreeNode<T>)this.getCurrent().getRight());
			}
			
			if (this.getCurrent() == null)
			{
				return null;
			}
		}
		
		this.reset();
		return this.getCurrent();
	}
	
	public TreeNode<T> getCurrent()
	{
		return this.current;
	}
	
	public final TreeNode<T> getLeft()
	{
		return this.left;
	}
	
	public final TreeNode<T> getParent()
	{
		return this.parent;
	}
	
	public final TreeNode<T> getRight()
	{
		return this.right;
	}
	
	public final Tree<T> getTree()
	{
		return this.tree;
	}
	
	@SuppressWarnings("unchecked")
	public void insert(final T data)
	{
		TreeNode<T> TreeNodeFound = this.find(data);
		
		if (TreeNodeFound == null)
		{
			TreeNode<T> newNode = new TreeNode<T>(data, null, null, null);
			int newSize = this.getTree().getSize();
			
			if (this.getTree().isEmpty())
			{
				this.getTree().setRoot(newNode);
				newSize++;
			}
			else
			{
				while (true) // TODO: Needs revision.
				{
					this.setParent((TreeNode<T>)this.getCurrent().getParent());
					this.setLeft((TreeNode<T>)this.getCurrent().getLeft());
					this.setRight((TreeNode<T>)this.getCurrent().getRight());
					
					boolean comparison = true; // (newNode.compareTo(this.getCurrent()) < 0)
					
					if (comparison)
					{
						this.setCurrent(this.getLeft());
						
						if (this.getCurrent() == null)
						{
							this.getParent().setLeft(newNode);
							newNode.setParent(this.getParent());
							newSize++;
							break;
						}
					}
					else
					{
						this.setCurrent(this.getRight());
						
						if (this.getCurrent() == null)
						{
							this.getParent().setRight(newNode);
							newNode.setParent(this.getParent());
							newSize++;
							break;
						}
					}
				}
			}
			
			this.getTree().setSize(newSize);
			this.reset();
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void reset()
	{
		this.setCurrent(this.getTree().getRoot());
		
		if (this.getCurrent() != null)
		{
			this.setParent((TreeNode<T>)this.getCurrent().getParent());
			this.setLeft((TreeNode<T>)this.getCurrent().getLeft());
			this.setRight((TreeNode<T>)this.getCurrent().getRight());
		}
		else
		{
			this.setParent(null);
			this.setLeft(null);
			this.setRight(null);
		}
	}
	
	public final void setCurrent(final TreeNode<T> current)
	{
		this.current = current;
	}
	
	public final void setLeft(final TreeNode<T> left)
	{
		this.left = left;
	}
	
	public final void setParent(final TreeNode<T> parent)
	{
		this.parent = parent;
	}
	
	public final void setRight(final TreeNode<T> right)
	{
		this.right = right;
	}
	
	public final void setTree(final Tree<T> tree)
	{
		this.tree = tree;
	}
}