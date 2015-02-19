/*
 * Title: AStarShortestPath2D
 * Author: Matthew Boyette
 * Date: 10/25/2013
 *
 * This class uses the A* search algorithm to find the shortest path from the start point to the goal point
 * within a 2-dimensional plane while avoiding polygonal obstacles.
 */

package api.util.games;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.swing.JPanel;

import api.util.Support;
import api.util.mathematics.Edge2D;
import api.util.mathematics.Vertex2D;

public class AStarShortestPath2D
{
	// This sub-class is a ready-made template used to display the results from the AStarShortestPath2D algorithm graphically.
	public final static class MapDisplay extends JPanel
	{
		protected final static long		serialVersionUID	= 1L;
		protected boolean				debugMode			= false;
		protected double				magnification		= 1;
		protected AStarShortestPath2D	shortestPath		= null;

		public MapDisplay(final AStarShortestPath2D shortestPath)
		{
			this(Color.WHITE, Color.BLACK, shortestPath);
		}

		public MapDisplay(final Color background, final Color foreground, final AStarShortestPath2D shortestPath)
		{
			this(Color.WHITE, Color.BLACK, shortestPath, 1.0);
		}

		public MapDisplay(final Color background, final Color foreground, final AStarShortestPath2D shortestPath,
			final double magnification)
		{
			super();
			this.setBackground(background);
			this.setForeground(foreground);
			this.shortestPath = shortestPath;
			this.debugMode = shortestPath.getDebugMode();
			this.magnification = magnification;
			this.repaint();
		}

		@Override
		public void paint(final Graphics g)
		{
			Graphics2D g2D = (Graphics2D)g;
			Ellipse2D v = null;
			Edge2D e = null;

			// Draw map.
			for (int i = 0; i < this.shortestPath.getEdges().size(); i++)
			{
				e = this.shortestPath.getEdges().get(i);

				double origX1 = e.getX1();
				double origX2 = e.getX2();
				double origY1 = e.getY1();
				double origY2 = e.getY2();

				e.setLine(origX1 * this.magnification, origY1 * this.magnification,
					origX2 * this.magnification, origY2 * this.magnification);
				g2D.draw(e);
			}

			// Draw start point.
			v = new Ellipse2D.Double(this.shortestPath.getStartPoint().getX() * this.magnification,
				this.shortestPath.getStartPoint().getY() * this.magnification, 3, 3);

			g2D.draw(v);
			g2D.fill(v);
			g2D.drawString("S", (float)(v.getCenterX() - 4), (float)(v.getCenterY() + 15.0));

			// Draw goal point.
			v = new Ellipse2D.Double(this.shortestPath.getGoalPoint().getX() * this.magnification,
				this.shortestPath.getGoalPoint().getY() * this.magnification, 3, 3);

			g2D.draw(v);
			g2D.fill(v);
			g2D.drawString("G", (float)(v.getCenterX() - 4), (float)(v.getCenterY() + 15.0));

			// Draw all vertices adjacent to the start point or the goal point.
			if (this.debugMode)
			{
				Node withinLOS = null;

				for (int i = 0; i < this.shortestPath.getStartPoint().getNodesWithinLOS().size(); i++)
				{
					withinLOS = this.shortestPath.getStartPoint().getNodesWithinLOS().get(i);
					v = new Ellipse2D.Double(withinLOS.getX() * this.magnification,
						withinLOS.getY() * this.magnification, 3, 3);

					g2D.draw(v);
					g2D.fill(v);
				}

				for (int i = 0; i < this.shortestPath.getGoalPoint().getNodesWithinLOS().size(); i++)
				{
					withinLOS = this.shortestPath.getGoalPoint().getNodesWithinLOS().get(i);
					v = new Ellipse2D.Double(withinLOS.getX() * this.magnification,
						withinLOS.getY() * this.magnification, 3, 3);

					g2D.draw(v);
					g2D.fill(v);
				}
			}

			// Draw solution.
			LinkedList<Node> solution = this.shortestPath.getSolution();

			if (!solution.isEmpty())
			{
				for (int i = 0; i < (solution.size() - 1); i++)
				{
					e = new Edge2D(solution.get(i), solution.get(i + 1));

					double origX1 = e.getX1();
					double origX2 = e.getX2();
					double origY1 = e.getY1();
					double origY2 = e.getY2();

					e.setLine(origX1 * this.magnification, origY1 * this.magnification,
						origX2 * this.magnification, origY2 * this.magnification);
					g2D.draw(e);
				}
			}
		}
	}

