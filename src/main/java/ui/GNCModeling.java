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

import myutil.TreeCell;
import nc.*;
import translator.CheckingError;
import ui.ncdd.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
* Class GNCModeling
* Use to translate graphical NC modeling to nc structure
* Creation: 20/11/2008
* @version 1.0 20/11/2008
* @author Ludovic APVRILLE
 */
public class GNCModeling  {
	private NCStructure ncs;
    private NCPanel ncp;
	private NCDiagramPanel ncdp;
    private LinkedList<CheckingError> checkingErrors, warnings;
	private CorrespondanceTGElement listE;
	//private NCStructure nc;
	
	private static int PATH_INDEX = 0;
	    
    public GNCModeling(NCPanel _ncp) {
        ncp = _ncp;
		ncdp = ncp.ncdp;
    }
    
	public NCStructure translateToNCStructure() {
		PATH_INDEX = 0;
		
        ncs = new NCStructure();
        checkingErrors = new LinkedList<CheckingError> ();
        warnings = new LinkedList<CheckingError> ();
		listE = new CorrespondanceTGElement();
        //boolean b;
        
		if (ncdp != null) {
			addEquipments();
			addSwitches();
			addTraffics();
			addLinks();
			addPaths();
			manageParameters();
		}
		
        return ncs;
    }
	
	public CorrespondanceTGElement getCorrespondanceTable() {
		return listE;
	}
    
    public List<CheckingError> getCheckingErrors() {
        return checkingErrors;
    }
    
    public LinkedList<CheckingError> getCheckingWarnings() {
        return warnings;
    }
    
	private void addEquipments() {
		ListIterator<NCEqNode> iterator = ncdp.getListOfEqNode().listIterator();
		NCEqNode node;
		NCEquipment eq;
	//	NCConnectorNode con;
		//int cpt;
		
		
		
		while(iterator.hasNext()) {
			node = iterator.next();
			
			// Find the only interface of that Equipment
			// If more than one interface -> error
			/*ArrayList<NCConnectorNode> cons = ncdp.getConnectorOfEq(node);
			
			if (cons.size() == 0) {
				ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Equipment " + node.getName() + " has non interface ans so not traffic can be sent over the network");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(node);
				warnings.add(ce);
				return;
			}
			
			if (cons.size() > 1) {
				ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Equipment: " + node.getName() + " has more than one interface");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(node);
				checkingErrors.add(ce);
				return;
			}
			
			con = cons.get(0);*/
			
			/*if ((con.hasParameter()) && (con.getParameter() >0)) {
				for(int i=0; i<con.getParameter(); i++) {
					eq = new NCEquipment();
					eq.setName(node.getName()+ "__" +i);
					ncs.equipments.add(eq);
				}
			} else {*/
				eq = new NCEquipment();
				eq.setName(node.getName());
				eq.setSchedulingPolicy(node.getSchedulingPolicy());
				
				eq.setType(node.getNodeType());
				ncs.equipments.add(eq);
			/*}*/
		}
	}
	
	private void addSwitches() {
		ListIterator<NCSwitchNode> iterator = ncdp.getListOfSwitchNode().listIterator();
		NCSwitchNode node;
		NCSwitch sw;
		NCCapacityUnit unit = new NCCapacityUnit();
		
		while(iterator.hasNext()) {
			node = iterator.next();
			
			sw= new NCSwitch();
			sw.setName(node.getNodeName());
			sw.setSchedulingPolicy(node.getSchedulingPolicy());
			sw.setSwitchingTechnique(node.getSwitchingTechnique());
			sw.setCapacity(node.getCapacity());
			unit.setUnit(node.getCapacityUnit());
			sw.setCapacityUnit(unit);
			sw.setTechnicalLatency(node.getTechnicalLatency());
			ncs.switches.add(sw);
		}
	}
	
