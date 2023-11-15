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
import tmltranslator.*;
import ui.tmlcompd.TMLCChannelOutPort;
import ui.tmlcompd.TMLCPortConnector;
import ui.tmldd.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class DrawerTMAPModeling
 * Class having a list of different constraints
 * Creation: 23/04/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 23/04/2010
 */
public class DrawerTMAPModeling {

    private final static int RADIUS = 400;
    private final static int XCENTER = 500;
    private final static int YCENTER = 450;

    private final static int DEC_COMP_Y = 45;

    private final static int LOOP_X = 150;

    private final static int X_SPACE = 300;


    private MainGUI mgui;
    private boolean hasError;
    private HashMap<HwNode, TGComponent> nodeMap;


    public DrawerTMAPModeling(MainGUI _mgui) {
        mgui = _mgui;
    }

    private void check(boolean condition, String text) throws MalformedTMLDesignException {
        if (!condition) {
            throw new MalformedTMLDesignException(text);
        }
    }


    // Not thread-safe
    public void drawTMAPModelingPanel(TMLMapping tmap, TMLArchiPanel panel) throws MalformedTMLDesignException {
        TraceManager.addDev("Drawing TMAP spec ...");

        nodeMap = new HashMap<>();

        hasError = false;

        if (tmap == null) {
            TraceManager.addDev("Null spec");
            hasError = true;
            return;
        }

        tmap.NullifyAutomata();
        TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
        syntax.checkSyntax();
        if (syntax.hasErrors() > 0) {
            TraceManager.addDev("hasError");
            TraceManager.addDev("Errors found at syntax checking of TMAP:\n" + syntax.printErrors());
            hasError = true;
            return;
        }

        TraceManager.addDev("Making HW components");
        makeBuses(tmap, panel.tmlap);

        makeOtherComponentsFromLinks(tmap, panel.tmlap);


        makeTaskMapping(tmap, panel.tmlap);

        makeChannelMapping(tmap, panel.tmlap);

        makeSecurityMapping(tmap, panel.tmlap);
    }

