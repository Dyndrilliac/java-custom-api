/*
 * Title: HashTable
 * Author: Matthew Boyette
 * Date: 6/19/2013
 * 
 * A specialized hash table data structure designed to allow a comparative analysis between Linear probing and Quadratic probing.
 */

package api.util.datastructures;

import java.lang.reflect.Array;

import api.util.Mathematics;

public class HashTable<T>
{
    private int      currentSize          = 0;
    private T[]      hashArray            = null;
    private double   loadFactor           = 0.0;
    private int      maximumSize          = 0;
    private boolean  probeIsLinear        = false;
    private Class<T> storageType          = null;
    private int      totalProbeLenFailure = 0;
    private int      totalProbeLenSuccess = 0;
    
    @SuppressWarnings({
        "unchecked"
    })
    public HashTable(final Class<T> type, final double loadFactor, final boolean useLinearProbe, final int fillSize)
    {
        this.currentSize = 0;
        this.loadFactor = loadFactor;
        this.probeIsLinear = useLinearProbe;
        this.storageType = type;
        this.setMaximumSize((int)(Math.ceil(fillSize / this.getLoadFactor())));
        this.hashArray = (T[])Array.newInstance(this.storageType, this.getMaximumSize());
    }
    
    public int find(final T data)
    {
        if (this.isEmpty())
        {
            return -1;
        }
        
        int retVal = this.hash(data);
        int quadStep = 0;
        int probeLen = 1;
        
        if (retVal < 0)
        {
            return retVal;
        }
        
        while ((this.wasIndexOccupiedPreviously(retVal)) && (data.equals(this.hashArray[retVal]) == false))
        {
            if (this.isProbeLinear())
            {
                retVal++;
            }
            else
            {
                quadStep++;
                retVal = (int)(retVal + Math.pow(quadStep, 2));
            }
            
            if (retVal >= this.getMaximumSize())
            {
                retVal %= this.getMaximumSize();
            }
            
            probeLen++;
        }
        
        if (this.hashArray[retVal] == null)
        {
            retVal = -1;
            this.totalProbeLenFailure += probeLen;
        }
        else
        {
            this.totalProbeLenSuccess += probeLen;
        }
        
        return retVal;
    }
    
    public int getCurrentSize()
    {
        return this.currentSize;
    }
    
    public double getLoadFactor()
    {
        return this.loadFactor;
    }
    
    public int getMaximumSize()
    {
        return this.maximumSize;
    }
    
    public int getTotalProbeLenFailure()
    {
        return this.totalProbeLenFailure;
    }
    
    public int getTotalProbeLenSuccess()
    {
        return this.totalProbeLenSuccess;
    }
    
    public int hash(final T data)
    {
        int hashVal = 0;
        
        if (data == null)
        {
            hashVal = -1;
        }
        else
        {
            if (data instanceof String)
            {
                String s = (String)data;
                
                for (int i = 0; i < s.length(); i++)
                {
                    // Take the integer value at the current character index, invert its bits (take one's complement), and store the absolute value as
                    // seedVal.
                    int seedVal = Math.abs(~((int)(s.charAt(i))));
                    // The hash is equal to the remainder of the hashTable size divided by the hash multiplied by 256 and added to seedVal.
                    hashVal = ((hashVal * 256) + seedVal) % this.getMaximumSize();
                }
            }
            else if (data instanceof Integer)
            {
                String s = data.toString();
                
                for (int i = 0; i < s.length(); i++)
                {
                    // Take the integer value at the current digit index, invert its bits (take one's complement), and store the absolute value as
                    // seedVal.
                    int seedVal = Math.abs(~(Integer.parseInt(s.substring(i, i + 1))));
                    // The hash is equal to the remainder of the hashTable size divided by the hash multiplied by 10 and added to seedVal.
                    hashVal = ((hashVal * 10) + seedVal) % this.getMaximumSize();
                }
            }
            else
            {
                hashVal = data.hashCode();
            }
        }
        
        return hashVal;
    }
    
    public int insert(final T data)
    {
        if (this.getCurrentSize() >= this.maximumSize)
        {
            return -1;
        }
        
        int step = 0;
        int hashVal = this.hash(data);
        
        if (hashVal < 0)
        {
            return hashVal;
        }
        
        while (this.isIndexOccupied(hashVal))
        {
            if (this.isProbeLinear())
            {
                hashVal++;
            }
            else
            {
                step++;
                hashVal = (int)(hashVal + Math.pow(step, 2));
            }
            
            if (hashVal >= this.getMaximumSize())
            {
                hashVal %= this.getMaximumSize();
            }
        }
        
        this.hashArray[hashVal] = data;
        this.currentSize++;
        return hashVal;
    }
    
    public boolean isEmpty()
    {
        return (this.getCurrentSize() <= 0);
    }
    
    private boolean isIndexOccupied(final int index)
    {
        boolean retVal = true;
        
        if ((this.hashArray[index] == null) || (this.hashArray[index].equals(0)) || (this.hashArray[index].equals("")))
        {
            retVal = false;
        }
        
        return retVal;
    }
    
    public boolean isProbeLinear()
    {
        return this.probeIsLinear;
    }
    
    private void setMaximumSize(final int maximumSize)
    {
        this.maximumSize = (int)Mathematics.makePrimeGreater(maximumSize);
    }
    
    private boolean wasIndexOccupiedPreviously(final int index)
    {
        boolean retVal = true;
        
        if (this.hashArray[index] == null)
        {
            retVal = false;
        }
        
        return retVal;
    }
}