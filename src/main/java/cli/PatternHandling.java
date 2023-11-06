package cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLTask;

/**
 * Class PatternHandling
 * Creation: 23/10/2023
 * Version 1.0 23/10/2023
 *
 * @author Jawher JERRAY
 */

public class PatternHandling extends Command {
    // PatternHandling commands
    private final static String CREATE = "create";
    private final static String CONFIGURE = "select-configure";
    private final static String INTEGRATE = "integrate";
    private final static String DIPLO_LOAD_TMAP = "diplodocus-load-tmap";

    private TMLMapping<?> tmap;
    private String patternName, patternsPath = "../ttool/build/resources/main/patterns/";
    private HashSet<String> selectedTasks = new HashSet<String>();
    private HashSet<String> allTasksOfModel = new HashSet<String>();
        
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
                + "-l \tget the list of selected tasks to add to the pattern\n"
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
                                    interpreter.print("The task " + argumentOfOption + "does not exist, it will not be added.");
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
                            interpreter.print("The selected tasks are :\n");
                            String printSelectedTasks = "";
                            for (int ind = 0; i < selectedTasks.size(); i++) {
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

        // Create Pattern
        Command configurePattern = new Command() {
            final String [] options = {"-s", "-p", "-ct", "-rct", "-co", "-mt", "-mc", "-s", "-m"};
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
                + "-co TASK1.PORT1 TASK2.PORT2\t connect PORT1 of TASK1 with PORT2 of TASK2\n"
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
                                    interpreter.print("The task " + argumentOfOption + "does not exist, it will not be added.");
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
                            interpreter.print("The selected tasks are :\n");
                            String printSelectedTasks = "";
                            for (int ind = 0; i < selectedTasks.size(); i++) {
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
}
