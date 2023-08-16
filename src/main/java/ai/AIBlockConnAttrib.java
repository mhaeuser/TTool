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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class AIBlockConnAttrib
 * <p>
 * Creation: 01/08/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 01/08/2023
 */


public class AIBlockConnAttrib extends AIInteract {


    public static String KNOWLEDGE_ON_JSON_FOR_BLOCKS = "When you are asked to identify SysML blocks and their connections, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{blocks: [{ \"name\": \"Name of block\"...]}" +
            "# Respect: Any block identifier must no contain any space. Use \"_\" instead if necessary.";

    public static String KNOWLEDGE_ON_JSON_FOR_CONNECTIONS = "When you are asked to identify SysML blocks and their connections, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{connections: [{ \"signal\": \"signal name\", \"nameOriginBlock\": \"Name of origin block\", \"nameDestinationBlock\": \"Name of " +
                    "destination block\"} ...]}" +
            "# Respect: do not provide a list of blocks, they are defined with the connections only. " +
            "# Respect: a signal name is the form: name(list of int and bool attributes), for instance: sig1(int x, bool b). sig1(x) is not valid. " +
            "# Respect: a signal name can be used in only one connection. " +
            "# Respect: Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int." +
            "# Respect: each attribute must be of type \"int\" or \"bool\" only" +
            "# Respect: origin and destination block can be the same" +
            "# Respect: Any identifier (block, attribute, etc.) must no contain any space. Use \"_\" instead.";
    public static String KNOWLEDGE_ON_JSON_FOR_ATTRIBUTES = "When you are asked to identify the attributes of SysML blocks, " +
            "return them as a JSON specification " +
            "formatted as follows:" +
            "{blocks: [{ \"name\": \"Name of block\", \"attributes\": [\"name\": \"name of attribute\", \"type\": \"int or bool\" ...} ...]}" +
            "Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int." +
            "# Respect: each attribute must be of type \"int\" or \"bool\" only" +
            "# Respect: Any identifier of blocks must no contain any space. Use \"_\" instead.";



    public static String[] KNOWLEDGE_STAGES = {KNOWLEDGE_ON_JSON_FOR_BLOCKS, KNOWLEDGE_ON_JSON_FOR_CONNECTIONS, KNOWLEDGE_ON_JSON_FOR_ATTRIBUTES};
    AvatarSpecification specification, specification0;
    private String[] QUESTION_IDENTIFY_SYSTEM_BLOCKS = {"From the following system specification, using the specified JSON format, identify the " +
            "typical system blocks. Do respect the JSON format, and provide only JSON (no explanation before or after).\n",
            "From the following system specification, using the specified JSON format, identify the " +
            "typical system blocks and their connections. Do respect the JSON format, and provide only JSON (no explanation before or after).\n",
            "From the previous JSON and system specification, find the typical attributes of all blocks by imagining all the necessary attributes " +
                    "that would be needed for the state machine diagram of each block. "};

    public String namesOfBlocks = "";

    public AIBlockConnAttrib(AIChatData _chatData) {
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
            ArrayList<String> errors = null;
            try {
                //TraceManager.addDev("Making specification from " + chatData.lastAnswer);
                if (stage == 0) {
                    namesOfBlocks = "";
                    errors = new ArrayList<>();
                    namesOfBlocks = getBlockNames(extractJSON(), errors);
                    TraceManager.addDev("Names of blocks: " + namesOfBlocks);
                    if (namesOfBlocks.length() == 0) {
                        errors.add("You must give the name of at least one block");
                    }
                } else if (stage == 1) {
                    specification0 = AvatarSpecification.fromJSONConnection(extractJSON(), "design", null, true);
                    errors = AvatarSpecification.getJSONErrors();
                } else if (stage == 2) {
                    specification = AvatarSpecification.fromJSON(extractJSON(), "design", null, true);
                    if (specification != null) {
                        specification.addSignalsAndConnection(specification0);
                        specification.makeMinimalStateMachines();
                        specification.improveNames();
                    }
                    //TraceManager.addDev("Full spec: " + specification);
                    errors = AvatarSpecification.getJSONErrors();
                }

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
                //TraceManager.addDev(" Avatar spec=" + specification);
                stage++;
                if (stage == KNOWLEDGE_STAGES.length) {
                    done = true;
                } else {
                    makeKnowledge(stage);
                    questionT = QUESTION_IDENTIFY_SYSTEM_BLOCKS[stage] + chatData.lastQuestion.trim();
                    if (namesOfBlocks.length() > 0) {
                        questionT += "\nThe blocks to be used are: " + namesOfBlocks.trim();
                    }
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
        chatData.aiinterface.clearKnowledge();

        String [] know = KNOWLEDGE_STAGES[stage].split("#");
        for(String s: know) {
            TraceManager.addDev("\nKnowledge added: " + s);
            chatData.aiinterface.addKnowledge(s, "ok");
        }

    }

    private String getBlockNames(String _spec, ArrayList<String> _errors) throws org.json.JSONException {
        int indexStart = _spec.indexOf('{');
        int indexStop = _spec.lastIndexOf('}');

        if ((indexStart == -1) || (indexStop == -1) || (indexStart > indexStop)) {
            throw new org.json.JSONException("Invalid JSON object (start)");
        }

        _spec = _spec.substring(indexStart, indexStop + 1);

        //TraceManager.addDev("Cut spec: " + _spec);

        JSONObject mainObject = new JSONObject(_spec);

        JSONArray blocks = mainObject.getJSONArray("blocks");

        if (blocks == null) {
            TraceManager.addDev("No \"blocks\" array in json");
            _errors.add("No \"blocks\" array in json");
            return "";
        }

        String listOfblocks = "";
        for (int i = 0; i < blocks.length(); i++) {
            JSONObject block = blocks.getJSONObject(i);
            String blockName = AvatarSpecification.removeSpaces(block.getString("name"));
            if (blockName != null) {
                listOfblocks += blockName + " ";
            }
        }

        return listOfblocks;
    }




}
