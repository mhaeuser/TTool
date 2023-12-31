package ui.graphlatencyanalysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import tmltranslator.simulation.DependencyGraphTranslator;
import tmltranslator.simulation.SimulationTransaction;
import ui.AbstractUITest;
import ui.SimulationTrace;
import ui.TMLArchiPanel;
import ui.simulationtraceanalysis.JFrameCompareLatencyDetail;
import ui.simulationtraceanalysis.JFrameLatencyDetailedAnalysis;
import ui.simulationtraceanalysis.LatencyAnalysisParallelAlgorithms;
import ui.simulationtraceanalysis.LatencyDetailedAnalysisMain;

public class CompareLatencyInSimulationTracesTest extends AbstractUITest {
    private static final String INPUT_PATH = "/ui/graphLatencyAnalysis/input";
    private static final String SIMULATIONTRACEPATHFILE1 = INPUT_PATH + "/graphTestSimulationTrace.xml";
    private static final String SIMULATIONTRACEPATHFILE2 = INPUT_PATH + "/testFunc.xml";
    private static final String MODELPATH = INPUT_PATH + "/GraphTestModel.xml";
    private static final String MAPPINGDIAGNAME = "Architecture2";
    private static final int OPERATOR1ID = 44;
    private static final int OPERATOR2ID = 26;
    private static final int OPERATOR3ID = 40;
    private static final int OPERATOR4ID = 28;
    private Vector<String> checkedTransactionsFile1 = new Vector<String>();
    private Vector<String> checkedTransactionsFile2 = new Vector<String>();
    private DependencyGraphTranslator dgraph1, dgraph2;
    private String task1, task2, task3, task4;
    private JFrameCompareLatencyDetail cld;
    private LatencyDetailedAnalysisMain LatencyDetailedAnalysisMain;
    private JFrameLatencyDetailedAnalysis jFrameLatencyDetailedAnalysis;
    private Vector<SimulationTransaction> transFile1, transFile2;
    private SimulationTrace simT1, simT2;
    private File file1, file2;
    private int row, row2;
    private HashMap<String, Integer> checkedT1 = new HashMap<String, Integer>();
    private HashMap<String, Integer> checkedT2 = new HashMap<String, Integer>();
    // protected MainGUI mainGUI1 = null;
    private Object[][] dataDetailedByTask, dataDetailedByTask2, dataHWDelayByTask, dataHWDelayByTask2;
    private Object[][] tableData2MinMax, tableData1MinMax, tableData2, tableData = null;

    public CompareLatencyInSimulationTracesTest() {
        super();
    }

