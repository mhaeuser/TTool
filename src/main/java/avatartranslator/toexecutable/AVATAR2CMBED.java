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




package avatartranslator.toexecutable;

import avatartranslator.*;
import myutil.*;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Class AVATAR2MBED
 * Creation: 
 * @version 1.1 	17/01/2018
 * @author Esteban BERNARD
 */
public class AVATAR2CMBED {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;


    private final static String UNUSED_ATTR = "__attribute__((unused))";
    private final static String GENERATED_PATH = "generated_src" + File.separator;
    private final static String UNKNOWN = "UNKNOWN";
    private final static String CR = "\n";

    private AvatarSpecification avspec;

    private Vector warnings;

    private MainFileMbed mainFileMbed;
    private Vector<TaskFileMbed> taskFilesMbed;
    private String makefile_src;
    private String makefile_SocLib;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private boolean includeUserCode = true;

    private Plugin plugin;


    public AVATAR2CMBED(AvatarSpecification _avspec, Plugin _plugin) {
        avspec = _avspec;
        plugin = _plugin;
    }

    public void setTimeUnit(int _timeUnit) {
        timeUnit = _timeUnit;
    }

    public void includeUserCode(boolean _inc) {
        includeUserCode = _inc;
    }

    public static String getGeneratedPath() {
        return GENERATED_PATH;
    }


