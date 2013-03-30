/*
	Title:  Graph
	Author: Matthew Boyette
	Date:   3/28/2013
	
	This class serves as the basis for all 2D graphs of lines, curves/arcs, and polygons.
*/
package api.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public abstract class Graph extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected Point2D origin          = null;
	private   Color   foregroundAxes  = Color.BLACK;
	private   Color   foregroundGraph = Color.BLUE;
	
	public Graph()
	{
		this.calculateOrigin();
	}
	
	public Graph(final Color foregroundAxes, final Color foregroundGraph)
	{
		this.setColors(foregroundAxes, foregroundGraph);
		this.calculateOrigin();
	}
	
	protected void calculateOrigin()
	{
		int xCenter = (this.getWidth() / 2);
		int yCenter = (this.getHeight() / 2);
		this.origin = new Point2D.Double(xCenter, yCenter);
	}
	
	public Color getForegroundAxes()
	{
		return this.foregroundAxes;
	}

	public Color getForegroundGraph()
	{
		return this.foregroundGraph;
	}

	public Point2D getOrigin()
	{
		return this.origin;
	}
	
	public Dimension getPreferredSize()
	{
		return (new Dimension(250, 250));
	}
	
	protected void drawPlane(final Graphics2D g2D, final int radius)
	{
		int xCenter = (int)this.getOrigin().getX();
		int yCenter = (int)this.getOrigin().getY();
		
		// Color
		g2D.setColor(this.getForegroundAxes());
		
		// Y-Axis
		g2D.drawLine((xCenter - radius), yCenter, (xCenter + radius), yCenter);
		g2D.drawLine((xCenter - radius), yCenter, ((xCenter - radius) + 5), (yCenter + 5));
		g2D.drawLine((xCenter - radius), yCenter, ((xCenter - radius) + 5), (yCenter - 5));
		g2D.drawLine((xCenter + radius), yCenter, ((xCenter + radius) - 5), (yCenter + 5));
		g2D.drawLine((xCenter + radius), yCenter, ((xCenter + radius) - 5), (yCenter - 5));
		g2D.drawString("Y", (xCenter + 8), (yCenter - radius - 8));
		
		// X-Axis
		g2D.drawLine(xCenter, (yCenter - radius), xCenter, (yCenter + radius));
		g2D.drawLine(xCenter, (yCenter - radius), (xCenter + 5), ((yCenter - radius) + 5));
		g2D.drawLine(xCenter, (yCenter - radius), (xCenter - 5), ((yCenter - radius) + 5));
		g2D.drawLine(xCenter, (yCenter + radius), (xCenter + 5), ((yCenter + radius) - 5));
		g2D.drawLine(xCenter, (yCenter + radius), (xCenter - 5), ((yCenter + radius) - 5));
		g2D.drawString("X", (xCenter + radius + 8), (yCenter + 8));
	}
	
	public void setColors(final Color foregroundAxes, final Color foregroundGraph)
	{
		this.foregroundAxes  = foregroundAxes;
		this.foregroundGraph = foregroundGraph;
	}
}