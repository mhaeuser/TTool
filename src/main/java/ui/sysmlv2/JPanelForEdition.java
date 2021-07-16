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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import myutil.*;
import ui.*;

/**
 * Class JPanelForEdition
 * Sysmlv2 edition
 * Creation: 15/07/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 15/07/2021
 */
public class JPanelForEdition extends JPanel implements ActionListener, DocumentListener, CaretListener, MouseListener {

    //private LatexStructure ls;
    private JFrameSysMLV2Text frame;

    private TextBar tb;
    private ActionBar ab;
    private JCheckBox spellcheck;
    private JCheckBox showOnlyCurrent;


    private JEditorPane jtp;
    private MyStyledEditorKit editorKit;
    private JScrollPane scroller;
    private LineNumber nl;
    private JComboBox templateBox;
    private JComboBox charBox;
    //private JTextField textForFind;
    //private JButton find, findPrevious, findFrame;

    private String text;
    private int firstIndex, lastIndex;
    private String shownText;

    //private int latexLine;

    private String word;
    private JMenuItem addToDic;
    private JMenuItem addToDicNoUpper;

    //private boolean settingText = false;
    //private boolean updateOnText = false;

    public final static int INCREMENT = 1024;
    private int maxIndex = INCREMENT;
    private int[] correspondance;

    private int selectedLine = -2;

    public JPanelForEdition(JFrameSysMLV2Text _frame) {
        super();
        frame = _frame;

        makeComponents();
        setComponents();

        correspondance = new int[INCREMENT];
    }


    @SuppressWarnings("unchecked")
    public void makeComponents() {
        JPanel panelTop = new JPanel();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c2 = new GridBagConstraints();
        panelTop.setLayout(gridbag2);

        setLayout(new BorderLayout());
        //setBorder(new javax.swing.border.TitledBorder("Managing breakpoints"));

        c2.gridwidth = GridBagConstraints.REMAINDER; //end row
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        c2.weighty = 1.0;
        c2.weightx = 1.0;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new javax.swing.border.TitledBorder("Edition options"));

        ab = new ActionBar(frame);
        JPanel topPanel = new JPanel();
        tb = new TextBar(frame);
        panel.add(tb, BorderLayout.SOUTH);
        panel.add(ab, BorderLayout.CENTER);
        showOnlyCurrent = new JCheckBox("Show only current");
        showOnlyCurrent.addActionListener(this);
        //showOnlyCurrent.setSelected(ConfigurationLatexEditor.ShowOnlyCurrent);
        //topPanel.add(showOnlyCurrent);
        spellcheck = new JCheckBox("Spell checking");
        spellcheck.addActionListener(this);
        //spellcheck.setSelected(ConfigurationLatexEditor.SpellCheck);
        //topPanel.add(spellcheck);
        templateBox = new JComboBox();
        /*for(Template t: mgui.getTemplates()) {
            templateBox.addItem(t.name);
        }*/
        //topPanel.add(templateBox);
        //topPanel.add(new JButton(mgui.actions[LEGUIAction.INSERT_TEMPLATE]));
        charBox = new JComboBox(EditionSet.charenc);
        //topPanel.add(charBox);

        // Find
        /*textForFind = new JTextField("", 15);
          textForFind.getDocument().addDocumentListener(this);
          find = new JButton(mgui.actions[LEGUIAction.FIND]);
          findFrame = new JButton(mgui.actions[LEGUIAction.FIND_FRAME]);
          findPrevious = new JButton(mgui.actions[LEGUIAction.FIND_PREVIOUS]);
          topPanel.add(textForFind);
          topPanel.add(find);
          topPanel.add(findPrevious);
          topPanel.add(findFrame);*/

        panel.add(topPanel, BorderLayout.NORTH);
        panelTop.add(panel, c2);


        // 2nd line panel2
        c2.weighty = 1.0;
        c2.weightx = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridheight = 1;
        panelTop.add(new JLabel(""), c2);

