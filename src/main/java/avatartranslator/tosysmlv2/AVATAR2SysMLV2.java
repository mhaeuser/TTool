/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */


package avatartranslator.tosysmlv2;

import java.io.File;
import java.util.List;
import java.util.Vector;

import avatartranslator.*;
import common.SpecConfigTTool;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.Plugin;
import myutil.TraceManager;

/**
 * Class AVATAR2CPOSIX
 * Creation: 21/09/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.3 15/01/2021
 */
public class AVATAR2SysMLV2 {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;


    //private final static String UNKNOWN = "UNKNOWN";
    private final static java.lang.String CR = "\n";
    private final static java.lang.String END = ";" + CR;
    private final static java.lang.String B_BRACKET = " {" + CR;
    private final static java.lang.String E_BRACKET = "}" + CR;
    private final static java.lang.String PART = "part";
    private final static java.lang.String PART_DEF = "part def";
    private final static java.lang.String ATTRIBUTE = "attribute";
    private final static java.lang.String METHOD = "method";
    private final static java.lang.String PRIVATE = "private";
    private final static java.lang.String ITEM = "item";
    private final static java.lang.String IN = "in";
    private final static java.lang.String OUT = "out";
    private final static java.lang.String BIND = "bind";
    private final static String DEC = "\t";


    private AvatarSpecification avspec;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;

    private Vector warnings;


    public AVATAR2SysMLV2(AvatarSpecification _avspec) {
        avspec = _avspec;
    }




    public void saveInFiles(String path) throws FileException {

        //TraceManager.addDev("save In Files AVATAR2CPOSIX");
        /*if (!SpecConfigTTool.checkAndCreateAVATARCodeDir(path)) {
            TraceManager.addDev("Directory cannot be created: " + path);
            throw new FileException("ERROR: Executable code directory cannot be created.");
        }*/
        //TraceManager.addDev("Creating dir for saving generated code");
        /*File src_dir = new File(path + GENERATED_PATH);
        if (!src_dir.exists()) {
            TraceManager.addDev("Creating: " + src_dir.getAbsolutePath());
            src_dir.mkdir();
        }*/

        //TraceManager.addDev("Generating main file");
        /*if (mainFile != null) {
            TraceManager.addDev("Generating main files in " + path + mainFile.getName() + ".h");
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".h", Conversion.indentString(mainFile.getHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".c", Conversion.indentString(mainFile.getMainCode(), 2));
        }

        //TraceManager.addDev("Generating task files");
        for (TaskFile taskFile : taskFiles) {
            TraceManager.addDev("Generating task files: " + (path + GENERATED_PATH + taskFile.getName()));
            FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".h", Conversion.indentString(taskFile.getFullHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".c", Conversion.indentString(taskFile.getMainCode(), 2));
        }*/



    }


    public Vector getWarnings() {
        return warnings;
    }


    public StringBuffer generateSysMLV2Spec(boolean _debug, boolean _tracing) {
        debug = _debug;
        tracing = _tracing;


        TraceManager.addDev("AVATAR2SysMLV2 avspec=" + avspec);

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls();
        avspec.removeTimers();

        //TraceManager.addDev("AVATAR2CPOSIX avspec=" + avspec);

        StringBuffer sysml = new StringBuffer();

       makeBlocks(sysml);

       makeInterconnections(sysml);

       makeStateMachines(sysml);

       return indent(sysml);

    }

    public void makeBlocks(StringBuffer sysml) {
        // Make non father blocks
        // For each block, consider the son


        StringBuffer partUsage = new StringBuffer("// Usage of parts" + CR);
        sysml.append("// Definition of parts" + CR);

        for(AvatarBlock block: avspec.getListOfBlocks()) {

            makePartDef(sysml, block);

            if (block.getFather() == null) {
                makePartUsage(partUsage, block, avspec.getListOfBlocks());
            }
        }

        sysml.append(partUsage);
    }

