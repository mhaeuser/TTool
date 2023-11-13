/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 * /**
 * Class CLIPatternHandlingTest
 * Creation: 13/11/2023
 * @version 1.0 13/11/2023
 * @author Jawher JERRAY
 * @see
 */

package cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import graph.AUTTransition;
import myutil.Conversion;
import myutil.TraceManager;
import org.junit.Test;
import test.AbstractTest;
import tmltranslator.compareTMLTest.CompareTML;
import tmltranslator.compareTMLTest.CompareTMAP;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLError;


public class CLIPatternHandlingTest extends AbstractTest implements InterpreterOutputInterface {

    private final static String PATH_TO_TEST_FILE = "cli/testPatternHandling/";
    private static final String PATH_PATTERNS [] = {PATH_TO_TEST_FILE + "tmr/"};
    private static final String PATH_CLI_TO_CREATE_PATTERNS [] = {"cli/create-tmr"};

    private static final String PATH_CLI_TO_CONFIGURATE_TMR_IN_MODELS [] = {"cli/configurate-tmr-for-modelWithOneSensor", "cli/configurate-tmr-for-modelWithThreeSensors"};
    private static final String PATH_CLI_TO_APPLY_TMR_IN_MODELS [] = {"cli/apply-tmr-in-modelWithOneSensor", "cli/apply-tmr-in-modelWithThreeSensors"};
    private static final String EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TMAP [] = {"expected/modelWithOneSensorIntegTMR.tmap", "expected/modelWithThreeSensorsIntegTMR.tmap"};
    private static final String EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TARCHI [] = {"expected/modelWithOneSensorIntegTMR.tarchi", "expected/modelWithThreeSensorsIntegTMR.tarchi"};
    private static final String EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TML [] = {"expected/modelWithOneSensorIntegTMR.tml", "expected/modelWithThreeSensorsIntegTMR.tml"};

    private static final String OBTAINED_MODELS_PATH = "resources/test/cli/testPatternHandling/tmr/";
    private static final String OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TMAP [] = {"modelsAfterIntegratingTMR/modelWithOneSensorIntegTMR.tmap", "modelsAfterIntegratingTMR/modelWithThreeSensorsIntegTMR.tmap"};
    private static final String OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TARCHI [] = {"modelsAfterIntegratingTMR/modelWithOneSensorIntegTMR.tarchi", "modelsAfterIntegratingTMR/modelWithThreeSensorsIntegTMR.tarchi"};
    private static final String OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TML [] = {"modelsAfterIntegratingTMR/modelWithOneSensorIntegTMR.tml", "modelsAfterIntegratingTMR/modelWithThreeSensorsIntegTMR.tml"};
    private StringBuilder outputResult;
	
	public CLIPatternHandlingTest() {
	    //
    }
	
    public void exit(int reason) {
	    TraceManager.addDev("Exit reason=" + reason);
	    assertTrue(reason == 0);
    }

    public void printError(String error) {
        TraceManager.addDev("Error=" + error);
    }

    public void print(String s) {
	    TraceManager.addDev("info from interpreter:" + s);
	    outputResult.append(s);
    }
	
