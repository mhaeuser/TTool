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

import avatartranslator.AvatarSpecification;
import avatartranslator.tosysmlv2.AVATAR2SysMLV2;
import common.ConfigurationTTool;
import help.HelpEntry;
import help.HelpManager;
import myutil.AIInterface;
import myutil.AIInterfaceException;
import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
import ui.avatarrd.AvatarRDPanel;
import ui.avatarrd.AvatarRDRequirement;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


/**
 * Class JFrameSysMLV2Text
 * Creation: 13/07/2021
 * version 1.0 13/07/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameAI extends JFrame implements ActionListener, Runnable {

    private static int IDENTIFY_REQUIREMENT = 1;
    private static int KIND_CLASSIFY_REQUIREMENT = 2;
    private static int IDENTIFY_PROPERTIES = 3;

    private static String[] POSSIBLE_ACTIONS = {"Chat", "Identify requirements from a specification", "Classify requirements from a requirement " +
            "diagram", "Identify properties from a design"};
    protected JComboBox<String> listOfPossibleActions;
    private String QUESTION_CLASSIFY_REQ = "I would like to identify the \"type\" attribute, i.e. the classification, " +
            "of the following requirements. Could you give me a correct type among: safety, security, functional, " +
            "non-functional, performance, business, stakeholder need. if the main category is security, you can use the " +
            "following sub categories: privacy, confidentiality, non-repudiation, controlled access, availability," +
            "immunity, data origin authenticity, freshness. Use the following format for the answer:" +
            " - Requirement name: classification\n";
    private String QUESTION_IDENTIFY_REQ = "Identify all the relevant requirements of the following specification. List them as a json array with " +
            "the following elements for each requirements in the array:" +
            " " +
            "name: name of the requirement, id: id of the requirement (as a string), doc: text of the requirement  " +
            "compose: all req names, derive: all req names, refine: all req names. The name " +
            "should be an english " +
            "name and not a number or an identifier";
    private String QUESTION_IDENTIFY_PROPERTIES = "List properties of the following SysML V2 specification.";

    private String KNOWLEDGE_ON_DESIGN_PROPERTIES = "Properties of Design are of the following types\n" +
            "- A<>expr means that all states of all paths must respect expr\n" +
            "- A[]expr means that all states of at least one path must respect expr\n" +
            "- E<>expr means that one state of all paths must respect expr\n" +
            "- E[]expr means that one state of one path must respect expr\n" +
            "expr is a boolean expression using either attributes of blocks or blocks states";

    private MainGUI mgui;
    private JTextPane question, console;
    private JTabbedPane answerPane;
    private ArrayList<ChatData> chats;

    private int currentChatIndex = -1;

    private JMenuBar menuBar;
    private JMenu help;
    private JPopupMenu helpPopup;

    private boolean go = false;

    private class ChatData {
        public AIInterface aiinterface;
        public boolean knowledgeOnProperties = false;
        public JTextPane answer = new JTextPane();
        public String lastAnswer = "";
        public int previousKind;
        public TDiagramPanel tdp;

        public ChatData() {}

        public void clear() {
            lastAnswer = "";
            answer.setText("");
        }
    }


    private JButton buttonClose, buttonStart, buttonApplyResponse;

    public JFrameAI(String title, MainGUI _mgui) {
        super(title);
        mgui = _mgui;
        chats = new ArrayList<>();
        makeComponents();

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
                "opéra (l’abus d’Alcôve est dangereux pour la santé)"};
        int x = (int)(Math.random()*names.length);
        return names[x];
    }


    private void close() {
        dispose();
    }

    private void start() {
        currentChatIndex = answerPane.getSelectedIndex();
        runChat();
    }

    private void applyResponse() {

        if (selectedChat().previousKind == KIND_CLASSIFY_REQUIREMENT) {
            applyRequirementClassification();
        } else if (selectedChat().previousKind == IDENTIFY_REQUIREMENT) {
            applyRequirementIdentification();
        } else {
            return;
        }
        question.setText("");
    }

    private void applyRequirementIdentification() {
        if (selectedChat().tdp == null) {
            error("No diagram has been selected\n");
            return;
        }

        if (!(selectedChat().tdp instanceof AvatarRDPanel)) {
            error("Wrong diagram has been selected\n");
            return;
        }

        AvatarRDPanel rdpanel = (AvatarRDPanel) selectedChat().tdp;

        inform("Enhancing requirement diagram with ai answer, please wait\n");
        int index = answerPane.getSelectedIndex();
        TraceManager.addDev("Considered JSON array: " + selectedChat().lastAnswer);
        try {
            rdpanel.loadAndUpdateFromText( selectedChat().lastAnswer );
        } catch (org.json.JSONException e) {
            TraceManager.addDev("JSON Exception: " + e.getMessage());
            inform("Answer provided by AI does not respect the JSON format necessary for TTool");
            rdpanel.repaint();
            return;
        }
        rdpanel.repaint();

        inform("Enhancing requirement diagram with ai answer: done<\n");
    }

    private void applyRequirementClassification() {
        if (selectedChat().tdp == null) {
            error("No diagram has been selected\n");
            return;
        }

        if (!(selectedChat().tdp instanceof AvatarRDPanel)) {
            error("Wrong diagram has been selected\n");
            return;
        }

        AvatarRDPanel rdpanel = (AvatarRDPanel) selectedChat().tdp;

        inform("Enhancing requirement diagram with ai answer, please wait\n");

        String automatedAnswer = selectedChat().lastAnswer;

        for (TGComponent tgc : rdpanel.getAllRequirements()) {
            AvatarRDRequirement req = (AvatarRDRequirement) tgc;
            String query = req.getValue() + ":";
            int index = automatedAnswer.indexOf(query);
            if (index != -1) {
                String kind = automatedAnswer.substring( index + query.length() ).trim();
                //TraceManager.addDev("Kind=" + kind);
                int indexSpace = kind.indexOf("\n");
                int indexSpace1 = kind.indexOf(" ");
                String kTmp;
                if ((indexSpace1 > -1) || (indexSpace > -1)) {
                    if ((indexSpace1 > -1) && (indexSpace > -1)) {
                        indexSpace = Math.min(indexSpace, indexSpace1);
                        kTmp = kind.substring(0, indexSpace);
                    } else if (indexSpace1 > -1) {
                        kTmp = kind.substring(0, indexSpace1);
                    } else {
                        kTmp = kind.substring(0, indexSpace);
                    }
                } else {
                    kTmp = kind;
                }

                //TraceManager.addDev("Looking for req=" +  req.getValue() + " to set kind to " + kTmp);
                String k = JDialogRequirement.getKindFromString(kTmp);
                if (k != null) {
                    req.setKind(k);
                    inform("\tRequirement " + req.getValue() + " kind was set to " + k + "\n");
                } else {
                    error("Unknown kind: " + k + "\n");
                }
            }
        }

        rdpanel.repaint();


    }

    private void enableDisableActions() {
        String chat = chats.get(answerPane.getSelectedIndex()).lastAnswer;
        buttonApplyResponse.setEnabled(chat != null && chat.length() > 0);
        buttonStart.setEnabled(!go);
    }

    private void setOptionsJTextPane(JTextPane jta, boolean _isEditable) {
        jta.setEditable(_isEditable);
        jta.setMargin(new Insets(10, 10, 10, 10));
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
    }


    private void runChat() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        go = true;
        enableDisableActions();

        if (makeAIInterface()) {
            if (listOfPossibleActions.getSelectedIndex() == 0) {
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
        }

        go = false;
        enableDisableActions();
    }

    private void simpleChat() {
        if (question.getText().trim().length() == 0) {
            error("No question is provided. Aborting.\n\n");
            return;
        }

        inform("Simple chat is selected\n");
        TraceManager.addDev("Appending: " + question.getText().trim() + " to answer");
        GraphicLib.appendToPane(chatOfStart().answer, "\nYou:" + question.getText().trim() + "\n", Color.blue);

        String lastChatAnswer = "";

        try {
            GraphicLib.appendToPane(console, "Connecting, waiting for answer\n", Color.blue);
            lastChatAnswer = chatOfStart().aiinterface.chat(question.getText().trim(), true, true);
        } catch (AIInterfaceException aiie) {
            error(aiie.getMessage());
            return;
        }
        inform("Got answer from ai. All done.\n\n");
        GraphicLib.appendToPane(chatOfStart().answer, "\nAI:" + lastChatAnswer + "\n", Color.red);
        chatOfStart().lastAnswer = lastChatAnswer;
        question.setText("");

    }

    private void identifyRequirements() {
        inform("Identifying requirements\n");
        TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();
        if (!(tdp instanceof AvatarRDPanel)) {
            error("A requirement diagram must be selected first");
            return;
        }
        if (!hasQuestion()) {
            error("A system specification must be provided in the \"question\" area");
            return;
        }

        TraceManager.addDev("Asking for requirements");
        String questionT = "\nTTool:" + QUESTION_IDENTIFY_REQ + "\n" + question.getText().trim() + "\n";
        String answer = makeQuestion(questionT, IDENTIFY_REQUIREMENT, tdp);


    }

    private void classifyRequirements() {
        inform("Classifying requirements is selected\n");
        TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();
        if (!(tdp instanceof AvatarRDPanel)) {
            error("A requirement diagram must be selected first");
            return;
        }

        String s = ((AvatarRDPanel) tdp).toSysMLV2TextExcludeType(true).toString();
        if (s.length() == 0) {
            error("Empty requirement diagram. Aborting");
            return;
        }

        TraceManager.addDev("Appending: " + s.trim() + " to answer");
        String question = "\nTTool:" + QUESTION_CLASSIFY_REQ + "\n" + s.trim() + "\n";
        makeQuestion(question, KIND_CLASSIFY_REQUIREMENT, tdp);
    }

    private void identifyProperties() {
        inform("Identifying properties\n");
        TURTLEPanel tp = mgui.getCurrentTURTLEPanel();
        if (!(tp instanceof AvatarDesignPanel)) {
            error("A design diagram must be selected first");
            return;
        }
        TDiagramPanel tdp = mgui.getCurrentTDiagramPanel();

        boolean ret = mgui.checkModelingSyntax(tp, true);
        if (!ret) {
            error("Design diagram has syntax errors. Correct them before.");
            return;
        }

        AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();
        AVATAR2SysMLV2 tosysmlv2 = new AVATAR2SysMLV2(avspec);
        StringBuffer sb = tosysmlv2.generateSysMLV2Spec(false, false);

        TraceManager.addDev("Appending: " + sb.toString().trim() + " to answer");
        String question = QUESTION_IDENTIFY_PROPERTIES + "\n" + sb.toString().trim();

        if (!(chats.get(answerPane.getSelectedIndex()).knowledgeOnProperties)) {
            chats.get(answerPane.getSelectedIndex()).knowledgeOnProperties = true;
            question = KNOWLEDGE_ON_DESIGN_PROPERTIES + "\n" + question;
        }
        question = "\nTTool:" + question + "\n";
        makeQuestion(question, IDENTIFY_PROPERTIES, tdp);
    }

    private String makeQuestion(String _question, int _kind, TDiagramPanel _tdp) {
        GraphicLib.appendToPane(chatOfStart().answer, _question, Color.blue);

        try {
            GraphicLib.appendToPane(console, "Connecting, waiting for answer\n", Color.blue);
            chatOfStart().lastAnswer = chatOfStart().aiinterface.chat(_question, true, true);
            chatOfStart().previousKind = _kind;
            chatOfStart().tdp = _tdp;
        } catch (AIInterfaceException aiie) {
            error(aiie.getMessage());
            return null;
        }
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

    private boolean makeAIInterface() {
        if (chatOfStart().aiinterface == null) {
            String key = ConfigurationTTool.OPENAIKey;
            if (key == null) {
                error("No key has been set. Aborting.\n");
                return false;
            } else {
                TraceManager.addDev("Setting key: " + key);
                chatOfStart().aiinterface = new AIInterface();
                chatOfStart().aiinterface.setURL(AIInterface.URL_OPENAI_COMPLETION);
                chatOfStart().aiinterface.setAIModel(AIInterface.MODEL_GPT_35);
                chatOfStart().aiinterface.setKey(key);
            }
        }
        return true;
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

    public boolean hasQuestion() {
        return question.getText().trim().length() > 0;
    }

    public void clear() {
        selectedChat().clear();
        // We must also remove the knowledge
        selectedChat().aiinterface.clearKnowledge();
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

        ChatData data  = chats.get(src);
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

	