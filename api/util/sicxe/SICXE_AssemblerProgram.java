/*
 * Title: SICXE_AssemblerProgram
 * Author: Matthew Boyette
 * Date: 3/27/2015
 * 
 * The purpose of this project is to implement a two-pass SIC/XE assembler.
 */

package api.util.sicxe;

import java.util.Collections;
import java.util.LinkedList;

import api.util.Lexer;
import api.util.Support;
import api.util.datastructures.SeparateChainingSymbolTable;
import api.util.stdlib.In;
import api.util.stdlib.Out;
import api.util.stdlib.StdOut;

public class SICXE_AssemblerProgram extends SimpleSymbolTable
{
	/*
	 * Construct the hard-coded static reference tables.
	 * 
	 * Directive table contains all assembler directives.
	 * Instruction table contains all program instructions.
	 * Register table contains all registers.
	 */
	public static final SeparateChainingSymbolTable<String,SICXE_OpCode>	DIRECTIVE_TABLE		= SICXE_AssemblerProgram.constructDirectiveTable
																									(
																									new SeparateChainingSymbolTable<String,SICXE_OpCode>()
																									);
	
	public static final SeparateChainingSymbolTable<String,SICXE_OpCode>	INSTRUCTION_TABLE	= SICXE_AssemblerProgram.constructInstructionTable
																									(
																									new SeparateChainingSymbolTable<String,SICXE_OpCode>()
																									);
	
	public static final SeparateChainingSymbolTable<String,Integer>			REGISTER_TABLE		= SICXE_AssemblerProgram.constructRegisterTable
																									(
																									new SeparateChainingSymbolTable<String,Integer>()
																									);
	
	protected static final String buildLiteralTableString(final SICXE_AssemblerProgram asmProgram)
	{
		// This method builds the literal table string for printing.
		// First get a list of all the keys in the literal table.
		LinkedList<String> literals = (LinkedList<String>)asmProgram.getLiteralTable().keys();
		
		// Sort the keys so the table can be easily searched visually.
		Collections.sort(literals);
		
		// Create an instance of StringBuilder to build the string.
		StringBuilder sb = new StringBuilder();
		
		// Append the table header.
		sb.append("\nLiteral\tHex Value\tLength\tAddress\n");
		
		// Loop through the list of keys.
		for (String s: literals)
		{
			SICXE_Literal l = SICXE_AssemblerProgram.resolveLiteral(s, asmProgram);
			
			// Append a row for each literal.
			sb.append(l.getInput() +
				"\t" +
				l.getExtraPaddedHexValue() +
				"\t\t" +
				l.getLength() +
				"\t" +
				Integer.toHexString(l.getAddress()) +
				"\n");
		}
		
		// Return the literal table string.
		return (sb.toString() + "\n");
	}
	
	protected static final String buildSymbolTableString(final SICXE_AssemblerProgram asmProgram)
	{
		// This method builds the symbol table string for printing.
		// First get a list of all the keys in the symbol table.
		LinkedList<String> symbols = (LinkedList<String>)asmProgram.getSymbolTable().keys();
		
		// Sort the keys so the table can be easily searched visually.
		Collections.sort(symbols);
		
		// Create an instance of StringBuilder to build the string.
		StringBuilder sb = new StringBuilder();
		
		// Append the special values tracked by the pass1 algorithm.
		sb.append("\nStart: " +
			Integer.toHexString(asmProgram.getStartVal()) +
			"\t\t\tEnd: " +
			Integer.toHexString(asmProgram.getEndVal()) +
			"\nLocation Counter: " +
			Integer.toHexString(asmProgram.getLocCtr()) +
			"\t\tProgram Length: " +
			Integer.toHexString(asmProgram.getPgmLen()) +
			"\n");
		
		// Append the table header.
		sb.append("\nTable Location\tLabel\tAddress\tUse\tCsect\n");
		
		// Loop through the list of keys.
		for (String s: symbols)
		{
			// Append a row for each symbol.
			sb.append(asmProgram.getSymbolTable().hash(s) +
				"\t\t" +
				s +
				"\t" +
				Integer.toHexString(asmProgram.getSymbolTable().get(s)) +
				"\t" +
				"main" +
				"\t" +
				"main" +
				"\n");
		}
		
		// Return the symbol table string.
		return (sb.toString() + "\n");
	}
	
