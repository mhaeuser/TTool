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

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import launcher.RTLLauncher;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;
import ui.window.JDialogSystemCGeneration;
import ui.*;

import java.io.File;
import java.util.BitSet;
import java.util.*;


/**
 * Class Action
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Action extends Command  {
    // Action commands
    private final static String OPEN = "open";
    private final static String START = "start";
    private final static String QUIT = "quit";
    private final static String CHECKSYNTAX = "check-syntax";
    private final static String DIPLO_INTERACTIVE_SIMULATION = "diplodocus-interactive-simulation";
    private final static String DIPLO_FORMAL_VERIFICATION = "diplodocus-formal-verification";
    private final static String DIPLO_ONETRACE_SIMULATION = "diplodocus-onetrace-simulation";
    private final static String DIPLO_GENERATE_TML = "diplodocus-generate-tml";
    private final static String DIPLO_UPPAAL = "diplodocus-uppaal";

    private final static String NAVIGATE_PANEL_TO_LEFT = "navigate-panel-to-left";

    private final static String NAVIGATE_LEFT_PANEL = "navigate-left-panel";

    private final static String GENERIC = "generic";

    public Action() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "action";
    }

    public String getShortCommand() {
        return "a";
    }

    public String getUsage() {
        return "action <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to trigger an action in TTool";
    }


    /*public  String executeCommand(String command, Interpreter interpreter) {
        int index = command.indexOf(" ");
        String nextCommand;
        String args;

        if (index == -1) {
            nextCommand = command;
            args = "";
        } else {
            nextCommand = command.substring(0, index);
            args = command.substring(index+1, command.length());
        }


        // Analyzing next command
        for(Command c: subcommands) {
            if ((c.getCommand().compareTo(nextCommand) == 0) || (c.getCommand().compareTo(nextCommand) == 0)) {
                return c.executeCommand(args, interpreter);
            }
        }


        String error = Interpreter.UNKNOWN_NEXT_COMMAND + nextCommand;
        return error;

    }*/

    public void fillSubCommands() {
        // Start
        Command start = new Command() {
            public String getCommand() { return START; }
            public String getShortCommand() { return "s"; }
            public String getDescription() { return "Starting the graphical interface of TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_ALREADY_STARTED;
                }
                TraceManager.addDev("Loading images");
                IconManager.loadImg();

                TraceManager.addDev("Preparing plugins");
                PluginManager.pluginManager = new PluginManager();
                PluginManager.pluginManager.preparePlugins(ConfigurationTTool.PLUGIN_PATH, ConfigurationTTool.PLUGIN, ConfigurationTTool.PLUGIN_PKG);


                TraceManager.addDev("Starting launcher");
                Thread t = new Thread(new RTLLauncher());
                t.start();

                TraceManager.addDev("Creating main window");
                interpreter.mgui = new MainGUI(false, true, true, true,
                        true, true, true, true, true, true,
                        true, false, true);
                interpreter.mgui.build();
                interpreter.mgui.start(interpreter.showWindow());

                interpreter.setTToolStarted(true);

                return null;
            }
        };

        // Open
        Command open = new Command() {
            public String getCommand() { return OPEN; }
            public String getShortCommand() { return "o"; }
            public String getDescription() { return "Opening a model in TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.openProjectFromFile(new File(command));

                return null;
            }
        };

        // Quit
        Command quit = new Command() {
            public String getCommand() { return QUIT; }
            public String getShortCommand() { return "q"; }
            public String getDescription() { return "Closing the graphical interface of TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }
                interpreter.mgui.quitApplication(false, false);
                return null;
            }
        };

        // Check syntax
        Command checkSyntax = new Command() {
            public String getCommand() { return CHECKSYNTAX; }
            public String getShortCommand() { return "cs"; }
            public String getDescription() { return "Checking the syntax of an opened model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();
                if (tp == null) {
                    return "No opened panel";
                }

                interpreter.mgui.checkModelingSyntax(tp, true);
                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusInteractiveSimulation = new Command() {
            public String getCommand() { return DIPLO_INTERACTIVE_SIMULATION; }
            public String getShortCommand() { return "dis"; }
            public String getDescription() { return "Interactive simulation of a DIPLODOCUS model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (!(tp instanceof TMLArchiPanel)) {
                    return "Current diagram is invalid for interactive simulationn";
                }

                if (interpreter.mgui.checkModelingSyntax(tp, true)) {
                    interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ANIMATION);
                }

                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusFormalVerification = new Command() {
            public String getCommand() { return DIPLO_FORMAL_VERIFICATION; }
            public String getShortCommand() { return "dots"; }
            public String getDescription() { return "Formal verification of a DIPLODOCUS mapping model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (!(tp instanceof TMLArchiPanel)) {
                    return "Current diagram is invalid for formal verification";
                }

                if (interpreter.mgui.checkModelingSyntax(tp, true)) {
                    interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ONE_TRACE);
                }

                return null;
            }
        };

        // Diplodocus interactive simulation
        Command diplodocusOneTraceSimulation = new Command() {
            public String getCommand() { return DIPLO_ONETRACE_SIMULATION; }
            public String getShortCommand() { return "dots"; }
            public String getDescription() { return "One-trace simulation of a DIPLODOCUS model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (!(tp instanceof TMLArchiPanel)) {
                    return "Current diagram is invalid for one-trace simulation";
                }


                if (interpreter.mgui.checkModelingSyntax(interpreter.mgui.getCurrentTURTLEPanel(), true)) {
                    interpreter.mgui.generateSystemC(JDialogSystemCGeneration.ONE_TRACE);
                }

                return null;
            }
        };

        // Diplodocus generate TML
        Command diplodocusGenerateTML = new Command() {
            public String getCommand() { return DIPLO_GENERATE_TML; }
            public String getShortCommand() { return "dgtml"; }
            public String getDescription() { return "Generate the TML code of a diplodocus model"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if ((!(tp instanceof TMLArchiPanel)) &&  (!(tp instanceof TMLComponentDesignPanel))){
                    return "Current diagram is invalid for generating TML";
                }

                if (interpreter.mgui.checkModelingSyntax(interpreter.mgui.getCurrentTURTLEPanel(), true)) {
                    String tmp = interpreter.mgui.generateTMLTxt();
                    if (tmp == null) {
                        return "TML generation failed";
                    } else {
                        return "TML spec generated in: " + tmp;
                    }

                }

                return null;
            }
        };

        // Diplodocus uppaal
        Command diplodocusUPPAAL = new Command() {
            public String getCommand() { return DIPLO_UPPAAL; }
            public String getShortCommand() { return "du"; }
            public String getDescription() { return "Use UPPAAL for formal verification of a DIPLO app"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                TURTLEPanel tp = interpreter.mgui.getCurrentTURTLEPanel();

                if (tp == null) {
                    return "No diagram to verify";
                }

                if (!(tp instanceof TMLComponentDesignPanel)) {
                    return "Current diagram is invalid for UPPAAL verification";
                }

                if (interpreter.mgui.checkModelingSyntax(tp, true)) {
                    String tmp = interpreter.mgui.generateTMLTxt();
                    boolean result = interpreter.mgui.gtm.generateUPPAALFromTML(SpecConfigTTool.UPPAALCodeDirectory, false, 8, false);

                    if (!result) {
                        interpreter.print("UPPAAL verification failed");
                    } else {
                        interpreter.print("UPPAAL verification done");
                    }

                }

                return null;
            }
        };

        // Navigation
        Command navigateLeftPanel = new Command() {
            public String getCommand() { return NAVIGATE_LEFT_PANEL; }
            public String getShortCommand() { return "nlf"; }
            public String getDescription() { return "Select the edition panel on the left"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.selectPanelOnTheLeft();
                return null;
            }
        };

        Command movePanelToTheLeftPanel = new Command() {
            public String getCommand() { return NAVIGATE_PANEL_TO_LEFT; }
            public String getShortCommand() { return "nptf"; }
            public String getDescription() { return "Select the edition panel on the left"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                interpreter.mgui.requestMoveLeftTab(interpreter.mgui.getCurrentJTabbedPane().getSelectedIndex());

                return null;
            }
        };

        Command generic = new Command() {
            public String getCommand() { return GENERIC; }
            public String getShortCommand() { return "g"; }
            public String getDescription() { return "Apply a generic function of TTool"; }

            public  String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                ActionPerformer.actionPerformed(interpreter.mgui, null, command.trim(), null);

                return null;
            }
        };


        subcommands.add(start);
        subcommands.add(open);
        subcommands.add(quit);
        subcommands.add(checkSyntax);
        subcommands.add(diplodocusInteractiveSimulation);
        subcommands.add(diplodocusFormalVerification);
        subcommands.add(diplodocusOneTraceSimulation);
        subcommands.add(diplodocusGenerateTML);
        subcommands.add(diplodocusUPPAAL);

        subcommands.add(navigateLeftPanel);

        subcommands.add(generic);

    }
}
