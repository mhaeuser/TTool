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
import translator.CheckingError;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Vector;


/**
 * Class TMLActivityElementEvent
 * Creation: 23/11/2005
 * @version 1.0 23/11/2005
 * @author Ludovic APVRILLE
 */
public abstract class TMLActivityElementEvent extends TMLActivityElement {
    protected TMLEvent event;
    protected Vector<String> datas;
    protected String variable; // Used for notified -> variable in which the result is stored:
                               // 0: no event
                               // >0: nb of event in the list

    protected List<TMLEvent> events;

    public TMLActivityElementEvent(String _name, Object _referenceObject) {
        super(_name, _referenceObject);

        datas = new Vector<String>();
    }

    public boolean hasEvents() {
        return events != null;
    }

    public List<TMLEvent> getEvents() {
        return events;
    }

    public void setEvent(TMLEvent _event) {
        event = _event;
    }

    public TMLEvent getEvent() {
        return event;
    }

    public void setVariable(String _variable) {
        variable = _variable;
    }

    public String getVariable() {
        return variable;
    }

    public void addParam(String _param) {
        datas.add(_param);
    }

    public int getNbOfParams() {
        return datas.size();
    }

    public String getParam(int _index) {
        if (_index < getNbOfParams()) {
            return datas.elementAt(_index);
        } else {
            return null;
        }
    }

    public void setParam(String _param, int _index) {
        datas.setElementAt(_param, _index);
    }

    public Vector<String> getVectorAllParams() {
        return datas;
    }

    public String getAllParams() {
        return getAllParams(",");
    }

    public String getAllParams(String separator) {
        String s = "";
        for(int i=0; i<getNbOfParams(); i++) {
            if (i != 0) {
                s+= separator;
            }
            s += TMLTextSpecification.modifyString(getParam(i));
        }
        return s;
    }

    public void replaceEventWith(TMLEvent oldEvt, TMLEvent newEvt) {
        if (event == oldEvt) {
            event = newEvt;
        }
    }

    public Vector<String> getDatas() {
        return datas;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLActivityElementEvent)) return false;
        if (!super.equalSpec(o)) return false;
        TMLActivityElementEvent tmlActivityElementEvent = (TMLActivityElementEvent) o;

        return (new HashSet<>(datas)).equals(new HashSet<>(tmlActivityElementEvent.getDatas())) &&
                Objects.equals(variable, tmlActivityElementEvent.getVariable());
    }

    public void fillValues(TMLActivityElementEvent newElt, TMLModeling tmlm) throws TMLCheckingError {
        super.fillValues(newElt, tmlm);
        newElt.setVariable(getVariable());
        newElt.datas.addAll(datas);

        TMLEvent evt = tmlm.getEventByName(event.getName());
        if (evt == null) {
            TraceManager.addDev("Null evt");
            throw new TMLCheckingError(CheckingError.STRUCTURE_ERROR, "Unknown event in cloned model: " + event.getName());
        }
        newElt.setEvent(evt);

    }


}
