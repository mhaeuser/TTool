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




package ui.avatardd;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogADDCrossbarNode;

import javax.swing.*;
import java.awt.*;

/**
   * Class ADDCrossbarNode
   * Node. To be used in Avatar Deployment Diagram
   * Creation: 30/06/2014
   * @version 1.0 30/06/2014
   * @author Ludovic APVRILLE
 */
public class ADDCrossbarNode extends ADDCommunicationNode implements WithAttributes {
    private int textY1 = 15;
    private int textY2 = 30;
    private int derivationx = 2;
    private int derivationy = 3;
    private String stereotype = "CROSSBAR";

    /* private int index = 0;
    private int nbOfAttachedInitiators = 0;
    private int nbOfAttachedTargets = 0;*/
    private int cluster_index = 0;
    private int cluster_address = 0;



    public ADDCrossbarNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        width = 250;
        height = 50;
        minWidth = 100;
        minHeight = 50;

        nbConnectingPoint = 16;
        connectingPoint = new TGConnectingPoint[16];

        connectingPoint[0] = new ADDConnectingPoint(this, 0, 0, true, false, 0.0, 0.0);
        connectingPoint[1] = new ADDConnectingPoint(this, 0, 0, true, false, 0.5, 0.0);
        connectingPoint[2] = new ADDConnectingPoint(this, 0, 0, true, false, 1.0, 0.0);
        connectingPoint[3] = new ADDConnectingPoint(this, 0, 0, true, false, 0.0, 0.5);
        connectingPoint[4] = new ADDConnectingPoint(this, 0, 0, true, false, 1.0, 0.5);
        connectingPoint[5] = new ADDConnectingPoint(this, 0, 0, true, false, 0.0, 1.0);
        connectingPoint[6] = new ADDConnectingPoint(this, 0, 0, true, false, 0.5, 1.0);
        connectingPoint[7] = new ADDConnectingPoint(this, 0, 0, true, false, 1.0, 1.0);

        connectingPoint[8] = new ADDConnectingPoint(this, 0, 0, true, false, 0.25, 0.0);
        connectingPoint[9] = new ADDConnectingPoint(this, 0, 0, true, false, 0.75, 0.0);
        connectingPoint[10] = new ADDConnectingPoint(this, 0, 0, true, false, 0.0, 0.25);
        connectingPoint[11] = new ADDConnectingPoint(this, 0, 0, true, false, 1.0, 0.25);
        connectingPoint[12] = new ADDConnectingPoint(this, 0, 0, true, false, 0.0, 0.75);
        connectingPoint[13] = new ADDConnectingPoint(this, 0, 0, true, false, 1.0, 0.75);
        connectingPoint[14] = new ADDConnectingPoint(this, 0, 0, true, false, 0.25, 1.0);
        connectingPoint[15] = new ADDConnectingPoint(this, 0, 0, true, false, 0.75, 1.0);

        addTGConnectingPointsComment();

        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;

        name = tdp.findNodeName("Crossbar");
        value = "name";

