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
import tmltranslator.HwCPU;
import tmltranslator.modelcompiler.ArchUnitMEC;
import tmltranslator.simulation.SimulationTransaction;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogCPUNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class TMLArchiCPUNode
 * Node. To be used in TML architecture diagrams.
 * Creation: 02/05/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.1 21/05/2008
 */
public class TMLArchiCPUNode extends TMLArchiNode implements SwallowTGComponent, WithAttributes, TMLArchiElementInterface {

	// Issue #31
//	private int textY1 = 15;
//    private int textY2 = 30;
//    private int derivationx = 2;
//    private int derivationy = 3;
    private String stereotype = "CPU";

    private int nbOfCores = HwCPU.DEFAULT_NB_OF_CORES;
    private int byteDataSize = HwCPU.DEFAULT_BYTE_DATA_SIZE;
    private int pipelineSize = HwCPU.DEFAULT_PIPELINE_SIZE;
    private int goIdleTime = HwCPU.DEFAULT_GO_IDLE_TIME;
    private int maxConsecutiveIdleCycles = HwCPU.DEFAULT_MAX_CONSECUTIVE_IDLE_CYCLES;
    private int taskSwitchingTime = HwCPU.DEFAULT_TASK_SWITCHING_TIME;
    private int branchingPredictionPenalty = HwCPU.DEFAULT_BRANCHING_PREDICTION_PENALTY;
    private int schedulingPolicy = HwCPU.DEFAULT_SCHEDULING;
    private int sliceTime = HwCPU.DEFAULT_SLICE_TIME;
    private int execiTime = HwCPU.DEFAULT_EXECI_TIME;
    private int execcTime = HwCPU.DEFAULT_EXECC_TIME;
    private int cacheMiss = HwCPU.DEFAULT_CACHE_MISS;
    private int encryption = HwCPU.ENCRYPTION_NONE;
    private String operation = "";

