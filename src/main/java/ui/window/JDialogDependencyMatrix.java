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

import myutil.BytePoint;
import myutil.TableSorter;
import myutil.TraceManager;
import ui.MainGUI;
import ui.TGComponent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Class JDialogDependencyMatrix
 * Dialog for setting requirement tables
 * Creation: 19/02/2009
 *
 * @author Ludovic APVRILLE
 * @version 1.0 19/02/2009
 */
public class JDialogDependencyMatrix extends JDialogBase implements ActionListener, ListSelectionListener {


    private boolean hasBeenCancelled = true;
    private MainGUI mgui;

    private JTabbedPane mainPane;
    private JTextField name;
    private JComboBox<String> rowPanelBox, rowDiagBox, colPanelBox, colDiagBox;
    private JList<String> rowClassList, colClassList;
    private DefaultListModel<String> rowClassListModel, colClassListModel;
    private JButton generateMatrix;

    private String value;
    private String rowDiag;
    private String columnDiag;
    private String rows;
    private String columns;
    private ArrayList<BytePoint> dependencies;

    private JPanel panelMatrix;
    private JLabel labelMatrix;
    private JTable matrix;

    protected JMenuItem edit0, edit1, edit2, edit3;
    protected int selectedRow, selectedCol;
    protected DependencyTableModel dtm;

    /* Creates new form  */
    public JDialogDependencyMatrix(JFrame f, MainGUI _mgui, String title, String _value, String _columnDiag, String _rowDiag,
                                   String _columns, String _rows, ArrayList<BytePoint> _dependencies) {
        super(f, title, true);

        mgui = _mgui;

        value = _value;
        rowDiag = _rowDiag;
        columnDiag = _columnDiag;
        rows = _rows;
        columns = _columns;
        dependencies = new ArrayList<>();
        dependencies.addAll(_dependencies);

        myInitComponents();
        initComponents();
        pack();

        makeMatrix();
    }

    private void myInitComponents() {

    }

