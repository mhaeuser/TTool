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




package ui.ftd;

import myutil.GraphicLib;
import myutil.NameChecker;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;


/**
 * Class FTDBlock
 * Node. To be used in Fault Tree Diagrams
 * Creation: 14/12/2017
 * @version 1.1 14/12/2017
 * @author Ludovic APVRILLE
 */
public class FTDBlock extends TGCScalableWithInternalComponent implements SwallowTGComponent,
        NameChecker.NameStartWithUpperCase, NameChecker.NameTakenFromValue {
//    private int textY1 = 3;
    private String stereotype = "block";

    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
//    private int textX = 1;


    public FTDBlock(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        textY = 3;
        textX = 1;
        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;
        initScaling(250, 200);
        
        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.0);
        connectingPoint[1] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.5, 0.0);
        connectingPoint[2] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.0);
        connectingPoint[3] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.5);
        connectingPoint[4] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.5);
        connectingPoint[5] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 1.0);
        connectingPoint[6] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.5, 1.0);
        connectingPoint[7] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 1.0);

        connectingPoint[8] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.25, 0.0);
        connectingPoint[9] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.75, 0.0);
        connectingPoint[10] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.25);
        connectingPoint[11] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.25);
        connectingPoint[12] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.0, 0.75);
        connectingPoint[13] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 1.0, 0.75);
        connectingPoint[14] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.25, 1.0);
        connectingPoint[15] = new FTDCompositionConnectingPoint(this, 0, 0, true, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findBlockName("Block");
        setValue(name);

        currentFontSize = maxFontSize;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic700;
    }
    
    public void internalDrawing(Graphics g)
    {
    	//Rectangle
    	Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        g.setColor(ColorManager.FTD_BLOCK);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);
        
    	//Strings
        String ster = "<<" + stereotype + ">>";
        Font f = g.getFont();
        Font f0 = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));

        int w = g.getFontMetrics().stringWidth(ster);
        int h =  f.getSize();
        if ((w < (2*textX + width)) && (h < height)) {
            g.drawString(ster, x + (width - w)/2, y +h);
        }
        g.setFont(f0);
        w  = g.getFontMetrics().stringWidth(value);
        h = 2 * f.getSize();
        if ((w < (2*textX + width)) && (h < height)) {
            drawSingleString(g, value, x + (width - w)/2, y + h);
        }
        
        // Icon
        g.drawImage(scale(IconManager.imgic1100.getImage()), x + scale(4), y + scale(4), null);
        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
    
    //@Override
    public void internalDrawin(Graphics g) {
        String ster = "<<" + stereotype + ">>";
        Font f = g.getFont();
        Font fold = f;

        //

        if ((rescaled) && (!tdp.isScaled())) {

            if (currentFontSize == -1) {
                currentFontSize = f.getSize();
            }
            rescaled = false;
            // Must set the font size ..
            // Find the biggest font not greater than max_font size
            // By Increment of 1
            // Or decrement of 1
            // If font is less than 4, no text is displayed

            int maxCurrentFontSize = Math.max(0, Math.min(height, maxFontSize));
            int w0, w1, w2;
            f = f.deriveFont((float)maxCurrentFontSize);
            g.setFont(f);
            //
            while(maxCurrentFontSize > (minFontSize-1)) {
                w0 = g.getFontMetrics().stringWidth(value);
                w1 = g.getFontMetrics().stringWidth(ster);
                w2 = Math.min(w0, w1);
                if (w2 < (width - (2*textX))) {
                    break;
                }
                maxCurrentFontSize --;
                f = f.deriveFont((float)maxCurrentFontSize);
                g.setFont(f);
            }
            currentFontSize = maxCurrentFontSize;

            if(currentFontSize <minFontSize) {
                displayText = false;
            } else {
                displayText = true;
                f = f.deriveFont((float)currentFontSize);
                g.setFont(f);
            }

        }

        //

        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        g.setColor(ColorManager.FTD_BLOCK);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        int w;
        if (displayText) {
            f = f.deriveFont((float)currentFontSize);
            Font f0 = g.getFont();
            g.setFont(f.deriveFont(Font.BOLD));

            w = g.getFontMetrics().stringWidth(ster);
            int h =  currentFontSize + (int)(textY * tdp.getZoom());
            if ((w < (2*textX + width)) && (h < height)) {
                drawSingleString(g, ster, x + (width - w)/2, y +h);
            }
            g.setFont(f0);
            w  = g.getFontMetrics().stringWidth(value);
            h = 2* (currentFontSize + (int)(textY * tdp.getZoom()));
            if ((w < (2*textX + width)) && (h < height)) {
                drawSingleString(g, value, x + (width - w)/2, y + h);
            }
        }

        g.setFont(fold);
        /*int w  = g.getFontMetrics().stringWidth(ster);
          Font f = g.getFont();
          g.setFont(f.deriveFont(Font.BOLD));
          g.drawString(ster, x + (width - w)/2, y + textY1);
          g.setFont(f);
          w  = g.getFontMetrics().stringWidth(value);
          g.drawString(value, x + (width - w)/2, y + textY2);*/

        // Icon
        //g.drawImage(IconManager.imgic1100.getImage(), x + 4, y + 4, null);
        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }
    
    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    public String getStereotype() {
        return stereotype;

    }

    public String getNodeName() {
        return name;
    }
    
    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;

        //String text = getName() + ": ";
        String s = (String)JOptionPane.showInputDialog(frame, "Block name",
                                                       "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                                                       null,
                                                       getValue());

        if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
            //boolean b;
            if (!TAttribute.isAValidId(s, false, false, false)) {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the Block: the new name is not a valid name",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            if (!tdp.isBlockNameUnique(s)) {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the Block: the new name is already in use",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            setValue(s);
            recalculateSize();



            if (tdp.actionOnDoubleClick(this)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(frame,
                                              "Could not change the name of the Block: this name is already in use",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
                setValue(oldValue);
            }
        }
        return false;
    }

    
    @Override
    public int getType() {
        return TGComponentManager.FTD_BLOCK;
    }
    
    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof FTDFault;

    }
    
    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
        if (tgc instanceof FTDFault) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }

        if (tgc instanceof FTDConstraint) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }

        if (tgc instanceof FTDCountermeasure) {
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
            addInternalComponent(tgc, 0);
            return true;
        }

        return false;
    }
    
    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }


    public Vector<FTDFault> getFaultList() {
        Vector<FTDFault> v = new Vector<>();
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof FTDFault) {
                v.add((FTDFault) tgcomponent[i]);
            }
        }
        return v;
    }
    
    @Override
    public void hasBeenResized() {
        for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof FTDFault) {
                tgcomponent[i].resizeWithFather();
            }
        }

    }
    
    @Override
    public int getDefaultConnector() {
        return TGComponentManager.FTD_COMPOSITION_CONNECTOR;
    }

}
