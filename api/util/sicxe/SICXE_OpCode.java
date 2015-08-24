/*
 * Title: SICXE_OpCode
 * Author: Matthew Boyette
 * Date: 3/27/2015
 * 
 * This class stores SIC/XE opcode data.
 */

package api.util.sicxe;

public class SICXE_OpCode
{
	private byte	format		= 0;
	private byte	numOperands	= 0;
	private byte	opCode		= 0;
	
	public SICXE_OpCode(final byte opCode, final byte format, final byte numOperands)
	{
		this.setOpCode(opCode);
		this.setFormat(format);
		this.setNumOperands(numOperands);
	}
	
	public final byte getFormat()
	{
		return this.format;
	}
	
	public final byte getNumOperands()
	{
		return this.numOperands;
	}
	
	public final byte getOpCode()
	{
		return this.opCode;
	}
	
	public final void setFormat(final byte format)
	{
		this.format = format;
	}
	
	public final void setNumOperands(final byte numOperands)
	{
		this.numOperands = numOperands;
	}
	
	public final void setOpCode(final byte opCode)
	{
		this.opCode = opCode;
	}
}