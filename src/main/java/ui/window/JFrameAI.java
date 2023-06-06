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


package ui.window;

import ai.AIChatData;
import ai.AIFeedback;
import ai.AIInteract;
import ai.AISysMLV2DiagramContent;
import avatartranslator.AvatarSpecification;
import avatartranslator.mutation.ApplyMutationException;
import avatartranslator.mutation.AvatarMutation;
import avatartranslator.mutation.ParseMutationException;
import avatartranslator.tosysmlv2.AVATAR2SysMLV2;
import cli.Interpreter;
import common.ConfigurationTTool;
import help.HelpEntry;
import help.HelpManager;
import myutil.AIInterface;
import myutil.AIInterfaceException;
import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.avatarbd.AvatarBDPanel;
import ui.avatarrd.AvatarRDPanel;
import ui.avatarrd.AvatarRDRequirement;
import ui.util.IconManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class JFrameSysMLV2Text
 * Creation: 13/07/2021
 * version 1.0 13/07/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameAI extends JFrame implements ActionListener {


    private static String[] POSSIBLE_ACTIONS = {"Chat", "Identify requirements from a specification", "Classify requirements from a requirement " +
            "diagram", "Identify properties from a design", "Identify system blocks from a specification", "Identify software blocks from a " +
            "specification", "A(I)MULET"};

    private static String[] AIInteractClass = {"AIChat", "AIReqIdent", "AIReqClassification", "AIDesignPropertyIdentification", "AIBlock", "AIChat", "AIChat"};

    protected JComboBox<String> listOfPossibleActions;


    private String QUESTION_IDENTIFY_PROPERTIES = "List properties of the following SysML V2 specification.";



    private MainGUI mgui;
    private JTextPane question, console;
    private JTabbedPane answerPane;
    private ArrayList<ChatData> chats;

    private int currentChatIndex = -1;

    private JMenuBar menuBar;
    private JMenu help;
    private JPopupMenu helpPopup;

    private boolean go = false;
    private boolean isAMULETFirstLoop = true;

    private HashMap<Integer, ImageIcon> rotatedI = new HashMap<>();
    private JButton buttonClose, buttonStart, buttonApplyResponse;

    public JFrameAI(String title, MainGUI _mgui) {
        super(title);
        mgui = _mgui;
        chats = new ArrayList<>();
        makeComponents();

    }

    public void setIcon(ChatData _data, Icon newIcon) {
        int index = chats.indexOf(_data);
        answerPane.setIconAt(index, newIcon);
    }

    public void makeComponents() {

        helpPopup = new JPopupMenu();
        helpPopup.add(new JLabel(IconManager.imgic7009));
        helpPopup.setPreferredSize(new Dimension(600, 900));
        menuBar = new JMenuBar();
        menuBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        help = new JMenu("?");
        menuBar.add(help);
        setJMenuBar(menuBar);

        help.setPreferredSize(new Dimension(30, 30));
        help.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                help();
            }
        });
        helpPopup.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeJlabel");
        helpPopup.getActionMap().put("closeJlabel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                helpPopup.setVisible(false);
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        setIconImage(IconManager.imgic152.getImage());

        // Top panel : options
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        panelTop.setBorder(new javax.swing.border.TitledBorder("Options"));

        listOfPossibleActions = new JComboBox<>(POSSIBLE_ACTIONS);
        panelTop.add(listOfPossibleActions, BorderLayout.CENTER);
        listOfPossibleActions.addActionListener(this);

        framePanel.add(panelTop, BorderLayout.NORTH);


        // Middle panel

        /*jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);*/

        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(new javax.swing.border.TitledBorder("Question"));
        questionPanel.setPreferredSize(new Dimension(450, 550));
        question = new JTextPane();
        //question.setPreferredSize(new Dimension(400, 500));
        setOptionsJTextPane(question, true);
        JScrollPane scrollPane = new JScrollPane(question);
        scrollPane.setPreferredSize(new Dimension(420, 500));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        questionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel answerPanel = new JPanel(new BorderLayout());
        answerPanel.setBorder(new javax.swing.border.TitledBorder("Answer"));
        answerPanel.setPreferredSize(new Dimension(550, 550));
        answerPane = new JTabbedPane();
        answerPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                enableDisableActions();
                // Perform your action here
            }
        });
        ;
        answerPane.addMouseListener(new JFrameAI.PopupListener(this));
        answerPane.setPreferredSize(new Dimension(500, 500));
        addChat(getChatName());
        answerPanel.add(answerPane, BorderLayout.CENTER);

        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setBorder(new javax.swing.border.TitledBorder("Console"));
        console = new JTextPane();
        //console.setPreferredSize(new Dimension(900, 150));
        inform("Select options and click on \"start\"\n");
        scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(900, 150));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consolePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel intermediate = new JPanel();
        intermediate.add(questionPanel);
        intermediate.add(answerPanel);
        middlePanel.add(intermediate, BorderLayout.CENTER);
        middlePanel.add(consolePanel, BorderLayout.SOUTH);

        framePanel.add(middlePanel, BorderLayout.CENTER);


        // Lower panel

        buttonClose = new JButton("Close", IconManager.imgic27);
        buttonClose.addActionListener(this);

        buttonStart = new JButton("Start", IconManager.imgic53);
        buttonStart.addActionListener(this);

        buttonApplyResponse = new JButton("Apply response", IconManager.imgic75);
        buttonApplyResponse.addActionListener(this);

        JPanel lowPart = new JPanel(new BorderLayout());
        JPanel jp = new JPanel();
        jp.add(buttonClose);
        jp.add(buttonStart);
        jp.add(buttonApplyResponse);
        lowPart.add(jp, BorderLayout.CENTER);
        framePanel.add(lowPart, BorderLayout.SOUTH);

        enableDisableActions();


        pack();
    }

    private void addChat(String nameOfChat) {

        ChatData data = new ChatData();
        chats.add(data);

        //answer.setPreferredSize(new Dimension(400, 500));
        //setOptionsJTextPane(answer, false);
        JScrollPane scrollPane = new JScrollPane(data.answer);
        scrollPane.setMinimumSize(new Dimension(400, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        answerPane.add(scrollPane);
        answerPane.setTitleAt(answerPane.getTabCount() - 1, nameOfChat);
        answerPane.setIconAt(answerPane.getTabCount() - 1, IconManager.imgic154);

    }

    public void actionPerformed(ActionEvent evt) {

        TraceManager.addDev("Action performed");

        if (evt.getSource() == buttonClose) {
            close();
        } else if (evt.getSource() == buttonStart) {
            TraceManager.addDev("start!");
            start();
        } else if (evt.getSource() == buttonApplyResponse) {
            applyResponse();
        } else if (evt.getSource() == listOfPossibleActions) {
            enableDisableActions();
        }
    }

    private String getChatName() {
        if (!mgui.picoZebre) {
            return "Chat";
        }
        String[] names = {"pico", "zebre", "pingouin", "chien", "minou", "kitty", "chaton", "whatsapp", "Luke Skywalker",
                "macareux", "ours", "italien", "paris-brest", "belle-mère", "apéro (l'abus d'alcool est dangereux pour la santé)",
                "carpe", "crocodile", "psychologue", "dr emacs", "3615-TTool", "100 balles et 1 mars",
                "opéra (l’abus d’Alcôve est dangereux pour la santé)", "chapon", "perroquet", "chameau volant", "Alice", "Oasis", "ATC RAK"};
        int x = (int) (Math.random() * names.length);
        return names[x];
    }

    private void close() {
        for (ChatData data : chats) {
            data.stopAnimation();
        }
        dispose();
    }

    private void start() {
        currentChatIndex = answerPane.getSelectedIndex();
        ChatData selected = selectedChat();

        if (selected.makeAIChatData()) {
            String selectedClass = AIInteractClass[listOfPossibleActions.getSelectedIndex()];
            selected.aiInteract = ai.AIInteract.getInstance("ai." + selectedClass, selected.aiChatData);

            if (selected.aiInteract == null) {
                error("Unknow selected type");
                return;
            }

            if (selected.aiInteract  instanceof AISysMLV2DiagramContent) {
                TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();
                String[] validDiagrams = ((AISysMLV2DiagramContent)(selected.aiInteract)).getValidDiagrams();
                String className = tdp.getClass().getName();

                boolean found = false;
                for(String s: validDiagrams) {
                    if (className.contains(s)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    String[] exclusions = ((AISysMLV2DiagramContent)(selected.aiInteract)).getDiagramExclusions();
                    ((AISysMLV2DiagramContent)(selected.aiInteract)).setDiagramContentInSysMLV2(tdp.toSysMLV2Text(exclusions).toString());
                }
            }

            selected.aiInteract.makeRequest(question.getText());
            /*if (listOfPossibleActions.getSelectedIndex() == 0) {
                simpleChat();
            }

            if (listOfPossibleActions.getSelectedIndex() == KIND_CLASSIFY_REQUIREMENT) {
                classifyRequirements();
            }

            if (listOfPossibleActions.getSelectedIndex() == IDENTIFY_REQUIREMENT) {
                identifyRequirements();
            }

            if (listOfPossibleActions.getSelectedIndex() == IDENTIFY_PROPERTIES) {
                identifyProperties();
            }

            if (listOfPossibleActions.getSelectedIndex() == IDENTIFY_SYSTEM_BLOCKS) {
                identifySystemBlocks();
            }

            if (listOfPossibleActions.getSelectedIndex() == AMULET) {
                amuletChat();
            }*/
        } else {
            error("AI interface failed (no key has been set?)");
        }



    }

    private void applyResponse() {
        ChatData selectedChat = selectedChat();
        if (selectedChat.lastAnswer == null || selectedChat.aiChatData == null) {
            error("No answer to apply");
            return ;
        }

        TraceManager.addDev("Class of answer: " + selectedChat.aiInteract.getClass().getName());

        if (selectedChat.aiInteract instanceof ai.AIBlock) {
            applyIdentifySystemBlocks(selectedChat.aiInteract.applyAnswer(null));
        } else if (selectedChat.aiInteract instanceof ai.AIReqIdent) {
            applyRequirementIdentification();
        } else if (selectedChat.aiInteract instanceof ai.AIReqClassification) {
            applyRequirementClassification();
        } else if (selectedChat.aiInteract instanceof ai.AIDesignPropertyIdentification) {
            // nothing up to now :-)
        }

        question.setText("");
    }

    private void applyRequirementIdentification() {

        TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();
        if (!(tdp instanceof AvatarRDPanel)) {
            error("A requirement diagram must be selected first");
            return;
        }

        AvatarRDPanel rdpanel = (AvatarRDPanel) tdp;

        inform("Enhancing requirement diagram with ai answer, please wait\n");
        TraceManager.addDev("Considered JSON array: " + selectedChat().lastAnswer);
        try {
            rdpanel.loadAndUpdateFromText(selectedChat().lastAnswer);
        } catch (org.json.JSONException e) {
            TraceManager.addDev("JSON Exception: " + e.getMessage());
            inform("Answer provided by AI does not respect the JSON format necessary for TTool");
            rdpanel.repaint();
            return;
        }
        rdpanel.repaint();

        inform("Enhancing requirement diagram with ai answer: done\n");
    }

    private void applyIdentifySystemBlocks(Object input) {
        if (input == null) {
            error("Invalid specification in answer");
            return;
        }

        TraceManager.addDev("Type of input:" + input.getClass());

        if (!(input instanceof AvatarSpecification)) {
            error("Invalid answer");
            return;
        }

        mgui.drawAvatarSpecification((AvatarSpecification) input);
        inform("System blocks added to diagram from ai answer: done\n");
    }

    private void applyRequirementClassification() {
        TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();
        if (!(tdp instanceof AvatarRDPanel)) {
            error("A requirement diagram must be selected first");
            return;
        }

        AvatarRDPanel rdpanel = (AvatarRDPanel) tdp;
        inform("Enhancing requirement diagram with ai answer, please wait\n");

        ChatData selected = selectedChat();
        for (TGComponent tgc : rdpanel.getAllRequirements()) {
            AvatarRDRequirement req = (AvatarRDRequirement) tgc;
            String kind = (String)(selected.aiInteract.applyAnswer(req.getValue()));
            if (kind != null) {
                String k = JDialogRequirement.getKindFromString(kind);
                if (k != null) {
                    req.setKind(k);
                    inform("\tRequirement " + req.getValue() + " kind was set to " + k + "\n");
                } else {
                    error("Unknown kind: " + k + "\n");
                }
            }
        }
        inform("Enhancing requirement diagram: done.\n");

        rdpanel.repaint();
    }

    private void applyMutations() throws IOException {

        AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();

        if (avspec == null){
            error("AVATAR specification missing\n");
            return;
        }

        inform("Applying mutations to the model, please wait\n");

        String automatedAnswer = selectedChat().lastAnswer;
        BufferedReader buff = new BufferedReader(new StringReader(automatedAnswer));
        String line ;
        Boolean mutationApplied = false;

        while ((line=buff.readLine())!=null){
            if (line.startsWith("add ") || line.startsWith("remove ") || line.startsWith("modify ")){
                try {
                    AvatarMutation am = AvatarMutation.createFromString(line);
                    if (am != null) {
                        am.apply(avspec);
                        mutationApplied = true;
                    }
                } catch (ParseMutationException e) {
                    TraceManager.addDev("Exception in parsing mutation: " + e.getMessage());
                    error(e.getMessage());
                } catch (ApplyMutationException e) {
                    TraceManager.addDev("Exception in applying mutation: " + e.getMessage());
                    error(e.getMessage());
                }
            }
        }

        if (mutationApplied) {
            mgui.drawAvatarSpecification(avspec);
        }

    }

    private void enableDisableActions() {
        if ((answerPane != null) && (chats != null) && (buttonApplyResponse != null) && (buttonStart != null)) {
            ChatData cd = chats.get(answerPane.getSelectedIndex());
            String chat = cd.lastAnswer;
            buttonApplyResponse.setEnabled(chat != null && chat.length() > 0 && !cd.doIconRotation);
            buttonStart.setEnabled(!cd.doIconRotation);
        }
    }

    private void setOptionsJTextPane(JTextPane jta, boolean _isEditable) {
        jta.setEditable(_isEditable);
        jta.setMargin(new Insets(10, 10, 10, 10));
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
    }



    private void injectAMULETKnowledge(ChatData myChat) {
        /*myChat.aiinterface.addKnowledge("AMULET is a SysML mutation language. In AMULET, adding a block b in a block diagram is written " +
                "\"add block b\".\nRemoving a block b from a block diagram is written \"remove block b\".\n","AMULET source-code for adding a block" +
                " myBlock is \"add block myBlock\" and for removing a block myBlock is \"remove block myBlock\".");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. Adding an input signal sig in a block b is written \"add input signal " +
                "sig in " +
                "b\". If the signal conveys parameters (e.g. int i and bool x), we will write \"add input signal sig(int i, bool x) in b\".\n" +
                "Removing an input signal sig from a block b is written \"remove input signal sig in b\".\n" +
                "Adding an output signal sig in a block b is written \"add output signal sig in b\". If the signal sig conveys parameters (e.g. int" +
                " i and bool x), we will write \"add output signal sig(int i, bool x) in b\".\n" +
                "Removing an output signal sig from a block b is written \"remove output signal sig in b\".\n", "In AMULET, adding an input" +
                " signal mySig in a block myBlock is written \"add input signal mySig in myBlock\". If this signal conveys three parameters int n, " +
                "int m and bool b, it is written \"add input signal mySig (int n, int m, bool b) in myBlock\". Adding an output signal mySig in a " +
                "block myBlock is written \"add output signal mySig in myBlock\". If this signal conveys two parameters bool b and int n, it is " +
                "written \"add output signal mySig(bool b, int n) in myBlock\". Removing an input signal mySig from a block myBlock is written " +
                "\"remove input signal mySig in myBlock\", and removing an output signal mySig from a block myBlock is written \"remove output " +
                "signal mySig in myBlock\".");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. Adding an integer attribute i in a block b is written \"add attribute " +
                "int i in b\".\n" + "Adding a boolean " +
                "attribute x in a block b is written \"add attribute bool x in b\".\n" + "Removing an attribute a from a block b is written" +
                " \"remove attribute a in b\"","In AMULET, adding an integer attribute n in a block myBlock is written \"add attribute int n in " +
                "myBlock\".\n" + "Adding a boolean attribute b in a block myBlock is written \"add attribute bool b in myBlock\".\n" +
                "Removing an attribute myAttribute from a block myBlock is written \"remove attribute myAttribute in myBlock\".");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a connection between the ports of two blocks b1 and b2 is " +
                "written \"add link between b1 and b2\".\n" +
                "Removing a connection between the ports of two blocks b1 and b2 is written \"remove link between b1 and b2\".", "In " +
                "AMULET, adding a connection between the ports of two blocks b1 and b2 is written \"add link between b1 and b2\".\n" + "Removing a " +
                "connection between the ports of two blocks b1 and b2 is written \"remove link between b1 and b2\".");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. If we want an input and an output signal to be synchronized, we need " +
                "to connect them. Connecting an input signal insig in a block b to an output signal outsig in a block c is written \"add connection " +
                "between outsig in c to insig in b\".\n" + "Removing a connection between an input signal insig in a block b to an output signal " +
                "outsig in a block c is written \"remove connection between outsig in c to insig in b\".","In AMULET, connecting an input signal " +
                "inSig in a block myBlock to an output signal outSig in a block mySecondBlock is written \"add connection between outSig in " +
                "mySecondBlock to inSig in myBlock\".\n" + "Removing a connection between an input signal inSig in a block myBlock to an output " +
                "signal outSig in a block mySecondBlock is written \"remove connection between outSig in mySecondBlock to inSig in myBlock\".");

        myChat.aiinterface.addKnowledge("Connections are only possible between two signals, an input one and an output one.","Right, we can't " +
                "connect a signal to an attribute but only to another signal of the opposite type (input, output).");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a state s in a block b's state-machine diagram is written " +
                "\"add state s in b\".\n Removing a state s from a block b's state-machine diagram is written \"remove state s in b\".", "In " +
                "AMULET, adding a state myState in a block myBlock's state-machine diagram is written \"add state myState in myBlock\".\n Removing a state myState " +
                "from a block myBlock's state-machine diagram is written \"remove state myState in myBlock\".");

        myChat.aiinterface.addKnowledge("Here are some more AMULET commands. Adding a transition t in a block b's state-machine diagram from a " +
                "state s0 to" +
                " a state s1 is written \"add transition t in b from s0 to s1\". If this transition has a guard (i.e., a boolean condition " +
                "boolean_condition allowing its firing), we will write \"add transition t in b from s0 to s2 with [boolean_condition]\".\n" +
                "Removing a transition t in a block b's state-machine diagram from a state s0 to a state s1 is written \"remove transition t in b\"" +
                " or, if there is only one transition from s0 to s1, \"remove transition in b from s0 to s1\".", "In AMULET, adding a transition " +
                "myTransition in a block myBlock's state-machine diagram from a state myState to a state mySecondState is written \"add transition " +
                "myTransition in myBlock from myState to mySecondState\". If this transition has a guard (i.e., a boolean condition " +
                "boolean_condition allowing its firing), we will write \"add transition myTransition in myBlock from myState to mySecondState with " +
                "[boolean_condition]\".\n" + "Removing a transition myTransition in a block myBlock's state-machine diagram from a state myState to" +
                " a state mySecondState is written \"remove transition myTransition in myState\" or, if there is only one transition from myState " +
                "to mySecondState, \"remove transition in myBlock from myState to mySecondState\".");

        myChat.aiinterface.addKnowledge("If we want a block b to receive a parameter p through an input signal and if b has no attribute of the " +
                "type of p, we need first to add the relevant attribute to b.","Right. For instance, if we want a block b to receive an " +
                "integer parameter n and if b has no integer attribute, we will first add an integer attribute n: add attribute int n in b");

        myChat.aiinterface.addKnowledge("Consider a block having an integer attribute myInt. If we want this block to send this value " +
                "through an output signal myOutSig, the output signal declaration will be myOutSig(int myInt).","Right. And if this block " +
                "has a boolean attribute myBool and we want it to receive a boolean value from an input signal myInSig and if we want this value to be assigned " +
                "to myBool, the input signal declaration will be myInSig(bool myBool).");

        myChat.aiinterface.addKnowledge("A link can only exist between two blocks. We can't add a link between to signals, only a connection.",
                "Right. If I want two blocks b1 and b2 to be connected, I will write \"add link between b1 and b2\", and if I want two signals s1 " +
                        "and s2 belonging to b1 and b2 to be connected, I will write \"add connection between s1 in b1 to s2 in b2\".");

        myChat.aiinterface.addKnowledge("If a block already exists in the model, we don't need to add it with an AMULET \"add\" command.",
                "Right, if the model I analyze already has a block b1, I will never write \"add block b1\" except if b1 has been deleted by another" +
                        " AMULET command.");

        myChat.aiinterface.addKnowledge("Now, I want you to answer only with the AMULET source code, without any comment nor other sentence that is" +
                " not AMULET source code.", "Understood, from now on I will only provide AMULET source code.");

        myChat.aiinterface.addKnowledge("Consider a block diagram with a block b1 and a block b2. I want b1 to send an integer value n to b2. Could" +
                " you provide the relevant AMULET source code?","add attribute int n in b2\n add attribute int n in b1\n add output signal sendInt" +
                "(int n) in b1\n add input signal receiveInt(int n) in b2");

        myChat.aiinterface.addKnowledge("Consider a block diagram with a block b1 and a block b2. I want b1 to send an boolean value b to b2. Could" +
                " you provide the relevant AMULET source code?","add attribute bool b in b1\n add attribute bool b in b2\n add output signal " +
                "sendBool(bool b) in b1\n add input signal receiveBool(bool b) in b2");*/
    }

    private void amuletChat() {

        /*if (!selectedChat().knowledgeOnAMULET){
            injectAMULETKnowledge(selectedChat());
            selectedChat().knowledgeOnAMULET = true;
        }*/

        String SysMLV2Design;
        TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
        if (!(tp instanceof AvatarDesignPanel)) {
            error("An AVATAR model must be selected first");
            return;
        }
        else {
            SysMLV2Design = mgui.gtm.toSysMLV2();
            if (SysMLV2Design == null){
                error("Please perform a syntax checking\n");
                return;
            }
            //selectedChat().aiinterface.addKnowledge("Consider the following model " + SysMLV2Design,"Understood");
        }

        if (question.getText().trim().length() == 0) {
            error("No question is provided. Aborting.\n\n");
            return;
        }

        String questionA = question.getText().trim() + "\n";
        //makeQuestion(questionA, AMULET, mgui.getCurrentTDiagramPanel());

        /**inform("AMULET chat is selected\n");
         TraceManager.addDev("Appending: " + question.getText().trim() + " to answer");
         GraphicLib.appendToPane(selectedChat().answer, "\nYou:" + question.getText().trim() + "\n", Color.blue);

         String lastChatAnswer = "";

         try {
         GraphicLib.appendToPane(console, "Connecting, waiting for answer\n", Color.blue);
         lastChatAnswer = selectedChat().aiinterface.chat(questionA, true, true);
         } catch (AIInterfaceException aiie) {
         error(aiie.getMessage());
         return;
         }
         inform("Got answer from ai. All done.\n\n");
         GraphicLib.appendToPane(selectedChat().answer, "\nAI:" + lastChatAnswer + "\n", Color.red);
         question.setText("");**/

    }

    private String makeQuestion(String _question, int _kind, TDiagramPanel _tdp) {
        GraphicLib.appendToPane(chatOfStart().answer, _question, Color.blue);

        //try {
            GraphicLib.appendToPane(console, "Connecting, waiting for answer\n", Color.blue);
            //chatOfStart().lastAnswer = chatOfStart().aiinterface.chat(_question, true, true);
            //chatOfStart().previousKind = _kind;
            chatOfStart().tdp = _tdp;
        /*} catch (AIInterfaceException aiie) {
            error(aiie.getMessage());
            return null;
        }*/
        inform("Got answer from ai. All done.\n\n");
        GraphicLib.appendToPane(chatOfStart().answer, "\nAI:\n" + chatOfStart().lastAnswer + "\n", Color.red);

        return chatOfStart().lastAnswer;

    }

    private ChatData selectedChat() {
        return chats.get(answerPane.getSelectedIndex());
    }

    private ChatData chatOfStart() {
        return chats.get(currentChatIndex);
    }


    private void error(String text) {
        GraphicLib.appendToPane(console, "\n****" + text + " ****\n\n", Color.red);
    }

    private void inform(String text) {
        GraphicLib.appendToPane(console, text, Color.blue);
    }

    public void help() {
        if (mgui == null) {
            TraceManager.addDev("Null mgui");
        }

        HelpManager hm = mgui.getHelpManager();
        HelpEntry he = hm.getHelpEntryWithHTMLFile("ai.html");
        mgui.openHelpFrame(he);

    }


    public void clear() {
        selectedChat().clear();
        // We must also remove the knowledge
        //selectedChat().aiinterface.clearKnowledge();
    }

    public void requestRenameTab() {
        int index = answerPane.getSelectedIndex();
        String oldName = answerPane.getTitleAt(index);
        String s = (String) JOptionPane.showInputDialog(this, "Name: ", "Renaming a tab", JOptionPane.PLAIN_MESSAGE,
                IconManager.imgic101, null,
                answerPane.getTitleAt(index));
        if ((s != null) && (s.length() > 0)) {
            // name already in use?
            if (s.compareTo(oldName) != 0) {
                if (index < answerPane.getTabCount()) {
                    answerPane.setTitleAt(index, s);
                }
            }
        }
    }

    public void removeCurrentTab() {
        int index = answerPane.getSelectedIndex();
        answerPane.remove(index);
        chats.remove(index);
    }

    public void requestMoveRightTab() {
        int index = answerPane.getSelectedIndex();
        requestMoveTabFromTo(index, index + 1);

    }

    public void requestMoveLeftTab() {
        int index = answerPane.getSelectedIndex();
        requestMoveTabFromTo(index, index - 1);
    }

    public void requestMoveTabFromTo(int src, int dst) {

        // Get all the properties
        Component comp = answerPane.getComponentAt(src);
        String label = answerPane.getTitleAt(src);
        Icon icon = answerPane.getIconAt(src);
        Icon iconDis = answerPane.getDisabledIconAt(src);
        String tooltip = answerPane.getToolTipTextAt(src);
        boolean enabled = answerPane.isEnabledAt(src);
        int keycode = answerPane.getMnemonicAt(src);
        int mnemonicLoc = answerPane.getDisplayedMnemonicIndexAt(src);
        Color fg = answerPane.getForegroundAt(src);
        Color bg = answerPane.getBackgroundAt(src);

        // Remove the tab
        answerPane.remove(src);

        // Add a new tab
        answerPane.insertTab(label, icon, comp, tooltip, dst);

        ChatData data = chats.get(src);
        chats.remove(src);
        chats.add(dst, data);

        // Restore all properties
        answerPane.setDisabledIconAt(dst, iconDis);
        answerPane.setEnabledAt(dst, enabled);
        answerPane.setMnemonicAt(dst, keycode);
        answerPane.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
        answerPane.setForegroundAt(dst, fg);
        answerPane.setBackgroundAt(dst, bg);

        answerPane.setSelectedIndex(dst);
    }

    private class ChatData implements AIFeedback, Runnable {

        public JTextPane answer = new JTextPane();
        public TDiagramPanel tdp;
        public boolean doIconRotation = false;
        private Thread t;
        public String lastAnswer;
        public AIChatData aiChatData;
        public AIInteract aiInteract;

        public ChatData() {
        }

        public boolean makeAIChatData() {
            aiChatData = new AIChatData();
            aiChatData.feedback = this;
            return aiChatData.makeAIInterface();
        }


        // AIFeedback methods
        public void addInformation(String text) {
            inform(text);
        }

        public void addError(String text) {
            error(text);
        }

        public void addToChat(String data, boolean user) {
            if (user) {
                GraphicLib.appendToPane(chatOfStart().answer, "\nTTool:" + data + "\n", Color.blue);
            } else {
                GraphicLib.appendToPane(chatOfStart().answer, "\nAI:" + data + "\n", Color.red);
            }
        }

        public void setAnswerText(String text) {
            lastAnswer = text;
            GraphicLib.appendToPane(selectedChat().answer, "\nAI:" + text + "\n", Color.red);
            enableDisableActions();
        }


        public void setRunning(boolean b) {
            if (b) {
                startAnimation();
            } else {
                stopAnimation();
            }
            enableDisableActions();
        }


        // Internal methods
        public void clear() {
            answer.setText("");
            lastAnswer = "";
        }


        public void startAnimation() {
            doIconRotation = true;
            t = new Thread(this);
            t.start();
        }

        public void stopAnimation() {
            doIconRotation = false;
            if (t != null) {
                t.interrupt();
            }
            t = null;
        }


        public void run() {
            int angle = 0;

            while (doIconRotation) {
                angle = (angle - 15) % 360;
                ImageIcon rotated = rotatedI.get(angle);
                if (rotated == null) {
                    rotated = IconManager.rotateImageIcon(IconManager.imgic154, angle);
                    rotatedI.put(angle, rotated);
                }

                setIcon(this, rotated);
                try {
                    Thread.currentThread().sleep(100);
                } catch (Exception e) {
                    TraceManager.addDev("Interrupted");
                    doIconRotation = false;
                }
                ;
            }
            setIcon(this, IconManager.imgic154);
        }
    }

    // Handling popup menu AI
    private class PopupListener extends MouseAdapter /* popup menus onto tabs */ {
        private JFrameAI frameAI;
        private JPopupMenu menu;

        private JMenuItem rename, remove, moveRight, moveLeft, addNew, clear;

        private Action listener = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();

                if (e.getSource() == rename) {
                    frameAI.requestRenameTab();
                } else if (e.getSource() == remove) {
                    frameAI.removeCurrentTab();
                } else if (e.getSource() == moveRight) {
                    frameAI.requestMoveRightTab();
                } else if (e.getSource() == remove) {
                    frameAI.requestMoveLeftTab();
                } else if (e.getSource() == addNew) {
                    addChat(getChatName());
                } else if (e.getSource() == clear) {
                    clear();
                }

            }
        };

        public PopupListener(JFrameAI _frameAI) {
            frameAI = _frameAI;
            createMenu();
        }

        public void mousePressed(MouseEvent e) {
            checkForPopup(e);
        }

        public void mouseReleased(MouseEvent e) {

            checkForPopup(e);
        }

        public void mouseClicked(MouseEvent e) {

            checkForPopup(e);
        }

        private void checkForPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                Component c = e.getComponent();
                // TraceManager.addDev("e =" + e + " Component=" + c);
                menu.show(c, e.getX(), e.getY());
            }
        }

        private void createMenu() {
            rename = createMenuItem("Rename");
            remove = createMenuItem("Remove");
            moveLeft = createMenuItem("Move to the left");
            moveRight = createMenuItem("Move to the right");
            addNew = createMenuItem("Add new chat");
            clear = createMenuItem("Clear current chat");

            menu = new JPopupMenu("Views");
            menu.add(addNew);
            menu.addSeparator();
            menu.add(clear);
            menu.addSeparator();
            menu.add(rename);
            menu.addSeparator();
            menu.add(remove);
            menu.addSeparator();
            menu.add(moveLeft);
            menu.add(moveRight);
        }

        private JMenuItem createMenuItem(String s) {
            JMenuItem item = new JMenuItem(s);
            item.setActionCommand(s);
            item.addActionListener(listener);
            return item;
        }
    }

} // Class

	