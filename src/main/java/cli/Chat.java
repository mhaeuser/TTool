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


import ai.AIChatData;
import ai.AIFeedback;
import ai.AIInteract;
import myutil.*;

import javax.swing.*;
import java.util.ArrayList;
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
    private final static String CLEAR_KNOWLEDGE = "clear-knowledge";
    private final static String PRINT_KNOWLEDGE = "print-knowledge";
    private final static String SET_USER_KNOWLEDGE = "set-user-knowledge";
    private final static String SET_ASSISTANT_KNOWLEDGE = "set-assistant-knowledge";
    private final static String ADD_KNOWLEDGE = "add-knowledge";
    private final static String QUERY = "query";

    private String key;
    private AIInterface aiinterface;

    private String userKnowledge;
    private String assistantKnowledge;

    private AIChatData chatData;
    private CLIAIFeedback feedback;


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
        return "AI based on ChatGPT to support System Engineering. Secret key must be configured. For each question, the question and the answer " +
                "are stored in the knowledge Database, reused for eash question. To clear the database, use the clear command";
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

                //String text = commands[commands.length-1];

                //System.out.println("Sending text: " + command);
                String text = command;

                if (aiinterface == null) {
                    makeAIInterface();
                }

                String response;
                try {
                    response = aiinterface.chat(text, true, true);
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
                return "Adding communication key to ChatGPT";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                String[] commands = command.split(" ");

                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                key = commands[commands.length-1];

                if (aiinterface != null) {
                    aiinterface.setKey(key);
                }

                return null;
            }
        };

        Command clear = new Command() {
            public String getCommand() {
                return CLEAR_KNOWLEDGE;
            }

            public String getShortCommand() {
                return "cl";
            }

            public String getDescription() {
                return "Clear knowledge";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                String[] commands = command.split(" ");

                if (commands.length < 1) {
                    return Interpreter.BAD;
                }

                if (aiinterface == null) {
                    return "knowledge canot be clear before a first question has been asked";
                }

                aiinterface.clearKnowledge();

                return null;
            }
        };

        Command pk = new Command() {
            public String getCommand() {
                return PRINT_KNOWLEDGE;
            }

            public String getShortCommand() {
                return "pk";
            }

            public String getDescription() {
                return "print current knowledge";
            }

            public String executeCommand(String command, Interpreter interpreter) {

                if (aiinterface == null) {
                    makeAIInterface();
                }

                ArrayList<AIInterface.AIKnowledge> db = aiinterface.getKnowledge();
                if ((db == null) || (db.size() ==0)) {
                    return "no knowledge";
                }

                for (AIInterface.AIKnowledge aik: aiinterface.getKnowledge()) {
                    System.out.println("user:\t\t" + aik.userKnowledge.substring(0, 30));
                    System.out.println("assistant:\t" + aik.assistantKnowledge.substring(0, 30));
                }

                return null;
            }
        };

        Command setUserKnowledge = new Command() {
            public String getCommand() {
                return SET_USER_KNOWLEDGE;
            }

            public String getShortCommand() {
                return "suk";
            }

            public String getDescription() {
                return "Set a user knowledge. Once a user and an assistant knowledge have been set, you can add a new knowledge";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (aiinterface == null) {
                    makeAIInterface();
                }

                if (command.length() == 0) {
                    return "Empty user knowledge";
                }

                userKnowledge = command;

                return null;
            }
        };

        Command setAssistantKnowledge = new Command() {
            public String getCommand() {
                return SET_ASSISTANT_KNOWLEDGE;
            }

            public String getShortCommand() {
                return "sak";
            }

            public String getDescription() {
                return "Set an assistant knowledge. Once a user and an assistant knowledge have been set, you can add a new knowledge";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (aiinterface == null) {
                    makeAIInterface();
                }

                if (command.length() == 0) {
                    return "Empty assistant knowledge";
                }

                assistantKnowledge = command;

                return null;
            }
        };

        Command addKnowledge = new Command() {
            public String getCommand() {
                return ADD_KNOWLEDGE;
            }

            public String getShortCommand() {
                return "ak";
            }

            public String getDescription() {
                return "Add a new knowledge once user and assistant knowledge have been set";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (aiinterface == null) {
                    makeAIInterface();
                }

                if (userKnowledge == null) {
                    return "User knowledge must be set first";
                }

                if (assistantKnowledge == null) {
                    return "Assistant knowledge must be set first";
                }

                aiinterface.addKnowledge(userKnowledge, assistantKnowledge);
                userKnowledge = null;
                assistantKnowledge = null;

                return null;
            }
        };

        Command query = new Command() {
            public String getCommand() {
                return QUERY;
            }

            public String getShortCommand() {
                return "q";
            }

            public String getDescription() {
                String listOfQuery = "";
                for(int i=0; i<AIInteract.SHORT_REQUEST_TYPES.length; i++) {
                    if (i > 0) {
                        listOfQuery += ", ";
                    }
                    listOfQuery += "\"" + AIInteract.SHORT_REQUEST_TYPES[i] + "\"" + " (" + AIInteract.REQUEST_TYPES[i] + ")";
                }
                return "query <QUERY_TYPE> Text of the query. QUERY_TYPE can be: " + listOfQuery;
            }

            public String executeCommand(String command, Interpreter interpreter) {
                String[] commands = command.trim().split(" ");
                if (commands.length < 2) {
                    return Interpreter.BAD;
                }

                String type = commands[0];
                int length = type.length();
                String text = command.substring(length+1).trim();

                // Testing the type
                int indexOfType = AIInteract.getIndexOfShortType(type);

                if (indexOfType == -1) {
                    return Interpreter.BAD_TYPE;
                }

                if (aiinterface == null) {
                    makeAIInterface();
                }

                if (!aiinterface.hasKey()) {
                    return AIInterface.NO_KEY;
                }

                if (feedback == null) {
                    feedback = new CLIAIFeedback(interpreter);
                }

                if (chatData == null) {
                    chatData = new AIChatData(aiinterface);
                    chatData.feedback = feedback;
                }

                AIInteract aii = AIInteract.getAIFromShortType(indexOfType, chatData);
                if (aii == null) {
                    return Interpreter.NO_AI_INTERFACE;
                }
                feedback.running = true;
                aii.makeRequest(text);

                int cpt = 0;

                interpreter.print("Computing");
                while(feedback.running && cpt < 1000) {
                    cpt ++;
                    try {
                        Thread.currentThread().sleep(100);
                        if (cpt % 5 == 0) {
                            interpreter.print("./" + feedback.running + " ");
                        }
                    } catch (Exception e) {
                    }
                }

                interpreter.print("Answer: " + chatData.lastAnswer);

                return null;
            }
        };


        addAndSortSubcommand(ask);
        addAndSortSubcommand(setKey);
        addAndSortSubcommand(clear);
        addAndSortSubcommand(pk);
        addAndSortSubcommand(setUserKnowledge);
        addAndSortSubcommand(setAssistantKnowledge);
        addAndSortSubcommand(addKnowledge);
        addAndSortSubcommand(query);

    }

    private void makeAIInterface() {
        aiinterface = new AIInterface();
        aiinterface.setURL(AIInterface.URL_OPENAI_COMPLETION);
        aiinterface.setAIModel(AIInterface.MODEL_GPT_35);
        aiinterface.setKey(key);
    }

    private class CLIAIFeedback implements AIFeedback {

        private Interpreter interpreter;
        private boolean running = false;

        public CLIAIFeedback(Interpreter _interpreter) {
            interpreter = _interpreter;
        }

        public void addInformation(String text) {
            interpreter.print("chat info: " + text);
        }
        public void addError(String text) {
            interpreter.print("chat error: " + text);
        }

        public void addToChat(String data, boolean user) {
            String s = "TTool: ";
            if (!user) {
                s = "AI: ";
            }
            interpreter.print(s + data);

        }

        //public void setNewBlockDiagram(AvatarSpecification avspec);

        public void setAnswerText(String text) {}
        public void clear() {}

        public void setRunning(boolean b) {
            running = b;
        }
    }



}
