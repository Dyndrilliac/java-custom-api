/*
 * Title: CMinusSemantics
 * Author: Matthew Boyette
 * Date: 11/02/2016 - 04/14/2017
 *
 * This class functions as a generic semantical analyzer for the C-Minus language.
 */

package api.util.cminus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

    public static final void checkArrayVariableHasIndex(final CMinusSemantics.SymTabRec record)
    {
        if ( record.isArr() )
        {
            CMinusSemantics.errorFlag = true;
        }
    }

    public static final int checkDeclarationArrayIndexType(final Token<CMinusLexer.TokenType> token, final int defaultValue)
    {
        int retVal = defaultValue;

        if ( token.getType() != CMinusLexer.TokenType.INTEGER )
        {
            CMinusSemantics.errorFlag = true;
        }
        else
        {
            retVal = Integer.parseInt(token.getData());
        }

        return retVal;
    }

    public static final void checkFunctionParamArgumentNumberAgreement(final CMinusSemantics.FunRec functionRecord, final int argTokens, final int argCount)
    {
        if ( argTokens <= 0 )
        {
            if ( functionRecord.getNumParams() != 0 )
            {
                CMinusSemantics.errorFlag = true;
            }
        }
        else if ( argTokens > 0 )
        {
            if ( functionRecord.getNumParams() != argCount )
            {
                CMinusSemantics.errorFlag = true;
            }
        }
    }

    public static final void checkFunctionParamArgumentTypeAgreement(final CMinusSemantics.FunRec functionRecord, final List<Token<CMinusLexer.TokenType>> args)
    {
        // TODO: Check to see if the type of each parameter agrees with the type of each argument.
    }

    public static final void checkFunctionReturns(final CMinusSemantics.FunRec functionRecord, final List<Token<CMinusLexer.TokenType>> tokens, final CMinusParser.CMinusParseResult cmpr)
    {
        List<Integer> indexList = new LinkedList<Integer>();

        for ( int i = 0; i < tokens.size(); i++ )
        {
            if ( tokens.get(i).getData().contentEquals("return") )
            {
                indexList.add(i);
            }
        }

        if ( functionRecord.type.contentEquals("void") )
        {
            if ( !indexList.isEmpty() )
            {
                for ( Integer index : indexList )
                {
                    if ( !tokens.get(index + 1).getData().contentEquals(";") )
                    {
                        CMinusSemantics.errorFlag = true;
                    }
                }
            }
        }
        else
        {
            if ( indexList.isEmpty() )
            {
                CMinusSemantics.errorFlag = true;
            }

            if ( !CMinusSemantics.errorFlag )
            {
                CMinusSemantics.checkTypeAgreement(cmpr.returnType, CMinusParser.CMinusParseResult.convertTypeSpecifierStringToReturnTypeEnum(functionRecord.type));
            }
        }
    }

    public static final void checkMain(FunRec functionRecord)
    {
        if ( functionRecord.name.contentEquals("main") )
        {
            if ( CMinusSemantics.seenMain == true )
            {
                CMinusSemantics.errorFlag = true;
            }
            else
            {
                if ( !functionRecord.isGlobal() )
                {
                    CMinusSemantics.errorFlag = true;
                }

                if ( !functionRecord.getParams().isEmpty() )
                {
                    CMinusSemantics.errorFlag = true;
                }

                if ( !functionRecord.type.contentEquals("void") )
                {
                    CMinusSemantics.errorFlag = true;
                }

                CMinusSemantics.seenMain = true;
            }
        }
    }

    public static final boolean checkTypeAgreement(final CMinusParser.CMinusParseResult.ReturnType returnTypeA, final CMinusParser.CMinusParseResult.ReturnType returnTypeB)
    {
        if ( returnTypeA != returnTypeB )
        {
            CMinusSemantics.errorFlag = true;
            return false;
        }
        else
            return true;
    }

    public static final void checkVariableArrayIndexType(final CMinusParser.CMinusParseResult cmpr)
    {
        if ( cmpr.returnType != CMinusParser.CMinusParseResult.ReturnType.INT )
        {
            CMinusSemantics.errorFlag = true;
        }
    }

    public static final int getArgumentCount(final List<Token<CMinusLexer.TokenType>> tokens, final int depth)
    {
        int commaCount = 0;

        for ( Token<CMinusLexer.TokenType> item : tokens )
        {
            if ( ( item.getParenthDepth() - depth ) == 0 )
            {
                if ( item.getData().contentEquals(",") )
                {
                    commaCount++;
                }
            }
        }

        return ( commaCount + 1 );
    }

    public static final void reinitialize()
    {
        CMinusSemantics.errorFlag = false;
        CMinusSemantics.seenMain = false;
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

    protected final boolean semantics(final List<Token<CMinusLexer.TokenType>> tokens, final SymTab<SymTabRec> symbolTables, final boolean silent)
    {
        if ( ( ( tokens != null ) && ( symbolTables != null ) ) )
        {
            this.setTokens(tokens);
            this.setSymbolTables(symbolTables);

            try
            {
                if ( ( tokens.isEmpty() ) ) { throw new RuntimeException("*ERROR*: Semantic error; cannot analyze empty token list."); }

                if ( !CMinusSemantics.seenMain )
                {
                    CMinusSemantics.errorFlag = true;
                }

                if ( CMinusSemantics.errorFlag ) { return this.setResult("REJECT"); }

                return this.setResult("ACCEPT");
            }
            catch ( final RuntimeException re )
            {
                if ( !silent )
                {
                    StdOut.println(re.getMessage());
                }
            }
        }

        return this.setResult("REJECT");
    }

    protected final boolean setResult(final String result)
    {
        this.result = result;
        CMinusSemantics.reinitialize();

        if ( result.contentEquals("ACCEPT") )
        {
            return true;
        }
        else
        {
            return false;
        }
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
