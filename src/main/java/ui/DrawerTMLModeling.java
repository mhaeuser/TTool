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

import myutil.TraceManager;
import tmltranslator.*;

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
    private final static int YCENTER = 350;


    private boolean hasError;
    private HashMap<TMLTask, TMLCPrimitiveComponent> taskMap;



    public DrawerTMLModeling() {

    }

    // Not thread-safe
    public  void drawTMLModelingPanel(TMLModeling tmlspec, TMLComponentDesignPanel panel) {
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

        makeTasks(tmlspec, panel);
        makeChannels(tmlspec, panel);

        panel.tmlctdp.repaint();


    }

    private boolean makeTasks(TMLModeling tmlspec, TMLComponentDesignPanel panel) {
        int taskID = 0;
        int nbOfTasks =  tmlspec.getTasks().size();
        for(Object task: tmlspec.getTasks()) {
            if (task instanceof TMLTask) {
                boolean ret = addTask((TMLTask) task, taskID, nbOfTasks, panel);
                if (!ret) return false;
                taskID ++;
            }
        }
        panel.tmlctdp.repaint();
        return true;
    }

    private boolean addTask(TMLTask task, int id, int nbOfTasks, TMLComponentDesignPanel panel) {
        int myX = (int)(XCENTER + RADIUS * cos(2*Math.PI/nbOfTasks*id));
        int myY = (int)(YCENTER + RADIUS * sin(2*Math.PI/nbOfTasks*id));
        int myType = TGComponentManager.TMLCTD_PCOMPONENT;

        TraceManager.addDev("myX=" + myX + " myY=" + myY);

        TGComponent tgc = TGComponentManager.addComponent(myX, myY, myType, panel.tmlctdp);
        if (tgc == null) {
            return false;
        }
        TMLCPrimitiveComponent comp = (TMLCPrimitiveComponent) tgc;
        tgc.setValue(task.getName());
        panel.tmlctdp.addBuiltComponent(tgc);

        TraceManager.addDev("Width=" + tgc.getWidth());
        //panel.tmlctdp.renamePrimitiveComponent(comp.getValue(), task.getTaskName());

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

        return true;
    }

    private boolean makeChannels(TMLModeling tmlspec, TMLComponentDesignPanel panel) {

        for(Object o: tmlspec.getChannels()) {
            if (o instanceof TMLChannel) {
                boolean ret = addChannel((TMLChannel) o, panel);
                if (!ret) return false;
            }
        }

        return true;
    }

    // Assumes 1 to 1 channel
    private boolean addChannel(TMLChannel chan, TMLComponentDesignPanel panel) {
        TMLTask task1 = chan.getOriginTask();
        TMLTask task2 = chan.getDestinationTask();

        if ((task1 == null) || (task2 == null)) {
            return false;
        }

        TMLCPrimitiveComponent c1 = taskMap.get(task1);
        TMLCPrimitiveComponent c2 = taskMap.get(task2);

        if ((c1 == null) || (c2 == null)) {
            return false;
        }

        // Adding ports to tasks
        //TMLCPrimitivePort p = new TMLCPrimitivePort();

        // Setting the characteristics
        // Connecting the ports




        return true;
    }






}