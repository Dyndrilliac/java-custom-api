/*
 * Title: CMinusParser
 * Author: Matthew Boyette
 * Date: 10/09/2016
 * 
 * This class functions as a generic syntactical analyzer for the C-Minus language.
 * 
 * TODO: Finish code for identifier parameters.
 * TODO: Finish code for recognizing function declarations.
 * TODO: Add identifiers to the appropriate symbol table.
 */

package api.util.cminus;

import java.util.LinkedList;
import java.util.List;
import api.util.cminus.CMinusLexer.TokenType;
import api.util.datastructures.SeparateChainingSymbolTable;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusParser
{
    /* @formatter:off
     * 
     * C-Minus Grammar Production Rules
     * 
     * <fun-declaration> -> <type-specifier> ID ( <params> ) <compound-stmt>
     * <params> -> <param-list> | VOID
     * <param-list> -> <param-list> , <param> | <param>
     * <param> -> <type-specifier> ID | <type-specifier> ID [ ]
     * <compound-stmt> -> { <local-declarations> <statement-list> }
     * <local-declarations> -> <local-declarations> <var-declaration> | <empty>
     * <statement-list> -> <statement-list> <statement> | <empty>
     * <statement> ->  <expression-stmt> | <compound-stmt> | <selection-stmt> | <iteration-stmt> | <return-stmt>
     * <expression-stmt> -> <expression> ; | ;
     * <selection-stmt> -> IF ( <expression> ) <statement> | IF ( <expression> ) <statement> ELSE <statement>
     * <iteration-stmt> -> WHILE  ( <expression> ) <statement>
     * <return-stmt> -> RETURN ; | RETURN <expression> ;
     * <expression> -> <var> = <expression> | <simple-expression>
     * <var> -> ID | ID [ <expression> ]
     * <simple-expression> -> <additive-expression> <relop> <additive-expression> | <additive-expression>
     * <additive-expression> -> <additive-expression> <addop> <term> | <term>
     * <term> -> <term> <mulop> <factor> | <factor>
     * <factor> -> ( <expression> ) | <var> | <call> | NUM
     * <call> -> ID ( <args> )
     * <args> -> <arg-list> | <empty>
     * <arg-list> -> <arg-list> , <expression> | <expression>
     * 
     * @formatter:on
     */

    public static class CMinusIdentifierParameters
    {
        // TODO: Finish code for identifier parameters.
    }

    protected static class CMinusParseException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private Token<CMinusLexer.TokenType> token;

        public CMinusParseException(final Token<CMinusLexer.TokenType> token)
        {
            super("*ERROR*: Syntax error on token \"" + token.getData() + "\", line " + token.getLineNo() + ".");
            this.setToken(token);
        }

        public final Token<CMinusLexer.TokenType> getToken()
        {
            return token;
        }

        protected final void setToken(final Token<CMinusLexer.TokenType> token)
        {
            this.token = token;
        }
    }

    protected static class CMinusParseResult
    {
        public static enum Type
        {
            ACCEPT, REJECT
        }

        public final int  begIndex;
        public final int  endIndex;
        public final Type resultType;

        public CMinusParseResult(final Type resultType, final int begIndex, final int endIndex)
        {
            this.resultType = resultType;
            this.begIndex = begIndex;
            this.endIndex = endIndex;
        }
    }

    private static final CMinusParseResult declaration(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        CMinusParseResult cmprA = CMinusParser.funDeclaration(tokens, index);
        CMinusParseResult cmprB = CMinusParser.variableDeclaration(tokens, index);

        if ( cmprA.resultType == CMinusParseResult.Type.ACCEPT )
        {
            return cmprA;
        }
        else if ( cmprB.resultType == CMinusParseResult.Type.ACCEPT )
        {
            return cmprB;
        }
        else
        {
            if ( cmprA.endIndex >= cmprB.endIndex )
            {
                return cmprA;
            }
            else
            {
                return cmprB;
            }
        }
    }

    private static final CMinusParseResult declarationList(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        CMinusParseResult declarationResult = CMinusParser.declaration(tokens, index);
        List<CMinusParseResult> declarationResults = new LinkedList<CMinusParseResult>();

        while ( declarationResult.resultType == CMinusParseResult.Type.ACCEPT )
        {
            declarationResults.add(declarationResult);
            declarationResult = CMinusParser.declaration(tokens, declarationResult.endIndex);
        }

        if ( declarationResults.isEmpty() )
        {
            return declarationResult;
        }
        else
        {
            declarationResult = declarationResults.get(declarationResults.size() - 1);
            return declarationResult;
        }
    }

    private static final CMinusParseResult funDeclaration(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        // TODO: Add identifiers to the appropriate symbol table.
        Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, index);
        int newIndex = index;

        try
        {
            if ( CMinusParser.isTypeSpecifier(token) )
            {
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isIdentifier(token) )
                {
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, "(") )
                    {
                        newIndex++;
                        token = CMinusParser.getToken(tokens, newIndex);

                        // TODO: Finish code for recognizing function declarations.
                    }
                }
            }
        }
        catch ( final NullPointerException npe )
        {
            newIndex--;
        }

        return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
    }

    private static final Token<CMinusLexer.TokenType> getToken(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        boolean validIndex = ( ( index >= 0 ) && ( index < tokens.size() ) );

        if ( validIndex )
        {
            return tokens.get(index);
        }
        else
        {
            return null;
        }
    }

    private static final boolean isAddOp(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.OPERATOR )
        {
            switch ( token.getData() )
            {
                case "+":
                    return true;

                case "-":
                    return true;

                default:
                    break;
            }
        }

        return false;
    }

    private static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( token.getType() == TokenType.GROUPING )
        {
            if ( token.getData().contentEquals(symbol) ) { return true; }
        }

        return false;
    }

    private static final boolean isIdentifier(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.IDENTIFIER ) { return true; }

        return false;
    }

    private static final boolean isMulOp(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.OPERATOR )
        {
            switch ( token.getData() )
            {
                case "*":
                    return true;

                case "/":
                    return true;

                default:
                    break;
            }
        }

        return false;
    }

    private static final boolean isNumber(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getData().matches(CMinusLexer.C_NUMBERS) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static final boolean isRelOp(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.OPERATOR )
        {
            switch ( token.getData() )
            {
                case "<":
                    return true;

                case "<=":
                    return true;

                case ">":
                    return true;

                case ">=":
                    return true;

                case "==":
                    return true;

                case "!=":
                    return true;

                default:
                    break;
            }
        }

        return false;
    }

    private static final boolean isTypeSpecifier(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.KEYWORD )
        {
            switch ( token.getData() )
            {
                case "float":
                    return true;

                case "int":
                    return true;

                case "void":
                    return true;

                default:
                    break;
            }
        }

        return false;
    }

    private static final CMinusParseResult program(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        return CMinusParser.declarationList(tokens, index);
    }

    private static final CMinusParseResult variableDeclaration(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        // TODO: Add identifiers to the appropriate symbol table.
        Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, index);
        int newIndex = index;

        try
        {
            if ( CMinusParser.isTypeSpecifier(token) )
            {
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isIdentifier(token) )
                {
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ";") )
                    {
                        return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                    }
                    else if ( CMinusParser.isGroupingSymbol(token, "[") )
                    {
                        newIndex++;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isNumber(token) )
                        {
                            newIndex++;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isGroupingSymbol(token, "]") )
                            {
                                newIndex++;
                                token = CMinusParser.getToken(tokens, newIndex);

                                if ( CMinusParser.isGroupingSymbol(token, ";") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                            }
                        }
                    }
                }
            }
        }
        catch ( final NullPointerException npe )
        {
            newIndex--;
        }

        return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
    }

    private List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables = null;
    private List<Token<CMinusLexer.TokenType>>                                    tokens       = null;

    public CMinusParser(final List<Token<CMinusLexer.TokenType>> tokens, final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables)
    {
        super();
        this.parse(tokens, symbolTables);
    }

    private CMinusParseResult accept(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        return CMinusParser.program(tokens, index);
    }

    public final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> getSymbolTables()
    {
        return symbolTables;
    }

    public final List<Token<CMinusLexer.TokenType>> getTokens()
    {
        return tokens;
    }

    private final void parse()
    {
        try
        {
            for ( int i = 0; i < this.getTokens().size(); i++ )
            {
                CMinusParseResult cmpr = this.accept(this.getTokens(), i);

                if ( cmpr.resultType == CMinusParseResult.Type.REJECT ) { throw new CMinusParseException(this.getTokens().get(cmpr.endIndex)); }

                i = cmpr.endIndex;
            }

            StdOut.println("ACCEPT");
        }
        catch ( final CMinusParseException cmpe )
        {
            StdOut.println(cmpe.getMessage());
        }
    }

    public final void parse(final List<Token<CMinusLexer.TokenType>> tokens, final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);
            this.parse();
        }
    }

    protected final void setSymbolTables(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables)
    {
        this.symbolTables = symbolTables;
    }

    protected final void setTokens(final List<Token<CMinusLexer.TokenType>> tokens)
    {
        this.tokens = tokens;
    }
}
