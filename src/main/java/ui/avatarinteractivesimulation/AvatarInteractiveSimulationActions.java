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


package ui.avatarinteractivesimulation;

import ui.util.IconManager;
import ui.TAction;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class AvatarInteractiveSimulationActions
 * <p>
 * Creation: 21/01/2011
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/01/2011
 * @see ui.TGComponent
 */
public class AvatarInteractiveSimulationActions extends AbstractAction {
    // Actions
    public static final int ACT_RUN_SIMU = 0;
    public static final int ACT_STOP_SIMU = 1;
    public static final int ACT_RESET_SIMU = 2;
    public static final int ACT_RUN_X_TIME_UNITS = 6;
    public static final int ACT_RUN_TO_TIME = 7;
    public static final int ACT_RUN_X_TRANSACTIONS = 8;
    public static final int ACT_RUN_X_COMMANDS = 9;
    public static final int ACT_RUN_SIMU_MAX_TRANS = 38;

    public static final int ACT_RUN_EXPLORATION = 20;
    public static final int ACT_RUN_TO_NEXT_BUS_TRANSFER = 21;
    public static final int ACT_BACK_ONE = 22;
    public static final int ACT_RUN_UNTIL_TASK_EXECUTES = 23;
    public static final int ACT_RUN_UNTIL_MEMORY_ACCESS = 24;
    public static final int ACT_RUN_UNTIL_CHANNEL_ACCESS = 25;

    public static final int ACT_SAVE_SD_PNG = 10;
    public static final int ACT_SAVE_SVG = 11;
    public static final int ACT_SAVE_TXT = 12;
    public static final int ACT_SAVE_CSV = 37;

    public static final int ACT_PRINT_BENCHMARK = 26;
    public static final int ACT_SAVE_BENCHMARK = 27;

    public static final int ACT_SAVE_STATE = 13;
    public static final int ACT_RESTORE_STATE = 14;

    public static final int ACT_START_ALL = 3;
    public static final int ACT_STOP_ALL = 4;
    public static final int ACT_STOP_AND_CLOSE_ALL = 5;

    public static final int ACT_UPDATE_VARIABLES = 15;
    public static final int ACT_UPDATE_CPUS = 16;
    public static final int ACT_UPDATE_MEMS = 17;
    public static final int ACT_UPDATE_BUS = 18;
    public static final int ACT_UPDATE_TASKS = 19;

    public static final int ACT_PRINT_CPUS = 28;
    public static final int ACT_PRINT_BUS = 29;

    public static final int ACT_ZOOM_IN = 30;
    public static final int ACT_ZOOM_OUT = 31;

    public static final int ACT_DELETE_ASYNC_MSG = 32;
    public static final int ACT_UP_ASYNC_MSG = 33;
    public static final int ACT_DOWN_ASYNC_MSG = 34;
    public static final int ACT_ADD_LATENCY = 35;
    public static final int ACT_REMOVE_ALL_TRANS = 36;

    public static final int NB_ACTION = 39;


    private static final TAction[] actions = new TAction[NB_ACTION];

    private EventListenerList listeners;

    public static final String JLF_IMAGE_DIR = "";

    public static final String LARGE_ICON = "LargeIcon";


