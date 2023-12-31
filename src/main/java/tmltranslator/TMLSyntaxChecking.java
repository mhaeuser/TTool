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


package tmltranslator;

import compiler.tmlparser.ParseException;
import compiler.tmlparser.SimpleNode;
import compiler.tmlparser.TMLExprParser;
import compiler.tmlparser.TokenMgrError;
import myutil.Conversion;
import myutil.TraceManager;
import tmltranslator.tomappingsystemc2.DiploSimulatorCodeGenerator;
import translator.CheckingError;

import java.io.StringReader;
import java.util.*;


/**
 * Class TMLSyntaxChecking
 * Used verifying the syntax of a TML specification
 * Creation: 12/09/2007
 *
 * @author Ludovic APVRILLE
 * @version 1.0 12/09/2007
 */
public class TMLSyntaxChecking {

    private final String WRONG_ORIGIN_CHANNEL = "is not declared as an origin channel of the task";
    private final String WRONG_DESTINATION_CHANNEL = "is not declared as a destination channel of the task";
    private final String WRONG_ORIGIN_EVENT = "is not declared as an origin event of the task";
    private final String WRONG_DESTINATION_EVENT = "is not declared as a destination event of the task";
    private final String WRONG_ORIGIN_REQUEST = "is not declared as an origin request of the task";
    private final String SYNTAX_ERROR = "syntax error";
    private final String WRONG_VARIABLE_IDENTIFIER = "forbidden variable's name";
    private final String VARIABLE_ERROR = "variable is not used according to its type";
    private final String UNDECLARED_VARIABLE = "Unknown variable";
    private final String SYNTAX_ERROR_VARIABLE_EXPECTED = "syntax error (variable expected)";
    private final String TIME_UNIT_ERROR = "unknown time unit";
    private final String NO_NEXT_OPERATOR_ERROR = "No next operator";
    private final String SAME_PORT_NAME = "Two ports have the same name";
    private final String WRONG_PARAMS = "The number of params is not compatible";
    private final String DUPLICATE_NAMES = "Two elements have the same name";

    private final String INVALID_NB_OF_GUARD = "The number of guards is not equal to the number of next elements";

    private final String ONE_BUS_PER_MEMORY = "Each memory must be connected to exactly one bus";
    private final String AT_LEAST_ONE_MEMORY_PER_CHANNEL = "Channel must be mapped to one memory";
    private final String AT_LEAST_ONE_BUS_PER_CHANNEL = "Channel must be mapped to at least one bus";
    private final String TOO_MANY_MEMORIES = "Channel is mapped on more than one memory";
    private final String INVALID_CHANNEL_PATH = "Channel path is invalid";
    private final String INVALID_BUS_PATH = "Bus path is invalid for channel"; // Should be a warning only
    private final String INVALID_ROUTING = "No possible routing for channel"; // Should be a warning only
    private final String LINK_ISSUE = "All components must be linked to a bus or be a bus";

    private final String DUPLICATE_PATH_TO_BUS = "Path to bus is duplicated"; // Should be a warning only
    private final String ONLY_ONE_NOC = "Only one NoC can be used"; // Should be a warning only

    private final String AT_LEAST_ONE_TASK = "Mapping has no task"; // Should be a warning only

    private final String DUPLICATE_RD_AUTHENTICITY = "Channel with authenticity check shall have only one read operator"; // Warning only
    private final String DUPLICATE_WR_AUTHENTICITY = "Channel with authenticity check shall have only one write operator"; // Warning only

    private final String SECURITY_PATTERN_NOT_MAPPED = "Security configuration shall be mapped to at least one memory";


    private ArrayList<TMLError> errors;
    private ArrayList<TMLError> warnings;
    private TMLModeling<?> tmlm;
    private TMLMapping<?> mapping;

    private boolean syntaxCheckForMappingOnly = false;


    public TMLSyntaxChecking(TMLModeling<?> _tmlm) {
        tmlm = _tmlm;
    }

    public TMLSyntaxChecking(TMLMapping<?> _mapping) {
        mapping = _mapping;
        tmlm = mapping.getTMLModeling();
    }

    public TMLSyntaxChecking(TMLMapping<?> _mapping, boolean _syntaxCheckForMappingOnly) {
        mapping = _mapping;
        syntaxCheckForMappingOnly = _syntaxCheckForMappingOnly;
        tmlm = mapping.getTMLModeling();
    }

    public void checkSyntax() {

        errors = new ArrayList<TMLError>();
        warnings = new ArrayList<TMLError>();

        TraceManager.addDev("Checking syntax of TML Mapping/ Modeling");
        if (!syntaxCheckForMappingOnly) {
            checkDuplicateNames();

            checkReadAndWriteInChannelsEventsAndRequests();

            checkActionSyntax();

            checkNextActions();

            checkChoices();

            checkPortName();

            checkAValidPortName();

            //checkOnlyOneReadAndWriteForChannelsWithAuthenticityCheck();
        }

        // Mapping or architecture
        if (mapping != null) {
            checkMemoryConnections();
            checkMemoriesOfChannels();
            checkBusesOfChannels();
            checkPathToMemory();
            checkPathValidity();
            checkNonDuplicatePathToBuses();
            checkOneNOC();
            checkRouting();
            //TraceManager.addDev("Checking link bus");
            checkLinkBuses();

            // Check at least one task is mapped
            checkAtLeastOneTask();

            // Check that if there is a memory for a channel, the memory is connected to the path

            // checking mapping of security pattern
            checkMappingOfSecurityPattern();
        }

    }


