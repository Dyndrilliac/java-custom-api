
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac StdRandom.java
 * Execution: java StdRandom
 * Dependencies: StdOut.java
 *
 * A library of static methods to generate pseudo-random numbers from
 * different distributions (bernoulli, uniform, gaussian, discrete,
 * and exponential). Also includes a method for shuffling an array.
 *
 *
 * % java StdRandom 5
 * seed = 1316600602069
 * 59 16.81826 true 8.83954 0
 * 32 91.32098 true 9.11026 0
 * 35 10.11874 true 8.95396 3
 * 92 32.88401 true 8.87089 0
 * 72 92.55791 true 9.46241 0
 *
 * % java StdRandom 5
 * seed = 1316600616575
 * 96 60.17070 true 8.72821 0
 * 79 32.01607 true 8.58159 0
 * 81 59.49065 true 9.10423 1
 * 96 51.65818 true 9.02102 0
 * 99 17.55771 true 8.99762 0
 *
 * % java StdRandom 5 1316600616575
 * seed = 1316600616575
 * 96 60.17070 true 8.72821 0
 * 79 32.01607 true 8.58159 0
 * 81 59.49065 true 9.10423 1
 * 96 51.65818 true 9.02102 0
 * 99 17.55771 true 8.99762 0
 *
 *
 * Remark
 * ------
 * - Relies on randomness of nextDouble() method in java.util.Random
 * to generate pseudorandom numbers in [0, 1).
 *
 * - This library allows you to set and get the pseudorandom number seed.
 *
 * - See http://www.honeylocust.com/RngPack/ for an industrial
 * strength random number generator in Java.
 *
 *************************************************************************/

import java.util.Random;

/**
 * <i>Standard random</i>. This class provides methods for generating
 * random number from various distributions.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/22library">Section 2.2</a> of <i>Introduction to Programming in Java: An
 * Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 * @author Matthew Boyette
 */
public final class StdRandom
{

	private static Random	random; // pseudo-random number generator

	private static long		seed;	// pseudo-random number generator seed

	// static initializer
	static
	{
		// System.nanoTime() makes for a better seed than System.currentTimeMillis()
		StdRandom.seed = System.nanoTime();
		StdRandom.random = new Random(StdRandom.seed);
	}

	/**
	 * Returns a boolean, which is true with probability .5, and false otherwise.
	 */
	public static boolean bernoulli()
	{
		return StdRandom.bernoulli(0.5);
	}

	/**
	 * Returns a boolean, which is true with probability p, and false otherwise.
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
	 */
	public static boolean bernoulli(final double p)
	{
		if (!((p >= 0.0) && (p <= 1.0)))
		{
			throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
		}
		return StdRandom.uniform() < p;
	}

	/**
	 * Returns a real number with a Cauchy distribution.
	 */
	public static double cauchy()
	{
		return Math.tan(Math.PI * (StdRandom.uniform() - 0.5));
	}

	/**
	 * Returns a number from a discrete distribution: i with probability a[i].
	 * throws IllegalArgumentException if sum of array entries is not (very nearly) equal to <tt>1.0</tt> throws IllegalArgumentException unless
	 * <tt>a[i] >= 0.0</tt> for each index <tt>i</tt>
	 */
	public static int discrete(final double[] a)
	{
		double EPSILON = 1E-14;
		double sum = 0.0;
		for (int i = 0; i < a.length; i++)
		{
			if (!(a[i] >= 0.0))
			{
				throw new IllegalArgumentException("array entry " + i + " must be nonnegative: " + a[i]);
			}
			sum = sum + a[i];
		}
		if ((sum > (1.0 + EPSILON)) || (sum < (1.0 - EPSILON)))
		{
			throw new IllegalArgumentException("sum of array entries does not approximately equal 1.0: " + sum);
		}

		// the for loop may not return a value when both r is (nearly) 1.0 and when the
		// cumulative sum is less than 1.0 (as a result of floating-point roundoff error)
		while (true)
		{
			double r = StdRandom.uniform();
			sum = 0.0;
			for (int i = 0; i < a.length; i++)
			{
				sum = sum + a[i];
				if (sum > r)
				{
					return i;
				}
			}
		}
	}

