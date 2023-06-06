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
 * Class AIReqIdent
 *
 * Creation: 06/06/2023
 * @version 1.0 06/06/2023
 * @author Ludovic APVRILLE
 */


public class AIReqIdent extends AIInteract {

    private String QUESTION_IDENTIFY_REQ = "Identify all the relevant requirements of the following specification. List them as a json array with " +
            "the following elements for each requirements in the array:" +
            " " +
            "name: name of the requirement, id: id of the requirement (as a string), doc: text of the requirement  " +
            "compose: all req names, derive: all req names, refine: all req names. The name " +
            "should be an english " +
            "name and not a number or an identifier. Identify the relations (compose, derive, refine) even if they are not given in the " +
            "specification. Use the name of requirements and not the id in the list of relations.";


    public AIReqIdent(AIChatData _chatData) {
        super(_chatData);
    }

    public void internalRequest() {
        String questionT = "\nTTool:" + QUESTION_IDENTIFY_REQ + "\n" + chatData.lastQuestion.trim()+ "\n";
        boolean ok = makeQuestion(questionT);
    }

    public Object applyAnswer(Object input) {
        return null;
    }

	
    
}