	// This sub-class is merely an extension of the Vertex2D class specifically for the A* algorithm.
	public final static class Node extends Vertex2D implements Comparable<Node>
	{
		protected final static long	serialVersionUID	= 1L;
		protected double			cost				= 0;
		protected double			estimatedTotal		= 0;
		protected LinkedList<Node>	nodesWithinLOS		= null;
		protected Node				predecessor			= null;

		public Node(final double x, final double y)
		{
			super(x, y);
			this.nodesWithinLOS = new LinkedList<Node>();
		}

		@Override
		public final int compareTo(final Node n)
		{
			return java.lang.Double.compare(this.getEstimatedTotal(), n.getEstimatedTotal());
		}

		public final double getCost()
		{
			return this.cost;
		}

		public final double getEstimatedTotal()
		{
			return this.estimatedTotal;
		}

		public final LinkedList<Node> getNodesWithinLOS()
		{
			return this.nodesWithinLOS;
		}

		public final Node getPredecessor()
		{
			return this.predecessor;
		}

		public final void setCost(final double cost)
		{
			this.cost = cost;
		}

		public final void setEstimatedTotal(final double estimatedTotal)
		{
			this.estimatedTotal = estimatedTotal;
		}

		public final void setPredecessor(final Node predecessor)
		{
			this.predecessor = predecessor;
		}
	}

	private boolean					debugMode	= false;
	private LinkedList<Edge2D>		edges		= null;					// List of all edges for the polygonal obstacles.
	private Node					goalPoint	= null;					// Goal point.
	private final LinkedList<Node>	solution	= new LinkedList<Node>();	// List of vertices which comprise the solution.
	private Node					startPoint	= null;					// Start point.
	private LinkedList<Node>		vertices	= null;					// List of all vertices/points/nodes of interest (including start and goal point).

	public AStarShortestPath2D(final boolean debugMode, final Node startPoint, final Node goalPoint,
		final LinkedList<Node> vertices, final LinkedList<Edge2D> edges)
	{
		this.startPoint = startPoint;
		this.goalPoint = goalPoint;
		this.vertices = vertices;
		this.edges = edges;
		this.debugMode = debugMode;
		this.determineLineOfSight();
		this.solve();
	}

	public AStarShortestPath2D(final boolean debugMode, final String filePath)
	{
		this.vertices = new LinkedList<Node>();
		this.edges = new LinkedList<Edge2D>();
		this.debugMode = debugMode;
		this.parseMapFile(filePath);
		this.determineLineOfSight();
		this.solve();
	}

	public final LinkedList<Node> actions(final Node n)
	{
		return n.getNodesWithinLOS();
	}

	// This method determines which vertices are within line of sight of each other.
	protected void determineLineOfSight()
	{
		for (int i = 0; i < this.getVertices().size(); i++)
		{
			for (int j = 0; j < this.getVertices().size(); j++)
			{
				if (i != j)
				{
					if (this.getVertices().get(i).isWithinLineOfSightOf(this.getVertices().get(j), this.getEdges()))
					{
						this.getVertices().get(i).getNodesWithinLOS().add(this.getVertices().get(j));
					}
				}
			}
		}
	}

	// Evaluation function. This method takes a node 'n' and calculates the estimated total cost of the path through 'n' to the goal.
	public double f(final Node n)
	{
		return (this.g(n) + this.h(n));
	}

	// This method takes a node 'n' and calculates the cost so far to reach 'n'. Without this, A* search becomes greedy best-first search.
	protected double g(final Node n)
	{
		return n.getCost();
	}

	public boolean getDebugMode()
	{
		return this.debugMode;
	}

	public LinkedList<Edge2D> getEdges()
	{
		return this.edges;
	}

	public Node getGoalPoint()
	{
		return this.goalPoint;
	}

	public LinkedList<Node> getSolution()
	{
		return this.solution;
	}

	public Node getStartPoint()
	{
		return this.startPoint;
	}

	public LinkedList<Node> getVertices()
	{
		return this.vertices;
	}

	// Heuristic function. This method takes a node 'n' and calculates the estimated cost (straight line distance) from 'n' to the goal.
	protected double h(final Node n)
	{
		return n.distance(this.getGoalPoint());
	}

