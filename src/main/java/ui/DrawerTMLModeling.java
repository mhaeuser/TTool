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
import org.apache.batik.anim.timing.Trace;
import tmltranslator.*;

import ui.tmlcompd.TMLCChannelOutPort;
import ui.tmlcompd.TMLCPortConnector;
import ui.tmlcompd.TMLCPrimitiveComponent;
import ui.tmlcompd.TMLCPrimitivePort;


import java.awt.*;
import java.util.HashMap;


import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Class DrawerTMLModeling
 * Class having a list of different constraints
 * Creation: 23/04/2010
 * @version 1.0 23/04/2010
 * @author Ludovic APVRILLE
 */
public class DrawerTMLModeling  {

    private final static int RADIUS = 400;
    private final static int XCENTER = 500;
    private final static int YCENTER = 450;


    private MainGUI mgui;
    private boolean hasError;
    private HashMap<TMLTask, TMLCPrimitiveComponent> taskMap;




    public DrawerTMLModeling(MainGUI _mgui) {
        mgui = _mgui;
    }

    private void check(boolean condition, String text) throws MalformedTMLDesignException {
        if (!condition) {
            throw new MalformedTMLDesignException(text);
        }
    }

    // Not thread-safe
    public  void drawTMLModelingPanel(TMLModeling tmlspec, TMLComponentDesignPanel panel) throws MalformedTMLDesignException{
        TraceManager.addDev("Drawing TML spec");

        taskMap = new HashMap<>();

        hasError = false;

        if (tmlspec == null) {
            hasError = true;
            return;
        }

        TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlspec);
        syntax.checkSyntax();
        if (syntax.hasErrors() > 0) {
            hasError = true;
            return;
        }

        TraceManager.addDev("Adding tasks");
        makeTasks(tmlspec, panel);
        panel.tmlctdp.repaint();
        TraceManager.addDev("Adding channels");
        makeChannels(tmlspec, panel);
        panel.tmlctdp.repaint();
        TraceManager.addDev("Adding events");
        makeEvents(tmlspec, panel);
        TraceManager.addDev("Adding requests");
        makeRequests(tmlspec, panel);

