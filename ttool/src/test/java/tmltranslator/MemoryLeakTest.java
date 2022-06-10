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
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AbstractUITest;
import ui.TDiagramPanel;
import ui.TMLArchiPanel;
import ui.TURTLEPanel;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MemoryLeakTest extends AbstractTest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_MEMORY_LEAK = {"fpga_reconfig5", "fpga_reconfig6"};
    private String SIM_DIR;
    final int [] NB_OF_ML_STATES = {21, 21};
    final int [] NB_OF_ML_TRANSTIONS = {20, 20};
    final int [] MIN_ML_CYCLES = {55, 56};
    final int [] MAX_ML_CYCLES = {55, 56};
    static String CPP_DIR = "../../../../simulators/c++2/";
    static String valgrindVersionCmd = "valgrind --version";
    static String valgrindExecCmd = "valgrind --leak-check=full --log-file=";
    static String EXPECTED_OUTPUT = "ERROR SUMMARY: 0 errors from 0 contexts (suppressed: 0 from 0)"; // different valgrind version can lead to different output but the ERROR SUMMARY should be the same

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public MemoryLeakTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test
    public void testMemoryLeak() throws Exception {
        for (int i = 0; i < MODELS_MEMORY_LEAK.length; i++) {
            String s = MODELS_MEMORY_LEAK[i];
            SIM_DIR = DIR_GEN + s + "_memoryLeak/";
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

            if (syntax.hasErrors() > 0) {
                for (TMLError error: syntax.getErrors()) {
                    System.out.println("Error: " + error.toString());
                }

            }

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
            // check if valgrind installed or not
            boolean isValgrindInstalled = false;
            try {
                proc = Runtime.getRuntime().exec(valgrindVersionCmd);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
                isValgrindInstalled = true;
            } catch (Exception e) {
                System.out.println("FAILED: valgrind is not installed");
            }

            //run test with valgrind
            if(isValgrindInstalled) {
                String logPath = SIM_DIR + "valgrind.log";
                proc = Runtime.getRuntime().exec(valgrindExecCmd + logPath + " ./" + SIM_DIR + "run.x" + " -cmd 1 0");
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing valgrind: " + str);
                }

                boolean errorFound = true;
                String sCurrentLine = "";
                BufferedReader br = new BufferedReader(new FileReader(logPath));
                while ((sCurrentLine = br.readLine()) != null) {
                    if(sCurrentLine.contains(EXPECTED_OUTPUT)) {
                        errorFound = false;
                        System.out.println(sCurrentLine);
                    }
                }

                assertFalse(errorFound);// no error and memory leak

            } else {
                //valgrind is not installed, so run test without it
                String graphPath = SIM_DIR + "testgraph_" + s;
                try {

                    String[] params = new String[3];

                    params[0] = "./" + SIM_DIR + "run.x";
                    params[1] = "-cmd";
                    params[2] = "1 0; 1 7 100 100 " + graphPath;
                    proc = Runtime.getRuntime().exec(params);
                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                    monitorError(proc);

                    while ((str = proc_in.readLine()) != null) {
                        // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                        System.out.println("executing: " + str);
                    }
                } catch (Exception e) {
                    // Probably make is not installed
                    System.out.println("FAILED: executing simulation " + e.getCause());
                    return;
                }

                File graphFile = new File(graphPath + ".aut");
                String graphData = "";
                try {
                    graphData = FileUtils.loadFileData(graphFile);
                } catch (Exception e) {
                    fail();
                }

                AUTGraph graph = new AUTGraph();
                graph.buildGraph(graphData);

                // States and transitions
                System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates());
                assertEquals(NB_OF_ML_STATES[i], graph.getNbOfStates());
                System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
                assertEquals(NB_OF_ML_TRANSTIONS[i], graph.getNbOfTransitions());

                // Min and max cycles
                int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
                System.out.println("executing: minvalue of " + s + " " + minValue);
                assertEquals(MIN_ML_CYCLES[i], minValue);

                int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
                System.out.println("executing: maxvalue of " + s + " " + maxValue);
                assertEquals(MAX_ML_CYCLES[i], maxValue);
            }
        }
    }
}