	private void addTraffics() {
		ListIterator<NCTrafficArtifact> iterator = ncdp.getTrafficArtifacts().listIterator();
		NCTrafficArtifact arti;
		NCTraffic tr;
		NCTimeUnit unit;
		
		while(iterator.hasNext()) {
			arti = iterator.next();
			tr = new NCTraffic();
			tr.setName(arti.getValue());
			tr.setPeriodicType(arti.getPeriodicType());
			tr.setPeriod(arti.getPeriod());
			unit = new NCTimeUnit();
			unit.setUnit(arti.getPeriodUnit());
			tr.setPeriodUnit(unit);
			tr.setDeadline(arti.getDeadline());
			unit = new NCTimeUnit();
			unit.setUnit(arti.getDeadlineUnit());
			tr.setDeadlineUnit(unit);
			tr.setMinPacketSize(arti.getMinPacketSize());
			tr.setMaxPacketSize(arti.getMaxPacketSize());
			tr.setPriority(arti.getPriority());
			ncs.traffics.add(tr);
		}
	}
	
	private void addLinks() {
		ListIterator<NCConnectorNode> iterator = ncdp.getListOfLinks().listIterator();
		NCConnectorNode nccn;
		NCLink lk = null;//, lkr;
		boolean added;
		TGComponent tgc;
		TGConnectingPoint tp;
		NCLinkedElement ncle;
		String name;
		NCCapacityUnit nccu;
		NCSwitchNode switch1, switch2;
		
		while(iterator.hasNext()) {
			added = false;
			nccn = iterator.next();
			
			if (ncdp.isALinkBetweenEquipment(nccn)) {
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Link connected between two equipments: " + nccn.getInterfaceName());
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(nccn);
				checkingErrors.add(ce);
				
			} else {
				switch1 = null;
				switch2 = null;
				
				lk = new NCLink();
				lk.setName(nccn.getInterfaceName());
				
				tp = nccn.getTGConnectingPointP1();
				tgc = ncdp.getComponentToWhichBelongs(tp);
				
				if (tgc == null) {
					addLinkError(nccn);
				} else {
					if ((tgc instanceof NCEqNode) || (tgc instanceof NCSwitchNode)) {
						if (tgc instanceof NCEqNode) {
							name =  tgc.getName();
						} else {
							switch1 = ((NCSwitchNode)tgc);
							name = switch1.getNodeName();
						}
						ncle = ncs.getNCLinkedElementByName(name);
						if (ncle == null) {
							addLinkError(nccn);
						} else {
							lk.setLinkedElement1(ncle);
						}
					} else {
						addLinkError(nccn);
					}
				}
				
				tp = nccn.getTGConnectingPointP2();
				tgc = ncdp.getComponentToWhichBelongs(tp);
				
				if (tgc == null) {
					addLinkError(nccn);
				} else {
					if ((tgc instanceof NCEqNode) || (tgc instanceof NCSwitchNode)) {
						if (tgc instanceof NCEqNode) {
							name =  tgc.getName();
						} else {
							switch2 = ((NCSwitchNode)tgc);
							name = switch2.getNodeName();
						}
						ncle = ncs.getNCLinkedElementByName(name);
						if (ncle == null) {
							addLinkError(nccn);
						} else {
							lk.setLinkedElement2(ncle);
						}
					} else {
						addLinkError(nccn);
					}
				}
				
				// Must also check that there is at most one link between two elements
				if (ncs.hasSimilarLink(lk)) {
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Duplicate link between elements: " + nccn.getInterfaceName());
					ce.setTDiagramPanel(ncdp);
					ce.setTGComponent(nccn);
					checkingErrors.add(ce);
				} else {
					ncs.links.add(lk);
					added = true;
				}
				
				if (!nccn.hasCapacity()) {
					// In that case, must set the capacity of switches
					if ((switch1 == null) && (switch2 == null)) {
						lk.setCapacity(nccn.getCapacity());
						nccu = new NCCapacityUnit();
						nccu.setUnit(nccn.getCapacityUnit());
						lk.setCapacityUnit(nccu);
					} else {
						if (switch1 == null && switch2 != null ) {
							lk.setCapacity(switch2.getCapacity());
							nccu = new NCCapacityUnit();
							nccu.setUnit(switch2.getCapacityUnit());
							lk.setCapacityUnit(nccu);
						}
						// DB: Seems to be a bug...
						else if (switch2 == null && switch1 != null ) {
							lk.setCapacity(switch1.getCapacity());
							nccu = new NCCapacityUnit();
							
							// DB looks like a copy paste error
							nccu.setUnit(switch1.getCapacityUnit());
//							nccu.setUnit(switch2.getCapacityUnit());
							lk.setCapacityUnit(nccu);
						}
						else {
						//if ((switch1 != null) && (switch2 != null)) {
							if (switch1.getCapacity() != switch2.getCapacity()) {
								UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Link with no capacity between two switches of different capacity: " + nccn.getInterfaceName());
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(nccn);
								checkingErrors.add(ce);
							} else {
								lk.setCapacity(switch1.getCapacity());
								nccu = new NCCapacityUnit();
								nccu.setUnit(switch2.getCapacityUnit());
								lk.setCapacityUnit(nccu);
							}
						}
					}
				} else {
					lk.setCapacity(nccn.getCapacity());
					nccu = new NCCapacityUnit();
					nccu.setUnit(nccn.getCapacityUnit());
					lk.setCapacityUnit(nccu);
				}
			}
			if (added) {
				ncs.links.add(lk.cloneReversed());
			}
		}
	}
	
