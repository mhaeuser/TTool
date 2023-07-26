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


import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.tosysmlv2.AVATAR2SysMLV2;
import myutil.TraceManager;
import org.apache.batik.anim.timing.Trace;

import java.util.ArrayList;

/**
 * Class AIStateMachine
 * <p>
 * Creation: 02/06/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 13/06/2023
 */


public class AIStateMachine extends AIInteract implements AISysMLV2DiagramContent, AIAvatarSpecificationRequired {
    private static String[] SUPPORTED_DIAGRAMS = {"BD"};
    private static String[] EXCLUSIONS_IN_INPUT = {"state",  "method"};

    public static String KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES = "When you are asked to identify the SysML state machine of a block, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{states: [{ \"name\": \"Name of state\", transitions [{ \"destinationstate\" : \"state name\",  \"guard\": \"boolean condition\", " +
            "\"after\": \"time " +
        "value\", \"action\":" +
            " \"attribute action or signal receiving/sending\"}]}]} ." +
            "# Respect: in actions, use only attributes and signals already defined in the corresponding block" +
            "# Respect: at least one state must be called \"Start\", which is the start state" +
            "# Respect: if a guard, an action, or an after is empty, use an empty string \"\", do not use \"null\"" +
            "# Respect: an action contains either a variable affectation, e.g. \"x = x + 1\" or a signal send/receive " +
            "# Respect: if a transition contains several actions, use a \";\" to separate them " +
            "# Respect: a signal send is out::signalName(..) and a signal receive is in::signaNamd(...) " +
            "# Respect: the attribute of an action is named by its identifier, do not reference its block " +
            "# Respect: A guard cannot contain a reference to a signal " +
            "# Respect: To reference the attribute \"x\" of block \"B\", use \"x\" and never \"B.x\" nor \"B::x\"";


    private AvatarSpecification specification;

    private String diagramContentInSysMLV2;

    private static String KNOWLEDGE_SYSTEM_SPECIFICATION = "The specification of the system is:";
    private static String KNOWLEDGE_SYSTEM_BLOCKS = "The specification of the blocks in SysML V2 is:";

    private String[] QUESTION_IDENTIFY_STATE_MACHINE = {"From the  system specification, and from the definition of blocks and" +
            " their " +
            "connections, identify the state machine of block: "};


    public AIStateMachine(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {

        // Add the knowledge, retrieve the block names, attributes, etc.
        if (!chatData.knowledgeOnStateMachines) {
            chatData.aiinterface.addKnowledge(KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES, "ok");
            chatData.knowledgeOnStateMachines = true;
        }

        chatData.aiinterface.addKnowledge(KNOWLEDGE_SYSTEM_SPECIFICATION + chatData.lastQuestion, "ok");
        chatData.aiinterface.addKnowledge(KNOWLEDGE_SYSTEM_BLOCKS + diagramContentInSysMLV2, "ok");

        // Getting block names for SysMLV2 spec
        //TraceManager.addDev("SysML V2 spec: " + diagramContentInSysMLV2);
        ArrayList<String> blockNames = AVATAR2SysMLV2.getAllBlockNames(diagramContentInSysMLV2);

        TraceManager.addDev("Going to handle the following blocks: ");
        for(String s: blockNames) {
            TraceManager.addDev("\tblock: " + s);
        }


        boolean done = false;
        int cpt = 0;

        String questionT;


        for(String blockName: blockNames) {
            TraceManager.addDev("Handling block: " + blockName);
            done = false; cpt = 0;
            int max = 10;


            questionT = QUESTION_IDENTIFY_STATE_MACHINE[0] + blockName;
            while (!done && cpt < max) {
                done = true;
                boolean ok = makeQuestion(questionT);
                if (!ok) {
                    TraceManager.addDev("Make question failed");
                }

                if (ok && specification != null) {
                    AvatarBlock b = specification.getBlockWithName(blockName);
                    if (b != null) {
                        TraceManager.addDev("Making the state machine of " + blockName);
                        ArrayList<String> errors = b.makeStateMachineFromJSON(extractJSON(), cpt == (max - 1));
                        if ((errors != null) && (errors.size() > 0)) {
                            done = false;
                            questionT = "Your answer was not correct because of the following errors:";
                            for (String s : errors) {
                                TraceManager.addDev("Error in JSON: " + s);
                                questionT += "\n- " + s;
                            }
                        } else {
                            TraceManager.addDev("SMD done for Block " + blockName);
                        }
                    } else {
                        TraceManager.addDev("ERROR: no block named " + blockName);
                    }
                } else {
                    TraceManager.addDev("Null specification or false ok");
                }

                waitIfConditionTrue(!done && cpt < max);

                cpt ++;
            }
            // Remove knowledge of previous questions
            while(cpt > 0) {
                cpt --;
                chatData.aiinterface.removePreviousKnowledge();
            }
        }
        TraceManager.addDev("Reached end of AIStateMachine internal request cpt=" + cpt);

    }

    public Object applyAnswer(Object input) {
        TraceManager.addDev("Apply answer in AIState Machine");
        if (specification == null) {
            TraceManager.addDev("Null spec");
        } else {
            TraceManager.addDev("Non null spec");
        }
        if (input == null) {
            return specification;
        }

        return specification;
    }

    public void setAvatarSpecification(AvatarSpecification _specification) {
        specification = _specification;
    };

    public void setDiagramContentInSysMLV2(String _diagramContentInSysMLV2) {
        diagramContentInSysMLV2 = _diagramContentInSysMLV2;
    };

    public String[] getValidDiagrams() {
        return SUPPORTED_DIAGRAMS;
    }

    public String[] getDiagramExclusions() {
        return EXCLUSIONS_IN_INPUT;
    }





}
