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


package tmltranslator.toavatar;

import avatartranslator.*;
import myutil.TraceManager;
import tmltranslator.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FullTML2Avatar
 * Creation: 29/02/2016
 *
 * @author Ludovic APVRILLE, Letitia LI
 * @version 2.0 19/06/2020
 */
public class FullTML2Avatar {
    private final static String NOTIFIED = "_NOTIFIED";

    //private TMLMapping<?> tmlmap;
    private TMLModeling<?> tmlmodel;

    private Map<SecurityPattern, List<AvatarAttribute>> symKeys = new HashMap<SecurityPattern, List<AvatarAttribute>>();
    private Map<SecurityPattern, List<AvatarAttribute>> pubKeys = new HashMap<SecurityPattern, List<AvatarAttribute>>();
    private Map<String, String> nameMap = new HashMap<String, String>();
    //private AvatarAttribute pKey;
    public Map<TMLChannel, Integer> channelMap = new HashMap<TMLChannel, Integer>();
    public Map<TMLTask, AvatarBlock> taskBlockMap = new HashMap<TMLTask, AvatarBlock>();
    public Map<String, Integer> originDestMap = new HashMap<String, Integer>();
    private Map<String, AvatarSignal> signalInMap = new HashMap<String, AvatarSignal>();
    private Map<String, AvatarSignal> signalOutMap = new HashMap<String, AvatarSignal>();
    private Map<TMLRequest, AvatarSignal> signalRequest = new HashMap<TMLRequest, AvatarSignal>();
    public Map<String, Object> stateObjectMap = new HashMap<String, Object>();
    public Map<TMLTask, List<SecurityPattern>> accessKeys = new HashMap<TMLTask, List<SecurityPattern>>();

    HashMap<String, List<String>> secChannelMap = new HashMap<String, List<String>>();

    HashMap<String, AvatarAttributeState> signalAuthOriginMap = new HashMap<String, AvatarAttributeState>();
    HashMap<String, AvatarAttributeState> signalAuthDestMap = new HashMap<String, AvatarAttributeState>();

    public ArrayList<SecurityPattern> secPatterns = new ArrayList<SecurityPattern>();

    List<AvatarSignal> signals = new ArrayList<AvatarSignal>();
    private final static Integer channelPublic = 0;
    private final static Integer channelPrivate = 1;
    private final static Integer channelUnreachable = 2;
    public int loopLimit = 1;
    AvatarSpecification avspec;
    ArrayList<String> attrsToCheck;
    List<String> allStates;
    boolean mc = true;
    boolean security = false;

    private Object referenceObject;

    public FullTML2Avatar(TMLModeling<?> tmlmodel, Object _referenceObject) {
        this.tmlmodel = tmlmodel;
        referenceObject = _referenceObject;

        allStates = new ArrayList<String>();
        attrsToCheck = new ArrayList<String>();

    }


