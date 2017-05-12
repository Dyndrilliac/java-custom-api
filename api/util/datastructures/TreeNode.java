/*
 * Title: TreeNode
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * A minimalist generic node data structure which stores raw data.
 * This type of node has methods that are helpful when stored in a binary tree data structure.
 */

package api.util.datastructures;

import api.util.Support;

public class TreeNode<T extends Comparable<? super T>> extends DataNode<T>
{
    public static enum TRAVERSAL_ORDER
    {
        DEFAULT, IN, POST, PRE
    }

    private Node parent = null;

    public TreeNode(final T data, final Node parent, final Node right, final Node left)
    {
        super(data, right, left);
        this.setParent(parent);
    }

    protected String generateDataString(final int minWidth)
    {
        String data = "";

        if ( this.getData() != null )
        {
            data = this.getData().toString();
        }

        if ( !data.matches("\\[.+\\]") )
        {
            data = String.format("[%-" + ( minWidth - 3 ) + "s]", data);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public final int getDepth()
    {
        if ( this.isRoot() )
        {
            return 0;
        }
        else
        {
            return ( ( (TreeNode<T>) this.getParent() ).getDepth() + 1 );
        }
    }

    @SuppressWarnings("unchecked")
    public final int getHeight()
    {
        if ( this.isLeaf() )
        {
            return 0;
        }
        else
        {
            return ( Math.max(( ( (TreeNode<T>) this.getLeft() ).getHeight() + 1 ), ( ( (TreeNode<T>) this.getRight() ).getHeight() + 1 )) );
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

    public final boolean hasLeft()
    {
        if ( this.getLeft() != null ) { return true; }

        return false;
    }

    public final boolean hasRight()
    {
        if ( this.getRight() != null ) { return true; }

        return false;
    }

    public final boolean isLeaf()
    {
        return ( ( this.getLeft() == null ) && ( this.getRight() == null ) );
    }

    @SuppressWarnings("unchecked")
    public final boolean isLeft()
    {
        if ( this.isRoot() == false )
        {
            if ( this == ( (TreeNode<T>) this.getParent() ).getLeft() ) { return true; }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public final boolean isRight()
    {
        if ( this.isRoot() == false )
        {
            if ( this == ( (TreeNode<T>) this.getParent() ).getRight() ) { return true; }
        }

        return false;
    }

    public final boolean isRoot()
    {
        return ( this.getParent() == null );
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

    @Override
    public String toString()
    {
        return this.toString(TRAVERSAL_ORDER.DEFAULT, 15);
    }

    public String toString(final int minWidth)
    {
        return toString(TRAVERSAL_ORDER.DEFAULT, minWidth).toString();
    }

    public String toString(final TRAVERSAL_ORDER travOrder)
    {
        return toString(travOrder, 15).toString();
    }

    public String toString(final TRAVERSAL_ORDER travOrder, final int minWidth)
    {
        return this.toStringBuilder(new StringBuilder(), true, new StringBuilder(), travOrder, minWidth).toString();
    }

    @SuppressWarnings("unchecked")
    public StringBuilder toStringBuilder(final StringBuilder prefix, final boolean isTail, final StringBuilder sb, final TRAVERSAL_ORDER travOrder, final int minWidth)
    {
        String connector1, connector2, padding1, padding2, dataString = "";
        connector1 = new StringBuilder(Support.padRight("└", minWidth - 1, '─')).append(" ").toString();
        connector2 = new StringBuilder(Support.padRight("┌", minWidth - 1, '─')).append(" ").toString();
        padding1 = Support.padRight("│", minWidth + 1, ' ');
        padding2 = Support.padRight(" ", minWidth + 1, ' ');
        dataString = String.format("%-" + ( minWidth ) + "s%s%n", ( isTail ? connector1 : connector2 ), this.generateDataString(minWidth));

        switch ( travOrder )
        {
            case IN: // In-Order LtR Traversal

                if ( this.getLeft() != null )
                {
                    ( (TreeNode<T>) this.getLeft() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding1 : padding2), false, sb, travOrder, minWidth);
                }

                sb.append(prefix).append(dataString);

                if ( this.getRight() != null )
                {
                    ( (TreeNode<T>) this.getRight() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding2 : padding1), true, sb, travOrder, minWidth);
                }
                break;

            case POST: // TODO: Post-Order LtR Traversal

                if ( this.getLeft() != null )
                {
                    ( (TreeNode<T>) this.getLeft() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding1 : padding2), false, sb, travOrder, minWidth);
                }

                if ( this.getRight() != null )
                {
                    ( (TreeNode<T>) this.getRight() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding1 : padding2), false, sb, travOrder, minWidth);
                }

                sb.append(prefix).append(dataString);
                break;

            case PRE: // Pre-Order LtR Traversal

                dataString = dataString.replace('┌', '├');

                sb.append(prefix).append(dataString);

                if ( this.getLeft() != null )
                {
                    if ( this.getRight() != null )
                    {
                        ( (TreeNode<T>) this.getLeft() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding2 : padding1), false, sb, travOrder, minWidth);
                    }
                    else
                    {
                        ( (TreeNode<T>) this.getLeft() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding2 : padding1), true, sb, travOrder, minWidth);
                    }
                }

                if ( this.getRight() != null )
                {
                    ( (TreeNode<T>) this.getRight() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding2 : padding1), true, sb, travOrder, minWidth);
                }
                break;

            default: // In-Order RtL Traversal **Default**

                if ( this.getRight() != null )
                {
                    ( (TreeNode<T>) this.getRight() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding1 : padding2), false, sb, travOrder, minWidth);
                }

                sb.append(prefix).append(dataString);

                if ( this.getLeft() != null )
                {
                    ( (TreeNode<T>) this.getLeft() ).toStringBuilder(new StringBuilder().append(prefix).append(isTail ? padding2 : padding1), true, sb, travOrder, minWidth);
                }
        }

        return sb;
    }
}