    private void initComponents() {
        setFont(new Font("Helvetica", Font.PLAIN, 14));

        Container c = getContentPane();
        mainPane = new JTabbedPane();
        c.add(BorderLayout.CENTER, mainPane);


        // Name panel
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Name:"));
        name = new JTextField("", 30);
        if (value != null) {
            name.setText(value);
        }
        name.setEditable(true);
        namePanel.add(name);
        c.add(BorderLayout.NORTH, namePanel);

        // Tab to generate Matrix

        GridBagLayout gsp = new GridBagLayout();
        GridBagConstraints csp = new GridBagConstraints();
        JPanel panelConfiguration = new JPanel(gsp);


        generateMatrix = new JButton("Generate Dependency Matrix");
        generateMatrix.addActionListener(this);
        checkButtons();


        // rows
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();


        JPanel panel0 = new JPanel(gridbag0);
        panel0.setBorder(new javax.swing.border.TitledBorder("Setting row parameters "));
        c0.gridwidth = 1;
        c0.gridheight = 2;
        c0.weighty = GridBagConstraints.BOTH;
        c0.weightx = GridBagConstraints.BOTH;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row

        panel0.add(new JLabel("Main diagram:"), c0);


        rowPanelBox = new JComboBox<String>();
        panel0.add(rowPanelBox, c0);

        panel0.add(new JLabel("Sub diagram:"), c0);
        rowDiagBox = new JComboBox<>();
        panel0.add(rowDiagBox, c0);

        panel0.add(new JLabel("Elements:"), c0);
        rowClassListModel = new DefaultListModel<>();
        rowClassList = new JList<>(rowClassListModel);
        rowClassList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroll = new JScrollPane(rowClassList);
        //scroll.setPreferredSize(new Dimension(400, 300));
        panel0.add(scroll, c0);

        generateMatrix = new JButton("Generate Dependency Matrix");
        generateMatrix.addActionListener(this);
        checkButtons();

        rowClassList.addListSelectionListener(this);
        rowDiagBox.addActionListener(this);
        rowPanelBox.addActionListener(this);

        ArrayList<String> allPanels = mgui.getAllPanelNames();
        for (String s : allPanels) {
            rowPanelBox.addItem(s);
        }

        csp.gridwidth = 1;
        csp.gridheight = 30;
        csp.weighty = GridBagConstraints.BOTH;
        csp.weightx = GridBagConstraints.BOTH;
        panelConfiguration.add(panel0, csp);


        // Columns
        gridbag0 = new GridBagLayout();
        c0 = new GridBagConstraints();

        panel0 = new JPanel(gridbag0);
        panel0.setBorder(new javax.swing.border.TitledBorder("Setting column parameters "));

        c0.gridwidth = 1;
        c0.gridheight = 2;
        c0.weighty = GridBagConstraints.BOTH;
        c0.weightx = GridBagConstraints.BOTH;
        c0.gridwidth = GridBagConstraints.REMAINDER; //end row

        panel0.add(new JLabel("Main diagram:"), c0);

        colPanelBox = new JComboBox<String>();
        panel0.add(colPanelBox, c0);

        panel0.add(new JLabel("Sub diagram:"), c0);
        colDiagBox = new JComboBox<>();
        panel0.add(colDiagBox, c0);

        panel0.add(new JLabel("Elements:"), c0);
        colClassListModel = new DefaultListModel<>();
        colClassList = new JList<>(colClassListModel);
        colClassList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scroll = new JScrollPane(colClassList);
        //scroll.setPreferredSize(new Dimension(400, 300));
        panel0.add(scroll, c0);


        colClassList.addListSelectionListener(this);
        colDiagBox.addActionListener(this);
        colPanelBox.addActionListener(this);

        allPanels = mgui.getAllPanelNames();
        for (String s : allPanels) {
            colPanelBox.addItem(s);
        }

        csp.gridwidth = GridBagConstraints.REMAINDER; //end row
        panelConfiguration.add(panel0, csp);

        csp.gridheight = 1;
        panelConfiguration.add(generateMatrix, csp);

        if (dependencies.size() == 0) {
            mainPane.add("Configuration", panelConfiguration);
        }


        // Tab to edit Matrix
        // Empty if no matrix
        panelMatrix = new JPanel(new BorderLayout());
        labelMatrix = new JLabel("");
        panelMatrix.add(BorderLayout.NORTH, labelMatrix);
        mainPane.add("Matrix", panelMatrix);


        initButtons(c, this);

    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        // Compare the action command to the known actions.
        if (evt.getSource() == closeButton) {
            closeDialog();
        } else if (evt.getSource() == cancelButton) {
            cancelDialog();
        } else if (evt.getSource() == rowPanelBox) {
            fillDiagBox(rowPanelBox, rowDiagBox);
            checkButtons();
        } else if (evt.getSource() == rowDiagBox) {
            fillClassBox(rowPanelBox, rowDiagBox, rowClassList, rowClassListModel);
            checkButtons();
        } else if (evt.getSource() == colPanelBox) {
            fillDiagBox(colPanelBox, colDiagBox);
            checkButtons();
        } else if (evt.getSource() == colDiagBox) {
            fillClassBox(colPanelBox, colDiagBox, colClassList, colClassListModel);
            checkButtons();
        } else if (evt.getSource() == generateMatrix) {
            prepareMatrixElements();
            makeMatrix();
        } else if (evt.getSource() == edit0) {
            setValueInMatrix(0);
        } else if (evt.getSource() == edit1) {
            setValueInMatrix(1);
        } else if (evt.getSource() == edit2) {
            setValueInMatrix(2);
        } else if (evt.getSource() == edit3) {
            setValueInMatrix(3);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //TraceManager.addDev("List selection listener");
        checkButtons();
    }


    private void closeDialog() {
        hasBeenCancelled = false;
        dispose();
    }

    private void cancelDialog() {
        dispose();
    }


    public boolean hasBeenCancelled() {
        return hasBeenCancelled;
    }

    private void prepareMatrixElements() {
        TraceManager.addDev("Preparing matrix elements");

        // row
        int index1 = rowPanelBox.getSelectedIndex();
        if (index1 == -1) {
            return;
        }

        int index2 = rowDiagBox.getSelectedIndex();
        if (index2 == -1) {
            return;
        }

        rowDiag = (String) rowDiagBox.getSelectedItem();

        List<String> categories = rowClassList.getSelectedValuesList();
        if (categories == null) {
            return;
        }

        ArrayList<TGComponent> allElts = mgui.getAllElementsOfCategories(index1, index2, categories);
        rows = "";
        for (TGComponent tgc : allElts) {
            if (rows.length() > 0) {
                rows += "$";
            }
            String val = tgc.getValue();
            rows += val.substring(0, Math.min(20, val.length())) + "/" + tgc.getUUID();
        }
        TraceManager.addDev("Rows=" + rows);

        // col
        index1 = colPanelBox.getSelectedIndex();
        if (index1 == -1) {
            return;
        }

        index2 = colDiagBox.getSelectedIndex();
        if (index2 == -1) {
            return;
        }
        columnDiag = (String) colDiagBox.getSelectedItem();

        categories = colClassList.getSelectedValuesList();
        if (categories == null) {
            return;
        }

        allElts = mgui.getAllElementsOfCategories(index1, index2, categories);
        columns = "";
        for (TGComponent tgc : allElts) {
            if (columns.length() > 0) {
                columns += "$";
            }
            String val = tgc.getValue();
            columns += val.substring(0, Math.min(20, val.length())) + "/" + tgc.getUUID();
        }
        TraceManager.addDev("Columns=" + columns);

    }


    private void makeMatrix() {
        if ((columnDiag == null) || (columnDiag.length() == 0)) {
            makeMatrixIssue("invalid diagram for column");
            return;
        }

        if ((rowDiag == null) || (rowDiag.length() == 0)) {
            makeMatrixIssue("invalid diagram for row");
            return;
        }

        if ((columns == null) || (columns.length() == 0)) {
            makeMatrixIssue("No element in column");
            return;
        }

        if ((rows == null) || (rows.length() == 0)) {
            makeMatrixIssue("No element in row");
            return;
        }

        //TraceManager.addDev("Going to make the matrix / rows=" + rows + " / cols=" + columns);

        labelMatrix.setText("Matrix between " + rowDiag + " and " + columnDiag);

        dtm = new DependencyTableModel(rows.split("\\$"), columns.split("\\$"),
                dependencies);
        //TableSorter sorterRTM = new TableSorter(dtm);
        matrix = new JTable(dtm);
        matrix.getTableHeader().setReorderingAllowed(false);
        matrix.setCellSelectionEnabled(true);
        matrix.setDragEnabled(true);
        matrix.setDropMode(DropMode.INSERT_ROWS);
        matrix.setTransferHandler(new TableRowTransferHandler(matrix));

        JPopupMenu popup = createDependencyPopup();

        matrix.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { //Button3 is rightclick
                    //TraceManager.addDev("Right click");
                    int r = matrix.rowAtPoint(e.getPoint());
                    int c = matrix.columnAtPoint(e.getPoint());
                    selectedRow = -1;
                    selectedRow = -1;
                    if (r >= 0 && r < matrix.getRowCount() && c >= 0 && c < matrix.getColumnCount()) {
                        //TraceManager.addDev("Popup");
                        selectedRow = r;
                        selectedCol = c;
                        popup.show(matrix, e.getX(), e.getY());
                    }
                }
            }
        });

        
        matrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane jspRTM = new JScrollPane(matrix);
        jspRTM.setWheelScrollingEnabled(true);
        jspRTM.getVerticalScrollBar().setUnitIncrement(10);
        panelMatrix.add(BorderLayout.CENTER, jspRTM);

        if (mainPane != null) {
            mainPane.setSelectedIndex(mainPane.getTabCount()-1);
        }

    }

    private void makeMatrixIssue(String s) {
        if (labelMatrix != null) {
            labelMatrix.setText("Error: " + s);
        }
    }

    private void checkButtons() {
        generateMatrix.setEnabled(
                (rowClassList != null) &&
                        (colClassList != null) &&
                        (rowClassList.getSelectedIndex() != -1) &&
                        (colClassList.getSelectedIndex() != -1) &&
                        (matrix == null));
    }

    private void fillDiagBox(JComboBox<String> panelBox, JComboBox<String> diagBox) {
        //TraceManager.addDev("Fill  Diag Box");
        int index = panelBox.getSelectedIndex();
        if (index == -1) {
            return;
        }

        //TraceManager.addDev("Fill  Diag Box 1");


        diagBox.removeAllItems();
        String[] elts = mgui.getAllDiagNamesFromPanel(index);
        if (elts != null) {
            for (String st : elts) {
                diagBox.addItem(st);
            }
        }
    }

    private void fillClassBox(JComboBox<String> panelBox, JComboBox<String> diagBox, JList<String> classList, DefaultListModel<String> model) {
        //TraceManager.addDev("Fill  Class List");
        int index1 = panelBox.getSelectedIndex();
        if (index1 == -1) {
            return;
        }

        //TraceManager.addDev("Fill  Class List 1");

        int index2 = diagBox.getSelectedIndex();
        if (index2 == -1) {
            return;
        }
        //TraceManager.addDev("Fill  Class List 2");


        HashSet<String> elts = mgui.getAllCategories(index1, index2);
        if (elts == null) {
            return;
        }

        //TraceManager.addDev("Fill  Class List 3");

        model.removeAllElements();
        for (String st : elts) {
            model.addElement(st);
        }

    }



    private JPopupMenu createDependencyPopup() {
        JPopupMenu menu = new JPopupMenu("Change dependency");

        edit0 = new JMenuItem("Remove dependency");
        edit0.setActionCommand("edit0");
        edit0.addActionListener(this);
        menu.add(edit0);

        edit1 = new JMenuItem("->");
        edit1.setActionCommand("edit1");
        edit1.addActionListener(this);
        menu.add(edit1);

        edit2 = new JMenuItem("<-");
        edit2.setActionCommand("edit2");
        edit2.addActionListener(this);
        menu.add(edit2);

        edit3 = new JMenuItem("<->");
        edit3.setActionCommand("edit3");
        edit3.addActionListener(this);
        menu.add(edit3);

        return menu;
    }

    public void setValueInMatrix(int value) {
        TraceManager.addDev("set value in matrix" + value);
        //selectedRow = matrix.getSelectedRow();
        //selectedCol = matrix.getSelectedColumn();
        if ((selectedCol >= 0) && (selectedRow >= 0) & dtm != null) {
            dtm.mySetValueAt(value, selectedRow, selectedCol-1);
            matrix.repaint();
        }
    }

    // Getting information of matrix
    public boolean hasMatrix() {
        return matrix != null;
    }

    public String getName() {
        if (name == null) {
            return "";
        }
        return name.getText();
    }

    public String getRowDiag() {
        return rowDiag;
    }

    public String getColumnDiag() {
        return columnDiag;
    }

    public String getRows() {
        if (dtm == null) {
            return rows;
        }

        ArrayList<String> rowsN = dtm.getRows();
        String s = "";
        for(String r: rowsN) {
            if (s.length() > 0) {
                s += "$";
            }
            s += r;
        }

        return s;
    }

    public String getColumns() {
        if (dtm == null) {
            return columns;
        }

        ArrayList<String> colsN = dtm.getCols();
        String s = "";
        for(String r: colsN) {
            if (s.length() > 0) {
                s += "$";
            }
            s += r;
        }

        return s;
    }

    public ArrayList<BytePoint> getDependencies() {

        if (dtm == null) {
            return null;
        }

        return dtm.getNonNullPoints();
    }




}