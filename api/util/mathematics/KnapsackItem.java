/*
 * Title: KnapsackItem
 * Author: Matthew Boyette
 * Date: 5/27/2013
 * 
 * This is a helper object which represents an item in the 0-1 Knapsack variant problem.
 */

package api.util.mathematics;

import java.util.Comparator;

public class KnapsackItem
{
	public final static class KnapsackItemAscending implements Comparator<KnapsackItem>
	{
		@Override
		public final int compare(final KnapsackItem item1, final KnapsackItem item2)
		{
			int retVal = item2.getWeight() - item1.getWeight();
			
			if (retVal == 0)
			{
				retVal = item2.getValue() - item1.getValue();
			}
			
			return retVal;
		}
	}
	
	public final static class KnapsackItemDescending implements Comparator<KnapsackItem>
	{
		@Override
		public final int compare(final KnapsackItem item1, final KnapsackItem item2)
		{
			int retVal = item1.getWeight() - item2.getWeight();
			
			if (retVal == 0)
			{
				retVal = item1.getValue() - item2.getValue();
			}
			
			return retVal;
		}
	}
	
	private int	value	= 0;
	private int	weight	= 0;
	
	public KnapsackItem(final int value, final int weight)
	{
		this.setValue(value);
		this.setWeight(weight);
	}
	
	public KnapsackItem(final int weight)
	{
		this.setValue(1);
		this.setWeight(weight);
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
		
		if (!(obj instanceof KnapsackItem))
		{
			return false;
		}
		
		KnapsackItem other = (KnapsackItem)obj;
		
		if (this.getValue() != other.getValue())
		{
			return false;
		}
		
		if (this.getWeight() != other.getWeight())
		{
			return false;
		}
		
		return true;
	}
	
	public final int getValue()
	{
		return this.value;
	}
	
	public final int getWeight()
	{
		return this.weight;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.getValue();
		result = (prime * result) + this.getWeight();
		return result;
	}
	
	public final void setValue(final int value)
	{
		this.value = value;
	}
	
	public final void setWeight(final int weight)
	{
		this.weight = weight;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("KnapsackItem [");
		
		if (this.getValue() != 1)
		{
			builder.append("weight=");
			builder.append(this.getWeight());
			builder.append(", value=");
			builder.append(this.getValue());
		}
		else
		{
			builder.append(this.getWeight());
		}
		
		builder.append("]");
		return builder.toString();
	}
}