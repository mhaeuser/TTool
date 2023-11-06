package cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLTask;
import tmltranslator.patternhandling.PatternCloneTask;
import tmltranslator.patternhandling.PatternConfiguration;

/**
 * Class PatternHandling
 * Creation: 23/10/2023
 * Version 1.0 23/10/2023
 *
 * @author Jawher JERRAY
 */

public class PatternHandling extends Command {
    private final static String NO_NAME_NO_TASK_FOR_PATTERN = "No name or no tasks are giving for the pattern";

    private final static String PATTERN_NOT_EXIST = "Pattern does not exist";
    private final static String CLONE_TASK_EXIST = "Cloned task already exists";
    private final static String TASK_TO_CLONE_NOT_EXIST = "Task to clone does not exist";
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
    private PatternConfiguration patternConfiguration;
        
    public PatternHandling() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "pattern-handling";
    }

    public String getShortCommand() {
        return "ph";
    }

    public String getUsage() {
        return "pattern-handling <subcommand> <options>";
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
                                    interpreter.print("The task " + argumentOfOption + "does not exist, it will not be removed.");
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
            final String [] options = {"-s", "-p", "-ct", "-rct", "-co", "-plnc", "-pl", "-t", "-cpl", "-cpd","-cpm","-cpr","-tcl","-tcm", "-tcn", "-tcr", "-mcl", "-mcm", "-mcn", "-mcr", "-ua", "-m"};
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
                + "-ct TASK1 TASK2\tclone TASK1 from TASK2\n"
                + "-rct TASK\t remove the clone task TASK\n"
                + "-co TASK1.PORT1 TASK2.PORT2 [NEW] [C] [WA] [SA]\t connect PORT1 of TASK1 (pattern side) with PORT2 of TASK2 (model side). Other possible options:\n\t*Set NEW to specify that this is a new port that needs to be created.\n\t*Set C to ensure confidentiality for this channel\n\t*Set WA to ensure weak authenticity for this channel\n\t*Set SA to ensure strong authenticity for this channel\n"
                + "-plnc \tget the list of pattern's ports that are not yet connected\n"
                + "-pl PORT \tget list of available ports in model that could be used to connect with PORT (pattern side)\n"
                + "-t \tget all tasks of the model\n"
                + "-cpl \tget list of ports to be configured\n"
                + "-cpd PORT\tchoose removing PORT as decision\n"
                + "-cpm PORT1 PORT2\tchoose merging PORT1 with PORT2 as decision\n"
                + "-cpr PORT\tremove the configuration decision for PORT\n"
                + "-tcl \tget list of tasks to be mapped \n"
                + "-tcm TASK1 TASK2\tmap TASK1 in same CPU as TASK2\n"
                + "-tcn TASK BUS\tmap TASK in new CPU linked to bus BUS\n"
                + "-tcr TASK\t remove the mapping of TASK\n"
                + "-mcl \tmap get list of channels to be mapped \n"
                + "-mcm CHANNEL1 CHANNEL2\tmap CHANNEL1 in same memory and buses as CHANNEL2\n"
                + "-mcn CHANNEL BUS\tmap CHANNEL in new memory linked to bus BUS\n"
                + "-mcr CHANNEL\t remove the mapping of CHANNEL\n"
                + "-ua TASK ATTRIBUTE VALUE \tput the value VALUE of attribute ATTRIBUTE of the task TASK \n"
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
                            } else {
                                selectedPatternName = null;
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
                            interpreter.print("There is " + listPatterns.size() + " pattern(s):\n");
                            int cont = 1;
                            for (String pattern : listPatterns) {
                                interpreter.print(cont + ". " + pattern + "\n");
                                cont++;
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
                            if (allTasksOfModel.contains(argumentsOfOption.get(0))) {
                                return CLONE_TASK_EXIST;
                            }
                            if (!allTasksOfModel.contains(argumentsOfOption.get(1))) {
                                return TASK_TO_CLONE_NOT_EXIST;
                            }
                            PatternCloneTask patterClone = new PatternCloneTask(argumentsOfOption.get(0), argumentsOfOption.get(1));
                            patternConfiguration.addClonedTasks(patterClone);
                            break;
                        case "-rct":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            if (argumentsOfOption.size() != 0) {
                                return Interpreter.BAD;
                            }
                            
                            interpreter.print("This model contains " + allTasksOfModel.size() + " task(s) :\n");
                            String printTasks = "";
                            for (int ind = 0; i < allTasksOfModel.size(); i++) {
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
                                selectedTasks.add(argumentOfOption);
                            }
                            break;
                        case "-m":
                            while (i+1 < commands.length && !Arrays.asList(options).contains(commands[i+1])) {
                                argumentsOfOption.add(commands[i+1]);
                                i += 1;
                            }
                            for (String argumentOfOption : argumentsOfOption) {
                                if (allTasksOfModel.contains(argumentOfOption)) {
                                    selectedTasks.remove(argumentOfOption);
                                } else {
                                    interpreter.print("The task " + argumentOfOption + "does not exist, it will not be removed.");
                                }
                            }
                            interpreter.mgui.gtm.createPattern(new ArrayList<String>(selectedTasks), patternName, patternsPath);
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
