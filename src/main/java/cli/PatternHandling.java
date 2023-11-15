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


package cli;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import myutil.FileUtils;
import tmltranslator.HwNode;
import tmltranslator.TMLChannel;
import tmltranslator.TMLEvent;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLRequest;
import tmltranslator.TMLSyntaxChecking;
import tmltranslator.TMLTask;
import tmltranslator.patternhandling.AttributeTaskJsonFile;
import tmltranslator.patternhandling.MappingPatternChannel;
import tmltranslator.patternhandling.MappingPatternTask;
import tmltranslator.patternhandling.PatternChannelWithSecurity;
import tmltranslator.patternhandling.PatternCloneTask;
import tmltranslator.patternhandling.PatternConfiguration;
import tmltranslator.patternhandling.PatternConnection;
import tmltranslator.patternhandling.PatternCreation;
import tmltranslator.patternhandling.PatternPortsConfig;
import tmltranslator.patternhandling.PortTaskJsonFile;
import tmltranslator.patternhandling.TaskPattern;
import tmltranslator.patternhandling.TaskPorts;
import ui.GTURTLEModeling;
import ui.TMLArchiPanel;
import ui.TURTLEPanel;

/**
 * Class PatternHandling
 * Creation: 23/10/2023
 * Version 1.0 23/10/2023
 *
 * @author Jawher JERRAY
 */

public class PatternHandling extends Command {
    private final static String SYNTAX_ERROR = "Syntax Error";
    private final static String NO_NAME_NO_TASK_FOR_PATTERN = "No name or no tasks is giving for the pattern";

    private final static String PATTERN_NOT_EXIST = "Pattern does not exist";
    private final static String NO_PATTERN_SELECTED = "No pattern selected";
    public final static String PATTERN_NOT_CONNECTED = "Some pattern ports are not connected yet";
    private final static String PORT_CONFIGURATION_NOT_FINISHED = "Some ports configuration are missing";
    private final static String CLONE_TASK_EXIST = "Cloned task already exists";
    private final static String TASK_TO_CLONE_NOT_EXIST = "Task to clone does not exist";
    private final static String TASK_NOT_EXIST = "Task does not exist";
    private final static String PATTERN_TASK_NOT_EXIST = "Pattern task does not exist or all its external ports are already connected";
    private final static String PATTERN_PORT_NOT_EXIST = "Pattern port does not exist or it's already connected";
    private final static String MODEL_TASK_NOT_EXIST = "Model task does not exist";
    private final static String MODEL_PORT_NOT_EXIST = "Model port does not exist or its already used";
    private final static String MODEL_PORT_CONFIG_NOT_EXIST = "Model port does not exist or it's already configured";
    private final static String MODEL_PORT_MERGE_WITH_NOT_EXIST = "Model port to merge with does not exist or can't be used";
    private final static String PORTS_TYPES_NOT_MATCH = "The selected port in pattern side does not match with the selected port in the model side";
    private final static String ATTRIBUTE_NOT_EXIST = "Attribute does not exist";
    private final static String VALUE_BAD = "Unreadable value";
    private final static String TASK_TO_MAP_NOT_EXIST = "Task to map does not exist or it's already mapped";
    private final static String TASK_IN_SAME_HW_MAP_NOT_EXIST = "Selected task in same HW does not exist or can't be used";
    private final static String CHANNEL_TO_MAP_NOT_EXIST = "Channel to map does not exist or it's already mapped";
    private final static String CHANNEL_IN_SAME_MEM_MAP_NOT_EXIST = "Selected channel in same memory does not exist or can't be used";
    private final static String BUS_NOT_EXIST = "Bus does not exist";
    private final static String CONFIG_JSON_FILE_NOT_EXIST = "Config json file does not exist";
    private final static String CONFIG_JSON_FILE_MISSING = "Path of the configuration json is missing or no pattern is selected";
    // PatternHandling commands
    private final static String CREATE = "create-pattern";
    private final static String CONFIGURE = "configure-pattern";
    private final static String INTEGRATE = "apply-pattern";
    private final static String DIPLO_LOAD_TMAP = "diplodocus-load-tmap";

    private TMLMapping<?> tmap;
    private String tabName = "";
    private String patternName, patternsPath = "../ttool/build/resources/main/patterns/";
    private HashSet<String> selectedTasks = new HashSet<String>();
    private HashSet<String> allTasksOfModel = new HashSet<String>();

    private Vector<String> listPatterns = new Vector<String>();
    private String selectedPatternPath, selectedPatternName;
    private PatternConfiguration patternConfiguration = new PatternConfiguration();
    LinkedHashMap<String, TaskPattern> patternTasksAll, patternTasksLeft;
    LinkedHashMap<String, TaskPorts> portsTaskOfModelAll;
    LinkedHashMap<String, TaskPorts> portsTaskModelLeft;
    List<PatternPortsConfig> portsLeftToConfig;
    List<MappingPatternTask> tasksLeftToMap;
    List<MappingPatternChannel> channelsLeftToMap;

    private String selectedPatternToIntergrate, selectedPatternPathToIntergrate, selectedJsonFilePath;

