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


package ui.avatarrd;

import myutil.TraceManager;
import org.json.JSONObject;
import org.json.JSONArray;

import myutil.NameChecker;
import ui.*;

import java.awt.*;
import java.util.*;

/**
 * Class AvatarRDPanel
 * Panel for drawing Avatar requirement diagrams
 * Creation: 20/04/2010
 *
 * @author Ludovic APVRILLE
 * @version 1.0 20/04/2010
 */
public class AvatarRDPanel extends TDiagramPanel implements TDPWithAttributes, NameChecker.SystemWithNamedElements {
    private static String CR = "\n";

    public Vector validated, ignored;

    public AvatarRDPanel(MainGUI mgui, TToolBar _ttb) {
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
        return "<AvatarRDPanel name=\"" + name + "\"" + sizeParam() + zoomParam() + " >";
    }
    
    @Override
    public String getXMLTail() {
        return "</AvatarRDPanel>";
    }
    
    @Override
    public String getXMLSelectedHead() {
        return "<AvatarRDPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel +
                "\"" + zoomParam()  + ">";
    }
    
    @Override
    public String getXMLSelectedTail() {
        return "</AvatarRDPanelCopy>";
    }
    
    @Override
    public String getXMLCloneHead() {
        return "<AvatarRDPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 +
                "\"" + zoomParam()  + ">";
    }
    
    @Override
    public String getXMLCloneTail() {
        return "</AvatarRDPanelCopy>";
    }


    public void makePostLoadingProcessing() throws MalformedModelingException {

    }
    
    /*public int nbOfVerifyStartingAt(TGComponent tgc) {
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc1, tgc2;
        TGConnectingPoint p;
        
        int cpt = 0;
        
        while(iterator.hasNext()) {
            tgc1 = (TGComponent)(iterator.next());
            if (tgc1 instanceof TGConnectorVerify) {
                p = ((TGConnectorVerify)(tgc1)).getTGConnectingPointP1();
                if (tgc.belongsToMeOrSon(p) != null) {
                    cpt ++;
                }
            }
        }
        
        return cpt;
    }*/

    public LinkedList<TGComponent> getAllRequirements() {
        LinkedList<TGComponent> list = new LinkedList<TGComponent>();
        TGComponent tgc;

        ListIterator iterator = getComponentList().listIterator();

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            if (tgc instanceof AvatarRDRequirement) {
                list.add(tgc);
            }
        }