    private void makeBuses(TMLMapping tmap, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {
        int busIndex = 75;
        final int busY = 350;

        TraceManager.addDev("Nb of buses to investigate:" + tmap.getArch().getBUSs().size());


        for (HwNode node : tmap.getArch().getBUSs()) {
            HwBus bus = (HwBus) node;

            TraceManager.addDev("Adding bus:" + bus.getName());

            TMLArchiBUSNode comp = new TMLArchiBUSNode(busIndex, busY, panel.getMinX(),
                    panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                    true, null, panel);

            comp.setName(bus.getName());
            comp.setDataSize(bus.byteDataSize);
            comp.setPipelineSize(bus.pipelineSize);
            comp.setSliceTime(bus.sliceTime);
            comp.setBurstSize(bus.burstSize);
            comp.setArbitrationPolicy(bus.arbitration);
            comp.setPrivacy(bus.privacy);
            comp.setClockRatio(bus.clockRatio);
            comp.resize(comp.getWidth(), 110);

            panel.addBuiltComponent(comp);
            nodeMap.put(bus, comp);

            busIndex += comp.getWidth() + X_SPACE;
        }
    }


    private void makeOtherComponentsFromLinks(TMLMapping tmap, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {
        int cpuIndex = 50;
        int memoryIndex = 75;
        int bridgeIndex = 275;
        final int cpuY = 50;
        final int memY = 550;
        final int bridgeY = 250;

        for (HwLink link : tmap.getArch().getHwLinks()) {
            TGComponent compBus = nodeMap.get(link.bus);

            check(compBus != null, "Construction of bus failed");

            TGComponent tgc = nodeMap.get(link.hwnode);

            if (tgc == null) {
                // We need to add the component
                if (link.hwnode instanceof HwCPU) {
                    tgc = addCPU((HwCPU) (link.hwnode), link.bus, panel, cpuIndex, cpuY);
                    cpuIndex += X_SPACE;
                } else if (link.hwnode instanceof HwFPGA) {
                        tgc = addFPGA((HwFPGA) (link.hwnode), link.bus, panel, cpuIndex, cpuY);
                        cpuIndex += X_SPACE;
                } else if (link.hwnode instanceof HwA) {
                    tgc = addHwA((HwA) (link.hwnode), link.bus, panel, cpuIndex, cpuY);
                    cpuIndex += X_SPACE;
                } else if (link.hwnode instanceof HwMemory) {
                    tgc = addMemory((HwMemory) (link.hwnode), link.bus, panel, memoryIndex, memY);
                    memoryIndex += X_SPACE;
                } else if (link.hwnode instanceof HwBridge) {
                    //TraceManager.addDev("Checking for bridge: " + link.hwnode.getName());
                    tgc = addBridge((HwBridge) (link.hwnode), link.bus, panel, bridgeIndex, bridgeY);
                    bridgeIndex += X_SPACE;
                }
                if (tgc != null) {
                    nodeMap.put(link.hwnode, tgc);
                    panel.addBuiltComponent(tgc);
                }
            }

            check(tgc != null, "Invalid component: could not be added: " + link.hwnode.getClass().getName() + " is not supported");
            addLinkConnector(tgc, compBus, panel);
        }
    }

    private TMLArchiCPUNode addCPU(HwCPU cpu, HwBus bus, TMLArchiDiagramPanel panel, int cpuIndex, int cpuY) {
        TMLArchiCPUNode cpuComp = new TMLArchiCPUNode(cpuIndex, cpuY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel);

        cpuComp.setName(cpu.getName());
        cpuComp.setNbOfCore(cpu.nbOfCores);
        cpuComp.setByteDataSize(cpu.byteDataSize);
        cpuComp.setPipelineSize(cpu.pipelineSize);
        cpuComp.setGoIdleTime(cpu.goIdleTime);
        cpuComp.setMaxConsecutiveIdleCycles(cpu.maxConsecutiveIdleCycles);
        cpuComp.setExeciTime(cpu.execiTime);
        cpuComp.setExeccTime(cpu.execcTime);
        cpuComp.setTaskSwitchingTime(cpu.taskSwitchingTime);
        cpuComp.setBranchingPredictionPenalty(cpu.branchingPredictionPenalty);
        cpuComp.setCacheMiss(cpu.cacheMiss);
        cpuComp.setSchedulingPolicy(cpu.schedulingPolicy);
        cpuComp.setSliceTime(cpu.sliceTime);
        cpuComp.setEncryption(cpu.encryption);

        return cpuComp;
    }

    private TMLArchiHWANode addHwA(HwA hwa, HwBus bus, TMLArchiDiagramPanel panel, int cpuIndex, int cpuY) {
        TMLArchiHWANode hwaComp = new TMLArchiHWANode(cpuIndex, cpuY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel);

        hwaComp.setName(hwa.getName());
        hwaComp.setByteDataSize(hwa.byteDataSize);
        hwaComp.setExeciTime(hwa.execiTime);
        hwaComp.setExeccTime(hwa.execcTime);

        return hwaComp;
    }

    private TMLArchiFPGANode addFPGA(HwFPGA fpga, HwBus bus, TMLArchiDiagramPanel panel, int cpuIndex, int cpuY) {
        TMLArchiFPGANode fpgaComp = new TMLArchiFPGANode(cpuIndex, cpuY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel);

        fpgaComp.setName(fpga.getName());
        fpgaComp.setCapacity(fpga.capacity);
        fpgaComp.setByteDataSize(fpga.byteDataSize);
        fpgaComp.setReconfigurationTime(fpga.reconfigurationTime);
        fpgaComp.setGoIdleTime(fpga.goIdleTime);
        fpgaComp.setMaxConsecutiveIdleCycles(fpga.maxConsecutiveIdleCycles);
        fpgaComp.setExeciTime(fpga.execiTime);
        fpgaComp.setExeccTime(fpga.execcTime);
        fpgaComp.setMappingPenalty(fpga.mappingPenalty);
        fpgaComp.setOperation(fpga.getOperation());
        fpgaComp.setScheduling(fpga.scheduling);

        return fpgaComp;
    }

    private TMLArchiMemoryNode addMemory(HwMemory mem, HwBus bus, TMLArchiDiagramPanel panel, int memIndex, int memY) {
        TMLArchiMemoryNode memComp = new TMLArchiMemoryNode(memIndex, memY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel);

        memComp.setName(mem.getName());
        memComp.setByteDataSize(mem.byteDataSize);
        memComp.setMemorySize(mem.memorySize);
        memComp.setBufferType(mem.bufferType);

        return memComp;
    }

    private TMLArchiBridgeNode addBridge(HwBridge bridge, HwBus bus, TMLArchiDiagramPanel panel, int memIndex, int memY) {
        TMLArchiBridgeNode bridgeComp = new TMLArchiBridgeNode(memIndex, memY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel);

        bridgeComp.setName(bridge.getName());
        bridgeComp.setBufferByteDataSize(bridge.bufferByteSize);

        return bridgeComp;
    }


    @SuppressWarnings("unchecked")
    private void makeTaskMapping(TMLMapping tmap, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {
        int cpt = 0;
        List<HwExecutionNode> onnodes = tmap.getNodes();
        List<TMLTask> tasks = tmap.getMappedTasks();

        check(onnodes.size() == tasks.size(), "Tasks and execution nodes in mapping should be of the same dimension");

        for(cpt=0; cpt < onnodes.size(); cpt ++) {
            HwExecutionNode node = onnodes.get(cpt);
            TMLTask task = tasks.get(cpt);

            TGComponent tgc = nodeMap.get(node);
            check(tgc != null, "No graphical component corresponding to execution node " + node.getName());
            check(tgc instanceof SwallowTGComponent && tgc instanceof TMLArchiElementInterface,
                    "Invalid graphical component for task " + task.getName());

            Point p = getRandomCoordinate(tgc);

            TMLArchiArtifact artifact = new TMLArchiArtifact(p.x, p.y, panel.getMinX(), panel.getMaxX(),
                    panel.getMinY(), panel.getMaxY(), false, tgc, panel);

            String refAndName = task.getName();
            String[] splitName = refAndName.split("__");
            if (splitName.length >= 2) {
                artifact.setTaskName(splitName[1]);
                artifact.setReferenceTaskName(splitName[0]);
            } else if (splitName.length == 1) {
                String tabName = ((TGComponent)tmap.getTMLModeling().getReference()).getTDiagramPanel().tp.getNameOfTab();
                artifact.setTaskName(refAndName);
                artifact.setReferenceTaskName(tabName);
            } else {
                artifact.setTaskName(refAndName);
            }
            artifact.makeFullValue();
            artifact.setPriority(task.getPriority());
            artifact.setOperation(task.getOperation());


            panel.addComponent(artifact, p.x, p.y, true, true);
        }
    }

    @SuppressWarnings("unchecked")
    private void makeChannelMapping(TMLMapping tmap, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {
        int cpt = 0;
        List<HwCommunicationNode> oncommnodes = tmap.getCommunicationNodes();
        List<TMLElement> elts = tmap.getMappedCommunicationElement();

        check(oncommnodes.size() == elts.size(), "Tasks and execution nodes in mapping should be of the same dimension");
        boolean isMultiMapping = false;
        List<TMLElement> mappedElems = new ArrayList<TMLElement>();
        for (TMLElement elem : elts) {
            if (!mappedElems.contains(elem)) {
                if (elem instanceof TMLChannel) {
                    TMLChannel ch = (TMLChannel) elem;
                    ArrayList<HwCommunicationNode> commNodes = tmap.getAllCommunicationNodesOfChannel(ch);
                    for (HwCommunicationNode node : commNodes) {
                        if (node instanceof HwMemory) {
                            TGComponent tgc = nodeMap.get(node);
                            check(tgc != null, "No graphical component corresponding to communication node " + node.getName());
                            check(tgc instanceof SwallowTGComponent && tgc instanceof TMLArchiElementInterface,
                                    "Invalid graphical component for task " + node.getName());

                            Point p = getRandomCoordinate(tgc);
                            TMLArchiCommunicationArtifact artifact = new TMLArchiCommunicationArtifact(p.x, p.y, panel.getMinX(), panel.getMaxX(),
                                    panel.getMinY(), panel.getMaxY(), false, tgc, panel);
                            
                            String refAndName = elem.getName();
                            String[] splitName = refAndName.split("__");
                            if (splitName.length >= 2) {
                                artifact.setCommunicationName(splitName[1]);
                                artifact.setReferenceCommunicationName(splitName[0]);
                            } else if (splitName.length == 1) {
                                String tabName = ((TGComponent)tmap.getTMLModeling().getReference()).getTDiagramPanel().tp.getNameOfTab();
                                artifact.setCommunicationName(refAndName);
                                artifact.setReferenceCommunicationName(tabName);
                            } else {
                                artifact.setCommunicationName(refAndName);
                            }
                            ArrayList<String> otherCommNames = new ArrayList<String>();
                            for (HwCommunicationNode otherNode : commNodes) {
                                if (otherNode != node) {
                                    otherCommNames.add(otherNode.getName());
                                }
                            }
                            artifact.addNewOtherCommunicationNames(otherCommNames);
                            panel.addComponent(artifact, p.x, p.y, true, true);
                            isMultiMapping = true;
                            mappedElems.add(elem);
                            break;
                        }
                    }
                }
                if (!isMultiMapping) {
                    for(cpt=0; cpt < oncommnodes.size(); cpt ++) {
                        TMLElement elt = elts.get(cpt);
                        if (elem.equals(elt)) {
                            HwCommunicationNode node = oncommnodes.get(cpt);
                        
                            TGComponent tgc = nodeMap.get(node);
                            check(tgc != null, "No graphical component corresponding to communication node " + node.getName());
                            check(tgc instanceof SwallowTGComponent && tgc instanceof TMLArchiElementInterface,
                                    "Invalid graphical component for task " + node.getName());

                            Point p = getRandomCoordinate(tgc);
                            TMLArchiCommunicationArtifact artifact = new TMLArchiCommunicationArtifact(p.x, p.y, panel.getMinX(), panel.getMaxX(),
                                    panel.getMinY(), panel.getMaxY(), false, tgc, panel);
                            String refAndName = elt.getName();
                            String[] splitName = refAndName.split("__");
                            if (splitName.length >= 2) {
                                artifact.setCommunicationName(splitName[1]);
                                artifact.setReferenceCommunicationName(splitName[0]);
                            } else if (splitName.length == 1) {
                                String tabName = ((TGComponent)tmap.getTMLModeling().getReference()).getTDiagramPanel().tp.getNameOfTab();
                                artifact.setCommunicationName(refAndName);
                                artifact.setReferenceCommunicationName(tabName);
                            } else {
                                artifact.setCommunicationName(refAndName);
                            }
                            panel.addComponent(artifact, p.x, p.y, true, true);
                        }
                    }
                    mappedElems.add(elem);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void makeSecurityMapping(TMLMapping tmap, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {
        for(Object obj: tmap.mappedSecurity.keySet()) {
            if (obj instanceof SecurityPattern) {
                SecurityPattern sp = (SecurityPattern) obj;
                List<HwMemory> mems = tmap.getListBySecurityPattern(sp);
                for(HwMemory mem: mems) {
                    TraceManager.addDev("Found a memory " + mem.getName() + " for security pattern " + sp.getName());
                    TGComponent tgc = nodeMap.get(mem);
                    check(tgc != null, "No graphical component corresponding to memory node " + mem.getName());
                    check(tgc instanceof SwallowTGComponent && tgc instanceof TMLArchiElementInterface,
                            "Invalid graphical component for task " + mem.getName());
                    Point p = getRandomCoordinate(tgc);
                    TMLArchiKey artifact = new TMLArchiKey(p.x, p.y, panel.getMinX(), panel.getMaxX(),
                            panel.getMinY(), panel.getMaxY(), false, tgc, panel);
                    artifact.setReferenceKey(sp.getName());
                    artifact.makeFullValue();
                    panel.addComponent(artifact, p.x, p.y, true, true);
                }
            }
        }
    }

    private void addLinkConnector(TGComponent p1, TGComponent p2, TMLArchiDiagramPanel panel) throws MalformedTMLDesignException {

        check(p1 != null, "Null component at origin of link connector");
        check(p2 != null, "Null component at destination of link connector");

        int myX = p1.getX() + p1.getWidth() / 2;
        int myY = p1.getY() + p1.getHeight() / 2;

        Point pt1 = computePoint(p1, p2);
        Point pt2 = computePoint(p2, p1);

        check(pt1 != null, "No intersection in " + p1.getName());
        check(pt2 != null, "No intersection in " + p2.getName());

        TGConnectingPoint tgcp1 = p1.closerFreeTGConnectingPointCompatibility(pt1.x, pt1.y, true, false,
                TGComponentManager.CONNECTOR_NODE_TMLARCHI);
        TGConnectingPoint tgcp2 = p2.closerFreeTGConnectingPointCompatibility(pt2.x, pt2.y, false, true,
                TGComponentManager.CONNECTOR_NODE_TMLARCHI);

        check(tgcp1 != null, "No available connecting point for link in " + p1.getName());
        check(tgcp2 != null, "No available connecting point for link in " + p2.getName());

        //TraceManager.addDev("Before Free in " + p1.getName() + ": " + p1.nbOfFreeTGConnectingPoint());
        //TraceManager.addDev("Before Free in " + p2.getName() + ": " + p2.nbOfFreeTGConnectingPoint());

        tgcp1.setFree(false);
        tgcp2.setFree(false);

        //TraceManager.addDev("After Free in " + p1.getName() + ": " + p1.nbOfFreeTGConnectingPoint());
        //TraceManager.addDev("After Free in " + p2.getName() + ": " + p2.nbOfFreeTGConnectingPoint());

        TMLArchiConnectorNode conn = new TMLArchiConnectorNode(myX, myY, panel.getMinX(),
                panel.getMaxX(), panel.getMinY(), panel.getMaxY(),
                true, null, panel, tgcp1, tgcp2, null);

        panel.addBuiltConnector(conn);
    }


    private Point computePoint(TGComponent c1, TGComponent c2) {
        /*int randomX1 = (int)(Math.random() * Math.min(c1.getWidth(), c2.getWidth()));
        int randomY1 = (int)(Math.random() * Math.min(c1.getHeight(), c2.getHeight()));
        int randomX2 = (int)(Math.random() * Math.min(c1.getWidth(), c2.getWidth()));
        int randomY2 = (int)(Math.random() * Math.min(c1.getHeight(), c2.getHeight()));*/
        int randomX1 = c1.getWidth()/2;
        int randomY1 = c1.getHeight()/2;
        int randomX2 = c2.getWidth()/2;
        int randomY2 = c2.getHeight()/2;
        //TraceManager.addDev("Random X1= " + randomX1 + " Y1 = " + randomY1);
        return GraphicLib.intersectionRectangleSegment(c1.getX(), c1.getY(), c1.getWidth(), c1.getHeight(),
                c1.getX()+randomX1, c1.getY() + randomY1,
                c2.getX()+randomX2, c2.getY() + randomY2);
    }

    private String getSplitName(String compoundName, boolean isOrigin) {
        String[] splitName = compoundName.split("__");
        if (splitName.length > 3) {
            if (isOrigin) {
                return splitName[1];
            } else {
                return splitName[3];
            }
        } else if (splitName.length > 1) {
            return splitName[1];
        }
        return compoundName;
    }

    private Point getRandomCoordinate(TGComponent tgc) {
        int x,y;

        x = tgc.getX() + 10 + (int) (Math.random() * (tgc.getWidth() - 100));
        y = tgc.getY() + 30 + (int) (Math.random() * (tgc.getHeight() - 70));

        return new Point(x, y);
    }


}