    public int hasErrors() {
        if (errors == null) {
            return 0;
        }
        return errors.size();
    }

    public int hasWarnings() {
        if (warnings == null) {
            return 0;
        }
        return warnings.size();
    }

    public String getErrorAndWarningString() {
        StringBuffer ret = new StringBuffer("Errors:\n");
        for(TMLError e: getErrors()) {
            ret.append("- " + e.toString() + "\n");
        }

        ret.append("\nWarning:\n");
        for(TMLError e: getWarnings()) {
            ret.append("- " + e.toString() + "\n");
        }
        return ret.toString();
    }

    public ArrayList<TMLError> getErrors() {
        return errors;
    }

    public ArrayList<TMLError> getWarnings() {
        return warnings;
    }

    public void addErrorByReference(Object referenceObject, TMLTask t, TMLActivityElement elt, String message, int type) {
        TMLError error = new TMLError(type);
        error.message = message;
        error.task = t;
        error.element = elt;
        error.referenceObject = referenceObject;
        errors.add(error);
    }


    public void addError(TMLTask t, TMLActivityElement elt, String message, int type) {
        TMLError error = new TMLError(type);
        error.message = message;
        error.task = t;
        error.element = elt;
        if (t != null) {
            error.referenceObject = t.getReferenceObject();
        } else if (elt!= null) {
            error.referenceObject = elt.getReferenceObject();
        }
        errors.add(error);
    }

    public void addWarning(TMLTask t, TMLActivityElement elt, String message, int type) {
        TMLError error = new TMLError(type);
        error.message = message;
        error.task = t;
        error.element = elt;
        if (t != null) {
            error.referenceObject = t.getReferenceObject();
        } else if (elt!= null) {
            error.referenceObject = elt.getReferenceObject();
        }
        warnings.add(error);
    }


    public void checkNextActions() {
        for (TMLTask t : tmlm.getTasks()) {
            TMLActivity tactivity = t.getActivityDiagram();
            int n = tactivity.nElements();
            for (int i = 0; i < n; i++) {
                TMLActivityElement elt = tactivity.get(i);
                if (!(elt instanceof TMLStopState)) {
                    if (elt.getNbNext() == 0) {
                        addError(t, elt, elt.getName() + ": " + NO_NEXT_OPERATOR_ERROR, TMLError.ERROR_BEHAVIOR);
                    }
                }
            }
        }
    }

    public void checkChoices() {
        for (TMLTask t : tmlm.getTasks()) {
            TMLActivity tactivity = t.getActivityDiagram();
            int n = tactivity.nElements();
            for (int i = 0; i < n; i++) {
                TMLActivityElement elt = tactivity.get(i);
                if (elt instanceof TMLChoice) {
                    if (elt.getNbNext() != ((TMLChoice) elt).getNbGuard()) {
                        addError(t, elt, elt.getName() + ": " + INVALID_NB_OF_GUARD, TMLError.ERROR_BEHAVIOR);
                    }
                }
            }
        }
    }


    public void checkDuplicateNames() {
        List<TMLElement> elts;

        for(TMLTask task: tmlm.getTasks()) {
            elts = tmlm.getAllElementsWithName(task.getName());
            if ((elts != null) && (elts.size() > 1)) {
                addError(null, null, DUPLICATE_NAMES + ": invalid task name " + task.getName(), TMLError.ERROR_STRUCTURE);
            }
        }

        for(TMLChannel ch: tmlm.getChannels()) {
            elts = tmlm.getAllElementsWithName(ch.getName());
            if ((elts != null) && (elts.size() > 1)) {
                addError(null, null, DUPLICATE_NAMES + ": invalid channel name " + ch.getName(), TMLError.ERROR_STRUCTURE);
            }
        }

        for(TMLEvent evt: tmlm.getEvents()) {
            elts = tmlm.getAllElementsWithName(evt.getName());
            if ((elts != null) && (elts.size() > 1)) {
                addError(null, null, DUPLICATE_NAMES + ": invalid event name " + evt.getName(), TMLError.ERROR_STRUCTURE);
            }
        }

        for(TMLRequest req: tmlm.getRequests()) {
            elts = tmlm.getAllElementsWithName(req.getName());
            if ((elts != null) && (elts.size() > 1)) {
                addError(null, null, DUPLICATE_NAMES + ": invalid request name " + req.getName(), TMLError.ERROR_STRUCTURE);
            }
        }


    }




