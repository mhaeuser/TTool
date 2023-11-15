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


package tmltranslator.patternhandling;
/**
 * Class TMRGeneration
 * TMR Generation in separate thread
 * Creation: 26/07/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 26/07/2023
 */
 
import myutil.TraceManager;
import tmltranslator.*;

import java.util.*;
 
public class TMRGeneration implements Runnable {
    private Map<String, List<String>> selectedSensorsTasks;
    private String selectedRecieverTask;
    private String interpretersCompTime;
    private String voterCompTime;
    private String voterTimeOut;
    private TMLMapping<?> tmap;

    public TMRGeneration(Map<String, List<String>> selectedSensorsTasks, String selectedRecieverTask, String interpretersCompTime, String voterCompTime, String voterTimeOut, TMLMapping<?> tmap) {
        this.selectedSensorsTasks = selectedSensorsTasks;
        this.selectedRecieverTask = selectedRecieverTask;
        this.interpretersCompTime = interpretersCompTime;
        this.voterCompTime = voterCompTime;
        this.voterTimeOut = voterTimeOut;
        this.tmap = tmap;
    }
    
    public TMLMapping<?> startThread() {
        Thread t = new Thread(this);
        t.start();
        try {
            t.join();
        }
        catch (Exception e) {
            TraceManager.addDev("Error in TMR Generation Thread");
        }
        return tmap;
    }

    public void run() {
        TraceManager.addDev("Adding TMR");
        if (tmap == null) {
            return;
        }

        TMLModeling<?> tmlmNew = tmap.getTMLModeling();
        for (TMLTask task : tmlmNew.getTasks()) {
            int index = task.getName().indexOf("__");
            if (index > 0) {
                task.setName(task.getName().substring(index + 2));
            }
        }
        for (TMLChannel ch : tmlmNew.getChannels()) {
            int index = ch.getName().indexOf("__");
            if (index > 0) {
                ch.setName(ch.getName().substring(index + 2));
            }
        }
        for (TMLEvent evt : tmlmNew.getEvents()) {
            int index = evt.getName().indexOf("__");
            if (index > 0) {
                evt.setName(evt.getName().substring(index + 2));
            }
        }
        for (TMLRequest req : tmlmNew.getRequests()) {
            int index = req.getName().indexOf("__");
            if (index > 0) {
                req.setName(req.getName().substring(index + 2));
            }
        }
        
        List<String> parameters = new ArrayList<String>();
        parameters.add(interpretersCompTime);
        parameters.add(voterTimeOut);
        parameters.add(voterCompTime);
        
        if (selectedSensorsTasks.size() == 1) {
            String taskMainSensor = selectedSensorsTasks.keySet().iterator().next();
            for (int indexI=0; indexI < 2; indexI++) {
                List<TMLChannel> channelsSensor = new ArrayList<TMLChannel>();
                for (String channelSensorName: selectedSensorsTasks.get(taskMainSensor)) {
                    TMLChannel channelSensor = tmlmNew.getChannelByName(channelSensorName);
                    channelsSensor.add(channelSensor);
                }
                String newSensorName = taskMainSensor + "_duplicate" + (indexI+1);
                tmap = duplicateSensor(tmap, channelsSensor, newSensorName);
            }
        }
        List <String> sensorsTask = new ArrayList<String>(selectedSensorsTasks.keySet());
        for (int i=0 ; i < selectedSensorsTasks.get(sensorsTask.get(0)).size() ; i++) {
            List<TMLChannel> channelsSensorReceiverList = new ArrayList<TMLChannel>();
            for (String taskSensor: selectedSensorsTasks.keySet()) {
                TMLChannel channelSensor = tmlmNew.getChannelByName((selectedSensorsTasks.get(taskSensor)).get(i));
                channelsSensorReceiverList.add(channelSensor);
            }
            tmap = tmrIntegration(tmap, channelsSensorReceiverList, parameters);
        }
    } 

