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

import ui.TGCSysMLV2;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class JFrameSysMLV2Text
 * Creation: 13/07/2021
 * version 1.0 13/07/2021
 * @author Ludovic APVRILLE
 */
public class JFrameSysMLV2Text extends JFrame implements ActionListener {


	private TGCSysMLV2 comp;

	private JButton buttonCancel, buttonClose;
	private JTextArea jta;


	public JFrameSysMLV2Text(String title, TGCSysMLV2 _comp, String[] _lines, ImageIcon imgic) {
		super(title);
		comp = _comp;
		
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		Container framePanel = getContentPane();
		framePanel.setLayout(new BorderLayout());

		String line = "";
		if (_lines != null) {
			for (int i=0; i < _lines.length; i++) {
				line += _lines[i] + "\n";
			}
		}

		jta = new JTextArea(line);
		// Issue #35: This should not be editable because changes will not be 
		// taken into account
		jta.setEditable( true );
//		jta.setEditable(true);
		jta.setMargin(new Insets(10, 10, 10, 10));
		jta.setTabSize(3);
		Font f = new Font("Courrier", Font.BOLD, 12); 
		jta.setFont(f);
		JScrollPane jsp = new JScrollPane(jta);
		
		framePanel.add(jsp, BorderLayout.CENTER);
		
		buttonCancel = new JButton("Cancel", IconManager.imgic27);
		buttonCancel.addActionListener(this);
		buttonClose = new JButton("Save and close", IconManager.imgic25);
		buttonClose.addActionListener(this);
		
		JPanel jp = new JPanel();
		jp.add(buttonCancel);
		jp.add(buttonClose);
		
		framePanel.add(jp, BorderLayout.SOUTH);

		
		pack();
	}
	
 	public void actionPerformed(ActionEvent evt)  {
		
		if (evt.getSource() == buttonCancel) {
			cancel();
		} else if (evt.getSource() == buttonClose) {
			close();
		}
	}

	private void cancel() {
    	dispose();
	}

	private void close() {
    	if (comp != null) {
    		comp.setLines(jta.getText());
    		comp.getTDiagramPanel().repaint();
		}
    	dispose();
	}

	
    
} // Class 

	