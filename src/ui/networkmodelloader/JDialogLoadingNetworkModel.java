/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class JDialogNetworkModelPanel
 * Dialog for managing the loading of network models
 * Creation: 28/05/2017
 * @version 1.1 28/05/2017
 * @author Ludovic APVRILLE
 * @author Ludovic Apvrille
 * @see
 */

package ui.networkmodelloader;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

import ui.*;
import myutil.*;

public class JDialogLoadingNetworkModel extends javax.swing.JFrame implements ActionListener, Runnable  {

    private ArrayList<NetworkModel> listOfModels;

    protected Frame f;
    protected MainGUI mgui;

    protected final static int NOT_LISTED = 1;
    protected final static int LISTED = 2;
    protected final static int SELECTED = 3;

    private int mode;

    //components
    protected JTextArea jta;
    private JTextAreaWriter textAreaWriter;
    protected JButton start;
    protected JButton stop;

    protected JScrollPane jsp;

    private Thread t;
    private boolean go = false;
    private boolean hasError = false;
    protected boolean startProcess = false;

    private String url;
    private NetworkModelPanel panel;


    /** Creates new form  */
    public JDialogLoadingNetworkModel(Frame _f, MainGUI _mgui, String title, String _url) {
        super(title);

        f = _f;
        mgui = _mgui;

        url = _url;

        listOfModels = new ArrayList<NetworkModel>();


        initComponents();
        myInitComponents();
        pack();
        Thread t = new Thread(this);
        t.start();


        //getGlassPane().addMouseListener( new MouseAdapter() {});
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }


    protected void myInitComponents() {
        mode = NOT_LISTED;
        setButtons();
    }

    protected void initComponents() {

        Container c = getContentPane();
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(new BorderLayout());
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new NetworkModelPanel(listOfModels);
        jsp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.NORTH);


        jta = new ScrolledJTextArea();
        jta.setEditable(false);
        jta.setMargin(new Insets(10, 10, 10, 10));
        jta.setTabSize(3);
        jta.append("Connecting to " + url + ".\n Please wait ...\n\n");
        Font f = new Font("Courrier", Font.BOLD, 12);
        jta.setFont(f);
        textAreaWriter = new JTextAreaWriter( jta );

        jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        c.add(jsp, BorderLayout.CENTER);

        start = new JButton("Load", IconManager.imgic23);
        stop = new JButton("Cancel", IconManager.imgic55);

        start.setPreferredSize(new Dimension(200, 30));
        stop.setPreferredSize(new Dimension(200, 30));

        start.addActionListener(this);
        stop.addActionListener(this);

        JPanel jp2 = new JPanel();
        jp2.add(stop);
        jp2.add(start);

        c.add(jp2, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent evt)  {
        //String command = evt.getActionCommand();

        // Compare the action command to the known actions.
        if (evt.getSource() == start)  {
            loadFile();
        } else if (evt.getSource() == stop) {
            cancel();
        }
    }


    public void loadFile() {
        // Run the retreiver + analyzer
    }

    public void cancel() {
        dispose();
    }


    public void run() {
        // Loading main file describing models, giving information on this, and filling the array of models
        // Accsing the main file
        try {
	    HttpURLConnection connection;
	    TraceManager.addDev("URL: going to create it to: " + url);
            URL mainFile = new URL(url);
	    TraceManager.addDev("URL creation");
	    connection = (HttpURLConnection)(mainFile.openConnection());
	    TraceManager.addDev("Connection setup 0");
	    String redirect = connection.getHeaderField("Location");
	    if (redirect != null){
		TraceManager.addDev("Redirection found");
		connection = (HttpURLConnection)(new URL(redirect).openConnection());
	    }
	    //connection.setRequestMethod("GET");
	    //connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	    TraceManager.addDev("Connection setup 1");
	    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            jta.append("Connection established...\n");
            String inputLine;
	    NetworkModel nm = null;
            while ((inputLine = in.readLine()) != null) {
		if (inputLine.startsWith("#FILE")) {
		    nm = new NetworkModel(inputLine.substring(5, inputLine.length()).trim());
		    listOfModels.add(nm);
		}
		if (inputLine.startsWith("-TYPE")) {
		    if (nm != null) {
			nm.type = NetworkModel.stringToNetworkModelType(inputLine.substring(5, inputLine.length()).trim());
		    }
		}

		if (inputLine.startsWith("-DESCRIPTION")) {
		    if (nm != null) {
			nm.description = inputLine.substring(12, inputLine.length()).trim();
		    }
		}

		if (inputLine.startsWith("-IMG")) {
		    if (nm != null) {
			nm.image = inputLine.substring(4, inputLine.length()).trim();
		    }
		}
		
                //System.out.println(inputLine);
		
	    }
		jta.append("\n" + listOfModels.size() + " loaded, you can now select a model to be loaded\n");
	    mode = LISTED;
	    panel.repaint();
            in.close();
        } catch (Exception e) {
            jta.append("Error: " + e.getMessage() + " when retreiving file " + url );
        }
    }

    protected void checkMode() {
        mode = NOT_LISTED;
    }

    protected void setButtons() {
        switch(mode) {
        case NOT_LISTED:
            start.setEnabled(false);
            stop.setEnabled(true);
            //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            getGlassPane().setVisible(false);
            break;
        case LISTED:
            start.setEnabled(false);
            stop.setEnabled(true);
            getGlassPane().setVisible(true);
            //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            break;
        case SELECTED:
        default:
            start.setEnabled(true);
            stop.setEnabled(true);
            getGlassPane().setVisible(false);
            break;
        }
    }


    public void appendOut(String s) {
        jta.append(s);
    }


}
