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
import myutil.TraceManager;

import java.util.ArrayList;

/**
 * Class AIStateMachine
 * <p>
 * Creation: 02/06/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 13/06/2023
 */


public class AIStateMachine extends AIInteract implements AISysMLV2DiagramContent {
    private static String[] SUPPORTED_DIAGRAMS = {"BD"};
    private static String[] EXCLUSIONS_IN_INPUT = {"type"};

    public static String KNOWLEDGE_ON_JSON_FOR_STATE_MACHINES = "When you are asked to identify the SysML state machine of a block, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{states: [{ \"name\": \"Name of state\", transitions [{ \"destinationstate\" : \"state name\",  \"guard\": \"boolean condition\", " +
            "\"after\": \"time " +
        "value\", \"action\":" +
            " \"attribute action or signal receiving/sending\"}]}]} ." +
            "# Respect: in actions, use only attributes and signals already defined in the corresponding block" +
            "# Respect: at least one state must be called Start, which is the start state";


    private AvatarSpecification specification;

    private String diagramContentInSysMLV2;

    private String[] KNOWLEDGE_SYSTEM_SPECIFICATION = {"The specification of the system is:"};
    private String[] KNOWLEDGE_SYSTEM_BLOCKS = {"The specification of the blocks in SysML V2 is:"};

    private String[] QUESTION_IDENTIFY_STATE_MACHINE = {"From the  system specification, and from the definition of blocks and" +
            " their " +
            "connections, identify the state machine of block:"};


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
        ArrayList<String> blockNames = new ArrayList<>();

        boolean done = false;
        int cpt = 0;

        String questionT;


        for(String blockName: blockNames) {
            while (!done && cpt < 3) {
                cpt++;

                questionT = QUESTION_IDENTIFY_STATE_MACHINE + blockName;

                boolean ok = makeQuestion(questionT);
                if (!ok) {
                    done = true;
                    TraceManager.addDev("Make question failed");
                }

                // Checking if only correct attributes are used, only valid signals, that there is a "Start" state, etc.
                ArrayList<String> errors;
                try {
                    TraceManager.addDev("Making specification from " + chatData.lastAnswer);
                    specification = AvatarSpecification.fromJSON(extractJSON(), "design", null);
                    errors = AvatarSpecification.getJSONErrors();

                } catch (org.json.JSONException e) {
                    TraceManager.addDev("Invalid JSON spec: " + extractJSON() + " because " + e.getMessage() + ": INJECTING ERROR");
                    errors = new ArrayList<>();
                    errors.add("There is an error in your JSON: " + e.getMessage() + ". probably the JSON spec was incomplete. Do correct it. I need " +
                            "the full specification at once.");
                }

                if ((errors != null) && (errors.size() > 0)) {
                    questionT = "Your answer was not correct because of the following errors:";
                    for (String s : errors) {
                        questionT += "\n- " + s;
                    }
                } else {
                    TraceManager.addDev(" Avatar spec=" + specification);
                }

                waitIfConditionTrue(!done && cpt < 20);

                cpt++;
            }
        }
        TraceManager.addDev("Reached end of AIBlock internal request cpt=" + cpt);

    }

    public Object applyAnswer(Object input) {
        if (input == null) {
            return specification;
        }

        return specification;
    }


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
