/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class AvatarPragma
 * Creation: 20/05/2010
 * @version 1.1 01/07/2014
 * @author Ludovic APVRILLE, Raja GATGOUT
 * @see
 */

package cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import graph.AUTTransition;
import myutil.Conversion;
import myutil.TraceManager;
import org.junit.Test;
import test.AbstractTest;


public class CLIAvatarModelCheckerTest extends AbstractTest implements InterpreterOutputInterface {

    final static String PATH_TO_TEST_FILE = "cli/input/";
    final static String PATH_TO_EXPECTED_FILE = "cli/expected/";
    private StringBuilder outputResult;

    private static final String DEADLOCKS [] = {"#MainBlock", "-stop", "#Receiver", "-stopCreated", "-MainChoice"};
	
	public CLIAvatarModelCheckerTest() {
	    //
    }
	

    public void exit(int reason) {
	    System.out.println("Exit reason=" + reason);
	    assertTrue(reason == 0);
    }

    public void printError(String error) {
        System.out.println("Error=" + error);
    }

    public void print(String s) {
	    System.out.println("info from interpreter:" + s);
	    outputResult.append(s);
    }
	
	@Test
	public void testTranslationAndModelChecking() {
	    String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker";
	    String script;
	    
	    outputResult = new StringBuilder();

	    File f = new File(filePath);
	    assertTrue(myutil.FileUtils.checkFileForOpen(f));

	    script = myutil.FileUtils.loadFileData(f);

	    assertTrue(script.length() > 0);

	    boolean show = false;
        Interpreter interpret = new Interpreter(script, this, show);
        interpret.interpret();

        // Must now load the graph
        filePath = "rgmodelchecker.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" +
                graph.getNbOfTransitions());
        assertTrue(graph.getNbOfStates() == 14);
        assertTrue(graph.getNbOfTransitions() == 16);