	public void addLinkError(NCConnectorNode _nccn) {
		UICheckingError ce;
		ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Badly connected link: " +_nccn.getInterfaceName());
		ce.setTDiagramPanel(ncdp);
		ce.setTGComponent(_nccn);
		checkingErrors.add(ce);
	}
	
	private void addPaths() {
		
		// Consider each traffic
		// For each traffic, its builds a tree
		// Then, its generates the corresponding paths
		ListIterator<NCTrafficArtifact> iterator = ncdp.getTrafficArtifacts().listIterator();
		NCTrafficArtifact arti;
		NCTraffic tr;
		TreeCell tree;
		int ret;
		//NCPath path;
		NCEqNode node;
		NCEquipment eq;
		List<String> list;
		
		while(iterator.hasNext()) {
			arti = iterator.next();
			
			tr = ncs.getTrafficByName(arti.getValue());
			if (tr == null) {
				return;
			}
			
			tree = new TreeCell();
			ret = buildTreeFromTraffic(tree, arti);
			
			if (ret < 0) {
			} else {
				// May build the paths from the tree
				node = ncdp.getNCEqNodeOf(arti);
				if (node == null) {
					return;
				}
				
				eq = (NCEquipment)(ncs.getNCLinkedElementByName(node.getName()));
				if (eq == null) {
					return;
				}
				
				list = new ArrayList<String>();
				
				exploreTree(tree, list, eq, tr);
			}
			
		}
	}
	
	private void exploreTree(TreeCell tree, List<String> list, NCEquipment origin, NCTraffic traffic) {
		NCSwitchNode sw;
		NCSwitch ncsw;
		NCLinkedElement ncle;
		NCLink ncl;
		
		if (tree.isLeaf()) {
			//
			NCPath path = new NCPath();
			path.traffic = traffic;
			path.origin = origin;
			ncle = ncs.getNCLinkedElementByName((((NCEqNode)(tree.getElement())).getName()));
			if (ncle == null) {
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown equipment named " + (((NCEqNode)(tree.getElement())).getName()) + " needed to generate path");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(null);
				checkingErrors.add(ce);
				return;
			}
			path.destination = (NCEquipment)ncle;
			ncsw = null;
			
			for(String s: list) {
				ncle = ncs.getNCLinkedElementByName(s);
				if (ncle instanceof NCSwitch) {
					if (ncsw == null) {
						ncl = ncs.getLinkWith(path.origin, ncle);
						if (ncl != null) {
						path.links.add(ncl);
						} else {
							UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown link on path origin and switch:" + ncle.getName());
							ce.setTDiagramPanel(ncdp);
							ce.setTGComponent(null);
							checkingErrors.add(ce);
							return;
						}
					} else {
						ncl = ncs.getLinkWith(ncsw, ncle);
						if (ncl != null) {
							path.links.add(ncl);
						} else {
							UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown link between switches " + ncsw.getName() + " and " + ncle.getName());
							ce.setTDiagramPanel(ncdp);
							ce.setTGComponent(null);
							checkingErrors.add(ce);
							return;
						}
					}
					
					ncsw = (NCSwitch)ncle;
					path.switches.add(ncsw);
				} else {
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown switch named " + s + " needed to generate path");
					ce.setTDiagramPanel(ncdp);
					ce.setTGComponent(null);
					checkingErrors.add(ce);
					return;
				}
			}
			ncl = ncs.getLinkWith(ncsw, path.destination);
			if (ncl != null) {
				path.links.add(ncl);
			} else {
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown link between last switch and destination");
					ce.setTDiagramPanel(ncdp);
					ce.setTGComponent(null);
					checkingErrors.add(ce);
					return;
				}
			//
			path.setName("path" + PATH_INDEX);
			PATH_INDEX++;
			ncs.paths.add(path);
			
			
			
			// not a leaf
		} else {
			sw = (NCSwitchNode)(tree.getElement());
			TreeCell next = new TreeCell();
			//
			list.add(sw.getName());
			for(int i=0; i<tree.getNbOfChildren(); i++) {
				next = tree.getChildrenByIndex(i);
				exploreTree(next, list, origin, traffic);
			}
			//
			list.remove(list.size()-1);
		}
	}
	
