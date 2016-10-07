/*
 * Title: CarGUI
 * Author: Matthew Boyette
 * Date: 4/13/2013
 * 
 * This class provides a control panel for a Car object.
 */

package api.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import api.gui.draw.Car;
import api.util.Support;

public class CarGUI extends JPanel implements ActionListener
{
    protected static class GUIListener implements ActionListener
    {
        protected CarGUI parent = null;

        public GUIListener(final CarGUI gui)
        {
            this.parent = gui;
        }

        @Override
        public final void actionPerformed(final ActionEvent event)
        {
            // Update speed.
            JSlider velocity = this.parent.getVelocitySlider();
            Car car = this.parent.getCar();
            car.setVelocity(velocity.getValue());
        }
    }

    protected static final long serialVersionUID = 1L;
    protected JButton           blueBodyButton   = null;
    protected Car               car              = null;
    protected JButton           directionButton  = null;
    protected JButton           greenBodyButton  = null;
    protected final Timer       guiTimer         = new Timer(333, new GUIListener(this));
    protected JButton           hornButton       = null;
    protected JButton           redBodyButton    = null;
    protected JButton           startCarButton   = null;
    protected JButton           startRadioButton = null;
    protected JButton           stopCarButton    = null;
    protected JButton           stopRadioButton  = null;
    protected JSlider           velocitySlider   = null;
    protected JButton           yellowBodyButton = null;

    public CarGUI(final Car car)
    {
        this.setCar(car);
        this.drawGUI();
        this.guiTimer.start();
    }

    @Override
    public void actionPerformed(final ActionEvent event)
    {
        switch ( event.getActionCommand() )
        {
            case "Start Car":

                this.getCar().animationStart();
                break;

            case "Stop Car":

                this.getCar().animationStop();
                break;

            case "Change Direction":

                if ( this.getCar().getDirection() == Car.DIRECTION_LEFT )
                {
                    this.getCar().setDirection(Car.DIRECTION_RIGHT);
                }
                else
                {
                    this.getCar().setDirection(Car.DIRECTION_LEFT);
                }

                break;

            case "Honk Horn":

                this.getCar().honkHorn();
                break;

            case "Start Radio":

                this.getCar().startRadio();
                break;

            case "Stop Radio":

                this.getCar().stopRadio();
                break;

            case "Red":

                this.getCar().setBodyColor(Color.RED);
                break;

            case "Blue":

                this.getCar().setBodyColor(Color.BLUE);
                break;

            case "Green":

                this.getCar().setBodyColor(Color.GREEN);
                break;

            case "Yellow":

                this.getCar().setBodyColor(Color.YELLOW);
                break;

            default:

                break;
        }
    }

    public void drawGUI()
    {
        JPanel motionControls = new JPanel();
        JPanel soundControls = new JPanel();
        JPanel colorControls = new JPanel();
        this.startCarButton = new JButton("Start Car");
        this.stopCarButton = new JButton("Stop Car");
        this.directionButton = new JButton("Change Direction");
        this.hornButton = new JButton("Honk Horn");
        this.velocitySlider = new JSlider(SwingConstants.HORIZONTAL, 1, 3, 1);
        this.startRadioButton = new JButton("Start Radio");
        this.stopRadioButton = new JButton("Stop Radio");
        this.redBodyButton = new JButton("Red");
        this.blueBodyButton = new JButton("Blue");
        this.greenBodyButton = new JButton("Green");
        this.yellowBodyButton = new JButton("Yellow");

        this.startCarButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.stopCarButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.directionButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.hornButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.velocitySlider.setFont(Support.DEFAULT_TEXT_FONT);
        this.startRadioButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.stopRadioButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.redBodyButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.blueBodyButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.greenBodyButton.setFont(Support.DEFAULT_TEXT_FONT);
        this.yellowBodyButton.setFont(Support.DEFAULT_TEXT_FONT);

        this.startCarButton.addActionListener(this);
        this.stopCarButton.addActionListener(this);
        this.directionButton.addActionListener(this);
        this.hornButton.addActionListener(this);
        this.startRadioButton.addActionListener(this);
        this.stopRadioButton.addActionListener(this);
        this.redBodyButton.addActionListener(this);
        this.blueBodyButton.addActionListener(this);
        this.greenBodyButton.addActionListener(this);
        this.yellowBodyButton.addActionListener(this);

        this.setLayout(new BorderLayout());
        motionControls.setLayout(new BorderLayout());
        soundControls.setLayout(new FlowLayout());
        colorControls.setLayout(new FlowLayout());
        this.add(motionControls, BorderLayout.NORTH);
        this.add(soundControls, BorderLayout.CENTER);
        this.add(colorControls, BorderLayout.SOUTH);
        motionControls.add(this.startCarButton, BorderLayout.EAST);
        motionControls.add(this.stopCarButton, BorderLayout.WEST);
        motionControls.add(this.directionButton, BorderLayout.CENTER);
        motionControls.add(this.velocitySlider, BorderLayout.SOUTH);
        soundControls.add(this.stopRadioButton);
        soundControls.add(this.hornButton);
        soundControls.add(this.startRadioButton);
        colorControls.add(this.redBodyButton);
        colorControls.add(this.blueBodyButton);
        colorControls.add(this.greenBodyButton);
        colorControls.add(this.yellowBodyButton);
    }

    public final Car getCar()
    {
        return this.car;
    }

    public final JSlider getVelocitySlider()
    {
        return this.velocitySlider;
    }

    public final void setCar(final Car car)
    {
        this.car = car;
    }
}
