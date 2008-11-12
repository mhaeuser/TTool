/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class DesignPanelTranslator
 * Creation: 17/08/2006
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.util.*;



import myutil.*;
import ui.ad.*;
import ui.cd.*;

import translator.*;

import ui.osad.*;


public class DesignPanelTranslator {
	protected TURTLEDesignPanelInterface dp;
	protected Vector checkingErrors;
	protected CorrespondanceTGElement listE;

	public DesignPanelTranslator(TURTLEDesignPanelInterface _dp) {
		dp = _dp;
		reinit();
	}

	public void reinit() {
		checkingErrors = new Vector();
		listE = new CorrespondanceTGElement();
	}

	public Vector getErrors() {
		return checkingErrors;
	}

	public Vector getWarnings() {
		return null;
	}

	public CorrespondanceTGElement getCorrespondanceTGElement() {
		return listE;
	}

	public TURTLEModeling generateTURTLEModeling() {
		Vector tclasses = new Vector();
		TGComponent tgc;

		ListIterator iterator = dp.getStructurePanel().getComponentList().listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TClassInterface) {
				tclasses.add(tgc);
			}
		}

		return generateTURTLEModeling(tclasses, "");
	}

	public TURTLEModeling generateTURTLEModeling(Vector tclasses, String preName) {
		TURTLEModeling tmodel = new TURTLEModeling();
		addTClasses(dp, tclasses, preName, tmodel);
		addRelations(dp, tmodel);
		return tmodel;
	}

	private void addCheckingError(CheckingError ce) {
		if (checkingErrors == null) {
			checkingErrors = new Vector();
		}
		checkingErrors.addElement(ce);
	}


	public void addTClasses(TURTLEDesignPanelInterface dp, Vector tclasses, String preName, TURTLEModeling tm) {
		TDiagramPanel tdp;
		// search for class diagram panels
		tdp = dp.panelAt(0);
		/*if (tdp instanceof TClassDiagramPanel) {
            addTClassesFromPanel((TClassDiagramPanel)tdp, tclasses, preName, tm);
        }*/
		if (tdp instanceof ClassDiagramPanelInterface) {
			addTClassesFromPanel((ClassDiagramPanelInterface)tdp, tclasses, preName, tm);
		}
	}

	private void addTClassesFromPanel(ClassDiagramPanelInterface tdp, Vector tclasses, String preName, TURTLEModeling tm) {
		LinkedList list = tdp.getComponentList();
		Iterator iterator = list.listIterator();

		// search for tclasses
		TGComponent tgc;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if ((tgc instanceof TClassInterface) && (tclasses.contains(tgc))) {
				addTClassFromTClassInterface((TClassInterface)tgc, tdp, preName, tm);
			}
		}
	}

	private void addTClassFromTClassInterface(TClassInterface tgc, ClassDiagramPanelInterface tdp, String preName, TURTLEModeling tm) {
		//System.out.println("Adding TClass: " + tgc.getClassName());
		TClass t = new TClass(preName + tgc.getClassName(), tgc.isStart());

		Vector v;
		int i;
		TAttribute a;
		Param p;
		Gate g; boolean internal; int type;

		// Attributes
		v = tgc.getAttributes();
		for(i=0; i<v.size(); i++) {
			a = (TAttribute)(v.elementAt(i));
			if (a.getType() == TAttribute.NATURAL) {
				p = new Param(a.getId(), Param.NAT, a.getInitialValue());
				p.setAccess(a.getAccessString());
				t.addParameter(p);
			}
			if (a.getType() == TAttribute.BOOLEAN) {
				p = new Param(a.getId(), Param.BOOL, a.getInitialValue());
				p.setAccess(a.getAccessString());
				t.addParameter(p);
			}
			
			if (a.getType() == TAttribute.QUEUE_NAT) {
				p = new Param(a.getId(), Param.QUEUE_NAT, a.getInitialValue());
				p.setAccess(a.getAccessString());
				t.addParameter(p);
				//System.out.println("Adding queuenat parameter");
			}

			if (a.getType() == TAttribute.OTHER) {
				addTDataAttributes(a, t, tdp, tm);
			}
		}

		// Gates
		v = tgc.getGates();
		for(i=0; i<v.size(); i++) {
			a = (TAttribute)(v.elementAt(i));
			internal = (a.getAccess() == TAttribute.PRIVATE);
			switch(a.getType()) {
			case TAttribute.GATE:
				type = Gate.GATE;
				break;
			case TAttribute.OUTGATE:
				type = Gate.OUTGATE;
				break;
			case
			TAttribute.INGATE:
				type = Gate.INGATE;
				break;
			default:
				type = -1;
			}
			if (type > -1) {
				internal = false; // We consider all gates as public gates -> private is given for documentation purpose only
				g = new Gate(a.getId(), type, internal);
				t.addGate(g);
			}
		}


		tm.addTClass(t);
		listE.addCor(t, (TGComponent)tgc, preName);

		// Activity Diagram
		buildActivityDiagram(t);

	}

	private void addTDataAttributes(TAttribute a, TClass t, ClassDiagramPanelInterface tdp, TURTLEModeling tm) {
		//System.out.println("Find data: " + a.getId() + " getTypeOther=" + a.getTypeOther());
		if (tdp instanceof TClassDiagramPanel) {
			TCDTData tdata  = ((TClassDiagramPanel)tdp).findTData(a.getTypeOther());
			if (tdata == null) {
				CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Unknown type: " + a.getTypeOther());
				ce.setTClass(t);
				ce.setTDiagramPanel((TDiagramPanel)tdp);
				addCheckingError(ce);
				return ;
			}

			Vector v = tdata.getAttributes();
			TAttribute b; Param p;
			for(int i=0; i<v.size(); i++) {
				b = (TAttribute)(v.elementAt(i));
				if (b.getType() == TAttribute.NATURAL) {
					p = new Param(a.getId() + "__" + b.getId(), Param.NAT, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
				if (b.getType() == TAttribute.BOOLEAN) {
					p = new Param(a.getId() + "__" + b.getId(), Param.BOOL, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
				
				if (b.getType() == TAttribute.QUEUE_NAT) {
					p = new Param(a.getId() + "__" + b.getId(), Param.QUEUE_NAT, b.getInitialValue());
					p.setAccess(a.getAccessString());
					t.addParameter(p);
				}
			}
		}

	}

	private void buildActivityDiagram(TClass t) {
		int j;
		//TActivityDiagramPanel tadp;
		ActivityDiagramPanelInterface adpi;
		TDiagramPanel tdp;
		//t.printParams();

		// find the panel of this TClass
		TClassInterface tci = (TClassInterface)(listE.getTG(t));

		String name = tci.getClassName();
		int index_name = name.indexOf(':');
		// instance
		if (index_name != -1) {
			name = name.substring(index_name+2, name.length());
		}

		adpi = tci.getBehaviourDiagramPanel();
		if (adpi == null) {
			return;
		}

		tdp = (TDiagramPanel)adpi;

		// search for start state
		LinkedList list = adpi.getComponentList();
		Iterator iterator = list.listIterator();
		TGComponent tgc;
		TGComponent tss = null;
		int cptStart = 0;
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TADStartState){
				tss = tgc;
				cptStart ++;
			} else if (tgc instanceof TOSADStartState) {
				tss = tgc;
				cptStart ++;
			}
		}

		if (tss == null) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the activity diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}

		if (cptStart > 1) {
			CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the activity diagram of " + name);
			ce.setTClass(t);
			ce.setTDiagramPanel(tdp);
			addCheckingError(ce);
			return;
		}

		ADStart ads;
		//ADActionState ada;
		ADActionStateWithGate adag;
		ADActionStateWithParam adap;
		ADActionStateWithMultipleParam adamp;
		ADChoice adch;
		ADDelay add;
		ADJunction adj;
		ADLatency adl;
		ADParallel adp;
		ADSequence adseq;
		ADPreempt adpre;
		ADStop adst;
		ADTimeInterval adti;
		ADTLO adtlo;
		String s, s1;
		Gate g;
		Param p;

		int nbActions;
		String sTmp;

		// Creation of the activity diagram
		ads = new ADStart();
		listE.addCor(ads, tss);
		ActivityDiagram ad = new ActivityDiagram(ads);
		t.setActivityDiagram(ad);
		//System.out.println("Making activity diagram of " + t.getName());

		// Creation of other elements
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			
			/*if (tgc.getCheckableAccessibility()) {
				
			}*/
			
			if (tgc instanceof TADActionState) {
				s = ((TADActionState)tgc).getAction();
				s = s.trim();
				//remove ';' if last character
				if (s.substring(s.length()-1, s.length()).compareTo(";") == 0) {
					s = s.substring(0, s.length()-1);
				}
				nbActions = Conversion.nbChar(s, ';') + 1;
				//System.out.println("Nb Actions in state: " + nbActions);

				s = TURTLEModeling.manageDataStructures(t, s);

				g = t.getGateFromActionState(s);
				p = t.getParamFromActionState(s);
				if ((g != null) && (nbActions == 1)){
					//System.out.println("Action state with gate found " + g.getName() + " value:" + t.getActionValueFromActionState(s));
					adag = new ADActionStateWithGate(g);
					ad.addElement(adag);
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("s1=" + s1);
					/*if (s1 == null) {
                        System.out.println("oh ho !");
                    }*/
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					//System.out.println("hi");
					if (s1 == null) {
						//System.out.println("ho");
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Invalid expression: " + t.getActionValueFromActionState(s));
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);
						return;
					}
					s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
					//System.out.println("Adding type done s1=" + s1);
					adag.setActionValue(s1);
					listE.addCor(adag, tgc);
				} else if ((p != null) && (nbActions == 1)){
					//System.out.println("Action state with param found " + p.getName() + " value:" + t.getExprValueFromActionState(s));
					if (t.getExprValueFromActionState(s).trim().startsWith("=")) {
						CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, s + " should not start with a '=='");
						ce.setTClass(t);
						ce.setTGComponent(tgc);
						ce.setTDiagramPanel(tdp);
						addCheckingError(ce);  
					}
					adap = new ADActionStateWithParam(p);
					ad.addElement(adap);
					adap.setActionValue(TURTLEModeling.manageDataStructures(t, t.getExprValueFromActionState(s)));
					listE.addCor(adap, tgc);

				} else if ((p != null) && (nbActions > 1)){
					//System.out.println("Action state with multi param found " + p.getName() + " value:" + t.getExprValueFromActionState(s));
					// Checking params
					CheckingError ce;
					for(j=0; j<nbActions; j++) {
						sTmp = TURTLEModeling.manageDataStructures(t,((TADActionState)(tgc)).getAction(j));
						if (sTmp == null) {
							ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (0) (" + s + "): \"" + s + "\" is not a correct expression");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
						}

						p = t.getParamFromActionState(sTmp);
						if (p == null) {
							ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (1) (" + s + "): \"" + sTmp + "\" is not a correct expression");
							ce.setTClass(t);
							ce.setTGComponent(tgc);
							ce.setTDiagramPanel(tdp);
							addCheckingError(ce);
						}
					}

					adamp = new ADActionStateWithMultipleParam();
					ad.addElement(adamp);
					adamp.setActionValue(TURTLEModeling.manageDataStructures(t, s));
					listE.addCor(adamp, tgc);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Action state (2) (" + s + "): \"" + s + "\" is not a correct expression");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					//System.out.println("Bad action state found " + s);
				}

			} else if (tgc instanceof TADChoice) {
				adch = new ADChoice();
				ad.addElement(adch);
				listE.addCor(adch, tgc);
			} else if (tgc instanceof TADDeterministicDelay) {
				add = new ADDelay();
				ad.addElement(add);
				add.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADDeterministicDelay)tgc).getDelayValue()));
				listE.addCor(add, tgc);
			} else if (tgc instanceof TADJunction) {
				adj = new ADJunction();
				ad.addElement(adj);
				listE.addCor(adj, tgc);
			} else if (tgc instanceof TADNonDeterministicDelay) {
				adl = new ADLatency();
				ad.addElement(adl);
				adl.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADNonDeterministicDelay)tgc).getLatencyValue()));
				listE.addCor(adl, tgc);
			} else if (tgc instanceof TADParallel) {
				adp = new ADParallel();
				ad.addElement(adp);
				adp.setValueGate(((TADParallel)tgc).getValueGate());
				listE.addCor(adp, tgc);
			} else if (tgc instanceof TADSequence) {
				adseq = new ADSequence();
				ad.addElement(adseq);
				listE.addCor(adseq, tgc);
			} else if (tgc instanceof TADPreemption) {
				adpre = new ADPreempt();
				ad.addElement(adpre);
				listE.addCor(adpre, tgc);
			} else if (tgc instanceof TADStopState) {
				adst = new ADStop();
				ad.addElement(adst);
				listE.addCor(adst, tgc);
			} else if (tgc instanceof TADTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TADTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TADTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TADTimeLimitedOffer) {
				s = ((TADTimeLimitedOffer)tgc).getAction();
				g = t.getGateFromActionState(s);
				if (g != null) {
					adtlo = new ADTLO(g);
					ad.addElement(adtlo);
					adtlo.setLatency("0");
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
					//System.out.println("Adding type done");
					adtlo.setAction(s1);
					adtlo.setDelay(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOffer)tgc).getDelay()));
					listE.addCor(adtlo, tgc);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time-limited offer (" + s + ", " + ((TADTimeLimitedOffer)tgc).getDelay() + "): \"" + s + "\" is not a correct expression");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					//System.out.println("Bad time limited offer found " + s);
				}
			} else if (tgc instanceof TADTimeLimitedOfferWithLatency) {
				s = ((TADTimeLimitedOfferWithLatency)tgc).getAction();
				g = t.getGateFromActionState(s);
				if (g != null) {
					adtlo = new ADTLO(g);
					ad.addElement(adtlo);
					adtlo.setLatency(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOfferWithLatency)tgc).getLatency()));
					s1 = t.getActionValueFromActionState(s);
					//System.out.println("Adding type");
					s1 = TURTLEModeling.manageGateDataStructures(t, s1);
					s1 = TURTLEModeling.addTypeToDataReceiving(t, s1);
					//System.out.println("Adding type done");
					adtlo.setAction(s1);
					adtlo.setDelay(TURTLEModeling.manageGateDataStructures(t, ((TADTimeLimitedOfferWithLatency)tgc).getDelay()));
					listE.addCor(adtlo, tgc);
				} else {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, "Time-limited offer (" + s + ", " + ((TADTimeLimitedOfferWithLatency)tgc).getLatency() + ", " + ((TADTimeLimitedOfferWithLatency)tgc).getDelay() + "): \"" + s + "\" is not a correct expression");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
					//System.out.println("Bad time limited offer found " + s);
				}

				// TURTLE-OS AD
			} else if (tgc instanceof TOSADTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TOSADTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TOSADTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TOSADIntTimeInterval) {
				adti = new ADTimeInterval();
				ad.addElement(adti);
				adti.setValue(TURTLEModeling.manageGateDataStructures(t, ((TOSADIntTimeInterval)tgc).getMinDelayValue()), TURTLEModeling.manageGateDataStructures(t, ((TOSADIntTimeInterval)tgc).getMaxDelayValue()));
				listE.addCor(adti, tgc);
			} else if (tgc instanceof TOSADStopState) {
				adst = new ADStop();
				ad.addElement(adst);
				listE.addCor(adst, tgc);
			} else if (tgc instanceof TOSADJunction) {
				adj = new ADJunction();
				ad.addElement(adj);
				listE.addCor(adj, tgc);
			} else if (tgc instanceof TOSADChoice) {
				adch = new ADChoice();
				ad.addElement(adch);
				listE.addCor(adch, tgc);
			} if (tgc instanceof TOSADActionState) {
				s = ((TOSADActionState)tgc).getAction();
				s = s.trim();
				//remove ';' if last character
				if (s.substring(s.length()-1, s.length()).compareTo(";") == 0) {
					s = s.substring(0, s.length()-1);
				}
				nbActions = Conversion.nbChar(s, ';') + 1;

				if (nbActions>1) {
					CheckingError ce = new CheckingError(CheckingError.BEHAVIOR_ERROR, s + " should not start with a '=='");
					ce.setTClass(t);
					ce.setTGComponent(tgc);
					ce.setTDiagramPanel(tdp);
					addCheckingError(ce);
				} else {
					//s = TURTLEModeling.manageDataStructures(t, s);
					g = t.getGateFromActionState(s);
					p = t.getParamFromActionState(s);

					if (p != null) {
						adap = new ADActionStateWithParam(p);
						ad.addElement(adap);
						adap.setActionValue(TURTLEModeling.manageDataStructures(t, t.getExprValueFromActionState(s)));
						listE.addCor(adap, tgc);
					} else {
						adag = new ADActionStateWithGate(g);
						ad.addElement(adag);
						listE.addCor(adag, tgc);
						adag.setActionValue(s);
					}
				}
				//System.out.println("Nb Actions in state: " + nbActions);
			}
		}

		TGConnectingPoint p1, p2;
		//TGConnectorFullArrow tgco;
		TGComponent tgc1, tgc2, tgc3;
		ADComponent ad1, ad2;

		// Managing Java code
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof PreJavaCode) {
				ad1 = listE.getADComponentByIndex(tgc, tdp.count);
				if (ad1 != null) {
					ad1.setPreJavaCode(tgc.getPreJavaCode());
				}
			}
			if (tgc instanceof PostJavaCode) {
				ad1 = listE.getADComponentByIndex(tgc, tdp.count);
				if (ad1 != null) {
					ad1.setPostJavaCode(tgc.getPostJavaCode());
				}
			}
		}

		// Connecting elements
		TGConnectorBetweenElementsInterface tgcbei;
		iterator = list.listIterator();
		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof TGConnectorBetweenElementsInterface) {
				tgcbei = (TGConnectorBetweenElementsInterface)tgc;
				p1 = tgcbei.getTGConnectingPointP1();
				p2 = tgcbei.getTGConnectingPointP2();

				// identification of connected components
				tgc1 = null; tgc2 = null;
				for(j=0; j<list.size(); j++) {
					tgc3 = 	(TGComponent)(list.get(j));
					if (tgc3.belongsToMe(p1)) {
						tgc1 = tgc3;
					}
					if (tgc3.belongsToMe(p2)) {
						tgc2 = tgc3;
					}
				}

				// connecting turtle modeling components
				if ((tgc1 != null) && (tgc2 != null)) {
					//ADComponent ad1, ad2;
					ad1 = listE.getADComponentByIndex(tgc1, tdp.count);
					ad2 = listE.getADComponentByIndex(tgc2, tdp.count);
					if ((ad1 == null) || (ad2 == null)) {
						//System.out.println("Correspondance issue");
					}
					int index = 0;
					if ((ad1 != null ) && (ad2 != null)) {
						if ((tgc1 instanceof TADTimeLimitedOffer) || (tgc1 instanceof TADTimeLimitedOfferWithLatency)) {
							index = tgc1.indexOf(p1) - 1;
							ad1.addNextAtIndex(ad2, index);
						} else if (tgc1 instanceof TADChoice) {
							TADChoice tadch = (TADChoice)tgc1;
							index = tgc1.indexOf(p1) - 1;
							((ADChoice)ad1).addGuard(TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(index)));
							ad1.addNext(ad2);
						} else if ((tgc1 instanceof TADSequence) ||(tgc1 instanceof TADPreemption)){
							index = tgc1.indexOf(p1) - 1;
							ad1.addNextAtIndex(ad2, index);
						} else if (tgc1 instanceof TOSADChoice) {
							TOSADChoice tadch = (TOSADChoice)tgc1;
							index = tgc1.indexOf(p1) - 1;
							((ADChoice)ad1).addGuard(TURTLEModeling.manageGateDataStructures(t, tadch.getGuard(index)));
							ad1.addNext(ad2);
						} else {
							ad1.addNextAtIndex(ad2, index);
							//System.out.println("Adding connector from " + ad1 + " to " + ad2);
						}
					}
				}
			}
		}
		// Increasing count of this panel
		tdp.count ++;
	}

	public void addRelations(TURTLEDesignPanelInterface dp, String prename, TURTLEModeling tm) {
		addRelationFromPanel(dp.getStructurePanel(), prename, tm);
	}

	public void addRelations(TURTLEDesignPanelInterface dp, TURTLEModeling tm) {
		addRelationFromPanel(dp.getStructurePanel(), "", tm);
	}

	private void addRelationFromPanel(ClassDiagramPanelInterface tdp, String prename, TURTLEModeling tm) {
		LinkedList list = tdp.getComponentList();
		Iterator iterator = list.listIterator();
		// search for Composition Operator
		TGComponent tgc;

		while(iterator.hasNext()) {
			tgc = (TGComponent)(iterator.next());
			if (tgc instanceof CompositionOperatorInterface) {
				addRelationFromCompositionOperator((CompositionOperatorInterface)tgc, tdp, prename, tm);
			}
		}
	}

	private void addRelationFromCompositionOperator(CompositionOperatorInterface tco, ClassDiagramPanelInterface tdp, String prename, TURTLEModeling tm) {
		TClassInterface t1 = tdp.getTClass1ToWhichIamConnected(tco);
		TClassInterface t2 = tdp.getTClass2ToWhichIamConnected(tco);

		TGConnector tgco = tdp.getTGConnectorAssociationOf(tco);

		if ((t1 != null) && (t2 != null) && (tgco != null)) {
			TClass tc1 = tm.getTClassWithName(prename + t1.getValue());
			TClass tc2 = tm.getTClassWithName(prename + t2.getValue());

			if ((tc1 != null) && (tc2 != null)) {
				int type = typeOf(tco);
				if (type == -1) {
					return;
				}

				Relation r;

				if (tgco instanceof TGConnectorAssociationWithNavigation) {
					r = new Relation(type, tc1, tc2, true);
				}	else {
					r = new Relation(type, tc1, tc2, false);
				}

				tm.addRelation(r);
				//System.out.println("Adding " + Relation.translation(type) + " relation between " + tc1.getName() + " and " + tc2.getName());

				// if tgco is a synchro operator -> synchronizations gates
				if (tco instanceof TCDSynchroOperator) {
					Vector gates = ((TCDSynchroOperator)tco).getGates();
					setGatesOf(r, gates, tc1, tc2);
				}

				if (tco instanceof TCDInvocationOperator) {
					Vector gates = ((TCDInvocationOperator)tco).getGates();
					setGatesOf(r, gates, tc1, tc2);
				}

				// if tgco watcdog -> list of gates
				if (tco instanceof TCDWatchdogOperator) {
					Vector gates = ((TCDWatchdogOperator)tco).getGates();
					setWatchdogGatesOf(r, gates, tc1, tc2);
				}

			}
		}
	}

	private int typeOf(CompositionOperatorInterface tco) {
		if (tco instanceof TCDParallelOperator) {
			return Relation.PAR;
		} else if (tco instanceof TCDPreemptionOperator) {
			return 	Relation.PRE;
		} else if (tco instanceof TCDSequenceOperator) {
			return 	Relation.SEQ;
		} else if (tco instanceof TCDSynchroOperator) {
			return 	Relation.SYN;
		} else if (tco instanceof TCDInvocationOperator) {
			return 	Relation.INV;
		} else if (tco instanceof TCDWatchdogOperator) {
			return 	Relation.WAT;
		}
		return -1;
	}

	private void setGatesOf(Relation r, Vector gates, TClass tc1, TClass tc2) {
		TTwoAttributes tt;
		Gate g1, g2;

		for(int i=0; i<gates.size(); i++) {
			tt = (TTwoAttributes)(gates.elementAt(i));
			g1 = tc1.getGateByName(tt.ta1.getId());
			g2 = tc2.getGateByName(tt.ta2.getId());

			if ((g1 != null) && (g2 != null)) {
				r.addGates(g1, g2);
				//System.out.println("Adding gates " + g1.getName() + " = " + g2.getName());
			}
		}
	}

	private void setWatchdogGatesOf(Relation r, Vector gates, TClass tc1, TClass tc2) {
		//TTwoAttributes tt;
		TAttribute t;
		Gate g1;

		for(int i=0; i<gates.size(); i++) {
			t = (TAttribute)(gates.elementAt(i));
			g1 = tc1.getGateByName(t.getId());

			if (g1 != null)  {
				r.addGates(g1, g1);
			}
		}
	}

}