        // Graph minimization


	}


	
	@Test
    public void testStateLimitCoffeeMachine() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_n";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must now load the graph
        filePath = "rgmodelchecker.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());
        assertTrue(graph.getNbOfStates() == 12);
        assertTrue(graph.getNbOfTransitions() > 10);
    }
	
	@Test
    public void testReachabilityLivenessCoffeeMachine() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_rl";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must now load the graph
        filePath = "rgmodelchecker.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();
        
        filePath = getBaseResourcesDir() + PATH_TO_EXPECTED_FILE + "modelchecker_rl_expected";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String expectedOutput = myutil.FileUtils.loadFileData(f);

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());
        assertTrue(graph.getNbOfStates() == 14);
        assertTrue(graph.getNbOfTransitions() == 16);

        System.out.println("\nExpected:>" + expectedOutput + "<");
        System.out.println("\nObtained:>" + outputResult.toString() + "<");

        String s1 = reworkStringForComparison(expectedOutput);
        String s2 = reworkStringForComparison(outputResult.toString());
        assertEquals(s1, s2);
    }
	
	@Test
    public void testReachabilityLivenessSafetyAirbusDoor_V2() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_s";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();
        
     // Must now load the graph
        filePath = "rgmodelchecker.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);
        
        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        filePath = getBaseResourcesDir() + PATH_TO_EXPECTED_FILE + "modelchecker_s_expected";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String expectedOutput = myutil.FileUtils.loadFileData(f);

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());
        assertTrue(graph.getNbOfStates() == 251);
        assertTrue(graph.getNbOfTransitions() > 700);
        assertTrue(graph.getNbOfTransitions() < 770);

        String s1 = expectedOutput.trim();
        String s2 = outputResult.toString().trim();

        System.out.println("TEST expected=\t>" + s1 + "<\nTEST output:\t>" + s2 + "<");

        // Rework string
        s1 = reworkStringForComparison(s1);
        s2 = reworkStringForComparison(s2);

        //System.out.println("TEST expected=\t>" + s1 + "<\nTEST output:\t>" + s2 + "<");

       /*for(int i=0; i<s1.length(); i++) {
           System.out.println(i + "\t" + s1.substring(i, i+1) + " " + s2.substring(i, i+1));
       }*/
        
        assertTrue(s1.equals(s2));
    }

    @Test
    public void testCoffeeMachine_NoTime() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_s_notime";
        String script;

        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must now load the graph
        filePath = "rgmodelchecker_notime.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());

        ArrayList<AUTTransition> trs = graph.getTransitions();
        for(AUTTransition tr: trs) {
            //System.out.println("label:" + tr.transition);
            assertTrue(tr.transition.endsWith("[0...0]"));
        }
    }

    @Test
    public void testCoffeeMachine_Time() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_s_time";
        String script;

        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        // Must now load the graph
        filePath = "rgmodelchecker_time.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        System.out.println("states=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());

        //At least one state with different time
        ArrayList<AUTTransition> trs = graph.getTransitions();
        int cpt = 0;
        for(AUTTransition tr: trs) {
            //System.out.println("label:" + tr.transition);
           if (!tr.transition.endsWith("[0...0]")) {
               cpt ++;
           }
        }
        System.out.println("Nb of transition with non-zero time: " + cpt);
        assertTrue(cpt >= 1);
    }
	
	@Test
	public void testValidateCoffeeMachine() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_val1";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);
        
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, false);       
        interpret.interpret();
        
        assertTrue(outputResult.toString().contains("true"));
    }
	
	@Test
    public void testValidateAirbusDoor_V2() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_val2";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);
        
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, false);       
        interpret.interpret();
        
        assertTrue(outputResult.toString().contains("true"));
    }
	
	@Test
    public void testValidatePressureController() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_val3";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);
        
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, false);       
        interpret.interpret();
        
        assertTrue(outputResult.toString().contains("true"));
    }
	
	@Test
    public void testCoffeeMachineAsync() {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_val4";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);
        
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, false);       
        interpret.interpret();
        
        assertTrue(outputResult.toString().contains("true"));
    }
	
	@Test
    public void testCliCustomQuery () {
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelchecker_q";
        String script;
        
        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);
        
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, false);       
        interpret.interpret();
        
        filePath = getBaseResourcesDir() + PATH_TO_EXPECTED_FILE + "modelchecker_q_expected";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String expectedOutput = myutil.FileUtils.loadFileData(f);

        assertEquals(expectedOutput, outputResult.toString()+"\n");
    }

    @Test
    public void testAdvancedRandom() {
	    System.out.println("advanced random model checker test");
        String filePath = getBaseResourcesDir() + PATH_TO_TEST_FILE + "scriptmodelcheckerrandom";
        String script;

        outputResult = new StringBuilder();

        File f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();
        System.out.println("Graph generated");

        // Must now load the graph
        filePath = "rgmodelcheckerrandom.aut";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));
        String data = myutil.FileUtils.loadFileData(f);

        assertTrue(data.length() > 0);
        AUTGraph graph = new AUTGraph();
        graph.buildGraph(data);
        graph.computeStates();

        System.out.println("random Cstates=" + graph.getNbOfStates() + " transitions=" + graph.getNbOfTransitions());
        assertTrue(graph.getNbOfStates() == 49);
        assertTrue(graph.getNbOfTransitions() == 48);

        filePath = "deadlockmodel.txt";
        f = new File(filePath);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        BufferedReader reader;
        String block = "";
        String state = "";
        try {
            reader = new BufferedReader(new FileReader("deadlockmodel.txt"));
            String line = "";
            while (line != null) {
                // read next line
                line = reader.readLine();
                if (line  == null) {
                    break;
                }
                TraceManager.addDev("line of file=" + line);
                if (line.startsWith("#")) {
                    block = line.trim();
                    TraceManager.addDev("Block=" + block);
                }
                if (line.startsWith("-")) {
                    state = line.trim();
                    assertTrue(checkNames(block, state));
                }

            }
            reader.close();
        } catch (IOException e) {
            assertTrue(false);
        }



    }

    private boolean checkNames(String blockName, String stateName) {
        TraceManager.addDev("Checking for block=" + blockName + " state=" + stateName);
        boolean blockFound = false;
        for(String s: DEADLOCKS) {
            TraceManager.addDev("s=" + s);
            if (s.compareTo(blockName) == 0) {
                blockFound = true;
                TraceManager.addDev("Found block");
            } else if (blockFound) {
                if (s.startsWith("#")) {
                    return false;
                }
                if (s.compareTo(stateName) == 0) {
                    TraceManager.addDev("s found");
                    return true;
                }
            }
        }
        return false;
    }

}
