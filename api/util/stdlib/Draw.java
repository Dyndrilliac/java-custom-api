
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac Draw.java
 * Execution: java Draw
 *
 * Drawing library. This class provides a basic capability for creating
 * drawings with your programs. It uses a simple graphics model that
 * allows you to create drawings consisting of points, lines, and curves
 * in a window on your computer and to save the drawings to a file.
 * This is the object-oriented version of standard draw; it supports
 * multiple indepedent drawing windows.
 *
 * Todo
 * ----
 * - Add support for gradient fill, etc.
 *
 * Remarks
 * -------
 * - don't use AffineTransform for rescaling since it inverts
 * images and strings
 * - careful using setFont in inner loop within an animation -
 * it can cause flicker
 *
 *************************************************************************/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

public final class Draw implements ActionListener, MouseListener, MouseMotionListener, KeyListener
{

	// pre-defined colors
	public static final Color	BLACK				= Color.BLACK;

	public static final Color	BLUE				= Color.BLUE;
	/**
	 * Shade of blue used in Introduction to Programming in Java.
	 * The RGB values are (9, 90, 166).
	 */
	public static final Color	BOOK_BLUE			= new Color(9, 90, 166);
	/**
	 * Shade of red used in Algorithms 4th edition.
	 * The RGB values are (173, 32, 24).
	 */
	public static final Color	BOOK_RED			= new Color(173, 32, 24);
	// boundary of drawing canvas, 0% border
	private static final double	BORDER				= 0.0;
	public static final Color	CYAN				= Color.CYAN;
	public static final Color	DARK_GRAY			= Color.DARK_GRAY;
	private static final Color	DEFAULT_CLEAR_COLOR	= Draw.WHITE;
	// default font
	private static final Font	DEFAULT_FONT		= new Font("SansSerif", Font.PLAIN, 16);
	// default colors
	private static final Color	DEFAULT_PEN_COLOR	= Draw.BLACK;
	// default pen radius
	private static final double	DEFAULT_PEN_RADIUS	= 0.002;
	// default canvas size is SIZE-by-SIZE
	private static final int	DEFAULT_SIZE		= 512;
	private static final double	DEFAULT_XMAX		= 1.0;

	private static final double	DEFAULT_XMIN		= 0.0;

	private static final double	DEFAULT_YMAX		= 1.0;

	private static final double	DEFAULT_YMIN		= 0.0;
	public static final Color	GRAY				= Color.GRAY;

	public static final Color	GREEN				= Color.GREEN;
	public static final Color	LIGHT_GRAY			= Color.LIGHT_GRAY;
	public static final Color	MAGENTA				= Color.MAGENTA;
	public static final Color	ORANGE				= Color.ORANGE;
	public static final Color	PINK				= Color.PINK;

	public static final Color	RED					= Color.RED;

	public static final Color	WHITE				= Color.WHITE;

	public static final Color	YELLOW				= Color.YELLOW;

	/**
	 * Test client.
	 */
	public static void main(final String[] args)
	{

		// create one drawing window
		Draw draw1 = new Draw("Test client 1");
		draw1.square(.2, .8, .1);
		draw1.filledSquare(.8, .8, .2);
		draw1.circle(.8, .2, .2);
		draw1.setPenColor(Draw.MAGENTA);
		draw1.setPenRadius(.02);
		draw1.arc(.8, .2, .1, 200, 45);

		// create another one
		Draw draw2 = new Draw("Test client 2");
		draw2.setCanvasSize(900, 200);
		// draw a blue diamond
		draw2.setPenRadius();
		draw2.setPenColor(Draw.BLUE);
		double[] x = {.1, .2, .3, .2};
		double[] y = {.2, .3, .2, .1};
		draw2.filledPolygon(x, y);

		// text
		draw2.setPenColor(Draw.BLACK);
		draw2.text(0.2, 0.5, "bdfdfdfdlack text");
		draw2.setPenColor(Draw.WHITE);
		draw2.text(0.8, 0.8, "white text");
	}

	// show we draw immediately or wait until next show?
	private boolean							defer			= false;
	// current font
	private Font							font;

	// the frame for drawing to the screen
	private JFrame							frame			= new JFrame();

	private int								height			= Draw.DEFAULT_SIZE;

	private final Object					keyLock			= new Object();

	private final TreeSet<Integer>			keysDown		= new TreeSet<Integer>();

	// keyboard state
	private final LinkedList<Character>		keysTyped		= new LinkedList<Character>();
	// event-based listeners
	private final ArrayList<DrawListener>	listeners		= new ArrayList<DrawListener>();

