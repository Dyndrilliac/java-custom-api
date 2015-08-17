/*
 * Title: Lexer
 * Author: Matthew Boyette
 * Date: 3/27/2015
 * 
 * This class functions as a generic lexical analyzer.
 */

package api.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.util.datastructures.Token;
import api.util.stdlib.StdOut;

public class Lexer<T>
{
	/*
	 * This helper enum class represents the various special types of tokens we are interested in.
	 */
	public static enum TokenType
	{
		GROUPING(Lexer.GROUPINGS),
		IDENTIFIER(Lexer.IDENTIFIERS),
		NUMBER(Lexer.NUMBERS),
		OPERATOR(Lexer.OPERATORS);
		
		private final String	pattern;
		
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
	
	public static final String	GROUPINGS	= "[\\(\\)\\{\\}\\[\\]<>,\"\';:\\?]";
	public static final String	IDENTIFIERS	= "\\b(\\w+\\.)?\\w+\\b";
	public static final String	INTEGERS	= "\\b\\d+\\b";
	public static final String	NUMBERS		= "\\b(\\d*\\.)?\\d+\\b";
	public static final String	OPERATORS	= "[`~!@#$%^&*\\-=+|]";
	
	public static void main(final String[] args)
	{
		// It comes with a limited test bed program so you can lex arbitrary input quickly.
		StdOut.println("RegEx Grammars:");
		StdOut.println("GROUPINGS:\t" + Lexer.GROUPINGS);
		StdOut.println("IDENTIFIERS:\t" + Lexer.IDENTIFIERS);
		StdOut.println("NUMBERS:\t" + Lexer.NUMBERS);
		StdOut.println("OPERATORS:\t" + Lexer.OPERATORS);
		
		String input = Support.getInputString(null,
			"Please provide an expression.",
			"Expression Lexer Input");
		
		ArrayList<Token<TokenType>> tokens = (new Lexer<TokenType>()).lex(input);
		
		StdOut.println("\nInput:\t" + input + "\n");
		
		for (Token<TokenType> token: tokens)
		{
			StdOut.println(token);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Token<T>> lex(final String s)
	{
		// Strip out whitespace.
		String input = s.replaceAll("\\s+", " ");
		
		// The tokens to return.
		ArrayList<Token<T>> tokens = new ArrayList<Token<T>>();
		
		// Lexer logic begins here.
		StringBuffer tokenPatternsBuffer = new StringBuffer();
		
		for (TokenType tokenType: TokenType.values())
		{
			tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType, tokenType.getPattern()));
		}
		
		Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));
		
		// Begin matching tokens.
		Matcher matcher = tokenPatterns.matcher(input);
		
		while (matcher.find())
		{
			Token<T> token = null;
			
			if (matcher.group(TokenType.GROUPING.name()) != null)
			{
				token = new Token<T>((T)TokenType.GROUPING, matcher.group(TokenType.GROUPING.name()));
				tokens.add(token);
				continue;
			}
			else
				if (matcher.group(TokenType.NUMBER.name()) != null)
				{
					token = new Token<T>((T)TokenType.NUMBER, matcher.group(TokenType.NUMBER.name()));
					tokens.add(token);
					continue;
				}
				else
					if (matcher.group(TokenType.IDENTIFIER.name()) != null)
					{
						token = new Token<T>((T)TokenType.IDENTIFIER, matcher.group(TokenType.IDENTIFIER.name()));
						tokens.add(token);
						continue;
					}
					else
						if (matcher.group(TokenType.OPERATOR.name()) != null)
						{
							token = new Token<T>((T)TokenType.OPERATOR, matcher.group(TokenType.OPERATOR.name()));
							tokens.add(token);
							continue;
						}
		}
		
		return tokens;
	}
}