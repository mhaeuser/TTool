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

package ui.tmlad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import myutil.GraphicLib;
import ui.AllowedBreakpoint;
import ui.BasicErrorHighlight;
import ui.CheckableAccessibility;
import ui.CheckableLatency;
import ui.ColorManager;
import ui.EmbeddedComment;
import ui.ErrorHighlight;
import ui.LinkedReference;
import ui.MalformedModelingException;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import ui.TGConnectingPoint;
import ui.ad.TADComponentWithoutSubcomponents;
import ui.util.IconManager;
import ui.window.JDialogMultiStringAndTabs;
import ui.window.TabInfo;

/**
 * Class TMLADReadChannel
 * Action of writing data in channel
 * Creation: 21/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/11/2005
* @version 1.1 05/11/2019
 */
public class TMLADReadCAMS extends TADComponentWithoutSubcomponents/* Issue #69 TGCWithoutInternalComponent*/ implements CheckableAccessibility, LinkedReference, CheckableLatency, EmbeddedComment, AllowedBreakpoint, BasicErrorHighlight {
    private Map<String, String> latencyVals;
    protected int lineLength = 5;
    protected int textX = 5;
    protected int textX0 = 2;
    protected int textY0 = 0;
    protected int textY1 = 15;
    protected int linebreak = 10;

    protected int decSec = 4;

    protected int latencyX = 30;
    protected int latencyY = 25;
    protected int textWidth = 10;
    protected int textHeight = 20;

    protected String channelName = "ch";
    protected String nbOfSamples = "1";
    protected String securityContext = "";
    protected boolean isAttacker = false;

    protected int stateOfError = 0; // Not yet checked

    public final static int NOT_VERIFIED = 0;
    public final static int REACHABLE = 1;
    public final static int NOT_REACHABLE = 2;

    public int reachabilityInformation;

	public boolean isEncForm = true;
	
    public TMLADReadCAMS(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 30;
        height = 20;
        minWidth = 30;

        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[2];
        connectingPoint[0] = new TGConnectingPointTMLAD(this, 0, -lineLength, true, false, 0.5, 0.0);
        connectingPoint[1] = new TGConnectingPointTMLAD(this, 0, lineLength, false, true, 0.5, 1.0);

        moveable = true;
        editable = true;
        removable = true;

        makeValue();
        name = "read channel";

        myImageIcon = IconManager.imgic906;
        latencyVals = new ConcurrentHashMap<String, String>();
        //latencyVals.put("sendChannel: sensorData", "3");
    }

	/*public void addLatency(String name, String num){
        latencyVals.put(name,num);
	}*/

    public Map<String, String> getLatencyMap() {
        return latencyVals;
    }

