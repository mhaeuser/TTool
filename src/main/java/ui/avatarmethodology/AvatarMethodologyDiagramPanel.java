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




package ui.avatarmethodology;

import org.w3c.dom.Element;
import ui.*;

/**
 * Class AvatarMethodologyDiagramPanel
 * Panel for displaying the diplodocus methodology
* Creation: 26/08/2014
* @version 1.0 26/08/2014
 * @author Ludovic APVRILLE
 */
public class AvatarMethodologyDiagramPanel extends TDiagramPanel implements TDPWithAttributes {
    //public Vector validated, ignored;
    
    public  AvatarMethodologyDiagramPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
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
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.addTClass(tgcc.getClassName());
            return true;
        }*/
        return false;
    }
    
    @Override
    public boolean actionOnRemove(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            TCDTClass tgcc = (TCDTClass)(tgc);
            mgui.removeTClass(tgcc.getClassName());
            resetAllInstancesOf(tgcc);
            return true;
        }*/
        return false;
    }
    
    @Override
    public boolean actionOnValueChanged(TGComponent tgc) {
        /*if (tgc instanceof TCDTClass) {
            return actionOnDoubleClick(tgc);
        }*/
        return false;
    }
    
    @Override
    public String getXMLHead() {
        return "<AvatarMethodologyDiagramPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
    }
    
    @Override
    public String getXMLTail() {
        return "</AvatarMethodologyDiagramPanel>";
    }
    
    @Override
    public String getXMLSelectedHead() {
        return "<AvatarMethodologyDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    @Override
    public String getXMLSelectedTail() {
        return "</AvatarMethodologyDiagramPanelCopy>";
    }
    
    @Override
    public String getXMLCloneHead() {
        return "<AvatarMethodologyDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    @Override
    public String getXMLCloneTail() {
        return "</AvatarMethodologyDiagramPanelCopy>";
    }
    
    
    public void makePostLoadingProcessing() throws MalformedModelingException {
        
    }
	
    @Override
	public void enhance() {
		autoAdjust();
    }
    
    public void loadExtraParameters(Element elt) {
    }
}