	public int buildTreeFromTraffic(TreeCell tree, NCTrafficArtifact arti) {
		NCEqNode node = ncdp.getNCEqNodeOf(arti);
		int ret;
		NCConnectorNode lk;
		
		if (node == null) {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Badly mapped traffic: " + arti.getValue());
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(arti);
			checkingErrors.add(ce);
			return -1;
		}
		
		// Find the only interface of that Equipment
		// If more than one interface -> error
		List<NCSwitchNode> listsw = ncdp.getSwitchesOfEq(node);
		
		if (listsw.size() == 0) {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Equipment " + node.getName() + " has non interface: traffic " + arti.getValue() + " cannot be sent over the network");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(node);
			warnings.add(ce);
			return -3;
		}
		
		if (listsw.size() > 1) {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Equipment: " + node.getName() + " has more than one interface");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(node);
			checkingErrors.add(ce);
			return -4;
		}
		
		
		
		// List size = 1. Great.
		tree.setElement(listsw.get(0));
		lk = ncdp.getConnectorOfEq(node).get(0);
		
		ret = buildTreeFromSwitch(tree, tree, listsw.get(0), lk, arti);
		
		// print tree
		
		
		if (ret < 0) {
			return ret;
		}
		
		// Must check out that all leafs are Equipments
		ArrayList<TreeCell> list = tree.getAllLeafs();
		
		if (list.size() == 0) {
			return -1;
		}
		
		for(TreeCell cell: list) {
			if (!(cell.getElement() instanceof NCEqNode)) {
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Bad route for traffic " + arti.getValue() + " at switch " + cell.getElement().toString() + " (ignoring traffic)");
				ce.setTDiagramPanel(ncdp);
				ce.setTGComponent(node);
				checkingErrors.add(ce);
				return -1;
			}
		}
		
		// Good tree!
		return 1;
	}
	
	public int buildTreeFromSwitch(TreeCell root, TreeCell tree, NCSwitchNode sw, NCConnectorNode arrivingLink, NCTrafficArtifact arti) {
		String lkname = arrivingLink.getInterfaceName();
		boolean error;
		NCLink link;
		NCLinkedElement ncle;
		NCEqNode ncen;
		NCSwitchNode ncsn;
		TreeCell cell;
		NCConnectorNode nextArriving;
		
		List<NCRoute> computed = new ArrayList<NCRoute>(); 
		
		// Get all routes concerning that traffic on that switch
		List<NCRoute> routes = ncdp.getAllRoutesFor(sw, arti);
		
		//
		
		// Get all next swithes, according to routes, and fill the tree
		// Verify that there is at least one possibile route
		boolean found = false;
		for(NCRoute route: routes) {
			if (route.inputInterface.equals(lkname)) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "No possible route for traffic " + arti.getValue() + " on switch " + sw.getNodeName() + " (ignoring trafic)");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(sw);
			warnings.add(ce);
			return -1;
		}
		
		// Verify that no route on the same switch outputs on the same input interface
		found = false;
		for(NCRoute route: routes) {
			if (route.outputInterface.equals(lkname)) {
				found = true;
				break;
			}
		}
		
		if (found) {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Cycle detected for traffic " + arti.getValue() + " on switch " + sw.getNodeName() + " (ignoring trafic)");
			ce.setTDiagramPanel(ncdp);
			ce.setTGComponent(sw);
			checkingErrors.add(ce);
			return -1;
		}
		
