package ui.GraphLatencyAnalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import ui.TMLArchiPanel;
import ui.interactivesimulation.SimulationTransaction;
import ui.simulationtraceanalysis.DirectedGraphTranslator;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.latencyDetailedAnalysisMain;
import ui.AbstractUITest;
import ui.SimulationTrace;

public class NestedStructurePLAN extends AbstractUITest {

    private static final String mappingDiagName = "Architecture";
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";

    private static final String simulationTracePathFile = INPUT_PATH + "/loopseqTrace.xml";
    private static final String modelPath = INPUT_PATH + "/loopseqgraph.xml";

    private latencyDetailedAnalysisMain latencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis latencyDetailedAnalysis;
    private static DirectedGraphTranslator dgt;
    private Vector<SimulationTransaction> transFile1;
    private HashMap<String, Integer> checkedDropDown = new HashMap<String, Integer>();

    private static final int operator1ID = 69;
    private static final int operator2ID = 53;
    private static String task1;
    private static String task2;

    private static Object[][] allLatencies;

    @Before
    public void NestedStructurePLAN() throws InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + modelPath));
        // mainGUI.openProjectFromFile(new File(modelPath));

        final TMLArchiPanel panel = findArchiPanel(mappingDiagName);

        if (panel == null) {
            System.out.println("NULL Panel");
        } else {
            System.out.println("Non NULL Panel");
        }

        mainGUI.checkModelingSyntax(panel, true);
        SimulationTrace file2 = new SimulationTrace("graphTestSimulationTrace", 6, simulationTracePathFile);
        latencyDetailedAnalysisMain = new latencyDetailedAnalysisMain(3, mainGUI, file2, false, false, 3);

        latencyDetailedAnalysisMain.latencyDetailedAnalysis(file2, panel, false, false, mainGUI);

        latencyDetailedAnalysis = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis();

        if (latencyDetailedAnalysis != null) {
            latencyDetailedAnalysis.setVisible(false);
            try {
                latencyDetailedAnalysis.getT().join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            dgt = latencyDetailedAnalysis.getDgraph();

        }

    }

    @Test
    public void parseFile() {

        assertNotNull(latencyDetailedAnalysis);

        int graphsize = dgt.getGraphsize();

        assertTrue(graphsize == 57);

        // test sequence to all its nexts
        assertTrue(dgt.edgeExists(66, 65));
        assertTrue(dgt.edgeExists(66, 63));
        // test sequence branch to another
        assertTrue(dgt.edgeExists(64, 63));
        assertTrue(dgt.edgeExists(63, 62));
        // test ordered sequence
        assertTrue(dgt.edgeExists(62, 61));
        assertTrue(dgt.edgeExists(62, 60));
        // test nested sequence to unordered sequence
        assertTrue(dgt.edgeExists(57, 60));
        assertTrue(dgt.edgeExists(68, 60));

        // test unordered sequence nexts
        assertTrue(dgt.edgeExists(61, 67));
        assertTrue(dgt.edgeExists(61, 58));
        assertTrue(dgt.edgeExists(68, 58));
        assertTrue(dgt.edgeExists(57, 67));

        // test ordered sequence nexts
        assertTrue(dgt.edgeExists(26, 25));
        assertTrue(dgt.edgeExists(26, 21));
        assertTrue(dgt.edgeExists(26, 23));

        // test ordered sequence ends

        assertTrue(dgt.edgeExists(24, 21));
        // nested seq loop
        assertTrue(dgt.edgeExists(27, 23));
        assertTrue(dgt.edgeExists(30, 36));
        assertTrue(dgt.edgeExists(29, 36));
        // inside loop only connected to loop vertex
        assertFalse(dgt.edgeExists(30, 22));
        assertFalse(dgt.edgeExists(29, 22));

        // sequence last branch end not connected to other branches
        assertFalse(dgt.edgeExists(22, 25));
        assertFalse(dgt.edgeExists(22, 21));

        // sequence branches not connected backward

        assertFalse(dgt.edgeExists(27, 25));

        // loop for ever edges+ nested loops and seq
        assertTrue(dgt.edgeExists(44, 49));
        assertTrue(dgt.edgeExists(44, 51));
        assertTrue(dgt.edgeExists(44, 53));
        assertTrue(dgt.edgeExists(43, 42));
        assertTrue(dgt.edgeExists(43, 41));
        assertTrue(dgt.edgeExists(45, 41));
        assertTrue(dgt.edgeExists(40, 49));

        assertTrue(dgt.edgeExists(48, 54));
        assertTrue(dgt.edgeExists(48, 51));
        assertTrue(dgt.edgeExists(48, 53));

        assertTrue(dgt.edgeExists(50, 49));
        assertTrue(dgt.edgeExists(50, 53));
        assertTrue(dgt.edgeExists(50, 54));
        assertTrue(dgt.edgeExists(52, 51));
        assertTrue(dgt.edgeExists(52, 49));
        assertTrue(dgt.edgeExists(52, 54));

        assertFalse(dgt.edgeExists(40, 54));
        assertFalse(dgt.edgeExists(40, 42));

        transFile1 = latencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new File(getBaseResourcesDir() + simulationTracePathFile));

        // transFile1 =
        // latencyDetailedAnalysisMain.getLatencyDetailedAnalysis().parseFile(new
        // File(simulationTracePathFile));

        assertTrue(transFile1.size() == 38);
        checkedDropDown = latencyDetailedAnalysis.getCheckedT();

        for (Entry<String, Integer> cT : checkedDropDown.entrySet()) {

            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == operator1ID) {
                task1 = taskName;

            } else if (id == operator2ID) {
                task2 = taskName;

            }
        }

        // int i = dropDown.indexOf(checkedDropDown.get);
        // int j = dropDown.indexOf(t2);

        // task1 = dropDown.get(i);
        // task2 = dropDown.get(j);

        allLatencies = dgt.latencyDetailedAnalysis(task1, task2, transFile1, false, false);

        assertTrue(allLatencies.length == 1);
        assertTrue((int) allLatencies[0][1] == 60);
        assertTrue((int) allLatencies[0][3] == 212);
        assertTrue((int) allLatencies[0][4] == 152);
    }

}
