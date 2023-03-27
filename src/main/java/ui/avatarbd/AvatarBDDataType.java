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


package ui.avatarbd;

import myutil.GenericTree;
import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogAvatarBlock;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;


/**
 * Class AvatarBDDataType
 * Data type. To be used in AVATAR Block Diagrams
 * Creation: 18/06/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.1 18/06/2010
 */
public class AvatarBDDataType extends TGCScalableWithInternalComponent implements GenericTree {
//    private int textY1 = 3;
    private String stereotype = "datatype";

    private int maxFontSize = 12;
    private int minFontSize = 4;
    private int currentFontSize = -1;
    private boolean displayText = true;
//    private int textX = 7;

    private int limitName = -1;
    private int limitAttr = -1;
    // private int limitMethod = -1;

    // Icon
    private int iconSize = 15;
    // private boolean iconIsDrawn = false;


    // TAttribute, AvatarMethod, AvatarSignal
    protected LinkedList<TAttribute> myAttributes;

    public String oldValue;

    public AvatarBDDataType(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        textY = 3;
        textX = 7;
        width = 250;
        height = 200;
        minWidth = 5;
        minHeight = 2;
        initScaling(250, 200);

        nbConnectingPoint = 0;
        connectingPoint = new TGConnectingPoint[0];

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        multieditable = true;
        removable = true;
        userResizable = true;

        name = tdp.findAvatarBDBlockName("DataType");
        setValue(name);
        oldValue = value;

        currentFontSize = maxFontSize;
        oldScaleFactor = tdp.getZoom();

        myImageIcon = IconManager.imgic700;

        myAttributes = new LinkedList<TAttribute>();

        actionOnAdd();
    }

    @Override
    public void internalDrawing(Graphics graph) {
        Font font = graph.getFont();
        this.internalDrawingAux(graph);
        graph.setFont(font);
    }

