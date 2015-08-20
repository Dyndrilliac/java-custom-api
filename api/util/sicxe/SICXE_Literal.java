/*
 * Title: SICXE_Literal
 * Author: Matthew Boyette
 * Date: 3/27/2015
 * 
 * This class represents a SIC/XE literal.
 */

package api.util.sicxe;

import api.util.Support;

public class SICXE_Literal
{
	public static enum LiteralType
	{
		CHAR,	// =C'TEST STRING' | C'TEST STRING'
		HEX,	// =X'1F3A' | X'1F3A'
		INVALID;
	}
	
	private int			address			= -1;
	private String		input			= null;
	private boolean		isTrueLiteral	= false;
	private int			length			= -1;
	private LiteralType	type			= null;
	private String		value			= null;
	
	public SICXE_Literal(final String s)
	{
		this.setInput(s);
		
		if (s != null)
		{
			String literal = null;
			
			if (s.charAt(0) == '=')
			{
				literal = s.substring(1);
				this.setTrueLiteral(true);
			}
			else
			{
				literal = s;
				this.setTrueLiteral(false);
			}
			
			this.setValue(literal.substring(2, literal.length() - 1));
			
			switch (literal.charAt(0))
			{
				case 'C':
					
					this.setLength(this.getValue().length());
					this.setType(LiteralType.CHAR);
					break;
				
				case 'X':
					
					if (Support.isStringParsedAsInteger(this.getValue()))
					{
						this.setValue(Integer.toHexString(Integer.parseInt(this.getValue())));
					}
					
					this.setLength((int)Math.ceil(this.getValue().length() / 2.0));
					this.setType(LiteralType.HEX);
					break;
				
				default:
					
					this.setValue(null);
					this.setLength(-1);
					this.setType(LiteralType.INVALID);
					break;
			}
		}
	}
	
	public final int getAddress()
	{
		return this.address;
	}
	
	public final String getExtraPaddedHexValue()
	{
		return Support.padEvenly(this.getPaddedHexValue(), '0', 6);
	}
	
	public final String getHexValue()
	{
		String result = null;
		
		if (this.getType() == LiteralType.HEX)
		{
			result = this.getValue();
		}
		
		if (this.getType() == LiteralType.CHAR)
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < this.getLength(); i++)
			{
				sb.append(String.format("%02X", (int)this.getValue().charAt(i)));
			}
			
			result = sb.toString();
		}
		
		return result;
	}
	
	public final String getInput()
	{
		return this.input;
	}
	
	public final int getLength()
	{
		return this.length;
	}
	
	public final String getPaddedHexValue()
	{
		return Support.padEvenly(this.getHexValue(), '0');
	}
	
	public final LiteralType getType()
	{
		return this.type;
	}
	
	public final String getValue()
	{
		return this.value;
	}
	
	public final boolean isTrueLiteral()
	{
		return this.isTrueLiteral;
	}
	
	public final void setAddress(final int address)
	{
		this.address = address;
	}
	
	protected final void setInput(final String input)
	{
		this.input = input;
	}
	
	protected final void setLength(final int length)
	{
		this.length = length;
	}
	
	protected final void setTrueLiteral(final boolean isTrueLiteral)
	{
		this.isTrueLiteral = isTrueLiteral;
	}
	
	protected final void setType(final LiteralType type)
	{
		this.type = type;
	}
	
	protected final void setValue(final String value)
	{
		this.value = value;
	}
}