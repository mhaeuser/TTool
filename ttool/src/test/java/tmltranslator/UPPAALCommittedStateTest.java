package tmltranslator;

import myutil.FileException;
import myutil.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import test.AbstractTest;
import tmltranslator.touppaal.TML2UPPAAL;
import uppaaldesc.UPPAALSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UPPAALCommittedStateTest extends AbstractTest {

    final String MODEL = "spec";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RESOURCES_DIR = getBaseResourcesDir() + "/tmltranslator/touppaal/";
    }

    public UPPAALCommittedStateTest() {
        super();
    }


    @Test
    public void compareUPPAALSpecTest() throws Exception {

        String s = MODEL;
        // Loading the input TML model
        System.out.println("executing: loading " + s);
        TMLTextSpecification tmlTextSpecification = new TMLTextSpecification(s);
        File f = new File(RESOURCES_DIR + s + ".tml");

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
        boolean parsed = tmlTextSpecification.makeTMLModeling(spec);
        assertTrue(parsed);

        // Checking syntax
        System.out.println("executing: checking syntax " + s);
        TMLModeling tmlModeling = tmlTextSpecification.getTMLModeling();
        TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlModeling);
        syntax.checkSyntax();
        assertEquals(syntax.hasErrors(), 0);


        // Generating UPPAAL specification from TML model
        TML2UPPAAL tml2UPPAAL = new TML2UPPAAL(tmlModeling);
        tml2UPPAAL.setSizeInfiniteFIFO(1024);
        UPPAALSpec uppaal = tml2UPPAAL.generateUPPAAL(false);
        // Saving UPPAAL specification
        try {
            tml2UPPAAL.saveInFile(RESOURCES_DIR);
        }
        catch (FileException e) {
            System.out.println("Exception saving generated UPPAAL specification " + s + ".xml");
        }


        // Checking if the first location of the automaton "Application__Component" is committed
        BufferedReader reader = new BufferedReader(new FileReader(RESOURCES_DIR + s + ".xml"));
        String line = reader.readLine();

        // 1. Extracting the lines corresponding to the first location of the automaton "Application__Component"
        ArrayList<String> location = new ArrayList<String>();
        boolean inTemplate = false;
        boolean inLocation = false;
        while (line != null){
            if(line.contains("<name>Application__Component</name>")){
                inTemplate = true;
            }
            else if(line.contains("<location id") & inTemplate){
                inLocation = true;
                location.add(line);
            }
            else if(line.contains("</location>") & inTemplate & inLocation){
                location.add(line);
                break;
            }
            else if(inTemplate & inLocation){
                location.add(line);
            }
            line = reader.readLine();
        }
        reader.close();

        // 2. Checking if the extracted location is committed
        boolean isCommitted = location.contains("<committed />") | location.contains("<committed/>");
        boolean isUrgent = location.contains("<urgent />") | location.contains("<urgent/>");
        if (isCommitted) {
            System.out.println("The first location of the automaton Application__Component is committed.");
            assertTrue(isCommitted | isUrgent);
        }
        else if (isUrgent) {
            System.out.println("The first location of the automaton Application__Component is urgent.");
            assertTrue(isCommitted | isUrgent);
        }
        else {
            System.out.println("The first location of the automaton Application__Component is not urgent nor committed.");
            assertTrue(isCommitted | isUrgent);
        }


    }


}
