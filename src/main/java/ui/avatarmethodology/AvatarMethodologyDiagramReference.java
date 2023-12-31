/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */




package ui.avatarmethodology;


import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogManageListOfString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
   * Class AvatarMethodologyDiagramReference
   * Diagram reference requirement: Used to reference diagrams from the
   * Avatar methodology
   * Creation: 26/08/2014
   * @version 1.0 26/08/2014
   * @author Ludovic APVRILLE
 */
public abstract class AvatarMethodologyDiagramReference extends TGCScalableWithInternalComponent implements SwallowTGComponent  {
    public String oldValue; 
    //protected int textX = 5; 
//    protected int textY = 22; 
//    protected int lineHeight = 30;
//    protected double dlineHeight = 0.0;
//    protected int reqType = 0;
    // 0: normal, 1: formal, 2: security
    //protected int startFontSize = 10;
    //protected Graphics graphics;
    //protected int iconSize = 30;
    private static final int ICON_SIZE = 30;
    protected Font myFont, myFontB;
    protected int maxFontSize = 30;
    protected int minFontSize = 4;
    //protected int currentFontSize = -1;
    //protected boolean displayText = true;

    protected int typeOfReference;

    protected final static String[] TYPE_STR = {"Assumptions", "Requirements", "Analysis", "Design", "Properties", "Prototyping"};
    protected final static int NB_TYPE = 6;

    protected final static int ASSUMPTIONS = 0;
    protected final static int REQUIREMENT = 1;
    protected final static int ANALYSIS = 2;
    protected final static int DESIGN = 3;
    protected final static int PROPERTY = 4;
    protected final static int PROTOTYPING = 5;

    protected JMenuItem diagramReference;



    // Icon
    //private int iconSize = 18;
    //private boolean iconIsDrawn = false;