        return list;

    }

    public AvatarRDRequirement getReqWithName(String name) {
        for(TGComponent tgc: componentList) {
            if (tgc instanceof AvatarRDRequirement) {
                if (tgc.getValue().compareTo(name) == 0) {
                    return (AvatarRDRequirement)tgc;
                }
            }
        }
        return null;
    }

    public AvatarRDRequirement getReqWithId(String id) {
        for(TGComponent tgc: componentList) {
            if (tgc instanceof AvatarRDRequirement) {
                if (  ((AvatarRDRequirement)(tgc)).getReqID().compareTo(id) == 0) {
                    return (AvatarRDRequirement)tgc;
                }
            }
        }
        return null;
    }

    public AvatarRDRequirement addRequirement(String _name, String _id) {
        // Find a less populated location, closer to (minX, minY)
        Point p = findLessPopulatedLocation(200, 120);

        AvatarRDRequirement rdReq = new AvatarRDRequirement(p.x, p.y, getMinX(), getMaxX(), getMinY(), getMaxY(), true, null, this);
        rdReq.setValue(_name);
        rdReq.setReqID(_id);
        rdReq.adaptItsSizeAndWrapText();
        addBuiltComponent(rdReq);


        return rdReq;
    }
    
    /*public boolean isLinkedByVerifyTo(TGComponent tgc1, TGComponent tgc2) {
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2;
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof TGConnectorVerify) {
                p1 = ((TGConnectorVerify)(tgc)).getTGConnectingPointP1();
                p2 = ((TGConnectorVerify)(tgc)).getTGConnectingPointP2();
                if ((tgc1.belongsToMeOrSon(p1) != null) && (tgc2.belongsToMeOrSon(p2)!=null)) {
                    return true;
                }
            }
        }
        
        return false;
    }*/
    
    @Override
    public void enhance() {
        autoAdjust();
    }

    //
    // For requirement table defined in RequirementsTableModel
    //

    public ArrayList<AvatarRDProperty> getAllPropertiesVerify(TGComponent req) {
        ArrayList<AvatarRDProperty> listOfProps = new ArrayList<>();

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDVerifyConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDVerifyConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDVerifyConnector) (tgc)).getTGConnectingPointP2();
                pother = null;
                if (req.belongsToMe(p1)) {
                    pother = p2;
                    //TraceManager.addDev("pother = p2");
                }
                if (req.belongsToMe(p2)) {
                    pother = p1;
                    //TraceManager.addDev("pother = p1");
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDProperty) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDProperty) foundC);
                    }
                }
            }
        }

        return listOfProps;

    }

    public ArrayList<AvatarRDElementReference> getAllElementsSatified(TGComponent req) {
        ArrayList<AvatarRDElementReference> listOfProps = new ArrayList<>();

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDSatisfyConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDSatisfyConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDSatisfyConnector) (tgc)).getTGConnectingPointP2();
                pother = null;
                if (req.belongsToMe(p1)) {
                    pother = p2;
                    //TraceManager.addDev("pother = p2");
                }
                if (req.belongsToMe(p2)) {
                    pother = p1;
                    //TraceManager.addDev("pother = p1");
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDElementReference) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDElementReference) foundC);
                    }
                }
            }
        }

        return listOfProps;

    }

    public ArrayList<AvatarRDRequirement> getAllImmediateSons(TGComponent req) {
        HashSet<TGComponent> met = new HashSet<>();
        return getAllImmediateSons(req, met);
    }

    public ArrayList<AvatarRDRequirement> getAllImmediateSons(TGComponent req, HashSet<TGComponent> met) {
        ArrayList<AvatarRDRequirement> listOfProps = new ArrayList<>();

        if (met.contains(req)) {
            return listOfProps;
        }

        met.add(req);

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDCompositionConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDCompositionConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDCompositionConnector) (tgc)).getTGConnectingPointP2();
                pother = null;

                if (req.belongsToMe(p2)) {
                    pother = p1;
                    //TraceManager.addDev("pother = p1");
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDRequirement) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDRequirement) foundC);
                    }
                    if (foundC instanceof AvatarRDRequirementReference) {
                        AvatarRDRequirement refReq = ((AvatarRDRequirementReference)foundC).getReference();
                        if (refReq != null) {
                            listOfProps.add(refReq);
                        }
                        listOfProps.addAll(getAllImmediateSons(foundC, met));
                    }
                }
            }
        }

        if (req instanceof AvatarRDRequirement) {
            AvatarRDRequirement myReq = (AvatarRDRequirement)req;
            for (AvatarRDRequirementReference ref : myReq.getAllReferences()) {
                AvatarRDPanel myPanel = (AvatarRDPanel) (ref.getTDiagramPanel());
                listOfProps.addAll(myPanel.getAllImmediateSons(ref));
            }
        }

        return listOfProps;

    }


    public ArrayList<AvatarRDRequirement> getAllSons(AvatarRDRequirement req) {
        //TraceManager.addDev("\nGet all sons of: " + req);
        HashSet<AvatarRDRequirement> met = new HashSet<>();
        met.add(req);

        ArrayList<AvatarRDRequirement> listOfSons = new ArrayList<>();
        ArrayList<AvatarRDRequirement> tmpList = new ArrayList<>();
        ArrayList<AvatarRDRequirement> tmpNew = new ArrayList<>();
        tmpList.add(req);

        for (; ; ) {
            tmpNew.clear();
            for (AvatarRDRequirement rq : tmpList) {
                tmpNew.addAll(rq.getAllImmediateSons());
            }
            tmpList.clear();
            for (AvatarRDRequirement rq : tmpNew) {
                if (!(met.contains(rq))) {
                    //TraceManager.addDev("Adding req:" + rq);
                    met.add(rq);
                    listOfSons.add(rq);
                    tmpList.add(rq);
                    if (rq.getAllReferences().size() > 0) {
                        //TraceManager.addDev("Adding references of " + rq);
                    }
                    for (AvatarRDRequirementReference ref : rq.getAllReferences()) {
                        AvatarRDRequirement newReq = ref.getReference();
                        //TraceManager.addDev("Adding son of references = " + newReq);
                        if (newReq != null) {
                            if (!(met.contains(newReq))) {
                                listOfSons.add(newReq);
                                tmpList.add(newReq);
                            }
                        }
                    }


                }
            }
            if (tmpList.size() == 0) {
                break;
            }
        }

        return listOfSons;
    }

    public ArrayList<AvatarRDRequirement> getAllImmediateFathers(TGComponent req) {
        HashSet<TGComponent> met = new HashSet<>();
        return getAllImmediateFathers(req, met);
    }


    public ArrayList<AvatarRDRequirement> getAllImmediateFathers(TGComponent req, HashSet<TGComponent> met) {

        ArrayList<AvatarRDRequirement> listOfProps = new ArrayList<>();

        if (met.contains(req)) {
            return listOfProps;
        }

        met.add(req);

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDCompositionConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDCompositionConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDCompositionConnector) (tgc)).getTGConnectingPointP2();
                pother = null;

                if (req.belongsToMe(p1)) {
                    pother = p2;
                    //TraceManager.addDev("pother = p1");
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDRequirement) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDRequirement) foundC);
                    }
                    if (foundC instanceof AvatarRDRequirementReference) {
                        AvatarRDRequirement refReq = ((AvatarRDRequirementReference)foundC).getReference();
                        if (refReq != null) {
                            listOfProps.add(refReq);
                        }
                        listOfProps.addAll(getAllImmediateFathers(foundC, met));
                    }
                }
            }
        }

        if (req instanceof AvatarRDRequirement) {
            AvatarRDRequirement myReq = (AvatarRDRequirement)req;
            for (AvatarRDRequirementReference ref : myReq.getAllReferences()) {
                AvatarRDPanel myPanel = (AvatarRDPanel) (ref.getTDiagramPanel());
                listOfProps.addAll(myPanel.getAllImmediateFathers(ref));
            }
        }

        return listOfProps;

    }

    public ArrayList<AvatarRDRequirement> getAllFathers(AvatarRDRequirement req) {
        HashSet<AvatarRDRequirement> met = new HashSet<>();
        met.add(req);

        ArrayList<AvatarRDRequirement> listOfSons = new ArrayList<>();
        ArrayList<AvatarRDRequirement> tmpList = new ArrayList<>();
        ArrayList<AvatarRDRequirement> tmpNew = new ArrayList<>();
        tmpList.add(req);

        for (; ; ) {
            tmpNew.clear();
            for (AvatarRDRequirement rq : tmpList) {
                tmpNew.addAll(getAllImmediateFathers(rq));
            }
            tmpList.clear();

            for (AvatarRDRequirement rq : tmpNew) {
                if (!(met.contains(rq))) {
                    //TraceManager.addDev("Adding req:" + rq);
                    met.add(rq);
                    listOfSons.add(rq);
                    tmpList.add(rq);
                    if (rq.getAllReferences().size() > 0) {
                        //TraceManager.addDev("Adding references of " + rq);
                    }
                    for (AvatarRDRequirementReference ref : rq.getAllReferences()) {
                        AvatarRDRequirement newReq = ref.getReference();
                        //TraceManager.addDev("Adding son of references = " + newReq);
                        if (newReq != null) {
                            if (!(met.contains(newReq))) {
                                listOfSons.add(newReq);
                                tmpList.add(newReq);
                            }
                        }
                    }


                }
            }

            /*for (AvatarRDRequirement rq : tmpNew) {
                if (!(met.contains(rq))) {
                    met.add(rq);
                    listOfSons.add(rq);
                    tmpList.add(rq);
                }
            }*/

            if (tmpList.size() == 0) {
                break;
            }
        }

        return listOfSons;
    }


    public ArrayList<AvatarRDRequirement> getAllMeRefine(AvatarRDRequirement req, int origin) {
        ArrayList<AvatarRDRequirement> listOfProps = new ArrayList<>();

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDRefineConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDRefineConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDRefineConnector) (tgc)).getTGConnectingPointP2();
                pother = null;

                if (origin == 0) {
                    if (req.belongsToMe(p1)) {
                        pother = p2;
                        //TraceManager.addDev("pother = p1");
                    }
                } else {
                    if (req.belongsToMe(p2)) {
                        pother = p1;
                        //TraceManager.addDev("pother = p1");
                    }
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDRequirement) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDRequirement) foundC);
                    }
                }
            }
        }

        return listOfProps;

    }

    public ArrayList<AvatarRDRequirement> getAllMeDerive(AvatarRDRequirement req, int origin) {
        ArrayList<AvatarRDRequirement> listOfProps = new ArrayList<>();

        // We parse all AvatarRDVerifyConnector
        ListIterator iterator = getComponentList().listIterator();
        TGComponent tgc;
        TGConnectingPoint p1, p2, pother;

        while (iterator.hasNext()) {
            tgc = (TGComponent) (iterator.next());
            //TraceManager.addDev("Considering component=" + tgc);
            if (tgc instanceof AvatarRDDeriveConnector) {
                //TraceManager.addDev("Connector verify");
                p1 = ((AvatarRDDeriveConnector) (tgc)).getTGConnectingPointP1();
                p2 = ((AvatarRDDeriveConnector) (tgc)).getTGConnectingPointP2();
                pother = null;

                if (origin == 0) {
                    if (req.belongsToMe(p1)) {
                        pother = p2;
                        //TraceManager.addDev("pother = p1");
                    }
                } else {
                    if (req.belongsToMe(p2)) {
                        pother = p1;
                        //TraceManager.addDev("pother = p1");
                    }
                }
                //TraceManager.addDev("pother computed");

                //TraceManager.addDev("pother=" + pother);

                if (pother != null) {
                    TGComponent foundC = getComponentToWhichBelongs(pother);
                    //TraceManager.addDev("FoundC=" + foundC);
                    if (foundC instanceof AvatarRDRequirement) {
                        //TraceManager.addDev("Adding foundC");
                        listOfProps.add((AvatarRDRequirement) foundC);
                    }
                }
            }
        }

        return listOfProps;

    }

    public void updateReferences() {
        ListIterator iterator = getComponentList().listIterator();
        while (iterator.hasNext()) {
            TGComponent tgc = (TGComponent) (iterator.next());
            if (tgc instanceof AvatarRDRequirementReference) {
                ((AvatarRDRequirementReference)tgc).updateReference();
            }
        }
    }

    private void addIfNotExcluded(StringBuffer sb, String s, String category, String [] exclusions) {
        if (exclusions != null) {
            for (int i = 0; i < exclusions.length; i++) {
                if (exclusions[i].compareTo(category) == 0) {
                    return;
                }
            }
        }
        sb.append(s);
    }

    public StringBuffer toSysMLV2Text(String [] exclusions) {
        StringBuffer sb = new StringBuffer();
        for(TGComponent component: getComponentList()) {
            if (component instanceof AvatarRDRequirement) {
                AvatarRDRequirement req = (AvatarRDRequirement)component;
                sb.append("requirement " + req.getValue() + CR);
                addIfNotExcluded(sb, "\ttext: " + req.getText() + CR , "text", exclusions);
                addIfNotExcluded(sb, "\ttype: " + req.getKind() + CR, "type", exclusions);

                for(TGComponent relation: getComponentList()) {
                    if (relation instanceof TGConnector) {
                        if ( (relation instanceof AvatarRDRefineConnector) || (relation instanceof AvatarRDCompositionConnector)
                                || (relation instanceof AvatarRDDeriveConnector)) {
                            if (((TGConnector) relation).getTGConnectingPointP1().getFather() == req) {
                                if (((TGConnector)relation).getTGConnectingPointP2().getFather() instanceof AvatarRDRequirement) {
                                    AvatarRDRequirement req2 =
                                            (AvatarRDRequirement)(((TGConnector)relation).getTGConnectingPointP2().getFather());
                                    String relationS = "refine";
                                    if (relation instanceof AvatarRDCompositionConnector) {
                                        relationS = "compose";
                                    } else if (relation instanceof AvatarRDDeriveConnector) {
                                        relationS = "derive";
                                    }
                                    sb.append("\t" + relationS + ": " + req2.getValue() + CR);
                                }
                            }
                        }
                    }
                }
                sb.append("end requirement " + CR + CR);
            }
        }
        return sb;
    }


    /**
     *
     * @param _spec: the json specification
     *            Elements of a requirement: name, id, doc, category, refine, derive, compose
     *            {
     *   "requirements": [
     *     {
     *       "name": "REQ1",
     *       "text": "The system shall be able to autonomously navigate through a warehouse.",
     *       "compose": ["REQ5", "REQ6"]
     *     },
     *     {
     *       "name": "REQ2",
     *       "text": "The system shall include a map of the warehouse to navigate through.",
     *       "refine": ["REQ1"],
     *       "derive": ["REQ3"]
     *     },
     *
     */
    public void loadAndUpdateFromText(String _spec) throws org.json.JSONException {

        // cut before and after what is not part of the json array
        int indexStart = _spec.indexOf('[');
        int indexStop = _spec.lastIndexOf(']');

        if ((indexStart == -1) || (indexStop == -1) || (indexStart > indexStop)) {
            throw new org.json.JSONException("Invalid JSON array");
        }

        _spec = _spec.substring(indexStart, indexStop+1);



        TraceManager.addDev("Cut spec: " + _spec);

        JSONArray reqArray = new JSONArray(_spec);
        for(int i=0; i<reqArray.length(); i++) {
            String name = reqArray.getJSONObject(i).getString("name");
            String id = reqArray.getJSONObject(i).getString("id");
            TraceManager.addDev("Handling requirements " + name + " / " + id);

            // If no requirement has this name, nor id, we create a new one.
            // If a requirement has the same id xor the same name, we update it
            // If a requirement has both, we simply select it

            name = name.trim().replaceAll(" ", "_");

            AvatarRDRequirement reqName = getReqWithName(name);
            AvatarRDRequirement reqId = getReqWithId(id);
            AvatarRDRequirement selectedReq = null;

            if (reqName != null) {
                if (reqId != null) {
                    if (reqName == reqId) {
                        selectedReq = reqName;
                    } else {
                        // Incoherency situation. We assume the name goes first
                        selectedReq = reqName;
                        selectedReq.setReqID(id);
                    }
                } else {
                    selectedReq = reqName;
                    selectedReq.setReqID(id);
                }
            } else if (reqId != null) {
                // We know that the name is null
                // We update the name of the requirement
                selectedReq = reqId;
                selectedReq.setValue(name);

            } else {
                if (selectedReq == null) {
                    // We create a new Requirement
                    selectedReq = addRequirement(name, id);
                }
            }

            // We complete other values
            String doc = reqArray.getJSONObject(i).getString("doc");
            selectedReq.setText(doc);


        }

        // 2nd parsing: relations
        for(int i=0; i<reqArray.length(); i++) {
            String name = reqArray.getJSONObject(i).getString("name");
            name = name.trim().replaceAll(" ", "_");
            AvatarRDRequirement reqName = getReqWithName(name);

            if (reqName != null) {
                String [] relations = {"compose", "derive", "refine"};
                for (int j=0; j<relations.length; j++) {
                    JSONArray rel = reqArray.getJSONObject(i).getJSONArray(relations[j]);
                    for(int k=0; k<rel.length(); k++) {
                        String nameOther = rel.getString(k);
                        AvatarRDRequirement reqOther = getReqWithName(nameOther.trim().replaceAll(" ", "_"));
                        if (reqOther != null) {
                            addRelation(relations[j], reqName, reqOther);
                        }
                    }
                }

            }
        }



    }

    public void addRelation(String relation, AvatarRDRequirement reqOrigin, AvatarRDRequirement reqDest) {

        TraceManager.addDev("Adding " + relation + " between " + reqOrigin.getValue() + " and " + reqDest.getValue());

        TGConnectingPoint p1 = reqOrigin.closerFreeTGConnectingPoint(reqDest.getX(), reqDest.getY(), true);
        TGConnectingPoint p2 = reqDest.closerFreeTGConnectingPoint(reqOrigin.getX(), reqOrigin.getY(), false);

        if ( (p1 != null) && (p2 != null) ) {
            TGConnector conn = null;
            if (relation.compareTo("compose") == 0) {
                conn = new AvatarRDCompositionConnector(reqOrigin.getX(), reqOrigin.getY(), reqOrigin.getCurrentMinX(), reqOrigin.getCurrentMinY(),
                        reqOrigin.getCurrentMaxX(), reqOrigin.getCurrentMaxY(), true, null, reqOrigin.getTDiagramPanel(),
                        p1, p2, null);
            } else if (relation.compareTo("refine") == 0) {
                conn = new AvatarRDRefineConnector(reqOrigin.getX(), reqOrigin.getY(), reqOrigin.getCurrentMinX(), reqOrigin.getCurrentMinY(),
                        reqOrigin.getCurrentMaxX(), reqOrigin.getCurrentMaxY(), true, null, reqOrigin.getTDiagramPanel(),
                        p1, p2, null);
            } else if (relation.compareTo("derive") == 0) {
                conn = new AvatarRDDeriveConnector(reqOrigin.getX(), reqOrigin.getY(), reqOrigin.getCurrentMinX(), reqOrigin.getCurrentMinY(),
                        reqOrigin.getCurrentMaxX(), reqOrigin.getCurrentMaxY(), true, null, reqOrigin.getTDiagramPanel(),
                        p1, p2, null);
            }

            if (conn != null) {
                p1.setFree(false);
                p2.setFree(false);
                reqOrigin.getTDiagramPanel().addComponent(conn, p1.getX(), p1.getY(), false, true);
            }
        }

    }


}







