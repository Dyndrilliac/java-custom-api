/*
 * Title: Games
 * Author: Matthew Boyette
 * Date: 1/21/2012
 *
 * This class is merely a collection of useful static methods that support code recycling. Specifically, this
 * class offers methods and classes which would be useful for games, animation, or utilities that are meant to
 * accompany games and animation.
 */

package api.util;

public final class Games
{
	/*
	 * This method is useful in games like Dungeons and Dragons. The user provides the number of dice and the number
	 * of sides each die has. The resulting array contains the result of each die separately and the final element in
	 * the array is the sum of all the dice.
	 */
	public final static int[] throwDice(final int numberOfDice, final int numberOfSides)
	{
		int[] resultsArray = new int[numberOfDice + 1];

		for (int i = 0; i < numberOfDice; i++)
		{
			resultsArray[i] = Mathematics.getRandomInteger(1, numberOfSides, true);
		}

		resultsArray[numberOfDice] = Mathematics.getSumFromIntegerArray(resultsArray);

		return resultsArray;
	}
}