	/**
	 * Returns a real number from an exponential distribution with rate lambda.
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>lambda > 0.0</tt>
	 */
	public static double exp(final double lambda)
	{
		if (!(lambda > 0.0))
		{
			throw new IllegalArgumentException("Rate lambda must be positive");
		}
		return -Math.log(1 - StdRandom.uniform()) / lambda;
	}

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS BELOW RELY ON JAVA.UTIL.RANDOM ONLY INDIRECTLY VIA
	// THE STATIC METHODS ABOVE.
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a real number with a standard Gaussian distribution.
	 */
	public static double gaussian()
	{
		// use the polar form of the Box-Muller transform
		double r, x, y;
		do
		{
			x = StdRandom.uniform(-1.0, 1.0);
			y = StdRandom.uniform(-1.0, 1.0);
			r = (x * x) + (y * y);
		}
		while ((r >= 1) || (r == 0));
		return x * Math.sqrt((-2 * Math.log(r)) / r);

		// Remark: y * Math.sqrt(-2 * Math.log(r) / r)
		// is an independent random gaussian
	}

	/**
	 * Returns a real number from a gaussian distribution with given mean and stddev
	 */
	public static double gaussian(final double mean, final double stddev)
	{
		return mean + (stddev * StdRandom.gaussian());
	}

	/**
	 * Returns an integer with a geometric distribution with mean 1/p.
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>p >= 0.0</tt> and <tt>p <= 1.0</tt>
	 */
	public static int geometric(final double p)
	{
		if (!((p >= 0.0) && (p <= 1.0)))
		{
			throw new IllegalArgumentException("Probability must be between 0.0 and 1.0");
		}
		// using algorithm given by Knuth
		return (int)Math.ceil(Math.log(StdRandom.uniform()) / Math.log(1.0 - p));
	}

	/**
	 * Returns the seed of the psedurandom number generator.
	 */
	public static long getSeed()
	{
		return StdRandom.seed;
	}

	/**
	 * Unit test.
	 */
	public static void main(final String[] args)
	{
		int N = Integer.parseInt(args[0]);
		if (args.length == 2)
		{
			StdRandom.setSeed(Long.parseLong(args[1]));
		}
		double[] t = {.5, .3, .1, .1};

		StdOut.println("seed = " + StdRandom.getSeed());
		for (int i = 0; i < N; i++)
		{
			StdOut.printf("%2d ", StdRandom.uniform(100));
			StdOut.printf("%8.5f ", StdRandom.uniform(10.0, 99.0));
			StdOut.printf("%5b ", StdRandom.bernoulli(.5));
			StdOut.printf("%7.5f ", StdRandom.gaussian(9.0, .2));
			StdOut.printf("%2d ", StdRandom.discrete(t));
			StdOut.println();
		}

		String[] a = "A B C D E F G".split(" ");
		for (String s: a)
		{
			StdOut.print(s + " ");
		}
		StdOut.println();
	}

	/**
	 * Returns a real number with a Pareto distribution with parameter alpha.
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>alpha > 0.0</tt>
	 */
	public static double pareto(final double alpha)
	{
		if (!(alpha > 0.0))
		{
			throw new IllegalArgumentException("Shape parameter alpha must be positive");
		}
		return Math.pow(1 - StdRandom.uniform(), -1.0 / alpha) - 1.0;
	}

	/**
	 * Return an integer with a Poisson distribution with mean lambda.
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>lambda > 0.0</tt> and not infinite
	 */
	public static int poisson(final double lambda)
	{
		if (!(lambda > 0.0))
		{
			throw new IllegalArgumentException("Parameter lambda must be positive");
		}
		if (Double.isInfinite(lambda))
		{
			throw new IllegalArgumentException("Parameter lambda must not be infinite");
		}
		// using algorithm given by Knuth
		// see http://en.wikipedia.org/wiki/Poisson_distribution
		int k = 0;
		double p = 1.0;
		double L = Math.exp(-lambda);
		do
		{
			k++;
			p *= StdRandom.uniform();
		}
		while (p >= L);
		return k - 1;
	}

