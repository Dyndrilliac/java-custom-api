/*
 * Title: KeyValueNode
 * Author: Matthew Boyette
 * Date: 2/11/2015
 * 
 * A minimalist generic node data structure which stores key-value pairs.
 */

package api.util.datastructures;

public class KeyValueNode<K,V> extends Node
{
    private KeyValuePair<K,V> kvp = null;
    
    public KeyValueNode(final K key, final V value, final Node next, final Node previous)
    {
        super(next, previous);
        this.setKeyValuePair(new KeyValuePair<K,V>(key, value));
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!super.equals(obj))
        {
            return false;
        }
        
        if (!(obj instanceof KeyValueNode))
        {
            return false;
        }
        
        KeyValueNode<?,?> other = (KeyValueNode<?,?>)obj;
        
        if (this.getKeyValuePair() == null)
        {
            if (other.getKeyValuePair() != null)
            {
                return false;
            }
        }
        else if (!this.getKeyValuePair().equals(other.getKeyValuePair()))
        {
            return false;
        }
        
        return true;
    }
    
    public final K getKey()
    {
        return this.getKeyValuePair().getKey();
    }
    
    public final KeyValuePair<K,V> getKeyValuePair()
    {
        return this.kvp;
    }
    
    public final V getValue()
    {
        return this.getKeyValuePair().getValue();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.getKeyValuePair() == null) ? 0 : this.getKeyValuePair().hashCode());
        return result;
    }
    
    public final void setKey(final K key)
    {
        this.getKeyValuePair().setKey(key);
    }
    
    public final void setKeyValuePair(final KeyValuePair<K,V> kvp)
    {
        this.kvp = kvp;
    }
    
    public final void setValue(final V value)
    {
        this.getKeyValuePair().setValue(value);
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyValueNode [kvp=");
        builder.append(this.getKeyValuePair());
        
        if (this.getNext() != null)
        {
            builder.append(", next=");
            builder.append(this.getNext().hashCode());
        }
        
        if (this.getPrevious() != null)
        {
            builder.append(", previous=");
            builder.append(this.getPrevious().hashCode());
        }
        
        builder.append("]");
        return builder.toString();
    }
}