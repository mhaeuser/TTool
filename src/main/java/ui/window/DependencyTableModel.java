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
import myutil.MyMath;
import myutil.TraceManager;
import ui.TGComponent;
import ui.avatarrd.AvatarRDRequirement;
import ui.req.Requirement;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Class DependencyTableModel
 * Main data for dependency matrices
 * Creation: 23/06/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 23/06/2021
 */
public class DependencyTableModel extends AbstractTableModel implements Reorderable {
    public static final String[] VALUES = {"", "->", "<-", "<->"};

    private ArrayList<String> cols, rows;
    private byte[][] values;


    public DependencyTableModel(String[] _rows, String[] _cols, ArrayList<BytePoint> _points) {
        rows = new ArrayList<>(Arrays.asList(_rows));
        cols = new ArrayList<>(Arrays.asList(_cols));
        values = new byte[_rows.length][_cols.length];
        fillValues(_points);
    }

    public ArrayList<String> getRows() {
        return rows;
    }

    public ArrayList<String> getCols() {
        return cols;
    }



    // From AbstractTableModel
    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return cols.size() + 1;
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
                return rows.get(row);
        }

        int val = values[row][column-1];
        if ((val >=0) && (val<VALUES.length)) {
            return VALUES[val];
        }
        return "";
    }

    public byte getByteValue(String sR, String sC) {
        int colIndex = -1;
        for(int j=1; j<getColumnCount(); j++) {
            //TraceManager.addDev("getColumnName(j)="+getColumnName(j));
            if (getColumnName(j).equals(sC)) {
                colIndex = j;
                break;
            }
        }

        if (colIndex == -1) {
            return -1;
        }

        int rowIndex = -1;
        for(int i=0; i<getRowCount(); i++) {
            //TraceManager.addDev("rows.get(i)="+rows.get(i));
            if (rows.get(i).equals(sR)) {
                rowIndex = i;
                break;
            }
        }

        if (rowIndex == -1) {
            return -1;
        }

        return values[rowIndex][colIndex-1];
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "row / col";
        }
        return cols.get(columnIndex-1);
    }

    private void fillValues(ArrayList<BytePoint> _points) {
        for(BytePoint p: _points) {
            if ((p.x >= 0) && (p.y >= 0) && (p.x < rows.size()) && (p.y<cols.size()))  {
                values[p.x][p.y] = p.value;
            }
        }
    }

    public ArrayList<BytePoint> getNonNullPoints() {
        ArrayList<BytePoint> points = new ArrayList<>();
        for(int i=0; i<rows.size(); i++) {
            for(int j=0; j< cols.size(); j++) {
                if (values[i][j] > 0) {
                    BytePoint pt = new BytePoint(i, j, values[i][j]);
                    //TraceManager.addDev("Adding point: " + pt);
                    points.add(pt);
                }
            }
        }
        return points;
    }

    public void mySetValueAt(int value, int selectedRow, int selectedCol) {
        if ((selectedRow >= 0) && (selectedCol >= 0) && (selectedRow < rows.size()) && (selectedCol<cols.size())) {
            values[selectedRow][selectedCol] = (byte) value;
        }
    }


    public void upRow(int indexR) {

        // Saving row values
        Collections.swap(rows, indexR, indexR - 1);

        byte[] line = new byte[cols.size()];
        for (int j = 0; j < cols.size(); j++) {
            line[j] = values[indexR][j];
        }

        for (int j = 0; j < cols.size(); j++) {
            values[indexR][j] = values[indexR-1][j];
            values[indexR-1][j] = line[j];
        }
    }

    public void downRow(int indexR) {
        // Saving row values
        Collections.swap(rows, indexR, indexR + 1);

        byte[] line = new byte[cols.size()];
        for (int j = 0; j < cols.size(); j++) {
            line[j] = values[indexR][j];
        }

        for (int j = 0; j < cols.size(); j++) {
            values[indexR][j] = values[indexR+1][j];
            values[indexR+1][j] = line[j];
        }
    }

    public void leftCol(int indexCol) {
        Collections.swap(cols, indexCol, indexCol - 1);

        byte[] col = new byte[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            col[i] = values[i][indexCol];
        }

        for (int i = 0; i < rows.size(); i++) {
            values[i][indexCol] = values[i][indexCol-1];
            values[i][indexCol-1] = col[i];
        }
    }

    public void rightCol(int indexCol) {
        Collections.swap(cols, indexCol, indexCol + 1);

        byte[] col = new byte[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            col[i] = values[i][indexCol];
        }

        for (int i = 0; i < rows.size(); i++) {
            values[i][indexCol] = values[i][indexCol+1];
            values[i][indexCol+1] = col[i];
        }
    }


    public void reorderRow(int fromIndex, int toIndex) {
        TraceManager.addDev("Reordering from " + fromIndex + " to " + toIndex);

        // String of row
        String s = rows.get(fromIndex);
        rows.remove(fromIndex);
        int tmpI = toIndex;
        if (toIndex > fromIndex) {
            tmpI --;
        }
        rows.add(tmpI, s);

        // Values must be changed as well
        // We ignore the from and leave one space of the new line

        // We first save the line



        values = MyMath.moveLineCreateNew(values, fromIndex, toIndex);

        //printValues();

    }

    private void printValues() {
        TraceManager.addDev("\n");
        for(int i=0; i<rows.size(); i++) {
            String s = "" + i+ "\t";
            for (int j = 0; j < cols.size(); j++) {
                s += values[i][j] + " ";
            }
            TraceManager.addDev(s);
        }
        TraceManager.addDev("\n");
    }

}