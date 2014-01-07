/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
* Class JFrameAvatarInteractiveSimulation
* Creation: 21/01/2011
* version 1.0 21/01/2011
* @author Ludovic APVRILLE
* @see
*/

package ui.avatarinteractivesimulation;

//import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


import myutil.*;
import ui.*;
import ui.file.*;

import avatartranslator.*;
import avatartranslator.directsimulation.*;


public	class JFrameAvatarInteractiveSimulation extends JFrame implements AvatarSimulationInteraction, ActionListener, Runnable, MouseListener, ItemListener, ListSelectionListener, WindowListener/*, StoppableGUIElement, SteppedAlgorithm, ExternalCall*/ {
	private static int TRACED_TRANSACTIONS = 1000;
	private static int LAST_TRANSACTIONS = 0;
	
	
	private static String buttonStartS = "Start simulator";
	private static String buttonStopAndCloseS = "Stop simulator and close";
	
	private static int NOT_STARTED = 0;
	private static int STARTED = 1;
	
	private static long SPACE_UPDATE_TIME = 100;
	
	private Frame f;
	private MainGUI mgui;
	private String title;
	
	protected JButton buttonClose, buttonStart, buttonStopAndClose;
	protected JTextArea jta;
	protected JScrollPane jsp;
	
	protected Thread t;
	protected int threadMode = 0;
	protected boolean go;
	
	// Control command
	protected JButton resetCommand, runCommand, StopCommand;
	protected AvatarMainCommandsToolBar mctb;
	protected AvatarSaveCommandsToolBar sctb;
	//protected StateCommandsToolBar stctb;
	//protected BenchmarkCommandsToolBar bctb;
	
	
	// Commands
	JPanel main, mainTop, commands, save, state, infos; 
	//outputs, cpuPanel; // from MGUI
	JCheckBox latex, debug, animate, diploids, update, openDiagram, animateWithInfo, executeEmptyTransition, executeStateEntering, traceInSD;
	JTabbedPane commandTab, infoTab;
	protected JTextField displayedTransactionsText, lastTransactionsText;
	protected JTextField paramMainCommand;
	protected JTextField saveFileName;
	protected JTextField stateFileName;
	protected JTextField benchmarkFileName;
	//protected JComboBox cpus, busses, mems, tasks, chans;
	
	//List of transactions
	
	private JList listPendingTransactions;
	private TGComponent selectedComponentForTransaction;
	private AvatarSimulationBlock previousBlock;
	
	private int invokedLater = 0;
	
	
	
	//private String[] cpuIDs, busIDs, memIDs, taskIDs, chanIDs;
	
	// Status elements
	JLabel statuss, status, time, info;
	
	// Task elements
	//TaskVariableTableModel tvtm;
	//JButton updateTaskVariableInformationButton;
	//private JScrollPane jspTaskVariableInfo;
	
	// Breakpoints
	//JPanelBreakPoints jpbp;
	
	// Set variables
	//JPanelSetVariables jpsv;
	
	// Blocks
	private JPanel blockPanel;
	private BlockTableModel blocktm;
	private JScrollPane jspBlockInfo;
	
	// Variables
	private JPanel variablePanel;
	private VariableTableModel variabletm;
	private JScrollPane jspVariableInfo;
	
	// Transactions
	private JPanel transactionPanel;
	private TransactionTableModel transactiontm;
	private JScrollPane jspTransactionInfo;
	
	// DisplayedBlocks
	private JPanel displayedBlockPanel;
	private JScrollPane jspDisplayedBlocks;
	private Vector<JCheckBox> displayedBlocks;
	
	// RandomBlocks
	private JPanel randomPanel;
	private JTextField randomValue;
	private JCheckBox imposeRandom;
	private JButton updateRandom;
	
	// Asynchronous transactions
	private JPanel asyncPanel;
	private JComboBox comboFIFOs;
	private Vector<AvatarInteractiveSimulationFIFOData> fifos;
	private JButton delete, up, down;
	private JList asyncmsgs;
	private int nbOfAsyncMsgs;
	
	// Sequence Diagram
	private AvatarSpecificationSimulationSDPanel sdpanel;
	
	//JButton updateBlockInformationButton;
	
	
	private int busyMode = 0; // Mode of AvatarSpecificationSimulation
	
	// For managing actions
	public	AvatarInteractiveSimulationActions [] actions;
    public	MouseHandler mouseHandler;
    public  KeyListener keyHandler;
	
	
	/*private Hashtable <Integer, String> valueTable;
	private Hashtable <Integer, Integer> rowTable;
	
	private Hashtable <Integer, Integer> runningTable;
	private Hashtable <String, String> diagramTable;*/
	
	
	// new
	private AvatarSpecification avspec;
	private AvatarSpecificationSimulation ass;
	private Thread simulationThread;
	private boolean resetThread;
	private boolean killThread;
	
	private LinkedList<TGComponent> runningTGComponents;
	private int nbOfAllExecutedElements = 0;
	
	private long previousTime;
	
	private boolean simulationRunning;
	
	public JFrameAvatarInteractiveSimulation(Frame _f, MainGUI _mgui, String _title, AvatarSpecification _avspec) {
		super(_title);
		
		f = _f;
		mgui = _mgui;
		title = _title;
		avspec = _avspec;
		
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE );
		setIconImage(IconManager.img5100);
		setBackground(Color.WHITE);

		try {
			setBackground(new Color(50, 40, 40, 200));
		} catch (Exception e) {
			setBackground(new Color(50, 40, 40));
		}
		initActions();
		
		initSimulation();
		