	// for synchronization
	private final Object					mouseLock		= new Object();

	// mouse state
	private boolean							mousePressed	= false;
	private double							mouseX			= 0;

	private double							mouseY			= 0;

	// name of window
	private String							name			= "Draw";
	private Graphics2D						offscreen, onscreen;
	// double buffered graphics
	private BufferedImage					offscreenImage, onscreenImage;

	// current pen color
	private Color							penColor;
	// current pen radius
	private double							penRadius;

	// canvas size
	private int								width			= Draw.DEFAULT_SIZE;

	private double							xmin, ymin, xmax, ymax;

	/**
	 * Create an empty drawing object.
	 */
	public Draw()
	{
		this.init();
	}

	/**
	 * Create an empty drawing object with the given name.
	 *
	 * @param name
	 *            the title of the drawing window.
	 */
	public Draw(final String name)
	{
		this.name = name;
		this.init();
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		FileDialog chooser = new FileDialog(this.frame, "Use a .png or .jpg extension", FileDialog.SAVE);
		chooser.setVisible(true);
		String filename = chooser.getFile();
		if (filename != null)
		{
			this.save(chooser.getDirectory() + File.separator + chooser.getFile());
		}
	}

	/*************************************************************************
	 * Event-based interactions.
	 *************************************************************************/

	public void addListener(final DrawListener listener)
	{
		// ensure there is a window for listenting to events
		this.show();
		this.listeners.add(listener);
		this.frame.addKeyListener(this);
		this.frame.addMouseListener(this);
		this.frame.addMouseMotionListener(this);
		this.frame.setFocusable(true);
	}

