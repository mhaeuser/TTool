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

package ui.interactivesimulation;

import tmltranslator.TMLModeling;
import ui.TGComponent;

import javax.swing.table.AbstractTableModel;
import java.util.Map;

/**
 * Class TaskTableModel
 * Information on TML tasks
 * Creation: 02/06/2009
 * @version 1.0 02/06/2009
 * @author Ludovic APVRILLE
 */
public class TaskTableModel extends AbstractTableModel {
	
	private TMLModeling<TGComponent> tmlm;
	private Map<Integer, String> valueTable;
	private Map<Integer, Integer> rowTable;
	
	private int nbOfRows;
	
	//private String [] names;
	public TaskTableModel(TMLModeling<TGComponent> _tmlm, Map<Integer, String> _valueTable, Map<Integer, Integer> _rowTable) {
		tmlm = _tmlm;
		valueTable = _valueTable;
		rowTable = _rowTable;
		computeData();
	}

	// From AbstractTableModel
	public int getRowCount() {
		return nbOfRows;
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int row, int column) {
		if (tmlm == null) {
			return "-";
		}
		
		if (column == 0) {
			return getTaskName(row);
		} else if (column == 1) {
			return getTaskID(row);
		} else if (column == 2) {
			return getTaskStatus(row);
		} else if (column == 3) {
			return getTaskCycles(row);
		}
		return "";
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Task Name";
		case 1:
			return "Task ID";
		case 2:
			return "State";
		case 3:
			return "Executed cycles";
		}
		return "unknown";
	}
	
	// Assumes tmlm != null
	private String getTaskName(int row) {
		return tmlm.getTasks().get(row).getName();
	}
	
	// Assumes tmlm != null
	private String getTaskID(int row) {
		return ""+tmlm.getTasks().get(row).getID();
	}
//	
//	private String getVariableName(int row) {
//		int cpt = 0;
//		int size;
//		for(TMLTask task: tmlm.getTasks()) {
//			size = task.getAttributes().size();
//			cpt += size;
//			if (row < cpt) {
//				return "" + task.getAttributes().get(row+size-cpt).getName();
//			}
//		}
//		
//		return "unknown name";
//	}
	
	public String getCycles(String _s) {
		if (_s == null) {
			return null;
		}
		int index = _s.indexOf(';');
		if (index == -1) {
			return _s;
		}
		return _s.substring(index+1, _s.length());
	}
	
	public String getState(String _s) {
		if (_s == null) {
			return null;
		}
		int index = _s.indexOf(';');
		if (index == -1) {
			return _s;
		}
		return _s.substring(0, index);
	}
	
	private String getTaskCycles(int row) {
		int ID = tmlm.getTasks().get(row).getID();
		String s = getCycles(valueTable.get(ID));
		
		if ((s != null) && (s.length() > 0)) {
			return s;
		}
		
	
		valueTable.put(ID, "-");
		rowTable.put(ID, row);
		return "-";
	}
	
	public String getTaskStatus(int row) {
		int ID = tmlm.getTasks().get(row).getID();
		String s = getState(valueTable.get(ID));
		
		if ((s != null) && (s.length() > 0)) {
			return s;
		}
		
	
		valueTable.put(ID, "-");
		rowTable.put(ID, row);
		return "-";
	}
	
	private void computeData() {
		if (tmlm == null) {
			nbOfRows = 0;
			return ;
		}
		
		nbOfRows = tmlm.getTasks().size();
		
		for(int i=0; i<nbOfRows; i++) {
			getTaskStatus(i);
		}

	}
}
