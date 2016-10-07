/*
 * Title: Fans
 * Author: Matthew Boyette
 * Date: 3/25/2013
 * 
 * This class draws four fans in a 2x2 square.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;

public class Fans extends JPanel
{
    private static final long serialVersionUID = 1L;

    public Fans()
    {
        this.setLayout(new GridLayout(2, 2));
        this.add(new Fan());
        this.add(new Fan());
        this.add(new Fan());
        this.add(new Fan());
    }

    public Fans(final Color foregroundColor)
    {
        this.setLayout(new GridLayout(2, 2));
        this.add(new Fan(foregroundColor));
        this.add(new Fan(foregroundColor));
        this.add(new Fan(foregroundColor));
        this.add(new Fan(foregroundColor));
    }

    @Override
    public Dimension getPreferredSize()
    {
        return ( new Dimension(250, 250) );
    }
}
