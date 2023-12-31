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
import tmltranslator.modelcompiler.ArchUnitMEC;

import java.util.Objects;


/**
 * Class HwNode
 * Creation: 05/09/2007
 * @version 1.0 05/09/2007
 * @author Ludovic APVRILLE
 */
public abstract class HwNode extends DIPLOElement  {
    public static final int DEFAULT_CLOCK_RATIO = 1;

    protected int maximumNbOfMappedElement;
    protected String name;
    protected ArchUnitMEC mec;

    public int clockRatio = DEFAULT_CLOCK_RATIO; /* 2 means 1 cycle out of 2, etc. */

    public HwNode(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void addMECToHwExecutionNode( ArchUnitMEC _mec )     {
        mec = _mec;
    }

    public ArchUnitMEC getArchUnitMEC() {
        return mec;
    }

    public abstract String toXML();

    public boolean equalSpec(Object o) {
        TraceManager.addDev("equalSpec in HwNode");
        if (!(o instanceof HwNode))
            return false;
        HwNode hwNode = (HwNode) o;
        if (mec != null && (!mec.equalSpec(hwNode.getArchUnitMEC())))
            return false;
        return maximumNbOfMappedElement == hwNode.maximumNbOfMappedElement &&
                clockRatio == hwNode.clockRatio &&
                name.equals(hwNode.getName());
    }

    public void setName(String _name) {
        name = _name;
    }

    public abstract HwNode deepClone(TMLArchitecture _archi) throws TMLCheckingError;

    public void fillValues(HwNode newNode, TMLArchitecture _archi)  throws TMLCheckingError {
        newNode.maximumNbOfMappedElement = maximumNbOfMappedElement;
        newNode.setName(name);
        newNode.clockRatio = clockRatio;
        if (mec != null) {
            newNode.addMECToHwExecutionNode(getArchUnitMEC().deepClone(_archi));
        }
    }

}
