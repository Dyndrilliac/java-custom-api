package api.util;

import static org.junit.Assert.fail;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import edu.princeton.cs.algs4.StdOut;

public class SupportTest
{
    List<Integer> failedTests;
    boolean       testResults;

    @Test
    public void testCheckPalindrome()
    {
        // Test data.
        final String[] testStrings = { "", "aabaa", "abac", "a", "az", "abacaba", "z", "aaabaaaa", "zzzazzazz", "hlbeeykoqqqqokyeeblh" };
        final boolean[] testBools = { true, true, false, true, false, true, true, false, false, true, true };

        // Reset the test tracking variables.
        failedTests = new LinkedList<Integer>();
        testResults = true;

        // Run the tests in a loop.
        for ( int i = 0; i < testStrings.length; i++ )
        {
            if ( Support.checkPalindrome(testStrings[i]) != testBools[i] )
            {
                StdOut.println("Support.checkPalindrome(" + testStrings[i] + ") = " + testBools[i]);
                testResults = false;
                failedTests.add(i);
            }
        }

        // If any of the tests failed, print a message to the console, and also print which tests failed.
        if ( !testResults ) fail("Unit test for \'Support.checkPalindrome\' failed on test numbers:\n\t" + failedTests.toString());
    }
}
