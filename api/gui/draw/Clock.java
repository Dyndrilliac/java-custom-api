/*
 * Title: Clock
 * Author: Matthew Boyette
 * Date: 3/25/2013
 * 
 * This class creates a static clock. The clock can be initialized with the current time, a custom time, or a random time.
 * If desired, basic animating routines are included and can be toggled on and off after initialization.
 * The animation however is limited to the current time, which is why the clock does not initialize animated.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.Timer;

import api.util.Mathematics;

public class Clock extends JPanel
{
    protected final static class ClockListener implements ActionListener
    {
        Clock parent = null;
        
        public ClockListener(final Clock clock)
        {
            this.parent = clock;
        }
        
        @Override
        public final void actionPerformed(final ActionEvent event)
        {
            // Set the time.
            this.parent.setCurrentTime();
        }
    }
    
    private final static long serialVersionUID = 1L;
    private final Timer       animationTimer   = new Timer(500, new ClockListener(this));
    private Color             fgcCircle        = Color.BLACK;
    private Color             fgcHour          = Color.RED;
    private Color             fgcMinute        = Color.BLUE;
    private Color             fgcSecond        = Color.GREEN;
    private int               hour             = 0;
    private boolean           isAnimated       = false;
    private int               minute           = 0;
    private int               second           = 0;
    
    public Clock(final boolean isCurrent)
    {
        super();
        
        if (isCurrent)
        {
            this.setCurrentTime();
        }
        else
        {
            this.setRandomTime();
        }
    }
    
    public Clock(final int hour, final int minute, final int second)
    {
        super();
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
    }
    
    public final int getHour()
    {
        return this.hour;
    }
    
    public final int getMinute()
    {
        return this.minute;
    }
    
    @Override
    public final Dimension getPreferredSize()
    {
        return (new Dimension(250, 250));
    }
    
    public final int getSecond()
    {
        return this.second;
    }
    
    public final boolean isAnimated()
    {
        return this.isAnimated;
    }
    
    @Override
    public final void paintComponent(final Graphics g)
    {
        super.paintComponent(g);
        
        // Initialize clock parameters.
        int clockRadius = (int)(Math.min(this.getWidth(), this.getHeight()) * 0.8 * 0.5);
        int xCenter = this.getWidth() / 2;
        int yCenter = this.getHeight() / 2;
        
        // Draw circle.
        g.setColor(this.fgcCircle);
        g.drawOval((xCenter - clockRadius), (yCenter - clockRadius), (2 * clockRadius), (2 * clockRadius));
        g.drawString("12", (xCenter - 5), ((yCenter - clockRadius) + 12));
        g.drawString("9", ((xCenter - clockRadius) + 3), (yCenter + 5));
        g.drawString("3", ((xCenter + clockRadius) - 10), (yCenter + 3));
        g.drawString("6", (xCenter - 3), ((yCenter + clockRadius) - 3));
        
        // Draw exact time below the clock.
        g.drawString(this.toString(), (xCenter - 30), (yCenter + clockRadius + 20));
        
        // Draw the second hand.
        int sLength = (int)(clockRadius * 0.8);
        int xSecond = (int)(xCenter + (sLength * Math.sin(this.getSecond() * ((2 * Math.PI) / 60))));
        int ySecond = (int)(yCenter - (sLength * Math.cos(this.getSecond() * ((2 * Math.PI) / 60))));
        g.setColor(this.fgcSecond);
        g.drawLine(xCenter, yCenter, xSecond, ySecond);
        
        // Draw the minute hand.
        int mLength = (int)(clockRadius * 0.65);
        int xMinute = (int)(xCenter + (mLength * Math.sin(this.getMinute() * ((2 * Math.PI) / 60))));
        int yMinute = (int)(yCenter - (mLength * Math.cos(this.getMinute() * ((2 * Math.PI) / 60))));
        g.setColor(this.fgcMinute);
        g.drawLine(xCenter, yCenter, xMinute, yMinute);
        
        // Draw the hour hand.
        int hLength = (int)(clockRadius * 0.5);
        int xHour = (int)(xCenter + (hLength * Math.sin(((this.getHour() % 12) + (this.getMinute() / 60.0)) * ((2 * Math.PI) / 12))));
        int yHour = (int)(yCenter - (hLength * Math.cos(((this.getHour() % 12) + (this.getMinute() / 60.0)) * ((2 * Math.PI) / 12))));
        g.setColor(this.fgcHour);
        g.drawLine(xCenter, yCenter, xHour, yHour);
    }
    
    public final void setColors(final Color fgcCircle, final Color fgcHour, final Color fgcMinute, final Color fgcSecond)
    {
        this.fgcCircle = fgcCircle;
        this.fgcHour = fgcHour;
        this.fgcMinute = fgcMinute;
        this.fgcSecond = fgcSecond;
    }
    
    public final void setCurrentTime()
    {
        Calendar calendar = new GregorianCalendar();
        this.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        this.setMinute(calendar.get(Calendar.MINUTE));
        this.setSecond(calendar.get(Calendar.SECOND));
    }
    
    public final void setHour(final int hour)
    {
        this.hour = hour;
        this.repaint();
    }
    
    public final void setMinute(final int minute)
    {
        this.minute = minute;
        this.repaint();
    }
    
    public final void setRandomTime()
    {
        this.setHour(Mathematics.getRandomInteger(0, 23, true));
        this.setMinute(Mathematics.getRandomInteger(0, 59, true));
        this.setSecond(Mathematics.getRandomInteger(0, 59, true));
    }
    
    public final void setSecond(final int second)
    {
        this.second = second;
        this.repaint();
    }
    
    public final void startAnimation()
    {
        this.isAnimated = true;
        this.animationTimer.start();
    }
    
    public final void stopAnimation()
    {
        this.isAnimated = false;
        this.animationTimer.stop();
    }
    
    @Override
    public final String toString()
    {
        String hour = null, minute = null, second = null, ampm = null;
        int h = 0;
        
        if (this.getHour() < 12)
        {
            ampm = "AM";
        }
        else
        {
            ampm = "PM";
            h = (this.getHour() - 12);
            
            if (h < 10)
            {
                if (h == 0)
                {
                    hour = "12";
                }
                else
                {
                    hour = ("0" + h);
                }
            }
            else
            {
                hour = ("" + h);
            }
        }
        
        if (this.getHour() < 10)
        {
            if (this.getHour() == 0)
            {
                hour = "12";
            }
            else
            {
                hour = ("0" + this.getHour());
            }
        }
        else
        {
            if (this.getHour() < 12)
            {
                hour = ("" + this.getHour());
            }
        }
        
        if (this.getMinute() < 10)
        {
            minute = ("0" + this.getMinute());
        }
        else
        {
            minute = ("" + this.getMinute());
        }
        
        if (this.getSecond() < 10)
        {
            second = ("0" + this.getSecond());
        }
        else
        {
            second = ("" + this.getSecond());
        }
        
        return (hour + ":" + minute + ":" + second + " " + ampm);
    }
}