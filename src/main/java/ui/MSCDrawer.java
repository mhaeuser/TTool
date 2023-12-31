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

import sddescription.*;
import ui.iod.InteractionOverviewDiagramPanel;
import ui.sd.SDInstance;
import ui.sd.SequenceDiagramPanel;
import ui.sd.TGConnectorMessageSD;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Class MSCDrawer
 * Draw textual SD modeling
 * Creation: 17/07/2009
 * @version 1.0 17/07/2009
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class MSCDrawer {
    public final static int DEC = 50;
    public final static int DEC_COMP = 30;
    public final static int INIT_X = 100;
    public final static int INIT_Y = 40;

    private MainGUI mgui;
    private HMSC hmsc;
    private MSC msc;
    private int indexAnalysis;
    private AnalysisPanel ap;
    private Vector<Instance> telements;
    private Vector<TGComponent> gelements;

    //private double radius;
    //private double centerX;
    //private double centerY;

    public MSCDrawer(MainGUI _mgui) {
        mgui = _mgui;
    }

    public void setMSC(MSC _msc) {
        msc = _msc;
    }

    public void setHMSC(HMSC _hmsc) {
        hmsc = _hmsc;
    }

    public boolean drawFromMSC(int _analysisNb) {
        telements = new Vector<>();
        gelements = new Vector<>();
        try {
            //makeDrawable();
            //
            addAnalysis(_analysisNb);
            //
            drawBasicHMSC();

            makeMSC(hmsc, msc);
            //
            //drawActivityDiagrams();
            //
            //drawRelations();
            //
        } catch (MalformedSDException msde) {
            
            return false;
        }

        return true;
    }

    private void addAnalysis(int _analysisNb) throws MalformedSDException {
        indexAnalysis = mgui.createAnalysis("Analysis " + _analysisNb);
        //
        if (indexAnalysis < 0) {
            throw new MalformedSDException("bad index");
        }
        try {
            ap = (AnalysisPanel)(mgui.tabs.elementAt(indexAnalysis));
        } catch (Exception e) {
            throw new MalformedSDException("Analysis panel not found");
        }

        ap.iodp.setMinX(10);
        ap.iodp.setMaxX(1900);
        ap.iodp.setMinY(10);
        ap.iodp.setMaxY(900);
    }

    private void drawBasicHMSC() throws MalformedSDException {
        // Creating basicHMSC : one start node + reference to SD + stop node
        // Creating tclass
        int myX, myY;

        myX = 400;
        myY = 100;
        TGComponent tgc1 = TGComponentManager.addComponent(myX, myY, TGComponentManager.IOD_START_STATE, ap.iodp);
        myY += DEC;
        TGComponent tgc2 = TGComponentManager.addComponent(myX, myY, TGComponentManager.IOD_REF_SD, ap.iodp);
        tgc2.setValue(msc.getName());
        myY += DEC;
        TGComponent tgc3 = TGComponentManager.addComponent(myX, myY, TGComponentManager.IOD_STOP_STATE, ap.iodp);

        ap.iodp.addBuiltComponent(tgc1);
        ap.iodp.addBuiltComponent(tgc2);
        ap.iodp.addBuiltComponent(tgc3);

        // Links between tgc1, tgc2, tgc3
        connectHMSC(tgc1, tgc2, ap.iodp);
        connectHMSC(tgc2, tgc3, ap.iodp);
    }

    public TGConnector connectHMSC(TGComponent _tgc1, TGComponent _tgc2, InteractionOverviewDiagramPanel _iodp) throws MalformedSDException {
        // Connect both points
        TGConnectingPoint p1, p2;

        p1 = _tgc1.findFirstFreeTGConnectingPoint(true, false);
        p2 = _tgc2.findFirstFreeTGConnectingPoint(false, true);

        if ((p1 == null) || (p2 == null)) {
            throw new MalformedSDException("Could not find free connecting points");
        }
        p1.setFree(false);
        p2.setFree(false);

        TGConnector tgco = TGComponentManager.addConnector(p1.x, p1.y, TGComponentManager.CONNECTOR_INTERACTION, _iodp, p1, p2, new Vector<Point>());
        ap.iodp.addBuiltComponent(tgco);

        return tgco;
    }

    // Make the SD
    public void makeMSC(HMSC _hmsc, MSC _msc) throws MalformedSDException {
        SequenceDiagramPanel sdp;
        ListIterator iterator;
        Instance instance;
        int myX = INIT_X;
        int myY = INIT_Y;
        TGComponent tgc;
        ArrayList<SDInstance> ginstances;
        SDInstance sdi;
        //SDInstance sdi;

        // Creates the panel
        if (!ap.addSequenceDiagram(_msc.getName())) {
            throw new MalformedSDException("Could not create the SD named " + _msc.getName());
        }

        try {
            sdp = (SequenceDiagramPanel)(ap.panelAt(1));
        } catch (Exception e) {
            throw new MalformedSDException("Could not get the SD named " + _msc.getName());
        }

        // Put instances
        ginstances = new ArrayList<SDInstance>();
        iterator = _hmsc.getInstances().listIterator();
        while(iterator.hasNext()) {
            instance = (Instance)(iterator.next());
            tgc = TGComponentManager.addComponent(myX, myY, TGComponentManager.SD_INSTANCE, sdp);
            tgc.setValue(instance.getName());
            sdp.addBuiltComponent(tgc);
            ginstances.add((SDInstance)tgc);
            telements.add(instance);
            gelements.add(tgc);
            myX += (5 * DEC);
        }

        // Put events on instances -> sending, receving are ignored for the moment
        //ListIterator li2;
        ListIterator li1 = msc.getEvts().listIterator();
        Evt evt, evt1, evt2;
        int indexInstance;

        while(li1.hasNext()) {
            evt = (Evt)(li1.next());
            indexInstance = hmsc.getInstances().indexOf(evt.getInstance());
            sdi = ginstances.get(indexInstance);
            myX = INIT_X + (5*DEC) * indexInstance;
            myY = INIT_Y + DEC_COMP + msc.getOrderOfEvt(evt) * (DEC_COMP);
            tgc = null;

            if (evt.getType() == Evt.VARIABLE_SET) {
                tgc = TGComponentManager.addComponent(myX, myY, TGComponentManager.SD_ACTION_STATE, sdp);
                tgc.setValue(evt.getActionId());
            }

            if (tgc != null) {
                // Check first the size of the instance as well as the size of the panel
                if (myY > (sdi.getY() + sdi.getHeight() + 20)) {
                    // Increase size of all instances
                    sdp.increaseInstanceSize(250);
                }
                // Add the component to the instance
                sdi.addSwallowedTGComponent(tgc, myX, myY);
            }

        }

        // Sending, receiving events
        // For them, we parse links between events
        li1 = msc.getLinksEvts().listIterator();
        LinkEvts le;
        TGConnectingPoint p1, p2;
        TGConnectorMessageSD msg;

        while(li1.hasNext()) {
            le = (LinkEvts)(li1.next());

            evt1 = le.evt1;
            evt2 = le.evt2;

            if (evt2.getType() == Evt.SEND_SYNC) {
                evt = evt2;
                evt2 = evt1;
                evt1 = evt;
            }

            if ((evt1.getType() == Evt.SEND_SYNC) && (evt2.getType() == Evt.RECV_SYNC)) {
                indexInstance = hmsc.getInstances().indexOf(evt1.getInstance());
                sdi = ginstances.get(indexInstance);
                myX = INIT_X + (5*DEC) * indexInstance;
                myY = INIT_Y + DEC_COMP + msc.getOrderOfEvt(evt1) * (DEC_COMP);
                p1 = sdi.closerFreeTGConnectingPoint(myX, myY);

                indexInstance = hmsc.getInstances().indexOf(evt2.getInstance());
                sdi = ginstances.get(indexInstance);
                myX = INIT_X + (5*DEC) * indexInstance;
                myY = INIT_Y + DEC_COMP + msc.getOrderOfEvt(evt2) * (DEC_COMP);
                p2 = sdi.closerFreeTGConnectingPoint(myX, myY);

                if ((p1 != null) && (p2 != null)) {
                    msg = (TGConnectorMessageSD)(TGComponentManager.addConnector(p1.x, p1.y, TGComponentManager.CONNECTOR_MESSAGE_SYNC_SD, sdp, p1, p2, new Vector<Point>()));
                    msg.setValue(evt1.getActionId());
                    sdp.addBuiltComponent(msg);
                    
                }
            } else {
                
            }
        }


        /*while(li1.hasNext()) {
          instance = (Instance)(li1.next());
          if (instance != null) {
          li2 = _msc.getOrders();
          while(li2.hasNext()) {
          order = (Order)(li2.next());

          }
          }
          }*/

    }

    /*private void drawTClasses() throws MalformedSDException {
      TClass t;
      int total = tm.classNb();
      radius = 200 + 30 * total;
      centerX = radius + 50;
      centerY = radius + 50;

      int maxX = 1900;
      int maxY = 900;
      while(maxX < (radius *2 + 200)) {
      maxX = maxX + 500;
      dp.tcdp.setMaxX(maxX);
      }
      while(maxY < (radius *2 + 200)) {
      maxY = maxY + 500;
      dp.tcdp.setMaxY(maxY);
      }
      dp.tcdp.updateSize();

      for(int i=0; i<total; i++) {
      drawTClass(tm.getTClassAtIndex(i), i, total);
      }
      }

      private void drawTClass(TClass t, int index, int total) throws MalformedTURTLEModelingException {
      // Calculate where the class should be added
      // We use a circle to dipose classes
      double angle = 2*Math.PI*index/total;
      int myX = (int)(Math.cos(angle)*radius + centerX);
      int myY = (int)(Math.sin(angle)*radius + centerY);

      // Creating tclass
      TGComponent tgc = TGComponentManager.addComponent(myX, myY, TGComponentManager.TCD_TCLASS, dp.tcdp);
      TCDTClass tcd = (TCDTClass)tgc;

      telements.add(t);
      gelements.add(tgc);

      // setting tclass properties
      tgc.setValue(t.getName());
      tgc.setValueWithChange(t.getName());
      tcd.setStart(t.isActive());

      // Adding tclass to the diagram
      dp.tcdp.addBuiltComponent(tgc);
      tcd.recalculateSize();

      // Managing gates
      addAttributes(t, tcd);
      addGates(t, tcd);
      tcd.checkSizeOfSons();
      }

      public void addAttributes(TClass t, TCDTClass tcd) throws MalformedTURTLEModelingException {
      Vector params = t.getParamList();
      Vector attributes = new Vector();
      Param p;
      TAttribute ta;

      for(int i=0; i<params.size(); i++) {
      p = (Param)(params.elementAt(i));
      ta = null;
      if (p.getType().compareTo(Param.NAT) ==0) {
      ta = new TAttribute(TAttribute.PRIVATE, p.getName(), p.getValue(), TAttribute.NATURAL);
      } else if (p.getType().compareTo(Param.BOOL) ==0) {
      ta = new TAttribute(TAttribute.PRIVATE, p.getName(), p.getValue(), TAttribute.BOOLEAN);
      } else if (p.getType().compareTo(Param.QUEUE_NAT) ==0) {
      ta = new TAttribute(TAttribute.PRIVATE, p.getName(), p.getValue(), TAttribute.QUEUE_NAT);
      } else {
      throw new MalformedTURTLEModelingException("attribute of an unknown type");
      }
      if (ta != null) {
      attributes.add(ta);
      }
      }

      tcd.setAttributes(attributes);
      }

      public void addGates(TClass t, TCDTClass tcd) throws MalformedTURTLEModelingException {
      Vector tmgates = t.getGateList();
      Vector gates = new Vector();
      Gate g;
      TAttribute ta;

      for(int i=0; i<tmgates.size(); i++) {
      g = (Gate)(tmgates.elementAt(i));
      ta = new TAttribute(TAttribute.PUBLIC, g.getName(), "", g.getType()+1);
      gates.add(ta);
      }

      tcd.setGates(gates);
      }

      private void drawActivityDiagrams()  throws MalformedTURTLEModelingException {
      int total = tm.classNb();
      for(int i=0; i<total; i++) {
      drawActivityDiagram(tm.getTClassAtIndex(i));
      }
      }

      private void drawActivityDiagram(TClass t) throws MalformedTURTLEModelingException {
      ActivityDiagram ad = t.getActivityDiagram();
      ADStart ads = ad.getStartState();
      int index = mgui.tabs.indexOf(dp);
      TActivityDiagramPanel tadp = mgui.getActivityDiagramPanel(index, t.getName());
      tadp.removeAll();
      makeADOf(ads, tadp, null, 0, 0);
      tadp.makeGraphicalOptimizations();
      }

      private void makeADOf(ADComponent adc, TActivityDiagramPanel tadp, TGComponent previous, int indexNext, int totalNext) throws MalformedTURTLEModelingException {
      // Check if component has already been computed
      if (telements.contains(adc)) {
      int index = telements.indexOf(adc);
      TGComponent tgcc = (TGComponent)(gelements.elementAt(index));
      // make link if required
      if (!(tgcc instanceof TADStartState)) {
      TGConnector tgco = connectAD(tgcc, previous, tadp, false, indexNext, totalNext);
      tadp.addBuiltConnector(tgco);
      }
      return;
      }

      // make component from adc
      //
      TGComponent tgc = addToAD(adc, tadp);
      if (tgc ==null) {
      
      throw new MalformedTURTLEModelingException("null component");
      }

      // Adding component
      tadp.addBuiltComponent(tgc);
      telements.add(adc);
      gelements.add(tgc);

      // Linking component to the previous one
      if (!(tgc instanceof TADStartState)) {
      TGConnector tgco = connectAD(tgc, previous, tadp, true, indexNext, totalNext);
      tadp.addBuiltConnector(tgco);
      }

      // Managing nexts of this component
      ADComponent nextAdc;
      for(int i=0; i<adc.getNbNext(); i++) {
      makeADOf(adc.getNext(i), tadp, tgc, i, adc.getNbNext());
      }
      }

      public TGComponent addToAD(ADComponent adc, TDiagramPanel tadp) throws MalformedTURTLEModelingException {
      int i;

      if (adc instanceof ADActionStateWithGate) {
      ADActionStateWithGate adasw = (ADActionStateWithGate)adc;
      TADActionState tadas = (TADActionState)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_ACTION_STATE, tadp));
      //
      try {
      tadas.setValue(adasw.getGate().getName() + adasw.getLimitOnGate() + adasw.getActionValue());
      } catch (Exception e) {
      tadas.setValue("Unknown gate");
      }
      return tadas;
      } else if(adc instanceof ADActionStateWithMultipleParam) {
      ADActionStateWithMultipleParam adawp = ((ADActionStateWithMultipleParam)adc);
      TADActionState tadas = (TADActionState)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_ACTION_STATE, tadp));
      tadas.setValue(adawp.getActionValue());
      return tadas;
      } else if(adc instanceof ADActionStateWithParam) {
      ADActionStateWithParam adawp = ((ADActionStateWithParam)adc);
      TADActionState tadas = (TADActionState)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_ACTION_STATE, tadp));
      try {
      //
      //
      tadas.setValue(adawp.getParam().getName() + " = " + adawp.getActionValue());
      } catch (Exception e) {
      tadas.setValue("unknown = unknown");
      }
      return tadas;
      } else if (adc instanceof ADChoice) {
      TADChoice tadc = (TADChoice)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_CHOICE, tadp));
      /*if (adc.getNbNext() > 3) {
      
      }*/
    /*for(i=0; i<3; i++) {
      if (((ADChoice)(adc)).isGuarded(i)) {
      tadc.setGuard(((ADChoice)adc).getGuard(i), i);
      }
      }
      return tadc;
      } else if (adc instanceof ADDelay) {
      TADDeterministicDelay tadd = (TADDeterministicDelay)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_DETERMINISTIC_DELAY, tadp));
      tadd.setDelayValue(((ADDelay)adc).getValue());
      return tadd;
      } else if (adc instanceof ADJunction) {
      return TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_JUNCTION, tadp);
      } else if (adc instanceof ADLatency) {
      TADNonDeterministicDelay tadnd = (TADNonDeterministicDelay)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_NON_DETERMINISTIC_DELAY, tadp));
      tadnd.setLatencyValue(((ADLatency)adc).getValue());
      return tadnd;
      } else if (adc instanceof ADParallel) {
      TADParallel tadpl = (TADParallel)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_PARALLEL, tadp));
      //
      tadpl.setValueGate(((ADParallel)adc).getValueGate());
      return tadpl;
      } else if (adc instanceof ADPreempt) {
      TADPreemption tadpr = (TADPreemption)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_PREEMPTION, tadp));
      return tadpr;
      } else if (adc instanceof ADSequence) {
      TADSequence tadseq = (TADSequence)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_SEQUENCE, tadp));
      return tadseq;
      } else if (adc instanceof ADStart) {
      TADStartState tadstart = (TADStartState)(TGComponentManager.addComponent(600, 75, TGComponentManager.TAD_START_STATE, tadp));
      return tadstart;
      } else if (adc instanceof ADStop) {
      TADStopState tadstop = (TADStopState)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_STOP_STATE, tadp));
      return tadstop;
      } else if (adc instanceof ADTLO) {
      TADTimeLimitedOfferWithLatency tadtlo = (TADTimeLimitedOfferWithLatency)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_TIME_LIMITED_OFFER_WITH_LATENCY, tadp));
      tadtlo.setAction(((ADTLO)adc).getGate().getName() + ((ADTLO)adc).getAction());
      tadtlo.setDelay(((ADTLO)adc).getDelay());
      tadtlo.setLatency(((ADTLO)adc).getLatency());
      return tadtlo;
      } else if (adc instanceof ADTimeInterval) {
      TADTimeInterval adti = (TADTimeInterval)(TGComponentManager.addComponent(10, 10, TGComponentManager.TAD_DELAY_NON_DETERMINISTIC_DELAY, tadp));
      adti.setMinValue(((ADTimeInterval)adc).getMinValue());
      adti.setMaxValue(((ADTimeInterval)adc).getMaxValue());
      return adti;
      }

      
      throw new MalformedTURTLEModelingException("unknown component ->"+adc);
      }

      public TGConnector connectAD(TGComponent tgc, TGComponent previous, TActivityDiagramPanel tadp, boolean move, int indexNext, int totalNext) throws MalformedTURTLEModelingException {
      boolean makeSquare = true;
      int index = -1;

      // Find TGconnectingPoints

      //P1
      TGConnectingPoint p1 = null;

      if ((previous instanceof TADParallel) || (previous instanceof TADPreemption) || (previous instanceof TADSequence)) {
      switch(totalNext) {
      case 1:
      index=7;
      break;
      case 2:
      switch(indexNext) {
      case 0:
      index = 6;
      break;
      default:
      index = 8;
      break;
      }
      break;
      case 3:
      switch(indexNext) {
      case 0:
      index = 5;
      break;
      case 1:
      index = 7;
      break;
      default:
      index = 9;
      break;
      }
      break;
      default:
      }
      }

      if ((index != -1) && ((previous instanceof TADPreemption) || (previous instanceof TADSequence))) {
      index = index - 4;
      }

      if (index > -1 ) {
      p1 = previous.tgconnectingPointAtIndex(index);
      } else {
      p1 = previous.findFirstFreeTGConnectingPoint(true, false);
      }

      if (p1 == null) {
      throw new MalformedTURTLEModelingException("p1 connecting point not found");
      }

      TGConnectingPoint p2 = null;
      if (tgc instanceof TADJunction) {
      if (tgc.tgconnectingPointAtIndex(0).isFree()) {
      p2 = tgc.findFirstFreeTGConnectingPoint(false, true);
      } else {
      p2 = tgc.closerFreeTGConnectingPoint(p1.getX(), p1.getY(), true);
      }
      } else {
      p2 = tgc.findFirstFreeTGConnectingPoint(false, true);
      }


      if (p2 == null) {
      throw new MalformedTURTLEModelingException("p2 connecting point not found on component:" + tgc);
      }

      // Move tgc component according to points
      if (move) {
      int decX = 0;
      int decY = 5;
      if (previous instanceof TADChoice) {
      makeSquare = false;
      if (p1 == previous.tgconnectingPointAtIndex(1)) {
      decX = -90;
      }
      if (p1 == previous.tgconnectingPointAtIndex(2)) {
      decX = 90;
      }
      decY = 20;
      }

      if (previous instanceof TADTimeLimitedOfferWithLatency) {
      if (p1 == previous.tgconnectingPointAtIndex(2)) {
      decX = 50;
      }
      }

      if ((previous instanceof TADSequence) || (previous instanceof TADPreemption)) {
      makeSquare = false;
      if (p1 == previous.tgconnectingPointAtIndex(1)) {
      decX = -80;
      }
      if (p1 == previous.tgconnectingPointAtIndex(2)) {
      decX = -40;
      }
      if (p1 == previous.tgconnectingPointAtIndex(4)) {
      decX = +40;
      }
      if (p1 == previous.tgconnectingPointAtIndex(5)) {
      decX = +80;
      }
      decY = 20;
      }

      if (previous instanceof TADParallel) {
      makeSquare = false;
      if (p1 == previous.tgconnectingPointAtIndex(5)) {
      decX = -100;
      }
      if (p1 == previous.tgconnectingPointAtIndex(6)) {
      decX = -50;
      }
      if (p1 == previous.tgconnectingPointAtIndex(8)) {
      decX = +50;
      }
      if (p1 == previous.tgconnectingPointAtIndex(9)) {
      decX = +100;
      }
      decY = 20;
      }

      decX = Math.max(p1.getX() - p2.getX() + tgc.getX() + decX, tadp.getMinX());
      decY = Math.max(p1.getY() - p2.getY() + tgc.getY() + decY, tadp.getMinY());

      if (decX > tadp.getMaxX()) {
      tadp.setMaxX(tadp.getMaxX() + 500);
      tadp.updateSize();
      }

      if (decY > tadp.getMaxY()) {
      //
      tadp.setMaxY(tadp.getMaxY() + 500);
      tadp.updateSize();
      }

      tgc.setMoveCd(decX, decY);
      }

      // Connect both points
      p1.setFree(false);
      p2.setFree(false);

      TGConnector tgco = TGComponentManager.addConnector(p1.x, p1.y, TGComponentManager.CONNECTOR_AD_DIAGRAM, tadp, p1, p2, new Vector());

      if (makeSquare) {
      tgco.makeSquareWithoutMovingTGComponents();
      }

      return tgco;
      }

      private void drawRelations()  throws MalformedTURTLEModelingException {
      Relation r;
      for(int i = 0; i < tm.relationNb(); i++) {
      r = tm.getRelationAtIndex(i);
      drawRelation(r);
      }
      }

      private void drawRelation(Relation r) throws MalformedTURTLEModelingException {
      // Identify invloved TClasses
      int index1 = telements.indexOf(r.t1);
      int index2 = telements.indexOf(r.t2);

      if ((index1 <0) ||(index2 <0)) {
      throw new MalformedTURTLEModelingException("relation with no tclasses");
      }

      try {
      TCDTClass t1 = (TCDTClass)(gelements.elementAt(index1));
      TCDTClass t2 = (TCDTClass)(gelements.elementAt(index2));

      // Make connector
      //
      TGConnector tgco = makeAssociation(r, t1, t2);
      tgco.makeSquareWithoutMovingTGComponents();
      dp.tcdp.addBuiltConnector(tgco);

      // Add relation semantics to connector (and gates if necessary)
      TGComponent operator = makeSemantics(r, tgco, t1, t2);
      if ((r.type == Relation.SYN) || (r.type == Relation.INV)) {
      makeGates(operator, r, t1, t2);
      }
      } catch (Exception e) {
      throw new MalformedTURTLEModelingException("error happened when making a relation");
      }

      }

      private TGConnector makeAssociation(Relation r, TCDTClass t1, TCDTClass t2) throws MalformedTURTLEModelingException {
      TGConnectingPoint p1 = t1.closerFreeTGConnectingPoint(t2.getX(), t2.getY());
      TGConnectingPoint p2 = t2.closerFreeTGConnectingPoint(t1.getX(), t1.getY());

      if ((p1 != null) && (p2 != null)) {
      p1.setFree(false);
      p2.setFree(false);
      if ((r.type == Relation.PRE) || (r.type == Relation.SEQ) ||(r.type == Relation.INV)) {
      return TGComponentManager.addConnector(p1.x, p1.y, TGComponentManager.CONNECTOR_ASSOCIATION_NAVIGATION, dp.tcdp, p1, p2, new Vector());
      } else {
      return TGComponentManager.addConnector(p1.x, p1.y, TGComponentManager.CONNECTOR_ASSOCIATION, dp.tcdp, p1, p2, new Vector());
      }
      }

      return null;
      }


      private TGComponent makeSemantics(Relation r, TGConnector tgco, TCDTClass t1, TCDTClass t2) throws MalformedTURTLEModelingException {
      // has at leat 3 tgconnecting points -> take the one in thne middle
      TGConnectingPoint pt;
      if (tgco.getNbConnectingPoint() < 1) {
      throw new MalformedTURTLEModelingException("No connecting point");
      }

      pt = tgco.getTGConnectingPointAtIndex(Math.min(1, tgco.getNbConnectingPoint()-1));

      TGComponent operator = null;
      int type = 0;
      // Add a operator corresponding to the relation semantics
      switch(r.type) {
      case   Relation.PAR:
      type = TGComponentManager.TCD_PARALLEL_OPERATOR;
      break;
      case Relation.SYN:
      type = TGComponentManager.TCD_SYNCHRO_OPERATOR;
      break;
      case Relation.INV:
      type = TGComponentManager.TCD_INVOCATION_OPERATOR;
      break;
      case Relation.SEQ:
      type = TGComponentManager.TCD_SEQUENCE_OPERATOR;
      break;
      case Relation.PRE:
      type = TGComponentManager.TCD_PREEMPTION_OPERATOR;
      break;
      default:
      type = -1;
      }

      // Add operator if non null
      if (type == -1) {
      throw new MalformedTURTLEModelingException("Unknown relation type");
      }

      // Is the line horizontal or vertical?
      boolean vertical = true ;
      if (tgco.isPtOnVerticalSegment(pt)) {
      vertical = false;
      }

      int myX, myY;

      if (vertical) {
      myX = pt.getX() - 50;
      myY = pt.getY() - 100;
      } else {
      myX = pt.getX() + 75;
      myY = pt.getY() - 12;
      }

      operator = TGComponentManager.addComponent(myX, myY, type, dp.tcdp);
      telements.add(r);
      gelements.add(operator);
      dp.tcdp.addBuiltComponent(operator);

      TGConnectingPoint pop;
      if (vertical) {
      pop = operator.getTGConnectingPointAtIndex(2);
      } else {
      pop = operator.getTGConnectingPointAtIndex(0);
      }

      // Connects the connector to the operator
      pt.setFree(false);
      pop.setFree(false);
      TGConnector dashco = TGComponentManager.addConnector(pt.x, pt.y, TGComponentManager.CONNECTOR_ATTRIBUTE, dp.tcdp, pt, pop, new Vector());
      //dashco.makeSquareWithoutMovingTGComponents();
      dp.tcdp.addBuiltConnector(dashco);

      if (operator instanceof TCDCompositionOperatorWithSynchro) {
      ((TCDCompositionOperatorWithSynchro)(operator)).structureChanged();
      }

      return operator;

      }

      // If invocation / synchro -> set synchronization gates
      public void makeGates(TGComponent operator, Relation r, TCDTClass t1, TCDTClass t2)  throws MalformedTURTLEModelingException {
      Vector gates = null;
      TTwoAttributes tt;
      TAttribute ta1, ta2;
      Gate g1, g2;
      try {
      gates = ((TCDCompositionOperatorWithSynchro)operator).getGates();

      } catch (Exception e){
      throw new MalformedTURTLEModelingException("Gates of synchro relation may not be set");
      }

      for(int i=0; i<r.gatesOfT1.size(); i++) {
      g1 = (Gate)(r.gatesOfT1.elementAt(i));
      g2 = (Gate)(r.gatesOfT2.elementAt(i));
      ta1 = t1.getGateById(g1.getName());
      ta2 = t2.getGateById(g2.getName());
      tt = new TTwoAttributes(t1, t2, ta1, ta2);
      gates.add(tt);
      }

      ((TCDCompositionOperatorWithSynchro)operator).getSynchroGateList().makeValue();

      }

      public void makeDrawable() {
      tm.unmergeChoices();

      TClass t;
      for(int i=0; i<tm.classNb(); i++) {
      t = tm.getTClassAtIndex(i);
      makeDrawable(t.getActivityDiagram(), false);
      }
      }

      public int makeDrawable(ActivityDiagram ad, boolean debug) {
      ADComponent adc, adc1;
      ADJunction adj1, adj2 = null;
      int i=0;

      while(i<ad.size()) {
      adc = (ADComponent)(ad.elementAt(i));

      // Ensure that at most 3 elements lead to a junction -> if more, remove one
      if (adc instanceof ADJunction) {
      adj1 = (ADJunction)adc;
      if (ad.getNbComponentLeadingTo(adj1) > 3) {
      // Find an appropriate new junction
      if (adj1.getNext(0) instanceof ADJunction) {
      adj2 = (ADJunction)(adj1.getNext(0));
      if (ad.getNbComponentLeadingTo(adj1) > 2) {
      // No space left on that junction ...
      // Create a new junction
      adj2 = new ADJunction();
      ad.add(adj2);
      adj2.addNext(adj1.getNext(0));
      adj1.removeAllNext();
      adj1.addNext(adj2);
      }
      } else {
      adj2 = new ADJunction();
      ad.add(adj2);
      adj2.addNext(adj1.getNext(0));
      adj1.removeAllNext();
      adj1.addNext(adj2);
      }
      adc1 = ad.getFirstComponentLeadingTo(adc);
      adc1.updateNext(adj1, adj2);
      return makeDrawable(ad, debug);
      }
      }
      i++;
      }
      return 0;
      }*/
          }
