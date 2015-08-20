/*
 * Title: KeyValuePair
 * Author: Matthew Boyette
 * Date: 2/11/2015
 * 
 * A minimalist generic data structure which stores key-value pairs.
 */

package api.util.datastructures;

public class KeyValuePair<K,V>
{
	private K	key		= null;
	private V	value	= null;
	
	public KeyValuePair(final K key, final V value)
	{
		this.setKey(key);
		this.setValue(value);
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof KeyValuePair))
		{
			return false;
		}
		
		KeyValuePair<?,?> other = (KeyValuePair<?,?>)obj;
		
		if (this.getKey() == null)
		{
			if (other.getKey() != null)
			{
				return false;
			}
		}
		else
			if (!this.getKey().equals(other.getKey()))
			{
				return false;
			}
		
		if (this.getValue() == null)
		{
			if (other.getValue() != null)
			{
				return false;
			}
		}
		else
			if (!this.getValue().equals(other.getValue()))
			{
				return false;
			}
		
		return true;
	}
	
	public final K getKey()
	{
		return this.key;
	}
	
	public final V getValue()
	{
		return this.value;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.getKey() == null) ? 0 : this.getKey().hashCode());
		result = (prime * result) + ((this.getValue() == null) ? 0 : this.getValue().hashCode());
		return result;
	}
	
	public final void setKey(final K key)
	{
		this.key = key;
	}
	
	public final void setValue(final V value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("KeyValuePair [key=");
		builder.append(this.getKey());
		builder.append(", value=");
		builder.append(this.getValue());
		builder.append("]");
		return builder.toString();
	}
}