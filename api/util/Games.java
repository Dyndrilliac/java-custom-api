/*
	Title:  Games
	Author: Matthew Boyette
	Date:   1/21/2012
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, this 
	class offers methods which would be useful for games, animation, and utilities that are meant to accompany 
	games.
*/
package api.util;

import java.util.Random;

public class Games
{
	// Pretty self-explanatory. Returns a random integer between 'min' and 'max'.
	// The user tells the method whether the desired maximum is inclusive or exclusive.
	// Exclusive range in interval notation: [min, max)
	// Inclusive range in interval notation: [min, max]
	public static int getRandomInteger(final int min, final int max, final boolean isMaxInclusive)
	{
		Random randomGenerator = new Random(System.nanoTime());

		if (isMaxInclusive)
		{
			return (randomGenerator.nextInt(max - min + 1) + min);
		}
		else
		{
			return (randomGenerator.nextInt(max - min) + min);
		}
	}
	
	// Pretty self-explanatory. Takes an array of integers, and returns the sum.
	public static int getSumFromIntegerArray(final int[] arrayOfIntegers)
	{
		int sum = 0;

		for (int i = 0; i < arrayOfIntegers.length; i++)
		{
			sum += arrayOfIntegers[i];
		}

		return sum;
	}
	
	/*
		This method is useful in games like Dungeons and Dragons. The user provides the number of dice and the number 
		of sides each die has. The resulting array contains the result of each die separately and the final element in 
		the array is the sum of all the dice.
	*/
	public static int[] throwDice(final int numberOfDice, final int numberOfSides)
	{
		int[] resultsArray = new int[numberOfDice+1];

		for (int i = 0; i < numberOfDice; i++)
		{
			resultsArray[i] = getRandomInteger(1, numberOfSides, true);
		}

		resultsArray[numberOfDice] = getSumFromIntegerArray(resultsArray);

		return resultsArray;
	}
}