	/**
	 * Returns a real number uniformly in [0, 1).
	 *
	 * @deprecated clearer to use {@link #uniform()}
	 */
	@Deprecated
	public static double random()
	{
		return StdRandom.uniform();
	}

	/**
	 * Sets the seed of the psedurandom number generator.
	 */
	public static void setSeed(final long s)
	{
		StdRandom.seed = s;
		StdRandom.random = new Random(StdRandom.seed);
	}

	/**
	 * Rearrange the elements of a double array in random order.
	 */
	public static void shuffle(final double[] a)
	{
		int N = a.length;
		for (int i = 0; i < N; i++)
		{
			int r = i + StdRandom.uniform(N - i);     // between i and N-1
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Rearrange the elements of the subarray a[lo..hi] in random order.
	 */
	public static void shuffle(final double[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (lo > hi) || (hi >= a.length))
		{
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++)
		{
			int r = i + StdRandom.uniform((hi - i) + 1);     // between i and hi
			double temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Rearrange the elements of an int array in random order.
	 */
	public static void shuffle(final int[] a)
	{
		int N = a.length;
		for (int i = 0; i < N; i++)
		{
			int r = i + StdRandom.uniform(N - i);     // between i and N-1
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Rearrange the elements of the subarray a[lo..hi] in random order.
	 */
	public static void shuffle(final int[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (lo > hi) || (hi >= a.length))
		{
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++)
		{
			int r = i + StdRandom.uniform((hi - i) + 1);     // between i and hi
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Rearrange the elements of an array in random order.
	 */
	public static void shuffle(final Object[] a)
	{
		int N = a.length;
		for (int i = 0; i < N; i++)
		{
			int r = i + StdRandom.uniform(N - i);     // between i and N-1
			Object temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Rearrange the elements of the subarray a[lo..hi] in random order.
	 */
	public static void shuffle(final Object[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (lo > hi) || (hi >= a.length))
		{
			throw new IndexOutOfBoundsException("Illegal subarray range");
		}
		for (int i = lo; i <= hi; i++)
		{
			int r = i + StdRandom.uniform((hi - i) + 1);     // between i and hi
			Object temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
	}

	/**
	 * Return real number uniformly in [0, 1).
	 */
	public static double uniform()
	{
		return StdRandom.random.nextDouble();
	}

	/**
	 * Returns a real number uniformly in [a, b).
	 *
	 * @throws IllegalArgumentException
	 *             unless <tt>a < b</tt>
	 */
	public static double uniform(final double a, final double b)
	{
		if (!(a < b))
		{
			throw new IllegalArgumentException("Invalid range");
		}
		return a + (StdRandom.uniform() * (b - a));
	}

	/**
	 * Returns an integer uniformly between 0 (inclusive) and N (exclusive).
	 *
	 * @throws IllegalArgumentException
	 *             if <tt>N <= 0</tt>
	 */
	public static int uniform(final int N)
	{
		if (N <= 0)
		{
			throw new IllegalArgumentException("Parameter N must be positive");
		}
		return StdRandom.random.nextInt(N);
	}

	/**
	 * Returns an integer uniformly in [a, b).
	 *
	 * @throws IllegalArgumentException
	 *             if <tt>b <= a</tt>
	 * @throws IllegalArgumentException
	 *             if <tt>b - a >= Integer.MAX_VALUE</tt>
	 */
	public static int uniform(final int a, final int b)
	{
		if (b <= a)
		{
			throw new IllegalArgumentException("Invalid range");
		}
		if (((long)b - a) >= Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException("Invalid range");
		}
		return a + StdRandom.uniform(b - a);
	}

	// don't instantiate
	private StdRandom()
	{
	}
}