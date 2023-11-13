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

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import avatartranslator.*;
import avatartranslator.intboolsolver.AvatarIBSolver;
import common.SpecConfigTTool;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.Plugin;
import myutil.TraceManager;

/**
 * Class AVATAR2SysMLV2
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
    private final static java.lang.String COMMA_SEP = ", ";
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
    private final static java.lang.String STATE = "state";
    private final static java.lang.String ENTRY = "entry";
    private final static java.lang.String EXIT = "exit";
    private final static java.lang.String FIRST = "first";
    private final static java.lang.String SEND = "send";
    private final static java.lang.String ACCEPT = "accept";
    private final static java.lang.String ACTION = "action";
    private final static java.lang.String IF = "IF";
    private final static java.lang.String DO = "do";
    private final static java.lang.String AFTER = "after";
    private final static java.lang.String THEN = "then";
    private final static String DEC = "\t";

    private final static String DEC_METHOD = PRIVATE + " " + METHOD;


    private AvatarSpecification avspec;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private String [] exclusions;

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


    public StringBuffer generateSysMLV2Spec(boolean _debug, boolean _tracing, String[] _exclusions) {
        debug = _debug;
        tracing = _tracing;
        exclusions = _exclusions;


        //TraceManager.addDev("AVATAR2SysMLV2 avspec=" + avspec);

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls();
        avspec.removeTimers();
        // Reset parser
        AvatarIBSolver.clearAttributes();

        //TraceManager.addDev("AVATAR2CPOSIX avspec=" + avspec);

        StringBuffer sysml = new StringBuffer();

       makeBlocks(sysml);

       makeInterconnections(sysml);



       return indent(sysml);

    }

    public  boolean isExcluded(String key) {
        return isExcluded(key, exclusions);
    }

    public static boolean isExcluded(String key, String []_exclusions) {
        if (_exclusions == null) {
          return false;
        }
        boolean ret = false;

        for(String s: _exclusions) {
            if (s.compareTo(key) == 0) {
                return true;
            }
        }

        return ret;
    }

    private void addIfNotExcluded(StringBuffer sb, String s, String category, String [] exclusions) {
        if (exclusions != null) {
            for (int i = 0; i < exclusions.length; i++) {
                if (exclusions[i].compareTo(category) == 0) {
                    return;
                }
            }
        }
        sb.append(s);
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

        if (!isExcluded("attributes")) {
            for (AvatarAttribute aa : block.getAttributes()) {
                sysml.append(getAttribute(aa) + END);
            }
        }

        if (block.getAttributes().size()>0) {
            sysml.append(CR);
        }

        // methods

        if (!isExcluded("method")) {
            for (AvatarMethod am : block.getMethods()) {
                //TraceManager.addDev("Handling method:" + am);
                sysml.append(getMethod(am) + END);
            }
        }

        if (block.getMethods().size()>0) {
            sysml.append(CR);
        }

        // signals
        if (!isExcluded("signals")) {
            for (AvatarSignal as : block.getSignals()) {
                //TraceManager.addDev("Handling method:" + as);
                sysml.append(getSignal(as) + END);
            }
        }

        // State machine

        sysml.append(CR);
        sysml.append(getStateMachine(block.getStateMachine()));

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
        sysml.append("// Binding between signals" + CR);
        for(AvatarRelation ar: avspec.getRelations()) {
            sysml.append(getBindings(ar));
        }
    }



    public void makeStateMachines(StringBuffer sysml) {
        for(AvatarBlock block: avspec.getListOfBlocks()) {
            sysml.append(getStateMachine(block.getStateMachine()));
        }
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
        java.lang.String ret = DEC_METHOD + " ";

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
        String characteristics = getRelationCharacteristics(ar);

        for(int i=0; i<size; i++) {
            ret.append(BIND + " " + "(" + characteristics + ")");
            ret.append(ar.getBlock1().getName() + "." + ar.getSignals1().get(i).getName() + " = " +
                    ar.getBlock2().getName() + "." + ar.getSignals2().get(i).getName() + END);
        }
        ret.append(CR);

        return ret;
    }

    public  StringBuffer getStateMachine(AvatarStateMachine asm) {
        StringBuffer ret = new StringBuffer("");

        // declare all states
        // Then handle each transition from a state until another state is reached

        if (!isExcluded("state")) {
            for (AvatarStateMachineElement asme : asm.getListOfElements()) {
                if (!isExcluded("state")) {
                    if (asme instanceof AvatarState) {
                        ret.append(STATE + " " + asme.getName() + END);
                    }
                }
            }

            ret.append(CR);

            for (AvatarStateMachineElement asme : asm.getListOfElements()) {
                if ((asme instanceof AvatarState) || (asme instanceof AvatarStartState)) {
                    ret.append(getStateHandling(asme));
                }
            }
        }

        return ret;
    }

    public static StringBuffer getStateHandling(AvatarStateMachineElement asme) {
        StringBuffer ret = new StringBuffer(CR);

        if (asme instanceof  AvatarStartState) {
            ret.append(STATE + " " + ENTRY + B_BRACKET);
        } else if (asme instanceof  AvatarState) {
            ret.append(STATE + " " + asme.getName() + B_BRACKET);
        }

        // for each transition getting our of this state
        for(AvatarStateMachineElement next: asme.getNexts()) {
            // We iterate until we reach a state or no next (in that case: exit)
            AvatarStateMachineElement toConsider = next;
            int nbB = 0;

            while(toConsider != null) {

                if (toConsider instanceof AvatarTransition) {
                    AvatarTransition at = (AvatarTransition) toConsider;
                    if (at.hasDelay()) {
                        ret.append(AFTER + " " + at.getMinDelay() + " " + at.getMaxDelay() + CR);
                    }
                    if (!(at.hasNonDeterministicGuard())) {
                        ret.append(IF + " " + at.getGuard() + CR);
                        ret.append(DO + " " + B_BRACKET + CR);
                        nbB ++;
                    }

                    if (at.hasActions()) {
                        for(AvatarAction aa: at.getActions()) {
                            ret.append(ACTION + " "+ aa.getName() + CR);
                        }
                    }

                } else if (toConsider instanceof AvatarRandom) {
                    AvatarRandom rand = (AvatarRandom) toConsider;
                    ret.append(ACTION + rand.getVariable() + " = random(" + rand.getMinValue() + ", " +
                            rand.getMaxValue() + ")" + CR);

                } else if (toConsider instanceof AvatarActionOnSignal) {
                    AvatarActionOnSignal aaos = (AvatarActionOnSignal) toConsider;
                    if (aaos.isSending()) {
                        ret.append(SEND + " " + aaos.getSignal().getName() + "(" + aaos.getAllVals() + ")" + CR);
                    } else {
                        ret.append(ACCEPT + " " + aaos.getSignal().getName() + "(" + aaos.getAllVals() + ")" + CR);
                    }
                }

                if (toConsider instanceof AvatarStopState) {
                    ret.append(THEN + " " + EXIT + END + CR);
                    toConsider = null;

                } else if (toConsider instanceof AvatarState) {
                    ret.append(THEN + " " + toConsider.getName() + END + CR);
                    toConsider = null;

                } else {
                    toConsider = toConsider.getNext(0);
                }

                if (toConsider == null) {
                    while(nbB>0) {
                        ret.append(E_BRACKET + CR);
                        nbB --;
                    }
                }


            }
        }

        ret.append(E_BRACKET);

        return ret;
    }



    public static String getRelationCharacteristics(AvatarRelation ar) {
        String ret = "type=";
        if (ar.isAsynchronous()) {
            ret += "asynchronous";
        } else if (ar.isAMS()){
            ret += "AMS";
        } else {
            ret += "synchronous";
        }


        if (ar.isAsynchronous()) {
            ret+= COMMA_SEP;
            ret += "isBlocking=" + ar.isBlocking();
            ret+= COMMA_SEP;
            ret+= "sizeOfFIFO=" + ar.getSizeOfFIFO();
        }

        if (!ar.isAsynchronous() && !ar.isAMS()) {
            ret+= COMMA_SEP;
            ret += "isBroadcast=" + ar.isBroadcast();
        }

        ret+= COMMA_SEP;
        ret += "isPrivate=" + ar.isPrivate();

        ret+= COMMA_SEP;
        ret += "isLossy=" + ar.isLossy();

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

    public static ArrayList<String> getAllBlockNames(String _spec) {
        ArrayList<String> listOfBlockNames = new ArrayList<>();

        // Reading spec line and line and looking for:
        // part <name> :
        Scanner scanner = new Scanner(_spec);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("part ")) {
                line = line.substring(5);
                int index = line.indexOf(":");
                if (index > -1) {
                    listOfBlockNames.add(line.substring(0, index).trim());
                }
            }
        }

        return listOfBlockNames;
    }



}
