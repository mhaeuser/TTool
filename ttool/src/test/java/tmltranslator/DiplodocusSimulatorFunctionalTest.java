package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.Conversion;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusSimulatorFunctionalTest extends AbstractTest {


    private final String [] TMAP_MODELS = {
            "simple1",
            "PrioScheds_basic",
            "Test_Busses_med",
            "Test_BusSlices",
            "Test_delay_param",
            "Test_TruncateExe",
            "Test_TruncateWrite_0",
            "Test_TruncateWrite_1",
            "Test_TruncateWrite_2",
    };
    private final String[] [] EXPECTED_TRACES = {
            {"simple1_out1.txt", "simple1_out2.txt"},
            {"PrioScheds_basic_out.txt"},
            {"Test_Busses_med_out.txt"},
            {"Test_BusSlices_out.txt"},
            {"Test_delay_param_out.txt"},
            {"Test_TruncateExe_out.txt"},
            {"Test_TruncateWrite_0_out.txt"},
            {"Test_TruncateWrite_1_out.txt"},
            {"Test_TruncateWrite_2_out.txt"}
    };

    final String DIR_GEN = "test_diplo_functional_simulator/";
    private String SIM_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/functional/";

    }

    public DiplodocusSimulatorFunctionalTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }


    @Test
    public void testSimulationGraph() throws Exception {
        for(int i=0; i<TMAP_MODELS.length; i++) {
            String s = TMAP_MODELS[i];
            SIM_DIR = DIR_GEN + s + "/";
            // Load the TML
            System.out.println("executing: loading " + s);
            TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(s);
            File f = new File(RESOURCES_DIR + s + ".tmap");
            System.out.println("executing: new file loaded " + s + " path to file: " + f.getAbsolutePath());
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                System.out.println("Exception executing: loading " + s);
                assertTrue(false);
            }

            System.out.println("executing: loading done for " + s);


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

            // Generate C++ code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertTrue(error == null);

            File directory = new File(SIM_DIR);
            if (! directory.exists()){
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() +  "../../../../simulators/c++2/";
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
                    proc_in = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
                    while ( ( str = proc_in.readLine() ) != null ) {
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
                proc_in = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );

                monitorError(proc);

                while ( ( str = proc_in.readLine() ) != null ) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing: " + "make -C " + SIM_DIR);
                return;
            }
            System.out.println("SUCCESS: executing: " + "make -C " + SIM_DIR);

            // Run the simulator
            String graphPath = SIM_DIR + "testgraph_" + s;
            try {

                String[] params = new String [4];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-otxt";
                params[2] = "" + TMAP_MODELS[i] + "_out.txt";
                params[3] = graphPath;
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );

                monitorError(proc);

                while ( ( str = proc_in.readLine() ) != null ) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the file, and compare it with the possible outputs

            String traceFileS = FileUtils.loadFile(TMAP_MODELS[i] + "_out.txt");
            traceFileS = Conversion.replaceAllChar(traceFileS, ' ', "");

            boolean found = false;
            for(int j=0; j<EXPECTED_TRACES[i].length; j++) {
                String currentFileName = RESOURCES_DIR + EXPECTED_TRACES[i][j];
                System.out.println("executing: loading golden model: " + currentFileName);
                String goldenModel = FileUtils.loadFile(currentFileName);
                goldenModel = Conversion.replaceAllChar(goldenModel, ' ', "");
                if (goldenModel.compareTo(traceFileS) != 0) {
                    found = true;
                    break;
                }
            }

            if (found) {
                System.out.println("Test TMAP_MODELS[i]: KO");
            } else {
                System.out.println("Test TMAP_MODELS[i]: OK");
            }
            assertFalse(found);
        }

    }


}
