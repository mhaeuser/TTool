package ui;
/**
 * Class TMRGeneration
 * TMR Generation in separate thread
 * Creation: 26/07/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 26/07/2023
 */
 
 import avatartranslator.*;
import myutil.BoolExpressionEvaluator;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.TraceManager;

import tmltranslator.*;
import tmltranslator.toavatarsec.TML2Avatar;

import ui.tmlad.*;
import ui.tmlcd.TMLTaskDiagramPanel;
import ui.tmlcd.TMLTaskOperator;
import ui.tmlcompd.*;
import ui.tmlcp.TMLCPPanel;
import ui.tmldd.*;
import common.ConfigurationTTool;

import ui.tmlsd.TMLSDPanel;

import java.awt.Point;

import java.util.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
public class TMRGeneration implements Runnable {
	MainGUI gui;
	List<String> selectedSensorsTasks;
	String selectedRecieverTask;
	TMLMapping<TGComponent> tmap;
	
	public TMRGeneration(MainGUI gui, List<String> selectedSensorsTasks, String selectedRecieverTask, TMLMapping<TGComponent> tmap) {
		this.gui = gui;
		this.selectedSensorsTasks = selectedSensorsTasks;
		this.selectedRecieverTask = selectedRecieverTask;
		this.tmap = tmap;
	}
    
	public void startThread() {
		Thread t = new Thread(this);
		t.start();
		try {
			t.join();
		}
		catch (Exception e) {
			TraceManager.addDev("Error in TMR Generation Thread");
		}
		return;
	}
    
