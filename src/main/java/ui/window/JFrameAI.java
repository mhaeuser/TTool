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
import help.HelpEntry;
import help.HelpManager;
import myutil.GraphicLib;
import myutil.TraceManager;
import ui.*;
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
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class JFrameAI
 * Creation: 01/03/2023
 * version 1.0 01/03/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameAI extends JFrame implements ActionListener {


    private static String[] POSSIBLE_ACTIONS = {"Chat", "Identify requirements from a specification", "Classify requirements from a requirement " +
            "diagram", "Identify properties from a design", "Identify system blocks from a specification", "Identify software blocks from a " +
            "specification", "A(I)MULET"};

    private static String[] AIInteractClass = {"AIChat", "AIReqIdent", "AIReqClassification", "AIDesignPropertyIdentification", "AIBlock", "AIChat",
            "AIAmulet"};

    protected JComboBox<String> listOfPossibleActions;


    private MainGUI mgui;
    private JTextPane question, console;
    private JTabbedPane answerPane;
    private ArrayList<ChatData> chats;

    private int currentChatIndex = -1;

    private JMenuBar menuBar;
    private JMenu help;
    private JPopupMenu helpPopup;


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
        } else if (selectedChat.aiInteract instanceof ai.AIAmulet) {
            applyMutations();
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

    private void applyMutations(){
        AvatarSpecification avspec = mgui.gtm.getAvatarSpecification();
        if (avspec == null){
            error("AVATAR specification not found: aborting.\n");
            return;
        }

        inform("Applying mutations to the model, please wait\n");
        avspec = (AvatarSpecification) (selectedChat().aiInteract.applyAnswer( avspec ));

        if (avspec != null) {
            mgui.drawAvatarSpecification(avspec);
            inform("Mutations applied");
        } else {
            error("No mutations applied");
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

	