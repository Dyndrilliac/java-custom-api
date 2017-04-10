/*
 * Title: CMinusCodeGeneration
 * Author: Matthew Boyette
 * Date: 04/10/2017
 *
 * This class functions as a generic semantical analyzer for the C-Minus language.
 */

package api.util.cminus;

import java.util.List;
import api.util.cminus.CMinusSemantics.SymTab;
import api.util.cminus.CMinusSemantics.SymTabRec;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusCodeGeneration
{
    public static int          statementCounter   = 0;
    public static StringBuffer staticStringBuffer = new StringBuffer();
    public static int          variableCounter    = 0;

    protected static final void reinitialize()
    {
        CMinusCodeGeneration.staticStringBuffer = new StringBuffer();
    }

    private StringBuffer                       result       = null;
    private SymTab<SymTabRec>                  symbolTables = null;
    private List<Token<CMinusLexer.TokenType>> tokens       = null;

    public CMinusCodeGeneration(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        super();
        this.codeGen(tokens, symbolTables, silent);
    }

    protected final boolean codeGen(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);

            try
            {
                if ( ( tokens.isEmpty() ) ) { throw new RuntimeException("*ERROR*: Generation error; cannot generate code from empty token list."); }

                return this.setResult(true);
            }
            catch ( final RuntimeException re )
            {
                if ( !silent )
                {
                    StdOut.println(re.getMessage());
                }
            }
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
        this.result = CMinusCodeGeneration.staticStringBuffer;
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
