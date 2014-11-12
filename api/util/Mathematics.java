/*
	Title:  Mathematics
	Author: Matthew Boyette
	Date:   11/05/2014
	
	This class is merely a collection of useful static methods that support code recycling. Specifically, this
	class offers methods and classes which provide a means to perform mathematic operations.
*/

package api.util;

import java.util.Random;

public final class Mathematics
{
	// Pretty self-explanatory. Returns a random integer between 'min' and 'max'.
	// The user tells the method whether the desired maximum is inclusive or exclusive.
	// Exclusive range in interval notation: [min, max)
	// Inclusive range in interval notation: [min, max]
	public final static int getRandomInteger(final int min, final int max, final boolean isMaxInclusive)
	{
		Random randomGenerator = new Random(System.nanoTime());
		
		if (isMaxInclusive)
		{
			return (randomGenerator.nextInt((max - min) + 1) + min);
		}
		else
		{
			return (randomGenerator.nextInt(max - min) + min);
		}
	}
	
	// Pretty self-explanatory. Takes an array of integers, and returns the sum.
	public final static int getSumFromIntegerArray(final int[] arrayOfIntegers)
	{
		int sum = 0;
		
		for (int arrayOfInteger: arrayOfIntegers)
		{
			sum += arrayOfInteger;
		}
		
		return sum;
	}
	
	public final static boolean isPrime(final long n)
	{
		// Every prime number is an integer greater than one. If 'n' is less than or equal to one, mark it as composite (not prime).
		if (n > 1)
		{	// Check to make sure 'n' is not two or three. If it is either, than we can go ahead and mark it as prime.
			if ((n != 2) && (n != 3))
			{	// Since two and three have been handled, we want to know if 'n' is evenly divisible by two or three and mark it as composite.
				if (((n % 2) != 0) || ((n % 3) != 0))
				{
					// Every prime number can be represented by the form 6k+1 or 6k-1. If 'n' cannot be represented this way,
					// then we mark it as composite.
					if ((((n + 1) % 6) == 0) || (((n - 1) % 6) == 0))
					{
						// If a number can be factored into two numbers, at least one of them should be less than or equal to its square root.
						long limit = (long)Math.ceil(Math.sqrt(n));
						// Since we have eliminated all primes less than five, and two is the only even prime,
						// we only need to check odd numbers from here on out.
						for (long i = 5; i <= limit; i += 2)
						{
							// Every prime number is only evenly divisible by itself and one.
							// 'i' will never equal 'n' and 'i' will never equal one.
							// Thus, if 'n' is evenly divisible by 'i' then it cannot be prime.
							if ((n % i) == 0)
							{
								return false;
							}
						}
						
						return true;
					}
				}
			}
			else
			{
				return true;
			}
		}
		
		return false;
	}
	
	// Logarithm of arbitrary base.
	public final static double logarithm(final double n, final double base)
	{
		return (Math.log(n)/Math.log(base));
	}
}