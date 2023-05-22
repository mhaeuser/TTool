package ui;

import org.junit.*;
import tmltranslator.compareTMLTest.CompareTML;
import ui.tmlcompd.TMLComponentTaskDiagramPanel;


import java.io.*;

import static org.junit.Assert.*;
/*
 * #issue 82 + 186
 * author : Minh Hiep
 */
public class TDiagramPanelCloneTest extends AbstractUITest {


    private final static String [] EXPECTED_FILES = {"expected_spec1.tml", "expected_spec2.tml", "expected_spec3.tml", "expected_spec4.tml"};
    private final static String [] COMP_NAMES = {"CompositeComp1", "CompositeComp2", "PrimitiveComp5", "PrimitiveComp6"};

    private static String FILE_DIR = getBaseResourcesDir() + "tmltranslator/expected/";

    private  TDiagramPanel diagramPanel;
    private  TGComponent [] components;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/ui/diagram2tml/input/CloneCompositeComponentTest.xml";
    }

    public TDiagramPanelCloneTest() {
        super();
        mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }

    @Before
    public void setUp() {
        for(TURTLEPanel _tab : mainGUI.getTabs()) {
            if(_tab instanceof TMLComponentDesignPanel) {
                for (TDiagramPanel tdp : _tab.getPanels()) {
                    if (tdp instanceof  TMLComponentTaskDiagramPanel) {
                        diagramPanel = tdp;
                        mainGUI.selectTab(diagramPanel);
                        break;
                    }
                }
                break;
            }
        }

        components = new TGComponent[EXPECTED_FILES.length];

        if (diagramPanel != null) {
            for (TGComponent tgc : diagramPanel.getAllComponentList()) {
                for(int i=0; i<Math.min(EXPECTED_FILES.length, COMP_NAMES.length); i++) {
                    if (tgc.getValue().equals(COMP_NAMES[i])) {
                        System.out.println("Found component:" + i + " / " + tgc.getValue());
                        components[i] = tgc;
                    }
                }
            }
       }
    }

   @Test
    public void testCloneCompositeComponentWithNullFather() throws Exception {
       System.out.println("Test  testCloneCompositeComponentWithNullFather ");
       if (diagramPanel != null) {
           System.out.println("Not null testCloneCompositeComponentWithNullFather ");
           for (int i = 0; i < EXPECTED_FILES.length; i++) {
               System.out.println("Testing with " + components[i].getValue());
               CompareTML compTML = new CompareTML();
               diagramPanel.cloneComponent(components[i]);
               mainGUI.checkModelingSyntax(true);
               mainGUI.generateTMLTxt();
               File f1 = new File(FILE_DIR + EXPECTED_FILES[i]);
               File f2 = new File("spec.tml");  // Generated file after executing "TML generation"
               System.out.println("Comparing " + f1.getAbsolutePath() + " with " + f2.getAbsolutePath());
               assertTrue(compTML.compareTML(f1, f2));
           }
       }
       System.out.println("End test  testCloneCompositeComponentWithNullFather ");
    }

}