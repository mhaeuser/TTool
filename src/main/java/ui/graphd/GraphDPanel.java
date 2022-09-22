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

//import java.awt.*;

import graph.AUTGraph;
import graph.AUTState;
import graph.AUTTransition;
import myutil.TraceManager;
import ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import org.w3c.dom.*;
//import org.xml.sax.*;
//import javax.xml.parsers.*;

/**
 * Class GraphDPanel
 * Panel used for drawing graphs
 * Creation: 24/06/2022
 *
 * @author Ludovic APVRILLE
 * @version 1.0 24/06/2022
 */
public class GraphDPanel extends TDiagramPanel implements TDPWithAttributes, Runnable {

    public int GOOD_CONNECTOR = 200;
    public int INC = 5;

    public GraphDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        //addComponent(400, 50, TGComponentManager.EBRDD_START_STATE, false);
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);*/
    }

    @Override
    public boolean actionOnDoubleClick(TGComponent tgc) {
        return true;
    }

    @Override
    public boolean actionOnAdd(TGComponent tgc) {
        return false;
    }

    @Override
    public boolean actionOnValueChanged(TGComponent tgc) {
        return false;
    }

    @Override
    public boolean actionOnRemove(TGComponent tgc) {
        return false;
    }

    @Override
    public String getXMLHead() {
        return "<GraphDPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + fontModifierParam() + " >";
    }

    @Override
    public String getXMLTail() {
        return "</GraphDPanel>";
    }

    @Override
    public String getXMLSelectedHead() {
        return "<GraphDPanelPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\""
                + heightSel + "\"" + zoomParam() + fontModifierParam() + ">";
    }

    @Override
    public String getXMLSelectedTail() {
        return "</GraphDPanelPanelCopy>";
    }

    @Override
    public String getXMLCloneHead() {
        return "<GraphDPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + zoomParam()
                + fontModifierParam() + ">";
    }

    @Override
    public String getXMLCloneTail() {
        return "</GraphDPanelCopy>";
    }

    public void makeGraphicalOptimizations() {
        // Segments of connector that mask components

        // Components over others

        // Position correctly guards of choice
    }


    @Override
    public boolean hasAutoConnect() {
        return false;
    }

    public void setConnectorsToFront() {
        TGComponent tgc;

        //

        Iterator iterator = componentList.listIterator();

        ArrayList<TGComponent> list = new ArrayList<TGComponent>();

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            if (!(tgc instanceof TGConnector)) {
                list.add(tgc);
            }
        }

        //
        for (TGComponent tgc1 : list) {
            //
            componentList.remove(tgc1);
            componentList.add(tgc1);
        }
    }


    // Making a graph from AUT
    public void makeGraphAUT(AUTGraph graph) {

        TraceManager.addDev("Making graph");

        graph.computeStates();

        HashMap<AUTState, GraphDVertex> map = new HashMap<>();

        int nbOfStates = graph.getNbOfStates();
        double angle = 360.0 / nbOfStates;
        int xc = 500;
        int yc = 500;
        double radius = 400.0;
        double initAngle = 0;
        int cptStates = 0;

        // Making states
        for (AUTState st : graph.getStates()) {
            GraphDVertex v1 = map.get(st);
            if (v1 == null) {
                v1 = makeVertex(angle, xc, yc, radius, initAngle, cptStates, st.id, st.info,
                        st.inTransitions.size() > 0, st.outTransitions.size() > 0);

                map.put(st, v1);
                cptStates++;
            }
        }


        // Making transitions
        for (AUTTransition tr : graph.getTransitions()) {


            // We consider the states of the transitions
            int index1 = tr.origin;
            AUTState st1 = graph.getState(index1);

            int index2 = tr.destination;
            AUTState st2 = graph.getState(index2);

            //TraceManager.addDev("Handling transactions: "+ tr.toString() + " with states " + st1.id + " -> " + st2.id);

            // We create them if they do not exist

            GraphDVertex v1 = map.get(st1);
            GraphDVertex v2 = map.get(st2);

            if ((v1 != null) && (v2 != null)) {
                //TraceManager.addDev("Vertex found");
                TGConnectingPoint p1 = v1.findFirstFreeTGConnectingPoint(true, true);
                TGConnectingPoint p2 = v2.findFirstFreeTGConnectingPoint(true, true);

                if ((p1 != null) && (p2 != null)) {
                    //TraceManager.addDev("Connecting points found");
                    GraphDEdgeConnector connector = new GraphDEdgeConnector(p1.getX(), p1.getY(), getMinX(), getMinY(), getMaxX(), getMaxY(),
                            true,
                            null, this, p1, p2, null);
                    connector.setValue(tr.transition);
                    //TraceManager.addDev("Adding transition with value=" + tr.transition + " label=" + tr.getLabel());
                    addBuiltConnector(connector);

                    if (tr.transition.startsWith("!")) {
                        connector.setCurrentColor(Color.blue);
                    }


                }
            }

        }

        //autoUpdate();


    }

    private GraphDVertex makeVertex(double angle, int xc, int yc, double radius, double initAngle, int cptStates, int index, String info, boolean in,
                                    boolean out) {
        // compute coordinates
        double angleC = initAngle + angle * cptStates;
        //TraceManager.addDev("angle = " + angle + " init=" + initAngle + " cptStates=" + cptStates + " -> angleC = " + angleC);
        int x = (int) (xc + Math.cos(Math.toRadians(angleC)) * radius);
        int y = (int) (xc + Math.sin(Math.toRadians(angleC)) * radius);

        //TraceManager.addDev("Making vertex with index=" + index + " at (" + x + "," + y + ")");

        GraphDVertex dv = new GraphDVertex(x, y, getMinX(), getMinY(), getMaxX(), getMaxY(), true, null, this);
        addBuiltComponent(dv);

        if (info == null) {
            dv.setValue("" + index);
        } else {
            info = info.trim();
            if (info.endsWith("(New)")) {
                dv.setAsNew(true);
                info = info.substring(0, info.length()-5);
            } else {
                dv.setAsNew(false);
            }
            dv.setValue("" + index + " / " + info);
        }



        // Setting colors
        // Green for out states only
        // Red for in states only
        if (in) {
            if (!out) {
                // deadlock
                dv.setCurrentColor(Color.RED);
            }
        } else {
            if (!in) {
                // init state
                dv.setCurrentColor(Color.GREEN);
            }
        }

        // Blue for comm states
        if (info.contains("Sending ")) {
            dv.setCurrentColor(ColorManager.TML_COMPOSITE_COMPONENT);
        } else if (info.contains("Receiving ")) {
            dv.setCurrentColor(ColorManager.BUS_BOX);
        }



        return dv;
    }



    @Override
    public void enhance() {
        autoUpdate();
    }

    public void autoUpdate() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        long minEnergy = Long.MAX_VALUE;
        boolean finished = false;
        int cptNoMin = 0;
        int nbOfSteps = 0;

        // Compute Connexion Map
        ArrayList<VVE> map = new ArrayList<>();

        Object[] comps = componentList.toArray();

        makeMap(map, comps);


        while (!finished) {

            long newEnergy = updateStep(map, comps);
            //TraceManager.addDev("min energy:" + minEnergy + " current:" + minEnergy);
            if (newEnergy < minEnergy) {
                minEnergy = newEnergy;
                cptNoMin = 0;
            } else {
                cptNoMin++;
                if (cptNoMin == 10) {
                    finished = true;
                }
            }
            if (nbOfSteps == 1000) {
                finished = true;
            }

            if (nbOfSteps % 1 == 0) {
                repaint();
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {

            }
        }

    }

    private long updateStep(ArrayList<VVE> map, Object[] comps) {
        // For all state couples, we see if they are connected or not
        // Connected -> they are getting closer to the transition length
        // Not connected -> getting further
        // States cannot get outside of min / max of panel

        long energy = 0;
        double max_distance = new Point(getMinX(), getMinY()).distance(new Point(getMaxX(), getMaxY()));

        for (int i = 0; i < comps.length - 1; i++) {
            TGComponent tgc1 = (TGComponent) comps[i];
            if (tgc1 instanceof GraphDVertex) {
                for (int j = i + 1; j < comps.length; j++) {
                    TGComponent tgc2 = (TGComponent) comps[j];



                    if (tgc2 instanceof GraphDVertex) {
                        ArrayList<VVE> connections = getConnections(tgc1, tgc2, map);
                        double energyTmp = 10;

                        int dist;
                        if (connections.isEmpty()) {
                            //TraceManager.addDev(tgc1.getValue() + " is NOT connected with " + tgc2.getValue());
                            dist = (int) max_distance / 2;
                            dist = (int)(GOOD_CONNECTOR * 1.5);
                            if (tgc1.distance(tgc2) < dist) {
                                adjust((GraphDVertex) tgc1, (GraphDVertex) tgc2, dist, INC);

                                energyTmp = Math.abs(dist - tgc1.distance(tgc2));
                                energy += energyTmp * energyTmp;
                            }
                        } else {
                            //TraceManager.addDev(tgc1.getValue() + " is connected with " + tgc2.getValue());
                            dist = GOOD_CONNECTOR;
                            adjust((GraphDVertex) tgc1, (GraphDVertex) tgc2, dist, INC);

                            energyTmp = Math.abs(dist - tgc1.distance(tgc2));
                            energy += energyTmp;
                        }

                        // We must also ensure that two states do not overlap
                        double overlap = tgc1.distance(tgc2);
                        if (overlap < tgc1.getWidth() * 4) {
                            makeFurther((GraphDVertex)tgc1, (GraphDVertex) tgc2, INC);
                            energyTmp = (tgc1.getWidth() * 4) - tgc1.distance(tgc2);
                            energy += energyTmp * energyTmp * energyTmp * energyTmp * energyTmp * energyTmp;
                        }

                        // Last, the closer to the center, the better it is

                        Point mid = new Point((getMinX() + getMaxX())/2, (getMinY() + getMaxY())/2);
                        energyTmp = new Point(tgc1.getX(), tgc1.getY()).distance(mid);
                        energy += energyTmp;
                        energyTmp = new Point(tgc2.getX(), tgc2.getY()).distance(mid);
                        energy += energyTmp;

                    }
                }
            }
        }

        return energy;
    }


    private void makeFurther(GraphDVertex tgc1, GraphDVertex tgc2, int INC) {
        double deltaX = (tgc2.getX() - tgc1.getX()) / 100.0;
        double deltaY = (tgc2.getY() - tgc1.getY()) / 100.0;

        int x = (int) (tgc1.getX() - INC * deltaX);
        x = Math.min(x, getMaxX());
        x = Math.max(x, getMinX());

        int y = (int) (tgc1.getY() - INC * deltaY);
        y = Math.min(y, getMaxY());
        y = Math.max(y, getMinY());
        tgc1.setMoveCd(x, y);

        x = (int) (tgc2.getX() + INC * deltaX);
        x = Math.min(x, getMaxX()-50);
        x = Math.max(x, getMinX()+50);

        y = (int) (tgc2.getY() + INC * deltaY);
        y = Math.min(y, getMaxY()-50);
        y = Math.max(y, getMinY()+50);

        tgc2.setMoveCd(x, y);


    }

    private void makeCloser(GraphDVertex tgc1, GraphDVertex tgc2, int INC) {
        double deltaX = (tgc2.getX() - tgc1.getX()) / 100.0;
        double deltaY = (tgc2.getY() - tgc1.getY()) / 100.0;

        int x = (int) (tgc1.getX() + INC * deltaX);
        x = Math.min(x, getMaxX());
        x = Math.max(x, getMinX());

        int y = (int) (tgc1.getY() + INC * deltaY);
        y = Math.min(y, getMaxY());
        y = Math.max(y, getMinY());

        tgc1.setMoveCd(x, y);

        x = (int) (tgc2.getX() - INC * deltaX);
        x = Math.min(x, getMaxX());
        x = Math.max(x, getMinX());

        y = (int) (tgc2.getY() - INC * deltaY);
        y = Math.min(y, getMaxY());
        y = Math.max(y, getMinY());

        tgc2.setMoveCd(x, y);
    }

    private void adjust(GraphDVertex tgc1, GraphDVertex tgc2, int DIST, int INC) {
        double distance = tgc1.distance(tgc2);

        if (distance > DIST + DIST * INC / 100) {
            makeCloser(tgc1, tgc2, INC);
        } else if (distance < DIST - DIST * INC / 100) {
            makeFurther(tgc1, tgc2, INC);
        }
    }


    private ArrayList<VVE> getConnections(TGComponent tgc1, TGComponent tgc2, ArrayList<VVE> map) {
        ArrayList<VVE> ret = new ArrayList<>();

        for (VVE vve : map) {
            if (vve.connected(tgc1, tgc2)) {
                ret.add(vve);
            }
        }

        return ret;
    }

    private void makeMap(ArrayList<VVE> map, Object[] comps) {
        for (int i = 0; i < comps.length - 1; i++) {
            TGComponent tgc1 = (TGComponent) comps[i];
            if (tgc1 instanceof GraphDVertex) {
                for (int j = i + 1; j < comps.length; j++) {
                    TGComponent tgc2 = (TGComponent) comps[j];

                    if (tgc2 instanceof GraphDVertex) {
                        for (TGComponent conn : componentList) {
                            if (conn instanceof GraphDEdgeConnector) {
                                GraphDEdgeConnector c = (GraphDEdgeConnector) conn;
                                if ((c.getTGConnectingPointP1().getFather() == tgc1) || (c.getTGConnectingPointP2().getFather() == tgc1)) {
                                    if ((c.getTGConnectingPointP1().getFather() == tgc2) || (c.getTGConnectingPointP2().getFather() == tgc2))
                                    map.add(new VVE((GraphDVertex) tgc1, (GraphDVertex) tgc2, c));
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private class VVE {
        public GraphDVertex v1;
        public GraphDVertex v2;
        public GraphDEdgeConnector c;

        public VVE(GraphDVertex _v1, GraphDVertex _v2, GraphDEdgeConnector _c) {
            v1 = _v1;
            v2 = _v2;
            c = _c;
        }

        public boolean connected(TGComponent c1, TGComponent c2) {
            return (c1 == v1) && (c2 == v2);
        }
    }


}
