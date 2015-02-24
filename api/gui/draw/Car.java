/*
 * Title: Car
 * Author: Matthew Boyette
 * Date: 4/13/2013
 *
 * This class draws a Car which can be animated to drive across the screen.
 * Interactive user control is provided by a separate CarGUI swing object.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.Timer;

import api.util.Support;

public class Car extends JPanel
{
	protected static class CarListener implements ActionListener
	{
		protected Car	parent	= null;

		public CarListener(final Car car)
		{
			this.parent = car;
		}

		@Override
		public final void actionPerformed(final ActionEvent event)
		{
			// Move the car.
			this.parent.move();
		}
	}

	public static final int		DIRECTION_LEFT		= 1;
	public static final int		DIRECTION_RIGHT		= 2;
	private static final long	serialVersionUID	= 1L;
	public static final int		VELOCITY_FAST		= 3;
	public static final int		VELOCITY_MEDIUM		= 2;
	public static final int		VELOCITY_SLOW		= 1;
	private final Timer			animation			= new Timer(333, new CarListener(this));
	private Color				bodyColor			= Color.RED;
	private int					direction			= Car.DIRECTION_RIGHT;
	private Point2D				origin				= null;
	private Component			parent				= null;
	private int					velocity			= Car.VELOCITY_SLOW;

	public Car(final Component parent)
	{
		this.calculateOrigin();
		this.parent = parent;
	}

	public void animationStart()
	{
		Support.playAudioClipFromURL(this.parent, "http://static1.grsites.com/archive/sounds/cars/cars005.wav");
		this.animation.start();
	}

	public void animationStop()
	{
		Support.playAudioClipFromURL(this.parent, "http://static1.grsites.com/archive/sounds/cars/cars024.wav");
		this.animation.stop();
	}

	protected void calculateOrigin()
	{
		double xCenter = (this.getWidth() / 2.0);
		double yCenter = (this.getHeight() / 2.0);
		this.setOrigin(new Point2D.Double(xCenter, yCenter));
	}

	public final Color getBodyColor()
	{
		return this.bodyColor;
	}

	public final int getDirection()
	{
		return this.direction;
	}

	public final Point2D getOrigin()
	{
		return this.origin;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return (new Dimension(this.parent.getWidth(), (this.parent.getHeight() / 4)));
	}

	public final int getVelocity()
	{
		return this.velocity;
	}

	public void honkHorn()
	{
		Support.playAudioClipFromURL(this.parent, "http://static1.grsites.com/archive/sounds/cars/cars014.wav");
	}

	public void move()
	{
		Point2D oldOrigin = new Point2D.Double(this.getOrigin().getX(), this.getOrigin().getY());
		double updatedX = 0;
		double updatedY = 0;
		double rate = (this.getVelocity() * 20.0);

		if (this.getDirection() == Car.DIRECTION_LEFT)
		{
			// Move to the left.
			updatedX = oldOrigin.getX() - rate;
			updatedY = oldOrigin.getY();

			if (updatedX < 0)
			{
				updatedX = this.getWidth();
			}
		}
		else
		{
			// Move to the right.
			updatedX = oldOrigin.getX() + rate;
			updatedY = oldOrigin.getY();

			if (updatedX > this.getWidth())
			{
				updatedX = 0;
			}
		}

		this.setOrigin(new Point2D.Double(updatedX, updatedY));
		this.repaint();
	}

	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		GradientPaint solidColor = null;
		Graphics2D g2D = (Graphics2D)g;
		int radius = (int)(Math.min(this.getWidth(), this.getHeight()) * .5);

		// Calculate lengths and widths.
		double bodyLength = (radius / 2.0);
		double bodyWidth = (radius / 4.0);
		double wheelLength = (radius / 10.0);
		double wheelWidth = (radius / 10.0);
		double wheel2diff = (radius / 10.0);

		// Draw rectangular body.
		double bodyX = this.getOrigin().getX();
		double bodyY = this.getOrigin().getY();
		RoundRectangle2D carBody = new RoundRectangle2D.Double(bodyX, bodyY, bodyLength, bodyWidth, 5, 5);
		solidColor = new GradientPaint(0, 0, this.getBodyColor(), 100, 0, this.getBodyColor());
		g2D.setPaint(solidColor);
		g2D.fill(carBody);

		// Change colors for the wheels.
		solidColor = new GradientPaint(0, 0, Color.BLACK, 100, 0, Color.BLACK);

		// Draw the left circular wheel.
		double wheel1X = this.getOrigin().getX();
		double wheel1Y = this.getOrigin().getY() + (bodyWidth - (wheel2diff / 2));
		Ellipse2D wheel1 = new Ellipse2D.Double(wheel1X, wheel1Y, wheelLength, wheelWidth);
		g2D.setPaint(solidColor);
		g2D.fill(wheel1);

		// Draw the right circular wheel.
		double wheel2X = this.getOrigin().getX() + (bodyLength - wheel2diff);
		double wheel2Y = this.getOrigin().getY() + (bodyWidth - (wheel2diff / 2));
		Ellipse2D wheel2 = new Ellipse2D.Double(wheel2X, wheel2Y, wheelLength, wheelWidth);
		g2D.setPaint(solidColor);
		g2D.fill(wheel2);
	}

	public final void setBodyColor(final Color bodyColor)
	{
		this.bodyColor = bodyColor;
		this.repaint();
	}

	public final void setDirection(final int direction)
	{
		this.direction = direction;
	}

	protected final void setOrigin(final Point2D origin)
	{
		this.origin = origin;
	}

	public final void setVelocity(final int velocity)
	{
		this.velocity = velocity;
	}

	public void startRadio()
	{
		Support.openWebPageInDefaultBrowser(this.parent, "https://www.youtube.com/watch?v=Hva5KS8TFEQ&list=PLLsuQeGbt7lp94HjetXZPXfyVDMxNAcxV");
	}

	public void stopRadio()
	{
		try
		{
			final String os = System.getProperty("os.name");

			// Since I chose YouTube as my way of implementing the "radio" functionality, the only way I could turn it off is by closing the browser window.
			// Unfortunately, I couldn't figure out a more elegant way to do this than closing all open browser windows.
			// If you had a more user friendly strategy in mind, I'd love to hear about it.
			if (os.contains("Windows"))
			{
				Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
				Runtime.getRuntime().exec("taskkill /F /IM iexplorer.exe");
				Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
				Runtime.getRuntime().exec("taskkill /F /IM safari.exe");
				Runtime.getRuntime().exec("taskkill /F /IM opera.exe");
			}
			else
			{
				// Assuming the OS will be some version of Unix, Linux, or Mac.
				Runtime.getRuntime().exec("kill `ps -ef | grep -i firefox | grep -v grep | awk '{print $2}'`");
				Runtime.getRuntime().exec("kill `ps -ef | grep -i chrome | grep -v grep | awk '{print $2}'`");
				Runtime.getRuntime().exec("kill `ps -ef | grep -i safari | grep -v grep | awk '{print $2}'`");
			}
		}
		catch (final Exception exception)
		{
			Support.displayException(this.parent, exception, false);
		}
	}
}