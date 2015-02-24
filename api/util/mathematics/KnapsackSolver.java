/*
 * Title: 0-1 Knapsack Subset-Sum Solver
 * Author: Matthew Boyette
 * Date: 5/27/2013
 */

package api.util.mathematics;

import java.util.LinkedList;
import java.util.Stack;

import javax.swing.JOptionPane;

public class KnapsackSolver
{
	protected static String formatSolution(final int[] solution)
	{
		StringBuilder sb = new StringBuilder();

		for (int element: solution)
		{
			sb.append(element + " ");
		}

		return sb.toString().trim();
	}

	/*
	 * This method takes an array of weights and a knapsack capacity.
	 * It attempts to fill the knapsack exactly with the given weights.
	 * It only considers items in the array that are in slots start, start+1, start+2, and so on.
	 * When the method is initially called it should be called with start equal to 0.
	 * In addition the method also takes as input the size of the current partial solution.
	 * This is initially 0.
	 */
	protected static int[] knapsack(final int[] weights, final int capacity, final int start, final int solutionSize)
	{
		if (capacity > 0)
		{
			// For each i, try placing the ith item in the knapsack.
			for (int i = start; i < weights.length; i++)
			{
				// Fit the smaller knapsack with items chosen from i+1, i+2, i+3...
				int[] answer = KnapsackSolver.knapsack(weights, capacity - weights[i], i + 1, solutionSize + 1);

				if (answer != null)
				{
					answer[solutionSize] = weights[i];
					return answer;
				}
			}

			return null;
		}

		// We have found a solution. Create an array of the correct size and send it back for filling.
		else
			if (capacity == 0)
			{
				int[] temp = new int[solutionSize];
				return temp;
			}
			else
			{
				// Capacity is negative, so no solution is possible.
				return null;
			}
	}

	protected static String reverseSolution(final String s)
	{
		String retVal = "";
		Stack<String> myStack = new Stack<String>();
		String[] solution = s.trim().split(" ");

		for (String element: solution)
		{
			myStack.push(element);
		}

		while (myStack.empty() == false)
		{
			retVal = retVal + myStack.pop() + " ";
		}

		return retVal.trim();
	}

	private int					capacity	= 0;
	private LinkedList<String>	solutionSet	= null;
	private int[]				weights		= {0};

	public KnapsackSolver(final int[] weights, final int capacity)
	{
		this.setSolutionSet(new LinkedList<String>());
		this.setWeights(weights);
		this.setCapacity(capacity);
		this.populateSolutionSet();
	}

	protected void addSolution(final int[] solution)
	{
		String formattedSolution = KnapsackSolver.formatSolution(solution);

		if (!this.getSolutionSet().contains(KnapsackSolver.reverseSolution(formattedSolution)))
		{
			this.getSolutionSet().add(KnapsackSolver.formatSolution(solution));
		}
	}

	public void displaySolutionSet()
	{
		if (this.getSolutionSet().isEmpty())
		{
			JOptionPane.showMessageDialog(null,
				"This application was unable to find any solutions for the given parameters.",
				"No Solutions",
				JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(null,
				"Optimal Solution: " +
					KnapsackSolver.formatSolution(KnapsackSolver.knapsack(this.getWeights(), this.getCapacity(), 0, 0)) + "\n\n" +
					"Solutions Found:\n\n" + this.getSolutionSet().toString(),
					this.getSolutionSet().size() + " Solutions Found",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public final int getCapacity()
	{
		return this.capacity;
	}

	public final LinkedList<String> getSolutionSet()
	{
		return this.solutionSet;
	}

	public final int[] getWeights()
	{
		return this.weights;
	}

	protected void populateSolutionSet()
	{
		int counter = 0;
		int[] reverseWeights = new int[this.getWeights().length];
		int[] result = null;

		for (int i = 0, j = (this.getWeights().length - 1); i < this.getWeights().length; i++, j--)
		{
			reverseWeights[j] = this.getWeights()[i];
		}

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
			result = KnapsackSolver.knapsack(this.getWeights(), this.getCapacity(), counter, 0);

			if (result != null)
			{
				this.addSolution(result);
			}

			result = KnapsackSolver.knapsack(reverseWeights, this.getCapacity(), counter, 0);

			if (result != null)
			{
				this.addSolution(result);
			}

			counter++;
		}
		while (result != null);
	}

	protected final void setCapacity(final int capacity)
	{
		this.capacity = capacity;
	}

	protected final void setSolutionSet(final LinkedList<String> solutionSet)
	{
		this.solutionSet = solutionSet;
	}

	protected final void setWeights(final int[] weights)
	{
		this.weights = weights;
	}
}