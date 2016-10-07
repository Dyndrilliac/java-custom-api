/*
 * Title: Token
 * Author: Matthew Boyette
 * Date: 9/25/2016
 * 
 * This helper class represents a token in a string.
 */

package api.util.datastructures;

public class Token<T>
{
    private int    braceDepth;
    private int    bracketDepth;
    private String data;
    private int    lineNo;
    private int    parenthDepth;
    private T      type;

    public Token(final T type, final String data)
    {
        this(type, data, 0);
    }

    public Token(final T type, final String data, final int lineNo)
    {
        this(type, data, lineNo, 0, 0, 0);
    }

    public Token(final T type, final String data, final int lineNo, final int braceDepth, final int bracketDepth, final int parenthDepth)
    {
        super();
        this.setType(type);
        this.setData(data);
        this.setLineNo(lineNo);
        this.setBraceDepth(braceDepth);
        this.setBracketDepth(bracketDepth);
        this.setParenthDepth(parenthDepth);
    }

    public final int getBraceDepth()
    {
        return braceDepth;
    }

    public final int getBracketDepth()
    {
        return bracketDepth;
    }

    public final String getData()
    {
        return this.data;
    }

    public final int getLineNo()
    {
        return this.lineNo;
    }

    public final int getParenthDepth()
    {
        return parenthDepth;
    }

    public final T getType()
    {
        return this.type;
    }

    protected final void setBraceDepth(final int braceDepth)
    {
        this.braceDepth = braceDepth;
    }

    protected final void setBracketDepth(final int bracketDepth)
    {
        this.bracketDepth = bracketDepth;
    }

    protected final void setData(final String data)
    {
        this.data = data;
    }

    protected final void setLineNo(final int lineNo)
    {
        this.lineNo = lineNo;
    }

    protected final void setParenthDepth(final int parenthDepth)
    {
        this.parenthDepth = parenthDepth;
    }

    protected final void setType(final T type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s", this.getType(), this.getData());
    }
}
