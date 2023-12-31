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

package ui.avatarsmd;

import avatartranslator.AvatarRandom;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTMLADRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Class AvatarSMDRandom
 * Random operator of an AVATAR State Machine diagram
 * Creation: 12/07/2010
 * @version 1.0 12/07/2010
 * @author Ludovic APVRILLE
 */
public class AvatarSMDRandom extends  AvatarSMDBasicCanBeDisabledComponent /* Issue #69 AvatarSMDBasicComponent*/ implements EmbeddedComment, BasicErrorHighlight {
    protected int lineLength = 5;
//    protected int textX =  5;
//    protected int textY =  15;
    protected int arc = 5;
    protected String valueRandom = "";
    protected String variable;
    protected String minValue;
    protected String maxValue;
    protected String extraAttribute1;
    protected String extraAttribute2;
    protected int functionId;

    protected int stateOfError = 0; // Not yet checked

    public AvatarSMDRandom(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 30;
        height = 20;
        minWidth = 30;
        textX = 5;
        textY = 15;
        initScaling(30,20);

        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new AvatarSMDConnectingPoint(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new AvatarSMDConnectingPoint(this, 0, lineLength, false, true, 0.5, 1.0);

        moveable = true;
        editable = true;
        removable = true;

        variable = "x";
        minValue = "0";
        maxValue = "10";
        functionId = 0;
        extraAttribute1 = "";
        extraAttribute2 = "";

        myImageIcon = IconManager.imgic912;
    }

    private void makeValue() {
        valueRandom = variable + " = RANDOM" + functionId + "[" + minValue + ", " + maxValue + "] "
                + AvatarRandom.DISTRIBUTION_LAWS_SHORT[functionId].trim();
    }

	public void setVariable(String v){
		variable=v;
        makeValue();
	}

    public void setMaxValue(String _maxValue) {
		maxValue = _maxValue;
        makeValue();
	}

    public void setMinValue(String _minValue) {
		minValue = _minValue;
        makeValue();
	}

	@Override
    public void internalDrawing(Graphics g) {
        if (valueRandom.length() == 0) {
            makeValue();
        }

        int w  = g.getFontMetrics().stringWidth(valueRandom);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width/2 - w1/2, y);
            width = w1;
            //updateConnectingPoints();
        }

        Color c = g.getColor();

        if (stateOfError > 0)  {
            switch(stateOfError) {
            case ErrorHighlight.OK:
                g.setColor(ColorManager.RANDOM);
                break;
            default:
                g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
            }
            g.fillRoundRect(x, y, width, height, arc, arc);
            g.setColor(c);
        }

        g.drawRoundRect(x, y, width, height, arc, arc);

        // Issue #69
    	if ( !isEnabled() && isContainedInEnabledState() ) {
	    	g.setColor( ColorManager.DISABLED_FILLING );
	    	g.fillRoundRect(x, y, width, height, arc, arc);
	    	g.setColor( c );
    	}

    	g.drawLine(x+(width/2), y, x+(width/2), y - lineLength);
        g.drawLine(x+(width/2), y+height, x+(width/2), y + lineLength + height);
        //g.drawLine(x+width, y+height/2, x+width +lineLength, y+height/2);
//			Issue #31:
//        g.drawString(valueRandom, x + (width - w) / 2 , y + textY);
    	drawSingleString(g, valueRandom, getCenter(g, valueRandom), y + height - scale(5));
    }

	@Override
    public boolean editOnDoubleClick(JFrame frame) {
        boolean error = false;
        String errors = "";
        //int tmp;
        String tmpName;

        JDialogTMLADRandom dialog = new JDialogTMLADRandom(frame, "Setting RANDOM attributes",
                getVariable(), getMinValue(), getMaxValue(), getFunctionId(), getExtraAttribute1(), getExtraAttribute2());
        //dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog, 500, 450);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getVariable().length() != 0) {
            tmpName = dialog.getVariable();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false, false)) {
                error = true;
                errors += "Variable  ";
            } else {
                variable = tmpName;
            }
        }

        if (dialog.getMinValue().length() != 0) {
            minValue = dialog.getMinValue();
        } else {
            error = true;
            errors += "Min value  ";
        }

        if (dialog.getMaxValue().length() != 0) {
            maxValue = dialog.getMaxValue();
        } else {
            error = true;
            errors += "Max value  ";
        }

        functionId = dialog.getFunctionId();

        String formerExtra = extraAttribute1;
        extraAttribute1 = dialog.getExtraAttribute1();
        double extra1;
        try {
            extra1 = Double.parseDouble(extraAttribute1);
        } catch (Exception e) {
            extraAttribute1 = formerExtra;
        }

        formerExtra = extraAttribute2;
        extraAttribute2 = dialog.getExtraAttribute2();
        double extra2;
        try {
            extra2 = Double.parseDouble(extraAttribute2);
        } catch (Exception e) {
            extraAttribute2 = formerExtra;
        }


        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Invalid value for the following attributes: " + errors,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        makeValue();

        return true;
    }

	@Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+(width/2), y-lineLength, x+(width/2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        if ((int)(Line2D.ptSegDistSq(x+width, y+height/2, x+width +lineLength, y+height/2, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }

    public String getVariable() {
        return variable;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public int getFunctionId() {
        return functionId;
    }

    public String getExtraAttribute1() {
        return extraAttribute1;
    }

    public String getExtraAttribute2() {
        return extraAttribute2;
    }

	@Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data variable=\"");
        sb.append(getVariable());
        sb.append("\" minValue=\"");
        sb.append(getMinValue());
        sb.append("\" maxValue=\"");
        sb.append(getMaxValue());
        sb.append("\" functionId=\"");
        sb.append(getFunctionId());
        sb.append("\" extraAttribute1=\"");
        sb.append(getExtraAttribute1());
        sb.append("\" extraAttribute2=\"");
        sb.append(getExtraAttribute2());
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
        //    int k;
            String s;

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
                            if (elt.getTagName().equals("Data")) {
                                variable = elt.getAttribute("variable");
                                minValue = elt.getAttribute("minValue");
                                maxValue = elt.getAttribute("maxValue");
                                extraAttribute1 = elt.getAttribute("extraAttribute1");
                                if (extraAttribute1 == null) {
                                    extraAttribute1 = "";
                                }
                                extraAttribute2 = elt.getAttribute("extraAttribute2");
                                if (extraAttribute2 == null) {
                                    extraAttribute2 = "";
                                }
                                s = elt.getAttribute("functionId");
                                if (s != null) {
                                    try {
                                        functionId = Integer.parseInt(s);
                                    } catch (Exception e){
                                        functionId = AvatarRandom.RANDOM_UNIFORM_LAW;
                                    }
                                }
                                //
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
        makeValue();
    }

	@Override
    public int getType() {
        return TGComponentManager.AVATARSMD_RANDOM;
    }

	@Override
    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }
}
