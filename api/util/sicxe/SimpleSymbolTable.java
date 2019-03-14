/*
 * Title: SimpleSymbolTable
 * Author: Matthew Boyette
 * Date: 2/11/2015
 * 
 * The purpose of this project is to implement an efficient hash-based symbol table for the first and second passes of the SIC/XE assembler.
 * It makes use of prime numbers, double hashing, hash chaining, and automatic size adjustment to avoid collisions.
 */

package api.util.sicxe;

import api.util.datastructures.SeparateChainingSymbolTable;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

public class SimpleSymbolTable
{
    private SeparateChainingSymbolTable<String, Integer> symbolTable = null;

    public SimpleSymbolTable(final String fileName)
    {
        super();
        this.initialize();

        if ( ( fileName != null ) && ( !fileName.trim().isEmpty() ) )
        {
            this.readFile(fileName.trim());
        }
    }

    public final SeparateChainingSymbolTable<String, Integer> getSymbolTable()
    {
        return this.symbolTable;
    }

    protected void initialize()
    {
        this.setSymbolTable(new SeparateChainingSymbolTable<String, Integer>(2));
    }

    protected void readFile(final String fileName)
    {
        In inputStream = null; // Stream object for file input.

        // Try to read the file.
        try
        {
            // Initialize file stream. If the given path is invalid, an exception is thrown.
            inputStream = new In(fileName);

            while ( inputStream.hasNextLine() )
            {
                // Get the next line in the file.
                String line = inputStream.readLine();

                if ( line != null )
                {
                    line = line.trim();
                }

                if ( ( line != null ) && ( line.isEmpty() == false ) )
                {
                    if ( line.matches("[a-zA-Z]+ [0-9]+") )
                    {
                        String s[] = line.split(" ");

                        if ( this.getSymbolTable().contains(s[0]) )
                        {
                            StdOut.println("ERROR:\t" + s[0] + " already exists at location " + this.getSymbolTable().hash(s[0]) + ".");
                        }
                        else
                        {
                            this.getSymbolTable().put(s[0], Integer.parseInt(s[1]));
                            StdOut.println("STORED:\t" + s[0] + " " + s[1] + " at location " + this.getSymbolTable().hash(s[0]) + ".");
                        }
                    }
                    else
                    {
                        if ( this.getSymbolTable().contains(line) == false )
                        {
                            StdOut.println("ERROR:\t" + line + " not found.");
                        }
                        else
                        {
                            StdOut.println("FOUND:\t" + line + " at location " + this.getSymbolTable().hash(line) + " with value " + this.getSymbolTable().get(line) + ".");
                        }
                    }
                }
            }
        }
        catch ( final IllegalArgumentException iae )
        {
            iae.printStackTrace();
        }
        catch ( final NullPointerException npe )
        {
            npe.printStackTrace();
        }
        finally
        {
            if ( inputStream != null )
            {
                // Close the input stream.
                inputStream.close();
                inputStream = null;
            }
        }
    }

    protected final void setSymbolTable(final SeparateChainingSymbolTable<String, Integer> symbolTable)
    {
        this.symbolTable = symbolTable;
    }
}
