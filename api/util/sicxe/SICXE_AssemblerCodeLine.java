/*
 * Title: SICXE_AssemblerCodeLine
 * Author: Matthew Boyette
 * Date: 3/27/2015
 * 
 * This class represents a line of SIC/XE assembler code.
 */

package api.util.sicxe;

import java.util.List;
import api.util.datastructures.Token;
import api.util.sicxe.SICXE_Lexer.TokenType;

public class SICXE_AssemblerCodeLine
{
    public static final String[] parseLine(final String s)
    {
        /*///@formatter:off
         * Line format (space delimited): Label OpCode Operand(s) Comment
         * Label & Comment are optional fields. Operand(s) are comma delimited.
         * OpCode must either be an assembler directive or a program instruction.
         * Some OpCodes don't take any Operands (like RSUB).
         *///@formatter:on

        String comment = null, label = null, opCode = null, operand = null;
        List<Token<TokenType>> tokens = ( new SICXE_Lexer<TokenType>() ).lex(s);
        int opCodeToken = -1;

        for ( int i = 0; i < tokens.size(); i++ )
        {
            Token<TokenType> token = tokens.get(i);

            if ( SICXE_AssemblerProgram.isOpCode(token.getData()) )
            {
                opCodeToken = i;
                opCode = token.getData().trim();
                break;
            }
        }

        if ( opCode != null )
        {
            if ( opCodeToken > 0 )
            {
                label = tokens.get(opCodeToken - 1).getData().trim();
            }

            int remainingTokens = ( tokens.size() - ( opCodeToken + 1 ) );

            if ( remainingTokens > 0 )
            {
                boolean doesOpCodeTakeOperands = false;
                byte numOperands = 0;
                int operandToken = ( opCodeToken + 1 );

                if ( SICXE_AssemblerProgram.isAssemblerDirective(opCode) )
                {
                    numOperands = SICXE_AssemblerProgram.DIRECTIVE_TABLE.get(opCode).getNumOperands();
                }

                if ( SICXE_AssemblerProgram.isProgramInstruction(opCode) )
                {
                    numOperands = SICXE_AssemblerProgram.INSTRUCTION_TABLE.get(opCode).getNumOperands();
                }

                if ( numOperands > 0 )
                {
                    doesOpCodeTakeOperands = true;
                }

                if ( doesOpCodeTakeOperands )
                {
                    operand = tokens.get(operandToken).getData().trim();

                    if ( remainingTokens > 1 )
                    {
                        int punctToken = ( operandToken + 1 );
                        Token<TokenType> punct = tokens.get(punctToken);

                        if ( tokens.get(operandToken).getType() == TokenType.OPERATOR )
                        {
                            operand = ( operand + punct.getData() ).trim();
                        }
                        else
                        {
                            switch ( punct.getData() )
                            {
                                case ",":
                                case "-":
                                case "+":
                                case "/":
                                case "*":

                                    if ( remainingTokens > 2 )
                                    {
                                        int argToken = ( punctToken + 1 );
                                        Token<TokenType> arg = tokens.get(argToken);

                                        operand = ( operand + punct.getData() + arg.getData() ).trim();
                                    }

                                    break;

                                default:

                                    break;
                            }
                        }
                    }
                }

                // TODO: Identify comments.
            }
        }

        String[] result = { label, opCode, operand, comment
        };
        return result;
    }

    private int     address       = -1;
    private String  comment       = null;
    private String  input         = null;
    private boolean isFullComment = false;
    private String  label         = null;
    private int     lineNum       = -1;
    private String  objectCode    = null;
    private String  opCode        = null;
    private String  operand       = null;

    public SICXE_AssemblerCodeLine(final String s, final int lineNum, final int locCtr)
    {
        this.setInput(s);
        this.setLineNum(lineNum);
        this.setFullComment(true);

        if ( ( s != null ) && ( s.trim().isEmpty() == false ) )
        {
            if ( ( s.trim().charAt(0) != '.' ) && ( s.trim().charAt(0) != ';' ) && ( !s.trim().startsWith("//") ) && ( !s.trim().startsWith("/*") ) )
            {
                String[] parts = SICXE_AssemblerCodeLine.parseLine(s.trim());
                this.setFullComment(false);
                this.setAddress(locCtr);
                this.setLabel(parts[0]);
                this.setObjectCode("");
                this.setOpCode(parts[1]);
                this.setOperand(parts[2]);
                this.setComment(parts[3]);
            }
        }

        if ( this.isFullComment() )
        {
            this.setComment(this.getInput());
            this.setAddress( -1);
            this.setLabel(null);
            this.setObjectCode(null);
            this.setOpCode(null);
            this.setOperand(null);
        }
    }

    public final int getAddress()
    {
        return this.address;
    }

    public final String getComment()
    {
        return this.comment;
    }

    public final String getInput()
    {
        return this.input;
    }

    public final String getLabel()
    {
        return this.label;
    }

    public final int getLineNum()
    {
        return this.lineNum;
    }

    public final String getObjectCode()
    {
        return this.objectCode;
    }

    public final String getOpCode()
    {
        return this.opCode;
    }

    public final String getOperand()
    {
        return this.operand;
    }

    public final boolean isFullComment()
    {
        return this.isFullComment;
    }

    public final void setAddress(final int address)
    {
        this.address = address;
    }

    protected final void setComment(final String comment)
    {
        this.comment = comment;
    }

    protected final void setFullComment(final boolean isFullComment)
    {
        this.isFullComment = isFullComment;
    }

    protected final void setInput(final String input)
    {
        this.input = input;
    }

    protected final void setLabel(final String label)
    {
        this.label = label;
    }

    protected final void setLineNum(final int lineNum)
    {
        this.lineNum = lineNum;
    }

    public final void setObjectCode(final String objectCode)
    {
        this.objectCode = objectCode;
    }

    protected final void setOpCode(final String opCode)
    {
        this.opCode = opCode;
    }

    protected final void setOperand(final String operand)
    {
        this.operand = operand;
    }
}
