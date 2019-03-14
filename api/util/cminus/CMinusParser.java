/*
 * Title: CMinusParser
 * Author: Matthew Boyette
 * Date: 10/09/2016 - 04/14/2017
 *
 * This class functions as a generic syntactical analyzer for the C-Minus language. The parser is built in the model of a linear-bounded automaton processing a finite strand of
 * tape which represents the list of tokens. I keep track of which index of the tape I am currently looking at and can read, write, go forwards, go backwards, and can look back
 * or ahead at any previous or future token, respectively. This effectively parses any language that is LL(k) parsable for any finite constant non-negative integer k. This parser
 * could be made GLL(k) capable by executing all of the calls to the static declaration method in parallel.
 */

package api.util.cminus;

import java.util.LinkedList;
import java.util.List;
import api.util.cminus.CMinusSemantics.VarRec;
import api.util.datastructures.Token;
import edu.princeton.cs.introcs.StdOut;

public class CMinusParser
{
    // This class represents an exception that has occurred during an attempted parsing operation.
    public static class CMinusParseException extends Exception
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
            return this.token;
        }

        protected final void setToken(final Token<CMinusLexer.TokenType> token)
        {
            this.token = token;
        }
    }

    // This class contains the parsing algorithms, organized by which grammar production rule each one is associated with.
    public static class CMinusParseProduction
    {
        public static final CMinusParseResult additiveExpression(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            String s1 = null, s2 = null;
            int newIndex = index, expressionStart = newIndex, expressionStop, e1, e2;
            CMinusParseResult termResult = CMinusParseProduction.term(symbolTables, tokens, newIndex);
            CMinusParseResult cmpr = termResult;
            expressionStop = cmpr.endIndex;
            e1 = expressionStop - expressionStart;

            while ( ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( cmpr.endIndex ) < tokens.size() ) )
            {
                if ( ( e1 ) == 1 )
                {
                    s1 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                }
                else if ( ( e1 ) > 1 )
                {
                    s1 = "";
                }

                newIndex = cmpr.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);
                boolean isAdd = token.getData().contentEquals("+");

                if ( CMinusParser.isAdditionOperator(token) )
                {
                    newIndex++;
                    expressionStart = newIndex;
                    termResult = CMinusParseProduction.term(symbolTables, tokens, newIndex);
                    CMinusSemantics.checkTypeAgreement(termResult.returnType, cmpr.returnType);
                    cmpr = termResult;
                    expressionStop = cmpr.endIndex;
                    e2 = expressionStop - expressionStart;
                    newIndex = cmpr.endIndex;

                    if ( ( e2 ) == 1 )
                    {
                        s2 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                    }
                    else if ( ( e2 ) > 1 )
                    {
                        s2 = "";
                    }

                    CMinusCodeGeneration.QuadrupleWriter.writeAddOrSub(isAdd, s1, s2, ( ( e1 == e2 ) && ( e1 == 1 ) ));
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(cmpr.returnType, cmpr.parseResult, index, newIndex);
        }

        public static final CMinusParseResult argList(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index, expressionStart = newIndex, expressionStop;
            CMinusParseResult expressionResult = CMinusParseProduction.expression(symbolTables, tokens, newIndex);
            expressionStop = expressionResult.endIndex;

            while ( ( expressionResult.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( expressionResult.endIndex ) < tokens.size() ) )
            {
                String arg = null;

                if ( ( expressionStop - expressionStart ) == 1 )
                {
                    arg = tokens.subList(expressionStart, expressionStop).get(0).getData();
                }
                else if ( ( expressionStop - expressionStart ) > 1 )
                {
                    arg = "";
                }

                CMinusCodeGeneration.QuadrupleWriter.writeArgument(arg);
                newIndex = expressionResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isGroupingSymbol(token, ",") )
                {
                    newIndex++;
                    expressionStart = newIndex;
                    expressionResult = CMinusParseProduction.expression(symbolTables, tokens, newIndex);
                    expressionStop = expressionResult.endIndex;
                    newIndex = expressionStop;
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(expressionResult.returnType, expressionResult.parseResult, index, newIndex);
        }

        public static final CMinusParseResult args(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            // Empty arg-list.
            if ( CMinusParser.isGroupingSymbol(token, ")") ) { return new CMinusParseResult(CMinusParseResult.ReturnType.VOID, CMinusParseResult.ParseResult.ACCEPT, index, newIndex); }

            return argList(symbolTables, tokens, newIndex);
        }

        public static final CMinusParseResult call(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index, argListStart = 0, argListStop = 0, argCount = 0;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isIdentifier(token) )
            {
                CMinusSemantics.FunRec record = (CMinusSemantics.FunRec) symbolTables.get(token.getData());
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( ( CMinusParser.isGroupingSymbol(token, "(") ) )
                {
                    newIndex++;
                    argListStart = newIndex;
                    CMinusParseResult cmpr = CMinusParseProduction.args(symbolTables, tokens, newIndex);

                    if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                    {
                        newIndex = Math.max(newIndex, cmpr.endIndex);
                        argListStop = newIndex;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( ( CMinusParser.isGroupingSymbol(token, ")") ) )
                        {
                            if ( record == null )
                            {
                                return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                            }
                            else
                            {
                                List<Token<CMinusLexer.TokenType>> args = tokens.subList(argListStart, argListStop);
                                argCount = CMinusSemantics.getArgumentCount(args, token.getParenthDepth());
                                CMinusSemantics.checkFunctionParamArgumentNumberAgreement(record, ( argListStop - argListStart ), argCount);

                                if ( argCount > 0 )
                                {
                                    // TODO: Check to see if the type of each parameter agrees with the type of each argument.
                                    CMinusSemantics.checkFunctionParamArgumentTypeAgreement(record, args);
                                }

                                CMinusCodeGeneration.QuadrupleWriter.writeFunctionCall(record, argCount);
                                return new CMinusParseResult(record.type, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                            }
                        }
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult compoundStatement(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            for ( CMinusSemantics.SymTabRec param : params )
            {
                CMinusSemantics.addSymbol(param, symbolTables);
            }

            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "{") )
            {
                newIndex++;
                CMinusParseResult cmpr = CMinusParseProduction.declarationList(symbolTables, tokens, newIndex, true);

                if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                {
                    newIndex = Math.max(newIndex, cmpr.endIndex);
                    cmpr = CMinusParseProduction.statementList(symbolTables, params, tokens, newIndex);

                    if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                    {
                        newIndex = Math.max(newIndex, cmpr.endIndex);
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(cmpr.returnType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex); }
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult declaration(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isLocal)
        {
            int newIndex = index, arraySize = -1;
            String symbolName, symbolType;
            List<CMinusSemantics.SymTabRec> params = new LinkedList<CMinusSemantics.SymTabRec>();
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isTypeSpecifier(token) )
            {
                symbolType = token.getData();
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isIdentifier(token) )
                {
                    symbolName = token.getData();
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( ( CMinusParser.isGroupingSymbol(token, "(") ) ) // Function Declaration
                    {
                        if ( !isLocal ) // Necessary to re-use this method for local declarations.
                        {
                            newIndex++;
                            CMinusParseResult cmpr = CMinusParseProduction.parameters(symbolTables, params, tokens, newIndex);

                            if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                            {
                                newIndex = cmpr.endIndex + 1;
                                token = CMinusParser.getToken(tokens, newIndex);

                                if ( CMinusParser.isGroupingSymbol(token, ")") )
                                {
                                    CMinusSemantics.FunRec functionRecord = new CMinusSemantics.FunRec(symbolName, symbolTables.getScope(), symbolType, params);
                                    CMinusSemantics.addSymbol(functionRecord, symbolTables);
                                    CMinusSemantics.checkMain(functionRecord);
                                    CMinusCodeGeneration.QuadrupleWriter.writeFunctionStart(functionRecord);
                                    newIndex++;
                                    symbolTables.enterScope();
                                    cmpr = CMinusParseProduction.compoundStatement(symbolTables, params, tokens, newIndex);
                                    CMinusSemantics.checkFunctionReturns(functionRecord, tokens.subList(newIndex + 1, cmpr.endIndex), cmpr);
                                    symbolTables.exitScope();
                                    CMinusCodeGeneration.QuadrupleWriter.writeFunctionStop(functionRecord);
                                    return cmpr;
                                }
                            }
                        }
                    }
                    else // Variable Declaration
                    {
                        CMinusSemantics.SymTabRec record = null;

                        if ( CMinusParser.isGroupingSymbol(token, ";") )
                        {
                            record = new CMinusSemantics.VarRec(symbolName, symbolTables.getScope(), symbolType);
                            CMinusSemantics.addSymbol(record, symbolTables);
                            CMinusCodeGeneration.QuadrupleWriter.writeVariableAlloc((VarRec) record);
                            return new CMinusParseResult(symbolType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                        }
                        else if ( CMinusParser.isGroupingSymbol(token, "[") )
                        {
                            newIndex++;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isNumber(token) )
                            {
                                arraySize = CMinusSemantics.checkDeclarationArrayIndexType(token, arraySize);
                                newIndex++;
                                token = CMinusParser.getToken(tokens, newIndex);

                                if ( CMinusParser.isGroupingSymbol(token, "]") )
                                {
                                    newIndex++;
                                    token = CMinusParser.getToken(tokens, newIndex);

                                    if ( CMinusParser.isGroupingSymbol(token, ";") )
                                    {
                                        record = new CMinusSemantics.ArrRec(symbolName, symbolTables.getScope(), symbolType, arraySize);
                                        CMinusSemantics.addSymbol(record, symbolTables);
                                        CMinusCodeGeneration.QuadrupleWriter.writeArrayAlloc((CMinusSemantics.ArrRec) record);
                                        return new CMinusParseResult(symbolType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
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
                    CMinusParseResult cmpr = CMinusParseProduction.statement(symbolTables, params, tokens, newIndex, true);

                    if ( ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT ) || CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.ParseResult.EMPTY, index, newIndex); }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult declarationList(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isLocal)
        {
            CMinusParseResult declarationResult = CMinusParseProduction.declaration(symbolTables, tokens, index, isLocal);

            while ( ( declarationResult.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( declarationResult.endIndex + 1 ) < tokens.size() ) )
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
                    if ( declarationResult.parseResult == CMinusParseResult.ParseResult.EMPTY ) { return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, declarationResult.begIndex, declarationResult.endIndex); }
                }
            }

            return declarationResult;
        }

        public static final CMinusParseResult expression(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index, expressionStart, expressionStop, variableStart = newIndex, variableStop;
            CMinusParseResult cmpr = CMinusParseProduction.variable(symbolTables, tokens, variableStart, false);
            final List<Token<CMinusLexer.TokenType>> expression, variable;

            if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
            {
                newIndex = cmpr.endIndex + 1;
                variableStop = newIndex;
                variable = tokens.subList(variableStart, variableStop);
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isAssignmentOperator(token) )
                {
                    expressionStart = newIndex + 1;
                    CMinusParseResult cmpr2 = CMinusParseProduction.expression(symbolTables, tokens, expressionStart);
                    expressionStop = cmpr2.endIndex;
                    expression = tokens.subList(expressionStart, expressionStop);

                    if ( CMinusSemantics.checkTypeAgreement(cmpr.returnType, cmpr2.returnType) )
                    {
                        CMinusCodeGeneration.QuadrupleWriter.writeAssignment(variable, expression);
                    }

                    return cmpr2;
                }
            }

            cmpr = CMinusParseProduction.simpleExpression(symbolTables, tokens, index);

            if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT ) { return cmpr; }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult expressionStatement(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isReturn)
        {
            String result = null;
            int newIndex = index, expressionStart, expressionStop;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, ";") )
            {
                return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
            }
            else
            {
                expressionStart = newIndex;
                CMinusParseResult cmpr = CMinusParseProduction.expression(symbolTables, tokens, newIndex);

                if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                {
                    newIndex = cmpr.endIndex;
                    expressionStop = newIndex;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ";") )
                    {
                        if ( isReturn )
                        {
                            int e1 = expressionStop - expressionStart;

                            if ( ( e1 ) == 1 )
                            {
                                result = tokens.subList(expressionStart, expressionStop).get(0).getData();
                            }
                            else if ( ( e1 ) > 1 )
                            {
                                result = "";
                            }

                            CMinusCodeGeneration.QuadrupleWriter.writeReturn(result);
                        }

                        return new CMinusParseResult(cmpr.returnType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult factor(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "(") )
            {
                newIndex++;
                CMinusParseResult cmpr = CMinusParseProduction.expression(symbolTables, tokens, newIndex);

                if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                {
                    newIndex = cmpr.endIndex;
                    token = CMinusParser.getToken(tokens, newIndex);
                    if ( CMinusParser.isGroupingSymbol(token, ")") ) { return new CMinusParseResult(cmpr.returnType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex + 1); }
                }
            }
            else if ( CMinusParser.isIdentifier(token) )
            {
                Token<CMinusLexer.TokenType> nextToken = CMinusParser.getToken(tokens, newIndex + 1);
                CMinusParseResult cmpr = null;

                if ( CMinusParser.isGroupingSymbol(nextToken, "(") )
                {
                    cmpr = CMinusParseProduction.call(symbolTables, tokens, newIndex);
                }
                else
                {
                    cmpr = CMinusParseProduction.variable(symbolTables, tokens, newIndex, true);
                }

                return new CMinusParseResult(cmpr.returnType, cmpr.parseResult, cmpr.begIndex, cmpr.endIndex + 1);
            }
            else if ( CMinusParser.isNumber(token) )
            {
                String type;

                if ( token.getType() == CMinusLexer.TokenType.INTEGER )
                {
                    type = "int";
                }
                else
                {
                    type = "float";
                }

                return new CMinusParseResult(type, CMinusParseResult.ParseResult.ACCEPT, index, newIndex + 1);
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult iterationOrSelectionStatement(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean isSelectionStatement)
        {
            // TODO: IF/ELSE/ELSE-IF
            int newIndex = index, bpStart = CMinusCodeGeneration.statementCounter;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isGroupingSymbol(token, "(") )
            {
                newIndex++;
                CMinusCodeGeneration.isInControlStmnt = true;
                CMinusParseResult cmpr = CMinusParseProduction.expression(symbolTables, tokens, newIndex);
                CMinusCodeGeneration.isInControlStmnt = false;

                if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                {
                    newIndex = cmpr.endIndex;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, ")") )
                    {
                        newIndex++;
                        cmpr = CMinusParseProduction.statement(symbolTables, params, tokens, newIndex, false);

                        if ( isSelectionStatement )
                        {
                            newIndex = cmpr.endIndex + 1;
                            token = CMinusParser.getToken(tokens, newIndex);

                            if ( CMinusParser.isKeyword(token, "else") )
                            {
                                return CMinusParseProduction.statement(symbolTables, params, tokens, newIndex + 1, false);
                            }
                            else
                            {
                                return cmpr;
                            }
                        }
                        else
                        {
                            CMinusCodeGeneration.QuadrupleWriter.writeUnconditionalBranch(bpStart);

                            for ( int i = CMinusCodeGeneration.staticQuadBuffer.size() - 1; i >= 0; i-- )
                            {
                                CMinusCodeGeneration.QuadrupleWriter.Quadruple q = CMinusCodeGeneration.staticQuadBuffer.get(i);

                                if ( q.getStatementResult().contentEquals("$BP") )
                                {
                                    q.setStatementResult(Integer.toString(CMinusCodeGeneration.statementCounter));
                                    break;
                                }
                            }

                            return cmpr;
                        }
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult parameter(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            int newIndex = index;
            String symbolName, symbolType;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

            if ( CMinusParser.isTypeSpecifier(token) )
            {
                symbolType = token.getData();
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isIdentifier(token) )
                {
                    symbolName = token.getData();
                    newIndex++;
                    token = CMinusParser.getToken(tokens, newIndex);

                    if ( CMinusParser.isGroupingSymbol(token, "[") )
                    {
                        newIndex++;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "]") )
                        {
                            params.add(new CMinusSemantics.ArrRec(symbolName, symbolTables.getScope() + 1, symbolType, -1));
                            return new CMinusParseResult(symbolType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                        }
                    }
                    else
                    {
                        newIndex--;
                        params.add(new CMinusSemantics.VarRec(symbolName, symbolTables.getScope() + 1, symbolType));
                        return new CMinusParseResult(symbolType, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult parameterList(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult parameterResult = CMinusParseProduction.parameter(symbolTables, params, tokens, index);

            while ( ( parameterResult.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( parameterResult.endIndex + 2 ) < tokens.size() ) )
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, parameterResult.endIndex + 1);

                if ( CMinusParser.isGroupingSymbol(token, ",") )
                {
                    parameterResult = CMinusParseProduction.parameter(symbolTables, params, tokens, parameterResult.endIndex + 2);
                }
                else
                {
                    break;
                }
            }

            return parameterResult;
        }

        public static final CMinusParseResult parameters(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult cmpr = CMinusParseProduction.parameterList(symbolTables, params, tokens, index);

            if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
            {
                return cmpr;
            }
            else
            {
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, index);

                if ( CMinusParser.isTypeSpecifier(token) )
                {
                    if ( token.getData().contentEquals("void") ) { return new CMinusParseResult(token.getData(), CMinusParseResult.ParseResult.ACCEPT, index, index); }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, index);
        }

        public static final CMinusParseResult simpleExpression(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            String s1 = null, s2 = null, operator = "";
            int newIndex = index, expressionStart = newIndex, expressionStop, e1, e2;
            CMinusParseResult additiveResult = CMinusParseProduction.additiveExpression(symbolTables, tokens, newIndex);
            CMinusParseResult cmpr = additiveResult;
            expressionStop = cmpr.endIndex;
            e1 = expressionStop - expressionStart;

            if ( additiveResult.parseResult == CMinusParseResult.ParseResult.ACCEPT )
            {
                if ( ( e1 ) == 1 )
                {
                    s1 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                }
                else if ( ( e1 ) > 1 )
                {
                    s1 = "";
                }

                newIndex = additiveResult.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isRelationalOperator(token) )
                {
                    operator = token.getData();
                    cmpr = CMinusParseProduction.additiveExpression(symbolTables, tokens, newIndex + 1);
                    expressionStart = newIndex + 1;
                    expressionStop = cmpr.endIndex;
                    e2 = expressionStop - expressionStart;

                    if ( ( e2 ) == 1 )
                    {
                        s2 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                    }
                    else if ( ( e2 ) > 1 )
                    {
                        s2 = "";
                    }

                    CMinusSemantics.checkTypeAgreement(additiveResult.returnType, cmpr.returnType);
                    CMinusCodeGeneration.QuadrupleWriter.writeComparison(s1, s2, operator, ( ( e1 == e2 ) && ( e1 == 1 ) ));
                    return cmpr;
                }
                else
                {
                    return additiveResult;
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult statement(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean checkFirstsOnly)
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
                return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
            }
            else if ( type > 0 )
            {
                switch ( type )
                {
                    case COMPOUND_TYPE:
                        symbolTables.enterScope();
                        CMinusParseResult cmpr = CMinusParseProduction.compoundStatement(symbolTables, params, tokens, newIndex);
                        symbolTables.exitScope();
                        return cmpr;

                    case SELECTION_TYPE:
                        return CMinusParseProduction.iterationOrSelectionStatement(symbolTables, params, tokens, newIndex + 1, true);

                    case RETURN_TYPE:
                        return CMinusParseProduction.expressionStatement(symbolTables, tokens, newIndex + 1, true);

                    case ITERATION_TYPE:
                        return CMinusParseProduction.iterationOrSelectionStatement(symbolTables, params, tokens, newIndex + 1, false);

                    case EXPRESSION_TYPE:
                        return CMinusParseProduction.expressionStatement(symbolTables, tokens, newIndex, false);

                    default:
                        break;
                }
            }
            else
            {
                if ( CMinusParser.isGroupingSymbol(token, "}") ) { return new CMinusParseResult(CMinusParseResult.ParseResult.EMPTY, index, newIndex); }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }

        public static final CMinusParseResult statementList(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<CMinusSemantics.SymTabRec> params, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            CMinusParseResult statementResult = CMinusParseProduction.statement(symbolTables, params, tokens, index, false);
            CMinusParseResult.ReturnType returnType = CMinusParseResult.ReturnType.NONE;

            while ( ( statementResult.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( statementResult.endIndex + 1 ) < tokens.size() ) )
            {
                returnType = statementResult.returnType;
                statementResult = CMinusParseProduction.statement(symbolTables, params, tokens, statementResult.endIndex + 1, false);
            }

            if ( statementResult.begIndex == statementResult.endIndex )
            {
                if ( statementResult.parseResult == CMinusParseResult.ParseResult.EMPTY ) { return new CMinusParseResult(returnType, CMinusParseResult.ParseResult.ACCEPT, statementResult.begIndex, statementResult.endIndex); }
            }

            return statementResult;
        }

        public static final CMinusParseResult term(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index)
        {
            String s1 = null, s2 = null;
            int newIndex = index, expressionStart = newIndex, expressionStop, e1, e2;
            CMinusParseResult factorResult = CMinusParseProduction.factor(symbolTables, tokens, newIndex);
            CMinusParseResult cmpr = factorResult;
            expressionStop = cmpr.endIndex;
            e1 = expressionStop - expressionStart;

            while ( ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT ) && ( ( cmpr.endIndex ) < tokens.size() ) )
            {
                if ( ( e1 ) == 1 )
                {
                    s1 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                }
                else if ( ( e1 ) > 1 )
                {
                    s1 = "";
                }

                newIndex = cmpr.endIndex;
                Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex);
                boolean isMult = token.getData().contentEquals("*");

                if ( CMinusParser.isMultiplicationOperator(token) )
                {
                    newIndex++;
                    expressionStart = newIndex;
                    factorResult = CMinusParseProduction.factor(symbolTables, tokens, newIndex);
                    CMinusSemantics.checkTypeAgreement(factorResult.returnType, cmpr.returnType);
                    cmpr = factorResult;
                    expressionStop = cmpr.endIndex;
                    e2 = expressionStop - expressionStart;
                    newIndex = factorResult.endIndex;

                    if ( ( e2 ) == 1 )
                    {
                        s2 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                    }
                    else if ( ( e2 ) > 1 )
                    {
                        s2 = "";
                    }

                    CMinusCodeGeneration.QuadrupleWriter.writeMultOrDiv(isMult, s1, s2, ( ( e1 == e2 ) && ( e1 == 1 ) ));
                }
                else
                {
                    break;
                }
            }

            return new CMinusParseResult(factorResult.returnType, factorResult.parseResult, index, newIndex);
        }

        public static final CMinusParseResult variable(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final List<Token<CMinusLexer.TokenType>> tokens, final int index, final boolean disp)
        {
            String op2 = null;
            int newIndex = index, expressionStart, expressionStop;
            Token<CMinusLexer.TokenType> token = CMinusParser.getToken(tokens, newIndex), symbol;

            if ( CMinusParser.isIdentifier(token) )
            {
                CMinusSemantics.SymTabRec record = symbolTables.get(token.getData());
                symbol = token;
                newIndex++;
                token = CMinusParser.getToken(tokens, newIndex);

                if ( CMinusParser.isGroupingSymbol(token, "[") )
                {
                    newIndex++;
                    expressionStart = newIndex;
                    CMinusParseResult cmpr = CMinusParseProduction.expression(symbolTables, tokens, newIndex);

                    if ( cmpr.parseResult == CMinusParseResult.ParseResult.ACCEPT )
                    {
                        CMinusSemantics.checkVariableArrayIndexType(cmpr);
                        newIndex = cmpr.endIndex;
                        expressionStop = newIndex;
                        token = CMinusParser.getToken(tokens, newIndex);

                        if ( CMinusParser.isGroupingSymbol(token, "]") )
                        {
                            if ( record == null )
                            {
                                return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                            }
                            else
                            {
                                if ( disp )
                                {
                                    if ( ( expressionStop - expressionStart ) == 1 )
                                    {
                                        op2 = tokens.subList(expressionStart, expressionStop).get(0).getData();
                                    }
                                    else if ( ( expressionStop - expressionStart ) > 1 )
                                    {
                                        op2 = "";
                                    }

                                    CMinusCodeGeneration.QuadrupleWriter.writeDisplacement((CMinusSemantics.ArrRec) record, op2);
                                }

                                return new CMinusParseResult(record.type, CMinusParseResult.ParseResult.ACCEPT, index, newIndex);
                            }
                        }
                    }
                }
                else
                {
                    if ( record == null )
                    {
                        return new CMinusParseResult(CMinusParseResult.ParseResult.ACCEPT, index, newIndex - 1);
                    }
                    else
                    {
                        if ( symbol.getParenthDepth() == 0 )
                        {
                            CMinusSemantics.checkArrayVariableHasIndex(record);
                        }

                        return new CMinusParseResult(record.type, CMinusParseResult.ParseResult.ACCEPT, index, newIndex - 1);
                    }
                }
            }

            return new CMinusParseResult(CMinusParseResult.ParseResult.REJECT, index, newIndex);
        }
    }

    // This simple class allows me to recursively parse the token list by maintaining the current index in the list and accurately determining success or failure.
    public static class CMinusParseResult
    {
        public static enum ParseResult
        {
            ACCEPT, EMPTY, REJECT
        }

        public static enum ReturnType
        {
            FLOAT, INT, NONE, VOID
        }

        public static String convertReturnTypeEnumToTypeSpecifierString(final ReturnType returnType)
        {
            switch ( returnType )
            {
                case FLOAT:
                    return "float";

                case INT:
                    return "int";

                case VOID:
                    return "void";

                default:
                    return "none";
            }
        }

        public static ReturnType convertTypeSpecifierStringToReturnTypeEnum(final String typeSpecifier)
        {
            switch ( typeSpecifier )
            {
                case "float":
                    return ReturnType.FLOAT;

                case "int":
                    return ReturnType.INT;

                case "void":
                    return ReturnType.VOID;

                default:
                    return ReturnType.NONE;
            }
        }

        public final int         begIndex;
        public final int         endIndex;
        public final ParseResult parseResult;
        public final ReturnType  returnType;

        public CMinusParseResult(final ParseResult parseResult, final int begIndex, final int endIndex)
        {
            this(ReturnType.NONE, parseResult, begIndex, endIndex);
        }

        public CMinusParseResult(final ReturnType returnType, final ParseResult parseResult, final int begIndex, final int endIndex)
        {
            this.begIndex = begIndex;
            this.endIndex = endIndex;
            this.parseResult = parseResult;
            this.returnType = returnType;
        }

        public CMinusParseResult(final String typeSpecifier, final ParseResult parseResult, final int begIndex, final int endIndex)
        {
            this.begIndex = begIndex;
            this.endIndex = endIndex;
            this.parseResult = parseResult;
            this.returnType = CMinusParseResult.convertTypeSpecifierStringToReturnTypeEnum(typeSpecifier);
        }
    }

    public static final String EOF_TOKEN = "$";

    public static final Token<CMinusLexer.TokenType> getToken(final List<Token<CMinusLexer.TokenType>> tokens, final int index)
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

    public static final boolean isAdditionOperator(final Token<CMinusLexer.TokenType> token)
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
                    return false;
            }
        }
        else
            return false;
    }

    public static final boolean isAssignmentOperator(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.OPERATOR )
        {
            switch ( token.getData() )
            {
                case "=":
                    return true;

                default:
                    return false;
            }
        }
        else
            return false;
    }

    public static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.GROUPING )
        {
            return true;
        }
        else
            return false;
    }

    public static final boolean isGroupingSymbol(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( CMinusParser.isGroupingSymbol(token) )
        {
            return token.getData().contentEquals(symbol);
        }
        else
            return false;
    }

    public static final boolean isIdentifier(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.IDENTIFIER )
        {
            return true;
        }
        else
            return false;
    }

    public static final boolean isKeyword(final Token<CMinusLexer.TokenType> token)
    {
        if ( token.getType() == CMinusLexer.TokenType.KEYWORD )
        {
            return true;
        }
        else
            return false;
    }

    public static final boolean isKeyword(final Token<CMinusLexer.TokenType> token, final String symbol)
    {
        if ( CMinusParser.isKeyword(token) )
        {
            return token.getData().contentEquals(symbol);
        }
        else
            return false;
    }

    public static final boolean isMultiplicationOperator(final Token<CMinusLexer.TokenType> token)
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
                    return false;
            }
        }
        else
            return false;
    }

    public static final boolean isNumber(final Token<CMinusLexer.TokenType> token)
    {
        return token.getData().matches(CMinusLexer.C_NUMBERS);
    }

    public static final boolean isRelationalOperator(final Token<CMinusLexer.TokenType> token)
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
                    return false;
            }
        }
        else
            return false;
    }

    public static final boolean isTypeSpecifier(final Token<CMinusLexer.TokenType> token)
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
                    return false;
            }
        }
        else
            return false;
    }

    private String                                            result       = "REJECT";
    private CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables = null;
    private List<Token<CMinusLexer.TokenType>>                tokens       = null;

    public CMinusParser(final List<Token<CMinusLexer.TokenType>> tokens, final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final boolean silent)
    {
        super();
        this.parse(tokens, symbolTables, silent);
    }

    public final String getResult()
    {
        return this.result;
    }

    public final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> getSymbolTables()
    {
        return this.symbolTables;
    }

    public final List<Token<CMinusLexer.TokenType>> getTokens()
    {
        return this.tokens;
    }

    protected final boolean parse(final List<Token<CMinusLexer.TokenType>> tokens, final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables, final boolean silent)
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
                symbolTables.enterScope();
                CMinusParseResult cmpr = CMinusParseProduction.declarationList(symbolTables, tokens, 0, false);
                symbolTables.exitScope();

                if ( cmpr.parseResult != CMinusParseResult.ParseResult.ACCEPT ) { throw new CMinusParseException(CMinusParser.getToken(tokens, cmpr.endIndex)); }

                tokens.remove(tokens.size() - 1);
                this.setResult("ACCEPT");
                return true;
            }
            catch ( final CMinusParseException cmpe )
            {
                if ( !silent )
                {
                    StdOut.println(cmpe.getMessage());
                }
            }
        }

        return false;
    }

    protected final void setResult(String result)
    {
        this.result = result;
    }

    protected final void setSymbolTables(final CMinusSemantics.SymTab<CMinusSemantics.SymTabRec> symbolTables)
    {
        this.symbolTables = symbolTables;
    }

    protected final void setTokens(final List<Token<CMinusLexer.TokenType>> tokens)
    {
        this.tokens = tokens;
    }
}
