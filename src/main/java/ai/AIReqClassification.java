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


/**
 * Class AIReqClassification
 * <p>
 * Creation: 06/06/2023
 *
 * @author Ludovic APVRILLE
 * @version 1.0 06/06/2023
 */


public class AIReqClassification extends AIInteract implements AISysMLV2DiagramContent {

    private static String QUESTION_CLASSIFY_REQ = "I would like to identify the \"type\" attribute, i.e. the classification, " +
            "of the following requirements. Could you give me a correct type among: safety, security, functional, " +
            "non-functional, performance, business, stakeholder need. if the main category is security, you can use the " +
            "following sub categories: privacy, confidentiality, non-repudiation, controlled access, availability," +
            "immunity, data origin authenticity, freshness. Use the following format for the answer:" +
            " - Requirement name: classification\n";

    private static String[] SUPPORTED_DIAGRAMS = {"RD"};
    private static String[] EXCLUSIONS_IN_INPUT = {"type"};

    private String diagramContent;


    public AIReqClassification(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {
        String questionT;
        if (diagramContent != null) {
            questionT = "\nTTool:" + QUESTION_CLASSIFY_REQ + "\n" + diagramContent + "\n";
        } else {
            questionT = "\nTTool:" + QUESTION_CLASSIFY_REQ + "\n" + chatData.lastQuestion.trim() + "\n";
        }
        boolean ok = makeQuestion(questionT);

    }

    // input: name of requirement ; output: name of kind or null if kind not found
    public Object applyAnswer(Object input) {
        if (!(input instanceof String)) {
            return null;
        }
        String reqName = (String) input;
        String query = reqName + ":";
        int index = chatData.lastAnswer.indexOf(query);
        if (index == -1) {
            return null;
        }


        String kind = chatData.lastAnswer.substring(index + query.length()).trim();
        //TraceManager.addDev("Kind=" + kind);
        int indexSpace = kind.indexOf("\n");
        int indexSpace1 = kind.indexOf(" ");
        String kTmp;
        if ((indexSpace1 > -1) || (indexSpace > -1)) {
            if ((indexSpace1 > -1) && (indexSpace > -1)) {
                indexSpace = Math.min(indexSpace, indexSpace1);
                kTmp = kind.substring(0, indexSpace);
            } else if (indexSpace1 > -1) {
                kTmp = kind.substring(0, indexSpace1);
            } else {
                kTmp = kind.substring(0, indexSpace);
            }
        } else {
            kTmp = kind;
        }
        return kTmp;
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

}
