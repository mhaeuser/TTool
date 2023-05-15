package test;

import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.io.File.pathSeparator;
import static java.lang.System.getenv;
import static java.nio.file.Files.isExecutable;
import static java.util.regex.Pattern.quote;
import static org.junit.Assert.fail;

public abstract class AbstractTest {

    protected static final String TXT_EXT = ".txt";
    protected static final String XML_EXT = ".xml";
    protected static String RESOURCES_DIR = "";
    protected static String INPUT_DIR;
    protected static String EXPECTED_CODE_DIR;
    protected static String ACTUAL_CODE_DIR;

    protected static String getBaseResourcesDir() {
        final String systemPropResDir = System.getProperty("resources_dir");

        if (systemPropResDir == null) {
            return "resources/test/";
        }

        return systemPropResDir;
    }

    public static boolean canExecute(final String exe) {
        boolean existsInPath = Stream.of(System.getenv("PATH").
                        split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve(exe)));
        return existsInPath;
    }

    protected void checkResult(final String actualCode,
                               final String fileName) {
        try {
            final String expectedCode = FileUtils.loadFile(EXPECTED_CODE_DIR + fileName + TXT_EXT);

            if (!expectedCode.equals(actualCode)) {
                saveActualResults(fileName + TXT_EXT, actualCode);
            }
        } catch (FileException ex) {
            handleException(ex);
        }
    }

    protected void checkResultXml(final String actualCode,
                                  final String fileName) {

        System.out.println("Comparing with " + actualCode.substring(0, 30) + " with file: " + fileName);

        // Since this function fails because tasks are not always in the same order, it is deactivated


        try {
            final String expectedCode = FileUtils.loadFile(EXPECTED_CODE_DIR + fileName + XML_EXT);

            //FileUtils.saveFile(EXPECTED_CODE_DIR + fileName + XML_EXT, actualCode);

            if (!compareXml(actualCode, expectedCode)) {
                saveActualResults(fileName + XML_EXT, actualCode);
            }
        } catch (ParserConfigurationException | SAXException | IOException | FileException ex) {
            handleException(ex);
        }
    }

    private void saveActualResults(final String fileName,
                                   final String actualCode) {
        final String filePath = ACTUAL_CODE_DIR + fileName;
        final File fileToSave = new File(filePath);
        final File dir = fileToSave.getParentFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            FileUtils.saveFile(filePath, actualCode);
            fail("Differences were found between actual and expected code!!");
        } catch (final FileException ex) {
            handleException(ex);
        }
    }

    protected boolean compareXml(final String result,
                                 final String expected)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        documentFactory.setNamespaceAware(true);
        documentFactory.setCoalescing(true);
        documentFactory.setIgnoringElementContentWhitespace(true);
        documentFactory.setIgnoringComments(true);
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        final Document doc1 = documentBuilder.parse(new ByteArrayInputStream(result.getBytes()));
        doc1.normalizeDocument();

        final Document doc2 = documentBuilder.parse(new ByteArrayInputStream(expected.getBytes()));
        doc2.normalizeDocument();

        return doc1.isEqualNode(doc2);
    }

    protected void handleException(final Throwable th) {
        th.printStackTrace();
        fail(th.getLocalizedMessage());
    }

    protected String reworkStringForComparison(String input) {
        String ret = Conversion.replaceAllString(input, "\n", "");
        ret = Conversion.replaceAllString(ret, "\t", "");
        ret = Conversion.replaceAllString(ret, " ", "");
        return ret;


    }

    protected void monitorError(Process proc) {
        BufferedReader proc_err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        new Thread() {
            @Override
            public void run() {
                String line;
                try {
                    while ((line = proc_err.readLine()) != null) {
                        System.out.println("NOC executing err: " + line);
                    }
                } catch (Exception e) {
                    //System.out.println("FAILED reading errors");
                    return;
                }

            }
        }.start();
    }


}
