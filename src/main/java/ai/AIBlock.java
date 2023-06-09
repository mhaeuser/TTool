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
import myutil.TraceManager;

import java.util.ArrayList;

/**
 * Class AIBlock
 * <p>
 * Creation: 02/06/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 02/06/2023
 */


public class AIBlock extends AIInteract {

    public static String KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES = "When you are asked to identify SysML blocks, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{blocks: [{ \"name\": \"Name of block\", \"attributes\": [\"name\": \"name of attribute\", \"type\": \"int or bool\" ...} ...]}" +
            "Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int." +
            "# Respect: each attribute must be of type \"int\" or \"bool\" only" +
            "# Respect: Any identifier (block, attribute, etc.) must no contain any space. Use \"_\" instead.";
    public static String KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_CONNECTIONS = "When you are ask to identify signals of blocks, JSON is as follows: " +
            "{blocks: [{ \"name\": \"Name of block\", \"signals\": " +
            "[ ... signals ... ] ... (no need to relist the attributes of signals, nor to give a direction). " +
            "#Respect: signals are defined like this in JSON: {\"signal\": \"input sig1(int x, bool b)\"} if the signal is an input signal" +
            " and {\"signal\": \"output sig1(int x, bool b)\"} if the signal is an output signal" +
            "#Respect 2 signals with the same name are assumed to be connected: this is the only way to connect signals. " +
            "#Respect: Two connected signals must have " +
            "the same list of attributes, even if they are " +
            "defined in two different blocks. One of them must be output, the other one must be input" +
            "#Respect: all input signals must have exactly one corresponding output signal, i.e. a signal with the same name" +
            "#Respect: two signals with the same name must be defined in different blocks";
            /*"and after " +
            "the blocks, add the " +
            "following JSON: " +
            "connections: [{\"block1\" : name of first block, \"sig1\": name of first " +
            "signal\", \"block2\" : name of second block" +
            "\"sig2\": \"name of second signal\"}, ." +
            "..]. " +  ".#" +
            "Respect: in a connection, sig1 and sig2 must be different. The name of the signal only include its identifier, so not " +
            "\"input\" nor " +
            "\"output\", nor its attributes.#" +
            "Two connected signals must have the \" +\n" +
            "            \"same list of attributes." +
            "# A signal must be involved in one connection exactly";*/




    public static String[] KNOWLEDGE_STAGES = {KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES, KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_CONNECTIONS};
    AvatarSpecification specification;
    private String[] QUESTION_IDENTIFY_SYSTEM_BLOCKS = {"From the following system specification, using the specified JSON format, identify the " +
            "typical system blocks and their attributes. Do respect the JSON format, and provide only JSON (no explanation before or after).\n",
            "From the previous JSON and system specification, update " +
            "this JSON with" +
            " the signals you have to identify. If necessary, you can add new blocks and new attributes."};


    public AIBlock(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {

        int stage = 0;
        String questionT = QUESTION_IDENTIFY_SYSTEM_BLOCKS[stage] + "\n" + chatData.lastQuestion.trim() + "\n";

        makeKnowledge(stage);

        boolean done = false;
        int cpt = 0;

        // Blocks and attributes
        while (!done && cpt < 20) {
            cpt++;
            boolean ok = makeQuestion(questionT);
            if (!ok) {
                done = true;
                TraceManager.addDev("Make question failed");
            }
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
                stage++;
                if (stage == KNOWLEDGE_STAGES.length) {
                    done = true;
                } else {
                    makeKnowledge(stage);
                    questionT = QUESTION_IDENTIFY_SYSTEM_BLOCKS[stage] + "\n";
                }
            }

            waitIfConditionTrue(!done && cpt < 20);

            cpt++;
        }
        TraceManager.addDev("Reached end of AIBlock internal request cpt=" + cpt);

    }

    public Object applyAnswer(Object input) {
        if (input == null) {
            return specification;
        }

        return specification;
    }

    public void makeKnowledge(int stage) {
        TraceManager.addDev("makeKnowledge. stage: " + stage + " chatData.knowledgeOnBlockJSON: " + chatData.knowledgeOnBlockJSON);
        if (stage > chatData.knowledgeOnBlockJSON) {
            chatData.knowledgeOnBlockJSON++;

            String [] know = KNOWLEDGE_STAGES[chatData.knowledgeOnBlockJSON].split("#");

            for(String s: know) {
                TraceManager.addDev("\nKnowledge added: " + s);
                chatData.aiinterface.addKnowledge(s, "ok");
            }
        }
    }




}
