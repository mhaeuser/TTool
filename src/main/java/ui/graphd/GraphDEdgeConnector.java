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


package ui.graphd;

import avatartranslator.ElementWithNew;
import myutil.Conversion;
import myutil.GraphicLib;
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.Vector;

/**
 * Class GraphDEdgeConnector
 * Connector to be used in graphs to connect vertices
 * Creation: 24/06/2022
 *
 * @author Ludovic APVRILLE
 * @version 1.0 24/06/2022
 */
public class GraphDEdgeConnector extends TGConnector implements ColorCustomizable, SpecificActionAfterAdd {


    protected int arrowLength = 10;
    protected int widthValue, heightValue;
    protected int xWidthValue = 0;
    protected int yWidthValue = 0;

    private boolean circle = false;
    private int xCenter, yCenter;
    private int radiusCircle;

    private int xi1, yi1, xi2, yi2;

    private float x3, y3;
    private double randomA = 0.0;
    private double randomB = 0.0;
    private Path2D.Double path;
    private QuadCurve2D curve;


    public GraphDEdgeConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp,
                               TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
        super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);

        value = "info";
        editable = true;

    }

    @Override
    protected void internalDrawing(Graphics g) {

        if (p1.getX() == p2.getX() && (p2.getY() == p1.getY())) {
            Color c = g.getColor();
            drawLastSegment(g, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if ((getState() == TGState.POINTER_ON_ME) || (getState() == TGState.POINTED) || (getState() == TGState.MOVING)) {
                ColorManager.setColor(g, state, 0, isEnabled());
            } else {
                g.setColor(getCurrentColor());
            }
            g.setColor(c);
        } else {
            super.internalDrawing(g);
        }


    }

    @Override
    protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {

        Color tmpColor = g.getColor();

        if (p1.getFather() != null && p1.getFather() instanceof ElementWithNew) {
            if (((ElementWithNew)(p1.getFather())).isNew()) {
                g.setColor(ColorManager.NEW);
            }
        }

        if (p2.getFather() != null && p2.getFather() instanceof ElementWithNew) {
            if (((ElementWithNew)(p2.getFather())).isNew()) {
                g.setColor(ColorManager.NEW);
            }

        }

        try {


            // Connecting points have the same location
            // We draw a circle with an arrow
            if ((x1 == x2) && (y1 == y2)) {

                if (randomA == 0) {
                    randomA = Math.toRadians(Math.random() * 360 - 180);
                }

                if (randomB == 0) {
                    randomB = Math.toRadians(Math.random() * 50 + 50);
                }

                circle = true;



                //TraceManager.addDev("RandomA = " + Math.toDegrees(randomA));

                radiusCircle = p1.getFather().getWidth() / 2;

                xCenter = (int) (x1 + Math.cos(randomA) * radiusCircle);
                yCenter = (int) (y1 + Math.sin(randomA) * radiusCircle);

                g.drawOval(xCenter - 3, yCenter - 3, 6, 6);

                // We have to compute intermediate points
                // point i 1: obtained from (x1, y1) to center, continuing on radius * 3
                xi1 = x1 + (xCenter - x1) * 6;
                yi1 = y1 + (yCenter - y1) * 6;

                xi2 = (int) (xCenter + (xi1 - xCenter) * Math.cos(randomB) - (yi1 - yCenter) * Math.sin(randomB));
                yi2 = (int) (yCenter + (xi1 - xCenter) * Math.sin(randomB) + (yi1 - yCenter) * Math.cos(randomB));

                g.drawOval(xCenter - 3, yCenter - 3, 6, 6);

                GraphicLib.arrowWithLine(g, 1, 1, 10, xi1, yi1, xCenter, yCenter, false);
                g.drawLine(xi1, yi1, xi2, yi2);
                g.drawLine(xCenter, yCenter, xi2, yi2);

                widthValue = g.getFontMetrics().stringWidth(value);
                heightValue = g.getFontMetrics().getHeight();
                xWidthValue = (xi1 + xi2) / 2 - widthValue / 2;
                yWidthValue = (yi1 + yi2) / 2 - g.getFontMetrics().getDescent();
                g.drawString(value, xWidthValue, (yi1 + yi2) / 2 - 5);


            } else {
                circle = false;
                // Updating x1, y1, x2, y2 -> point on the circle
                Point px1 = new Point(x1, y1);
                Point px2 = new Point(x2, y2);


                double radius = p2.getFather().getWidth() / 2;
                Point2D center = new Point(p2.getFather().getX() + p2.getFather().getWidth() / 2,
                        p2.getFather().getY() + p2.getFather().getWidth() / 2);

                Point2D p2p = GraphicLib.intersectionP(px1, px2, center, radius, true);
                if (p2p != null) {
                    x2 = (int) (p2p.getX());
                    y2 = (int) (p2p.getY());
                }


                GraphicLib.arrowWithLine(g, 1, 1, 10, x1, y1, x2, y2, false);


                if (!tdp.isScaled()) {
                    widthValue = g.getFontMetrics().stringWidth(value);
                    heightValue = g.getFontMetrics().getHeight();
                    xWidthValue = (x1 + x2) / 2 - widthValue / 2;
                    yWidthValue = (y1 + y2) / 2 - g.getFontMetrics().getDescent();
                    g.drawString(value, (x1 + x2) / 2 - widthValue / 2, (y1 + y2) / 2 - 5);
                }


            }


        } catch (Exception e) {
            GraphicLib.arrowWithLine(g, 1, 1, 10, x1, y1, x2, y2, false);
        }

       g.setColor(tmpColor);
    }

    public void specificActionAfterAdd() {
        tdp.bringToBack(this);
    }


    public boolean editOnDoubleClick(JFrame frame) {
        //
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String) JOptionPane.showInputDialog(frame, text,
                "setting message name", JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
                null,
                getValue());
        if (s != null) {
            setValue(s);
            return true;
        }
        return false;
    }

    public TGComponent extraIsOnOnlyMe(int x1, int y1) {

        /*if (curve != null) {
            double distance = GraphicLib.distance(new Point(x1, y1), curve, curve.getFlatness());
            if (distance < 5) {
                return this;
            }
        }*/

        if (path != null) {
            /*Point p1 = null, p2 = null;
            final PathIterator pathIterator = path.getPathIterator(null);
            final double[] seg = new double[6];

            TraceManager.addDev("Testing path");

            while(!pathIterator.isDone()) {
                pathIterator.currentSegment(seg);
                TraceManager.addDev("Segment:" + GraphicLib.segmentValue(seg));
                if (p1 == null) {
                    p1 = new Point((int) seg[0], (int) seg[1]);
                } else {
                    p2 = new Point((int) seg[0], (int) seg[1]);
                    if ((int)(Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x, y)) < distanceSelected) {
                        return this;
                    }
                    p2 = p1;
                }
                pathIterator.next();
            }*/

            //

        }

        if (circle) {
            if ((int) (Line2D.ptSegDistSq(xCenter, yCenter, xi1, yi1, x1, y1)) < distanceSelected) {
                return this;
            }
            if ((int) (Line2D.ptSegDistSq(xCenter, yCenter, xi2, yi2, x1, y1)) < distanceSelected) {
                return this;
            }
            if ((int) (Line2D.ptSegDistSq(xi2, yi2, xi1, yi1, x1, y1)) < distanceSelected) {
                return this;
            }


        }

        //
        if (GraphicLib.isInRectangle(x1, y1, xWidthValue, yWidthValue - heightValue,
                widthValue, heightValue)) {
            return this;
        }


        return null;
    }


    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<trans randomA=\"");
        sb.append(randomA);
        sb.append("\" randomB=\"");
        sb.append(randomB);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {


        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String typeOther;
            String id, valueAtt;
            String tmp;



            //TraceManager.addDev("LEP Begin Block  = " + this + " trace=");
            //Thread.currentThread().dumpStack();

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

                            if (elt.getTagName().equals("trans")) {
                                randomA = Double.valueOf(elt.getAttribute("randomA"));
                                randomB = Double.valueOf(elt.getAttribute("randomB"));
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }


    @Override
    public int getType() {
        return TGComponentManager.GRAPHD_EDGE_CONNECTOR;
    }

    // Color management
    public Color getMainColor() {
        return Color.BLACK;
    }

    public ImageIcon getImageIcon() {
        return IconManager.imgic202;
    }

}







