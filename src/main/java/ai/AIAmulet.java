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


package ai;


import avatartranslator.AvatarSpecification;
import avatartranslator.mutation.ApplyMutationException;
import avatartranslator.mutation.AvatarMutation;
import avatartranslator.mutation.ParseMutationException;
import myutil.TraceManager;
import ui.window.JFrameAI;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Class AIAmulet
 *
 * Creation: 06/06/2023
 * @version 1.0 06/06/2023
 * @author Ludovic APVRILLE
 */


public class AIAmulet extends AIInteract implements AISysMLV2DiagramContent {


    private static String[] SUPPORTED_DIAGRAMS = {"BD"};
    private static String[] EXCLUSIONS_IN_INPUT = {};

    private String diagramContent;

    public AIAmulet(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {
        if (!chatData.knowledgeOnAMULET) {
            injectAMULETKnowledge();
            chatData.knowledgeOnAMULET = true;
        }

        if (diagramContent != null) {
            //TraceManager.addDev("\n\nUse the following SysML V2 model: " + diagramContent + "\n");
            chatData.aiinterface.addKnowledge("Consider the following model " + diagramContent,"Understood");
        }

        String questionT = "\nTTool:" + chatData.lastQuestion.trim()+ "\n";
        boolean ok = makeQuestion(questionT);
    }

    public Object applyAnswer(Object input) {
        if (!(input instanceof AvatarSpecification)) {
            return null;
        }

        AvatarSpecification avspec = (AvatarSpecification) input;

        String automatedAnswer = chatData.lastAnswer;
        BufferedReader buff = new BufferedReader(new StringReader(automatedAnswer));
        String line ;
        Boolean mutationApplied = false;

        try {

            while ((line = buff.readLine()) != null) {
                if (line.startsWith("add ") || line.startsWith("remove ") || line.startsWith("modify ")) {
                    try {
                        AvatarMutation am = AvatarMutation.createFromString(line);
                        if (am != null) {
                            am.apply(avspec);
                            mutationApplied = true;
                        }
                    } catch (ParseMutationException e) {
                        TraceManager.addDev("Exception in parsing mutation: " + e.getMessage());
                        error(e.getMessage());
                    } catch (ApplyMutationException e) {
                        TraceManager.addDev("Exception in applying mutation: " + e.getMessage());
                        error(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            error("Mutation application failed: " + e.getMessage());
        }

        if (mutationApplied) {
            return avspec;
        }

        return null;
    }

    public void setDiagramContentInSysMLV2(String _diagramContentInSysMLV2) {
        diagramContent = _diagramContentInSysMLV2;
    }

    public String[] getValidDiagrams() {
        return SUPPORTED_DIAGRAMS;
    }

    public String[] getDiagramExclusions() {
        return EXCLUSIONS_IN_INPUT;
    }

    private void injectAMULETKnowledge() {
        chatData.aiinterface.addKnowledge("AMULET is a SysML mutation language. In AMULET, adding a block b in a block diagram is written " +
                "\"add block b\".\nRemoving a block b from a block diagram is written \"remove block b\".\n","AMULET source-code for adding a block" +
                " myBlock is \"add block myBlock\" and for removing a block myBlock is \"remove block myBlock\".");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. Adding an input signal sig in a block b is written \"add input signal " +
                "sig in " +
                "b\". If the signal conveys parameters (e.g. int i and bool x), we will write \"add input signal sig(int i, bool x) in b\".\n" +
                "Removing an input signal sig from a block b is written \"remove input signal sig in b\".\n" +
                "Adding an output signal sig in a block b is written \"add output signal sig in b\". If the signal sig conveys parameters (e.g. int" +
                " i and bool x), we will write \"add output signal sig(int i, bool x) in b\".\n" +
                "Removing an output signal sig from a block b is written \"remove output signal sig in b\".\n", "In AMULET, adding an input" +
                " signal mySig in a block myBlock is written \"add input signal mySig in myBlock\". If this signal conveys three parameters int n, " +
                "int m and bool b, it is written \"add input signal mySig (int n, int m, bool b) in myBlock\". Adding an output signal mySig in a " +
                "block myBlock is written \"add output signal mySig in myBlock\". If this signal conveys two parameters bool b and int n, it is " +
                "written \"add output signal mySig(bool b, int n) in myBlock\". Removing an input signal mySig from a block myBlock is written " +
                "\"remove input signal mySig in myBlock\", and removing an output signal mySig from a block myBlock is written \"remove output " +
                "signal mySig in myBlock\".");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. Adding an integer attribute i in a block b is written \"add attribute " +
                "int i in b\".\n" + "Adding a boolean " +
                "attribute x in a block b is written \"add attribute bool x in b\".\n" + "Removing an attribute a from a block b is written" +
                " \"remove attribute a in b\"","In AMULET, adding an integer attribute n in a block myBlock is written \"add attribute int n in " +
                "myBlock\".\n" + "Adding a boolean attribute b in a block myBlock is written \"add attribute bool b in myBlock\".\n" +
                "Removing an attribute myAttribute from a block myBlock is written \"remove attribute myAttribute in myBlock\".");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a connection between the ports of two blocks b1 and b2 is " +
                "written \"add link between b1 and b2\".\n" +
                "Removing a connection between the ports of two blocks b1 and b2 is written \"remove link between b1 and b2\".", "In " +
                "AMULET, adding a connection between the ports of two blocks b1 and b2 is written \"add link between b1 and b2\".\n" + "Removing a " +
                "connection between the ports of two blocks b1 and b2 is written \"remove link between b1 and b2\".");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. If we want an input and an output signal to be synchronized, we need " +
                "to connect them. Connecting an input signal insig in a block b to an output signal outsig in a block c is written \"add connection " +
                "between outsig in c to insig in b\".\n" + "Removing a connection between an input signal insig in a block b to an output signal " +
                "outsig in a block c is written \"remove connection between outsig in c to insig in b\".","In AMULET, connecting an input signal " +
                "inSig in a block myBlock to an output signal outSig in a block mySecondBlock is written \"add connection between outSig in " +
                "mySecondBlock to inSig in myBlock\".\n" + "Removing a connection between an input signal inSig in a block myBlock to an output " +
                "signal outSig in a block mySecondBlock is written \"remove connection between outSig in mySecondBlock to inSig in myBlock\".");

        chatData.aiinterface.addKnowledge("Connections are only possible between two signals, an input one and an output one.","Right, we can't " +
                "connect a signal to an attribute but only to another signal of the opposite type (input, output).");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a state s in a block b's state-machine diagram is written " +
                "\"add state s in b\".\n Removing a state s from a block b's state-machine diagram is written \"remove state s in b\".", "In " +
                "AMULET, adding a state myState in a block myBlock's state-machine diagram is written \"add state myState in myBlock\".\n Removing a state myState " +
                "from a block myBlock's state-machine diagram is written \"remove state myState in myBlock\".");

        chatData.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a transition t in a block b's state-machine diagram from a " +
                "state s0 to" +
                " a state s1 is written \"add transition t in b from s0 to s1\". If this transition has a guard (i.e., a boolean condition " +
                "boolean_condition allowing its firing), we will write \"add transition t in b from s0 to s2 with [boolean_condition]\".\n" +
                "Removing a transition t in a block b's state-machine diagram from a state s0 to a state s1 is written \"remove transition t in b\"" +
                " or, if there is only one transition from s0 to s1, \"remove transition in b from s0 to s1\".", "In AMULET, adding a transition " +
                "myTransition in a block myBlock's state-machine diagram from a state myState to a state mySecondState is written \"add transition " +
                "myTransition in myBlock from myState to mySecondState\". If this transition has a guard (i.e., a boolean condition " +
                "boolean_condition allowing its firing), we will write \"add transition myTransition in myBlock from myState to mySecondState with " +
                "[boolean_condition]\".\n" + "Removing a transition myTransition in a block myBlock's state-machine diagram from a state myState to" +
                " a state mySecondState is written \"remove transition myTransition in myState\" or, if there is only one transition from myState " +
                "to mySecondState, \"remove transition in myBlock from myState to mySecondState\".");