	private static final SeparateChainingSymbolTable<String,SICXE_OpCode> constructDirectiveTable
		(
			final SeparateChainingSymbolTable<String,SICXE_OpCode> symTable
		)
	{
		// This table contains all of the possible assembler directives.
		// Pre-sorted in ascending alphabeticl order.
		symTable.put("BASE", new SICXE_OpCode(0, 0, 1));
		symTable.put("BYTE", new SICXE_OpCode(0, 1, 1));
		symTable.put("CSECT", new SICXE_OpCode(0, 0, 1));
		symTable.put("END", new SICXE_OpCode(0, 0, 1));
		symTable.put("EQU", new SICXE_OpCode(0, 0, 1));
		symTable.put("EXTDEF", new SICXE_OpCode(0, 0, 1));
		symTable.put("EXTREF", new SICXE_OpCode(0, 0, 1));
		symTable.put("LTORG", new SICXE_OpCode(0, 0, 0));
		symTable.put("NOBASE", new SICXE_OpCode(0, 0, 0));
		symTable.put("ORG", new SICXE_OpCode(0, 0, 1));
		symTable.put("RESB", new SICXE_OpCode(0xFF, 1, 1));
		symTable.put("RESW", new SICXE_OpCode(0xFFFFFF, 3, 1));
		symTable.put("START", new SICXE_OpCode(0, 0, 1));
		symTable.put("USE", new SICXE_OpCode(0, 0, 1));
		symTable.put("WORD", new SICXE_OpCode(0, 3, 1));
		
		return symTable;
	}
	
