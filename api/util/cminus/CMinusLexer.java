/*
 * Title: CMinusLexer
 * Author: Matthew Boyette
 * Date: 9/08/2016
 * 
 * This class functions as a generic lexical analyzer for the C-Minus language.
 */

package api.util.cminus;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import api.util.Lexer;
import api.util.Support;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusLexer<T> extends Lexer<T>
{
    /*
     * This helper enumerator class represents each of the various special types of tokens of interest.
     */
    public static enum TokenType
    {
        // @formatter:off
        COMMENT(Lexer.C_COMMENTS),
        GROUPING(CMinusLexer.C_GROUPINGS),
        KEYWORD(CMinusLexer.C_KEYWORDS),
        IDENTIFIER(CMinusLexer.C_IDENTIFIERS),
        NUMBER(CMinusLexer.C_NUMBERS),
        INTEGER(CMinusLexer.C_INTEGERS),
        FLOAT(CMinusLexer.C_FLOATS),
        OPERATOR(CMinusLexer.C_OPERATORS),
        WHITESPACE(Lexer.C_WHITESPACES),
        ERROR(CMinusLexer.C_ERRORS);
        // @formatter:on

        private String pattern;

        private TokenType(final String pattern)
        {
            this.pattern = pattern;
        }

        public final String getPattern()
        {
            return this.pattern;
        }

        @Override
        public final String toString()
        {
            return this.name();
        }
    }

    protected static class CMinusLexException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private Token<TokenType> token;

        public CMinusLexException(final Token<TokenType> token)
        {
            super("*ERROR*: Lexical error on token \"" + token.getData() + "\", line " + token.getLineNo() + ".");
            this.setToken(token);
        }

        public final Token<TokenType> getToken()
        {
            return this.token;
        }

        protected final void setToken(final Token<TokenType> token)
        {
            this.token = token;
        }
    }

    // RegExr patterns describing the various components of the C-Minus language grammar.
    public static final String C_ERRORS      = "([^\\(\\)\\{\\}\\[\\]\\+\\-\\*\\/\\<\\>\\=,;\\s]+)";
    public static final String C_FLOATS      = "([^\\s\\S])";
    public static final String C_GROUPINGS   = "([\\(\\)\\{\\}\\[\\],;])";
    public static final String C_IDENTIFIERS = "([A-Za-z]+)";
    public static final String C_INTEGERS    = "([^\\s\\S])";
    public static final String C_KEYWORDS    = "(\\b((else)|(float)|(if)|(int)|(return)|(void)|(while))\\b)";
    public static final String C_NUMBERS     = "((\\B)?\\d+(\\.\\d+)?((E|e)(\\+|\\-)?\\d+)?)";
    public static final String C_OPERATORS   = "(\\<\\=)|(\\>\\=)|(\\=\\=)|(\\!\\=)|([\\+\\-\\*\\/\\<\\>\\=])";

    public static void main(final String[] args)
    {
        // It comes with a limited test bed program so you can lex arbitrary input quickly.
        StdOut.println("RegExr Grammars:");
        StdOut.println("COMMENTS   :  " + Lexer.C_COMMENTS);
        StdOut.println("GROUPINGS  :  " + CMinusLexer.C_GROUPINGS);
        StdOut.println("KEYWORDS   :  " + CMinusLexer.C_KEYWORDS);
        StdOut.println("IDENTIFIERS:  " + CMinusLexer.C_IDENTIFIERS);
        StdOut.println("INTEGERS   :  " + CMinusLexer.C_NUMBERS);
        StdOut.println("FLOATS     :  " + CMinusLexer.C_NUMBERS);
        StdOut.println("OPERATORS  :  " + CMinusLexer.C_OPERATORS);
        StdOut.println("WHITESPACES:  " + Lexer.C_WHITESPACES);
        StdOut.println();

        String input = Support.getInputString(null, "Please provide an expression.", "Expression Lexer Input");
        ( new CMinusLexer<TokenType>() ).lex(input, 0, false, true, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Token<T>> lex(final String s, final int lineNo, final boolean silent, final boolean ignoreWhiteSpace, final boolean ignoreComments)
    {
        // Skip empty lines.
        if ( s.isEmpty() )
        {
            if ( !silent )
            {
                StdOut.println();
            }

            return ( new LinkedList<Token<T>>() );
        }

        if ( !silent )
        {
            // Echo input.
            StdOut.println("INPUT: " + s);
        }

        // A buffer for the tokens we want to return.
        LinkedList<Token<T>> tokens = ( new LinkedList<Token<T>>() );

        // Lexer logic begins here.
        StringBuffer tokenPatternsBuffer = new StringBuffer();

        for ( TokenType tokenType : TokenType.values() )
        {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType, tokenType.getPattern()));
        }

        Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

        // Begin matching tokens using the designated RegExr patterns.
        Matcher matcher = tokenPatterns.matcher(s);

        while ( matcher.find() )
        {
            Token<T> token = null;

            if ( this.Depth[DepthType.COMMENT.ordinal()] > 0 )
            {
                if ( matcher.group(TokenType.COMMENT.name()) != null )
                {
                    token = new Token<T>((T) TokenType.COMMENT, matcher.group(TokenType.COMMENT.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);

                    if ( token.getData().contentEquals("/*") )
                    {
                        this.Depth[DepthType.COMMENT.ordinal()]++;
                    }
                    else if ( token.getData().contentEquals("*/") )
                    {
                        this.Depth[DepthType.COMMENT.ordinal()]--;
                    }
                }

                if ( ignoreComments )
                {
                    continue;
                }
            }
            else
            {
                if ( matcher.group(TokenType.COMMENT.name()) != null )
                {
                    token = new Token<T>((T) TokenType.COMMENT, matcher.group(TokenType.COMMENT.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);

                    if ( token.getData().contentEquals("/*") )
                    {
                        this.Depth[DepthType.COMMENT.ordinal()]++;
                    }
                    else if ( token.getData().contentEquals("*/") )
                    {
                        Token<T> new_tok1 = new Token<T>((T) TokenType.OPERATOR, "*", lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                        tokens.add(new_tok1);

                        Token<T> new_tok2 = new Token<T>((T) TokenType.OPERATOR, "/", lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                        tokens.add(new_tok2);

                        if ( !silent )
                        {
                            StdOut.println(new_tok1);
                            StdOut.println(new_tok2);
                        }

                        continue;
                    }

                    if ( ignoreComments )
                    {
                        continue;
                    }
                }
                else if ( matcher.group(TokenType.GROUPING.name()) != null )
                {
                    token = new Token<T>((T) TokenType.GROUPING, matcher.group(TokenType.GROUPING.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);

                    switch ( token.getData() )
                    {
                        case "(":
                            this.Depth[DepthType.PARENTH.ordinal()]++;
                            break;

                        case ")":
                            this.Depth[DepthType.PARENTH.ordinal()]--;
                            break;

                        case "[":
                            this.Depth[DepthType.BRACKET.ordinal()]++;
                            break;

                        case "]":
                            this.Depth[DepthType.BRACKET.ordinal()]--;
                            break;

                        case "{":
                            this.Depth[DepthType.BRACE.ordinal()]++;
                            break;

                        case "}":
                            this.Depth[DepthType.BRACE.ordinal()]--;
                            break;

                        default:
                            break;
                    }
                }
                else if ( matcher.group(TokenType.KEYWORD.name()) != null )
                {
                    token = new Token<T>((T) TokenType.KEYWORD, matcher.group(TokenType.KEYWORD.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                }
                else if ( matcher.group(TokenType.IDENTIFIER.name()) != null )
                {
                    token = new Token<T>((T) TokenType.IDENTIFIER, matcher.group(TokenType.IDENTIFIER.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                }
                else if ( matcher.group(TokenType.NUMBER.name()) != null )
                {
                    if ( Support.isStringParsedAsInteger(matcher.group(TokenType.NUMBER.name())) )
                    {
                        token = new Token<T>((T) TokenType.INTEGER, matcher.group(TokenType.NUMBER.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                    }
                    else if ( Support.isStringParsedAsDouble(matcher.group(TokenType.NUMBER.name())) )
                    {
                        token = new Token<T>((T) TokenType.FLOAT, matcher.group(TokenType.NUMBER.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                    }
                    else
                    {
                        token = new Token<T>((T) TokenType.NUMBER, matcher.group(TokenType.NUMBER.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                    }
                }
                else if ( matcher.group(TokenType.OPERATOR.name()) != null )
                {
                    token = new Token<T>((T) TokenType.OPERATOR, matcher.group(TokenType.OPERATOR.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);
                }
                else if ( matcher.group(TokenType.WHITESPACE.name()) != null )
                {
                    token = new Token<T>((T) TokenType.WHITESPACE, matcher.group(TokenType.WHITESPACE.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);

                    if ( ignoreWhiteSpace )
                    {
                        continue;
                    }
                }
                else if ( matcher.group(TokenType.ERROR.name()) != null )
                {
                    token = new Token<T>((T) TokenType.ERROR, matcher.group(TokenType.ERROR.name()), lineNo, this.Depth[DepthType.BRACE.ordinal()], this.Depth[DepthType.BRACKET.ordinal()], this.Depth[DepthType.PARENTH.ordinal()]);

                    try
                    {
                        throw new CMinusLexException((Token<TokenType>) token);
                    }
                    catch ( final CMinusLexException cmle )
                    {
                        StdOut.println(cmle.getMessage());
                    }

                    continue;
                }
            }

            tokens.add(token);

            if ( !silent )
            {
                StdOut.println(token);
            }
        }

        return tokens;
    }
}
