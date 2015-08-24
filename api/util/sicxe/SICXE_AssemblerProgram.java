/*
 * Title: SICXE_AssemblerProgram
 * Author: Matthew Boyette
 * Date: 3/27/2015 - 4/23/2015
 * 
 * The purpose of this class is to provide a feature complete implementation for a two-pass SIC/XE assembler.
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
	// Directive table contains all possible assembler directives.
	public static final SeparateChainingSymbolTable<String,SICXE_OpCode>	DIRECTIVE_TABLE		= SICXE_AssemblerProgram.constructDirectiveTable
																									(
																									new SeparateChainingSymbolTable<String,SICXE_OpCode>()
																									);
	
	// Output file extensions.
	public static final String												fileExtLst			= ".lst";
	public static final String												fileExtMid			= ".mid";
	public static final String												fileExtObj			= ".obj";
	
	// Instruction table contains all possible program instructions.
	public static final SeparateChainingSymbolTable<String,SICXE_OpCode>	INSTRUCTION_TABLE	= SICXE_AssemblerProgram.constructInstructionTable
																									(
																									new SeparateChainingSymbolTable<String,SICXE_OpCode>()
																									);
	
	// Register table contains all possible registers.
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
		sb.append("Literal\t\tHex Value\tLength\tAddress\n");
		
		// Loop through the list of keys.
		for (String s: literals)
		{
			SICXE_Literal l = SICXE_AssemblerProgram.resolveLiteral(s, asmProgram);
			
			// Append a row for each literal.
			sb.append(String.format("%-10S", l.getInput()) +
				"\t" +
				String.format("%-10S", l.getHexValue()) +
				"\t" +
				String.format("%-4d", l.getLength()) +
				"\t" +
				String.format("%-3X", l.getAddress()) +
				"\n");
		}
		
		// Return the literal table string.
		return sb.toString();
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
			String.format("%-15X", asmProgram.getStartVal()) +
			"\tEnd: " +
			String.format("%-3X", asmProgram.getEndVal()) +
			"\nLocation Counter: " +
			String.format("%-3X", asmProgram.getLocCtr()) +
			"\tProgram Length: " +
			String.format("%-3X", asmProgram.getPgmLen()) +
			"\n");
		
		// Append the table header.
		sb.append("\nTable Location\tLabel\tAddress\tUse\t\tCSect\n");
		
		// Loop through the list of keys.
		for (String s: symbols)
		{
			// Append a row for each symbol.
			sb.append(String.format("%-10d", asmProgram.getSymbolTable().hash(s)) +
				"\t\t" +
				String.format("%-6S", s) +
				"\t" +
				String.format("%-4X", asmProgram.getSymbolTable().get(s)) +
				"\t" +
				"main" +
				"\t" +
				"main" +
				"\n");
		}
		
		// Return the symbol table string.
		return sb.toString();
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
		symTable.put("B", 3);
		symTable.put("S", 4);
		symTable.put("T", 5);
		symTable.put("F", 6);
		symTable.put("PC", 8);
		symTable.put("SW", 9);
		
		return symTable;
	}
	
	// TODO: Expressions - Relative vs Absolute Symbols
	protected static final int evaluateExpression(final String expression, final SICXE_AssemblerProgram asmProgram)
	{
		Integer left = null, right = null, result = 0;
		
		if (expression != null)
		{
			String[] parts = expression.split(SICXE_Lexer.SICXE_OPERATORS, 2);
			String sign = expression.replaceAll("\\w+", "");
			
			if (expression.matches(Lexer.INTEGERS +
				SICXE_Lexer.SICXE_OPERATORS +
				Lexer.INTEGERS))
			{
				left = SICXE_AssemblerProgram.resolveOperand(parts[0].trim(), asmProgram);
				right = SICXE_AssemblerProgram.resolveOperand(parts[1].trim(), asmProgram);
			}
			else
				if (expression.matches(SICXE_Lexer.SICXE_IDENTIFIERS +
					SICXE_Lexer.SICXE_OPERATORS +
					Lexer.INTEGERS))
				{
					left = SICXE_AssemblerProgram.resolveSymbol(parts[0].trim(), asmProgram);
					right = SICXE_AssemblerProgram.resolveOperand(parts[1].trim(), asmProgram);
				}
				else
					if (expression.matches(Lexer.INTEGERS +
						SICXE_Lexer.SICXE_OPERATORS +
						SICXE_Lexer.SICXE_IDENTIFIERS))
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
		if (operand.matches(SICXE_Lexer.SICXE_IDENTIFIERS +
			SICXE_Lexer.SICXE_OPERATORS +
			SICXE_Lexer.SICXE_IDENTIFIERS))
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
	private String												fileName		= null;
	private boolean												isBaseFlag		= false;
	private int													lineCtr			= 0;
	private SICXE_AssemblerCodeLine[]							lines			= null;
	private SeparateChainingSymbolTable<String,SICXE_Literal>	literalTable	= null;
	private int													locCtr			= 0;
	private boolean												pass1Error		= false;
	private boolean												pass2Error		= false;
	private int													pgmLen			= 0;
	private int													startVal		= 0;
	
	public SICXE_AssemblerProgram(final String fileName)
	{
		super(fileName);
	}
	
	// TODO: Assign addresses to literals (create literal pools)
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
	
	public void assembleProgram()
	{
		// Try to assemble the program.
		try
		{
			StdOut.println("University of North Florida: SIC/XE Assembler");
			StdOut.println("Version Date 4/23/2015");
			
			// Execute the first pass of the SIC/XE assembler.
			this.pass1();
			
			if (!this.isPass1Error())
			{
				// Execute the second pass of the SIC/XE assembler.
				this.pass2();
				
				if (this.isPass2Error())
				{
					StdOut.println("Errors (Pass 2): partial object code generation, but no object file instantiation. " +
						"Refer to " + this.getFileName() + SICXE_AssemblerProgram.fileExtLst + ".\n");
				}
				else
				{
					StdOut.println("Assembler report file: " + this.getFileName() + SICXE_AssemblerProgram.fileExtLst);
					StdOut.println("\t  object file: " + this.getFileName() + SICXE_AssemblerProgram.fileExtObj);
					StdOut.println("\t  middle file: " + this.getFileName() + SICXE_AssemblerProgram.fileExtMid);
				}
			}
			else
			{
				StdOut.println("Errors (Pass 1): no object code generated. " +
					"Refer to " + this.getFileName() + SICXE_AssemblerProgram.fileExtMid + ".\n");
			}
		}
		catch (final Exception exception)
		{
			exception.printStackTrace();
			System.exit(-2);
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
	
	public final String getFileName()
	{
		return this.fileName;
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
	
	protected void handleBase(final SICXE_AssemblerCodeLine acl)
	{
		if (acl.getOpCode() != null)
		{
			switch (acl.getOpCode())
			{
				case "BASE":
					
					if (acl.getOperand() != null)
					{
						Integer baseAddress = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this);
						
						if (baseAddress != null)
						{
							this.setBaseAddress(baseAddress);
							this.setBaseFlag(true);
						}
					}
					break;
					
				case "NOBASE":
					
					this.setBaseAddress(0);
					this.setBaseFlag(true);
					break;
					
				default:
					
					break;
			}
		}
	}
	
	protected void handleLabel(final SICXE_AssemblerCodeLine acl, final Out out)
	{
		if (acl.getLabel() != null)
		{
			if (this.getSymbolTable().contains(acl.getLabel()))
			{
				// If the label is already in the symbol table, then trigger a duplicate label error.
				String errorString = "ERROR: Duplicate label found on line " + acl.getLineNum() + ".";
				out.println(errorString);
				this.setPass1Error(true);
			}
			else
			{
				if (acl.getOpCode() != null)
				{
					// EQU labels.
					// [Label] = [Operand]
					// Associate the label with the value on the same line.
					// Store them in the symbol table.
					if (acl.getOpCode().equals("EQU"))
					{
						if (acl.getOperand() != null)
						{
							Integer value = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this);
							
							if (value != null)
							{
								this.getSymbolTable().put(acl.getLabel(), value);
							}
						}
					}
					// Regular labels.
					// [Label] = [locCtr]
					// Associate the label with the address of the program instruction on the same line.
					// Store them in the symbol table.
					else
					{
						this.getSymbolTable().put(acl.getLabel(), this.getLocCtr());
					}
				}
			}
		}
	}
	
	protected void handleLiteral(final SICXE_AssemblerCodeLine acl, final boolean pass1)
	{
		if (acl.getOperand() != null)
		{
			// TODO: Literals - Pass 1
			if (pass1)
			{
				// Search for literals and add them to the literal table if they aren't already there.
				if (acl.getOperand().matches(SICXE_Lexer.SICXE_LITERALS))
				{
					if (!this.getLiteralTable().contains(acl.getOperand()))
					{
						SICXE_Literal literal = new SICXE_Literal(acl.getOperand());
						this.getLiteralTable().put(acl.getOperand(), literal);
					}
				}
			}
			// TODO: Literals - Pass 2
			else
			{
				//
			}
		}
	}
	
	protected void incrementLocCtr(final SICXE_AssemblerCodeLine acl, final Out out)
	{
		if (acl.getOpCode() != null)
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
					 * 
					 * Some assembler storage directives interact with locCtr in other ways, such as LTORG and ORG.
					 */
					int format = SICXE_AssemblerProgram.DIRECTIVE_TABLE.get(acl.getOpCode()).getFormat();
					
					switch (acl.getOpCode())
					{
						case "WORD":
							
							// Increment locCtr by 3
							incAmount = format;
							break;
						
						case "RESW":
							
							// Increment locCtr by [Operand] * 3
							if (acl.getOperand() != null)
							{
								Integer numWords = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this);
								
								if (numWords != null)
								{
									incAmount = format * numWords;
								}
							}
							break;
						
						case "RESB":
							
							// Increment locCtr by [Operand]
							if (acl.getOperand() != null)
							{
								incAmount = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this);
							}
							break;
						
						case "ORG":
							
							// Set locCtr to [Operand]
							if (acl.getOperand() != null)
							{
								this.setLocCtr(SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this));
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
										incAmount = format;
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
				// If the opCode is neither a valid program instruction nor a valid assembler directive,
				// then trigger an unsupported operation code error.
				String errorString = "ERROR: Unsupported operation code found on line " + acl.getLineNum() + ".";
				out.println(errorString);
				this.setPass1Error(true);
			}
		}
	}
	
	@Override
	protected void initialize()
	{
		super.initialize();
		this.setBaseAddress(0);
		this.setBaseFlag(false);
		this.setEndVal(0);
		this.setFileName(null);
		this.setLineCtr(0);
		this.setLines(null);
		this.setLiteralTable(new SeparateChainingSymbolTable<String,SICXE_Literal>());
		this.setLocCtr(0);
		this.setPass1Error(false);
		this.setPass2Error(false);
		this.setPgmLen(0);
		this.setStartVal(0);
	}
	
	public final boolean isBaseFlag()
	{
		return this.isBaseFlag;
	}
	
	public final boolean isPass1Error()
	{
		return this.pass1Error;
	}
	
	public final boolean isPass2Error()
	{
		return this.pass2Error;
	}
	
	protected void makeObjectCode(final SICXE_AssemblerCodeLine acl, final Out out)
	{
		String objectCode = "";
		
		if (acl.getOpCode() != null)
		{
			// TODO: Handle memory-oriented assembler directives.
			if (SICXE_AssemblerProgram.isAssemblerDirective(acl.getOpCode()))
			{
				switch (acl.getOpCode())
				{
					case "BYTE":
						
						//
						break;
					
					case "WORD":
						
						//
						break;
					
					default:
						
						break;
				}
			}
			
			// Handle program instructions.
			if (SICXE_AssemblerProgram.isProgramInstruction(acl.getOpCode()))
			{
				SICXE_OpCode opCodeInfo = SICXE_AssemblerProgram.INSTRUCTION_TABLE.get(acl.getOpCode());
				String opCode = String.format("%02X", opCodeInfo.getOpCode());
				
				switch (opCodeInfo.getFormat())
				{
					case 1: // Simplest case: just set objectCode to the hex string of the operation code.
						
						// FIX, FLOAT, HIO, NORM, SIO, TIO.
						objectCode = opCode;
						break;
					
					case 2: // Next simplest case: as case 1, but also append the operands.
						
						boolean registerError = false;
						boolean shiftQuantityError = false;
						boolean interruptError = false;
						
						// ADDR, CLEAR, COMPR, DIVR, MULR, RMO, SHIFTL, SHIFTR, SUBR, SVC, TIXR.
						if (acl.getOperand() != null)
						{
							int reg1;
							
							if (opCodeInfo.getNumOperands() == 2)
							{
								String[] operands = acl.getOperand().split(",", 2);
								
								if (operands.length > 1)
								{
									reg1 = SICXE_AssemblerProgram.REGISTER_TABLE.get(operands[0]);
									
									if ((reg1 >= 0) && (reg1 <= 9))
									{
										int reg2;
										
										switch (acl.getOpCode())
										{
											// SHIFTL, SHIFTR.
											case "SHIFTL":
											case "SHIFTR":
												
												// Here reg2 = n+1 bits to be shifted.
												reg2 = SICXE_AssemblerProgram.resolveOperand(operands[1], this);
												
												if ((reg2 >= 1) && (reg2 <= 16))
												{
													objectCode = opCode + reg1 + String.format("%-1X", (reg2 - 1));
												}
												else
												{
													shiftQuantityError = true;
												}
												break;
											
											// ADDR, COMPR, DIVR, MULR, RMO, SUBR.
											default:
												
												reg2 = SICXE_AssemblerProgram.REGISTER_TABLE.get(operands[1]);
												
												if ((reg2 >= 0) && (reg2 <= 9))
												{
													objectCode = opCode + reg1 + reg2;
												}
												else
												{
													registerError = true;
												}
												break;
										}
									}
									else
									{
										registerError = true;
									}
								}
								else
								{
									registerError = true;
								}
							}
							// CLEAR, SVC, TIXR.
							else
							{
								// SVC.
								if (acl.getOpCode().equals("SVC"))
								{
									// Here reg2 = interrupt code.
									reg1 = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this);
									
									if ((reg1 >= 0) && (reg1 <= 15))
									{
										objectCode = opCode + String.format("%-1X", reg1) + "0";
									}
									else
									{
										interruptError = true;
									}
								}
								// CLEAR, TIXR.
								else
								{
									reg1 = SICXE_AssemblerProgram.REGISTER_TABLE.get(acl.getOperand());
									
									if ((reg1 >= 0) && (reg1 <= 9))
									{
										objectCode = opCode + reg1 + "0";
									}
									else
									{
										registerError = true;
									}
								}
							}
						}
						else
						{
							registerError = true;
						}
						
						if (registerError)
						{
							String errorString = "ERROR: Invalid register or no register found in operand on line " +
								acl.getLineNum() + ".";
							out.println(errorString);
							this.setPass2Error(true);
						}
						
						if (shiftQuantityError)
						{
							String errorString = "ERROR: Invalid shift quantity entered on line " +
								acl.getLineNum() + ".";
							out.println(errorString);
							this.setPass2Error(true);
						}
						
						if (interruptError)
						{
							String errorString = "ERROR: Invalid interrupt code entered on line " +
								acl.getLineNum() + ".";
							out.println(errorString);
							this.setPass2Error(true);
						}
						break;
					
					default: // Most complex case: formats 3 & 4. Must figure out bit flags and displacement.
							 // We will do SIC simple and SIC/XE extended first bescause they are the simplest.
						
						/*
						 * Bit Flags: nixbpe
						 * BaseRelative	(n=1, i=1, b=1, p=0)
						 * Direct		(n=1, i=1, b=0, p=0)
						 * Extended		(b=0, p=0, e=1)
						 * Immediate	(n=0, i=1, x=0)
						 * Indexed		(both n & i = 0 or 1, x=1)
						 * Indirect		(n=1, i=0, x=0)
						 * PCRelative	(n=1, i=1, b=0, p=1)
						 * Simple		(n=0, i=0, b=0, p=0, e=0)
						 */
						
						boolean extended = false;
						boolean immediate = false;
						boolean indexed = false;
						boolean indirect = false;
						boolean simple = false;
						
						switch (acl.getOpCode().charAt(0))
						{
							case '+': // SIC/XE Extended Direct (Format 4)
								
								extended = true;
								break;
							
							case '*': // SIC Simple Direct (Format 3), all flags = 0 except possibly x (indexed).
								
								simple = true;
								break;
							
							default: // SIC/XE Normal (Format 3)
								
								break;
						}
						
						if (acl.getOperand() != null)
						{
							String bitFlags = "", operand = acl.getOperand();
							int targetAddress = 0, origin = opCodeInfo.getFormat() + acl.getAddress();
							
							switch (acl.getOperand().charAt(0))
							{
								case '#':
									
									immediate = true;
									opCode = String.format("%02X", (opCodeInfo.getOpCode() + 1));
									break;
								
								case '@':
									
									indirect = true;
									opCode = String.format("%02X", (opCodeInfo.getOpCode() + 2));
									break;
								
								default:
									
									if (!simple)
									{
										opCode = String.format("%02X", (opCodeInfo.getOpCode() + 3));
									}
									
									// Indexed addressing cannot be used with immediate or indirect addressing.
									if (acl.getOperand().contains(","))
									{
										indexed = true;
										operand = operand.substring(0, operand.indexOf(","));
									}
									break;
							}
							
							if (immediate || indirect)
							{
								operand = operand.substring(1, operand.length());
							}
							
							targetAddress = SICXE_AssemblerProgram.resolveOperand(operand, this);
							
							if (simple)	// Next simplest case: as case 1, but determine if indexing is in use
										// and then append the target address.
							{
								if (indexed)
								{
									bitFlags = "8";
								}
								else
								{
									bitFlags = "0";
								}
								
								objectCode = opCode + bitFlags + String.format("%03X", targetAddress);
							}
							else
							{
								if (extended)	// Next simplest case: as case 1, but determine if indexing is in use
												// and then append the target address.
								{
									if (indexed)
									{
										bitFlags = "9";
									}
									else
									{
										bitFlags = "1";
									}
									
									objectCode = opCode + bitFlags + String.format("%05X", targetAddress);
								}
								else
								{
									objectCode = this.makeObjectCode_Hard(opCode, origin, targetAddress, indexed);
								}
							}
						}
						// Handle RSUB variants.
						else
						{
							if (acl.getOpCode().contains("RSUB"))
							{
								// *RSUB
								if (simple)
								{
									objectCode = opCode + "0000";
								}
								// RSUB, +RSUB
								else
								{
									opCode = String.format("%02X", (opCodeInfo.getOpCode() + 3));
									
									// +RSUB
									if (extended)
									{
										objectCode = opCode + "100000";
									}
									// RSUB
									else
									{
										objectCode = opCode + "0000";
									}
								}
							}
							else
							{
								String errorString = "ERROR: Missing operand or operand not found in symbol table on line " +
									acl.getLineNum() + ".";
								out.println(errorString);
								this.setPass2Error(true);
							}
						}
						break;
				}
			}
		}
		
		acl.setObjectCode(objectCode);
	}
	
	// TODO: Finish object code generation for the hardest cases: SIC/XE format 3.
	protected String makeObjectCode_Hard(final String opCode, final int origin, final int targetAddress,
		final boolean indexed)
	{
		int baseDisp = (targetAddress - this.getBaseAddress());
		int pcDisp = (targetAddress - origin);
		
		/*
		 * Determine the addressing mode based on the displacement.
		 * Order of preference: PCRelative -> BaseRelative -> Direct
		 * 
		 * Direct:			TA = displacement			(0 <= displacement <= 4095)
		 * PCRelative:		TA = (PC) + displacement	(-2048 <= displacement <= 2047)
		 * BaseRelative:	TA = (B) + displacement		(0 <= displacement <= 4095)
		 */
		
		// Bit Flags = 8 (Direct), A (PCRelative), or C (BaseRelative)
		if (indexed)
		{
			if ((pcDisp >= -2048) && (pcDisp <= 2047))
			{
				return (opCode + "A" + String.format("%03X", pcDisp));
			}
			else
			{
				if (this.isBaseFlag())
				{
					if ((baseDisp >= 0) && (baseDisp <= 4095))
					{
						return (opCode + "C" + String.format("%03X", baseDisp));
					}
				}
			}
			
			return (opCode + "8" + String.format("%03X", targetAddress));
		}
		// Bit Flags = 0 (Direct), 2 (PCRelative), or 4 (BaseRelative)
		else
		{
			if ((pcDisp >= -2048) && (pcDisp <= 2047))
			{
				return (opCode + "2" + String.format("%03X", pcDisp));
			}
			else
			{
				if (this.isBaseFlag())
				{
					if ((baseDisp >= 0) && (baseDisp <= 4095))
					{
						return (opCode + "4" + String.format("%03X", baseDisp));
					}
				}
			}
			
			return (opCode + "0" + String.format("%03X", targetAddress));
		}
	}
	
	// TODO: Output generated object code to object file.
	protected void outputObjectFile()
	{
		Out out = new Out(this.getFileName() + SICXE_AssemblerProgram.fileExtObj);
		SICXE_AssemblerCodeLine acl = null;
		
		// Loop through the file line-by-line from the beginning.
		for (int i = 0; i < this.getLineCtr(); i++)
		{
			acl = this.getLines()[i];
			
			if (acl != null)
			{
				if (acl.getOpCode() != null)
				{
					if (acl.getOpCode().equals("START"))
					{
						out.print("");
						continue;
					}
					
					if (acl.getOpCode().equals("END"))
					{
						out.print("");
						break;
					}
					
					out.print("");
				}
			}
		}
	}
	
	/*
	 * TODO: USE Directive and Program Blocks - Pass 1
	 * TODO: CSECT / EXTDEF / EXTREF - Pass 1
	 * TODO: Macro Processor - Pass 1
	 */
	protected void pass1()
	{
		Out out = new Out(this.getFileName() + SICXE_AssemblerProgram.fileExtMid);
		SICXE_AssemblerCodeLine acl = null;
		
		// Loop through the file line-by-line from the beginning.
		for (int i = 0; i < this.getLineCtr(); i++)
		{
			acl = this.getLines()[i];
			
			if (acl != null)
			{
				String lineNumString = String.format("%03d", acl.getLineNum());
				
				if (acl.isFullComment())
				{
					// Don't process full comments. Just print them to the intermediate file.
					out.println(lineNumString + ":" + acl.getInput());
				}
				else
				{
					// Process START assembler directive, if present.
					this.processStartDirective(acl);
					
					// Set the address for this line of assembly code by copying locCtr.
					acl.setAddress(this.getLocCtr());
					
					// Print to the intermediate file.
					out.println(lineNumString + ":" +
						String.format("%04X", this.getLocCtr()) +
						"\t" + acl.getInput());
					
					// Handle label, if present.
					this.handleLabel(acl, out);
					
					// Handle literal, if present.
					this.handleLiteral(acl, true);
					
					// Handle BASE assembler directive, if present.
					this.handleBase(acl);
					
					// Increment locCtr depending on the given opCode.
					this.incrementLocCtr(acl, out);
					
					// Process END assembler directive, if present.
					if (this.processEndDirective(acl))
					{
						break;
					}
				}
			}
		}
		
		// Calculate program length.
		this.setPgmLen(this.getLocCtr() - this.getStartVal());
		
		// Output the symbol and literal tables as necessary.
		// If there are no literals in the program, skip the literal table.
		if (!this.getLiteralTable().isEmpty())
		{
			out.println(SICXE_AssemblerProgram.buildSymbolTableString(this));
			out.print(SICXE_AssemblerProgram.buildLiteralTableString(this));
		}
		else
		{
			out.print(SICXE_AssemblerProgram.buildSymbolTableString(this));
		}
	}
	
	/*
	 * TODO: USE Directive and Program Blocks - Pass 2
	 * TODO: CSECT / EXTDEF / EXTREF - Pass 2
	 * TODO: Macro Processor - Pass 2
	 */
	protected void pass2()
	{
		Out out = new Out(this.getFileName() + SICXE_AssemblerProgram.fileExtLst);
		SICXE_AssemblerCodeLine acl = null;
		
		// Print the listing/report file preamble.
		out.println("*********************************************");
		out.println("University of North Florida: SIC/XE Assembler");
		out.println("Version Date 4/23/2015");
		out.println(Support.getDateTimeStamp());
		out.println("*********************************************");
		out.println("ASSEMBLER REPORT");
		out.println("----------------");
		out.println("\t Loc\tObject Code\tSource Code");
		out.println("\t ---\t-----------\t-----------");
		
		// Loop through the file line-by-line from the beginning.
		for (int i = 0; i < this.getLineCtr(); i++)
		{
			acl = this.getLines()[i];
			
			if (acl != null)
			{
				String lineNumString = String.format("%03d", acl.getLineNum());
				
				if (acl.isFullComment())
				{
					// Don't process full comments. Just print them to the listing/report file.
					out.println(lineNumString + "- " + acl.getInput());
				}
				else
				{
					// Handle literal, if present.
					this.handleLiteral(acl, false);
					
					// Handle BASE assembler directive, if present.
					this.handleBase(acl);
					
					// Generate the object byte code for this line of assembly source code.
					this.makeObjectCode(acl, out);
					
					// Print to the listing/report file.
					out.println(lineNumString + "- " +
						String.format("%05X", acl.getAddress()) +
						"\t" + String.format("%-8S", acl.getObjectCode()) +
						"\t" + acl.getInput());
				}
			}
		}
		
		// If there was an error during pass 2, don't bother generating an object code file.
		if (!this.isPass2Error())
		{
			this.outputObjectFile();
		}
	}
	
	protected boolean processEndDirective(final SICXE_AssemblerCodeLine acl)
	{
		if (acl.getOpCode() != null)
		{
			if (acl.getOpCode().equals("END"))
			{
				if (acl.getOperand() != null)
				{
					Integer address = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this, 16);
					
					if (address != null)
					{
						this.setEndVal(address);
					}
				}
				
				this.addressLiterals();
				return true;
			}
		}
		
		return false;
	}
	
	protected void processStartDirective(final SICXE_AssemblerCodeLine acl)
	{
		if (acl.getOpCode() != null)
		{
			if (acl.getOpCode().equals("START"))
			{
				if (acl.getOperand() != null)
				{
					Integer address = SICXE_AssemblerProgram.resolveOperand(acl.getOperand(), this, 16);
					
					if (address != null)
					{
						this.setStartVal(address);
						this.setLocCtr(address);
						this.setEndVal(address);
					}
				}
			}
		}
	}
	
	@Override
	protected void readFile(final String fileName)
	{
		// Try to read from the given file.
		try
		{
			// Initialize data input.
			// If the given file is invalid or inaccessible, an exception is thrown.
			In inputStream = new In(fileName);
			
			// Establish how many lines of text are in the file.
			this.setLineCtr(Support.countLinesInTextFile(fileName));
			this.setLines(new SICXE_AssemblerCodeLine[this.getLineCtr()]);
			
			// Read in the file's data line by line and store it for future analysis.
			for (int i = 0; ((i < this.getLineCtr()) && (inputStream.hasNextLine())); i++)
			{
				String line = inputStream.readLine();
				
				if (line != null)
				{
					this.getLines()[i] = new SICXE_AssemblerCodeLine(line.toUpperCase(), i + 1, 0);
				}
			}
			
			this.setFileName(fileName);
			this.assembleProgram();
		}
		catch (final Exception exception)
		{
			StdOut.println("Error opening source file.");
			StdOut.println("Unable to read source code from file " + fileName + " - program aborts.");
			System.exit(-1);
		}
	}
	
	protected final void setBaseAddress(final int baseAddress)
	{
		this.baseAddress = baseAddress;
	}
	
	protected final void setBaseFlag(final boolean isBaseFlag)
	{
		this.isBaseFlag = isBaseFlag;
	}
	
	protected final void setEndVal(final int endVal)
	{
		this.endVal = endVal;
	}
	
	protected final void setFileName(final String fileName)
	{
		this.fileName = fileName;
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
	
	protected final void setPass1Error(final boolean pass1Error)
	{
		this.pass1Error = pass1Error;
	}
	
	protected final void setPass2Error(final boolean pass2Error)
	{
		this.pass2Error = pass2Error;
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