	private static final SeparateChainingSymbolTable<String,SICXE_OpCode> constructInstructionTable
		(
			final SeparateChainingSymbolTable<String,SICXE_OpCode> symTable
		)
	{
		// This table contains all of the possible program instructions.
		// Pre-sorted in ascending alphabeticl order.
		symTable.put("*ADD", new SICXE_OpCode(0x18, 3, 1));
		symTable.put("*AND", new SICXE_OpCode(0x40, 3, 1));
		symTable.put("*COMP", new SICXE_OpCode(0x28, 3, 1));
		symTable.put("*DIV", new SICXE_OpCode(0x24, 3, 1));
		symTable.put("*J", new SICXE_OpCode(0x3C, 3, 1));
		symTable.put("*JEQ", new SICXE_OpCode(0x30, 3, 1));
		symTable.put("*JGT", new SICXE_OpCode(0x34, 3, 1));
		symTable.put("*JLT", new SICXE_OpCode(0x38, 3, 1));
		symTable.put("*JSUB", new SICXE_OpCode(0x48, 3, 1));
		symTable.put("*LDA", new SICXE_OpCode(0x00, 3, 1));
		symTable.put("*LDCH", new SICXE_OpCode(0x50, 3, 1));
		symTable.put("*LDL", new SICXE_OpCode(0x08, 3, 1));
		symTable.put("*LDX", new SICXE_OpCode(0x04, 3, 1));
		symTable.put("*MUL", new SICXE_OpCode(0x20, 3, 1));
		symTable.put("*OR", new SICXE_OpCode(0x44, 3, 1));
		symTable.put("*RD", new SICXE_OpCode(0xD8, 3, 1));
		symTable.put("*RSUB", new SICXE_OpCode(0x4C, 3, 0));
		symTable.put("*STA", new SICXE_OpCode(0x0C, 3, 1));
		symTable.put("*STCH", new SICXE_OpCode(0x54, 3, 1));
		symTable.put("*STL", new SICXE_OpCode(0x14, 3, 1));
		symTable.put("*STSW", new SICXE_OpCode(0xE8, 3, 1));
		symTable.put("*STX", new SICXE_OpCode(0x10, 3, 1));
		symTable.put("*SUB", new SICXE_OpCode(0x1C, 3, 1));
		symTable.put("*TD", new SICXE_OpCode(0xE0, 3, 1));
		symTable.put("*TIX", new SICXE_OpCode(0x2C, 3, 1));
		symTable.put("*WD", new SICXE_OpCode(0xDC, 3, 1));
		symTable.put("+ADD", new SICXE_OpCode(0x18, 4, 1));
		symTable.put("+ADDF", new SICXE_OpCode(0x58, 4, 1));
		symTable.put("+AND", new SICXE_OpCode(0x40, 4, 1));
		symTable.put("+COMP", new SICXE_OpCode(0x28, 4, 1));
		symTable.put("+COMPF", new SICXE_OpCode(0x88, 4, 1));
		symTable.put("+DIV", new SICXE_OpCode(0x24, 4, 1));
		symTable.put("+DIVF", new SICXE_OpCode(0x64, 4, 1));
		symTable.put("+J", new SICXE_OpCode(0x3C, 4, 1));
		symTable.put("+JEQ", new SICXE_OpCode(0x30, 4, 1));
		symTable.put("+JGT", new SICXE_OpCode(0x34, 4, 1));
		symTable.put("+JLT", new SICXE_OpCode(0x38, 4, 1));
		symTable.put("+JSUB", new SICXE_OpCode(0x48, 4, 1));
		symTable.put("+LDA", new SICXE_OpCode(0x00, 4, 1));
		symTable.put("+LDB", new SICXE_OpCode(0x68, 4, 1));
		symTable.put("+LDCH", new SICXE_OpCode(0x50, 4, 1));
		symTable.put("+LDF", new SICXE_OpCode(0x70, 4, 1));
		symTable.put("+LDL", new SICXE_OpCode(0x08, 4, 1));
		symTable.put("+LDS", new SICXE_OpCode(0x6C, 4, 1));
		symTable.put("+LDT", new SICXE_OpCode(0x74, 4, 1));
		symTable.put("+LDX", new SICXE_OpCode(0x04, 4, 1));
		symTable.put("+LPS", new SICXE_OpCode(0xD0, 4, 1));
		symTable.put("+MUL", new SICXE_OpCode(0x20, 4, 1));
		symTable.put("+MULF", new SICXE_OpCode(0x60, 4, 1));
		symTable.put("+OR", new SICXE_OpCode(0x44, 4, 1));
		symTable.put("+RD", new SICXE_OpCode(0xD8, 4, 1));
		symTable.put("+RSUB", new SICXE_OpCode(0x4C, 4, 0));
		symTable.put("+SSK", new SICXE_OpCode(0xEC, 4, 1));
		symTable.put("+STA", new SICXE_OpCode(0x0C, 4, 1));
		symTable.put("+STB", new SICXE_OpCode(0x78, 4, 1));
		symTable.put("+STCH", new SICXE_OpCode(0x54, 4, 1));
		symTable.put("+STF", new SICXE_OpCode(0x80, 4, 1));
		symTable.put("+STI", new SICXE_OpCode(0xD4, 4, 1));
		symTable.put("+STL", new SICXE_OpCode(0x14, 4, 1));
		symTable.put("+STS", new SICXE_OpCode(0x7C, 4, 1));
		symTable.put("+STSW", new SICXE_OpCode(0xE8, 4, 1));
		symTable.put("+STT", new SICXE_OpCode(0x84, 4, 1));
		symTable.put("+STX", new SICXE_OpCode(0x10, 4, 1));
		symTable.put("+SUB", new SICXE_OpCode(0x1C, 4, 1));
		symTable.put("+SUBF", new SICXE_OpCode(0x5C, 4, 1));
		symTable.put("+TD", new SICXE_OpCode(0xE0, 4, 1));
		symTable.put("+TIX", new SICXE_OpCode(0x2C, 4, 1));
		symTable.put("+WD", new SICXE_OpCode(0xDC, 4, 1));
		symTable.put("ADD", new SICXE_OpCode(0x18, 3, 1));
		symTable.put("ADDF", new SICXE_OpCode(0x58, 3, 1));
		symTable.put("ADDR", new SICXE_OpCode(0x90, 2, 2));
		symTable.put("AND", new SICXE_OpCode(0x40, 3, 1));
		symTable.put("CLEAR", new SICXE_OpCode(0xB4, 2, 1));
		symTable.put("COMP", new SICXE_OpCode(0x28, 3, 1));
		symTable.put("COMPF", new SICXE_OpCode(0x88, 3, 1));
		symTable.put("COMPR", new SICXE_OpCode(0xA0, 2, 2));
		symTable.put("DIV", new SICXE_OpCode(0x24, 3, 1));
		symTable.put("DIVF", new SICXE_OpCode(0x64, 3, 1));
		symTable.put("DIVR", new SICXE_OpCode(0x9C, 2, 2));
		symTable.put("FIX", new SICXE_OpCode(0xC4, 1, 0));
		symTable.put("FLOAT", new SICXE_OpCode(0xC0, 1, 0));
		symTable.put("HIO", new SICXE_OpCode(0xF4, 1, 0));
		symTable.put("J", new SICXE_OpCode(0x3C, 3, 1));
		symTable.put("JEQ", new SICXE_OpCode(0x30, 3, 1));
		symTable.put("JGT", new SICXE_OpCode(0x34, 3, 1));
		symTable.put("JLT", new SICXE_OpCode(0x38, 3, 1));
		symTable.put("JSUB", new SICXE_OpCode(0x48, 3, 1));
		symTable.put("LDA", new SICXE_OpCode(0x00, 3, 1));
		symTable.put("LDB", new SICXE_OpCode(0x68, 3, 1));
		symTable.put("LDCH", new SICXE_OpCode(0x50, 3, 1));
		symTable.put("LDF", new SICXE_OpCode(0x70, 3, 1));
		symTable.put("LDL", new SICXE_OpCode(0x08, 3, 1));
		symTable.put("LDS", new SICXE_OpCode(0x6C, 3, 1));
		symTable.put("LDT", new SICXE_OpCode(0x74, 3, 1));
		symTable.put("LDX", new SICXE_OpCode(0x04, 3, 1));
		symTable.put("LPS", new SICXE_OpCode(0xD0, 3, 1));
		symTable.put("MUL", new SICXE_OpCode(0x20, 3, 1));
		symTable.put("MULF", new SICXE_OpCode(0x60, 3, 1));
		symTable.put("MULR", new SICXE_OpCode(0x98, 2, 2));
		symTable.put("NORM", new SICXE_OpCode(0xC8, 1, 0));
		symTable.put("OR", new SICXE_OpCode(0x44, 3, 1));
		symTable.put("RD", new SICXE_OpCode(0xD8, 3, 1));
		symTable.put("RMO", new SICXE_OpCode(0xAC, 2, 2));
		symTable.put("RSUB", new SICXE_OpCode(0x4C, 3, 0));
		symTable.put("SHIFTL", new SICXE_OpCode(0xA4, 2, 2));
		symTable.put("SHIFTR", new SICXE_OpCode(0xA8, 2, 2));
		symTable.put("SIO", new SICXE_OpCode(0xF0, 1, 0));
		symTable.put("SSK", new SICXE_OpCode(0xEC, 3, 1));
		symTable.put("STA", new SICXE_OpCode(0x0C, 3, 1));
		symTable.put("STB", new SICXE_OpCode(0x78, 3, 1));
		symTable.put("STCH", new SICXE_OpCode(0x54, 3, 1));
		symTable.put("STF", new SICXE_OpCode(0x80, 3, 1));
		symTable.put("STI", new SICXE_OpCode(0xD4, 3, 1));
		symTable.put("STL", new SICXE_OpCode(0x14, 3, 1));
		symTable.put("STS", new SICXE_OpCode(0x7C, 3, 1));
		symTable.put("STSW", new SICXE_OpCode(0xE8, 3, 1));
		symTable.put("STT", new SICXE_OpCode(0x84, 3, 1));
		symTable.put("STX", new SICXE_OpCode(0x10, 3, 1));
		symTable.put("SUB", new SICXE_OpCode(0x1C, 3, 1));
		symTable.put("SUBF", new SICXE_OpCode(0x5C, 3, 1));
		symTable.put("SUBR", new SICXE_OpCode(0x94, 2, 2));
		symTable.put("SVC", new SICXE_OpCode(0xB0, 2, 1));
		symTable.put("TD", new SICXE_OpCode(0xE0, 3, 1));
		symTable.put("TIO", new SICXE_OpCode(0xF8, 1, 0));
		symTable.put("TIX", new SICXE_OpCode(0x2C, 3, 1));
		symTable.put("TIXR", new SICXE_OpCode(0xB8, 2, 1));
		symTable.put("WD", new SICXE_OpCode(0xDC, 3, 1));
		
		return symTable;
	}
	
