/*
 * Title: CMinusParser
 * Author: Matthew Boyette
 * Date: 10/09/2016
 *
 * This class functions as a generic syntactical analyzer for the C-Minus language.
 *
 * TODO: Finish code for identifier parameters.
 * TODO: Add identifiers to symbol tables.
 * TODO: Add identifiers to symbol tables.
 * TODO: Finish code for simple expressions.
 * TODO: Finish code to check if a given token is in the first set of simple expressions.
 */

package api.util.cminus;

import java.util.List;
import api.util.cminus.CMinusLexer.TokenType;
import api.util.datastructures.SeparateChainingSymbolTable;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusParser
{
    public static class CMinusIdentifierParameters
    {
        // TODO: Finish code for identifier parameters.
    }

    protected static class CMinusParseException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private Token<CMinusLexer.TokenType> token;

        public CMinusParseException()
        {
            super("*ERROR*: Syntax error; cannot parse empty token list.");
            this.setToken(null);
        }

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

    protected static class CMinusParseProduction
    {
        public static final CMinusParseResult compoundStatement(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "{") )
            {
                newIndex++;
                CMinusParseResult cmpr = CMinusParseProduction.declarationList(symbolTables, tokens, newIndex, true);

                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                {
                    newIndex = Math.max(newIndex, cmpr.endIndex);
                    cmpr = CMinusParseProduction.statementList(symbolTables, tokens, newIndex);

                    if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                    {
                        newIndex = Math.max(newIndex, cmpr.endIndex);
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult declaration(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isLocal)
        {
            // TODO: Add identifiers to symbol tables.
            int newIndex = index;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isTypeSpecifier(token) )
                {
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isIdentifier(token) )
                    {
                        newIndex++;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( ( CMinusParser.isGroupingSymbol(token, "(") ) ) // Function Declaration
                        {
                            if ( !isLocal ) // Necessary to re-use this method for local declarations.
                            {
                                newIndex++;
                                CMinusParseResult cmpr = CMinusParseProduction.parameters(symbolTables, tokens, newIndex);

                                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                                {
                                    newIndex = cmpr.endIndex + 1;
                                    token = CMinusParser.getToken(tokens, newIndex);

                                    if ( CMinusParser.isGroupingSymbol(token, ")") )
                                    {
                                        newIndex++;
                                        return CMinusParseProduction.compoundStatement(symbolTables, tokens, newIndex);
                                    }
                                }
                            }
                        }
                        else // Variable Declaration
                        {
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

                // Decide if empty.
                if ( isLocal )
                {
                    if ( index == newIndex )
                    {
                        boolean isEmpty = ( CMinusParser.isGroupingSymbol(token, "}") || ( CMinusParseProduction.statement(symbolTables, tokens, newIndex, true).resultType == CMinusParseResult.Type.ACCEPT ) );

                        if ( isEmpty ) { return new CMinusParseResult(CMinusParseResult.Type.EMPTY, index, newIndex); }
                    }
                }
            }
            catch ( final NullPointerException npe )
            {
                newIndex--;
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult declarationList(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isLocal)
        {
            CMinusParseResult declarationResult = CMinusParseProduction.declaration(symbolTables, tokens, index, isLocal);

            while ( ( declarationResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( declarationResult.endIndex + 1 ) < tokens.size() ) )
            {
                declarationResult = CMinusParseProduction.declaration(symbolTables, tokens, declarationResult.endIndex + 1, isLocal);
            }

            if ( isLocal )
            {
                if ( declarationResult.begIndex == declarationResult.endIndex )
                {
                    if ( declarationResult.resultType == CMinusParseResult.Type.EMPTY ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, declarationResult.begIndex, declarationResult.endIndex); }
                }
            }

            return declarationResult;
        }

        public static final CMinusParseResult expression(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;

            try
            {
                CMinusParseResult cmpr = CMinusParseProduction.variable(tokens, newIndex);

                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                {
                    newIndex = cmpr.endIndex + 1;
                    Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isAssignmentOperator(token) ) { return CMinusParseProduction.expression(tokens, newIndex + 1); }
                }

                cmpr = CMinusParseProduction.simpleExpression(tokens, index);

                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT ) { return cmpr; }
            }
            catch ( final NullPointerException npe )
            {
                newIndex--;
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult expressionStatement(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isGroupingSymbol(token, ";") )
                {
                    return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                }
                else
                {
                    CMinusParseResult cmpr = CMinusParseProduction.expression(tokens, newIndex);

                    if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                    {
                        newIndex = cmpr.endIndex + 1;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, ";") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                    }
                }
            }
            catch ( final NullPointerException npe )
            {
                newIndex--;
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult iterationOrSelectionStatement(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isSelectionStatement)
        {
            int newIndex = index;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isGroupingSymbol(token, "(") )
                {
                    newIndex++;
                    CMinusParseResult cmpr = CMinusParseProduction.expression(tokens, newIndex);

                    if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                    {
                        newIndex = cmpr.endIndex + 1;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, ")") )
                        {
                            newIndex++;
                            cmpr = CMinusParseProduction.statement(symbolTables, tokens, newIndex, false);

                            if ( isSelectionStatement )
                            {
                                newIndex++;
                                token = CMinusParser.getToken(tokens, newIndex);

                                if ( CMinusParser.isKeyword(token) )
                                {
                                    if ( token.getData().contentEquals("else") ) { return CMinusParseProduction.statement(symbolTables, tokens, newIndex + 1, false); }
                                }
                                else
                                {
                                    return cmpr;
                                }
                            }
                            else
                            {
                                return cmpr;
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

        public static final CMinusParseResult parameter(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            // TODO: Add identifiers to symbol tables.
            int newIndex = index;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isTypeSpecifier(token) )
                {
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isIdentifier(token) )
                    {
                        newIndex++;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "[") )
                        {
                            newIndex++;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isGroupingSymbol(token, "]") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                        }
                        else
                        {
                            return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex - 1);
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

        public static final CMinusParseResult parameterList(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult parameterResult = CMinusParseProduction.parameter(symbolTables, tokens, index);

            while ( ( parameterResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( parameterResult.endIndex + 2 ) < tokens.size() ) )
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, parameterResult.endIndex + 1);

                if ( CMinusParser.isGroupingSymbol(token, ",") )
                {
                    parameterResult = CMinusParseProduction.parameter(symbolTables, tokens, parameterResult.endIndex + 2);
                }
                else
                {
                    break;
                }
            }

            return parameterResult;
        }

        public static final CMinusParseResult parameters(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult cmpr = CMinusParseProduction.parameterList(symbolTables, tokens, index);

            if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
            {
                return cmpr;
            }
            else
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, index);

                if ( CMinusParser.isTypeSpecifier(token) )
                {
                    if ( token.getData().contentEquals("void") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, index); }
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, index);
        }

        public static final CMinusParseResult simpleExpression(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;

            // TODO: Finish code for simple expressions.

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult statement(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean checkFirstsOnly)
        {
            int newIndex = index;
            int type = 0;

            final int COMPOUND_TYPE = 1;
            final int SELECTION_TYPE = 2;
            final int RETURN_TYPE = 3;
            final int ITERATION_TYPE = 4;
            final int EXPRESSION_TYPE = 5;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                // First of compound statement.
                if ( CMinusParser.isGroupingSymbol(token, "{") )
                {
                    type = COMPOUND_TYPE;
                }
                // First of selection, return, and iteration statements.
                else if ( CMinusParser.isKeyword(token) )
                {
                    switch ( token.getData() )
                    {
                        case "if":
                            type = SELECTION_TYPE;
                            break;

                        case "return":
                            type = RETURN_TYPE;
                            break;

                        case "while":
                            type = ITERATION_TYPE;
                            break;

                        default:
                            break;
                    }
                }
                // First of expression statement.
                else if ( CMinusParser.isIdentifier(token) || CMinusParser.isGroupingSymbol(token, ";") || CMinusParser.isInFirstSetOfSimpleExpression(tokens, newIndex) )
                {
                    type = EXPRESSION_TYPE;
                }

                if ( checkFirstsOnly && ( type > 0 ) )
                {
                    return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                }
                else if ( type > 0 )
                {
                    switch ( type )
                    {
                        case COMPOUND_TYPE:

                            return CMinusParseProduction.compoundStatement(symbolTables, tokens, newIndex);

                        case SELECTION_TYPE:

                            return CMinusParseProduction.iterationOrSelectionStatement(symbolTables, tokens, newIndex + 1, true);

                        case RETURN_TYPE:

                            return CMinusParseProduction.expressionStatement(tokens, newIndex + 1);

                        case ITERATION_TYPE:

                            return CMinusParseProduction.iterationOrSelectionStatement(symbolTables, tokens, newIndex + 1, false);

                        case EXPRESSION_TYPE:

                            return CMinusParseProduction.expressionStatement(tokens, newIndex);

                        default:

                            break;
                    }
                }
                else
                {
                    // Decide if empty.
                    if ( index == newIndex )
                    {
                        if ( CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.Type.EMPTY, index, newIndex); }
                    }
                }
            }
            catch ( final NullPointerException npe )
            {
                newIndex--;
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult statementList(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult statementResult = CMinusParseProduction.statement(symbolTables, tokens, index, false);

            while ( ( statementResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( statementResult.endIndex + 1 ) < tokens.size() ) )
            {
                statementResult = CMinusParseProduction.statement(symbolTables, tokens, statementResult.endIndex + 1, false);
            }

            if ( statementResult.begIndex == statementResult.endIndex )
            {
                if ( statementResult.resultType == CMinusParseResult.Type.EMPTY ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, statementResult.begIndex, statementResult.endIndex); }
            }

            return statementResult;
        }

        public static final CMinusParseResult variable(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;

            try
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isIdentifier(token) )
                {
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, "[") )
                    {
                        newIndex++;
                        CMinusParseResult cmpr = CMinusParseProduction.expression(tokens, newIndex);

                        if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                        {
                            newIndex = cmpr.endIndex + 1;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isGroupingSymbol(token, "]") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                        }
                    }
                    else
                    {
                        return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex - 1);
                    }
                }
            }
            catch ( final NullPointerException npe )
            {
                newIndex--;
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }
    }

    protected static class CMinusParseResult
    {
        public static enum Type
        {
            ACCEPT, EMPTY, REJECT
        }

        public final int  begIndex;
        public final int  endIndex;
        public final Type resultType;

        public CMinusParseResult(final Type resultType, final int begIndex, final int endIndex)
        {
            this.begIndex = begIndex;
            this.endIndex = endIndex;
            this.resultType = resultType;
        }
    }

    protected static final Token<CMinusLexer.TokenType> getToken(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
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

    protected static final boolean isAdditionOperator(final Token<CMinusLexer.TokenType> token)
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

    protected static final boolean isAssignmentOperator(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.OPERATOR )
        {
            switch ( token.getData() )
            {
                case "=":
                    return true;

                default:
                    break;
            }
        }

        return false;
    }

    protected static final boolean isInFirstSetOfSimpleExpression(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        // TODO: Finish code to check if a given token is in the first set of simple expressions.

        return false;
    }

    protected static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( token.getType() == TokenType.GROUPING )
        {
            if ( token.getData().contentEquals(symbol) ) { return true; }
        }

        return false;
    }

    protected static final boolean isIdentifier(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.IDENTIFIER ) { return true; }

        return false;
    }

    protected static final boolean isKeyword(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == TokenType.KEYWORD ) { return true; }

        return false;
    }

    protected static final boolean isMultiplicationOperator(final Token<CMinusLexer.TokenType> token)
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

    protected static final boolean isNumber(final Token<CMinusLexer.TokenType> token)
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

    protected static final boolean isRelationalOperator(final Token<CMinusLexer.TokenType> token)
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

    protected static final boolean isTypeSpecifier(final Token<CMinusLexer.TokenType> token)
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

    private List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables = null;
    private List<Token<CMinusLexer.TokenType>>                                    tokens       = null;

    public CMinusParser(final List<Token<CMinusLexer.TokenType>> tokens, final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final boolean silent)
    {
        super();
        this.parse(tokens, symbolTables, silent);
    }

    public final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> getSymbolTables()
    {
        return symbolTables;
    }

    public final List<Token<CMinusLexer.TokenType>> getTokens()
    {
        return tokens;
    }

    public final void parse(final List<Token<CMinusLexer.TokenType>> tokens, final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final boolean silent)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);

            try
            {
                if ( tokens.isEmpty() ) { throw new CMinusParseException(); }

                CMinusParseResult cmpr = CMinusParseProduction.declarationList(symbolTables, tokens, 0, false);

                if ( cmpr.resultType != CMinusParseResult.Type.ACCEPT ) { throw new CMinusParseException(tokens.get(cmpr.endIndex)); }

                StdOut.println("ACCEPT");
            }
            catch ( final CMinusParseException cmpe )
            {
                if ( !silent )
                {
                    StdOut.println(cmpe.getMessage());
                }

                StdOut.println("REJECT");
            }
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
