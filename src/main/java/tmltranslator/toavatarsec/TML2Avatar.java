/* ludovic.apvrille AT enst.fr
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


package tmltranslator.toavatarsec;

import avatartranslator.*;
import myutil.TraceManager;
import proverifspec.ProVerifQueryResult;
import tmltranslator.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifQueryAuthResult;

/**
 * Class AVATAR2ProVerif
 * Creation: 29/02/2016
 *
 * @author Ludovic APVRILLE, Letitia LI
 * @version 1.1 29/02/2016
 */
public class TML2Avatar {
    private final static Integer channelPublic = 0;
    private final static Integer channelPrivate = 1;
    private final static Integer channelUnreachable = 2;
    //private AvatarAttribute pKey;
    public Map<TMLChannel, Integer> channelMap = new HashMap<TMLChannel, Integer>();
    public Map<TMLTask, AvatarBlock> taskBlockMap = new HashMap<TMLTask, AvatarBlock>();
    public Map<String, Integer> originDestMap = new HashMap<String, Integer>();
    public Map<String, Object> stateObjectMap = new HashMap<String, Object>();
    public Map<TMLTask, List<SecurityPattern>> accessKeys = new HashMap<TMLTask, List<SecurityPattern>>();
    public ArrayList<SecurityPattern> secPatterns = new ArrayList<SecurityPattern>();
    public int loopLimit = 1;
    HashMap<String, List<String>> secChannelMap = new HashMap<String, List<String>>();
    HashMap<String, List<AvatarAttributeState>> signalAuthOriginMap = new HashMap<String, List<AvatarAttributeState>>();
    HashMap<String, List<AvatarAttributeState>> signalAuthDestMap = new HashMap<String, List<AvatarAttributeState>>();
    List<AvatarSignal> signals = new ArrayList<AvatarSignal>();
    AvatarSpecification avspec;
    ArrayList<String> attrsToCheck;
    List<String> allStates;
    boolean mc = false;
    boolean security = false;
    private TMLMapping<?> tmlmap;
    private TMLModeling<?> tmlmodel;
    private Map<SecurityPattern, List<AvatarAttribute>> symKeys = new HashMap<SecurityPattern, List<AvatarAttribute>>();
    private Map<SecurityPattern, List<AvatarAttribute>> pubKeys = new HashMap<SecurityPattern, List<AvatarAttribute>>();
    private Map<String, String> nameMap = new HashMap<String, String>();
    private Map<String, AvatarSignal> signalInMap = new HashMap<String, AvatarSignal>();
    private Map<String, AvatarSignal> signalOutMap = new HashMap<String, AvatarSignal>();

    private Object referenceObject;

    public TML2Avatar(TMLMapping<?> tmlmap, boolean modelcheck, boolean sec, Object _referenceObject) {
        this.tmlmap = tmlmap;

        this.tmlmodel = tmlmap.getTMLModeling();

        allStates = new ArrayList<String>();
        attrsToCheck = new ArrayList<String>();
        mc = modelcheck;
        security = sec;

        referenceObject = _referenceObject;
    }

