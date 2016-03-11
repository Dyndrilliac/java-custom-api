/*
 * Title: Edge2D
 * Author: Matthew Boyette
 * Date: 10/25/2013
 * 
 * Represents an edge on a simple 2D graph. Can be weighted or non-weighted.
 */

package api.util.mathematics;

import java.awt.geom.Line2D;

public class Edge2D extends Line2D.Double
{
    private final static long serialVersionUID = 1L;
    private double            weight           = 0;
    
    public Edge2D(final Vertex2D v1, final Vertex2D v2)
    {
        super(v1, v2);
        this.weight = this.getP1().distance(this.getP2());
    }
    
    public double getWeight()
    {
        return this.weight;
    }
    
    @Override
    public String toString()
    {
        return "(" + this.getP1().toString() + " => " + this.getP2().toString() + ")";
    }
}