    public AvatarMethodologyDiagramReference(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        // Issue #31
        lineLength = 30;
        textX = 5;
        textY = 22;
    
        minWidth = 10;
        minHeight = lineLength;


        makeValue();
        
        initScaling(200, 120);
        // Issue #31
//        oldScaleFactor = tdp.getZoom();
//        dlineHeight = lineHeight * oldScaleFactor;
//        lineHeight = (int)dlineHeight;
//        dlineHeight = dlineHeight - lineHeight;
//
//        minWidth = 10;
//        minHeight = lineLength;


        nbConnectingPoint = 12;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.0, 0.25, TGConnectingPoint.WEST);
        connectingPoint[1] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[2] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.0, 0.75, TGConnectingPoint.WEST);
        connectingPoint[3] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 1.0, 0.25, TGConnectingPoint.EAST);
        connectingPoint[4] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 1.0, 0.5, TGConnectingPoint.EAST);
        connectingPoint[5] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 1.0, 0.75, TGConnectingPoint.EAST);
        connectingPoint[6] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.25, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[7] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.5, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[8] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.75, 0.0, TGConnectingPoint.NORTH);
        connectingPoint[9] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.25, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[10] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.5, 1.0, TGConnectingPoint.SOUTH);
        connectingPoint[11] = new AvatarMethodologyConnectingPoint(this, 0, 0, true, true, 0.75, 1.0, TGConnectingPoint.SOUTH);

        addTGConnectingPointsCommentTop();

        nbInternalTGComponent = 0;
        //tgcomponent = new TGComponent[nbInternalTGComponent];

        //    int h = 1;
        //TAttributeRequirement tgc0;
        //tgc0 = new TAttributeRequirement(x, y+height+h, 0, 0, height + h, height+h, true, this, _tdp);
        //tgcomponent[0] = tgc0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        multieditable = true;
        
        oldValue = value;

        myImageIcon = IconManager.imgic5006;


        actionOnAdd();
    }

    public void makeValue() {
        value = TYPE_STR[typeOfReference];
    }

    /**
     * From abstract class ui.TGComponent: declaration of abstract method
	 * InternalDrawing
	 * @param g
	 * */
    @Override
    public void internalDrawing(Graphics g) {
        Font f = g.getFont();
        //  Font fold = f;
        //    int w, c;
        //int size;



//        if (!tdp.isScaled()) {
//            graphics = g;
//        }
        // Issue #31 The font is already managed when drawing the panel
//        if (((rescaled) && (!tdp.isScaled())) || myFont == null) {
//            currentFontSize = tdp.getFontSize();
//            //
//            myFont = f.deriveFont((float)currentFontSize);
//            myFontB = myFont.deriveFont(Font.BOLD);
//
//            if (rescaled) {
//                rescaled = false;
//            }
//        }
        final int fontSize = g.getFont().getSize();
        displayText = fontSize >= minFontSize;
        //issue #31 displayText = currentFontSize >= minFontSize;

        g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        g.fillRect(x, y, width, height);
        ColorManager.setColor(g, getState(), 0);
        g.drawRect(x, y, width, height);

        //g.drawLine(x, y+lineHeight, x+width, y+lineHeight);
        //g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        //g.fillRect(x+1, y+1, width-1, lineHeight-1);
        //g.setColor(ColorManager.AVATAR_REQUIREMENT_TOP);
        //g.fillRect(x+1, y+1+lineHeight, width-1, height-1-lineHeight);
        ColorManager.setColor(g, getState(), 0);
        //if (!isTextReadable(g))
    	//	return;
        if (!isTextReadable(g) || !canTextGoInTheBox(g, fontSize, value, ICON_SIZE))
    		return;
        //if ((lineLength > 23) && (width > 23)){
            //g.drawImage(IconManager.img5100, x + width - iconSize + 1, y + 3, Color.yellow, null);
        g.drawImage( scale( IconManager.img5100 ), x + width - scale(ICON_SIZE + 1 ), y + scale( 3 ), Color.yellow, null);
        //}
    	
        if (displayText) {
            //size = currentFontSize - 2;
            //g.setFont(myFontB);

            drawLimitedString(g, value, x, y + fontSize + 3, width, 1);
            g.setFont(f);
        }
    }
    
	/**
	 * editOndoubleClick: permits edition of the element on double click
	 * by simply calling adddiagramReference
	 * @param frame
	 * @param _x
	 * @param _y
	 * @return boolean true
	 * */
    @Override
    public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
        addDiagramReference(frame);
        return true;
        // On the name ?
        /*oldValue = value;

          if ((displayText) && (_y <= (y + lineHeight))) {
          String text = getName() + ": ";
          if (hasFather()) {
          text = getTopLevelName() + " / " + text;
          }
          String s = (String)JOptionPane.showInputDialog(frame, text,
          "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
          null,
          getValue());

          if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
          //boolean b;
          if (!TAttribute.isAValidId(s, false, false)) {
          JOptionPane.showMessageDialog(frame,
          "Could not change the name of the Requirement: the new name is not a valid name",
          "Error",
          JOptionPane.INFORMATION_MESSAGE);
          return false;
          }

          if (!tdp.isRequirementNameUnique(s)) {
          JOptionPane.showMessageDialog(frame,
          "Could not change the name of the Requirement: the new name is already in use",
          "Error",
          JOptionPane.INFORMATION_MESSAGE);
          return false;
          }


          int size = graphics.getFontMetrics().stringWidth(s) + iconSize + 5;
          minDesiredWidth = Math.max(size, minWidth);
          if (minDesiredWidth != width) {
          newSizeForSon(null);
          }
          setValue(s);

          if (tdp.actionOnDoubleClick(this)) {
          return true;
          } else {
          JOptionPane.showMessageDialog(frame,
          "Could not change the name of the Requirement: this name is already in use",
          "Error",
          JOptionPane.INFORMATION_MESSAGE);
          setValue(oldValue);
          }
          }
          return false;
          }

          return editAttributes();*/

    }
    /**
     * Rescale: rescale the element with the help of a scaleFactor
     * From abstract class TGScalableComponent
     * @param scaleFactor
     * 
     * */
    // Issue #31 