	@Test
    public void testIntegratingTMR() throws Exception {
        String filePathCreateTMR = getBaseResourcesDir() + PATH_PATTERNS[0] +  PATH_CLI_TO_CREATE_PATTERNS[0];
        String script;
        outputResult = new StringBuilder();

        File f = new File(filePathCreateTMR);
        assertTrue(myutil.FileUtils.checkFileForOpen(f));

        script = myutil.FileUtils.loadFileData(f);

        assertTrue(script.length() > 0);

        boolean show = false;
        Interpreter interpret = new Interpreter(script, (InterpreterOutputInterface)this, show);
        interpret.interpret();

        for (int i = 0; i < PATH_CLI_TO_CONFIGURATE_TMR_IN_MODELS.length; i++) {
            String filePathConfigModel = getBaseResourcesDir() + PATH_PATTERNS[0] +  PATH_CLI_TO_CONFIGURATE_TMR_IN_MODELS[i];
            String scriptConfig;
            outputResult = new StringBuilder();
            File fConfig = new File(filePathConfigModel);
            assertTrue(myutil.FileUtils.checkFileForOpen(fConfig));
            scriptConfig = myutil.FileUtils.loadFileData(fConfig);
            assertTrue(scriptConfig.length() > 0);
            Interpreter interpretConfigModel = new Interpreter(scriptConfig, (InterpreterOutputInterface)this, false);
            interpretConfigModel.interpret();

            String filePathApplyToModel = getBaseResourcesDir() + PATH_PATTERNS[0] +  PATH_CLI_TO_APPLY_TMR_IN_MODELS[i];
            String scriptApplyToModel;
            outputResult = new StringBuilder();
            File fApplyToModel = new File(filePathApplyToModel);
            assertTrue(myutil.FileUtils.checkFileForOpen(fApplyToModel));
            scriptApplyToModel = myutil.FileUtils.loadFileData(fApplyToModel);
            assertTrue(scriptApplyToModel.length() > 0);
            Interpreter interpretApplyToModel = new Interpreter(scriptApplyToModel, (InterpreterOutputInterface)this, false);
            interpretApplyToModel.interpret();
           
            String filePathObtainedTmap = OBTAINED_MODELS_PATH + OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TMAP[i];
            String folderPathObtainedTmap = filePathObtainedTmap.substring(0, filePathObtainedTmap.lastIndexOf("/")+1);
            String fileName = filePathObtainedTmap.split("/")[filePathObtainedTmap.split("/").length -1];
            String modelName = fileName.split("\\.")[0];
            File fObtainedTmap = new File(filePathObtainedTmap);
            assertTrue(myutil.FileUtils.checkFileForOpen(fObtainedTmap));

            TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(modelName);
            TraceManager.addDev("Loading file: " + fObtainedTmap.getAbsolutePath());
            String obtainedOutputTmap = null;
            try {
                obtainedOutputTmap = myutil.FileUtils.loadFileData(fObtainedTmap);
            } catch (Exception e) {
                TraceManager.addDev("Exception executing: loading " + modelName);
                assertTrue(false);
            }
            TraceManager.addDev("Testing spec " + modelName);
            assertTrue(obtainedOutputTmap != null);
            TraceManager.addDev("Going to parse " + modelName);
            boolean parsed = tmts.makeTMLMapping(obtainedOutputTmap, folderPathObtainedTmap);
            assertTrue(parsed);

            TraceManager.addDev("Checking syntax " + modelName);
            // Checking syntax
            TMLMapping tmap = tmts.getTMLMapping();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();

            if (syntax.hasErrors() > 0) {
                for (TMLError error: syntax.getErrors()) {
                    TraceManager.addDev("Error: " + error.toString());
                }

            }
            assertTrue(syntax.hasErrors() == 0);


            String filePathExpectedTmap = getBaseResourcesDir() + PATH_PATTERNS[0] + EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TMAP[i];
            File fExpectedTmap = new File(filePathExpectedTmap);
            assertTrue(myutil.FileUtils.checkFileForOpen(fExpectedTmap));
            String expectedOutputTmap = myutil.FileUtils.loadFileData(fExpectedTmap);
            //TraceManager.addDev("\nExpected:>" + expectedOutputTmap + "<");
            //TraceManager.addDev("\nObtained:>" + obtainedOutputTmap + "<");
            CompareTMAP ctmap = new CompareTMAP();
            assertTrue("comparing between 2 TMAP files", ctmap.CompareTMAPFiles(fObtainedTmap, fExpectedTmap));
            

            String filePathObtainedTarchi = OBTAINED_MODELS_PATH + OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TARCHI[i];
            File fObtainedTarchi = new File(filePathObtainedTarchi);
            assertTrue(myutil.FileUtils.checkFileForOpen(fObtainedTarchi));
            String obtainedOutputTarchi = myutil.FileUtils.loadFileData(fObtainedTarchi);
            String filePathExpectedTarchi = getBaseResourcesDir() + PATH_PATTERNS[0] + EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TARCHI[i];
            File fExpectedTarchi = new File(filePathExpectedTarchi);
            assertTrue(myutil.FileUtils.checkFileForOpen(fExpectedTarchi));
            String expectedOutputTarchi = myutil.FileUtils.loadFileData(fExpectedTarchi);
            CompareTMAP ctarchi = new CompareTMAP();
            assertTrue("comparing between 2 Tarchi files", ctarchi.CompareTMAPFiles(fObtainedTarchi, fExpectedTarchi));
            //TraceManager.addDev("\nExpected Tarchi:>" + expectedOutputTarchi + "<");
            //TraceManager.addDev("\nObtained Tarchi:>" + obtainedOutputTarchi + "<");

            String filePathObtainedTML = OBTAINED_MODELS_PATH + OBTAINED_MODELS_AFTER_INTEGRATING_TMR_TML[i];
            File fObtainedTML = new File(filePathObtainedTML);
            assertTrue(myutil.FileUtils.checkFileForOpen(fObtainedTML));
            String obtainedOutputTML = myutil.FileUtils.loadFileData(fObtainedTML);
            String filePathExpectedTML = getBaseResourcesDir() + PATH_PATTERNS[0] + EXPECTED_MODELS_AFTER_INTEGRATING_TMR_TML[i];
            File fExpectedTML = new File(filePathExpectedTML);
            assertTrue(myutil.FileUtils.checkFileForOpen(fExpectedTML));
            String expectedOutputTML = myutil.FileUtils.loadFileData(fExpectedTML);
            CompareTML ctml = new CompareTML();
            assertTrue("comparing between 2 TML files", ctml.compareTML(fObtainedTML, fExpectedTML));
            //TraceManager.addDev("\nExpected TML:>" + expectedOutputTML + "<");
            //TraceManager.addDev("\nObtained TML:>" + obtainedOutputTML + "<");
        }
        
    }
}
