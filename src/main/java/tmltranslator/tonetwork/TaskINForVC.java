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


package tmltranslator.tonetwork;

import tmltranslator.*;

import java.util.Vector;


/**
 * Class TaskINForVC
 * Creation: 08/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 08/01/2019
 */
public class TaskINForVC extends TMLTask {

    public TaskINForVC(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
    }

    // Output Channels are given in the order of VCs

    public void generate(TMLEvent inPacketEvent, Vector<TMLEvent> inFeedbackEvents, TMLChannel inChannel,
                         TMLEvent outFeedbackEvent, Vector<TMLEvent> outVCEvents) {

        TMLSendEvent sendEvt;
        TMLStopState stop;

        // Attributes
        TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(pktlen);
        TMLAttribute dst = new TMLAttribute("dst", "dst", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dst);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);
        TMLAttribute feedbackDownstr = new TMLAttribute("feedbackDownstr", "feedbackDownstr", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(feedbackDownstr);
        TMLAttribute bufferSize = new TMLAttribute("bufferSize", "bufferSize", new TMLType(TMLType.NATURAL), "2");
        this.addAttribute(bufferSize);
        TMLAttribute requestedOutput = new TMLAttribute("requestedOutput", "requestedOutput", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(requestedOutput);
        TMLAttribute j = new TMLAttribute("j", "j", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(j);

        // Events and channels
        addTMLEvent(inPacketEvent);
        for(TMLEvent evt: inFeedbackEvents) {
            addTMLEvent(evt);
        }
        addReadTMLChannel(inChannel);
        addTMLEvent(outFeedbackEvent);
        for(TMLEvent evt: outVCEvents) {
            addTMLEvent(evt);
        }

        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        // Main Sequence
        TMLSequence seq = new TMLSequence("mainSequence", referenceObject);
        activity.addLinkElement(start, seq);

        // Loop at the left of the sequence
        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInit("j=0");
        loop.setCondition("j<bufferSize");
        loop.setIncrement("j=j+1");
        activity.addElement(loop);
        seq.addNext(loop);

        sendEvt = new TMLSendEvent("outFeedbackEvent", referenceObject);
        sendEvt.setEvent(outFeedbackEvent);
        activity.addElement(sendEvt);
        loop.addNext(sendEvt);

        stop = new TMLStopState("StopStateInLoop", referenceObject);
        activity.addElement(stop);
        sendEvt.addNext(stop);

        stop = new TMLStopState("StopStateAfterLoop", referenceObject);
        activity.addElement(stop);
        loop.addNext(stop);

        // Second activity after sequence
        TMLForLoop loop2 = new TMLForLoop("mainLoop", referenceObject);
        loop2.setInfinite(true);
        activity.addElement(loop2);
        seq.addNext(loop2);

        TMLWaitEvent waitEvt = new TMLWaitEvent("PacketEvent", referenceObject);
        waitEvt.setEvent(inPacketEvent);
        waitEvt.addParam("pktlen");
        waitEvt.addParam("dst");
        waitEvt.addParam("vc");
        waitEvt.addParam("eop");
        activity.addLinkElement(loop2, waitEvt);

        TMLActionState reqOut = new TMLActionState("ReqOut", referenceObject);
        reqOut.setAction("requestedOutput = dst");
        activity.addLinkElement(waitEvt, reqOut);


        // Main choice
        TMLChoice mainChoice = new TMLChoice("mainChoice", referenceObject);
        activity.addLinkElement(reqOut, mainChoice);

        // Each link to an output for a given packet
        for(int i=0;  i<outVCEvents.size(); i++) {
            TMLForLoop packetLoop = new TMLForLoop("packetLoop", referenceObject);
            packetLoop.setInit("j=0");
            packetLoop.setCondition("j<pktlen-1");
            packetLoop.setIncrement("j=j+1");
            activity.addLinkElement(mainChoice, packetLoop);
            mainChoice.addGuard("requestedOutput == " + i);

            // Inside packetloop
            sendEvt = new TMLSendEvent("info on packet", referenceObject);
            sendEvt.setEvent(outVCEvents.get(i));
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dst");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            activity.addLinkElement(packetLoop, sendEvt);

            waitEvt = new TMLWaitEvent("FeedbackDownEvent", referenceObject);
            waitEvt.setEvent(inFeedbackEvents.get(i));
            activity.addLinkElement(sendEvt, waitEvt);

            sendEvt = new TMLSendEvent("feedbackUpEvent", referenceObject);
            sendEvt.setEvent(outFeedbackEvent);
            activity.addLinkElement(waitEvt, sendEvt);

            TMLReadChannel read = new TMLReadChannel("ReadFlit" + i, referenceObject);
            read.addChannel(inChannel);
            read.setNbOfSamples("1");
            activity.addLinkElement(sendEvt, read);

            waitEvt = new TMLWaitEvent("PacketEventInLoop", referenceObject);
            waitEvt.setEvent(inPacketEvent);
            waitEvt.addParam("pktlen");
            waitEvt.addParam("dst");
            waitEvt.addParam("vc");
            waitEvt.addParam("eop");
            activity.addLinkElement(read, waitEvt);

            stop = new TMLStopState("StopStateInLoop", referenceObject);
            activity.addLinkElement(waitEvt, stop);

            // After packetloop
            sendEvt = new TMLSendEvent("infoOnPacketO", referenceObject);
            sendEvt.setEvent(outVCEvents.get(i));
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dst");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            activity.addLinkElement(loop, sendEvt);

            waitEvt = new TMLWaitEvent("FeedbackDownEventO", referenceObject);
            waitEvt.setEvent(inFeedbackEvents.get(i));
            activity.addLinkElement(sendEvt, waitEvt);

            sendEvt = new TMLSendEvent("feedbackUpEventO", referenceObject);
            sendEvt.setEvent(outFeedbackEvent);
            activity.addLinkElement(waitEvt, sendEvt);

            read = new TMLReadChannel("ReadFlitO" + i, referenceObject);
            read.addChannel(inChannel);
            read.setNbOfSamples("1");
            activity.addLinkElement(sendEvt, read);

            stop = new TMLStopState("StopStateOutLoop", referenceObject);
            activity.addLinkElement(waitEvt, stop);

        }

    }

}
