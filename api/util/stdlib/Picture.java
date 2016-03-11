
package api.util.stdlib;

/*************************************************************************
 * Compilation: javac Picture.java Execution: java Picture imagename Data type for manipulating individual pixels of an image. The original image can
 * be read from a file in jpg, gif, or png format, or the user can create a blank image of a given size. Includes methods for displaying the image in
 * a window on the screen or saving to a file. % java Picture mandrill.jpg Remarks ------- - pixel (x, y) is column x and row y, where (0, 0) is upper
 * left - see also GrayPicture.java for a grayscale version
 *************************************************************************/

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * This class provides methods for manipulating individual pixels of an image. The original image can be read from a <tt>.jpg</tt>, <tt>.gif</tt>, or
 * <tt>.png</tt> file or the user can create a blank image of a given size. This class includes methods for displaying the image in a window on the
 * screen or saving it to a file.
 * <p>
 * Pixel (<em>x</em>, <em>y</em>) is column <em>x</em> and row <em>y</em>. By default, the origin (0, 0) is upper left, which is a common convention
 * in image processing. The method <tt>setOriginLowerLeft()</tt> change the origin to the lower left.
 * <p>
 * For additional documentation, see <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of <i>Introduction to Programming in Java:
 * An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class Picture implements ActionListener
{
    /**
     * Tests this <tt>Picture</tt> data type. Reads a picture specified by the command-line argument, and shows it in a window on the screen.
     */
    public static void main(final String[] args)
    {
        Picture picture = new Picture(args[0]);
        System.out.printf("%d-by-%d\n", picture.width(), picture.height());
        picture.show();
    }

    private String        filename;                // name of file
    private JFrame        frame;                   // on-screen view
    private BufferedImage image;                   // the rasterized image
    private boolean       isOriginUpperLeft = true;  // location of origin

    private final int     width, height;           // width and height

    /**
     * Initializes a picture by reading in a .png, .gif, or .jpg from a File.
     */
    public Picture(final File file)
    {
        try
        {
            this.image = ImageIO.read(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not open file: " + file);
        }
        if (this.image == null)
        {
            throw new RuntimeException("Invalid image file: " + file);
        }
        this.width = this.image.getWidth(null);
        this.height = this.image.getHeight(null);
        this.filename = file.getName();
    }

    /**
     * Initializes a blank <tt>width</tt>-by-<tt>height</tt> picture, with <tt>width</tt> columns and <tt>height</tt> rows, where each pixel is black.
     */
    public Picture(final int width, final int height)
    {
        if (width < 0)
        {
            throw new IllegalArgumentException("width must be nonnegative");
        }
        if (height < 0)
        {
            throw new IllegalArgumentException("height must be nonnegative");
        }
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // set to TYPE_INT_ARGB to support transparency
        this.filename = width + "-by-" + height;
    }

    /**
     * Initializes a new picture that is a deep copy of <tt>picture</tt>.
     */
    public Picture(final Picture picture)
    {
        this.width = picture.width();
        this.height = picture.height();
        this.image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        this.filename = picture.filename;
        for (int col = 0; col < this.width(); col++)
        {
            for (int row = 0; row < this.height(); row++)
            {
                this.image.setRGB(col, row, picture.get(col, row).getRGB());
            }
        }
    }

    /**
     * Initializes a picture by reading in a .png, .gif, or .jpg from the given filename or URL name.
     */
    public Picture(final String filename)
    {
        this.filename = filename;
        try
        {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile())
            {
                this.image = ImageIO.read(file);
            }

            // now try to read from file in same directory as this .class file
            else
            {
                URL url = this.getClass().getResource(filename);
                if (url == null)
                {
                    url = new URL(filename);
                }
                this.image = ImageIO.read(url);
            }
            this.width = this.image.getWidth(null);
            this.height = this.image.getHeight(null);
        }
        catch (IOException e)
        {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }
    }

    /**
     * Opens a save dialog box when the user selects "Save As" from the menu.
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        FileDialog chooser = new FileDialog(this.frame, "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        if (chooser.getFile() != null)
        {
            this.save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Picture))
        {
            return false;
        }
        Picture other = (Picture)obj;
        if (this.filename == null)
        {
            if (other.filename != null)
            {
                return false;
            }
        }
        else if (!this.filename.equals(other.filename))
        {
            return false;
        }
        if (this.frame == null)
        {
            if (other.frame != null)
            {
                return false;
            }
        }
        else if (!this.frame.equals(other.frame))
        {
            return false;
        }
        if (this.height != other.height)
        {
            return false;
        }
        if (this.image == null)
        {
            if (other.image != null)
            {
                return false;
            }
        }
        else if (!this.image.equals(other.image))
        {
            return false;
        }
        if (this.isOriginUpperLeft != other.isOriginUpperLeft)
        {
            return false;
        }
        if (this.width != other.width)
        {
            return false;
        }
        return true;
    }

    /**
     * Returns the color of pixel (<tt>col</tt>, <tt>row</tt>).
     *
     * @return the color of pixel (<tt>col</tt>, <tt>row</tt>)
     * @throws IndexOutOfBoundsException unless both 0 &le; <tt>col</tt> &lt; <tt>width</tt> and 0 &le; <tt>row</tt> &lt; <tt>height</tt>
     */
    public Color get(final int col, final int row)
    {
        if ((col < 0) || (col >= this.width()))
        {
            throw new IndexOutOfBoundsException("col must be between 0 and " + (this.width() - 1));
        }
        if ((row < 0) || (row >= this.height()))
        {
            throw new IndexOutOfBoundsException("row must be between 0 and " + (this.height() - 1));
        }
        if (this.isOriginUpperLeft)
        {
            return new Color(this.image.getRGB(col, row));
        }
        else
        {
            return new Color(this.image.getRGB(col, this.height - row - 1));
        }
    }

    /**
     * Returns a JLabel containing this picture, for embedding in a JPanel, JFrame or other GUI widget.
     *
     * @return the <tt>JLabel</tt>
     */
    public JLabel getJLabel()
    {
        if (this.image == null)
        {
            return null;
        }         // no image available
        ImageIcon icon = new ImageIcon(this.image);
        return new JLabel(icon);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.filename == null) ? 0 : this.filename.hashCode());
        result = (prime * result) + ((this.frame == null) ? 0 : this.frame.hashCode());
        result = (prime * result) + this.height;
        result = (prime * result) + ((this.image == null) ? 0 : this.image.hashCode());
        result = (prime * result) + (this.isOriginUpperLeft ? 1231 : 1237);
        result = (prime * result) + this.width;
        return result;
    }

    /**
     * Returns the height of the picture.
     *
     * @return the height of the picture (in pixels)
     */
    public int height()
    {
        return this.height;
    }

    /**
     * Saves the picture to a file in a standard image format.
     */
    public void save(final File file)
    {
        this.filename = file.getName();
        if (this.frame != null)
        {
            this.frame.setTitle(this.filename);
        }
        String suffix = this.filename.substring(this.filename.lastIndexOf('.') + 1);
        suffix = suffix.toLowerCase();
        if (suffix.equals("jpg") || suffix.equals("png"))
        {
            try
            {
                ImageIO.write(this.image, suffix, file);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Error: filename must end in .jpg or .png");
        }
    }

    /**
     * Saves the picture to a file in a standard image format. The filetype must be .png or .jpg.
     */
    public void save(final String name)
    {
        this.save(new File(name));
    }

    /**
     * Sets the color of pixel (<tt>col</tt>, <tt>row</tt>) to given color.
     *
     * @throws IndexOutOfBoundsException unless both 0 &le; <tt>col</tt> &lt; <tt>width</tt> and 0 &le; <tt>row</tt> &lt; <tt>height</tt>
     * @throws NullPointerException if <tt>color</tt> is <tt>null</tt>
     */
    public void set(final int col, final int row, final Color color)
    {
        if ((col < 0) || (col >= this.width()))
        {
            throw new IndexOutOfBoundsException("col must be between 0 and " + (this.width() - 1));
        }
        if ((row < 0) || (row >= this.height()))
        {
            throw new IndexOutOfBoundsException("row must be between 0 and " + (this.height() - 1));
        }
        if (color == null)
        {
            throw new NullPointerException("can't set Color to null");
        }
        if (this.isOriginUpperLeft)
        {
            this.image.setRGB(col, row, color.getRGB());
        }
        else
        {
            this.image.setRGB(col, this.height - row - 1, color.getRGB());
        }
    }

    /**
     * Sets the origin to be the lower left pixel.
     */
    public void setOriginLowerLeft()
    {
        this.isOriginUpperLeft = false;
    }

    /**
     * Sets the origin to be the upper left pixel. This is the default.
     */
    public void setOriginUpperLeft()
    {
        this.isOriginUpperLeft = true;
    }

    /**
     * Displays the picture in a window on the screen.
     */
    public void show()
    {

        // create the GUI for viewing the image if needed
        if (this.frame == null)
        {
            this.frame = new JFrame();

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            menuBar.add(menu);
            JMenuItem menuItem1 = new JMenuItem(" Save...   ");
            menuItem1.addActionListener(this);
            menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            menu.add(menuItem1);
            this.frame.setJMenuBar(menuBar);

            this.frame.setContentPane(this.getJLabel());
            // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.frame.setTitle(this.filename);
            this.frame.setResizable(false);
            this.frame.pack();
            this.frame.setVisible(true);
        }

        // draw
        this.frame.repaint();
    }

    /**
     * Returns the width of the picture.
     *
     * @return the width of the picture (in pixels)
     */
    public int width()
    {
        return this.width;
    }

}
