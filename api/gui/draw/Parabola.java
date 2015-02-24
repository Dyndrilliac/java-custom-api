/*
 * Title: Parabola
 * Author: Matthew Boyette
 * Date: 3/25/2013
 *
 * This class creates a parabola drawn with accompanying X and Y axes.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;

import javax.swing.JPanel;

public class Parabola extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	private Point				beginPoint;
	private Point				endPoint;
	private Color				fgcGraph			= Color.BLUE;
	private Color				fgcPlane			= Color.BLACK;
	private Point				focalPoint;

	public Parabola()
	{
	}

	public Parabola(final Point focalPoint, final Point beginPoint, final Point endPoint)
	{
		this.setPoints(focalPoint, beginPoint, endPoint);
	}

	public final Point getBeginPoint()
	{
		return this.beginPoint;
	}

	public final Point getEndPoint()
	{
		return this.endPoint;
	}

	public final Point getFocalPoint()
	{
		return this.focalPoint;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return (new Dimension(250, 250));
	}

	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2D = (Graphics2D)g;

		int xCenter = this.getWidth() / 2;
		int yCenter = this.getHeight() / 2;
		int radius = (int)(Math.min(this.getWidth(), this.getHeight()) * 0.4);

		// Draw Axes
		g2D.setColor(this.fgcPlane);

		g2D.drawLine((xCenter - radius), yCenter, (xCenter + radius), yCenter);
		g2D.drawLine((xCenter - radius), yCenter, ((xCenter - radius) + 5), (yCenter + 5));
		g2D.drawLine((xCenter - radius), yCenter, ((xCenter - radius) + 5), (yCenter - 5));
		g2D.drawLine((xCenter + radius), yCenter, ((xCenter + radius) - 5), (yCenter + 5));
		g2D.drawLine((xCenter + radius), yCenter, ((xCenter + radius) - 5), (yCenter - 5));
		g2D.drawString("Y", (xCenter + 8), (yCenter - radius - 8));

		g2D.drawLine(xCenter, (yCenter - radius), xCenter, (yCenter + radius));
		g2D.drawLine(xCenter, (yCenter - radius), (xCenter + 5), ((yCenter - radius) + 5));
		g2D.drawLine(xCenter, (yCenter - radius), (xCenter - 5), ((yCenter - radius) + 5));
		g2D.drawLine(xCenter, (yCenter + radius), (xCenter + 5), ((yCenter + radius) - 5));
		g2D.drawLine(xCenter, (yCenter + radius), (xCenter - 5), ((yCenter + radius) - 5));
		g2D.drawString("X", (xCenter + radius + 8), (yCenter + 8));

		// Draw Parabola
		g2D.setColor(this.fgcGraph);

		QuadCurve2D quadratic = new QuadCurve2D.Double();
		Double x1 = xCenter + this.getBeginPoint().getX();
		Double x2 = xCenter + this.getEndPoint().getX();
		Double y1 = yCenter + this.getBeginPoint().getY();
		Double y2 = yCenter + this.getEndPoint().getY();
		Double ctrlX = xCenter + this.getFocalPoint().getX();
		Double ctrlY = yCenter + ((radius / 3) * 1.5) + this.getFocalPoint().getY();

		quadratic.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
		g2D.draw(quadratic);
	}

	public final void setBeginPoint(final Point beginPoint)
	{
		this.beginPoint = beginPoint;
	}

	public final void setColors(final Color fgcPlane, final Color fgcGraph)
	{
		this.fgcPlane = fgcPlane;
		this.fgcGraph = fgcGraph;
	}

	public final void setEndPoint(final Point endPoint)
	{
		this.endPoint = endPoint;
	}

	public final void setFocalPoint(final Point focalPoint)
	{
		this.focalPoint = focalPoint;
	}

	public final void setPoints(final Point focalPoint, final Point beginPoint, final Point endPoint)
	{
		this.setFocalPoint(focalPoint);
		this.setBeginPoint(beginPoint);
		this.setEndPoint(endPoint);
	}
}