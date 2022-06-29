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

package avatartranslator.mutation;

import avatartranslator.*;
import myutil.TraceManager;

/**
 * Class MdRandomMutation
 * Creation: 28/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 28/06/2022
 */

public class MdRandomMutation extends RandomMutation implements MdMutation {

    private RandomMutation current;

    private boolean variableNameChange = false;

    public MdRandomMutation(String _variable, String _blockName) {
        setBlockName(_blockName);
        setVariable(_variable);
        current = new RmRandomMutation(_variable, _blockName);
    }

    public void setCurrentVariable(String _variable) {
        current.setVariable(_variable);
        variableNameChange = true;
    }

    public void setCurrentValues(String _minValue, String _maxValue) {
        current.setValues(_minValue, _maxValue);
    }

    public void setCurrentFunction(int _functionId) {
        current.setFunction(_functionId);
    }

    public void setCurrentFunction(int _functionId, String _extraAttribute) {
        current.setFunction(_functionId, _extraAttribute);
    }

    public void setCurrentFunction(int _functionId, String _extraAttribute1, String _extraAttribute2) {
        current.setFunction(_functionId, _extraAttribute1, _extraAttribute2);
    }

    @Override
    public AvatarRandom getElement(AvatarSpecification _avspec) {
        try {
            return current.getElement(_avspec);
        } catch (Exception e) {
            return super.getElement(_avspec);
        }
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarRandom rand = current.getElement(_avspec);

        if (variableNameChange)
            rand.setVariable(this.getVariable());

        if (areValuesSet())
            rand.setValues(this.getMinValue(), this.getMaxValue());

        if (isFunctionSet()) {
            rand.setFunctionId(this.getFunctionId());
            rand.setExtraAttribute1(this.getExtraAttribute1());
            rand.setExtraAttribute2(this.getExtraAttribute2());
        }
    }
}
