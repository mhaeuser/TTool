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


import help.HelpEntry;
import help.HelpManager;
import help.ScoredHelpEntry;
import help.SearchResultHelpEntry;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class JFrameHelp
 * Creation: 07/03/2019
 * version 1.0 07/03/2019
 * @author Ludovic APVRILLE
 */
public	class JFrameHelp extends JFrame implements ActionListener {
    private JEditorPane pane;
    private HelpEntry he;
    private HelpManager hm;
    private JPanel jp01;
    private JButton back, forward, up, search;
    private JTextField searchT;
    private Vector<HelpEntry> visitedEntries;
    private int currentHEPointer;
    
    public JFrameHelp(String title, HelpManager hm, HelpEntry he) {
        super(title);
        this.he = he;
        this.hm = hm;
        visitedEntries = new Vector<>();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        Container framePanel = getContentPane();
        framePanel.setLayout(new BorderLayout());
        Font f = new Font("Courrier", Font.BOLD, 12);

        JPanel topButtons = new JPanel();
        back = new JButton("Back", IconManager.imgic53r);
        back.addActionListener(this);
        topButtons.add(back);
        forward = new JButton("Forward", IconManager.imgic53);
        forward.addActionListener(this);
        topButtons.add(forward);
        up = new JButton("Up", IconManager.imgic78Big);
        up.addActionListener(this);
        topButtons.add(up);


        // search
        searchT = new JTextField("", 20);
        searchT.setEnabled(true);
        searchT.setEditable(true);
        searchT.addActionListener(this);
        topButtons.add(searchT);

        search = new JButton("Search", IconManager.imgic5200);
        search.addActionListener(this);
        topButtons.add(search);

        framePanel.add(topButtons, BorderLayout.NORTH);
        // End of top panel


        jp01 = new JPanel();
        jp01.setLayout(new BorderLayout());
        jp01.setBorder(new javax.swing.border.TitledBorder("Help "));
        HTMLEditorKit kit = new HTMLEditorKit();
        pane = new JEditorPane("text/html;charset=UTF-8", "");
        pane.setEditorKit(kit);
        pane.setEditable(false);
        StyleSheet styleSheet = kit.getStyleSheet();

        // Load help.css

        //String imgsrc = this.getClass().getClassLoader().getSystemResource("ctlaall.png").toString();
        //TraceManager.addDev("PATH=" + imgsrc);

        URL url = HelpManager.getURL("help.css");
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String line;

            while ((line = in.readLine()) != null) {
                TraceManager.addDev("Line of css: " + line);
                styleSheet.addRule(line);
            }

            //styleSheet.addRule("h3 {color: green; margin-top: 15px;}");
            //styleSheet.addRule("p {color: blue; margin-top: 5em; margin-bottom: 5em;}");

        } catch (Exception e) {
            TraceManager.addDev("Failed style HTML:" + e.getMessage());
            styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
            styleSheet.addRule("h1 {color: blue; margin-top: 20px;}");
            styleSheet.addRule("h2 {color: #ff0000; margin-top: 20px;}");
            styleSheet.addRule("h3 {color: green; margin-top: 15px;}");
        }






        pane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    if (url == null) {
                        return;
                    }

