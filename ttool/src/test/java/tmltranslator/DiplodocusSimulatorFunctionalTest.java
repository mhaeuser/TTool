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
import java.util.Collection;
import java.util.HashMap;
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


    //@Test
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
                String goldenModel = FileUtils.loadFile(currentFileName).trim();
                goldenModel = Conversion.replaceAllString(goldenModel.trim(), "  ", " ");
                traceFileS = Conversion.replaceAllString(traceFileS.trim(), "  ", " ");

                //goldenModel = Conversion.replaceAllChar(goldenModel,   ' ', "");
                boolean equal = areTracesEqual(goldenModel, traceFileS);
                if (equal) {
                    found = true;
                    break;
                }
            }

            if (found) {
                System.out.println("Test TMAP_MODELS[i]: OK");
            } else {
                System.out.println("Test TMAP_MODELS[i]: KO");
            }

            assertTrue(found);
        }

    }

    @Test
    public void testTraceEqual() throws Exception {
        boolean b1 = areTracesEqual("coucou ID:3 progress", "coucou ID:34 progress");
        assertTrue(b1);
        boolean b2 = areTracesEqual("coucou ID=3 progress", "coucou ID=34 progress");
        assertFalse(b2);
        boolean b3 = areTracesEqual("coucou ID:3 progress ID:3 p", "coucou ID:34 progress ID:35 p");
        assertFalse(b3);
        boolean b4 = areTracesEqual("coucou ID:3 progress ID:3 p", "coucou ID:34 progress ID:34 p");
        assertTrue(b4);
        boolean b5 = areTracesEqual("coucou ID:3 progress ID:12 p", "coucou ID:34 progress ID:34 p");
        assertFalse(b5);
        boolean b6 = areTracesEqual("coucou ID:3 progress ID:12 p", "coucou ID:34 progress ID:35 p");
        assertTrue(b6);
    }

    private boolean areTracesEqual(String s1, String s2) {
        HashMap<Integer, Integer> mapOfIDs = new HashMap<>();

        int cpt1=0, cpt2=0;
        while( (cpt1<s1.length()) && (cpt2<s2.length()) ) {
            System.out.println("cpt1=" + cpt1 + " cpt2=" + cpt2);
            String ts1 = s1.substring(cpt1);
            if (ts1.startsWith("ID:")) {
                System.out.println("Testing ID " + ts1);
                String ts2 = s2.substring(cpt2);
                if (ts2.startsWith("ID:")) {

                    // We must extract the number
                    int index1 = ts1.indexOf(" ");
                    int index2 = ts2.indexOf(" ");
                    if ( (index1 == -1) || (index2 == -1)) {
                        return false;
                    }

                    String tts1 = ts1.substring(3, index1);
                    String tts2 = ts2.substring(3, index2);

                    try {
                        Integer i1 = Integer.valueOf(tts1);
                        Integer i2 = Integer.valueOf(tts2);

                        System.out.println("Found IDs: " + i1 + ", " + i2);

                        Integer i3 = mapOfIDs.get(i1);
                        if (i3 == null) {
                            System.out.println("Not in map:" + i1);
                            mapOfIDs.put(i1, i2);
                        } else {
                            System.out.println("Already in map:" + i1 + " with " + i3);
                            if (Integer.compare(i3, i2) != 0) {
                                return false;
                            }
                        }
                        cpt1 += 3 + tts1.length();
                        cpt2 += 3 + tts2.length();

                    } catch (Exception e) {
                        return false;
                    }


                } else {
                    return false;
                }

            } else {
                if (s1.charAt(cpt1) != s2.charAt(cpt2)) {
                    return false;
                }
                cpt1 ++;
                cpt2 ++;
            }
        }

        // Testing that one destination ID corresponds to only one origin ID
        Object [] ints = mapOfIDs.values().toArray();
        for (int i=0; i<ints.length-1; i++) {
            for (int j=i+1; j<ints.length; j++) {
                Integer i1 = (Integer)ints[i];
                Integer i2 = (Integer)ints[j];
                if (Integer.compare(i1, i2) == 0) {
                    System.out.println("i1=i2=" + i1);
                    return false;
                }
            }
        }

        return true;
    }


}
