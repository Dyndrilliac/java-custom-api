/*
 * Title: Lexer
 * Author: Matthew Boyette
 * Date: 9/25/2016
 * 
 * This class functions as a generic extendible lexical analyzer.
 * It's default settings are for the for the C language.
 */

package api.util;

import java.util.LinkedList;
import java.util.List;
import api.util.datastructures.Token;
import edu.princeton.cs.introcs.In;

public abstract class Lexer<T>
{
    /*
     * This helper enumerator class represents each of the various special types of depth we are tracking for each token.
     */
    protected static enum DepthType
    {
        BRACE, BRACKET, COMMENT, PARENTH
    }

    // RegExr patterns describing the various components of the C language grammar.
    public static final String C_ASSIGNMENT_OPS = "(\\+\\=)|(\\-\\=)|(\\*\\=)|(\\/\\=)|(%\\=)|(&\\=)|(\\^\\=)|(\\|\\=)|(\\=)|(\\=)|(\\=)";
    public static final String C_BIT_SHIFT_OPS  = "(\\<\\<)|(\\>\\>)";
    public static final String C_COMMENTS       = "(\\/\\/.*$)|(\\/\\*)|(\\*\\/)";
    public static final String C_COMPARISON_OPS = "(&&)|(\\|\\|)|(&)|(\\^)|(\\|)";
    public static final String C_CONDITION_OPS  = "(:)|(\\?)";
    public static final String C_ERRORS         = "([^\\(\\)\\{\\}\\[\\]\\.\\?:;,&%\\+\\-\\*\\/\\<\\>\\=\\!\\^\\|\\s]+)";
    public static final String C_GROUPINGS      = "([\\(\\)\\{\\}\\[\\]])|(\")|(\')|(,)|(;)|(\\.)|(\\-\\>)";
    public static final String C_IDENTIFIERS    = "([A-Za-z_]\\w*)";
    public static final String C_INCR_DECR_OPS  = "(\\+\\+)|(\\-\\-)";
    public static final String C_KEYWORDS       = "(\\b((auto)|(break)|(case)|(char)|(const)|(continue)|(default)|(do)|(double)|(else)|(enum)|(extern)|(float)|(for)|(goto)|(if)|(int)|(long)|(register)|(return)|(short)|(signed)|(sizeof)|(static)|(struct)|(switch)|(typedef)|(union)|(unsigned)|(void)|(volatile)|(while))\\b)";
    public static final String C_MATHEMATIC_OPS = "(\\+)|(\\-)|(\\*)|(\\/)|(%)";
    public static final String C_NUMBERS        = "((\\-)?\\d+(\\.\\d+)?((E|e)(\\+|\\-)?\\d+)?)|((\\-)?\\.\\d+((E|e)(\\+|\\-)?\\d+)?)";
    public static final String C_OPERATORS      = C_INCR_DECR_OPS + "|" + C_BIT_SHIFT_OPS + "|" + C_CONDITION_OPS + "|" + C_ASSIGNMENT_OPS + "|" + C_COMPARISON_OPS + "|" + C_MATHEMATIC_OPS;
    public static final String C_WHITESPACES    = "(\\s+)";

    // Internal Depth Tracking Array
    protected int[] Depth = { 0, 0, 0, 0 };

    public List<Token<T>> lex(final String s)
    {
        return this.lex(s, 0);
    }

    public List<Token<T>> lex(final String s, final int lineNo)
    {
        return this.lex(s, lineNo, true);
    }

    public List<Token<T>> lex(final String s, final int lineNo, final boolean silent)
    {
        return this.lex(s, lineNo, silent, true);
    }

    public List<Token<T>> lex(final String s, final int lineNo, final boolean silent, final boolean ignoreWhiteSpace)
    {
        return this.lex(s, lineNo, silent, ignoreWhiteSpace, true);
    }

    public abstract List<Token<T>> lex(final String s, final int lineNo, final boolean silent, final boolean ignoreWhiteSpace, final boolean ignoreComments);

    public List<Token<T>> lexFile(final String fileName)
    {
        return this.lexFile(fileName, false);
    }

    public List<Token<T>> lexFile(final String fileName, final boolean silent)
    {
        return this.lexFile(fileName, silent, false);
    }

    public List<Token<T>> lexFile(final String fileName, final boolean silent, final boolean ignoreWhiteSpace)
    {
        return this.lexFile(fileName, silent, ignoreWhiteSpace, true);
    }

    public List<Token<T>> lexFile(final String fileName, final boolean silent, final boolean ignoreWhiteSpace, final boolean ignoreComments)
    {
        // A buffer for the tokens we want to return.
        List<Token<T>> tokens = new LinkedList<Token<T>>();

        In inputStream = null;
        int lineNo = 0;

        try
        {
            // Try to open the given input file for a read operation.
            inputStream = new In(fileName);

            // Pass each line of text from the input file to lex(), and add all the returned tokens to our output token buffer.
            while ( inputStream.hasNextLine() )
            {
                lineNo++;
                tokens.addAll(this.lex(inputStream.readLine(), lineNo, silent, ignoreWhiteSpace, ignoreComments));
            }
        }
        catch ( final IllegalArgumentException iae )
        {
            iae.printStackTrace();
        }
        catch ( final NullPointerException npe )
        {
            npe.printStackTrace();
        }
        finally
        {
            if ( inputStream != null )
            {
                inputStream.close();
                inputStream = null;
            }
        }

        return tokens;
    }
}
