package cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLTask;
import tmltranslator.patternhandling.AttributeTaskJsonFile;
import tmltranslator.patternhandling.MappingPatternChannel;
import tmltranslator.patternhandling.PatternChannelWithSecurity;
import tmltranslator.patternhandling.PatternCloneTask;
import tmltranslator.patternhandling.PatternConfiguration;
import tmltranslator.patternhandling.PatternConnection;
import tmltranslator.patternhandling.PatternCreation;
import tmltranslator.patternhandling.PatternPortsConfig;
import tmltranslator.patternhandling.PortTaskJsonFile;
import tmltranslator.patternhandling.TaskPattern;
import tmltranslator.patternhandling.TaskPorts;

/**
 * Class PatternHandling
 * Creation: 23/10/2023
 * Version 1.0 23/10/2023
 *
 * @author Jawher JERRAY
 */

public class PatternHandling extends Command {
    private final static String NO_NAME_NO_TASK_FOR_PATTERN = "No name or no tasks is giving for the pattern";

    private final static String PATTERN_NOT_EXIST = "Pattern does not exist";
    private final static String NO_PATTERN_SELECTED = "No pattern selected";
    private final static String PATTERN_NOT_CONNECTED = "Some pattern ports are not connected yet";
    private final static String PORT_CONFIGURATION_NOT_FINISHED = "Some ports configuration are missing";
    private final static String CLONE_TASK_EXIST = "Cloned task already exists";
    private final static String TASK_TO_CLONE_NOT_EXIST = "Task to clone does not exist";
    private final static String TASK_NOT_EXIST = "Task does not exist";
    private final static String PATTERN_TASK_NOT_EXIST = "Pattern task does not exist or all its external ports are already connected";
    private final static String PATTERN_PORT_NOT_EXIST = "Pattern port does not exist or its already connected";
    private final static String MODEL_TASK_NOT_EXIST = "Model task does not exist";
    private final static String MODEL_PORT_NOT_EXIST = "Model port does not exist or its already used";
    private final static String PORTS_TYPES_NOT_MATCH = "The selected port in pattern side does not match with the selected port in the model side";
    private final static String ATTRIBUTE_NOT_EXIST = "Attribute does not exist";
    private final static String VALUE_BAD = "Unreadable value";
    // PatternHandling commands
    private final static String CREATE = "create";
    private final static String CONFIGURE = "select-configure";
    private final static String INTEGRATE = "integrate";
    private final static String DIPLO_LOAD_TMAP = "diplodocus-load-tmap";

    private TMLMapping<?> tmap;
    private String patternName, patternsPath = "../ttool/build/resources/main/patterns/";
    private HashSet<String> selectedTasks = new HashSet<String>();
    private HashSet<String> allTasksOfModel = new HashSet<String>();

