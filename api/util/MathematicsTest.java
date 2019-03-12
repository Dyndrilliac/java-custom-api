package api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import edu.princeton.cs.algs4.StdOut;

public class MathematicsTest
{
    List<Integer> failedTests;
    boolean       testResults;

    @Test
    public void testAdjacentElementsProduct()
    {
        // Test data.
        final int[][] testArrays = { { 3, 6, -2, -5, 7, 3 }, { -1, -2 }, { 5, 1, 2, 3, 1, 4 }, { 1, 2, 3, 0 }, { 9, 5, 10, 2, 24, -1, -48 }, { 5, 6, -4, 2, 3, 2, -23 }, { 4, 1, 2, 3, 1, 5 }, { -23, 4, -3, 8, -12 }, { 1, 0, 1, 0, 1000 } };
        final int[] testProducts = { 21, 2, 6, 6, 50, 30, 6, -12, 0 };

        // Reset the test tracking variables.
        failedTests = new LinkedList<Integer>();
        testResults = true;

        // Cannot proceed unless the number of test arrays matches the number of test products.
        assertEquals(testArrays.length, testProducts.length);

        // Run the tests in a loop.
        for ( int i = 0; i < testArrays.length; i++ )
        {
            if ( Mathematics.adjacentElementsProduct(testArrays[i]) != testProducts[i] )
            {
                StdOut.println("Mathematics.adjacentElementsProduct(" + Arrays.toString(testArrays[i]) + ") = " + testProducts[i]);
                testResults = false;
                failedTests.add(i);
            }
        }

        // If any of the tests failed, print a message to the console, and also print which tests failed.
        if ( !testResults ) fail("Unit test for \'Mathematics.adjacentElementsProduct\' failed on test numbers:\n\t" + failedTests.toString());
    }

    @Test
    public void testCenturyFromYear()
    {
        // Test data.
        final int[] testCenturies = { 20, 17, 20, 20, 21, 2, 4, 1, 1 };
        final int[] testYears = { 1905, 1700, 1988, 2000, 2001, 200, 374, 45, 8 };

        // Reset the test tracking variables.
        failedTests = new LinkedList<Integer>();
        testResults = true;

        // Cannot proceed unless the number of test years matches the number of test centuries.
        assertEquals(testYears.length, testCenturies.length);

        // Run the tests in a loop.
        for ( int i = 0; i < testYears.length; i++ )
        {
            if ( Mathematics.centuryFromYear(testYears[i]) != testCenturies[i] )
            {
                StdOut.println("Mathematics.centuryFromYear(" + testYears[i] + ") = " + testCenturies[i]);
                testResults = false;
                failedTests.add(i);
            }
        }

        // If any of the tests failed, print a message to the console, and also print which tests failed.
        if ( !testResults ) fail("Unit test for \'Mathematics.centuryFromYear\' failed on test numbers:\n\t" + failedTests.toString());
    }
}
