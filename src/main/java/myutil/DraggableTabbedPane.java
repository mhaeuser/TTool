// From stackOverflow
package myutil;    


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class DraggableTabbedPane extends JTabbedPane {

  private boolean dragging = false;
  private Image tabImage = null;
  private Point currentMouseLocation = null;
  private int draggedTabIndex = 0;
  private DraggableTabbedPaneCallbackInterface callback;
  private int myY = 0;

  public DraggableTabbedPane(DraggableTabbedPaneCallbackInterface callback) {
    super();
    this.callback = callback;
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {

        if(!dragging) {
          // Gets the tab index based on the mouse position
          int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());

          if(tabNumber >= 0) {
            draggedTabIndex = tabNumber;
            Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);
            bounds.height = bounds.height + 100;
            TraceManager.addDev("bounds width:" + bounds.width + " height:" + bounds.height);


            // Paint the tabbed pane to a buffer
            Image totalImage = new BufferedImage(getWidth(), getHeight() + 100, BufferedImage.TYPE_INT_ARGB);
            Graphics totalGraphics = totalImage.getGraphics();
            totalGraphics.setClip(bounds);
            // Don't be double buffered when painting to a static image.
            setDoubleBuffered(false);
            paintComponent(totalGraphics);

            // Paint just the dragged tab to the buffer
            tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = tabImage.getGraphics();
            myY = bounds.y;
            graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, DraggableTabbedPane.this);

            dragging = true;
            //repaint();
          }
        } else {
          currentMouseLocation = e.getPoint();

          // Need to repaint
          repaint();
        }

        super.mouseDragged(e);
      }
    });

    addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {

        if(dragging) {
          int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), 10);

          if(tabNumber >= 0) {
              if (draggedTabIndex != tabNumber) {
                  Component comp = getComponentAt(draggedTabIndex);
                  String title = getTitleAt(draggedTabIndex);
                  removeTabAt(draggedTabIndex);
                  insertTab(title, null, comp, null, tabNumber);
                  if (callback != null) {
                      callback.hasBeenDragged(draggedTabIndex, tabNumber);
                  }
              }

          }
        }

        dragging = false;
        tabImage = null;
      }
    });
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Are we dragging?
    if(dragging && currentMouseLocation != null && tabImage != null) {
      // Draw the dragged tab
      g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
      //g.drawImage(tabImage, currentMouseLocation.x, myY, this);
    }
  }

}

