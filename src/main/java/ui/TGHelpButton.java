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

import help.HelpEntry;
import help.HelpManager;
import myutil.TraceManager;
import ui.util.IconManager;
import ui.window.JDialogTGComponentHelp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class TGHelpButton
 * GGeneric help button
 * Creation: 20/05/2019
 * @version 1.1 20/05/2019
 * @author Ludovic APVRILLE
 */
public class TGHelpButton extends JButton {

    private JDialogTGComponentHelp helpDialog;
    private HelpEntry he;
    private MainGUI mgui;

    public TGHelpButton(Icon icon, String helpWord, MainGUI mgui, HelpManager hm) {
        super(icon);
        this.mgui = mgui;
        setGeneralConfiguration();
        makeHelpConfiguration(helpWord, mgui, hm);
    }

    public TGHelpButton(String text, Icon icon, String helpWord, MainGUI mgui, HelpManager hm) {
        super(text, icon);
        this.mgui = mgui;
        setGeneralConfiguration();
        makeHelpConfiguration(helpWord, mgui, hm);
    }

    private void setGeneralConfiguration() {
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(20,20));
    }

    public void makeHelpConfiguration(String helpWord, MainGUI mgui, HelpManager hm) {
        if (hm != null) {
            if (hm.loadEntries()) {
                if (helpWord.endsWith(".html")) {
                    he = hm.getHelpEntryWithHTMLFile(helpWord);
                } else{
                    he = hm.getFromMasterKeyword(helpWord);
                }
                if (he == null) {
                    TraceManager.addDev("NULL HE");
                } else {
                    buttonClick(this, he, mgui);
                }
            }
        }
    }

    // helpword can reference a HTML file or a master keyword
    public void addToPanel(JPanel panel, GridBagConstraints c) {
        if (he == null) {
            return;
        }
        c.weighty = 0.5;
        c.weightx = 0.5;
        c.gridwidth = GridBagConstraints.REMAINDER;


        panel.add(this, c);
    }

    public void addToPanel(JPanel panel) {
        if (he == null) {
            return;
        }
        //buttonClick(this, he, mgui);
        panel.add(this);
    }

    public void buttonClick(JButton but, HelpEntry he, MainGUI mgui) {
        //TraceManager.addDev("ADding action listener");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TraceManager.addDev("Action performed on help button!");
                if(helpDialog == null) {
                    helpDialog = new JDialogTGComponentHelp(mgui, he);
                    helpDialog.setLocationHelpWindow(but);
                } else {
                    if(!helpDialog.isVisible()) {
                        //helpDialog = new JDialogTGComponentHelp(mgui, he);
                        helpDialog.setLocationHelpWindow(but);
                        helpDialog.setVisible(true);
                    } else{
                        helpDialog.setVisible(false);
                    }
                }
            }
        });
    }



}
