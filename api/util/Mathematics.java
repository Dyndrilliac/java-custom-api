/*
 * Title: Mathematics
 * Author: Matthew Boyette
 * Date: 11/05/2014
 * 
 * This class is merely a collection of useful static methods that support code recycling.
 * Specifically, this class offers methods and classes which provide a means to perform mathematical operations.
 */

package api.util;

import edu.princeton.cs.introcs.StdRandom;

public final class Mathematics
{
    // Given an array of integers, find the pair of adjacent elements that has the largest product and return that product.
    public final static int adjacentElementsProduct(int[] inputArray)
    {
        int maxProduct = Integer.MIN_VALUE;
        int curProduct = 0;

        for ( int i = 0; i < ( inputArray.length - 1 ); i++ )
        {
            curProduct = inputArray[i] * inputArray[i + 1];
            maxProduct = Math.max(maxProduct, curProduct);
        }

        return maxProduct;
    }

    // Given a year, return the century it is in.
    // The first century spans from the year 1 up to and including the year 100, the second - from the year 101 up to and including the year 200, etc.
    // Guaranteed constraints: 1 ≤ year ≤ Integer.MAX_VALUE
    public final static int centuryFromYear(final int year)
    {
        return ( ( year % 100 ) != 0 ) ? ( ( year / 100 ) + 1 ) : year / 100;
    }

    // Recursive Fibonacci solver.
    /*
     * fib(0) == 0
     * fib(1) == 1
     * fib(n) == fib(n - 2) + fib(n - 1)
     * 
     * fib(0) = 0
     * fib(1) = 1
     * fib(2) = 0 + 1
     * fib(3) = 1 + 1
     * fib(4) = 1 + 2
     * fib(5) = 2 + 3
     * fib(6) = 3 + 5
     * fib(7) = 5 + 8
     */
    public final static long fib(final long n)
    {
        assert ( n >= 0 );

        if ( n == 0 ) return 0;

        if ( n == 1 ) return 1;

        return ( fib(n - 2) + fib(n - 1) );
    }

    // Pretty self-explanatory. Returns a random integer between 'min' and 'max'.
    // The user tells the method whether the desired maximum is inclusive or exclusive.
    // Exclusive range in interval notation: [min, max)
    // Inclusive range in interval notation: [min, max]
    public final static int getRandomInteger(final int min, final int max, final boolean isMaxInclusive)
    {
        int fMin = min, fMax = max;

        if ( isMaxInclusive )
        {
            fMax++;
        }

        return StdRandom.uniform(fMin, fMax);
    }

    // Pretty self-explanatory. Takes an array of integers, and returns the sum.
    public final static int getSumFromIntegerArray(final int[] arrayOfIntegers)
    {
        int sum = 0;

        for ( int arrayOfInteger : arrayOfIntegers )
        {
            sum += arrayOfInteger;
        }

        return sum;
    }

    public final static boolean isEven(final long n)
    {
        if ( ( n % 2 ) == 0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public final static boolean isOdd(final long n)
    {
        if ( ( n % 2 ) != 0 )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public final static boolean isPrime(final long n)
    {
        // Every prime number is an integer greater than one. If 'n' is less than or equal to one, mark it as composite (not prime).
        if ( n > 1 )
        {	// Check to make sure 'n' is not two or three. If it is either, than we can go ahead and mark it as prime.
            if ( ( n != 2 ) && ( n != 3 ) )
            {	// Since two and three have been handled, we want to know if 'n' is evenly divisible by two or three and mark it as composite.
                if ( Mathematics.isOdd(n) || ( ( n % 3 ) != 0 ) )
                {
                    // Every prime number can be represented by the form 6k+1 or 6k-1. If 'n' cannot be represented this way, then we mark it as composite.
                    if ( ( ( ( n + 1 ) % 6 ) == 0 ) || ( ( ( n - 1 ) % 6 ) == 0 ) )
                    {
                        // If a number can be factored into two numbers, at least one of them should be less than or equal to its square root.
                        long limit = (long) Math.ceil(Math.sqrt(n));

                        // Since we have eliminated all primes less than five, and two is the only even prime, we only need to check odd numbers from here on out.
                        for ( long i = 5; i <= limit; i += 2 )
                        {
                            // Every prime number is only evenly divisible by itself and one.
                            // 'i' will never equal 'n' and 'i' will never equal one.
                            // Thus, if 'n' is evenly divisible by 'i', then 'n' cannot be prime.
                            if ( ( n % i ) == 0 ) { return false; }
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
        return ( Math.log(n) / Math.log(base) );
    }

    // This method returns the first prime number greater than a given integer, if the given integer is not already prime.
    // Note that this method is subject to the size constraints of the primitive long type.
    // If the given integer is greater than the largest prime less than Long.MAX_VALUE (2^63-1), an IllegalArgumentException is thrown.
    public final static long makePrimeGreater(final long n)
    {
        long retVal = n;

        if ( retVal < 0 )
        {
            retVal = 0;
        }

        if ( retVal > 9223372036854775783L ) { throw new IllegalArgumentException("Argument causes buffer overflow; it's greater than the largest prime less than " + Long.MAX_VALUE + "."); }

        while ( Mathematics.isPrime(retVal) == false )
        {
            if ( Mathematics.isEven(retVal) )
            {
                if ( retVal == 0 )
                {
                    retVal += 2;
                }
                else
                {
                    retVal++;
                }
            }
            else
            {
                if ( retVal == 1 )
                {
                    retVal++;
                }
                else
                {
                    retVal += 2;
                }
            }
        }

        return retVal;
    }

    // This method returns the first prime number less than a given integer, if the given integer is not already prime.
    // Note that there are no prime numbers less than two.
    // If the given integer is less than two, an IllegalArgumentException is thrown.
    public final static long makePrimeLesser(final long n)
    {
        long retVal = n;

        if ( retVal < 2 ) { throw new IllegalArgumentException("There are no prime numbers less than two."); }

        while ( Mathematics.isPrime(retVal) == false )
        {
            if ( Mathematics.isEven(retVal) )
            {
                retVal--;
            }
            else
            {
                retVal -= 2;
            }
        }

        return retVal;
    }
}