    @Before
    public void GraphLatencyAnalysis()
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, InterruptedException {
        mainGUI.openProjectFromFile(new File(getBaseResourcesDir() + MODELPATH));
        final TMLArchiPanel panel = findArchiPanel(MAPPINGDIAGNAME);
        simT1 = new SimulationTrace("", 6, (getBaseResourcesDir() + SIMULATIONTRACEPATHFILE1));
        LatencyDetailedAnalysisMain = new LatencyDetailedAnalysisMain(3, mainGUI, simT1, false, false, 3);
        LatencyDetailedAnalysisMain.setCheckedTransactionsFile(new Vector<String>());
        try {
            LatencyDetailedAnalysisMain.latencyDetailedAnalysisForXML(mainGUI, simT1, false, true, 1);
            LatencyDetailedAnalysisMain.setTc(new LatencyAnalysisParallelAlgorithms(LatencyDetailedAnalysisMain));
            cld = new JFrameCompareLatencyDetail(LatencyDetailedAnalysisMain, mainGUI, checkedTransactionsFile1,
                    LatencyDetailedAnalysisMain.getMap1(), LatencyDetailedAnalysisMain.getCpanels1(), simT1, false,
                    LatencyDetailedAnalysisMain.getTc());
            if (cld == null) {
                System.out.println("NULL Panel");
            } else {
                cld.setVisible(false);
            }
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        dgraph1 = new DependencyGraphTranslator(LatencyDetailedAnalysisMain.getMap1());
        dgraph1.DrawDirectedGraph();
        if (dgraph1.getGraphsize() > 0) {
            checkedTransactionsFile1 = LatencyDetailedAnalysisMain.getCheckedTransactionsFile();
            checkedT1 = LatencyDetailedAnalysisMain.getCheckedT1();
            cld.setDgraph(null);
            LatencyDetailedAnalysisMain.setCheckedTransactionsFile(new Vector<String>());
            simT2 = new SimulationTrace("", 6, (getBaseResourcesDir() + SIMULATIONTRACEPATHFILE2));
            try {
                LatencyDetailedAnalysisMain.latencyDetailedAnalysisForXML(mainGUI, simT2, false, true, 1);
            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            dgraph2 = new DependencyGraphTranslator(LatencyDetailedAnalysisMain.getMap1());
            dgraph2.DrawDirectedGraph();
            checkedTransactionsFile2 = LatencyDetailedAnalysisMain.getCheckedTransactionsFile();
            checkedT2 = LatencyDetailedAnalysisMain.getCheckedT2();
        } else {
            System.out.println("Graph size is zero");
        }
    }

    @Test
    public void parseFile() {
        int graphsize = dgraph1.getGraphsize();
        assertTrue(1 > 0);
        graphsize = dgraph2.getGraphsize();
        assertTrue(graphsize > 0);
        assertTrue(checkedT1.size() == 3);
        assertTrue(checkedT2.size() == 3);
        for (Entry<String, Integer> cT : checkedT1.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == OPERATOR1ID) {
                task1 = taskName;
            } else if (id == OPERATOR2ID) {
                task2 = taskName;
            }
        }
        for (Entry<String, Integer> cT : checkedT2.entrySet()) {
            int id = cT.getValue();
            String taskName = cT.getKey();
            if (id == OPERATOR3ID) {
                task3 = taskName;
            } else if (id == OPERATOR4ID) {
                task4 = taskName;
            }
        }
        file1 = new File(simT1.getFullPath());
        file2 = new File(simT2.getFullPath());
        transFile1 = cld.parseFile(file1);
        transFile2 = cld.parseFile(file2);
        cld.setDgraph2(dgraph2);
        tableData = dgraph1.latencyDetailedAnalysis(task1, task2, transFile1, false, false);
        tableData1MinMax = dgraph1.latencyMinMaxAnalysis(task1, task2, transFile1);
        assertTrue(tableData.length > 0);
        assertTrue(tableData1MinMax.length > 0);
        tableData2 = dgraph2.latencyDetailedAnalysis(task3, task4, transFile2, false, false);
        tableData2MinMax = dgraph2.latencyMinMaxAnalysis(task3, task4, transFile2);
        assertTrue(tableData2.length > 0);
        assertTrue(tableData2MinMax.length > 0);
        // test row 1 table 1 and row 1 table 2
        row = 1;
        row2 = 1;
        dataDetailedByTask = dgraph1.getTaskByRowDetails(row);
        dataDetailedByTask2 = dgraph2.getTaskByRowDetails(row2);
        dataHWDelayByTask = dgraph1.getTaskHWByRowDetails(row);
        dataHWDelayByTask2 = dgraph2.getTaskHWByRowDetails(row2);
        assertTrue(dataDetailedByTask.length > 0);
        assertTrue(dataDetailedByTask2.length > 0);
        assertTrue(dataHWDelayByTask.length > 0);
        assertTrue(dataHWDelayByTask2.length > 0);
        // test max table 1 and max table 2
        dgraph1.getRowDetailsMinMax(row);
        dataDetailedByTask = dgraph1.getTasksByRowMinMax(row);
        dgraph2.getRowDetailsMinMax(row2);
        dataDetailedByTask2 = dgraph2.getTasksByRowMinMax(row2);
        dataHWDelayByTask = dgraph1.getTaskHWByRowDetailsMinMax(row);
        dataHWDelayByTask2 = dgraph2.getTaskHWByRowDetailsMinMax(row2);
        assertTrue(dataDetailedByTask.length > 0);
        assertTrue(dataDetailedByTask2.length > 0);
        assertTrue(dataHWDelayByTask.length > 0);
        assertTrue(dataHWDelayByTask2.length > 0);
    }
}