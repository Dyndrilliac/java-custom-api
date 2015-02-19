
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac StdStats.java
 * Execution: java StdStats < input.txt
 *
 * Library of statistical functions.
 *
 * The test client reads an array of real numbers from standard
 * input, and computes the minimum, mean, maximum, and
 * standard deviation.
 *
 * The functions all throw a NullPointerException if the array
 * passed in is null.
 *
 * The floating-point functions all return NaN if any input is NaN.
 *
 * Unlike Math.min() and Math.max(), the min() and max() functions
 * do not differentiate between -0.0 and 0.0.
 *
 * % more tiny.txt
 * 5
 * 3.0 1.0 2.0 5.0 4.0
 *
 * % java StdStats < tiny.txt
 * min 1.000
 * mean 3.000
 * max 5.000
 * std dev 1.581
 *
 * Should these funtions use varargs instead of array arguments?
 *
 *************************************************************************/

/**
 * <i>Standard statistics</i>. This class provides methods for computing
 * statistics such as min, max, mean, sample standard deviation, and
 * sample variance.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/22library">Section 2.2</a> of <i>Introduction to Programming in Java: An
 * Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class StdStats
{

	/**
	 * Test client.
	 * Convert command-line arguments to array of doubles and call various methods.
	 */
	public static void main(final String[] args)
	{
		double[] a = StdArrayIO.readDouble1D();
		StdOut.printf("       min %10.3f\n", StdStats.min(a));
		StdOut.printf("      mean %10.3f\n", StdStats.mean(a));
		StdOut.printf("       max %10.3f\n", StdStats.max(a));
		StdOut.printf("       sum %10.3f\n", StdStats.sum(a));
		StdOut.printf("    stddev %10.3f\n", StdStats.stddev(a));
		StdOut.printf("       var %10.3f\n", StdStats.var(a));
		StdOut.printf("   stddevp %10.3f\n", StdStats.stddevp(a));
		StdOut.printf("      varp %10.3f\n", StdStats.varp(a));
	}

	/**
	 * Returns the maximum value in the array a[], -infinity if no such value.
	 */
	public static double max(final double[] a)
	{
		double max = Double.NEGATIVE_INFINITY;
		for (double element: a)
		{
			if (Double.isNaN(element))
			{
				return Double.NaN;
			}
			if (element > max)
			{
				max = element;
			}
		}
		return max;
	}

	/**
	 * Returns the maximum value in the subarray a[lo..hi], -infinity if no such value.
	 */
	public static double max(final double[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		double max = Double.NEGATIVE_INFINITY;
		for (int i = lo; i <= hi; i++)
		{
			if (Double.isNaN(a[i]))
			{
				return Double.NaN;
			}
			if (a[i] > max)
			{
				max = a[i];
			}
		}
		return max;
	}

	/**
	 * Returns the maximum value in the array a[], Integer.MIN_VALUE if no such value.
	 */
	public static int max(final int[] a)
	{
		int max = Integer.MIN_VALUE;
		for (int element: a)
		{
			if (element > max)
			{
				max = element;
			}
		}
		return max;
	}

	/**
	 * Returns the average value in the array a[], NaN if no such value.
	 */
	public static double mean(final double[] a)
	{
		if (a.length == 0)
		{
			return Double.NaN;
		}
		double sum = StdStats.sum(a);
		return sum / a.length;
	}

	/**
	 * Returns the average value in the subarray a[lo..hi], NaN if no such value.
	 */
	public static double mean(final double[] a, final int lo, final int hi)
	{
		int length = (hi - lo) + 1;
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		if (length == 0)
		{
			return Double.NaN;
		}
		double sum = StdStats.sum(a, lo, hi);
		return sum / length;
	}

	/**
	 * Returns the average value in the array a[], NaN if no such value.
	 */
	public static double mean(final int[] a)
	{
		if (a.length == 0)
		{
			return Double.NaN;
		}
		double sum = 0.0;
		for (int element: a)
		{
			sum = sum + element;
		}
		return sum / a.length;
	}

	/**
	 * Returns the minimum value in the array a[], +infinity if no such value.
	 */
	public static double min(final double[] a)
	{
		double min = Double.POSITIVE_INFINITY;
		for (double element: a)
		{
			if (Double.isNaN(element))
			{
				return Double.NaN;
			}
			if (element < min)
			{
				min = element;
			}
		}
		return min;
	}

	/**
	 * Returns the minimum value in the subarray a[lo..hi], +infinity if no such value.
	 */
	public static double min(final double[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		double min = Double.POSITIVE_INFINITY;
		for (int i = lo; i <= hi; i++)
		{
			if (Double.isNaN(a[i]))
			{
				return Double.NaN;
			}
			if (a[i] < min)
			{
				min = a[i];
			}
		}
		return min;
	}

	/**
	 * Returns the minimum value in the array a[], Integer.MAX_VALUE if no such value.
	 */
	public static int min(final int[] a)
	{
		int min = Integer.MAX_VALUE;
		for (int element: a)
		{
			if (element < min)
			{
				min = element;
			}
		}
		return min;
	}

	/**
	 * Plots bars from (0, a[i]) to (i, a[i]) to standard draw.
	 */
	public static void plotBars(final double[] a)
	{
		int N = a.length;
		StdDraw.setXscale(0, N - 1);
		for (int i = 0; i < N; i++)
		{
			StdDraw.filledRectangle(i, a[i] / 2, .25, a[i] / 2);
		}
	}

	/**
	 * Plots line segments connecting points (i, a[i]) to standard draw.
	 */
	public static void plotLines(final double[] a)
	{
		int N = a.length;
		StdDraw.setXscale(0, N - 1);
		StdDraw.setPenRadius();
		for (int i = 1; i < N; i++)
		{
			StdDraw.line(i - 1, a[i - 1], i, a[i]);
		}
	}

	/**
	 * Plots the points (i, a[i]) to standard draw.
	 */
	public static void plotPoints(final double[] a)
	{
		int N = a.length;
		StdDraw.setXscale(0, N - 1);
		StdDraw.setPenRadius(1.0 / (3.0 * N));
		for (int i = 0; i < N; i++)
		{
			StdDraw.point(i, a[i]);
		}
	}

	/**
	 * Returns the sample standard deviation in the array a[], NaN if no such value.
	 */
	public static double stddev(final double[] a)
	{
		return Math.sqrt(StdStats.var(a));
	}

	/**
	 * Returns the sample standard deviation in the subarray a[lo..hi], NaN if no such value.
	 */
	public static double stddev(final double[] a, final int lo, final int hi)
	{
		return Math.sqrt(StdStats.var(a, lo, hi));
	}

	/**
	 * Returns the sample standard deviation in the array a[], NaN if no such value.
	 */
	public static double stddev(final int[] a)
	{
		return Math.sqrt(StdStats.var(a));
	}

	/**
	 * Returns the population standard deviation in the array a[], NaN if no such value.
	 */
	public static double stddevp(final double[] a)
	{
		return Math.sqrt(StdStats.varp(a));
	}

	/**
	 * Returns the population standard deviation in the subarray a[lo..hi], NaN if no such value.
	 */
	public static double stddevp(final double[] a, final int lo, final int hi)
	{
		return Math.sqrt(StdStats.varp(a, lo, hi));
	}

	/**
	 * Returns the sum of all values in the array a[].
	 */
	public static double sum(final double[] a)
	{
		double sum = 0.0;
		for (double element: a)
		{
			sum += element;
		}
		return sum;
	}

	/**
	 * Returns the sum of all values in the subarray a[lo..hi].
	 */
	public static double sum(final double[] a, final int lo, final int hi)
	{
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		double sum = 0.0;
		for (int i = lo; i <= hi; i++)
		{
			sum += a[i];
		}
		return sum;
	}

	/**
	 * Returns the sum of all values in the array a[].
	 */
	public static int sum(final int[] a)
	{
		int sum = 0;
		for (int element: a)
		{
			sum += element;
		}
		return sum;
	}

	/**
	 * Returns the sample variance in the array a[], NaN if no such value.
	 */
	public static double var(final double[] a)
	{
		if (a.length == 0)
		{
			return Double.NaN;
		}
		double avg = StdStats.mean(a);
		double sum = 0.0;
		for (double element: a)
		{
			sum += (element - avg) * (element - avg);
		}
		return sum / (a.length - 1);
	}

	/**
	 * Returns the sample variance in the subarray a[lo..hi], NaN if no such value.
	 */
	public static double var(final double[] a, final int lo, final int hi)
	{
		int length = (hi - lo) + 1;
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		if (length == 0)
		{
			return Double.NaN;
		}
		double avg = StdStats.mean(a, lo, hi);
		double sum = 0.0;
		for (int i = lo; i <= hi; i++)
		{
			sum += (a[i] - avg) * (a[i] - avg);
		}
		return sum / (length - 1);
	}

	/**
	 * Returns the sample variance in the array a[], NaN if no such value.
	 */
	public static double var(final int[] a)
	{
		if (a.length == 0)
		{
			return Double.NaN;
		}
		double avg = StdStats.mean(a);
		double sum = 0.0;
		for (int element: a)
		{
			sum += (element - avg) * (element - avg);
		}
		return sum / (a.length - 1);
	}

	/**
	 * Returns the population variance in the array a[], NaN if no such value.
	 */
	public static double varp(final double[] a)
	{
		if (a.length == 0)
		{
			return Double.NaN;
		}
		double avg = StdStats.mean(a);
		double sum = 0.0;
		for (double element: a)
		{
			sum += (element - avg) * (element - avg);
		}
		return sum / a.length;
	}

	/**
	 * Returns the population variance in the subarray a[lo..hi], NaN if no such value.
	 */
	public static double varp(final double[] a, final int lo, final int hi)
	{
		int length = (hi - lo) + 1;
		if ((lo < 0) || (hi >= a.length) || (lo > hi))
		{
			throw new RuntimeException("Subarray indices out of bounds");
		}
		if (length == 0)
		{
			return Double.NaN;
		}
		double avg = StdStats.mean(a, lo, hi);
		double sum = 0.0;
		for (int i = lo; i <= hi; i++)
		{
			sum += (a[i] - avg) * (a[i] - avg);
		}
		return sum / length;
	}

	private StdStats()
	{
	}
}
