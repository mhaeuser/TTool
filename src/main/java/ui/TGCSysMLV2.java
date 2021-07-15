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

package ui;

import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.sysmlv2.JFrameSysMLV2Text;
import ui.util.IconManager;


import javax.swing.*;
import java.awt.*;

/**
 * Class TGCSysMLV2
 * Generic text box for displaying notes
 * Creation: 13/07/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 13/07/2021
 */
public class TGCSysMLV2 extends TGCScalableWithoutInternalComponent implements ColorCustomizable {

    protected String[] lines;

    private static final int MARGIN_X = 5;
    private static final int MARGIN_Y = 5;

    protected Graphics myg;


    private int currentFontSize = -1;

    protected Graphics graphics;


    public TGCSysMLV2(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
//        this.width = 150;
//        this.height = 30;

        width = 150;
        height = 150;
        minWidth = 20;
        minHeight = 20;

        initScaling( 150, 150 );

        //oldScaleFactor = tdp.getZoom();

        this.nbConnectingPoint = 0;
        int len = this.makeTGConnectingPointsComment(16);
        int decw = 0;
        int dech = 0;
        for (int i = 0; i < 2; i++) {
            connectingPoint[len] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.0 + dech);
            connectingPoint[len + 1] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 0.0 + dech);
            connectingPoint[len + 2] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.0 + dech);
            connectingPoint[len + 3] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 0.5 + dech);
            connectingPoint[len + 4] = new TGConnectingPointComment(this, 0, 0, true, true, 1.0 + decw, 0.5 + dech);
            connectingPoint[len + 5] = new TGConnectingPointComment(this, 0, 0, true, true, 0.0 + decw, 1.0 + dech);
            connectingPoint[len + 6] = new TGConnectingPointComment(this, 0, 0, true, true, 0.5 + decw, 1.0 + dech);
            connectingPoint[len + 7] = new TGConnectingPointComment(this, 0, 0, true, true, 0.9 + decw, 1.0 + dech);
            len += 8;
        }

        this.moveable = true;
        this.editable = true;
        this.removable = true;
        this.userResizable = true;

        this.currentFontSize = tdp.getFontSize();

        this.name = "SysMLV2 Text";
        this.value = "";

        rescaled = true;

        this.myImageIcon = IconManager.imgic320;
    }


    @Override
    protected void internalDrawing(Graphics g) {
        Font f = g.getFont();


        if (((rescaled) && (!tdp.isScaled()))) {
            currentFontSize = tdp.getFontSize();

            if (rescaled) {
                rescaled = false;
            }
        }

        Color c = g.getColor();

        g.setColor(getCurrentColor());
        g.fillRect(x, y, width, height);
        g.setColor(c);
        g.drawRect(x, y, width, height);

        int xStr = x, yStr = y;
        Font f0 = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        int w = g.getFontMetrics().stringWidth(name);

        // Issue #31
        final int marginX = scale( MARGIN_X );
        final int marginY = scale( MARGIN_Y );

        xStr += width/2 - w/2;
        yStr += marginY + currentFontSize;

        g.drawString(name, xStr, yStr);
        g.setFont(f0);
        g.setColor(c);
    }

    public TGComponent isOnMe(int x1, int y1) {

        if (GraphicLib.isInRectangle(x1, y1, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;

        JFrameSysMLV2Text jfsmlv2t = new JFrameSysMLV2Text("Edit " + name,  this, lines, IconManager.imgic8);
        jfsmlv2t.setIconImage(IconManager.img8);
        GraphicLib.centerOnParent(jfsmlv2t, 740, 800);
        jfsmlv2t.setVisible(true);
        return true;
    }


    @Override
    public int getType() {
        return TGComponentManager.SYSMLV2_TEXT;
    }

    @Override
    protected String translateExtraParam() {
        if (lines == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer("<extraparam>\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append("<Line value=\"");
            sb.append(GTURTLEModeling.transformString(lines[i]));
            sb.append("\" />\n");
        }
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        lines = null;
        String line = "";
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;

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
                            if (elt.getTagName().equals("Line")) {
                                //
                                s = elt.getAttribute("value");
                                if (s.equals("null")) {
                                    s = "";
                                }
                                //TraceManager.addDev("Adding " + s + " in decoded format:" + GTURTLEModeling.decodeString(s));
                                line += GTURTLEModeling.decodeString(s) + "\n";
                            }
                        }
                    }
                }
            }
            setLines(line);

        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
    }

    public void setLines(String line) {
        lines = line.split(("\n"));
    }

    // Color management
    public Color getMainColor() {
        return Color.LIGHT_GRAY;
    }
}
