package tmltranslator;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import test.AbstractTest;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HTMLParseTest extends AbstractTest {
    final String DIR_GEN = "test_diplo_simulator/";
    final String [] MODELS_PARSE_HTML = {"parseFPGA_HTML", "parseCPU1_HTML","parseCPU2_HTML"};
    final String [] PARSE_FPGA = {"<- idle 451 ->","", "", "", ""};
    //final String [] PARSE_FPGA = {"","", "", "", ""};
    final String [] PARSE_SINGLE_CORE = {"<- idle 381 ->", "<- idle 401 ->", "<- idle 401 ->", "<- idle 401 ->", "<- idle 401 ->"};
    final String [] PARSE_MULTI_CORE = { "", "", "", "<- idle 389 ->", "<- idle 401 ->"};
    final static String EXPECTED_FILE_GET_ALL_TRANS = getBaseResourcesDir() + "tmltranslator/expected/expected_get_all_transactions.txt";
    private String SIM_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }

    public HTMLParseTest() {
        super();
        //mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }

    @Test
    public void testParseHTML() throws Exception {
        for (int i = 0; i < MODELS_PARSE_HTML.length; i++) {
            String s = MODELS_PARSE_HTML[i];
            SIM_DIR = DIR_GEN + s + "/";
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
                fail();
            }
            System.out.println("executing: testing spec " + s);
            assertNotNull(spec);
            System.out.println("executing: testing parsed " + s);
            boolean parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);

            System.out.println("executing: checking syntax " + s);
            // Checking syntax
            TMLMapping tmap = tmts.getTMLMapping();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertEquals(0, syntax.hasErrors());
            // Generate SystemC code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertNull(error);

            File directory = new File(SIM_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + "../../../../simulators/c++2/";
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
            Penalties penalty = new Penalties(SIM_DIR  + "src_simulator");
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



            // Run the simulator
            String graphPath = SIM_DIR + "testgraph_" + s;
            System.out.println("\n****************\ngraphPath=" + graphPath +  "\n****************\n");
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 0; 7 1 " + graphPath;
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);

                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    //System.out.println("executing: " + str);
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the graph
            Document htmlFile = null;
            try {
                htmlFile = Jsoup.parse(new File(graphPath + ".html"), "ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            } // right

            Elements div = htmlFile.select("td.not");
            System.out.println("Nb elements of " + s + " " + div.size() + " i=" + i);
            for (int k=0; k<div.size() ; k++) {
                System.out.println("div[" + k + "] = " + div.get(k).text());
            }
//            assertTrue(NB_Of_DELAY_STATES[i] == graph.getNbOfStates());
            switch (i){
                case 0:
                    for (int j = 0; j < 5; j++) {
                        System.out.println("FPGA " + s + ": >" + div.get(j).text() + "< vs >" + PARSE_FPGA[j] + "<");
                        assertTrue(PARSE_FPGA[j].equals(div.get(j).text()));
                    }
                    break;
                case 1:
                    for (int j = 0; j < 5; j++) {
                        System.out.println("SINGLE_CORE " + s + ": " + div.get(j).text());
                        assertTrue(PARSE_SINGLE_CORE[j].equals(div.get(j).text()));
                    }
                    break;
                default:
                    for (int j = 0; j < 5; j++) {
                        System.out.println("MULTI_CORE " + s + ": " + div.get(j).text() + " ; expecting: " + PARSE_MULTI_CORE[j]);
                        assertTrue(PARSE_MULTI_CORE[j].equals(div.get(j).text()));
                    }
                    break;
            }

        }
    }

    /*@Test
    public void testListAllTransactionsOfTask() throws Exception {
        for (int i = 0; i < 1; i++) {
            String s = MODELS_PARSE_HTML[i];
            SIM_DIR = DIR_GEN + s + "_lat/";
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
                fail();
            }
            System.out.println("executing: testing spec " + s);
            assertNotNull(spec);
            System.out.println("executing: testing parsed " + s);
            boolean parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);

            System.out.println("executing: checking syntax " + s);
            // Checking syntax
            TMLMapping tmap = tmts.getTMLMapping();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertEquals(0, syntax.hasErrors());
            // Generate SystemC code
            System.out.println("executing: sim code gen for " + s);
            final IDiploSimulatorCodeGenerator tml2systc;
            List<EBRDD> al = new ArrayList<EBRDD>();
            List<TEPE> alTepe = new ArrayList<TEPE>();
            tml2systc = DiploSimulatorFactory.INSTANCE.createCodeGenerator(tmap, al, alTepe);
            tml2systc.setModelName(s);
            String error = tml2systc.generateSystemC(false, true);
            assertNull(error);

            File directory = new File(SIM_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Putting sim files
            System.out.println("SIM executing: sim lib code copying for " + s);
            ConfigurationTTool.SystemCCodeDirectory = getBaseResourcesDir() + "../../../../simulators/c++2/";
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
            Penalties penalty = new Penalties(SIM_DIR  + "src_simulator");
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

            // Run the simulator
            String graphPath = SIM_DIR + "testgraph_" + s;
            try {

                String[] params = new String[4];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 0; 25 Application__Task2";
                params[3] = "echo \"Info transaction:\" >" + graphPath;
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                monitorError(proc);
                PrintStream out = new PrintStream(new FileOutputStream(graphPath + ".txt"));
                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                    System.out.println("executing: " + str);
                    if(str.contains("Info transaction:")){
                        out.append(str + "\n");
                    }
                }
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }

            // Compare results with expected ones
            // Must load the graph
            BufferedReader reader1 = new BufferedReader(new FileReader(graphPath + ".txt"));
            BufferedReader reader2 = new BufferedReader(new FileReader(EXPECTED_FILE_GET_ALL_TRANS));

            System.out.println("Comparing " + graphPath + ".txt with " + EXPECTED_FILE_GET_ALL_TRANS);

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            boolean areEqual = true;

            int lineNum = 1;

            while (line1 != null || line2 != null) {
                if (line1 == null || line2 == null) {
                    areEqual = false;
                    break;
                } else if (!line1.equalsIgnoreCase(line2)) {
                    areEqual = false;
                    break;
                }
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                lineNum++;
            }

            if (areEqual) {
                System.out.println("Two files have same content.");
                assertTrue(areEqual);
            } else {
                System.out.println("Two files have different content. They differ at line " + lineNum);
                System.out.println("File1 has " + line1 + " and File2 has " + line2 + " at line " + lineNum);
                assertTrue(areEqual);
            }
            reader1.close();
            reader2.close();
        }
    }*/
}