    public TMLArchiCPUNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        // Issue #31
//        width = 250;
//        height = 200;
        textY = 15;
        minWidth = 150;
        minHeight = 100;
        initScaling( 250, 200 );

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        connectingPoint[1] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 0.0);
        connectingPoint[2] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.0);
        connectingPoint[3] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.5);
        connectingPoint[4] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.5);
        connectingPoint[5] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 1.0);
        connectingPoint[6] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.5, 1.0);
        connectingPoint[7] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 1.0);

        connectingPoint[8] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 0.0);
        connectingPoint[9] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 0.0);
        connectingPoint[10] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.25);
        connectingPoint[11] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.25);
        connectingPoint[12] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.75);
        connectingPoint[13] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 1.0, 0.75);
        connectingPoint[14] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.25, 1.0);
        connectingPoint[15] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("CPU0");
        value = "name";

        myImageIcon = IconManager.imgic700;
    }

    @Override
    protected void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);

        // Issue #31
        final int derivationX = scale( DERIVATION_X );
        final int derivationY = scale( DERIVATION_Y );

        // Top lines
        g.drawLine(x, y, x + derivationX, y - derivationY);
        g.drawLine(x + width, y, x + width + derivationX, y - derivationY);
        g.drawLine(x + derivationX, y - derivationY, x + width + derivationX, y - derivationY);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationX, y - derivationY + height);
        g.drawLine(x + derivationX + width, y - derivationY, x + width + derivationX, y - derivationY + height);

        // Filling color
        g.setColor(ColorManager.CPU_BOX_1);
        g.fill3DRect(x + 1, y + 1, width - 1, height - 1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        drawSingleString(g,ster, x + (width - w) / 2, y + textY);// Issue #31
        g.setFont(f);
        w = g.getFontMetrics().stringWidth(name);
        drawSingleString(g,name, x + (width - w) / 2, y + 2 * textY );// Issue #31

        // Icon
        // Issue #31
        final int imgOffset = scale( 4 );
        g.drawImage( scale( IconManager.imgic1100.getImage() ), x + imgOffset/*4*/, y + imgOffset/*4*/, null);
    }

    @Override
    public TGComponent isOnOnlyMe(int x1, int y1) {
        Polygon pol = new Polygon();
        pol.addPoint(x, y);

        // Issue #31
        final int derivationX = scale( DERIVATION_X );
        final int derivationY = scale( DERIVATION_Y );
        pol.addPoint(x + derivationX, y - derivationY);
        pol.addPoint(x + derivationX + width, y - derivationY);
        pol.addPoint(x + derivationX + width, y + height - derivationY);
        pol.addPoint(x + width, y + height);
        pol.addPoint(x, y + height);
        if (pol.contains(x1, y1)) {
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
        boolean error = false;
        String errors = "";
        int tmp;
        String tmpName;
        JDialogCPUNode dialog = new JDialogCPUNode(getTDiagramPanel().getMainGUI(), frame, "Setting CPU attributes", this, MECType, transactions);
        dialog.setSize(600, 450);
        GraphicLib.centerOnParent(dialog, 500, 450);
        // dialog.show(); // blocked until dialog has been closed
        dialog.setVisible(true);
        
        MECType = dialog.getMECType();

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getNodeName().length() != 0) {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false, false, true,
                    true)) {
                error = true;
                errors += "Name of the node  ";
            } else {
                name = tmpName;
            }
        }

        schedulingPolicy = dialog.getSchedulingPolicy();
        if (schedulingPolicy == HwCPU.BASIC_ROUND_ROBIN) {
            stereotype = "CPURR";
        }

        if (schedulingPolicy == HwCPU.ROUND_ROBIN_PRIORITY_BASED) {
            stereotype = "CPURRPB";
        }

        if (schedulingPolicy == HwCPU.STRICT_PRIORITY) {
            stereotype = "CPUSP";
        }

        if (dialog.getSliceTime().length() != 0) {
            try {
                tmp = sliceTime;
                sliceTime = Integer.decode(dialog.getSliceTime()).intValue();
                if (sliceTime <= 0) {
                    sliceTime = tmp;
                    error = true;
                    errors += "Slice time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Slice time  ";
            }
        }

        if (dialog.getNbOfCores().length() != 0) {
            try {
                tmp = nbOfCores;
                nbOfCores = Integer.decode(dialog.getNbOfCores()).intValue();
                if (nbOfCores <= 0) {
                    nbOfCores = tmp;
                    error = true;
                    errors += "Nb of cores   ";
                }
            } catch (Exception e) {
                error = true;
                errors += "nb of cores  ";
            }
        }

        if (dialog.getByteDataSize().length() != 0) {
            try {
                tmp = byteDataSize;
                byteDataSize = Integer.decode(dialog.getByteDataSize()).intValue();
                if (byteDataSize <= 0) {
                    byteDataSize = tmp;
                    error = true;
                    errors += "Data size  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Data size  ";
            }
        }

        if (dialog.getPipelineSize().length() != 0) {
            try {
                tmp = pipelineSize;
                pipelineSize = Integer.decode(dialog.getPipelineSize()).intValue();
                if (pipelineSize <= 0) {
                    pipelineSize = tmp;
                    error = true;
                    errors += "Pipeline size  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Pipeline size  ";
            }
        }

        if (dialog.getGoIdleTime().length() != 0) {
            try {
                tmp = goIdleTime;
                goIdleTime = Integer.decode(dialog.getGoIdleTime()).intValue();
                if (goIdleTime < 0) {
                    goIdleTime = tmp;
                    error = true;
                    errors += "Go idle time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Go idle time  ";
            }
        }

        if (dialog.getMaxConsecutiveIdleCycles().length() != 0) {
            try {
                tmp = goIdleTime;
                maxConsecutiveIdleCycles = Integer.decode(dialog.getMaxConsecutiveIdleCycles()).intValue();
                if (maxConsecutiveIdleCycles < 0) {
                    maxConsecutiveIdleCycles = tmp;
                    error = true;
                    errors += "Max consecutive idle cycles  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Max consecutive idle cycles  ";
            }
        }

        if (dialog.getExeciTime().length() != 0) {
            try {
                tmp = execiTime;
                execiTime = Integer.decode(dialog.getExeciTime()).intValue();
                if (execiTime < 0) {
                    execiTime = tmp;
                    error = true;
                    errors += "execi time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "execi time  ";
            }
        }

        if (dialog.getExeccTime().length() != 0) {
            try {
                tmp = execcTime;
                execcTime = Integer.decode(dialog.getExeccTime()).intValue();
                if (execcTime < 0) {
                    execcTime = tmp;
                    error = true;
                    errors += "execc time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "execc time  ";
            }
        }

        if (dialog.getTaskSwitchingTime().length() != 0) {
            try {
                tmp = taskSwitchingTime;
                taskSwitchingTime = Integer.decode(dialog.getTaskSwitchingTime()).intValue();
                if (taskSwitchingTime <= 0) {
                    taskSwitchingTime = tmp;
                    error = true;
                    errors += "Task switching time  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Task switching time  ";
            }
        }

        if (dialog.getBranchingPredictionPenalty().length() != 0) {
            try {
                tmp = branchingPredictionPenalty;
                branchingPredictionPenalty = Integer.decode(dialog.getBranchingPredictionPenalty()).intValue();
                if ((branchingPredictionPenalty < 0) || (branchingPredictionPenalty > 100)) {
                    branchingPredictionPenalty = tmp;
                    error = true;
                    errors += "Mis-branching prediction  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Mis-branching prediction  ";
            }
        }

        if (dialog.getCacheMiss().length() != 0) {
            try {
                tmp = cacheMiss;
                cacheMiss = Integer.decode(dialog.getCacheMiss()).intValue();
                if ((cacheMiss < 0) || (cacheMiss > 100)) {
                    cacheMiss = tmp;
                    error = true;
                    errors += "Cache-miss  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Cache-miss  ";
            }
        }

        if (dialog.getClockRatio().length() != 0) {
            try {
                tmp = clockRatio;
                clockRatio = Integer.decode(dialog.getClockRatio()).intValue();
                if (clockRatio < 1) {
                    clockRatio = tmp;
                    error = true;
                    errors += "Clock divider  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "Clock divider  ";
            }
        }
        operation = dialog.getOperation().trim();

        encryption = dialog.getEncryption();
        if (error) {
            JOptionPane.showMessageDialog(frame,
                    "Invalid value for the following attributes: " + errors,
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_CPUNODE;
    }

    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return tgc instanceof TMLArchiArtifact;
    }

    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {

        //Set its coordinates
        if (tgc instanceof TMLArchiArtifact) {
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

    public Vector<TMLArchiArtifact> getArtifactList() {
        Vector<TMLArchiArtifact> v = new Vector<TMLArchiArtifact>();

        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                v.add((TMLArchiArtifact) tgcomponent[i]);
            }
        }
        return v;
    }

    @Override
    public void hasBeenResized() {
        for (int i = 0; i < nbInternalTGComponent; i++) {
            if (tgcomponent[i] instanceof TMLArchiArtifact) {
                tgcomponent[i].resizeWithFather();
            }
        }

    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        sb.append("<attributes nbOfCores=\"" + nbOfCores + "\" byteDataSize=\"" + byteDataSize + "\" ");
        sb.append(" schedulingPolicy=\"" + schedulingPolicy + "\" ");
        sb.append(" sliceTime=\"" + sliceTime + "\" ");
        sb.append(" goIdleTime=\"" + goIdleTime + "\" ");
        sb.append(" maxConsecutiveIdleCycles=\"" + maxConsecutiveIdleCycles + "\" ");
        sb.append(" pipelineSize=\"" + pipelineSize + "\" ");
        sb.append(" taskSwitchingTime=\"" + taskSwitchingTime + "\" ");
        sb.append(" branchingPredictionPenalty=\"" + branchingPredictionPenalty + "\" ");
        sb.append(" cacheMiss=\"" + cacheMiss + "\"");
        sb.append(" execiTime=\"" + execiTime + "\"");
        sb.append(" execcTime=\"" + execcTime + "\"");
        sb.append(" clockRatio=\"" + clockRatio + "\"");
         sb.append(" operation=\"" + operation + "\"");
        sb.append(" MECType=\"" + MECType.getIndex() + "\"");
        sb.append(" encryption=\"" + encryption + "\"");
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            // int t1id;
            String sstereotype = null, snodeName = null;

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();

                    // Issue #17 copy-paste error on j index
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null) {
                                name = snodeName;
                            }

                            if (elt.getTagName().equals("attributes")) {
                                //TraceManager.addDev("LOADING attributes");


                                try {
                                    // the "try" statement is for retro compatibility
                                    nbOfCores = Integer.decode(elt.getAttribute("nbOfCores")).intValue();
                                    //TraceManager.addDev("Setting cores to:" + nbOfCores);
                                } catch (Exception e) {
                                    //TraceManager.addDev("Coud not load number of cores");
                                }
                                byteDataSize = Integer.decode(elt.getAttribute("byteDataSize")).intValue();
                                schedulingPolicy = Integer.decode(elt.getAttribute("schedulingPolicy")).intValue();
                                goIdleTime = Integer.decode(elt.getAttribute("goIdleTime")).intValue();
                                pipelineSize = Integer.decode(elt.getAttribute("pipelineSize")).intValue();
                                taskSwitchingTime = Integer.decode(elt.getAttribute("taskSwitchingTime")).intValue();
                                branchingPredictionPenalty = Integer.decode(elt.getAttribute("branchingPredictionPenalty")).intValue();
                                if ((elt.getAttribute("cacheMiss") != null) && (elt.getAttribute("cacheMiss").length() > 0)) {
                                    cacheMiss = Integer.decode(elt.getAttribute("cacheMiss")).intValue();
                                }
                                if ((elt.getAttribute("execiTime") != null) && (elt.getAttribute("execiTime").length() > 0)) {
                                    execiTime = Integer.decode(elt.getAttribute("execiTime")).intValue();
                                }
                                if ((elt.getAttribute("execcTime") != null) && (elt.getAttribute("execcTime").length() > 0)) {
                                    execcTime = Integer.decode(elt.getAttribute("execcTime")).intValue();
                                }
                                if ((elt.getAttribute("maxConsecutiveIdleCycles") != null) && (elt.getAttribute("maxConsecutiveIdleCycles").length() > 0)) {
                                    maxConsecutiveIdleCycles = Integer.decode(elt.getAttribute("maxConsecutiveIdleCycles")).intValue();
                                }
                                if ((elt.getAttribute("clockRatio") != null) && (elt.getAttribute("clockRatio").length() > 0)) {
                                    clockRatio = Integer.decode(elt.getAttribute("clockRatio")).intValue();
                                }
                                if ((elt.getAttribute("MECType") != null) && (elt.getAttribute("MECType").length() > 0)) {
                                    if (elt.getAttribute("MECType").length() > 1) {       //old format
                                        MECType = ArchUnitMEC.Types.get(0);
                                    } else {
                                        MECType = ArchUnitMEC.Types.get(Integer.valueOf(elt.getAttribute("MECType")));
                                    }
                                }
                                if ((elt.getAttribute("sliceTime") != null) && (elt.getAttribute("sliceTime").length() > 0)) {
                                    sliceTime = Integer.decode(elt.getAttribute("sliceTime")).intValue();
                                }
                                if ((elt.getAttribute("operation") != null) && (elt.getAttribute("operation").length() > 0)) {
                                    operation = elt.getAttribute("operation");
                                    if (operation == null) {
                                        operation = "";
                                    }

                                }
                                if ((elt.getAttribute("encryption") != null) && (elt.getAttribute("encryption").length() > 0)) {
                                    encryption = Integer.decode(elt.getAttribute("encryption")).intValue();
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

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
    }

    public int getNbOfCores() {
        return nbOfCores;
    }
    public void setNbOfCore(int _nbOfCores) {
        nbOfCores = _nbOfCores;
    }

    public int getByteDataSize() {
        return byteDataSize;
    }
    public void setByteDataSize(int _byteDateSize) {
        byteDataSize = _byteDateSize;
    }

    public int getPipelineSize() {
        return pipelineSize;
    }
    public void setPipelineSize(int _pipelineSize) {
        pipelineSize = _pipelineSize;
    }

    public int getGoIdleTime() {
        return goIdleTime;
    }
    public void setGoIdleTime(int _goIdleTime) {
        goIdleTime = _goIdleTime;
    }

    public int getMaxConsecutiveIdleCycles() {
        return maxConsecutiveIdleCycles;
    }
    public void setMaxConsecutiveIdleCycles(int _maxConsecutiveIdleCycles) {
         maxConsecutiveIdleCycles = _maxConsecutiveIdleCycles;
    }

    public int getExeciTime() {
        return execiTime;
    }
    public void setExeciTime(int _execiTime) {
        execiTime = _execiTime;
    }

    public int getExeccTime() {
        return execcTime;
    }
    public void setExeccTime(int _execcTime) {
        execcTime = _execcTime;
    }

    public int getTaskSwitchingTime() {
        return taskSwitchingTime;
    }
    public void setTaskSwitchingTime(int _taskSwitchingTime) {
        taskSwitchingTime = _taskSwitchingTime;
    }

    public int getBranchingPredictionPenalty() {
        return branchingPredictionPenalty;
    }
    public void setBranchingPredictionPenalty(int _branchingPredictionPenalty) {
        branchingPredictionPenalty = _branchingPredictionPenalty;
    }

    public int getCacheMiss() {
        return cacheMiss;
    }
    public void setCacheMiss(int _cacheMiss) {
        cacheMiss = _cacheMiss;
    }

    public int getSchedulingPolicy() {
        return schedulingPolicy;
    }
    public void setSchedulingPolicy(int _schedulingPolicy) {
        schedulingPolicy = _schedulingPolicy;
    }

    public int getSliceTime() {
        return sliceTime;
    }
    public void setSliceTime(int _sliceTime) {
        sliceTime = _sliceTime;
    }

    public int getEncryption() {
        return encryption;
    }
    public void setEncryption(int _encryption) {
        encryption = _encryption;
    }
    public String getOperation() {
        return operation;
    }

    @Override
    public String getAttributes() {
        String attr = "";
        attr += "Nb of cores = " + nbOfCores + "\n";
        attr += "Data size (in byte) = " + byteDataSize + "\n";
        attr += "Pipeline size = " + pipelineSize + "\n";
        if (schedulingPolicy == HwCPU.DEFAULT_SCHEDULING) {
            attr += "Sched. policy = basic Round Robin\n";
        }
        attr += "Slice time (in microseconds) = " + sliceTime + "\n";
        attr += "Switching penalty (in cycle) = " + taskSwitchingTime + "\n";
        attr += "Go in idle mode (in cycle) = " + goIdleTime + "\n";
        attr += "Idle cycles to go idle = " + maxConsecutiveIdleCycles + "\n";
        attr += "EXECI exec. time (in cycle) = " + execiTime + "\n";
        attr += "EXECC exec. time (in cycle) = " + execcTime + "\n";
        attr += "Branch. pred. misrate (in %) = " + branchingPredictionPenalty + "\n";
        attr += "Cache miss (in %) = " + cacheMiss + "\n";
        attr += "Clock divider = " + clockRatio + "\n";
        attr += "Operation = " + operation + "\n";
        attr += "MECType = " + MECType.getIndex() + "\n";
        attr += "encryption = " + encryption + "\n";
        return attr;

    }

    @Override
    public int getComponentType() {
        return CONTROLLER;
    }
}
