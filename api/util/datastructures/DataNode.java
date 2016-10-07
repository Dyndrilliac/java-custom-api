/*
 * Title: DataNode
 * Author: Matthew Boyette
 * Date: 5/24/2013
 * 
 * A minimalist generic node data structure which stores raw data.
 */

package api.util.datastructures;

public class DataNode<T> extends Node
{
    private T data = null;

    public DataNode(final T data, final Node next, final Node previous)
    {
        super(next, previous);
        this.setData(data);
    }

    @Override
    public boolean equals(final Object obj)
    {
        if ( this == obj ) { return true; }
        if ( !super.equals(obj) ) { return false; }
        if ( !( obj instanceof DataNode ) ) { return false; }

        DataNode<?> other = (DataNode<?>) obj;

        if ( this.getData() == null )
        {
            if ( other.getData() != null ) { return false; }
        }
        else if ( !this.getData().equals(other.getData()) ) { return false; }

        return true;
    }

    public final T getData()
    {
        return this.data;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = ( prime * result ) + ( ( this.getData() == null ) ? 0 : this.getData().hashCode() );
        return result;
    }

    public final void setData(final T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DataNode [data=");
        builder.append(this.getData());

        if ( this.getNext() != null )
        {
            builder.append(", next=");
            builder.append(this.getNext().hashCode());
        }

        if ( this.getPrevious() != null )
        {
            builder.append(", previous=");
            builder.append(this.getPrevious().hashCode());
        }

        builder.append("]");
        return builder.toString();
    }
}