    public void checkConnections() {
        List<HwLink> links = tmlmap.getTMLArchitecture().getHwLinks();
        for (TMLTask t1 : tmlmodel.getTasks()) {
            List<SecurityPattern> keys = new ArrayList<SecurityPattern>();
            accessKeys.put(t1, keys);

            HwExecutionNode node1 = tmlmap.getHwNodeOf(t1);
            //Try to find memory using only private buses from origin
            List<HwNode> toVisit = new ArrayList<HwNode>();
            //List<HwNode> toMemory = new ArrayList<HwNode>();
            List<HwNode> complete = new ArrayList<HwNode>();
            for (HwLink link : links) {
                if (link.hwnode == node1) {
                    if (link.bus.privacy == 1) {
                        toVisit.add(link.bus);
                    }
                }
            }
            boolean memory = false;
            //memloop:
            while (toVisit.size() > 0) {
                HwNode curr = toVisit.remove(0);
                for (HwLink link : links) {
                    if (curr == link.bus) {
                        if (link.hwnode instanceof HwMemory) {
                            memory = true;
                            List<SecurityPattern> patterns = tmlmap.getMappedPatterns((HwMemory) link.hwnode);
                            accessKeys.get(t1).addAll(patterns);
                            //  break memloop;
                        }
                        if (!complete.contains(link.hwnode) && !toVisit.contains(link.hwnode) && link.hwnode instanceof HwBridge) {
                            toVisit.add(link.hwnode);
                        }
                    } else if (curr == link.hwnode) {
                        if (!complete.contains(link.bus) && !toVisit.contains(link.bus) && link.bus.privacy == 1) {
                            toVisit.add(link.bus);
                        }
                    }
                }
                complete.add(curr);
            }

            // Find path to secure memory from destination node

            for (TMLTask t2 : tmlmodel.getTasks()) {
                HwExecutionNode node2 = tmlmap.getHwNodeOf(t2);
                if (!memory) {
                    // There is no path to a private memory
                    originDestMap.put(t1.getName() + "__" + t2.getName(), channelPublic);

                } else if (node1 == node2) {
                    originDestMap.put(t1.getName() + "__" + t2.getName(), channelPrivate);

                } else {
                    //Navigate architecture for node

                    //HwNode last = node1;
                    List<HwNode> found = new ArrayList<HwNode>();
                    List<HwNode> done = new ArrayList<HwNode>();
                    List<HwNode> path = new ArrayList<HwNode>();
                    Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
                    for (HwLink link : links) {
                        if (link.hwnode == node1) {
                            found.add(link.bus);
                            List<HwNode> tmp = new ArrayList<HwNode>();
                            tmp.add(link.bus);
                            pathMap.put(link.bus, tmp);
                        }
                    }
                    outerloop:
                    while (found.size() > 0) {
                        HwNode curr = found.remove(0);
                        for (HwLink link : links) {
                            if (curr == link.bus) {
                                if (link.hwnode == node2) {
                                    path = pathMap.get(curr);
                                    break outerloop;
                                }
                                if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge) {
                                    found.add(link.hwnode);
                                    List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                                    tmp.add(link.hwnode);
                                    pathMap.put(link.hwnode, tmp);
                                }
                            } else if (curr == link.hwnode) {
                                if (!done.contains(link.bus) && !found.contains(link.bus)) {
                                    found.add(link.bus);
                                    List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
                                    tmp.add(link.bus);
                                    pathMap.put(link.bus, tmp);
                                }
                            }
                        }
                        done.add(curr);
                    }
                    if (path.size() == 0) {
                        originDestMap.put(t1.getName() + "__" + t2.getName(), channelUnreachable);
                    } else {
                        int priv = 1;
                        HwBus bus;
                        //Check if all buses and bridges are private
                        for (HwNode n : path) {
                            if (n instanceof HwBus) {
                                bus = (HwBus) n;
                                if (bus.privacy == 0) {
                                    priv = 0;
                                    break;
                                }
                            }
                        }
                        originDestMap.put(t1.getName() + "__" + t2.getName(), priv);
                    }
                }
            }
        }

    }


	/*	public void checkChannels(){
            List<TMLChannel> channels = tmlmodel.getChannels();
			List<TMLTask> destinations = new ArrayList<TMLTask>();
			TMLTask a; 
			for (TMLChannel channel: channels){	
				destinations.clear();
				if (channel.isBasicChannel()){
					a = channel.getOriginTask();
					destinations.add(channel.getDestinationTask());
				}
				else {
					a=channel.getOriginTasks().get(0);
					destinations.addAll(channel.getDestinationTasks());
				}  
				HwExecutionNode node1 = tmlmap.getHwNodeOf(a);
				for (TMLTask t: destinations){
					//List<HwBus> buses = new ArrayList<HwBus>();
					HwNode node2 = tmlmap.getHwNodeOf(t);

					//Check if each node has a secure path to memory
					
					
					

					if (node1==node2){
						channelMap.put(channel, channelPrivate);
					}

					if (node1!=node2){
						//Navigate architecture for node
						List<HwLink> links = tmlmap.getTMLArchitecture().getHwLinks();
						//HwNode last = node1;
						List<HwNode> found = new ArrayList<HwNode>();	
						List<HwNode> done = new ArrayList<HwNode>();
						List<HwNode> path = new ArrayList<HwNode>();
						Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();

						for (HwLink link: links){
							if (link.hwnode == node1){
								found.add(link.bus);
								List<HwNode> tmp = new ArrayList<HwNode>();
								tmp.add(link.bus);
								pathMap.put(link.bus, tmp);
							}
						}
						outerloop:
						while (found.size()>0){
							HwNode curr = found.remove(0);
							for (HwLink link: links){
								if (curr == link.bus){
									if (link.hwnode == node2){
										path = pathMap.get(curr);
										break outerloop;
									}
									if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
										found.add(link.hwnode);
										List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
										tmp.add(link.hwnode);
										pathMap.put(link.hwnode, tmp);
									}
								}
								else if (curr == link.hwnode){
									if (!done.contains(link.bus) && !found.contains(link.bus)){
										found.add(link.bus);
										List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
										tmp.add(link.bus);
										pathMap.put(link.bus, tmp);
									}
								}
							}
							done.add(curr);
						}

						if (path.size() ==0){
				
							channelMap.put(channel, channelUnreachable);
						}
						else {
							int priv=1;
							HwBus bus;
							//Check if all buses and bridges are private
							for (HwNode n: path){
								if (n instanceof HwBus){
									bus = (HwBus) n;
									if (bus.privacy ==0){
										priv=0;
										break;
									}
								}
							}

							channelMap.put(channel, priv);
							//TraceManager.addDev("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is " + (priv==1 ? "confidential" : "not confidential"));
						}
					}
				}
			}  
			TraceManager.addDev(channelMap);
		}*/

    public List<AvatarStateMachineElement> translateState(TMLActivityElement ae, AvatarBlock block, boolean autoAuthChans) {

        //		TMLActionState tmlaction;
        //		TMLChoice tmlchoice;
        //		TMLExecI tmlexeci;
        //		TMLExecIInterval tmlexecii;
        //	TMLExecC tmlexecc;
        //	TMLExecCInterval tmlexecci;
        //		TMLForLoop tmlforloop;
        //		TMLReadChannel tmlreadchannel;
        //		TMLSendEvent tmlsendevent;
        //		TMLSendRequest tmlsendrequest;
        //		TMLStopState tmlstopstate;
        //		TMLWaitEvent tmlwaitevent;
        //		TMLNotifiedEvent tmlnotifiedevent;
        //		TMLWriteChannel tmlwritechannel;
        //		TMLSequence tmlsequence;
        //		TMLRandomSequence tmlrsequence;
        //		TMLSelectEvt tmlselectevt;
        //		TMLDelay tmldelay;

        AvatarTransition tran = new AvatarTransition(block, "", null);
        List<AvatarStateMachineElement> elementList = new ArrayList<AvatarStateMachineElement>();

        if (ae == null) {
            return elementList;
        }

        if (ae instanceof TMLStopState) {
            AvatarStopState stops = new AvatarStopState(ae.getName(), ae.getReferenceObject(), block);
            elementList.add(stops);
            return elementList;
        } else if (ae instanceof TMLStartState) {
            AvatarStartState ss = new AvatarStartState(ae.getName(), ae.getReferenceObject(), block);
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ss.getReferenceObject());
            ss.addNext(tran);
            elementList.add(ss);
            elementList.add(tran);
        } else if (ae instanceof TMLRandom) {
            AvatarRandom ar = new AvatarRandom(ae.getName(), ae.getReferenceObject(), block);
            TMLRandom tmlr = (TMLRandom) ae;
            ar.setVariable(tmlr.getVariable());
            TraceManager.addDev("tmlr.getVariable()= " + tmlr.getVariable());
            TraceManager.addDev("tmlr.getMinValue()= " + tmlr.getMinValue());
            TraceManager.addDev("tmlr.getMaxValue()= " + tmlr.getMaxValue());
            ar.setValues(tmlr.getMinValue(), tmlr.getMaxValue());
            TraceManager.addDev("ar.getVariable()= " + ar.getVariable());
            TraceManager.addDev("ar.getMinValue()= " + ar.getMinValue());
            TraceManager.addDev("ar.getMaxValue()= " + ar.getMaxValue());
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            ar.addNext(tran);
            //Add to list
            elementList.add(ar);
            elementList.add(tran);
        } else if (ae instanceof TMLSequence) {
            //Get all list of sequences and paste together
            List<AvatarStateMachineElement> seq = translateState(ae.getNextElement(0), block, autoAuthChans);
            List<AvatarStateMachineElement> tmp;
            // elementList.addAll(seq);
            //get rid of any stops in the middle of the sequence and replace with the start of the next sequence
            for (int i = 1; i < ae.getNbNext(); i++) {
                tmp = translateState(ae.getNextElement(i), block, autoAuthChans);
                for (AvatarStateMachineElement e : seq) {
                    if (e instanceof AvatarStopState) {
                        //ignore
                    } else if (e.getNexts().size() == 0) {
                        e.addNext(tmp.get(0));
                        elementList.add(e);
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(tmp.get(0));
                        elementList.add(e);
                    } else {
                        elementList.add(e);
                    }
                }
                //elementList.addAll(tmp);
                seq = tmp;
            }
            //Put stop states on the end of the last in sequence

            for (AvatarStateMachineElement e : seq) {
                if (e.getNexts().size() == 0 && !(e instanceof AvatarStopState)) {
                    AvatarStopState stop = new AvatarStopState("stop", null, block);
                    e.addNext(stop);
                    elementList.add(stop);
                }
                elementList.add(e);
            }
            return elementList;

        } else if (ae instanceof TMLSendRequest) {
            TMLSendRequest sr = (TMLSendRequest) ae;
            TMLRequest req = sr.getRequest();
            AvatarSignal sig;

            boolean checkAcc = ae.hasCheckableAccessibility();
            boolean checked =  ae.hasCheckedAccessibility();
            AvatarState signalState = new AvatarState("signalstate_" + reworkStringName(ae.getName()) + "_" + reworkStringName(req.getName()),
                    ae.getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + req.getName(), ae.getReferenceObject());
            if (!signalOutMap.containsKey(req.getName())) {
                sig = new AvatarSignal(getNameReworked(req.getName(), 1), AvatarSignal.OUT, req.getReferenceObject());
                signals.add(sig);
                signalOutMap.put(req.getName(), sig);
                block.addSignal(sig);
                int cpt = 0;
                for (TMLType tmlt : req.getParams()) {
                    AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                    sig.addParameter(aa);
                    cpt++;
                }
            } else {
                sig = signalOutMap.get(req.getName());
            }

            AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
            for (int i = 0; i < sr.getNbOfParams(); i++) {
                if (block.getAvatarAttributeWithName(sr.getParam(i)) == null) {
                    //Throw Error
                    AvatarType type;
                    if (sr.getParam(i).matches("-?\\d+")) {
                        type = AvatarType.INTEGER;
                    } else if (sr.getParam(i).matches("(?i)^(true|false)")) {
                        type = AvatarType.BOOLEAN;
                    } else {
                        type = AvatarType.UNDEFINED;
                    }
                    String nameNewAtt = getNameReworked(req.getName(), 1) + "_" + req.getID() + "_" + i + "_" + sr.getParam(i);
                    if (block.getAvatarAttributeWithName(nameNewAtt) == null) {
                        AvatarAttribute avattr = new AvatarAttribute(nameNewAtt, type, block, null);
                        avattr.setInitialValue(sr.getParam(i));
                        block.addAttribute(avattr);
                        as.addValue(avattr.getName());
                        TraceManager.addDev("Missing Attribute " + sr.getParam(i));
                    } else {
                        as.addValue(block.getAvatarAttributeWithName(nameNewAtt).getName());
                    }
                } else {
                    //	Add parameter to signal and actiononsignal
                    as.addValue(sr.getParam(i));
                }
            }
                /*
				if (req.checkAuth){
					AvatarAttributeState authOrig = new AvatarAttributeState(block.getName()+"."+signalState.getName()+"."+requestData.getName(),ae.getReferenceObject(),requestData, signalState);
					signalAuthOriginMap.put(req.getName(), authOrig);
				}*/
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            elementList.add(signalState);
            signalState.addNext(signalTran);
            elementList.add(signalTran);
            signalTran.addNext(as);
            elementList.add(as);
            as.addNext(tran);
            elementList.add(tran);
        } else if (ae instanceof TMLRandomSequence) {
            //HashMap<Integer, List<AvatarStateMachineElement>> seqs = new HashMap<Integer, List<AvatarStateMachineElement>>();
            AvatarState choiceStateInit = new AvatarState("seqchoiceInit__" + reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            AvatarState choiceState = new AvatarState("seqchoice__" + reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            AvatarTransition tranChoiceStateInit = new AvatarTransition(block, "trans_seqchoiceInit__" + reworkStringName(ae.getName()), ae.getReferenceObject());
            elementList.add(choiceStateInit);
            elementList.add(tranChoiceStateInit);
            choiceStateInit.addNext(tranChoiceStateInit);
            tranChoiceStateInit.addNext(choiceState);
            elementList.add(choiceState);

            if (ae.getNbNext() == 1) {
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_0", ae.getReferenceObject());
                elementList.add(tran);
                choiceState.addNext(tran);
                List<AvatarStateMachineElement> set0 = translateState(ae.getNextElement(0), block, autoAuthChans);
                tran.addNext(set0.get(0));
                elementList.addAll(set0);
                return elementList;
            } else {
                block.addAttribute(new AvatarAttribute("seqNb_" + ae.getName() + ae.getID(), AvatarType.INTEGER, block, null));
                for (int i = 0; i < ae.getNbNext(); i++) {
                    tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + i, ae.getReferenceObject());
                    block.addAttribute(new AvatarAttribute("seq_" + ae.getName() + i + "_" + ae.getID(), AvatarType.INTEGER, block, null));
                    tran.addAction("seq_" + ae.getName() + i + "_" + ae.getID() + " = 1");
                    tran.addAction("seqNb_" + ae.getName() + ae.getID() +  " = " + "seqNb_" + ae.getName() + ae.getID() + " + 1");
                    tran.setGuard("seq_" + ae.getName() + i + "_" + ae.getID() + " == 0");
                    if (i == 0) {
                        tranChoiceStateInit.addAction("seqNb_" + ae.getName() + ae.getID() +  " = 0");
                    }
                    tranChoiceStateInit.addAction("seq_" + ae.getName() + i + "_" + ae.getID() + " = 0");
                    //tran.addAction(AvatarTerm.createActionFromString(block, "seq_"+ae.getName()+i+ae.getID()+" = 1"));
                    //tran.addAction(AvatarTerm.createActionFromString(block, "seqNb_"+ae.getName()+ae.getID()+" = 1 + " + "seqNb_"+ae.getName()+ae.getID()));
                    
                    choiceState.addNext(tran);
                    elementList.add(tran);
                    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block, autoAuthChans);
                    AvatarState choiceStateEnd = new AvatarState("seqchoiceend__" + i + "_" + reworkStringName(ae.getName()), ae.getReferenceObject(), block);
                    AvatarTransition tranChoiceStateEnd = new AvatarTransition(block, "trans_seqchoiceend__" + i + "_" + reworkStringName(ae.getName()), ae.getReferenceObject());
                    choiceStateEnd.addNext(tranChoiceStateEnd);
                    elementList.add(choiceStateEnd);
                    elementList.add(tranChoiceStateEnd);
                    //elementList.addAll(tmp);
                    for (AvatarStateMachineElement e : tmp) {
                        if (e instanceof AvatarStopState) {
                            //ignore
                        } else if (e.getNexts().size() == 0) {
                            e.addNext(choiceStateEnd);
                            tranChoiceStateEnd.addNext(choiceState);
                            elementList.add(e);
                        } else if (e.getNext(0) instanceof AvatarStopState) {
                            e.removeNext(0);
                            e.addNext(choiceStateEnd);
                            tranChoiceStateEnd.addNext(choiceState);
                            elementList.add(e);
                        } else {
                            elementList.add(e);
                        }
                    }
                    tran.addNext(tmp.get(0));
                }
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + ae.getNbNext(), ae.getReferenceObject());
                tran.setGuard("seqNb_" + ae.getName() + ae.getID() + " == " + ae.getNbNext());
                choiceState.addNext(tran);
                elementList.add(tran);
                AvatarStopState stop = new AvatarStopState("stop", null, block);
                tran.addNext(stop);
                elementList.add(stop);
                
            }
            /*if (ae.getNbNext() == 2) {
                List<AvatarStateMachineElement> set0 = translateState(ae.getNextElement(0), block, autoAuthChans);
                List<AvatarStateMachineElement> set1 = translateState(ae.getNextElement(1), block, autoAuthChans);
                //		elementList.addAll(set0);

                //Remove stop states of sets and route their transitions to the first element of the following sequence
                for (AvatarStateMachineElement e : set0) {
                    if (e instanceof AvatarStopState) {
                        //ignore
                    } else if (e.getNexts().size() == 0) {
                        e.addNext(set1.get(0));
                        elementList.add(e);
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(set1.get(0));
                        elementList.add(e);
                    } else {
                        elementList.add(e);
                    }
                }


                //Build branch 0
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_0", ae.getReferenceObject());
                choiceState.addNext(tran);
                elementList.add(tran);
                tran.addNext(set0.get(0));
                //Put stop states at the end of set1 if they don't already exist
                AvatarStopState stop = new AvatarStopState("stop", null, block);
                for (AvatarStateMachineElement e : set1) {
                    if (e.getNexts().size() == 0 && (e instanceof AvatarTransition)) {
                        e.addNext(stop);
                    }
                    elementList.add(e);
                }
                elementList.add(stop);

                //Build branch 1
                List<AvatarStateMachineElement> set0_1 = translateState(ae.getNextElement(0), block, autoAuthChans);
                List<AvatarStateMachineElement> set1_1 = translateState(ae.getNextElement(1), block, autoAuthChans);
                for (AvatarStateMachineElement e : set1_1) {
                    if (e instanceof AvatarStopState) {
                        //ignore
                    } else if (e.getNexts().size() == 0) {
                        e.addNext(set0_1.get(0));
                        elementList.add(e);
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(set0_1.get(0));
                        elementList.add(e);
                    } else {
                        elementList.add(e);
                    }
                }
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_1", ae.getReferenceObject());
                elementList.add(tran);
                choiceState.addNext(tran);
                tran.addNext(set1_1.get(0));
                stop = new AvatarStopState("stop", null, block);
                for (AvatarStateMachineElement e : set0_1) {
                    if (e.getNexts().size() == 0 && (e instanceof AvatarTransition)) {
                        e.addNext(stop);
                    }
                    elementList.add(e);
                }
                elementList.add(stop);

            } else {
                //This gets really complicated in Avatar...
                for (int i = 0; i < ae.getNbNext(); i++) {
                    //For each of the possible state blocks, translate 1 and recurse on the remaining random sequence
                    tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + i, ae.getReferenceObject());
                    choiceState.addNext(tran);
                    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block, autoAuthChans);


                    AvatarState choiceStateEnd = new AvatarState("seqchoiceend__" + i + "_" +
                            reworkStringName(ae.getName()), ae.getReferenceObject(), block);
                    elementList.add(choiceStateEnd);


                    //Remove stop states from the first generated set
                    for (AvatarStateMachineElement e : tmp) {
                        if (e instanceof AvatarStopState) {
                            //ignore
                        } else if (e.getNexts().size() == 0) {
                            //e.addNext(set1.get(0));
                            e.addNext(choiceStateEnd);
                            elementList.add(e);
                        } else if (e.getNext(0) instanceof AvatarStopState) {
                            //Remove the transition to AvatarStopState
                            e.removeNext(0);
                            e.addNext(choiceStateEnd);
                            //e.addNext(set1.get(0));
                            elementList.add(e);
                        } else {
                            elementList.add(e);
                        }
                    }


                    tran.addNext(tmp.get(0));

                    TMLRandomSequence newSeq = new TMLRandomSequence("seqchoice__" + i + "_" + ae.getNbNext() + "_" + ae.getName(), ae.getReferenceObject());
                    for (int j = 0; j < ae.getNbNext(); j++) {
                        if (j != i) {
                            newSeq.addNext(ae.getNextElement(j));
                        }
                    }


                    tran = new AvatarTransition(block, "__after_" + ae.getNextElement(i).getName(), ae.getReferenceObject());
                    choiceStateEnd.addNext(tran);
                    elementList.add(tran);

                    List<AvatarStateMachineElement> nexts = translateState(newSeq, block, autoAuthChans);
                    elementList.addAll(nexts);
                    tran.addNext(nexts.get(0));

                }

            }*/
            return elementList;
        } else if (ae instanceof TMLActivityElementEvent) {
            TMLActivityElementEvent aee = (TMLActivityElementEvent) ae;
            TMLEvent evt = aee.getEvent();

            boolean checkAcc = ae.hasCheckableAccessibility();
            boolean checked =  ae.hasCheckedAccessibility();
            AvatarState signalState = new AvatarState("signalstate_" + reworkStringName(ae.getName()) + "_" + evt.getName(), ae.getReferenceObject(),
                    block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + evt.getName(),
                    ae.getReferenceObject());

            if (ae instanceof TMLSendEvent) {
                AvatarSignal sig;
                if (!signalOutMap.containsKey(evt.getName())) {
                    sig = new AvatarSignal(getNameReworked(evt.getName(), 1), AvatarSignal.OUT, evt.getReferenceObject());
                    signals.add(sig);
                    block.addSignal(sig);
                    signalOutMap.put(evt.getName(), sig);
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }
                } else {
                    sig = signalOutMap.get(evt.getName());
                }
                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
                for (int i = 0; i < aee.getNbOfParams(); i++) {
                    if (block.getAvatarAttributeWithName(aee.getParam(i)) == null) {
                        //Throw Error
                        AvatarType type;
                        if (aee.getParam(i).matches("-?\\d+")) {
                            type = AvatarType.INTEGER;
                        } else if (aee.getParam(i).matches("(?i)^(true|false)")) {
                            type = AvatarType.BOOLEAN;
                        } else {
                            type = AvatarType.UNDEFINED;
                        }
                        String nameNewAtt = getNameReworked(evt.getName(), 1) + "_" + evt.getID() + "_" + i + "_" + aee.getParam(i);
                        if (block.getAvatarAttributeWithName(nameNewAtt) == null) {
                            AvatarAttribute avattr = new AvatarAttribute(nameNewAtt, type, block, null);
                            avattr.setInitialValue(aee.getParam(i));
                            block.addAttribute(avattr);
                            as.addValue(avattr.getName());
                            TraceManager.addDev("Missing Attribute " + aee.getParam(i));
                        } else {
                            as.addValue(block.getAvatarAttributeWithName(nameNewAtt).getName());
                        }
                    } else {
                        //	Add parameter to signal and actiononsignal
                        as.addValue(aee.getParam(i));
                    }
                }

                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                elementList.add(signalState);
                signalState.addNext(signalTran);
                elementList.add(signalTran);
                signalTran.addNext(as);
                elementList.add(as);
                as.addNext(tran);
                elementList.add(tran);


            } else if (ae instanceof TMLWaitEvent) {
                AvatarSignal sig;
                if (!signalInMap.containsKey(evt.getName())) {
                    sig = new AvatarSignal(getNameReworked(evt.getName(), 3), AvatarSignal.IN, evt.getReferenceObject());
                    signals.add(sig);
                    block.addSignal(sig);
                    signalInMap.put(evt.getName(), sig);
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }
                } else {
                    sig = signalInMap.get(evt.getName());
                }
                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
                for (int i = 0; i < aee.getNbOfParams(); i++) {
                    if (block.getAvatarAttributeWithName(aee.getParam(i)) == null) {
                        //Throw Error
                        AvatarType type;
                        if (aee.getParam(i).matches("-?\\d+")) {
                            type = AvatarType.INTEGER;
                        } else if (aee.getParam(i).matches("(?i)^(true|false)")) {
                            type = AvatarType.BOOLEAN;
                        } else {
                            type = AvatarType.UNDEFINED;
                        }
                        String nameNewAtt = getNameReworked(evt.getName(), 3) + "_" + evt.getID() + "_" + i + "_" + aee.getParam(i);
                        if (block.getAvatarAttributeWithName(nameNewAtt) == null) {
                            AvatarAttribute avattr = new AvatarAttribute(nameNewAtt, type, block, null);
                            avattr.setInitialValue(aee.getParam(i));
                            block.addAttribute(avattr);
                            as.addValue(avattr.getName());
                            TraceManager.addDev("Missing Attribute " + aee.getParam(i));
                        } else {
                            as.addValue(block.getAvatarAttributeWithName(nameNewAtt).getName());
                        }
                    } else {
                        //	Add parameter to signal and actiononsignal
                        as.addValue(aee.getParam(i));
                    }
                }

                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                elementList.add(signalState);
                signalState.addNext(signalTran);
                elementList.add(signalTran);
                signalTran.addNext(as);
                elementList.add(as);
                as.addNext(tran);
                elementList.add(tran);
            } else {
                //Notify Event, I don't know how to translate this
                TraceManager.addDev("Notify event");
                AvatarRandom as = new AvatarRandom(ae.getName(), ae.getReferenceObject(), block);
                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                as.setVariable(aee.getVariable());
                as.setValues("0", "1");
                as.addNext(tran);
                elementList.add(as);
                elementList.add(tran);
            }

        } else if (ae instanceof TMLActivityElementWithAction) {
            //Might be encrypt or decrypt
            AvatarState as = new AvatarState(ae.getValue().replaceAll(
                    " ", "").replaceAll("\\*", "").replaceAll("\\+", "").replaceAll(
                            "\\-", "") + "_" + ae.getName().replaceAll(" ", "").replaceAll(
                                    "\\*", "").replaceAll("\\+", "").replaceAll(
                                            "\\-", ""), ae.getReferenceObject(), block);
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            as.addNext(tran);
            elementList.add(as);
            elementList.add(tran);
            if (security && ae.securityPattern != null) {
                //If encryption
                if (ae.securityPattern != null && ae.securityPattern.process == SecurityPattern.ENCRYPTION_PROCESS) {
                    secPatterns.add(ae.securityPattern);
                    if (ae.securityPattern.type.equals(SecurityPattern.ADVANCED_PATTERN)) {
                        //Type Advanced
                        tran.addAction(ae.securityPattern.formula);
                    } else if (ae.securityPattern.type.equals(SecurityPattern.SYMMETRIC_ENC_PATTERN)) {
                        //Type Symmetric Encryption
                        if (!ae.securityPattern.nonce.isEmpty()) {
                            //Concatenate nonce to data

                            //Create concat2 method
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.nonce, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));

                            AvatarMethod concat2 = new AvatarMethod("concat2", ae);
                            concat2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            concat2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.nonce));
                            concat2.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));

                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.nonce) != null) {
                                block.addMethod(concat2);
                                tran.addAction(ae.securityPattern.name + "=concat2(" + ae.securityPattern.name + "," + ae.securityPattern.nonce + ")");
                            }
                        }
                        if (!ae.securityPattern.key.isEmpty()) {
                            //Securing a key

                            //Create sencrypt method for key
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));

                            AvatarMethod sencrypt = new AvatarMethod("sencrypt", ae);
                            sencrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.key));
                            sencrypt.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                            sencrypt.addReturnParameter(block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key));

                            if (block.getAvatarAttributeWithName(ae.securityPattern.key) != null && block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null) {
                                block.addMethod(sencrypt);
                                tran.addAction("encryptedKey_" + ae.securityPattern.key + " = sencrypt(key_" + ae.securityPattern.key + ", key_" + ae.securityPattern.name + ")");
                            }
                        } else {
                            //Securing data

                            //Create sencrypt method for data
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));

                            AvatarMethod sencrypt = new AvatarMethod("sencrypt", ae);
                            sencrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            sencrypt.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                            sencrypt.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));

                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null) {
                                block.addMethod(sencrypt);
                            }
                            tran.addAction(ae.securityPattern.name + "_encrypted = sencrypt(" + ae.securityPattern.name + ", key_" + ae.securityPattern.name + ")");
                        }
                        //Set as origin for authenticity
                        ae.securityPattern.originTask = block.getName();
                        ae.securityPattern.state1 = as;
                    } else if (ae.securityPattern.type.equals(SecurityPattern.ASYMMETRIC_ENC_PATTERN)) {
                        if (!ae.securityPattern.nonce.isEmpty()) {
                            //Concatenating a nonce
                            //Add concat2 method
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.nonce, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            AvatarMethod concat2 = new AvatarMethod("concat2", ae);
                            concat2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            concat2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.nonce));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.nonce) != null) {
                                block.addMethod(concat2);
                                tran.addAction(ae.securityPattern.name + "=concat2(" + ae.securityPattern.name + "," + ae.securityPattern.nonce + ")");
                            }
                        }
                        //Securing a key instead of data
                        if (!ae.securityPattern.key.isEmpty()) {
                            //Add aencrypt method
                            AvatarMethod aencrypt = new AvatarMethod("aencrypt", ae);
                            block.addAttribute(new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("pubKey_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            aencrypt.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.key));
                            aencrypt.addParameter(block.getAvatarAttributeWithName("pubKey_" + ae.securityPattern.name));
                            aencrypt.addReturnParameter(block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key));
                            if (block.getAvatarAttributeWithName("key_" + ae.securityPattern.key) != null && block.getAvatarAttributeWithName("pubKey_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key) != null) {
                                block.addMethod(aencrypt);
                                tran.addAction("encryptedKey_" + ae.securityPattern.key + " = aencrypt(key_" + ae.securityPattern.key + ", pubKey_" + ae.securityPattern.name + ")");
                            }
                        } else {
                            //Securing data

                            //Add aencrypt method
                            AvatarMethod aencrypt = new AvatarMethod("aencrypt", ae);
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("pubKey_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null));
                            aencrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            aencrypt.addParameter(block.getAvatarAttributeWithName("pubKey_" + ae.securityPattern.name));
                            aencrypt.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));
                            if (block.getAvatarAttributeWithName("pubKey_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null) {
                                block.addMethod(aencrypt);
                                tran.addAction(ae.securityPattern.name + "_encrypted = aencrypt(" + ae.securityPattern.name + ", pubKey_" + ae.securityPattern.name + ")");
                            }
                        }
                        //Set as origin state for authenticity
                        ae.securityPattern.originTask = block.getName();
                        ae.securityPattern.state1 = as;
                    } else if (ae.securityPattern.type.equals(SecurityPattern.NONCE_PATTERN)) {
                        //Do nothing except occupy time to forge nonce


                        //Create a nonce by a random function
							/*AvatarRandom arandom = new AvatarRandom("randomnonce",ae.getReferenceObject());
							arandom.setVariable(ae.securityPattern.name);
							arandom.setValues("0","10");
							elementList.add(arandom);
							tran.addNext(arandom);
							tran = new AvatarTransition(block, "__afterrandom_"+ae.getName(), ae.getReferenceObject());
							arandom.addNext(tran);
							elementList.add(tran);
							block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));*/
                    } else if (ae.securityPattern.type.equals(SecurityPattern.HASH_PATTERN)) {
                        AvatarMethod hash = new AvatarMethod("hash", ae);
                        hash.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            block.addMethod(hash);
                        }
                        tran.addAction(ae.securityPattern.name + "_encrypted = hash(" + ae.securityPattern.name + ")");
                    } else if (ae.securityPattern.type.equals(SecurityPattern.MAC_PATTERN)) {
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                        block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null)); //msg + mac(msg)
                        if (!ae.securityPattern.nonce.isEmpty()) {
                            //Add nonce

                            //Add concat2 method
                            AvatarMethod concat = new AvatarMethod("concat2", ae);
                            concat.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            concat.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.nonce));
                            concat.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.nonce) != null) {
                                block.addMethod(concat);
                                tran.addAction(ae.securityPattern.name + "=concat2(" + ae.securityPattern.name + "," + ae.securityPattern.nonce + ")");
                            }
                        }

                        //Create MAC method
                        AvatarMethod mac = new AvatarMethod("MAC", ae);
                        AvatarAttribute macattr = new AvatarAttribute(ae.securityPattern.name + "_mac", AvatarType.INTEGER, block, null);
                        block.addAttribute(macattr);


                        mac.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                        mac.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                        mac.addReturnParameter(macattr);

                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null) {
                            block.addMethod(mac);
                            tran.addAction(ae.securityPattern.name + "_mac = MAC(" + ae.securityPattern.name + ",key_" + ae.securityPattern.name + ")");
                        }

                        //Concatenate msg and mac(msg)

                        //Create concat2 method
                        AvatarMethod concat = new AvatarMethod("concat2", ae);
                        concat.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                        concat.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_mac"));
                        concat.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));
                        //concat.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.));
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null) {
                            block.addMethod(concat);
                            tran.addAction(ae.securityPattern.name + "_encrypted = concat2(" + ae.securityPattern.name + "," + ae.securityPattern.name + "_mac)");
                        }
                        ae.securityPattern.originTask = block.getName();
                        ae.securityPattern.state1 = as;
                    }

                    // Set attribute state for authenticity
                    if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                        AvatarAttributeState authOrigin = new AvatarAttributeState(block.getName() + "." + as.getName() + "." +
                                ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), as);
                        if (signalAuthOriginMap.containsKey(ae.securityPattern.name)) {
                            signalAuthOriginMap.get(ae.securityPattern.name).add(authOrigin);
                        } else {
                            LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                            tmp.add(authOrigin);
                            signalAuthOriginMap.put(ae.securityPattern.name, tmp);
                        }
                    }

                } else if (ae.securityPattern != null && ae.securityPattern.process == SecurityPattern.DECRYPTION_PROCESS) {
                    //Decryption action
                    //block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                    //block.addAttribute(new AvatarAttribute(ae.securityPattern.name+"_encrypted", AvatarType.INTEGER, block, null));
                    if (ae.securityPattern.type.equals(SecurityPattern.SYMMETRIC_ENC_PATTERN)) {
                        if (!ae.securityPattern.key.isEmpty()) {
                            //Decrypting a key
                            //Add sdecrypt method
                            AvatarMethod sdecrypt = new AvatarMethod("sdecrypt", ae);
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));

                            sdecrypt.addParameter(block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key));
                            sdecrypt.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                            sdecrypt.addReturnParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.key));
                            if (block.getAvatarAttributeWithName("key_" + ae.securityPattern.key) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key) != null) {
                                block.addMethod(sdecrypt);
                                tran.addAction("key_" + ae.securityPattern.key + " = sdecrypt(encryptedKey_" + ae.securityPattern.key + ", key_" + ae.securityPattern.name + ")");
                            }
                        } else {
                            //Decrypting data
                            AvatarMethod sdecrypt = new AvatarMethod("sdecrypt", ae);
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null));

                            sdecrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));
                            sdecrypt.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                            sdecrypt.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                                block.addMethod(sdecrypt);
                                tran.addAction(ae.securityPattern.name + " = sdecrypt(" + ae.securityPattern.name + "_encrypted, key_" + ae.securityPattern.name + ")");
                            }
                        }
                        if (!ae.securityPattern.nonce.isEmpty()) {
                            //Separate out the nonce
                            block.addAttribute(new AvatarAttribute("testnonce_" + ae.securityPattern.nonce, AvatarType.INTEGER, block, null));
                            //Add get2 method
                            AvatarMethod get2 = new AvatarMethod("get2", ae);
                            get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            get2.addParameter(block.getAvatarAttributeWithName("testnonce_" + ae.securityPattern.nonce));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null &&
                                    block.getAvatarAttributeWithName("testnonce_" + ae.securityPattern.nonce) != null) {
                                block.addMethod(get2);
                                tran.addAction("get2(" + ae.securityPattern.name + "," + ae.securityPattern.name + ",testnonce_" + ae.securityPattern.nonce + ")");
                            }

                            //Add state after get2 statement
                            AvatarState guardState = new AvatarState(reworkStringName(ae.getName()) + "_guarded", ae.getReferenceObject(), block);
                            tran.addNext(guardState);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            guardState.addNext(tran);
                            elementList.add(guardState);
                            elementList.add(tran);

                            //Guard transition to determine if nonce matches
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                        }

                        // Add a dummy state afterwards for authenticity after decrypting the data
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName()) + "_dummy", ae.getReferenceObject(), block);
                        ae.securityPattern.state2 = dummy;
                        tran.addNext(dummy);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." +
                                    ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            if (signalAuthDestMap.containsKey(ae.securityPattern.name)) {
                                signalAuthDestMap.get(ae.securityPattern.name).add(authDest);
                            } else {
                                LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                                tmp.add(authDest);
                                signalAuthDestMap.put(ae.securityPattern.name, tmp);
                            }
                        }
                    } else if (ae.securityPattern.type.equals(SecurityPattern.ASYMMETRIC_ENC_PATTERN)) {
                        AvatarMethod adecrypt = new AvatarMethod("adecrypt", ae);

                        if (!ae.securityPattern.key.isEmpty()) {
                            //Decrypting key
                            //Add adecrypt method
                            block.addAttribute(new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("privKey_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("key_" + ae.securityPattern.key, AvatarType.INTEGER, block, null));

                            adecrypt.addParameter(block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key));
                            adecrypt.addParameter(block.getAvatarAttributeWithName("privKey_" + ae.securityPattern.name));
                            adecrypt.addReturnParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.key));

                            if (block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key) != null &&
                                    block.getAvatarAttributeWithName("privKey_" + ae.securityPattern.name) != null &&
                                    block.getAvatarAttributeWithName("key_" + ae.securityPattern.key) != null) {
                                block.addMethod(adecrypt);
                                tran.addAction("key_" + ae.securityPattern.key + " = adecrypt(encryptedKey_" + ae.securityPattern.key + ", privKey_" + ae.securityPattern.name + ")");
                            }
                        } else {
                            //Decrypting data

                            //Add adecrypt method
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                            block.addAttribute(new AvatarAttribute("privKey_" + ae.securityPattern.name, AvatarType.INTEGER, block, null));

                            adecrypt.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));
                            adecrypt.addParameter(block.getAvatarAttributeWithName("privKey_" + ae.securityPattern.name));
                            adecrypt.addReturnParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null && block.getAvatarAttributeWithName("privKey_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                                block.addMethod(adecrypt);
                                tran.addAction(ae.securityPattern.name + " = adecrypt(" + ae.securityPattern.name + "_encrypted, privKey_" + ae.securityPattern.name + ")");
                            }
                        }
                        if (!ae.securityPattern.nonce.isEmpty()) {
                            block.addAttribute(new AvatarAttribute("testnonce_" + ae.securityPattern.nonce, AvatarType.INTEGER, block, null));
                            AvatarMethod get2 = new AvatarMethod("get2", ae);
                            get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                            get2.addParameter(block.getAvatarAttributeWithName("testnonce_" + ae.securityPattern.nonce));
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                                block.addMethod(get2);
                            }

                            tran.addAction("get2(" + ae.securityPattern.name + "," + ae.securityPattern.name + ",testnonce_" + ae.securityPattern.nonce + ")");
                            AvatarState guardState = new AvatarState(reworkStringName(ae.getName()) + "_guarded", ae.getReferenceObject(), block);
                            tran.addNext(guardState);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            elementList.add(guardState);
                            elementList.add(tran);
                            guardState.addNext(tran);
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                        }
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName()) + "_dummy", ae.getReferenceObject(), block);
                        tran.addNext(dummy);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);
                        ae.securityPattern.state2 = dummy;
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." + ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            if (signalAuthDestMap.containsKey(ae.securityPattern.name)) {
                                signalAuthDestMap.get(ae.securityPattern.name).add(authDest);
                            } else {
                                LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                                tmp.add(authDest);
                                signalAuthDestMap.put(ae.securityPattern.name, tmp);
                            }
                        }
                    } else if (ae.securityPattern.type.equals(SecurityPattern.MAC_PATTERN)) {
                        //Separate MAC from MSG

                        //Add get2 method
                        AvatarMethod get2 = new AvatarMethod("get2", ae);
                        AvatarAttribute mac = new AvatarAttribute(ae.securityPattern.name + "_mac", AvatarType.INTEGER, block, null);
                        block.addAttribute(mac);
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null));
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));

                        get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted"));
                        get2.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                        get2.addParameter(mac);
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName(ae.securityPattern.name + "_encrypted") != null) {
                            block.addMethod(get2);
                            tran.addAction("get2(" + ae.securityPattern.name + "_encrypted," + ae.securityPattern.name + "," + ae.securityPattern.name + "_mac)");
                        }

                        //Add verifymac method
                        AvatarMethod verifymac = new AvatarMethod("verifyMAC", ae);
                        block.addAttribute(new AvatarAttribute("testnonce_" + ae.securityPattern.name, AvatarType.BOOLEAN, block, null));

                        verifymac.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name));
                        verifymac.addParameter(block.getAvatarAttributeWithName("key_" + ae.securityPattern.name));
                        verifymac.addParameter(block.getAvatarAttributeWithName(ae.securityPattern.name + "_mac"));
                        verifymac.addReturnParameter(block.getAvatarAttributeWithName("testnonce_" + ae.securityPattern.name));

                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.name) != null) {
                            block.addMethod(verifymac);
                            tran.addAction("testnonce_" + ae.securityPattern.name + "=verifyMAC(" + ae.securityPattern.name + ", key_" + ae.securityPattern.name + "," + ae.securityPattern.name + "_mac)");

                        }


                        if (!ae.securityPattern.nonce.isEmpty()) {
                            block.addAttribute(new AvatarAttribute("testnonce_" + ae.securityPattern.nonce, AvatarType.INTEGER, block, null));
                            tran.addAction("get2(" + ae.securityPattern.name + "," + ae.securityPattern.name + ",testnonce_" + ae.securityPattern.nonce + ")");
                        }

                        AvatarState guardState = new AvatarState(reworkStringName(ae.getName()) + "_guarded", ae.getReferenceObject(), block);
                        tran.addNext(guardState);
                        tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                        elementList.add(guardState);
                        elementList.add(tran);
                        guardState.addNext(tran);
                        tran.setGuard("testnonce_" + ae.securityPattern.name);

                        if (!ae.securityPattern.nonce.isEmpty()) {

                            //Add extra state and transition

                            AvatarState guardState2 = new AvatarState(reworkStringName(ae.getName()) + "_guarded2", ae.getReferenceObject(), block);
                            tran.addNext(guardState2);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                            elementList.add(guardState2);
                            elementList.add(tran);

                            guardState2.addNext(tran);
                        }
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName()) + "_dummy", ae.getReferenceObject(), block);
                        ae.securityPattern.state2 = dummy;
                        tran.addNext(dummy);
                        elementList.add(tran);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);

                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." + ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            if (signalAuthDestMap.containsKey(ae.securityPattern.name)) {
                                signalAuthDestMap.get(ae.securityPattern.name).add(authDest);
                            } else {
                                LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                                tmp.add(authDest);
                                signalAuthDestMap.put(ae.securityPattern.name, tmp);
                            }
                        }
                    }


                    //Can't decrypt hash or nonce
                }
            } else {
                //Translate state without security
                if (ae instanceof TMLActionState) {
                    String val = ((TMLActionState) ae).getAction();
                    tran.addAction(reworkStringName(val));
                } else if (ae instanceof TMLExecI) {
                    tran.setDelays(reworkStringName(((TMLExecI) (ae)).getAction()), reworkStringName(((TMLExecI) (ae)).getAction()));
                } else if (ae instanceof TMLExecC) {
                    tran.setDelays(reworkStringName(((TMLExecC) (ae)).getAction()), reworkStringName(((TMLExecC) (ae)).getAction()));
                }
            }
        } else if (ae instanceof TMLActivityElementWithIntervalAction) {
            AvatarState as = new AvatarState(reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            as.addNext(tran);
            elementList.add(as);
            elementList.add(tran);

            // Channels
        } else if (ae instanceof TMLActivityElementChannel) {
            TMLActivityElementChannel aec = (TMLActivityElementChannel) ae;
            TMLChannel ch = aec.getChannel(0);
            AvatarSignal sig;
            boolean checkAcc = ae.hasCheckableAccessibility();
            boolean checked =  ae.hasCheckedAccessibility();
            AvatarState signalState = new AvatarState("signalstate_" + reworkStringName(ae.getName()) + "_" +
                    ch.getName(), ae.getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + ch.getName(),
                    ae.getReferenceObject());

            if (ae instanceof TMLReadChannel) {
                // Read channel
                // Create signal if it does not already exist
                TraceManager.addDev("InMap  Looking for signal: " + ch.getName());
                if (!signalInMap.containsKey(ch.getName())) {
                    TraceManager.addDev("Not in InMap. Creating " + getName(ch.getName()));
                    sig = new AvatarSignal(getName(ch.getName()), AvatarSignal.IN, ch.getReferenceObject());
                    signals.add(sig);
                    block.addSignal(sig);
                    signalInMap.put(ch.getName(), sig);
                    AvatarAttribute channelData = new AvatarAttribute(getName(ch.getName()) + "_chData", AvatarType.INTEGER, block, null);
                    if (block.getAvatarAttributeWithName(getName(ch.getName()) + "_chData") == null) {
                        block.addAttribute(channelData);
                    }
                    sig.addParameter(channelData);
                } else {
                    sig = signalInMap.get(ch.getName());
                }
                TraceManager.addDev("InMap sig= " + sig.getSignalName());
                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);

                if (ae.securityPattern != null) {
                    //If nonce
                    if (ae.securityPattern.type.equals(SecurityPattern.NONCE_PATTERN)) {
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                        as.addValue(ae.securityPattern.name);
                    }
                    //Send the encrypted key
                    else if (!ae.securityPattern.key.isEmpty()) {
                        as.addValue("encryptedKey_" + ae.securityPattern.key);
                        AvatarAttribute data = new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null);
                        block.addAttribute(data);
                    } else {
                        //Send the encrypted data
                        if (!secChannelMap.containsKey(ae.securityPattern.name)) {
                            List<String> tmp = new ArrayList<String>();
                            secChannelMap.put(ae.securityPattern.name, tmp);
                        }

                        secChannelMap.get(ae.securityPattern.name).add(ch.getName());
                        if (aec.getEncForm()) {
                            as.addValue(ae.securityPattern.name + "_encrypted");
                            AvatarAttribute data = new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null);
                            block.addAttribute(data);
                        } else {
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) == null) {
                                AvatarAttribute data = new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null);
                                block.addAttribute(data);
                            }
                            as.addValue(ae.securityPattern.name);

                        }
                    }
                } else {
                    as.addValue(ch.getDestinationPort().getName() + "_chData");
                }

                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                elementList.add(signalState);
                signalState.addNext(signalTran);
                elementList.add(signalTran);
                signalTran.addNext(as);
                as.addNext(tran);
                elementList.add(as);
                elementList.add(tran);
                if (ch.checkAuth) {
                    //Add aftersignal state
                    AvatarState afterSignalState = new AvatarState("aftersignalstate_" + reworkStringName(ae.getName()) +
                            "_" + ch.getName(), ae.getReferenceObject(), block);
                    tran.addNext(afterSignalState);
                    tran = new AvatarTransition(block, "__aftersignalstate_" + ae.getName(), ae.getReferenceObject());
                    afterSignalState.addNext(tran);
                    elementList.add(afterSignalState);
                    elementList.add(tran);
                    if (autoAuthChans || ae.securityPattern == null) {
                        if (block.getAvatarAttributeWithName(getName(ch.getName()) + "_chData") == null) {
                            AvatarAttribute channelData = new AvatarAttribute(getName(ch.getName()) + "_chData", AvatarType.INTEGER, block, null);
                            block.addAttribute(channelData);
                        }
                        AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + reworkStringName(afterSignalState.getName()) + "." +
                                getName(ch.getName()) + "_chData", ae.getReferenceObject(), block.getAvatarAttributeWithName(getName(ch.getName())
                                + "_chData"), afterSignalState);
                        if (signalAuthDestMap.containsKey(ch.getName())) {
                            signalAuthDestMap.get(ch.getName()).add(authDest);
                        } else {
                            LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                            tmp.add(authDest);
                            signalAuthDestMap.put(ch.getName(), tmp);
                        }
                    }
                }

            } else {

                // Write Channel
                TraceManager.addDev("OutMap  Looking for signal: " + ch.getName());
                if (!signalOutMap.containsKey(ch.getName())) {
                    TraceManager.addDev("Not in OutMap. Creating " + getName(ch.getName()));
                    //Add signal if it does not exist
                    sig = new AvatarSignal(getName(ch.getName()), AvatarSignal.OUT, ch.getReferenceObject());
                    signals.add(sig);
                    block.addSignal(sig);
                    signalOutMap.put(ch.getName(), sig);
                    AvatarAttribute channelData = new AvatarAttribute(getName(ch.getName()) + "_chData", AvatarType.INTEGER, block, null);
                    if (block.getAvatarAttributeWithName(getName(ch.getName()) + "_chData") == null) {
                        block.addAttribute(channelData);
                    }
                    sig.addParameter(channelData);
                } else {
                    sig = signalOutMap.get(ch.getName());
                }
                TraceManager.addDev("OutMap sig= " + sig.getSignalName());


                //Add the confidentiality pragma for this channel data
                if (ch.checkConf) {
                    if (ch.originalOriginTasks.size() != 0 && ch.getOriginPort().getName().contains("PORTORIGIN")) {
                        //	System.out.println("Channel " + ch.getOriginPort().getName() + " block " + block.getName());
                        if (!attrsToCheck.contains(ch.getOriginPort().getName() + "_chData")) {
                            for (TMLTask origTask : ch.originalOriginTasks) {
                                AvatarBlock bl = avspec.getBlockWithName(origTask.getName().split("__")[origTask.getName().split("__").length - 1]);
                                if (bl != null) {
                                    AvatarAttribute attr = bl.getAvatarAttributeWithName(block.getName() + "_chData");
                                    if (attr != null) {
                                        attrsToCheck.add(ch.getOriginPort().getName() + "_chData");
                                        avspec.addPragma(new AvatarPragmaSecret("#Confidentiality " + bl.getName() + "." + block.getName() +
                                                "_chData", ch.getReferenceObject(), attr));
                                    }
                                }
                            }
                        }
                    } else {
                        if (!attrsToCheck.contains(ch.getOriginPort().getName() + "_chData")) {
                            AvatarAttribute attr = block.getAvatarAttributeWithName(ch.getOriginPort().getName() + "_chData");
                            if (attr != null) {
                                attrsToCheck.add(ch.getOriginPort().getName() + "_chData");
                                avspec.addPragma(new AvatarPragmaSecret("#Confidentiality " + block.getName() + "." + ch.getName() +
                                        "_chData", ch.getReferenceObject(), attr));
                            }
                        }
                    }
                }

                // Add the authenticity pragma for this channel data
                // To be removed in case another authenticity pragma is used on the channel
                // Also, to be duplicated for each send / receive
                if (ch.checkAuth) {
                    if (autoAuthChans || ae.securityPattern == null) {
                        if (block.getAvatarAttributeWithName(getName(ch.getName()) + "_chData") == null) {
                            AvatarAttribute channelData = new AvatarAttribute(getName(ch.getName()) + "_chData", AvatarType.INTEGER, block, null);
                            block.addAttribute(channelData);
                        }
                        AvatarAttributeState authOrigin = new AvatarAttributeState(block.getName() + "." + reworkStringName(signalState.getName()) + "." +
                                getName(ch.getName()) + "_chData", ae.getReferenceObject(), block.getAvatarAttributeWithName(getName(ch.getName()) + "_chData"), signalState);
                        if (signalAuthOriginMap.containsKey(ch.getName())) {
                            signalAuthOriginMap.get(ch.getName()).add(authOrigin);
                        } else {
                            LinkedList<AvatarAttributeState> tmp = new LinkedList<AvatarAttributeState>();
                            tmp.add(authOrigin);
                            signalAuthOriginMap.put(ch.getName(), tmp);
                        }
                    }

                }

                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);

                if (ae.securityPattern != null) {
                    //send nonce
                    if (ae.securityPattern.type.equals(SecurityPattern.NONCE_PATTERN)) {
                        block.addAttribute(new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null));
                        as.addValue(ae.securityPattern.name);
                    }
                    //send encrypted key
                    else if (!ae.securityPattern.key.isEmpty()) {
                        as.addValue("encryptedKey_" + ae.securityPattern.key);
                        AvatarAttribute data = new AvatarAttribute("encryptedKey_" + ae.securityPattern.key, AvatarType.INTEGER, block, null);
                        block.addAttribute(data);
                    } else {
                        //send encrypted data 
                        //
                        if (aec.getEncForm()) {
                            as.addValue(ae.securityPattern.name + "_encrypted");
                            AvatarAttribute data = new AvatarAttribute(ae.securityPattern.name + "_encrypted", AvatarType.INTEGER, block, null);
                            block.addAttribute(data);
                        } else {
                            //Send unecrypted form
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) == null) {
                                AvatarAttribute data = new AvatarAttribute(ae.securityPattern.name, AvatarType.INTEGER, block, null);
                                block.addAttribute(data);
                            }
                            as.addValue(ae.securityPattern.name);
                        }

                        if (!secChannelMap.containsKey(ae.securityPattern.name)) {
                            List<String> tmp = new ArrayList<String>();
                            secChannelMap.put(ae.securityPattern.name, tmp);
                        }
                        secChannelMap.get(ae.securityPattern.name).add(ch.getName());
                    }
                } else {
                    //No security pattern
                    //	TraceManager.addDev("no security pattern for " + ch.getName());
                    as.addValue(ch.getOriginPort().getName() + "_chData");
                }

                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                elementList.add(signalState);
                signalState.addNext(signalTran);
                elementList.add(signalTran);
                signalTran.addNext(as);
                as.addNext(tran);
                elementList.add(as);
                elementList.add(tran);
            }
        } else if (ae instanceof TMLForLoop) {
            TMLForLoop loop = (TMLForLoop) ae;
            if (loop.isInfinite()) {
                //Make initializaton, then choice state with transitions
                List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block, autoAuthChans);
                /*List<AvatarStateMachineElement> afterloop =*/
                translateState(ae.getNextElement(1), block, autoAuthChans);
                AvatarState initState = new AvatarState(reworkStringName(ae.getName()) + "__init", ae.getReferenceObject(), block);
                elementList.add(initState);
                //Build transition to choice
                tran = new AvatarTransition(block, "loop_init__" + ae.getName(), ae.getReferenceObject());
                tran.addAction(AvatarTerm.createActionFromString(block, "loop_index=0"));
                elementList.add(tran);
                initState.addNext(tran);
                //Choice state
                AvatarState as = new AvatarState(reworkStringName(ae.getName()) + "__choice", ae.getReferenceObject(), block);
                elementList.add(as);
                tran.addNext(as);
                //transition to first element of loop
                tran = new AvatarTransition(block, "loop_increment__" + ae.getName(), ae.getReferenceObject());
                //Set default loop limit guard
                tran.setGuard(AvatarGuard.createFromString(block, "loop_index != " + loopLimit));
                tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
                tran.addNext(elements.get(0));
                as.addNext(tran);
                elementList.add(tran);
                //Process elements in loop to remove stop states and empty transitions, and loop back to choice
                for (AvatarStateMachineElement e : elements) {
                    if (e instanceof AvatarStopState) {
                    } else if (e.getNexts().size() == 0) {
                        if (e instanceof AvatarTransition) {
                            e.addNext(as);
                            elementList.add(e);
                        }
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(as);
                        elementList.add(e);
                    } else {
                        elementList.add(e);
                    }
                }

                //Transition if exiting loop
                tran = new AvatarTransition(block, "end_loop__" + ae.getName(), ae.getReferenceObject());
                tran.setGuard(new AvatarGuardElse());
                as.addNext(tran);
                AvatarStopState stop = new AvatarStopState("stop", null, block);
                tran.addNext(stop);
                elementList.add(tran);
                elementList.add(stop);
                return elementList;
            } else {
                //Make initializaton, then choice state with transitions
                List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block, autoAuthChans);
                List<AvatarStateMachineElement> afterloop = translateState(ae.getNextElement(1), block, autoAuthChans);
                AvatarState initState = new AvatarState(reworkStringName(ae.getName()) + "__init", ae.getReferenceObject(), block);
                elementList.add(initState);
                //Build transition to choice
                tran = new AvatarTransition(block, "loop_init__" + ae.getName(), ae.getReferenceObject());
                tran.addAction(AvatarTerm.createActionFromString(block, loop.getInit()));
                tran.addAction(AvatarTerm.createActionFromString(block, "loop_index=0"));
                elementList.add(tran);
                initState.addNext(tran);
                //Choice state
                AvatarState as = new AvatarState(reworkStringName(ae.getName()) + "__choice", ae.getReferenceObject(), block);
                elementList.add(as);
                tran.addNext(as);
                //transition to first element of loop
                tran = new AvatarTransition(block, "loop_increment__" + ae.getName(), ae.getReferenceObject());
                //Set default loop limit guard
                tran.setGuard(AvatarGuard.createFromString(block, "loop_index != " + loopLimit));
                /*AvatarGuard guard = */
                AvatarGuard.createFromString(block, loop.getCondition().replaceAll("<", "!="));
                int error = AvatarSyntaxChecker.isAValidGuard(avspec, block, loop.getCondition().replaceAll("<", "!="));
                if (error != 0) {
                    tran.addGuard(loop.getCondition().replaceAll("<", "!="));
                }
                tran.addAction(AvatarTerm.createActionFromString(block, loop.getIncrement()));
                tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
                if (elements.size() > 0) {
                    tran.addNext(elements.get(0));
                    as.addNext(tran);
                    elementList.add(tran);
                }
                //Process elements in loop to remove stop states and empty transitions, and loop back to choice
                for (AvatarStateMachineElement e : elements) {
                    if (e instanceof AvatarStopState) {
                    } else if (e.getNexts().size() == 0) {
                        e.addNext(as);
                        elementList.add(e);
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(as);
                        elementList.add(e);
                    } else {
                        elementList.add(e);
                    }
                }

                //Transition if exiting loop
                tran = new AvatarTransition(block, "end_loop__" + ae.getName(), ae.getReferenceObject());
                tran.setGuard(new AvatarGuardElse());
                as.addNext(tran);
                if (afterloop.size() == 0) {
                    afterloop.add(new AvatarStopState("stop", null, block));
                }
                tran.addNext(afterloop.get(0));
                elementList.add(tran);
                elementList.addAll(afterloop);
                return elementList;
            }
        } else if (ae instanceof TMLChoice) {
            AvatarState as = new AvatarState(reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            //Make many choices
            elementList.add(as);
            TMLChoice c = (TMLChoice) ae;
            for (int i = 0; i < c.getNbGuard(); i++) {
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + i, ae.getReferenceObject());
                //tran.setGuard(c.getGuard(i));
                as.addNext(tran);
                List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block, autoAuthChans);
                if (nexts.size() > 0) {
                    tran.addNext(nexts.get(0));
                    elementList.add(tran);
                    elementList.addAll(nexts);
                }
            }
            return elementList;

        } else if (ae instanceof TMLSelectEvt) {
            AvatarState as = new AvatarState(reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            elementList.add(as);
            //Make many choices
            //TMLSelectEvt c = (TMLSelectEvt) ae;
            for (int i = 0; i < ae.getNbNext(); i++) {
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + i, ae.getReferenceObject());
                as.addNext(tran);
                List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block, autoAuthChans);
                tran.addNext(nexts.get(0));
                elementList.add(tran);
                elementList.addAll(nexts);
            }
            return elementList;
        } else {
            TraceManager.addDev("undefined tml element " + ae);
        }
        List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(0), block, autoAuthChans);
        if (nexts.size() == 0) {
            //in an infinite loop i hope
            return elementList;
        }
        tran.addNext(nexts.get(0));
        elementList.addAll(nexts);
        return elementList;
    }

    public String processName(String name, int id) {
        name = reworkStringName(name).replaceAll("-", "_");
        if (allStates.contains(name)) {
            return name + id;

        } else {
            allStates.add(name);
            return name;
        }
    }
		/*	public AvatarPragma generatePragma(String[] s){

			}*/

    public String getName(String s) {
        //	System.out.println("String " + s);
        if (nameMap.containsKey(s)) {
            return nameMap.get(s);
        } else {
            if (!s.contains("__")) {
                nameMap.put(s, s);
                return s;
            } else if (s.split("__").length == 1 || s.split("__").length == 2 || s.split("__").length == 3 || s.split("__").length == 4) {
                nameMap.put(s, s.split("__")[s.split("__").length - 1]);
                return s.split("__")[s.split("__").length - 1];
            } else if (s.contains("JOIN") || s.contains("FORK")) {
                String t = "";
                t += s.split("__")[0];
                for (int i = 2; i < s.split("__").length; i++) {
                    t += "JOIN" + s.split("__")[i];
                }
                nameMap.put(s, t);
                return t;
            } else {
	       /*     String t = "";
                for (int i = 0; i < s.split("__").length; i++) {
                    t += s.split("__")[i];
                }*/
                nameMap.put(s, s);
                return s;
                // nameMap.put(s, s.split("__")[s.split("__").length - 1]);
                // return s.split("__")[s.split("__").length - 1];
            }
        }
    }

    public AvatarSpecification generateAvatarSpec(String _loopLimit, boolean autoAuthChans) {

        TraceManager.addDev("security patterns " + tmlmodel.secPatterns);
        TraceManager.addDev("keys " + tmlmap.mappedSecurity);


        //TODO: Make state names readable
        //TODO: Put back numeric guards
        //TODO: Calculate for temp variable
        if (tmlmap.getTMLModeling().getReference() != null) {
            this.avspec = new AvatarSpecification("spec", referenceObject);
        } else {
            this.avspec = new AvatarSpecification("spec", null);
        }
        attrsToCheck.clear();
        tmlmodel.removeForksAndJoins();

//        System.out.println("MODIFIED model " + tmlmodel);

        for (TMLChannel chan : tmlmodel.getChannels()) {
            //System.out.println("chan " + chan);
            TMLTask task = chan.getOriginTask();

            TMLTask task2 = chan.getDestinationTask();
            HwExecutionNode node = tmlmap.getHwNodeOf(task);
            HwExecutionNode node2 = tmlmap.getHwNodeOf(task2);
            if (node == null) {
                tmlmap.addTaskToHwExecutionNode(task, node2);
            }

            if (node2 == null) {
                tmlmap.addTaskToHwExecutionNode(task2, node);
            }

            if (chan.getName().contains("fork__") || chan.getName().contains("FORKCHANNEL")) {
                chan.setName(chan.getName().replaceAll("__", ""));
            }

        }

        //Only set the loop limit if it's a number
        String pattern = "^[0-9]{1,2}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(_loopLimit);
        if (m.find()) {
            loopLimit = Integer.valueOf(_loopLimit);
        }
        for (TMLChannel channel : tmlmodel.getChannels()) {
            TraceManager.addDev("Checking auth of channel " + channel.getName() + ": " + channel.isCheckAuthChannel());
            channel.checkAuth = channel.isCheckAuthChannel();
            for (TMLPortWithSecurityInformation p : channel.ports) {
                channel.checkConf = channel.checkConf || p.getCheckConf();
            }
        }

        AvatarBlock top = new AvatarBlock("TOP__TOP", avspec, null);
        if (mc) {
            avspec.addBlock(top);
            AvatarStateMachine topasm = top.getStateMachine();
            AvatarStartState topss = new AvatarStartState("start", null, topasm.getOwner());
            topasm.setStartState(topss);
            topasm.addElement(topss);
        }

        List<TMLTask> tasks = tmlmap.getTMLModeling().getTasks();

        for (TMLTask task : tasks) {
            AvatarBlock block = new AvatarBlock(task.getName().split("__")[task.getName().split("__").length - 1], avspec, task.getReferenceObject());
            if (mc) {
                block.setFather(top);
            }
            taskBlockMap.put(task, block);
            avspec.addBlock(block);
        }

        checkConnections();
        //	checkChannels();

        distributeKeys();

        TraceManager.addDev("ALL KEYS " + accessKeys);
			/*for (TMLTask t: accessKeys.keySet()){
				TraceManager.addDev("TASK " +t.getName());
				for (SecurityPattern sp: accessKeys.get(t)){
					TraceManager.addDev(sp.name);
				}
			}*/

        for (TMLTask task : tasks) {
            AvatarBlock block = taskBlockMap.get(task);
            // Add temp variable for unsendable signals

            // Add all channel signals
            for (TMLChannel chan : tmlmodel.getChannels(task)) {

                if (chan.hasOriginTask(task)) {
                    if (chan.getOriginPort() == null) {
                        TraceManager.addDev("NULL PORT in chan " + chan.getName());
                    } else if (chan.getOriginPort().getName() == null) {
                        TraceManager.addDev("NULL PORT NAME" + chan.getName());
                    }
                    AvatarSignal sig = new AvatarSignal(chan.getOriginPort().getName(), AvatarSignal.OUT, chan.getReferenceObject());

                    block.addSignal(sig);
                    signals.add(sig);
                    AvatarAttribute channelData = new AvatarAttribute(chan.getOriginPort().getName() + "_chData", AvatarType.INTEGER, block,
                            null);
                    if (block.getAvatarAttributeWithName(chan.getOriginPort().getName() + "_chData") == null) {
                        block.addAttribute(channelData);
                    }
                    sig.addParameter(channelData);
                    signalOutMap.put(chan.getName(), sig);

                } else if (chan.hasDestinationTask(task)) {
                    //AvatarSignal sig = new AvatarSignal(getName(chan.getName()), AvatarSignal.IN, chan.getReferenceObject());
                    AvatarSignal sig = new AvatarSignal(chan.getDestinationPort().getName(), AvatarSignal.IN, chan.getReferenceObject());
                    block.addSignal(sig);
                    signals.add(sig);
                    signalInMap.put(chan.getName(), sig);
                    AvatarAttribute channelData = new AvatarAttribute(chan.getDestinationPort().getName() + "_chData", AvatarType.INTEGER, block,
                            null);
                    if (block.getAvatarAttributeWithName(chan.getDestinationPort().getName() + "_chData") == null) {
                        block.addAttribute(channelData);
                    }
                    sig.addParameter(channelData);
                }
            }

            // Add all event signals
            for (TMLEvent evt : tmlmodel.getEvents(task)) {
                //TraceManager.addDev("Handling evt: " + evt.getName() + " in task " + task.getName());
                if (evt.hasOriginTask(task)) {
                    String name = getNameReworked(evt.getName(), 1) + "_out";
                    TraceManager.addDev("Adding OUT evt:" + name);
                    AvatarSignal sig = new AvatarSignal(name, AvatarSignal.OUT, evt.getReferenceObject());
                    block.addSignal(sig);
                    signals.add(sig);
                    //Adding parameter
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }
                    signalOutMap.put(evt.getName(), sig);

                }

                if (evt.hasDestinationTask(task)) {
                    String name = getNameReworked(evt.getName(), 3) + "_in";
                    TraceManager.addDev("Adding IN evt:" + name);
                    AvatarSignal sig = new AvatarSignal(name, AvatarSignal.IN, evt.getReferenceObject());
                    block.addSignal(sig);
                    signals.add(sig);
                    signalInMap.put(evt.getName(), sig);

                    //Adding parameter
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }/*

                    name = getNameReworked(evt.getName(), 3) + NOTIFIED;
                    sig = block.addSignalIfApplicable(name, AvatarSignal.IN, evt.getReferenceObject());
                    signalInMap.put(evt.getName() + NOTIFIED, sig);

                    //Adding parameter
                    AvatarAttribute aa = new AvatarAttribute("p" + cpt, AvatarType.INTEGER, null, null);
                    sig.addParameter(aa); */

                }
            }

            AvatarAttribute tmp = new AvatarAttribute("tmp", AvatarType.INTEGER, block, null);
            block.addAttribute(tmp);

				/*   tmp = new AvatarAttribute("aliceandbob", AvatarType.INTEGER, block, null);
				     block.addAttribute(tmp);
				     tmp = new AvatarAttribute("aliceandbob_encrypted", AvatarType.INTEGER, block, null);
				     block.addAttribute(tmp);*/
            AvatarAttribute loop_index = new AvatarAttribute("loop_index", AvatarType.INTEGER, block, null);
            block.addAttribute(loop_index);
            for (TMLAttribute attr : task.getAttributes()) {
                AvatarType type;
                if (attr.getType().getType() == TMLType.NATURAL) {
                    type = AvatarType.INTEGER;
                } else if (attr.getType().getType() == TMLType.BOOLEAN) {
                    type = AvatarType.BOOLEAN;
                } else {
                    type = AvatarType.UNDEFINED;
                }
                AvatarAttribute avattr = new AvatarAttribute(attr.getName(), type, block, null);
                avattr.setInitialValue(attr.getInitialValue());
                block.addAttribute(avattr);
            }
            //AvatarTransition last;
            AvatarStateMachine asm = block.getStateMachine();

            //TODO: Create a fork with many requests. This looks terrible
            if (tmlmodel.getRequestToMe(task) != null) {
                //Create iteration attribute
                AvatarAttribute req_loop_index = new AvatarAttribute("req_loop_index", AvatarType.INTEGER, block, null);
                block.addAttribute(req_loop_index);

                //TMLRequest request= tmlmodel.getRequestToMe(task);
                //Oh this is fun...let's restructure the state machine
                //Create own start state, and ignore the returned one
                List<AvatarStateMachineElement> elementList = translateState(task.getActivityDiagram().get(0), block, autoAuthChans);
                AvatarStartState ss = new AvatarStartState("start", task.getActivityDiagram().get(0).getReferenceObject(), block);
                asm.addElement(ss);
                AvatarTransition at = new AvatarTransition(block, "__after_start", task.getActivityDiagram().get(0).getReferenceObject());
                at.addAction(AvatarTerm.createActionFromString(block, "req_loop_index = 0"));
                ss.addNext(at);
                asm.addElement(at);

                AvatarState loopstart = new AvatarState("loopstart", task.getActivityDiagram().get(0).getReferenceObject(), block);
                at.addNext(loopstart);
                asm.addElement(loopstart);

                //Find the original start state, transition, and next element
                AvatarStateMachineElement start = elementList.get(0);
                AvatarStateMachineElement startTran = start.getNext(0);
                AvatarStateMachineElement newStart = startTran.getNext(0);
                elementList.remove(start);
                elementList.remove(startTran);
                //Find every stop state, remove them, reroute transitions to them
                //For now, route every transition to stop state to remove the loop on requests 

                for (AvatarStateMachineElement e : elementList) {
                    e.setName(processName(e.getName(), e.getID()));
                    stateObjectMap.put(task.getName().split("__")[1] + "__" + e.getName(), e.getReferenceObject());

                    if (e instanceof AvatarStopState) {
                        //ignore it
                    } else {
                        for (int i = 0; i < e.getNexts().size(); i++) {
                            if (e.getNext(i) instanceof AvatarStopState) {
                                e.removeNext(i);
                                //Route it back to the loop start
                                e.addNext(loopstart);
                            }
                        }
                        asm.addElement(e);
                    }
                }

                //Create exit after # of loop iterations is maxed out
                /*AvatarStopState stop =*/
                new AvatarStopState("stop", task.getActivityDiagram().get(0).getReferenceObject(), block);
                /*AvatarTransition exitTran = */
                new AvatarTransition(block, "to_stop", task.getActivityDiagram().get(0).getReferenceObject());


                //Add Requests, direct transition to start of state machine
                for (Object obj : tmlmodel.getRequestsToMe(task)) {
                    TMLRequest req = (TMLRequest) obj;
                    AvatarTransition incrTran = new AvatarTransition(block, "__after_loopstart__" + req.getName(), task.getActivityDiagram().get(0).getReferenceObject());
                    incrTran.addAction(AvatarTerm.createActionFromString(block, "req_loop_index = req_loop_index + 1"));
                    incrTran.setGuard(AvatarGuard.createFromString(block, "req_loop_index != " + loopLimit));
                    asm.addElement(incrTran);
                    loopstart.addNext(incrTran);
                    AvatarSignal sig;
                    if (!signalInMap.containsKey(req.getName())) {
                        sig = new AvatarSignal(getName(req.getName()), AvatarSignal.IN, req.getReferenceObject());
                        block.addSignal(sig);
                        signals.add(sig);
                        signalInMap.put(req.getName(), sig);
                        int cpt = 0;
                        for (TMLType tmlt : req.getParams()) {
                            AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                            sig.addParameter(aa);
                            cpt++;
                        }
                    } else {
                        sig = signalInMap.get(req.getName());
                    }
                    AvatarActionOnSignal as = new AvatarActionOnSignal("getRequest__" + req.getName(), sig, req.getReferenceObject(), block);
                    incrTran.addNext(as);
                    asm.addElement(as);
						/*as.addValue(req.getName()+"__reqData");
						AvatarAttribute requestData= new AvatarAttribute(req.getName()+"__reqData", AvatarType.INTEGER, block, null);
						block.addAttribute(requestData);*/
                    for (int i = 0; i < req.getNbOfParams(); i++) {
                        if (block.getAvatarAttributeWithName(req.getParam(i)) == null) {
                            //Throw Error
                            AvatarType type;
                            if (req.getParam(i).matches("-?\\d+")) {
                                type = AvatarType.INTEGER;
                            } else if (req.getParam(i).matches("(?i)^(true|false)")) {
                                type = AvatarType.BOOLEAN;
                            } else {
                                type = AvatarType.UNDEFINED;
                            }
                            String nameNewAtt = req.getName() + "_" + req.getID() + "_" + i + "_" + req.getParam(i);
                            if (block.getAvatarAttributeWithName(nameNewAtt) == null) {
                                AvatarAttribute avattr = new AvatarAttribute(nameNewAtt, type, block, null);
                                avattr.setInitialValue(req.getParam(i));
                                block.addAttribute(avattr);
                                as.addValue(avattr.getName());
                                TraceManager.addDev("Missing Attribute " + req.getParam(i));
                            } else {
                                as.addValue(block.getAvatarAttributeWithName(nameNewAtt).getName());
                            }
                        } else {
                            //	Add parameter to signal and actiononsignal
                            as.addValue(req.getParam(i));
                        }
                    }
                    AvatarTransition tran = new AvatarTransition(block, "__after_" + req.getName(), task.getActivityDiagram().get(0).getReferenceObject());
                    as.addNext(tran);
                    asm.addElement(tran);
                    tran.addNext(newStart);
						/*if (req.checkAuth){
							AvatarState afterSignalState = new AvatarState("aftersignalstate_"+req.getName().replaceAll(" ","")+"_"+req.getName().replaceAll(" ",""),req.getReferenceObject());
							AvatarTransition afterSignalTran = new AvatarTransition(block, "__aftersignalstate_"+req.getName(), req.getReferenceObject());
							tran.addNext(afterSignalState);
							afterSignalState.addNext(afterSignalTran);
							asm.addElement(afterSignalState);
							asm.addElement(afterSignalTran);
							afterSignalTran.addNext(newStart);
							AvatarAttributeState authDest = new AvatarAttributeState(block.getName()+"."+afterSignalState.getName()+"."+requestData.getName(),obj,requestData, afterSignalState);
							signalAuthDestMap.put(req.getName(), authDest);
						}  
						else {
							tran.addNext(newStart);
						}*/

                }


                asm.setStartState(ss);

            } else {
                //Not requested
                List<AvatarStateMachineElement> elementList = translateState(task.getActivityDiagram().get(0), block, autoAuthChans);
                for (AvatarStateMachineElement e : elementList) {
                    e.setName(processName(e.getName(), e.getID()));
                    asm.addElement(e);
                    stateObjectMap.put(task.getName().split("__")[1] + "__" + e.getName(), e.getReferenceObject());
                }
                asm.setStartState((AvatarStartState) elementList.get(0));
            }
            for (SecurityPattern secPattern : secPatterns) {
                AvatarAttribute sec = block.getAvatarAttributeWithName(secPattern.name);
                if (sec != null) {
                    boolean checkAuthSecPattern = false;
                    for (TMLChannel ch : tmlmodel.getChannels(task)) {
                        if (ch.hasOriginTask(task) && ch.isCheckConfChannel()) {
                            for (TMLActivityElement actElem : task.getActivityDiagram().getElements()) {
                                if (actElem instanceof TMLWriteChannel) {
                                    TMLWriteChannel wc = (TMLWriteChannel) actElem;
                                    if (wc != null && wc.hasChannel(ch) && actElem.securityPattern != null
                                            && actElem.securityPattern.getName().equals(secPattern.getName())) {
                                        checkAuthSecPattern = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //sec = new AvatarAttribute(secPattern.name, AvatarType.INTEGER, block, null);
                    //AvatarAttribute enc = new AvatarAttribute(secPattern.name+"_encrypted", AvatarType.INTEGER, block, null);
                    //	block.addAttribute(sec);
                    //	block.addAttribute(enc);
                    //}
                    if (checkAuthSecPattern) {
                        avspec.addPragma(new AvatarPragmaSecret("#Confidentiality " + block.getName() + "." + secPattern.name, null, sec));
                    }
                }
            }

        }


        // Add authenticity pragmas
        for (String s : signalAuthOriginMap.keySet()) {
            for (AvatarAttributeState attributeStateOrigin : signalAuthOriginMap.get(s)) {
                if (signalAuthDestMap.containsKey(s)) {
                    for (AvatarAttributeState attributeStateDest : signalAuthDestMap.get(s)) { 
                        AvatarPragmaAuthenticity pragma = new AvatarPragmaAuthenticity(
                                "#Authenticity " + attributeStateOrigin.getName() + " " + attributeStateDest.getName(),
                                attributeStateOrigin.getReferenceObject(), attributeStateOrigin, attributeStateDest);
                        if (secChannelMap.containsKey(s)) {
                            for (String channel : secChannelMap.get(s)) {
                                TMLChannel ch = tmlmodel.getChannelByShortName(channel);
                                if (ch != null) {
                                    if (ch.checkAuth) {
                                        avspec.addPragma(pragma);
                                        break;
                                    }
                                }
                            }
                        } else {
                            avspec.addPragma(pragma);
                        }
                    }
                }
            }
        }

        //Create relations
        //Channels are ?? to ??
        //Requests are n to 1
        //Events are ?? to ??
        AvatarBlock fifo = new AvatarBlock("FIFO", avspec, null);
        for (TMLChannel channel : tmlmodel.getChannels()) {
				/*if (channel.getName().contains("JOINCHANNEL")){
					//System.out.println("JOINCHANNEL");
					AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
					ar.setPrivate(false);
					if (channel.getType()==TMLChannel.BRBW){
						ar.setAsynchronous(true);		
						ar.setSizeOfFIFO(channel.getSize());
						ar.setBlocking(true);
					}
					else if (channel.getType()==TMLChannel.BRNBW){
						ar.setAsynchronous(true);
						ar.setSizeOfFIFO(channel.getSize());
						ar.setBlocking(false);
					}
					else {
						//Create new block, hope for best
						if (mc){
							fifo = createFifo(channel.getName());
							ar.setAsynchronous(false);
						}
					}
					//System.out.println(channel.getName() + " " +channel.getOriginTask().getName() + " " + channel.getDestinationTask().getName());
					//Find in signal
					//Sig1 contains IN Signals, Sig2 contains OUT signals
					List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
					List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
					for (AvatarSignal sig: taskBlockMap.get(channel.getDestinationTask()).getSignals()){
						if (sig.getInOut()==AvatarSignal.IN){
							String name = sig.getName();
							String tmp = getName(channel.getName());
							if (name.equals(tmp.split("JOIN")[tmp.split("JOIN").length-1]) || name.equals(tmp)){
								sig1.add(sig);
							}
						}
					}
					for (AvatarSignal sig: taskBlockMap.get(channel.getOriginTask()).getSignals()){
						if (sig.getInOut()==AvatarSignal.OUT){
							String name = sig.getName();
							String tmp = getName(channel.getName());
							if (name.equals(tmp.split("JOIN")[tmp.split("JOIN").length-1]) || name.equals(tmp)){
								sig2.add(sig);
							}
						}
					}

					if (sig1.size()==1 && sig2.size()==1){
						if (channel.getType()==TMLChannel.NBRNBW && mc){
							AvatarSignal read = fifo.getSignalByName("readSignal");

							ar.block2= fifo;
							//Set IN signal with read
							ar.addSignals(sig1.get(0), read);
							AvatarRelation ar2= new AvatarRelation(channel.getName()+"2", fifo, taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
							AvatarSignal write = fifo.getSignalByName("writeSignal");
							//set OUT signal with write
							ar2.addSignals(write, sig2.get(0));
							ar2.setAsynchronous(false);
							avspec.addRelation(ar2);
						}
						else {
							ar.addSignals(sig2.get(0), sig1.get(0));
						}
					}		
					avspec.addRelation(ar);
				}
		
				else if (channel.getName().contains("FORKCHANNEL") || channel.getName().contains("fork__")){
					System.out.println("FORKCHANNEL " + channel.getName());
					AvatarRelation ar= new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
					ar.setPrivate(false);

					//System.out.println(channel.getName() + " " +channel.getOriginTask().getName() + " " + channel.getDestinationTask().getName());
					//Find in signal
					//Sig1 contains IN Signals, Sig2 contains OUT signals
					List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
					List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
					for (AvatarSignal sig: taskBlockMap.get(channel.getDestinationTask()).getSignals()){
						if (sig.getInOut()==AvatarSignal.IN){
							String name = sig.getName();
							String tmp = getName(channel.getName());
							if (name.equals(tmp.split("FORK")[tmp.split("FORK").length-1]) || name.equals(tmp)){
								sig1.add(sig);
							}
						}
					}
					for (AvatarSignal sig: taskBlockMap.get(channel.getOriginTask()).getSignals()){
						if (sig.getInOut()==AvatarSignal.OUT){
							String name = sig.getName();
							String tmp = getName(channel.getName());
							if (name.equals(tmp.split("FORK")[tmp.split("FORK").length-1]) || name.equals(tmp)){
								sig2.add(sig);
							}
						}
					}

					if (sig1.size()==1 && sig2.size()==1){
						if (channel.getType()==TMLChannel.NBRNBW && mc){
							AvatarSignal read = fifo.getSignalByName("readSignal");

							ar.block2= fifo;
							//Set IN signal with read
							ar.addSignals(sig1.get(0), read);
							AvatarRelation ar2= new AvatarRelation(channel.getName()+"2", fifo, taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
							AvatarSignal write = fifo.getSignalByName("writeSignal");
							//set OUT signal with write
							ar2.addSignals(write, sig2.get(0));
							ar2.setAsynchronous(false);
							avspec.addRelation(ar2);
						}
						else {
							ar.addSignals(sig2.get(0), sig1.get(0));
						}
					}		
					avspec.addRelation(ar);
				}*/

            if (channel.isBasicChannel()) {
                //TraceManager.addDev("CHAN checking basic channel " + channel.getName());
                AvatarRelation ar = new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()),
                        taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
                LinkedList<HwCommunicationNode> path = tmlmap.findNodesForElement(channel);
                //TraceManager.addDev("CHAN checking basic channel " + channel.getName() + " path size: " + path.size());
                if (path.size() != 0) {
                    ar.setPrivate(true);
                    for (HwCommunicationNode node : path) {
                        //TraceManager.addDev("CHAN\t Element of path: " + node.getName());
                        if (node instanceof HwBus) {
                            if (node.privacy == HwCommunicationNode.BUS_PUBLIC) {
                                ar.setPrivate(false);
                                //TraceManager.addDev("CHAN\t Set as public: " + channel.getName() + "because of " + node.getName());
                                break;
                            }
                        }
                    }
                } else {
                    if (channel.originalOriginTasks.size() == 0) {
                        ar.setPrivate(originDestMap.get(channel.getOriginTask().getName() + "__" + channel.getDestinationTask().getName()) == 1);
                    } else {
                        //System.out.println("complex channel " + channel.getName());
                        //Find privacy of original tasks
                        boolean priv = true;
                        for (TMLTask task1 : channel.originalOriginTasks) {
                            for (TMLTask task2 : channel.originalDestinationTasks) {
                                if (originDestMap.get(task1.getName() + "__" + task2.getName()) != 1) {
                                    priv = false;
                                    break;
                                }

                            }

                        }
                        ar.setPrivate(priv);
                    }
                }
                if (channel.getType() == TMLChannel.BRBW) {
                    ar.setAsynchronous(true);
                    ar.setSizeOfFIFO(channel.getSize());
                    ar.setBlocking(true);
                } else if (channel.getType() == TMLChannel.BRNBW) {
                    ar.setAsynchronous(true);
                    ar.setSizeOfFIFO(channel.getSize());
                    ar.setBlocking(false);
                } else {
                    //Create new block, hope for best
                    if (mc) {
                        fifo = createFifo(channel.getName());
                        ar.setAsynchronous(false);
                    }
                }
                //Find in signal

                List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
                //Sig1 contains IN Signals, Sig2 contains OUT signals
                sig1.add(signalInMap.get(channel.getName()));
                List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
                sig2.add(signalOutMap.get(channel.getName()));
                for (AvatarSignal sig : signals) {
                    if (sig.getInOut() == AvatarSignal.IN) {
                        String name = sig.getName();
                        if (name.equals(getName(channel.getName()))) {
                            //			sig1.add(sig);
                        }
                    }
                }
                //Find out signal
                for (AvatarSignal sig : signals) {
                    if (sig.getInOut() == AvatarSignal.OUT) {
                        String name = sig.getName();
                        if (name.equals(channel.getOriginPort().getName())) {
                            //				sig2.add(sig);
                        }
                    }
                }
                //System.out.println("size " + sig1.size() + " " + sig2.size());
                if (sig1.size() == 0) {
                    sig1.add(new AvatarSignal(getName(channel.getName()), AvatarSignal.IN, null));
                }
                if (sig2.size() == 0) {
                    sig2.add(new AvatarSignal(getName(channel.getName()), AvatarSignal.OUT, null));
                }
                if (sig1.size() == 1 && sig2.size() == 1) {
                    if (channel.getType() == TMLChannel.NBRNBW && mc) {
                        AvatarSignal read = fifo.getSignalByName("readSignal");

                        ar.block2 = fifo;
                        //Set IN signal with read
                        ar.addSignals(sig1.get(0), read);
                        AvatarRelation ar2 = new AvatarRelation(channel.getName() + "2", fifo, taskBlockMap.get(channel.getDestinationTask()), channel.getReferenceObject());
                        AvatarSignal write = fifo.getSignalByName("writeSignal");
                        //set OUT signal with write
                        ar2.addSignals(write, sig2.get(0));
                        //		System.out.println("Set " + sig2.get(0) + " and write");
                        ar2.setAsynchronous(false);
                        avspec.addRelation(ar2);
                    } else {
                        ar.addSignals(sig2.get(0), sig1.get(0));
                    }
                } else {
                    //Create relation if it does not exist
                    if (top.getSignalByName(getName(channel.getName()) + "in") == null) {
                        AvatarRelation relation = new AvatarRelation(channel.getName(), top, top, channel.getReferenceObject());
                        AvatarSignal s1 = new AvatarSignal(getName(channel.getName()) + "in", AvatarSignal.IN, null);
                        AvatarSignal s2 = new AvatarSignal(getName(channel.getName()) + "out", AvatarSignal.OUT, null);
                        top.addSignal(s1);
                        top.addSignal(s2);
                        relation.addSignals(s2, s1);
                        avspec.addRelation(relation);
                        //	System.out.println("Failure to match signals for TMLChannel "+ channel.getName());
                    }
                }
                avspec.addRelation(ar);
            } else {
                //System.out.println("WTF Found non-basic channel");
                //If not a basic channel, create a relation between TOP block and itself
                AvatarRelation relation = new AvatarRelation(channel.getName(), top, top, channel.getReferenceObject());
                AvatarSignal s1 = new AvatarSignal(getName(channel.getName()) + "in", AvatarSignal.IN, null);
                AvatarSignal s2 = new AvatarSignal(getName(channel.getName()) + "out", AvatarSignal.OUT, null);
                top.addSignal(s1);
                top.addSignal(s2);
                relation.addSignals(s2, s1);
                avspec.addRelation(relation);
                for (TMLTask t1 : channel.getOriginTasks()) {
                    for (TMLTask t2 : channel.getDestinationTasks()) {
                        AvatarRelation ar = new AvatarRelation(channel.getName(), taskBlockMap.get(t1), taskBlockMap.get(t2), channel.getReferenceObject());
                        ar.setPrivate(originDestMap.get(t1.getName() + "__" + t2.getName()) == 1);
                        //Find in signal
                        List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
                        List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
                        for (AvatarSignal sig : signals) {
                            if (sig.getInOut() == AvatarSignal.IN) {
                                String name = sig.getName();
                                if (name.equals(getName(channel.getName()))) {
                                    sig1.add(sig);
                                }
                            }
                        }
                        //Find out signal
                        for (AvatarSignal sig : signals) {
                            if (sig.getInOut() == AvatarSignal.OUT) {
                                String name = sig.getName();
                                if (name.equals(getName(channel.getName()))) {
                                    sig2.add(sig);
                                }
                            }
                        }
                        if (sig1.size() == 0) {
                            sig1.add(new AvatarSignal(getName(channel.getName()), AvatarSignal.IN, null));
                        }
                        if (sig2.size() == 0) {
                            sig2.add(new AvatarSignal(getName(channel.getName()), AvatarSignal.OUT, null));
                        }
                        if (sig1.size() == 1 && sig2.size() == 1) {
                            ar.addSignals(sig2.get(0), sig1.get(0));
                        } else {
                            System.out.println("Failure to match signals for TMLChannel " + channel.getName() + " between " + t1.getName() + " and " + t2.getName());
                        }
                        avspec.addRelation(ar);
                    }
                }
            }
        }
        for (TMLRequest request : tmlmodel.getRequests()) {
            for (TMLTask t1 : request.getOriginTasks()) {
                AvatarRelation ar = new AvatarRelation(request.getName(), taskBlockMap.get(t1), taskBlockMap.get(request.getDestinationTask()), request.getReferenceObject());
                ar.setPrivate(originDestMap.get(t1.getName() + "__" + request.getDestinationTask().getName()) == 1);
                List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
                List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
                for (AvatarSignal sig : signals) {
                    if (sig.getInOut() == AvatarSignal.IN) {
                        String name = sig.getName();

                        if (name.equals(getName(request.getName()))) {
                            sig1.add(sig);
                        }
                    }
                }
                //Find out signal
                for (AvatarSignal sig : signals) {
                    if (sig.getInOut() == AvatarSignal.OUT) {
                        String name = sig.getName();

                        if (name.equals(getName(request.getName()))) {
                            sig2.add(sig);
                        }
                    }
                }
                if (sig1.size() == 0) {
                    sig1.add(new AvatarSignal(getName(request.getName()), AvatarSignal.IN, null));
                }
                if (sig2.size() == 0) {
                    sig2.add(new AvatarSignal(getName(request.getName()), AvatarSignal.OUT, null));
                }
                if (sig1.size() == 1 && sig2.size() == 1) {
                    ar.addSignals(sig2.get(0), sig1.get(0));
                } else {
                    //Throw error
                    System.out.println("Could not match for " + request.getName());
                }

                ar.setAsynchronous(false);
                avspec.addRelation(ar);
            }
        }
        for (TMLEvent event : tmlmodel.getEvents()) {

            AvatarRelation ar = new AvatarRelation(event.getName(), taskBlockMap.get(event.getOriginTask()), taskBlockMap.get(event.getDestinationTask()),
                    event.getReferenceObject());
            ar.setAsynchronous(true);
            ar.setPrivate(true);


            AvatarSignal sigOut = signalOutMap.get(event.getName());
            AvatarSignal sigIn = signalInMap.get(event.getName());
            ar.addSignals(sigOut, sigIn);

            if (event.isBlocking()) {
                ar.setAsynchronous(true);
                ar.setBlocking(true);
                ar.setSizeOfFIFO(event.getMaxSize());
            } else {
                ar.setAsynchronous(true);
                ar.setBlocking(false);
                ar.setSizeOfFIFO(event.getMaxSize());

            }
            avspec.addRelation(ar);
        }

        //	System.out.println("Avatar relations " + avspec.getRelations());

        for (AvatarSignal sig : signals) {
            //	System.out.println("signal " + sig.getName());
            //check that all signals are put in relations
            AvatarRelation ar = avspec.getAvatarRelationWithSignal(sig);
            if (ar == null) {
                System.out.println("missing relation for " + sig.getName());
            }
        }
        //Check if we matched up all signals
        for (SecurityPattern sp : symKeys.keySet()) {
            if (symKeys.get(sp).size() > 1) {
                String keys = "";
                for (AvatarAttribute key : symKeys.get(sp)) {
                    keys = keys + " " + key.getBlock().getName() + "." + key.getName();
                }
                avspec.addPragma(new AvatarPragmaInitialKnowledge("#InitialSessionKnowledge " + keys, null, symKeys.get(sp), true));
            }
        }
        for (SecurityPattern sp : pubKeys.keySet()) {
            if (pubKeys.get(sp).size() != 0) {
                String keys = "";
                List<String> pubKeyNames = new ArrayList<String>();
                for (AvatarAttribute key : pubKeys.get(sp)) {
                    if (!pubKeyNames.contains(key.getBlock().getName() + "." + key.getName())) {
                        keys = keys + " " + key.getBlock().getName() + "." + key.getName();
                        pubKeyNames.add(key.getBlock().getName() + "." + key.getName());
                    }
                }
                //	avspec.addPragma(new AvatarPragmaInitialKnowledge("#InitialSessionKnowledge "+keys, null, pubKeys.get(sp),true));
                //System.out.println("pragma " + keys);
            }
        }

        tmlmap.getTMLModeling().secChannelMap = secChannelMap;

//			System.out.println("avatar spec\n" +avspec);
        return avspec;
    }

    public AvatarSpecification generateAvatarSpec(String _loopLimit) {
        return generateAvatarSpec(_loopLimit, false);
    }

    public void backtraceReachability(Map<AvatarPragmaReachability, ProVerifQueryResult> reachabilityResults) {
        for (AvatarPragmaReachability pragma : reachabilityResults.keySet()) {
            ProVerifQueryResult result = reachabilityResults.get(pragma);
            if (!result.isProved())
                continue;

            int r = result.isSatisfied() ? 1 : 2;

            String s = pragma.getBlock().getName() + "__" + pragma.getState().getName();

            if (stateObjectMap.containsKey(s)) {
                Object obj = stateObjectMap.get(s);
                if (obj instanceof SecurityCheckable) {
                    SecurityCheckable wc = (SecurityCheckable) obj;
                    wc.setReachabilityInformation(r);
                }
            }
        }
    }

    public void backtraceAuthenticityADReadChannels(ProVerifOutputAnalyzer pvoa, String mappingName) {
        Map<AvatarPragmaAuthenticity, ProVerifQueryAuthResult> authenticityResults = pvoa.getAuthenticityResults();
        for (AvatarPragmaAuthenticity pragma : authenticityResults.keySet()) {
            ProVerifQueryAuthResult result = authenticityResults.get(pragma);
            if (!result.isProved() || !result.isWeakProved())
                continue;
            int resWeakAuthStatus = 1;
            int resStrongAuthStatus = 1;
            if (result.isWeakProved()) {
                resWeakAuthStatus = result.isWeakSatisfied() ? 2 : 3;
            }

            if (result.isProved()) {
                resStrongAuthStatus = result.isSatisfied() ? 2 : 3;
            }

            if (pragma.getAttrB().getReferenceObject()!= null && pragma.getAttrB().getReferenceObject() instanceof SecurityBacktracer) {
                SecurityBacktracer rc = (SecurityBacktracer) pragma.getAttrB().getReferenceObject();
                TMLChannel channel = tmlmodel.getChannelByShortName(rc.getCommunicationName());
                if (channel != null) {
                    rc.setAuthCheck(channel.checkAuth);
                }
                if (rc.getWeakAuthStatus() < 3) {
                    rc.setWeakAuthStatus(resWeakAuthStatus);
                    if (rc.getStrongAuthStatus() < 3) {
                        rc.setStrongAuthStatus(resStrongAuthStatus);
                    }
                }
            }

            if (pragma.getAttrB().getReferenceObject()!= null && pragma.getAttrB().getReferenceObject() instanceof SecurityDecryptor) {
                SecurityDecryptor dec = (SecurityDecryptor) pragma.getAttrB().getReferenceObject();
                for (TMLTask t : taskBlockMap.keySet()) {
                    if (taskBlockMap.get(t).equals(pragma.getAttrB().getAttribute().getBlock())) {
                        chDestinationTask:
                        for (TMLChannel ch : tmlmodel.getChannels(t)) {
                            if (ch.hasDestinationTask(t)) {
                                for (TMLActivityElement actElem : t.getActivityDiagram().getElements()) {
                                    if (actElem instanceof TMLReadChannel) {
                                        TMLReadChannel rc = (TMLReadChannel) actElem;
                                        if (rc.hasChannel(ch) && actElem.securityPattern != null
                                                && actElem.securityPattern.getName().equals(dec.getSecurityContext())) {
                                            dec.setAuthCheck(ch.checkAuth);
                                            break chDestinationTask;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (dec.getWeakAuthStatus() < 3) {
                    dec.setWeakAuthStatus(resWeakAuthStatus);
                    if (dec.getStrongAuthStatus() < 3) {
                        dec.setStrongAuthStatus(resStrongAuthStatus);
                    }
                }
            }
        }
    }

    public void distributeKeys() {
        List<TMLTask> tasks = tmlmap.getTMLModeling().getTasks();
        for (TMLTask t : accessKeys.keySet()) {
            AvatarBlock b = taskBlockMap.get(t);
            for (SecurityPattern sp : accessKeys.get(t)) {
                if (sp.type.equals(SecurityPattern.SYMMETRIC_ENC_PATTERN) || sp.type.equals(SecurityPattern.MAC_PATTERN)) {
                    AvatarAttribute key = new AvatarAttribute("key_" + sp.name, AvatarType.INTEGER, b, null);
                    if (symKeys.containsKey(sp)) {
                        symKeys.get(sp).add(key);
                    } else {
                        LinkedList<AvatarAttribute> tmp = new LinkedList<AvatarAttribute>();
                        tmp.add(key);
                        symKeys.put(sp, tmp);
                    }
                    b.addAttribute(key);
                } else if (sp.type.equals(SecurityPattern.ASYMMETRIC_ENC_PATTERN)) {
                    AvatarAttribute pubkey = new AvatarAttribute("pubKey_" + sp.name, AvatarType.INTEGER, b, null);
                    b.addAttribute(pubkey);

                    AvatarAttribute privkey = new AvatarAttribute("privKey_" + sp.name, AvatarType.INTEGER, b, null);
                    b.addAttribute(privkey);
                    avspec.addPragma(new AvatarPragmaPrivatePublicKey("#PrivatePublicKeys " + b.getName() + " " + privkey.getName() + " " +
                            pubkey.getName(), null, privkey, pubkey));
                    if (pubKeys.containsKey(sp)) {
                        pubKeys.get(sp).add(pubkey);
                    } else {
                        LinkedList<AvatarAttribute> tmp = new LinkedList<AvatarAttribute>();
                        tmp.add(pubkey);
                        pubKeys.put(sp, tmp);
                    }
                    //Distribute public key everywhere
                    for (TMLTask task2 : tasks) {
                        AvatarBlock b2 = taskBlockMap.get(task2);
                        pubkey = new AvatarAttribute("pubKey_" + sp.name, AvatarType.INTEGER, b2, null);
                        b2.addAttribute(pubkey);
                        if (pubKeys.containsKey(sp)) {
                            pubKeys.get(sp).add(pubkey);
                        }
                    }
                }
            }
        }

    }

    public AvatarBlock createFifo(String name) {
        AvatarBlock fifo = new AvatarBlock("FIFO__FIFO" + name, avspec, null);
        AvatarState root = new AvatarState("root", null, fifo, false, false);
        AvatarSignal read = new AvatarSignal("readSignal", AvatarSignal.IN, null);
        AvatarAttribute data = new AvatarAttribute("data", AvatarType.INTEGER, fifo, null);
        fifo.addAttribute(data);
        read.addParameter(data);
        AvatarSignal write = new AvatarSignal("writeSignal", AvatarSignal.OUT, null);
        write.addParameter(data);
        AvatarStartState start = new AvatarStartState("start", null, fifo);
        AvatarTransition afterStart = new AvatarTransition(fifo, "afterStart", null);
        fifo.addSignal(read);
        fifo.addSignal(write);
        AvatarTransition toRead = new AvatarTransition(fifo, "toReadSignal", null);
        AvatarTransition toWrite = new AvatarTransition(fifo, "toWriteSignal", null);
        AvatarTransition afterRead = new AvatarTransition(fifo, "afterReadSignal", null);
        AvatarTransition afterWrite = new AvatarTransition(fifo, "afterWriteSignal", null);
        AvatarActionOnSignal readAction = new AvatarActionOnSignal("read", read, null, fifo);
        AvatarActionOnSignal writeAction = new AvatarActionOnSignal("write", write, null, fifo);

        AvatarStateMachine asm = fifo.getStateMachine();
        asm.addElement(start);
        asm.setStartState(start);
        asm.addElement(afterStart);
        asm.addElement(root);
        asm.addElement(toRead);
        asm.addElement(toWrite);
        asm.addElement(afterRead);
        asm.addElement(afterWrite);
        asm.addElement(readAction);
        asm.addElement(writeAction);

        start.addNext(afterStart);
        afterStart.addNext(root);
        root.addNext(toRead);
        root.addNext(toWrite);
        toRead.addNext(readAction);
        toWrite.addNext(writeAction);
        readAction.addNext(afterRead);
        writeAction.addNext(afterWrite);
        afterRead.addNext(root);
        afterWrite.addNext(root);

        avspec.addBlock(fifo);
        return fifo;
    }


    public AvatarSpecification convertToSecurityType(AvatarSpecification spec) {
        return spec;
    }

    public AvatarType getAvatarType(TMLType p) {
        switch (p.getType()) {
            case TMLType.NATURAL:
                return AvatarType.INTEGER;
            case TMLType.BOOLEAN:
                return AvatarType.BOOLEAN;
        }
        return AvatarType.UNDEFINED;
    }

    public String getNameReworked(String name, int index) {
        String[] split = name.split("__");
        if (split.length > index) {
            return split[index];
        }
        return name;
    }

    private String reworkStringName(String _name) {
        String ret = _name.replaceAll(" ", "");
        ret = ret.replaceAll("__", "_");
        ret = ret.replaceAll("-", "");
        return ret;
    }

   /*  public static boolean isInteger(String string) {         
        if(string == null || string.equals("")) {
            return false;
        }
        
        try {
            int intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            
        }
        return false;
    }*/

}



