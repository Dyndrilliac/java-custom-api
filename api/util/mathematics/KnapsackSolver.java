/*
 * Title: KnapsackSolver
 * Author: Matthew Boyette
 * Date: 5/27/2013
 * 
 * This object finds all of the solutions for the 0-1 Knapsack variant of the Subset-Sum problem recursively.
 * 
 * @formatter:off
 * 
 * TODO: Needs revision. Using the input: 32 17 15 12 10 5 3 2
 * 
 * Algorithm finds these solutions:
 * 
 * 12 10 5 3 2 = 32 {Optimal Solution}
 * 17 12 3 = 32
 * 17 10 5 = 32
 * 17 15 = 32
 * 15 12 5 = 32
 * 
 * But misses these solutions:
 * 
 * 17 10 3 2 = 32
 * 15 12 3 2 = 32
 * 15 10 5 2 = 32
 * 
 * @formatter:on
 */

package api.util.mathematics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

public class KnapsackSolver
{
    protected static List<KnapsackItem> knapsack(final List<KnapsackItem> items, final int capacity, final int start)
    {
        if ( capacity > 0 )
        {
            for ( int i = start; i < items.size(); i++ )
            {
                List<KnapsackItem> answer = KnapsackSolver.knapsack(items, capacity - items.get(i).getWeight(), i + 1);

                if ( answer != null )
                {
                    answer.add(items.get(i));
                    return answer;
                }
            }

            return null;
        }
        else
        {
            if ( capacity == 0 )
            {
                List<KnapsackItem> temp = new LinkedList<KnapsackItem>();
                return temp;
            }
            else
            {
                return null;
            }
        }
    }

    private int                      capacity     = 0;
    private List<KnapsackItem>       items        = null;
    private List<List<KnapsackItem>> solutionSets = null;

    public KnapsackSolver(final int capacity, final int itemWeights[])
    {
        List<KnapsackItem> items = new LinkedList<KnapsackItem>();

        for ( int itemWeight : itemWeights )
        {
            items.add(new KnapsackItem(itemWeight));
        }

        this.reset(capacity, items);
    }

    public KnapsackSolver(final int capacity, final int itemWeights[], final int itemValues[])
    {
        assert ( itemWeights.length == itemValues.length );
        List<KnapsackItem> items = new LinkedList<KnapsackItem>();

        for ( int i = 0; i < itemWeights.length; i++ )
        {
            items.add(new KnapsackItem(itemValues[i], itemWeights[i]));
        }

        this.reset(capacity, items);
    }

    public KnapsackSolver(final int capacity, final List<KnapsackItem> items)
    {
        this.reset(capacity, items);
    }

    public void displaySolutionSets()
    {
        if ( this.getSolutionSets().isEmpty() )
        {
            JOptionPane.showMessageDialog(null, "This application was unable to find any solution sets for the given parameters.", "No Solution Sets", JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            final int numSolutionSets = this.getSolutionSets().size();
            final StringBuilder sb = new StringBuilder();
            sb.append("Solution Sets Found:\n\n");

            for ( int i = 0; i < numSolutionSets; i++ )
            {
                sb.append("Solution Set [" + i + "]: " + this.getSolutionSets().get(i).toString() + "\n");
            }

            JOptionPane.showMessageDialog(null, sb.toString(), numSolutionSets + " Solutions Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public final int getCapacity()
    {
        return this.capacity;
    }

    public final List<KnapsackItem> getItems()
    {
        return this.items;
    }

    public final List<List<KnapsackItem>> getSolutionSets()
    {
        return this.solutionSets;
    }

    protected void populateSolutionSets()
    {
        List<KnapsackItem> solution = null;
        List<KnapsackItem> tempItems = new LinkedList<KnapsackItem>(this.getItems());

        do
        {
            for ( int i = 0; i < this.getItems().size(); i++ )
            {
                solution = KnapsackSolver.knapsack(tempItems, this.getCapacity(), i);

                if ( solution != null )
                {
                    Collections.sort(solution, new KnapsackItem.KnapsackItemAscending());

                    if ( this.getSolutionSets().contains(solution) == false )
                    {
                        this.getSolutionSets().add(solution);
                    }
                }
            }

            tempItems = tempItems.subList(0, tempItems.size() - 1);
        }
        while ( tempItems.size() >= 1 );
    }

    public void reset(final int capacity, final List<KnapsackItem> items)
    {
        Collections.sort(items, new KnapsackItem.KnapsackItemDescending());
        this.setCapacity(capacity);
        this.setItems(items);
        this.setSolutionSets(new LinkedList<List<KnapsackItem>>());
        this.populateSolutionSets();
    }

    protected final void setCapacity(final int capacity)
    {
        this.capacity = capacity;
    }

    protected final void setItems(final List<KnapsackItem> items)
    {
        this.items = items;
    }

    protected final void setSolutionSets(final List<List<KnapsackItem>> solutionSets)
    {
        this.solutionSets = solutionSets;
    }
}
