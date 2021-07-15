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

import myutil.TraceManager;

import java.io.File;

/**
 * Class EditionSet
 * Sysmlv2 edition
 * Creation: 15/07/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 15/07/2021
 */
public class EditionSet {

    public static String[] charenc = {"DEFAULT", "ISO-8859-1", "UTF-8", "UTF-16"};

    //private SysMLV2Structure ls;
    public JPanelForEdition jpfe;
    //  private int indexPrevious = 0;
    protected int indexLS = 0;
    //protected ArrayList<LatexStructure> undos;
    protected File file;
    protected JFrameSysMLV2Text frame;
    private boolean saved = false;

    public EditionSet(JFrameSysMLV2Text _frame) {
        frame = _frame;
        jpfe = new JPanelForEdition(frame);
        //ls = new LatexStructure("");
        //jpfe.setLatexStructure(ls);
        //undos = new ArrayList<LatexStructure>();
        addPreviousLS();
    }

    public EditionSet(JFrameSysMLV2Text _frame, String _text) {
        frame = _frame;
        jpfe = new JPanelForEdition(frame);
        //ls = new LatexStructure(_text);
        //jpfe.setLatexStructure(ls);
        //undos = new ArrayList<LatexStructure>();
        addPreviousLS();
    }

    public void setEncoding(String s) {
        if (jpfe == null) {
            return;
        }

        for (int i = 0; i < charenc.length; i++) {
            if (charenc[i].toLowerCase().compareTo(s.toLowerCase()) == 0) {
                TraceManager.addDev("Setting encoding to index:" + i);
                jpfe.setEncoder(i);
                return;
            }
        }

        jpfe.setEncoder(0);

    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean _saved) {
        //TraceManager.addDev("*** Setting saved to = " + _saved);
        saved = _saved;
        frame.setMode(frame.SAVED);
    }

    public void updatePanels() {
        applyMode();
    }

    protected JPanelForEdition getJPanelForEdition() {
        return jpfe;
    }

    protected void applyMode() {
        //mgui.setUndoRedo(indexLS, undos.size());
    }

    public File getFile() {
        return file;
    }



    public void selectedElementDocument(int _index) {
        if (jpfe != null) {
            jpfe.selectedElementDocument(_index);
        }
    }


    public void updateText() {
        if (jpfe != null) {
            jpfe.updateText();
        }
    }

    public void setMatchCase(boolean _b) {
        if (jpfe != null) {
            jpfe.setMatchCase(_b);
        }
    }

    public int manageFind(String _text) {
        if (jpfe != null) {
            return jpfe.manageFind(_text);
        }
        return -1;
    }

    public void replaceOne(String _find, String _replace) {
        if (jpfe != null) {
            jpfe.replaceOne(_find, _replace);
        }
    }

    public void replaceAll(String _find, String _replace) {
        if (jpfe != null) {
            jpfe.replaceAll(_find, _replace);
        }
    }

    public void updateFind() {
        if (jpfe != null) {
            jpfe.updateFind();
        }
    }

    public void findPrevious() {
        if (jpfe != null) {
            jpfe.findPrevious();
        }
    }

    public void strongUpdateText() {
        if (jpfe != null) {
            jpfe.strongUpdateText();
        }
    }

    public void changeOnLS() {
        applyMode();
        addPreviousLS();
    }

    private synchronized void addPreviousLS() {
        //TraceManager.addDev("adding undo");
        /*try {
            while (indexLS < (undos.size() - 1)) {
                TraceManager.addDev("Removing useless redos");
                undos.remove(undos.size() - 1);
            }
            undos.add(ls.getACloneOfMe());
            if (undos.size() > MainGUI.UNDO_SIZE) {
                undos.remove(0);
            }
            indexLS = undos.size() - 1;
            printState();
            setSaved(false);
            applyMode();
        } catch (Exception e) {
            TraceManager.addError("UNDO function failed: " + e.getMessage());
        }*/
    }