    @Override
    public void internalDrawing(Graphics g) {
        int w = g.getFontMetrics().stringWidth(value);
        int w1 = Math.max(minWidth, w + 2 * textX);
        if ((w1 != width) & (!tdp.isScaled())) {
            setCd(x + width / 2 - w1 / 2, y);
            width = w1;
            //updateConnectingPoints();
        }

        if (stateOfError > 0) {
            Color c = g.getColor();
            switch (stateOfError) {
                case ErrorHighlight.OK:
                    g.setColor(ColorManager.TML_PORT_CHANNEL);
                    break;
                default:
                    g.setColor(ColorManager.UNKNOWN_BOX_ACTION);
            }
            // Making the polygon
            int[] px1 = {x, x + width, x + width, x, x + linebreak};
            int[] py1 = {y, y, y + height, y + height, y + (height / 2)};
            g.fillPolygon(px1, py1, 5);
            g.setColor(c);
        }

        //g.drawRoundRect(x, y, width, height, arc, arc);
        g.drawLine(x + (width / 2), y, x + (width / 2), y - lineLength);
        g.drawLine(x + (width / 2), y + height, x + (width / 2), y + lineLength + height);

        Color c = g.getColor();
        int x1 = x + 1;
        int y1 = y + 1;
        int height1 = height;
        int width1 = width;
        g.setColor(ColorManager.TML_PORT_CHANNEL);
        g.drawLine(x1, y1, x1 + width1, y1);
        g.drawLine(x1 + width1, y1, x1 + width1, y1 + height1);
        g.drawLine(x1, y1 + height1, x1 + width1, y1 + height1);
        g.drawLine(x1, y1, x1 + linebreak, y1 + height1 / 2);
        g.drawLine(x1, y1 + height1, x1 + linebreak, y1 + height1 / 2);
        g.setColor(c);

        g.drawLine(x, y, x + width, y);
        g.drawLine(x + width, y, x + width, y + height);
        g.drawLine(x, y + height, x + width, y + height);
        g.drawLine(x, y, x + linebreak, y + height / 2);
        g.drawLine(x, y + height, x + linebreak, y + height / 2);
        if (isAttacker) {
            g.drawString("attack", x + (width - w) / 2, y + textY0);
        } else {
            g.drawString("chl", x + (width - w) / 2, y + textY0);
        }
        g.drawString(value, x + linebreak + textX0, y + textY1);

        if (!securityContext.equals("")) {
        	c = g.getColor();
	        if (!isEncForm){
	        	g.setColor(Color.RED);
	        }
            g.drawString("sec:" + securityContext, x + 3 * width / 4, y + height + textY1 - decSec);
            g.setColor(c);
        }
        drawReachabilityInformation(g);
        if (getCheckLatency()) {
            ConcurrentHashMap<String, String> latency = tdp.getMGUI().getLatencyVals(getDIPLOID());

            if (latency != null) {
                latencyVals = latency;
                drawLatencyInformation(g);
            }
        }
    }

    private void drawLatencyInformation(Graphics g) {
        int index = 1;
        for (String s : latencyVals.keySet()) {
            int w = g.getFontMetrics().stringWidth(s);
            g.drawString(s, x - latencyX - w + 1, y - latencyY * index - 2);
            g.drawRect(x - latencyX - w, y - latencyY * index - textHeight, w + 4, textHeight);
            g.drawLine(x, y, x - latencyX, y - latencyY * index);
            g.drawString(latencyVals.get(s), x - latencyX / 2, y - latencyY * index / 2);
            index++;
        }
    }

    private void drawReachabilityInformation(Graphics g) {
        if (reachabilityInformation > 0) {
            Color c = g.getColor();
            Color c1;
            switch (reachabilityInformation) {
                case REACHABLE:
                    c1 = Color.green;
                    break;
                case NOT_REACHABLE:
                    c1 = Color.red;
                    break;
                default:
                    return;
            }

            GraphicLib.arrowWithLine(g, 1, 0, 10, x - 30, y - 3, x - 15, y - 3, true);
            g.drawOval(x - 11, y - 10, 7, 9);
            g.setColor(c1);
            g.fillRect(x - 12, y - 7, 9, 7);
            g.setColor(c);
            g.drawRect(x - 12, y - 7, 9, 7);
            if (reachabilityInformation == NOT_REACHABLE) {
                g.drawLine(x - 14, y - 9, x - 1, y + 3);
                g.drawLine(x - 14, y + 3, x - 1, y - 9);
            }
        }
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }

        if ((int) (Line2D.ptSegDistSq(x + (width / 2), y - lineLength, x + (width / 2), y + lineLength + height, _x, _y)) < distanceSelected) {
            return this;
        }