	private static final SeparateChainingSymbolTable<String,Integer> constructRegisterTable
		(
			final SeparateChainingSymbolTable<String,Integer> symTable
		)
	{
		// This table contains all of the possible registers.
		symTable.put("A", 0);
		symTable.put("X", 1);
		symTable.put("L", 2);
		symTable.put("PC", 8);
		symTable.put("SW", 9);
		symTable.put("B", 3);
		symTable.put("S", 4);
		symTable.put("T", 5);
		symTable.put("F", 6);
		
		return symTable;
	}
	
	protected static final int evaluateExpression(final String expression, final SICXE_AssemblerProgram asmProgram)
	{
		Integer left = null;
		Integer right = null;
		Integer result = null;
		
		if (expression != null)
		{
			String[] parts = expression.split(SICXE_Lexer.SICXE_OPERATORS, 2);
			String sign = expression.replaceAll("\\w+", "");
			
			if (expression.matches(Lexer.INTEGERS + SICXE_Lexer.SICXE_OPERATORS + Lexer.INTEGERS))
			{
				left = SICXE_AssemblerProgram.resolveOperand(parts[0].trim(), asmProgram);
				right = SICXE_AssemblerProgram.resolveOperand(parts[1].trim(), asmProgram);
			}
			else
				if (expression.matches(SICXE_Lexer.SICXE_IDENTIFIERS + SICXE_Lexer.SICXE_OPERATORS + Lexer.INTEGERS))
				{
					left = SICXE_AssemblerProgram.resolveSymbol(parts[0].trim(), asmProgram);
					right = SICXE_AssemblerProgram.resolveOperand(parts[1].trim(), asmProgram);
				}
				else
					if (expression.matches(Lexer.INTEGERS + SICXE_Lexer.SICXE_OPERATORS + SICXE_Lexer.SICXE_IDENTIFIERS))
					{
						left = SICXE_AssemblerProgram.resolveOperand(parts[0].trim(), asmProgram);
						right = SICXE_AssemblerProgram.resolveSymbol(parts[1].trim(), asmProgram);
					}
					else
					{
						left = SICXE_AssemblerProgram.resolveSymbol(parts[0].trim(), asmProgram);
						right = SICXE_AssemblerProgram.resolveSymbol(parts[1].trim(), asmProgram);
					}
			
			if ((left != null) && (right != null))
			{
				switch (sign)
				{
					case "-":
						
						result = (left - right);
						break;
					
					case "+":
						
						result = (left + right);
						break;
					
					case "*":
						
						result = (left * right);
						break;
					
					case "/":
						
						result = (left / right);
						break;
					
					default:
						
						break;
				}
			}
		}
		// TODO: Clean up this method, possible null pointer dereference of 'result'.
		return result;
	}
	
