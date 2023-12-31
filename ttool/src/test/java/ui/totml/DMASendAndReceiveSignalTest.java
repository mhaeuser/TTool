package ui.totml;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.TMLError;
import tmltranslator.TMLMapping;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import translator.CheckingError;
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

public class DMASendAndReceiveSignalTest extends AbstractUITest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_CPU_SHOWTRACE = {"ZigBeeTutorial"};
    private String SIM_DIR;
    final String [] SIM_TIME_TRANS = {"Simulated time: 130 time units."};
    static String CPP_DIR = "../../../../simulators/c++2/";
    static String mappingName = "Mapping_2";
    private TMLArchiDiagramPanel currTdp;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public DMASendAndReceiveSignalTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test(timeout = 600000)
    public void testZigbeeModelGeneratedCCode() throws Exception {
        for (int i = 0; i < MODELS_CPU_SHOWTRACE.length; i++) {
            String s = MODELS_CPU_SHOWTRACE[i];
            SIM_DIR = DIR_GEN + s + "_dmaGeneratedCCode/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            TMLArchiPanel _tab = findArchiPanel(mappingName);
            for (TDiagramPanel tdp : _tab.getPanels()) {
                if (tdp instanceof TMLArchiDiagramPanel) {
                    mainGUI.selectTab(tdp);
                    currTdp = (TMLArchiDiagramPanel) tdp;
                    break;
                }
            }

            mainGUI.checkModelingSyntax(true);
            TMLMapping tmap = mainGUI.gtm.getTMLMapping();

            List<CheckingError> errors = mainGUI.getCheckingErrors();
            assertTrue(errors.size() == 0);

            /*TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();

            for(TMLError error: syntax.getErrors()) {
                System.out.println("Error: " + error.toString());
            }

            assertTrue(syntax.hasErrors() == 0);*/


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

            ArrayList<String> arr = new ArrayList<>();
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 6 50";
                proc = Runtime.getRuntime().exec(params);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    if (str.contains("Simulated time")) {
                        arr.add(str);
                    }
                    System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation " + e.getCause());
                return;
            }

            for (int j = 0; j < arr.size(); j++) {
                assertTrue(arr.get(j).equals(SIM_TIME_TRANS[j]));
                TraceManager.addDev("check string at " + j + " :pass, content = " + arr.get(j));
            }

            TraceManager.addDev("Test Done!");
        }
    }
}
