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

/**
 * Class AIBlock
 *
 * Creation: 02/06/2023
 * @version 1.0 02/06/2023
 * @author Ludovic APVRILLE
 */


public class AIBlock extends AIInteract {

    AvatarSpecification specification;

   public static String KNOWLEDGES_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES = "When you are asked to identify SysML blocks, " +
           "return them as a JSON specification " +
            "formatted as follows:" +
            "{blocks: [{ \"name\": \"Name of block\", \"attributes\": [\"name\": \"name of attribute\", \"type\": \"int or bool\" ...} ...]" +
            "Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int.";


    private String QUESTION_IDENTIFY_SYSTEM_BLOCKS = "From the following system specification, using the specified JSON format, identify the " +
            "typical system blocks and their attributes. Do respect the JSON format.\n";


    public AIBlock(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {

        String questionT = QUESTION_IDENTIFY_SYSTEM_BLOCKS + "\n" + chatData.lastQuestion.trim() + "\n";
        if (!chatData.knowledgeOnBlockJSON) {
            chatData.knowledgeOnBlockJSON = true;
            chatData.aiinterface.addKnowledge(KNOWLEDGES_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES, "ok");
        }

        boolean done = false;
        int cpt = 0;

        while (!done) {
            boolean ok = makeQuestion(questionT);
            if (!ok) {
                done = true;
            }
            try {
                specification = AvatarSpecification.fromJSON(chatData.lastAnswer, "design", null);
                TraceManager.addDev(" Avatar spec=" + specification);
                done = true;
            } catch (org.json.JSONException e) {
                done = true;
            }
        }

    }

    public Object applyAnswer(Object input) {
        if (input == null) {
            return specification;
        }

        return specification;
    }

	
    
}
