package tmltranslator;

import avatartranslator.AvatarSpecification;
import avatartranslator.toproverif.AVATAR2ProVerif;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.AUTGraph;
import myutil.FileUtils;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import proverifspec.ProVerifSpec;
import req.ebrdd.EBRDD;
import tepe.TEPE;
import test.AbstractTest;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.toavatarsec.TML2Avatar;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.tomappingsystemc2.Penalties;
import ui.AbstractUITest;
import ui.TDiagramPanel;
import ui.TMLArchiPanel;
import ui.TURTLEPanel;
import ui.tmldd.TMLArchiDiagramPanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DiplodocusSecurityTest extends AbstractTest {
    final static String DIR_GEN = "tmltranslator/test_diplo_security/";
    final static String DIR_MODELS = "tmltranslator/test_diplo_security_models/";
    final String [] MODELS_DIPLO_SECURITY = {"symetric"};
    private static final List<List<String>> LIST_OF_LISTS_OF_QUERIES = Arrays.asList(
            Arrays.asList("Query not attacker(Alice___SymmetricExchange__comm_chData[!1 = v]) is true.")
            //Arrays.asList("string2a", "string2b"),
            //Arrays.asList("string3a", "string3b", "string3c", "string3d")
    );
    private static final String PROVERIF_SUMMARY = "Verification summary:";
    private static final String PROVERIF_QUERY = "Query";


    static String PROVERIF_DIR;
    static String MODELS_DIR;



    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        PROVERIF_DIR = getBaseResourcesDir() + DIR_GEN;
        MODELS_DIR = getBaseResourcesDir() + DIR_MODELS;
        RESOURCES_DIR = getBaseResourcesDir() + DIR_MODELS;
    }

    public DiplodocusSecurityTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        TraceManager.devPolicy = TraceManager.TO_CONSOLE;
        Path path = Paths.get(PROVERIF_DIR);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

    }

    @Test
    public void testSecurityModels() throws Exception {

        // Test if proverif is installed in the path
        System.out.println("Testing if \"proverif\" is in the PATH");
        if (!(canExecute("proverif"))) {
            return;
        }


        for (int i = 0; i < MODELS_DIPLO_SECURITY.length; i++) {
            String s = MODELS_DIPLO_SECURITY[i];
            System.out.println("Checking the security of " + s);
            TMLMappingTextSpecification tmts = new TMLMappingTextSpecification(s);
            File f = new File(RESOURCES_DIR + s + ".tmap");
            System.out.println("Loading file: " + f.getAbsolutePath());
            String spec = null;
            try {
                spec = FileUtils.loadFileData(f);
            } catch (Exception e) {
                System.out.println("Exception executing: loading " + s);
                assertTrue(false);
            }
            System.out.println("Testing spec " + s);
            assertTrue(spec != null);
            System.out.println("Going to parse " + s);
            boolean parsed = tmts.makeTMLMapping(spec, RESOURCES_DIR);
            assertTrue(parsed);

            System.out.println("Checking syntax " + s);
            // Checking syntax
            TMLMapping tmap = tmts.getTMLMapping();

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            syntax.checkSyntax();

            if (syntax.hasErrors() > 0) {
                for (TMLError error: syntax.getErrors()) {
                    System.out.println("Error: " + error.toString());
                }

            }

            assertTrue(syntax.hasErrors() == 0);

            // Generate ProVerif code
            System.out.println("Generating ProVerif code for " + s);
            TML2Avatar t2a = new TML2Avatar(tmap, false, true);
            AvatarSpecification avatarspec = t2a.generateAvatarSpec("2");
            AVATAR2ProVerif avatar2proverif = new AVATAR2ProVerif(avatarspec);
            ProVerifSpec proverif = avatar2proverif.generateProVerif(true, true, 0, true,
                    true);

            TraceManager.addDev("Saving spec in " + PROVERIF_DIR + s);
            FileUtils.saveFile(PROVERIF_DIR + s, proverif.getStringSpec());

            TraceManager.addDev("Running Proverif");
            Process proc;
            BufferedReader proc_in;
            String str;
            String cmd = "proverif -in pitype " + PROVERIF_DIR + s;
            boolean summaryFound = false;

            try {
                proc = Runtime.getRuntime().exec(cmd);
                proc_in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                while ((str = proc_in.readLine()) != null) {
                    // TraceManager.addDev( "Sending " + str + " from " + port + " to client..." );

                    System.out.println("Output from ProVerif: " + str);

                    if (summaryFound && (str.contains(PROVERIF_QUERY))) {
                        assertTrue(contains(i, str));
                    }

                    if (str.contains(PROVERIF_SUMMARY)) {
                        summaryFound = true;
                    }
                }
            } catch (Exception e) {
                // probably make is not installed
                System.out.println("FAILED: executing: " + cmd + ": " + e.getMessage());
                return;
            }

        }
    }

    private boolean contains(int index, String str) {
        str = str.trim();
        for(String s: LIST_OF_LISTS_OF_QUERIES.get(index)) {
            if (str.compareTo(s.trim()) == 0) {
                TraceManager.addDev("Query: " + s + " is correct");
                return true;
            }
        }
        return false;
    }
}