    TMLMapping<?> duplicateSensor(TMLMapping<?> _tmap, List<TMLChannel> _channelsBetweenSensorAndReceiver, String newSensorName) {

        if (_channelsBetweenSensorAndReceiver.size() == 0) {
            TraceManager.addDev("No channels between sensors and receiver");
            return _tmap;
        }
        // Check if all channels have the same Destination Task (the same receiver)
        TMLTask receiverTask = _channelsBetweenSensorAndReceiver.get(0).getDestinationTask();
        boolean _isSameDestTask = true;
        for (int i = 1 ; i < _channelsBetweenSensorAndReceiver.size() ; i++) {
            if (_channelsBetweenSensorAndReceiver.get(i).getDestinationTask() != receiverTask) {
                _isSameDestTask = false;
                break;
            }
        }
        if (!_isSameDestTask) {
            TraceManager.addDev("Sensors are not connected to the same receiver");
            return _tmap;
        }
        TMLTask sensorTask = _channelsBetweenSensorAndReceiver.get(0).getOriginTask();

        TMLModeling<?> _tmlm = _tmap.getTMLModeling();
        TMLTask duplicatedSensorTask = new TMLTask(newSensorName, receiverTask.getReferenceObject(), null);
        _tmlm.addTask(duplicatedSensorTask);

        // Add attributes of Sensor to the new one
        duplicatedSensorTask.getAttributes().addAll(sensorTask.getAttributes());
        Map<TMLChannel, TMLChannel> channelsSensorReceiver = new HashMap<TMLChannel, TMLChannel>();
        for (TMLChannel channelSensorReceiver : _channelsBetweenSensorAndReceiver) {
            // Create new channel between new sensor and receiver
            TMLChannel channelDuplicatedSensorToReceiver = new TMLChannel(newSensorName.toLowerCase() + channelSensorReceiver.getName() + "_Data", duplicatedSensorTask.getReferenceObject());

            channelDuplicatedSensorToReceiver.setPorts(new TMLPort(channelDuplicatedSensorToReceiver.getName(), channelDuplicatedSensorToReceiver.getReferenceObject()), new TMLPort(channelDuplicatedSensorToReceiver.getName(), channelDuplicatedSensorToReceiver.getReferenceObject()));
            
            channelDuplicatedSensorToReceiver.setOriginTask(duplicatedSensorTask);
            channelDuplicatedSensorToReceiver.setDestinationTask(receiverTask);
            channelDuplicatedSensorToReceiver.setType(channelSensorReceiver.getType());
            channelDuplicatedSensorToReceiver.setSize(channelSensorReceiver.getSize());
            channelDuplicatedSensorToReceiver.setMax(channelSensorReceiver.getMax());
            // set Duplicated Sensor as Origin of channelDuplicatedSensorToReceiver
            duplicatedSensorTask.addWriteTMLChannel(channelDuplicatedSensorToReceiver);
            // set Receiver as Destination of channelDuplicatedSensorToReceiver
            receiverTask.addReadTMLChannel(channelDuplicatedSensorToReceiver);
            _tmlm.addChannel(channelDuplicatedSensorToReceiver);
            channelsSensorReceiver.put(channelSensorReceiver, channelDuplicatedSensorToReceiver);
            if (selectedSensorsTasks.containsKey(newSensorName)) {
                selectedSensorsTasks.get(newSensorName).add(channelDuplicatedSensorToReceiver.getName());
            } else {
                List<String> newSensorChannelsName = new ArrayList<String>();
                newSensorChannelsName.add(channelDuplicatedSensorToReceiver.getName());
                selectedSensorsTasks.put(newSensorName, newSensorChannelsName);
            }
        }

        //Build Activity Diagram for duplicatedSensorTask
        TMLActivity duplicatedSensorTaskA = duplicatedSensorTask.getActivityDiagram();
        
        Map<TMLActivityElement, TMLActivityElement> replaceElem = new HashMap<TMLActivityElement, TMLActivityElement>();
        Map<TMLActivityElement, TMLActivityElement> changeElemNext = new HashMap<TMLActivityElement, TMLActivityElement>();
        Map<TMLActivityElement, TMLActivityElement> corrElemNext = new HashMap<TMLActivityElement, TMLActivityElement>();
        Vector<TMLActivityElement> duplElements = new Vector<TMLActivityElement>();
        for (int i=0; i < sensorTask.getActivityDiagram().getElements().size(); i++) {
            try {
                TMLActivityElement duplElement = sensorTask.getActivityDiagram().get(i).deepClone(_tmlm);
                duplElement.setReferenceObject(duplicatedSensorTaskA.getReferenceObject());
                duplElements.add(duplElement);
                corrElemNext.put(sensorTask.getActivityDiagram().get(i), duplElement);
            } catch (TMLCheckingError ex) {
                
            }
            
        }
        
        for (int i=0; i < sensorTask.getActivityDiagram().getElements().size(); i++) {
            for (int j=0; j < sensorTask.getActivityDiagram().get(i).getNbNext(); j++) {
                duplElements.elementAt(i).addNext(corrElemNext.get(sensorTask.getActivityDiagram().get(i).getNextElement(j)));
            }
        }
        
        for (int i=0; i < duplElements.size(); i++) {
            TMLActivityElement elemDuplSens = duplElements.elementAt(i);
            if (elemDuplSens instanceof TMLWriteChannel) {
                TMLWriteChannel elemDuplSensWr = (TMLWriteChannel) sensorTask.getActivityDiagram().get(i);
                if (elemDuplSensWr.getChannel(0).getDestinationTask() == receiverTask && channelsSensorReceiver.containsKey(elemDuplSensWr.getChannel(0))) {
                    TMLChannel linkCh = channelsSensorReceiver.get(elemDuplSensWr.getChannel(0));
                    TMLWriteChannel wrChannel = new TMLWriteChannel(linkCh.getName(), duplicatedSensorTaskA.getReferenceObject());
                    wrChannel.setNbOfSamples(elemDuplSensWr.getNbOfSamples());
                    wrChannel.addChannel(linkCh);
                    for (TMLActivityElement nextElem : elemDuplSens.getNexts()) {
                        wrChannel.addNext(nextElem);
                    }
                    duplicatedSensorTaskA.addElement(wrChannel);
                    replaceElem.put(elemDuplSens, wrChannel);
                } else {
                    TMLActivityElement elemDuplSensNext = elemDuplSens.getNextElement(0);
                    while(elemDuplSensNext instanceof TMLActivityElementEvent || (elemDuplSensNext instanceof TMLActivityElementChannel && ((TMLWriteChannel)elemDuplSensNext).getChannel(0).getDestinationTask() != receiverTask) || elemDuplSensNext instanceof TMLSendRequest) {
                        elemDuplSensNext = elemDuplSensNext.getNextElement(0);
                    }
                    changeElemNext.put(elemDuplSens, elemDuplSensNext);
                }
            } else if (elemDuplSens instanceof TMLActivityElementEvent || elemDuplSens instanceof TMLActivityElementChannel || elemDuplSens instanceof TMLSendRequest) {
                TMLActivityElement elemDuplSensNext = elemDuplSens.getNextElement(0);
                while(elemDuplSensNext instanceof TMLActivityElementEvent || (elemDuplSensNext instanceof TMLActivityElementChannel && ((TMLWriteChannel)elemDuplSensNext).getChannel(0).getDestinationTask() != receiverTask) || elemDuplSensNext instanceof TMLSendRequest) {
                    elemDuplSensNext = elemDuplSensNext.getNextElement(0);
                }
                changeElemNext.put(elemDuplSens, elemDuplSensNext);
            } else {
                duplicatedSensorTaskA.addElement(elemDuplSens);
            }
        }
        
        for (int i=0; i < duplicatedSensorTaskA.getElements().size(); i++) {
            for (int j=0; j < duplicatedSensorTaskA.get(i).getNbNext(); j++) {
                if (changeElemNext.containsKey(duplicatedSensorTaskA.get(i).getNextElement(j))) {
                    duplicatedSensorTaskA.get(i).setNewNext(duplicatedSensorTaskA.get(i).getNextElement(j), changeElemNext.get(duplicatedSensorTaskA.get(i).getNextElement(j)));
                }
                if (replaceElem.containsKey(duplicatedSensorTaskA.get(i).getNextElement(j))) {
                    duplicatedSensorTaskA.get(i).setNewNext(duplicatedSensorTaskA.get(i).getNextElement(j), replaceElem.get(duplicatedSensorTaskA.get(i).getNextElement(j)));
                }
            }
        }
        boolean findStart = false;
        for (int i=0; i < duplicatedSensorTaskA.getElements().size(); i++) {
            TMLActivityElement elemDuplSens = duplicatedSensorTaskA.get(i);
            if (elemDuplSens instanceof TMLStartState) {
                duplicatedSensorTaskA.setFirst(elemDuplSens);
                findStart = true;
                break;
            }
        }
        if (!findStart) {
            TMLStartState startDuplicatedSensor = new TMLStartState("start", duplicatedSensorTaskA.getReferenceObject());
            duplicatedSensorTaskA.setFirst(startDuplicatedSensor);
            for (int i=0; i < sensorTask.getActivityDiagram().getFirst().getNbNext(); i++) {
                TMLActivityElement nextStart = corrElemNext.get(sensorTask.getActivityDiagram().getFirst().getNextElement(i));
                if (changeElemNext.containsKey(nextStart)) {
                    nextStart = changeElemNext.get(nextStart);
                }
                if (replaceElem.containsKey(nextStart)) {
                    nextStart = changeElemNext.get(nextStart);
                }
                startDuplicatedSensor.addNext(nextStart);
            }
        } 

        for (int i=0; i < duplicatedSensorTaskA.getElements().size(); i++) {
            if (duplicatedSensorTaskA.get(i) instanceof TMLDelay) {
                TMLDelay delay = (TMLDelay)duplicatedSensorTaskA.get(i);
                delay.setMaxDelay("10");
                delay.setMinDelay("10");
                delay.setValue("10");
            }
        }

        // Update Architecture
        HwExecutionNode hwOfSensor = _tmap.getHwNodeOf(sensorTask);
        if (hwOfSensor != null) {
            _tmap.addTaskToHwExecutionNode(duplicatedSensorTask, hwOfSensor);
        }
        for (TMLChannel ch : _channelsBetweenSensorAndReceiver) { 
            for (HwCommunicationNode commNode : _tmap.getAllCommunicationNodesOfChannel(ch)) {
                _tmap.addCommToHwCommNode(channelsSensorReceiver.get(ch), commNode);
            }
        }
        
        return _tmap;
    }

