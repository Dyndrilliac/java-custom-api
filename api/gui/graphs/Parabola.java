/*
	Title:  Parabola
	Author: Matthew Boyette
	Date:   3/28/2013
	
	This class provides the implementation for a Parabola graph (quadratic function).
*/
package api.gui.graphs;

import api.gui.Graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

public class Parabola extends Graph
{
	private static final long serialVersionUID = 1L;
	
	private Point2D focalPoint;
	private Point2D beginPoint;
	private Point2D endPoint;
	
	public Parabola(final Point2D focalPoint, final Point2D beginPoint, final Point2D endPoint)
	{
		this.setPoints(focalPoint, beginPoint, endPoint);
	}
	
	protected void drawParabola(final Graphics2D g2D, final int radius)
	{
		int xCenter = (int)this.getOrigin().getX();
		int yCenter = (int)this.getOrigin().getY();
		
		g2D.setColor(this.getForegroundAxes());
		
		Double x1    = (xCenter + this.getBeginPoint().getX());
		Double y1    = (yCenter + this.getBeginPoint().getY());
		Double ctrlX = (xCenter + this.getFocalPoint().getX());
		Double ctrlY = (yCenter + this.getFocalPoint().getY());
		Double x2    = (xCenter + this.getEndPoint().getX());
		Double y2    = (yCenter + this.getEndPoint().getY());
		
		QuadCurve2D quadratic = new QuadCurve2D.Double(x1, y1, ctrlX, ctrlY, x2, y2);
		g2D.draw(quadratic);
	}
	
	public Point2D getBeginPoint()
	{
		return this.beginPoint;
	}
	
	public Point2D getEndPoint()
	{
		return this.endPoint;
	}
	
	public Point2D getFocalPoint()
	{
		return this.focalPoint;
	}
	
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2D    = (Graphics2D)g;
		int        radius = (int)(Math.min(this.getWidth(), this.getHeight()) * 0.4);
		
		this.drawPlane(g2D, radius);
		this.drawParabola(g2D, radius);
	}
	
	public void setBeginPoint(final Point2D beginPoint)
	{
		this.beginPoint = beginPoint;
	}
	
	public void setEndPoint(final Point2D endPoint)
	{
		this.endPoint = endPoint;
	}
	
	public void setFocalPoint(final Point2D focalPoint)
	{
		this.focalPoint = focalPoint;
	}
	
	public void setPoints(final Point2D focalPoint, final Point2D beginPoint, final Point2D endPoint)
	{
		this.setFocalPoint(focalPoint);
		this.setBeginPoint(beginPoint);
		this.setEndPoint(endPoint);
	}
}