		for(NCRoute route: routes) {
			
			if (route.inputInterface.equals(lkname)) {
				// Must check that two routes don't have the same output interface
				//
				error = false;
				for(NCRoute route1: computed) {
					if ((route1 != route) && (route1.outputInterface.equals(route.outputInterface))) {
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Two similar routes " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring similar routes)");
						ce.setTDiagramPanel(ncdp);
						ce.setTGComponent(sw);
						warnings.add(ce);
						error = true;
					} 
				}
				computed.add(route);
				
				//
				// Is it an existing output interface?
				if (error == false) {
					link = ncs.hasLinkWith(sw.getNodeName(), route.outputInterface);
					if (link == null) {
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "No such output interface " + route.outputInterface + " in route " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring route)");
						ce.setTDiagramPanel(ncdp);
						ce.setTGComponent(sw);
						warnings.add(ce);	
					} else {
						//
						// Is the destination equipment a switch that is in the tree already?
						// If so, there is a cycle -> ignoring route
						if (link.getLinkedElement1().getName().equals(sw.getNodeName())) {
							ncle = link.getLinkedElement2();
						} else {
							ncle = link.getLinkedElement1();
						}
						
						//
						// Is the next of the route an equipment?
						if (ncle instanceof NCEquipment) {
							ncen = ncdp.getEquipmentByName(ncle.getName());
							if (ncen == null) {
								UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Internal error: could not find the equipment named " + ncle.getName() + " on diagram (ignoring route)");
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(sw);
								checkingErrors.add(ce);	
							} else {
								// Adding an equipment -> leaf of the tree
								//
								cell = new TreeCell();
								cell.setElement(ncen);
								tree.addChildren(cell);
							}
						} else {
							// The next equipment is a switch
							ncsn = ncdp.getSwitchByName(ncle.getName());
							
							// Is this switch already in the tree?
							/*if (root.containsElement(ncsn)) {
								ce = new CheckingError(CheckingError.STRUCTURE_ERROR, "Cycle detected because of route " + route.toString() + " on switch " + sw.getNodeName() + " (ignoring route)");
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(sw);
								warnings.add(ce);
							} else {*/
								// Good input and output -> must deal with the route to a switch
								cell = new TreeCell();
								cell.setElement(ncsn);
								tree.addChildren(cell);
								nextArriving = ncdp.getLinkByName(link.getName());
								if (nextArriving == null) {
									UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Internal error: could not find the equipment named " + ncle.getName() + " on diagram (ignoring route)");
									ce.setTDiagramPanel(ncdp);
									ce.setTGComponent(sw);
									checkingErrors.add(ce);	
								} else {
									// Recursive call
									//
									buildTreeFromSwitch(root, cell, ncsn, nextArriving, arti);
									//
								}
								//}
						}
					}
				}
				
			}
		}
		
		
		return 0;
	}
	
	public void manageParameters() {
		//ListIterator iterator;
		NCEqNode node1, node2;
		List<NCConnectorNode> cons1, cons2;
		NCConnectorNode con1, con2;
		int i;
		int parameter1, parameter2;
		NCEquipment nceq1, nceq2;
		NCTraffic tr1;
		NCPath path1;
		NCLink link1, link2, link11, link22;
		//NCPath path1;
		
		//ArrayList<NCEquipment> newEquipments = new ArrayList<NCEquipment>();
		//List<NCTraffic> newTraffics = new ArrayList<NCTraffic>();
		//List<NCLink> newLinks = new ArrayList<NCLink>();
		List<NCPath> newPaths = new ArrayList<NCPath>();
		
		List<NCEquipment> oldEquipments = new ArrayList<NCEquipment>();
		List<NCTraffic> oldTraffics = new ArrayList<NCTraffic>();
		List<NCLink> oldLinks = new ArrayList<NCLink>();
		List<NCPath> oldPaths = new ArrayList<NCPath>();
		
		// Are there any path to duplicate?
		for(NCPath path: ncs.paths) {
			node1 = ncdp.getNCENodeByName(path.origin.getName());
			node2 = ncdp.getNCENodeByName(path.destination.getName());
			if ((node1 != null) && (node2 != null)) {
				cons1 = ncdp.getConnectorOfEq(node1);
				cons2 = ncdp.getConnectorOfEq(node2);
				if ((cons1.size() == 1) && (cons2.size() == 1)) {
					con1 = cons1.get(0);
					con2 = cons2.get(0);
					
					if (con1.hasParameter() != con1.hasParameter()) {
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Network structure error: parameters of links starting from " + node1.getNodeName() + " and " + node2.getNodeName() + " are not compatible");
						ce.setTDiagramPanel(ncdp);
						ce.setTGComponent(node1);
						checkingErrors.add(ce);	
					} else {
						parameter1 = con1.getParameter();
						parameter2 = con2.getParameter();
						if (con1.hasParameter()) {
							if (parameter1 != parameter2) {
								UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Network structure error: parameters of links starting from " + node1.getNodeName() + " and " + node2.getNodeName() + " are not compatible");
								ce.setTDiagramPanel(ncdp);
								ce.setTGComponent(node1);
								checkingErrors.add(ce);	
							} else {
								if (parameter1 < 2) {
									UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Network structure error: parameters of links starting from " + node1.getNodeName() + " and " + node2.getNodeName() + " are not correctly set -> ignoring");
									ce.setTDiagramPanel(ncdp);
									ce.setTGComponent(node1);
									warnings.add(ce);	
								} else {
									if (!oldEquipments.contains(path.origin)) {
										link1 = ncs.getLinkWith(path.origin);
										for (i=0; i<parameter1; i++) {
											nceq1 = new NCEquipment();
											nceq1.setName(path.origin.getName() + "__" + i);
											ncs.equipments.add(nceq1);
											
											link11 = (NCLink)(link1.clone());
											if (link11.getLinkedElement1() == path.origin) {
												link11.setLinkedElement1(nceq1);
											} else {
												link11.setLinkedElement2(nceq1);
											}
											link11.setName(link1.getName() + "__" + i);
											ncs.links.add(link11);
										}
										oldEquipments.add(path.origin);
										oldLinks.add(link1);
									}
									
									if (!oldEquipments.contains(path.destination)) {
										link2 = ncs.getLinkWith(path.destination);
										for (i=0; i<con1.getParameter(); i++) {
											nceq2 = new NCEquipment();
											nceq2.setName(path.destination.getName() + "__" + i);
											ncs.equipments.add(nceq2);
											
											link22 = (NCLink)(link2.clone());
											if (link22.getLinkedElement1() == path.destination) {
												link22.setLinkedElement1(nceq2);
											} else {
												link22.setLinkedElement2(nceq2);
											}
											link22.setName(link2.getName() + "__" + i);
											ncs.links.add(link22);
										}
										oldEquipments.add(path.destination);
										oldLinks.add(link2);
									}
									
									// Paths and traffics are duplicated
									oldPaths.add(path);
									oldTraffics.add(path.traffic);
									for(i=0; i<parameter1; i++) {
										tr1 = path.traffic.cloneTraffic();
										tr1.setName(path.traffic.getName() + "__" + i);
										//
										ncs.traffics.add(tr1);
									}
									
									for(i=0; i<parameter1; i++) {
										path1 = (NCPath)(path.clone());
										path1.setName("path" + PATH_INDEX);
										PATH_INDEX++;
										tr1 = ncs.getTrafficByName(path.traffic.getName() + "__" + i);
										if (tr1 != null) {
											path1.traffic = tr1;
											nceq1 = ncs.getNCEquipmentByName(path.origin.getName() + "__" + i);
											nceq2 = ncs.getNCEquipmentByName(path.destination.getName() + "__" + i);
											if ((nceq1 != null) && (nceq2 != null)) {
												path1.origin = nceq1;
												path1.destination = nceq2;
												newPaths.add(path1);
											} else {
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// Add new paths
		for(NCPath path: newPaths) {
			ncs.paths.add(path);
		}
		
		// Remove all elements in oldTraffic , oldEquipments and oldLinks from ncs
		for(NCTraffic tr: oldTraffics) {
			ncs.traffics.remove(tr);
		}
		
		for(NCEquipment eq: oldEquipments) {
			ncs.equipments.remove(eq);
		}
		
		for(NCPath pa: oldPaths) {
			ncs.paths.remove(pa);
		}  
		
		for(NCLink li: oldLinks) {
			ncs.links.remove(li);
		}
		
		// Rename paths
		int cpt=0;
		for(NCPath pa: ncs.paths) {
			pa.setName("path" + cpt);
			cpt ++;
		}
	}
}
