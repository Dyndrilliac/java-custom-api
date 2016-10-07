/*
 * Title: SeparateChainingSymbolTable
 * Author: Matthew Boyette
 * Date: 2/11/2015
 * 
 * A minimalist generic hash table data structure.
 */

package api.util.datastructures;

import java.util.LinkedList;
import api.util.Mathematics;

public class SeparateChainingSymbolTable<K, V>
{
    private static final long PRIMES(final long capacity)
    {
        return Mathematics.makePrimeLesser((long) Math.pow(2, capacity));
    }

    private long                                capacity  = 0;    // Current capacity factor.
    private long                                curSize   = 0;    // Current table size (number of key-value pairs).
    private long                                maxSize   = 0;    // Maximum table size (number of key-value pairs).
    private SequentialSearchSymbolTable<K, V>[] symTables = null; // Internal array of linked-list symbol tables.

    // Create separate chaining hash table with the default capacity factor.
    public SeparateChainingSymbolTable()
    {
        this(0);
    }

    // Create separate chaining hash table with a specific capacity factor.
    @SuppressWarnings("unchecked")
    public SeparateChainingSymbolTable(final long capacity)
    {
        this.setCapacity(capacity);
        this.setMaxSize(SeparateChainingSymbolTable.PRIMES(this.getCapacity()));
        this.setSymTables(new SequentialSearchSymbolTable[(int)this.getMaxSize()]);

        for ( int i = 0; i < this.getMaxSize(); i++ )
        {
            this.getSymTables()[i] = new SequentialSearchSymbolTable<K, V>();
        }
    }

    // Is the key in the symbol table?
    public final boolean contains(final K key)
    {
        return ( this.get(key) != null );
    }

    // Delete key (and associated value) if key is in the table.
    public final void delete(final K key)
    {
        long i = this.hash(key);

        if ( this.getSymTables()[(int)i].contains(key) )
        {
            this.setCurSize(this.getCurSize() - 1);
        }

        this.getSymTables()[(int)i].delete(key);

        // Reduce table size if average length of list <= 2 and the table is larger than the initial capacity.
        if ( ( this.getMaxSize() > SeparateChainingSymbolTable.PRIMES(7) ) && ( this.getCurSize() <= ( 2 * this.getMaxSize() ) ) )
        {
            this.setCapacity(this.getCapacity() - 1);
            this.resize(SeparateChainingSymbolTable.PRIMES(this.getCapacity()));
        }
    }

    // Return value associated with key, null if no such key.
    public final V get(final K key)
    {
        long i = this.hash(key);
        return this.getSymTables()[(int)i].get(key);
    }

    // Return the current capacity factor of the symbol table.
    public final long getCapacity()
    {
        return this.capacity;
    }

    // Return the current size of the symbol table.
    public final long getCurSize()
    {
        return this.curSize;
    }

    // Return the maximum size of the symbol table.
    public final long getMaxSize()
    {
        return this.maxSize;
    }

    // Return the internal symbol table array.
    protected final SequentialSearchSymbolTable<K, V>[] getSymTables()
    {
        return this.symTables;
    }

    // Hash value between 0 and m-1.
    public final long hash(final K key)
    {
        return ( ( key.hashCode() & 0x7FFFFFFF ) % this.getMaxSize() );
    }

    // Is the symbol table empty?
    public final boolean isEmpty()
    {
        return ( this.getCurSize() == 0 );
    }

    // Return keys in symbol table as an Iterable.
    public final Iterable<K> keys()
    {
        LinkedList<K> queue = new LinkedList<K>();

        for ( int i = 0; i < this.getMaxSize(); i++ )
        {
            for ( K key : this.getSymTables()[i].keys() )
            {
                queue.add(key);
            }
        }

        return queue;
    }

    // Insert key-value pair into the table.
    public final void put(final K key, final V value)
    {
        if ( value == null )
        {
            this.delete(key);
            return;
        }

        // Increase table size if average length of list >= 10.
        if ( this.getCurSize() >= ( 10 * this.getMaxSize() ) )
        {
            this.setCapacity(this.getCapacity() + 1);
            this.resize(SeparateChainingSymbolTable.PRIMES(this.getCapacity()));
        }

        long i = this.hash(key);

        if ( !this.getSymTables()[(int)i].contains(key) )
        {
            this.setCurSize(this.getCurSize() + 1);
        }

        this.getSymTables()[(int)i].put(key, value);
    }

    // Resize the hash table to have the given number of chains by rehashing all of the keys.
    protected final void resize(final long chains)
    {
        SeparateChainingSymbolTable<K, V> temp = new SeparateChainingSymbolTable<K, V>(chains);

        for ( int i = 0; i < this.getMaxSize(); i++ )
        {
            for ( K key : this.getSymTables()[i].keys() )
            {
                temp.put(key, this.getSymTables()[i].get(key));
            }
        }

        this.setMaxSize(temp.getMaxSize());
        this.setCurSize(temp.getCurSize());
        this.setSymTables(temp.getSymTables());
    }

    // Set the current capacity factor of the symbol table.
    protected final void setCapacity(final long capacity)
    {
        if ( ( capacity <= 7 ) || ( capacity >= 31 ) )
        {
            if ( capacity <= 7 )
            {
                this.capacity = 7;
            }
            else if ( capacity >= 31 )
            {
                this.capacity = 31;
            }
        }
        else
        {
            this.capacity = capacity;
        }
    }

    // Set the current size of the symbol table.
    protected final void setCurSize(final long curSize)
    {
        this.curSize = curSize;
    }

    // Set the maximum size of the symbol table.
    protected final void setMaxSize(final long maxSize)
    {
        this.maxSize = maxSize;
    }

    // Set the internal symbol table array.
    protected final void setSymTables(final SequentialSearchSymbolTable<K, V>[] symTables)
    {
        this.symTables = symTables;
    }
}
