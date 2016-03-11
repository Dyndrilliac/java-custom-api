/*
 * Title: SeparateChainingSymbolTable
 * Author: Matthew Boyette
 * Date: 2/11/2015
 * 
 * A minimalist generic hash table data structure.
 */

package api.util.datastructures;

import java.util.LinkedList;

public class SeparateChainingSymbolTable<K,V>
{
    // Largest primes <= 2^i for i = 7 to 31.
    private static final int[]                 PRIMES    = {
        127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071, 262139, 524287, 1048573, 2097143, 4194301, 8388593, 16777213, 33554393,
        67108859, 134217689, 268435399, 536870909, 1073741789, 2147483647
                                                         };
    
    private int                                capacity  = 0;   // Current capacity factor.
    private int                                curSize   = 0;   // Current table size (number of key-value pairs).
    private int                                maxSize   = 0;   // Maximum table size (number of key-value pairs).
    private SequentialSearchSymbolTable<K,V>[] symTables = null; // Internal array of linked-list symbol tables.
                                                                 
    // Create separate chaining hash table with the default capacity factor.
    public SeparateChainingSymbolTable()
    {
        this(0);
    }
    
    // Create separate chaining hash table with a specific capacity factor.
    @SuppressWarnings("unchecked")
    public SeparateChainingSymbolTable(final int capacity)
    {
        this.setCapacity(capacity);
        this.setMaxSize(SeparateChainingSymbolTable.PRIMES[this.getCapacity()]);
        this.setSymTables(new SequentialSearchSymbolTable[this.getMaxSize()]);
        
        for (int i = 0; i < this.getMaxSize(); i++)
        {
            this.getSymTables()[i] = new SequentialSearchSymbolTable<K,V>();
        }
    }
    
    // Is the key in the symbol table?
    public final boolean contains(final K key)
    {
        return (this.get(key) != null);
    }
    
    // Delete key (and associated value) if key is in the table.
    public final void delete(final K key)
    {
        int i = this.hash(key);
        
        if (this.getSymTables()[i].contains(key))
        {
            this.setCurSize(this.getCurSize() - 1);
        }
        
        this.getSymTables()[i].delete(key);
        
        // Reduce table size if average length of list <= 2 and the table is larger than the initial capacity.
        if ((this.getMaxSize() > SeparateChainingSymbolTable.PRIMES[0]) && (this.getCurSize() <= (2 * this.getMaxSize())))
        {
            this.setCapacity(this.getCapacity() - 1);
            this.resize(SeparateChainingSymbolTable.PRIMES[this.getCapacity()]);
        }
    }
    
    // Return value associated with key, null if no such key.
    public final V get(final K key)
    {
        int i = this.hash(key);
        return this.getSymTables()[i].get(key);
    }
    
    // Return the current capacity factor of the symbol table.
    public final int getCapacity()
    {
        return this.capacity;
    }
    
    // Return the current size of the symbol table.
    public final int getCurSize()
    {
        return this.curSize;
    }
    
    // Return the maximum size of the symbol table.
    public final int getMaxSize()
    {
        return this.maxSize;
    }
    
    // Return the internal symbol table array.
    protected final SequentialSearchSymbolTable<K,V>[] getSymTables()
    {
        return this.symTables;
    }
    
    // Hash value between 0 and m-1.
    public final int hash(final K key)
    {
        return ((key.hashCode() & 0x7FFFFFFF) % this.getMaxSize());
    }
    
    // Is the symbol table empty?
    public final boolean isEmpty()
    {
        return (this.getCurSize() == 0);
    }
    
    // Return keys in symbol table as an Iterable.
    public final Iterable<K> keys()
    {
        LinkedList<K> queue = new LinkedList<K>();
        
        for (int i = 0; i < this.getMaxSize(); i++)
        {
            for (K key: this.getSymTables()[i].keys())
            {
                queue.add(key);
            }
        }
        
        return queue;
    }
    
    // Insert key-value pair into the table.
    public final void put(final K key, final V value)
    {
        if (value == null)
        {
            this.delete(key);
            return;
        }
        
        // Increase table size if average length of list >= 10.
        if (this.getCurSize() >= (10 * this.getMaxSize()))
        {
            this.setCapacity(this.getCapacity() + 1);
            this.resize(SeparateChainingSymbolTable.PRIMES[this.getCapacity()]);
        }
        
        int i = this.hash(key);
        
        if (!this.getSymTables()[i].contains(key))
        {
            this.setCurSize(this.getCurSize() + 1);
        }
        
        this.getSymTables()[i].put(key, value);
    }
    
    // Resize the hash table to have the given number of chains by rehashing all of the keys.
    protected final void resize(final int chains)
    {
        SeparateChainingSymbolTable<K,V> temp = new SeparateChainingSymbolTable<K,V>(chains);
        
        for (int i = 0; i < this.getMaxSize(); i++)
        {
            for (K key: this.getSymTables()[i].keys())
            {
                temp.put(key, this.getSymTables()[i].get(key));
            }
        }
        
        this.setMaxSize(temp.getMaxSize());
        this.setCurSize(temp.getCurSize());
        this.setSymTables(temp.getSymTables());
    }
    
    // Set the current capacity factor of the symbol table.
    protected final void setCapacity(final int capacity)
    {
        if ((capacity < 0) || (capacity >= SeparateChainingSymbolTable.PRIMES.length))
        {
            if (capacity < 0)
            {
                this.capacity = 0;
            }
            else if (capacity >= SeparateChainingSymbolTable.PRIMES.length)
            {
                this.capacity = (SeparateChainingSymbolTable.PRIMES.length - 1);
            }
        }
        else
        {
            this.capacity = capacity;
        }
    }
    
    // Set the current size of the symbol table.
    protected final void setCurSize(final int curSize)
    {
        this.curSize = curSize;
    }
    
    // Set the maximum size of the symbol table.
    protected final void setMaxSize(final int maxSize)
    {
        this.maxSize = maxSize;
    }
    
    // Set the internal symbol table array.
    protected final void setSymTables(final SequentialSearchSymbolTable<K,V>[] symTables)
    {
        this.symTables = symTables;
    }
}