        myImageIcon = IconManager.imgic700;
    }

    public void internalDrawing(Graphics g) {
        Color c = g.getColor();
        g.draw3DRect(x, y, width, height, true);


        // Top lines
        g.drawLine(x, y, x + derivationx, y - derivationy);
        g.drawLine(x + width, y, x + width + derivationx, y - derivationy);
        g.drawLine(x + derivationx, y - derivationy, x + width + derivationx, y - derivationy);

        // Right lines
        g.drawLine(x + width, y + height, x + width + derivationx, y - derivationy + height);
        g.drawLine(x + derivationx + width, y - derivationy, x + width + derivationx, y - derivationy + height);

        // Filling color
        g.setColor(ColorManager.BUS_BOX);
        g.fill3DRect(x+1, y+1, width-1, height-1, true);
        g.setColor(c);

        // Strings
        String ster = "<<" + stereotype + ">>";
        int w  = g.getFontMetrics().stringWidth(ster);
        Font f = g.getFont();
        g.setFont(f.deriveFont(Font.BOLD));
        drawSingleString(g, ster, x + (width - w)/2, y + textY1);
        g.setFont(f);
        w  = g.getFontMetrics().stringWidth(name);
        drawSingleString(g, name, x + (width - w)/2, y + textY2);

        // Icon
        //g.drawImage(IconManager.imgic1102.getImage(), x + width - 20, y + 4, null);
        g.drawImage(IconManager.imgic1102.getImage(), x + 4, y + 4, null);
        //g.drawImage(IconManager.img9, x + width - 20, y + 4, null);
    }

    public TGComponent isOnOnlyMe(int x1, int y1) {

        Polygon pol = new Polygon();
        pol.addPoint(x, y);
        pol.addPoint(x + derivationx, y - derivationy);
        pol.addPoint(x + derivationx + width, y - derivationy);
        pol.addPoint(x + derivationx + width, y + height - derivationy);
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

    public boolean editOnDoubleClick(JFrame frame) {
        boolean error = false;
        String errors = "";
        int tmp;
        String tmpName;

        JDialogADDCrossbarNode dialog = new JDialogADDCrossbarNode(frame, "Setting crossbar attributes", this);
        //dialog.setSize(500, 450);
        GraphicLib.centerOnParent(dialog, 500, 450);
        dialog.setVisible( true ); // blocked until dialog has been closed

        if (!dialog.isRegularClose()) {
            return false;
        }

        if (dialog.getNodeName().length() != 0) {
            tmpName = dialog.getNodeName();
            tmpName = tmpName.trim();
            if (!TAttribute.isAValidId(tmpName, false, false, false)) {
                error = true;
                errors += "Name of the node  ";
            } else {
                name = tmpName;
            }
        }

	/*     if (dialog.getIndex().length() != 0) {
            try {
                tmp = index;
                index = Integer.decode(dialog.getIndex()).intValue();
                if (index < 0) {
                    index = tmp;
                    error = true;
                    errors += "index ";
                }
            } catch (Exception e) {
                error = true;
                errors += "index  ";
            }
        }*/

	/*	   if (dialog.getNbOfAttachedInitiators().length() != 0) {
            try {
                tmp = nbOfAttachedInitiators;
                nbOfAttachedInitiators = Integer.decode(dialog.getNbOfAttachedInitiators()).intValue();
                if (nbOfAttachedInitiators < 0) {
                    nbOfAttachedInitiators = tmp;
                    error = true;
                    errors += "nbOfAttachedInitiators  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "nbOfAttachedInitiators  ";
            }
        }*/


	   /*        if (dialog.getNbOfAttachedTargets().length() != 0) {
            try {
                tmp = nbOfAttachedTargets;
                nbOfAttachedTargets = Integer.decode(dialog.getNbOfAttachedTargets()).intValue();
                if (nbOfAttachedTargets < 0) {
                    nbOfAttachedTargets = tmp;
                    error = true;
                    errors += "nbOfAttachedTargets  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "nbOfAttachedTargets  ";
            }
	    }*/

        if (dialog.getClusterIndex().length() != 0) {
            try {
                tmp = cluster_index;
                cluster_index = Integer.decode(dialog.getClusterIndex()).intValue();
                if (cluster_index < 0) {
                    cluster_index = tmp;
                    error = true;
                    errors += "cluster_index  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "cluster_index  ";
            }
        }

        if (dialog.getClusterAddress().length() != 0) {
            try {
                tmp = cluster_address;
                cluster_address = Integer.decode(dialog.getClusterAddress()).intValue();
                if (cluster_address < 0) {
                    cluster_address = tmp;
                    error = true;
                    errors += "cluster_address  ";
                }
            } catch (Exception e) {
                error = true;
                errors += "cluster_address ";
            }
        }


        if (error) {
            JOptionPane.showMessageDialog(frame,
                                          "Invalid value for the following attributes: " + errors,
                                          "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }


    public int getType() {
        return TGComponentManager.ADD_CROSSBARNODE;
    }

    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info stereotype=\"" + stereotype + "\" nodeName=\"" + name);
        sb.append("\" />\n");
        //sb.append("<attributes index=\"" + index + "\" ");
        //sb.append(" nbOfAttachedInitiators=\"" + nbOfAttachedInitiators + "\" ");

        //sb.append(" nbOfAttachedTargets=\"" + nbOfAttachedTargets + "\" ");
        sb.append("<attributes cluster_index=\"" + cluster_index + "\" ");
        sb.append(" cluster_address=\"" + cluster_address + "\" ");
        sb.append("/>\n");
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
          //  int t1id;
            String sstereotype = null, snodeName = null;

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
                                sstereotype = elt.getAttribute("stereotype");
                                snodeName = elt.getAttribute("nodeName");
                            }
                            if (sstereotype != null) {
                                stereotype = sstereotype;
                            }
                            if (snodeName != null){
                                name = snodeName;
                            }

                            if (elt.getTagName().equals("attributes")) {
				//   index = Integer.decode(elt.getAttribute("index")).intValue();
				//     nbOfAttachedInitiators =Integer.decode(elt.getAttribute("nbOfAttachedInitiators")).intValue();
				//    nbOfAttachedTargets = Integer.decode(elt.getAttribute("nbOfAttachedTargets")).intValue();
                                cluster_index = Integer.decode(elt.getAttribute("cluster_index")).intValue();
                                cluster_address = Integer.decode(elt.getAttribute("cluster_address")).intValue();

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new MalformedModelingException();
        }
    }

    public String getAttributes() {
        String attr = "";
        //attr += "index = " + index + "\n";
        //attr += "nbOfAttachedInitiators = " + nbOfAttachedInitiators + "\n";
        //attr += "nbOfAttachedTargets = " + nbOfAttachedTargets + "\n";
        attr += "cluster_address = " + cluster_address+ "\n";
        attr += "cluster_index= " + cluster_index + "\n";
        return attr;
    }

    public String getAttributesToFile() {
        String attr = "";
        //attr += index + "\n";
	// attr += nbOfAttachedInitiators+ "\n";
	// attr += nbOfAttachedTargets+ "\n";
        attr += cluster_index + "\n";
        attr += cluster_address + "\n";
        return attr;
    }

    //public int getIndex() { return index;}
    //public int getNbOfAttachedInitiators() { return nbOfAttachedInitiators;}
    //public int getNbOfAttachedTargets() { return nbOfAttachedTargets;}
    public int getClusterIndex() { return cluster_index;}
    public int getClusterAddress() { return cluster_address;}

    public String toString(){
        return "Crossbar";
    }

}
