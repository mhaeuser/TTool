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

import ui.TAction;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class ©
 * <p>
 * Creation: 15/07/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 15/07/2021
 * @see ui.sysmlv2.JFrameSysMLV2Text
 */
public class SysMLV2Actions extends AbstractAction {
    // Actions
    public static final int INSERT_REQUIREMENT = 0;
    public static final int ACT_SAVE = 1;
    public static final int INSERT_STATE_MACHINE = 2;
    public static final int INSERT_CONSTRAINT = 3;

    public static final int NB_ACTION = 4;


    public static final TAction[] actions = new TAction[NB_ACTION];
    public static final String LARGE_ICON = "LargeIcon";
    private EventListenerList listeners;


    public SysMLV2Actions(int id) {
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
        actions[ACT_SAVE] = new TAction("sysmlv2-save", "Save text", IconManager.imgic24, IconManager.imgic25,
                "Save", "Save text under edition", 'S');

        actions[INSERT_REQUIREMENT] = new TAction("sysmlv2-insert-req", "Insert requirement", IconManager.imgic84,
                IconManager.imgic84,
                "Requirement", "Insert requirement", 'R');

        actions[INSERT_CONSTRAINT] = new TAction("sysmlv2-insert-constraint", "Insert constraint", IconManager.imgic82,
                IconManager.imgic82,
                "Constraint", "Insert constraint", 'C');

        actions[INSERT_STATE_MACHINE] = new TAction("sysmlv2-insert-sm", "Insert state machine", IconManager.imgic63,
                IconManager.imgic63,
                "State machine", "Insert state machine", 'M');

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