	public static final boolean isAssemblerDirective(final String s)
	{
		return SICXE_AssemblerProgram.DIRECTIVE_TABLE.contains(s);
	}
	
	public static final boolean isOpCode(final String s)
	{
		return (SICXE_AssemblerProgram.isAssemblerDirective(s) || SICXE_AssemblerProgram.isProgramInstruction(s));
	}
	
	public static final boolean isProgramInstruction(final String s)
	{
		return SICXE_AssemblerProgram.INSTRUCTION_TABLE.contains(s);
	}
	
	protected static final void output(final Out out, final String s)
	{
		StdOut.print(s);
		out.print(s);
	}
	
	protected static final void outputLn(final Out out, final String s)
	{
		StdOut.println(s);
		out.println(s);
	}
	
	protected static final SICXE_Literal resolveLiteral(final String literal, final SICXE_AssemblerProgram asmProgram)
	{
		if ((literal != null) && (asmProgram.getLiteralTable().contains(literal)))
		{
			return asmProgram.getLiteralTable().get(literal);
		}
		
		return null;
	}
	
	protected static final Integer resolveOperand(final String operand, final SICXE_AssemblerProgram asmProgram)
	{
		return SICXE_AssemblerProgram.resolveOperand(operand, asmProgram, 10);
	}
	
