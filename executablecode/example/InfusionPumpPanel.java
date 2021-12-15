import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class InfusionPumpPanel extends JPanel  {

    private boolean doorOpened = false;
    private boolean yellowLED = false;
    private boolean redLED = false;
    private boolean blueLED = false;


    private BufferedImage image;

    public InfusionPumpPanel() {
        try {
            image = ImageIO.read(new File("infusomat.png"));
        } catch (Exception e) {System.out.println("No image file found: " + e.getMessage());}
        setBackground(Color.white);
    }

    public void setDoorOpened(boolean opened) {
        doorOpened = opened;
    }

    public void setYellowLed(boolean b) {
        yellowLED = b;
    }

    public void setRedLed(boolean b) {
        redLED = b;
    }

    public void setBlueLed(boolean b) {
        blueLED = b;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.drawImage(image, 0, 0, null);


        if (doorOpened) {
            g.drawString("Door opened", 700, 180);
        } else {
            g.drawString("Door closed", 700, 180);
        }

        if (yellowLED) {
            Color c = g.getColor();
            g.setColor(Color.YELLOW);
            g.fillRect(142, 44, 12, 8);
            g.setColor(c);
        }

        if (redLED) {
            Color c = g.getColor();
            g.setColor(Color.RED);
            g.fillRect(172, 44, 12, 8);
            g.setColor(c);
        }

        if (blueLED) {
            Color c = g.getColor();
            g.setColor(Color.BLUE);
            g.fillRect(204, 44, 12, 8);
            g.setColor(c);
        }


    }


}