    private Vector<String> listPatterns = new Vector<String>();
    private String selectedPatternPath, selectedPatternName;
    private PatternConfiguration patternConfiguration = new PatternConfiguration();
    LinkedHashMap<String, TaskPattern> patternTasksAll, patternTasksLeft;
    LinkedHashMap<String, TaskPorts> portsTaskOfModelAll;// = new LinkedHashMap<String, TaskPorts>();
    LinkedHashMap<String, TaskPorts> portsTaskModelLeft;// = new LinkedHashMap<String, TaskPorts>();
        
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
            final String [] options = {"-n", "-a", "-l", "-t", "-r", "-m"};
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
                return "[OPTION]... [NAME] [TASKS]\n"
                + "-n NAME\tset a name for the pattern to create\n"
                + "-a TASKS\tselect tasks to add to the pattern (seperated by a space)\n"
                + "-l \tget the list of selected tasks to be added to the pattern\n"
                + "-t \tget all tasks of the model\n"
                + "-r TASKS\tremove selected tasks\n"
                + "-m \tmake the pattern\n";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                
                if (tmap == null) {
                    tmap =  interpreter.mgui.gtm.getTMLMapping();
                    if (tmap == null) {
                        return Interpreter.TMAP_NO_SPEC + "\nLoad a TMAP Spec using " + DIPLO_LOAD_TMAP + " command or move to Arch tab.";
                    } else {
                        allTasksOfModel.clear();
                        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                            String[] taskNameSplit = task.getName().split("__");
                            allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                        }
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
                                interpreter.mgui.gtm.createPattern(new ArrayList<String>(selectedTasks), patternName, patternsPath);
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
            final String [] options = {"-s", "-p", "-sp", "-ct", "-ctr", "-co", "-cor", "-col", "-plnc", "-pl", "-t", "-cpl", "-cpd","-cpm","-cpr","-tcl","-tcm", "-tcn", "-tcr", "-mcl", "-mcm", "-mcn", "-mcr", "-ua", "-m"};
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
                + "-s NAME\tselect a pattern by its name\n"
                + "-p \tget the list of all the created patterns\n"
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
                + "-cpl \tget list of ports to be configured\n"
                + "-cpd [TASK.PORT] [ALL]\tchoose removing PORT as decision (or ALL to remove all the ports)\n"
                + "-cpm TASK.PORT1 PORT2\tchoose merging PORT1 with PORT2 as decision\n"
                + "-cpr TASK.PORT\tremove the configuration decision for PORT\n"
                + "-tcl \tget list of tasks to be mapped \n"
                + "-tcm TASK1 TASK2\tmap TASK1 in same CPU as TASK2\n"
                + "-tcn TASK BUS\tmap TASK in new CPU linked to bus BUS\n"
                + "-tcr TASK\tremove the mapping of TASK\n"
                + "-mcl \tmap get list of channels to be mapped \n"
                + "-mcm TASK1.CHANNEL1 TASK2.CHANNEL2\tmap CHANNEL1 in same memory and buses as CHANNEL2\n"
                + "-mcn TASK.CHANNEL BUS\tmap CHANNEL in new memory linked to bus BUS\n"
                + "-mcr TASK.CHANNEL\tremove the mapping of CHANNEL\n"
                + "-ua TASK ATTRIBUTE VALUE \tput the value VALUE of attribute ATTRIBUTE of the task TASK\n"
                + "-m \tmake the configuration of the pattern\n";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                
                if (tmap == null) {
                    tmap =  interpreter.mgui.gtm.getTMLMapping();
                    if (tmap == null) {
                        return Interpreter.TMAP_NO_SPEC + "\nLoad a TMAP Spec using " + DIPLO_LOAD_TMAP + " command or move to Arch tab.";
                    } else {
                        allTasksOfModel.clear();
                        for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                            String[] taskNameSplit = task.getName().split("__");
                            allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                        }
                    }
                }

