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


package ui.sysmlv2;

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
public class JFrameSysMLV2Text extends JFrame implements ActionListener {

    public final String REQUIREMENT_TEXT = "requirement def id '1.1' name {\n" +
            "\tdoc  /* documentation */\n\n" +
            "\tsubject x : y\n\n" +
            "\tattribute x : y ;\n\n" +
            "\trequire constraint { x < y }\n\n" +
            "}\n";

    public final String CONSTRAINT_TEXT = "constraint def MyLovelyConstraint (\n" +
            "\tweight : int[0..*],\n\n" +
            "\tmaxTotalWeight : int) {\n\n" +
            "\tsum(weight) <= maxTotalWeight\n\n" +
            "}\n";

    public final String STATE_MACHINE_TEXT = "state def VehicleStates {\n" +
            "\tdoc  /* documentation */\n\n" +
            "\tentry: then off;\n\n" +
            "\tstate off;\n\n" +
            "\ttransition off_to_starting\n\n" +
            "\t\tfirst off\n\n" +
            "\t\taccept VehicleStartSignal\n\n" +
            "\t\tthen starting;\n\n" +
            "\tstate starting;\n\n" +
            "\ttransition starting_to_on\n\n" +
            "\t\tfirst starting\n\n" +
            "\t\taccept VehicleOnSignal\n\n" +
            "\t\tthen on;\n\n" +
            "\tstate on;\n\n" +
            "\ttransition on_to_off\n\n" +
            "\t\tfirst on\n\n" +
            "\t\taccept VehicleOffSignal\n\n" +
            "\t\tthen off;\n\n" +
            "}\n";



    public static final int SAVED = 1;
    public SysMLV2Actions[] actions;
    private TGCSysMLV2 comp;
    private JLabel statuss;
    private JButton buttonCancel, buttonClose;
    private JTextArea jta;
    private JPanelForEdition jpfe;
    public MouseHandler mouseHandler;


    public JFrameSysMLV2Text(String title, TGCSysMLV2 _comp, String[] _lines, ImageIcon imgic) {
        super(title);
        comp = _comp;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());

        initActions();

        statuss = createStatusBar();

        // Mouse handler
        mouseHandler = new MouseHandler(statuss);

        String line = "";
        if (_lines != null) {
            for (int i = 0; i < _lines.length; i++) {
                line += _lines[i] + "\n";
            }
        }


        jta = new JTextArea(line);

        /*jta.setEditable(true);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);*/

        jpfe = new JPanelForEdition(this);
        jpfe.setText(line);


        //JScrollPane jsp = new JScrollPane(jpfe);

        framePanel.add(jpfe, BorderLayout.CENTER);

        buttonCancel = new JButton("Cancel", IconManager.imgic27);
        buttonCancel.addActionListener(this);
        buttonClose = new JButton("Save and close", IconManager.imgic25);
        buttonClose.addActionListener(this);

        JPanel lowPart = new JPanel(new BorderLayout());
        JPanel jp = new JPanel();
        jp.add(buttonCancel);
        jp.add(buttonClose);
        lowPart.add(jp, BorderLayout.CENTER);
        lowPart.add(statuss, BorderLayout.SOUTH);

        framePanel.add(lowPart, BorderLayout.SOUTH);


        pack();
    }

    private void initActions() {
        actions = new SysMLV2Actions[SysMLV2Actions.NB_ACTION];
        for (int i = 0; i < SysMLV2Actions.NB_ACTION; i++) {
            actions[i] = new SysMLV2Actions(i);
            actions[i].addActionListener(this);
        }

    }

    private JLabel createStatusBar() {
        statuss = new JLabel("Ready...");
        statuss.setForeground(ColorManager.InteractiveSimulationText);
        statuss.setBorder(BorderFactory.createEtchedBorder());
        return statuss;
    }

    public void actionPerformed(ActionEvent evt) {

        TraceManager.addDev("Action performed");

        if (evt.getSource() == buttonCancel) {
            cancel();
        } else if (evt.getSource() == buttonClose) {
            close();
        } else if (evt.getActionCommand().equals(actions[SysMLV2Actions.ACT_SAVE].getActionCommand())) {
            TraceManager.addDev("save");
            saveText();
        } else if ( evt.getActionCommand().equals(actions[SysMLV2Actions.INSERT_REQUIREMENT].getActionCommand())) {
            TraceManager.addDev("insert req");
            insertText(REQUIREMENT_TEXT);
        } else if ( evt.getActionCommand().equals(actions[SysMLV2Actions.INSERT_CONSTRAINT].getActionCommand())) {
            TraceManager.addDev("insert constraint");
            insertText(CONSTRAINT_TEXT);
        } else if ( evt.getActionCommand().equals(actions[SysMLV2Actions.INSERT_STATE_MACHINE].getActionCommand())) {
            TraceManager.addDev("insert smd");
            insertText(STATE_MACHINE_TEXT);
        }


    }

    private void cancel() {
        dispose();
    }

    private void saveText() {
        if (comp != null) {
            comp.setLines(jpfe.getText());
            comp.getTDiagramPanel().repaint();
        }
    }

    private void close() {
        saveText();
        dispose();
    }


    public void setMode(int mode) {

    }

    public void setStatusBarText(String text) {

    }

    private void insertText(String toBeInserted) {
        if (jpfe != null) {
            jpfe.insertText(toBeInserted, "");
        }
    }

    /**
     * This adapter is constructed to handle mouse over component events.
     */
    private class MouseHandler extends MouseAdapter {

        private JLabel label;

        /**
         * ctor for the adapter.
         *
         * @param label the JLabel which will recieve value of the
         *              Action.LONG_DESCRIPTION key.
         */
        public MouseHandler(JLabel label) {
            setLabel(label);
        }

        public void setLabel(JLabel label) {
            this.label = label;
        }

        public void mouseEntered(MouseEvent evt) {
            if (evt.getSource() instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) evt.getSource();
                Action action = button.getAction();
                if (action != null) {
                    String message = (String) action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }


} // Class 

	