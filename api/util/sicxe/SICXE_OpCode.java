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
	private Integer	format		= null;
	private Integer	numOperands	= null;
	private Integer	opCode		= null;
	
	public SICXE_OpCode(final Integer opCode, final Integer format, final Integer numOperands)
	{
		this.setOpCode(opCode);
		this.setFormat(format);
		this.setNumOperands(numOperands);
	}
	
	public final Integer getFormat()
	{
		return this.format;
	}
	
	public final Integer getNumOperands()
	{
		return this.numOperands;
	}
	
	public final Integer getOpCode()
	{
		return this.opCode;
	}
	
	public final void setFormat(final Integer format)
	{
		this.format = format;
	}
	
	public final void setNumOperands(final Integer numOperands)
	{
		this.numOperands = numOperands;
	}
	
	public final void setOpCode(final Integer opCode)
	{
		this.opCode = opCode;
	}
}