	protected static final Integer resolveOperand(final String operand, final SICXE_AssemblerProgram asmProgram,
		final int radix)
	{
		Integer result = null;
		
		// Handle expressions.
		if (operand.matches(SICXE_Lexer.SICXE_IDENTIFIERS + SICXE_Lexer.SICXE_OPERATORS + SICXE_Lexer.SICXE_IDENTIFIERS))
		{
			result = SICXE_AssemblerProgram.evaluateExpression(operand, asmProgram);
		}
		
		// Handle locCtr reference symbol.
		if (operand.equals("*"))
		{
			result = asmProgram.getLocCtr();
		}
		
		// Handle integer operands.
		if (operand.matches(Lexer.INTEGERS))
		{
			result = Integer.parseInt(operand, radix);
		}
		
		// Handle symbol references.
		if (asmProgram.getSymbolTable().contains(operand))
		{
			result = SICXE_AssemblerProgram.resolveSymbol(operand, asmProgram);
		}
		
		return result;
	}
	
	protected static final Integer resolveSymbol(final String symbol, final SICXE_AssemblerProgram asmProgram)
	{
		if ((symbol != null) && (asmProgram.getSymbolTable().contains(symbol)))
		{
			return asmProgram.getSymbolTable().get(symbol);
		}
		
		return null;
	}
	
	private int													baseAddress		= 0;
	private int													endVal			= 0;
	private boolean												isBaseFlagSet	= false;
	private int													lineCtr			= 0;
	private SICXE_AssemblerCodeLine[]							lines			= null;
	private SeparateChainingSymbolTable<String,SICXE_Literal>	literalTable	= null;
	private int													locCtr			= 0;
	private int													pgmLen			= 0;
	private int													startVal		= 0;
	
	public SICXE_AssemblerProgram(final String fileName)
	{
		super(fileName);
	}
	
	protected void addressLiterals()
	{
		LinkedList<String> literals = (LinkedList<String>)this.getLiteralTable().keys();
		
		for (String literal: literals)
		{
			SICXE_Literal l = SICXE_AssemblerProgram.resolveLiteral(literal, this);
			
			if (l.getAddress() < 0)
			{
				l.setAddress(this.getLocCtr());
				this.setLocCtr(this.getLocCtr() + l.getLength());
			}
			
			this.getLiteralTable().put(literal, l);
		}
	}
	
	public final int getBaseAddress()
	{
		return this.baseAddress;
	}
	
	public final int getEndVal()
	{
		return this.endVal;
	}
	
	public final int getLineCtr()
	{
		return this.lineCtr;
	}
	
	public final SICXE_AssemblerCodeLine[] getLines()
	{
		return this.lines;
	}
	
	public final SeparateChainingSymbolTable<String,SICXE_Literal> getLiteralTable()
	{
		return this.literalTable;
	}
	
	public final int getLocCtr()
	{
		return this.locCtr;
	}
	
	public final int getPgmLen()
	{
		return this.pgmLen;
	}
	
	public final int getStartVal()
	{
		return this.startVal;
	}
	
