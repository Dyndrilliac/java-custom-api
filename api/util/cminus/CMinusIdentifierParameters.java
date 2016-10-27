/*
 * Title: CMinusIdentifierParameters
 * Author: Matthew Boyette
 * Date: 10/24/2016
 * 
 * This class is a specialized data structure needed to help the parser build the symbol table(s) required for semantic analysis and code generation. 
 */

package api.util.cminus;

public class CMinusIdentifierParameters
{
    public static enum PARAM_TYPE
    {
        FUNCTION_DECLARATION, FUNCTION_PARAMETER, VARIABLE_DECLARATION
    }

    public static enum VALUE_TYPE
    {
        FLOAT, INTEGER, VOID
    }

    public CMinusIdentifierParameters()
    {
        // TODO: Finish code for identifier parameters.
    }
}
