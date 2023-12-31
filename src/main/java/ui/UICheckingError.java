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

package ui;

import translator.CheckingError;

/**
 * Class UICheckingError
 * Creation: 07/06/2017
 * @version 1.0 07/06/2017
 * @author Florian LUGOU
 */
public class UICheckingError extends CheckingError {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6532490783339577084L;
	
	public static final String MESSAGE_CHOICE_BOTH_STOCHASTIC_DETERMINISTIC = "Badly formatted choice: it has both non-determinitic/ stochastic and regular guards";

	private TDiagramPanel tdp;
    private TGComponent tgc;

    public UICheckingError(int _type, String _message) {
        super(_type, _message);
    }

    public UICheckingError(CheckingError ce) {
        super(ce);
    }

    public void setTDiagramPanel(TDiagramPanel _tdp) {
        tdp = _tdp;
    }
    
    public void setTGComponent(TGComponent _tgc) {
        tgc = _tgc;
    }
    
    public TDiagramPanel getTDiagramPanel() {
        return tdp;
    }
    
    public TGComponent getTGComponent() {
        return tgc;
    }   
}
