package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import test.AbstractTest;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AbstractUITest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DiplodocusInsertSignalTest extends AbstractTest {

    final String DIR_GEN = "test_diplo_simulator/";
    // test for inserting signals
    private static final String [] MODELS_SIGNALS = {"signal"};
    private static final int [] NB_Of_SIGNAL_STATES = {14};
    private static final int [] NB_Of_SIGNAL_TRANSTIONS = {13};
    private static final int [] MIN_SIGNAL_CYCLES = {35};
    private static final int [] MAX_SIGNAL_CYCLES = {35};

    private static final String CPP_DIR = "../../../../simulators/c++2/";
    private String SIM_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }

    public DiplodocusInsertSignalTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test
    public void testInsertSignals() throws Exception {
        for (int i = 0; i < MODELS_SIGNALS.length; i++) {
            String s = MODELS_SIGNALS[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: loading " + s);
            TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(s);
            File f = new File(RESOURCES_DIR + s + ".tmap");
            System.out.println("executing: new file loaded " + s);
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                System.out.println("Exception executing: loading " + s);
                assertTrue(false);
            }
            System.out.println("executing: testing spec " + s);
            assertTrue(spec != null);
            System.out.println("executing: testing parsed " + s);
            boolean parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);

            System.out.println("executing: checking syntax " + s);
            // Checking syntax
            TMLMapping tmap = tmts.getTMLMapping();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);
            // Generate SystemC code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertTrue(error == null);

            File directory = new File(SIM_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + CPP_DIR;
            boolean simFiles = SpecConfigTTool.checkAndCreateSystemCDir(SIM_DIR);
            File signals = new File(RESOURCES_DIR  + "signals.txt");
            File f_signal = new File(SIM_DIR);
            FileUtils.copyFileToDirectory(signals, f_signal, false);


            System.out.println("SIM executing: sim lib code copying done with result " + simFiles);
            assertTrue(simFiles);

            System.out.println("SIM Saving file in: " + SIM_DIR);
            tml2systc.saveFile(SIM_DIR, "appmodel");

            // Compile it
            System.out.println("executing: compile");
            Process proc;
            BufferedReader proc_in;
            String str;
            boolean mustRecompileAll;
            Penalties penalty = new Penalties(SIM_DIR + File.separator + "src_simulator");
            int changed = penalty.handlePenalties(false);

            if (changed == 1) {
                mustRecompileAll = true;
            } else {
                mustRecompileAll = false;
            }

            if (mustRecompileAll) {
                System.out.println("executing: " + "make -C " + SIM_DIR + " clean");
                try {
                    proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + " clean");
                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((str = proc_in.readLine()) != null) {
                        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                        System.out.println("executing: " + str);
                    }
                } catch (Exception e) {
                    // probably make is not installed
                    System.out.println("FAILED: executing: " + "make -C " + SIM_DIR + " clean");
                    return;
                }
            }

            System.out.println("executing: " + "make -C " + SIM_DIR);
            try {

                proc = Runtime.getRuntime().exec("make -C " + SIM_DIR + "");
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing: " + "make -C " + SIM_DIR);
                return;
            }
            System.out.println("SUCCESS: executing: " + "make -C " + SIM_DIR);

            // Run the simulator and test insert signal by file
            String graphPath = DIR_GEN+"../" + "test";
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-signals";
                params[2] = SIM_DIR + "signals.txt";
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the graph
            File graphFile = new File(graphPath + ".aut");
            String graphData = "";
            try {
                graphData = FileUtils.loadFileData(graphFile);
            } catch (Exception e) {
                assertTrue(false);
            }

            AUTGraph graph = new AUTGraph();
            graph.buildGraph(graphData);

            // States and transitions
            System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates());
            assertTrue(NB_Of_SIGNAL_STATES[i] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
            assertTrue(NB_Of_SIGNAL_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue);
            assertTrue(MIN_SIGNAL_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue);
            assertTrue(MAX_SIGNAL_CYCLES[i] == maxValue);


            //test Insert signal by command
            graphPath = SIM_DIR + "testgraph_" + s + "_cmd";
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 5 10; 1 16 evt1to2 1 (101); 1 5 21; 1 16 evt1to2 1 (50); 1 0;1 7 100 100 " + graphPath;
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the graph
            graphFile = new File(graphPath + ".aut");
            graphData = "";
            try {
                graphData = FileUtils.loadFileData(graphFile);
            } catch (Exception e) {
                assertTrue(false);
            }

            graph = new AUTGraph();
            graph.buildGraph(graphData);

            // States and transitions
            System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates());
            assertTrue(NB_Of_SIGNAL_STATES[i] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
            assertTrue(NB_Of_SIGNAL_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue);
            assertTrue(MIN_SIGNAL_CYCLES[i] == minValue);

            maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue);
            assertTrue(MAX_SIGNAL_CYCLES[i] == maxValue);
        }
    }

}