    public AvatarSpecification generateAvatarSpec(String _loopLimit) {


        //TODO: Make state names readable
        //TODO: Put back numeric guards
        //TODO: Calculate for temp variable
        if (tmlmodel.getReference() != null) {
            this.avspec = new AvatarSpecification("spec", referenceObject);
        } else {
            this.avspec = new AvatarSpecification("spec", null);
        }
        attrsToCheck.clear();
        tmlmodel.removeForksAndJoins();


        for (TMLChannel chan : tmlmodel.getChannels()) {
            //TraceManager.addDev("chan " + chan);
            TMLTask task = chan.getOriginTask();
            TMLTask task2 = chan.getDestinationTask();

            /*if (chan.getName().contains("fork__") || chan.getName().contains("FORKCHANNEL")) {
                chan.setName(chan.getName().replaceAll("__", ""));
            }*/
        }

        //Only set the loop limit if it's a number
        String pattern = "^[0-9]{1,2}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(_loopLimit);
        if (m.find()) {
            loopLimit = Integer.valueOf(_loopLimit);
        }

        for (TMLChannel channel : tmlmodel.getChannels()) {
            for (TMLPortWithSecurityInformation p : channel.ports) {
                channel.checkConf = channel.checkConf || p.getCheckConf();
                channel.checkAuth = channel.checkAuth || p.getCheckAuth();
            }
        }

        AvatarBlock top = new AvatarBlock("TOP__TOP", avspec, null);
        if (mc) {
            avspec.addBlock(top);
            AvatarStateMachine topasm = top.getStateMachine();
            AvatarStartState topss = new AvatarStartState("start", null, top);
            topasm.setStartState(topss);
            topasm.addElement(topss);
        }

        List<TMLTask> tasks = tmlmodel.getTasks();

        for (TMLTask task : tasks) {
            AvatarBlock block = new AvatarBlock(task.getName().split("__")[task.getName().split("__").length - 1], avspec, task.getReferenceObject());
            if (mc) {
                block.setFather(top);
            }
            taskBlockMap.put(task, block);
            avspec.addBlock(block);
        }

        // checkConnections();
        //	checkChannels();

        //distributeKeys();

        //TraceManager.addDev("ALL KEYS " + accessKeys);
			/*for (TMLTask t: accessKeys.keySet()){
				TraceManager.addDev("TASK " +t.getName());
				for (SecurityPattern sp: accessKeys.get(t)){
					TraceManager.addDev(sp.name);
				}
			}*/


        for (TMLRequest request : tmlmodel.getRequests()) {
            //TraceManager.addDev("Handling request: " + request.getName());

            AvatarBlock bDest = taskBlockMap.get(request.getDestinationTask());
            AvatarRelation ar = new AvatarRelation(request.getName(), top,
                    taskBlockMap.get(request.getDestinationTask()), request.getReferenceObject());
            ar.setPrivate(true);

            AvatarSignal sigReqOut = new AvatarSignal(bDest.getName() + "_" + getLastName(request.getName()), ui.AvatarSignal.OUT, request
                    .getReferenceObject
                            ());
            top.addSignal(sigReqOut);
            signalRequest.put(request, sigReqOut);

            AvatarSignal sigReqIn = new AvatarSignal(getLastName(request.getName()), ui.AvatarSignal.IN, request.getReferenceObject());
            bDest.addSignal(sigReqIn);


            // Adding parameters
            int cpt = 0;
            for (TMLType tmlt : request.getParams()) {
                AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                sigReqOut.addParameter(aa);
                sigReqIn.addParameter(aa);
                cpt++;
            }

            ar.setAsynchronous(true);
            ar.setSizeOfFIFO(8);
            ar.setBlocking(false);
            ar.addSignals(sigReqOut, sigReqIn);
            avspec.addRelation(ar);


            for (TMLTask t1 : request.getOriginTasks()) {

                //signalOutMap.add(request.getName(), sigReqOut);
                /*TraceManager.addDev("T1=" + t1.getName() + " request=" + request.getName());
                TraceManager.addDev("Dest tak=" + request.getDestinationTask().getName());
                AvatarBlock b = taskBlockMap.get(t1);

                if (b == null) {
                    TraceManager.addDev("Block b null for t1");
                } else {
                    TraceManager.addDev("B / t1 = " + b.getName());
                }

                b = taskBlockMap.get(request.getDestinationTask());
                if (b == null) {
                    TraceManager.addDev("Block b null for dest task");
                } else {
                    TraceManager.addDev("B / dest = " + b.getName());
                }*/

                /*AvatarRelation ar = new AvatarRelation(request.getName(), taskBlockMap.get(t1),
                        taskBlockMap.get(request.getDestinationTask()), request.getReferenceObject());
                //ar.setPrivate(originDestMap.get(t1.getName() + "__" + request.getDestinationTask().getName()) == 1);
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
                    TraceManager.addDev("Could not match for " + request.getName());
                }*/

                // Adding signals to blocks


            }
        }


        for (TMLTask task : tasks) {

            AvatarBlock block = taskBlockMap.get(task);
            //Add temp variable for unsendable signals


            //Add all signals
            for (TMLChannel chan : tmlmodel.getChannels(task)) {

                if (chan.hasOriginTask(task)) {
                    TraceManager.addDev("Handling channel: " + chan.getName() + " -> sig: " + chan.getOriginPort().getName() + "_out");
                    AvatarSignal sig = new AvatarSignal(chan.getOriginPort().getName() + "_out", AvatarSignal.OUT, chan.getReferenceObject());

                    block.addSignal(sig);
                    signals.add(sig);
                    AvatarAttribute channelData = new AvatarAttribute(reworkStringName(chan.getOriginPort().getName() + "_chData"),
                            AvatarType.INTEGER, block, null);
                    if (block.getAvatarAttributeWithName(reworkStringName(chan.getOriginPort().getName() + "_chData")) == null) {
                        block.addAttribute(channelData);
                    }
                    //sig.addParameter(channelData);
                    signalOutMap.put(chan.getName(), sig);

                }

                if (chan.hasDestinationTask(task)) {
                    TraceManager.addDev("Handling channel: " + chan.getName() + " -> sig: " + chan.getDestinationPort().getName() + "_in");
                    AvatarSignal sig = new AvatarSignal(reworkStringName(chan.getDestinationPort().getName() + "_in"), AvatarSignal.IN,
                            chan.getReferenceObject());
                    block.addSignal(sig);
                    signals.add(sig);
                    signalInMap.put(chan.getName(), sig);
                    AvatarAttribute channelData = new AvatarAttribute(reworkStringName(getName(chan.getDestinationPort().getName()) + "_chData"), AvatarType.INTEGER,
                            block, null);
                    if (block.getAvatarAttributeWithName(reworkStringName(getName(chan.getDestinationPort().getName()) + "_chData")) == null) {
                        block.addAttribute(channelData);
                    }
                    //sig.addParameter(channelData);
                }
            }

            // Add all events
            for (TMLEvent evt : tmlmodel.getEvents(task)) {
                //TraceManager.addDev("Handling evt: " + evt.getName() + " in task " + task.getName());
                if (evt.hasOriginTask(task)) {
                    String name = getNameReworked(evt.getName(), 1) + "_out";
                    //TraceManager.addDev("Adding OUT evt:" + name);
                    AvatarSignal sig = block.addSignalIfApplicable(name, AvatarSignal.OUT, evt.getReferenceObject());
                    signalOutMap.put(evt.getName(), sig);

                    //Adding parameter
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }

                }

                if (evt.hasDestinationTask(task)) {
                    String name = getNameReworked(evt.getName(), 3) + "_in";
                    //TraceManager.addDev("Adding IN evt:" + name);
                    AvatarSignal sig = block.addSignalIfApplicable(name, AvatarSignal.IN, evt.getReferenceObject());
                    signalInMap.put(evt.getName(), sig);

                    //Adding parameter
                    int cpt = 0;
                    for (TMLType tmlt : evt.getParams()) {
                        AvatarAttribute aa = new AvatarAttribute("p" + cpt, getAvatarType(tmlt), null, null);
                        sig.addParameter(aa);
                        cpt++;
                    }

                    name = getNameReworked(evt.getName(), 3) + NOTIFIED;
                    sig = block.addSignalIfApplicable(name, AvatarSignal.IN, evt.getReferenceObject());
                    signalInMap.put(evt.getName() + NOTIFIED, sig);

                    //Adding parameter
                    AvatarAttribute aa = new AvatarAttribute("p" + cpt, AvatarType.INTEGER, null, null);
                    sig.addParameter(aa);

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
                AvatarAttribute avattr = new AvatarAttribute(reworkStringName(attr.getName()), type, block, null);
                avattr.setInitialValue(attr.getInitialValue());
                block.addAttribute(avattr);
            }
            //AvatarTransition last;
            AvatarStateMachine asm = block.getStateMachine();


            //TODO: Create a fork with many requests. This looks terrible
            // No! This is easy!!
            if (tmlmodel.getRequestToMe(task) != null) {
                //Create iteration attribute
                AvatarAttribute req_loop_index = new AvatarAttribute("req_loop_index", AvatarType.INTEGER, block, null);
                block.addAttribute(req_loop_index);

                //TMLRequest request= tmlmodel.getRequestToMe(task);
                //Oh this is fun...let's restructure the state machine
                //Create own start state, and ignore the returned one
                List<AvatarStateMachineElement> elementList = translateState(task.getActivityDiagram().get(0), block);
                AvatarStartState ss = new AvatarStartState("start", task.getActivityDiagram().get(0).getReferenceObject(), block);
                asm.addElement(ss);
                AvatarTransition at = new AvatarTransition(block, "__after_start", task.getActivityDiagram().get(0).getReferenceObject());
                //at.addAction(AvatarTerm.createActionFromString(block, "req_loop_index = 0"));
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
                    AvatarTransition incrTran = new AvatarTransition(block, "__after_loopstart_" + req.getName(), task.getActivityDiagram().get(0).getReferenceObject());
                    //incrTran.addAction(AvatarTerm.createActionFromString(block, "req_loop_index = req_loop_index + 1"));
                    //incrTran.setGuard(AvatarGuard.createFromString(block, "req_loop_index != " + loopLimit));
                    asm.addElement(incrTran);
                    loopstart.addNext(incrTran);
                    AvatarSignal sig = block.getSignalByName(getLastName(req.getName()));

                    AvatarActionOnSignal as = new AvatarActionOnSignal("getRequest_" + req.getName(), sig, req.getReferenceObject(), block);
                    incrTran.addNext(as);
                    asm.addElement(as);
						/*as.addValue(req.getName()+"__reqData");
						AvatarAttribute requestData= new AvatarAttribute(req.getName()+"__reqData", AvatarType.INTEGER, block, null);
						block.addAttribute(requestData);*/

                    String att1 = "arg";
                    String att2 = "_req";
                    int cpt = 1;
                    for (int i = 0; i < req.getNbOfParams(); i++) {
                        String arg = att1 + cpt + att2;
                        as.addValue(arg);
                        AvatarAttribute aa = block.getAvatarAttributeWithName(arg);
                        if (aa == null) {
                            aa = new AvatarAttribute(arg, sig.getListOfAttributes().get(cpt).getType(), block, req.getReferenceObject());
                            block.addAttribute(aa);
                        }
                        cpt++;

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
                List<AvatarStateMachineElement> elementList = translateState(task.getActivityDiagram().get(0), block);
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
                    //sec = new AvatarAttribute(secPattern.name, AvatarType.INTEGER, block, null);
                    //AvatarAttribute enc = new AvatarAttribute(secPattern.name+"_encrypted", AvatarType.INTEGER, block, null);
                    //	block.addAttribute(sec);
                    //	block.addAttribute(enc);
                    //}
                    avspec.addPragma(new AvatarPragmaSecret("#Confidentiality " + block.getName() + "." + secPattern.name, null, sec));
                }
            }

        }


        //Add authenticity pragmas
        for (String s : signalAuthOriginMap.keySet()) {
            if (signalAuthDestMap.containsKey(s)) {
                AvatarPragmaAuthenticity pragma = new AvatarPragmaAuthenticity("#Authenticity " + signalAuthOriginMap.get(s).getName() + " " + signalAuthDestMap.get(s).getName(), signalAuthOriginMap.get(s).getReferenceObject(), signalAuthOriginMap.get(s), signalAuthDestMap.get(s));
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

        //Create relations
        //Channels are ?? to ??
        //Requests are n to 1
        //Events are ?? to ??
        AvatarBlock fifo = null;

        for (TMLChannel channel : tmlmodel.getChannels()) {
            // We assume one to one because fork and join have been removed

            if (channel.isBasicChannel()) {
                //TraceManager.addDev("Checking channel " + channel.getName());
                AvatarRelation ar = null;


                if (channel.getType() == TMLChannel.BRBW) {
                    ar = new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel
                            .getDestinationTask()), channel.getReferenceObject());
                    ar.setAsynchronous(true);
                    ar.setSizeOfFIFO(channel.getMax());
                    ar.setBlocking(true);
                    ar.setPrivate(true);
                } else if (channel.getType() == TMLChannel.BRNBW) {
                    ar = new AvatarRelation(channel.getName(), taskBlockMap.get(channel.getOriginTask()), taskBlockMap.get(channel
                            .getDestinationTask()), channel.getReferenceObject());
                    ar.setAsynchronous(true);
                    ar.setSizeOfFIFO(channel.getMax());
                    ar.setBlocking(false);
                    ar.setPrivate(true);
                } else {
                    // Create new block, hope for best
                    if (mc) {
                        fifo = createFifo(channel.getName());
                        ar = new AvatarRelation(channel.getName() + "_OUT", taskBlockMap.get(channel.getOriginTask()), fifo, channel
                                .getReferenceObject());
                        ar.setAsynchronous(false);
                        ar.setPrivate(true);
                    }
                }

                //Find in signal
                List<AvatarSignal> sig1 = new ArrayList<AvatarSignal>();
                //Sig1 contains IN Signals, Sig2 contains OUT signals
                sig1.add(signalInMap.get(channel.getName()));

                List<AvatarSignal> sig2 = new ArrayList<AvatarSignal>();
                sig2.add(signalOutMap.get(channel.getName()));


                if (sig1.size() == 1 && sig2.size() == 1) {

                    if (channel.getType() == TMLChannel.NBRNBW && mc) {
                        //TraceManager.addDev("NBRNBW channel!!");
                        AvatarSignal read = fifo.getSignalByName("readSignal");
                        AvatarSignal write = fifo.getSignalByName("writeSignal");

                        //Set IN signal with read
                        ar.addSignals(sig2.get(0), read);

                        AvatarRelation ar2 = new AvatarRelation(channel.getName() + "_IN", fifo, taskBlockMap.get(channel.getDestinationTask()),
                                channel.getReferenceObject());
                        //set OUT signal with write
                        ar2.addSignals(write, sig1.get(0));
                        //		System.out.println("Set " + sig2.get(0) + " and write");
                        ar2.setAsynchronous(false);
                        ar.setPrivate(true);

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
                        relation.setPrivate(true);
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
                relation.setPrivate(true);
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


        for (TMLEvent event : tmlmodel.getEvents()) {

            //TraceManager.addDev("Handling Event:" + event.getName() + " 1:" + taskBlockMap.get(event.getOriginTask()).getName() + " 2:" +
            // taskBlockMap.get(event.getDestinationTask()).getName());

            // For each event, we create a FIFO, and so a double relation


            AvatarRelation ar = new AvatarRelation(event.getName(), taskBlockMap.get(event.getOriginTask()), taskBlockMap.get(event.getDestinationTask()),
                    event.getReferenceObject());
            ar.setAsynchronous(true);
            ar.setPrivate(true);


            AvatarSignal sigOut = signalOutMap.get(event.getName());
            AvatarSignal sigIn = signalInMap.get(event.getName());
            ar.addSignals(sigOut, sigIn);

            //TraceManager.addDev("Relation for event " + event.getName() + " sigout:" + sigOut.getSignalName() + " sigin:" + sigIn.getSignalName());

            //AvatarSignal sigNotified = signalInMap.get(event.getName() + NOTIFIED);
            //ar.addSignals(sigOut, sigIn);

            //AvatarBlock ab0 = taskBlockMap.get(event.getOriginTask());
            //AvatarBlock ab1 = taskBlockMap.get(event.getDestinationTask());
            //ab0.addSignal(new AvatarSignal(event.getName(), AvatarSignal.OUT, null));
            //ab1.addSignal(new AvatarSignal(event.getName(), AvatarSignal.IN, null));

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


            // Old way to do: using extra blocks. We simply use an asynchronous channel
            /*AvatarBlock FifoEvt = AvatarBlockTemplate.getFifoBlockWithNotified("Block" + event.getName(),
                    avspec, ar, event.getReferenceObject(), sigOut, sigIn, sigNotified, event.getMaxSize(), event.getID());
            avspec.addBlock(FifoEvt);

            ar = new AvatarRelation(event.getName() + "_FIFOIN", taskBlockMap.get(event.getOriginTask()), FifoEvt,
                    event.getReferenceObject());
            ar.addSignals(sigOut, FifoEvt.getAvatarSignalWithName("write"));
            avspec.addRelation(ar);

            ar = new AvatarRelation(event.getName() + "_FIFOIN", FifoEvt, taskBlockMap.get(event.getDestinationTask()),
                    event.getReferenceObject());
            ar.addSignals(FifoEvt.getAvatarSignalWithName("read"), sigIn);
            ar.addSignals(FifoEvt.getAvatarSignalWithName("notified"), sigNotified);
            avspec.addRelation(ar);*/


        }

        //	System.out.println("Avatar relations " + avspec.getRelations());

        for (AvatarSignal sig : signals) {
            //	System.out.println("signal " + sig.getName());
            //check that all signals are put in relations
            AvatarRelation ar = avspec.getAvatarRelationWithSignal(sig);
            if (ar == null) {
                TraceManager.addDev("missing relation for " + sig.getName());
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

        tmlmodel.secChannelMap = secChannelMap;

//			System.out.println("avatar spec\n" +avspec);
        avspec.removeEmptyTransitions(true);
        avspec.groupUselessTransitions();
        return avspec;
    }


    public List<AvatarStateMachineElement> translateState(TMLActivityElement ae, AvatarBlock block) {


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
            ar.setValues(tmlr.getMinValue(), tmlr.getMaxValue());
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            ar.addNext(tran);
            //Add to list
            elementList.add(ar);
            elementList.add(tran);

        } else if (ae instanceof TMLSequence) {
            //Get all list of sequences and paste together
            List<AvatarStateMachineElement> seq = translateState(ae.getNextElement(0), block);
            List<AvatarStateMachineElement> tmp;
            // elementList.addAll(seq);
            //get rid of any stops in the middle of the sequence and replace with the start of the next sequence
            for (int i = 1; i < ae.getNbNext(); i++) {
                tmp = translateState(ae.getNextElement(i), block);
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
            AvatarState signalState =
                    new AvatarState("signalstate_" + reworkStringName(ae.getName()) + "_" + reworkStringName(req.getName()),
                    ae.getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + req.getName(), ae.getReferenceObject());
            sig = signalRequest.get(req);

            //TraceManager.addDev("Found sig=" + sig.getSignalName());

            /*if (!signalOutMap.containsKey(req.getName())) {
                sig = new AvatarSignal(getName(req.getName()+"_out"), AvatarSignal.OUT, req.getReferenceObject());
                signals.add(sig);
                signalOutMap.put(req.getName(), sig);
                block.addSignal(sig);
            } else {
                sig = signalOutMap.get(req.getName());
            }*/

            AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);

            //TraceManager.addDev("Send request in block " + block.getName() + " nb of params:" + sr.getNbOfParams());

            for (int i = 0; i < sr.getNbOfParams(); i++) {

                if (block.getAvatarAttributeWithName(sr.getParam(i)) == null) {
                    //Throw Error
                    //TraceManager.addDev("Missing Attribute " + sr.getParam(i));
                    as.addValue("tmp");
                } else {
                    //	Add parameter to signal and actiononsignal
                    //sig.addParameter(block.getAvatarAttributeWithName(sr.getParam(i)));
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
            AvatarState choiceState = new AvatarState("seqchoice__" + reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            elementList.add(choiceState);

            if (ae.getNbNext() == 1) {
                // We just use the choice state
                tran = new AvatarTransition(block, "__after_" + ae.getName() + "_0", ae.getReferenceObject());
                elementList.add(tran);
                choiceState.addNext(tran);
                List<AvatarStateMachineElement> set0 = translateState(ae.getNextElement(0), block);
                tran.addNext(set0.get(0));
                elementList.addAll(set0);
                return elementList;

            } else if (ae.getNbNext() == 2) {
                List<AvatarStateMachineElement> set0 = translateState(ae.getNextElement(0), block);
                List<AvatarStateMachineElement> set1 = translateState(ae.getNextElement(1), block);
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
                List<AvatarStateMachineElement> set0_1 = translateState(ae.getNextElement(0), block);
                List<AvatarStateMachineElement> set1_1 = translateState(ae.getNextElement(1), block);
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
                    elementList.add(tran);
                    List<AvatarStateMachineElement> tmp = translateState(ae.getNextElement(i), block);


                    AvatarState choiceStateEnd = new AvatarState("seqchoiceend__" + i + "_" + reworkStringName(ae.getName()),
                            ae.getReferenceObject(), block);
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

                    List<AvatarStateMachineElement> nexts = translateState(newSeq, block);
                    elementList.addAll(nexts);
                    tran.addNext(nexts.get(0));

                }

            }
            return elementList;

        } else if (ae instanceof TMLActivityElementEvent) {
            TMLActivityElementEvent aee = (TMLActivityElementEvent) ae;
            TMLEvent evt = aee.getEvent();

            boolean checkAcc = ae.hasCheckableAccessibility();
            boolean checked =  ae.hasCheckedAccessibility();
            AvatarState signalState = new AvatarState("signalstate_" + reworkStringName(ae.getName() + "_" + evt.getName()),
                    ae.getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + evt.getName(), ae.getReferenceObject());

            if (ae instanceof TMLSendEvent) {

                AvatarSignal sig = signalOutMap.get(evt.getName());
                //TraceManager.addDev("sig=" + sig);

                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
                for (int i = 0; i < aee.getNbOfParams(); i++) {
                    as.addValue(aee.getParam(i));
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
                //TraceManager.addDev("Looking for IN evt: " + evt.getName());
                AvatarSignal sig = signalInMap.get(evt.getName());
                //TraceManager.addDev("sig=" + sig);

                AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
                for (int i = 0; i < aee.getNbOfParams(); i++) {
                    as.addValue(aee.getParam(i));

                    /*if (block.getAvatarAttributeWithName(aee.getParam(i)) == null) {
                        //Throw Error
                        as.addValue("tmp");
                        TraceManager.addDev("Missing Attribute " + aee.getParam(i));
                    } else {
                        //	Add parameter to signal and actiononsignal
                        TraceManager.addDev("Param #" + i + ":" + aee.getParam(i));
                        AvatarAttribute aa = block.getAvatarAttributeWithName(aee.getParam(i));
                        if (aa == null) {
                            TraceManager.addDev("NULL Att:");
                        }
                        sig.addParameter(block.getAvatarAttributeWithName(aee.getParam(i)));



                        TraceManager.addDev("Param #" + i + " (1)");
                        as.addValue(aee.getParam(i));
                        TraceManager.addDev("Param #" + i + " (2)");
                    }*/
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
                //TraceManager.addDev("Class: " + ae.getClass() +  " ae=" + ae.toString());
                //Notify Event: we use a query signal
                AvatarSignal sig = signalInMap.get(evt.getName());

                String param = ((TMLActivityElementEvent)ae).getVariable();
                AvatarAttribute at = block.getAvatarAttributeWithName(param);
                if (at == null) {
                    at = new AvatarAttribute(reworkStringName(param), AvatarType.INTEGER, block, ae.getReferenceObject());
                }
                //TraceManager.addDev("sig=" + sig);
                AvatarQueryOnSignal aqos = new AvatarQueryOnSignal(ae.getName(), sig, at, ae.getReferenceObject(), block);

                tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());

                aqos.addNext(tran);
                elementList.add(aqos);
                elementList.add(tran);
            }

        } else if (ae instanceof TMLActivityElementWithAction) {
            //Might be encrypt or decrypt
            AvatarState as =
                    new AvatarState(reworkStringName(ae.getValue()+"_" + ae.getName()), ae.getReferenceObject(), block);
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
                    //Set attributestate for authenticity
                    if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                        AvatarAttributeState authOrigin = new AvatarAttributeState(block.getName() + "." + as.getName() + "." + ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), as);
                        signalAuthOriginMap.put(ae.securityPattern.name, authOrigin);
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
                            if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null && block.getAvatarAttributeWithName("testnonce_" + ae.securityPattern.nonce) != null) {
                                block.addMethod(get2);
                                tran.addAction("get2(" + ae.securityPattern.name + "," + ae.securityPattern.name + ",testnonce_" + ae.securityPattern.nonce + ")");
                            }

                            //Add state after get2 statement
                            AvatarState guardState = new AvatarState(reworkStringName(ae.getName() + "_guarded"), ae.getReferenceObject(), block);
                            tran.addNext(guardState);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            guardState.addNext(tran);
                            elementList.add(guardState);
                            elementList.add(tran);

                            //Guard transition to determine if nonce matches
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                        }
                        //Add a dummy state afterwards for authenticity after decrypting the data
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName() + "_dummy"), ae.getReferenceObject(), block);
                        ae.securityPattern.state2 = dummy;
                        tran.addNext(dummy);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." + ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            signalAuthDestMap.put(ae.securityPattern.name, authDest);
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

                            if (block.getAvatarAttributeWithName("encryptedKey_" + ae.securityPattern.key) != null && block.getAvatarAttributeWithName("privKey_" + ae.securityPattern.name) != null && block.getAvatarAttributeWithName("key_" + ae.securityPattern.key) != null) {
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
                            AvatarState guardState = new AvatarState(reworkStringName(ae.getName() + "_guarded"), ae.getReferenceObject(), block);
                            tran.addNext(guardState);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            elementList.add(guardState);
                            elementList.add(tran);
                            guardState.addNext(tran);
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                        }
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName() + "_dummy"), ae.getReferenceObject(), block);
                        tran.addNext(dummy);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);
                        ae.securityPattern.state2 = dummy;
                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." +
                                    ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            signalAuthDestMap.put(ae.securityPattern.name, authDest);
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

                        AvatarState guardState = new AvatarState(reworkStringName(ae.getName() + "_guarded"), ae.getReferenceObject(), block);
                        tran.addNext(guardState);
                        tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                        elementList.add(guardState);
                        elementList.add(tran);
                        guardState.addNext(tran);
                        tran.setGuard("testnonce_" + ae.securityPattern.name);

                        if (!ae.securityPattern.nonce.isEmpty()) {

                            //Add extra state and transition

                            AvatarState guardState2 = new AvatarState(reworkStringName(ae.getName() + "_guarded2"),
                                    ae.getReferenceObject(), block);
                            tran.addNext(guardState2);
                            tran = new AvatarTransition(block, "__guard_" + ae.getName(), ae.getReferenceObject());
                            tran.setGuard("testnonce_" + ae.securityPattern.nonce + "==" + ae.securityPattern.nonce);
                            elementList.add(guardState2);
                            elementList.add(tran);

                            guardState2.addNext(tran);
                        }
                        AvatarState dummy = new AvatarState(reworkStringName(ae.getName() + "_dummy"), ae.getReferenceObject(), block);
                        ae.securityPattern.state2 = dummy;
                        tran.addNext(dummy);
                        elementList.add(tran);
                        tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
                        dummy.addNext(tran);
                        elementList.add(dummy);
                        elementList.add(tran);

                        if (block.getAvatarAttributeWithName(ae.securityPattern.name) != null) {
                            AvatarAttributeState authDest = new AvatarAttributeState(block.getName() + "." + dummy.getName() + "." + ae.securityPattern.name, ae.getReferenceObject(), block.getAvatarAttributeWithName(ae.securityPattern.name), dummy);
                            signalAuthDestMap.put(ae.securityPattern.name, authDest);
                        }
                    }


                    //Can't decrypt hash or nonce
                }
            } else {
                // See if action.
                if (ae instanceof TMLActionState) {
                    String val = ((TMLActionState) ae).getAction();
                    tran.addAction(reworkStringName(val));
                } else if (ae instanceof TMLExecI) {
                    tran.setDelays(   reworkStringName(((TMLExecI) (ae)).getAction()), reworkStringName(((TMLExecI) (ae)).getAction()));
                } else if (ae instanceof TMLExecC) {
                    tran.setDelays(  reworkStringName(((TMLExecC) (ae)).getAction()), reworkStringName(((TMLExecC) (ae)).getAction()));
                }

            }

        } else if (ae instanceof TMLActivityElementWithIntervalAction) {
            AvatarState as = new AvatarState(reworkStringName(ae.getName()), ae.getReferenceObject(), block);
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());
            TMLActivityElementWithIntervalAction ia = (TMLActivityElementWithIntervalAction) ae;
            tran.setDelays(reworkStringName(ia.getMinDelay()), reworkStringName(ia.getMaxDelay()));
            as.addNext(tran);
            elementList.add(as);
            elementList.add(tran);

        } else if (ae instanceof TMLActivityElementChannel) {
            TMLActivityElementChannel aec = (TMLActivityElementChannel) ae;
            TMLChannel ch = aec.getChannel(0);
            AvatarSignal sig;

            //String nv = getName(ch.getName()) + "_chData";

            boolean checkAcc = ae.hasCheckableAccessibility();
            boolean checked =  ae.hasCheckedAccessibility();
            AvatarState signalState = new AvatarState("signalstate_" + reworkStringName(ae.getName() + "_" + ch.getName()),
                    ae.getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTran = new AvatarTransition(block, "__after_signalstate_" + ae.getName() + "_" + ch.getName(), ae.getReferenceObject());
            AvatarTransition signalTranBefore = new AvatarTransition(block, "__before_signalstateint_" + ae.getName() + "_" + ch.getName(), ae
                    .getReferenceObject());
            AvatarState signalStateIntermediate = new AvatarState("signalstateinter_" + reworkStringName(
                    ae.getName() + "_" + ch.getName())
                    , ae
                    .getReferenceObject(), block, checkAcc, checked);
            AvatarTransition signalTranInit = new AvatarTransition(block, "_init_signalstate_" + ae.getName() + "_" + ch.getName(), ae
                    .getReferenceObject());

            String nameCh;
            if (ae instanceof TMLReadChannel) {
                sig = signalInMap.get(ch.getName());
                nameCh = ch.getDestinationPort().getName();
            } else {
                sig = signalOutMap.get(ch.getName());
                nameCh = ch.getOriginPort().getName();
            }
            nameCh = reworkStringName(nameCh + "_chData");
            block.addIntAttributeIfApplicable(nameCh);

            if (sig == null) {
                TraceManager.addDev("NULL signal for ch=" + ch.getName());
            }

            AvatarActionOnSignal as = new AvatarActionOnSignal(ae.getName(), sig, ae.getReferenceObject(), block);
            tran = new AvatarTransition(block, "__after_" + ae.getName(), ae.getReferenceObject());

            elementList.add(signalState);
            elementList.add(signalStateIntermediate);
            elementList.add(as);
            elementList.add(tran);
            elementList.add(signalTranInit);
            elementList.add(signalTranBefore);
            elementList.add(signalTran);

            signalState.addNext(signalTranInit);
            signalTranInit.addAction(nameCh + " = 0");
            signalTranInit.addNext(signalStateIntermediate);
            signalStateIntermediate.addNext(signalTran);
            signalTran.setGuard(nameCh + "< (" + aec.getNbOfSamples() + ")");
            signalTran.addNext(as);
            as.addNext(signalTranBefore);
            signalTranBefore.addAction(nameCh + " = " + nameCh + " + 1 ");
            signalTranBefore.addNext(signalStateIntermediate);

            signalStateIntermediate.addNext(tran);
            tran.setGuard("else");


        } else if (ae instanceof TMLForLoop) {
            TMLForLoop loop = (TMLForLoop) ae;

            TraceManager.addDev("Handling loop.  init: " + loop.getInit() +
                    "  condition: " + loop.getCondition() + " increment:" + loop.getIncrement());


            if (loop.isInfinite()) {
                //Make initializaton, then choice state with transitions
                List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block);
                /*List<AvatarStateMachineElement> afterloop =*/
                translateState(ae.getNextElement(1), block);
                AvatarState initState = new AvatarState(reworkStringName(ae.getName() + "__init"), ae.getReferenceObject(), block);
                elementList.add(initState);
                //Build transition to choice
                tran = new AvatarTransition(block, "loop_init__" + ae.getName(), ae.getReferenceObject());
                tran.addAction("loop_index=0");
                elementList.add(tran);
                initState.addNext(tran);
                //Choice state
                AvatarState as = new AvatarState(reworkStringName(ae.getName() + "__choice"), ae.getReferenceObject(), block);
                elementList.add(as);
                tran.addNext(as);
                //transition to first element of loop
                tran = new AvatarTransition(block, "loop_increment__" + ae.getName(), ae.getReferenceObject());
                //Set default loop limit guard
                tran.setGuard(reworkStringName(AvatarGuard.createFromString(block, "loop_index != " + loopLimit).toString()));
                //tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
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
                List<AvatarStateMachineElement> elements = translateState(ae.getNextElement(0), block);
                List<AvatarStateMachineElement> afterloop = translateState(ae.getNextElement(1), block);

                AvatarState initState = new AvatarState(reworkStringName(ae.getName() + "__init"), ae.getReferenceObject(), block);
                elementList.add(initState);
                //Build transition to choice
                tran = new AvatarTransition(block, "loop_init__" + ae.getName(), ae.getReferenceObject());
                tran.addAction(AvatarTerm.createActionFromString(block, loop.getInit()));
                //tran.addAction(AvatarTerm.createActionFromString(block, "loop_index=0"));
                elementList.add(tran);
                initState.addNext(tran);

                //Choice state
                AvatarState as = new AvatarState(reworkStringName(ae.getName() + "__choice"), ae.getReferenceObject(), block);
                elementList.add(as);
                tran.addNext(as);

                //End state
                AvatarState asEnd = new AvatarState(reworkStringName(ae.getName() + "__incr"), ae.getReferenceObject(), block);
                elementList.add(asEnd);
                AvatarTransition tranToEnd = new AvatarTransition(block, "loop_init__" + ae.getName(), ae.getReferenceObject());
                tranToEnd.addAction(AvatarTerm.createActionFromString(block, loop.getIncrement()));
                elementList.add(tranToEnd);
                asEnd.addNext(tranToEnd);
                tranToEnd.addNext(as);


                //transition to first element of loop
                tran = new AvatarTransition(block, "loop_increment__" + ae.getName(), ae.getReferenceObject());
                //Set default loop limit guard
                tran.setGuard(reworkStringName( AvatarGuard.createFromString(block, loop.getCondition()).toString())) ;
                /*AvatarGuard guard = */
                //AvatarGuard.createFromString(block, loop.getCondition());
                //tran.addAction(AvatarTerm.createActionFromString(block, loop.getIncrement()));
                //tran.addAction(AvatarTerm.createActionFromString(block, "loop_index = loop_index + 1"));
                if (elements.size() > 0) {
                    tran.addNext(elements.get(0));
                    as.addNext(tran);
                    elementList.add(tran);
                }
                //Process elements in loop to remove stop states and empty transitions, and loop back to choice
                for (AvatarStateMachineElement e : elements) {
                    if (e instanceof AvatarStopState) {
                    } else if (e.getNexts().size() == 0) {
                        e.addNext(asEnd);
                        elementList.add(e);
                    } else if (e.getNext(0) instanceof AvatarStopState) {
                        //Remove the transition to AvatarStopState
                        e.removeNext(0);
                        e.addNext(asEnd);
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
                tran.setGuard(reworkStringName(c.getGuard(i)));
                as.addNext(tran);
                List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block);
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
                //tran = new AvatarTransition(block, "__after_" + ae.getName() + "_" + i, ae.getReferenceObject());
                //as.addNext(tran);
                List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(i), block);
                //tran.addNext(nexts.get(0));
                //elementList.add(tran);
                nexts.remove(0);
                as.addNext(nexts.get(0));
                elementList.addAll(nexts);
            }
            return elementList;

        } else {
            TraceManager.addDev("Undefined tml element " + ae);
        }

        List<AvatarStateMachineElement> nexts = translateState(ae.getNextElement(0), block);
        if (nexts.size() == 0) {
            //in an infinite loop i hope
            return elementList;
        }
        tran.addNext(nexts.get(0));
        elementList.addAll(nexts);
        return elementList;
    }

    public String processName(String name, int id) {
        name = reworkStringName(name);
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
            } else if (s.split("__").length == 1) {
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



		/*public void backtraceReachability( Map<AvatarPragmaReachability, ProVerifQueryResult> reachabilityResults) {
			for (AvatarPragmaReachability pragma: reachabilityResults.keySet())
			{
				ProVerifQueryResult result = reachabilityResults.get(pragma);
				if (!result.isProved())
					continue;

				int r = result.isSatisfied() ? 1 : 2;

				String s = pragma.getBlock().getName() + "__" + pragma.getState().getName();

				if (stateObjectMap.containsKey(s)) {
					Object obj = stateObjectMap.get(s);
					if (obj instanceof TMLADWriteChannel){
						TMLADWriteChannel wc =(TMLADWriteChannel) obj;
						wc.reachabilityInformation=r;
					}
					if (obj instanceof TMLADReadChannel){
						TMLADReadChannel wc =(TMLADReadChannel) obj;
						wc.reachabilityInformation=r;
					}

					if (obj instanceof TMLADSendEvent){
						TMLADSendEvent wc =(TMLADSendEvent) obj;
						wc.reachabilityInformation=r;
					}

					if (obj instanceof TMLADSendRequest){
						TMLADSendRequest wc =(TMLADSendRequest) obj;
						wc.reachabilityInformation=r;
					}
					if (obj instanceof TMLADWaitEvent){
						TMLADWaitEvent wc =(TMLADWaitEvent) obj;
						wc.reachabilityInformation=r;
					}		
				}
			}
		}*/


    public AvatarBlock createFifo(String name) {
        AvatarBlock fifo = new AvatarBlock("FIFO__FIFO" + name, avspec, null);
        AvatarState root = new AvatarState("root", null, fifo, false, false);
        AvatarSignal read = new AvatarSignal("readSignal", AvatarSignal.IN, null);
        //AvatarAttribute data = new AvatarAttribute("data", AvatarType.INTEGER, fifo, null);
        //fifo.addAttribute(data);
        //read.addParameter(data);
        AvatarSignal write = new AvatarSignal("writeSignal", AvatarSignal.OUT, null);
        //write.addParameter(data);
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


    public String getLastName(String name) {
        String[] split = name.split("__");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return name;
    }

    public String getNameReworked(String name, int index) {
        String[] split = name.split("__");
        if (split.length > index) {
            return split[index];
        }
        return name;
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

    private String reworkStringName(String _name) {
        String ret =  _name.replaceAll(" ", "");
        ret = ret.replaceAll("__", "_");
        ret = ret.replaceAll("-", "");
        return ret;
    }


}



