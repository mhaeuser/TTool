import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


public class MainInfusionPump extends JFrame implements Feeder, MouseListener {

    private static int ON_OFF  = 0;
    private static int OK  = 1;
    private static int UP  = 2;
    private static int DOWN  = 3;
    private static int LEFT  = 4;
    private static int RIGHT  = 5;
    private static int START_STOP  = 6;
    private static int OPEN_DOOR  = 6;
    private static int RESET  = 8;


    private InfusionPumpPanel ipp;
    private DatagramServer ds;

    public MainInfusionPump() {
        super("infusion pump demonstration");
        setSize(800, 250);
        setVisible(true);
        ds = new DatagramServer();
        ds.setFeeder(this);

        initComponents();
        ds.runServer();
    }

    public void initComponents() {
        setLayout(new BorderLayout());
        ipp = new InfusionPumpPanel();
        ipp.addMouseListener(this);
        ipp.setPreferredSize(new Dimension(800,250));
        add(ipp, BorderLayout.CENTER);
        ipp.revalidate();
    }

    public void setMessage(String msg) {
        if (ipp == null) {
            return;
        }

        int index;
        String s;
        int duration;
        System.out.println("Got message:" + msg);
        try {
            if (msg.startsWith("y")) {
                s = msg.substring(1, msg.length());
                if (s.compareTo("1") == 0) {
                    ipp.setYellowLed(true);
                } else {
                    ipp.setYellowLed(false);
                }
            }

            if (msg.startsWith("r")) {
                s = msg.substring(1, msg.length());
                if (s.compareTo("1") == 0) {
                    ipp.setRedLed(true);
                } else {
                    ipp.setRedLed(false);
                }
            }

            if (msg.startsWith("y")) {
                s = msg.substring(1, msg.length());
                if (s.compareTo("1") == 0) {
                    ipp.setBlueLed(true);
                } else {
                    ipp.setBlueLed(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception when computing message: " + e.getMessage());
        }

        ipp.repaint();
    }

    public void mouseClicked(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        System.out.println("Mouse clicked (" + x + "," + y + ")");

        // ON/OFF?
        if ((x>520)&&(x<551)&&(y>65)&&(y<97)) {
            System.out.println("Mouse clicked on ON/OFF");
            if (ds != null) {
                ds.sendDatagramTo("" + ON_OFF);
            }
        }

        // START_STOP?
        if ((x>520)&&(x<551)&&(y>110)&&(y<140)) {
            System.out.println("Mouse clicked on start/stop");
            if (ds != null) {
                ds.sendDatagramTo("" + START_STOP);
            }
        }

        // OK?
        if ((x>439)&&(x<469)&&(y>110)&&(y<140)) {
            System.out.println("Mouse clicked on OK");
            if (ds != null) {
                ds.sendDatagramTo("" + OK);
            }
        }

        // UP?
        if ((x>363)&&(x<394)&&(y>65)&&(y<97)) {
            System.out.println("Mouse clicked on UP");
            if (ds != null) {
                ds.sendDatagramTo("" + UP);
            }
        }

        // DOWN?
        if ((x>363)&&(x<394)&&(y>110)&&(y<140)) {
            System.out.println("Mouse clicked on DOWN");
            if (ds != null) {
                ds.sendDatagramTo("" + DOWN);
            }
        }

        // LEFT?
        if ((x>331)&&(x<361)&&(y>91)&&(y<117)) {
            System.out.println("Mouse clicked on LEFT");
            if (ds != null) {
                ds.sendDatagramTo("" + LEFT);
            }
        }

        // RIGHT?
        if ((x>399)&&(x<429)&&(y>91)&&(y<117)) {
            System.out.println("Mouse clicked on RIGHT");
            if (ds != null) {
                ds.sendDatagramTo("" + RIGHT);
            }
        }

        // OPEN_DOOR?
        if ((x>589)&&(x<618)&&(y>91)&&(y<117)) {
            System.out.println("Mouse clicked on OPEN_DOOR");
            if (ds != null) {
                ds.sendDatagramTo("" + OPEN_DOOR);
            }
        }

    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    public static void main(String[] args) {
        MainInfusionPump mmw = new MainInfusionPump();
    }

}
