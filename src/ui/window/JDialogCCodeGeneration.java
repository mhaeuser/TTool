/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.f
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
 *
 * /**
 * Class JDialogCCodeGeneration
 * Dialog for managing the generation and compilation of SystemC code
 * Creation: 27/04/2005
 * @version 1.2 27/04/2015
 * @author Andrea ENRICI, Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import launcher.LauncherException;
import launcher.RshClient;
import myutil.FileUtils;
import myutil.GraphicLib;
import myutil.MasterProcessInterface;
import myutil.ScrolledJTextArea;
import ui.GTURTLEModeling;
import ui.IconManager;
import ui.MainGUI;


public class JDialogCCodeGeneration extends javax.swing.JDialog implements ActionListener, Runnable, MasterProcessInterface, ListSelectionListener  {

    protected MainGUI mgui;

    private static String textSysC1 = "Generate C code in";
    private static String textSysC2 = "Compile C code in";
    private static String textSysC4 = "Run simulation to completion:";
    private static String textSysC5 = "Run interactive simulation:";
    private static String textSysC6 = "Run formal verification:";

    private static String unitCycle = "1";

    private static String[] simus = { "SystemC Simulator - LabSoC version",
                                      "C++ Simulator - LabSoc version" };
    private static int selectedItem = 1;

    protected static String pathCode;
    protected static String pathCompiler;
    protected static String pathExecute;
    protected static String pathInteractiveExecute;
    protected static String pathFormalExecute;

    protected static boolean interactiveSimulationSelected = true;
    //protected static boolean optimizeModeSelected = true;

    protected final static int NOT_STARTED = 1;
    protected final static int STARTED = 2;
    protected final static int STOPPED = 3;

    int mode;

    //components
    protected JTextArea jta;
    protected JButton start;
    protected JButton stop;
    protected JButton close;

    protected JRadioButton exe, exeint, exeformal;
    protected ButtonGroup exegroup;
    protected JLabel gen, comp;
    protected JTextField code1, code2, compiler1, exe1, exe2, exe3, exe2int, exe2formal;
    protected JTabbedPane jp1;
    protected JScrollPane jsp;
    protected JCheckBox removeCppFiles, removeXFiles;//, debugmode, optimizemode;
    protected JComboBox versionSimulator;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

    private String hostSystemC;

    protected RshClient rshc;

    private int automatic;
    private boolean wasClosed = false;

		private GTURTLEModeling gtm;


    /** Creates new form  */
    public JDialogCCodeGeneration(Frame f, MainGUI _mgui, String title, String _hostSystemC, String _pathCode, String _pathCompiler, String _pathExecute, String _pathInteractiveExecute, String _graphPath, GTURTLEModeling _gtm ) {

        super(f, title, true);

        mgui = _mgui;
				gtm = _gtm;

        if (pathCode == null) {
            pathCode = _pathCode;
        }

        if (pathCompiler == null)
            pathCompiler = _pathCompiler;

        if (pathExecute == null)
            pathExecute = _pathExecute;

        if (pathInteractiveExecute == null) {
            if (_graphPath != null) {
                _pathInteractiveExecute += " -gpath " + _graphPath;
            }
            pathInteractiveExecute = _pathInteractiveExecute;
        }

        if (pathFormalExecute == null) {
            pathFormalExecute = pathInteractiveExecute;

            int index = pathFormalExecute.indexOf("-server");
            if (index != -1) {
                pathFormalExecute = pathFormalExecute.substring(0, index) + pathFormalExecute.substring(index+7, pathFormalExecute.length());
                pathFormalExecute += " -explo";
            }
        }
        hostSystemC = _hostSystemC;
        initComponents();
        myInitComponents();
        pack();
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (automatic > 0) {
            startProcess();
        }
    }

    protected void myInitComponents() {
        mode = NOT_STARTED;
        setButtons();
        setList();
        updateInteractiveSimulation();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());

        jp1 = new JTabbedPane();

        JPanel jp01 = new JPanel();
        GridBagLayout gridbag01 = new GridBagLayout();
        GridBagConstraints c01 = new GridBagConstraints();
        jp01.setLayout(gridbag01);
        jp01.setBorder(new javax.swing.border.TitledBorder("Code generation"));

        JPanel jp02 = new JPanel();
        GridBagLayout gridbag02 = new GridBagLayout();
        GridBagConstraints c02 = new GridBagConstraints();
        jp02.setLayout(gridbag02);
        jp02.setBorder(new javax.swing.border.TitledBorder("Compilation"));

        JPanel jp03 = new JPanel();
        GridBagLayout gridbag03 = new GridBagLayout();
        GridBagConstraints c03 = new GridBagConstraints();
        jp03.setLayout(gridbag03);
        jp03.setBorder(new javax.swing.border.TitledBorder("Execution"));


        c01.gridheight = 1;
        c01.weighty = 1.0;
        c01.weightx = 1.0;
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row
        c01.fill = GridBagConstraints.BOTH;
        c01.gridheight = 1;

        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row

        gen = new JLabel(textSysC1);
        jp01.add(gen, c01);

        code1 = new JTextField(pathCode, 100);
        jp01.add(code1, c01);

        jp01.add(new JLabel(" "), c01);
        c01.gridwidth = GridBagConstraints.REMAINDER; //end row

        removeCppFiles = new JCheckBox("Remove old .h, .c, .o  files");
        removeCppFiles.setSelected(true);
        jp01.add(removeCppFiles, c01);

        removeXFiles = new JCheckBox("Remove old .x files");
        removeXFiles.setSelected(true);
        jp01.add(removeXFiles, c01);

        /*debugmode = new JCheckBox("Put debug information in code");
        debugmode.setSelected(false);
        jp01.add(debugmode, c01);*/

        /*optimizemode = new JCheckBox("Optimize code");
        optimizemode.setSelected(optimizeModeSelected);
        jp01.add(optimizemode, c01);*/

        jp01.add(new JLabel(" "), c01);

        jp1.add("Synthesize code", jp01);

        // Panel 02
        c02.gridheight = 1;
        c02.weighty = 1.0;
        c02.weightx = 1.0;
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row
        c02.fill = GridBagConstraints.BOTH;
        c02.gridheight = 1;

        comp = new JLabel(textSysC2);
        jp02.add(comp, c02);

        code2 = new JTextField(pathCode, 100);
        jp02.add(code2, c02);

        jp02.add(new JLabel("with"), c02);

        compiler1 = new JTextField(pathCompiler, 100);
        jp02.add(compiler1, c02);

        jp02.add(new JLabel(" "), c02);

        jp1.add("Compile", jp02);

        // Panel 03
        c03.gridheight = 1;
        c03.weighty = 1.0;
        c03.weightx = 1.0;
        c03.gridwidth = GridBagConstraints.REMAINDER; //end row
        c03.fill = GridBagConstraints.BOTH;
        c03.gridheight = 1;

        exegroup = new ButtonGroup();

        jp03.add(new JLabel(" "), c03);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row

        exe = new JRadioButton(textSysC4, false);
        exe.addActionListener(this);
        exegroup.add(exe);
        jp03.add(exe, c03);

        exe2 = new JTextField(pathExecute, 100);
        jp03.add(exe2, c02);

        jp03.add(new JLabel(" "), c02);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row

        exeint = new JRadioButton(textSysC5, true);
        exeint.addActionListener(this);
        exegroup.add(exeint);
        jp03.add(exeint, c03);
        exe2int = new JTextField(pathInteractiveExecute, 100);
        jp03.add(exe2int, c02);

        jp03.add(new JLabel(" "), c03);
        c02.gridwidth = GridBagConstraints.REMAINDER; //end row

        exeformal = new JRadioButton(textSysC6, true);
        exeformal.addActionListener(this);
        exegroup.add(exeformal);
        jp03.add(exeformal, c03);
        exe2formal = new JTextField(pathFormalExecute, 100);
        jp03.add(exe2formal, c02);

        jp03.add(new JLabel(" "), c03);

        //jp1.add("Execute", jp03);

        c.add(jp1, BorderLayout.NORTH);
        if (automatic > 0) {
            GraphicLib.enableComponents(jp1, false);
        }

        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        if (automatic == 0) {
            jta.append("Select options and then, click on 'start' to launch C code compilation\n\n");
        }
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);


        start = new JButton("Start", IconManager.imgic53);
        stop = new JButton("Stop", IconManager.imgic55);


        start.setPreferredSize(new Dimension(100, 30));
        stop.setPreferredSize(new Dimension(100, 30));


        start.addActionListener(this);
        stop.addActionListener(this);



        close = new JButton("Close", IconManager.imgic27);
        close.setPreferredSize(new Dimension(100, 30));
        close.addActionListener(this);

        JPanel jp2 = new JPanel();
        if (automatic == 0) {
            jp2.add(start);
            jp2.add(stop);
        }
        jp2.add(close);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void updateInteractiveSimulation() {
        if (automatic == 0) {
            exe2.setEnabled(exe.isSelected());
            exe2int.setEnabled(exeint.isSelected());
            exe2formal.setEnabled(exeformal.isSelected());
        }
    }

    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        // Compare the action command to the known actions.
        updateInteractiveSimulation();
        if (command.equals("Start"))  {
            startProcess();
        } else if (command.equals("Stop")) {
            stopProcess();
        } else if (command.equals("Close")) {
            closeDialog();
        }
    }

    public void closeDialog() {
        if (mode == STARTED) {
            stopProcess();
        }
        //optimizeModeSelected = optimizemode.isSelected();
        wasClosed = true;
        dispose();
    }

    public boolean wasClosed() {
        return wasClosed;
    }

    public void stopProcess() {
        try {
            rshc.stopFillJTA();
        } catch (LauncherException le) {

        }
        rshc = null;
        mode =  STOPPED;
        setButtons();
        go = false;
    }

    public void startProcess() {
        if (automatic > 0) {
            startProcess = false;
            t = new Thread(this);
            mode = STARTED;
            go = true;
            t.start();
        } else {
            startProcess = false;
            t = new Thread(this);
            mode = STARTED;
            setButtons();
            go = true;
            t.start();
        }
    }

    private void testGo() throws InterruptedException {
        if (go == false) {
            throw new InterruptedException("Stopped by user");
        }
    }

    public void run() {

        String cmd;
        String data;
        hasError = false;

        try {
					if (automatic > 0)	{
						hasError = generateCode();
						testGo();
						compileCode();
						testGo();
					}
					else	{
						if( jp1.getSelectedIndex() == 0 )	{	//Code generation
							hasError = generateCode();
						}
						testGo();
						// Compilation
						if( jp1.getSelectedIndex() == 1 )	{
							compileCode();
						}
						if( ( hasError == false ) && ( jp1.getSelectedIndex() < 1 ) )	{
							jp1.setSelectedIndex( jp1.getSelectedIndex() + 1 );
            }
					}
				}
				catch( InterruptedException ie )	{
					jta.append("Process interrupted!\n");
				}
				jta.append("\n\nReady to process next command...\n");
				
				checkMode();
        setButtons();
    }

    private boolean generateCode() throws InterruptedException {

        String list;
        int cycle = 0;
				boolean error = false;

        jta.append( "Generating C code...\n\n" );
        if( removeCppFiles.isSelected() )	{
					jta.append( "Removing all .h files...\n" );
					list = FileUtils.deleteFiles( code1.getText(), ".h" );
          if( list.length() == 0 )	{
          	jta.append("No files were deleted\n");
          }
					else	{
						jta.append("Files deleted:\n" + list + "\n");
					}
					jta.append("\nRemoving all .c files...\n");
					list = FileUtils.deleteFiles( code1.getText(), ".c" );
					if( list.length() == 0 )	{
						jta.append( "No files were deleted\n" );
					}
					else	{
						jta.append("Files deleted:\n" + list + "\n");
					}
					jta.append("\nRemoving all .o files...\n");
					list = FileUtils.deleteFiles( code1.getText(), ".o" );
					if( list.length() == 0 )	{
						jta.append( "No files were deleted\n" );
					}
					else	{
						jta.append( "Files deleted:\n" + list + "\n" );
          }
        }
        if (removeXFiles.isSelected()) {
          jta.append( "\nRemoving all .x files...\n" );
          list = FileUtils.deleteFiles( code1.getText(), ".x" );
          if( list.length() == 0 )	{
						jta.append("No files were deleted\n");
          }
					else	{
						jta.append("Files deleted:\n" + list + "\n");
          }
        }
        testGo();
    		error = gtm.generateCcode( code1.getText() );
				if( !error )	{
					File dir = new File( code1.getText() );
					StringBuffer s = new StringBuffer();
					jta.append( "\nSource files successfully generated:\n" );
					for( File f: dir.listFiles() )	{
						try	{
							if( f.getCanonicalPath().contains(".c") || f.getCanonicalPath().contains(".h") )	{
								s.append( f.getCanonicalPath() + "\n" );
							}
						}
						catch( IOException ioe )	{
            	jta.append("Error: " + ioe.getMessage() + "\n");
            	mode = STOPPED;
            	setButtons();
							return true;
						}
					}
					jta.append( s.toString() );
				}
				return error;
    }   //End of method generateCode()

    public void compileCode() throws InterruptedException {
        String cmd = compiler1.getText();

        jta.append("Compiling C code with command: \n" + cmd + "\n****\n");

        rshc = new RshClient(hostSystemC);
        // Assuma data are on the remote host
        // Command
        try {
            processCmd(cmd, jta);
        } catch( Exception e ) {
            jta.append("**** ERROR ****\n" + e.getMessage() + "\n");
            mode = STOPPED;
            setButtons();
            return;
        }
    }

    protected void processCmd( String cmd, JTextArea _jta ) throws Exception	{

			String s;
			Process p;
			p = Runtime.getRuntime().exec( cmd );
			BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			while( ( s = br.readLine() ) != null )	{
				_jta.append( s + "\n" );
			}
			p.waitFor();
			p.destroy();
			if( p.exitValue() != 0 )	{
				throw new Exception( "Make exit status: " + p.exitValue() );
			}
    }

    protected void checkMode() {
        mode = NOT_STARTED;
    }

    protected void setButtons() {
        if (automatic == 0) {
            switch(mode) {
            case NOT_STARTED:
                start.setEnabled(true);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            case STARTED:
                start.setEnabled(false);
                stop.setEnabled(true);
                close.setEnabled(false);
                getGlassPane().setVisible(true);
                break;
            case STOPPED:
            default:
                start.setEnabled(false);
                stop.setEnabled(false);
                close.setEnabled(true);
                getGlassPane().setVisible(false);
                break;
            }
        } else {
            close.setEnabled(true);
        }
    }

    public boolean hasToContinue() {
        return (go == true);
    }

    public void appendOut(String s) {
        jta.append(s);
    }

    public void setError() {
        hasError = true;
    }

    public String getPathInteractiveExecute() {
        return pathInteractiveExecute;
    }

    // List selection listener
    public void valueChanged(ListSelectionEvent e) {
        setList();
    }

    private void setList() {
    }

}	//End of class