    public PatternHandling() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "pattern";
    }

    public String getShortCommand() {
        return "pa";
    }

    public String getUsage() {
        return "pattern <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to create, configure or integrate a pattern into a DIPLODOCUS model.";
    }

    public void fillSubCommands() {

        // Create Pattern
        Command createPattern = new Command() {
            final String [] options = {"-n", "-p","-a", "-l", "-t", "-r", "-m"};
            public String getCommand() {
                return CREATE;
            }

            public String getShortCommand() {
                return "c";
            }

            public String getDescription() {
                return "Create a pattern from a model by giving a name to the new pattern and selecting its tasks";
            }

            public String getUsage() {
                return "[OPTION]... [NAME] [PATH] [TASKS]\n"
                + "-n NAME\tset a name for the pattern to create\n"
                + "-p PATH\tset a path for the files that will be generated for the pattern\n"
                + "-a TASKS\tselect tasks to add to the pattern (seperated by a space)\n"
                + "-l \tget the list of selected tasks to be added to the pattern\n"
                + "-t \tget all tasks of the model\n"
                + "-r TASKS\tremove selected tasks\n"
                + "-m \tmake the pattern\n";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                /*if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }*/
                
                if (tmap == null) {
                    if (interpreter.mgui != null) {
                        TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                        if (!(tp instanceof TMLArchiPanel)) {
                            return "Current panel does not contain TMAP (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +" or move to Arch panel.)";
                        }
                        if(!interpreter.mgui.checkModelingSyntax(tp, true)) {
                            return SYNTAX_ERROR + "in current panel (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +")";
                        }
                        tmap = interpreter.mgui.gtm.getTMLMapping();
                        nullifyAll();
                        if (tmap == null) {
                            return Interpreter.TMAP_NO_SPEC + "\nLoad a textual TMAP Spec using " + DIPLO_LOAD_TMAP + " command or move to Arch panel.";
                        } else {
                            for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                                String[] taskNameSplit = task.getName().split("__");
                                allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                                if (taskNameSplit.length > 1) {
                                    tabName = taskNameSplit[0];
                                } 
                                task.setName(taskNameSplit[taskNameSplit.length-1]);
                            }
                            for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
                                String[] chNameSplit = ch.getName().split("__");
                                ch.setName(chNameSplit[chNameSplit.length-1]);
                            }
                            for (TMLEvent evt : tmap.getTMLModeling().getEvents()) {
                                String[] evtNameSplit = evt.getName().split("__");
                                evt.setName(evtNameSplit[evtNameSplit.length-1]);
                            }
                            for (TMLRequest req : tmap.getTMLModeling().getRequests()) {
                                String[] reqNameSplit = req.getName().split("__");
                                req.setName(reqNameSplit[reqNameSplit.length-1]);
                            }
                        }
                    } else {
                        return Interpreter.TTOOL_NOT_STARTED;
                    }
                }

                String[] commands = command.split(" ");
                for (int i = 0; i < commands.length; i++) {
                    List<String> argumentsOfOption = new ArrayList<String>();
                    
                    switch (commands[i]) {
                        case "-n":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            patternName = argumentsOfOption.get(0);
                            break;
                        case "-p":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            patternsPath = argumentsOfOption.get(0);
                            break;
                        case "-a":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            for (String argumentOfOption : argumentsOfOption) {
                                if (allTasksOfModel.contains(argumentOfOption)) {
                                    selectedTasks.add(argumentOfOption);
                                } else {
                                    interpreter.print("The task " + argumentOfOption + " does not exist, it will not be added.");
                                }
                            }
                            break;
                        case "-l":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            interpreter.print("The selected tasks are :");
                            String printSelectedTasks = "";
                            for (int ind = 0; ind < selectedTasks.size(); ind++) {
                                if (ind == 0) {
                                    printSelectedTasks = (String) selectedTasks.toArray()[ind];
                                } else {
                                    printSelectedTasks = printSelectedTasks + "\t" + (String) selectedTasks.toArray()[ind];
                                }
                            }
                            interpreter.print(printSelectedTasks);
                            break;
                        case "-t":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            
                            interpreter.print("This model contains " + allTasksOfModel.size() + " task(s) :");
                            String printTasks = "";
                            for (int ind = 0; ind < allTasksOfModel.size(); ind++) {
                                if (ind == 0) {
                                    printTasks = (String) allTasksOfModel.toArray()[ind];
                                } else {
                                    printTasks = printTasks + "\t" + (String) allTasksOfModel.toArray()[ind];
                                }
                            }
                            interpreter.print(printTasks);
                            break;
                        case "-r":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            for (String argumentOfOption : argumentsOfOption) {
                                if (allTasksOfModel.contains(argumentOfOption)) {
                                    selectedTasks.remove(argumentOfOption);
                                } else {
                                    interpreter.print("The task " + argumentOfOption + " does not exist, it will not be removed.");
                                }
                            }
                            break;
                        case "-m":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (patternName != null && !patternName.equals("") && selectedTasks.size() != 0) {
                                GTURTLEModeling.createPatternFromTmap(new ArrayList<String>(selectedTasks), patternName, patternsPath, tmap);
                                selectedTasks.clear();
                                patternName = null;
                            } else {
                                return NO_NAME_NO_TASK_FOR_PATTERN;
                            }
                            
                            break;
                        default:
                            return Interpreter.BAD;
                    }
                }
                return null;
            }
        };

        // Create Pattern
        Command configurePattern = new Command() {
            final String [] options = {"-n", "-p", "-pal", "-sp", "-ct", "-ctr", "-co", "-cor", "-col", "-plnc", "-pl", "-t", "-b", "-cptl", "-cpd", "-cpm", "-cpml", "-cpr", "-cpl", "-tctl", "-tcm", "-tcml", "-tcn", "-tcr", "-tcl", "-mctl", "-mcm", "-mcml", "-mcn", "-mcr", "-mcl", "-ua", "-m"};
            public String getCommand() {
                return CONFIGURE;
            }

            public String getShortCommand() {
                return "sc";
            }

            public String getDescription() {
                return "Select a pattern and Configure how it will be integrated into a given model";
            }

            public String getUsage() {
                return "[OPTION]... [NAME] [TASK]\n"
                + "-n NAME\tselect a pattern by its name\n"
                + "-p PATH\tselect a pattern by the path of its folder\n"
                + "-pal \tget the list of all the created patterns\n"
                + "-sp \tget selected pattern\n"
                + "-ct TASK1 TASK2\tclone TASK1 from TASK2\n"
                + "-ctr TASK\t remove the clone task TASK\n"
                + "-ctl TASK\t get the list of cloned tasks\n"
                + "-co TASK1.PORT1 TASK2.PORT2 [NEW] [C] [WA] [SA]\tconnect PORT1 of TASK1 (pattern side) with PORT2 of TASK2 (model side). Other possible options:\n\t*Set NEW to specify that this is a new port that needs to be created.\n\t*Set C to ensure confidentiality for this channel\n\t*Set WA to ensure weak authenticity for this channel\n\t*Set SA to ensure strong authenticity for this channel\n"
                + "-cor TASK.PORT\tremove connections that have TASK.PORT in the pattern side.\n"
                + "-col \tget the list of connections already made\n"
                + "-plnc \tget the list of pattern's ports that are not yet connected\n"
                + "-pl TASK.PORT \tget list of available ports in model that could be used to connect with PORT (pattern side)\n"
                + "-t \tget all tasks of the model\n"
                + "-b \tget all buses of the model\n"
                + "-cptl \tget list of ports to be configured\n"
                + "-cpd [TASK.PORT] [ALL]\tchoose removing PORT as decision (or ALL to remove all the ports)\n"
                + "-cpm TASK.PORT1 PORT2\tchoose merging PORT1 with PORT2 as decision\n"
                + "-cpml TASK.PORT\tget list of ports that can be merge with TASK.PORT\n"
                + "-cpr TASK.PORT\tremove the configuration decision for PORT\n"
                + "-cpl \tget list of configured ports\n"
                + "-tctl \tget list of tasks to be mapped \n"
                + "-tcm TASK1 TASK2\tmap TASK1 in same CPU as TASK2\n"
                + "-tcml\tmap get list of tasks that can be used to map in same CPU\n"
                + "-tcn TASK BUS\tmap TASK in new CPU linked to bus BUS\n"
                + "-tcr TASK\tremove the mapping of TASK\n"
                + "-tcl \tget list of mapped tasks\n"
                + "-mctl \tmap get list of channels to be mapped\n"
                + "-mcm TASK1.CHANNEL1 TASK2.CHANNEL2\tmap CHANNEL1 in same memory and buses as CHANNEL2\n"
                + "-mcml\tmap get list of channels that can be used to map in same Memory\n"
                + "-mcn TASK.CHANNEL BUS\tmap CHANNEL in new memory linked to bus BUS\n"
                + "-mcr TASK.CHANNEL\tremove the mapping of CHANNEL\n"
                + "-mcl \tmap get list of mapped channels\n"
                + "-ua TASK ATTRIBUTE VALUE \tput the value VALUE of attribute ATTRIBUTE of the task TASK\n"
                + "-m [PATH]\tmake the json configuration file of the pattern, you can optionally specify the path of this new file\n";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                /*if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }*/
                
                if (tmap == null) {
                    if (interpreter.mgui != null) {
                        TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                        if (!(tp instanceof TMLArchiPanel)) {
                            return "Current panel does not contain TMAP (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +" or move to Arch panel.)";
                        }
                        if(!interpreter.mgui.checkModelingSyntax(tp, true)) {
                            return SYNTAX_ERROR + "in current panel (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +")";
                        }
                        tmap = interpreter.mgui.gtm.getTMLMapping();
                        nullifyAll();
                        if (tmap == null) {
                            return Interpreter.TMAP_NO_SPEC + "\nLoad a textual TMAP Spec using " + DIPLO_LOAD_TMAP + " command or move to Arch panel.";
                        } else {
                            for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                                String[] taskNameSplit = task.getName().split("__");
                                allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                                if (taskNameSplit.length > 1) {
                                    tabName = taskNameSplit[0];
                                } 
                                task.setName(taskNameSplit[taskNameSplit.length-1]);
                            }
                            for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
                                String[] chNameSplit = ch.getName().split("__");
                                ch.setName(chNameSplit[chNameSplit.length-1]);
                            }
                            for (TMLEvent evt : tmap.getTMLModeling().getEvents()) {
                                String[] evtNameSplit = evt.getName().split("__");
                                evt.setName(evtNameSplit[evtNameSplit.length-1]);
                            }
                            for (TMLRequest req : tmap.getTMLModeling().getRequests()) {
                                String[] reqNameSplit = req.getName().split("__");
                                req.setName(reqNameSplit[reqNameSplit.length-1]);
                            }
                        }
                    } else {
                        return Interpreter.TTOOL_NOT_STARTED;
                    }
                }

                String[] commands = command.split(" ");
                for (int i = 0; i < commands.length; i++) {
                    List<String> argumentsOfOption = new ArrayList<String>();
                    
                    switch (commands[i]) {
                        case "-n":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            listPatterns = getFoldersName(patternsPath);
                            if (listPatterns.contains(argumentsOfOption.get(0))) {
                                selectedPatternName = argumentsOfOption.get(0);
                                selectedPatternPath = patternsPath + selectedPatternName;
                                patternTasksAll = TaskPattern.parsePatternJsonFile(selectedPatternPath, selectedPatternName+".json");
                                portsTaskOfModelAll = TaskPorts.getListPortsTask(tmap.getTMLModeling());
                                patternConfiguration = new PatternConfiguration();
                                patternTasksLeft = null;
                                portsTaskModelLeft = null;
                                portsLeftToConfig = null;
                                tasksLeftToMap = null;
                                channelsLeftToMap = null;
                            } else {
                                //selectedPatternName = null;
                                return PATTERN_NOT_EXIST;
                            }
                            break;
                        case "-p":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            File folderPattern = new File(argumentsOfOption.get(0));
                            if (folderPattern.isDirectory()) {
                                selectedPatternName = argumentsOfOption.get(0).split("/")[argumentsOfOption.get(0).split("/").length-1];
                                selectedPatternName = selectedPatternName.split("\\.")[0];
                                selectedPatternPath =  argumentsOfOption.get(0);
                                if (!selectedPatternName.endsWith("/")) {
                                    selectedPatternPath += "/";
                                }
                                patternTasksAll = TaskPattern.parsePatternJsonFile(selectedPatternPath, selectedPatternName+".json");
                                portsTaskOfModelAll = TaskPorts.getListPortsTask(tmap.getTMLModeling());
                                patternConfiguration = new PatternConfiguration();
                                patternTasksLeft = null;
                                portsTaskModelLeft = null;
                                portsLeftToConfig = null;
                                tasksLeftToMap = null;
                                channelsLeftToMap = null;
                            } else {
                                //selectedPatternName = null;
                                return PATTERN_NOT_EXIST;
                            }
                            break;
                        case "-pal":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            listPatterns = getFoldersName(patternsPath);
                            interpreter.print("There is " + listPatterns.size() + " pattern(s):");
                            int cont = 1;
                            for (String pattern : listPatterns) {
                                interpreter.print(cont + ". " + pattern);
                                cont++;
                            }
                            break;
                        case "-sp":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName != null) {
                                interpreter.print("The selected pattern is " + selectedPatternName);
                            } else {
                                //selectedPatternName = null;
                                return NO_PATTERN_SELECTED;
                            }
                            break;
                        case "-ct":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            String clonedTask = argumentsOfOption.get(0);
                            String selectedTaskToClone = argumentsOfOption.get(1);
                            if (allTasksOfModel.contains(clonedTask)) {
                                return CLONE_TASK_EXIST;
                            }

                            if (PatternCloneTask.isAClonedTask(patternConfiguration.getClonedTasks(), clonedTask) != null) {
                                return CLONE_TASK_EXIST;
                            }
                            
                            if (!allTasksOfModel.contains(selectedTaskToClone)) {
                                return TASK_TO_CLONE_NOT_EXIST;
                            }
                            
                            PatternCloneTask patterClone = new PatternCloneTask(clonedTask, selectedTaskToClone);
                            patternConfiguration.addClonedTasks(patterClone);
                            portsTaskOfModelAll.put(clonedTask, TaskPorts.cloneTaskPort(portsTaskOfModelAll.get(selectedTaskToClone)));
                            
                            break;
                        case "-ctr":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            for (String argumentOfOption : argumentsOfOption) {
                                PatternCloneTask patternCloneToRemove = PatternCloneTask.isAClonedTask(patternConfiguration.getClonedTasks(), argumentOfOption);
                                
                                if (patternCloneToRemove != null) {
                                    patternConfiguration.removeClonedTask(patternCloneToRemove);
                                    //portsTaskOfModelLeft.remove(patternCloneToRemove.getClonedTask());
                                    portsTaskOfModelAll.remove(patternCloneToRemove.getClonedTask());
                                    List<PatternConnection> patternConnectionsToRemove = new ArrayList<PatternConnection>();
                                    for (PatternConnection patternConnection: patternConfiguration.getPortsConnection()) {
                                        if (patternConnection.getModelTaskName().equals(patternCloneToRemove.getClonedTask())) {
                                            patternConnectionsToRemove.add(patternConnection);
                                        }
                                    }
                                    patternConfiguration.getPortsConnection().removeAll(patternConnectionsToRemove);
                                    if (patternConnectionsToRemove != null) {
                                        patternConfiguration.getTasksMapping().clear();
                                        patternConfiguration.getChannelsMapping().clear();
                                        patternConfiguration.getPortsConfig().clear();
                                    }
                                } else {
                                    interpreter.print("The cloned task " + argumentOfOption + "does not exist, it will not be removed.");
                                }
                            }
                            break;
                        case "-ctl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            interpreter.print("The cloned tasks are:");
                            for (PatternCloneTask cloneTask : patternConfiguration.getClonedTasks()) {
                                interpreter.print(cloneTask.getStringDisplay());
                            }
                            break;
                        case "-co":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() < 2 || argumentsOfOption.size() > 5) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            String patternTask = null, patternPort = null, modelTask = null, modelPort =null;
                            Boolean isNewPort = false;
                            Boolean isConf = false;
                            int auth = -1;
                            for (int ind = 0; ind < argumentsOfOption.size(); ind++) {
                                if (ind == 0) {
                                    String[] taskPort = argumentsOfOption.get(ind).split("\\.");
                                    if (taskPort.length == 2) {
                                        patternTask = taskPort[0];
                                        patternPort = taskPort[1];
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                } else if (ind == 1) {
                                    String[] taskPort = argumentsOfOption.get(ind).split("\\.");
                                    if (taskPort.length == 2) {
                                        modelTask = taskPort[0];
                                        modelPort = taskPort[1];
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                } else {
                                    if (argumentsOfOption.get(ind).equals("NEW")) {
                                        isNewPort = true;
                                    } else if (argumentsOfOption.get(ind).equals("C")) {
                                        isConf = true;
                                    } else if (argumentsOfOption.get(ind).equals("WA")) {
                                        auth = PatternChannelWithSecurity.WEAK_AUTHENTICITY;
                                    } else if (argumentsOfOption.get(ind).equals("SA")) {
                                        auth = PatternChannelWithSecurity.STRONG_AUTHENTICITY;
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                }
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                            PortTaskJsonFile portPattern;
                            if (patternTasksLeft.containsKey(patternTask)) {
                                portPattern = patternTasksLeft.get(patternTask).getExternalPortByName(patternPort);
                            } else {
                                return PATTERN_TASK_NOT_EXIST;
                            }
                            if (portPattern == null) {
                                return PATTERN_PORT_NOT_EXIST;
                            }
                            int portModelType;
                            if (portsTaskModelLeft.containsKey(modelTask)) {
                                portModelType = portsTaskModelLeft.get(modelTask).getPortTypeByName(modelPort);
                            } else {
                                return MODEL_TASK_NOT_EXIST;
                            }
                            if (portModelType == 0 && !isNewPort) {
                                return MODEL_PORT_NOT_EXIST;
                            }
                            boolean samePortType = false;
                            if (!isNewPort) {
                                if (portPattern.getType().equals(PatternCreation.CHANNEL) && portPattern.getMode().equals(PatternCreation.MODE_INPUT) && portModelType == TaskPorts.WRITE_CHANNEL) {
                                    samePortType = true;
                                } else if (portPattern.getType().equals(PatternCreation.CHANNEL) && portPattern.getMode().equals(PatternCreation.MODE_OUTPUT) && portModelType == TaskPorts.READ_CHANNEL) {
                                    samePortType = true;
                                } else if (portPattern.getType().equals(PatternCreation.EVENT) && portPattern.getMode().equals(PatternCreation.MODE_INPUT) && portModelType == TaskPorts.SEND_EVENT) {
                                    samePortType = true;
                                } else if (portPattern.getType().equals(PatternCreation.EVENT) && portPattern.getMode().equals(PatternCreation.MODE_OUTPUT) && portModelType == TaskPorts.WAIT_EVENT) {
                                    samePortType = true;
                                } else {
                                    return PORTS_TYPES_NOT_MATCH;
                                }
                            }
                            
                            if (samePortType || isNewPort) {
                                if (isConf) {
                                    portPattern.setConfidentiality(PatternCreation.WITH_CONFIDENTIALITY);
                                }
                                if (auth == PatternChannelWithSecurity.NO_AUTHENTICITY) {
                                    portPattern.setAuthenticity(PatternCreation.WITHOUT_AUTHENTICITY);
                                } else if (auth == PatternChannelWithSecurity.WEAK_AUTHENTICITY) {
                                    portPattern.setAuthenticity(PatternCreation.WEAK_AUTHENTICITY);
                                } else if (auth == PatternChannelWithSecurity.STRONG_AUTHENTICITY) {
                                    portPattern.setAuthenticity(PatternCreation.STRONG_AUTHENTICITY);
                                }
                                PatternConnection patternConnection = new PatternConnection(patternTask, patternPort, modelTask, modelPort, isNewPort);
                                patternConfiguration.addPortsConnection(patternConnection);
                            }
                            break;
                        case "-plnc":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            LinkedHashMap<String, TaskPattern> patternTasksLeftCur = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            interpreter.print("The pattern's port that are still not connected:");
                            for (String taskName : patternTasksLeftCur.keySet()) {
                                for (PortTaskJsonFile portTask : patternTasksLeftCur.get(taskName).getExternalPorts()) {
                                    interpreter.print(taskName + "." + portTask.getName());
                                }
                            }
                            break;
                        case "-cor":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            List<PatternConnection> patternConnectionsToRemove = new ArrayList<PatternConnection>();
                            for (String argumentOfOption : argumentsOfOption) {
                                String patternTaskToRemove = null, patternPortToRemove = null;
                                String[] taskPortToRemove = argumentOfOption.split("\\.");
                                if (taskPortToRemove.length == 2) {
                                    patternTaskToRemove = taskPortToRemove[0];
                                    patternPortToRemove = taskPortToRemove[1];
                                } else {
                                    return Interpreter.BAD;
                                }
                                boolean taskPortExist = false;
                                for (PatternConnection patternConnection : patternConfiguration.getPortsConnection()) {
                                    if (patternConnection.getPatternTaskName().equals(patternTaskToRemove) && patternConnection.getPatternChannel().equals(patternPortToRemove)) {
                                        patternConnectionsToRemove.add(patternConnection);
                                        taskPortExist = true;
                                        break;
                                    }
                                }
                                if (!taskPortExist) {
                                    interpreter.print(argumentOfOption + " does not exist, it will not be removed.");
                                }
                            }
                            patternConfiguration.getPortsConnection().removeAll(patternConnectionsToRemove);
                            if (patternConnectionsToRemove != null) {
                                patternConfiguration.getTasksMapping().clear();
                                patternConfiguration.getChannelsMapping().clear();
                                patternConfiguration.getPortsConfig().clear();
                            }
                            break;
                        case "-col":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            interpreter.print("The connections made are:");
                            for (PatternConnection patternConnection : patternConfiguration.getPortsConnection()) {
                                interpreter.print(patternConnection.getStringDisplay());
                            }
                            break;
                        case "-pl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            String patternTaskSelected = null, patternPortSelected = null;
                            String[] taskPort = argumentsOfOption.get(0).split("\\.");
                            if (taskPort.length == 2) {
                                patternTaskSelected = taskPort[0];
                                patternPortSelected = taskPort[1];
                            } else {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                            PortTaskJsonFile portPatternSelected;
                            if (patternTasksLeft.containsKey(patternTaskSelected)) {
                                portPatternSelected = patternTasksLeft.get(patternTaskSelected).getExternalPortByName(patternPortSelected);
                            } else {
                                return PATTERN_TASK_NOT_EXIST;
                            }
                            if (portPatternSelected == null) {
                                return PATTERN_PORT_NOT_EXIST;
                            }
                            interpreter.print("The available ports in model that could be used to connect with this port are :");
                            for (String taskName : portsTaskModelLeft.keySet()) {
                                if (portPatternSelected.getType().equals(PatternCreation.CHANNEL) && portPatternSelected.getMode().equals(PatternCreation.MODE_INPUT)) {
                                    for (String wr : portsTaskModelLeft.get(taskName).getWriteChannels()) {
                                        interpreter.print(taskName + "." + wr);
                                    }
                                } else if (portPatternSelected.getType().equals(PatternCreation.CHANNEL) && portPatternSelected.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                                    for (String wr : portsTaskModelLeft.get(taskName).getReadChannels()) {
                                        interpreter.print(taskName + "." + wr);
                                    }
                                } else if (portPatternSelected.getType().equals(PatternCreation.EVENT) && portPatternSelected.getMode().equals(PatternCreation.MODE_INPUT)) {
                                    for (String wr : portsTaskModelLeft.get(taskName).getSendEvents()) {
                                        interpreter.print(taskName + "." + wr);
                                    }
                                } else if (portPatternSelected.getType().equals(PatternCreation.EVENT) && portPatternSelected.getMode().equals(PatternCreation.MODE_OUTPUT)) {
                                    for (String wr : portsTaskModelLeft.get(taskName).getWaitEvents()) {
                                        interpreter.print(taskName + "." + wr);
                                    }
                                }
                            }
                            break;
                        case "-t":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            
                            interpreter.print("This model contains " + allTasksOfModel.size() + " task(s) :");
                            String printTasks = "";
                            for (int ind = 0; ind < allTasksOfModel.size(); ind++) {
                                if (ind == 0) {
                                    printTasks = (String) allTasksOfModel.toArray()[ind];
                                } else {
                                    printTasks = printTasks + "\t" + (String) allTasksOfModel.toArray()[ind];
                                }
                            }
                            interpreter.print(printTasks);
                            break;
                        case "-b":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            List<String> listBus = new ArrayList<String>();
                            for (HwNode bus : tmap.getArch().getBUSs()) {
                                listBus.add(bus.getName());
                            }
                            interpreter.print("This model contains " + listBus.size() + " bus(s) :");
                            String printBuses = "";
                            for (int ind = 0; ind < listBus.size(); ind++) {
                                if (ind == 0) {
                                    printBuses = listBus.get(ind);
                                } else {
                                    printBuses = printBuses + "\t" + listBus.get(ind);
                                }
                            }
                            interpreter.print(printBuses);
                            break;
                        case "-cptl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }

                            portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                            portsLeftToConfig = PatternPortsConfig.getPortsLeftToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), patternConfiguration.getPortsConfig(), tmap.getTMLModeling());
                            interpreter.print("The ports that need to be configured are :");
                            for (PatternPortsConfig portToConfig : portsLeftToConfig) {
                                interpreter.print(portToConfig.getTaskOfChannelToConfig() + "." + portToConfig.getChannelToConfig());
                            }
                            
                            break;
                        case "-cpd":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            for (String argumentOfOption : argumentsOfOption) {
                                if (argumentOfOption.equals("ALL")) {
                                    portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                                    portsLeftToConfig = PatternPortsConfig.getPortsLeftToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), patternConfiguration.getPortsConfig(), tmap.getTMLModeling());
                                    for (PatternPortsConfig portConfig : portsLeftToConfig) {
                                        portConfig.setIsChannelToRemove(true);
                                        patternConfiguration.getPortsConfig().add(portConfig);
                                    }
                                } else {
                                    String modelTaskToConfig = null, modelPortToConfig = null;
                                    String[] taskPortToConfig = argumentOfOption.split("\\.");
                                    if (taskPortToConfig.length == 2) {
                                        modelTaskToConfig = taskPortToConfig[0];
                                        modelPortToConfig = taskPortToConfig[1];
                                        portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                                        portsLeftToConfig = PatternPortsConfig.getPortsLeftToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), patternConfiguration.getPortsConfig(), tmap.getTMLModeling());
                                        boolean isConfigured = false;
                                        for (PatternPortsConfig portConfig : portsLeftToConfig) {
                                            if (portConfig.getTaskOfChannelToConfig().equals(modelTaskToConfig) && portConfig.getChannelToConfig().equals(modelPortToConfig)) {
                                                portConfig.setIsChannelToRemove(true);
                                                patternConfiguration.getPortsConfig().add(portConfig);
                                                isConfigured = true;
                                                break;
                                            }
                                        }
                                        if (!isConfigured) {
                                            return MODEL_PORT_CONFIG_NOT_EXIST;
                                        }
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                }
                            }
                            break;
                        case "-cpm":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            PatternPortsConfig portToConfig = null;
                            String modelTaskToConfig = null, modelPortToConfig = null;
                            for (int ind = 0; ind < argumentsOfOption.size(); ind++) {
                                if (ind == 0) {
                                    String[] taskPortToConfig = argumentsOfOption.get(ind).split("\\.");
                                    if (taskPortToConfig.length == 2) {
                                        modelTaskToConfig = taskPortToConfig[0];
                                        modelPortToConfig = taskPortToConfig[1];
                                        portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                                        portsLeftToConfig = PatternPortsConfig.getPortsLeftToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), patternConfiguration.getPortsConfig(), tmap.getTMLModeling());
                                        boolean isConfigured = false;
                                        for (PatternPortsConfig portConfig : portsLeftToConfig) {
                                            if (portConfig.getTaskOfChannelToConfig().equals(modelTaskToConfig) && portConfig.getChannelToConfig().equals(modelPortToConfig)) {
                                                portToConfig = portConfig;
                                                isConfigured = true;
                                                break;
                                            }
                                        }
                                        if (!isConfigured) {
                                            return MODEL_PORT_CONFIG_NOT_EXIST;
                                        }
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                } else if (ind == 1) {
                                    if (portToConfig != null) {
                                        List<PatternPortsConfig> portsToConfig = PatternPortsConfig.getPortsToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), tmap.getTMLModeling());
                                        List<String> portsCanBeMergedWith = PatternPortsConfig.getPortsCanBeMergedWith(portsTaskOfModelAll, portsToConfig, modelTaskToConfig);
                                        if (portsCanBeMergedWith.contains(argumentsOfOption.get(ind))) {
                                            portToConfig.setMergeWith(argumentsOfOption.get(ind));
                                            patternConfiguration.getPortsConfig().add(portToConfig);
                                        } else {
                                            return MODEL_PORT_MERGE_WITH_NOT_EXIST;
                                        }
                                    }
                                }
                            }
                            break;
                        case "-cpml":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            String[] taskPortToConfig = argumentsOfOption.get(0).split("\\.");
                            if (taskPortToConfig.length == 2) {
                                if (!portsTaskOfModelAll.containsKey(taskPortToConfig[0])) {
                                    return TASK_NOT_EXIST;
                                }
                                portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                                List<PatternPortsConfig> portsToConfig = PatternPortsConfig.getPortsToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), tmap.getTMLModeling());
                                List<String> portsCanBeMergedWith = PatternPortsConfig.getPortsCanBeMergedWith(portsTaskOfModelAll, portsToConfig, taskPortToConfig[0]);
                                interpreter.print("List of ports that can be merge with " + taskPortToConfig[0] +"." + taskPortToConfig[1] +":");
                                for (String portCanBeMergedWith : portsCanBeMergedWith) {
                                    interpreter.print(portCanBeMergedWith);
                                }
                            } else {
                                return Interpreter.BAD;
                            }
                            break;
                        case "-cpr":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            List<PatternPortsConfig> patternPortsConfigToRemove = new ArrayList<PatternPortsConfig>();
                            for (int ind = 0; ind < argumentsOfOption.size(); ind++) {
                                String[] taskPortToRemove = argumentsOfOption.get(ind).split("\\.");
                                if (taskPortToRemove.length == 2) {
                                    boolean isPortConfigToRemoveExist = false;
                                    for (PatternPortsConfig patternPortsConfig : patternConfiguration.getPortsConfig()) {
                                        if (patternPortsConfig.getTaskOfChannelToConfig().equals(taskPortToRemove[0]) && patternPortsConfig.getChannelToConfig().equals(taskPortToRemove[1])) {
                                            patternPortsConfigToRemove.add(patternPortsConfig);
                                            isPortConfigToRemoveExist = true;
                                            break;
                                        }
                                    }
                                    if (!isPortConfigToRemoveExist) {
                                        interpreter.print(argumentsOfOption.get(ind) + " does not exist, it will not be removed.");
                                    }
                                } else {
                                    interpreter.print(argumentsOfOption.get(ind) + ": " + Interpreter.BAD);
                                }
                            }
                            patternConfiguration.getPortsConfig().removeAll(patternPortsConfigToRemove);
                            break;
                        case "-cpl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            interpreter.print("List of configured ports:");
                            for (PatternPortsConfig patternPortsConfig : patternConfiguration.getPortsConfig()) {
                                interpreter.print(patternPortsConfig.getStringDisplay());
                            }
                            break;
                        case "-tctl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            tasksLeftToMap = MappingPatternTask.getTasksLeftToMap(patternConfiguration.getTasksMapping(), MappingPatternTask.getTasksToMap(patternTasksAll, patternConfiguration.getClonedTasksName()));
                            interpreter.print("The tasks that need to be mapped are :");
                            for (MappingPatternTask taskMap : tasksLeftToMap) {
                                interpreter.print(taskMap.getTaskToMapName());
                            }
                            break;
                        case "-tcm":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            List<MappingPatternTask> allTasksToMap = MappingPatternTask.getTasksToMap(patternTasksAll, patternConfiguration.getClonedTasksName());
                            tasksLeftToMap = MappingPatternTask.getTasksLeftToMap(patternConfiguration.getTasksMapping(), allTasksToMap);
                            boolean taskToMapExist = false;
                            MappingPatternTask taskToMap = null; 
                            for (MappingPatternTask taskMap : tasksLeftToMap) {
                                if (taskMap.getTaskToMapName().equals(argumentsOfOption.get(0))) {
                                    taskToMap = taskMap;
                                    taskToMapExist = true;
                                    break;
                                }
                            }
                            if (!taskToMapExist) {
                                return TASK_TO_MAP_NOT_EXIST;
                            }
                            boolean taskSameHwExist = false;
                            int originSameTask = 0;
                            if (allTasksOfModel.contains(argumentsOfOption.get(1))) {
                                taskSameHwExist = true;
                                originSameTask = MappingPatternTask.ORIGIN_MODEL;
                            }
                            if (!taskSameHwExist) {
                                for (MappingPatternTask taskMap : patternConfiguration.getTasksMapping()) {
                                    if (taskMap.getTaskToMapName().equals(argumentsOfOption.get(1))) {
                                        taskSameHwExist = true;
                                        originSameTask = taskMap.getOrigin();
                                        break;
                                    }
                                }
                            }
                            if (!taskSameHwExist) {
                                return TASK_IN_SAME_HW_MAP_NOT_EXIST;
                            }
                            if (taskSameHwExist && taskToMapExist) {
                                taskToMap.setSameHwAs(argumentsOfOption.get(1), originSameTask);
                                patternConfiguration.getTasksMapping().add(taskToMap);
                            }
                            break;
                        case "-tcml":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            interpreter.print("The list of tasks that can be used to map in same CPU:");
                            for (String taskMap : allTasksOfModel) {
                                interpreter.print(taskMap);
                            }
                            for (MappingPatternTask taskMap : patternConfiguration.getTasksMapping()) {
                                interpreter.print(taskMap.getTaskToMapName());
                            }
                            break;
                        case "-tcn":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            tasksLeftToMap = MappingPatternTask.getTasksLeftToMap(patternConfiguration.getTasksMapping(), MappingPatternTask.getTasksToMap(patternTasksAll, patternConfiguration.getClonedTasksName()));
                            boolean taskToMapInNewCPUExist = false;
                            MappingPatternTask taskToMapInNewCPU = null; 
                            for (MappingPatternTask taskMap : tasksLeftToMap) {
                                if (taskMap.getTaskToMapName().equals(argumentsOfOption.get(0))) {
                                    taskToMapInNewCPU = taskMap;
                                    taskToMapInNewCPUExist = true;
                                    break;
                                }
                            }
                            if (!taskToMapInNewCPUExist) {
                                return TASK_TO_MAP_NOT_EXIST;
                            }
                            boolean busNewCPUExist = false;
                            for (HwNode bus : tmap.getArch().getBUSs()) {
                                if (bus.getName().equals(argumentsOfOption.get(1))) {
                                    busNewCPUExist = true;
                                    break;
                                }
                            }
                            if (!busNewCPUExist) {
                                return BUS_NOT_EXIST;
                            }
                            if (taskToMapInNewCPUExist && busNewCPUExist) {
                                taskToMapInNewCPU.setBusNameForNewHw(argumentsOfOption.get(1));
                                patternConfiguration.getTasksMapping().add(taskToMapInNewCPU);
                            }
                            break;
                        case "-tcr":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            List<MappingPatternTask> mappingTasksToRemove = new ArrayList<MappingPatternTask>();
                            boolean isMappingTaskToRemoveExist = false;
                            for (int ind = 0; ind < argumentsOfOption.size(); ind++) {
                                for (MappingPatternTask mappingTask : patternConfiguration.getTasksMapping()) {
                                    if (mappingTask.getTaskToMapName().equals(argumentsOfOption.get(ind))) {
                                        mappingTasksToRemove.add(mappingTask);
                                        isMappingTaskToRemoveExist = true;
                                        break;
                                    }
                                }
                                if (!isMappingTaskToRemoveExist) {
                                    interpreter.print(argumentsOfOption.get(ind) + " does not exist, it will not be removed.");
                                }
                            }
                            patternConfiguration.getTasksMapping().removeAll(mappingTasksToRemove);
                            break;
                        case "-tcl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            interpreter.print("list of mapped tasks:");
                            for (MappingPatternTask mappingTask : patternConfiguration.getTasksMapping()) {
                                interpreter.print(mappingTask.getStringDisplay());
                            }
                            break;
                        case "-mctl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            channelsLeftToMap = MappingPatternChannel.getChannelsLeftToMap(patternConfiguration.getChannelsMapping(), MappingPatternChannel.getChannelsToMap(patternConfiguration.getPortsConnection(), patternTasksAll,patternConfiguration.getClonedTasks()));
                            interpreter.print("The channels that need to be mapped are :");
                            for (MappingPatternChannel channelMap : channelsLeftToMap) {
                                interpreter.print(channelMap.getTaskOfChannelToMap() + "." + channelMap.getChannelToMapName());
                            }
                            break;
                        case "-mcm":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            String[] channelTaskToMap = argumentsOfOption.get(0).split("\\.");
                            if (channelTaskToMap.length != 2) {
                                return argumentsOfOption.get(0) + ": " + Interpreter.BAD;
                            }
                            String[] channelTaskSameMem = argumentsOfOption.get(1).split("\\.");
                            if (channelTaskSameMem.length != 2) {
                                return argumentsOfOption.get(1) + ": " + Interpreter.BAD;
                            }
                            List<MappingPatternChannel> allChannelsToMap = MappingPatternChannel.getChannelsToMap(patternConfiguration.getPortsConnection(), patternTasksAll,patternConfiguration.getClonedTasks());
                            channelsLeftToMap = MappingPatternChannel.getChannelsLeftToMap(patternConfiguration.getChannelsMapping(), allChannelsToMap);
                            boolean channelToMapExist = false;
                            MappingPatternChannel channelToMap = null; 
                            for (MappingPatternChannel channelMap : channelsLeftToMap) {
                                if (channelMap.getTaskOfChannelToMap().equals(channelTaskToMap[0]) && channelMap.getChannelToMapName().equals(channelTaskToMap[1])) {
                                    channelToMap = channelMap;
                                    channelToMapExist = true;
                                    break;
                                }
                            }
                            if (!channelToMapExist) {
                                return CHANNEL_TO_MAP_NOT_EXIST;
                            }
                            boolean channelSameHwExist = false;
                            int originSameChannel = 0;
                            for(String st : portsTaskOfModelAll.keySet()) {
                                if (!patternConfiguration.getClonedTasksName().contains(st)) {
                                    for (String wc : portsTaskOfModelAll.get(st).getWriteChannels()) {
                                        if (st.equals(channelTaskSameMem[0]) && wc.equals(channelTaskSameMem[1])) {
                                            originSameChannel = MappingPatternTask.ORIGIN_MODEL;
                                            channelSameHwExist = true;
                                        } 
                                    }
                                }
                            }
                            if (!channelSameHwExist) {
                                for (MappingPatternChannel channelMap : patternConfiguration.getChannelsMapping()) {
                                    if (channelMap.getTaskOfChannelToMap().equals(channelTaskSameMem[0]) && channelMap.getChannelToMapName().equals(channelTaskSameMem[1])) {
                                        channelSameHwExist = true;
                                        originSameChannel = channelMap.getOrigin();
                                        break;
                                    }
                                }
                            }
                            if (!channelSameHwExist) {
                                return CHANNEL_IN_SAME_MEM_MAP_NOT_EXIST;
                            }
                            if (channelSameHwExist && channelToMapExist) {
                                channelToMap.setTaskAndChannelInSameMem(channelTaskSameMem[0], channelTaskSameMem[1], originSameChannel);
                                patternConfiguration.getChannelsMapping().add(channelToMap);
                            }
                            break;
                        case "-mcml":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            interpreter.print("The list of channels that can be used to map in same memory:");
                            for(String st : portsTaskOfModelAll.keySet()) {
                                if (!patternConfiguration.getClonedTasksName().contains(st)) {
                                    for (String wc : portsTaskOfModelAll.get(st).getWriteChannels()) {
                                        interpreter.print(st + "." + wc);
                                    }
                                }
                            }
                            for (MappingPatternChannel channelMap : patternConfiguration.getChannelsMapping()) {
                                interpreter.print(channelMap.getTaskOfChannelToMap() + "." + channelMap.getChannelToMapName());
                            }
                            break;
                        case "-mcn":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            String[] channelTaskToMapNew = argumentsOfOption.get(0).split("\\.");
                            if (channelTaskToMapNew.length != 2) {
                                return argumentsOfOption.get(0) + ": " + Interpreter.BAD;
                            }
                            channelsLeftToMap = MappingPatternChannel.getChannelsLeftToMap(patternConfiguration.getChannelsMapping(), MappingPatternChannel.getChannelsToMap(patternConfiguration.getPortsConnection(), patternTasksAll,patternConfiguration.getClonedTasks()));
                            boolean channelToMapInNewMemExist = false;
                            MappingPatternChannel channelToMapInNewMem = null; 
                            for (MappingPatternChannel channelMap : channelsLeftToMap) {
                                if (channelMap.getTaskOfChannelToMap().equals(channelTaskToMapNew[0]) && channelMap.getChannelToMapName().equals(channelTaskToMapNew[1])) {
                                    channelToMapInNewMem = channelMap;
                                    channelToMapInNewMemExist = true;
                                    break;
                                }
                            }
                            if (!channelToMapInNewMemExist) {
                                return CHANNEL_TO_MAP_NOT_EXIST;
                            }
                            boolean busNewMemExist = false;
                            for (HwNode bus : tmap.getArch().getBUSs()) {
                                if (bus.getName().equals(argumentsOfOption.get(1))) {
                                    busNewMemExist = true;
                                    break;
                                }
                            }
                            if (!busNewMemExist) {
                                return BUS_NOT_EXIST;
                            }
                            if (channelToMapInNewMemExist && busNewMemExist) {
                                channelToMapInNewMem.setBusNameForNewMem(argumentsOfOption.get(1));
                                patternConfiguration.getChannelsMapping().add(channelToMapInNewMem);
                            }
                            break;
                        case "-mcr":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() == 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            List<MappingPatternChannel> mappingChannelsToRemove = new ArrayList<MappingPatternChannel>();
                            boolean isMappingChannelToRemoveExist = false;
                            for (int ind = 0; ind < argumentsOfOption.size(); ind++) {
                                String[] channelTaskToRem = argumentsOfOption.get(ind).split("\\.");
                                if (channelTaskToRem.length != 2) {
                                    interpreter.print(argumentsOfOption.get(ind) + ": " + Interpreter.BAD);
                                    break;
                                }
                                for (MappingPatternChannel mappingChannel : patternConfiguration.getChannelsMapping()) {
                                    if (mappingChannel.getTaskOfChannelToMap().equals(channelTaskToRem[0]) && mappingChannel.getChannelToMapName().equals(channelTaskToRem[1])) {
                                        mappingChannelsToRemove.add(mappingChannel);
                                        isMappingChannelToRemoveExist = true;
                                        break;
                                    }
                                }
                                if (!isMappingChannelToRemoveExist) {
                                    interpreter.print(argumentsOfOption.get(ind) + " does not exist, it will not be removed.");
                                }
                            }
                            patternConfiguration.getChannelsMapping().removeAll(mappingChannelsToRemove);
                            break;
                        case "-mcl":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() != 0) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            interpreter.print("list of mapped channels:");
                            for (MappingPatternChannel mappedChannel : patternConfiguration.getChannelsMapping()) {
                                interpreter.print(mappedChannel.getStringDisplay());
                            }
                            break;
                        case "-ua":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 3) {
                                return Interpreter.BAD;
                            }
                            String task = argumentsOfOption.get(0);
                            String attribute = argumentsOfOption.get(1);
                            String value = argumentsOfOption.get(2);
                            
                            boolean isTaskExist = false;
                            boolean isAttributeExist = false;
                            boolean isUpdated = false;
                            AttributeTaskJsonFile attributeTask = null;
                            for (String taskPattern : patternTasksAll.keySet()) {
                                if (taskPattern.equals(task)) {
                                    isTaskExist = true;
                                    for (AttributeTaskJsonFile attributeTaskJsonFile : patternTasksAll.get(taskPattern).getAttributes()) {
                                        if (attributeTaskJsonFile.getName().equals(attribute)) {
                                            isAttributeExist = true;
                                            if (value.matches("-?\\d+") || value.matches("(?i)^(true|false)")) {
                                                attributeTaskJsonFile.setValue(value);
                                                attributeTask = attributeTaskJsonFile;
                                                isUpdated = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (!isTaskExist) {
                                return TASK_NOT_EXIST;
                            }
                            if (!isAttributeExist) {
                                return ATTRIBUTE_NOT_EXIST;
                            }
                            if (!isUpdated) {
                                return VALUE_BAD;
                            }
                            if (patternConfiguration.getUpdatedPatternAttributes().get(task) != null) {
                                patternConfiguration.getUpdatedPatternAttributes().get(task).add(attributeTask);
                            } else {
                                List<AttributeTaskJsonFile> attributesTask = new ArrayList<AttributeTaskJsonFile>();
                                attributesTask.add(attributeTask);
                                patternConfiguration.getUpdatedPatternAttributes().put(task, attributesTask);
                            }
                            break;
                        case "-m":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() > 1) {
                                return Interpreter.BAD;
                            }
                            patternTasksLeft = TaskPattern.getPatternTasksLeft(patternTasksAll, patternConfiguration.getPortsConnection());
                            if (patternTasksLeft.keySet().size() > 1) {
                                return PATTERN_NOT_CONNECTED;
                            }
                            portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                            portsLeftToConfig = PatternPortsConfig.getPortsLeftToConfig(portsTaskOfModelAll, portsTaskModelLeft, patternConfiguration.getClonedTasksName(), patternConfiguration.getPortsConfig(), tmap.getTMLModeling());
                            if (portsLeftToConfig.size() != 0) {
                                return PORT_CONFIGURATION_NOT_FINISHED;
                            }
                            if (argumentsOfOption.size() == 0) {
                                if (selectedPatternName != null && !selectedPatternName.equals("")) {
                                    patternConfiguration.loadChannelsWithSecurity(patternTasksAll);
                                    GTURTLEModeling.createJsonPatternConfigFile(selectedPatternPath, selectedPatternName, patternConfiguration);
                                } else {
                                    return NO_NAME_NO_TASK_FOR_PATTERN;
                                }
                            } else if (argumentsOfOption.size() == 1) {
                                patternConfiguration.loadChannelsWithSecurity(patternTasksAll);
                                GTURTLEModeling.createJsonPatternConfigFile(argumentsOfOption.get(0), patternConfiguration);
                            }
                            break;
                        default:
                            return Interpreter.BAD;
                    }
                }
                return null;
            }
        };


        // Diplodocus load TMAP
        Command diplodocusLoadTMAP = new Command() {
            public String getCommand() {
                return DIPLO_LOAD_TMAP;
            }

            public String getShortCommand() {
                return "dltmap";
            }

            public String getDescription() {
                return "Load a TMAP specification";
            }

            public String getUsage() {
                return "[PATH]\n"
                + "[PATH]\tLoad a Textual TMAP specification\n"
                + "\tLoad a TMAP specification from the current panel\n";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                /*if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }*/

                String[] commands = command.split(" ");
                if (commands.length > 1) {
                    return Interpreter.BAD;
                }
                if (commands.length == 1 && commands[0] != "") {
                    String tmlFileName = commands[0].split("/")[commands[0].split("/").length-1];
                    String tmlFilePath = commands[0].substring(0, commands[0].lastIndexOf("/")+1);
                    TMLMappingTextSpecification<Class<?>>  ts = new TMLMappingTextSpecification<Class<?>>(tmlFileName);
                    File f = new File(commands[0]);
                    String spec = null;
                    try {
                        spec = FileUtils.loadFileData(f);
                    } catch (Exception e) {
                        System.out.println("Exception loading " + tmlFileName);
                    }
                    boolean parsed = ts.makeTMLMapping(spec, tmlFilePath);
                    tmap = ts.getTMLMapping();
                    // Checking syntax
                    TMLSyntaxChecking syntax = new TMLSyntaxChecking(tmap);
                    syntax.checkSyntax();
                    nullifyAll();
                } else {
                    if (interpreter.mgui != null) {
                        TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                        if (!(tp instanceof TMLArchiPanel)) {
                            return "Current diagram is invalid for formal verification";
                        }
                        if(!interpreter.mgui.checkModelingSyntax(tp, true)) {
                            return SYNTAX_ERROR;
                        }

                        tmap = interpreter.mgui.gtm.getTMLMapping();
                        nullifyAll();
                        if (tmap == null) {
                            return "Fail to load TMAP specification"; 
                        }
                    } else {
                        return Interpreter.TTOOL_NOT_STARTED;
                    }
                }
                
                for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                    String[] taskNameSplit = task.getName().split("__");
                    allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                    if (taskNameSplit.length > 1) {
                        tabName = taskNameSplit[0];
                    } 
                    task.setName(taskNameSplit[taskNameSplit.length-1]);
                }
                for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
                    String[] chNameSplit = ch.getName().split("__");
                    ch.setName(chNameSplit[chNameSplit.length-1]);
                }
                for (TMLEvent evt : tmap.getTMLModeling().getEvents()) {
                    String[] evtNameSplit = evt.getName().split("__");
                    evt.setName(evtNameSplit[evtNameSplit.length-1]);
                }
                for (TMLRequest req : tmap.getTMLModeling().getRequests()) {
                    String[] reqNameSplit = req.getName().split("__");
                    req.setName(reqNameSplit[reqNameSplit.length-1]);
                }

                return null;
            }
        };

        Command integratePattern = new Command() {
            final String [] options = {"-l", "-n", "-p", "-pc", "-g", "-d"};
            public String getCommand() {
                return INTEGRATE;
            }

            public String getShortCommand() {
                return "a";
            }

            public String getDescription() {
                return "Inegrate a pattern to a model by giving a name to the pattern and the path of the configuration file";
            }

            public String getUsage() {
                return "[OPTION]... [NAME] [PATH]\n"
                + "-l NAME\tlist of available patterns\n"
                + "-n NAME\tselect a pattern by its name\n"
                + "-p PATH\tselect a pattern by the path of its folder\n"
                + "-pc PATH\tpath of the configuration json file\n"
                + "-g [PATH] [NAME]\tgenerate TMAP file of the model after intergrating the pattern, you can optionally specify the path of this new tmap file and its name\n"
                + "-d \tdraw the new model after integrating the pattern\n";
            }
            
            @SuppressWarnings("unchecked")
            public String executeCommand(String command, Interpreter interpreter) {
                /*if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }*/
                
                if (tmap == null) {
                    if (interpreter.mgui != null) {
                        TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                        if (!(tp instanceof TMLArchiPanel)) {
                            return "Current panel does not contain TMAP (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +" or move to Arch panel.)";
                        }
                        if(!interpreter.mgui.checkModelingSyntax(tp, true)) {
                            return SYNTAX_ERROR + "in current panel (NB: you can load a textual TMAP Spec using " + DIPLO_LOAD_TMAP +")";
                        }
                        tmap = interpreter.mgui.gtm.getTMLMapping();
                        nullifyAll();
                        if (tmap == null) {
                            return Interpreter.TMAP_NO_SPEC + "\nLoad a textual TMAP Spec using " + DIPLO_LOAD_TMAP + " command or move to Arch panel.";
                        } else {
                            for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                                String[] taskNameSplit = task.getName().split("__");
                                allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                                if (taskNameSplit.length > 1) {
                                    tabName = taskNameSplit[0];
                                } 
                                task.setName(taskNameSplit[taskNameSplit.length-1]);
                            }
                            for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
                                String[] chNameSplit = ch.getName().split("__");
                                ch.setName(chNameSplit[chNameSplit.length-1]);
                            }
                            for (TMLEvent evt : tmap.getTMLModeling().getEvents()) {
                                String[] evtNameSplit = evt.getName().split("__");
                                evt.setName(evtNameSplit[evtNameSplit.length-1]);
                            }
                            for (TMLRequest req : tmap.getTMLModeling().getRequests()) {
                                String[] reqNameSplit = req.getName().split("__");
                                req.setName(reqNameSplit[reqNameSplit.length-1]);
                            }
                        }
                    } else {
                        return Interpreter.TTOOL_NOT_STARTED;
                    }
                }
                String[] commands = command.split(" ");
                for (int i = 0; i < commands.length; i++) {
                    List<String> argumentsOfOption = new ArrayList<String>();
                    switch (commands[i]) {
                        case "-l":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            listPatterns = getFoldersName(patternsPath);
                            interpreter.print("There is " + listPatterns.size() + " pattern(s):");
                            int cont = 1;
                            for (String pattern : listPatterns) {
                                interpreter.print(cont + ". " + pattern);
                                cont++;
                            }
                            break;
                        case "-n":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            listPatterns = getFoldersName(patternsPath);
                            if (listPatterns.contains(argumentsOfOption.get(0))) {
                                selectedPatternToIntergrate = argumentsOfOption.get(0);
                                selectedPatternPathToIntergrate = patternsPath + selectedPatternToIntergrate + "/";
                            } else {
                                return PATTERN_NOT_EXIST;
                            }
                            break;
                        case "-p":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            File folderPattern = new File(argumentsOfOption.get(0));
                            if (folderPattern.isDirectory()) {
                                selectedPatternToIntergrate = argumentsOfOption.get(0).split("/")[argumentsOfOption.get(0).split("/").length-1];
                                selectedPatternPathToIntergrate = argumentsOfOption.get(0);
                                if (!selectedPatternPathToIntergrate.endsWith("/")) {
                                    selectedPatternPathToIntergrate += "/";
                                }
                            } else {
                                return PATTERN_NOT_EXIST;
                            }

                            break;
                        case "-pc":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 1) {
                                return Interpreter.BAD;
                            }
                            Path path = Paths.get(argumentsOfOption.get(0));
                            if (Files.exists(path)) {
                                selectedJsonFilePath = argumentsOfOption.get(0);
                            } else {
                                return CONFIG_JSON_FILE_NOT_EXIST;
                            }
                            break;
                        case "-g":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0 && argumentsOfOption.size() != 2) {
                                return Interpreter.BAD;
                            }
                            if (selectedJsonFilePath != null && selectedPatternToIntergrate != null) {
                                TMLMapping<?> tmapGen = GTURTLEModeling.integratePatternTMAP(tabName, selectedPatternPathToIntergrate, selectedPatternToIntergrate, selectedJsonFilePath, tmap);
                                if (tabName != "") {
                                    for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                                        String[] taskNameSplit = task.getName().split("__");
                                        if (taskNameSplit.length == 1) {
                                            task.setName(tabName + "__" + task.getName());
                                        }
                                    }
                                    for (TMLChannel ch : tmap.getTMLModeling().getChannels()) {
                                        String[] channelNameSplit = ch.getName().split("__");
                                        if (channelNameSplit.length == 1) {
                                            ch.setName(tabName + "__" + ch.getName());
                                        }
                                    }
                                    for (TMLEvent evt : tmap.getTMLModeling().getEvents()) {
                                        String[] eventNameSplit = evt.getName().split("__");
                                        if (eventNameSplit.length == 1) {
                                            evt.setName(tabName + "__" + evt.getName());
                                        }
                                    }
                                    for (TMLRequest req : tmap.getTMLModeling().getRequests()) {
                                        String[] requestNameSplit = req.getName().split("__");
                                        if (requestNameSplit.length == 1) {
                                            req.setName(tabName + "__" + req.getName());
                                        }
                                    }
                                }
                                TMLMappingTextSpecification<Class<?>> spec = new TMLMappingTextSpecification<Class<?>>(selectedPatternToIntergrate);
                                spec.toTextFormat((TMLMapping<Class<?>>) tmapGen);
                                if (argumentsOfOption.size() == 0) {
                                    String folderGenTmap = selectedJsonFilePath.substring(0, selectedJsonFilePath.lastIndexOf("/"));
                                    if (!folderGenTmap.endsWith("/")) {
                                        folderGenTmap += "/";
                                    }
                                    try {
                                        Files.createDirectories(Paths.get(folderGenTmap));
                                        spec.saveFile(folderGenTmap, "spec");
                                    } catch (Exception e) {
                                        return "Files could not be saved: " + e.getMessage();
                                    }
                                } else if (argumentsOfOption.size() == 2) {
                                    String folderGenTmap = argumentsOfOption.get(0);
                                    String nameGenTmap = argumentsOfOption.get(1);
                                    if (!folderGenTmap.endsWith("/")) {
                                        folderGenTmap += "/";
                                    }
                                    try {
                                        Files.createDirectories(Paths.get(folderGenTmap));
                                        spec.saveFile(folderGenTmap, nameGenTmap);
                                    } catch (Exception e) {
                                        return "Files could not be saved: " + e.getMessage();
                                    }
                                }
                            } else {
                                return CONFIG_JSON_FILE_MISSING;
                            }
                            break;
                        case "-d":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (interpreter.mgui != null) {
                                if (selectedJsonFilePath != null && selectedPatternToIntergrate != null) {
                                    interpreter.mgui.gtm.integratePattern(interpreter.mgui, selectedPatternPathToIntergrate, selectedPatternToIntergrate, selectedJsonFilePath);
                                } else {
                                    return CONFIG_JSON_FILE_MISSING;
                                }
                            } else {
                                return Interpreter.TTOOL_NOT_STARTED;
                            }
                            break;
                        default:
                            return Interpreter.BAD;
                    }
                }
                return null;
            }
        };
        addAndSortSubcommand(createPattern);
        addAndSortSubcommand(configurePattern);
        addAndSortSubcommand(integratePattern);
        addAndSortSubcommand(diplodocusLoadTMAP);
    }

    Vector<String> getFoldersName(String path) {
        Vector<String> folders = new Vector<String>();
        File directoryPath = new File(path);
        String[] directories = directoryPath.list((dir, name) -> new File(dir, name).isDirectory());
        if (directories != null) {
            folders = new Vector<String>(Arrays.asList(directories));
        }
        return folders;
    }

    private void nullifyAll() {
        selectedTasks.clear();
        patternName = null;
        allTasksOfModel.clear();
        listPatterns.clear();
        selectedPatternPath = null;
        selectedPatternName = null;
        patternConfiguration = new PatternConfiguration();
        patternTasksAll = null; 
        patternTasksLeft = null;
        portsTaskOfModelAll = null;
        portsTaskModelLeft = null;
        portsLeftToConfig = null;
        tasksLeftToMap = null;
        channelsLeftToMap = null;
        selectedPatternToIntergrate = null;
        selectedJsonFilePath = null;
    }
}
