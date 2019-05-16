package ui.window;

import help.HelpEntry;
import help.HelpManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TGComponentHelp extends JDialog implements ActionListener {

    MainGUI mainGUI;
    HelpEntry helpEntry;
    HelpManager helpManager;
    JButton helpBut;
    JEditorPane pane;


    public TGComponentHelp(MainGUI _mgui, HelpEntry _he) {
        mainGUI = _mgui;
        helpEntry = _he;

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        Font f = new Font("Courrier", Font.BOLD, 12);

        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        pane = new JEditorPane("text/html;charset=UTF-8", "");
        pane.setEditable(false);
        pane.setText(helpEntry.getHTMLContent());

        JScrollPane jsp = new JScrollPane(pane);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        helpPanel.add(jsp, BorderLayout.CENTER);

        framePanel.add(helpPanel, BorderLayout.CENTER);

        helpBut = new JButton("Help", IconManager.imgic32);

        helpManager = new HelpManager();
        if(helpManager.loadEntries()) {
            mainGUI.setHelpManager(helpManager);
        }

        helpBut.addActionListener(this);

        JPanel jp = new JPanel();
        jp.add(helpBut);
        framePanel.add(jp, BorderLayout.SOUTH);

        setSize(300, 200);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == helpBut) {
            mainGUI.openHelpFrame(helpEntry);
        }
    }

    public void setLocationHelpWindow(Component c) {
        // target location
        int dx, dy;
        // target GC
        GraphicsDevice gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle gcBounds;

        Dimension windowSize = getSize();
        //TraceManager.addDev("window size ==> height = " + windowSize.height + " width = " + windowSize.width);

        gcBounds = gc.getDefaultConfiguration().getBounds();

        //TraceManager.addDev("gc bounds ==> (x,y) = " + "(" + gcBounds.x + "," + gcBounds.y + ")" + " gc bound width = " + gcBounds.width + " gc " +
        //       "bound height = " + gcBounds.height);

        Dimension compSize = c.getSize();

        Point compLocation = c.getLocationOnScreen();
        //TraceManager.addDev("button location ==> (x,y) = " + "(" + compLocation.x + "," + compLocation.y + ")" );

        dx = compLocation.x + compSize.width;
        dy = compLocation.y + compSize.height;

        if (dy + windowSize.height > gcBounds.y + gcBounds.height) {
            dy = gcBounds.y + gcBounds.height - windowSize.height;
        }

        if (compLocation.x + windowSize.width > gcBounds.width + gcBounds.x) {
            dx = compLocation.x - windowSize.width;
        }
        //TraceManager.addDev("help window location ==> (x,y) = " + "(" + dx+ "," + dy + ")" );
        setLocation(dx, dy);
    }
}