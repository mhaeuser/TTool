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




package translator.touppaal;

import translator.ADComponent;
import translator.TClass;
import uppaaldesc.UPPAALLocation;
import uppaaldesc.UPPAALTemplate;

import java.util.ArrayList;

/**
* Class RelationTIFUPPAAL
* Creation: 16/05/2007
* @version 1.1 16/05/2007
* @author Ludovic APVRILLE
 */
public class RelationTIFUPPAAL {
    
	private ArrayList<TClassTemplate> tts;
	private ArrayList<ADComponentLocation> adcls;
    
    public RelationTIFUPPAAL() {
        tts = new ArrayList<TClassTemplate>();
		adcls = new ArrayList<ADComponentLocation>();
    }
	
	public void addTClassTemplate(TClassTemplate tt) {
		tts.add(tt);
	}
	
	public void addTClassTemplate(TClass _t, UPPAALTemplate _template, int _id) {
		tts.add(new TClassTemplate(_t, _template, _id));
	}
	
	public void addADComponentLocation(ADComponentLocation adcl) {
		adcls.add(adcl);
	}
	
	public void addADComponentLocation(ADComponent adc, UPPAALLocation _loc1, UPPAALLocation _loc2) {
		adcls.add(new ADComponentLocation(adc, _loc1, _loc2));
	}
	
	public TClassTemplate getFirstTClassTemplate(TClass _t) {
		for(TClassTemplate tt:tts) {
			if (tt.tclass == _t) {
				return tt;
			}
		}
		return null;
	}
	
	public ADComponentLocation getFirstADComponentLocation(ADComponent _adc) {
		for(ADComponentLocation adcl:adcls) {
			if (adcl.adc == _adc) {
				return adcl;
			}
		}
		return null;
	}
	
	
	
	public void setIds(UPPAALTemplate _template, int _beginid, int _endid) {
		for(TClassTemplate tt:tts) {
			if (tt.template == _template) {
				tt.beginid = _beginid;
				tt.endid = _endid;
				return;
			}
		}
	}
	
	public String getRQuery(TClass _t, ADComponent _adc) {
		//
		
		ADComponentLocation adcl = getFirstADComponentLocation(_adc);
		TClassTemplate tt = getFirstTClassTemplate(_t);
		
		if (tt == null) {
			
		}
		
		if (adcl == null) {
			
		}
		
		if ((tt == null) || (adcl == null)) {
			return null;
		}
		
		String q="";
		for(int i=tt.beginid; i<tt.endid+1; i++) {
			q += tt.template.getName() + "__" + i;
			q += "." + adcl.endloc.name;
			if (i != tt.endid) {
				q += " || ";
			}
		}
		return q;
	}
	
	public String toString() {
		String s="TClass / Templates\n";
		for(TClassTemplate tt:tts) {
			s+=tt.toString() + "\n";
		}
		s+="\nADComponents vc locations:\n";
		for(ADComponentLocation adcl:adcls) {
			s+=adcl.toString() + "\n";
		}
		return s;
	}
    
}