/*
 * Title: KnapsackSolver
 * Author: Matthew Boyette
 * Date: 5/27/2013
 * 
 * This object solves 0-1 Knapsack variant of the Subset-Sum problem.
 */

package api.util.mathematics;

import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import api.util.Support;

public class KnapsackSolver
{
	/*
	 * This method takes a list of items and a knapsack capacity.
	 * It attempts to fill the knapsack exactly with the given items.
	 * It only considers items in the list that are in slots start, start+1, start+2, and so on.
	 * When the method is initially called it should be called with start equal to 0.
	 * In addition the method also takes as input the size of the current partial solution.
	 * This is initially 0.
	 */
	protected static LinkedList<KnapsackItem> knapsack(final LinkedList<KnapsackItem> items, final int capacity,
		final int start)
	{
		if (capacity > 0)
		{
			// For each i, try placing the ith item in the knapsack.
			for (int i = start; i < items.size(); i++)
			{
				// Fit the smaller knapsack with items chosen from i+1, i+2, i+3...
				LinkedList<KnapsackItem> answer = KnapsackSolver.knapsack(items, capacity - items.get(i).getWeight(), i + 1);
				
				if (answer != null)
				{
					answer.add(items.get(i));
					Collections.sort(items, new KnapsackItem.KnapsackItemDescending());
					return answer;
				}
			}
			
			return null;
		}
		// We have found a solution. Create an array of the correct size and send it back for filling.
		else
			if (capacity == 0)
			{
				LinkedList<KnapsackItem> temp = new LinkedList<KnapsackItem>();
				return temp;
			}
			else
			{
				// Capacity is negative, so no solution is possible.
				return null;
			}
	}
	
	private int										capacity		= 0;
	private LinkedList<KnapsackItem>				items			= null;
	private LinkedList<LinkedList<KnapsackItem>>	solutionSets	= null;
	
	public KnapsackSolver(final int capacity, final int[] itemWeights)
	{
		LinkedList<KnapsackItem> items = new LinkedList<KnapsackItem>();
		KnapsackItem item = null;
		
		for (int itemWeight: itemWeights)
		{
			item = new KnapsackItem(1, itemWeight);
			items.add(item);
		}
		
		this.reset(capacity, items);
	}
	
	public KnapsackSolver(final int capacity, final LinkedList<KnapsackItem> items)
	{
		this.reset(capacity, items);
	}
	
	public void displaySolutionSets()
	{
		if (this.getSolutionSets().isEmpty())
		{
			JOptionPane.showMessageDialog(null,
				"This application was unable to find any solution sets for the given parameters.",
				"No Solution Sets",
				JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			final int numSolutionSets = this.getSolutionSets().size();
			final StringBuilder sb = new StringBuilder();
			sb.append("Solution Sets Found:\n\n");
			
			for (int i = 0; i < numSolutionSets; i++)
			{
				sb.append("Solution Set [" + i + "]: " + this.getSolutionSets().get(i).toString() + "\n");
			}
			
			JOptionPane.showMessageDialog(null,
				sb.toString(),
				numSolutionSets + " Solutions Found",
				JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public final int getCapacity()
	{
		return this.capacity;
	}
	
	public final LinkedList<KnapsackItem> getItems()
	{
		return this.items;
	}
	
	public final LinkedList<LinkedList<KnapsackItem>> getSolutionSets()
	{
		return this.solutionSets;
	}
	
	protected void populateSolutionSets()
	{
		final LinkedList<KnapsackItem> reverseItems = new LinkedList<KnapsackItem>();
		LinkedList<KnapsackItem> result;
		int counter = 0;
		reverseItems.addAll(this.getItems());
		Collections.sort(reverseItems, new KnapsackItem.KnapsackItemAscending());
		Support.displayDebugMessage(null, "Items: " + this.getItems() + "\nReversed: " + reverseItems.toString() + "\n");
		
		/*
		 * TODO: Needs revision. Does not successfully find all solutions.
		 * Given the input 32 17 15 12 10 5 3 2
		 * Capacity = 32
		 * Weights = {17 15 12 10 5 3 2}
		 * 
		 * Finds these solutions:
		 * 
		 * 17 15 = 32
		 * 17 12 3 = 32
		 * 17 10 5 = 32
		 * 15 12 5 = 32
		 * 12 10 5 3 2 = 32 (optimal solution)
		 * 
		 * Misses these solutions:
		 * 
		 * 17 10 3 2 = 32
		 * 15 12 3 2 = 32
		 * 15 10 5 2 = 32
		 */
		
		do
		{
			result = KnapsackSolver.knapsack(this.getItems(), this.getCapacity(), counter);
			
			if (result != null)
			{
				if (this.getSolutionSets().contains(result) == false)
				{
					this.getSolutionSets().add(result);
				}
			}
			
			result = KnapsackSolver.knapsack(reverseItems, this.getCapacity(), counter);
			
			if (result != null)
			{
				if (this.getSolutionSets().contains(result) == false)
				{
					this.getSolutionSets().add(result);
				}
			}
			
			counter++;
		}
		while (result != null);
	}
	
	public void reset(final int capacity, final LinkedList<KnapsackItem> items)
	{
		Collections.sort(items, new KnapsackItem.KnapsackItemDescending());
		this.setCapacity(capacity);
		this.setItems(items);
		this.setSolutionSets(new LinkedList<LinkedList<KnapsackItem>>());
		this.populateSolutionSets();
	}
	
	protected final void setCapacity(final int capacity)
	{
		this.capacity = capacity;
	}
	
	protected final void setItems(final LinkedList<KnapsackItem> items)
	{
		this.items = items;
	}
	
	protected final void setSolutionSets(final LinkedList<LinkedList<KnapsackItem>> solutionSets)
	{
		this.solutionSets = solutionSets;
	}
}