/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paritech.fr
 * andrea.enrici AT telecom-paristech.fr
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
 */




import myutil.FileUtils;
import tmltranslator.TMLError;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLTextSpecification;
import tmltranslator.tomappingsystemc2.DiploSimulatorFactory;
import tmltranslator.tomappingsystemc2.IDiploSimulatorCodeGenerator;
import tmltranslator.toturtle.Mapping2TIF;
import tmltranslator.toturtle.TML2TURTLE;
import tmltranslator.touppaal.TML2UPPAAL;
import translator.TURTLEModeling;
import translator.TURTLETranslator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


/**
 * Class TMLTranslator
 * Linecommand application for translating TIF to other languages
 * Creation: 29/06/2007
 * @version 1.1 30/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLTranslator  {
    // 0 -> LOTOS
    // 1 -> UPPAAL
    // 2 -> SystemC
    // 3 -> SystemC2
    // 4 -> TML
    // 5 -> Systemc3

    private static int conversionType;
    private static File outputFile;
    private static String outputFileName;
    private static String inputData;
    private static String outputData;
    private static TMLModeling<Object> tmlm;
    private static TMLMapping<Object> tmap;
    private static boolean debug = false;
    private static boolean optimize = false;


    private static void printCopyright() {
        System.out.println("TMLTranslator: (C) GET/ENST, Ludovic APVRILLE, Andrea ENRICI\n{ludovic.apvrille, andrea.enrici}@telecom-paristech.fr");
        System.out.println("TMLTranslator is released under a CECILL License. See http://www.cecill.info/index.en.html");
        System.out.println("For more information on TURTLE related technologies, please consult http://ttool.telecom-paristech.fr/");

        System.out.println("Enjoy!!!\n");
    }

    private static void printUsage() {
        System.out.println("TMLTranlator: usage");
        System.out.println("TMLTranlator <options> <language> <inputfile> [<outputfile> or <outputdirectory>]");
        System.out.println("<options> are optional. There might be : -debug or -optimize");
        System.out.println("<language> may be: LOTOS, UPPAAL, SystemC, SystemC2, TML");
        System.out.println("LOTOS, UPPAAL: an output file must be provided");
        System.out.println("For SystemC, SystemC2, TML: a target directory must be provided");
        System.out.println("<inputfile> should be in TML format, and be readable");
        System.out.println("<outputfile> or <outputdirectory> should be writeable");
    }

    private static boolean checkArgs(String [] args) {
        return !(args.length < 3);
    }

    private static boolean hasDebug(String [] args) {
        return args[0].equals("-debug") || args[1].equals("-debug");
    }

    private static boolean hasOptimize(String[] args) {
        return args[0].equals("-optimize") || args[1].equals("-optimize");

    }

    private static boolean analyseArgs(String[] args) {
        System.out.println("Converting to " + args[0]);

        if (args[0].toUpperCase().equals("LOTOS")) {
            conversionType = 0;
        } else if (args[0].toUpperCase().equals("UPPAAL")) {
            conversionType = 1;
        } else if (args[0].toUpperCase().equals("SYSTEMC")) {
            conversionType = 2;
        } else if (args[0].toUpperCase().equals("SYSTEMC2")) {
            conversionType = 3;
        } else if (args[0].toUpperCase().equals("TML")) {
            conversionType = 4;
        } else {
            return false;
        }
        return true;
    }

    private static boolean prepareFiles(String args[]) {

        File inputFile = new File(args[1]);
        outputFile = new File(args[2]);
        outputFileName = args[2];
        try {
            if (!FileUtils.checkFileForOpen(inputFile)) {
                System.out.println("Cannot read file: " + args[1]);
                return false;
            }

            inputData = FileUtils.loadFileData(inputFile);

            if ((conversionType == 0) || (conversionType == 1)) {
                if (!FileUtils.checkFileForSave(outputFile)) {
                    System.out.println("Cannot create / open file: " + args[1]);
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }

        return true;
    }

    private static boolean checkSyntax(TMLSyntaxChecking syntax) {
        System.out.println("Syntax checking phase");
        syntax.checkSyntax();
        return (syntax.hasErrors() == 0);
    }

    private static boolean loadMapping(String title, String path) {
        boolean ret;
        System.out.println("Loading mapping");
        TMLMappingTextSpecification<Object> spec = new TMLMappingTextSpecification<>(title);
        ret = spec.makeTMLMapping(inputData, path);
        System.out.println("load ended:");
        List<TMLError> warnings;

        if (!ret) {
            System.out.println("\n\n*** TML Modeling *** \n");
            System.out.println("Compilation:\n" + spec.printSummary());
        }

        if (ret) {
            System.out.println("Format OK");
            tmap = spec.getTMLMapping();
            tmlm = tmap.getTMLModeling();


            //TMLTextSpecification textspec = new TMLTextSpecification("toto");
            //String s = textspec.toTextFormat(tmlm);
            //System.out.println(s);

            // Checking syntax
            System.out.println("--- Checking syntax of the whole specification (TML, TARCHI, TMAP)---");
            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
            ret = checkSyntax(syntax);

            if (!ret) {
                System.out.println("Compilation:\n" + syntax.printSummary());
                return false;
            }

            System.out.println("Compilation:\n" + spec.printSummary());


            if (optimize) {
                warnings = tmlm.optimize();
                System.out.println(tmlm.printSummary(warnings));
            }
            //spec.toTextFormat(tmlm);
            //System.out.println("TMLModeling=" + spec);
        }

        return ret;
    }

    private static boolean loadTML(String title) {
        boolean ret;
        List<TMLError> warnings;
        //System.out.println("load");
        TMLTextSpecification<Object> spec = new TMLTextSpecification<>(title, true);
        ret = spec.makeTMLModeling(inputData);
        //System.out.println("load ended");
        tmlm = spec.getTMLModeling();

        if (!ret) {
            System.out.println("Compilation:\n" + spec.printSummary());
        }

        if (ret) {
            //System.out.println("Format OK");
            spec.toTextFormat(tmlm);
            //System.out.println("TMLModeling=" + spec);

            TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmlm);
            ret = checkSyntax(syntax);

            if (!ret) {
                System.out.println("Compilation:\n" + syntax.printSummary());
                return false;
            }

            System.out.println("Compilation:\n" + spec.printSummary());

            if (optimize) {
                warnings = tmlm.optimize();
                System.out.println(tmlm.printSummary(warnings) + "\n");
            }
        }

        return ret;
    }

    private static boolean convertToLOTOSFromMapping() {
        Mapping2TIF m2tif = new Mapping2TIF(tmap);
        m2tif.setShowSampleChannels(false);
        m2tif.setShowChannels(true);
        m2tif.setShowEvents(true);
        m2tif.setShowRequests(true);
        m2tif.setShowExecs(false);
        m2tif.setShowBusTransfers(false);
        m2tif.setShowScheduling(false);
        m2tif.setIsClocked(false);
        m2tif.setTickValue("10");
        m2tif.setIsEndClocked(false);
        m2tif.setIsCountTick(true);
        m2tif.hasMaxCountTick(false);
        m2tif.setShowTaskState(false);
        m2tif.setShowChannelState(false);
        m2tif.setShowBlockedCPU(false);
        m2tif.setShowTerminateCPUs(true);
        m2tif.setShowBranching(false);
        m2tif.setRandomTasks(false);
        TURTLEModeling tm = m2tif.generateTURTLEModeling();

        TURTLETranslator tt = new TURTLETranslator(tm);
        outputData = tt.generateLOTOS(true);
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputData.getBytes());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error when writing LOTOS file");
            return false;
        }
        return true;
    }

    private static boolean convertToLOTOS() {
        TML2TURTLE totif = new TML2TURTLE(tmlm);
        TURTLEModeling tm = totif.generateTURTLEModeling();
        TURTLETranslator tt = new TURTLETranslator(tm);
        outputData = tt.generateLOTOS(true);
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputData.getBytes());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error when writing LOTOS file");
            return false;
        }
        return true;
    }

    private static boolean convertToUPPAAL() {
        TML2UPPAAL toup = new TML2UPPAAL(tmlm);
        toup.generateUPPAAL(debug);
        try {
            toup.saveInPathFile(outputFileName);
        } catch (Exception e) {
            System.out.println("Error when writing UPPAAL specification: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean convertToSystemC() {
        tmltranslator.tomappingsystemc.TML2MappingSystemC map;
        if (tmap == null) {
            map = new tmltranslator.tomappingsystemc.TML2MappingSystemC(tmlm);
        } else {
            map = new tmltranslator.tomappingsystemc.TML2MappingSystemC(tmap);
        }
        map.generateSystemC(debug, true);
        try {
            map.saveFile(outputFileName, "appmodel");
        } catch (Exception e) {
            System.out.println("SystemC generation failed: " + e.getMessage());
            return false;
        }
        //System.out.println("SystemC conversion not yet implemented");
        return true;
    }

    private static boolean convertToSystemC2() {
        //System.out.println("Converting to SystemC2 ... yo!");
        final IDiploSimulatorCodeGenerator map;
//        tmltranslator.tomappingsystemc2.TML2MappingSystemC map;
        if (tmap == null) {
            map = DiploSimulatorFactory.INSTANCE.createCodeGenerator( tmlm );
        } 
        else {
            map = DiploSimulatorFactory.INSTANCE.createCodeGenerator( tmap );
        }
        
        map.generateSystemC(debug, true);
        
        try {
            map.saveFile(outputFileName, "appmodel");
        } catch (Exception e) {
            System.out.println("SystemC generation failed: " + e.getMessage());
            return false;
        }
        //System.out.println("SystemC conversion not yet implemented");
        return true;
    }

    /*public static boolean convertToSystemC3() {

        tmltranslator.tomappingsystemc3.TML2MappingSystemC map;

        System.out.println("Converting to SystemC3...");
        if( tmap == null )      {
            System.out.println("Taking TMLModeling...");
            map = new tmltranslator.tomappingsystemc3.TML2MappingSystemC( tmlm );
        }
        else    {
            System.out.println("Taking TMLMapping...");
            map = new tmltranslator.tomappingsystemc3.TML2MappingSystemC( tmap );
        }
        map.generateSystemC( debug, true );
        try {
            map.saveFile( outputFileName, "appmodel" );
        }
        catch( Exception e ) {
            System.out.println( "SystemC generation failed: " + e.getMessage() );
            return false;
        }
        System.out.println( "Sorry...SystemC3 conversion not yet implemented" );
        return true;
	}*/

    private static boolean convertToTML() {

        if (tmap == null) {
            TMLTextSpecification<Object> tspec = new TMLTextSpecification<>("spec");
            tspec.toTextFormat(tmlm);
            try {
                tspec.saveFile(outputFileName, "spec");
            } catch (Exception e) {
                System.out.println("TML generation failed: " + e.getMessage());
                return false;
            }
        } else {
            TMLMappingTextSpecification<Object> mapspec = new TMLMappingTextSpecification<>("spec");
            mapspec.toTextFormat(tmap);
            try {
                mapspec.saveFile(outputFileName, "spec");
            } catch (Exception e) {
                System.out.println("TML/Mapping generation failed: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static boolean saveData() {
        try {
            System.out.println("Writing data in " + outputFile.getAbsolutePath() + " data=" + outputData);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(outputData.getBytes());
            fos.close();
        } catch (Exception e) {
            System.out.println("Error when writing output file: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String[] tmp;

        printCopyright();

        if (!checkArgs(args)) {
            printUsage();
            return;
        }

        int nbOfOptions = 0;
        if (hasDebug(args)) {
            debug = true;
            nbOfOptions ++;
        }
        if (hasOptimize(args)) {
            optimize = true;
            nbOfOptions ++;
        }

        if (nbOfOptions > 0) {
            debug = true;
            tmp = new String[args.length - nbOfOptions];
            System.arraycopy(args, nbOfOptions, tmp, 0, args.length - nbOfOptions);
            args = tmp;
        }

        if (!analyseArgs(args)) {
            printUsage();
            return;
        }

        if (!prepareFiles(args)) {
            printUsage();
            return;
        }

        if (args[1].endsWith(".tmap")) {
            String path;
            int index;
            index = args[1].lastIndexOf(File.separatorChar);
            if (index != -1) {
                path = args[1].substring(0, index+1);
            } else {
                path = "." + File.separatorChar;
            }
            //System.out.println("path=" + path);
            if (!loadMapping(args[1], path)) {
                System.out.println("Compilation failed => no code generation");
                return;
            }
        } else {
            if (!loadTML(args[1])) {
                System.out.println("Compilation failed => no code generation");
                return;
            }
        }

        boolean convert = false;

        //testSplit();
        /*String s = "\t \ttoto";
          System.out.println("toto=" + s);
          s = s.trim();
          System.out.println("toto=" + s);*/

        if (tmap != null) {
            System.out.println("Mapping: " + tmap.toXML());
        }

        switch(conversionType) {
            case 0:
                if (tmap == null) {
                    convert = convertToLOTOS();
                } else {
                    convert = convertToLOTOSFromMapping();
                }
                break;
            case 1:
                convert = convertToUPPAAL();
                break;
            case 2:
                convert = convertToSystemC();
                break;
            case 3:
                convert = convertToSystemC2();
                break;
            case 4:
                convert = convertToTML();
                break;
	    /*case 5:
	      convert = convertToSystemC3();*/
        }

        if (!convert) {
            System.out.println("Error during conversion");
            return;
        }

        System.out.println("Conversion done");

        if (outputData != null) {
            if (!saveData()) {
                return;
            }
            System.out.println("Specification written in " + outputFile.getName() + ": " + outputData.length() + " bytes");
        }



    }

} // Class TMLTranslator
