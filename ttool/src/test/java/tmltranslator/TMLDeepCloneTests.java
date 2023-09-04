package tmltranslator;

import myutil.FileUtils;
import myutil.TraceManager;
import test.AbstractTest;
import tmltranslator.compareTMLTest.CompareTML;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TMLDeepCloneTests extends AbstractTest {

    private final static String PATH_TO_TEST_CLONE_FILE = "/tmltranslator/simulator/";

    private final static String[] TML_APP_FILES = {"signal.tml", "scp.tml"};

    //final static String EMPTY_FILE = getBaseResourcesDir() + PATH_TO_TEST_COMPARE_FILE + "file1.tml";


    // Test true cases
    @Test
    public void testDeepCloneTMLApplication() {
        for(String fileName: TML_APP_FILES) {
            fileName = getBaseResourcesDir() + PATH_TO_TEST_CLONE_FILE + fileName;

            TMLTextSpecification tmlts = new TMLTextSpecification(fileName);

            File f = new File(fileName);
            TraceManager.addDev("TMLDeepCloneTest new file loaded " + fileName + " path to file: " + f.getAbsolutePath());
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                TraceManager.addDev("TMLDeepCloneTest. Exception executing: loading " + fileName);
                assertTrue(false);
            }

            // Parsing
            assertTrue(spec != null);
            boolean parsed = tmlts.makeTMLModeling(spec);
            assertTrue(parsed);

            TMLTextSpecification tmltsw = new TMLTextSpecification("original");
            String outputTML = tmltsw.toTextFormat(tmlts.getTMLModeling());

            TraceManager.addDev("Original:\n" + outputTML + "\n\n");


            // Syntax checking
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlts.getTMLModeling());
            syntax.checkSyntax();
            assertTrue(syntax.hasErrors() == 0);

            //////Clone
            TMLModeling cloned = null;
            try {
                cloned = tmlts.getTMLModeling().deepClone();
            } catch(TMLCheckingError tmlce) {
                TraceManager.addDev("TMLDeepCloneTest. Exception executing: clone " + fileName + ": " + tmlce.getMessage());
                assertTrue(false);
            }

            tmltsw = new TMLTextSpecification("cloned");
            outputTML = tmltsw.toTextFormat(cloned);

            TraceManager.addDev("Cloned:\n" + outputTML + "\n\n");

            syntax = new TMLSyntaxChecking(cloned);
            syntax.checkSyntax();
            TraceManager.addDev("Cloned errors and warnings:\n" + syntax.getErrorAndWarningString() + "\n\n");
            assertTrue(syntax.hasErrors() == 0);

            // Comparing the two TML Modeling
            //boolean equal = cloned.equalSpec(tmlts.getTMLModeling());
            //assertTrue(equal);


        }
    }
}