    public void saveInFiles(String path) throws FileException {

        TraceManager.addDev("Generating files");

        if (mainFileMbed != null) {
            TraceManager.addDev("Generating main files in " + path + mainFileMbed.getName() + ".h");
            FileUtils.saveFile(path + GENERATED_PATH + mainFileMbed.getName() + ".h", Conversion.indentString(mainFileMbed.getHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + mainFileMbed.getName() + ".cpp", Conversion.indentString(mainFileMbed.getMainCode(), 2));
        }

        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            FileUtils.saveFile(path + GENERATED_PATH + taskFileMbed.getName() + ".h", Conversion.indentString(taskFileMbed.getFullHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + taskFileMbed.getName() + ".cpp", Conversion.indentString(taskFileMbed.getMainCode(), 2));
        }

        // Standard Makefile
        makeMakefileSrc(GENERATED_PATH);
        FileUtils.saveFile(path + "Makefile.src", makefile_src);

        // Makefile for SocLib
        makeMakefileSocLib();
        FileUtils.saveFile(path + "Makefile.soclib", makefile_SocLib);
    }


    public Vector getWarnings() {
        return warnings;
    }



    public void generateCMBED(boolean _debug, boolean _tracing) {
        debug = _debug;
        tracing = _tracing;
	
        mainFileMbed = new MainFileMbed("main", plugin);
        taskFilesMbed = new Vector<TaskFileMbed>();

        avspec.removeCompositeStates();
        avspec.removeLibraryFunctionCalls ();
        avspec.removeTimers();


        if (avspec.hasApplicationCode() && includeUserCode) {
            mainFileMbed.appendToBeforeMainCode("/* User code */\n");
            mainFileMbed.appendToBeforeMainCode(avspec.getApplicationCode());
            mainFileMbed.appendToBeforeMainCode("\n/* End of User code */\n\n");
        }

        makeMainMutex();

		makeConcurrencyMutex();
		
        makeSynchronousChannels();

        makeAsynchronousChannels();

        makeTasks();

        makeMainHeader();

        makeThreadsInMain(_debug);

    }

    public void makeMainMutex() {
        // Create a main mutex
        mainFileMbed.appendToHCode("/* Main mutex */" + CR);
        mainFileMbed.appendToBeforeMainCode("/* Main mutex */" + CR);
        mainFileMbed.appendToHCode("extern rtos::Mutex __mainMutex;" + CR + CR);
        mainFileMbed.appendToBeforeMainCode("rtos::Mutex __mainMutex;" + CR + CR);

    }
	
	public void makeConcurrencyMutex() {
        // Create a main mutex
        mainFileMbed.appendToHCode("/* ConcurrencyMutex mutex */" + CR);
        mainFileMbed.appendToBeforeMainCode("/* ConcurrencyMutex mutex */" + CR);
        mainFileMbed.appendToHCode("extern rtos::Mutex __concurrencyMutex;" + CR + CR);
        mainFileMbed.appendToBeforeMainCode("rtos::Mutex __concurrencyMutex;" + CR + CR);

    }

    public void makeSynchronousChannels() {

        // Create a synchronous channel per relation/signal
    	mainFileMbed.appendToHCode("/* TTOOL original generator */" + CR);
        mainFileMbed.appendToHCode("/* Synchronous channels */" + CR);
        mainFileMbed.appendToBeforeMainCode("/* Synchronous channels */" + CR);
        mainFileMbed.appendToMainCode("/* Synchronous channels */" + CR);
        for(AvatarRelation ar: avspec.getRelations()) {
            if (!ar.isAsynchronous()) {
                for(int i=0; i<ar.nbOfSignals(); i++) {
                    mainFileMbed.appendToHCode("extern syncchannel __" + getChannelName(ar, i)  + ";" + CR);
                    mainFileMbed.appendToBeforeMainCode("syncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
                    mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
                    if (ar.isBroadcast()) {
                        mainFileMbed.appendToMainCode("setBroadcast(&__" + getChannelName(ar, i) + ", true);" + CR);
                    }
                }
            }
        }

        //mainFileMbed.appendToHCode("pthread_mutex_t mainMutex;" + CR);

    }

    public void makeAsynchronousChannels() {

        // Create a synchronous channel per relation/signal
        mainFileMbed.appendToHCode("/* Asynchronous channels */" + CR);
        mainFileMbed.appendToBeforeMainCode("/* Asynchronous channels */" + CR);
        mainFileMbed.appendToMainCode("/* Asynchronous channels */" + CR);
        for(AvatarRelation ar: avspec.getRelations()) {
            if (ar.isAsynchronous()) {
                for(int i=0; i<ar.nbOfSignals(); i++) {
                    mainFileMbed.appendToHCode("extern asyncchannel __" + getChannelName(ar, i)  + ";" + CR);
                    mainFileMbed.appendToBeforeMainCode("asyncchannel __" + getChannelName(ar, i) + ";" + CR);
                    mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
                    mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
                    if (ar.isBlocking()) {
                        mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 1;" + CR);
                    } else {
                        mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 0;" + CR);
                    }
                    mainFileMbed.appendToMainCode("__" + getChannelName(ar, i) + ".maxNbOfMessages = " + ar.getSizeOfFIFO() + ";" + CR);
                }
            }
        }

        //mainFileMbed.appendToHCode("pthread_mutex_t mainMutex;" + CR);

    }

    public void makeTasks() {
        for(AvatarBlock block: avspec.getListOfBlocks()) {
            makeTask(block);
        }
    }

    public void makeTask(AvatarBlock block) {
        TaskFileMbed taskFileMbed = new TaskFileMbed(block.getName());

        //taskFileMbed.addToHeaderCode("#include \"main.h\"" + CR);

        //taskFileMbed.addToMainCode("#include \"" + block.getName() + ".h\"");

        if (includeUserCode) {
            String tmp = block.getGlobalCode();
            if (tmp != null) {
                taskFileMbed.addToMainCode(CR + "// Header code defined in the model" + CR + tmp + CR + "// End of header code defined in the model" + CR + CR);
            }
        }

        defineAllStates(block, taskFileMbed);

        defineAllMethods(block, taskFileMbed);

        makeMainFunction(block, taskFileMbed);

        taskFilesMbed.add(taskFileMbed);
    }

    public void defineAllStates(AvatarBlock _block, TaskFileMbed _taskFile) {
        int id = 1;

        _taskFile.addToMainCode("#define STATE__START__STATE 0" + CR);

        for (AvatarStateMachineElement asme: _block.getStateMachine().getListOfElements()) {
            if (asme instanceof AvatarState) {
                _taskFile.addToMainCode("#define STATE__" + asme.getName() + " " + id + CR);
                id ++;
            }
        }
        _taskFile.addToMainCode("#define STATE__STOP__STATE " + id + CR);
        _taskFile.addToMainCode(CR);
    }

    public void defineAllMethods(AvatarBlock _block, TaskFileMbed _taskFile) {
        Vector<String> allNames = new Vector<String>();
        for (AvatarMethod am: _block.getMethods()) {
            makeMethod(_block, am, allNames, _taskFile);
        }

        // Make method of father
        makeFatherMethod(_block, _block, allNames, _taskFile);
    }

    private void makeFatherMethod(AvatarBlock _originBlock, AvatarBlock _currentBlock, Vector<String> _allNames, TaskFileMbed _taskFile) {
        if (_currentBlock.getFather() == null) {
            return;
        }

        for (AvatarMethod am: _currentBlock.getFather().getMethods()) {
            makeMethod(_originBlock, am, _allNames, _taskFile);
        }

        makeFatherMethod(_originBlock, _currentBlock.getFather(), _allNames, _taskFile);

    }

    private void makeMethod(AvatarBlock _block, AvatarMethod _am, Vector<String> _allNames, TaskFileMbed _taskFile) {
        String ret = "";
        List<AvatarAttribute> list;
        List<AvatarAttribute> listA;

        String nameMethod = _block.getName() + "__" +_am.getName();

        for(String s: _allNames) {
            if (s.compareTo(nameMethod) == 0) {
                return;
            }
        }

        list = _am.getListOfReturnAttributes();
        if (list.size() == 0) {
            ret += "void";
        } else {
            ret += getCTypeOf(list.get(0));
        }

        ret += " " + nameMethod + "(";
        list = _am.getListOfAttributes();
        int cpt = 0;
        for(AvatarAttribute aa: list) {
            if (cpt != 0) {
                ret += ", ";
            }
            ret += getCTypeOf(aa) + " " + aa.getName();
            cpt ++;
        }

        ret += ") {" + CR;

        if (tracing) {
            String tr = "";
            cpt = 0;
            if (list.size() > 0) {
                ret += "char my__attr[CHAR_ALLOC_SIZE];" + CR;
                ret += "sprintf(my__attr, \"";
                for(AvatarAttribute aa: list) {
                    if (cpt != 0) {
                        tr += ",";
                        ret += ",";
                    }
                    tr += aa.getName();
                    ret += "%d";
                    cpt ++;
                }
                ret += "\"," + tr + ");" + CR;
                ret += traceFunctionCall(_block.getName(), _am.getName(), "my__attr");
            }  else {
                ret += traceFunctionCall(_block.getName(), _am.getName(), null);
            }
        }

        if (debug) {
            ret += "debugMsg(\"-> ....() Executing method " + _am.getName() + "\");" + CR;

            list = _am.getListOfAttributes();
            cpt = 0;
            for(AvatarAttribute aa: list) {
                ret += "debugInt(\"Attribute " + aa.getName() + " = \"," + aa.getName() + ");" + CR;
            }
        }

        listA = list;
        list = _am.getListOfReturnAttributes();
        if (list.size() != 0) {
            // Returns the first attribute. If not possible, return 0;
            // Implementation is provided by the user?
            // In that case, no need to generate the code!
            if (_am.isImplementationProvided()) {
                ret += "return __userImplemented__" + nameMethod + "(";
                cpt = 0;
                for(AvatarAttribute aaa: listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt ++;
                }
                ret+= ");" + CR;
                //TraceManager.addDev("Adding a call to the method");

            } else {

                if (listA.size() >0) {
                    ret += "return " + listA.get(0).getName() + ";" + CR;
                } else {
                    ret += "return 0;" + CR;
                }
            }
        } else {
            if (_am.isImplementationProvided()) {
                ret += "__userImplemented__" + nameMethod + "(";
                cpt = 0;
                for(AvatarAttribute aaa: listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt ++;
                }
                ret+= ");" + CR;

            }
        }
        ret += "}" + CR + CR;
        _taskFile.addToMainCode(ret + CR);

    }

    public void makeMainHeader() {
        mainFileMbed.appendToBeforeMainCode(CR);
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            mainFileMbed.appendToBeforeMainCode("#include \"" + taskFileMbed.getName() + ".h\"" + CR);
        }
        mainFileMbed.appendToBeforeMainCode(CR);

    }

    public void makeMainFunction(AvatarBlock _block, TaskFileMbed _taskFile) {
        int i;

        String s = "void mainFunc__" + _block.getName() + "(void *arg)";
        String sh = "extern " + s + ";" + CR;
        s+= "{" + CR;

        s += makeAttributesDeclaration(_block, _taskFile);

        s+= CR + "int __currentState = STATE__START__STATE;" + CR;

        int nbOfMaxParams = _block.getMaxNbOfParams();
        //s+= "request *__req;" + CR;
        for(i=0; i<_block.getMaxNbOfMultipleBranches(); i++) {
            s+= UNUSED_ATTR + " request __req" + i + ";" + CR;
            s+= UNUSED_ATTR + "int *__params" + i + "[" + nbOfMaxParams + "];" + CR;
        }
        s+= UNUSED_ATTR + "setOfRequests __list;" + CR;

        s+= UNUSED_ATTR + "size_t __myCond;" + CR;
        s+= UNUSED_ATTR + "request *__returnRequest;" + CR;

        s+= CR + "char * __myname = ((owner*)arg)->ownerName;" + CR;
		s+= "rtos::Thread * __myself = ((owner*)arg)->ownerThread;" + CR;
        /*if (tracing) {
          s+= CR + "char __value[CHAR_ALLOC_SIZE];" + CR;
          }*/

        //s+= CR + "pthread_cond_init(&__myCond, NULL);" + CR;

        s+= CR + "fillListOfRequests(&__list, __myname, __myself, &__myCond, &__mainMutex);" + CR;

        s+= "//printf(\"my name = %s\\n\", __myname);" + CR;

        s+= CR + "/* Main loop on states */" + CR;
        s+= "while(__currentState != STATE__STOP__STATE) {" + CR;

        
        s += "switch(__currentState) {" + CR;

        // Making start state
        AvatarStateMachine asm = _block.getStateMachine();
        s += "case STATE__START__STATE: " + CR;
        s += traceStateEntering("__myname", "__StartState");
        s += makeBehaviourFromElement(_block, asm.getStartState(), true);
        s += "break;" + CR + CR;

        String tmp;
        // Making other states
        for(AvatarStateMachineElement asme: asm.getListOfElements()) {
            if (asme instanceof AvatarState) {
                s += "case STATE__" + asme.getName() + ": " + CR;
                s += traceStateEntering("__myname", asme.getName());

                if (includeUserCode) {
                    tmp = ((AvatarState)asme).getEntryCode();
                    if (tmp != null) {
                        if (tmp.trim().length() > 0) {
                            s += "/* Entry code */\n" + tmp + "\n/* End of entry code */\n\n";
                        }
                    }
                }

                s += makeBehaviourFromElement(_block, asme, true);
                s += "break;" + CR + CR;
            }
        }

        s += "}" + CR;

        s += "}" + CR;

        s+= "//printf(\"Exiting = %s\\n\", __myname);" + CR;
        s+= "return ;" + CR;
        s += "}" + CR;
        _taskFile.addToMainCode(s + CR);
        _taskFile.addToHeaderCode(sh + CR);
    }

    public String makeBehaviourFromElement(AvatarBlock _block, AvatarStateMachineElement _asme, boolean firstCall) {
        AvatarStateMachineElement asme0;


        if (_asme == null) {
            return "";
        }

        String ret = "";
        int i;

        if (_asme instanceof AvatarStartState) {
            return makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarTransition) {
            AvatarTransition at = (AvatarTransition)_asme;

            if (at.isGuarded()) {
                String g = modifyGuard(at.getGuard().toString ());

                ret += "if (!" + g + ") {" + CR;
                if (debug) {
                    ret += "debug2Msg(__myname, \"Guard failed: " + g + "\");" + CR;
                }
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;
            }

            if (at.hasDelay()) {
                ret+= "waitFor(" + reworkDelay(at.getMinDelay()) + ", " + reworkDelay(at.getMaxDelay()) + ");" + CR;
            }

            String act;
            ret += makeActionsOfTransaction(_block, at);
            /*for(i=0; i<at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call
            act = at.getAction(i);
            if (at.isAMethodCall(act)) {
            ret +=  modifyMethodName(_block, act) + ";" + CR;
            } else {
            ret +=  act + ";" + CR;
            }
            }*/


            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarState) {
            if (!firstCall) {
                if (debug) {
                    ret += "debug2Msg(__myname, \"-> (=====) Entering state + " + _asme.getName() + "\");" + CR;
                }
                return ret + "__currentState = STATE__" + _asme.getName() + ";" + CR;
            } else {
                if (_asme.nbOfNexts() == 0) {
                    return ret + "__currentState = STATE__STOP__STATE;" + CR;
                }

                if (_asme.nbOfNexts() == 1) {
                    return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
                }

                // Complex case of states -> several nexts
                // Put in list all


                // 1) Only immediatly executable transitions
                for(i=0; i<_asme.nbOfNexts(); i++) {
                    if (_asme.getNext(i) instanceof AvatarTransition) {
                        AvatarTransition at = (AvatarTransition)(_asme.getNext(i));

                        if (at.hasActions()) {
                            ret += makeImmediateAction(at, i);
                        } else {
                            if (at.getNext(0) instanceof AvatarActionOnSignal) {
                                ret += makeSignalAction(at, i);
                            } else {
                                // nothing special to do : immediate choice
                                ret += makeImmediateAction(at, i);
                            }
                        }
                    }
                }

                // Make all requests
                // Test if at least one request in the list!
                ret += "if (nbOfRequests(&__list) == 0) {" + CR;
                ret += "debug2Msg(__myname, \"No possible request\");" + CR;
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;

                ret += "__returnRequest = executeListOfRequests(&__list);" + CR;
                ret += "clearListOfRequests(&__list);" + CR ;
                ret += traceRequest();

                // Resulting requests
                for(i=0; i<_asme.nbOfNexts(); i++) {
                    if (i != 0) {
                        ret += "else ";
                    }
                    AvatarTransition at = (AvatarTransition)(_asme.getNext(i));
                    if (at.hasActions()) {
                        ret += " if (__returnRequest == &__req" + i + ") {" + CR;
                        ret += makeActionsOfTransaction(_block, at);
                        /*for(int j=0; j<at.getNbOfAction(); j++) {
                          if (at.isAMethodCall(at.getAction(j))) {
                          ret +=  modifyMethodName(_block, at.getAction(j)) + ";" + CR;
                          } else {
                          ret +=  at.getAction(j) + ";" + CR;

                          }

                          }*/
                        ret += makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                    }  else {
                        if (at.getNext(0) instanceof AvatarActionOnSignal) {
                            ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0).getNext(0), false) + CR + "}";
                        } else {
                            // nothing special to do : immediate choice
                            ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                        }
                    }
                    ret += CR;

                }
                return ret;
            }
        }