    public AvatarInteractiveSimulationActions(int id) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return;
        }

        putValue(Action.NAME, actions[id].NAME);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, actions[id].SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, actions[id].LONG_DESCRIPTION);
        //putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
        if (actions[id].MNEMONIC_KEY != 0) {
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].MNEMONIC_KEY, java.awt.event.InputEvent.CTRL_MASK));
        }
        putValue(Action.ACTION_COMMAND_KEY, actions[id].ACTION_COMMAND_KEY);

    }

    public void setName(int index, String name) {
        actions[index].NAME = name;
        putValue(Action.NAME, actions[index].NAME);
    }

    public void init() {
        actions[ACT_RUN_SIMU] = new TAction("run-simu", "Run simulation", IconManager.imgic1302, IconManager.imgic1302, "Run simulation", "Run simulation until next breakpoint. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_SIMU_MAX_TRANS] = new TAction("run-simu-max-trans", "Run simulation to next breakpoint or max number of trans", IconManager.imgic1302, IconManager.imgic1302, "Run simulation to next breakpoint or max number of trans", "Run simulation until next breakpoint or max transactions are executed. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_STOP_SIMU] = new TAction("stop-simu", "Stop simulation", IconManager.imgic55, IconManager.imgic55, "Stop simulation", "Stop simulation. Works only if the simulator is \"busy\"", 'S');
        actions[ACT_RESET_SIMU] = new TAction("reset-simu", "Reset simulation", IconManager.imgic45, IconManager.imgic45, "Reset simulation", "Reset simulation", 'T');
        actions[ACT_RUN_X_TIME_UNITS] = new TAction("run-x-time-units", "Run x time units", IconManager.imgic1300, IconManager.imgic1300, "Run x time units", "Run simulation for x units of time. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_TO_TIME] = new TAction("run-to-time", "Run to time x", IconManager.imgic1304, IconManager.imgic1304, "Run to time x", "Run simulation until time x is reached. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_X_TRANSACTIONS] = new TAction("run-x-transactions", "Run x transactions", IconManager.imgic1306, IconManager.imgic1306, "Run x transactions", "Run simulation for x transactions. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_X_COMMANDS] = new TAction("run-x-commands", "x Step-by-Step", IconManager.imgic1330, IconManager.imgic1330, "x Step-by-Step", "Run simulation for x commands. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_REMOVE_ALL_TRANS] = new TAction("remove-all-trans","Remove all transactions",IconManager.imgic337, IconManager.imgic337,"Remove all transactions", "Remove all the transactions stored in transaction list", 'R');

        actions[ACT_BACK_ONE] = new TAction("back-one", "Back one transaction", IconManager.imgic47, IconManager.imgic47, "Back one transaction", "Go one transaction backward", 'B');


        actions[ACT_RUN_EXPLORATION] = new TAction("run-exploration", "Run exploration", IconManager.imgic1326, IconManager.imgic1326, "Run exploration", "Explore branches of the simulation", 'R');
        actions[ACT_RUN_TO_NEXT_BUS_TRANSFER] = new TAction("run-until-bus-transfer", "Run until bus transfer", IconManager.imgic1316, IconManager.imgic1316, "Run until transfer on bus", "Run until a transfer on the indicated bus is performed. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_UNTIL_TASK_EXECUTES] = new TAction("run-to-task", "Run until a task executes", IconManager.imgic1318, IconManager.imgic1318, "Run until a task executes", "Run until a task executes. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_UNTIL_MEMORY_ACCESS] = new TAction("run-to-memory-accessd", "Run until a memory access is performed", IconManager.imgic1322, IconManager.imgic1322, "Run until a memory access is performed", "Run simulation until a memory access is performed on selected memory. Works only if the simulator is \"ready\"", 'R');
        actions[ACT_RUN_UNTIL_CHANNEL_ACCESS] = new TAction("run--to-channel-access", "Run until a channel is accessed", IconManager.imgic1324, IconManager.imgic1324, "Run until a channel is accessed", "Run until a channel is accessed. Works only if the simulator is \"ready\"", 'R');

        actions[ACT_SAVE_SD_PNG] = new TAction("save-sd-png", "Save SD trace in PNG format", IconManager.imgic5104, IconManager.imgic5104, "Save SD trace in PNG format", "Save SD trace in PNG format", '0');
        actions[ACT_SAVE_SVG] = new TAction("save-svg", "Save trace in SVG format", IconManager.imgic1328, IconManager.imgic1328, "Save trace in SVG format", "Save trace in SVG format", 'R');
        actions[ACT_SAVE_TXT] = new TAction("save-txt", "Save trace in TXT format", IconManager.imgic1314, IconManager.imgic1314, "Save trace in TXT format", "Save trace in TXT format", 'R');
        actions[ACT_SAVE_CSV] = new TAction("save-csv", "Save trace in CSV format", IconManager.imgic1334, IconManager.imgic1334, "Save trace in " +
                "CSV " +
                "format", "Save trace in CSV format", 'V');

        actions[ACT_SAVE_STATE] = new TAction("save-state", "Save simulation state in File", IconManager.imgic341, IconManager.imgic341, "Save simulation state in File", "Save simulation state in File", 'R');
        actions[ACT_RESTORE_STATE] = new TAction("restore-state", "Restore simulation state from File", IconManager.imgic339, IconManager.imgic339, "Restore simulation state from File", "Restore simulation state from File", 'R');

        actions[ACT_START_ALL] = new TAction("start-all", "Connect to simulator", IconManager.imgic53, IconManager.imgic53, "Connect", "Start the server - if it is not yet running - and connect to it", 'C');
        actions[ACT_STOP_ALL] = new TAction("stop-all", "Quit simulation window", IconManager.imgic27, IconManager.imgic27, "Quit simulation window", "Quit the simulation window without terminating the simulation", 'Q');
        actions[ACT_STOP_AND_CLOSE_ALL] = new TAction("stop-and-close-all", "Terminate simulation and quit", IconManager.imgic27, IconManager.imgic27, "Terminate simulation and quit", "Terminate the simulation and quit the simulation window", 'T');

        actions[ACT_UPDATE_VARIABLES] = new TAction("update-variables", "Update variables", IconManager.imgic75, IconManager.imgic75, "Update variables", "Update variables", 'R');
        actions[ACT_UPDATE_CPUS] = new TAction("update-cpus", "Update CPU info", IconManager.imgic75, IconManager.imgic75, "Update CPU information", "Update information on CPUs", 'R');
        actions[ACT_UPDATE_MEMS] = new TAction("update-mems", "Update Memories information", IconManager.imgic75, IconManager.imgic75, "Update Memories information", "Update information on Memories", 'R');
        actions[ACT_UPDATE_BUS] = new TAction("update-bus", "Update bus info", IconManager.imgic75, IconManager.imgic75, "Update bus information", "Update information on busses", 'R');
        actions[ACT_UPDATE_TASKS] = new TAction("update-tasks", "Update task information", IconManager.imgic75, IconManager.imgic75, "Update task information", "Update information on tasks", 'R');

        actions[ACT_PRINT_CPUS] = new TAction("print-cpus", "Print CPU info", IconManager.imgic75, IconManager.imgic75, "Print CPU information", "Print information on CPUs", 'R');
        actions[ACT_PRINT_BUS] = new TAction("print-bus", "Print Bus info", IconManager.imgic75, IconManager.imgic75, "Print Bus information", "Print information on Busses", 'R');

        actions[ACT_PRINT_BENCHMARK] = new TAction("print-benchmark", "Print benchmark", IconManager.imgic29, IconManager.imgic29, "Print benchmark", "Print benchmark at simulator side", 'R');
        actions[ACT_SAVE_BENCHMARK] = new TAction("save-benchmark", "Save benchmark", IconManager.imgic25, IconManager.imgic25, "Save benchmark", "Save benchmark at simulator side", 'R');

        actions[ACT_ZOOM_IN] = new TAction("zoommore-command", "Zoom +", IconManager.imgic317, IconManager.imgic317, "Zoom +", "Zoom +", '0');
        actions[ACT_ZOOM_OUT] = new TAction("zoomless-command", "Zoom -", IconManager.imgic315, IconManager.imgic315, "Zoom -", "Zoom -", '0');

        actions[ACT_DELETE_ASYNC_MSG] = new TAction("delete-async-command", "Delete", IconManager.imgic336, IconManager.imgic336, "Delete msg +", "Delete the selected message", '0');
        actions[ACT_UP_ASYNC_MSG] = new TAction("up-async-command", "Up", IconManager.imgic78, IconManager.imgic78, "Up msg", "Put a async msg closer to the FIFO exit", '0');
        actions[ACT_DOWN_ASYNC_MSG] = new TAction("up-async-command", "Down", IconManager.imgic79, IconManager.imgic79, "Down msg", "Put a async msg further from the FIFO exit", '0');
        actions[ACT_ADD_LATENCY] = new TAction("add-latency-command", "Add latency", IconManager.imgic75, IconManager.imgic75, "Add latency", "Add latency checkpoint", '0');
    }

    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

    public String getShortDescription() {
        return (String) getValue(Action.SHORT_DESCRIPTION);
    }

    public String getLongDescription() {
        return (String) getValue(Action.LONG_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt) {
        //
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();

            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
                    (String) getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length - 2; i += 2) {
                ((ActionListener) listenerList[i + 1]).actionPerformed(e);
            }
        }
    }

    public void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, l);
    }
}
