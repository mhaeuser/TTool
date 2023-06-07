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
            "Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int. Any identifier (block," +
            "attribute, etc.) must no contain any space. User \"_\" instead.";
    public static String KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_CONNECTIONS = "When you are ask to identify signals of blocks, give a name to each " +
            "signal, and list their attributes. Also, each signal must be connected with exactly one signal. Two connected signals must have the " +
            "same list of attributes. For instance, if block b1 has a signal s1(int x), and b2 a signal s2(int y), s1 and s2 can be connected. In " +
            "JSON, signals are given in the definition of blocks. JSON is as follows: {blocks: [{ \"name\": \"Name of block\", \"signals\": " +
            "[\"signal\": \"sig1(int x, bool b)\"...] (no need to relist the attributes of signals) and after the blocks, add the following JSON: " +
            "connections: [{\"sig1\": \"name of first " +
            "signal\", " +
            "\"sig2\": \"name of second signal\"}, ." +
            "..]. " +
            "Also, in this JSON, keep the attributes you have identified at previous step";
    public static String[] KNOWLEDGE_STAGES = {KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES, KNOWLEDGE_ON_JSON_FOR_BLOCKS_AND_CONNECTIONS};
    AvatarSpecification specification;
    private String[] QUESTION_IDENTIFY_SYSTEM_BLOCKS = {"From the following system specification, using the specified JSON format, identify the " +
            "typical system blocks and their attributes. Do respect the JSON format.\n", "From the previous JSON and system specification, identify" +
            " the siganls and connection between signals"};
    private String KNOWLEDGE_ON_JSON_FOR_BLOCKS = "JSON for block diagram is as follows: " +
            "{blocks: [{ \"name\": \"Name of block\", \"attributes\": [\"name\": \"name of attribute\", \"type\": \"int or boolean\" ...}" + " same" +
            "(with its parameters : int, boolean ; and its return type : nothing, int or boolean)" +
            "and signals (with its list of parameters : int or boolean, and a type (input, output)" +
            " then the list of connections between block signals: \"connections\": [\n" + "{\n" + " \"sourceBlock\": \"name of block\",\n" +
            " \"sourceSignal\": \"name of output signal\",\n" +
            " \"destinationBlock\": \"name of destination block\",\n" +
            " \"destinationSignal\": \"rechargeBattery\",\n" +
            " \"communicationType\": \"synchronous (or asynchronous)\"\n" +
            "}. A connection must connect one output signal of a block to one input signal of a block. All signals must be connected to exactly one" +
            "connection";
    private String KNOWLEDGE_ON_JSON_FOR_BLOCKS_2 = "The system has two blocks B1 et B2.\n" +
            "B1 has an attribute x of type int and B2 has one attribute y of  type bool.\n" +
            "B1 also has a method: \"int getValue(int val)\" and an output signal sendInfo(int x).\n" +
            "B2 has an input signal \"getValue(int val)\".\n" +
            "sendInfo of B1 is connected to getValue of block B2.";
    private String KNOWLEDGE_ON_JSON_FOR_BLOCKS_ANSWER_2 = "{\n" +
            "  \"blocks\": [\n" +
            "    {\n" +
            "      \"name\": \"B1\",\n" +
            "      \"attributes\": [\n" +
            "        {\n" +
            "          \"name\": \"x\",\n" +
            "          \"type\": \"int\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"methods\": [\n" +
            "        {\n" +
            "          \"name\": \"getValue\",\n" +
            "          \"parameters\": [\n" +
            "            {\n" +
            "              \"name\": \"val\",\n" +
            "              \"type\": \"int\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"returnType\": \"int\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"signals\": [\n" +
            "        {\n" +
            "          \"name\": \"sendInfo\",\n" +
            "          \"parameters\": [\n" +
            "            {\n" +
            "              \"name\": \"x\",\n" +
            "              \"type\": \"int\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"output\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"B2\",\n" +
            "      \"attributes\": [\n" +
            "        {\n" +
            "          \"name\": \"y\",\n" +
            "          \"type\": \"bool\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"signals\": [\n" +
            "        {\n" +
            "          \"name\": \"getValue\",\n" +
            "          \"parameters\": [\n" +
            "            {\n" +
            "              \"name\": \"val\",\n" +
            "              \"type\": \"int\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"input\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"connections\": [\n" +
            "    {\n" +
            "      \"sourceBlock\": \"B1\",\n" +
            "      \"sourceSignal\": \"sendInfo\",\n" +
            "      \"destinationBlock\": \"B2\",\n" +
            "      \"destinationSignal\": \"getValue\",\n" +
            "      \"communicationType\": \"synchronous\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private String KNOWLEDGE_ON_DESIGN_PROPERTIES = "Properties of Design are of the following types\n" +
            "- A<>expr means that all states of all paths must respect expr\n" +
            "- A[]expr means that all states of at least one path must respect expr\n" +
            "- E<>expr means that one state of all paths must respect expr\n" +
            "- E[]expr means that one state of one path must respect expr\n" +
            "expr is a boolean expression using either attributes of blocks or blocks states";

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
                TraceManager.addDev("Invalid JSON spec: " + extractJSON() + " because " + e.getMessage());
                done = true;
                errors = new ArrayList<>();
                errors.add(e.getMessage());
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

            cpt++;
        }

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
            TraceManager.addDev("Knowledge added: " + KNOWLEDGE_STAGES[chatData.knowledgeOnBlockJSON]);
            chatData.aiinterface.addKnowledge(KNOWLEDGE_STAGES[chatData.knowledgeOnBlockJSON], "ok");
        }
    }


}