	protected void parseMapFile(final String filePath) throws IllegalArgumentException
	{
		if ((filePath == null) || filePath.isEmpty())
		{
			throw new IllegalArgumentException();
		}

		Scanner inputStream = null;
		String lineOfText = null;
		String[] coordinateStrings = null;
		String[] vertexStrings = null;
		LinkedList<Node> tempVertexList = null;

		try
		{
			inputStream = new Scanner(new File(filePath));

			// The first line is the pair of coordinates for the start point, separated by a comma.
			if (inputStream.hasNextLine())
			{
				lineOfText = inputStream.nextLine().trim();
				coordinateStrings = lineOfText.split(",");
				this.startPoint = new Node(Double.parseDouble(coordinateStrings[0]), Double.parseDouble(coordinateStrings[1]));
			}

			// The second line is the pair of coordinates for the goal point, separated by a comma.
			if (inputStream.hasNextLine())
			{
				lineOfText = inputStream.nextLine().trim();
				coordinateStrings = lineOfText.split(",");
				this.goalPoint = new Node(Double.parseDouble(coordinateStrings[0]), Double.parseDouble(coordinateStrings[1]));
			}

			// Add the start point to the list of vertices.
			this.getVertices().add(this.getStartPoint());

			// Each additional line contains the pairs of coordinates for the vertices in each polygon.
			// Each coordinate pair is separated by a semi-colon. The actual coordinates are separated by commas.
			while (inputStream.hasNextLine())
			{
				tempVertexList = new LinkedList<Node>();
				lineOfText = inputStream.nextLine().trim();
				vertexStrings = lineOfText.split(";");

				for (String vertexString: vertexStrings)
				{
					coordinateStrings = vertexString.split(",");
					Node v = new Node(Double.parseDouble(coordinateStrings[0]), Double.parseDouble(coordinateStrings[1]));
					tempVertexList.add(v);
					this.getVertices().add(v);
				}

				for (int i = 0; i < tempVertexList.size(); i++)
				{
					if (i == (tempVertexList.size() - 1))
					{
						Edge2D e = new Edge2D(tempVertexList.get(i), tempVertexList.get(0));
						this.getEdges().add(e);
					}
					else
					{
						Edge2D e = new Edge2D(tempVertexList.get(i), tempVertexList.get(i + 1));
						this.getEdges().add(e);
					}
				}
			}

			// Add the goal point to the list of vertices.
			this.getVertices().add(this.getGoalPoint());
		}
		catch (final Exception exception)
		{
			Support.displayException(null, exception, false);
		}
		finally
		{
			if (inputStream != null)
			{
				inputStream.close();
				inputStream = null;
			}
		}
	}

	protected void reconstruct_path(Node current)
	{
		if (current == null)
		{
			throw new IllegalArgumentException();
		}

		while (current != null)
		{
			this.getSolution().addFirst(current);
			current = current.getPredecessor();
		}
	}

	// This method is my implementation of the A* algorithm.
	// It returns true if it reaches the desired destination, otherwise it returns false.
	// TODO: http://en.wikipedia.org/wiki/A*_search_algorithm
	protected void solve()
	{
		// Initial variables setup.
		PriorityQueue<Node> openset = new PriorityQueue<Node>();
		LinkedList<Node> closedset = new LinkedList<Node>();
		LinkedList<Node> neighbors = null;
		Node current = null;
		double tempG = 0;
		double tempF = 0;

		// Set the initial cost values for the start point.
		this.getStartPoint().setCost(0);
		this.getStartPoint().setEstimatedTotal(this.f(this.getStartPoint()));

		// Add the start point to the open set.
		openset.add(this.getStartPoint());

		// Keep going until the open set is empty.
		while (!openset.isEmpty())
		{
			// Get the node from the open set with the lowest f() score.
			current = openset.peek();

			// Goal test.
			if ((current.getX() == this.getGoalPoint().getX()) &&
				(current.getY() == this.getGoalPoint().getY()))
			{
				if (this.debugMode)
				{
					System.out.println("Reached goal!");
				}

				this.reconstruct_path(current);
				return;
			}

			// Remove current from the open set.
			current = openset.poll();

			// Add current the closed set.
			closedset.add(current);

			// For each neighbor to the current node...
			neighbors = this.actions(current);
			for (Node neighbor: neighbors)
			{
				tempG = (this.g(current) + current.distance(neighbor));
				tempF = (tempG + this.h(neighbor));

				// Ignore neighbors that are in the closed set.
				if (closedset.contains(neighbor) && (tempF >= this.f(neighbor)))
				{
					continue;
				}

				if (!openset.contains(neighbor) || (tempF < this.f(neighbor)))
				{
					// Set necessary properties for the neighbor.
					neighbor.setPredecessor(current);
					neighbor.setCost(tempG);
					neighbor.setEstimatedTotal(tempF);

					// Add neighbors to the open set if they haven't been evaluated.
					if (!openset.contains(neighbor))
					{
						openset.add(neighbor);
					}
				}
			}
		}
	}
}