        if (_asme instanceof AvatarStopState) {
            return ret + "__currentState = STATE__STOP__STATE;" + CR;
        }

        if (_asme instanceof AvatarRandom) {
            AvatarRandom ar = (AvatarRandom)_asme;
            ret += ar.getVariable() + " = computeRandom(" + ar.getMinValue() + ", " + ar.getMaxValue() + ");" + CR;
            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarActionOnSignal) {
            AvatarActionOnSignal aaos = (AvatarActionOnSignal)_asme;
            ret += makeSignalAction(aaos, 0, false, "", "");
            AvatarSignal as = aaos.getSignal();
            AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
            ret += executeOneRequest("__req0");
            ret += traceRequest();
        }

        // Default
        return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
    }

    private String makeSignalAction(AvatarTransition _at, int _index) {
        String ret = "";
        AvatarActionOnSignal aaos;

        if (!(_at.getNext(0) instanceof AvatarActionOnSignal)) {
            return "";
        }

        aaos = (AvatarActionOnSignal)(_at.getNext(0));

        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString ());
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += makeSignalAction(aaos, _index, true, _at.getMinDelay(), _at.getMaxDelay());
        } else {
            ret += makeSignalAction(aaos, _index, false, "", "");
        }
        ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;

        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;
    }

    private String makeSignalAction(AvatarActionOnSignal _aaos, int _index, boolean hasDelay, String minDelay, String maxDelay) {
        String ret = "";
        int i;

        AvatarSignal as = _aaos.getSignal();
        AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);

        String delay;

        if (hasDelay) {
            delay = "1, " + reworkDelay(minDelay) + ", " + reworkDelay(maxDelay);
        } else {
            delay = "0, 0, 0";
        }

        if (ar != null) {

            // Sending
            if (_aaos.isSending()) {
                // Putting params
                for(i=0; i<_aaos.getNbOfValues() ;i++) {
                    ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", SEND_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                    ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID()+ ", SEND_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                        ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID()+ ", SEND_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                        ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    }
                }

                // Receiving
            } else {
                for(i=0; i<_aaos.getNbOfValues() ;i++) {
                    ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
                }
                if (ar.isAsynchronous()) {
                    ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                    ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                } else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                        ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    } else {
                        ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
                        ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    }
                }
            }
        }

        return ret;
    }

    private String makeImmediateAction(AvatarTransition _at, int _index) {
        String ret = "";
        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString ());
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 1, " + reworkDelay(_at.getMinDelay()) + ", " + reworkDelay(_at.getMaxDelay()) + ", 0, __params" + _index + ");" + CR;
        } else {
            ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 0, 0, 0, 0, __params" + _index + ");" + CR;
        }
        ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;
        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;

    }

    private String executeOneRequest(String var) {
        String ret = "__returnRequest = executeOneRequest(&__list, &" + var + ");" + CR;
        ret += "clearListOfRequests(&__list);" + CR;
        return ret;
    }


    public String makeAttributesDeclaration(AvatarBlock _block, TaskFileMbed _taskFile) {
        String ret = "";
        for(AvatarAttribute aa: _block.getAttributes()) {
            ret += getCTypeOf(aa) + " " + aa.getName() + " = " + aa.getInitialValue() + ";" + CR;
        }
        return ret;
    }

    public void makeThreadsInMain(boolean _debug) {
        mainFileMbed.appendToMainCode(CR + "/* Threads of tasks */" + CR);
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            mainFileMbed.appendToMainCode("rtos::Thread thread__" + taskFileMbed.getName() + ";" + CR);
        }

        makeArgumentsInMain(_debug);

        if (_debug) {
            mainFileMbed.appendToMainCode("/* Activating debug messages */" + CR);
            mainFileMbed.appendToMainCode("activeDebug();" + CR);
        }



        mainFileMbed.appendToMainCode("/* Activating randomness */" + CR);
        mainFileMbed.appendToMainCode("initRandom();" + CR);

        //mainFileMbed.appendToMainCode("/* Initializing the main mutex */" + CR);
        //mainFileMbed.appendToMainCode("if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}" + CR + CR);

        mainFileMbed.appendToMainCode("/* Initializing mutex of messages */" + CR);
        mainFileMbed.appendToMainCode("initMessages();" + CR);


        if (avspec.hasApplicationCode()&& includeUserCode) {
            mainFileMbed.appendToMainCode("/* User initialization */" + CR);
            mainFileMbed.appendToMainCode("__user_init();" + CR);
        }


        mainFileMbed.appendToMainCode(CR + CR + mainDebugMsg("Starting tasks"));
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
			mainFileMbed.appendToMainCode(CR + "owner __" + taskFileMbed.getName() + ";" + CR);
			mainFileMbed.appendToMainCode("__" + taskFileMbed.getName() + ".ownerName = \"" + taskFileMbed.getName() + "\";" + CR);
			mainFileMbed.appendToMainCode("__" + taskFileMbed.getName() + ".ownerThread = &thread__" + taskFileMbed.getName() + ";" + CR);
            mainFileMbed.appendToMainCode("thread__" + taskFileMbed.getName() + ".start(mainFunc__" + taskFileMbed.getName() + ", (void*)&__" + taskFileMbed.getName() + ");"  + CR);
            
			//mainFileMbed.appendToMainCode("pthread_create(&thread__" + taskFileMbed.getName() + ", NULL, mainFunc__" + taskFileMbed.getName() + ", (void *)\"" + taskFileMbed.getName() + "\");" + CR);
        }

        mainFileMbed.appendToMainCode(CR + CR + mainDebugMsg("Joining tasks"));
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            mainFileMbed.appendToMainCode("thread__" + taskFileMbed.getName() + ".join();" + CR);
        }

        mainFileMbed.appendToMainCode(CR + CR + mainDebugMsg("Application terminated"));
        mainFileMbed.appendToMainCode("return 0;" + CR);
    }

    public void makeArgumentsInMain(boolean _debug) {
        mainFileMbed.appendToMainCode("/* Activating tracing  */" + CR);

        if (tracing) {
            mainFileMbed.appendToMainCode("if (argc>1){" + CR);
            mainFileMbed.appendToMainCode("activeTracingInFile(argv[1]);" + CR + "} else {" + CR);
            mainFileMbed.appendToMainCode("activeTracingInConsole();" + CR + "}" + CR);
        }
    }

    public void makeMakefileSrc(String _path) {
        makefile_src = "SRCS = ";
        makefile_src += _path + "main.c ";
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            makefile_src += _path + taskFileMbed.getName() + ".c ";
        }

    }

    public void makeMakefileSocLib() {
        makefile_SocLib = "objs = ";
        makefile_SocLib += "main.o ";
        for(TaskFileMbed taskFileMbed: taskFilesMbed) {
            makefile_SocLib += taskFileMbed.getName() + ".o ";
        }

    }


    public String getCTypeOf(AvatarAttribute _aa) {
        String ret = "int";
        if (_aa.getType() == AvatarType.BOOLEAN) {
            ret = "bool";
        }
        return ret;
    }

    public String getChannelName(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
    }

    public String getChannelName(AvatarRelation _ar, AvatarSignal _as) {
        int index = _ar.getIndexOfSignal(_as);
        return getChannelName(_ar, index);
    }

    public String modifyGuard(String _g) {
        String g = Conversion.replaceAllString(_g, "[", "(").trim();
        g = Conversion.replaceAllString(g, "]", ")").trim();
        g = Conversion.replaceOp(g, "and", "&&");
        g = Conversion.replaceOp(g, "or", "||");
        g = Conversion.replaceOp(g, "not", "!");
        TraceManager.addDev("Guard=" + g);
        return g;
    }

    public String reworkDelay(String _delay) {

        switch(timeUnit) {
        case USEC:
            return _delay;
        case MSEC:
            return "(" + _delay + ")*1000";
        case SEC:
            return "(" + _delay + ")*1000000";
        }

        return _delay;
    }

    private String modifyMethodName(AvatarBlock _ab, AvatarTerm term) {
        if (term instanceof AvatarAttribute)
            return term.getName ();
        if (term instanceof AvatarConstant)
            return term.getName ();
        if (term instanceof AvatarTermRaw)
            return term.getName ();
        if (term instanceof AvatarArithmeticOp) {
            AvatarArithmeticOp aop = (AvatarArithmeticOp) term;
            return this.modifyMethodName (_ab, aop.getTerm1 ())
                + aop.getOperator ()
                + this.modifyMethodName (_ab, aop.getTerm2 ());
        }
        if (term instanceof AvatarTuple) {
            boolean first = true;
            String res = "(";
            for (AvatarTerm tterm: ((AvatarTuple) term).getComponents ()) {
                if (first)
                    first = false;
                else
                    res += ", ";
                res += this.modifyMethodName (_ab, tterm);
            }

            return res + ")";
        }
        if (term instanceof AvatarTermFunction)
            return  _ab.getName () + "__" + ((AvatarTermFunction) term).getMethod ().getName ()
                + this.modifyMethodName (_ab, ((AvatarTermFunction) term).getArgs ());
        return "";
    }

    private String traceRequest() {
        if (!tracing) {
            return "";
        }
        return "traceRequest(__myname, __returnRequest);" + CR;
    }

    private String traceVariableModification(String blockName, String varName, String type) {
        if (!tracing) {
            return "";
        }

        return "traceVariableModification(\"" + blockName + "\", \"" + varName + "\", " + varName + "," + type + ");" + CR;
    }

    private String traceFunctionCall(String blockName, String functionName, String params) {
        if (!tracing) {
            return "";
        }

        if (params == null) {
            params = "\"-\"";
        }
        return "traceFunctionCall(\"" + blockName + "\", \"" + functionName + "\", " + params + ");" + CR;
    }

    private String traceStateEntering(String name, String stateName) {
        if (!tracing) {
            return "";
        }
        return "traceStateEntering(" + name + ", \"" + stateName + "\");" + CR;
    }

    private String mainDebugMsg(String s) {
        if (!debug) {
            return "";
        }
        return "debugMsg(\"" + s + "\");" + CR;
    }

    private String taskDebugMsg(String s) {
        if (!debug) {
            return "";
        }

        return "debug2Msg(__myname, \"" + s + "\");" + CR;
    }

    public String makeActionsOfTransaction(AvatarBlock _block, AvatarTransition _at) {
        String ret = "";
        String type;
        for(int i=0; i<_at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call

            AvatarAction act = _at.getAction(i);
            TraceManager.addDev("Action=" + act);
            if (act.isAMethodCall()) {
                TraceManager.addDev("Method call");
                String actModified = modifyMethodName (_block, (AvatarTermFunction) act);
                ret +=  actModified + ";" + CR;
            } else {
                TraceManager.addDev("Else");
                String actModified = modifyMethodName (_block, ((AvatarActionAssignment) act).getLeftHand ())
                    + " = " + modifyMethodName (_block, ((AvatarActionAssignment) act).getRightHand ());
                AvatarLeftHand leftHand = ((AvatarActionAssignment) act).getLeftHand ();
                ret += actModified + ";" + CR;
                if (leftHand instanceof AvatarAttribute) {
                    if (((AvatarAttribute) leftHand).isInt()) {
                        type = "0";
                    } else {
                        type = "1";
                    }
                    ret += traceVariableModification(_block.getName(), leftHand.getName (), type);
                }

            }
        }

        return ret;
    }

}