    //added by minh hiep
    public void checkAValidPortName() {
        List<TMLChannel> tmlChannels = tmlm.getChannels();
        List<TMLEvent> tmlEvents = tmlm.getEvents();
        List<TMLRequest> tmlRequests = tmlm.getRequests();

        for (TMLChannel channel : tmlChannels) {
            if (!TMLTextSpecification.isAValidId(channel.getName()))
                addError(null, null, WRONG_VARIABLE_IDENTIFIER + ": invalid port name", TMLError.ERROR_STRUCTURE);
        }

        for (TMLEvent event : tmlEvents) {
            if (!TMLTextSpecification.isAValidId(event.getName()))
                addError(null, null, WRONG_VARIABLE_IDENTIFIER + ": invalid port name", TMLError.ERROR_STRUCTURE);
        }

        for (TMLRequest request: tmlRequests) {
            if (!TMLTextSpecification.isAValidId(request.getName()))
                addError(null, null, WRONG_VARIABLE_IDENTIFIER + ": invalid port name", TMLError.ERROR_STRUCTURE);
        }
    }

    public void checkPortName() {
        // checks if two ports with the same type (origin, destination) have the same name
        HashMap<String, TMLPort> origin = new HashMap<String, TMLPort>();
        HashMap<String, TMLPort> destination = new HashMap<String, TMLPort>();

        // Channels
        for (TMLChannel tmlc : tmlm.getChannels()) {
            TMLPort p = tmlc.getOriginPort();
            tryToAddPort(p, origin, "origin");
            p = tmlc.getDestinationPort();
            tryToAddPort(p, destination, "destination");

            for (TMLPort po : tmlc.getOriginPorts()) {
                tryToAddPort(po, origin, "origin");
            }

            for (TMLPort po : tmlc.getDestinationPorts()) {
                tryToAddPort(po, destination, "destination");
            }
        }

        // Events
        for (TMLEvent tmle : tmlm.getEvents()) {
            TMLPort p = tmle.getOriginPort();
            tryToAddPort(p, origin, "origin");
            p = tmle.getDestinationPort();
            tryToAddPort(p, destination, "destination");

            for (TMLPort po : tmle.getOriginPorts()) {
                tryToAddPort(po, origin, "origin");
            }

            for (TMLPort po : tmle.getDestinationPorts()) {
                tryToAddPort(po, destination, "destination");
            }
        }

        // Request

        /*for(TMLRequest tmlr: tmlm.getRequests()) {
            TMLPort p = tmlr.getOriginPort();
            tryToAddPort(p, origin, "origin");
            p = tmlr.getDestinationPort();
            tryToAddPort(p, destination, "destination");

            for(TMLPort po: tmlr.getOriginPorts()) {
                tryToAddPort(po, origin, "origin");
            }

            for(TMLPort po: tmlr.getDestinationPorts()) {
                tryToAddPort(po, destination, "destination");
            }
        }*/

    }

    private void tryToAddPort(TMLPort _p, HashMap<String, TMLPort> map, String origin) {
        if (_p == null) {
            return;
        }

        TMLPort inP = map.get(_p.getName());

        if (inP != null) {
            addError(null, null, SAME_PORT_NAME + ": " + _p.getName() + "(" + origin + " ports)", TMLError.ERROR_STRUCTURE);
        } else {
            //TraceManager.addDev("Adding port with name=" + _p.getName() + " for kind=" + origin);
            map.put(_p.getName(), _p);
        }
    }

    public void checkReadAndWriteInChannelsEventsAndRequests() {
        TMLChannel ch;
        TMLEvent evt;
        TMLRequest request;

        for (TMLTask t : tmlm.getTasks()) {
            TMLActivity tactivity = t.getActivityDiagram();
            TMLActivityElement elt;
            int n = tactivity.nElements();
            for (int i = 0; i < n; i++) {
                elt = tactivity.get(i);
                //TraceManager.addDev("Task= " + t.getName() + " element=" + elt);

                if (elt instanceof TMLWriteChannel) {
                    for (int j = 0; j < ((TMLWriteChannel) elt).getNbOfChannels(); j++) {
                        ch = ((TMLWriteChannel) elt).getChannel(j);
                        if (ch.isBasicChannel()) {
                            //TraceManager.addDev("Write in channel" + ch.getName());
                            if (ch.getOriginTask() != t) {
                                //TraceManager.addDev("Origin task=" + ch.getOriginTask().getName() + " / task = " + t.getName() + "tch=" + ch.getOriginTask() + " t=" + t);
                                //TraceManager.addDev("tml:" + tmlm.toString());
                                //  TMLTextSpecification  tmlt = new TMLTextSpecification("toto");
                                //TraceManager.addDev("tml:" + tmlt.toTextFormat(tmlm));
                                addError(t, elt, ch.getName() + ": " + WRONG_ORIGIN_CHANNEL, TMLError.ERROR_BEHAVIOR);
                            }
                        }
                    }
                }

                if (elt instanceof TMLReadChannel) {
                    ch = ((TMLReadChannel) elt).getChannel(0);
                    if (ch.isBasicChannel()) {
                        //TraceManager.addDev("Read channel");
                        if (ch.getDestinationTask() != t) {
                            addError(t, elt, ch.getName() + ": " + WRONG_DESTINATION_CHANNEL, TMLError.ERROR_BEHAVIOR);
                        }
                    }
                }

                if (elt instanceof TMLSendEvent) {
                    evt = ((TMLSendEvent) elt).getEvent();
                    //TraceManager.addDev("In Task " + t.getName() + " = " + evt);
                    if (evt == null) {
                        addError(t, elt, "Null event in SendEvt of Task " + t.getName(), TMLError.ERROR_BEHAVIOR);
                    } else {
                        if (evt.isBasicEvent()) {
                            //TraceManager.addDev("send evt= " + evt.getName() + " task=" + t.getName() + " origin=" + evt.getOriginTask().getName());
                            if (evt.getOriginTask() != t) {
                                addError(t, elt, evt.getName() + ": " + WRONG_ORIGIN_EVENT, TMLError.ERROR_BEHAVIOR);
                            }
                        }
                    }
                }

                if (elt instanceof TMLWaitEvent) {
                    evt = ((TMLWaitEvent) elt).getEvent();
                    if (evt.isBasicEvent()) {
                        /*try {
                            TraceManager.addDev("wait evt= " + evt.getName());
                        } catch (Exception e) {
                            TraceManager.addDev("Error on evt = " + evt);
                        }
                        if (evt.getDestinationTask() == null) {
                            TraceManager.addDev("Null destination task");
                        }*/

                        //TraceManager.addDev("wait evt= " + evt.getName() + " task=" + t.getName() + " destination=" + evt.getDestinationTask().getName());
                        if (evt.getDestinationTask() != t) {
                            addError(t, elt, evt.getName() + ": " + WRONG_DESTINATION_EVENT, TMLError.ERROR_BEHAVIOR);
                        }
                    }
                }

                if (elt instanceof TMLNotifiedEvent) {
                    evt = ((TMLNotifiedEvent) elt).getEvent();
                    //TraceManager.addDev("Write channel");
                    if (evt.getDestinationTask() != t) {
                        addError(t, elt, evt.getName() + ": " + WRONG_DESTINATION_EVENT, TMLError.ERROR_BEHAVIOR);
                    }
                }

                if (elt instanceof TMLSendRequest) {
                    request = ((TMLSendRequest) elt).getRequest();
                    //TraceManager.addDev("Write channel");
                    if (!request.isAnOriginTask(t)) {
                        addError(t, elt, request.getName() + ": " + WRONG_ORIGIN_REQUEST, TMLError.ERROR_BEHAVIOR);
                    }
                }
            }
        }
    }