        TraceManager.addDev("All done");
        panel.tmlctdp.repaint();


    }

    private void makeTasks(TMLModeling tmlspec, TMLComponentDesignPanel panel) {
        int taskID = 0;
        int nbOfTasks =  tmlspec.getTasks().size();
        for(Object task: tmlspec.getTasks()) {
            if (task instanceof TMLTask) {
                addTask((TMLTask) task, taskID, nbOfTasks, panel, taskID == 0);
                taskID ++;
            }
        }
    }

    private void addTask(TMLTask task, int id, int nbOfTasks, TMLComponentDesignPanel panel, boolean setDiagramName) {
        int myX = (int)(XCENTER + RADIUS * cos(2*Math.PI/nbOfTasks*id));
        int myY = (int)(YCENTER + RADIUS * sin(2*Math.PI/nbOfTasks*id));
        int myType = TGComponentManager.TMLCTD_PCOMPONENT;

        TraceManager.addDev("myX=" + myX + " myY=" + myY + " Adding built component");


        TMLCPrimitiveComponent comp = new TMLCPrimitiveComponent(myX, myY, panel.tmlctdp.getMinX(),
                panel.tmlctdp.getMaxX(), panel.tmlctdp.getMinY(), panel.tmlctdp.getMaxY(),
                true, null, panel.tmlctdp, task.getTaskName());
        // Adding a built component
        panel.tmlctdp.addBuiltComponent(comp);

        TraceManager.addDev("Width=" + comp.getWidth());

        for(TMLAttribute attr: task.getAttributes()) {
            TAttribute ta;
            if (attr.getType().getType() == TMLType.NATURAL) {
                ta = new TAttribute(TAttribute.PRIVATE, attr.getName(), attr.getInitialValue(), TAttribute.NATURAL);
            } else if (attr.getType().getType() == TMLType.BOOLEAN) {
                ta = new TAttribute(TAttribute.PRIVATE, attr.getName(), attr.getInitialValue(), TAttribute.BOOLEAN);
            } else {
                ta = new TAttribute(TAttribute.PRIVATE, attr.getName(), attr.getInitialValue(), attr.getType().getTypeOther());
            }
            comp.getAttributeList().add(ta);
        }

        taskMap.put(task, comp);
        if (setDiagramName) {
            String longName = task.getName();
            int index = longName.indexOf("__");
            if (index > 0) {
                int indexTab = mgui.getIndexOfPanel(panel);
                TraceManager.addDev("Found panel at index:" + indexTab + " ; renaming to " + longName.substring(0, index));
                if (indexTab > -1) {
                   mgui.renameTab(indexTab, longName.substring(0, index));
                }
            }
        }

    }

    private void makeChannels(TMLModeling tmlspec, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {

        for(Object o: tmlspec.getChannels()) {
            if (o instanceof TMLChannel) {
                addChannel((TMLChannel) o, panel);
            }
        }
    }

    private void makeEvents(TMLModeling tmlspec, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {

        for(Object o: tmlspec.getEvents()) {
            if (o instanceof TMLEvent) {
                addEvent((TMLEvent) o, panel);
            }
        }
    }

    private void makeRequests(TMLModeling tmlspec, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {

        for(Object o: tmlspec.getRequests()) {
            if (o instanceof TMLRequest) {
                addRequest((TMLRequest) o, panel);
            }
        }
    }

    // Assumes 1 to 1 channel
    private void addChannel(TMLChannel chan, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {
        TraceManager.addDev("Adding channel " + chan.getName());

        TMLTask task1 = chan.getOriginTask();
        TMLTask task2 = chan.getDestinationTask();

        check(task1 != null, "Invalid origin task for channel " + chan.getName());
        check(task2 != null, "Invalid destination task for channel " + chan.getName());

        TMLCPrimitiveComponent c1 = taskMap.get(task1);
        TMLCPrimitiveComponent c2 = taskMap.get(task2);

        check(c1 != null, "No component corresponding to task " + task1.getName());
        check(c2 != null, "No component corresponding to task " + task2.getName());

        // Adding ports to tasks
        Point p = computePoint(c1, c2);
        TMLCChannelOutPort p1 = addPort(c1, chan.getName(), TMLCPrimitivePort.TML_PORT_CHANNEL, true, chan.isLossy(), chan.getLossPercentage(),
                chan.getMaxNbOfLoss(), chan.isInfinite(), chan.isBlockingAtOrigin(), chan.getMax(), chan.getSize(), null,
                panel, p );
        check(p1 != null, "No free port available for setting output event " + chan.getName());
        p = computePoint(c2, c1);
        TMLCChannelOutPort p2 = addPort(c2, chan.getName(), TMLCPrimitivePort.TML_PORT_CHANNEL, false, chan.isLossy(), chan.getLossPercentage(),
                chan.getMaxNbOfLoss(), chan.isInfinite(), chan.isBlockingAtDestination(), chan.getMax(), chan.getSize(), null,
                panel, p );
        check(p2 != null, "No free port available for setting input event " + chan.getName());

        // Connecting the ports
        addPortConnector(p1, p2, panel);

    }

    // Assumes 1 to 1 event
    private void addEvent(TMLEvent evt, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {
        TraceManager.addDev("Adding event " + evt.getName());

        TMLTask task1 = evt.getOriginTask();
        TMLTask task2 = evt.getDestinationTask();

        check(task1 != null, "Invalid origin task for event " + evt.getName());
        check(task2 != null, "Invalid destination task for event " + evt.getName());

        TMLCPrimitiveComponent c1 = taskMap.get(task1);
        TMLCPrimitiveComponent c2 = taskMap.get(task2);

        check(c1 != null, "No component corresponding to task " + task1.getName());
        check(c2 != null, "No component corresponding to task " + task2.getName());

        // Adding ports to tasks

        Point p = computePoint(c1, c2);
        TMLCChannelOutPort p1 = addPort(c1, evt.getName(), TMLCPrimitivePort.TML_PORT_EVENT, true, evt.isLossy(), evt.getLossPercentage(),
               evt.getMaxNbOfLoss(), evt.isInfinite(), evt.isBlockingAtOrigin(), evt.getMaxSize(), 1, evt.getParams(),
                panel, p );
        check(p1 != null, "No free port available for setting output event " + evt.getName());

        p = computePoint(c2, c1);
        TMLCChannelOutPort p2 = addPort(c2, evt.getName(), TMLCPrimitivePort.TML_PORT_EVENT, false, evt.isLossy(), evt.getLossPercentage(),
                       evt.getMaxNbOfLoss(), evt.isInfinite(), evt.isBlockingAtDestination(), evt.getMaxSize(), 1, evt.getParams(),
                panel, p );
        check(p2 != null, "No free port available for setting input event " + evt.getName());

        // Connecting the ports
        addPortConnector(p1, p2, panel);

    }

    // Assumes n to 1 requests
    private void addRequest(TMLRequest req, TMLComponentDesignPanel panel) throws MalformedTMLDesignException {
        TraceManager.addDev("Adding request " + req.getName());

        if (req.getOriginTasks().size() == 0) {
            throw new MalformedTMLDesignException("No origin request for request " + req.getName());
        }

        TMLCChannelOutPort p2 = null;
        TMLTask task2 = req.getDestinationTask();
        check(task2 != null, "Invalid destination task for channel " + req.getName());
        TMLCPrimitiveComponent c2 = taskMap.get(task2);
        check(c2 != null, "No component corresponding to task " + task2.getName());

        for(TMLTask task1: req.getOriginTasks()) {

            check(task1 != null, "Invalid origin task for request " + req.getName());
            TMLCPrimitiveComponent c1 = taskMap.get(task1);
            check(c1 != null, "No component corresponding to task " + task1.getName());

            // Adding ports to tasks
            Point p = computePoint(c1, c2);
            TMLCChannelOutPort p1 = addPort(c1, req.getName(), TMLCPrimitivePort.TML_PORT_REQUEST, true, req.isLossy(), req.getLossPercentage(),
                    req.getMaxNbOfLoss(), true, req.isBlockingAtOrigin(), 8, 1, req.getParams(),
                    panel, p);
            check(p1 != null, "No free port available for setting output request " + req.getName());

            p = computePoint(c2, c1);
            if (p2 == null) {
                p2 = addPort(c2, req.getName(), TMLCPrimitivePort.TML_PORT_REQUEST, false, req.isLossy(), req.getLossPercentage(),
                        req.getMaxNbOfLoss(), true, req.isBlockingAtDestination(), 8, 1, req.getParams(),
                        panel, p);
                check(p2 != null, "No free port available for setting input request " + req.getName());
            }

            // Connecting the ports
            addPortConnector(p1, p2, panel);
        }

    }

    private TMLCChannelOutPort addPort(TGComponent tgc, String name, int portType, boolean isOrigin, boolean isLossy,
                                       int lossPercentage, int maxNbOfLoss,
                                       boolean isInfinite, boolean isBlocking, int maxSamples, int widthSamples,
                                       java.util.Vector<tmltranslator.TMLType> params,
                                       TMLComponentDesignPanel panel, Point p) {

        if (p == null) {
            p = new Point (tgc.getX() + tgc.getWidth()/2, tgc.getY() + tgc.getHeight()/2);
        }



        TMLCChannelOutPort comp = new TMLCChannelOutPort(p.x, p.y, panel.tmlctdp.getMinX(),
                panel.tmlctdp.getMaxX(), panel.tmlctdp.getMinY(), panel.tmlctdp.getMaxY(),
                true, null, panel.tmlctdp);


        comp.setPortName(name);
        // Extra correct name, depending on whether it is the sender or the receiver
        String[] splitName = name.split("__");
        if (splitName.length > 3) {
            if (isOrigin) {
                comp.setCommName(splitName[1]);
            } else {
                comp.setCommName(splitName[3]);
            }
        } else if (splitName.length > 1){
            comp.setCommName(splitName[1]);
        } else {
            comp.setCommName(name);
        }

        comp.setPortType(portType);
        comp.setIsOrigin(isOrigin);
        comp.setLoss(isLossy, maxNbOfLoss, lossPercentage);
        comp.setMainSemantics(isInfinite, isBlocking, maxSamples, widthSamples);
        if (params != null) {
            int cpt = 0;
            for(TMLType type: params) {
                TType t = new TType(type.getType(), type.getTypeOther());
                comp.setParam(cpt, t);
                cpt ++;
            }
        }

        if (tgc instanceof SwallowTGComponent) {
            ((SwallowTGComponent)tgc).addSwallowedTGComponent(comp, p.x, p.y);
            return comp;
        }

        return null;
    }

    private void addPortConnector(TMLCChannelOutPort p1, TMLCChannelOutPort p2, TMLComponentDesignPanel panel) {
        int myX = p1.getX() + p1.getWidth() / 2;
        int myY = p1.getY() + p1.getHeight() / 2;
        p1.getTGConnectingPointAtIndex(0).setFree(false);
        p2.getTGConnectingPointAtIndex(0).setFree(false);
        TMLCPortConnector conn = new TMLCPortConnector(myX, myY, panel.tmlctdp.getMinX(),
                panel.tmlctdp.getMaxX(), panel.tmlctdp.getMinY(), panel.tmlctdp.getMaxY(),
                true, null, panel.tmlctdp, p1.getTGConnectingPointAtIndex(0), p2.getTGConnectingPointAtIndex(0), null);

        panel.tmlctdp.addBuiltConnector(conn);
    }

    private Point computePoint(TGComponent c1, TGComponent c2) {
        int randomX1 = (int)(Math.random() * Math.min(c1.getWidth(), c2.getWidth()));
        int randomY1 = (int)(Math.random() * Math.min(c1.getHeight(), c2.getHeight()));
        int randomX2 = (int)(Math.random() * Math.min(c1.getWidth(), c2.getWidth()));
        int randomY2 = (int)(Math.random() * Math.min(c1.getHeight(), c2.getHeight()));
        //TraceManager.addDev("Random X1= " + randomX1 + " Y1 = " + randomY1);
        return GraphicLib.intersectionRectangleSegment(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight(),
                c1.getX()+randomX1, c1.getY() + randomY1,
                c2.getX()+randomX2, c2.getY() + randomY2);
    }





}