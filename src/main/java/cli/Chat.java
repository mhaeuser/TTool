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

import avatartranslator.*;
import avatartranslator.directsimulation.AvatarSpecificationSimulation;
import avatartranslator.modelchecker.AvatarModelChecker;
import avatartranslator.modelchecker.CounterexampleQueryReport;
import avatartranslator.modelchecker.SpecificationActionLoop;
import avatartranslator.modelcheckervalidator.ModelCheckerValidator;
import avatartranslator.mutation.ApplyMutationException;
import avatartranslator.mutation.AvatarMutation;
import avatartranslator.mutation.ParseMutationException;
import common.ConfigurationTTool;
import common.SpecConfigTTool;
import graph.RG;
import launcher.RTLLauncher;
import launcher.RshClient;
import launcher.RshClientReader;
import myutil.*;
import proverifspec.ProVerifOutputAnalyzer;
import proverifspec.ProVerifOutputListener;
import proverifspec.ProVerifQueryAuthResult;
import proverifspec.ProVerifQueryResult;
import tmltranslator.TMLMapping;
import tmltranslator.TMLMappingTextSpecification;
import tmltranslator.TMLModeling;
import tmltranslator.TMLTextSpecification;
import ui.MainGUI;
import ui.avatarinteractivesimulation.AvatarInteractiveSimulationActions;
import ui.avatarinteractivesimulation.JFrameAvatarInteractiveSimulation;
import ui.util.IconManager;
import ui.window.JDialogProverifVerification;
import ui.window.JDialogSystemCGeneration;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * Class Action
 * Creation: 05/10/2018
 * Version 2.0 05/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Chat extends Command  {
    // Action commands
    private final static String ASK = "ask";
    private final static String SET_KEY = "setaikey";

    private String key;
    private AIInterface aiinterface;

    public Chat() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "chat";
    }

    public String getShortCommand() {
        return "c";
    }

    public String getUsage() {
        return "chat <subcommand> <options>";
    }

    public String getDescription() {
        return "AI based on ChatGPT to support System Engineering. Secret key must be configured";
    }


    public void fillSubCommands() {
        // Start
        Command ask = new Command() {
            public String getCommand() {
                return ASK;
            }

            public String getShortCommand() {
                return "a";
            }

            public String getDescription() {
                return "Asking a general question to ChatGPT";
            }

            public String executeCommand(String command, Interpreter interpreter) {

                if (key == null) {
                    return "Key must be set first";
                }

                String[] commands = command.split(" ");

                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                String text = commands[commands.length-1];

                if (aiinterface == null) {
                    aiinterface = new AIInterface();
                    aiinterface.setURL(AIInterface.URL_OPENAI_COMPLETION);
                    aiinterface.setAIModel(AIInterface.MODEL_GPT_35);
                    aiinterface.setKey(key);
                }

                StringBuilder response;
                try {
                    response = aiinterface.chat(text);
                } catch (AIInterfaceException aiie) {
                    return aiie.getMessage();
                }

                System.out.println(response);

                return null;
            }
        };

        Command setKey = new Command() {
            public String getCommand() {
                return SET_KEY;
            }

            public String getShortCommand() {
                return "sk";
            }

            public String getDescription() {
                return "AAdding communication key to ChatGPT";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                String[] commands = command.split(" ");

                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                key = commands[commands.length-1];

                return null;
            }
        };

        Command setModel = new Command() {
            public String getCommand() {
                return SET_KEY;
            }

            public String getShortCommand() {
                return "sk";
            }

            public String getDescription() {
                return "AAdding communication key to ChatGPT";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                String[] commands = command.split(" ");

                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                key = commands[commands.length-1];

                return null;
            }
        };



        addAndSortSubcommand(ask);
        addAndSortSubcommand(setKey);

    }



}