    private void internalDrawingAux(Graphics graph) {
        //Rectangle
        Color c = graph.getColor();
        graph.drawRect(this.x, this.y, this.width, this.height);
        graph.setColor(ColorManager.AVATAR_DATATYPE);
        //graph.setColor(BLOCK_TYPE_COLOR.get(typeStereotype));
        graph.fillRect(this.x + 1, this.y + 1, this.width - 1, this.height - 1);
        graph.setColor(c);

        //string
        int h = graph.getFontMetrics().getAscent() + graph.getFontMetrics().getLeading() + textY;

        if (h + graph.getFontMetrics().getDescent() + textY >= this.height)
            return;
        Font f = graph.getFont();



        String ster = "<<" + stereotype + ">>";
        //String ster = BLOCK_TYPE_STR.get(typeStereotype);
        int w = graph.getFontMetrics().stringWidth(ster);
        h = graph.getFontMetrics().getAscent() + graph.getFontMetrics().getLeading() + textY;
        graph.setFont(f.deriveFont(Font.BOLD));
        if (w + 2 * textX < this.width)
            drawSingleString(graph, ster, getCenter(graph, ster), this.y + h);
        h += graph.getFontMetrics().getHeight() + textY;
        w = graph.getFontMetrics().stringWidth(value);
        graph.setFont(f.deriveFont(Font.PLAIN));
        if (w + 2 * textX < this.width)
            drawSingleString(graph, this.value, getCenter(graph, value), this.y + h);

        //draw separator
        this.limitName = this.y + h;
        //TraceManager.addDev("limitName=" + limitName);

        if (h + textY >= this.height)
            return;
        h += graph.getFontMetrics().getDescent() + textY;
        //if (canTextGoInTheBox(graph, h, "line", 0));
        graph.drawLine(this.x, this.y + h, this.x + this.width, this.y + h);
        h += textY;

        //ICON

        if ((width > 2*scale(IconManager.iconSize)) && (height > 2*
                scale(IconManager.iconSize)))
            drawImageWithCheck(graph, IconManager.img5100, this.x + this.width - scale(IconManager.iconSize) - textX, this.y + textX);

        //Attributes
        if (tdp.areAttributesVisible()) {
            int attributeFontSize = f.getSize() * 5 / 6;
            graph.setFont(f.deriveFont((float) attributeFontSize));
            int step = graph.getFontMetrics().getHeight();
            h += textY;
            limitAttr = limitName;
            for (TAttribute attr : this.myAttributes) {
                h += step;
                if (h >= this.height - textX) {
                    this.limitAttr = this.y + this.height;
                    return;
                }

                // Get the string for this parameter
                String attrString = attr.toAvatarString();
                w = graph.getFontMetrics().stringWidth(attrString);
                if (w + 2 * textX < this.width) {
                    //graph.drawString(attrString, this.x + textX, this.y + h);
                    drawSingleString(graph, attrString, this.x + textX, this.y + h);
                }
            }
        }



    }



    
    /*@Override
    public void internalDrawing(Graphics g) {
        //Rectangle
    	Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        g.setColor(ColorManager.AVATAR_DATATYPE);
        g.fill3DRect(x + 1, y + 1, width - 1, height - 1, true);
        g.setColor(c);

    	//Strings
    	String ster = "<<" + stereotype + ">>";
        Font f = g.getFont();

        g.setFont(f.deriveFont(Font.BOLD));

        int strWidth = g.getFontMetrics().stringWidth(ster);
        int currentHeight = f.getSize();
        if ((strWidth < (2 * textX + width)) && (currentHeight < height)) {
            drawSingleString(g, ster, x + (width - strWidth) / 2, y + currentHeight);
        }

        g.setFont(f.deriveFont(Font.PLAIN));
        strWidth = g.getFontMetrics().stringWidth(value);
        currentHeight = 2 * f.getSize();
        if ((strWidth < (2 * textX + width)) && (currentHeight < height)) {
            drawSingleString(g, value, x + (width - strWidth) / 2, y + currentHeight);
        }
        limitName = y + currentHeight;
    
        currentHeight = currentHeight + 3;
        if (currentHeight < height) {
            g.drawLine(x, y + currentHeight, x + width, y + currentHeight);
        }

        // Icon
        if ((width > 30) && (height > (iconSize + 2 * textX))) {
            g.drawImage(scale(IconManager.img5100), x + width - scale(iconSize) - textX, y + textX, null);
        }

        if (!this.tdp.areAttributesVisible())
            return;
        int attributeFontSize = f.getSize() * 5 / 6;
        g.setFont(f.deriveFont((float) attributeFontSize));
        int step = g.getFontMetrics().getHeight();
        h += textY;
        limitAttr = limitName;
        for (TAttribute attr : this.myAttributes) {
            h += step;
            if (h >= this.height - textX) {
                this.limitAttr = this.y + this.height;
                return;
            }

            // Get the string for this parameter
            String attrString = attr.toAvatarString();
            w = g.getFontMetrics().stringWidth(attrString);

            if (w + 2 * textX < this.width) {
                //graph.drawString(attrString, this.x + textX, this.y + h);
                drawSingleString(g, attrString, this.x + textX, this.y + h);

            }
        }
        }*/
    