    TMLMapping<?> tmrIntegration(TMLMapping<?> _tmap, List<TMLChannel> _channelsBetweenSensorsAndReceiver, List<String> _parameters) {
        if (_channelsBetweenSensorsAndReceiver.size() == 0) {
            TraceManager.addDev("No channels between sensors and receiver");
            return _tmap;
        }
        // Check if all channels have the same Destination Task (the same receiver)
        TMLTask receiverTask = _channelsBetweenSensorsAndReceiver.get(0).getDestinationTask();
        boolean _isSameDestTask = true;
        for (int i = 1 ; i < _channelsBetweenSensorsAndReceiver.size() ; i++) {
            if (_channelsBetweenSensorsAndReceiver.get(i).getDestinationTask() != receiverTask) {
                _isSameDestTask = false;
                break;
            }
        }
        if (!_isSameDestTask) {
            TraceManager.addDev("Sensors are not connected to the same receiver");
            return _tmap;
        }
        TMLModeling<?> _tmlm = _tmap.getTMLModeling();
        // List Sensors Tasks
        List<TMLTask> sensorsTasks = new ArrayList<TMLTask>();
        for (TMLChannel channelSensorReceiver : _channelsBetweenSensorsAndReceiver) {
            sensorsTasks.add(channelSensorReceiver.getOriginTask());
        }
        // Create an Interpreter Task for each sensor
        List<TMLTask> interpretersTasks = new ArrayList<TMLTask>();
        for (TMLChannel channelSensorReceiver : _channelsBetweenSensorsAndReceiver) {
            TMLTask sensorTask = channelSensorReceiver.getOriginTask();
            TMLTask interpreterTask = new TMLTask("Interpreter_" + channelSensorReceiver.getName(), receiverTask.getReferenceObject(), null);
            TMLAttribute calTime = new TMLAttribute("calculationTimeInterpreter", new TMLType(TMLType.NATURAL), _parameters.get(0));
            interpreterTask.addAttribute(calTime);
            _tmlm.addTask(interpreterTask);

            // Add attributes of Sensor to Interpreter
            interpreterTask.getAttributes().addAll(sensorTask.getAttributes());

            // set channel Destination (that was between Sensor and receiver) to Interpreter
            channelSensorReceiver.setDestinationTask(interpreterTask);
            channelSensorReceiver.setPorts(channelSensorReceiver.getOriginPort(), new TMLPort(channelSensorReceiver.getName(), channelSensorReceiver.getReferenceObject()));
            interpreterTask.addReadTMLChannel(channelSensorReceiver);

            // Create new channel between interpreter and voter
            TMLChannel channelInterpreterToVoter = new TMLChannel(channelSensorReceiver.getOriginPort().getName() + "_ChInterpreter", interpreterTask.getReferenceObject());
            channelInterpreterToVoter.setPorts(new TMLPort(channelInterpreterToVoter.getName(), channelInterpreterToVoter.getReferenceObject()), new TMLPort(channelInterpreterToVoter.getName(), channelInterpreterToVoter.getReferenceObject()));
            // set Interpreter as Origin of channelInterpreterToVoter
            channelInterpreterToVoter.setOriginTask(interpreterTask);
            channelInterpreterToVoter.setType(TMLChannel.BRBW);
            channelInterpreterToVoter.setSize(channelSensorReceiver.getSize());
            channelInterpreterToVoter.setMax(channelSensorReceiver.getMax());
            interpreterTask.addWriteTMLChannel(channelInterpreterToVoter);
            _tmlm.addChannel(channelInterpreterToVoter);

            // Create new event between interpreter and voter
            TMLEvent evtInterpreter = new TMLEvent(channelSensorReceiver.getOriginPort().getName() + "_EvtInterpreter", interpreterTask.getReferenceObject(), 1, true);
            evtInterpreter.setPorts(new TMLPort(evtInterpreter.getName(), evtInterpreter.getReferenceObject()), new TMLPort(evtInterpreter.getName(), evtInterpreter.getReferenceObject()));
            // set Interpreter as Origin of evtInterpreter
            evtInterpreter.setOriginTask(interpreterTask);
            interpreterTask.addTMLEvent(evtInterpreter);
            _tmlm.addEvent(evtInterpreter);

            //Build Activity Diagram for Interpreter
            TMLActivity interpreterA = interpreterTask.getActivityDiagram();
            TMLStartState startInterpreter = new TMLStartState("start", interpreterA.getReferenceObject());
            interpreterA.setFirst(startInterpreter);
            // Add New loop Forever
            TMLForLoop lForEverLoop = new TMLForLoop("loopForever", interpreterA.getReferenceObject());
            lForEverLoop.setInfinite(true);
            interpreterA.addElement(lForEverLoop);
            startInterpreter.addNext(lForEverLoop);
            
            // Add New Read Channel
            TMLReadChannel readChannel = new TMLReadChannel(channelSensorReceiver.getOriginPort().getName(), interpreterA.getReferenceObject());
            readChannel.setNbOfSamples("1");
            readChannel.addChannel(channelSensorReceiver);
            interpreterA.addElement(readChannel);
            lForEverLoop.addNext(readChannel);
            
            // Add New ExecC Opt
            TMLExecC execCOpt = new TMLExecC("execC", interpreterA.getReferenceObject());
            execCOpt.setValue(calTime.getName());
            execCOpt.setAction(calTime.getName());
            interpreterA.addElement(execCOpt);
            readChannel.addNext(execCOpt);

            // Add New Send Event
            TMLSendEvent sendEvt = new TMLSendEvent("evt_" + channelSensorReceiver.getOriginPort().getName(), interpreterA.getReferenceObject());
            sendEvt.setEvent(evtInterpreter);
            interpreterA.addElement(sendEvt);
            execCOpt.addNext(sendEvt);

            // Add New Write Channel
            TMLWriteChannel writeChInterpreter = new TMLWriteChannel("res_" + channelSensorReceiver.getOriginPort().getName(), interpreterA.getReferenceObject());
            writeChInterpreter.setNbOfSamples("1");
            writeChInterpreter.addChannel(channelInterpreterToVoter);
            interpreterA.addElement(writeChInterpreter);
            sendEvt.addNext(writeChInterpreter);

            //Add Stop
            TMLStopState stop = new TMLStopState("stop", interpreterA.getReferenceObject());
            interpreterA.addElement(stop);
            writeChInterpreter.addNext(stop);

            //Add Stop for LoopforEver
            TMLStopState stopLoopInterpreter = new TMLStopState("stop", interpreterA.getReferenceObject());
            interpreterA.addElement(stopLoopInterpreter);
            lForEverLoop.addNext(stopLoopInterpreter);

            interpretersTasks.add(interpreterTask);
        }

        // Add a Timer Task for Voter
        String prefixName = "";
        for (TMLTask sensorTask : sensorsTasks) {
            prefixName += sensorTask.getName() + "_";
        }
        //prefixName = prefixName.substring(0, prefixName.length() - 1);
        prefixName += _channelsBetweenSensorsAndReceiver.get(0).getName();
        String timerTaskName = "Timer_" + prefixName;
        TMLTask timerTask = new TMLTask(timerTaskName, receiverTask.getReferenceObject(), null);
        TMLAttribute timeOutReceivingData = new TMLAttribute("dataReceivingTimeout", new TMLType(TMLType.NATURAL), _parameters.get(1));
  
        timerTask.addAttribute(timeOutReceivingData);
        _tmlm.addTask(timerTask);
        
        // Create new Event evtTimeout
        TMLEvent evtTimer = new TMLEvent("evtTimeout_" + prefixName, timerTask.getReferenceObject(), 1, true);
        evtTimer.setPorts(new TMLPort(evtTimer.getName(), evtTimer.getReferenceObject()), new TMLPort(evtTimer.getName(), evtTimer.getReferenceObject()));
        // set Origin of Event to Timer 
        evtTimer.setOriginTask(timerTask); 
        timerTask.addTMLEvent(evtTimer);
        _tmlm.addEvent(evtTimer);

        // Build Activity Diagram For Timer
        TMLActivity timerA = timerTask.getActivityDiagram();
        TMLStartState startTimer = new TMLStartState("start", timerA.getReferenceObject());
        timerA.setFirst(startTimer);
        // Add new Loop Forever
        TMLForLoop lForEverLoopTimer = new TMLForLoop("foreverLoop", timerA.getReferenceObject());
        lForEverLoopTimer.setInfinite(true);
        timerA.addElement(lForEverLoopTimer);
        startTimer.addNext(lForEverLoopTimer);

        // Add new Delay
        TMLDelay delayTimeOut = new TMLDelay("delay", timerA.getReferenceObject());
        delayTimeOut.setActiveDelay(true);
        delayTimeOut.setMaxDelay(timeOutReceivingData.getName());
        delayTimeOut.setMinDelay(timeOutReceivingData.getName());
        delayTimeOut.setUnit("ns");
        timerA.addElement(delayTimeOut);
        lForEverLoopTimer.addNext(delayTimeOut);

        // Add new Send Event
        TMLSendEvent sendEventTimer = new TMLSendEvent("send" + evtTimer.getName(), timerA.getReferenceObject());
        sendEventTimer.setEvent(evtTimer);
        timerA.addElement(sendEventTimer);
        delayTimeOut.addNext(sendEventTimer);

        // Add new Stop
        TMLStopState stopTimer = new TMLStopState("stop", timerA.getReferenceObject());
        timerA.addElement(stopTimer);
        sendEventTimer.addNext(stopTimer);

        //Add Stop for LoopforEver
        TMLStopState stopLoopTimer = new TMLStopState("stop", timerA.getReferenceObject());
        timerA.addElement(stopLoopTimer);
        lForEverLoopTimer.addNext(stopLoopTimer);


        // Create Voter 
        String voterTaskName = "Voter_" + prefixName;
        TMLTask voterTask = new TMLTask(voterTaskName, receiverTask.getReferenceObject(), null);
        TMLAttribute computationTimeVoter = new TMLAttribute("computationTimeVoter",  new TMLType(TMLType.NATURAL), _parameters.get(2));
        voterTask.addAttribute(computationTimeVoter);
        TMLAttribute resVoter = new TMLAttribute("res_" + prefixName ,  new TMLType(TMLType.BOOLEAN));
        voterTask.addAttribute(resVoter);
        _tmlm.addTask(voterTask);
        
        // set evtTimer as Destination Event to Voter Task (from Timer)
        evtTimer.setDestinationTask(voterTask);
        voterTask.addTMLEvent(evtTimer);

        // set Destination Event and channel to Voter task (From interpreters)
        for (TMLTask interpreterTask : interpretersTasks) {
            // Destination event
            interpreterTask.getSendEvents().get(0).getEvent().setDestinationTask(voterTask);
            voterTask.addTMLEvent(interpreterTask.getSendEvents().get(0).getEvent());

            //Destination channel
            interpreterTask.getWriteChannels().get(0).getChannel(0).setDestinationTask(voterTask);
            voterTask.addReadTMLChannel(interpreterTask.getWriteChannels().get(0).getChannel(0));
            interpreterTask.getWriteChannels().get(0).getChannel(0).setPorts(new TMLPort(interpreterTask.getWriteChannels().get(0).getChannel(0).getName(), interpreterTask.getWriteChannels().get(0).getChannel(0).getReferenceObject()), new TMLPort(interpreterTask.getWriteChannels().get(0).getChannel(0).getName(), interpreterTask.getWriteChannels().get(0).getChannel(0).getReferenceObject()));
        }   

        //Create new Event moveto_FAIL_SAFE
        TMLEvent evtResVoter = new TMLEvent("moveto_FAIL_SAFE_" + prefixName, voterTask.getReferenceObject(), 1, true);
        evtResVoter.setPorts(new TMLPort(evtResVoter.getName(), evtResVoter.getReferenceObject()), new TMLPort(evtResVoter.getName(), evtResVoter.getReferenceObject()));
        evtResVoter.addParam(new TMLType(TMLType.BOOLEAN));
        // set Voter as Origin of evtResVoter
        evtResVoter.setOriginTask(voterTask); 
        voterTask.addTMLEvent(evtResVoter);
        _tmlm.addEvent(evtResVoter);

        // Create new Channel between Voter and Receiver Task
        TMLChannel channelResultVoterReceiver = new TMLChannel("result_" + prefixName, voterTask.getReferenceObject());
        channelResultVoterReceiver.setPorts(new TMLPort(channelResultVoterReceiver.getName(), channelResultVoterReceiver.getReferenceObject()), new TMLPort(channelResultVoterReceiver.getName(), channelResultVoterReceiver.getReferenceObject()));
        channelResultVoterReceiver.setType(TMLChannel.BRBW);
        channelResultVoterReceiver.setSize(_channelsBetweenSensorsAndReceiver.get(0).getSize());
        channelResultVoterReceiver.setMax(_channelsBetweenSensorsAndReceiver.get(0).getMax());
        
        // set Voter as Origin of channelResultVoterReceiver
        channelResultVoterReceiver.setOriginTask(voterTask); 
        voterTask.addWriteTMLChannel(channelResultVoterReceiver);
        _tmlm.addChannel(channelResultVoterReceiver);

        // Build Activity Diagram For Voter
        TMLActivity voterA = voterTask.getActivityDiagram();
        TMLStartState startVoter = new TMLStartState("start", voterA.getReferenceObject());
        voterA.setFirst(startVoter);
        // Add new Loop Forever
        TMLForLoop lForEverLoopVoter = new TMLForLoop("foreverLoop", voterA.getReferenceObject());
        lForEverLoopVoter.setInfinite(true);
        voterA.addElement(lForEverLoopVoter);
        startVoter.addNext(lForEverLoopVoter);

        // Add new Loop 
        TMLForLoop loopEvtsVoter = new TMLForLoop("loop", voterA.getReferenceObject());
        String counterEvtsFromInterpreters = "counter_" + prefixName;
        TMLAttribute counterEvtsFromInterpretersAttrib = new TMLAttribute(counterEvtsFromInterpreters,  new TMLType(TMLType.NATURAL), "0");
        voterTask.addAttribute(counterEvtsFromInterpretersAttrib);

        loopEvtsVoter.setInit(counterEvtsFromInterpreters + " = 0");
        loopEvtsVoter.setCondition(counterEvtsFromInterpreters + "<" + _channelsBetweenSensorsAndReceiver.size());
        loopEvtsVoter.setIncrement("");
        voterA.addElement(loopEvtsVoter);
        lForEverLoopVoter.addNext(loopEvtsVoter);


        // Add new Select Event
        TMLSelectEvt selectEvtVoter = new TMLSelectEvt("selectEvent", voterA.getReferenceObject());
        voterA.addElement(selectEvtVoter);
        loopEvtsVoter.addNext(selectEvtVoter);
        List<String> boolAttributesForEvtsName = new ArrayList<String>();
        for (TMLTask interpreterTask : interpretersTasks) {
            // Add new Wait event from an interpreter
            TMLWaitEvent waitEventFromInterpreter = new TMLWaitEvent("waitEventFromInterpreter", voterA.getReferenceObject());
            waitEventFromInterpreter.setEvent(interpreterTask.getEventSet().iterator().next());
            voterA.addElement(waitEventFromInterpreter);
            selectEvtVoter.addNext(waitEventFromInterpreter);

            // Add new Read Channel from an interpreter
            TMLReadChannel readChFromInterpreter = new TMLReadChannel("readChannelFromInterpreter", voterA.getReferenceObject());
            readChFromInterpreter.setNbOfSamples("1");
            readChFromInterpreter.addChannel(interpreterTask.getWriteChannels().get(0).getChannel(0));
            voterA.addElement(readChFromInterpreter);
            waitEventFromInterpreter.addNext(readChFromInterpreter);

            // Add new Choice
            TMLChoice choiceVoter = new TMLChoice("choice", voterA.getReferenceObject());

            TMLAttribute isEventAlreadyReceived = new TMLAttribute("isEvt"+ interpreterTask.getName()+"Received",  new TMLType(TMLType.BOOLEAN), "false");
            voterTask.addAttribute(isEventAlreadyReceived);

            choiceVoter.addGuard(isEventAlreadyReceived.getName()+" == false");
            voterA.addElement(choiceVoter);
            readChFromInterpreter.addNext(choiceVoter);

            // Add new Action State increment counter
            TMLActionState actionIncrementCounterVoter = new TMLActionState("actionState", voterA.getReferenceObject());
            actionIncrementCounterVoter.setAction(counterEvtsFromInterpreters + " = " + counterEvtsFromInterpreters + " + 1");
            voterA.addElement(actionIncrementCounterVoter);
            choiceVoter.addNext(actionIncrementCounterVoter);

            // Add new Action State put attrib isEventAlreadyReceived to True 
            TMLActionState actionFromAttribIsEventAlreadyReceived = new TMLActionState("actionState", voterA.getReferenceObject());
            actionFromAttribIsEventAlreadyReceived.setAction(isEventAlreadyReceived.getName() + " = true");
            boolAttributesForEvtsName.add(isEventAlreadyReceived.getName());
            voterA.addElement(actionFromAttribIsEventAlreadyReceived);
            actionIncrementCounterVoter.addNext(actionFromAttribIsEventAlreadyReceived);

            // Add new Stop
            TMLStopState stopVoter = new TMLStopState("stop", voterA.getReferenceObject());
            voterA.addElement(stopVoter);
            actionFromAttribIsEventAlreadyReceived.addNext(stopVoter);

        }
        
        // Add new Wait event from an Timer
        TMLWaitEvent waitEventFromTimer = new TMLWaitEvent("waitEventFromTimer", voterA.getReferenceObject());
        waitEventFromTimer.setEvent(evtTimer);
        voterA.addElement(waitEventFromTimer);
        selectEvtVoter.addNext(waitEventFromTimer);

        // Add new Action State change counter
        TMLActionState actionChangeCounterVoter = new TMLActionState("actionChangeCounterVoter", voterA.getReferenceObject());
        actionChangeCounterVoter.setAction(counterEvtsFromInterpreters + " = " + _channelsBetweenSensorsAndReceiver.size());
        voterA.addElement(actionChangeCounterVoter);
        waitEventFromTimer.addNext(actionChangeCounterVoter);

        // Add new Stop
        TMLStopState stopVoterAfterTimerEvt = new TMLStopState("stop", voterA.getReferenceObject());
        voterA.addElement(stopVoterAfterTimerEvt);
        actionChangeCounterVoter.addNext(stopVoterAfterTimerEvt);

        // Add new Sequence
        TMLSequence squenceVoter = new TMLSequence("sequence", voterA.getReferenceObject());
        voterA.addElement(squenceVoter);
        loopEvtsVoter.addNext(squenceVoter);

        for (String boolAttributesForEvt : boolAttributesForEvtsName) {
            // Add new Action State put attributes isEventAlreadyReceived to False 
            TMLActionState actionFromAttribIsEventAlreadyReceived = new TMLActionState("actionState" + boolAttributesForEvt, voterA.getReferenceObject());
            actionFromAttribIsEventAlreadyReceived.setAction(boolAttributesForEvt + " = false");
            voterA.getElements().lastElement().addNext(actionFromAttribIsEventAlreadyReceived);
            voterA.addElement(actionFromAttribIsEventAlreadyReceived);
        }

        // Add New ExecC Opt for Voter
        TMLExecC execCOptVoter = new TMLExecC("execC", voterA.getReferenceObject());
        execCOptVoter.setValue(computationTimeVoter.getName());
        execCOptVoter.setAction(computationTimeVoter.getName());
        voterA.getElements().lastElement().addNext(execCOptVoter);
        voterA.addElement(execCOptVoter);
        

        // Add New Choice for Voter
        TMLChoice choiceResVoter = new TMLChoice("choice", voterA.getReferenceObject());
        voterA.addElement(choiceResVoter);
        execCOptVoter.addNext(choiceResVoter);

        // Add new Action State put result to True 
        TMLActionState actionResAttribToTrue = new TMLActionState("actionResAttribToTrue", voterA.getReferenceObject());
        actionResAttribToTrue.setAction(resVoter.getName() + " = true");
        voterA.addElement(actionResAttribToTrue);
        choiceResVoter.addGuard("");
        choiceResVoter.addNext(actionResAttribToTrue);

        // Add new Stop
        TMLStopState stopAfterResTrueVoter = new TMLStopState("stop", voterA.getReferenceObject());
        voterA.addElement(stopAfterResTrueVoter);
        actionResAttribToTrue.addNext(stopAfterResTrueVoter);

        // Add new Action State put result to False 
        TMLActionState actionResAttribToFalse = new TMLActionState("actionResAttribToFalse", voterA.getReferenceObject());
        actionResAttribToFalse.setAction(resVoter.getName() + " = false");
        voterA.addElement(actionResAttribToFalse);
        choiceResVoter.addGuard("");
        choiceResVoter.addNext(actionResAttribToFalse);

        // Add new Stop
        TMLStopState stopAfterResFalseVoter = new TMLStopState("stop", voterA.getReferenceObject());
        voterA.addElement(stopAfterResFalseVoter);
        actionResAttribToFalse.addNext(stopAfterResFalseVoter);

        // Add new Send Event with res in Parameter
        TMLSendEvent sendEventResVoter = new TMLSendEvent("send" + evtResVoter.getName(), voterA.getReferenceObject());
        sendEventResVoter.addParam(resVoter.getName());
        sendEventResVoter.setEvent(evtResVoter);
        voterA.addElement(sendEventResVoter);
        squenceVoter.addNext(sendEventResVoter);

        // Add new Write Channel
        TMLWriteChannel writeChVoter = new TMLWriteChannel("write" + channelResultVoterReceiver.getName(), voterA.getReferenceObject());
        writeChVoter.setNbOfSamples("1");
        writeChVoter.addChannel(channelResultVoterReceiver);
        voterA.addElement(writeChVoter);
        sendEventResVoter.addNext(writeChVoter);

        // Add new Stop
        TMLStopState stopVoter = new TMLStopState("stop", voterA.getReferenceObject());
        voterA.addElement(stopVoter);
        writeChVoter.addNext(stopVoter);

        //Add Stop for LoopforEver
        TMLStopState stopLoopVoter = new TMLStopState("stop", voterA.getReferenceObject());
        voterA.addElement(stopLoopVoter);
        lForEverLoopVoter.addNext(stopLoopVoter);


        // Receiver Updates

        //Add Attribute
        receiverTask.addAttribute(resVoter);

        // Add new Read Channel to Receiver from Voter
        channelResultVoterReceiver.setDestinationTask(receiverTask);
        receiverTask.addReadTMLChannel(channelResultVoterReceiver);
        

        // Add new Wait Event to Receiver from Voter
        evtResVoter.setDestinationTask(receiverTask);
        receiverTask.addTMLEvent(evtResVoter);

    
        // Update Activity Diagram Receiver
        TMLActivity receiverA = receiverTask.getActivityDiagram();
        
        int index_ch = 0;
        List<TMLActivityElement> elemsToAdd = new ArrayList<TMLActivityElement>();
        List<TMLActivityElement> elemsToRemove = new ArrayList<TMLActivityElement>();
        for (TMLChannel ch : _channelsBetweenSensorsAndReceiver) {
            for (TMLActivityElement elemReceiverA : receiverA.getElements()) {
                if (elemReceiverA instanceof TMLReadChannel) {
                    TMLReadChannel readElemReceiverA = (TMLReadChannel) elemReceiverA;
                    if (readElemReceiverA.hasChannel(ch)) {
                        List <TMLActivityElement> nextElems = elemReceiverA.getNexts();
                        TMLActivityElement prevElem =  receiverA.getPrevious(elemReceiverA);
                        if (index_ch == 0) {
                            // Replace the channel from the first sensor connected to the receiver into the result Channel from Voter 
  
                            // Add new Wait event to Receiver from Voter
                            TMLWaitEvent waitEventFromVoter = new TMLWaitEvent("waitEventFromVoter", receiverA.getReferenceObject());
                            waitEventFromVoter.setEvent(evtResVoter);
                            waitEventFromVoter.addParam(resVoter.getName());
                            prevElem.setNewNext(elemReceiverA, waitEventFromVoter);
                            elemsToAdd.add(waitEventFromVoter);
                            
                            // Replace the Read Channel of Receiver to the channel from Voter
                            readElemReceiverA.replaceChannelWith(ch, channelResultVoterReceiver);
                            waitEventFromVoter.addNext(readElemReceiverA);
                        } else {
                            // Delete channels connected to the receiver from the other sensors
                            prevElem.removeNext(elemReceiverA);
                            for (TMLActivityElement nextElem : nextElems) {
                                prevElem.addNext(nextElem);
                            }
                            elemsToRemove.add(readElemReceiverA);
                        }
                    }
                }
            }
            index_ch += 1;
        }
        for (TMLActivityElement elemToAdd : elemsToAdd) {
            receiverA.addElement(elemToAdd);
        }
        for (TMLActivityElement elemToRemove : elemsToRemove) {
            receiverA.removeElement(elemToRemove);
        }
        
        // Remove from receiver channels that come from sensors
        for (TMLChannel ch : receiverTask.getReadTMLChannels()) {
            if (_channelsBetweenSensorsAndReceiver.contains(ch)) {
                receiverTask.getReadTMLChannels().remove(ch);
            }
        }

        // Update Architecture 
        TMLArchitecture _tmlarch =  _tmap.getArch();
        // New CPU for the Voter and interpreters Tasks 
        HwCPU _cpuVoter = new HwCPU("CPU_" + voterTaskName);
        // New Memory for the Voter and interpreters Channels 
        HwMemory _memoryVoter = new HwMemory("Memory_" + voterTaskName);
        // New Bridge for the Voter and interpreters Channels 
        HwBridge _bridgeVoter = new HwBridge("Brigde_" + voterTaskName);
        // New Bus for the Voter and interpreters Channels 
        HwBus _busVoter = new HwBus("Bus_" + voterTaskName);

        //Update Memory Voter properties
        HwMemory memoryOfChannelSensor = _tmap.getMemoryOfChannel(_channelsBetweenSensorsAndReceiver.get(0));
        if (memoryOfChannelSensor != null) {
            _memoryVoter.byteDataSize = memoryOfChannelSensor.byteDataSize;
            _memoryVoter.clockRatio = memoryOfChannelSensor.clockRatio;
            _memoryVoter.bufferType = memoryOfChannelSensor.bufferType;
        }
        HwExecutionNode hwOfReceiver = _tmap.getHwNodeOf(receiverTask);
        if (hwOfReceiver != null) {
            if (hwOfReceiver instanceof HwCPU) {
                HwCPU cpuOfReceiver = (HwCPU) hwOfReceiver;
                //Update CPU Voter properties
                _cpuVoter.byteDataSize = cpuOfReceiver.byteDataSize;
                _cpuVoter.pipelineSize = cpuOfReceiver.pipelineSize;
                _cpuVoter.goIdleTime = cpuOfReceiver.goIdleTime;
                _cpuVoter.taskSwitchingTime = cpuOfReceiver.taskSwitchingTime;
                _cpuVoter.branchingPredictionPenalty = cpuOfReceiver.branchingPredictionPenalty;
                _cpuVoter.execiTime = cpuOfReceiver.execiTime;
                HwLink linkBridgeVoterBusReceiver = new HwLink("link");
                for (HwLink link : _tmlarch.getHwLinks()) {
                    if (link.hwnode == cpuOfReceiver) {
                        HwBus busOfReceiver = link.bus;
                        // Add link BridgeVoter and BusReceiver
                        linkBridgeVoterBusReceiver = new HwLink("link_" + _bridgeVoter.getName() + "_to_" + busOfReceiver.getName());
                        linkBridgeVoterBusReceiver.bus = busOfReceiver;
                        linkBridgeVoterBusReceiver.hwnode = _bridgeVoter;
                        
                    }
                }
                if (linkBridgeVoterBusReceiver.bus != null) {
                    _tmlarch.addHwLink(linkBridgeVoterBusReceiver);
                }
            }
        }
        // Add New HWs to the architecture
        _tmlarch.addHwNode(_cpuVoter);
        _tmlarch.addHwNode(_memoryVoter);
        _tmlarch.addHwNode(_bridgeVoter);
        _tmlarch.addHwNode(_busVoter);
        
        // Add link CPUVoter and BusVoter
        HwLink linkCPUBusVoter = new HwLink("link_" + _cpuVoter.getName() + "_to_" + _busVoter.getName());
        linkCPUBusVoter.bus = _busVoter;
        linkCPUBusVoter.hwnode = _cpuVoter;
        _tmlarch.addHwLink(linkCPUBusVoter);
        // Add link MemoryVoter and BusVoter
        HwLink linkMemoryBusVoter = new HwLink("link_" + _memoryVoter.getName() + "_to_" + _busVoter.getName());
        linkMemoryBusVoter.bus = _busVoter;
        linkMemoryBusVoter.hwnode = _memoryVoter;
        _tmlarch.addHwLink(linkMemoryBusVoter);
        // Add link BridgeVoter and BusVoter
        HwLink linkBridgeBusVoter = new HwLink("link_" + _bridgeVoter.getName() + "_to_" + _busVoter.getName());
        linkBridgeBusVoter.bus = _busVoter;
        linkBridgeBusVoter.hwnode = _bridgeVoter;
        _tmlarch.addHwLink(linkBridgeBusVoter);
        
        // Mapping Voter, Timer and interpreters Tasks To CPUVoter
        _tmap.addTaskToHwExecutionNode(voterTask, _cpuVoter);
        _tmap.addTaskToHwExecutionNode(timerTask, _cpuVoter);
        
        for (TMLTask interpreterTask : interpretersTasks) {
            _tmap.addTaskToHwExecutionNode(interpreterTask, _cpuVoter);
        }
        
        // Mapping Voter and interpreters Channels To MemoryVoter
        for (TMLTask interpreterTask : interpretersTasks) {
            for (TMLChannel writeChannelInterpreter : interpreterTask.getWriteTMLChannels()) {
                _tmap.addCommToHwCommNode(writeChannelInterpreter, _memoryVoter);
                _tmap.addCommToHwCommNode(writeChannelInterpreter, _busVoter);
                for (HwCommunicationNode mappedNode : _tmap.getAllCommunicationNodesOfChannel(_channelsBetweenSensorsAndReceiver.get(0))) {
                    if (mappedNode instanceof HwBus) {
                        _tmap.addCommToHwCommNode(writeChannelInterpreter, mappedNode);
                    }   
                }
            }
        }
        
        _tmap.addCommToHwCommNode(channelResultVoterReceiver, _memoryVoter);
        _tmap.addCommToHwCommNode(channelResultVoterReceiver, _busVoter);
        for (HwCommunicationNode mappedNode : _tmap.getAllCommunicationNodesOfChannel(_channelsBetweenSensorsAndReceiver.get(0))) {
            if (mappedNode instanceof HwBus) {
                _tmap.addCommToHwCommNode(channelResultVoterReceiver, mappedNode);
            }   
        }
        
        return _tmap;
    }
}