	protected void handleLabel(final SICXE_AssemblerCodeLine acl, final int lineNum, final Out out)
	{
		// EQU labels.
		// [Label] EQU [Value]
		// Associate the label with the value on the same line. Store them in the symbol table.
		if (acl.getOpCode().equals("EQU"))
		{
			if (acl.getOperand() != null)
			{
				Integer value = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(),
					this);
				
				if (value != null)
				{
					this.getSymbolTable().put(acl.getLabel(), value);
				}
			}
		}
		// Regular labels.
		// Associate the label with the address of the program instruction on the same line.
		// This is implemented by storing the label in the symbol table with the current value of locCtr.
		else
		{
			if (this.getSymbolTable().contains(acl.getLabel()))
			{
				// If the label is already in the symbol table, trigger a duplicate symbol error.
				String errorString = "PASS 1 ERROR: syntax error line " + lineNum + "; " +
					"duplicate symbol.";
				SICXE_AssemblerProgram.outputLn(out, errorString);
			}
			else
			{
				// If the label isn't a key in the symbol table, add it with the current value of locCtr.
				this.getSymbolTable().put(acl.getLabel(), this.getLocCtr());
			}
		}
	}
	
	protected void incrementLocCtr(final SICXE_AssemblerCodeLine acl, final int lineNum, final Out out)
	{
		if (SICXE_AssemblerProgram.isOpCode(acl.getOpCode()))
		{
			int incAmount = 0;
			
			if (SICXE_AssemblerProgram.isProgramInstruction(acl.getOpCode()))
			{
				// If the opCode is a program instruction, then increment locCtr by the instruction's format number.
				incAmount = SICXE_AssemblerProgram.INSTRUCTION_TABLE.get(acl.getOpCode()).getFormat();
			}
			
			if (SICXE_AssemblerProgram.isAssemblerDirective(acl.getOpCode()))
			{
				/*
				 * If the opCode is an assembler directive, then determine if it is a storage directive.
				 * The only assembler directives that increment locCtr are the storage directives.
				 * 
				 * BYTE, RESB, RESW, WORD
				 */
				
				switch (acl.getOpCode())
				{
					case "WORD":
						
						// Increment locCtr by 3
						incAmount = SICXE_AssemblerProgram.DIRECTIVE_TABLE.get(acl.getOpCode()).getFormat();
						break;
					
					case "RESW":
						
						// Increment locCtr by [Operand] * 3
						if (acl.getOperand() != null)
						{
							Integer numWords = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(),
								this);
							
							if (numWords != null)
							{
								incAmount = SICXE_AssemblerProgram.DIRECTIVE_TABLE.get(acl.getOpCode()).getFormat() * numWords;
							}
						}
						
						break;
					
					case "RESB":
						
						// Increment locCtr by [Operand]
						if (acl.getOperand() != null)
						{
							incAmount = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(),
								this);
						}
						
						break;
					
					case "LTORG":
						
						this.addressLiterals();
						break;
					
					case "BYTE":
						
						// Increment locCtr by the size in bytes of the [Operand]
						if (acl.getOperand() != null)
						{
							if (acl.getOperand().matches(SICXE_Lexer.SICXE_LITERALS))
							{
								incAmount = (new SICXE_Literal(acl.getOperand())).getLength();
							}
							else
							{
								if (Support.isStringParsedAsInteger(acl.getOperand()))
								{
									incAmount = SICXE_AssemblerProgram.DIRECTIVE_TABLE.get(acl.getOpCode()).getFormat();
								}
							}
						}
						
						break;
					
					default:
						
						break;
				}
			}
			
			this.setLocCtr(this.getLocCtr() + incAmount);
		}
		else
		{
			// If the opCode is neither a program instruction nor an assembler directive,
			// then trigger the invalid operation code error.
			String errorString = "PASS 1 ERROR: syntax error line " + lineNum + "; " +
				"invalid operation code.";
			SICXE_AssemblerProgram.outputLn(out, errorString);
		}
	}
	
	@Override
	protected void initialize()
	{
		super.initialize();
		this.setLiteralTable(new SeparateChainingSymbolTable<String,SICXE_Literal>());
		this.setLines(null);
		this.setStartVal(0);
		this.setLineCtr(0);
		this.setLocCtr(0);
		this.setEndVal(0);
		this.setPgmLen(0);
	}
	
	public final boolean isBaseFlagSet()
	{
		return this.isBaseFlagSet;
	}
	
	protected void pass1(final String fileName)
	{
		Out outputStream = new Out(fileName + ".pass1-results");
		SICXE_AssemblerCodeLine acl = null;
		
		// Get the first line of assembly code in the file, skipping initial full comment lines.
		for (int i = 0; i < this.getLineCtr(); i++)
		{
			acl = this.getLines()[i];
			
			if (acl != null)
			{
				if (!acl.isFullComment())
				{
					// Process START directive, if present.
					if (acl.getOpCode().equals("START"))
					{
						this.processStartDirective(acl);
						break;
					}
				}
			}
		}
		
		// Loop through the file line-by-line from the beginning.
		for (int i = 0; i < this.getLineCtr(); i++)
		{
			acl = this.getLines()[i];
			
			if (acl != null)
			{
				if (acl.isFullComment())
				{
					// Don't process full comments. Just pass them to the output.
					SICXE_AssemblerProgram.outputLn(outputStream, (i + 1) + ":" + acl.getInput());
				}
				else
				{
					// Update the address for this line of code by copying locCtr.
					acl.setAddress(this.getLocCtr());
					
					// Print output to the console and the intermediate file.
					SICXE_AssemblerProgram.outputLn(outputStream,
						(i + 1) + ":" + Integer.toHexString(this.getLocCtr()) + "\t" + acl.getInput());
					
					// Handle label, if present.
					if (acl.getLabel() != null)
					{
						this.handleLabel(acl, (i + 1), outputStream);
					}
					
					// Search for literals and add them to the literal table if they aren't already added.
					if ((acl.getOperand() != null) && (acl.getOperand().matches(SICXE_Lexer.SICXE_LITERALS)))
					{
						if (!this.getLiteralTable().contains(acl.getOperand()))
						{
							SICXE_Literal literal = new SICXE_Literal(acl.getOperand());
							this.getLiteralTable().put(acl.getOperand(), literal);
						}
					}
					
					if (acl.getOpCode() != null)
					{
						// Increment locCtr, depending on the given opCode.
						this.incrementLocCtr(acl, (i + 1), outputStream);
						
						// Process END directive, if present.
						if (acl.getOpCode().equals("END"))
						{
							this.processEndDirective(acl);
							break;
						}
					}
				}
			}
		}
		
		// Calculate program length.
		this.setPgmLen(this.getLocCtr() - this.getStartVal());
		// Output symbol table.
		SICXE_AssemblerProgram.output(outputStream, SICXE_AssemblerProgram.buildSymbolTableString(this));
		// Output literal table.
		if (!this.getLiteralTable().isEmpty())
		{
			SICXE_AssemblerProgram.output(outputStream, SICXE_AssemblerProgram.buildLiteralTableString(this));
		}
	}
	
	protected void pass2(final String fileName)
	{
		// TODO: Implement pass 2 of the SIC/XE assembler.
	}
	
	protected void processEndDirective(final SICXE_AssemblerCodeLine acl)
	{
		this.addressLiterals();
		
		if (acl.getOperand() != null)
		{
			Integer address = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(),
				this, 16);
			
			if (address != null)
			{
				this.setEndVal(address);
			}
		}
	}
	
	protected void processStartDirective(final SICXE_AssemblerCodeLine acl)
	{
		if (acl.getOperand() != null)
		{
			Integer address = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(),
				this, 16);
			
			if (address != null)
			{
				this.setStartVal(address);
				this.setLocCtr(address);
				this.setEndVal(address);
			}
		}
	}
	
	@Override
	protected void readFile(final String fileName)
	{
		// Try to read the file.
		try
		{
			// Initialize data. If the given file path is invalid, an exception is thrown.
			In inputStream = new In(fileName);
			
			// Establish how many lines are in the file.
			this.setLineCtr(Support.countLinesInTextFile(fileName));
			this.setLines(new SICXE_AssemblerCodeLine[this.getLineCtr()]);
			
			// Read in the file's data line by line and store it.
			for (int i = 0; ((i < this.getLineCtr()) && (inputStream.hasNextLine())); i++)
			{
				String line = inputStream.readLine();
				
				if (line != null)
				{
					this.getLines()[i] = new SICXE_AssemblerCodeLine(line.toUpperCase(), i, 0);
				}
			}
			
			// Execute the first pass of the SIC/XE assembler.
			this.pass1(fileName);
			
			// Execute the second pass of the SIC/XE assembler.
			this.pass2(fileName);
		}
		catch (final Exception exception)
		{
			exception.printStackTrace();
		}
	}
	
	protected final void setBaseAddress(final int baseAddress)
	{
		this.baseAddress = baseAddress;
	}
	
	protected final void setBaseFlagSet(final boolean isBaseFlagSet)
	{
		this.isBaseFlagSet = isBaseFlagSet;
	}
	
	protected final void setEndVal(final int endVal)
	{
		this.endVal = endVal;
	}
	
	protected final void setLineCtr(final int lineCtr)
	{
		this.lineCtr = lineCtr;
	}
	
	protected final void setLines(final SICXE_AssemblerCodeLine[] lines)
	{
		this.lines = lines;
	}
	
	protected final void setLiteralTable(final SeparateChainingSymbolTable<String,SICXE_Literal> literalTable)
	{
		this.literalTable = literalTable;
	}
	
	protected final void setLocCtr(final int locCtr)
	{
		this.locCtr = locCtr;
	}
	
	protected final void setPgmLen(final int pgmLen)
	{
		this.pgmLen = pgmLen;
	}
	
	protected final void setStartVal(final int startVal)
	{
		this.startVal = startVal;
	}
}