    public void checkActionSyntax() {
        TMLWaitEvent tmlwe;
        TMLSendEvent tmlase;
        TMLSendRequest tmlsr;
        TMLChoice choice;
        TMLForLoop loop;
        TMLEvent evt;
        TMLRequest req;
        TMLType type;
        TMLRandom random;
        int j;
        int elseg, afterg;
        TMLAttribute attr;

        //  StringReader toParse;
        String action;


        for (TMLTask t : tmlm.getTasks()) {
            TMLActivity tactivity = t.getActivityDiagram();
            TMLActivityElement elt;

            // Checking names of atrributes
            for (TMLAttribute attri : t.getAttributes()) {
                if (!TMLTextSpecification.isAValidId(attri.getName())) {
                    addError(t, null, WRONG_VARIABLE_IDENTIFIER + ": invalid identifier", TMLError.ERROR_STRUCTURE);
                }
            }

            int n = tactivity.nElements();
            //TraceManager.addDev("Task" + t.getName());
            for (int i = 0; i < n; i++) {
                elt = tactivity.get(i);
                //TraceManager.addDev("elt=" + elt);
                if (elt instanceof TMLActionState) {
                    action = ((TMLActivityElementWithAction) elt).getAction();
                    parsingAssignment(t, elt, action);

                } else if (elt instanceof TMLActivityElementWithAction) {
                    action = ((TMLActivityElementWithAction) elt).getAction();
                    parsing(t, elt, "actionnat", action);

                } else if (elt instanceof TMLActivityElementWithIntervalAction) {
                    //TraceManager.addDev("Parsing TMLActivityElementWithIntervalAction");
                    action = ((TMLActivityElementWithIntervalAction) elt).getMinDelay();
                    parsing(t, elt, "actionnat", action);
                    action = ((TMLActivityElementWithIntervalAction) elt).getMaxDelay();
                    parsing(t, elt, "actionnat", action);

                    if (elt instanceof TMLDelay) {
                        action = ((TMLDelay) elt).getUnit().trim();

                        if (!(TMLDelay.isAValidUnit(action))) {
                            addError(t, elt, TIME_UNIT_ERROR + "in expression " + action, TMLError.ERROR_BEHAVIOR);
                        }
                    }

                } else if (elt instanceof TMLActivityElementChannel) {
                    action = ((TMLActivityElementChannel) elt).getNbOfSamples();
                    parsing(t, elt, "actionnat", action);

                } else if (elt instanceof TMLSendEvent) {
                    tmlase = (TMLSendEvent) elt;
                    evt = tmlase.getEvent();

                    if (evt == null) {
                        addError(t, elt, "Empty event in Sendevent of task " + t.getTaskName(), TMLError.ERROR_BEHAVIOR);
                    } else if (tmlase.getNbOfParams() != evt.getNbOfParams()) {
                        addError(t, elt, WRONG_PARAMS + " between event " + evt.getName() +
                                " (nb:" + evt.getNbOfParams() +
                                ") and send event (nb:" + tmlase.getNbOfParams() + ") in task " + t.getTaskName(), TMLError.ERROR_BEHAVIOR);
                    } else {

                        for (j = 0; j < tmlase.getNbOfParams(); j++) {
                            action = tmlase.getParam(j);
                            if ((action != null) && (action.length() > 0)) {
                                type = evt.getType(j);
                                if ((type == null) || (type.getType() == TMLType.NATURAL)) {
                                    parsing(t, elt, "actionnat", action);
                                } else {
                                    parsing(t, elt, "actionbool", action);
                                }
                            }
                        }
                    }

                } else if (elt instanceof TMLWaitEvent) {
                    tmlwe = (TMLWaitEvent) elt;
                    evt = tmlwe.getEvent();
                    //TraceManager.addDev("Nb of params of wait event:" + tmlwe.getNbOfParams() + " task=" + t.getTaskName());


                    if (tmlwe.getNbOfParams() != evt.getNbOfParams()) {
                        addError(t, elt, WRONG_PARAMS + " between event " + evt.getName() +
                                " (nb:" + evt.getNbOfParams() +
                                ") and wait event (nb:" + tmlwe.getNbOfParams() + ") in task " + t.getTaskName(), TMLError.ERROR_BEHAVIOR);
                    } else {

                        for (j = 0; j < tmlwe.getNbOfParams(); j++) {
                            action = tmlwe.getParam(j).trim();
                            if ((action != null) && (action.length() > 0)) {
                                if (!(Conversion.isId(action))) {
                                    addError(t, elt, SYNTAX_ERROR_VARIABLE_EXPECTED + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                                } else {
                                    // Declared variable?
                                    attr = t.getAttributeByName(action);
                                    if (attr == null) {
                                        addError(t, elt, UNDECLARED_VARIABLE + ": " + action + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                                        TraceManager.addDev("1 In task: " + t.getName() + " extended name:" + t.getNameExtension());
                                    } else {
                                        //TraceManager.addDev("Nb of params:" + tmlwe.getEvent().getNbOfParams() + " j:" + j);
                                        if (tmlwe.getEvent().getType(j).getType() == 0) {
                                            TraceManager.addDev("0");
                                        }
                                        if (attr.getType().getType() != tmlwe.getEvent().getType(j).getType()) {
                                            TraceManager.addDev("Type0:" + attr.getType().getType() + " type1:" + tmlwe.getEvent().getType(j).getType());
                                            addError(t, elt, VARIABLE_ERROR + " :" + action + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if (elt instanceof TMLSendRequest) {
                    tmlsr = (TMLSendRequest) elt;
                    req = tmlsr.getRequest();
                    for (j = 0; j < tmlsr.getNbOfParams(); j++) {
                        action = tmlsr.getParam(j);
                        if ((action != null) && (action.length() > 0)) {
                            type = req.getType(j);
                            if ((type == null) || (type.getType() == TMLType.NATURAL)) {
                                parsing(t, elt, "actionnat", action);
                            } else {
                                parsing(t, elt, "actionbool", action);
                            }
                        }
                    }

                } else if (elt instanceof TMLChoice) {
                    choice = (TMLChoice) elt;
                    elseg = choice.getElseGuard();
                    afterg = choice.getAfterGuard();
                    for (j = 0; j < choice.getNbGuard(); j++) {
                        /*if (action.length() == 1) {
                            if ((action.compareTo("[") == 0) || (action.compareTo("]") == 0)) {
                                addError(t, elt, SYNTAX_ERROR  + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                            }

                        }*/
                        //TraceManager.addDev("Testing guard: " + choice.getGuard(j));
                        if (!choice.isNonDeterministicGuard(j) && !choice.isStochasticGuard(j)) {
                            if ((j != elseg) && (j != afterg)) {
                                action = choice.getGuard(j);
                                action = action.trim();

                                parsing(t, elt, "guard", action);
                            }
                        }
                    }

                } else if (elt instanceof TMLForLoop) {
                    loop = (TMLForLoop) elt;
                    if (loop.getInit().trim().length() > 0) {
                        parsing(t, elt, "assnat", loop.getInit());
                    }
                    if (loop.getCondition().trim().length() > 0) {
                        parsing(t, elt, "actionbool", loop.getCondition());
                    }
                    if (loop.getIncrement().trim().length() > 0) {
                        parsing(t, elt, "assnat", loop.getIncrement());
                    }

                } else if (elt instanceof TMLRandom) {
                    random = (TMLRandom) elt;
                    parsing(t, elt, "actionnat", random.getMinValue());
                    parsing(t, elt, "actionnat", random.getMaxValue());
                    parsing(t, elt, "natid", random.getVariable());
                    parsing(t, elt, "natnumeral", "" + random.getFunctionId());
                }
            }
        }
    }

    public void parsingAssignment(TMLTask t, TMLActivityElement elt, String action) {
        int index = action.indexOf("=");

        if (index == -1) {
            addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        }

        String var = action.substring(0, index).trim();
        TMLAttribute attrFound = null;
        for (TMLAttribute attr : t.getAttributes()) {
            if (attr.getName().compareTo(var) == 0) {
                attrFound = attr;
                break;
            }
        }

        if (attrFound == null) {
            addError(t, elt, UNDECLARED_VARIABLE+ " :" + var + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            TraceManager.addDev("2 In task: " + t.getName() + " extended name:" + t.getNameExtension());
            return;
        }

        if (attrFound.isNat()) {
            parsing(t, elt, "assnat", action);
        } else {
            parsing(t, elt, "assbool", action);
        }

    }


    /**
     * Parsing in two steps:
     * 1. Parsing the expression with no variable checking
     * 2. Parsing the expression with variables values to see whether variables are well-placed or not
     * The second parsing is performed iff the first one succeeds
     * @param t         : TML task {@link TMLTask}
     * @param elt       : TML activity element {@link TMLActivityElement}
     * @param parseCmd  : String - parse command
     * @param action    : String
     */
    public void parsing(TMLTask t, TMLActivityElement elt, String parseCmd, String action) {
        if (action == null) {
            return;
        }
        TMLExprParser parser;
        SimpleNode root;

        // First parsing
        if (!(parseCmd.startsWith("guard"))) {
            parser = new TMLExprParser(new StringReader(parseCmd + " " + action));
            try {
                //TraceManager.addDev("\nParsing :" + parseCmd + " " + action);
                root = parser.CompilationUnit();
                //root.dump("pref=");
                //TraceManager.addDev("Parse ok");
            } catch (ParseException e) {
                TraceManager.addDev("ParseException --------> Parse error in: " + parseCmd + " " + action);
                addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                return;
            } catch (TokenMgrError tke) {
                TraceManager.addDev("TokenMgrError --------> Parse error in: " + parseCmd + " " + action);
                addError(t, elt, SYNTAX_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
                return;
            }
        }

        // Second parsing
        // We only replace variables values after the "=" sign
        if (parseCmd.compareTo("natnumeral") == 0) {
            return;
        }
        int index = action.indexOf('=');
        String modif = action;

        if ((parseCmd.compareTo("assnat") == 0) || (parseCmd.compareTo("assbool") == 0)) {
            if (index != -1) {
                modif = action.substring(index + 1, action.length());
            }

            if (parseCmd.compareTo("assnat") == 0) {
                parseCmd = "actionnat";
            } else {
                parseCmd = "actionbool";
            }
        }

        if (parseCmd.compareTo("natid") == 0) {
            parseCmd = "natnumeral";
        }

        for (TMLAttribute attr : t.getAttributes()) {
            modif = tmlm.putAttributeValueInString(modif, attr);
        }
        parser = new TMLExprParser(new StringReader(parseCmd + " " + modif));
        try {
            //TraceManager.addDev("\nParsing :" + parseCmd + " " + modif);
            root = parser.CompilationUnit();
            //root.dump("pref=");
            //TraceManager.addDev("Parse ok");
        } catch (ParseException e) {
            TraceManager.addDev("ParseException --------> Parse error in :" + parseCmd + " " + action);
            addError(t, elt, VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        } catch (TokenMgrError tke) {
            TraceManager.addDev("TokenMgrError --------> Parse error in :" + parseCmd + " " + action + " modified action:" + modif);
            addError(t, elt, VARIABLE_ERROR + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            return;
        }

        // Tree analysis: if the tree contains a variable, then, this variable has not been declared
        List<String> vars = root.getVariables();
        for (String s : vars) {
            addError(t, elt, UNDECLARED_VARIABLE + " :" + s + " in expression " + action, TMLError.ERROR_BEHAVIOR);
            TraceManager.addDev("3 In task: " + t.getName() + " extended name:" + t.getNameExtension());
            TraceManager.addDev("TASK:" + t.toXML());
        }

    }

    public String printSummary() {
        String ret = "";
        if (errors.size() == 0) {
            ret += printWarnings();
            ret += "Syntax checking: successful\n";
            ret += "No error, " + warnings.size() + " warning(s)\n";
        } else {
            ret += printErrors() + printWarnings();
            ret += "Syntax checking: failed\n";
            ret += errors.size() + " error(s), " + warnings.size() + " warning(s)\n";
        }

        return ret;
    }

    public String printErrors() {
        String ret = "*** ERRORS:";
        for (TMLError error : errors) {
            ret += "ERROR / task " + error.task.getName() + " / element " + error.element.getName() + ": " + error.message + "\n";
        }
        return ret;
    }

    public String printWarnings() {
        String ret = "";
        for (TMLError error : warnings) {
            ret += "ERROR / task " + error.task.getName() + " / element: " + error.element.getName() + ": " + error.message + "\n";
        }
        return ret;
    }

    public void checkMemoryConnections() {
        if (mapping == null) {
            return;
        }

        TMLArchitecture tmlArchitecture = mapping.getTMLArchitecture();
        if (tmlArchitecture == null) {
            return;
        }

        for(HwNode node: tmlArchitecture.getMemories()) {
            int cpt = 0;
            for(HwLink link: tmlArchitecture.getHwLinks()) {
                if (link.hwnode == node) {
                    cpt ++;
                    if (cpt > 1) {
                        addError(null, null, ONE_BUS_PER_MEMORY + ": " + node.getName(), TMLError.ERROR_STRUCTURE);
                        break;
                    }
                }
            }
        }
    }



    // Mapping
    public void checkMemoriesOfChannels() {

        Iterator<TMLChannel> channelIt = tmlm.getChannels().iterator();
        while (channelIt.hasNext()) {
            TMLChannel ch = channelIt.next();
            TraceManager.addDev("Checking mapping of channel:" + ch.getName());
            int n = mapping.getNbOfMemoriesOfChannel(ch);
            if (n == 0) {
                // Too few memories
                addWarning(null, null, AT_LEAST_ONE_MEMORY_PER_CHANNEL + ": " + ch.getName(), TMLError.ERROR_STRUCTURE);
            } else if (n > 1) {
                // Too many memories
                String s = mapping.getStringOfMemoriesOfChannel(ch);
                addError(null, null, TOO_MANY_MEMORIES + ": " + ch.getName() + " mapped in " + s, TMLError.ERROR_STRUCTURE);
            }
        }
    }

    public void checkBusesOfChannels() {

        Iterator<TMLChannel> channelIt = tmlm.getChannels().iterator();
        while (channelIt.hasNext()) {
            TMLChannel ch = channelIt.next();
            TraceManager.addDev("Checking mapping of channel:" + ch.getName());
            int n = mapping.getNbOfBusesOfChannel(ch);
            if (n == 0) {
                // Too few buses
                addWarning(null, null, AT_LEAST_ONE_BUS_PER_CHANNEL + ": " + ch.getName(), TMLError.ERROR_STRUCTURE);
            }
        }
    }

    // For each hw element to which a path is mapped
    // We must check that both the reader and the writer
    // can access to that element without going through a CPU
    public void checkPathToMemory() {
        Iterator<TMLChannel> channelIt = tmlm.getChannels().iterator();
        while (channelIt.hasNext()) {
            TMLChannel ch = channelIt.next();
            checkPathToMemoryFromCPU(ch);
            checkPathToMemoryFromAllHwCommNode(ch);
        }
    }

    private void checkPathToMemoryFromCPU(TMLChannel ch) {
        //We must consider all channel sources and destination
        // We first select all involved tasks
        ArrayList<TMLTask> tasks = ch.getAllTasks();

        // Then we find the corresponding CPUs
        for (TMLTask task : tasks) {
            //We collect all the CPUs
            //TraceManager.addDev("Collecting all CPUs of task: " + task.getTaskName());
            for (HwExecutionNode origin : mapping.getAllHwExecutionNodesOfTask(task)) {
                // And then we check the paths between node and all the nodes of ch
                for (HwCommunicationNode destination : mapping.getAllCommunicationNodesOfChannel(ch)) {
                    //TraceManager.addDev("Computing path between " + origin.getName() + " and " + destination.getName());
                    if (!mapping.checkPath(origin, destination)) {
                        //TraceManager.addDev("Checking checkPathToMemoryFromCPU: Adding error");
                        addError(null, null, INVALID_CHANNEL_PATH + ": " + ch.getName(), TMLError.ERROR_STRUCTURE);
                        return;
                    }
                }

            }
        }
    }

    private void checkPathToMemoryFromAllHwCommNode(TMLChannel ch) {
        //TraceManager.addDev("Checking checkPathToMemoryFromAllHwCommNode");
        HwMemory mem = mapping.getMemoryOfChannel(ch);
        if (mem != null) {
            for (HwCommunicationNode origin : mapping.getAllCommunicationNodesOfChannel(ch)) {
                if (origin != mem) {
                    if (!mapping.checkPath(origin, mem)) {
                        TraceManager.addDev("Checking checkPathToMemoryFromAllHwCommNode: Adding error");
                        addError(null, null, INVALID_CHANNEL_PATH + ": " + ch.getName(), TMLError.ERROR_STRUCTURE);
                        return;
                    }
                }
            }
        }
    }


    private void checkPathValidity() {
        Iterator<TMLChannel> channelIt = tmlm.getChannels().iterator();
        while (channelIt.hasNext()) {
            TMLChannel ch = channelIt.next();
            checkPathValidityForChannel(ch);
        }
    }

    private void checkPathValidityForChannel(TMLChannel ch) {
        HwMemory mem = mapping.getMemoryOfChannel(ch);

        // we want to verify that if there is at least one bus/bridge which is used in the mapping
        // the path is complete. Otherwise, a warning is given for that channel mapping
        ArrayList<HwCommunicationNode> elts = mapping.getAllCommunicationNodesOfChannel(ch);

        if (elts.size() > 1) {
            // We construct the hardware paths of ch
            TMLChannelPath path = mapping.makePathOfChannel(ch);
            if (path == null) {
                addError(null, null, INVALID_BUS_PATH + ": " + ch.getName(), TMLError.ERROR_STRUCTURE);
            }
        }

    }


    private void checkNonDuplicatePathToBuses() {
        TraceManager.addDev("Checking duplicate links to buses");
        HashMap<HwBus, ArrayList<HwNode>> map = new HashMap<HwBus, ArrayList<HwNode>> ();

        ArrayList<HwNode> list;
        for (HwLink link: mapping.getTMLArchitecture().getHwLinks()) {
            list = map.get(link.bus);
            if (list == null) {
                ArrayList<HwNode> newList = new ArrayList<HwNode>();
                newList.add(link.hwnode);
                map.put(link.bus, newList);
            } else if (list.contains(link.hwnode)) {
                addErrorByReference(null, null, null, DUPLICATE_PATH_TO_BUS + ": from " + link.hwnode.getName() + " to "
                        + link.bus.getName(), TMLError.ERROR_STRUCTURE);
            } else {
                list.add(link.hwnode);
            }
        }
    }


    private void checkOneNOC() {
        //TraceManager.addDev("Checking NOC Nodes");
        int nb = mapping.getNbOfNoCs();

        if (nb > 1) {
            addError(null, null, ONLY_ONE_NOC, TMLError.ERROR_STRUCTURE);
            return;
        }

    }

    private void checkRouting() {
        DiploSimulatorCodeGenerator gen = new DiploSimulatorCodeGenerator(mapping);
        for(TMLChannel ch: mapping.getTMLModeling().getChannels()) {
            String s = gen.determineRouting(mapping.getHwNodeOf(ch.getOriginTask()),
                    mapping.getHwNodeOf(ch.getDestinationTask()), ch);
            if (s == null) {
                TraceManager.addDev(INVALID_ROUTING + ": " + ch.getName() + " between " + ch.getOriginTask().getName() + " and " +
                        ch.getDestinationTask().getName());
                addError(ch.getOriginTask(), null, INVALID_ROUTING + ": " + ch.getName() + " between " + ch.getOriginTask().getName() + " and " +
                        ch.getDestinationTask().getName(), TMLError.ERROR_STRUCTURE);
            }
        }
    }

    private void checkLinkBuses() {
        for(HwNode node: mapping.getArch().getHwNodes()) {
            if (!((node instanceof HwBus) || (node instanceof HwNoC))) { // Not a bus
                // It must have one link to a bus
                //TraceManager.addDev("Working with node=" + node.getName());
                boolean hasALink = false;
                for(HwLink link: mapping.getArch().getHwLinks()) {
                    if (link.hwnode == node) {
                        //TraceManager.addDev("Link found from " + node.getName() + " to bus " + link.bus.getName());
                        hasALink = true;
                        break;
                    }
                }
                if (!hasALink) {
                    addError(null, null, LINK_ISSUE + ": " + node.getName() + " has not link to a bus"  , TMLError.ERROR_STRUCTURE);
                }

            }
        }
    }

    public void checkAtLeastOneTask() {
        int nbOfTasks = mapping.getMappedTasks().size();

        TraceManager.addDev("Nb of tasks: " + nbOfTasks);

        if (nbOfTasks == 0) {
            TraceManager.addDev("Adding a warning: " + AT_LEAST_ONE_TASK);
            addWarning(null, null, AT_LEAST_ONE_TASK, TMLError.ERROR_STRUCTURE);
        }
    }

    public void checkOnlyOneReadAndWriteForChannelsWithAuthenticityCheck() {
        TraceManager.addDev("Checking for auth channels");
        for(TMLChannel ch: tmlm.getChannels()) {
            //TraceManager.addDev("Checking for auth channel " + ch.getName());
            if (ch.isCheckAuthChannel()) {
                TraceManager.addDev("Channel " + ch.getName() + " has checkAuth. Check for RD/WR");
                // We have to go through all tasks and rd / write operators
                int nbOfRead = 0;
                int nbOfWrite = 0;
                TMLTask foundTR = null, foundTW = null;
                TMLReadChannel rc = null;
                TMLWriteChannel wc = null;


                for(TMLTask t: tmlm.getTasks()) {
                    for(TMLElement elt: t.getActivityDiagram().getElements()) {
                        if (elt instanceof TMLActivityElementChannel) {
                            TMLActivityElementChannel aec = (TMLActivityElementChannel)elt;
                            if (aec.hasChannel(ch)) {
                                if (aec instanceof TMLReadChannel) {
                                    TraceManager.addDev("\t found read in Channel " + ch.getName() + " in task " + t.getName());
                                    nbOfRead ++;
                                    foundTR = t;
                                    rc = (TMLReadChannel)aec;
                                } else {
                                    TraceManager.addDev("\t found write in Channel " + ch.getName() + " in task " + t.getName());
                                    nbOfWrite ++;
                                    foundTW = t;
                                    wc = (TMLWriteChannel)aec;
                                }
                            }
                        }
                    }
                }

                if (nbOfWrite > 1) {
                    // W with different crypto configuration?
                    TraceManager.addDev("Adding warning because nbOfWrite > 1");
                    addWarning(foundTW, wc, DUPLICATE_WR_AUTHENTICITY + ". Channel: " + ch.getName(), TMLError.ERROR_STRUCTURE);
                }

                if (nbOfRead > 1) {
                    // Rwith different crypto configuration?
                    TraceManager.addDev("Adding warning because nbOfRead > 1");
                    addWarning(foundTR, rc, DUPLICATE_RD_AUTHENTICITY + ". Channel: " + ch.getName(), TMLError.ERROR_STRUCTURE);
                }
            }
        }

    }

    public void checkMappingOfSecurityPattern() {
        for(SecurityPattern sp: tmlm.secPatterns) {
            if (!(sp.isNonceType())) {
                List<HwMemory> mems = mapping.getMappedMemory(sp);
                if ((mems == null) || (mems.size() == 0)) {
                    addWarning(null, null, SECURITY_PATTERN_NOT_MAPPED + ": " + sp.getName(), TMLError.ERROR_STRUCTURE);
                }
            }

        }
    }



}
