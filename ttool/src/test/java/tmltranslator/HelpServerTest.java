package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
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
import ui.AbstractUITest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class HelpServerTest extends AbstractTest {

    private final String DIR_GEN = "test_diplo_simulator/";
    private final String [] MODELS_HELP_SERVER = {"fpga_clock_divider"};
    private String SIM_DIR;
    private final String[] HELP_SERVER = {"active-breakpoints/ab",
            "add-breakpoint/abp",
            "add-virtual-signals/avs",
            "calculate-latencies/cl",
            "choose-branch/cb"};
    private final String[] HELP_COMMAND = {"Help on command: run-to-next-breakpoint",
            "Runs the simulation until a breakpoint is met",
            "alias: rtnb",
            "code: 1 0"};
    private final String[] INVALID_PARAMETERS = {"Invalid parameter! Please run \"./run.x -help\" for more information"};
    private static String CPP_DIR = "../../../../simulators/c++2/";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";
    }

    public HelpServerTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + CPP_DIR;
    }

    @Test(timeout = 600000)
    public void testMulticoreNotHangingWhenSaveTrace() throws Exception {
        for (int i = 0; i < MODELS_HELP_SERVER.length; i++) {
            String s = MODELS_HELP_SERVER[i];
            SIM_DIR = DIR_GEN + s + "_helpserver/";
            System.out.println("executing: checking syntax " + s);
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

            if (syntax.hasErrors() != 0) {
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

            String graphPath = SIM_DIR + "testgraph_" + s;
            String tempData = "";
            try {
                // helpserver test
                String[] params = new String[2];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-helpserver";
                proc = Runtime.getRuntime().exec(params);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    tempData += str + "\n";
                }

                System.out.println("HELP_SERVER = " + tempData);
                for (int j = 0; j < HELP_SERVER.length; j++) {
                    System.out.println("Checked HELP_SERVER = " + tempData.contains(HELP_SERVER[j]));
                    assertTrue(tempData.contains(HELP_SERVER[j]));
                }

                //helpcommand test
                tempData = "";
                params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-helpcommand";
                params[2] = "rtnb";
                proc = Runtime.getRuntime().exec(params);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    tempData += str + "\n";;
                }

                tempData = Conversion.replaceAllChar(tempData, '\n', "");

                System.out.println("HELP_COMMAND = " + tempData);
                for (int j = 0; j < HELP_COMMAND.length; j++) {
                    System.out.println("Checked HELP_COMMAND = " + HELP_COMMAND[j] + " / " + tempData.contains(HELP_COMMAND[j]));
                    assertTrue(tempData.contains(HELP_COMMAND[j]));
                }

                //detect invalid parameter test
                tempData = "";
                params = new String[2];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-helpp";
                proc = Runtime.getRuntime().exec(params);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    tempData += str + "\n";
                }

                System.out.println("INVALID_PARAMETERS = " + tempData);
                for (String invalid_parameter : INVALID_PARAMETERS) {
                    System.out.println("Checked INVALID_PARAMETERS = " + tempData.contains(invalid_parameter));
                    assertTrue(tempData.contains(invalid_parameter));
                }

            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation " + e.getCause());
                return;
            }
        }
    }
}
