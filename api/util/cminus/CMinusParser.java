/*
 * Title: CMinusParser
 * Author: Matthew Boyette
 * Date: 10/09/2016
 *
 * This class functions as a generic syntactical analyzer for the C-Minus language. The parser is built in the model of a Turing machine processing a finite strand of tape
 * which represents the list of tokens. I keep track of which index of the tape I am currently looking at and can read, write, go forwards, go backwards, and can look back
 * or ahead at any previous or future token, respectively. This effectively parses any language that is LL(k) parsable for any finite constant non-negative integer k. This
 * parser could be made GLL(k) capable by executing all of the calls to the static declaration method in parallel.
 */

package api.util.cminus;

import java.util.List;
import api.util.datastructures.SeparateChainingSymbolTable;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusParser
{
    // This class represents an exception that has occurred during an attempted parsing operation.
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

    // This class contains the parsing algorithms, organized by which grammar production rule each one is associated with.
    protected static class CMinusParseProduction
    {
        public static final CMinusParseResult additiveExpression(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            CMinusParseResult termResult = CMinusParseProduction.term(tokens, newIndex);

            while ( ( termResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( termResult.endIndex ) < tokens.size() ) )
            {
                newIndex = termResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isAdditionOperator(token) )
                {
                    newIndex++;
                    termResult = CMinusParseProduction.term(tokens, newIndex);
                    newIndex = termResult.endIndex;
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(termResult.resultType, index, newIndex);
        }

        public static final CMinusParseResult argList(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            CMinusParseResult expressionResult = CMinusParseProduction.expression(tokens, newIndex);

            while ( ( expressionResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( expressionResult.endIndex ) < tokens.size() ) )
            {
                newIndex = expressionResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isGroupingSymbol(token, ",") )
                {
                    newIndex++;
                    expressionResult = CMinusParseProduction.expression(tokens, newIndex);
                    newIndex = expressionResult.endIndex;
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(expressionResult.resultType, index, newIndex);
        }

        public static final CMinusParseResult args(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            // Empty arg-list
            if ( CMinusParser.isGroupingSymbol(token, ")") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }

            return argList(tokens, newIndex);
        }

        public static final CMinusParseResult call(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isIdentifier(token) )
            {
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( ( CMinusParser.isGroupingSymbol(token, "(") ) )
                {
                    newIndex++;
                    CMinusParseResult cmpr = CMinusParseProduction.args(tokens, newIndex);

                    if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                    {
                        newIndex = Math.max(newIndex, cmpr.endIndex);
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( ( CMinusParser.isGroupingSymbol(token, ")") ) ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

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
            int newIndex = index;
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
                                    CMinusParser.addSymbol(symbolTables, tokens.subList(index, newIndex), CMinusIdentifierParameters.PARAM_TYPE.FUNCTION_DECLARATION);
                                    return CMinusParseProduction.compoundStatement(symbolTables, tokens, newIndex + 1);
                                }
                            }
                        }
                    }
                    else // Variable Declaration
                    {
                        if ( CMinusParser.isGroupingSymbol(token, ";") )
                        {
                            CMinusParser.addSymbol(symbolTables, tokens.subList(index, newIndex), CMinusIdentifierParameters.PARAM_TYPE.VARIABLE_DECLARATION);
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

                                    if ( CMinusParser.isGroupingSymbol(token, ";") )
                                    {
                                        CMinusParser.addSymbol(symbolTables, tokens.subList(index, newIndex), CMinusIdentifierParameters.PARAM_TYPE.VARIABLE_DECLARATION);
                                        return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if ( isLocal ) // Decide if empty.
            {
                if ( index == newIndex )
                {
                    CMinusParseResult cmpr = CMinusParseProduction.statement(symbolTables, tokens, newIndex, true);

                    if ( ( cmpr.resultType == CMinusParseResult.Type.ACCEPT ) || CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.Type.EMPTY, index, newIndex); }
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult declarationList(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isLocal)
        {
            CMinusParseResult declarationResult = CMinusParseProduction.declaration(symbolTables, tokens, index, isLocal);

            while ( ( declarationResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( declarationResult.endIndex + 1 ) < tokens.size() ) )
            {
                if ( ( !isLocal ) && ( tokens.get(declarationResult.endIndex + 1).getData().contentEquals(CMinusParser.EOF_TOKEN) ) )
                {
                    break;
                }

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
            CMinusParseResult cmpr = CMinusParseProduction.variable(tokens, newIndex);

            if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
            {
                newIndex = cmpr.endIndex + 1;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isAssignmentOperator(token) ) { return CMinusParseProduction.expression(tokens, newIndex + 1); }
            }

            cmpr = CMinusParseProduction.simpleExpression(tokens, index);

            if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT ) { return cmpr; }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult expressionStatement(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
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
                    newIndex = cmpr.endIndex;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ";") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult factor(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "(") )
            {
                newIndex++;
                CMinusParseResult cmpr = CMinusParseProduction.expression(tokens, newIndex);

                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                {
                    newIndex = cmpr.endIndex;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ")") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex + 1); }
                }
            }
            else if ( CMinusParser.isIdentifier(token) )
            {
                Token<CMinusLexer.TokenType> nextToken = CMinusParser.getToken(tokens, newIndex + 1);
                CMinusParseResult cmpr = null;

                if ( CMinusParser.isGroupingSymbol(nextToken, "(") )
                {
                    cmpr = CMinusParseProduction.call(tokens, newIndex);
                }
                else
                {
                    cmpr = CMinusParseProduction.variable(tokens, newIndex);
                }

                return new CMinusParseResult(cmpr.resultType, cmpr.begIndex, cmpr.endIndex + 1);
            }
            else if ( CMinusParser.isNumber(token) ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex + 1); }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult iterationOrSelectionStatement(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isSelectionStatement)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "(") )
            {
                newIndex++;
                CMinusParseResult cmpr = CMinusParseProduction.expression(tokens, newIndex);

                if ( cmpr.resultType == CMinusParseResult.Type.ACCEPT )
                {
                    newIndex = cmpr.endIndex;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ")") )
                    {
                        newIndex++;
                        cmpr = CMinusParseProduction.statement(symbolTables, tokens, newIndex, false);

                        if ( isSelectionStatement )
                        {
                            newIndex = cmpr.endIndex + 1;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isKeyword(token, "else") )
                            {
                                return CMinusParseProduction.statement(symbolTables, tokens, newIndex + 1, false);
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

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }

        public static final CMinusParseResult parameter(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
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

                        if ( CMinusParser.isGroupingSymbol(token, "]") )
                        {
                            CMinusParser.addSymbol(symbolTables, tokens.subList(index, newIndex), CMinusIdentifierParameters.PARAM_TYPE.FUNCTION_PARAMETER);
                            return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                        }
                    }
                    else
                    {
                        newIndex--;
                        CMinusParser.addSymbol(symbolTables, tokens.subList(index, newIndex), CMinusIdentifierParameters.PARAM_TYPE.FUNCTION_PARAMETER);
                        return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex);
                    }
                }
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
            CMinusParseResult additiveResult = CMinusParseProduction.additiveExpression(tokens, newIndex);

            if ( additiveResult.resultType == CMinusParseResult.Type.ACCEPT )
            {
                newIndex = additiveResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isRelationalOperator(token) )
                {
                    return CMinusParseProduction.additiveExpression(tokens, newIndex + 1);
                }
                else
                {
                    return additiveResult;
                }
            }

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

            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            // First set of compound statement.
            if ( CMinusParser.isGroupingSymbol(token, "{") )
            {
                type = COMPOUND_TYPE;
            }
            // First set of selection, return, and iteration statements.
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
            // First set of expression statement.
            else if ( CMinusParser.isIdentifier(token) || CMinusParser.isGroupingSymbol(token, "(") || CMinusParser.isNumber(token) || CMinusParser.isGroupingSymbol(token, ";") )
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
                if ( CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.Type.EMPTY, index, newIndex); }
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

        public static final CMinusParseResult term(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            CMinusParseResult factorResult = CMinusParseProduction.factor(tokens, newIndex);

            while ( ( factorResult.resultType == CMinusParseResult.Type.ACCEPT ) && ( ( factorResult.endIndex ) < tokens.size() ) )
            {
                newIndex = factorResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isMultiplicationOperator(token) )
                {
                    newIndex++;
                    factorResult = CMinusParseProduction.factor(tokens, newIndex);
                    newIndex = factorResult.endIndex;
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(factorResult.resultType, index, newIndex);
        }

        public static final CMinusParseResult variable(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
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
                        newIndex = cmpr.endIndex;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "]") ) { return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex); }
                    }
                }
                else
                {
                    return new CMinusParseResult(CMinusParseResult.Type.ACCEPT, index, newIndex - 1);
                }
            }

            return new CMinusParseResult(CMinusParseResult.Type.REJECT, index, newIndex);
        }
    }

    // This simple class allows me to recursively parse the token list by maintaining the current index in the list and accurately determining success or failure.
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

    protected final static String EOF_TOKEN = "$";
    protected final static SeparateChainingSymbolTable<String, CMinusIdentifierParameters> GLOBAL_SYMTABLE = new SeparateChainingSymbolTable<String, CMinusIdentifierParameters>();

    protected static final void addSymbol(final List<SeparateChainingSymbolTable<String, CMinusIdentifierParameters>> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final CMinusIdentifierParameters.PARAM_TYPE paramType)
    {
        // TODO: Add symbols to symbol table(s).
        int size = tokens.size();

        switch ( paramType )
        {
            case FUNCTION_DECLARATION:
                break;

            case FUNCTION_PARAMETER:
                break;

            case VARIABLE_DECLARATION:
                break;

            default:
                return;
        }
    }

    protected static final Token<CMinusLexer.TokenType> getToken(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
    {
        boolean indexLowerCheck = ( index >= 0 );
        boolean indexUpperCheck = ( index < tokens.size() );
        boolean validIndex = ( indexLowerCheck && indexUpperCheck );

        if ( validIndex )
        {
            return tokens.get(index);
        }
        else
        {
            int newIndex = index;

            if ( !indexLowerCheck )
            {
                newIndex = 0;
            }
            else if ( !indexUpperCheck )
            {
                newIndex = tokens.size() - 1;
            }

            return tokens.get(newIndex);
        }
    }

    protected static final boolean isAdditionOperator(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.OPERATOR )
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
        if ( token.getType() == CMinusLexer.TokenType.OPERATOR )
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

    protected static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.GROUPING ) { return true; }

        return false;
    }

    protected static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( CMinusParser.isGroupingSymbol(token) ) { return token.getData().contentEquals(symbol); }

        return false;
    }

    protected static final boolean isIdentifier(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.IDENTIFIER ) { return true; }

        return false;
    }

    protected static final boolean isKeyword(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.KEYWORD ) { return true; }

        return false;
    }

    protected static final boolean isKeyword(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( CMinusParser.isKeyword(token) ) { return token.getData().contentEquals(symbol); }

        return false;
    }

    protected static final boolean isMultiplicationOperator(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.OPERATOR )
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
        return token.getData().matches(CMinusLexer.C_NUMBERS);
    }

    protected static final boolean isRelationalOperator(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.OPERATOR )
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
        if ( token.getType() == CMinusLexer.TokenType.KEYWORD )
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

                int lastLineNo = tokens.get(tokens.size() - 1).getLineNo();
                tokens.add(new Token<CMinusLexer.TokenType>(CMinusLexer.TokenType.WHITESPACE, CMinusParser.EOF_TOKEN, lastLineNo));
                symbolTables.add(CMinusParser.GLOBAL_SYMTABLE);
                CMinusParseResult cmpr = CMinusParseProduction.declarationList(symbolTables, tokens, 0, false);

                if ( cmpr.resultType != CMinusParseResult.Type.ACCEPT ) { throw new CMinusParseException(CMinusParser.getToken(tokens, cmpr.endIndex)); }

                StdOut.println("ACCEPT");
                tokens.remove(tokens.size() - 1);
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