    public synchronized boolean backward() {
        try {
            /*if(indexLS == (undos.size()-1)) {
              indexLS --;
              }*/
            /*indexLS--;
            ls = undos.get(indexLS).getACloneOfMe();
            setSaved(false);
            applyMode();
            mgui.setMode(MainGUI.OPENED);
            int pos = jpfe.getPosition();
            jpfe.setLatexStructure(ls);
            jpfe.selectedElementDocument(pos);
            printState();*/
        } catch (Exception e) {
            TraceManager.addError("Backward function failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    public void printState() {
        /*TraceManager.addDev("Size of undos:" + undos.size() + " index=" + indexLS);
          int cpt = 0;
          String s;
          for(LatexStructure last: undos) {

          s = last.getText().substring(0, 10);
          if (cpt == indexLS) {
          s = "*-> " + s;
          }
          TraceManager.addDev(s);
          cpt ++;
          }*/

    }

    public synchronized boolean forward() {
        /*try {
            indexLS++;
            ls = undos.get(indexLS).getACloneOfMe();
            setSaved(false);
            applyMode();
            mgui.setMode(MainGUI.OPENED);
            int pos = jpfe.getPosition();
            jpfe.setLatexStructure(ls);
            jpfe.selectedElementDocument(pos);
            printState();
        } catch (Exception e) {
            TraceManager.addError("forward function failed: " + e.getMessage());
            return false;
        }*/

        return true;
    }

    public void command(int index) {
        //TraceManager.addDev("command: " + index);
        /*if (index == LEGUIAction.INSERT_ITEMIZE) {
            jpfe.insertItemize();
        } else if (index == LEGUIAction.INSERT_ENUMERATE) {
            jpfe.insertEnumerate();
        } else if (index == LEGUIAction.INSERT_ITEM) {
            jpfe.insertItem();
        } else if (index == LEGUIAction.ALIGN_LEFT) {
            jpfe.alignLeft();
        } else if (index == LEGUIAction.ALIGN_RIGHT) {
            jpfe.alignRight();
        } else if (index == LEGUIAction.ALIGN_CENTER) {
            jpfe.alignCenter();
        } else if (index == LEGUIAction.ALIGN_JUSTIFY) {
            jpfe.alignJustify();
        } else if (index == LEGUIAction.TEXT_BOLD) {
            jpfe.insertBold();
        } else if (index == LEGUIAction.TEXT_ITALICS) {
            jpfe.insertItalics();
        } else if (index == LEGUIAction.TEXT_UNDERLINE) {
            jpfe.insertUnderline();
        } else if (index == LEGUIAction.INSERT_FIGURE) {
            jpfe.insertFigure();

        } else if (index == LEGUIAction.TEXT_SIZE_0) {
            jpfe.insertTextOfSize(0);
        } else if (index == LEGUIAction.TEXT_SIZE_1) {
            jpfe.insertTextOfSize(1);
        } else if (index == LEGUIAction.TEXT_SIZE_2) {
            jpfe.insertTextOfSize(2);
        } else if (index == LEGUIAction.TEXT_SIZE_3) {
            jpfe.insertTextOfSize(3);
        } else if (index == LEGUIAction.TEXT_SIZE_4) {
            jpfe.insertTextOfSize(4);
        } else if (index == LEGUIAction.TEXT_SIZE_5) {
            jpfe.insertTextOfSize(5);
        } else if (index == LEGUIAction.TEXT_SIZE_6) {
            jpfe.insertTextOfSize(6);
        } else if (index == LEGUIAction.TEXT_SIZE_7) {
            jpfe.insertTextOfSize(7);
        } else if (index == LEGUIAction.TEXT_SIZE_8) {
            jpfe.insertTextOfSize(8);
        } else if (index == LEGUIAction.TEXT_SIZE_9) {
            jpfe.insertTextOfSize(9);

        } else if (index == LEGUIAction.COMMENT_REGION) {
            jpfe.commentOrUncommentRegion();
        } else if (index == LEGUIAction.INSERT_TEMPLATE) {
            jpfe.insertCurrentTemplate();
        } else if (index == LEGUIAction.INSERT_REFERENCE) {
            jpfe.insertReference();
        } else if (index == LEGUIAction.INSERT_CITE) {
            jpfe.insertCite();
        } else if (index == LEGUIAction.INSERT_LABEL) {
            jpfe.insertLabel();
        } else if (index == LEGUIAction.INSERT_SECTION) {
            jpfe.insertSection();
        } else if (index == LEGUIAction.INSERT_SUBSECTION) {
            jpfe.insertSubsection();
        } else if (index == LEGUIAction.INSERT_BLOCK) {
            jpfe.insertBlock();
        } else if (index == LEGUIAction.INSERT_ALERTBLOCK) {
            jpfe.insertAlertBlock();
        } else if (index == LEGUIAction.INSERT_EXAMPLEBLOCK) {
            jpfe.insertExampleBlock();
        } else if (index == LEGUIAction.INSERT_NOTE) {
            jpfe.insertNote();
        } else if (index == LEGUIAction.INSERT_COLUMNS) {
            jpfe.insertColumns();
        } else if (index == LEGUIAction.INSERT_COLUMN) {
            jpfe.insertColumn();
        } else if (index == LEGUIAction.INSERT_LISTING) {
            jpfe.insertListing();
        } else if (index == LEGUIAction.INSERT_TABULAR) {
            jpfe.insertTabular();
        } else if (index == LEGUIAction.INSERT_ROWCOLOR) {
            jpfe.insertRowColor();
        } else if (index == LEGUIAction.INSERT_HLINE) {
            jpfe.insertHLine();
        } else if (index == LEGUIAction.INSERT_FRAME) {
            jpfe.insertFrame();
        }*/
    }

    public int getSelectedCharSet() {
        if (jpfe == null) {
            return 0;
        }
        return jpfe.getSelectedCharSet();

    }
} // Class
