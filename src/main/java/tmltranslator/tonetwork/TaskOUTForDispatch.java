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

import myutil.TraceManager;
import tmltranslator.*;

import java.util.Vector;


/**
 * Class TaskOUTForDispatch
 * Creation: 09/01/2019
 *
 * @author Ludovic Apvrille
 * @version 1.0 09/01/2019
 */
public class TaskOUTForDispatch extends TMLTask {

    public TaskOUTForDispatch(String name, Object referenceToClass, Object referenceToActivityDiagram) {
        super(name, referenceToClass, referenceToActivityDiagram);
        setDaemon(TMAP2Network.DAEMON);
    }

    // Output Channels are given in the order of VCs

    public void generate(Vector<TMLEvent> inPacketEvents, Vector<TMLEvent> feedbackEvents,
                         Vector<TMLEvent> outSelectEvents, TMLEvent outPktEvent, TMLChannel outPkt) {

        TMLSendEvent sendEvt;
        TMLWaitEvent waitEvt;
        TMLStopState stop;
        TMLChoice mainChoice = null;

        // Attributes
        TMLAttribute pktlen = new TMLAttribute("pktlen", "pktlen", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(pktlen);
        TMLAttribute dstX = new TMLAttribute("dstX", "dstX", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dstX);
        TMLAttribute dstY = new TMLAttribute("dstY", "dstY", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(dstY);
        TMLAttribute vc = new TMLAttribute("vc", "vc", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(vc);
        TMLAttribute eop = new TMLAttribute("eop", "eop", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(eop);
        TMLAttribute chid = new TMLAttribute("chid", "chid", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(chid);
        TMLAttribute nEvt = new TMLAttribute("nEvt", "nEvt", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(nEvt);
        TMLAttribute loopExit = new TMLAttribute("loopExit", "loopExit", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(loopExit);
        TMLAttribute feedback = new TMLAttribute("feedback", "feedback", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(feedback);
        TMLAttribute higherPrio = new TMLAttribute("higherPrio", "higherPrio", new TMLType(TMLType.NATURAL), "0");
        this.addAttribute(higherPrio);

        // Events & Channels
        for (TMLEvent evt : inPacketEvents) {
            addTMLEvent(evt);
        }
        for (TMLEvent evt : feedbackEvents) {
            addTMLEvent(evt);
        }

        addTMLEvent(outPktEvent);
        addTMLChannel(outPkt);
        for (TMLEvent evt : outSelectEvents) {
            addTMLEvent(evt);
        }

        // Activity Diagram
        TMLStartState start = new TMLStartState("mainStart", referenceObject);
        activity.setFirst(start);

        TMLForLoop loop = new TMLForLoop("mainLoop", referenceObject);
        loop.setInfinite(true);
        activity.addLinkElement(start, loop);




        TMLActionState loopExitSetting = new TMLActionState("loopExitSetting", referenceObject);
        loopExitSetting.setAction("loopExit = 2");
        activity.addLinkElement(loop, loopExitSetting);

        TMLStopState tmlstopstate = new TMLStopState("Stop after infinite loop", referenceObject);
        activity.addLinkElement(loop, tmlstopstate);

        TMLSequence mainSeq = new TMLSequence("MainSequence", referenceObject);
        activity.addLinkElement(loopExitSetting, mainSeq);


        // Right branch mainSec i =0
        int i = 0;

        TMLNotifiedEvent notified = new TMLNotifiedEvent("NotificationOfEvent" + i, referenceObject);
        notified.setEvent(inPacketEvents.get(i));
        notified.setVariable("nEvt");
        activity.addLinkElement(mainSeq, notified);

        mainChoice = new TMLChoice("ChoiceOf" + i, referenceObject);
        activity.addLinkElement(notified, mainChoice);

        activity.addLinkElement(mainChoice, new TMLStopState("StopAfterMainChoice", referenceObject));
        mainChoice.addGuard("nEvt == 0");

        TMLForLoop loopInside = new TMLForLoop("LoopInside" + i, referenceObject);
        loopInside.setInit("loopExit=0");
        loopInside.setCondition("loopExit<1");
        loopInside.setIncrement("loopExit=loopExit");
        activity.addLinkElement(mainChoice, loopInside);
        mainChoice.addGuard("nEvt > 0");

        TMLNotifiedEvent notifiedFeedback = new TMLNotifiedEvent("FeedbackNotifiedEvt" + i, referenceObject);
        notifiedFeedback.setEvent(feedbackEvents.get(i));
        notifiedFeedback.setVariable("feedback");
        activity.addLinkElement(loopInside, notifiedFeedback);

        TMLChoice internalChoice = new TMLChoice("InternalChoice" + i, referenceObject);
        activity.addLinkElement(notifiedFeedback, internalChoice);

        // Left branch of internal choice
        sendEvt = new TMLSendEvent("SelectEvent", referenceObject);
        sendEvt.setEvent(outSelectEvents.get(i));
        activity.addLinkElement(internalChoice, sendEvt);
        internalChoice.addGuard("feedback > 0");

        waitEvt = new TMLWaitEvent("PacketEventInLoop", referenceObject);
        //TraceManager.addDev("Nb Of params of " + inPacketEvents.get(i).getName() + " = " + inPacketEvents.get(i).getNbOfParams());
        waitEvt.setEvent(inPacketEvents.get(i));
        waitEvt.addParam("pktlen");
        waitEvt.addParam("dstX");
        waitEvt.addParam("dstY");
        waitEvt.addParam("vc");
        waitEvt.addParam("eop");
        waitEvt.addParam("chid");
        activity.addLinkElement(sendEvt, waitEvt);

        TMLActionState reqOut = new TMLActionState("ExitLoop" + i, referenceObject);
        reqOut.setAction("loopExit = eop");
        activity.addLinkElement(waitEvt, reqOut);

        sendEvt = new TMLSendEvent("infoOnPacket" + i, referenceObject);
        sendEvt.setEvent(outPktEvent);
        sendEvt.addParam("pktlen");
        sendEvt.addParam("dstX");
        sendEvt.addParam("dstY");
        sendEvt.addParam("vc");
        sendEvt.addParam("eop");
        sendEvt.addParam("chid");
        activity.addLinkElement(reqOut, sendEvt);

        TMLWriteChannel write = new TMLWriteChannel("WriteChannel" + i, referenceObject);
        write.addChannel(outPkt);
        write.setNbOfSamples("1");
        activity.addLinkElement(sendEvt, write);

        waitEvt = new TMLWaitEvent("ConsumeTokenEvt" + i, referenceObject);
        waitEvt.setEvent(feedbackEvents.get(i));
        activity.addLinkElement(write, waitEvt);

        stop = new TMLStopState("StopLeftBranch" + i, referenceObject);
        activity.addLinkElement(waitEvt, stop);

        // Right branch of internal choice
        TMLActionState actionEnd = new TMLActionState("MustExitLoop" + i, referenceObject);
        actionEnd.setAction("loopExit = 2");
        activity.addLinkElement(internalChoice, actionEnd);
        internalChoice.addGuard("feedback == 0");

        stop = new TMLStopState("StopRightBranch" + i, referenceObject);
        activity.addLinkElement(actionEnd, stop);

        //after loopInside
        stop = new TMLStopState("StopInsideLoop" + i, referenceObject);
        activity.addLinkElement(loopInside, stop);


        // Other branches
        for (i = 1; i < inPacketEvents.size(); i++) {


            mainChoice = new TMLChoice("mainChoice" + i, referenceObject);
            activity.addLinkElement(mainSeq, mainChoice);
            activity.addLinkElement(mainChoice, new TMLStopState("StopOfMainChoice", referenceObject));
            mainChoice.addGuard("else ");
            mainChoice.addGuard("loopExit == " + (i + 1));


            notified = new TMLNotifiedEvent("NotificationOfEvent" + i, referenceObject);
            notified.setEvent(inPacketEvents.get(i));
            notified.setVariable("nEvt");

            activity.addLinkElement(mainChoice, notified);

            mainChoice = new TMLChoice("ChoiceOf" + i, referenceObject);
            activity.addLinkElement(notified, mainChoice);

            loopInside = new TMLForLoop("LoopInside" + i, referenceObject);
            loopInside.setInit("loopExit=0");
            loopInside.setCondition("loopExit<1");
            loopInside.setIncrement("loopExit=loopExit");
            activity.addLinkElement(mainChoice, loopInside);
            mainChoice.addGuard("nEvt > 0");

            activity.addLinkElement(mainChoice, new TMLStopState("NoEvtOUTInVC" + i, referenceObject));
            mainChoice.addGuard("else");


            notifiedFeedback = new TMLNotifiedEvent("FeedbackNotifiedEvt" + i, referenceObject);
            notifiedFeedback.setEvent(feedbackEvents.get(i));
            notifiedFeedback.setVariable("feedback");
            activity.addLinkElement(loopInside, notifiedFeedback);

            internalChoice = new TMLChoice("InternalChoice" + i, referenceObject);
            activity.addLinkElement(notifiedFeedback, internalChoice);

            // Left branch of internal choice
            sendEvt = new TMLSendEvent("SelectEvent", referenceObject);
            sendEvt.setEvent(outSelectEvents.get(i));
            activity.addLinkElement(internalChoice, sendEvt);
            internalChoice.addGuard("feedback > 0");

            waitEvt = new TMLWaitEvent("PacketEventInLoop", referenceObject);
            //TraceManager.addDev("Nb Of params of " + inPacketEvents.get(i).getName() + " = " + inPacketEvents.get(i).getNbOfParams());
            waitEvt.setEvent(inPacketEvents.get(i));
            waitEvt.addParam("pktlen");
            waitEvt.addParam("dstX");
            waitEvt.addParam("dstY");
            waitEvt.addParam("vc");
            waitEvt.addParam("eop");
            waitEvt.addParam("chid");
            activity.addLinkElement(sendEvt, waitEvt);

            reqOut = new TMLActionState("ExitLoop" + i, referenceObject);
            reqOut.setAction("loopExit = eop");
            activity.addLinkElement(waitEvt, reqOut);

            sendEvt = new TMLSendEvent("infoOnPacket" + i, referenceObject);
            sendEvt.setEvent(outPktEvent);
            sendEvt.addParam("pktlen");
            sendEvt.addParam("dstX");
            sendEvt.addParam("dstY");
            sendEvt.addParam("vc");
            sendEvt.addParam("eop");
            sendEvt.addParam("chid");
            activity.addLinkElement(reqOut, sendEvt);

            write = new TMLWriteChannel("WriteChannel" + i, referenceObject);
            write.addChannel(outPkt);
            write.setNbOfSamples("1");
            activity.addLinkElement(sendEvt, write);

            waitEvt = new TMLWaitEvent("ConsumeTokenEvt" + i, referenceObject);
            waitEvt.setEvent(feedbackEvents.get(i));
            activity.addLinkElement(write, waitEvt);

            TMLNotifiedEvent notifiedHigherPrio = new TMLNotifiedEvent("HigherPrioMsg" + i, referenceObject);
            notifiedHigherPrio.setEvent(inPacketEvents.get(0));
            notifiedHigherPrio.setVariable("higherPrio");
            activity.addLinkElement(waitEvt, notifiedHigherPrio);

            TMLChoice higherPrioChoice = new TMLChoice("HigherPrioChoice", referenceObject);
            activity.addLinkElement(notifiedHigherPrio, higherPrioChoice);

            TMLActionState settingExit = new TMLActionState("SettingExitHigherPrio", referenceObject);
            settingExit.setAction("loopExit = 1");
            higherPrioChoice.addGuard("higherPrio > 0");
            activity.addLinkElement(higherPrioChoice, settingExit);

            stop = new TMLStopState("StopLeftBranch1" + i, referenceObject);
            activity.addLinkElement(settingExit, stop);

            stop = new TMLStopState("StopLeftBranch2" + i, referenceObject);
            higherPrioChoice.addGuard("else");
            activity.addLinkElement(higherPrioChoice, stop);


            // Right branch of internal choice
            actionEnd = new TMLActionState("MustExitLoop" + i, referenceObject);
            actionEnd.setAction("loopExit = 2");
            activity.addLinkElement(internalChoice, actionEnd);
            internalChoice.addGuard("feedback == 0");

            stop = new TMLStopState("StopRightBranch" + i, referenceObject);
            activity.addLinkElement(actionEnd, stop);

            //after loopInside
            stop = new TMLStopState("StopInsideLoop" + i, referenceObject);
            activity.addLinkElement(loopInside, stop);

        }

    }

}
