/*
 * Title: Vertex2D
 * Author: Matthew Boyette
 * Date: 10/25/2013
 *
 * Represents a vertex on a simple 2D graph.
 */

package api.util.mathematics;

import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Vertex2D extends Point2D.Double
{
	private final static long	serialVersionUID	= 1L;

	public Vertex2D(final double x, final double y)
	{
		super(x, y);
	}

	public boolean isWithinLineOfSightOf(final Vertex2D destination, final LinkedList<Edge2D> edgesOfObstacles)
	{
		boolean isWithinLOS = true;

		// Imaginary edge representing the path.
		Edge2D e1 = new Edge2D(this, destination);

		// Test the path against each edge of the obstacles.
		for (int i = 0; i < edgesOfObstacles.size(); i++)
		{
			Edge2D e2 = edgesOfObstacles.get(i);

			// If the path intersects the edge...
			if (e1.intersectsLine(e2))
			{
				isWithinLOS = false;
			}
		}

		return isWithinLOS;
	}
}