	/**
	 * Draw an arc of radius r, centered on (x, y), from angle1 to angle2 (in degrees).
	 *
	 * @param x
	 *            the x-coordinate of the center of the circle
	 * @param y
	 *            the y-coordinate of the center of the circle
	 * @param r
	 *            the radius of the circle
	 * @param angle1
	 *            the starting angle. 0 would mean an arc beginning at 3 o'clock.
	 * @param angle2
	 *            the angle at the end of the arc. For example, if
	 *            you want a 90 degree arc, then angle2 should be angle1 + 90.
	 * @throws RuntimeException
	 *             if the radius of the circle is negative
	 */
	public void arc(final double x, final double y, final double r, final double angle1, double angle2)
	{
		if (r < 0)
		{
			throw new RuntimeException("arc radius can't be negative");
		}
		while (angle2 < angle1)
		{
			angle2 += 360;
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * r);
		double hs = this.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.draw(new Arc2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));
		}
		this.draw();
	}

	/*************************************************************************
	 * User and screen coordinate systems
	 *************************************************************************/

	/**
	 * Draw a circle of radius r, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the circle
	 * @param y
	 *            the y-coordinate of the center of the circle
	 * @param r
	 *            the radius of the circle
	 * @throws RuntimeException
	 *             if the radius of the circle is negative
	 */
	public void circle(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("circle radius can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * r);
		double hs = this.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.draw(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/**
	 * Clear the screen to the default color (white).
	 */
	public void clear()
	{
		this.clear(Draw.DEFAULT_CLEAR_COLOR);
	}

	/**
	 * Clear the screen to the given color.
	 *
	 * @param color
	 *            the Color to make the background
	 */
	public void clear(final Color color)
	{
		this.offscreen.setColor(color);
		this.offscreen.fillRect(0, 0, this.width, this.height);
		this.offscreen.setColor(this.penColor);
		this.draw();
	}

	// create the menu bar (changed to private)
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenuItem menuItem1 = new JMenuItem(" Save...   ");
		menuItem1.addActionListener(this);
		menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(menuItem1);
		return menuBar;
	}

	// draw onscreen if defer is false
	private void draw()
	{
		if (this.defer)
		{
			return;
		}
		this.onscreen.drawImage(this.offscreenImage, 0, 0, null);
		this.frame.repaint();
	}

	/**
	 * Draw an ellipse with given semimajor and semiminor axes, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the ellipse
	 * @param y
	 *            the y-coordinate of the center of the ellipse
	 * @param semiMajorAxis
	 *            is the semimajor axis of the ellipse
	 * @param semiMinorAxis
	 *            is the semiminor axis of the ellipse
	 * @throws RuntimeException
	 *             if either of the axes are negative
	 */
	public void ellipse(final double x, final double y, final double semiMajorAxis, final double semiMinorAxis)
	{
		if (semiMajorAxis < 0)
		{
			throw new RuntimeException("ellipse semimajor axis can't be negative");
		}
		if (semiMinorAxis < 0)
		{
			throw new RuntimeException("ellipse semiminor axis can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * semiMajorAxis);
		double hs = this.factorY(2 * semiMinorAxis);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.draw(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	private double factorX(final double w)
	{
		return (w * this.width) / Math.abs(this.xmax - this.xmin);
	}

	private double factorY(final double h)
	{
		return (h * this.height) / Math.abs(this.ymax - this.ymin);
	}

	/**
	 * Draw filled circle of radius r, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the circle
	 * @param y
	 *            the y-coordinate of the center of the circle
	 * @param r
	 *            the radius of the circle
	 * @throws RuntimeException
	 *             if the radius of the circle is negative
	 */
	public void filledCircle(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("circle radius can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * r);
		double hs = this.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.fill(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/**
	 * Draw an ellipse with given semimajor and semiminor axes, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the ellipse
	 * @param y
	 *            the y-coordinate of the center of the ellipse
	 * @param semiMajorAxis
	 *            is the semimajor axis of the ellipse
	 * @param semiMinorAxis
	 *            is the semiminor axis of the ellipse
	 * @throws RuntimeException
	 *             if either of the axes are negative
	 */
	public void filledEllipse(final double x, final double y, final double semiMajorAxis, final double semiMinorAxis)
	{
		if (semiMajorAxis < 0)
		{
			throw new RuntimeException("ellipse semimajor axis can't be negative");
		}
		if (semiMinorAxis < 0)
		{
			throw new RuntimeException("ellipse semiminor axis can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * semiMajorAxis);
		double hs = this.factorY(2 * semiMinorAxis);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.fill(new Ellipse2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/**
	 * Draw a filled polygon with the given (x[i], y[i]) coordinates.
	 *
	 * @param x
	 *            an array of all the x-coordindates of the polygon
	 * @param y
	 *            an array of all the y-coordindates of the polygon
	 */
	public void filledPolygon(final double[] x, final double[] y)
	{
		int N = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)this.scaleX(x[0]), (float)this.scaleY(y[0]));
		for (int i = 0; i < N; i++)
		{
			path.lineTo((float)this.scaleX(x[i]), (float)this.scaleY(y[i]));
		}
		path.closePath();
		this.offscreen.fill(path);
		this.draw();
	}

	/**
	 * Draw a filled rectangle of given half width and half height, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the rectangle
	 * @param y
	 *            the y-coordinate of the center of the rectangle
	 * @param halfWidth
	 *            is half the width of the rectangle
	 * @param halfHeight
	 *            is half the height of the rectangle
	 * @throws RuntimeException
	 *             if halfWidth or halfHeight is negative
	 */
	public void filledRectangle(final double x, final double y, final double halfWidth, final double halfHeight)
	{
		if (halfWidth < 0)
		{
			throw new RuntimeException("half width can't be negative");
		}
		if (halfHeight < 0)
		{
			throw new RuntimeException("half height can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * halfWidth);
		double hs = this.factorY(2 * halfHeight);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.fill(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/**
	 * Draw a filled square of side length 2r, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the square
	 * @param y
	 *            the y-coordinate of the center of the square
	 * @param r
	 *            radius is half the length of any side of the square
	 * @throws RuntimeException
	 *             if r is negative
	 */
	public void filledSquare(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("square side length can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * r);
		double hs = this.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.fill(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/**
	 * Get the current font.
	 */
	public Font getFont()
	{
		return this.font;
	}

	/*************************************************************************
	 * Drawing images.
	 *************************************************************************/

	// get an image from the given filename
	private Image getImage(final String filename)
	{

		// to read from file
		ImageIcon icon = new ImageIcon(filename);

		// try to read from URL
		if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
		{
			try
			{
				URL url = new URL(filename);
				icon = new ImageIcon(url);
			}
			catch (Exception e)
			{ /* not a url */
			}
		}

		// in case file is inside a .jar
		if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
		{
			URL url = Draw.class.getResource(filename);
			if (url == null)
			{
				throw new RuntimeException("image " + filename + " not found");
			}
			icon = new ImageIcon(url);
		}

		return icon.getImage();
	}

	/**
	 * Get the current pen color.
	 */
	public Color getPenColor()
	{
		return this.penColor;
	}

	/**
	 * Get the current pen radius.
	 */
	public double getPenRadius()
	{
		return this.penRadius;
	}

	/**
	 * Has the user typed a key?
	 *
	 * @return true if the user has typed a key, false otherwise
	 */
	public boolean hasNextKeyTyped()
	{
		synchronized (this.keyLock)
		{
			return !this.keysTyped.isEmpty();
		}
	}

	private void init()
	{
		if (this.frame != null)
		{
			this.frame.setVisible(false);
		}
		this.frame = new JFrame();
		this.offscreenImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		this.onscreenImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		this.offscreen = this.offscreenImage.createGraphics();
		this.onscreen = this.onscreenImage.createGraphics();
		this.setXscale();
		this.setYscale();
		this.offscreen.setColor(Draw.DEFAULT_CLEAR_COLOR);
		this.offscreen.fillRect(0, 0, this.width, this.height);
		this.setPenColor();
		this.setPenRadius();
		this.setFont();
		this.clear();

		// add antialiasing
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		this.offscreen.addRenderingHints(hints);

		// frame stuff
		ImageIcon icon = new ImageIcon(this.onscreenImage);
		JLabel draw = new JLabel(icon);

		draw.addMouseListener(this);
		draw.addMouseMotionListener(this);

		this.frame.setContentPane(draw);
		this.frame.addKeyListener(this);    // JLabel cannot get keyboard focus
		this.frame.setResizable(false);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes all windows
		this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);      // closes only current window
		this.frame.setTitle(this.name);
		this.frame.setJMenuBar(this.createMenuBar());
		this.frame.pack();
		this.frame.requestFocusInWindow();
		this.frame.setVisible(true);
	}

	/**
	 * Is the keycode currently being pressed? This method takes as an argument
	 * the keycode (corresponding to a physical key). It can handle action keys
	 * (such as F1 and arrow keys) and modifier keys (such as shift and control).
	 * See <a href = "http://download.oracle.com/javase/6/docs/api/java/awt/event/KeyEvent.html">KeyEvent.java</a>
	 * for a description of key codes.
	 *
	 * @return true if keycode is currently being pressed, false otherwise
	 */
	public boolean isKeyPressed(final int keycode)
	{
		synchronized (this.keyLock)
		{
			return this.keysDown.contains(keycode);
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyPressed(final KeyEvent e)
	{
		synchronized (this.keyLock)
		{
			this.keysDown.add(e.getKeyCode());
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyReleased(final KeyEvent e)
	{
		synchronized (this.keyLock)
		{
			this.keysDown.remove(e.getKeyCode());
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void keyTyped(final KeyEvent e)
	{
		synchronized (this.keyLock)
		{
			this.keysTyped.addFirst(e.getKeyChar());
		}

		// notify all listeners
		for (DrawListener listener: this.listeners)
		{
			listener.keyTyped(e.getKeyChar());
		}
	}

	/**
	 * Draw a line from (x0, y0) to (x1, y1).
	 *
	 * @param x0
	 *            the x-coordinate of the starting point
	 * @param y0
	 *            the y-coordinate of the starting point
	 * @param x1
	 *            the x-coordinate of the destination point
	 * @param y1
	 *            the y-coordinate of the destination point
	 */
	public void line(final double x0, final double y0, final double x1, final double y1)
	{
		this.offscreen.draw(new Line2D.Double(this.scaleX(x0), this.scaleY(y0), this.scaleX(x1), this.scaleY(y1)));
		this.draw();
	}

	/*************************************************************************
	 * Drawing geometric shapes.
	 *************************************************************************/

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseClicked(final MouseEvent e)
	{
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseDragged(final MouseEvent e)
	{
		synchronized (this.mouseLock)
		{
			this.mouseX = this.userX(e.getX());
			this.mouseY = this.userY(e.getY());
		}
		// doesn't seem to work if a button is specified
		for (DrawListener listener: this.listeners)
		{
			listener.mouseDragged(this.userX(e.getX()), this.userY(e.getY()));
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseEntered(final MouseEvent e)
	{
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseExited(final MouseEvent e)
	{
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseMoved(final MouseEvent e)
	{
		synchronized (this.mouseLock)
		{
			this.mouseX = this.userX(e.getX());
			this.mouseY = this.userY(e.getY());
		}
	}

	/**
	 * Is the mouse being pressed?
	 *
	 * @return true or false
	 */
	public boolean mousePressed()
	{
		synchronized (this.mouseLock)
		{
			return this.mousePressed;
		}
	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mousePressed(final MouseEvent e)
	{
		synchronized (this.mouseLock)
		{
			this.mouseX = this.userX(e.getX());
			this.mouseY = this.userY(e.getY());
			this.mousePressed = true;
		}
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			for (DrawListener listener: this.listeners)
			{
				listener.mousePressed(this.userX(e.getX()), this.userY(e.getY()));
			}
		}

	}

	/**
	 * This method cannot be called directly.
	 */
	@Override
	public void mouseReleased(final MouseEvent e)
	{
		synchronized (this.mouseLock)
		{
			this.mousePressed = false;
		}
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			for (DrawListener listener: this.listeners)
			{
				listener.mouseReleased(this.userX(e.getX()), this.userY(e.getY()));
			}
		}
	}

	/**
	 * What is the x-coordinate of the mouse?
	 *
	 * @return the value of the x-coordinate of the mouse
	 */
	public double mouseX()
	{
		synchronized (this.mouseLock)
		{
			return this.mouseX;
		}
	}

	/**
	 * What is the y-coordinate of the mouse?
	 *
	 * @return the value of the y-coordinate of the mouse
	 */
	public double mouseY()
	{
		synchronized (this.mouseLock)
		{
			return this.mouseY;
		}
	}

	/**
	 * What is the next key that was typed by the user?
	 *
	 * @return the next key typed
	 */
	public char nextKeyTyped()
	{
		synchronized (this.keyLock)
		{
			return this.keysTyped.removeLast();
		}
	}

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y).
	 *
	 * @param x
	 *            the center x-coordinate of the image
	 * @param y
	 *            the center y-coordinate of the image
	 * @param s
	 *            the name of the image/picture, e.g., "ball.gif"
	 * @throws RuntimeException
	 *             if the image is corrupt
	 */
	public void picture(final double x, final double y, final String s)
	{
		Image image = this.getImage(s);
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		int ws = image.getWidth(null);
		int hs = image.getHeight(null);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}

		this.offscreen.drawImage(image, (int)Math.round(xs - (ws / 2.0)), (int)Math.round(ys - (hs / 2.0)), null);
		this.draw();
	}

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y),
	 * rotated given number of degrees
	 *
	 * @param x
	 *            the center x-coordinate of the image
	 * @param y
	 *            the center y-coordinate of the image
	 * @param s
	 *            the name of the image/picture, e.g., "ball.gif"
	 * @param degrees
	 *            is the number of degrees to rotate counterclockwise
	 * @throws RuntimeException
	 *             if the image is corrupt
	 */
	public void picture(final double x, final double y, final String s, final double degrees)
	{
		Image image = this.getImage(s);
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		int ws = image.getWidth(null);
		int hs = image.getHeight(null);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}

		this.offscreen.rotate(Math.toRadians(-degrees), xs, ys);
		this.offscreen.drawImage(image, (int)Math.round(xs - (ws / 2.0)), (int)Math.round(ys - (hs / 2.0)), null);
		this.offscreen.rotate(Math.toRadians(+degrees), xs, ys);

		this.draw();
	}

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y), rescaled to w-by-h.
	 *
	 * @param x
	 *            the center x coordinate of the image
	 * @param y
	 *            the center y coordinate of the image
	 * @param s
	 *            the name of the image/picture, e.g., "ball.gif"
	 * @param w
	 *            the width of the image
	 * @param h
	 *            the height of the image
	 * @throws RuntimeException
	 *             if the image is corrupt
	 */
	public void picture(final double x, final double y, final String s, final double w, final double h)
	{
		Image image = this.getImage(s);
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(w);
		double hs = this.factorY(h);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.drawImage(image, (int)Math.round(xs - (ws / 2.0)),
				(int)Math.round(ys - (hs / 2.0)),
				(int)Math.round(ws),
				(int)Math.round(hs), null);
		}
		this.draw();
	}

	/**
	 * Draw picture (gif, jpg, or png) centered on (x, y), rotated
	 * given number of degrees, rescaled to w-by-h.
	 *
	 * @param x
	 *            the center x-coordinate of the image
	 * @param y
	 *            the center y-coordinate of the image
	 * @param s
	 *            the name of the image/picture, e.g., "ball.gif"
	 * @param w
	 *            the width of the image
	 * @param h
	 *            the height of the image
	 * @param degrees
	 *            is the number of degrees to rotate counterclockwise
	 * @throws RuntimeException
	 *             if the image is corrupt
	 */
	public void picture(final double x, final double y, final String s, final double w, final double h, final double degrees)
	{
		Image image = this.getImage(s);
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(w);
		double hs = this.factorY(h);
		if ((ws < 0) || (hs < 0))
		{
			throw new RuntimeException("image " + s + " is corrupt");
		}
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}

		this.offscreen.rotate(Math.toRadians(-degrees), xs, ys);
		this.offscreen.drawImage(image, (int)Math.round(xs - (ws / 2.0)),
			(int)Math.round(ys - (hs / 2.0)),
			(int)Math.round(ws),
			(int)Math.round(hs), null);
		this.offscreen.rotate(Math.toRadians(+degrees), xs, ys);

		this.draw();
	}

	/**
	 * Draw one pixel at (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the pixel
	 * @param y
	 *            the y-coordinate of the pixel
	 */
	private void pixel(final double x, final double y)
	{
		this.offscreen.fillRect((int)Math.round(this.scaleX(x)), (int)Math.round(this.scaleY(y)), 1, 1);
	}

	/**
	 * Draw a point at (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the point
	 * @param y
	 *            the y-coordinate of the point
	 */
	public void point(final double x, final double y)
	{
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double r = this.penRadius;
		// double ws = factorX(2*r);
		// double hs = factorY(2*r);
		// if (ws <= 1 && hs <= 1) pixel(x, y);
		if (r <= 1)
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.fill(new Ellipse2D.Double(xs - (r / 2), ys - (r / 2), r, r));
		}
		this.draw();
	}

	/**
	 * Draw a polygon with the given (x[i], y[i]) coordinates.
	 *
	 * @param x
	 *            an array of all the x-coordindates of the polygon
	 * @param y
	 *            an array of all the y-coordindates of the polygon
	 */
	public void polygon(final double[] x, final double[] y)
	{
		int N = x.length;
		GeneralPath path = new GeneralPath();
		path.moveTo((float)this.scaleX(x[0]), (float)this.scaleY(y[0]));
		for (int i = 0; i < N; i++)
		{
			path.lineTo((float)this.scaleX(x[i]), (float)this.scaleY(y[i]));
		}
		path.closePath();
		this.offscreen.draw(path);
		this.draw();
	}

	/**
	 * Draw a rectangle of given half width and half height, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the rectangle
	 * @param y
	 *            the y-coordinate of the center of the rectangle
	 * @param halfWidth
	 *            is half the width of the rectangle
	 * @param halfHeight
	 *            is half the height of the rectangle
	 * @throws RuntimeException
	 *             if halfWidth or halfHeight is negative
	 */
	public void rectangle(final double x, final double y, final double halfWidth, final double halfHeight)
	{
		if (halfWidth < 0)
		{
			throw new RuntimeException("half width can't be negative");
		}
		if (halfHeight < 0)
		{
			throw new RuntimeException("half height can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * halfWidth);
		double hs = this.factorY(2 * halfHeight);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.draw(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/*************************************************************************
	 * Drawing text.
	 *************************************************************************/

	/**
	 * Save to file - suffix must be png, jpg, or gif.
	 *
	 * @param filename
	 *            the name of the file with one of the required suffixes
	 */
	public void save(final String filename)
	{
		File file = new File(filename);
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);

		// png files
		if (suffix.toLowerCase().equals("png"))
		{
			try
			{
				ImageIO.write(this.offscreenImage, suffix, file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// need to change from ARGB to RGB for jpeg
		// reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
		else
			if (suffix.toLowerCase().equals("jpg"))
			{
				WritableRaster raster = this.offscreenImage.getRaster();
				WritableRaster newRaster;
				newRaster = raster.createWritableChild(0, 0, this.width, this.height, 0, 0, new int[] {0, 1, 2});
				DirectColorModel cm = (DirectColorModel)this.offscreenImage.getColorModel();
				DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(),
					cm.getRedMask(),
					cm.getGreenMask(),
					cm.getBlueMask());
				BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false, null);
				try
				{
					ImageIO.write(rgbBuffer, suffix, file);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			else
			{
				System.out.println("Invalid image file type: " + suffix);
			}
	}

	// helper functions that scale from user coordinates to screen coordinates and back
	private double scaleX(final double x)
	{
		return (this.width * (x - this.xmin)) / (this.xmax - this.xmin);
	}

	private double scaleY(final double y)
	{
		return (this.height * (this.ymax - y)) / (this.ymax - this.ymin);
	}

	/**
	 * Set the window size to w-by-h pixels.
	 *
	 * @param w
	 *            the width as a number of pixels
	 * @param h
	 *            the height as a number of pixels
	 * @throws a
	 *             RunTimeException if the width or height is 0 or negative
	 */
	public void setCanvasSize(final int w, final int h)
	{
		if ((w < 1) || (h < 1))
		{
			throw new RuntimeException("width and height must be positive");
		}
		this.width = w;
		this.height = h;
		this.init();
	}

	/**
	 * Set the font to the default font (sans serif, 16 point).
	 */
	public void setFont()
	{
		this.setFont(Draw.DEFAULT_FONT);
	}

	/**
	 * Set the font to the given value.
	 *
	 * @param f
	 *            the font to make text
	 */
	public void setFont(final Font f)
	{
		this.font = f;
	}

	/*************************************************************************
	 * Save drawing to a file.
	 *************************************************************************/

	/**
	 * Set the upper-left hand corner of the drawing window to be (x, y), where (0, 0) is upper left.
	 *
	 * @param x
	 *            the number of pixels from the left
	 * @param y
	 *            the number of pixels from the top
	 * @throws a
	 *             RunTimeException if the width or height is 0 or negative
	 */
	public void setLocationOnScreen(final int x, final int y)
	{
		this.frame.setLocation(x, y);
	}

	/**
	 * Set the pen color to the default color (black).
	 */
	public void setPenColor()
	{
		this.setPenColor(Draw.DEFAULT_PEN_COLOR);
	}

	/**
	 * Set the pen color to the given color.
	 *
	 * @param color
	 *            the Color to make the pen
	 */
	public void setPenColor(final Color color)
	{
		this.penColor = color;
		this.offscreen.setColor(this.penColor);
	}

	/*************************************************************************
	 * Mouse interactions.
	 *************************************************************************/

	/**
	 * Set the pen color to the given RGB color.
	 *
	 * @param red
	 *            the amount of red (between 0 and 255)
	 * @param green
	 *            the amount of green (between 0 and 255)
	 * @param blue
	 *            the amount of blue (between 0 and 255)
	 * @throws IllegalArgumentException
	 *             if the amount of red, green, or blue are outside prescribed range
	 */
	public void setPenColor(final int red, final int green, final int blue)
	{
		if ((red < 0) || (red >= 256))
		{
			throw new IllegalArgumentException("amount of red must be between 0 and 255");
		}
		if ((green < 0) || (green >= 256))
		{
			throw new IllegalArgumentException("amount of red must be between 0 and 255");
		}
		if ((blue < 0) || (blue >= 256))
		{
			throw new IllegalArgumentException("amount of red must be between 0 and 255");
		}
		this.setPenColor(new Color(red, green, blue));
	}

	/**
	 * Set the pen size to the default (.002).
	 */
	public void setPenRadius()
	{
		this.setPenRadius(Draw.DEFAULT_PEN_RADIUS);
	}

	/**
	 * Set the radius of the pen to the given size.
	 *
	 * @param r
	 *            the radius of the pen
	 * @throws RuntimeException
	 *             if r is negative
	 */
	public void setPenRadius(final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("pen radius must be positive");
		}
		this.penRadius = r * Draw.DEFAULT_SIZE;
		BasicStroke stroke = new BasicStroke((float)this.penRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		// BasicStroke stroke = new BasicStroke((float) penRadius);
		this.offscreen.setStroke(stroke);
	}

	/**
	 * Set the x-scale to be the default (between 0.0 and 1.0).
	 */
	public void setXscale()
	{
		this.setXscale(Draw.DEFAULT_XMIN, Draw.DEFAULT_XMAX);
	}

	/**
	 * Set the x-scale (a 10% border is added to the values)
	 *
	 * @param min
	 *            the minimum value of the x-scale
	 * @param max
	 *            the maximum value of the x-scale
	 */
	public void setXscale(final double min, final double max)
	{
		double size = max - min;
		this.xmin = min - (Draw.BORDER * size);
		this.xmax = max + (Draw.BORDER * size);
	}

	/**
	 * Set the y-scale to be the default (between 0.0 and 1.0).
	 */
	public void setYscale()
	{
		this.setYscale(Draw.DEFAULT_YMIN, Draw.DEFAULT_YMAX);
	}

	/**
	 * Set the y-scale (a 10% border is added to the values).
	 *
	 * @param min
	 *            the minimum value of the y-scale
	 * @param max
	 *            the maximum value of the y-scale
	 */
	public void setYscale(final double min, final double max)
	{
		double size = max - min;
		this.ymin = min - (Draw.BORDER * size);
		this.ymax = max + (Draw.BORDER * size);
	}

	/**
	 * Display on-screen and turn off animation mode:
	 * subsequent calls to
	 * drawing methods such as <tt>line()</tt>, <tt>circle()</tt>, and <tt>square()</tt> will be displayed on screen when called. This is the default.
	 */
	public void show()
	{
		this.defer = false;
		this.draw();
	}

	/**
	 * Display on screen, pause for t milliseconds, and turn on <em>animation mode</em>: subsequent calls to
	 * drawing methods such as <tt>line()</tt>, <tt>circle()</tt>, and <tt>square()</tt> will not be displayed on screen until the next call to <tt>show()</tt>.
	 * This is useful for producing animations (clear the screen, draw a bunch of shapes,
	 * display on screen for a fixed amount of time, and repeat). It also speeds up
	 * drawing a huge number of shapes (call <tt>show(0)</tt> to defer drawing
	 * on screen, draw the shapes, and call <tt>show(0)</tt> to display them all
	 * on screen at once).
	 *
	 * @param t
	 *            number of milliseconds
	 */
	public void show(final int t)
	{
		this.defer = false;
		this.draw();
		try
		{
			Thread.sleep(t);
		}
		catch (InterruptedException e)
		{
			System.out.println("Error sleeping");
		}
		this.defer = true;
	}

	/**
	 * Draw a square of side length 2r, centered on (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the center of the square
	 * @param y
	 *            the y-coordinate of the center of the square
	 * @param r
	 *            radius is half the length of any side of the square
	 * @throws RuntimeException
	 *             if r is negative
	 */
	public void square(final double x, final double y, final double r)
	{
		if (r < 0)
		{
			throw new RuntimeException("square side length can't be negative");
		}
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		double ws = this.factorX(2 * r);
		double hs = this.factorY(2 * r);
		if ((ws <= 1) && (hs <= 1))
		{
			this.pixel(x, y);
		}
		else
		{
			this.offscreen.draw(new Rectangle2D.Double(xs - (ws / 2), ys - (hs / 2), ws, hs));
		}
		this.draw();
	}

	/*************************************************************************
	 * Keyboard interactions.
	 *************************************************************************/

	/**
	 * Write the given text string in the current font, centered on (x, y).
	 *
	 * @param x
	 *            the center x-coordinate of the text
	 * @param y
	 *            the center y-coordinate of the text
	 * @param s
	 *            the text
	 */
	public void text(final double x, final double y, final String s)
	{
		this.offscreen.setFont(this.font);
		FontMetrics metrics = this.offscreen.getFontMetrics();
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		int ws = metrics.stringWidth(s);
		int hs = metrics.getDescent();
		this.offscreen.drawString(s, (float)(xs - (ws / 2.0)), (float)(ys + hs));
		this.draw();
	}

	/**
	 * Write the given text string in the current font, centered on (x, y) and
	 * rotated by the specified number of degrees
	 *
	 * @param x
	 *            the center x-coordinate of the text
	 * @param y
	 *            the center y-coordinate of the text
	 * @param s
	 *            the text
	 * @param degrees
	 *            is the number of degrees to rotate counterclockwise
	 */
	public void text(final double x, final double y, final String s, final double degrees)
	{
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		this.offscreen.rotate(Math.toRadians(-degrees), xs, ys);
		this.text(x, y, s);
		this.offscreen.rotate(Math.toRadians(+degrees), xs, ys);
	}

	/**
	 * Write the given text string in the current font, left-aligned at (x, y).
	 *
	 * @param x
	 *            the x-coordinate of the text
	 * @param y
	 *            the y-coordinate of the text
	 * @param s
	 *            the text
	 */
	public void textLeft(final double x, final double y, final String s)
	{
		this.offscreen.setFont(this.font);
		FontMetrics metrics = this.offscreen.getFontMetrics();
		double xs = this.scaleX(x);
		double ys = this.scaleY(y);
		// int ws = metrics.stringWidth(s);
		int hs = metrics.getDescent();
		this.offscreen.drawString(s, (float)(xs), (float)(ys + hs));
		this.show();
	}

	private double userX(final double x)
	{
		return this.xmin + ((x * (this.xmax - this.xmin)) / this.width);
	}

	private double userY(final double y)
	{
		return this.ymax - ((y * (this.ymax - this.ymin)) / this.height);
	}

	public void xorOff()
	{
		this.offscreen.setPaintMode();
	}

	public void xorOn()
	{
		this.offscreen.setXORMode(Draw.DEFAULT_CLEAR_COLOR);
	}

}
