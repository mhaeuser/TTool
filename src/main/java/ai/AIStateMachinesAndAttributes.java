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
 * Class AIStateMachinesAndAttributes
 * <p>
 * Creation: 04/08/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 04/08/2023
 */


public class AIStateMachinesAndAttributes extends AIInteract implements AISysMLV2DiagramContent, AIAvatarSpecificationRequired {
    private static String[] SUPPORTED_DIAGRAMS = {"BD"};
    private static String[] EXCLUSIONS_IN_INPUT = {"state",  "method"};

    public static String KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES = "When you are asked to identify the SysML state machine of a block, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{states: [{ \"name\": \"Name of state\", transitions [{ \"destinationstate\" : \"state name\",  \"guard\": \"boolean condition\", " +
            "\"after\": \"time " +
        "value\", \"action\":" +
            " \"attribute action or signal receiving/sending\"}]}]} .";

    public static String ATTRIBUTES_JSON_FOR_STATE_MACHINES = "Now, give the type of all attributes (and not signals) you have just used using the " +
            "following JSON " +
            "format: {attributes: [{\"name\" : \"name of attribute\",  \"type\": \"int or boolean\"";

    public static String[] CONSTRAINTS_ON_JSON_FOR_STATE_MACHINES = {
            "# Respect: in actions, use only signals already defined in the block.",
                    "# Respect: at least one state must be called \"Start\", which is the start state.",
                    "# Respect: if a guard, an action, or an after is empty, use an empty string \"\", do not use \"null\"." ,
                    "# Respect: an action contains either a variable affectation, e.g. \"x = x + 1\" or a signal send/receive. " ,
                    "# Respect: if a transition contains several actions, use a \";\" to separate them. " ,
                    "# Respect: a signal send or receive is signalName(..) with inside the right attributes or values. " ,
                    "# Respect: the attribute of an action is named by its identifier, do not reference its block. " ,
                    "# Respect: a state machine can use only the attribute of its block. " ,
                    "# Respect: A guard cannot contain a reference to a signal. " ,
                    "# Respect: To reference the attribute \"x\" of block \"B\", use \"x\" and never \"B.x\" nor \"B::x\"" };


    private AvatarSpecification specification;

    private String diagramContentInSysMLV2;

    private static String KNOWLEDGE_SYSTEM_SPECIFICATION = "The specification of the system is:";
    private static String KNOWLEDGE_SYSTEM_BLOCKS = "The specification of the blocks in SysML V2 is:";

    private String[] QUESTION_IDENTIFY_STATE_MACHINE = {"From the  system specification, and from the definition of blocks and" +
            " their " +
            "connections, identify the state machine of block: "};


    public AIStateMachinesAndAttributes(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {

        // Add the knowledge, retrieve the block names, attributes, etc.

        /*if (!chatData.knowledgeOnStateMachines) {
            chatData.aiinterface.addKnowledge(KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES, "ok");
            chatData.knowledgeOnStateMachines = true;
        }

        chatData.aiinterface.addKnowledge(KNOWLEDGE_SYSTEM_SPECIFICATION + chatData.lastQuestion, "ok");
        chatData.aiinterface.addKnowledge(KNOWLEDGE_SYSTEM_BLOCKS + diagramContentInSysMLV2, "ok");*/

        // Getting block names for SysMLV2 spec
        //TraceManager.addDev("SysML V2 spec: " + diagramContentInSysMLV2);
        //ArrayList<String> blockNames = AVATAR2SysMLV2.getAllBlockNames(diagramContentInSysMLV2);
        ArrayList<String> blockNames = specification.getAllBlockNames();

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
            int max = 3;


            initKnowledge();

            AvatarBlock b = specification.getBlockWithName(blockName);
            questionT = QUESTION_IDENTIFY_STATE_MACHINE[0] + blockName;
            if (b != null) {
                questionT += "\nThis block has the following attributes:\n " + specification.getStringAttributes(b);
                questionT += "\nThis block has the following signals:\n " + specification.getStringSignals(b);
            }
            while (!done && cpt < max) {
                done = true;


                boolean ok1 = makeQuestion(questionT);
                if (!ok1) {
                    TraceManager.addDev("Make question #1 failed");
                }
                String json1 = extractJSON();

                //chatData.aiinterface.addKnowledge(questionT, chatData.lastAnswer);
                questionT = ATTRIBUTES_JSON_FOR_STATE_MACHINES;
                boolean ok2 = makeQuestion(questionT);

                if (!ok2) {
                    TraceManager.addDev("Make question #2 failed");
                }
                String json2 = extractJSON();

                if (ok1 && ok2 && specification != null) {
                    if (b != null) {
                        ArrayList<String> errors = new ArrayList<>();
                        ArrayList<String> ret;
                        TraceManager.addDev("Adding attributes of the state machine of " + blockName);
                        ret = b.addAttributesFromJSON(json2);
                        if (ret != null) {
                            errors.addAll(ret);
                        }
                        TraceManager.addDev("Making the state machine of " + blockName);

                        errors.addAll(b.makeStateMachineFromJSON(json1, true));

                        ret = b.addAttributesFromJSON(json2);
                        if (ret != null) {
                            errors.addAll(ret);
                        }

                        if ((errors != null) && (errors.size() > 0)) {
                            done = false;
                            initKnowledge();

                            questionT += "Your specification was: " + json1 + ". But it is not correct because of the following errors:";

                            for (String s : errors) {
                                TraceManager.addDev("Error in JSON: " + s);
                                questionT += "\n- " + s;
                            }
                            questionT += "\nProvide only the updated state machine using only the already defined attributes";
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
            initKnowledge();
            /*while(cpt > 0) {
                cpt --;
                chatData.aiinterface.removePreviousKnowledge();
            }*/
        }
        TraceManager.addDev("Reached end of AIStateMachine internal request cpt=" + cpt);

    }

    private void initKnowledge() {
        chatData.aiinterface.clearKnowledge();

        chatData.aiinterface.addKnowledge(KNOWLEDGE_SYSTEM_SPECIFICATION + chatData.lastQuestion, "ok");
        chatData.aiinterface.addKnowledge(KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES, "ok");

        for(int i=0; i<CONSTRAINTS_ON_JSON_FOR_STATE_MACHINES.length; i++) {
            chatData.aiinterface.addKnowledge(CONSTRAINTS_ON_JSON_FOR_STATE_MACHINES[i], "ok");
        }
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
