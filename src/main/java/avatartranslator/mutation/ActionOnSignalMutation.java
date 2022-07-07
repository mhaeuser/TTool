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

import java.util.List;

import avatartranslator.*;
import java.util.LinkedList;
//import myutil.TraceManager;

/**
 * Class ActionOnSignalMutation
 * Creation: 27/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 27/06/2022
 */

public abstract class ActionOnSignalMutation extends UnnamedStateMachineElementMutation {

    protected ActionOnSignalMutation(String _blockName, String _signalName) {
        super(_blockName);
        setSignalName(_signalName);
        initValues();
    }

    protected ActionOnSignalMutation(String _blockName, String _name, int _nameType) {
        super(_blockName, _name, _nameType);
        initValues();
    }

    protected ActionOnSignalMutation(String _blockName, String _name, int _nameType, String _signalName) {
        this(_blockName, _name, _nameType);
        setSignalName(_signalName);
    }

    private String signalName;

    private List<String> values;
    private boolean valuesSet = false;

	private boolean checkLatency;
    private boolean checkLatencySet = false;

    protected String getSignalName() {
        return signalName;
    }

    private void setSignalName(String _signalName) {
        signalName = _signalName;
    }

    protected List<String> getValues() {
        return values;
    }

    protected String getValue(int i) {
        return values.get(i);
    }

    protected boolean areValuesSet() {
        return valuesSet;
    }

    public void noValues() {
        values.clear();
        valuesSet = true;
    }

    public void addValue(String _value) {
        values.add(_value);
        valuesSet = true;
    }

    private void initValues() {
        values = new LinkedList<>();
    }

    protected boolean getCheckLatency() {
        return checkLatency;
    }

    protected boolean isCheckLatencySet() {
        return checkLatencySet;
    }

    public void setCheckLatency(boolean b) {
        checkLatency = b;
        checkLatencySet = true;
    }

    @Override
    public AvatarActionOnSignal getElement(AvatarSpecification _avspec) {
        if (!isNameSet()) {
            //TraceManager.addDev("name not set");
            AvatarStateMachine asm = getAvatarStateMachine(_avspec);
            List<AvatarStateMachineElement> elts =  asm.getListOfElements();
            for (AvatarStateMachineElement elt : elts) {
                if (elt instanceof AvatarActionOnSignal) {
                    AvatarActionOnSignal aaos = (AvatarActionOnSignal)elt;
                    //TraceManager.addDev(aaos.getSignal().getName() + " == " + this.getSignalName());
                    boolean flag = aaos.getSignal().getName().equals(this.getSignalName());

                    if (flag && areValuesSet()) {
                        int len = aaos.getNbOfValues();
                        for (int i = 0; flag && i < len; i++) {
                            flag = (aaos.getValue(i).equals(this.getValue(i)));
                        }
                    }

                    if (flag && isCheckLatencySet()) {
                        flag = (aaos.getCheckLatency() == this.getCheckLatency());
                    }

                    if (flag) return aaos;
                }
            }
            return null;
        }
        AvatarStateMachineElement element = super.getElement(_avspec);
        if (element != null && element instanceof AvatarActionOnSignal) return (AvatarActionOnSignal)element;
        return null;
    }
}