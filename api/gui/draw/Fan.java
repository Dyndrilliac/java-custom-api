/*
 * Title: Fan
 * Author: Matthew Boyette
 * Date: 3/25/2013
 * 
 * This class draws a fan.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Fan extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	
	public Fan()
	{
		this.setForeground(Color.BLACK);
	}
	
	public Fan(final Color foregroundColor)
	{
		this.setForeground(foregroundColor);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return (new Dimension((250 / 4), (250 / 4)));
	}
	
	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		
		int xCenter = this.getWidth() / 2;
		int yCenter = this.getHeight() / 2;
		int radius = (int)(Math.min(this.getWidth(), this.getHeight()) * 0.4);
		
		int x = xCenter - radius;
		int y = yCenter - radius;
		
		g.setColor(this.getForeground());
		g.drawOval(x, y, 2 * radius, 2 * radius);
		g.fillArc(x, y, 2 * radius, 2 * radius, 0, 30);
		g.fillArc(x, y, 2 * radius, 2 * radius, 90, 30);
		g.fillArc(x, y, 2 * radius, 2 * radius, 180, 30);
		g.fillArc(x, y, 2 * radius, 2 * radius, 270, 30);
	}
}