    //}



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
    public boolean editOnDoubleClick(JFrame frame, int _x, int _y) {
        // On the name ?
        if ((((limitName == -1) && (displayText) && (_y <= (y + 2 * currentFontSize)))) || ((displayText) && (_y < limitName))) {
            oldValue = value;

            //String text = getName() + ": ";
            String s = (String) JOptionPane.showInputDialog(frame, "Datatype name",
                    "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                    null,
                    getValue());

            if ((s != null) && (s.length() > 0) && (!s.equals(oldValue))) {
                //boolean b;
                if (!TAttribute.isAValidId(s, false, false, false)) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not change the name of the data type: the new name is not a valid name",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }

                if (!tdp.isAvatarBlockNameUnique(s)) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not change the name of the data type: the new name is already in use",
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
                            "Could not change the name of the data type: this name is already in use",
                            "Error",
                            JOptionPane.INFORMATION_MESSAGE);
                    setValue(oldValue);
                }
            }
            return false;
        }

        // And so -> attributes!

        JDialogAvatarBlock jdab = new JDialogAvatarBlock(myAttributes, null, null, null, frame,
                "Setting attributes of " + value, "Attribute", 0,
                null, false, null);
        setJDialogOptions(jdab);
        //  jdab.setSize(650, 575);
        GraphicLib.centerOnParent(jdab, 650, 575);
        jdab.setVisible(true); // blocked until dialog has been closed
        //makeValue();
        //if (oldValue.equals(value)) {
        //return false;
        //}
        rescaled = true;
        return true;
    }

    protected void setJDialogOptions(JDialogAvatarBlock _jdab) {
        //jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
        _jdab.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
        _jdab.addType(TAttribute.getStringAvatarType(TAttribute.BOOLEAN), true);
        _jdab.addType(TAttribute.getStringAvatarType(TAttribute.INTEGER), true);
        _jdab.enableInitialValue(true);
        _jdab.enableRTLOTOSKeyword(false);
        _jdab.enableJavaKeyword(false);
        _jdab.enableUPPAALKeyword(false);
    }

    @Override
    public int getType() {
        return TGComponentManager.AVATARBD_DATATYPE;
    }

    @Override
    protected String translateExtraParam() {
        TAttribute a;

        //
        //value = "";
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for (int i = 0; i < myAttributes.size(); i++) {
            //
            a = myAttributes.get(i);
            //
            //value = value + a + "\n";
            sb.append("<Attribute access=\"");
            sb.append(a.getAccess());
            sb.append("\" var=\"");
            sb.append(a.getConstant());
            sb.append("\" id=\"");
            sb.append(a.getId());
            sb.append("\" value=\"");
            sb.append(a.getInitialValue());
            sb.append("\" type=\"");
            sb.append(a.getType());
            sb.append("\" typeOther=\"");
            sb.append(a.getTypeOther());
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int isConstant, access, type;
            String typeOther;
            String id, valueAtt;

            //
            //

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Attribute")) {
                                //
                                try {
                                    isConstant = Integer.decode(elt.getAttribute("var")).intValue();
                                } catch (Exception e) {
                                    isConstant = TAttribute.VARIABLE;
                                }
                                access = Integer.decode(elt.getAttribute("access")).intValue();
                                type = Integer.decode(elt.getAttribute("type")).intValue();
                                try {
                                    typeOther = elt.getAttribute("typeOther");
                                } catch (Exception e) {
                                    typeOther = "";
                                }
                                id = elt.getAttribute("id");

                                try {
                                    valueAtt = elt.getAttribute("value");
                                } catch (Exception e) {
                                    valueAtt = "";
                                }


                                if (valueAtt == null) {
                                    valueAtt = "";
                                }

                                if (valueAtt.equals("null")) {
                                    valueAtt = "";
                                }



                                if ((TAttribute.isAValidId(id, false, false, false))) {
                                    if ((valueAtt.length() == 0 ) || ((TAttribute.isAValidInitialValue(type, valueAtt)))) {
                                        if (type == TAttribute.NATURAL) {
                                            type = TAttribute.INTEGER;
                                        }
                                        TAttribute ta = new TAttribute(isConstant, access, id, valueAtt, type, typeOther);
                                        ta.isAvatar = true;
                                        myAttributes.add(ta);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public String getDataTypeName() {
        return value;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.AVATARBD_PORT_CONNECTOR;
    }

    public LinkedList<TAttribute> getAttributeList() {
        return myAttributes;
    }

    public void addAttribute(TAttribute ta) {
        myAttributes.add(ta);
    }

    @Override
    public String toString() {
        return "Data type: " + getValue();
    }

    @Override
    public int getChildCount() {
        //TraceManager.addDev("Counting childs!");
        return this.myAttributes.size();
    }

    @Override
    public Object getChild(int index) {

        return this.myAttributes.get(index);
    }

    @Override
    public int getIndexOfChild(Object child) {
        if (child instanceof TAttribute) {
            return this.myAttributes.indexOf(child);
        }

        return -1;
    }

}