    public void makePartDef(StringBuffer sysml, AvatarBlock block) {
        sysml.append(PART_DEF + " " + getPartDef(block) + B_BRACKET);

        for(AvatarAttribute aa: block.getAttributes()) {
            sysml.append(getAttribute(aa) + END);
        }

        if (block.getAttributes().size()>0) {
            sysml.append(CR);
        }

        // methods
        for(AvatarMethod am: block.getMethods()) {
            //TraceManager.addDev("Handling method:" + am);
            sysml.append(getMethod(am) + END);
        }

        if (block.getMethods().size()>0) {
            sysml.append(CR);
        }

        // signals
        for(AvatarSignal as: block.getSignals()) {
            //TraceManager.addDev("Handling method:" + as);
            sysml.append(getSignal(as) + END);
        }

        // parts

        sysml.append(E_BRACKET + CR);
    }

    public void makePartUsage(StringBuffer sysml, AvatarBlock block, List<AvatarBlock> listOfBlocks) {
        sysml.append(PART + " " + block.getName() + " : " + getPartDef(block) + B_BRACKET);
        // Must add internal parts
        for(AvatarBlock internal: listOfBlocks) {
            if (internal.getFather() == block) {
                sysml.append(PART + " " + internal.getName() + " : " + getPartDef(internal) + END);
            }
        }

        sysml.append(E_BRACKET + CR);
    }

    public void makeInterconnections(StringBuffer sysml) {
        for(AvatarRelation ar: avspec.getRelations()) {
            sysml.append(getBindings(ar));
        }
    }



    public void makeStateMachines(StringBuffer sysml) {

    }



    public static String getAttribute(AvatarAttribute aa) {
        java.lang.String ret = PRIVATE + " " + ATTRIBUTE + " ";
        ret += aa.getName();

        if (aa.hasInitialValue()) {
            ret += " = " + aa.getInitialValue();
        }

        ret += " : " + aa.getType().getStringType();

        return ret;
    }

    public static String getMethod(AvatarMethod am) {
        java.lang.String ret = PRIVATE + " " + METHOD + " ";
        ret += am.toString();

        return ret;
    }

    public static String getSignal(AvatarSignal as) {
        java.lang.String ret = "";
        if (as.isIn()) {
            ret += IN;
        } else {
            ret += OUT;
        }
        ret += " " + ITEM + " " + as.toString();

        return ret;
    }

    public static StringBuffer getBindings(AvatarRelation ar) {
        StringBuffer ret = new StringBuffer("");

        int size = ar.getSignals1().size();

        for(int i=0; i<size; i++) {
            ret.append(BIND + " " + ar.getBlock1().getName() + "." + ar.getSignals1().get(i).getName() + " = " +
                    ar.getBlock2().getName() + "." + ar.getSignals2().get(i).getName() + END);
        }
        ret.append(CR);

        return ret;
    }

    public static String getPartDef(AvatarBlock _block) {
        return "Block__" + _block.getName();
    }

    public static StringBuffer indent(StringBuffer _input) {
        StringBuffer output = new StringBuffer();

        String[] lines = _input.toString().split("\\r?\\n");

        int dec = 0;
        for(int i=0; i<lines.length; i++) {
            String line = lines[i].trim();
            //TraceManager.addDev("Handling line=" + line);
            long countE = line.chars().filter(ch -> ch == '}').count();
            long countB = line.chars().filter(ch -> ch == '{').count();
            int decNeg = (int)(countE - countB);
            int decPrime = dec;
            if (decNeg > 0) {
                decPrime = dec - decNeg;

            }
            line = makeDec(line, decPrime);
            output.append(line + CR);

            dec += countB - countE;
        }

        return output;

    }

    public static String makeDec(String input, int dec) {
        //TraceManager.addDev("In of makeDec:" + input);
        String ret = "";
        for(int i=0; i<dec; i++) {
            ret += DEC;
        }
        ret += input;

        //TraceManager.addDev("Out of makeDec:" + ret);
        return ret;
    }



}
