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




package ui;

import myutil.GraphicLib;
import ui.tmldd.TMLArchiDiagramPanel;
import ui.tmldd.TMLArchiDiagramToolBar;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Vector;

/**
 * Class TMLArchiPanel
 * Managenemt of TML architecture panels
 * Creation: 18/09/2007
 * @version 1.0 18/09/2007
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class TMLArchiPanel extends TURTLEPanel {
    public TMLArchiDiagramPanel tmlap;
    public Vector<TGComponent> validated, ignored;

    public TMLArchiPanel(MainGUI _mgui) {
        super(_mgui);

        // Issue #41 Ordering of tabbed panes
        tabbedPane = GraphicLib.createTabbedPane();//new JTabbedPane();

        cl = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    mgui.paneDesignAction(e);
                }
            };

        tabbedPane.addChangeListener(cl);
        tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));
    }

    @Override
    public void init() {

        //  Class Diagram toolbar
        TMLArchiDiagramToolBar toolBarTML = new TMLArchiDiagramToolBar(mgui);
        toolbars.add(toolBarTML);

        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BorderLayout());

        //Class diagram
        tmlap = new TMLArchiDiagramPanel(mgui, toolBarTML);
        tmlap.setName("DIPLODOCUS architecture and mapping Diagram");

        toolBarTML.setPanel(tmlap);
        // Diagram toolbar

        tmlap.tp = this;
        tdp = tmlap;
        panels.add(tmlap); // Always first in list
        JScrollDiagramPanel jsp = new JScrollDiagramPanel(tmlap);
        tmlap.jsp = jsp;
        jsp.setWheelScrollingEnabled(true);
        jsp.getVerticalScrollBar().setUnitIncrement( MainGUI.INCREMENT );
        toolBarPanel.add(toolBarTML, BorderLayout.NORTH);
        toolBarPanel.add(jsp, BorderLayout.CENTER);
        tabbedPane.addTab("DIPLODOCUS architecture and mapping Diagram", IconManager.imgic60, toolBarPanel, "Opens DIPLODOCUS architecture diagram");
        tabbedPane.setSelectedIndex(0);
        mgui.changeMade(tmlap, TDiagramPanel.NEW_COMPONENT);

        //jsp.setVisible(true);

    }

    public String saveHeaderInXml(String extensionToName) {
        if (extensionToName == null) {
            return "<Modeling type=\"TML Architecture\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
        }
        return "<Modeling type=\"TML Architecture\" nameTab=\"" + mgui.getTabName(this) + extensionToName +"\" >\n";
    }

    public String saveTailInXml() {
        return "</Modeling>\n\n\n";
    }

    public String toString() {
        return mgui.getTitleAt(this) + " (DIPLODOCUS Mapping View)";
    }

    public void renameMapping(String oldName, String newName) {
        if (tmlap != null) {
            tmlap.renameMapping(oldName, newName);
        }
    }

    public boolean addRandomTasks( String []taskNames) {
        if (tmlap == null) {
            return false;
        }

        return tmlap.addRandomTasks(taskNames);
    }

    public boolean getConsiderExecOperators() {
        if (tmlap != null) {
            return tmlap.getConsiderExecOperators();
        }
        return true;
    }

    public boolean getConsiderTimingOperators() {
        if (tmlap != null) {
            return tmlap.getConsiderTimingOperators();
        }
        return true;
    }

    public void setConsiderExecOperators(boolean _considerExecOperators) {
        if (tmlap != null) {
            tmlap.setConsiderExecOperators(_considerExecOperators);
        }
    }

    public void setConsiderTimingOperators(boolean _considerTimingOperators) {
        if (tmlap != null) {
            tmlap.setConsiderTimingOperators(_considerTimingOperators);
        }
    }
}
