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

public class DiplodocusParsingTest extends AbstractTest {

    private final String [] MODELS = {"test_parsing_1", "test_parsing_2"};
    private final boolean [] EXPECTED_RESULTS_SYNTAX_CHECKING_OK = {true, false};


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/parsing/";

    }

    public DiplodocusParsingTest() {
        super();
        //mainGUI.openProjectFromFile(new File(RESOURCES_DIR));
    }



    @Test
    public void testSimulationGraph() throws Exception {
        for(int i=0; i<MODELS.length; i++) {
            String s = MODELS[i];
            // Load the TML
            System.out.println("executing: loading " + s);
            TMLTextSpecification tmts = new TMLTextSpecification(s);
            File f = new File(RESOURCES_DIR + s + ".tml");
            System.out.println("executing: new file loaded " + s);
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                System.out.println("Exception executing: loading " + s);
                assertTrue(false);
            }

            System.out.println("executing: making TML modeling ");
            boolean parsed = tmts.makeTMLModeling(spec);
            assertTrue(parsed);


            System.out.println("executing: checking syntax " + s);
            // Checking syntax

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmts.getTMLModeling());
            syntax.checkSyntax();

            assertEquals(EXPECTED_RESULTS_SYNTAX_CHECKING_OK[i], syntax.hasErrors() == 0);

        }

    }

}
