package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.FileUtils;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import remotesimulation.RemoteConnection;
import remotesimulation.RemoteConnectionException;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DiplodocusSimulatorNullifyTimeExecOpsTest extends AbstractTest {
    private final String DIR_GEN = "test_diplo_simulator/";
    private final String [] MODELS_TERMINATE = {"nullifytest"};
    private String SIM_DIR;

    private static final String SIM_KEYWORD_TIME_BEG = "Simulated time:";
    private static final String SIM_KEYWORD_TIME_END = "time";
    private static final String [] VALUES = {"22", "3", "10", "22"};

    private static String CPP_DIR = "../../../../simulators/c++2/";



    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public DiplodocusSimulatorNullifyTimeExecOpsTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test(timeout = 600000) // 10 minutes
    public void testIsSimulationTerminated() throws Exception {
        for (int i = 0; i < MODELS_TERMINATE.length; i++) {
            String s = MODELS_TERMINATE[i];
            SIM_DIR = DIR_GEN + s + "/";
            // Load the TML
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

            makeSim( s, tmap, 0);

            tmap.getTMLModeling().nullifyOperators(true, true);
            makeSim( s, tmap, 1);

            parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);

            // Checking syntax
            tmap = tmts.getTMLMapping();
            tmap.getTMLModeling().nullifyOperators(false, true);
            makeSim( s, tmap, 2);


            parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);
            // Checking syntax
            tmap = tmts.getTMLMapping();
            tmap.getTMLModeling().nullifyOperators(true, false);
            makeSim( s, tmap, 3);
            

        }
    }

    private void makeSim(String s, TMLMapping tmap, int valueIndex) throws Exception {
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
        Penalties penalty = new Penalties(SIM_DIR + "src_simulator");
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

        System.out.println("executing: " + SIM_DIR + "/run.x");
        try {
            proc = Runtime.getRuntime().exec(SIM_DIR + "/run.x");
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            monitorError(proc);

            boolean simulationTime = false;
            while ((str = proc_in.readLine()) != null) {
                System.out.println("executing: " + str);
                if (str.startsWith(SIM_KEYWORD_TIME_BEG)) {
                    String str1 = str.substring(SIM_KEYWORD_TIME_BEG.length());
                    int index = str1.indexOf(SIM_KEYWORD_TIME_END);
                    System.out.println("executing: str1=" + str1);
                    if (index != -1) {
                        String str2 = str1.substring(0, index).trim();
                        System.out.println("executing: str2=" + str2 + " index value="+valueIndex);
                        simulationTime = str2.compareTo(VALUES[valueIndex]) == 0;
                    }
                }
            }
            assertTrue(simulationTime);
        } catch (Exception e) {
            // Probably make is not installed
            System.out.println("FAILED: executing: " + "make -C " + SIM_DIR);
            fail();
            return;
        }
    }

}

