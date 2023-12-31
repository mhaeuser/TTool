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
import myutil.TraceManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogCommunicationArtifact;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
   * Class TMLArchiCommunicationArtifact
   * Communication Artifact of a deployment diagram
   * Creation: 22/11/2007
   * @version 1.0 22/11/2007
   * @author Ludovic APVRILLE
 */
public class TMLArchiCommunicationArtifact extends TGCWithoutInternalComponent implements SwallowedTGComponent, WithAttributes,
        TMLArchiChannelInterface {

	// Issue #31
	private static final int SPACE = 5;
	private static final int CRAN = 5;
	private static final int FILE_X = 20;
	private static final int FILE_Y = 25;


    protected String oldValue = "";
    protected String referenceCommunicationName = "TMLCommunication";
    protected String communicationName = "name";
    protected String typeName = "channel";
    protected int priority = 5; // Between 0 and 10
    protected ArrayList<String> mappedElements = new ArrayList<>();

    public TMLArchiCommunicationArtifact(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
                                         TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        textX = 5;
        textY = 15;
        minWidth = 75;
        initScaling( 75, 40 );

        nbConnectingPoint = 0;
        addTGConnectingPointsComment();

        moveable = true;
        editable = true;
        removable = true;

        value = "";
        communicationName = "name";
        referenceCommunicationName = "TMLCommunication";


        makeFullValue();

        myImageIcon = IconManager.imgic702;
    }

    @Override
    public boolean isHidden() {
		//TraceManager.addDev("Archi task artifact: Am I hidden?" + getValue());
		boolean ret = false;
		if (tdp != null) {
		    if (tdp instanceof TMLArchiDiagramPanel) {
			ret = !(((TMLArchiDiagramPanel)(tdp)).inCurrentView(this));
			
		    }
		}
		//TraceManager.addDev("Hidden? -> " + ret);
		return ret;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int _priority) {
        priority = _priority;
    }

    @Override
    protected void internalDrawing(Graphics g) {
        if (oldValue.compareTo(value) != 0) {
            setValue(value, g);
        }

        g.drawRect(x, y, width, height);

        // Issue #31
        final int space = scale( SPACE );
        final int marginFileX = scale( SPACE + FILE_X );
        final int marginFileY = scale( SPACE + FILE_Y );
        final int marginCran = scale( SPACE + CRAN );
        
        g.drawLine(x+width-marginFileX/*space-fileX*/, y + space, x+width- marginFileX/*space-fileX*/, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width-marginFileX/*space-fileX*/, y + space, x+width- marginCran/*space-cran*/, y+space);
        g.drawLine(x+width- marginCran/*space-cran*/, y+space, x+width-space, y+ marginCran/*space + cran*/);
        g.drawLine(x+width-space, y+ marginCran/*space + cran*/, x+width-space, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width-space, y+ marginFileY/*space+fileY*/, x+width- marginFileX/*space-fileX*/, y+ marginFileY/*space+fileY*/);
        g.drawLine(x+width- marginCran/*space-cran*/, y+space, x+width- marginCran/*space-cran*/, y+ marginCran/*space+cran*/);
        g.drawLine(x+width- marginCran/*space-cran*/, y+ marginCran/*space+cran*/, x + width-space, y+ marginCran/*space+cran*/);

        g.drawString(value, x + textX , y + textY);

        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.ITALIC));
        g.drawString(typeName, x + textX , y + textY + scale( 20 ) );// Issue #31
        g.setFont(f);

        if (g.getColor() == ColorManager.POINTER_ON_ME_0) {
            TDiagramPanel tdp = getTDiagramPanel();
            TGComponent tgc;
            if (tdp != null) {
                for(String ss: mappedElements) {
                            if (ss.length() > 0) {
                                ListIterator<TGComponent> iterator = tdp.getComponentList().listIterator();

                                while(iterator.hasNext()) {
                                    tgc = iterator.next();

                                    if (tgc instanceof TMLArchiNode) {
                                        //TraceManager.addDev("Testing |" + tgc.getName() + "|  vs | " + s + "|");
                                        if (tgc.getName().compareTo(ss) == 0) {
                                            //TraceManager.addDev("Ok");
                                            GraphicLib.dashedLine(g, getX() + getWidth()/2, getY() + getHeight()/2, tgc.getX() + tgc.getWidth()/2, tgc.getY() + tgc.getHeight()/2);
                                        }
                                    }
                                }

                    }
                }
            }
        }
    }

    public void setValue(String val, Graphics g) {
        oldValue = value;
        int w  = g.getFontMetrics().stringWidth(value);

        // Issue #31
        final int marginFileX = scale( SPACE + FILE_X );

        int w1 = Math.max(minWidth, w + 2 * textX + marginFileX /*fileX + space*/);

        //
        if (w1 != width) {
            width = w1;
            resizeWithFather();
        }
        //
    }

    @Override
    public void resizeWithFather() {
        if ((father != null) && (father instanceof TMLArchiCommunicationNode)) {
            //
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            //setCd(Math.min(x, father.getWidth() - getWidth()), Math.min(y, father.getHeight() - getHeight()));
            setMoveCd(x, y);
        }
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String tmp;
        boolean error = false;

        JDialogCommunicationArtifact dialog = new JDialogCommunicationArtifact(frame, "Setting channel artifact attributes", this,
                mappedElements);
        GraphicLib.centerOnParent(dialog, 800, 700);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getReferenceCommunicationName() == null) {
            return false;
        }

        if (dialog.getReferenceCommunicationName().length() != 0) {
            tmp = dialog.getReferenceCommunicationName();
            referenceCommunicationName = tmp;

        }

        if (dialog.getCommunicationName().length() != 0) {
            tmp = dialog.getCommunicationName();

            if (!TAttribute.isAValidId(tmp, false, false, false, false, false)) {
                error = true;
            } else {
                communicationName = tmp;
            }
        }

        if (dialog.getTypeName().length() != 0) {
            typeName = dialog.getTypeName();
        }

        priority = dialog.getPriority();
        TraceManager.addDev("Priority is now: " + priority);

        ((TMLArchiDiagramPanel)tdp).setPriority(getFullValue(), priority);

        mappedElements = dialog.getMappedElements();

        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Name is non-valid",
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

        makeFullValue();

        return !error;

    }

    private void makeFullValue() {
        value = referenceCommunicationName + "::" + communicationName;
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public int getType() {
        return TGComponentManager.TMLARCHI_COMMUNICATION_ARTIFACT;
    }

    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info value=\"" + value + "\" communicationName=\"" + communicationName + "\" referenceCommunicationName=\"");
        sb.append(referenceCommunicationName);
        sb.append("\" mappedElements=\"");
        String me = "";
        for(String s: mappedElements) {
            me += " " + s;
        }
        me = me.trim();
        sb.append(me);
        sb.append("\" priority=\"");
        sb.append(priority);
        sb.append("\" typeName=\"" + typeName);
        sb.append("\" />\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }

    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        //
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            String svalue = null, sname = null, sreferenceCommunication = null, stype = null;
            String prio = null;

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
                            if (elt.getTagName().equals("info")) {
                                svalue = elt.getAttribute("value");
                                sname = elt.getAttribute("communicationName");
                                sreferenceCommunication = elt.getAttribute("referenceCommunicationName");
                                stype = elt.getAttribute("typeName");
                                prio = elt.getAttribute("priority");
                                String me = elt.getAttribute("mappedElements");
                                String[] mes = me.split(" ");
                                for(String s: mes) {
                                    mappedElements.add(s);
                                }
                            }
                            if (svalue != null) {
                                value = svalue;
                            }
                            if (sname != null){
                                communicationName = sname;
                            }
                            if (sreferenceCommunication != null) {
                                referenceCommunicationName = sreferenceCommunication;
                            }
                            if (stype != null){
                                typeName = stype;
                            }

                            if ((prio != null) && (prio.trim().length() > 0)) {
                                priority = Integer.decode(prio).intValue();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            
            throw new MalformedModelingException( e );
        }
        
        makeFullValue();
    }

    public DesignPanel getDesignPanel() {
        return tdp.getGUI().getDesignPanel(value);
    }

    public String getReferenceCommunicationName() {
        return referenceCommunicationName;
    }

    public void setReferenceCommunicationName(String _referenceCommunicationName) {
        referenceCommunicationName = _referenceCommunicationName;
        makeFullValue();
    }

    public String getCommunicationName() {
        return communicationName;
    }
    public void setCommunicationName(String _communicationName) {
        communicationName = _communicationName;
        makeFullValue();
    }

    public ArrayList<String> getOtherCommunicationNames() {
        return mappedElements;
    }

    public void addNewOtherCommunicationNames(ArrayList<String> _mappedElements) {
        for(String mappedElem: _mappedElements) {
            if (!mappedElements.contains(mappedElem)) {
                mappedElements.add(mappedElem);
            }
        }
        makeFullValue();
    }

    public String getFullValue() {
        String tmp = getValue();
        tmp += " (" + getTypeName() + ")";
        return tmp;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getAttributes() {
        return "Priority = " + priority;
    }
}