                    String link = e.getURL().toString();
                   if (link.startsWith("file://")) {
                       // Open the corresponding file in TTool
                       String fileToOpen = link.substring(7, link.length());
                       TraceManager.addDev("File to open:" + fileToOpen);
                       if (hm == null) {
                           return;
                       }
                       HelpEntry he = hm.getHelpEntryWithHTMLFile(fileToOpen);
                       if (he != null) {
                           setHelpEntry(he);
                       } else {
                           TraceManager.addDev("Null HE");
                       }
                   }
                }
            }
        });


        //TraceManager.addDev("HMLTContent:" + he.getHTMLContent());
        JScrollPane jsp1 = new JScrollPane(pane);
        jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jp01.add(jsp1, BorderLayout.CENTER);

        framePanel.add(jp01, BorderLayout.CENTER);

        JButton button1 = new JButton("Close", IconManager.imgic27);
        button1.addActionListener(this);
        JPanel jp = new JPanel();
        jp.add(button1);
        framePanel.add(jp, BorderLayout.SOUTH);

        setHelpEntry(he);

        setSize(500,600);
        pack();

    }



    public void setHelpEntry(HelpEntry he) {
        TraceManager.addDev("Set Help Entry to " + he.getPathToHTMLFile());
        if(visitedEntries.size() > 0) {
            if (currentHEPointer < visitedEntries.size() - 1) {
                visitedEntries.subList(currentHEPointer+1, visitedEntries.size()).clear();
            }
        }

        visitedEntries.add(he);
        currentHEPointer = visitedEntries.size() - 1;
        this.he = he;
        updatePanel();
    }

    private void updatePanel() {
        TraceManager.addDev("Update panel");
        back.setEnabled(currentHEPointer != 0);
        forward.setEnabled(currentHEPointer < visitedEntries.size()-1);
        up.setEnabled(he.getFather() != null);


        jp01.setBorder(new javax.swing.border.TitledBorder("Help of: " + he.getMasterKeyword()));
        String content = handleImages(he.getHTMLContent());
        he.setHTMLContent(content);
        //String content = he.getHTMLContent();
        //TraceManager.addDev("HTML content is:" + content);
        pane.setText(content);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();        
        if (command.equals("Close")) {
            setVisible (false);
            return;
        } else if (evt.getSource() == back) {
            back();
        } else if (evt.getSource() == forward) {
            forward();
        } else if (evt.getSource() == up) {
            up();
        } else if ((evt.getSource() == search) || (evt.getSource() == searchT)) {
            search();
        }
    }

    public void back() {
        //TraceManager.addDev("Back");
        if (currentHEPointer < 1) {
            return;
        }
        currentHEPointer --;
        he = visitedEntries.get(currentHEPointer);
        updatePanel();
    }

    public void forward() {
        //TraceManager.addDev("Forward");

        if (currentHEPointer >= visitedEntries.size()-1) {
            return;
        }
        currentHEPointer ++;
        he = visitedEntries.get(currentHEPointer);
        updatePanel();

    }

    public void up() {
        //TraceManager.addDev("Up");

        if (he.getFather() == null) {
            return;
        }
        setHelpEntry(he.getFather());
    }

    public void search() {
        TraceManager.addDev("Search");

        if (hm == null) {
            TraceManager.addDev("Null HM");
            return;
        }


        String test = searchT.getText().trim().toLowerCase();
        if (test.length() == 0) {
            TraceManager.addDev("Empty search");
            return;
        }

        SearchResultHelpEntry srhe = new SearchResultHelpEntry();
        srhe.fillInfos("searchresult search help list index");


        Vector<ScoredHelpEntry> scores = new Vector<>();

        hm.searchInKeywords(test.split(" "), srhe, scores);
        hm.searchInContent(test.split(" "), srhe, scores);
        srhe.setScores(scores);

        TraceManager.addDev("search help: " + srhe.toString());

        srhe.mergeResults();

        TraceManager.addDev("search help after merge: " + srhe.toString());

        srhe.sortResults();

        TraceManager.addDev("search help after sort: " + srhe.toString());


        TraceManager.addDev("Setting new help entry with search results ");
        setHelpEntry(srhe);
    }

    private String handleImages(String initialContent) {
        int index;
        int cpt = 0;

        while ((index = initialContent.indexOf("<img src=\"file:")) != -1) {

            URL url = null;

            String tmpContent = initialContent.substring(index + 15, initialContent.length());
            int index2 = tmpContent.indexOf("\"");
            if (index2 == -1) return initialContent;

            String infoFile = tmpContent.substring(0, index2);

            TraceManager.addDev("infoFile 1 =  " + infoFile);

            if (infoFile.startsWith("../ui/util/")) {
                infoFile = infoFile.substring(11, infoFile.length());
                url = IconManager.class.getResource(infoFile);
            } else if (infoFile.startsWith("../help/")) {
                infoFile = infoFile.substring(8, infoFile.length());
                url = HelpEntry.class.getResource(infoFile);
            }

            TraceManager.addDev("infoFile 2 =  " + infoFile);


            TraceManager.addDev("Image url=" + url);

            if (url != null) {
                String imgsrc = url.toString();
                TraceManager.addDev("Infofile:" + infoFile + " imgsrc=" + imgsrc);
                String tmp1 = initialContent.substring(0, index + 10);
                String tmp2 = initialContent.substring(index + 15 + index2, initialContent.length());
                initialContent = tmp1 + imgsrc + tmp2;
                TraceManager.addDev("New initial content:" + initialContent);
            } else {
                return initialContent;
            }
            cpt ++;
            if (cpt == 1000) {
                return initialContent;
            }
        }

        return initialContent;

    }

    
} // Class

