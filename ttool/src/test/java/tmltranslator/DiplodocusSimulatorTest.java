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
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLTextSpecification;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.*;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusSimulatorTest extends AbstractUITest {

    //final String [] MODELS = {"scp"};
    final String [] MODELS = {"scp", "ssdf"};
    final String DIR_GEN = "test_diplo_simulator/";
    final int [] NB_Of_STATES = {422, 1824};
    final int [] NB_Of_TRANSTIONS = {421, 1823};
    final int [] MIN_CYCLES = {209, 4106};
    final int [] MAX_CYCLES = {864, 4106};
    //model for daemon task
    final String [] MODELS_DAEMON = {"daemontest1", "daemontest2"};
    final int [] NB_Of_DAEMON_STATES = {12, 114};
    final int [] NB_Of_DAEMON_TRANSTIONS = {11, 113};
    final int [] MIN_DAEMON_CYCLES = {181, 2743};
    final int [] MAX_DAEMON_CYCLES = {181, 2743};

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
            System.out.println("executing: nb of states " + graph.getNbOfStates());
            assertTrue(NB_Of_STATES[i] == graph.getNbOfStates());
            System.out.println("executing: nb of transitions " + graph.getNbOfTransitions());
            assertTrue(NB_Of_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue " + minValue);
            assertTrue(MIN_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue " + maxValue);
            assertTrue(MAX_CYCLES[i] == maxValue);

        }

    }
    @Test
    public void testSimulationGraphDaemon() throws Exception {
        for (int i = 0; i < MODELS_DAEMON.length; i++) {
            String s = MODELS_DAEMON[i];
            SIM_DIR = DIR_GEN + s + "/";
            System.out.println("executing: checking syntax " + s);
            // select architecture tab
            mainGUI.openProjectFromFile(new File(RESOURCES_DIR + s + ".xml"));
            for(TURTLEPanel _tab : mainGUI.getTabs()) {
                if(_tab instanceof TMLArchiPanel) {
                    for (TDiagramPanel tdp : _tab.getPanels()) {
                        if (tdp instanceof TMLArchiDiagramPanel) {
                            mainGUI.selectTab(tdp);
                            break;
                        }
                    }
                    break;
                }
            }
            mainGUI.checkModelingSyntax(true);
            TMLMapping tmap = mainGUI.gtm.getTMLMapping();
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
            Penalties penalty = new Penalties(SIM_DIR  + "src_simulator");
            int changed = penalty.handlePenalties(true);

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
                params[1] = "-explo";
                params[2] = "-gname";
                params[3] = graphPath;
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
            assertTrue(NB_Of_DAEMON_STATES[i] == graph.getNbOfStates());
            System.out.println("executing: nb transitions of " + s + " " + graph.getNbOfTransitions());
            assertTrue(NB_Of_DAEMON_TRANSTIONS[i] == graph.getNbOfTransitions());

            // Min and max cycles
            int minValue = graph.getMinValue("allCPUsFPGAsTerminated");
            System.out.println("executing: minvalue of " + s + " " + minValue);
            assertTrue(MIN_DAEMON_CYCLES[i] == minValue);

            int maxValue = graph.getMaxValue("allCPUsFPGAsTerminated");
            System.out.println("executing: maxvalue of " + s + " " + maxValue);
            assertTrue(MAX_DAEMON_CYCLES[i] == maxValue);
        }
    }

}