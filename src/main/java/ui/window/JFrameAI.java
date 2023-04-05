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

import myutil.TraceManager;
import ui.ColorManager;
import ui.TGCSysMLV2;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Class JFrameSysMLV2Text
 * Creation: 13/07/2021
 * version 1.0 13/07/2021
 *
 * @author Ludovic APVRILLE
 */
public class JFrameAI extends JFrame implements ActionListener {

    private String [] POSSIBLE_ACTIONS = {"Chat", "Classify requirements"};

    private JTextArea question, answer, console;
    protected JComboBox<String>  listOfPossibleActions;


    private JButton buttonClose, buttonStart, buttonApplyResponse;

    public JFrameAI(String title) {
        super(title);
        makeComponents();
    }

    public void makeComponents() {

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

        framePanel.add(panelTop, BorderLayout.NORTH);


        // Middle panel

        /*jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);*/

        JPanel questionPanel = new JPanel();
        questionPanel.setBorder(new javax.swing.border.TitledBorder("Question"));
        questionPanel.setPreferredSize(new Dimension(450, 550));
        question = new JTextArea();
        question.setPreferredSize(new Dimension(400, 500));
        JScrollPane scrollPane = new JScrollPane(question);
        questionPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel answerPanel = new JPanel();
        answerPanel.setBorder(new javax.swing.border.TitledBorder("Answer"));
        answerPanel.setPreferredSize(new Dimension(450, 550));
        answer = new JTextArea();
        answer.setPreferredSize(new Dimension(400, 500));
        setOptionsJTextArea(answer, true);
        scrollPane = new JScrollPane(answer);
        answerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel consolePanel = new JPanel();
        consolePanel.setBorder(new javax.swing.border.TitledBorder("Console"));
        console = new JTextArea();
        console.setPreferredSize(new Dimension(900, 150));
        addToConsole("Select options and click on \"start\"");
        scrollPane = new JScrollPane(console);
        consolePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel intermediate = new JPanel();
        intermediate.add(questionPanel);
        intermediate.add(answerPanel);
        middlePanel.add(intermediate, BorderLayout.CENTER);
        middlePanel.add(consolePanel, BorderLayout.SOUTH);

        framePanel.add(middlePanel, BorderLayout.CENTER);



        // Lower panel

        buttonClose = new JButton("Close", IconManager.imgic26);
        buttonClose.addActionListener(this);

        buttonStart = new JButton("Start", IconManager.imgic53);
        buttonStart.addActionListener(this);

        buttonApplyResponse = new JButton("Apply response", IconManager.imgic25);
        buttonStart.addActionListener(this);

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



    public void actionPerformed(ActionEvent evt) {

        TraceManager.addDev("Action performed");

        if (evt.getSource() == buttonClose) {
            close();
        } else if (evt.getSource() == buttonStart) {
            start();
        } else if (evt.getSource() == buttonApplyResponse) {
            applyResponse();
        }
    }


    private void close() {
        dispose();
    }

    private void start() {

    }

    private void applyResponse() {

    }

    private void enableDisableActions() {

    }

    private void setOptionsJTextArea(JTextArea jta, boolean _isEditable) {
        jta.setEditable(_isEditable);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
    }

    private void addToConsole() {
        console.append("Select options and click on \"start\"");
    }








} // Class 

	