                String[] commands = command.split(" ");
                for (int i = 0; i < commands.length; i++) {
                    List<String> argumentsOfOption = new ArrayList<String>();
                    
                    switch (commands[i]) {
                        case "-s":
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
                                patternTasksAll = TaskPattern.parsePatternJsonFile(patternsPath+"/"+patternName, patternName+".json");
                                //patternTasksLeft = TaskPattern.parsePatternJsonFile(patternsPath+"/"+patternName, patternName+".json");
                                portsTaskOfModelAll = TaskPorts.getListPortsTask(tmap.getTMLModeling());
                                //portsTaskOfModelLeft = TaskPorts.getListPortsTask(tmap.getTMLModeling());
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
                                interpreter.print("The selected pattern is " +  selectedPatternName);
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
                            
                            //portsTaskOfModelLeft.put(clonedTask, TaskPorts.cloneTaskPort(portsTaskOfModelAll.get(selectedTaskToClone)));
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
                                    String[] taskPort = argumentsOfOption.get(ind).split(".");
                                    if (taskPort.length == 2) {
                                        patternTask = taskPort[0];
                                        patternPort = taskPort[1];
                                    } else {
                                        return Interpreter.BAD;
                                    }
                                } else if (ind == 1) {
                                    String[] taskPort = argumentsOfOption.get(ind).split(".");
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
                            if (portModelType == 0) {
                                return MODEL_PORT_NOT_EXIST;
                            }
                            boolean samePortType = false;
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
                            if (samePortType) {
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
                                String[] taskPortToRemove = argumentOfOption.split(".");
                                boolean taskPortExist = false;
                                if (taskPortToRemove.length == 2) {
                                    patternTaskToRemove = taskPortToRemove[0];
                                    patternPortToRemove = taskPortToRemove[1];
                                    taskPortExist = true;
                                } else {
                                    interpreter.print(argumentOfOption + " does not exist, it will not be removed.");
                                }
                                if (taskPortExist) {
                                    for (PatternConnection patternConnection : patternConfiguration.getPortsConnection()) {
                                        if (patternConnection.getPatternTaskName().equals(patternTaskToRemove) && patternConnection.getPatternChannel().equals(patternPortToRemove)) {
                                            patternConnectionsToRemove.add(patternConnection);
                                            break;
                                        }
                                    }
                                }
                                
                            }
                            patternConfiguration.getPortsConnection().removeAll(patternConnectionsToRemove);
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
                            String[] taskPort = argumentsOfOption.get(0).split(".");
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
                            List<String> clonedTasksAll = new ArrayList<String>();
                            for (PatternCloneTask cloneT : patternConfiguration.getClonedTasks()) {
                                clonedTasksAll.add(cloneT.getClonedTask());
                            }
                            portsTaskModelLeft = TaskPorts.getPortsTaskOfModelLeft(portsTaskOfModelAll, patternConfiguration.getPortsConnection());
                            List<PatternPortsConfig> portsToConfig = PatternPortsConfig.getPortsToConfig(portsTaskOfModelAll, portsTaskModelLeft, clonedTasksAll, tmap.getTMLModeling());
                            interpreter.print("The ports that need to be configured are :");
                            for (PatternPortsConfig portToConfig : portsToConfig) {
                                boolean isToConfig = true;
                                for (PatternPortsConfig portConfigured : patternConfiguration.getPortsConfig()) {
                                    if (portToConfig.getTaskOfChannelToConfig().equals(portConfigured.getTaskOfChannelToConfig()) && portToConfig.getChannelToConfig().equals(portConfigured.getChannelToConfig())) {
                                        isToConfig = false;
                                        break;
                                    }
                                }
                                if (isToConfig) {
                                    interpreter.print(portToConfig.getTaskOfChannelToConfig() + "." + portToConfig.getChannelToConfig());
                                }
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
                            if (selectedPatternName == null) {
                                return NO_PATTERN_SELECTED;
                            }
                            String taskOfChannelToMap;
                            String channelToMap;
                            int origin = MappingPatternChannel.ORIGIN_CLONE;
                            String[] taskChannelToMap = argumentsOfOption.get(0).split(".");
                            if (taskChannelToMap.length == 2) {
                                taskOfChannelToMap = taskChannelToMap[0];
                                channelToMap = taskChannelToMap[1];
                            } else {
                                return Interpreter.BAD;
                            }
                            String taskOfChannelSameHw;
                            String channelSameHw;
                            String[] taskChannelSameHw = argumentsOfOption.get(1).split(".");
                            if (taskChannelSameHw.length == 2) {
                                taskOfChannelSameHw = taskChannelSameHw[0];
                                channelSameHw = taskChannelSameHw[1];
                            } else {
                                return Interpreter.BAD;
                            }
                            for (String patternName : patternTasksAll.keySet()) {
                                if (patternName.equals(taskOfChannelToMap)) {
                                    for (PortTaskJsonFile port : patternTasksAll.get(patternName).getInternalPorts()) {
                                        if (port.getName().equals(channelToMap)) {
                                            origin = MappingPatternChannel.ORIGIN_PATTERN;
                                            break;
                                        }
                                    }
                                    for (PortTaskJsonFile port : patternTasksAll.get(patternName).getExternalPorts()) {
                                        if (port.getName().equals(channelToMap)) {
                                            origin = MappingPatternChannel.ORIGIN_PATTERN;
                                            break;
                                        }
                                    }
                                }
                            }
                            MappingPatternChannel channelMapping = new MappingPatternChannel(taskOfChannelToMap, channelToMap, origin);
                            channelMapping.setTaskAndChannelInSameMem(taskOfChannelSameHw, channelSameHw, i);
                            patternConfiguration.addChannelsMapping(channelMapping);
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
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            if (patternName != null && !patternName.equals("") && selectedTasks.size() != 0) {
                                interpreter.mgui.gtm.createJsonPatternConfigFile(selectedPatternPath, selectedPatternName, patternConfiguration);
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


        // Diplodocus load TMAP
        Command diplodocusLoadTMAP = new Command() {
            public String getCommand() {
                return DIPLO_LOAD_TMAP;
            }

            public String getShortCommand() {
                return "dltmap";
            }

            public String getDescription() {
                return "Load a textual TMAP specification";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String[] commands = command.split(" ");
                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                TMLMappingTextSpecification<?> ts = interpreter.mgui.loadTMAPTxt(commands[0]);

                if (ts == null) {
                    return "Fail to load TMAP specification";
                }

                if (ts.getErrors().size() > 0) {
                    return "TMAP specification has errors";
                }

                tmap = ts.getTMLMapping();
                allTasksOfModel.clear();
                for (TMLTask task : tmap.getTMLModeling().getTasks()) {
                    String[] taskNameSplit = task.getName().split("__");
                    allTasksOfModel.add(taskNameSplit[taskNameSplit.length-1]);
                }

                return null;
            }
        };


        addAndSortSubcommand(createPattern);
        addAndSortSubcommand(configurePattern);
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
}