		makeComponents();
		setComponents();
	}
	
	private void initSimulation() {
		TraceManager.addDev("Init simulation");
		runningTGComponents = new LinkedList<TGComponent>();
		nbOfAllExecutedElements = 0;
		resetMetElements();
		ass = new AvatarSpecificationSimulation(avspec, this);
		//ass.initialize();
		simulationRunning = false;
		simulationThread = new Thread(this);
		simulationThread.start();
		TraceManager.addDev("Init simulation done");
	}
	
	public synchronized void setSimulationRunning() {
		simulationRunning = true;
	}
	
	public synchronized void stopSimulationRunning() {
		simulationRunning = false;
	}
	
	
	public synchronized void updatePending() {
		invokedLater = 0;
		//TraceManager.addDev("Simulation is already running -> beg of code:" + Thread.currentThread());
		if (ass == null) {
			return;
		}
		
		try {
		
			Vector<AvatarSimulationPendingTransaction> ll = (Vector<AvatarSimulationPendingTransaction>)(ass.getPendingTransitions().clone());
		
			listPendingTransactions.clearSelection();
			selectedComponentForTransaction = null;
			if (ll != null) {
				listPendingTransactions.setListData(ll);
				int random = (int)(Math.floor((Math.random()*ll.size())));
				listPendingTransactions.setSelectedIndex(random);
			} else {
				listPendingTransactions.setListData(new Vector<AvatarSimulationPendingTransaction>());
			}
		} catch (Exception e) {}
		
		//TraceManager.addDev("Simulation is already running -> end of code:" + Thread.currentThread());
	}
	
	public void run() {
		if (simulationRunning == true) {
			updatePending();
			
		} else {
			setSimulationRunning();
			previousTime = System.currentTimeMillis();
			ass.runSimulation();
			TraceManager.addDev("Simulation thread ended");
			stopSimulationRunning();
		}
	}
	
	
	
	private JLabel createStatusBar()  {
        statuss = new JLabel("Ready...");
		statuss.setForeground(ColorManager.InteractiveSimulationText);
        statuss.setBorder(BorderFactory.createEtchedBorder());
        return statuss;
    }
	
	public void makeComponents() {
		JPanel jp01, jp02;
		//jp01.setPreferredSize(new Dimension(375, 400));
		GridBagLayout gridbag01;
		GridBagConstraints c01 ;
		
		//cp = new CommandParser();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());
		//framePanel.setBackground(ColorManager.InteractiveSimulationBackground);
		//framePanel.setForeground(new Color(255, 166, 38));		
		
		//System.out.println("Button start created");
		//buttonStart = new JButton(actions[InteractiveSimulationActions.ACT_RUN_SIMU]);
		buttonStopAndClose = new JButton(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL]);
		//buttonStopAndClose = new JButton(buttonStopAndCloseS, IconManager.imgic27);
		
		
		// statusBar
        statuss = createStatusBar();
		framePanel.add(statuss, BorderLayout.SOUTH);
        
        // Mouse handler
        mouseHandler = new MouseHandler(statuss);
		
		JPanel mainpanel = new JPanel(new BorderLayout());
		//mainpanel.setBackground(ColorManager.InteractiveSimulationBackground);
		framePanel.add(mainpanel, BorderLayout.CENTER);
		
		JPanel jp = new JPanel();
		//jp.setBackground(ColorManager.InteractiveSimulationBackground);
		//jp.setPreferredSize(new Dimension(800, 75));
		//jp.add(buttonStart);
		jp.add(buttonStopAndClose);
		mainpanel.add(jp, BorderLayout.NORTH);
		
		
		//GridBagLayout gridbag02 = new GridBagLayout();
		//GridBagConstraints c02 = new GridBagConstraints();
		//mainTop = new JPanel(gridbag02);
		//mainTop.setPreferredSize(new Dimension(800, 375));
		//c02.gridheight = 1;
		//c02.weighty = 1.0;
		//c02.weightx = 1.0;
		//c02.gridwidth = 1; 
		//c02.fill = GridBagConstraints.BOTH;
		//c02.gridheight = 1;
		
		// Ouput SD
		/*jta = new ScrolledJTextArea();
		jta.setBackground(ColorManager.InteractiveSimulationJTABackground);
		jta.setForeground(ColorManager.InteractiveSimulationJTAForeground);
		jta.setMinimumSize(new Dimension(800, 200));
		jta.setRows(15);
		//jta.setMaximumSize(new Dimension(800, 500));
		jta.setEditable(false);
		jta.setMargin(new Insets(10, 10, 10, 10));
		jta.setTabSize(3);
		jta.append("Interactive simulation ready to run\n");
		Font f = new Font("Courrier", Font.BOLD, 12);
		jta.setFont(f);
		jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.setViewportBorder(BorderFactory.createLineBorder(ColorManager.InteractiveSimulationBackground));
		
        //jsp.setColumnHeaderView(100);
        //jsp.setRowHeaderView(30);
		//jsp.setMaximumSize(new Dimension(800, 400));*/
		JPanel lowerPartPanel = new JPanel(); lowerPartPanel.setLayout(new BorderLayout());
		sdpanel = new AvatarSpecificationSimulationSDPanel(ass);
        //ass.setName("Interaction Overview Diagram");
        JScrollPane jsp	= new JScrollPane(sdpanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sdpanel.setMyScrollPanel(jsp);
        jsp.setWheelScrollingEnabled(true);
		//jsp.setPreferredSize(new Dimension(800, 400));
        jsp.getVerticalScrollBar().setUnitIncrement(mgui.INCREMENT);
		lowerPartPanel.add(jsp, BorderLayout.CENTER);
	

		
		// Commands
		commands = new JPanel(new BorderLayout());
		//commands.setFloatable(true);
		//commands.setMinimumSize(new Dimension(300, 250));
		commands.setBorder(new javax.swing.border.TitledBorder("Commands"));
		
		

		
		commandTab = new JTabbedPane();
		commands.add(commandTab, BorderLayout.CENTER);
		//commandTab.setBackground(ColorManager.InteractiveSimulationBackground);
		
		
		// Control commands
		jp01 = new JPanel(new BorderLayout());
		commandTab.addTab("Control", null, jp01, "Main control commands");
		//jp01.setMinimumSize(new Dimension(375, 400));
		//gridbag01 = new GridBagLayout();
		//c01 = new GridBagConstraints();
		//jp01.setLayout(gridbag01);
		
		mctb = new AvatarMainCommandsToolBar(this);
		jp01.add(mctb, BorderLayout.NORTH);
		
		jp02 = new JPanel();
		//jp01.setPreferredSize(new Dimension(375, 400));
		gridbag01 = new GridBagLayout();
		c01 = new GridBagConstraints();
		jp02.setLayout(gridbag01);
		
		c01.gridheight = 1;
		c01.weighty = 1.0;
		c01.weightx = 1.0;
		c01.gridwidth = 1; 
		c01.fill = GridBagConstraints.BOTH;
		c01.gridheight = 1;
		
		jp02.add(new JLabel("Command parameter: "), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		paramMainCommand = new JTextField("1", 30);
		jp02.add(paramMainCommand, c01);
		// list of pending transactions
        JPanel panellpt = new JPanel();
        panellpt.setLayout(new BorderLayout());
        panellpt.setBorder(new javax.swing.border.TitledBorder("Pending transactions"));
		
        listPendingTransactions = new JList();
        //listPendingTransactions.setPreferredSize(new Dimension(400, 300));
        listPendingTransactions.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        listPendingTransactions.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listPendingTransactions);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panellpt.add(scrollPane1);
		jp02.add(panellpt, c01);
		jp01.add(jp02, BorderLayout.CENTER);
		
		
		// Save commands
		jp01 = new JPanel(new BorderLayout());
		
		commandTab.addTab("Save trace", null, jp01, "Save commands");
		
		sctb = new AvatarSaveCommandsToolBar(this);
		jp01.add(sctb, BorderLayout.NORTH);
		
		jp02 = new JPanel();
		gridbag01 = new GridBagLayout();
		c01 = new GridBagConstraints();
		jp02.setLayout(gridbag01);
		
		c01.gridheight = 1;
		c01.weighty = 1.0;
		c01.weightx = 1.0;
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		c01.fill = GridBagConstraints.BOTH;
		c01.gridheight = 1;
		
		jp02.add(new JLabel("File name:"), c01);
		saveFileName = new JTextField(30);
		jp02.add(saveFileName, c01);
		
		jp01.add(jp02, BorderLayout.CENTER);
		
		// Benchmark commands
		/*jp01 = new JPanel(new BorderLayout());
		
		commandTab.addTab("Benchmarks", null, jp01, "Benchmarks");
		
		bctb = new BenchmarkCommandsToolBar(this);
		jp01.add(bctb, BorderLayout.NORTH);
		
		jp02 = new JPanel();
		gridbag01 = new GridBagLayout();
		c01 = new GridBagConstraints();
		jp02.setLayout(gridbag01);
		
		c01.gridheight = 1;
		c01.weighty = 1.0;
		c01.weightx = 1.0;
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		c01.fill = GridBagConstraints.BOTH;
		c01.gridheight = 1;
		
		jp02.add(new JLabel("File name:"), c01);
		benchmarkFileName = new JTextField(30);
		jp02.add(benchmarkFileName, c01);
		
		jp01.add(jp02, BorderLayout.CENTER);*/
		
		
		//Info
		infos = new JPanel(new BorderLayout());
		infos.setPreferredSize(new Dimension(300, 200));
		//infos.setPreferredSize(new Dimension(400, 450));
		infos.setBorder(new javax.swing.border.TitledBorder("Simulation information"));
		
		
		// Main panels
		//mainTop.add(infos, c02);
		//mainTop.add(commands, c02);
		JSplitPane mainTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, commands, infos);
		mainTop.setResizeWeight(0.5);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainTop, lowerPartPanel);
		split.setResizeWeight(0.5);
		mainpanel.add(split, BorderLayout.CENTER);
		
		//c02.gridwidth = GridBagConstraints.REMAINDER; //end row
		
		
		infoTab = new JTabbedPane();
		infoTab.setPreferredSize(new Dimension(300, 200));
		infos.add(infoTab, BorderLayout.CENTER);
		
		// Simulation time
		jp02 = new JPanel();
		infos.add(jp02, BorderLayout.NORTH);
		jp02.add(new JLabel("Status:"));
		status = new JLabel("Unknown");
		status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
		jp02.add(status);
		jp02.add(new JLabel(" "));
		jp02.add(new JLabel("Time:"));
		time = new JLabel("0");
		time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
		jp02.add(time);
		jp02.add(new JLabel(" "));
		jp02.add(new JLabel("Nb of transactions:"));
		info = new JLabel("Unknown");
		info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
		jp02.add(info);
		
		// Options
		jp01 = new JPanel();
		//jp01.setMinimumSize(new Dimension(375, 400));
		//jp01.setPreferredSize(new Dimension(375, 400));
		gridbag01 = new GridBagLayout();
		c01 = new GridBagConstraints();
		jp01.setLayout(gridbag01);
		
		
		// INFORMATION
		
		infoTab.addTab("Options", null, jp01, "Options on simulation");
		
		c01.gridheight = 1;
		c01.weighty = 1.0;
		c01.weightx = 1.0;
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		c01.fill = GridBagConstraints.BOTH;
		c01.gridheight = 1;
		
		//jp01.add(new JLabel(" "), c01);
		/*latex = new JCheckBox("Generate info in Latex format");
		jp01.add(latex, c01);*/
		/*debug = new JCheckBox("Print messages received from server");
		jp01.add(debug, c01);*/
		animate = new JCheckBox("Animate UML diagrams");
		jp01.add(animate, c01);
		diploids = new JCheckBox("Show AVATAR IDs on UML diagrams");
		jp01.add(diploids, c01);
		diploids.addItemListener(this);
		diploids.setSelected(false);
		animateWithInfo = new JCheckBox("Show transaction progression on UML diagrams");
		//jp01.add(animateWithInfo, c01);
		animateWithInfo.addItemListener(this);
		animateWithInfo.setSelected(true);
		openDiagram = new JCheckBox("Automatically open active state machine diagram");
		jp01.add(openDiagram, c01);
		openDiagram.setSelected(true);
		//update = new JCheckBox("Automatically update information (variables)");
		//jp01.add(update, c01);
		//update.addItemListener(this);
		//update.setSelected(true);
		
		animate.addItemListener(this);
		animate.setSelected(true);
		
		
		c01.gridwidth = 1; 
		traceInSD = new JCheckBox("Trace in sequence diagram");
		jp01.add(traceInSD, c01);
		traceInSD.addItemListener(this);
		traceInSD.setSelected(true);
		jp01.add(new JLabel("# of transactions:"), c01);
		displayedTransactionsText = new JTextField(""+TRACED_TRANSACTIONS, 10);
		jp01.add(displayedTransactionsText, c01);
		//displayedTransactionsText.addActionListener(this);
		displayedTransactionsText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					warn();
				}
				public void removeUpdate(DocumentEvent e) {
					warn();
				}
				public void insertUpdate(DocumentEvent e) {
					warn();
				}
				
				public void warn() {
					try {
					int nb = Integer.parseInt(displayedTransactionsText.getText());
					if ((nb > 0) && (nb <= 10000)){
						statuss.setText("Nb of traced transactions modified to: " + nb);
						if (sdpanel != null) {
							sdpanel.setNbOfDrawnTransactions(nb);
							if (sdpanel.isVisible()) {
								sdpanel.repaint();
							}
						}
						return;
					}
					} catch (Exception e) {
					}
					statuss.setText("Unknown / bad number: " + displayedTransactionsText.getText());
				}
		});
		jp01.add(new JLabel("Index of last transaction:"), c01);
		c01.gridwidth = GridBagConstraints.REMAINDER; //end row
		lastTransactionsText = new JTextField(""+LAST_TRANSACTIONS, 10);
		jp01.add(lastTransactionsText, c01);
		//displayedTransactionsText.addActionListener(this);
		lastTransactionsText.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					warn();
				}
				public void removeUpdate(DocumentEvent e) {
					warn();
				}
				public void insertUpdate(DocumentEvent e) {
					warn();
				}
				
				public void warn() {
					try {
					int nb = Integer.parseInt(lastTransactionsText.getText());
					if (nb > -1){
						statuss.setText("Index of last transation modified to: " + nb);
						if (sdpanel != null) {
							sdpanel.setLastDrawnTransactions(nb);
							if (sdpanel.isVisible()) {
								sdpanel.repaint();
							}
						}
						return;
					}
					} catch (Exception e) {
					}
					statuss.setText("Unknown / bad number: " + lastTransactionsText.getText());
				}
		});
		
		
		
		
		executeEmptyTransition = new JCheckBox("Automatically execute empty transitions");
		jp01.add(executeEmptyTransition, c01);
		executeEmptyTransition.setSelected(true);
		executeEmptyTransition.addItemListener(this);
		ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
		
		executeStateEntering = new JCheckBox("Automatically enter states");
		jp01.add(executeStateEntering, c01);
		executeStateEntering.setSelected(true);
		executeStateEntering.addItemListener(this);
		ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
		
		
		TableSorter sorterPI;
		JTable jtablePI;
		
		// Breakpoints
		/*jpbp = new JPanelBreakPoints(this, points);
		infoTab.addTab("Breakpoints", null, jpbp, "List of active breakpoints");*/
		
		// blocks
		blockPanel = new JPanel();
		blockPanel.setLayout(new BorderLayout());
		infoTab.addTab("Blocks", IconManager.imgic1202, blockPanel, "Current state of blocks");
		blocktm = new BlockTableModel(ass);
	
		sorterPI = new TableSorter(blocktm);
		jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(80);
		((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(150);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspBlockInfo = new JScrollPane(jtablePI);
		jspBlockInfo.setWheelScrollingEnabled(true);
		jspBlockInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspBlockInfo.setPreferredSize(new Dimension(250, 300));
		blockPanel.add(jspBlockInfo, BorderLayout.CENTER);
		
		// Variables
		variablePanel = new JPanel();
		variablePanel.setLayout(new BorderLayout());
		infoTab.addTab("Variables", IconManager.imgic1202, variablePanel, "Variables");
		variabletm = new VariableTableModel(ass);
	
		sorterPI = new TableSorter(variabletm);
		jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspVariableInfo = new JScrollPane(jtablePI);
		jspVariableInfo.setWheelScrollingEnabled(true);
		jspVariableInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspVariableInfo.setPreferredSize(new Dimension(250, 300));
		variablePanel.add(jspVariableInfo, BorderLayout.CENTER);
		//updateTaskInformationButton = new JButton(actions[InteractiveSimulationActions.ACT_UPDATE_TASKS]);
		//taskPanel.add(updateTaskInformationButton, BorderLayout.SOUTH);
		
		// Transactions
		transactionPanel = new JPanel();
		transactionPanel.setLayout(new BorderLayout());
		infoTab.addTab("Transactions", IconManager.imgic1202, transactionPanel, "Transactions");
		transactiontm = new TransactionTableModel(ass);
	
		/*sorterPI = new TableSorter(transactiontm);
		jtablePI = new JTable(sorterPI);
		sorterPI.setTableHeader(jtablePI.getTableHeader());
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(50);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(5)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(6)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspTransactionInfo = new JScrollPane(jtablePI);
		jspTransactionInfo.setWheelScrollingEnabled(true);
		jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspTransactionInfo.setPreferredSize(new Dimension(250, 300));
		transactionPanel.add(jspTransactionInfo, BorderLayout.CENTER);*/
		
		jtablePI = new JTable(transactiontm);
		((jtablePI.getColumnModel()).getColumn(0)).setPreferredWidth(50);
		((jtablePI.getColumnModel()).getColumn(1)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(2)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(3)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(4)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(5)).setPreferredWidth(100);
		((jtablePI.getColumnModel()).getColumn(6)).setPreferredWidth(75);
		((jtablePI.getColumnModel()).getColumn(7)).setPreferredWidth(100);
		jtablePI.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspTransactionInfo = new JScrollPane(jtablePI);
		jspTransactionInfo.setWheelScrollingEnabled(true);
		jspTransactionInfo.getVerticalScrollBar().setUnitIncrement(10);
		jspTransactionInfo.setPreferredSize(new Dimension(250, 300));
		transactionPanel.add(jspTransactionInfo, BorderLayout.CENTER);
		
		
		// Displayed blocks
		displayedBlockPanel = new JPanel();
		displayedBlockPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridheight = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		
		displayedBlocks = new Vector<JCheckBox>();
		for(AvatarSimulationBlock block: ass.getSimulationBlocks()) {
			JCheckBox jcb = new JCheckBox(block.getName(), true);
			block.selected = true;
			jcb.addActionListener(this);
			TraceManager.addDev("Adding block: " + block);
			displayedBlocks.add(jcb);
			displayedBlockPanel.add(jcb, c);
		}
		ass.computeSelectedSimulationBlocks();
		
		jspDisplayedBlocks = new JScrollPane(displayedBlockPanel);
		jspDisplayedBlocks.setWheelScrollingEnabled(true);
		jspDisplayedBlocks.getVerticalScrollBar().setUnitIncrement(10);
		jspDisplayedBlocks.setPreferredSize(new Dimension(250, 300));
		infoTab.addTab("Displayed blocks", IconManager.imgic1202, jspDisplayedBlocks, "Displayed blocks");
		
		//Randomness
		randomPanel = new JPanel();
		randomPanel.setLayout(new GridBagLayout());
		GridBagConstraints cr = new GridBagConstraints();
		
		cr.gridheight = 1;
		cr.weighty = 1.0;
		cr.weightx = 1.0;
		cr.gridwidth = GridBagConstraints.REMAINDER; //end row
		cr.fill = GridBagConstraints.BOTH;
		cr.gridheight = 1;
		imposeRandom = new JCheckBox("Force random value");
		imposeRandom.addActionListener(this);
		randomPanel.add(imposeRandom, cr);
		cr.gridwidth = 1;
		cr.fill = GridBagConstraints.HORIZONTAL;
		randomPanel.add(new JLabel("value:"), cr);
		randomValue = new JTextField();
		randomPanel.add(randomValue, cr);
		cr.gridwidth = GridBagConstraints.REMAINDER; //end row
		updateRandom = new JButton("Update", IconManager.imgic16);
		updateRandom.setToolTipText("Update random value");
		randomPanel.add(updateRandom, cr);
		updateRandom.addActionListener(this);
		
		infoTab.addTab("Randomness", IconManager.imgic1202, randomPanel, "Randomness");
		
		imposeRandom.setSelected(false);
		randomValue.setEnabled(false);
		
		
		//Asynchronous
		
		// Making vector of fifos
		AvatarInteractiveSimulationFIFOData fifo;
		fifos = new Vector<AvatarInteractiveSimulationFIFOData>();
		for(AvatarRelation ar: avspec.getRelations()) {
			if (ar.isAsynchronous()) {
				fifo = new AvatarInteractiveSimulationFIFOData(ar);
				fifos.add(fifo);
			}
		}
		
		
		asyncPanel = new JPanel();
		asyncPanel.setLayout(new GridBagLayout());
		GridBagConstraints ca = new GridBagConstraints();
		
		ca.gridheight = 1;
		ca.weighty = 1.0;
		ca.weightx = 1.0;
		ca.gridwidth = GridBagConstraints.REMAINDER; //end row
		ca.fill = GridBagConstraints.HORIZONTAL;
		ca.gridheight = 1;
		
		comboFIFOs = new JComboBox(fifos);
		comboFIFOs.addActionListener(this);
		asyncPanel.add(comboFIFOs, ca);
		ca.fill = GridBagConstraints.BOTH;

		JPanel borderjlist = new JPanel(new GridBagLayout());
		GridBagConstraints cb = new GridBagConstraints();
		
		cb.gridheight = 1;
		cb.weighty = 1.0;
		cb.weightx = 1.0;
		cb.gridwidth = GridBagConstraints.REMAINDER; //end row
		cb.fill = GridBagConstraints.BOTH;
		cb.gridheight = 1;
		borderjlist.setBorder(new javax.swing.border.TitledBorder("Top of selected FIFO:"));
		asyncmsgs = new JList();
		JScrollPane pane = new JScrollPane(asyncmsgs);
		borderjlist.add(pane, cb);
		asyncPanel.add(borderjlist, ca);
		infoTab.addTab("Asynch. msg", IconManager.imgic1202, asyncPanel, "Asynch. msg.");
		
		ca.fill = GridBagConstraints.NONE;
		ca.gridwidth = 1; 
		delete = new JButton(actions[AvatarInteractiveSimulationActions.ACT_DELETE_ASYNC_MSG]);
		//delete.addActionListener(this);
		asyncPanel.add(delete, ca);
		up = new JButton(actions[AvatarInteractiveSimulationActions.ACT_UP_ASYNC_MSG]);
		//up.addActionListener(this);
		asyncPanel.add(up, ca);
		ca.gridwidth = GridBagConstraints.REMAINDER; //end row
		down = new JButton(actions[AvatarInteractiveSimulationActions.ACT_DOWN_ASYNC_MSG]);
		//down.addActionListener(this);
		asyncPanel.add(down, ca);
		setDeleteUpDown();
		asyncmsgs.addListSelectionListener(this);
		
		
		pack();
		
	}
	
	private	void initActions() {
        actions = new AvatarInteractiveSimulationActions[AvatarInteractiveSimulationActions.NB_ACTION];
        for(int	i=0; i<AvatarInteractiveSimulationActions.NB_ACTION; i++) {
            actions[i] = new AvatarInteractiveSimulationActions(i);
            actions[i].addActionListener(this);
            //actions[i].addKeyListener(this);
        }
		
		//cpuIDs = makeCPUIDs();
		//busIDs = makeBusIDs();
		//memIDs = makeMemIDs();
		//taskIDs = makeTasksIDs();
		//chanIDs = makeChanIDs();
    }
	
	
	
	public void setComponents() {
		setAll();
		animateDiagrams();
		animateFutureTransactions();
	}
	
	public void close() {
		//killThread();
		if (ass != null) {
			ass.killSimulation();
		}
		dispose();
		setVisible(false);
		runningTGComponents.clear();
		resetMetElements();
		mgui.setAvatarAnimate(false);
	}
	
	public void runSimulation() {
		previousTime = System.currentTimeMillis();
		if (ass != null) {
			ass.setNbOfCommands(AvatarSpecificationSimulation.MAX_TRANSACTION_IN_A_ROW);
			ass.goSimulation();
		}
	}
	
	public void runXCommands() {
		String txt = paramMainCommand.getText();
		int nb;
		try {
			nb = Math.max(1, Integer.decode(txt).intValue());
		} catch (Exception e) {
			nb = 1;
		}
		if (ass != null) {
			ass.setNbOfCommands(nb);
			previousTime = System.currentTimeMillis();
			ass.goSimulation();
		}
	}
	
	public void stopSimulation() {
		//previousTime = System.currentTimeMillis();
		if (ass != null) {
			ass.stopSimulation();
		}
	}
	
	public void resetSimulation() {
		//resetThread();
		previousTime = System.currentTimeMillis();
		if (ass != null) {
			ass.resetSimulation();
		}
		//ass.killSimulation();
	}
	
	public void backwardOneTransaction() {
		previousTime = System.currentTimeMillis();
		ass.backOneTransactionBunch(); 
	}
	
	
	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e){
		/*if (e.getSource() == sendTextCommand) {
			if (sendTextCommand.isEnabled()) {
				sendCommand();
			}
		} else if (e.getSource() == printHelpTextCommands) {
			helpOnCommand();
		} else if (e.getSource() == listTextCommands) {
			listTextCommands();
		} /*else if (e.getSource() == resetCommand) {
		sendCommand("reset");
		} else if (e.getSource() == runCommand) {
		sendCommand("run-to-next-breakpoint");
		} else if (e.getSource() == StopCommand) {
		sendCommand("stop");
		}*/
	}
	
	// Command management
	
	public void printSeparator() {
		jta.append("-------------------------------------------------------------\n");
	}
	

	
	public void setMode(int _mode) {
		busyMode = _mode;
		//TraceManager.addDev("****************** mode set to " + busyMode);
		if (ass != null) {
			if (ass.getPendingTransitions() != null) {
				//TraceManager.addDev("# of Pending transactions: " + ass.getPendingTransitions().size());
			}
		}
		setAll();
		
		
		// Diagram animation?
		if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
			//TraceManager.addDev("Animating");
			updateMetElements();
			updateTransactionsTable();
			updateAsynchronousChannels();
			animateDiagrams();
		}
		
		animateFutureTransactions();
		
	}
	
	public void updateTransactionAndTime(int _nbOfTransactions, long clockValue) {
		//TraceManager.addDev("Update transactions and time");
		long timeNow = System.currentTimeMillis();
		if (timeNow - previousTime > SPACE_UPDATE_TIME) {
			previousTime = timeNow;
			setLabelColors();
		}
	}
	
	public void setAll() {
		boolean b= true;
		
		switch(busyMode) {
		case AvatarSpecificationSimulation.DONT_EXECUTE:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			
			b = true;
			break;
		case AvatarSpecificationSimulation.GATHER:
		case AvatarSpecificationSimulation.EXECUTE:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(true);
			b = false;
			break;
		case AvatarSpecificationSimulation.TERMINATED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		case AvatarSpecificationSimulation.INITIALIZE:
		case AvatarSpecificationSimulation.RESET:
		case AvatarSpecificationSimulation.KILLED:
			actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].setEnabled(false);
			actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].setEnabled(true);
			actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].setEnabled(false);
			b = true;
			break;
		}
		
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_SVG].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_PRINT_BENCHMARK].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_SAVE_BENCHMARK].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_ZOOM_IN].setEnabled(b);
		actions[AvatarInteractiveSimulationActions.ACT_ZOOM_OUT].setEnabled(b);
		
		setLabelColors();
		
		if ((blockPanel != null) && (blockPanel.isVisible())) {
			blockPanel.repaint();
		}
		
		if ((variablePanel != null) && (variablePanel.isVisible())) {
			variablePanel.repaint();
		}
		
		if ((sdpanel != null) && (sdpanel.isVisible())) {
			sdpanel.repaint();
			sdpanel.scrollToLowerPosition();
		}
		
		// Delete, up, down
		setDeleteUpDown(b);
	}
	
	public boolean isBusy() {
		if (busyMode == AvatarSpecificationSimulation.EXECUTE) {
			return true;
		}
		
		if (busyMode == AvatarSpecificationSimulation.GATHER) {
			return true;
		}
		
		return false;
	}
	
	
	public void setDeleteUpDown() {
		setDeleteUpDown(!isBusy());
	}
	
	public void setDeleteUpDown(boolean b) {
		if (down != null) {
			delete.setEnabled(b && (asyncmsgs.getSelectedIndex() > -1));
			up.setEnabled(b && (asyncmsgs.getSelectedIndex() > 0));
			down.setEnabled(b && (asyncmsgs.getSelectedIndex() > -1) && (asyncmsgs.getSelectedIndex() < (nbOfAsyncMsgs-1   )));
		}
	}   
	
	public void animateFutureTransactions() {
		setContentOfListOfPendingTransactions();
	}
	
	public void setLabelColors() {
		if ((time !=null) && (status != null) && (info != null)) {
			String oldTime = time.getText();
			int index = oldTime.indexOf("(");
			if (index != -1) {
				oldTime = oldTime.substring(0, index).trim();
			}
			String newTime = ""+ass.getClockValue();
			if (oldTime.compareTo(newTime) != 0) {
				newTime += " (before:" + oldTime + ")";
			}
			time.setText(newTime);
			if (ass.getAllTransactions() != null) {
				info.setText(""+ass.getAllTransactions().size());
			} else {
				info.setText("0");
			}
			switch(busyMode) {
			case AvatarSpecificationSimulation.DONT_EXECUTE:
				status.setText("Stopped");
				status.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				time.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				info.setForeground(ColorManager.InteractiveSimulationText_UNKNOWN);
				break;
			case AvatarSpecificationSimulation.GATHER:
			case AvatarSpecificationSimulation.EXECUTE:
			case AvatarSpecificationSimulation.RESET:
			case AvatarSpecificationSimulation.INITIALIZE:
				status.setText("Running...");
				status.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				time.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				info.setForeground(ColorManager.InteractiveSimulationText_BUSY);
				break;        
			case AvatarSpecificationSimulation.TERMINATED:
				status.setText("Terminated");
				status.setForeground(ColorManager.InteractiveSimulationText_TERM);
				time.setForeground(ColorManager.InteractiveSimulationText_TERM);
				info.setForeground(ColorManager.InteractiveSimulationText_TERM);
				break;
			case AvatarSpecificationSimulation.KILLED:
				status.setText("killed");
				status.setForeground(ColorManager.InteractiveSimulationText_TERM);
				time.setForeground(ColorManager.InteractiveSimulationText_TERM);
				info.setForeground(ColorManager.InteractiveSimulationText_TERM);
				break;
			}
		}
	}
	
	public void zoomIn() {
		if (sdpanel != null) {
			sdpanel.zoomIn();
		}
	}
	
	public void zoomOut() {
		if (sdpanel != null) {
			sdpanel.zoomOut();
		}
	}
	
	public synchronized void setContentOfListOfPendingTransactions() {
		if (invokedLater == 0) {
			invokedLater = 1;
			//TraceManager.addDev("Invoke later:" + invokedLater);
			EventQueue.invokeLater(this);
		}
	}
	
	public void resetMetElements() {
		if (avspec.getReferenceObject() instanceof AvatarDesignPanel) {
			((AvatarDesignPanel)(avspec.getReferenceObject())).resetMetElements();
			
		}
	}
	
	public void updateMetElements() {
		Hashtable<AvatarStateMachineElement, Integer> hashOfAllElements  = AvatarSimulationTransaction.hashOfAllElements;
		TGComponent tgc;
		Object o;
		
		if (hashOfAllElements == null) {
			nbOfAllExecutedElements = 0;
			return;
		}
		
		if (hashOfAllElements.hashCode() != nbOfAllExecutedElements) {
			Object objs[] = hashOfAllElements.keySet().toArray();
			TraceManager.addDev("Parsing array of elements: " + objs.length);
			for(int i=0; i<objs.length; i++) {
				o = objs[i];
				TraceManager.addDev("objs: " + o);
				Object oo = ((AvatarStateMachineElement)o).getReferenceObject();
				if (oo != null) {
					tgc = (TGComponent)oo;
					TraceManager.addDev("TGComponent: " + tgc);
					tgc.setAVATARMet(hashOfAllElements.get(o).intValue());
				}
				
			}
		}
		nbOfAllExecutedElements = hashOfAllElements.hashCode();
	}
	
	public void updateTransactionsTable() {
		if (transactiontm != null) {
			transactiontm.fireTableStructureChanged();
		}
	}
	
	public void updateAsynchronousChannels() {
		
		if (ass != null) {
			Vector<AvatarSimulationAsynchronousTransaction> msgs = ass.getAsynchronousMessages();
			
			if (fifos != null) {
				for(AvatarInteractiveSimulationFIFOData fifo: fifos) {
					fifo.nb=0;
				}
				
				
			}
			
			if (msgs != null) {
				
				if (msgs.size() > 0) {
					for(AvatarSimulationAsynchronousTransaction msg: msgs) {
						for(AvatarInteractiveSimulationFIFOData fifo0: fifos) {
							if (fifo0.fifo == msg.getRelation()) {
								fifo0.nb ++;
								break;
							}
						}
					}
				}
				
				if (asyncPanel != null) {
					asyncPanel.revalidate();
					comboFIFOs.revalidate();
					comboFIFOs.repaint();
				}
				
				
				
				if (asyncmsgs != null) {
					AvatarInteractiveSimulationFIFOData currentFifo = (AvatarInteractiveSimulationFIFOData)(comboFIFOs.getSelectedItem()); 
					if (currentFifo != null) {
						nbOfAsyncMsgs = 0;
						Vector<AvatarSimulationAsynchronousTransaction> vectorForList = new Vector<AvatarSimulationAsynchronousTransaction>();
						for(AvatarSimulationAsynchronousTransaction as: msgs) {
							if (as.getRelation() == currentFifo.fifo) {
								vectorForList.add(as);
								nbOfAsyncMsgs++;
							}
						}
						//Collections.reverse(vectorForList);
						asyncmsgs.setListData(vectorForList);
					}
				}	
				
			}
		}
	}
	
	public void animateDiagrams() {
		if ((animate != null) && (ass != null) && (ass.getSimulationBlocks() != null)) {
			if (animate.isSelected()) {
				// We go through all blocks
				runningTGComponents.clear();
				AvatarStateMachineElement asme;
				TGComponent tgc;
				for(AvatarSimulationBlock block: ass.getSimulationBlocks()) {
					asme = block.getCurrentAvatarElement();
					if (asme != null) {
						// Search for corresponding element in avatar spec
						tgc = (TGComponent)(asme.getReferenceObject());
						if (tgc != null) {
							//TraceManager.addDev("Found an object:" + tgc);
							runningTGComponents.add(tgc);
						}
					}
				}
				if (openDiagram.isSelected()) {
					if (ass.getPreviousBlock() != null) {
						//TraceManager.addDev("Open SMD diag" + ass.getPreviousBlock().getName());
						mgui.openAVATARSMD(ass.getPreviousBlock().getName());
					}
				}
			}
			mgui.setAvatarAnimate(animate.isSelected());
		}
		//TraceManager.addDev("End animate diag");
	}
	
	public boolean isRunningComponent(TGComponent _tgc) {
		if (isVisible()) {
			return runningTGComponents.contains(_tgc);
		}
		return false;
	}
	
	public boolean isSelectedComponentFromTransaction(TGComponent _tgc) {
		if (isVisible()) {
			return _tgc == selectedComponentForTransaction;
		}
		return false;
	}
	
	public void actSaveTxt() {
		TraceManager.addDev("Saving in txt format");
		String fileName = saveFileName.getText().trim();
		
		if (fileName.length() == 0) {
			fileName += "simulationtrace_fromttool.txt";
		}
		
		
		if (ConfigurationTTool.IMGPath != null) {
			fileName = ConfigurationTTool.IMGPath + System.getProperty("file.separator") + fileName;
		}
		
		boolean ok = true;
		
		try {
			ok = FileUtils.checkFileForSave(new File(fileName));
		} catch (Exception e) {
			ok = false;
		}
		
		if (!ok) {
			JOptionPane.showMessageDialog(this,
				"The capture could not be performed: the specified file is not valid",
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
            return;
		}
		
		
		try {
			FileUtils.saveFile(fileName, ass.getStringExecutedTransactions());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"The simulation trace in text format could not be saved: " + e.getMessage(),
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
            return;
		}
		
		JOptionPane.showMessageDialog(this,
				"Simulation trace was saved in " + fileName,
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
		
		//ass.printExecutedTransactions();
	}
	
	
	public void actSaveSvg() {
		TraceManager.addDev("Saving in svg format");
		
		// Testing file for save
		
		String fileName = saveFileName.getText().trim();
		
		if (fileName.length() == 0) {
			fileName += "simulationtrace_fromttool.svg";
		}
		
		if (ConfigurationTTool.IMGPath != null) {
			fileName = ConfigurationTTool.IMGPath + System.getProperty("file.separator") + fileName;
		}
		
		boolean ok = true;
		
		try {
			ok = FileUtils.checkFileForSave(new File(fileName));
		} catch (Exception e) {
			ok = false;
		}
		
		if (!ok) {
			JOptionPane.showMessageDialog(this,
				"The capture could not be performed: the specified file is not valid",
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
            return;
		}
		
		
		
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>\n");
		sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
		
		
		SVGGraphics svgg = new SVGGraphics(sdpanel.getLastGraphics());
		
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		//this.paint(svgg);
		TraceManager.addDev("Painting for svg");
		sdpanel.paintComponent(svgg); 
		TraceManager.addDev("Painting for svg done");
		sb.append(svgg.getSVGString());
		RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);		
		
		sb.append("</svg>");
		
		try {
			FileUtils.saveFile(fileName, sb.toString());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"The capture could not be performed: " + e.getMessage(),
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
            return;
		}
		
		JOptionPane.showMessageDialog(this,
				"The capture was performed in " + fileName,
				"Error",
				JOptionPane.INFORMATION_MESSAGE);
		
		//TraceManager.addDev("Svg=" + sb.toString());
		
		//return sb.toString();
		
	}
	
	public void actSaveSDPNG() {
		//Saving PNG file;
		BufferedImage bi;
		File file;
		
		bi = sdpanel.performCapture();
		
		String filePath="";
		if (ConfigurationTTool.IMGPath != null) {
			filePath += ConfigurationTTool.IMGPath;
			if (!filePath.endsWith(File.separator)) {
				filePath += File.separator;
			}
		}
		
		if ((saveFileName.getText() != null) && (saveFileName.getText().length() > 0)) {
			filePath += saveFileName.getText();
		} else {
			filePath += "simulationtrace_fromttool.png";
		}
		
		file = new File(filePath);
		
		mgui.writeImageCapture(bi, file, true);
	}
	
	public void deleteAsyncMsg() {
		//TraceManager.addDev("Deleting async msg");
		if ((ass != null) && (!isBusy())){
			int index = asyncmsgs.getSelectedIndex();
			if (index > -1) {
				boolean pendingModified = ass.removeAsyncMessage(((AvatarInteractiveSimulationFIFOData)(comboFIFOs.getSelectedItem())).fifo, index);
				updateAsynchronousChannels();
				if (pendingModified) {
					updatePending();
				}
			}
		}
		
	}
	
	public void upAsyncMsg() {
		//TraceManager.addDev("Up async msg");
		if ((ass != null) && (!isBusy())){
			int index = asyncmsgs.getSelectedIndex();
			if (index > 0) {
				ass.moveAsyncMessage(((AvatarInteractiveSimulationFIFOData)(comboFIFOs.getSelectedItem())).fifo, index ,index-1);
				updateAsynchronousChannels();
				asyncmsgs.setSelectedIndex(index-1);
			}
		}
		
		//printFullList();
		
	}
	
	public void downAsyncMsg() {
		//TraceManager.addDev("Down async msg");
		if ((ass != null) && (!isBusy())){
			int index = asyncmsgs.getSelectedIndex();
			if (index > -1) {
				//TraceManager.addDev("Moving from  index: " + index + " to: " + (index+1));
				ass.moveAsyncMessage(((AvatarInteractiveSimulationFIFOData)(comboFIFOs.getSelectedItem())).fifo, index, index+1);
				updateAsynchronousChannels();
				//TraceManager.addDev("Selecting list at index:" + index);
				asyncmsgs.repaint();
				asyncmsgs.setSelectedIndex(index+1);
			}
		}
		//printFullList();
	}
	
	public void printFullList() {
		int  i = 0;
		if (ass != null) {
			for(AvatarSimulationAsynchronousTransaction tr: ass.getAsynchronousMessages()) {
				TraceManager.addDev("#" + i + "\t: " + tr); 
				i++;
			}
		}
	}
	
	
	// Mouse management
	public void mouseReleased(MouseEvent e) {}
	
	
	
	/**
	* This adapter is constructed to handle mouse over	component events.
	*/
    private class MouseHandler extends MouseAdapter  {
        
        private	JLabel label;
        
        /**
		* ctor	for the	adapter.
		* @param label	the JLabel which will recieve value of the
		*		Action.LONG_DESCRIPTION	key.
		*/
        public MouseHandler(JLabel label)  {
            setLabel(label);
        }
        
        public void setLabel(JLabel label)  {
            this.label = label;
        }
        
        public void mouseEntered(MouseEvent evt)  {
            if (evt.getSource()	instanceof AbstractButton)  {
                AbstractButton button =	(AbstractButton)evt.getSource();
                Action action =	button.getAction();
                if (action != null)  {
                    String message = (String)action.getValue(Action.LONG_DESCRIPTION);
                    label.setText(message);
                }
            }
        }
    }
	
	public void	actionPerformed(ActionEvent evt)  {
		String command = evt.getActionCommand();
		//TraceManager.addDev("Command:" + command);
		
		if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_SIMU].getActionCommand()))  {
			runSimulation();
			//TraceManager.addDev("Start simulation!");
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RUN_X_COMMANDS].getActionCommand()))  {
			runXCommands();
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_SIMU].getActionCommand()))  {
			stopSimulation();
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_BACK_ONE].getActionCommand()))  {
			backwardOneTransaction();
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_RESET_SIMU].getActionCommand()))  {
			resetSimulation();
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_STOP_AND_CLOSE_ALL].getActionCommand()))  {
			close();
			return;
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_TXT].getActionCommand()))  {
			actSaveTxt();
			return;
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_SD_PNG].getActionCommand()))  {
			actSaveSDPNG();
			return;
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_SAVE_SVG].getActionCommand()))  {
			actSaveSvg();
			return;
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_ZOOM_IN].getActionCommand()))  {
			zoomIn();
			return;
			
		} else if (command.equals(actions[AvatarInteractiveSimulationActions.ACT_ZOOM_OUT].getActionCommand())) {
			zoomOut();
			return;
			
		} else if (evt.getSource() == displayedTransactionsText) {
			TraceManager.addDev("Entered text:" + displayedTransactionsText.getText());
			
		} else if ((evt.getSource() == imposeRandom) || (evt.getSource() == updateRandom)) {
			randomValue.setEnabled(imposeRandom.isSelected());
			if (ass != null) {
				if (imposeRandom.isSelected()) {
					int val;
					try {
						val = Integer.decode(randomValue.getText().trim()).intValue();
					} catch (Exception e) {
						val = -1;
					}
					ass.forceRandom(val);
				} else {
					ass.forceRandom(-1);
				}
			}
		} else if  (evt.getSource() == comboFIFOs) {
			updateAsynchronousChannels();
			setDeleteUpDown();
		} else if (evt.getSource() == delete)  {
			deleteAsyncMsg();
			return;
		} else if (evt.getSource() == up)  {
			TraceManager.addDev("Source = up");
			upAsyncMsg();
		} else if (evt.getSource() == down)  {
			TraceManager.addDev("Source = up");
			downAsyncMsg();
		}
		
		// Check for source of jcheckbox
		int index = 0;
		for(JCheckBox jcb: displayedBlocks) {
			if (evt.getSource() == jcb) {
				ass.getSimulationBlocks().get(index).selected = jcb.isSelected();
				TraceManager.addDev("Block " + ass.getSimulationBlocks().get(index) + " is now " + ass.getSimulationBlocks().get(index).selected);
				ass.computeSelectedSimulationBlocks();
				sdpanel.repaint();
				return;
			}
			index ++;
		}
	}
	
	public void error(String error) {
		jta.append("error: " + error + "\n");
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == asyncmsgs) {
			setDeleteUpDown();
			return;
		}
		
		int index = listPendingTransactions.getSelectedIndex();
		//TraceManager.addDev("Selected index = " +  index);
		if (index > -1) {
			try {
				AvatarSimulationPendingTransaction aspt = (AvatarSimulationPendingTransaction)(listPendingTransactions.getSelectedValue());
				selectedComponentForTransaction = (TGComponent)(aspt.elementToExecute.getReferenceObject());
				if ((selectedComponentForTransaction == null) && (aspt.linkedTransaction != null)) {
					//TraceManager.addDev("Adding reference object: " + aspt.linkedTransaction.elementToExecute.getReferenceObject());
					selectedComponentForTransaction = (TGComponent)(aspt.linkedTransaction.elementToExecute.getReferenceObject());
				}
				if (!(busyMode == AvatarSpecificationSimulation.GATHER) && !(busyMode == AvatarSpecificationSimulation.EXECUTE)) {
					ass.setIndexSelectedTransaction(listPendingTransactions.getSelectedIndex());
				}
				if (animate.isSelected() && (openDiagram.isSelected())) {
					if (aspt.asb != null) {
						previousBlock = aspt.asb;
						mgui.openAVATARSMD(previousBlock.getName());
					}
				}
			} catch (Exception ex){
				TraceManager.addDev("Exception selected component");
				selectedComponentForTransaction = null;
				if (previousBlock != null) {
					mgui.openAVATARSMD(previousBlock.getName());
				}
			}
		} else {
			selectedComponentForTransaction = null;
			if (previousBlock != null) {
				mgui.openAVATARSMD(previousBlock.getName());
			}
		}
		
	}
	

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == animate) {
			//TraceManager.addDev("Animate is ... " + animate.isSelected());
			animateDiagrams();
			diploids.setEnabled(animate.isSelected());
			animateWithInfo.setEnabled(animate.isSelected());
			openDiagram.setEnabled(animate.isSelected());
		} else if (e.getSource() == diploids) {
			mgui.setAVATARIDs(diploids.isSelected());
		} else if (e.getSource() == executeEmptyTransition) {
			ass.setExecuteEmptyTransition(executeEmptyTransition.isSelected());
		} else if (e.getSource() == executeStateEntering) {
			ass.setExecuteStateEntering(executeStateEntering.isSelected());
		} else if  (e.getSource() == traceInSD) {
			if (sdpanel != null) {
				sdpanel.setTrace(traceInSD.isSelected());
				sdpanel.repaint();
			}
			
		}
		
	}
	
	public void windowClosing(WindowEvent e) {
		TraceManager.addDev("Windows closed!");
        close();
    }
    
    public void windowClosed(WindowEvent e) {
    }
    
    public void windowOpened(WindowEvent e) {
    }
    
    public void windowIconified(WindowEvent e) {
    }
    
    public void windowDeiconified(WindowEvent e) {
    }
    
    public void windowActivated(WindowEvent e) {
    }
    
    public void windowDeactivated(WindowEvent e) {
    }
    
} // Class
