/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class SysmlsecMethodologyReferenceToDesign
   * Diagram reference to the main sysmlsecdesign: Used to reference diagrams from the
   * Sysmlsec methodology
   * Creation: 26/01/2016
   * @version 1.0 26/01/2016
   * @author Ludovic APVRILLE
   * @see
   */

package ui.sysmlsecmethodology;


import ui.*;

public class SysmlsecMethodologyReferenceToDesign extends SysmlsecMethodologyDiagramReference  {


    public SysmlsecMethodologyReferenceToDesign(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        initScaling(200, 70);

        nbConnectingPoint = 2;
        connectingPoint = new TGConnectingPoint[nbConnectingPoint];
        connectingPoint[0] = new SysmlsecMethodologyConnectingPoint(this, 0, 0, false, true, 0.0, 0.5, TGConnectingPoint.WEST);
        connectingPoint[1] = new SysmlsecMethodologyConnectingPoint(this, 0, 0, false, true, 0.20, 1.0, TGConnectingPoint.WEST);

        typeOfReference = DESIGN;

        addTGConnectingPointsCommentTop();

    }

    public  int getType() {
        return TGComponentManager.SYSMLSEC_METHODOLOGY_REF_DESIGN;
    }

    public boolean isAValidPanelType(TURTLEPanel panel) {
        if (panel instanceof AvatarDesignPanel) {
            return true;
        }

        return false;
    }

    public void makeValidationInfos(SysmlsecMethodologyDiagramName dn) {
        dn.setValidationsNumber(4);
        dn.setValidationsInfo(0, SysmlsecMethodologyDiagramName.SIM_ANIM);
        dn.setValidationsInfo(1, SysmlsecMethodologyDiagramName.UPP);
        dn.setValidationsInfo(2, SysmlsecMethodologyDiagramName.PROVERIF);
        dn.setValidationsInfo(3, SysmlsecMethodologyDiagramName.INVARIANTS);
    }

    public boolean makeCall(String diagramName, int index) {
        String tmp;

        switch(index) {
        case 0:
            if (!openDiagram(diagramName)) {
                return false;
            }
            if (tdp.getMGUI().checkModelingSyntax(diagramName, true)) {
                tdp.getMGUI().avatarSimulation();
                return true;
            }
            return false;
        case 1:
            if (!openDiagram(diagramName)) {
                return false;
            }
            if (tdp.getMGUI().checkModelingSyntax(diagramName, true)) {
                tdp.getMGUI().avatarUPPAALVerification();
                return true;
            }
            return false;
        case 2:
            if (!openDiagram(diagramName)) {
                return false;
            }
            if (tdp.getMGUI().checkModelingSyntax(diagramName, true)) {
                tdp.getMGUI().avatarProVerifVerification();
                return true;
            }
            return false;
        case 3:
            if (!openDiagram(diagramName)) {
                return false;
            }
            if (tdp.getMGUI().checkModelingSyntax(diagramName, true)) {
                tdp.getMGUI().avatarStaticAnalysis();
                return true;
            }
            return false;

        default:
            return false;
        }

    }

}