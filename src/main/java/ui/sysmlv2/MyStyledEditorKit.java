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
import javax.swing.text.*;

import myutil.*;

/**
 * Class MyStyledEditorKit
 * Creation: 15/07/2021
 * Version 1.0 15/07/2021
 * @author Ludovic APVRILLE
 * @see StyledEditorKit
 */
public class MyStyledEditorKit extends StyledEditorKit {

    private SyntaxDocument sd;
    private NumberedViewFactory nvf;
    private JPanelForEdition jpfe;

    public MyStyledEditorKit(JPanelForEdition _jpfe) {
        super();
        jpfe = _jpfe;
    }

    public Document createDefaultDocument() {
        sd = new SyntaxDocument();
        return sd;
    }

    public SyntaxDocument getSyntaxDocument() {
        return sd;
    }

    public void changeMade() {
        try {
            sd.processAllLinesChanged();
            //nvf.repaint();
        } catch (Exception e) {
            TraceManager.addDev("Exception on change made");
        }
    }

    public ViewFactory getViewFactory() {
        nvf =  new NumberedViewFactory(jpfe);
        return nvf;
    }

    public void setFind(String _find) {
        if (sd != null) {
            if (_find == null) {
                sd.setFind(null);
            } else {
                if (_find.trim().length() == 0) {
                    sd.setFind("");
                } else {
                    sd.setFind(_find);
                }
            }
        }
    }

    public String getFind() {
        if (sd != null) {
            return sd.getFind();
        }
        return null;
    }

    public int getFindFound() {
        if (sd != null) {
            return sd.findFound();
        }
        return 0;
    }

    public void setMatchCase(boolean _b) {
        if (sd != null) {
            sd.setMatchCase(_b);
        }
    }

    public boolean getMatchCase() {
        if (sd != null) {
            return sd.getMatchCase();
        }
        return false;
    }


} // Class