//    @Override
//    public void rescale(double scaleFactor){
//        dlineHeight = (lineLength + dlineHeight) / oldScaleFactor * scaleFactor;
//        lineLength = (int)(dlineHeight);
//        dlineHeight = dlineHeight - lineLength;
//
//        minHeight = lineLength;
//
//        super.rescale(scaleFactor);
//    }
   
	/**
	 * isOnOnlyMe, Coming from Abstract Method From TGCWithInternalComponent (Abstract Class) 
	 * @param x1
	 * @param y1
	 * @return TGComponent or null
	 * */
    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height))
            return this;
        return null;
    }

	/**
	 * addActionToPopupMenu
	 * @param componentMenu
	 * @param menuAL
	 * @param x
	 * @param y
	 * */
    @Override
    public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {

        componentMenu.addSeparator();

        diagramReference = new JMenuItem("Add diagram reference");
        diagramReference.addActionListener(menuAL);

        componentMenu.add(diagramReference);
    }

	/**
	 * eventOnPopup
	 * @param e
	 * @return boolean true
	 * */
    @Override
    public boolean eventOnPopup(ActionEvent e) {
        //   String s = e.getActionCommand();

        if (e.getSource() == diagramReference) {
        	
        	// DB: Issue #53: Pass the frame on which the dialog should be centered
            addDiagramReference( getTDiagramPanel().getMGUI().frame );
        }

        return true;
    }

	/**
	 * addDiagramReference: permits to pop a new window???
	 * @param frame
	 * */
    public void addDiagramReference(JFrame frame) {
        JDialogManageListOfString jdmlos;
        Vector<String> ignored; // Must be built from non selected TMLTaskDiagramPanel or TMLCompPanel
        Vector<String> selected; // Must be built from refered diagrams that have not been

        ignored = new Vector<String>();
        selected = new Vector<String>();

        fillIgnoredSelectedFromInternalComponents(ignored, selected);

        jdmlos = new JDialogManageListOfString(frame, ignored, selected, "Selection of diagrams", value);
        //jdmlos.setSize(550, 350);
        GraphicLib.centerOnParent(jdmlos, 550, 350);
        jdmlos.setVisible( true );

        if (!jdmlos.hasBeenCancelled()) {

            ignored = jdmlos.getIgnored();
            selected = jdmlos.getSelected();

            String nameTmp = jdmlos.getName();
            if ((nameTmp != null) && (nameTmp.trim().length() > 0)) {
                value = nameTmp.trim();
            }


            //We reconstruct the list of internal components.
            AvatarMethodologyDiagramName dn;
            nbInternalTGComponent = 0;
            tgcomponent = null;
            int index = 0;
            int tmpx, tmpy;
            for (String s : selected) {
                tmpy = (int) (y + (40 * tdp.getZoom()) + (index * 15 * tdp.getZoom()));
                tmpx = (int) (AvatarMethodologyDiagramName.X_MARGIN * tdp.getZoom());
                dn = new AvatarMethodologyDiagramName(x + tmpx, tmpy, x + tmpx, x + tmpx, tmpy, tmpy, true, this, getTDiagramPanel());
                //makeValidationInfos(dn);
                dn.setValue(s);
                dn.setFather(this);
                addInternalComponent(dn, index);
                index++;
            }
        }


        // We must first remove from internalComponents the one that are now ignored
        /*AvatarMethodologyDiagramName dn;
          TGComponent t;
          int i;
          for(String s: ignored) {
          t = null;
          for(i=0; i<nbInternalTGComponent; i++) {
          dn =  (AvatarMethodologyDiagramName)tgcomponent[i];
          if (dn.getValue().compareTo(s) == 0) {
          t = dn;
          break;
          }
          }
          if (t != null) {
          removeInternalComponent(t);
          }
          }


          // We then add the ones that are newly selected
          int index;
          index = 0;
          int tmpx, tmpy;
          for(String s: selected) {
          if (!hasAvatarMethodologyDiagramName(s)) {
          tmpy = (int)(y + (40*tdp.getZoom()) + (index * 15 *tdp.getZoom()));
          tmpx = (int)(AvatarMethodologyDiagramName.X_MARGIN*tdp.getZoom());
          dn = new  AvatarMethodologyDiagramName(x+tmpx, tmpy, x+tmpx, x+tmpx, tmpy, tmpy, true, this, getTDiagramPanel());
          //makeValidationInfos(dn);
          dn.setValue(s);
          addInternalComponent(dn, index);

          }
          index ++;
          }*/
    }

    public abstract void makeValidationInfos(AvatarMethodologyDiagramName dn);

    /**
     * hasAvatarMethodologyDiagramName
     * @param s
     * @return boolean denoting if the string s is in the tgcomponent list
     * */
    public boolean hasAvatarMethodologyDiagramName(String s) {
        for (int i = 0; i < nbInternalTGComponent; i++)
            if (tgcomponent[i].getValue().compareTo(s) == 0)
                return true;
        return false;
    }

    /**
     * fillIgnoredSelectedFromInternalComponents
     * @param ignored
     * @param selected
     * */
    public void fillIgnoredSelectedFromInternalComponents(Vector<String> ignored, Vector<String>selected) {
        // Get from mgui the list of all diagrams with type depends from the subclass
        // If diagrams have the same name -> we do not see the difference

        //TURTLEPanel tp;
        Vector<TURTLEPanel> tabs = getTDiagramPanel().getMGUI().getTabs();
        for( final TURTLEPanel panel : tabs ) {
            //tp = (TURTLEPanel)o;
            if (isAValidPanelType(panel)) {
                ignored.add(getTDiagramPanel().getMGUI().getTitleAt(panel));
            }
        }

        Vector<String> newSelected = new Vector<String>();
        TGComponent tgc;
        //Consider internal components (text) to figure out the ones that are selected
        for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof AvatarMethodologyDiagramName) {
                newSelected.add(tgc.getValue());
            }
        }

        // Remove from selected the one that do not exist anymore
        Vector<String> toBeRemoved = new Vector<String>();
        boolean found;
        for(String s: newSelected) {
            found = false;
            for(String ss: ignored) {
                if (ss.compareTo(s) == 0) {
                    toBeRemoved.add(ss);
                    found = true;
                }
            }
            if (found) {
                selected.add(s);
            }
        }

        for(String s: toBeRemoved) {
            ignored.remove(s);
        }

    }

    public abstract boolean isAValidPanelType(TURTLEPanel panel);

    /**
     * Permits to know if tgc is an instance of AvatarMethodologyDiagramName
     * @param tgc
     * @return boolean
     * */
    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof AvatarMethodologyDiagramName;
    }

    /**
     * addSwallowedTGComponent
     * @param tgc
     * @param x
     * @param y
     * @return boolean
     * */
    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        tgc.setFather(this);
        addInternalComponent(tgc, 0);
        //tgc.setDrawingZone(true);
        return true;
    }

    /**
     * removeSwallowedTGComponent
     * @param tgc
     * */
    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public abstract boolean makeCall(String diagramName, int index);

    /**
     * openDiagram
     * @param tabName
     * @return boolean
     * */
    protected boolean openDiagram(String tabName) {
        if (!tdp.getMGUI().selectMainTab(tabName)) {
            TraceManager.addDev("Diagram removed?");
            return false;
        }
        return true;
    }

    /**
     * giveInformation
     * @param info
     * 
     * */
    protected void giveInformation(String info) {
        tdp.getMGUI().setStatusBarText(info);
    }

    /*public String getDiagramReferences() {
      return referenceElements;
      }*/

}
