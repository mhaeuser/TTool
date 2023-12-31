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




package tmltranslator;

import myutil.TraceManager;

import java.util.Objects;

/**
 * Class TMLCommunicationElement
 * Creation: 22/11/2005
 * @version 1.0 22/11/2005
 * @author Ludovic APVRILLE
 */
public abstract class TMLCommunicationElement extends TMLElement {
    //private boolean infected;

    protected boolean isLossy;
    protected int lossPercentage;
    protected int maxNbOfLoss;

    public TMLCommunicationElement(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
    }

    public abstract boolean isBlockingAtOrigin();
    public abstract boolean isBlockingAtDestination();

    // No maxNbOfLoss -> set it to -1
    public void setLossy(boolean _isLossy, int _percentage, int _maxNbOfLoss) {
        if (_isLossy) {
            TraceManager.addDev("***** Setting lossy " + this + " precentage = " + _percentage + " maxLoss = " + _maxNbOfLoss);
        }
        isLossy = _isLossy;
        lossPercentage = _percentage;
        maxNbOfLoss = _maxNbOfLoss;
    }

    public boolean isLossy() {
        return (isLossy) && (lossPercentage>0);
    }

    public int getLossPercentage() {
        return lossPercentage;
    }

    public int getMaxNbOfLoss() {
        return maxNbOfLoss;
    }

    public void configLossy(boolean _isLossy, int _percentage, int _maxNbOfLoss) {
        isLossy = _isLossy;
        lossPercentage = _percentage;
        maxNbOfLoss = _maxNbOfLoss;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLCommunicationElement)) return false;
        if (!super.equalSpec(o)) return false;
        TMLCommunicationElement that = (TMLCommunicationElement) o;
        return isLossy == that.isLossy &&
                lossPercentage == that.lossPercentage &&
                maxNbOfLoss == that.maxNbOfLoss;
    }
}
