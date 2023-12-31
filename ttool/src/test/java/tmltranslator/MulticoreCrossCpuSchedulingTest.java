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
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MulticoreCrossCpuSchedulingTest extends AbstractTest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_CPU_CROSS = {"testMultiCoreRRPB", "testMultiCoreRR"};
    private String SIM_DIR;
    final int [] NB_OF_CS_STATES = {9, 9};
    final int [] NB_OF_CS_TRANSTIONS = {8, 8};
    final int [] MIN_CS_CYCLES = {57, 58};
    final int [] MAX_CS_CYCLES = {57, 58};
    static final String [] EXPECTED = {
            "MULTICORE: assign transaction Application__C1: Execi 15 t:0 l:15 (vl:15) params: to core 0\n" +
            "MULTICORE: assign transaction Application__S: Execi 16 t:0 l:16 (vl:16) params: to core 1\n" +
            "MULTICORE: assign transaction Application__S: Send Application__evt__Application__evt(evtFB) len:8 content:0 params: t:16 l:1 (vl:1) params: Ch: Application__evt__Application__evt to core 1\n" +
            "MULTICORE: assign transaction Application__C0: Execi 10 t:15 l:10 (vl:10) params: to core 0\n" +
            "MULTICORE: assign transaction Application__C0: Wait Application__evt__Application__evt params: t:25 l:1 (vl:1) params: Ch: Application__evt__Application__evt to core 0\n" +
            "MULTICORE: assign transaction Application__C0: Execi 15 t:26 l:15 (vl:15) params: to core 0\n" +
            "MULTICORE: assign transaction Application__C3: Execi 40 t:17 l:40 (vl:40) params: to core 1\n",

            "MULTICORE: assign transaction Application__C3: Execi 40 t:0 l:40 (vl:40) params: to core 0\n" +
            "MULTICORE: assign transaction Application__C0: Execi 10 t:0 l:10 (vl:10) params: to core 1\n" +
            "MULTICORE: assign transaction Application__C1: Execi 15 t:10 l:15 (vl:15) params: to core 1\n" +
            "MULTICORE: assign transaction Application__S: Execi 16 t:25 l:16 (vl:16) params: to core 1\n" +
            "MULTICORE: assign transaction Application__S: Send Application__evt__Application__evt(evtFB) len:8 content:0 params: t:41 l:1 (vl:1) params: Ch: Application__evt__Application__evt to core 1\n" +
            "MULTICORE: assign transaction Application__C0: Wait Application__evt__Application__evt params: t:42 l:1 (vl:1) params: Ch: Application__evt__Application__evt to core 0\n" +
            "MULTICORE: assign transaction Application__C0: Execi 15 t:43 l:15 (vl:15) params: to core 0\n"
    };
    static String CPP_DIR = "../../../../simulators/c++2/";
    static String mappingName = "Architecture";
    private String actualResult;
    private TMLArchiDiagramPanel currTdp;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public MulticoreCrossCpuSchedulingTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test(timeout = 600000)
    public void testMulticoreNotHangingWhenSaveTrace() throws Exception {
        for (int i = 0; i < MODELS_CPU_CROSS.length; i++) {
            actualResult = "";
            String s = MODELS_CPU_CROSS[i];
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
                    if (str.contains("MULTICORE: assign transaction")) {
                        actualResult += str + "\n";
                    }
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
                assertTrue(false);
            }

            AUTGraph graph = new AUTGraph();
            graph.buildGraph(graphData);

            // States and transitions
            System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates());
            assertTrue(NB_OF_CS_STATES[i] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
            assertTrue(NB_OF_CS_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue);
            assertTrue(MIN_CS_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue);
            assertTrue(MAX_CS_CYCLES[i] == maxValue);

            // compare which transaction belong to which core
            System.out.println("Scheduling on " + s + "\n" + actualResult);
            assertTrue(EXPECTED[i].equals(actualResult));
        }
    }
}
