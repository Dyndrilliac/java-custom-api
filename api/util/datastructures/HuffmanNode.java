/*
 * Title: HuffmanNode
 * Author: Matthew Boyette
 * Date: 6/5/2013
 * 
 * This is a special version of TreeNode designed to implement a Huffman Code Tree.
 */

package api.util.datastructures;

public class HuffmanNode<T> extends TreeNode<T> implements Comparable<HuffmanNode<T>>
{
    private int count = 0;

    public HuffmanNode(final T data, final Node parent, final Node right, final Node left, final int count)
    {
        super(data, parent, right, left);
        this.setCount(count);
    }

    @Override
    public int compareTo(final HuffmanNode<T> node)
    {
        return ( this.getCount() - node.getCount() );
    }

    public final int getCount()
    {
        return this.count;
    }

    public final void setCount(final int count)
    {
        this.count = count;
    }

    @Override
    public String toString()
    {
        if ( this.getData() != null )
        {
            return ( "[" + this.getData().toString() + " (" + this.getCount() + ")]" );
        }
        else
        {
            return ( "[(" + this.getCount() + ")]" );
        }
    }
}