        return null;
    }

    public void makeValue() {
        value = channelName + "(" + nbOfSamples + ") ";
    }

    public String getChannelName() {
        return channelName;
    }

    public String getSamplesValue() {
        return nbOfSamples;
    }

    public String getAction() {
        return value;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        TabInfo tab1 = new TabInfo("Name and samples");
        String[] labels = new String[2];
        String[] values = new String[2];
        labels[0] = "CAMS Channel name";
        values[0] = channelName;
        labels[1] = "Nb of samples";
        values[1] = nbOfSamples;
        tab1.labels=labels;
        tab1.values =  values;
        List<String[]> help = new ArrayList<String[]>();
        String[] allInChannels = tdp.getMGUI().getAllInChannels();
        if (isAttacker) {
            allInChannels = tdp.getMGUI().getAllCompInChannels();
        }
        help.add(allInChannels);
        help.add(null);
        tab1.help = help;

        TabInfo tab2 = new TabInfo("Security");
        labels = new String[3];
        values = new String[3];
        labels[0] = "Security Pattern";
        values[0] = securityContext;
        labels[1] = "Attacker?";
        values[1] = isAttacker ? "Yes" : "No";
        labels[2] = "Encrypted Form?";
        values[2] = isEncForm ? "Yes" : "No";        
        help = new ArrayList<String[]>();
        String[] choice = new String[]{"Yes", "No"};
        help.add(tdp.getMGUI().getCurrentCryptoConfig());
        help.add(choice);
        help.add(choice);
        tab2.labels=labels;
        tab2.values =  values;
        tab2.help = help;
        tab2.helpButtom = new String[]{null, "cryptographicconfiguration.html", "cryptographicconfiguration.html"};

        List<TabInfo> tabs = new ArrayList<>();
        tabs.add(tab1);
        tabs.add(tab2);

        //JDialogTwoString jdts = new JDialogTwoString(frame, "Setting channel's properties", "Channel name", channelName, "Nb of samples", nbOfSamples);
        JDialogMultiStringAndTabs jdmsat = new JDialogMultiStringAndTabs(tdp.getMGUI(), frame, "Read from channel", tabs);
        //jdms.setSize(600, 300);
        GraphicLib.centerOnParent(jdmsat, 600, 300);
        jdmsat.setVisible(true); // blocked until dialog has been closed

        if (jdmsat.hasBeenSet() && (jdmsat.hasValidString(0))) {
            channelName = jdmsat.getString(0, 0);
            nbOfSamples = jdmsat.getString(0, 1);
            securityContext = jdmsat.getString(1, 0);
            isAttacker = jdmsat.getString(1, 1).equals("Yes");
            isEncForm = jdmsat.getString(1, 2).equals("Yes");
            makeValue();
            return true;
        }

        return false;
    }

    public void setSamples(String sp) {
        nbOfSamples = sp;
        makeValue();
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<Data channelName=\"");
        sb.append(getChannelName());
        sb.append("\" nbOfSamples=\"");
        sb.append(getSamplesValue());
        sb.append("\" secPattern=\"");
        sb.append(securityContext);
        sb.append("\" isAttacker=\"");
        sb.append(isAttacker ? "Yes" : "No");
        sb.append("\" isEncForm=\"");
        sb.append(isEncForm ? "Yes" : "No");
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

            for (int i = 0; i < nl.getLength(); i++) {
                n1 = nl.item(i);

                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for (int j = 0; j < nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("Data")) {
                                channelName = elt.getAttribute("channelName");
                                nbOfSamples = elt.getAttribute("nbOfSamples");
                                securityContext = elt.getAttribute("secPattern");
                                isAttacker = elt.getAttribute("isAttacker").equals("Yes");
                                isEncForm = elt.getAttribute("isEncForm").equals("Yes");    
                                if (elt.getAttribute("isEncForm").equals("") || !elt.hasAttribute("isEncForm")){
                                	if (!securityContext.equals("")){
                                		isEncForm=true;
                                	}
                                }
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

    public String getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(String sc) {
        securityContext = sc;
    }

    public boolean isAttacker() {
        return isAttacker;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLAD_READ_CHANNEL;
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_TMLAD;
    }

    @Override
    public void setStateAction(int _stateAction) {
        stateOfError = _stateAction;
    }

    public boolean getEncForm(){
		return isEncForm;
	}
		
	public void setEncForm(boolean encForm){
		isEncForm=encForm;
	}


    public void setChannelName(String s) {
        channelName = s;
        makeValue();
    }
}
