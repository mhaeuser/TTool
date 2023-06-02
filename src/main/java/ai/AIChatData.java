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


import myutil.AIInterface;
import myutil.TraceManager;
import ui.TDiagramPanel;
import ui.util.IconManager;

import javax.swing.*;

/**
 * Class AIChatData
 *
 * Creation: 02/06/2023
 * @version 1.0 02/06/2023
 * @author Ludovic APVRILLE
 */


public class AIChatData implements Runnable {

   public static String KNOWLEDGES_ON_JSON_FOR_BLOCKS_AND_ATTRIBUTES = "When you are asked to identify SysML blocks, return them as a JSON specification" +
            " " +
            "formatted as follows:" +
            "{blocks: [{ \"name\": \"Name of block\", \"attributes\": [\"name\": \"name of attribute\", \"type\": \"int or bool\" ...} ...]" +
            "Use only attributes of type int or boolean. If you want to use \"String\" or another other attribute, use int.";

    public AIInterface aiinterface;
    public boolean knowledgeOnProperties = false;
    public boolean knowledgeOnBlockJSON = false;
    public boolean knowledgeOnAMULET = false;
    public AIFeedback feedback;
    public String lastAnswer = "";
    public int previousKind;
    private Thread t;

    public AIChatData() {

    }

    public void clear() {
        lastAnswer = "";
        feedback.setAnswerText("");
    }

    public void startRunning() {
        feedback.setRunning(true);
        t = new Thread(this);
        t.start();
    }

    public void stopRunning() {
        feedback.setRunning(false);
        if (t != null) {
            t.interrupt();
        }
        t = null;
    }

    public void run() {
        int angle = 0;

        while (feedback.mustRun()) {
            feedback.running();
        }
        feedback.reinitRunning();
    }
	
    
}