        add(panelTop, BorderLayout.NORTH);

        editorKit = new MyStyledEditorKit(this);

        c2.gridheight = 50;
        jtp = new JTextPane();
        jtp.setPreferredSize(new Dimension(0, 0));
        //jtp = new JTextArea();
        //System.out.println("Next text area");
        //jtp.setLineWrap(true);
        //jtp.setWrapStyleWord(true);


        Font f = new Font("Courrier", Font.PLAIN, 14);

        jtp.setFont(f);


        scroller = new JScrollPane();
        scroller.setViewportView(jtp);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroller, BorderLayout.CENTER);

        //add the LineNumber component:
        nl = new LineNumber(jtp, this);
        scroller.setRowHeaderView(nl);

        //activate Line_Highlight:
        //new LineHighlight(jtp, new Color(220,220,220));


        jtp.setBackground(Color.WHITE);
        jtp.setEditable(true);
        jtp.setEditorKitForContentType("text/rtf", editorKit);
        jtp.setContentType("text/rtf;");
        jtp.addCaretListener(this);
        jtp.addMouseListener(this);

        //              edit.setEditorKit(new StyledEditorKit());
        //              edit.setDocument(new SyntaxDocument());

        //jtp.setMargin(new Insets(10, 10, 10, 10));
        //jtp.setTabSize(2);
        //jtp.append("Text to edit");
        //jtp.setCaretPosition(jta.getDocument().getLength());


        FontMetrics fm = jtp.getFontMetrics(jtp.getFont());
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * 2;

        TabStop[] tabs = new TabStop[10];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = jtp.getDocument().getLength();
        ((StyledDocument) (jtp.getDocument())).setParagraphAttributes(0, length, attributes, false);

        jtp.getDocument().addDocumentListener(this);
        //JScrollPane jsp = new JScrollPane(jtp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //add(jsp, BorderLayout.CENTER);

    }

    public void setComponents() {
        jtp.setEnabled(false);
    }

    public void actionPerformed(ActionEvent evt) {

        // Compare the action command to the known actions.


    }

    public int manageFind(String _text) {
        //TraceManager.addDev("Action on find");
        editorKit.setFind(_text);
        editorKit.changeMade();
        frame.setStatusBarText("Occurences found: " + editorKit.getFindFound());
        return editorKit.getFindFound();
    }

    public void modifySpellCheck(boolean _mustSetText) {
        if (editorKit.getSyntaxDocument() != null) {
            editorKit.getSyntaxDocument().setCheckForSpelling(spellcheck.isSelected());
            editorKit.changeMade();
            /*if (_mustSetText) {
              if (ls.hasText()) {
              setText(ls.getText());
              } else {
              setText("");
              }
              }*/
        }
    }

    /*public void setLatexStructure(LatexStructure _ls) {
        TraceManager.addDev("Setting latex structure");
        ls = _ls;
        setText(ls.getText());
    }*/

    public void setText(String _text) {
        //TraceManager.addDev("Setting new text:" + _text);
        text = _text;
        //jtp.getDocument().putProperty(PlainDocument.tabSizeAttribute, 2);
        //jtp.setTabSize(2);
        jtp.getDocument().removeDocumentListener(this);
        jtp.setEnabled(true);
        //TraceManager.addDev("Enable actions");
        ab.setEnableActions(true);
        tb.setEnableActions(true);
        //mgui.actions[LEGUIAction.INSERT_TEMPLATE].setEnabled(true);
        spellcheck.setEnabled(true);

        shownText = _text;

        jtp.setText(shownText);
        jtp.getDocument().addDocumentListener(this);
    }

    public String getText() {
        return jtp.getText();
    }

    /*public int getLineCorrespondance(int _myLineNumber) {
      if (editorKit != null) {
      return editorKit.getLineCorrespondance(_myLineNumber);
      }
      return -1;
      }*/


    public void insertUpdate(DocumentEvent e) {
        //System.out.println("Insert update");

        updateText();

    }

    public void selectedElementDocument(int _index) {
        //jtp.setCaretPosition(jtp.getDocument().getLength());
        //TraceManager.addDev(ls.print(2309));

        //TraceManager.addDev(ls.print(_index));

        int index = Math.max(0, _index - 1);
        jtp.setCaretPosition(Math.min(index, jtp.getDocument().getLength()));
        try {
            Rectangle mRectangle2 = jtp.modelToView(index);
            Rectangle mRectangle3 = new Rectangle(mRectangle2.x, mRectangle2.y, mRectangle2.width, jtp.getVisibleRect().height);
            jtp.scrollRectToVisible(mRectangle3);
        } catch (Exception e) {
            TraceManager.addDev("Exception selectedElementDocument:" + e.getMessage());
        }

        setSelectedLine(getLineAtCaret(jtp));
        TraceManager.addDev("selectedline= " + selectedLine);
        nl.repaint();
    }

    public void setSelectedLine(int _selectedLine) {
        selectedLine = _selectedLine;
    }

    public int getSelectedLine() {
        return selectedLine;
    }

    public int getPosition() {
        return jtp.getCaretPosition();
    }


    public void removeUpdate(DocumentEvent e) {
        updateText();

    }

    public void changedUpdate(DocumentEvent e) {
        // we won't ever get this with a PlainDocument
    }

    public void manageCurrent() {
        if (showOnlyCurrent.isSelected()) {

            int index = getPosition();
            //int indexes[] = ls.getCurrentIndexesOfMainPosition(index);
            //firstIndex = Math.max(0, indexes[0]-1);
            //lastIndex = Math.max(0, indexes[1]-1);
            //setText(ls.getText());
            jtp.setCaretPosition(0);
            try {
                Rectangle mRectangle2 = jtp.modelToView(0);
                Rectangle mRectangle3 = new Rectangle(mRectangle2.x, mRectangle2.y, mRectangle2.width, jtp.getVisibleRect().height);
                jtp.scrollRectToVisible(mRectangle3);
            } catch (Exception e) {
                TraceManager.addDev("Exception selectedElementDocument:" + e.getMessage());
            }
            //mgui.setMode(mgui.GENERATE_CURRENT);
        } else {
            int index = getPosition();
            //setText(ls.getText());
            nl.repaint();
            selectedElementDocument(index + firstIndex);
            //mgui.setMode(mgui.NOT_GENERATE_CURRENT);
        }

        strongUpdateText();

    }

    public void strongUpdateText() {
        //TraceManager.addDev("Strong update");
        editorKit.changeMade();
        scroller.repaint();
        //nl.repaint();
        //editorKit = new MyStyledEditorKit();
        //jtp.setEditorKitForContentType("text/java", editorKit);
    }

    public void updateFind() {
        // Must go to next find
        String findTex = editorKit.getFind();
        boolean matchCase = editorKit.getMatchCase();

        int currentIndex = jtp.getCaretPosition();
        try {
            TraceManager.addDev("Must go to next find, current=" + currentIndex);
            String _text = jtp.getDocument().getText(currentIndex + 1, jtp.getDocument().getLength() - currentIndex - 1);

            int indexNext;
            if (matchCase) {
                indexNext = _text.indexOf(findTex);
            } else {
                indexNext = _text.toLowerCase().indexOf(findTex.toLowerCase());
            }
            TraceManager.addDev("Must go to next find, current=" + currentIndex + " next=" + indexNext);
            if (indexNext == -1) {
                frame.setStatusBarText("No next element");
            } else {
                selectedElementDocument(indexNext + currentIndex + 2);
                frame.setStatusBarText("Pointing to next element");
            }
        } catch (Exception e) {
            frame.setStatusBarText("Bad find command");
            TraceManager.addDev("Bad find command: " + e.getMessage());
        }
    }

    public void findPrevious() {
        // Must go to next find
        String findTex = editorKit.getFind();
        boolean matchCase = editorKit.getMatchCase();

        int currentIndex = jtp.getCaretPosition();
        try {
            TraceManager.addDev("Must go to previous find, current=" + currentIndex);
            String _text = jtp.getDocument().getText(0, currentIndex);

            int indexPrevious;
            if (matchCase) {
                indexPrevious = _text.lastIndexOf(findTex);
            } else {
                indexPrevious = _text.toLowerCase().lastIndexOf(findTex.toLowerCase());
            }

            TraceManager.addDev("Must go to previous find, current=" + currentIndex + " previous=" + indexPrevious);
            if (indexPrevious == -1) {
                frame.setStatusBarText("No previous element");
            } else {
                selectedElementDocument(indexPrevious + 1);
                frame.setStatusBarText("Pointing to previous element");
            }
        } catch (Exception e) {
            frame.setStatusBarText("Bad previous find command");
            TraceManager.addDev("Bad previous find command: " + e.getMessage());
        }
    }

    // If cursor is positionned on an element to replace, then replace it
    // Otherwise, do a findNext
    public void replaceOne(String _find, String _replace) {

        boolean matchCase = editorKit.getMatchCase();
        try {
            int minBeg = Math.max(jtp.getCaretPosition() - _find.length() + 1, 0);
            int maxBeg = Math.min(jtp.getCaretPosition() + _find.length(), jtp.getDocument().getLength() - minBeg);
            String _text = jtp.getDocument().getText(minBeg, maxBeg);

            TraceManager.addDev("Replace one text=>" + _text + "< caret pos=" + jtp.getCaretPosition());

            int indexFind;
            if (matchCase) {
                indexFind = _text.indexOf(_find);
            } else {
                indexFind = _text.toLowerCase().indexOf(_find.toLowerCase());
            }

            if (indexFind == -1) {
                // go to next find
                updateFind();
                frame.setStatusBarText("Nothing replaced: pointing to next element to be replaced");
                return;
            }


            SyntaxDocument sd = editorKit.getSyntaxDocument();
            sd.remove(minBeg + indexFind, _find.length());
            sd.insertString(minBeg + indexFind, _replace, sd.getNormalAttributeSet());

            // Going to next one
            updateFind();

            frame.setStatusBarText("1 occurence replaced ... pointing to the next one");
        } catch (Exception e) {
            frame.setStatusBarText("Bad replace one command");
            TraceManager.addDev("Bad replace one command: " + e.getMessage());
        }


    }

    public void replaceAll(String _find, String _replace) {
        boolean matchCase = editorKit.getMatchCase();

        try {
            int minBeg = Math.max(jtp.getCaretPosition() - _find.length() + 1, 0);
            String _text = jtp.getDocument().getText(minBeg, jtp.getDocument().getLength() - minBeg);
            if (!matchCase) {
                TraceManager.addDev("Case insensitive replace all");
                _text = _text.replaceAll("(?iu)" + _find.toLowerCase(), _replace);
            } else {
                TraceManager.addDev("Case sensitive replace all");
                _text = _text.replaceAll(_find, _replace);
            }
            SyntaxDocument sd = editorKit.getSyntaxDocument();
            sd.remove(minBeg, jtp.getDocument().getLength() - minBeg);
            TraceManager.addDev("1");
            sd.insertString(minBeg, _text, sd.getNormalAttributeSet());
            frame.setStatusBarText("All occurences replaced");
        } catch (Exception e) {
            frame.setStatusBarText("Bad replace all command");
            TraceManager.addDev("Bad replace all command: " + e.getMessage());
        }
    }

    public void updateText() {

        try {
            String _text;
            if (showOnlyCurrent.isSelected()) {
                shownText = jtp.getDocument().getText(0, jtp.getDocument().getLength());
                _text = text.substring(0, firstIndex) + shownText + text.substring(lastIndex, text.length());
            } else {
                _text = jtp.getDocument().getText(0, jtp.getDocument().getLength());
            }
            //TraceManager.addDev("Update text");
            /*if (_text.compareTo(ls.getText()) != 0) {
                //TraceManager.addDev("Text updated");
                ls.setText(_text);
                mgui.changeFromEdition();
            }*/
        } catch (BadLocationException ble) {
            TraceManager.addError("Bad location Exception 2");
        }
    }


    public void caretUpdate(CaretEvent e) {
        //TraceManager.addDev("Caret update");
        updateStatusInfo();
        //TraceManager.addDev("position=" + ls.print(position));
    }

    public void updateStatusInfo() {
        int position = jtp.getCaretPosition();
        //TraceManager.addDev("Caret position:" + position);
        StyledDocument stDoc = (StyledDocument) jtp.getDocument();
        int line = stDoc.getRootElements()[0].getElementIndex(position);
        int column = jtp.getCaretPosition() - stDoc.getRootElements()[0].getElement(line).getStartOffset();
        int start = jtp.getSelectionStart();
        int end = jtp.getSelectionEnd();

        String text = "line:" + (line + 1) + " column:" + (column + 1) + "   position in document=" + position;

        if ((start != end) && (start >= 0)) {
            text += "   characters: " + (end - start);

            int wor;
            String t = jtp.getSelectedText();
            if (t == null) {
                wor = 0;
            } else {
                wor = t.trim().split("\\s+").length;
            }
            text += "  words:" + wor;
        }

        frame.setStatusBarText(text);
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public String getEditionText() {
        try {
            return jtp.getDocument().getText(0, jtp.getDocument().getLength());
        } catch (Exception e) {
            return "Exception occured";
        }
    }


    public void insertItemize() {
        //TraceManager.addDev("insert itemize");
        insertText("\\begin{itemize}\n\\item ", "\n\\end{itemize}\n");
    }

    public void insertItem() {
        insertText("\\item ", "");
    }

    public void insertEnumerate() {
        insertText("\\begin{enumerate}\n\\item", "\n\\end{enumerate}\n");
    }


    public void insertFigure() {
        insertText("\\begin{center}\n\\includegraphics[width=10cm]{", "}\n%\\caption{}\n\\end{center}\n");
    }

    public void insertBold() {
        insertText("\\textbf{", "}");
    }

    public void insertItalics() {
        insertText("\\textit{", "}");
    }

    public void insertUnderline() {
        insertText("\\underline{", "}");
    }

    public void alignLeft() {
        insertText("\\begin{flushleft}\n", "\n\\end{flushleft}");
    }

    public void alignRight() {
        insertText("\\begin{flushright}\n", "\n\\end{flushright}");
    }

    public void alignCenter() {
        insertText("\\begin{center}\n", "\n\\end{center}");
    }

    public void alignJustify() {
        insertText("\\begin{justify}\n", "\n\\end{justify}");
    }

    public void insertNote() {
        insertText("%Note: ", "");
    }

    public void insertBlock() {
        insertText("\\begin{block}{title}\n", "\\end{block}");
    }

    public void insertAlertBlock() {
        insertText("\\begin{alertblock}{title}\n", "\\end{alertblock}");
    }

    public void insertExampleBlock() {
        insertText("\\begin{exampleblock}{title}\n", "\\end{exampleblock}");
    }

    public void commentOrUncommentRegion() {
        String selected = jtp.getSelectedText();
        String tmp;
        if (selected != null) {
            if (selected.startsWith("%")) {
                // Uncomment the region
                tmp = Conversion.replaceAllChar(selected, '%', "");
                jtp.replaceSelection(tmp);
            } else {
                tmp = selected;
                tmp = Conversion.replaceAllChar(tmp, '\n', "\n%");
                tmp = Conversion.replaceAllChar(tmp, '\r', "\r%");
                tmp = "%" + tmp;
                jtp.replaceSelection(tmp);
            }
        }


        /*SyntaxDocument sd = editorKit.getSyntaxDocument();
          if (sd != null) {
          try {
          int start = jtp.getSelectionStart();
          int end = jtp.getSelectionEnd();
          if ((start != end) && (start >=0)) {
          sd.insertString(end, text1, sd.getNormalAttributeSet());
          sd.insertString(start, text0, sd.getNormalAttributeSet());
          } else {
          sd.insertString(jtp.getCaretPosition(), text0 + text1, sd.getNormalAttributeSet());
          }
          } catch (BadLocationException ble) {
          TraceManager.addError("Exception when inserting text:" + ble.getMessage());
          }
          }*/
    }


    public void insertTextOfSize(int size) {
        switch (size) {
            case 0:
                insertText("\\tiny{", "}");
                break;
            case 1:
                insertText("\\scriptsize{", "}");
                break;
            case 2:
                insertText("\\footnotesize{", "}");
                break;
            case 3:
                insertText("\\small{", "}");
                break;
            case 4:
                insertText("\\normalsize{", "}");
                break;
            case 5:
                insertText("\\large{", "}");
                break;
            case 6:
                insertText("\\Large{", "}");
                break;
            case 7:
                insertText("\\LARGE{", "}");
                break;
            case 8:
                insertText("\\huge{", "}");
                break;
            default:
                insertText("\\Huge{", "}");
        }

    }

    public void insertReference() {
        insertText("\\ref{", "}");
    }

    public void insertCite() {
        insertText("\\cite{", "}");
    }

    public void insertLabel() {
        insertText("\\label{", "}");
    }

    public void insertSection() {
        insertText("\\section{", "}");
    }

    public void insertSubsection() {
        insertText("\\subsection{", "}");
    }

    public void insertColumns() {
        insertText("\\begin{columns}[c]\n", "\\end{columns}");
    }

    public void insertColumn() {
        insertText("\\column{6cm}\n", "");
    }

    public void insertListing() {
        insertText("\\begin{lstlisting}\n", "\\end{lstlisting}");
    }

    public void insertTabular() {
        insertText("\\begin{tabular}{|l|c|l}\n", "\\end{tabular}");
    }

    public void insertHLine() {
        insertText("\\hline\n", "");
    }

    public void insertRowColor() {
        insertText("\\rowcolor{color}\n", "");
    }

    public void insertFrame() {
        insertText("\\frame{\n\\frametitle{title}\n", "}\n");
    }

    public void insertText(String text0, String text1) {
        //System.out.println("Inserting text:" + s);
        SyntaxDocument sd = editorKit.getSyntaxDocument();
        if (sd != null) {
            try {
                int start = jtp.getSelectionStart();
                int end = jtp.getSelectionEnd();
                if ((start != end) && (start >= 0)) {
                    sd.insertString(end, text1, sd.getNormalAttributeSet());
                    sd.insertString(start, text0, sd.getNormalAttributeSet());
                } else {
                    sd.insertString(jtp.getCaretPosition(), text0 + text1, sd.getNormalAttributeSet());
                }
            } catch (BadLocationException ble) {
                TraceManager.addError("Exception when inserting text:" + ble.getMessage());
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        // Right click
        if (e.getButton() == MouseEvent.BUTTON3) {


        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void makePopup(int _x, int _y, String _w) {
        word = _w;

        JPopupMenu popup = new JPopupMenu();
        addToDic = new JMenuItem("Add \"" + _w + "\" to personal dictionary");
        popup.add(addToDic);
        addToDic.addActionListener(this);

        if (Character.isUpperCase(_w.charAt(0))) {
            String noUpper = Character.toLowerCase(_w.charAt(0)) + _w.substring(1, _w.length());
            addToDicNoUpper = new JMenuItem("Add \"" + noUpper + "\" (and \"" + _w + "\") to personal dictionary");
            addToDicNoUpper.addActionListener(this);
            popup.add(addToDicNoUpper);
        }
        popup.show(jtp, _x, _y);
    }


    public String getWord(int pos, String _onText) {
        // get beginning
        int begin = pos;
        if (!isPartOfAWord(_onText.charAt(pos))) {
            return "";
        }

        TraceManager.addDev("Ckick on char: " + _onText.charAt(pos));

        int index0, index1;
        index0 = begin - 1;
        char c;
        while (index0 > -1) {
            c = _onText.charAt(index0);
            if (!isPartOfAWord(c)) {
                index0++;
                break;
            }
            index0--;
        }

        index1 = begin + 1;
        while (index1 < _onText.length()) {
            c = _onText.charAt(index1);
            if (!isPartOfAWord(c)) {
                break;
            }
            index1++;
        }

        return _onText.substring(index0, index1);

    }

    public boolean isPartOfAWord(char c) {
        if (Character.isLetter(c) || (c == '-')) {
            return true;
        }
        return false;
    }

    public void addCorrespondance(int index, int value, int nb) {
        //TraceManager.addDev("index=" + index + " value=" + value + " nb=" + nb );
        boolean modified = false;
        for (int i = 0; i < nb; i++) {
            modified = modified || addCorrespondance(index - i, value);
        }

        if (modified) {
            nl.repaint();
        }
    }


    // return true in case of modification;
    public boolean addCorrespondance(int index, int value) {
        boolean modified = false;
        //TraceManager.addDev("add coor: index=" + index + "  value=" + value);

        if (index > maxIndex) {
            setMaxIndex(index);
            modified = true;

        }

        if (index < 0) {
            return false;
        }

        if (index >= correspondance.length) {
            int[] newone = new int[correspondance.length + INCREMENT];
            for (int i = 0; i < correspondance.length; i++) {
                newone[i] = correspondance[i];
            }
            correspondance = newone;
        }

        if (correspondance[index] == value) {
            return modified;
        }
        correspondance[index] = value;

        return true;
    }

    /*

     ** Return the current line number at the Caret position.

     */
    public static int getLineAtCaret(JTextComponent component) {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        return root.getElementIndex(caretPosition) + 1;
    }

    /*public int getCorrespondance(int index) {
      return correspondance[index];
      }*/

    public String getCorrespondance(int index) {
        if (index == 0) {
            return "0";
        }

        if (index >= maxIndex) {
            return "";
        }

        int c1 = correspondance[index];
        int c2 = correspondance[index - 1];

        if (c2 == c1) {
            if (index + 1 == correspondance.length) {
                return "e";
            }
            int c3 = correspondance[index + 1];
            if (c3 == c2) {
                return "|";
            }
            return "e";
        }

        //if (c != 0) {
        return String.valueOf(c1);
        //}

        //if (index == 0
    }

    public int getIntCorrespondance(int index) {
        if (index == 0) {
            return -10;
        }

        if (index >= maxIndex) {
            return -11;
        }

        int c1 = correspondance[index];
        int c2 = correspondance[index - 1];

        if (c2 == c1) {
            if (index + 1 == correspondance.length) {
                return -3;
            }
            int c3 = correspondance[index + 1];
            if (c3 == c2) {
                return -4;
            }
            return -3;
        }

        //if (c != 0) {
        return c1;
        //}

        //if (index == 0
    }

    public void setMaxIndex(int _maxIndex) {
        //TraceManager.addDev("Setting max index to = " + _maxIndex);
        maxIndex = _maxIndex;
        /*for(int i=maxIndex+1; i<correspondance.length; i++) {
          correspondance[i] = 0;
          }*/
    }

    public void setMatchCase(boolean _b) {
        if (editorKit != null) {
            editorKit.setMatchCase(_b);
        }
    }

    public int getSelectedCharSet() {
        if (charBox == null) {
            return 0;
        }

        return charBox.getSelectedIndex();
    }

    public void setEncoder(int index) {
        charBox.setSelectedIndex(index);
        TraceManager.addDev("Index set to:" + index);
    }

} // Class