    public void run() {
    	Map<String, Integer> channelIndexMap = new HashMap<String, Integer>();
    	int channelIndex=0;
    	TraceManager.addDev("Adding TMR");
        if (tmap == null) {
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        String dateAndTime = dateFormat.format(date);
        String name = "tmr"+dateAndTime;
        //Clone diagrams
        TURTLEPanel tmlap = tmap.getCorrespondanceList().getTG(tmap.getArch().getFirstCPU()).getTDiagramPanel().tp;
        int arch = gui.tabs.indexOf(tmlap);
        gui.cloneRenameTab(arch, name);
        TMLArchiPanel newarch = (TMLArchiPanel) gui.tabs.get(gui.tabs.size() - 1);

        TGComponent tgcomp = tmap.getTMLModeling().getTGComponent();
        TMLComponentDesignPanel tmlcdp = (TMLComponentDesignPanel) tgcomp.getTDiagramPanel().tp;

        int ind = gui.tabs.indexOf(tmlcdp);
        String tabName = gui.getTitleAt(tmlcdp);
        gui.cloneRenameTab(ind, name);
        TMLComponentDesignPanel t = (TMLComponentDesignPanel) gui.tabs.get(gui.tabs.size() - 1);
        TMLComponentTaskDiagramPanel tcdp = t.tmlctdp;
        //Create clone of architecture panel and map tasks to it
        newarch.renameMapping(tabName, tabName + "_" + name);

        TGConnector fromStart;
        //Find receiver component
        TMLCPrimitiveComponent receiverComponent = tcdp.getPrimitiveComponentByName(selectedRecieverTask);
        //Create an Interpreter for each selected sensor on the component diagram
        for (String selectedSensorName : selectedSensorsTasks) {
            TMLCPrimitiveComponent interpreterComponent = new TMLCPrimitiveComponent(0, 500, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
            TAttribute calTime = new TAttribute(2, "calculationTimeInterpreter", "10", 0);
            interpreterComponent.getAttributeList().add(calTime);
            tcdp.addComponent(interpreterComponent, 0, 500, false, true);
            interpreterComponent.setValueWithChange("Interpreter_" + selectedSensorName);
            //Find sensor component
            TMLCPrimitiveComponent sensorComponent = tcdp.getPrimitiveComponentByName(selectedSensorName);
            //Find connectors between Receiver and  Sensor
            List<TGConnector> portConnectorsSensorReceiverList = new ArrayList<TGConnector>();
            for (TMLCPrimitivePort originPort: sensorComponent.getAllChannelsOriginPorts()) {
                TraceManager.addDev("originPort: " + originPort.getPortName());
                for (TGConnector connector : originPort.getConnectors()){
                    TraceManager.addDev("connector: " + connector.getName());
                    TraceManager.addDev("connector getTGComponent1: " + connector.getTGComponent1().getName());
                    TraceManager.addDev("connector getTGComponent1.getFather(): " + connector.getTGComponent1().getFather().getName());
                    TraceManager.addDev("connector getTGComponent2: " + connector.getTGComponent2().getName());
                    TraceManager.addDev("connector getTGComponent2.getFather(): " + connector.getTGComponent2().getFather().getName());
                    if (connector.getTGComponent1().getFather().equals(receiverComponent) && connector.getTGComponent2().getFather().equals(sensorComponent) || connector.getTGComponent1().getFather().equals(sensorComponent) && connector.getTGComponent2().getFather().equals(receiverComponent)) {
                        portConnectorsSensorReceiverList.add(connector);
                    }
                }
            }
            // Add attributes of Sensor to Interpreter
            interpreterComponent.getAttributeList().addAll(sensorComponent.getAttributeList());
            // Add new input channel ports, output channel ports and output event ports to Interpreter 
            for (TGConnector connecSensorReceiver: portConnectorsSensorReceiverList) {
                TMLCChannelOutPort originChPortInterpreter = new TMLCChannelOutPort(sensorComponent.getX(), sensorComponent.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, interpreterComponent, tcdp);
                TMLCChannelOutPort destChPortInterpreter = new TMLCChannelOutPort(sensorComponent.getX(), sensorComponent.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, interpreterComponent, tcdp);
                TMLCChannelOutPort destEvtPortInterpreter = new TMLCChannelOutPort(sensorComponent.getX(), sensorComponent.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, interpreterComponent, tcdp);

                originChPortInterpreter.commName = ((TMLCPrimitivePort) connecSensorReceiver.getTGComponent1()).getPortName();
                originChPortInterpreter.isOrigin = false;
                originChPortInterpreter.typep = 0;
                interpreterComponent.addSwallowedTGComponent(originChPortInterpreter, 0, 0);

                destChPortInterpreter.commName = ((TMLCPrimitivePort) connecSensorReceiver.getTGComponent1()).getPortName()+"_ChInterpreter";
                destChPortInterpreter.isOrigin = true;
                destChPortInterpreter.typep = 0;
                interpreterComponent.addSwallowedTGComponent(destChPortInterpreter, 0, 0);

                destEvtPortInterpreter.commName = ((TMLCPrimitivePort) connecSensorReceiver.getTGComponent1()).getPortName()+"_EvtInterpreter";
                destEvtPortInterpreter.isOrigin = true;
                destEvtPortInterpreter.typep = 1;
                interpreterComponent.addSwallowedTGComponent(destEvtPortInterpreter, 0, 0);

            }

            //Build Activity Diagram for Interpreter
            TMLActivityDiagramPanel interpreterAD = t.getTMLActivityDiagramPanel("Interpreter_" + selectedSensorName);
            TMLADStartState start = (TMLADStartState) interpreterAD.getComponentList().get(0);
            int xpos = start.getX();
            int ypos = start.getY();
            fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
            xpos += 10;

            // Add New loop Forever
            TMLADForEverLoop lForEverLoop = new TMLADForEverLoop(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
            interpreterAD.addComponent(lForEverLoop, 300, 200, false, true);
            //Connect start and forEverLoop
            fromStart.setP1(start.getTGConnectingPointAtIndex(0));
            fromStart.setP2(lForEverLoop.getTGConnectingPointAtIndex(0));
            interpreterAD.addComponent(fromStart, 300, 200, false, true);
            if (portConnectorsSensorReceiverList.size() == 1) {
                // Add New Read Channel
                TMLADReadChannel readChannel = new TMLADReadChannel(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                readChannel.setChannelName(((TMLCPrimitivePort) portConnectorsSensorReceiverList.get(0).getTGComponent1()).getPortName());
                interpreterAD.addComponent(readChannel, 300, 200, false, true);
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                //Connect forEverLoop and readChannel
                fromStart.setP1(lForEverLoop.getTGConnectingPointAtIndex(1));
                fromStart.setP2(readChannel.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);

                // Add New ExecC Opt
                TMLADExecC execCOpt = new TMLADExecC(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                execCOpt.setDelayValue(calTime.getName());
                interpreterAD.addComponent(execCOpt, 300, 200, false, true);
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                //Connect readChannel and execCOpt
                fromStart.setP1(readChannel.getTGConnectingPointAtIndex(1));
                fromStart.setP2(execCOpt.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);

                // Add New Send Event
                TMLADSendEvent sendEvt = new TMLADSendEvent(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                sendEvt.setEventName("evt_" + ((TMLCPrimitivePort) portConnectorsSensorReceiverList.get(0).getTGComponent1()).getPortName());
                interpreterAD.addComponent(sendEvt, 300, 200, false, true);
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                //Connect execCOpt and sendEvent 
                fromStart.setP1(execCOpt.getTGConnectingPointAtIndex(1));
                fromStart.setP2(sendEvt.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);

                // Add New Write Channel
                TMLADWriteChannel writeChInterpreter = new TMLADWriteChannel(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                writeChInterpreter.setChannelName("res_" + ((TMLCPrimitivePort) portConnectorsSensorReceiverList.get(0).getTGComponent1()).getPortName());
                interpreterAD.addComponent(writeChInterpreter, 300, 200, false, true);
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                //Connect sendEvent and write Channel
                fromStart.setP1(sendEvt.getTGConnectingPointAtIndex(1));
                fromStart.setP2(writeChInterpreter.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);

                //Add Stop
                TMLADStopState stop = new TMLADStopState(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                interpreterAD.addComponent(stop, 300, 200, false, true);

                //Connect write Channel and stop
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                fromStart.setP1(writeChInterpreter.getTGConnectingPointAtIndex(1));
                fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);

                interpreterAD.repaint();
                
            } else if (portConnectorsSensorReceiverList.size() > 1) {
                // Add new Sequence
                TMLADSequence sequence = new TMLADSequence(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                interpreterAD.addComponent(sequence, 300, 200, false, true);
                fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                xpos += 10;
                //Connect forEverLoop and sequence
                fromStart.setP1(lForEverLoop.getTGConnectingPointAtIndex(1));
                fromStart.setP2(sequence.getTGConnectingPointAtIndex(0));
                interpreterAD.addComponent(fromStart, 300, 200, false, true);
                
                int indexConnector = 0;
                for (TGConnector connector : portConnectorsSensorReceiverList) {

                    // Add New Read Channel
                    TMLADReadChannel readChannel = new TMLADReadChannel(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                    readChannel.setChannelName(((TMLCPrimitivePort) connector.getTGComponent1()).getPortName());
                    interpreterAD.addComponent(readChannel, 300, 200, false, true);
                    fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                    xpos += 10;
                    //Connect Sequence and readChannel
                    fromStart.setP1(sequence.getTGConnectingPointAtIndex(indexConnector+1));
                    fromStart.setP2(readChannel.getTGConnectingPointAtIndex(0));
                    interpreterAD.addComponent(fromStart, 300, 200, false, true);

                    // Add New ExecC Opt
                    TMLADExecC execCOpt = new TMLADExecC(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                    execCOpt.setDelayValue(calTime.getName());
                    interpreterAD.addComponent(execCOpt, 300, 200, false, true);
                    fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                    xpos += 10;
                    //Connect readChannel and execCOpt
                    fromStart.setP1(readChannel.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(execCOpt.getTGConnectingPointAtIndex(0));
                    interpreterAD.addComponent(fromStart, 300, 200, false, true);

                    // Add New Send Event
                    TMLADSendEvent sendEvt = new TMLADSendEvent(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                    sendEvt.setEventName("evt_" + ((TMLCPrimitivePort) connector.getTGComponent1()).getPortName());
                    interpreterAD.addComponent(sendEvt, 300, 200, false, true);
                    fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                    xpos += 10;
                    //Connect execCOpt and sendEvent 
                    fromStart.setP1(execCOpt.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(sendEvt.getTGConnectingPointAtIndex(0));
                    interpreterAD.addComponent(fromStart, 300, 200, false, true);

                    // Add New Write Channel
                    TMLADWriteChannel writeChInterpreter = new TMLADWriteChannel(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                    writeChInterpreter.setChannelName("res_" + ((TMLCPrimitivePort) connector.getTGComponent1()).getPortName());
                    interpreterAD.addComponent(writeChInterpreter, 300, 200, false, true);
                    fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                    xpos += 10;
                    //Connect sendEvent and write Channel
                    fromStart.setP1(sendEvt.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(writeChInterpreter.getTGConnectingPointAtIndex(0));
                    interpreterAD.addComponent(fromStart, 300, 200, false, true);

                    //Add Stop
                    TMLADStopState stop = new TMLADStopState(300, 100, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD);
                    interpreterAD.addComponent(stop, 300, 200, false, true);

                    //Connect write Channel and stop
                    fromStart = new TGConnectorTMLAD(xpos, ypos, interpreterAD.getMinX(), interpreterAD.getMaxX(), interpreterAD.getMinY(), interpreterAD.getMaxY(), false, null, interpreterAD, null, null, new Vector<Point>());
                    xpos += 10;
                    fromStart.setP1(writeChInterpreter.getTGConnectingPointAtIndex(1));
                    fromStart.setP2(stop.getTGConnectingPointAtIndex(0));
                    interpreterAD.addComponent(fromStart, 300, 200, false, true);

                    indexConnector += 1;
                }
                interpreterAD.repaint();
            }
        }
        // Add Timer Component for Voter
        TMLCPrimitiveComponent timerComponent = new TMLCPrimitiveComponent(0, 500, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
        TAttribute timeOutReceivingData = new TAttribute(2, "dataReceivingTimeout", "10", 0);
        timerComponent.getAttributeList().add(timeOutReceivingData);
        tcdp.addComponent(timerComponent, 0, 500, false, true);
        String prefixName = "";
        for (String selectedSensorName : selectedSensorsTasks) {
            prefixName += selectedSensorName + "_";
        }
        prefixName = prefixName.substring(0, prefixName.length() - 1);
        String timerComponentName = "Timer_" + prefixName;
        timerComponent.setValueWithChange(timerComponentName);
        // Add Origin Event port in Timer Component
        TMLCChannelOutPort sendEvtPortTimer = new TMLCChannelOutPort(receiverComponent.getX(), receiverComponent.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, timerComponent, tcdp);
        sendEvtPortTimer.commName = "evtTimeout_" + prefixName;
        sendEvtPortTimer.isOrigin = true;
        sendEvtPortTimer.typep = 1;
        timerComponent.addSwallowedTGComponent(sendEvtPortTimer, 0, 0);

        // Build Activity Diagram For Timer
        TMLActivityDiagramPanel timerAD = t.getTMLActivityDiagramPanel(timerComponentName);
        TMLADStartState startTimer = (TMLADStartState) timerAD.getComponentList().get(0);
        int xposTimer = startTimer.getX();
        int yposTimer = startTimer.getY();
        fromStart = new TGConnectorTMLAD(xposTimer, yposTimer, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD, null, null, new Vector<Point>());
        xposTimer += 10;
        // Add new Loop Forever
        TMLADForEverLoop lForEverLoopTimer = new TMLADForEverLoop(300, 100, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD);
        timerAD.addComponent(lForEverLoopTimer, 300, 200, false, true);
        //Connect start and forEverLoop
        fromStart.setP1(startTimer.getTGConnectingPointAtIndex(0));
        fromStart.setP2(lForEverLoopTimer.getTGConnectingPointAtIndex(0));
        timerAD.addComponent(fromStart, 300, 200, false, true);
        
        // Add new Delay
        TMLADDelay delayTimeOut = new TMLADDelay(300, 100, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD);
        delayTimeOut.setDelayValue(timeOutReceivingData.getName());
        delayTimeOut.setUnit("ns");
        timerAD.addComponent(delayTimeOut, 300, 200, false, true);
        fromStart = new TGConnectorTMLAD(xposTimer, yposTimer, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD, null, null, new Vector<Point>());
        xposTimer += 10;
        //Connect forEverLoop and delayTimeOut
        fromStart.setP1(lForEverLoopTimer.getTGConnectingPointAtIndex(1));
        fromStart.setP2(delayTimeOut.getTGConnectingPointAtIndex(0));
        timerAD.addComponent(fromStart, 300, 200, false, true);

        // Add new Send Event
        TMLADSendEvent sendEventTimer = new TMLADSendEvent(300, 100, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD);
        sendEventTimer.setEventName(timeOutReceivingData.getName());
        timerAD.addComponent(sendEventTimer, 300, 200, false, true);
        fromStart = new TGConnectorTMLAD(xposTimer, yposTimer, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD, null, null, new Vector<Point>());
        xposTimer += 10;
        //Connect delayTime and sendEvent
        fromStart.setP1(delayTimeOut.getTGConnectingPointAtIndex(1));
        fromStart.setP2(sendEventTimer.getTGConnectingPointAtIndex(0));
        timerAD.addComponent(fromStart, 300, 200, false, true);

        // Add new Stop
        TMLADStopState stopTimer = new TMLADStopState(300, 100, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD);
        fromStart = new TGConnectorTMLAD(xposTimer, yposTimer, timerAD.getMinX(), timerAD.getMaxX(), timerAD.getMinY(), timerAD.getMaxY(), false, null, timerAD, null, null, new Vector<Point>());
        xposTimer += 10;
        timerAD.addComponent(stopTimer, 300, 200, false, true);
        //Connect sendEvent and stop
        fromStart.setP1(sendEventTimer.getTGConnectingPointAtIndex(1));
        fromStart.setP2(stopTimer.getTGConnectingPointAtIndex(0));
        timerAD.addComponent(fromStart, 300, 200, false, true);

        timerAD.repaint();
        
        // Create Voter 
        TMLCPrimitiveComponent voterComponent = new TMLCPrimitiveComponent(0, 500, tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxY(), false, null, tcdp);
        TAttribute computationTimeVoter = new TAttribute(2, "computationTimeVoter", "10", 0);
        voterComponent.getAttributeList().add(computationTimeVoter);
        tcdp.addComponent(voterComponent, 0, 500, false, true);
        String voterComponentName = "Voter_" + prefixName;
        voterComponent.setValueWithChange(voterComponentName);
        // Add Destination Event port in Voter Component (from Timer)
        TMLCChannelOutPort waitEvtPortFromTimer = new TMLCChannelOutPort(receiverComponent.getX(), receiverComponent.getY(), tcdp.getMinX(), tcdp.getMaxX(), tcdp.getMinY(), tcdp.getMaxX(), true, voterComponent, tcdp);
        waitEvtPortFromTimer.commName = "evtTimeout_" + prefixName;
        waitEvtPortFromTimer.isOrigin = false;
        waitEvtPortFromTimer.typep = 1;
        voterComponent.addSwallowedTGComponent(waitEvtPortFromTimer, 0, 0);


	} 
}
