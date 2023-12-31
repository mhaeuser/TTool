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

package ui.tmldd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogArchiKey;

import javax.swing.*;
import java.awt.*;

/**
   * Class TMLArchiKey
   * TML Key on Architecture diagram
   * Creation: 4/7/2016
   * @version 1.0 4/7/2016
   * @author Letitia LI, Ludovic APVRILLE
 */
public class TMLArchiKey extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes, TMLArchiSecurityInterface {

	// Issue #31
//    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
//    protected int textY2 =  35;
//    protected int space = 5;
//    protected int fileX = 20;
//    protected int fileY = 25;
//    protected int cran = 5;
	private static final int KEY_OFFSET_Y = 5;
	private static final int KEY_OFFSET_X = 20;

    //protected String oldValue = "";
    protected String referenceKey = "TMLKey";
    protected String typeName = "key";
    protected int priority = 5; // Between 0 and 10

    public TMLArchiKey(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
//        width = 75;
//        height = 40;
        minWidth = 75;
        minHeight = 30;//fileY + 5;
        textX = 5;
        textY = 15;
        
        initScaling( 75, 40 );

        nbConnectingPoint = 0;
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        value = "";
        referenceKey = "TMLKey";

        makeFullValue();

        //setPriority(((TMLArchiDiagramPanel)tdp).getPriority(getFullValue(), priority);

        myImageIcon = IconManager.imgic1118;
    }

    @Override
    public boolean isHidden() {
        //TraceManager.addDev("Archi task artifact: Am I hidden?" + getValue());
        boolean ret = false;
        if (tdp != null) {
            if (tdp instanceof TMLArchiDiagramPanel) {
                ret = !(((TMLArchiDiagramPanel)(tdp)).inCurrentView(this));

            }
        }
        //TraceManager.addDev("Hidden? -> " + ret);
        return ret;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }

    @Override
    protected void internalDrawing(Graphics g) {

    	// Issue #31
//    	if (oldValue.compareTo(value) != 0) {
//            setValue(value, g);
//        }
    	checkWidth( g );

        g.drawRect(x, y, width, height);
        Color c = g.getColor();
        g.setColor(ColorManager.MEMORY_BOX_2);
        g.fillRect(x+1, y+1, width-1, height-1);
        g.setColor(c);

        // Issue #31
        final int keyOffsetX = scale( KEY_OFFSET_X );
        final int keyOffsetY = scale( KEY_OFFSET_Y );
        final int shaftWidth = scale( 3 );
        
        // Key head
		g.fillOval(x+width-keyOffsetX, y+keyOffsetY, height/3, height/3);
		
		// Key shaft
		g.fillRect(x+width-keyOffsetY-keyOffsetX/2,y+keyOffsetY, shaftWidth/*3*/, height*3/4-keyOffsetY);
		
		// key teeth
		final int teethLength = scale( 8 );
		g.fillRect(x+width-keyOffsetY-keyOffsetX/2, y+height*3/4, teethLength/*8*/, shaftWidth/*3*/);
		g.fillRect(x+width-keyOffsetY-keyOffsetX/2, y+height*2/3, teethLength/*8*/, shaftWidth/*3*/);
/*
        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-fileX, y + space, x+width-space-cran, y+space);
        g.drawLine(x+width-space-cran, y+space, x+width-space, y+space + cran);
        g.drawLine(x+width-space, y+space + cran, x+width-space, y+space+fileY);
        g.drawLine(x+width-space, y+space+fileY, x+width-space-fileX, y+space+fileY);
        g.drawLine(x+width-space-cran, y+space, x+width-space-cran, y+space+cran);
        g.drawLine(x+width-space-cran, y+space+cran, x + width-space, y+space+cran);
*/
        // Issue #31
        //g.drawImage( scale( IconManager.img9 ), x+width-keyOffsetY-keyOffsetX + shaftWidth/*3*/, y + keyOffsetY + scale( 7 ), null );

        drawSingleString(g,value, x + textX , y + textY);

        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.ITALIC));
        // Issue #31
        drawSingleString(g,typeName, x + textX , y + textY + scale( 20 ) );
        g.setFont(f);
    }

    // Issue #31
    //private void setValue(String val/*, Graphics g*/) {
//    	oldValue = value;
//    	int w  = fileX + g.getFontMetrics().stringWidth(value) + textX;
//    	int w1 = Math.max(minWidth, w);
//
//    	//TraceManager.addDev("      Width=" + width + " w1=" + w1 + " value=" + value);
//    	if (w1 != width) {
//    		width = w1;
//    		resizeWithFather();
//    	}
    	//TraceManager.addDev("      Width=" + width + " w1=" + w1 + " value=" + value);
//    }

    @Override
    public void resizeWithFather() {
        if ((father != null) && (father instanceof TMLArchiMemoryNode)) {
            setCdRectangle(0, Math.max(0,father.getWidth() - getWidth()), 0, Math.max(father.getHeight() - getHeight(),0));
            setMoveCd(x, y);
        }
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        JDialogArchiKey dialog = new JDialogArchiKey(frame, "Setting channel artifact attributes", this);
        GraphicLib.centerOnParent(dialog, 700, 600);
        dialog.setVisible( true ); // blocked until dialog has been closed
        String tmp;
        boolean error=false;
        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getReferenceCommunicationName() == null) {
            return false;
        }

        if (dialog.getReferenceCommunicationName().length() != 0) {
            tmp = dialog.getReferenceCommunicationName();
            referenceKey = tmp;
        }

        if (dialog.getCommunicationName().length() != 0) {
            tmp = dialog.getCommunicationName();

            if (!TAttribute.isAValidId(tmp, false, false, false)) {
                error = true;
            } else {
                referenceKey = tmp;
            }
        }

        if (dialog.getTypeName().length() != 0) {
            typeName = dialog.getTypeName();
        }

        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Name is non-valid",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

        makeFullValue();

        return !error;
    }

    public void makeFullValue() {
        value = referenceKey;
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_KEY;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info value=\"" + value + "\" referenceKeyName=\"");
        sb.append(referenceKey);
        sb.append("\" priority=\"");
        sb.append(priority);
        sb.append("\" typeName=\"" + typeName);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
      //      int t1id;
            String svalue = null, sreferenceCommunication = null, stype = null;
            String prio = null;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                svalue = elt.getAttribute("value");
                                sreferenceCommunication = elt.getAttribute("referenceKeyName");
                                stype = elt.getAttribute("typeName");
                                prio = elt.getAttribute("priority");
                            }
                            if (svalue != null) {
                                value = svalue;
                            }
                            if (sreferenceCommunication != null) {
                                referenceKey = sreferenceCommunication;
                            }
                            if (stype != null){
                                typeName = stype;
                            }

                            if ((prio != null) && (prio.trim().length() > 0)) {
                                priority = Integer.decode(prio).intValue();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            
            throw new MalformedModelingException( e );
        }
        makeFullValue();
    }

    public DesignPanel getDesignPanel() {
        return tdp.getGUI().getDesignPanel(value);
    }

    public String getReferencKey() {
        return referenceKey;
    }

    public void setReferenceKey(String s){
        referenceKey=s;
    }
    public String getFullValue() {
        String tmp = getValue();
        tmp += " (" + getTypeName() + ")";
        return tmp;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getAttributes() {
        return "Priority = " + priority;
    }
}
