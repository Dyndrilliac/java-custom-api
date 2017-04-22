/*
 * Title: CMinusCodeGeneration
 * Author: Matthew Boyette
 * Date: 04/10/2017 - 04/14/2017
 *
 * This class functions as a generic intermediate code generator for the C-Minus language.
 */

package api.util.cminus;

import java.util.LinkedList;
import java.util.List;
import api.util.cminus.CMinusSemantics.SymTab;
import api.util.cminus.CMinusSemantics.SymTabRec;
import api.util.datastructures.Token;

public class CMinusCodeGeneration
{
    public static class QuadrupleWriter
    {
        public static enum INSTRUCTION
        {
            ADD, ALLOC, ARG, ASSIGN, BR, BRE, BRG, BRGE, BRL, BRLE, BRNE, CALL, COMP, DISP, DIV, END, FUNC, MULT, PARAM, RETURN, SUB
        }

        public static class Quadruple implements Comparable<Quadruple>
        {
            private int    statementIndex       = 0;
            private String statementInstruction = null;
            private String statementOperandA    = null;
            private String statementOperandB    = null;
            private String statementResult      = null;

            public Quadruple(final INSTRUCTION statementInstruction, final String statementResult)
            {
                this(statementInstruction, ( ( statementInstruction == INSTRUCTION.ALLOC ) ? "4" : "" ), "", statementResult);
            }

            public Quadruple(final INSTRUCTION statementInstruction, final String statementOperandA, final String statementOperandB, final String statementResult)
            {
                super();
                this.statementIndex = CMinusCodeGeneration.statementCounter++;
                this.statementInstruction = statementInstruction.name().toLowerCase();
                this.statementOperandA = statementOperandA;
                this.statementOperandB = statementOperandB;
                this.statementResult = statementResult;
            }

            @Override
            public int compareTo(Quadruple arg0)
            {
                return ( this.getStatementIndex() - arg0.getStatementIndex() );
            }

            @Override
            public boolean equals(final Object obj)
            {
                if ( this == obj ) return true;

                if ( obj == null ) return false;

                if ( !( obj instanceof Quadruple ) ) return false;

                Quadruple other = (Quadruple) obj;

                if ( statementIndex != other.statementIndex ) return false;

                if ( statementInstruction != other.statementInstruction ) return false;

                if ( statementOperandA == null )
                {
                    if ( other.statementOperandA != null ) return false;
                }
                else if ( !statementOperandA.equals(other.statementOperandA) ) return false;

                if ( statementOperandB == null )
                {
                    if ( other.statementOperandB != null ) return false;
                }
                else if ( !statementOperandB.equals(other.statementOperandB) ) return false;

                if ( statementResult == null )
                {
                    if ( other.statementResult != null ) return false;
                }
                else if ( !statementResult.equals(other.statementResult) ) return false;

                return true;
            }

            public final int getStatementIndex()
            {
                return statementIndex;
            }

            public final String getStatementInstruction()
            {
                return statementInstruction;
            }

            public final String getStatementOperandA()
            {
                return statementOperandA;
            }

            public final String getStatementOperandB()
            {
                return statementOperandB;
            }

            public final String getStatementResult()
            {
                return statementResult;
            }

            @Override
            public int hashCode()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + statementIndex;
                result = prime * result + ( ( statementInstruction == null ) ? 0 : statementInstruction.hashCode() );
                result = prime * result + ( ( statementOperandA == null ) ? 0 : statementOperandA.hashCode() );
                result = prime * result + ( ( statementOperandB == null ) ? 0 : statementOperandB.hashCode() );
                result = prime * result + ( ( statementResult == null ) ? 0 : statementResult.hashCode() );
                return result;
            }

            public final void setStatementResult(final String statementResult)
            {
                this.statementResult = statementResult;
            }

            @Override
            public final String toString()
            {
                return String.format("%-10d %-15s %-15s %-15s %-15s", this.statementIndex, this.statementInstruction, this.statementOperandA, this.statementOperandB, this.statementResult);
            }
        }