        chatData.aiinterface.addKnowledge("If we want a block b to receive a parameter p through an input signal and if b has no attribute of the " +
                "type of p, we need first to add the relevant attribute to b.","Right. For instance, if we want a block b to receive an " +
                "integer parameter n and if b has no integer attribute, we will first add an integer attribute n: add attribute int n in b");

        chatData.aiinterface.addKnowledge("Consider a block having an integer attribute myInt. If we want this block to send this value " +
                "through an output signal myOutSig, the output signal declaration will be myOutSig(int myInt).","Right. And if this block " +
                "has a boolean attribute myBool and we want it to receive a boolean value from an input signal myInSig and if we want this value to be assigned " +
                "to myBool, the input signal declaration will be myInSig(bool myBool).");

        chatData.aiinterface.addKnowledge("A link can only exist between two blocks. We can't add a link between to signals, only a connection.",
                "Right. If I want two blocks b1 and b2 to be connected, I will write \"add link between b1 and b2\", and if I want two signals s1 " +
                        "and s2 belonging to b1 and b2 to be connected, I will write \"add connection between s1 in b1 to s2 in b2\".");

        chatData.aiinterface.addKnowledge("If a block already exists in the model, we don't need to add it with an AMULET \"add\" command.",
                "Right, if the model I analyze already has a block b1, I will never write \"add block b1\" except if b1 has been deleted by another" +
                        " AMULET command.");

        chatData.aiinterface.addKnowledge("Now, I want you to answer only with the AMULET source code, without any comment nor other sentence that is" +
                " not AMULET source code.", "Understood, from now on I will only provide AMULET source code.");

        chatData.aiinterface.addKnowledge("Consider a block diagram with a block b1 and a block b2. I want b1 to send an integer value n to b2. Could" +
                " you provide the relevant AMULET source code?","add attribute int n in b2\n add attribute int n in b1\n add output signal sendInt" +
                "(int n) in b1\n add input signal receiveInt(int n) in b2");

        chatData.aiinterface.addKnowledge("Consider a block diagram with a block b1 and a block b2. I want b1 to send an boolean value b to b2. Could" +
                " you provide the relevant AMULET source code?","add attribute bool b in b1\n add attribute bool b in b2\n add output signal " +
                "sendBool(bool b) in b1\n add input signal receiveBool(bool b) in b2");
    }

	
    
}