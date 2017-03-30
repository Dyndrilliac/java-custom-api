/*
 * Title: CMinusSemantics
 * Author: Matthew Boyette
 * Date: 11/02/2016
 *
 * This class functions as a generic semantical analyzer for the C-Minus language.
 */

package api.util.cminus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import api.util.datastructures.Token;
import edu.princeton.cs.algs4.StdOut;

public class CMinusSemantics
{
    public static class ArrRec extends SymTabRec
    {
        public final int size;

        public ArrRec(final String name, final int scope, final String type, final int size)
        {
            super(name, scope, type);

            if ( type.contentEquals("void") )
            {
                CMinusSemantics.errorFlag = true;
            }

            this.size = size;
        }

        @Override
        public final boolean isArr()
        {
            return true;
        }

        @Override
        public final boolean isFun()
        {
            return false;
        }

        @Override
        public final boolean isVar()
        {
            return false;
        }
    }

    // This class represents an exception that has occurred during an attempted semantic analysis operation.
    protected static class CMinusSemanticException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private Token<CMinusLexer.TokenType> token;

        public CMinusSemanticException()
        {
            super("*ERROR*: Symantic error; cannot semantically analyze empty token list.");
            this.setToken(null);
        }

        public CMinusSemanticException(final Token<CMinusLexer.TokenType> token)
        {
            super("*ERROR*: Symantic error on token \"" + token.getData() + "\", line " + token.getLineNo() + ".");
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

    public static class FunRec extends SymTabRec
    {
        private List<SymTabRec> params;

        public FunRec(final String name, final int scope, final String type, final List<SymTabRec> params)
        {
            super(name, scope, type);
            this.params = params;
        }

        public final int getNumParams()
        {
            return ( ( this.params == null ) ? 0 : this.params.size() );
        }

        public final List<SymTabRec> getParams()
        {
            return this.params;
        }

        @Override
        public final boolean isArr()
        {
            return false;
        }

        @Override
        public final boolean isFun()
        {
            return true;
        }

        @Override
        public final boolean isVar()
        {
            return false;
        }

        public final void setParams(final List<SymTabRec> params)
        {
            this.params = params;
        }
    }

    public static class SymTab<E>
    {
        private HashMap<String, E>       currSymTab;
        private List<HashMap<String, E>> deletedSymTabs;
        private int                      scope;
        private List<HashMap<String, E>> symTabs;

        public SymTab()
        {
            this.symTabs = new ArrayList<HashMap<String, E>>();
            this.deletedSymTabs = new ArrayList<HashMap<String, E>>();
            this.scope = -1;
            this.currSymTab = null;
        }

        public final void decScope()
        {
            this.scope--;
        }

        public final void enterScope()
        {
            this.scope++;
            this.currSymTab = new HashMap<String, E>();
            this.symTabs.add(currSymTab);
        }

        public final void exitScope()
        {
            this.scope--;
            this.deletedSymTabs.add(this.symTabs.remove(this.symTabs.size() - 1));

            if ( this.scope >= 0 )
            {
                this.currSymTab = this.symTabs.get(this.symTabs.size() - 1);
            }
        }

        public final E get(final String s)
        {
            for ( int i = this.symTabs.size() - 1; i >= 0; i-- )
            {
                if ( this.symTabs.get(i).containsKey(s) ) { return this.symTabs.get(i).get(s); }
            }

            CMinusSemantics.errorFlag = true;
            return null;
        }

        public final int getScope()
        {
            return this.scope;
        }

        public final HashMap<String, E> getScopeTable(final int scope)
        {
            return this.symTabs.get(scope);
        }

        public final void incScope()
        {
            this.scope++;
        }

        public final void insert(final String s, final E e)
        {
            this.currSymTab.put(s, e);
        }

        public final boolean lookup(final String s)
        {
            return this.currSymTab.containsKey(s);
        }
    }

    public static abstract class SymTabRec
    {
        public final String name;
        public final int    scope;
        public final String type;

        public SymTabRec(final String name, final int scope, final String type)
        {
            this.name = name;
            this.scope = scope;
            this.type = type;
        }

        public abstract boolean isArr();

        public abstract boolean isFun();

        public final boolean isGlobal()
        {
            return scope == 0;
        }

        public final boolean isLocal()
        {
            return scope > 0;
        }

        public abstract boolean isVar();
    }

    public static class VarRec extends SymTabRec
    {
        public VarRec(final String name, final int scope, final String type)
        {
            super(name, scope, type);

            if ( type.contentEquals("void") )
            {
                CMinusSemantics.errorFlag = true;
            }
        }

        @Override
        public boolean isArr()
        {
            return false;
        }

        @Override
        public boolean isFun()
        {
            return false;
        }

        @Override
        public boolean isVar()
        {
            return true;
        }
    }

    public static boolean errorFlag = false;
    public static boolean seenMain  = false;

    public static final void addSymbol(final SymTabRec symbolRecord, final SymTab<SymTabRec> symbolTables)
    {
        if ( symbolTables.lookup(symbolRecord.name) )
        {
            CMinusSemantics.errorFlag = true;
            return;
        }

        symbolTables.insert(symbolRecord.name, symbolRecord);
    }

    private String                             result       = "ACCEPT";
    private SymTab<SymTabRec>                  symbolTables = null;
    private List<Token<CMinusLexer.TokenType>> tokens       = null;

    public CMinusSemantics(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        super();
        this.semantics(tokens, symbolTables, silent);
    }

    public final String getResult()
    {
        return this.result;
    }

    public final SymTab<SymTabRec> getSymbolTables()
    {
        return this.symbolTables;
    }

    public final List<Token<CMinusLexer.TokenType>> getTokens()
    {
        return this.tokens;
    }

    public final boolean semantics(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);

            try
            {
                if ( ( tokens.isEmpty() ) ) { throw new CMinusSemanticException(); }
                if ( CMinusSemantics.errorFlag )
                {
                    this.setResult("REJECT");
                    return false;
                }

                return true;
            }
            catch ( final CMinusSemanticException cmse )
            {
                if ( !silent )
                {
                    StdOut.println(cmse.getMessage());
                }
            }
        }

        this.setResult("REJECT");
        return false;
    }

    public final void setResult(final String result)
    {
        this.result = result;
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