        public static final void writeAddOrSub(final boolean isAdd, final String op1, final String op2, final boolean isNewVar)
        {
            String _op1 = CMinusCodeGeneration.checkOperand(op1);
            String _op2 = CMinusCodeGeneration.checkOperand(op2);

            String result = "";

            if ( isNewVar )
            {
                result = CMinusCodeGeneration.getTempVar(true, -1);
            }
            else
            {
                result = CMinusCodeGeneration.getTempVar(true, 0);
            }

            if ( isAdd )
            {
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ADD, _op1, _op2, result));
            }
            else
            {
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.SUB, _op1, _op2, result));
            }
        }

        public static final void writeArgument(final String result)
        {
            String _result = result;

            if ( _result != null )
            {
                if ( _result.isEmpty() )
                {
                    _result = CMinusCodeGeneration.getTempVar(true, 0);
                }
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ARG, _result));
        }

        public static final void writeArrayAlloc(final CMinusSemantics.ArrRec record)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ALLOC, Integer.toString(4 * record.size), "", record.name));
        }

        public static final void writeAssignment(final List<Token<CMinusLexer.TokenType>> variable, final List<Token<CMinusLexer.TokenType>> expression)
        {
            String op1 = "", result = "";

            if ( expression.size() == 1 )
            {
                // Operand1 is a single variable name
                op1 = expression.get(0).getData();
            }
            else if ( expression.size() > 1 )
            {
                // Operand1 is a new temporary variable
                op1 = CMinusCodeGeneration.getTempVar(true, 0);
            }

            if ( variable.size() == 1 )
            {
                // Result is a single variable name
                result = variable.get(0).getData();
            }
            else if ( variable.size() > 1 )
            {
                // Result is the current temporary variable
                result = CMinusCodeGeneration.getTempVar(true, 0);
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ASSIGN, op1, "", result));
        }

        public static final void writeComparison(final String op1, final String op2, final String operator, final boolean isNewVar)
        {
            String _op1 = CMinusCodeGeneration.checkOperand(op1);
            String _op2 = CMinusCodeGeneration.checkOperand(op2);

            String result = "";

            if ( isNewVar )
            {
                result = CMinusCodeGeneration.getTempVar(true, -1);
            }
            else
            {
                result = CMinusCodeGeneration.getTempVar(true, 0);
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.COMP, _op1, _op2, result));

            if ( CMinusCodeGeneration.isInControlStmnt )
            {
                CMinusCodeGeneration.QuadrupleWriter.writeConditionalBranch(operator);
            }
        }

        public static final void writeConditionalBranch(final String operator)
        {
            String op1 = CMinusCodeGeneration.getTempVar(true, 0);
            INSTRUCTION instruction = null;

            switch ( operator )
            {
                case "<":
                    instruction = INSTRUCTION.BRGE;
                    break;

                case "<=":
                    instruction = INSTRUCTION.BRG;
                    break;

                case ">":
                    instruction = INSTRUCTION.BRLE;
                    break;

                case ">=":
                    instruction = INSTRUCTION.BRL;
                    break;

                case "==":
                    instruction = INSTRUCTION.BRNE;
                    break;

                case "!=":
                    instruction = INSTRUCTION.BRE;
                    break;

                default:
                    instruction = INSTRUCTION.BR;
                    break;
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(instruction, op1, "", "$BP"));
        }

        public static final void writeDisplacement(final CMinusSemantics.ArrRec record, final String op2)
        {
            String _op2 = op2;

            if ( _op2 != null )
            {
                if ( _op2.isEmpty() )
                {
                    _op2 = CMinusCodeGeneration.getTempVar(true, 1);
                }
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.MULT, "4", _op2, CMinusCodeGeneration.getTempVar(true, -1)));
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.DISP, record.name, CMinusCodeGeneration.getTempVar(true, 0), CMinusCodeGeneration.getTempVar(true, -1)));
        }

        public static final void writeFunctionCall(final CMinusSemantics.FunRec functionRecord, final int argCount)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.CALL, functionRecord.name, Integer.toString(argCount), CMinusCodeGeneration.getTempVar(true, -1)));
        }

        public static final void writeFunctionStart(final CMinusSemantics.FunRec functionRecord)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.FUNC, functionRecord.name, functionRecord.type, Integer.toString(functionRecord.getNumParams())));

            for ( CMinusSemantics.SymTabRec record : functionRecord.getParams() )
            {
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.PARAM, ""));
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ALLOC, record.name));
            }
        }

        public static final void writeFunctionStop(final CMinusSemantics.FunRec functionRecord)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.END, INSTRUCTION.FUNC.name().toLowerCase(), functionRecord.name, ""));
        }

        public static final void writeMultOrDiv(final boolean isMult, final String op1, final String op2, final boolean isNewVar)
        {
            String _op1 = CMinusCodeGeneration.checkOperand(op1);
            String _op2 = CMinusCodeGeneration.checkOperand(op2);

            String result = "";

            if ( isNewVar )
            {
                result = CMinusCodeGeneration.getTempVar(true, -1);
            }
            else
            {
                result = CMinusCodeGeneration.getTempVar(true, 0);
            }

            if ( isMult )
            {
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.MULT, _op1, _op2, result));
            }
            else
            {
                CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.DIV, _op1, _op2, result));
            }
        }

        public static final void writeReturn(final String result)
        {
            String _result = result;

            if ( _result != null )
            {
                if ( _result.isEmpty() )
                {
                    _result = CMinusCodeGeneration.getTempVar(true, 0);
                }
            }

            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.RETURN, _result));
        }

        public static final void writeUnconditionalBranch(final int statementIndex)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.BR, Integer.toString(statementIndex)));
        }

        public static final void writeVariableAlloc(final CMinusSemantics.VarRec record)
        {
            CMinusCodeGeneration.staticQuadBuffer.add(new Quadruple(INSTRUCTION.ALLOC, record.name));
        }
    }

    public static boolean                            isInControlStmnt = false;
    public static int                                statementCounter = 0;
    protected static List<QuadrupleWriter.Quadruple> staticQuadBuffer = new LinkedList<QuadrupleWriter.Quadruple>();
    protected static int                             variableCounter  = -1;

    protected static final String checkOperand(final String operand)
    {
        if ( operand != null )
        {
            if ( operand.isEmpty() ) { return CMinusCodeGeneration.getTempVar(true, 1); }
        }

        return operand;
    }

    protected static final String getTempVar(final boolean bIncrement, final int iFixFlag)
    {
        if ( bIncrement )
        {
            if ( iFixFlag > 0 )
            {
                return "_t" + Integer.toString(CMinusCodeGeneration.variableCounter++);
            }
            else if ( iFixFlag < 0 )
            {
                return "_t" + Integer.toString( ++CMinusCodeGeneration.variableCounter);
            }
            else
            {
                return "_t" + Integer.toString(CMinusCodeGeneration.variableCounter);
            }
        }
        else
        {
            if ( iFixFlag > 0 )
            {
                return "_t" + Integer.toString(CMinusCodeGeneration.variableCounter--);
            }
            else if ( iFixFlag < 0 )
            {
                return "_t" + Integer.toString( --CMinusCodeGeneration.variableCounter);
            }
            else
            {
                return "_t" + Integer.toString(CMinusCodeGeneration.variableCounter);
            }
        }
    }

    public static final void reinitialize()
    {
        CMinusCodeGeneration.statementCounter = 0;
        CMinusCodeGeneration.staticQuadBuffer = new LinkedList<QuadrupleWriter.Quadruple>();
        CMinusCodeGeneration.variableCounter = -1;
        CMinusCodeGeneration.isInControlStmnt = false;
    }

    private StringBuffer                       result       = null;
    private SymTab<SymTabRec>                  symbolTables = null;
    private List<Token<CMinusLexer.TokenType>> tokens       = null;

    public CMinusCodeGeneration(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        super();
        this.result = new StringBuffer();
        this.codeGen(tokens, symbolTables, silent);
    }

    protected final boolean codeGen(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);

            return this.setResult(true);
        }

        return this.setResult(false);
    }

    public final String getResult()
    {
        return this.result.toString();
    }

    public final SymTab<SymTabRec> getSymbolTables()
    {
        return this.symbolTables;
    }

    public final List<Token<CMinusLexer.TokenType>> getTokens()
    {
        return this.tokens;
    }

    protected final boolean setResult(final boolean result)
    {
        for ( QuadrupleWriter.Quadruple quad : CMinusCodeGeneration.staticQuadBuffer )
        {
            if ( ( this.result != null ) && ( quad != null ) )
            {
                this.result.append(quad.toString() + "\n");
            }
        }

        CMinusCodeGeneration.reinitialize();
        return result;
    }

    protected final void setSymbolTables(final SymTab<SymTabRec> symbolTables)
    {
        this.symbolTables = symbolTables;
    }

    protected final void setTokens(final List<Token<CMinusLexer.TokenType>> tokens)
    {
        this.tokens = tokens;
    }
}
