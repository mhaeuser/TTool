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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusSimulatorTest extends AbstractTest {

    final String [] MODELS = {"scp", "ssdf"};
    final String DIR_GEN = "test_diplo_simulator/";
    final int [] NB_Of_STATES = {119, 1054};
    final int [] NB_Of_TRANSTIONS = {118, 1053};
    final int [] MIN_CYCLES = {192, 2510};
    final int [] MAX_CYCLES = {279, 2510};
    //model for daemon task
    final String [] MODELS_DAEMON = {"daemontest1", "daemontest2"};
    final int [] NB_Of_DAEMON_STATES = {8, 114};
    final int [] NB_Of_DAEMON_TRANSTIONS = {7, 113};
    final int [] MIN_DAEMON_CYCLES = {101, 501};
    final int [] MAX_DAEMON_CYCLES = {101, 501};

    // model for Daemon Run To Next Breakpoint
    final String MODELS_DAEMON_RTNB = "testDaemon";
    final int [] DAEMON_RTNBP_1 = {12, 11, 2147483647, 0}; // 2147483647==-1
    final int [] DAEMON_RTNBP_2 = {19, 18, 2147483647, 0}; // 2147483647==-1

    // model with paths allowing parallel transfers
    private static final String SIM_KEYWORD_TIME_BEG = "Simulated time:";
    private static final String SIM_KEYWORD_TIME_END = "time";
    final String[] MODELS_PARALLEL_TRANSFERS = {"testParallel-W-W-Transfers", "testParallel-R-R-Transfers", "testParallel-W-R-Transfers"};
    final String[] SIMULS_END_TIME = {"3", "3", "3"};
    private String SIM_DIR;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/simulator/";

    }

    public DiplodocusSimulatorTest() {
        super();
        //mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() throws Exception {
        SIM_DIR = getBaseResourcesDir() + "../../../../simulators/c++2/";
    }


    @Test
    public void testSimulationGraph() throws Exception {
        for(int i=0; i<MODELS.length; i++) {
            String s = MODELS[i];
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

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();

            assertTrue(syntax.hasErrors() == 0);

            // Generate C code
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
                params[1] = "-explo";
                params[2] = "-gname";
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
            System.out.println(MODELS[i] + ": executing: nb of states " + graph.getNbOfStates() + " expecting:" + NB_Of_STATES[i]);
            assertTrue(NB_Of_STATES[i] == graph.getNbOfStates());
            System.out.println(MODELS[i] + "executing: nb of transitions " + graph.getNbOfTransitions() + " expecting:" + NB_Of_TRANSTIONS[i]);
            assertTrue(NB_Of_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println(MODELS[i] + "executing: minvalue " + minValue + " expecting:" + MIN_CYCLES[i]);
            assertTrue(MIN_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println(MODELS[i] + "executing: maxvalue " + maxValue + " expecting: " + MAX_CYCLES[i]);
            assertTrue(MAX_CYCLES[i] == maxValue);

        }

    }

    @Test
    public void testSimulationGraphDaemon() throws Exception {
        for (int i = 0; i < MODELS_DAEMON.length; i++) {
            String s = MODELS_DAEMON[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: checking syntax " + s);
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

            // Run the simulator
            String graphPath = SIM_DIR + "testgraph_" + s;
            try {

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 0; 1 7 100 100 " + graphPath;
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
            System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates() + " ; expecting: " + NB_Of_DAEMON_STATES[i]);
            assertTrue(NB_Of_DAEMON_STATES[i] == graph.getNbOfStates() );
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions() + " ; expecting: " + NB_Of_DAEMON_TRANSTIONS[i]);
            assertTrue(NB_Of_DAEMON_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue + " Expected: " + MIN_DAEMON_CYCLES[i]);
            assertTrue(MIN_DAEMON_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue + " Expected: " + MAX_DAEMON_CYCLES[i]);
            assertTrue(MAX_DAEMON_CYCLES[i] == maxValue);
        }
    }

    @Test
    public void testDaemonRunToNextBreakPoint() throws Exception {
        String s = MODELS_DAEMON_RTNB;
        SIM_DIR = DIR_GEN + s + "/";
        System.out.println("executing: checking syntax " + s);
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

        // Run the simulator
        String graphPath = SIM_DIR + "testgraph_" + s;
        try {

            String[] params = new String[3];

            params[0] = "./" + SIM_DIR + "run.x";
            params[1] = "-cmd";
            params[2] = "11 2 26; 1 0; 1 7 100 100 " + graphPath;
            proc = Runtime.getRuntime().exec(params);
            //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            monitorError(proc);

            while ((str = proc_in.readLine()) != null) {
                // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                System.out.println("executing: " + str);
            }

            //second case
            params = new String[3];

            params[0] = "./" + SIM_DIR + "run.x";
            params[1] = "-cmd";
            params[2] = "11 2 26; 1 0; 1 0; 1 7 100 100 " + graphPath + "_second";
            proc = Runtime.getRuntime().exec(params);
            //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
            proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            monitorError(proc);

            while ((str = proc_in.readLine()) != null) {
                // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );
                System.out.println("executing second case: " + str);
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
        System.out.println("executing: nb states of " + s + " " + graph.getNbOfStates() + " ; expecting: " + DAEMON_RTNBP_1[0]);
        assertTrue(DAEMON_RTNBP_1[0] == graph.getNbOfStates());
        System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions() + " ; expecting: " + DAEMON_RTNBP_1[1]);
        assertTrue(DAEMON_RTNBP_1[1] == graph.getNbOfTransitions());

        // Min and max cycles
        int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
        System.out.println("executing: minvalue of " + s + " " + minValue + " ; expecting: " + DAEMON_RTNBP_1[2]);
        assertTrue(DAEMON_RTNBP_1[2] == minValue);

        int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
        System.out.println("executing: maxvalue of " + s + " " + maxValue + " ; expecting: " + DAEMON_RTNBP_1[3]);
        assertTrue(DAEMON_RTNBP_1[3] == maxValue);

        //test for second case
        graphFile = new File(graphPath + "_second.aut");
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
        assertTrue(DAEMON_RTNBP_2[0] == graph.getNbOfStates());
        System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
        assertTrue(DAEMON_RTNBP_2[1] == graph.getNbOfTransitions());

        // Min and max cycles
        minValue = graph.getMinValue("allCPUsFPGAsTerminated");
        System.out.println("executing: minvalue of " + s + " " + minValue);
        assertTrue(DAEMON_RTNBP_2[2] == minValue);

        maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
        System.out.println("executing: maxvalue of " + s + " " + maxValue);
        assertTrue(DAEMON_RTNBP_2[3] == maxValue);
    }

    @Test
    public void testPathsWithParallelTransfers() throws Exception {
        for (int i = 0; i < MODELS_PARALLEL_TRANSFERS.length; i++) {
            String s = MODELS_PARALLEL_TRANSFERS[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: checking syntax " + s);

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
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);

            // Generate C code
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
                    proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    while ((str = proc_in.readLine()) != null) {
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

                String[] params = new String[3];

                params[0] = "./" + SIM_DIR + "run.x";
                params[1] = "-cmd";
                params[2] = "1 0; 1 7 100 100 " + graphPath;
                proc = Runtime.getRuntime().exec(params);
                //proc = Runtime.getRuntime().exec("./" + SIM_DIR + "run.x -explo -gname testgraph_" + s);
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
                            System.out.println("executing: str2=" + str2);
                            simulationTime = str2.compareTo(SIMULS_END_TIME[i]) == 0;
                        }
                    }
                }
                assertTrue(simulationTime);
            } catch (Exception e) {
                // Probably make is not installed
                System.out.println("FAILED: executing simulation");
                return;
            }
        }
    }
}
