/*
 * Title: TreeIterator
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * An iterator for a minimalist generic binary tree data structure.
 */

package api.util.datastructures;

public class TreeIterator<T extends Comparable<? super T>>
{
    private Node    current = null;
    private Tree<T> tree    = null;

    public TreeIterator(final Tree<T> tree)
    {
        this.setTree(tree);
        this.reset(this.getTree().getSize());
    }

    boolean find(final T data, final boolean doRemoval)
    {
        int newSize = this.getTree().getSize();

        while ( this.getCurrent() != null )
        {
            if ( data.equals(this.getData()) )
            {
                // Data found. Do we want to remove it?
                if ( doRemoval )
                {
                    if ( this.removeNode(this.getCurrent()) )
                    {
                        newSize--;

                        if ( newSize == 0 )
                        {
                            this.getTree().setRoot(null);
                        }

                        this.reset(newSize);
                        return true;
                    }

                    this.reset(newSize);
                    return false;
                }

                this.reset(newSize);
                return true;
            }
            else
            {
                // Keep traversing tree.
                if ( data.compareTo(this.getData()) > 0 )
                {
                    this.setCurrent(this.getRight());
                }
                else
                {
                    this.setCurrent(this.getLeft());
                }
            }
        }

        this.reset(newSize);
        return false;
    }

    public final Node getCurrent()
    {
        return this.current;
    }

    @SuppressWarnings("unchecked")
    public final T getData()
    {
        if ( this.getCurrent() != null )
        {
            return ( (TreeNode<T>) this.getCurrent() ).getData();
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public final Node getLeft()
    {
        if ( this.getCurrent() != null )
        {
            return ( (TreeNode<T>) this.getCurrent() ).getLeft();
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public final Node getParent()
    {
        if ( this.getCurrent() != null )
        {
            return ( (TreeNode<T>) this.getCurrent() ).getParent();
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public final Node getRight()
    {
        if ( this.getCurrent() != null )
        {
            return ( (TreeNode<T>) this.getCurrent() ).getRight();
        }
        else
        {
            return null;
        }
    }

    public final Tree<T> getTree()
    {
        return this.tree;
    }

    public boolean insert(final T data)
    {
        int newSize = this.getTree().getSize();

        if ( this.getTree().isEmpty() )
        {
            this.getTree().setRoot(new TreeNode<T>(data, null, null, null));
        }
        else
        {
            while ( this.getCurrent() != null )
            {
                if ( data.compareTo(this.getData()) <= 0 )
                {
                    if ( this.getLeft() == null )
                    {
                        this.setLeft(new TreeNode<T>(data, this.getCurrent(), null, null));
                        break;
                    }
                    else
                    {
                        // Insert the new node to the left of this.getCurrent(), because this.getCurrent() already has a left child.
                        this.setCurrent(this.getLeft());
                    }
                }
                else if ( data.compareTo(this.getData()) > 0 )
                {
                    if ( this.getRight() == null )
                    {
                        this.setRight(new TreeNode<T>(data, this.getCurrent(), null, null));
                        break;
                    }
                    else
                    {
                        // Insert the new node to the right of this.getCurrent(), because this.getCurrent() already has a right child.
                        this.setCurrent(this.getRight());
                    }
                }
            }
        }

        newSize++;
        this.reset(newSize);
        return true;
    }

    @SuppressWarnings("unchecked")
    protected boolean removeNode(final Node node)
    {
        // Zero Child Case
        if ( ( (TreeNode<T>) node ).isLeaf() )
        {
            if ( !( (TreeNode<T>) node ).isRoot() )
            {
                if ( ( (TreeNode<T>) node ).isLeft() )
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setLeft(null);
                }
                else if ( ( (TreeNode<T>) node ).isRight() )
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setRight(null);
                }
            }

            return true;
        }
        // One Child Case
        else if ( ( ( (TreeNode<T>) node ).getLeft() == null ) ^ ( ( (TreeNode<T>) node ).getRight() == null ) )
        {
            if ( ( (TreeNode<T>) node ).isLeft() )
            {
                if ( ( (TreeNode<T>) node ).getLeft() != null )
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setLeft(( (TreeNode<T>) node ).getLeft());
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getLeft() ).setParent(( (TreeNode<T>) node ).getParent());
                }
                else
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setLeft(( (TreeNode<T>) node ).getRight());
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getRight() ).setParent(( (TreeNode<T>) node ).getParent());
                }
            }
            else if ( ( (TreeNode<T>) node ).isRight() )
            {
                if ( ( (TreeNode<T>) node ).getLeft() != null )
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setRight(( (TreeNode<T>) node ).getLeft());
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getLeft() ).setParent(( (TreeNode<T>) node ).getParent());
                }
                else
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getParent() ).setRight(( (TreeNode<T>) node ).getRight());
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getRight() ).setParent(( (TreeNode<T>) node ).getParent());
                }
            }
            else if ( ( (TreeNode<T>) node ).isRoot() )
            {
                Node temp = null;

                if ( ( (TreeNode<T>) node ).getLeft() != null )
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getLeft() ).setParent(null);
                    temp = ( (TreeNode<T>) node ).getLeft();
                }
                else
                {
                    ( (TreeNode<T>) ( (TreeNode<T>) node ).getRight() ).setParent(null);
                    temp = ( (TreeNode<T>) node ).getRight();
                }

                this.getTree().setRoot(temp);
            }

            return true;
        }
        // Two Child Case
        else if ( ( ( (TreeNode<T>) node ).getLeft() != null ) && ( ( (TreeNode<T>) node ).getRight() != null ) )
        {
            // http://www.algolist.net/Data_structures/Binary_search_tree/Removal
            Node temp = ( (TreeNode<T>) node ).getRight();

            while ( ( (TreeNode<T>) temp ).getLeft() != null )
            {
                temp = ( (TreeNode<T>) temp ).getLeft();
            }

            // Now 'temp' is the node containing the minimum value of the right sub-branch.
            ( (TreeNode<T>) node ).setData(( (TreeNode<T>) temp ).getData());
            return this.removeNode(temp);
        }

        return false;
    }

    public final void reset(final int newSize)
    {
        this.getTree().setSize(newSize);
        this.setCurrent(this.getTree().getRoot());
    }

    public final void setCurrent(final Node current)
    {
        this.current = current;
    }

    @SuppressWarnings("unchecked")
    public final void setLeft(final Node left)
    {
        if ( this.getCurrent() != null )
        {
            ( (TreeNode<T>) this.getCurrent() ).setLeft(left);
        }
    }

    @SuppressWarnings("unchecked")
    public final void setParent(final Node parent)
    {
        if ( this.getCurrent() != null )
        {
            ( (TreeNode<T>) this.getCurrent() ).setParent(parent);
        }
    }

    @SuppressWarnings("unchecked")
    public final void setRight(final Node right)
    {
        if ( this.getCurrent() != null )
        {
            ( (TreeNode<T>) this.getCurrent() ).setRight(right);
        }
    }

    public final void setTree(final Tree<T> tree)
    {
        this.tree = tree;
    }
}
