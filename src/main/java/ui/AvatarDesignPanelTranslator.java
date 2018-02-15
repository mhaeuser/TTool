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

import avatartranslator.*;
import myutil.Conversion;
import myutil.TraceManager;
import translator.CheckingError;
import ui.avatarbd.*;
import ui.avatarsmd.*;

import java.util.*;

/**
 * Class AvatarDesignPanelTranslator
 * Creation: 18/05/2010
 * @author Ludovic APVRILLE
 */
public class AvatarDesignPanelTranslator {

    protected AvatarDesignPanel adp;
    protected List<CheckingError> checkingErrors, warnings;
    protected CorrespondanceTGElement listE; // usual list
    //protected CorrespondanceTGElement listB; // list for particular element -> first element of group of blocks
    protected List <TDiagramPanel> panels;
    protected Map<String, List<TAttribute>> typeAttributesMap;
    protected Map<String, String> nameTypeMap;
    
    public AvatarDesignPanelTranslator(AvatarDesignPanel _adp) {
        adp = _adp;
        reinit();
    }

    public void reinit() {
        checkingErrors = new LinkedList<CheckingError> ();
        warnings = new LinkedList<CheckingError> ();
        listE = new CorrespondanceTGElement();
        panels = new LinkedList <TDiagramPanel>();
    }

    public List<CheckingError> getErrors() {
        return checkingErrors;
    }

    public List<CheckingError> getWarnings() {
        return warnings;
    }

    public CorrespondanceTGElement getCorrespondanceTGElement() {
        return listE;
    }

    public AvatarSpecification generateAvatarSpecification( List<AvatarBDStateMachineOwner> _blocks) {
        List<AvatarBDBlock> blocks = new LinkedList<AvatarBDBlock>();
        List<AvatarBDLibraryFunction> libraryFunctions = new LinkedList<AvatarBDLibraryFunction>();

        for (AvatarBDStateMachineOwner owner: _blocks)
            if (owner instanceof AvatarBDBlock)
                blocks.add ((AvatarBDBlock) owner);
            else
                libraryFunctions.add ((AvatarBDLibraryFunction) owner);

        AvatarSpecification as = new AvatarSpecification("avatarspecification", adp);

        if (adp != null) {
            AvatarBDPanel abdp = adp.getAvatarBDPanel();
            if (abdp != null) {
                as.addApplicationCode(abdp.getMainCode());
            }
        }
        typeAttributesMap = new HashMap<String, List<TAttribute>>();
        nameTypeMap = new HashMap<String,String>();
        createLibraryFunctions (as, libraryFunctions);
        createBlocks(as, blocks);
        createRelationsBetweenBlocks(as, blocks);
        makeBlockStateMachines(as);
        /*for (String s: nameTypeMap.keySet()){
          System.out.println(s + " "+ nameTypeMap.get(s));
          System.out.println(typeAttributesMap.get(nameTypeMap.get(s)).size());
          } */
        createPragmas(as, blocks);

        //TraceManager.addDev("Removing else guards");
        as.removeElseGuards();
        //TraceManager.addDev("Removing else guards ... done");
        //System.out.println(as.toString());
		adp.abdp.repaint();
        return as;
    }

    public class ErrorAccumulator extends avatartranslator.ErrorAccumulator {
        private TGComponent tgc;
        private TDiagramPanel tdp;
        private AvatarBlock ab;

        public ErrorAccumulator (TGComponent tgc, TDiagramPanel tdp, AvatarBlock ab) {
            this.tgc = tgc;
            this.tdp = tdp;
            this.ab = ab;
        }

        public ErrorAccumulator (TGComponent tgc, TDiagramPanel tdp) {
            this (tgc, tdp, null);
        }

        public CheckingError createError (String msg) {
            UICheckingError ce = new UICheckingError (CheckingError.BEHAVIOR_ERROR, msg);
            ce.setTGComponent (this.tgc);
            ce.setTDiagramPanel (this.tdp);

            return ce;
        }

        public void addWarning (CheckingError ce) {
            AvatarDesignPanelTranslator.this.addWarning (ce);
        }

        public void addWarning (String msg) {
            this.addWarning (this.createError (msg));
        }

        public void addError (CheckingError ce) {
            AvatarDesignPanelTranslator.this.addCheckingError (ce);
        }

        public void addError (String msg) {
            this.addError (this.createError (msg));
        }
    }

    public void createPragmas(AvatarSpecification _as, List<AvatarBDBlock> _blocks) {
        Iterator<TGComponent> iterator = adp.getAvatarBDPanel().getComponentList().listIterator();
        TGComponent tgc;
        AvatarBDPragma tgcn;
        AvatarBDSafetyPragma tgsp;
        AvatarBDPerformancePragma tgpp;
        String values [];
        String tmp;
        List<AvatarPragma> pragmaList;
        
        while(iterator.hasNext()) {
            tgc = iterator.next();
            if (tgc instanceof AvatarBDPragma) {
                ErrorAccumulator errorAcc = new ErrorAccumulator (tgc, adp.getAvatarBDPanel());
                tgcn = (AvatarBDPragma)tgc;
				tgcn.syntaxErrors.clear();
                values = tgcn.getValues();
                for(int i=0; i<values.length; i++) {
                    tmp = values[i].trim();
                    if ((tmp.startsWith("#") && (tmp.length() > 1))) {
                        tmp = tmp.substring(1, tmp.length()).trim();

                        //TraceManager.addDev("Reworking pragma =" + tmp);
                        pragmaList = AvatarPragma.createFromString(tmp, tgc, _as.getListOfBlocks(), typeAttributesMap, nameTypeMap, errorAcc);

                        //TraceManager.addDev("Reworked pragma =" + tmp);

                        for (AvatarPragma tmpPragma: pragmaList){
                            if (tmpPragma instanceof AvatarPragmaConstant){
                                AvatarPragmaConstant apg = (AvatarPragmaConstant) tmpPragma;
                                for (AvatarConstant ac: apg.getConstants()){
                                    _as.addConstant(ac);
                                }
                            }
                            _as.addPragma(tmpPragma);
                            //TraceManager.addDev("Adding pragma:" + tmp);
                        }
                    }
                    else {
                    	tgcn.syntaxErrors.add(values[i]);
                    }
                }
            }
            if (tgc instanceof AvatarBDSafetyPragma) {
                tgsp = (AvatarBDSafetyPragma)tgc;
                values = tgsp.getValues();
                tgsp.syntaxErrors.clear();
                for (String s: values){
					if (checkSafetyPragma(s, _blocks, _as, tgc)){
						_as.addSafetyPragma(s);
					}
					else {
						tgsp.syntaxErrors.add(s);

					}
                }
            }
			if (tgc instanceof AvatarBDPerformancePragma) {
				tgpp = (AvatarBDPerformancePragma)tgc;
                values = tgpp.getValues();
				for (String s: values){
					AvatarPragmaLatency pragma = checkPerformancePragma(s, _blocks, _as, tgc);
					if (pragma!=null){
						_as.addLatencyPragma(pragma);
					}
					else {
						tgpp.syntaxErrors.add(s);
					}
				}
			}
        }
    }
	
	public AvatarPragmaLatency checkPerformancePragma(String _pragma, List<AvatarBDBlock> _blocks, AvatarSpecification as, TGComponent tgc){	
		if (_pragma.contains("=") || (!_pragma.contains(">") && !_pragma.contains("<") && !_pragma.contains("?")) || !_pragma.contains("Latency(")){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "No latency expression found in pragma "+ _pragma);
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("No latency expression found in pragma "+ _pragma);
			return null;
		}

		String pragma = _pragma.trim();
		pragma = pragma.split("Latency\\(")[1];		
		//Find first block.state
		

		String p1 = pragma.split(",")[0];
		//Throw error if lack of '.' in block.signal
		if (!p1.contains(".")){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid block.signal format in pragma " + _pragma);
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Invalid block.signal format in pragma " + _pragma);
			return null;
		}
		String block1 = p1.split("\\.")[0];
		String state1 = p1.split("\\.",-1)[1];
		AvatarBlock bl1;
		AvatarStateMachineElement st1=null;
		List<String> id1= new ArrayList<String>();
		bl1 = as.getBlockWithName(block1);
		if (bl1==null){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Block " + block1 + " in pragma does not exist");
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Block " + block1 + " in pragma does not exist");
			return null;
		}
		AvatarStateMachine asm = bl1.getStateMachine();
		for (AvatarActionOnSignal aaos: asm.getAllAOSWithName(state1)){
			if (aaos.getCheckLatency()){
				id1.add((aaos.getSignal().isIn() ? "Receive signal" : "Send signal") +"-"+ aaos.getSignal().getName()+":"+aaos.getID());
				st1= aaos;
			}
		}
		AvatarState astate1 = asm.getStateWithName(state1);
		if (astate1!=null){
			if (astate1.getCheckLatency()){
				id1.add("State-"+state1 + ":"+ astate1.getID());
				st1= astate1;
			}
		}
		
		if (id1.size()==0){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Cannot find checkable state " + block1+ "." + state1 + " in pragma");
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Cannot find checkable state " + block1+ "." + state1 + " in pragma");
			return null;
		}
		
		

		
		//Find second block.signal
		//Throw error if lack of '.'


		String p2 = pragma.split(",")[1].split("\\)")[0];
		if (!p2.contains(".")){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid block.signal format in pragma "+ _pragma);
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Invalid block.signal format");
			return null;
		}

		String block2 = p2.split("\\.")[0];
		String state2 = p2.split("\\.",-1)[1];



		AvatarBlock bl2;
		AvatarStateMachineElement st2=null;

		bl2 = as.getBlockWithName(block2);
		if (bl2==null){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Block " + block2 + " in pragma does not exist");
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Block " + block2 + " in pragma does not exist");
			return null;
		}
		asm = bl2.getStateMachine();

		List<String> id2= new ArrayList<String>();
		for (AvatarActionOnSignal aaos: asm.getAllAOSWithName(state2)){

			if (aaos.getCheckLatency()){
				id2.add((aaos.getSignal().isIn() ? "Receive signal" : "Send signal")+"-"+ aaos.getSignal().getName()+":"+aaos.getID());
				st2=aaos;	
			}
		}
		
		AvatarState astate2 = asm.getStateWithName(state2);
		if (astate2!=null){
			if (astate2.getCheckLatency()){
				id2.add("State-"+state2 + ":"+ astate2.getID());
				st2= astate2;
			}
		}
		
		if (id2.size()==0){
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Cannot find checkable state " + block2+ "." + state2 + " in pragma");
		   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   	ce.setTGComponent(tgc);
		   	addWarning(ce);
			TraceManager.addDev("Cannot find checkable state " + block2+ "." + state2 + " in pragma");
			return null;
		}
	




		String equation = pragma.split("\\)")[1];
		equation = equation.replaceAll(" ","");
		int symbolType=0;
		int time=0;
		if (equation.substring(0,1).equals("<")){
			symbolType = AvatarPragmaLatency.lessThan;
			try {
				time= Integer.valueOf(equation.split("<")[1]);
			}
			catch (Exception e){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid number format in " + _pragma);
		    	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    	ce.setTGComponent(tgc);
		    	addWarning(ce);
				TraceManager.addDev("Invalid number format");
				return null;
			}
		}
		else if (equation.substring(0,1).equals(">")){
			symbolType = AvatarPragmaLatency.greaterThan;
			try {
			time= Integer.valueOf(equation.split(">")[1]);
			} 
			catch (Exception e){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Invalid number format in " + _pragma);
		    	ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    	ce.setTGComponent(tgc);
		    	addWarning(ce);
				TraceManager.addDev("Invalid number format");
				return null;
			}
		}
		else if (equation.substring(0,1).equals("?")){
			symbolType=AvatarPragmaLatency.query;
		}
		else {
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "no latency expression found in " + _pragma);
		    ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    ce.setTGComponent(tgc);
		    addWarning(ce);
			TraceManager.addDev("No latency expression found");
			return null;
		}
		return new AvatarPragmaLatency(_pragma, tgc, bl1, st1, bl2, st2, symbolType, time, id1, id2, _pragma);


	}
    public boolean checkSafetyPragma(String _pragma, List<AvatarBDBlock> _blocks, AvatarSpecification as, TGComponent tgc){
        //Todo: check types
        //Todo: handle complex types
        _pragma = _pragma.trim();

        if (_pragma.contains("=") && !(_pragma.contains("==") || _pragma.contains("<=") || _pragma.contains(">=") || _pragma.contains("!="))){
            //not a query
			UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Pragma " + _pragma + " cannot be parsed");
		    ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    ce.setTGComponent(tgc);
		    addWarning(ce);
            TraceManager.addDev("UPPAAL Pragma " + _pragma + " cannot be parsed");
            return false;
        }
        String header = _pragma.split(" ")[0];
        if (_pragma.contains("-->")){
            //will be implies
            _pragma = _pragma.replaceAll(" ","");
            String state1 = _pragma.split("-->")[0];
            String state2 = _pragma.split("-->")[1];
        //    System.out.println("checking... " + state1 + " " + state2);
            if (!state1.contains(".") || !state2.contains(".")){
                TraceManager.addDev("UPPAAL Pragma " + _pragma + " cannot be parsed: missing '.'");
                return false;
            }
            if (!statementParser(state1, as, _pragma, tgc)){
                return false;
            }
            //check the second half of implies
            if (!statementParser(state2,as, _pragma, tgc)){
                return false;
            }
        }
        else if (header.equals("E[]") || header.equals("E<>") || header.equals("A[]") || header.equals("A<>")){
            String state = _pragma.replace("E[]","").replace("A[]","").replace("E<>","").replace("A<>","").replaceAll(" ","");
            state = state.trim();
           // if (!state.contains("||") && !state.contains("&&")){
                if (!statementParser(state, as, _pragma, tgc)){
                    return false;
                }
            //}

            
        }
        else {
            TraceManager.addDev("UPPAAL Pragma " + _pragma + " cannot be parsed");
            return false;
        }
        return true;
    }
    public boolean statementParser(String state, AvatarSpecification as, String _pragma, TGComponent tgc){
        //check the syntax of a single statement
       

        //Divide into simple statements
        
        String[] split = state.split("[|&]+");
   //     System.out.println("split " + split[0]);
        if (split.length >1){
            boolean validity = true;
            for (String fragment: split){
                if (fragment.length()>2){
                    validity = validity && statementParser(fragment, as, _pragma, tgc);
                }
            }
            return validity;
        }
        String number= "[0-9]+";
        String bo = "(?i)true|false";
        if (state.contains("=") || state.contains("<") || state.contains(">")){
            String state1 = state.split("==|>(=)?|!=|<(=)?")[0];
            String state2 = state.split("==|>(=)?|!=|<(=)?")[1];
            if (!state1.contains(".")){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Pragma " + _pragma + " cannot be parsed: missing '.'");
			    ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    ce.setTGComponent(tgc);
			    addWarning(ce);
 
	            TraceManager.addDev("UPPAAL Pragma " + _pragma + " cannot be parsed: missing '.'");
            	return false;
            }
            String block1 = state1.split("\\.",2)[0];
            String attr1 = state1.split("\\.",2)[1];
           	if (attr1.contains(".")){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Complex UPPAAL Pragma attribute " + attr1 + " must contain __ and not .");
			    ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    ce.setTGComponent(tgc);
			    addWarning(ce);
 
           		TraceManager.addDev("Complex UPPAAL Pragma attribute " + attr1 + " must contain __ and not .");
           		return false;
           	}

            attr1 = attr1.replace(".","__");
            AvatarType p1Type= AvatarType.UNDEFINED;
            AvatarBlock bl1 = as.getBlockWithName(block1);
            if (bl1 !=null){
                //AvatarStateMachine asm = bl1.getStateMachine();
                if (bl1.getIndexOfAvatarAttributeWithName(attr1)==-1){
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " contains invalid attribute name " + attr1);
			    	ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    	ce.setTGComponent(tgc);
			    	addWarning(ce);
                    TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid attribute name " + attr1);
                    return false;
                }
                else {
                    int ind = bl1.getIndexOfAvatarAttributeWithName(attr1);
                    AvatarAttribute attr = bl1.getAttribute(ind);
                    p1Type = attr.getType();
                }
            }
            else {
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " contains invalid block name " + block1);
			   	ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    ce.setTGComponent(tgc);
			    addWarning(ce);
                TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid block name " + block1);
                return false;
            }
            if (state2.contains(".")){
                String block2 = state2.split("\\.",2)[0];
                String attr2= state2.split("\\.",2)[1];
                	if (attr2.contains(".")){
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Complex UPPAAL Pragma attribute " + attr2 + " must contain __ and not .");
			   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    		ce.setTGComponent(tgc);
			    		addWarning(ce);
           				TraceManager.addDev("Complex UPPAAL Pragma attribute " + attr2 + " must contain __ and not .");
           				return false;
           			}

                AvatarBlock bl2 = as.getBlockWithName(block2);
                if (bl2!=null){
                    if (bl2.getIndexOfAvatarAttributeWithName(attr2)==-1){
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " contains invalid attribute name " + attr2);
			   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
			    		ce.setTGComponent(tgc);
			    		addWarning(ce);
                        TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid attribute name " + attr2);
                        return false;
                    }
                    int ind = bl2.getIndexOfAvatarAttributeWithName(attr2);
                    AvatarAttribute attr = bl2.getAttribute(ind);
                    p1Type = attr.getType();

                }
                else {
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " contains invalid block name " + block2);
			   		ce.setTDiagramPanel(adp.getAvatarBDPanel());
			   		ce.setTGComponent(tgc);
			    	addWarning(ce);
                    TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid block name " + block2);
                    return false;
                }
            }
            else {
                if (state2.matches(number)){
                    if (p1Type != AvatarType.INTEGER){
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " has incompatible types");
			   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
			   			ce.setTGComponent(tgc);
			    		addWarning(ce);
                        TraceManager.addDev("UPPAAL Pragma " + _pragma + " has incompatible types");
                        return false;
                    }
                }
                else if (state2.matches(bo)){
                    if (p1Type != AvatarType.BOOLEAN){
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " has incompatible types");
			   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
			   			ce.setTGComponent(tgc);
			    		addWarning(ce);
                        TraceManager.addDev("UPPAAL Pragma " + _pragma + " has incompatible types");
                        return false;
                    }
                }
                else {
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " cannot be parsed");
			   		ce.setTDiagramPanel(adp.getAvatarBDPanel());
			   		ce.setTGComponent(tgc);
			    	addWarning(ce);
                    TraceManager.addDev("UPPAAL Pragma " + _pragma + " cannot be parsed");
                    return false;
                }
            }
        }
        else {
        	if (!state.contains(".")){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " cannot be parsed: missing '.'");
		   		ce.setTDiagramPanel(adp.getAvatarBDPanel());
		   		ce.setTGComponent(tgc);
		    	addWarning(ce);
	        	TraceManager.addDev("UPPAAL Pragma " + _pragma + " improperly formatted");
        		return false;
        	}
            String block1 = state.split("\\.",2)[0];
            String attr1 = state.split("\\.",2)[1];
           	if (attr1.contains(".")){
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Complex UPPAAL Pragma attribute " + attr1 + " must contain __ and not .");
	   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
	    		ce.setTGComponent(tgc);
	    		addWarning(ce);
       			TraceManager.addDev("Complex UPPAAL Pragma attribute " + attr1 + " must contain __ and not .");
         		return false;
           	}
            AvatarBlock bl1 = as.getBlockWithName(block1);
            if (bl1 !=null){
                AvatarStateMachine asm = bl1.getStateMachine();
                if (bl1.getIndexOfAvatarAttributeWithName(attr1)==-1 && asm.getStateWithName(attr1)==null){
					UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "UPPAAL Pragma " + _pragma + " contains invalid attribute or state name " + attr1);
		   			ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    		ce.setTGComponent(tgc);
		    		addWarning(ce);
   
                    TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid attribute or state name " + attr1);
                    return false;
                }
                
                int ind = bl1.getIndexOfAvatarAttributeWithName(attr1);
                if (ind !=-1){
                    AvatarAttribute attr = bl1.getAttribute(ind);
                    if (attr.getType()!=AvatarType.BOOLEAN){
						UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,"UPPAAL Pragma " + _pragma + " performs query on non-boolean attribute");
		   				ce.setTDiagramPanel(adp.getAvatarBDPanel());
		    			ce.setTGComponent(tgc);
		    			addWarning(ce);
                        TraceManager.addDev("UPPAAL Pragma " + _pragma + " performs query on non-boolean attribute");
                        return false;
                    }
                }
            }
            else {
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR,"UPPAAL Pragma " + _pragma + " contains invalid block name " + block1);
   				ce.setTDiagramPanel(adp.getAvatarBDPanel());
    			ce.setTGComponent(tgc);
    			addWarning(ce);
                TraceManager.addDev("UPPAAL Pragma " + _pragma + " contains invalid block name " + block1);
                return false;
            }
        }
        return true;
    }
    public String reworkPragma(String _pragma, LinkedList<AvatarBDBlock> _blocks) {
        String ret = "";
        int i;

        // Identify first keyword
        _pragma = _pragma.trim();

        int index = _pragma.indexOf(" ");

        if (index == -1) {
            return null;
        }

        String header = _pragma.substring(0, index).trim();

        for(i=0; i<AvatarPragma.PRAGMAS.length; i++) {
            if (header.compareTo(AvatarPragma.PRAGMAS[i]) == 0) {
                break;
            }
        }

        // Invalid header?
        if (i == AvatarPragma.PRAGMAS.length) {
            TraceManager.addDev("Invalid Pragma " + 0);
            return null;
        }



        ret = AvatarPragma.PRAGMAS_TRANSLATION[i] + " ";

        // Checking for arguments


        boolean b = ret.startsWith("Authenticity ");
        boolean b1 = ret.startsWith("PrivatePublicKeys ");
        String arguments [] = _pragma.substring(index+1, _pragma.length()).trim().split(" ");
        String tmp;
        String blockName, stateName, paramName = "";
        boolean found = false;
        LinkedList<TAttribute> types;
        AvatarBDBlock block;
        TAttribute ta;
     //   AvatarBlock ab;
        String myBlockName = "";




        for(i=0; i<arguments.length; i++) {
            tmp = arguments[i];
            TraceManager.addDev("arguments #=" + arguments.length + " pragma=" + _pragma + " tmp=" + tmp);

            if (b1) {

                // Private Public keys?
                if (i == 0) {
                    // Must be a block name
                    // Look for at least a block
                    found = false;
                    for(Object o: _blocks) {
                        block = (AvatarBDBlock)o;
                        //TraceManager.addDev("Comparing " + block.getBlockName() + " with " + tmp);
                        if (block.getBlockName().compareTo(tmp) ==0) {
                            myBlockName = block.getBlockName();
                            found = true;
                            ret = ret + tmp;
                            break;
                        }
                        /*for(Object oo: block.getAttributeList()) {
                          ta = (TAttribute)oo;
                          if (ta.getId().compareTo(tmp) == 0) {
                          found = true;

                          if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                          ret = ret  + tmp + " ";
                          } else if (ta.getType() == TAttribute.OTHER) {
                          // Must find all subsequent types
                          types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                          if (types == null) {
                          TraceManager.addDev("Invalid Pragma " + 1);
                          return null;

                          } else {
                          for(int j=0; j<types.size(); j++) {
                          ret = ret + tmp + "__" + ((TAttribute)(types.elementAt(j))).getId() + " ";
                          }
                          }
                          }

                          }
                          }*/
                    }
                    if (found == false) {
                        TraceManager.addDev("Invalid Pragma " + 2);
                        return null;
                    }

                } else if ((i == 1) || (i == 2)) {
                    // Shall be an attribute
                    TraceManager.addDev("i= " + i);
                    for(Object o: _blocks) {
                        block = (AvatarBDBlock)o;
                        TraceManager.addDev("block= " + block.getBlockName() + " my block name=" + myBlockName);
                        if (block.getBlockName().compareTo(myBlockName) == 0) {
                            TraceManager.addDev("Found the block " + ret);
                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                TraceManager.addDev("Attribute: " + ta.getId());
                                if (ta.getId().compareTo(tmp) == 0) {
                                    paramName = ta.getId();
                                    found = true;
                                    TraceManager.addDev("Pragma " + ret + " found=" + found);
                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret  + " " + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            TraceManager.addDev("Invalid Pragma " + 3);
                                            return null;
                                        } else {
                                            TraceManager.addDev("Pragma " + ret + " types size=" + types.size());
                                            for (TAttribute type: types)
                                                ret = ret  + " " + paramName + "__" + type.getId() + " ";
                                        }

                                    } else {
                                        TraceManager.addDev("Invalid Pragma " + 4);
                                        return null;
                                    }

                                    break;
                                }
                            }
                        }
                    }
                } else {
                    // Badly formatted
                    TraceManager.addDev("Invalid Pragma " + 5);
                    return null;
                }

                // Other than PrivatePublicKeys
            } else {
                index = tmp.indexOf(".");
                if (index == -1) {
                    return null;
                }
                blockName = tmp.substring(0, index);

                //TraceManager.addDev("blockName=" + blockName);
                // Search for the block
                for(Object o: _blocks) {
                    block = (AvatarBDBlock)o;
                    if (block.getBlockName().compareTo(blockName) == 0) {

                        if (b) {
                            // Authenticity
                            stateName = tmp.substring(index+1, tmp.length());
                            //TraceManager.addDev("stateName=" + stateName);
                            index = stateName.indexOf(".");
                            if (index == -1) {
                                return null;
                            }
                            paramName = stateName.substring(index+1, stateName.length());
                            stateName = stateName.substring(0, index);

                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                if (ta.getId().compareTo(paramName) == 0) {
                                    found = true;

                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret + blockName + "." + stateName + "." + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            return null;
                                        } else {
                                            for (TAttribute type: types)
                                                ret = ret + blockName + "." + stateName + "." + paramName + "__" + type.getId() + " ";
                                        }

                                    } else {
                                        return null;
                                    }

                                    break;
                                }
                            }

                        } else {
                            // Other: confidentiality, initial system knowledge, initial session knowledge, constant

                            paramName = tmp.substring(index+1, tmp.length());
                            for(Object oo: block.getAttributeList()) {
                                ta = (TAttribute)oo;
                                if (ta.getId().compareTo(paramName) == 0) {
                                    found = true;

                                    if ((ta.getType() == TAttribute.NATURAL) || (ta.getType() == TAttribute.INTEGER) || (ta.getType() == TAttribute.BOOLEAN)) {
                                        ret = ret + blockName + "." + paramName + " ";
                                    } else if (ta.getType() == TAttribute.OTHER) {
                                        // Must find all subsequent types
                                        types = adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther());
                                        if (types == null) {
                                            return null;
                                        } else {
                                            for (TAttribute type: types)
                                                ret = ret + blockName + "." + paramName + "__" + type.getId() + " ";
                                        }

                                    } else {
                                        return null;
                                    }

                                    break;
                                }
                            }

                        }
                    }
                }
            }

            if (!found) {
                return null;
            }

        }

        TraceManager.addDev("Reworked pragma: " + ret);

        return ret.trim();
    }

    private AvatarAttribute createRegularAttribute (AvatarStateMachineOwner _ab, TAttribute _a, String _preName) {
        AvatarType type = AvatarType.UNDEFINED;
        if (_a.getType() == TAttribute.INTEGER){
            type = AvatarType.INTEGER;
        } else if (_a.getType() == TAttribute.NATURAL){
            type = AvatarType.INTEGER;
        } else if (_a.getType() == TAttribute.BOOLEAN) {
            type = AvatarType.BOOLEAN;
        } else if (_a.getType() == TAttribute.TIMER) {
            type = AvatarType.TIMER;
        }
        AvatarAttribute aa = new AvatarAttribute(_preName + _a.getId(), type, _ab, _a);
        aa.setInitialValue(_a.getInitialValue());

        return aa;
    }

    public void addRegularAttribute(AvatarBlock _ab, TAttribute _a, String _preName) {
        _ab.addAttribute(this.createRegularAttribute (_ab, _a, _preName));
    }

    public void createLibraryFunctions (AvatarSpecification _as, List<AvatarBDLibraryFunction> _libraryFunctions) {
        for (AvatarBDLibraryFunction libraryFunction: _libraryFunctions) {
            AvatarLibraryFunction alf = new AvatarLibraryFunction (libraryFunction.getFunctionName (), _as, libraryFunction);
            _as.addLibraryFunction (alf);
            listE.addCor(alf, libraryFunction);
            libraryFunction.setAVATARID(alf.getID());

            // Create parameters
            for (TAttribute attr: libraryFunction.getParameters ())
                if (attr.getType() == TAttribute.INTEGER
                    || attr.getType() == TAttribute.NATURAL
                    || attr.getType() == TAttribute.BOOLEAN
                    || attr.getType() == TAttribute.TIMER)
                    alf.addParameter (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        // TODO: adapt
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addParameter (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

            // Create return values
            for (TAttribute attr: libraryFunction.getReturnAttributes ())
                if (attr.getType() == TAttribute.INTEGER
                    || attr.getType() == TAttribute.NATURAL
                    || attr.getType() == TAttribute.BOOLEAN
                    || attr.getType() == TAttribute.TIMER)
                    alf.addReturnAttribute (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addReturnAttribute (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

            // Create attributes
            for (TAttribute attr: libraryFunction.getAttributes ())
                if (attr.getType() == TAttribute.INTEGER
                    || attr.getType() == TAttribute.NATURAL
                    || attr.getType() == TAttribute.BOOLEAN
                    || attr.getType() == TAttribute.TIMER)
                    alf.addAttribute (this.createRegularAttribute (alf, attr, ""));
                else {
                    // other
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (attr.getTypeOther ());
                    if (types == null) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + attr.getTypeOther() + " used in " + alf.getName());
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.isEmpty ()) {
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + alf.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put (libraryFunction.getFunctionName()+"."+attr.getId(), attr.getTypeOther());
                            typeAttributesMap.put (attr.getTypeOther(), types);
                            for (TAttribute type: types)
                                alf.addAttribute (this.createRegularAttribute (alf, type, attr.getId() + "__"));
                        }
                    }
                }

            // Create methods
            for (ui.AvatarMethod uiam: libraryFunction.getMethods ()) {
                avatartranslator.AvatarMethod atam = new avatartranslator.AvatarMethod (uiam.getId (), uiam);
                atam.setImplementationProvided (uiam.isImplementationProvided());
                alf.addMethod (atam);
                this.makeParameters (alf, atam, uiam);
                this.makeReturnParameters (alf, libraryFunction, atam, uiam);
            }

            // Create signals
            for ( AvatarSignal uias: libraryFunction.getSignals ()) {
                avatartranslator.AvatarSignal atas;
                if (uias.getInOut() == AvatarSignal.IN)
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
                else
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);

                alf.addSignal (atas);
                this.makeParameters (alf, atas, uias);
            }
        }
    }

    public void createBlocks(AvatarSpecification _as, List<AvatarBDBlock> _blocks) {
        for(AvatarBDBlock block: _blocks) {
            AvatarBlock ab = new AvatarBlock(block.getBlockName(), _as, block);
            _as.addBlock(ab);
            listE.addCor(ab, block);
            block.setAVATARID(ab.getID());

            // Create attributes
            for (TAttribute a: block.getAttributeList ()) {
                if (a.getType() == TAttribute.INTEGER){
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.NATURAL){
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.BOOLEAN) {
                    addRegularAttribute(ab, a, "");
                } else if (a.getType() == TAttribute.TIMER) {
                    addRegularAttribute(ab, a, "");
                } else {
                    // other
                    // TraceManager.addDev(" -> Other type found: " + a.getTypeOther());
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel().getAttributesOfDataType(a.getTypeOther());
                    if (types == null) {
                        UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + a.getTypeOther() + " used in " + ab.getName());
                        ce.setTDiagramPanel(adp.getAvatarBDPanel());
                        addCheckingError(ce);
                        return;
                    } else {
                        if (types.size() ==0) {
                            UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Data type definition must contain at least one attribute:  " + ab.getName());
                            ce.setTDiagramPanel(adp.getAvatarBDPanel());
                            addCheckingError(ce);
                        } else {
                            nameTypeMap.put(block.getBlockName()+"."+a.getId(), a.getTypeOther());
                            typeAttributesMap.put(a.getTypeOther(), types);
                            for (TAttribute type: types)
                                addRegularAttribute(ab, type, a.getId() + "__");
                        }
                    }

                }
            }

            // Create methods
            for (ui.AvatarMethod uiam: block.getMethodList ()) {
                avatartranslator.AvatarMethod atam = new avatartranslator.AvatarMethod(uiam.getId(), uiam);
                atam.setImplementationProvided(uiam.isImplementationProvided());
                ab.addMethod(atam);
                makeParameters(ab, atam, uiam);
                makeReturnParameters(ab, block, atam, uiam);
            }

            // Create signals
            for (ui.AvatarSignal uias: block.getSignalList ()) {
                avatartranslator.AvatarSignal atas;
                if (uias.getInOut() == AvatarSignal.IN) {
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.IN, uias);
                } else {
                    atas = new avatartranslator.AvatarSignal(uias.getId(), avatartranslator.AvatarSignal.OUT, uias);
                }
                ab.addSignal(atas);
                makeParameters(ab, atas, uias);
            }

            // Put global code
            ab.addGlobalCode(block.getGlobalCode());

        }

        // Make block hierarchy
        for(AvatarBlock block: _as.getListOfBlocks()) {
            TGComponent tgc1 = listE.getTG(block);
            if ((tgc1 != null) && (tgc1.getFather() != null)) {
                TGComponent tgc2 = tgc1.getFather();
                AvatarBlock ab = listE.getAvatarBlock(tgc2);
                if (ab != null) {
                    block.setFather(ab);
                }
            }
        }
    }

    public void makeBlockStateMachines(AvatarSpecification _as) {
        // Make state machine of blocks
        for(AvatarBlock block: _as.getListOfBlocks())
            this.makeStateMachine(_as, block);

        // Make state machine of library functions
        for (AvatarLibraryFunction libraryFunction: _as.getListOfLibraryFunctions ())
            this.makeStateMachine (_as, libraryFunction);
    }

    public void makeReturnParameters(AvatarStateMachineOwner _ab, AvatarBDStateMachineOwner _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
        String rt = _uiam.getReturnType().trim();
        AvatarAttribute aa;
        AvatarType type = AvatarType.UNDEFINED;

        if (rt.length() == 0) {
            return;
        }

        if ((rt.compareTo("int") == 0) || (rt.compareTo("bool") == 0)) {
            aa = new AvatarAttribute("return__0", AvatarType.getType(rt), _ab, _block);
            _atam.addReturnParameter(aa);
        } else {
            LinkedList<TAttribute> types = adp.getAvatarBDPanel().getAttributesOfDataType(rt);
            if (types == null) {
                UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + rt + " declared as a return parameter of a method of " + _block.getOwnerName());
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(adp.getAvatarBDPanel());
                addCheckingError(ce);
                return;
            } else {
                int j=0;
                for (TAttribute ta: types) {
                    if (ta.getType() == TAttribute.INTEGER)
                        type = AvatarType.INTEGER;
                    else if (ta.getType() == TAttribute.NATURAL)
                        type = AvatarType.INTEGER;
                    else if (ta.getType() == TAttribute.BOOLEAN)
                        type = AvatarType.BOOLEAN;
                    else
                        type = AvatarType.INTEGER;
                    aa = new AvatarAttribute("return__" + j++, type, _ab, _block);
                    _atam.addReturnParameter(aa);
                }
            }
        }

    }

    public void makeParameters(AvatarStateMachineOwner _block, avatartranslator.AvatarMethod _atam, ui.AvatarMethod _uiam) {
        String typeIds[] = _uiam.getTypeIds();
        String types[] = _uiam.getTypes();

        for(int i=0; i<types.length; i++) {
            LinkedList<TAttribute> v = adp.getAvatarBDPanel().getAttributesOfDataType(types[i]);
            if (v == null) {
                if (AvatarType.getType(types[i]) == AvatarType.UNDEFINED) {
                    UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Unknown data type:  \"" + types[i] + "\" declared in method " + _atam + " of block " + _block.getName());
                    // TODO: adapt
                    // ce.setAvatarBlock(_block);
                    ce.setTDiagramPanel(adp.getAvatarBDPanel());
                    addCheckingError(ce);
                }
                AvatarAttribute aa = new AvatarAttribute(typeIds[i], AvatarType.getType(types[i]), _block, _uiam);
                _atam.addParameter(aa);
            } else {
                for (TAttribute ta: v) {
                    AvatarType type = AvatarType.UNDEFINED;
                    if (ta.getType() == TAttribute.INTEGER){
                        type = AvatarType.INTEGER;
                    } else if (ta.getType() == TAttribute.NATURAL){
                        type = AvatarType.INTEGER;
                    } else if (ta.getType() == TAttribute.BOOLEAN) {
                        type = AvatarType.BOOLEAN;
                    } else if (ta.getType() == TAttribute.TIMER) {
                        type = AvatarType.TIMER;
                    }
                    AvatarAttribute aa = new AvatarAttribute(typeIds[i] + "__" + ta.getId(), type, _block, _uiam);
                    _atam.addParameter(aa);
                }
            }
        }
    }

    public void manageAttribute (String _name, AvatarStateMachineOwner _ab, AvatarActionOnSignal _aaos, TDiagramPanel _tdp, TGComponent _tgc, String _idOperator) {
        TAttribute ta =  adp.getAvatarBDPanel().getAttribute(_name, _ab.getName());
        if (ta == null) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
            TraceManager.addDev("not found");
            return ;
        }

        //TraceManager.addDev("Found: " + ta.getId());

        LinkedList<String> v = new LinkedList<String> ();

        if (ta.getType() == TAttribute.OTHER)
            for (TAttribute tatmp: adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther()))
                v.add(_name + "__" + tatmp.getId());
        else
            v.add(_name);

        //TraceManager.addDev("Size of vector:" + v.size());
        for (String name: v) {
            AvatarAttribute aa = _ab.getAvatarAttributeWithName(name);
            if (aa == null) {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + _name + " in signal expression: " + _idOperator);
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(_tdp);
                ce.setTGComponent(_tgc);
                addCheckingError(ce);
                return ;
            } else {
                //TraceManager.addDev("-> Adding attr in action on signal in block " + _ab.getName() + ":" + _name + "__" + tatmp.getId());
                _aaos.addValue(name);
            }
        }
    }

    private void translateAvatarSMDSendSignal (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDSendSignal asmdss) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        avatartranslator.AvatarSignal atas = _ab.getAvatarSignalWithName (asmdss.getSignalName ());
        if (atas == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdss.getSignalName());

        // Get relation of that signal
        if (_ab instanceof AvatarBlock) {
            // Note that for library functions, signals are just placeholders so they don't need to be connected to anything
            AvatarRelation ar = _as.getAvatarRelationWithSignal (atas);
            if (ar == null) {
		if (atas.getReferenceObject() instanceof ui.AvatarSignal) {
		    //TraceManager.addDev("Send/ Setting as attached " + atas);
		    ((ui.AvatarSignal) atas.getReferenceObject()).attachedToARelation = false;
		}
		//TraceManager.addDev("Spec:" + _as.toString());
		throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Signal used for sending in " + asmdss.getValue() + " is not connected to a channel");
	    }
	    if (atas.getReferenceObject() instanceof ui.AvatarSignal) {
		//TraceManager.addDev("Send/ Setting as attached " + atas);
		((ui.AvatarSignal) atas.getReferenceObject()).attachedToARelation = true;
	    }
        }
	
        AvatarActionOnSignal aaos = new AvatarActionOnSignal ("action_on_signal", atas, asmdss);
        if (asmdss.hasCheckedAccessibility())
            aaos.setCheckable();

        if (asmdss.hasCheckedAccessibility())
            aaos.setChecked();

        if (aaos.isReceiving ())
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "A receiving signal is used for sending: " + asmdss.getValue());

        if (asmdss.getNbOfValues() == -1)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdss.getValue());

        for(int i=0; i<asmdss.getNbOfValues(); i++) {
            String tmp = asmdss.getValue(i);
            if (tmp.isEmpty ())
                throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Empty parameter in signal expression: " + asmdss.getValue());

            this.manageAttribute (tmp, _ab, aaos, tdp, asmdss, asmdss.getValue());
        }

        if (aaos.getNbOfValues () != atas.getListOfAttributes ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal sending: " + asmdss.getValue() + " -> nb of parameters does not match definition");

        // Checking expressions passed as parameter
        for (int i=0; i<aaos.getNbOfValues(); i++) {
            String theVal = aaos.getValue(i);
            if (atas.getListOfAttributes ().get (i).isInt ()) {
                if (AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, theVal) < 0)
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
            } else {
                // We assume it is a bool attribute
                if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0)
                    throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdss.getValue() + " -> value at index #" + i + " does not match definition");
            }
        }

        this.listE.addCor (aaos, asmdss);
        asmdss.setAVATARID (aaos.getID());
		if (asmdss.getCheckLatency()){
			aaos.setCheckLatency(true);
			_as.checkedIDs.add(asmdss.getName()+"-"+ asmdss.getSignalName()+":"+aaos.getID());
		}
        asm.addElement (aaos);
    }

    private void translateAvatarSMDLibraryFunctionCall (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDLibraryFunctionCall asmdlfc) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();

        /* Get Function corresponding to this call */
        AvatarBDLibraryFunction libraryFunction = asmdlfc.getLibraryFunction ();
        if (libraryFunction == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Should define a library function for this call");

        /* Get Avatar representation of this function */
        AvatarLibraryFunction aLibraryFunction = listE.getAvatarLibraryFunction (libraryFunction);
        if (aLibraryFunction == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Unknown library function '" + libraryFunction.getFunctionName () + "'");

        /* create Avatar representation of the function call */
        AvatarLibraryFunctionCall alfc = new AvatarLibraryFunctionCall ("library_function_call", aLibraryFunction, asmdlfc);

        /* Get the list of parameters passed to the function */
        LinkedList<TAttribute> parameters = asmdlfc.getParameters ();
        /* If the number of parameters does not match raise an error */
        if (parameters.size () != libraryFunction.getParameters ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Calling library function " + libraryFunction.getFunctionName () + " requires " + libraryFunction.getParameters ().size () + "parameters (" + parameters.size () + " provided)");

        /* Loop through the parameters */
        int i=0;
        for (TAttribute ta: parameters) {
            i ++;
            /* If parameter has not be filled in raise an error */
            if (ta == null)
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Missing parameter #" + i + " when calling library function " + libraryFunction.getFunctionName ());

            /* Check if type of parameter matches what's expected */
            TAttribute returnTA = libraryFunction.getParameters ().get (i-1);
            if (!ta.hasSameType (returnTA))
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Type of parameter #" + i + " when calling library function " + libraryFunction.getFunctionName () + " does not match");

            /* Creates all the parameters corresponding to this parameter */
            LinkedList<String> parameterNames = new LinkedList<String> ();
            if (ta.getType() == TAttribute.INTEGER
                || ta.getType() == TAttribute.NATURAL
                || ta.getType() == TAttribute.BOOLEAN
                || ta.getType() == TAttribute.TIMER)
                parameterNames.add (ta.getId ());
            else {
                LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (ta.getTypeOther ());
                if (types == null || types.isEmpty ())
                    throw new CheckingError (CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + ta.getTypeOther () + " when calling " + libraryFunction.getFunctionName ());

                for (TAttribute type: types)
                    parameterNames.add (ta.getId () + "__" + type.getId ());
            }

            /* Add flattened parameters */
            for (String parameterName: parameterNames) {
                /* Try to get the corresponding attribute */
                AvatarAttribute attr = _ab.getAvatarAttributeWithName (parameterName);
                /* If it does not exist raise an error */
                if (attr == null)
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Parameter '" + ta.getId () + "' passed when calling library function " + libraryFunction.getFunctionName () + " does not exist");
                alfc.addParameter (attr);
            }
        }

        /* Get the list of signals mapped to the function's placeholders */
        LinkedList<ui.AvatarSignal> signals = asmdlfc.getSignals ();
        /* If the number of signals does not match raise an error */
        if (signals.size () != libraryFunction.getSignals ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Calling library function " + libraryFunction.getFunctionName () + " requires " + libraryFunction.getSignals ().size () + " signals (" + signals.size () + " mapped)");

        /* Loop through the signals */
        i=0;
        for (ui.AvatarSignal uias: signals) {
            i ++;
            /* If signal has not be filled in raise an error */
            if (uias == null)
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Missing mapping for signal #" + i + " when calling library function " + libraryFunction.getFunctionName ());

            /* Check if prototype of signal matches what's expected */
            ui.AvatarSignal expectedSig = libraryFunction.getSignals ().get (i-1);
            if (!expectedSig.hasSamePrototype (uias))
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Prototype of signal #" + i + " when calling library function " + libraryFunction.getFunctionName () + " does not match");

            /* Try to get the corresponding signal */
            avatartranslator.AvatarSignal sig = _ab.getAvatarSignalWithName (uias.getId ());
            /* If it does not exist raise an error */
            if (sig == null)
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Signal '" + uias.getId () + "' mapped when calling library function " + libraryFunction.getFunctionName () + " does not exist");
            alfc.addSignal (sig);
        }

        /* Get the list of return attributes passed to the function */
        LinkedList<TAttribute> returnAttributes = asmdlfc.getReturnAttributes ();
        /* If the number of return attributes is greater that what the function can return raise an error */
        if (returnAttributes.size () > libraryFunction.getReturnAttributes ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Calling library function " + libraryFunction.getFunctionName () + " can only return " + libraryFunction.getReturnAttributes ().size () + " values (" + returnAttributes.size () + " expected)");

        /* Loop through the return attributes */
        i=0;
        for (TAttribute ta: returnAttributes) {
            LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute> ();
            /* If return attribute has not be filled in, add a dummy one */
            if (ta == null) {
                TAttribute returnTA = libraryFunction.getReturnAttributes ().get (i);
                String dummyName = "__dummy_return_attribute_" + returnTA.getId ();

                /* Creates all the attributes corresponding to this return attribute */
                if (returnTA.getType() == TAttribute.INTEGER
                    || returnTA.getType() == TAttribute.NATURAL
                    || returnTA.getType() == TAttribute.BOOLEAN
                    || returnTA.getType() == TAttribute.TIMER) {
                    AvatarAttribute attr = _ab.getAvatarAttributeWithName (dummyName);
                    if (attr == null) {
                        attr = this.createRegularAttribute (_ab, returnTA, "__dummy_return_attribute_");
                        _ab.addAttribute (attr);
                    }
                    attrs.add (attr);
                } else {
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (returnTA.getTypeOther ());
                    if (types == null || types.isEmpty ())
                        throw new CheckingError (CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + returnTA.getTypeOther () + " when calling " + libraryFunction.getFunctionName ());

                    for (TAttribute type: types) {
                        String attributeName = dummyName + "__" + type.getId ();
                        AvatarAttribute attr = _ab.getAvatarAttributeWithName (attributeName);
                        if (attr == null) {
                            attr = this.createRegularAttribute (_ab, type, dummyName + "__");
                            _ab.addAttribute (attr);
                        }
                        attrs.add (attr);
                    }
                }
            } else {
                /* Check if type of return attribute matches what's expected */
                TAttribute returnTA = libraryFunction.getReturnAttributes ().get (i);
                if (!ta.hasSameType (returnTA))
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Type of return attribute #" + (i+1) + " when calling library function " + libraryFunction.getFunctionName () + " does not match");

                /* Creates all the attributes corresponding to this return attribute */
                LinkedList<String> attributeNames = new LinkedList<String> ();
                if (ta.getType() == TAttribute.INTEGER
                    || ta.getType() == TAttribute.NATURAL
                    || ta.getType() == TAttribute.BOOLEAN
                    || ta.getType() == TAttribute.TIMER)
                    attributeNames.add (ta.getId ());
                else {
                    LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (ta.getTypeOther ());
                    if (types == null || types.isEmpty ())
                        throw new CheckingError (CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + ta.getTypeOther () + " when calling " + libraryFunction.getFunctionName ());

                    for (TAttribute type: types)
                        attributeNames.add (ta.getId () + "__" + type.getId ());
                }

                /* Add flattened parameters */
                for (String attributeName: attributeNames) {
                    AvatarAttribute attr = _ab.getAvatarAttributeWithName (attributeName);
                    /* If a return attribute was given but we can't find the corresponding one raise an error */
                    if (attr == null)
                        throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Attribute '" + ta.getId () + "' expected to hold return value #" + (i+1) + " when calling library function " + libraryFunction.getFunctionName () + " does not exist");
                    attrs.add (attr);
                }
            }

            for (AvatarAttribute attr: attrs)
                alfc.addReturnAttribute (attr);
            i ++;
        }

        /* If there were missing return attributes, add dummies ones */
        for (; i<libraryFunction.getReturnAttributes ().size (); i++) {
            TAttribute returnTA = libraryFunction.getReturnAttributes ().get (i);
            String dummyName = "__dummy_return_attribute_" + returnTA.getId ();

            LinkedList<AvatarAttribute> attrs = new LinkedList<AvatarAttribute> ();
            /* Creates all the attributes corresponding to this return attribute */
            if (returnTA.getType() == TAttribute.INTEGER
                || returnTA.getType() == TAttribute.NATURAL
                || returnTA.getType() == TAttribute.BOOLEAN
                || returnTA.getType() == TAttribute.TIMER) {
                AvatarAttribute attr = _ab.getAvatarAttributeWithName (dummyName);
                if (attr == null) {
                    attr = this.createRegularAttribute (_ab, returnTA, "__dummy_return_attribute_");
                    _ab.addAttribute (attr);
                }
                attrs.add (attr);
            } else {
                LinkedList<TAttribute> types = adp.getAvatarBDPanel ().getAttributesOfDataType (returnTA.getTypeOther ());
                if (types == null || types.isEmpty ())
                    throw new CheckingError (CheckingError.STRUCTURE_ERROR, "Unknown data type:  " + returnTA.getTypeOther () + " when calling " + libraryFunction.getFunctionName ());

                for (TAttribute type: types) {
                    String attributeName = dummyName + "__" + type.getId ();
                    AvatarAttribute attr = _ab.getAvatarAttributeWithName (attributeName);
                    if (attr == null) {
                        attr = this.createRegularAttribute (_ab, type, dummyName + "__");
                        _ab.addAttribute (attr);
                    }
                    attrs.add (attr);
                }
            }

            for (AvatarAttribute attr: attrs)
                alfc.addReturnAttribute (attr);
        }

        this.listE.addCor (alfc, asmdlfc);
        asmdlfc.setAVATARID (alfc.getID());
        asm.addElement (alfc);
    }


    private void translateAvatarSMDReceiveSignal (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDReceiveSignal asmdrs) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        avatartranslator.AvatarSignal atas = _ab.getAvatarSignalWithName (asmdrs.getSignalName ());
        if (atas == null)
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Unknown signal: " + asmdrs.getSignalName());

        // Get relation of that signal
        if (_ab instanceof AvatarBlock) {
            // Note that for library functions, signals are just placeholders so they don't need to be connected to anything
            AvatarRelation ar = _as.getAvatarRelationWithSignal (atas);
            if (ar == null) {
		if (atas.getReferenceObject() instanceof ui.AvatarSignal) {
		    //TraceManager.addDev("Receive/ Setting as attached " + atas);
		    ((ui.AvatarSignal) atas.getReferenceObject()).attachedToARelation = false;
		}
                throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Signal used for receiving in " + asmdrs.getValue() + " is not connected to a channel");
	    }
	    if (atas.getReferenceObject() instanceof ui.AvatarSignal) {
		//TraceManager.addDev("Receive/ Setting as attached " + atas);
		((ui.AvatarSignal) atas.getReferenceObject()).attachedToARelation = true;
	    }
        }
	if (atas.getReferenceObject() instanceof ui.AvatarSignal) {
	    ((ui.AvatarSignal) atas.getReferenceObject()).attachedToARelation = true;
	}
	
        AvatarActionOnSignal aaos = new AvatarActionOnSignal ("action_on_signal", atas, asmdrs);
        if (asmdrs.hasCheckableAccessibility())
            aaos.setCheckable();

        if (asmdrs.hasCheckedAccessibility())
            aaos.setChecked();

        if (aaos.isSending())
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "A sending signal is used for receiving: " + asmdrs.getValue());

        if (asmdrs.getNbOfValues() == -1)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal: " + asmdrs.getValue());

        for(int i=0; i<asmdrs.getNbOfValues(); i++) {
            String tmp = asmdrs.getValue(i);
            if (tmp.isEmpty ())
                throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Empty parameter in signal expression: " + asmdrs.getValue());

            this.manageAttribute (tmp, _ab, aaos, tdp, asmdrs, asmdrs.getValue());
        }

        if (aaos.getNbOfValues () != atas.getListOfAttributes ().size ())
            throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> nb of parameters does not match definition");

        // Checking expressions passed as parameter
        for (int i=0; i<aaos.getNbOfValues(); i++) {
            String theVal = aaos.getValue(i);
            if (atas.getListOfAttributes ().get (i).isInt ()) {
                if (AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, theVal) < 0)
                    throw new CheckingError (CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
            } else {
                // We assume it is a bool attribute
                if (AvatarSyntaxChecker.isAValidBoolExpr(_as, _ab, theVal) < 0)
                    throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed signal receiving: " + asmdrs.getValue() + " -> value at index #" + i + " does not match definition");
            }
        }

        this.listE.addCor (aaos, asmdrs);
        asmdrs.setAVATARID (aaos.getID());
        asm.addElement (aaos);
		if (asmdrs.getCheckLatency()){
			aaos.setCheckLatency(true);
			_as.checkedIDs.add(asmdrs.getName()+"-"+asmdrs.getSignalName()+":"+aaos.getID());
		}
    }

    private void translateAvatarSMDState (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDState tgc) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        AvatarState astate = asm.getStateWithName(tgc.getValue());
        if (astate == null) {
            astate = new AvatarState (tgc.getValue(), tgc);
            astate.setAsVerifiable(true);
            asm.addElement (astate);
        }

        if (tgc.hasCheckableAccessibility ())
            astate.setCheckable ();

        if (tgc.hasCheckedAccessibility())
            astate.setChecked();

        // Executable code
        astate.addEntryCode(tgc.getEntryCode());

        this.listE.addCor (astate, tgc);
        astate.addReferenceObject (tgc);
        tgc.setAVATARID (astate.getID());
		if (tgc.getCheckLatency()){
			astate.setCheckLatency(true);
			_as.checkedIDs.add(tgc.getName()+"-"+tgc.getValue()+":"+astate.getID());
		}
    }

    private void translateAvatarSMDRandom (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDRandom asmdrand) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        AvatarRandom arandom = new AvatarRandom ("random", asmdrand);
        String tmp1 = modifyString (asmdrand.getMinValue());
        int error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp1);
        if (error < 0)
            this.makeError (error, tdp, _ab, asmdrand, "min value of random", tmp1);

        String tmp2 = modifyString(asmdrand.getMaxValue());
        error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
        if (error < 0)
            this.makeError (error, tdp, _ab, asmdrand, "max value of random", tmp2);

        arandom.setValues (tmp1, tmp2);
        arandom.setFunctionId (asmdrand.getFunctionId());

        tmp1 = modifyString(asmdrand.getVariable());
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp1);

        if (aa == null)
            this.makeError (-3, tdp, _ab, asmdrand, "random", tmp1);
        // Checking type of variable -> must be an int
        else if (!(aa.isInt()))
            this.makeError (error, tdp, _ab, asmdrand, ": variable of random must be of type \"int\"", tmp2);

        arandom.setVariable (tmp1);

        asm.addElement (arandom);
        listE.addCor (arandom, asmdrand);
        asmdrand.setAVATARID (arandom.getID());
    }

    private void translateAvatarSMDSetTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDSetTimer asmdst) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdst.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName(tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer setting");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer setting: shall be a parameter of type \"Timer\"");

        tmp = this.modifyString (asmdst.getTimerValue ());
        int error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp);
        if (error < 0)
            this.makeError(error, tdp, _ab, asmdst, "value of the timer setting", tmp);

        AvatarSetTimer asettimer = new AvatarSetTimer("settimer__" + aa.getName(), asmdst);
        asettimer.setTimer (aa);
        asettimer.setTimerValue (tmp);
        asm.addElement (asettimer);
        this.listE.addCor (asettimer, asmdst);
        asmdst.setAVATARID (asettimer.getID());
    }

    private void translateAvatarSMDResetTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDResetTimer asmdrt) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdrt.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer reset");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer reset: shall be a parameter of type \"Timer\"");

        AvatarResetTimer aresettimer = new AvatarResetTimer("resettimer__" + aa.getName(), asmdrt);
        aresettimer.setTimer (aa);
        asm.addElement(aresettimer);
        this.listE.addCor (aresettimer, asmdrt);
        asmdrt.setAVATARID (aresettimer.getID());
    }

    private void translateAvatarSMDExpireTimer (TDiagramPanel tdp, AvatarSpecification _as, AvatarStateMachineOwner _ab, AvatarSMDExpireTimer asmdet) throws CheckingError {
        AvatarStateMachine asm = _ab.getStateMachine ();
        String tmp = asmdet.getTimerName();
        AvatarAttribute aa = _ab.getAvatarAttributeWithName (tmp);
        if (aa == null)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed timer parameter: " + tmp + " in timer expiration");

        if (aa.getType() != AvatarType.TIMER)
            throw new CheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed parameter: " + tmp + " in timer expiration: shall be a parameter of type \"Timer\"");

        AvatarExpireTimer aexpiretimer = new AvatarExpireTimer("expiretimer__" + aa.getName(), asmdet);
        aexpiretimer.setTimer(aa);
        asm.addElement(aexpiretimer);
        this.listE.addCor(aexpiretimer, asmdet);
        asmdet.setAVATARID(aexpiretimer.getID());
    }

    public void makeStateMachine (AvatarSpecification _as, AvatarStateMachineOwner _ab) {
        AvatarBDStateMachineOwner block = (AvatarBDStateMachineOwner) listE.getTG (_ab);
        AvatarStateMachine asm = _ab.getStateMachine ();

        if (block == null) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "No corresponding graphical block for " + _ab.getName());
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(adp.getAvatarBDPanel());
            addCheckingError(ce);
            return;
        }

        AvatarSMDPanel asmdp = block.getAvatarSMDPanel();
        if (asmdp == null)
            return;

        String name = block.getOwnerName();

        int size = checkingErrors.size();

        TDiagramPanel tdp = asmdp;

        // search for start state
        AvatarSMDStartState tss = null;
        for (TGComponent tgc: asmdp.getComponentList ())
            if (tgc instanceof AvatarSMDStartState) {
                if (tss == null)
                    tss = (AvatarSMDStartState) tgc;
                else {
                    UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in the state machine diagram of " + name);
                    ce.setTDiagramPanel(tdp);
                    addCheckingError(ce);
                    return;
                }
            }

        if (tss == null) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "No start state in the state machine diagram of " + name);
            ce.setTDiagramPanel(tdp);
            addCheckingError(ce);
            return;
        }

        // This shall also be true for all composite state: at most one start state!
        if (checkForStartStateOfCompositeStates (asmdp) != null) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "More than one start state in composite state");
            ce.setTDiagramPanel(tdp);
            addCheckingError(ce);
            return;
        }

        int choiceID = 0;
        // First pass: creating AVATAR components, but no interconnection between them
        for (TGComponent tgc: asmdp.getAllComponentList ())
            try {
                // Receive signal
                if (tgc instanceof AvatarSMDReceiveSignal)
                    this.translateAvatarSMDReceiveSignal (tdp, _as, _ab, (AvatarSMDReceiveSignal) tgc);
                // Send signals
                else if (tgc instanceof AvatarSMDSendSignal)
                    this.translateAvatarSMDSendSignal (tdp, _as, _ab, (AvatarSMDSendSignal) tgc);
                // Library Function Call
                else if (tgc instanceof AvatarSMDLibraryFunctionCall)
                    this.translateAvatarSMDLibraryFunctionCall (tdp, _as, _ab, (AvatarSMDLibraryFunctionCall) tgc);
                // State
                else if (tgc instanceof AvatarSMDState)
                    this.translateAvatarSMDState (tdp, _as, _ab, (AvatarSMDState) tgc);
                // Choice
                else if (tgc instanceof AvatarSMDChoice) {
                    AvatarState astate = new AvatarState ("choice__" + choiceID, tgc);
                    choiceID ++;
                    asm.addElement (astate);
                    listE.addCor (astate, tgc);
                    tgc.setAVATARID (astate.getID());
                }
                // Random
                else if (tgc instanceof AvatarSMDRandom)
                    this.translateAvatarSMDRandom (tdp, _as, _ab, (AvatarSMDRandom) tgc);
                // Set timer
                else if (tgc instanceof AvatarSMDSetTimer)
                    this.translateAvatarSMDSetTimer (tdp, _as, _ab, (AvatarSMDSetTimer) tgc);
                // Reset timer
                else if (tgc instanceof AvatarSMDResetTimer)
                    this.translateAvatarSMDResetTimer (tdp, _as, _ab, (AvatarSMDResetTimer) tgc);
                // Expire timer
                else if (tgc instanceof AvatarSMDExpireTimer)
                    this.translateAvatarSMDExpireTimer (tdp, _as, _ab, (AvatarSMDExpireTimer) tgc);
                // Start state
                else if (tgc instanceof AvatarSMDStartState) {
                    AvatarStartState astart = new AvatarStartState("start", tgc);
                    this.listE.addCor (astart, tgc);
                    tgc.setAVATARID (astart.getID());
                    asm.addElement(astart);
                    astart.setAsVerifiable(true);
                    if (tgc.getFather() == null)
                        asm.setStartState(astart);
                    // Stop state
                } else if (tgc instanceof AvatarSMDStopState) {
                    AvatarStopState astop = new AvatarStopState ("stop", tgc);
                    astop.setAsVerifiable(true);
                    this.listE.addCor(astop, tgc);
                    tgc.setAVATARID(astop.getID());
                    asm.addElement(astop);
                }
            } catch (CheckingError ce) {
                // TODO: adapt
                // ce.setAvatarBlock (_ab);
                UICheckingError uice = new UICheckingError(ce);
                uice.setTDiagramPanel (tdp);
                uice.setTGComponent (tgc);
                uice.addMessagePrefix ("State Machine of " + name + ": ");
                this.addCheckingError (uice);
            }

        if (checkingErrors.size() != size)
            return;

        // Remove all internal start states
        asm.removeAllInternalStartStates();

        // Make hierachy between states and elements
        for (TGComponent tgc: asmdp.getAllComponentList ())
            if (tgc != null && tgc.getFather() != null) {
                AvatarStateMachineElement element1 = (AvatarStateMachineElement)(listE.getObject(tgc));
                AvatarStateMachineElement element2 = (AvatarStateMachineElement)(listE.getObject(tgc.getFather()));
                if (element1 != null && element2 != null && element2 instanceof AvatarState)
                    element1.setState ((AvatarState) element2);
            }

        // Make next: handle transitions
        for (TGComponent tgc: asmdp.getAllComponentList ())
            if (tgc instanceof AvatarSMDConnector) {
                AvatarSMDConnector asmdco = (AvatarSMDConnector) tgc;
                TGComponent tgc1 = tdp.getComponentToWhichBelongs (asmdco.getTGConnectingPointP1());
                TGComponent tgc2 = tdp.getComponentToWhichBelongs (asmdco.getTGConnectingPointP2());
                if (tgc1 == null || tgc2 == null)
                    TraceManager.addDev("Tgcs null in Avatar translation");
                else {
                    AvatarStateMachineElement element1 = (AvatarStateMachineElement)(listE.getObject(tgc1));
                    AvatarStateMachineElement element2 = (AvatarStateMachineElement)(listE.getObject(tgc2));
                    if (element1 != null && element2 != null) {
                        AvatarTransition at = new AvatarTransition (_ab, "avatar transition", tgc);

                        // Guard
                        String tmp = modifyString (asmdco.getGuard());
                        AvatarGuard guard = AvatarGuard.createFromString (_ab, tmp);
                        if (guard.isElseGuard())
                            at.setGuard(guard);
                        else {
                            int error = AvatarSyntaxChecker.isAValidGuard (_as, _ab, tmp);
                            if (error < 0)
                                this.makeError (error, tdp, _ab, tgc, "transition guard", tmp);
                            else
                                at.setGuard (guard);
                        }

                        // Delays
                        String tmp1 = modifyString (asmdco.getAfterMinDelay ());
                        int error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp1);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "after min delay", tmp1);
                            tmp1 = null;
                        }
                        String tmp2 = modifyString (asmdco.getAfterMaxDelay ());
                        error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp2);
                        if (error < 0) {
                            this.makeError(error, tdp, _ab, tgc, "after max delay", tmp2);
                            tmp2 = null;
                        }

                        if (tmp1 != null && tmp2 != null)
                            at.setDelays(tmp1, tmp2);

                        // Compute min and max
                        tmp1 = modifyString (asmdco.getComputeMinDelay ());
                        error = AvatarSyntaxChecker.isAValidIntExpr (_as, _ab, tmp1);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "compute min ", tmp1);
                            tmp1 = null;
                        }
                        tmp2 = modifyString(asmdco.getComputeMaxDelay());
                        error = AvatarSyntaxChecker.isAValidIntExpr(_as, _ab, tmp2);
                        if (error < 0) {
                            this.makeError (error, tdp, _ab, tgc, "compute max ", tmp2);
                            tmp2 = null;
                        }

                        if (tmp1 != null && tmp2 != null)
                            at.setComputes(tmp1, tmp2);

                        // Probability
                        tmp1 = asmdco.getProbability ();
                        if ((tmp1 != null) && (tmp1.length()>0)) {
                            error = AvatarSyntaxChecker.isAValidProbabilityExpr(_as, _ab, tmp1);
                            if (error < 0) {
                                this.makeError(error, tdp, _ab, tgc, "probability ", tmp1);
                                tmp1 = null;
                            }
                            if (tmp1 != null) {
                                at.setProbability(new Double(tmp1).doubleValue());
                            }
                        }

                        // Actions
                        for(String s: asmdco.getActions())
                            if (s.trim().length() > 0) {
                                s = modifyString(s.trim());

                                // Variable assignation or method call?
                                if (!isAVariableAssignation(s)) {
                                    // Method call
                                    int index2 = s.indexOf(";");
                                    if (index2 != -1)
                                        this.makeError(error, tdp, _ab, tgc, "transition action", s);

                                    s = modifyStringMethodCall(s, _ab.getName());
                                    if (!AvatarBlock.isAValidMethodCall (_ab, s)) {
                                        UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formed transition method call: " + s);
                                        // TODO: adapt
                                        // ce.setAvatarBlock(_ab);
                                        ce.setTDiagramPanel(tdp);
                                        ce.setTGComponent(tgc);
                                        addCheckingError(ce);
                                    } else
                                        at.addAction(s);
                                } else {
                                    // Variable assignation
                                    error = AvatarSyntaxChecker.isAValidVariableExpr (_as, _ab, s);
                                    if (error < 0)
                                        this.makeError (error, tdp, _ab, tgc, "transition action", s);
                                    else
                                        at.addAction (s);
                                }
                            }

                        element1.addNext (at);
                        at.addNext (element2);
                        this.listE.addCor (at, tgc);
                        tgc.setAVATARID (at.getID());
                        asm.addElement (at);

                        // Check for after on composite transitions
                        if (at.hasDelay() && element1 instanceof AvatarState && asm.isACompositeTransition(at)) {
                            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "After clause cannot be used on composite transitions. Use timers instead.");
                            // TODO: adapt
                            // ce.setAvatarBlock(_ab);
                            ce.setTDiagramPanel(tdp);
                            ce.setTGComponent(tgc);
                            addCheckingError(ce);
                        }
                    }
                }
            }

        asm.handleUnfollowedStartState(_ab);

        // Investigate all states -> put warnings for all empty transitions from a state to the same one (infinite loop)
      //  int nb;
        for (AvatarStateMachineElement asmee: asm.getListOfElements())
            if (asmee instanceof AvatarState && ((AvatarState)asmee).hasEmptyTransitionsOnItself(asm) > 0) {
                UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "State(s) " + asmee.getName() + " has empty transitions on itself");
                // TODO: adapt
                // ce.setAvatarBlock(_ab);
                ce.setTDiagramPanel(tdp);
                ce.setTGComponent((TGComponent)(asmee.getReferenceObject()));
                addWarning(ce);
            }
    }

    private void makeError(int _error, TDiagramPanel _tdp, AvatarStateMachineOwner _ab, TGComponent _tgc, String _info, String _element) {
        if (_error == -3) {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Undeclared variable in " + _info + ": " + _element);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
        } else {
            UICheckingError ce = new UICheckingError(CheckingError.BEHAVIOR_ERROR, "Badly formatted " + _info + ": " + _element);
            // TODO: adapt
            // ce.setAvatarBlock(_ab);
            ce.setTDiagramPanel(_tdp);
            ce.setTGComponent(_tgc);
            addCheckingError(ce);
        }
    }

    // Checks whether all states with internal state machines have at most one start state
    private TGComponent checkForStartStateOfCompositeStates(AvatarSMDPanel _panel) {
        TGComponent tgc;
        Iterator<TGComponent> iterator = _panel.getComponentList().listIterator();
        
        while(iterator.hasNext()) {
            tgc = iterator.next();
            
            if (tgc instanceof AvatarSMDState) {
                tgc = (((AvatarSMDState)(tgc)).checkForStartStateOfCompositeStates());
                if (tgc != null) {
                    return tgc;
                }
            }
        }
        
        return null;
    }


    public void createRelationsBetweenBlocks(AvatarSpecification _as, List<AvatarBDBlock> _blocks) {
        adp.getAvatarBDPanel().updateAllSignalsOnConnectors();
        Iterator<TGComponent> iterator = adp.getAvatarBDPanel().getComponentList().listIterator();

        TGComponent tgc;
        AvatarBDPortConnector port;
        AvatarBDBlock block1, block2;
        LinkedList<String> l1, l2;
        int i;
        String name1, name2;
        AvatarRelation r;
        AvatarBlock b1, b2;
        avatartranslator.AvatarSignal atas1, atas2;

        while(iterator.hasNext()) {
            tgc = iterator.next();
           
            if (tgc instanceof AvatarBDPortConnector) {
                port = (AvatarBDPortConnector)tgc;
                block1 = port.getAvatarBDBlock1();
                block2 = port.getAvatarBDBlock2();

                //TraceManager.addDev("Searching block #1 with name " + block1.getBlockName() + " and block #2 with name " + block2.getBlockName());
                b1 = _as.getBlockWithName(block1.getBlockName());
                b2 = _as.getBlockWithName(block2.getBlockName());

                if ((b1 != null) && (b2 != null)) {
		    //TraceManager.addDev("B1 and B2 are not null");
                    r = new AvatarRelation("relation", b1, b2, tgc);
                    // Signals of l1
                    l1 = port.getListOfSignalsOrigin();
                    l2 = port.getListOfSignalsDestination();

                    for(i=0; i<l1.size(); i++) {
                        name1 = AvatarSignal.getSignalNameFromFullSignalString(l1.get(i));
                        name2 = AvatarSignal.getSignalNameFromFullSignalString(l2.get(i));
                        //TraceManager.addDev("Searching signal with name " + name1 +  " in block " + b1.getName());
                        atas1 = b1.getAvatarSignalWithName(name1);
                        atas2 = b2.getAvatarSignalWithName(name2);
			if ((atas1 != null) && (atas2 != null)) {
			    if(atas1.isCompatibleWith(atas2)) {
				//TraceManager.addDev("Signals " + atas1 + " and " + atas2 + " are compatible");
				r.addSignals(atas1, atas2);
			    } else {
				//TraceManager.addDev("Signals " + atas1 + " and " + atas2 + " are NOT compatible");
				UICheckingError ce = new UICheckingError(CheckingError.STRUCTURE_ERROR, "Wrong signal association betwen " + atas1 + " and " + atas2);
			    // TODO: adapt
			    // ce.setAvatarBlock(_ab);
				ce.setTDiagramPanel(tgc.getTDiagramPanel());
			    ce.setTGComponent(tgc);
			    addCheckingError(ce);
			    }
			} else {
			    TraceManager.addDev("Null signals in AVATAR relation: " + name1 + " " + name2);
			}
			
                    }
		    
                    // Attribute of the relation
                    r.setBlocking(port.isBlocking());
                    r.setAsynchronous(port.isAsynchronous());
                    r.setSizeOfFIFO(port.getSizeOfFIFO());
                    r.setPrivate(port.isPrivate());
                    r.setBroadcast(port.isBroadcast());
                    r.setLossy(port.isLossy());

                    _as.addRelation(r);
                } else {
                    TraceManager.addDev("Null block b1=" + b1 + " b2=" + b2);
                }
            }
        }
    }

    private void addCheckingError(CheckingError ce) {
        if (checkingErrors == null) {
            checkingErrors = new LinkedList<CheckingError> ();
        }
        checkingErrors.add (ce);
    }

    private void addWarning(CheckingError ce) {
        if (warnings == null) {
            warnings = new LinkedList<CheckingError> ();
        }
        warnings.add (ce);
    }

    private String modifyString(String _input) {
        return Conversion.replaceAllChar(_input, '.', "__");
    }

    private String modifyStringMethodCall(String _input, String _blockName) {

        int index0 = _input.indexOf('(');
        int index1 = _input.indexOf(')');

        if ((index0 == -1) || (index1 == -1) || (index1 < index0)) {
            return _input;
        }


        String s = _input.substring(index0+1, index1).trim();
       // String output = "";

        if (s.length() == 0) {
            return _input;
        }

        //TraceManager.addDev("-> -> Analyzing method call " + s);

        String [] actions = s.split(",");
        s = "";
        for(int i=0; i<actions.length; i++) {
            TAttribute ta = adp.getAvatarBDPanel().getAttribute(actions[i].trim(), _blockName);
            if (ta == null) {
                s = s + actions[i].trim();
            } else {
                if (ta.getType() == TAttribute.OTHER) {
                    boolean first = true;
                    for (TAttribute tatmp: adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther())) {
                        if (first)
                            first = false;
                        else
                            s = s + ", ";
                        s += actions[i].trim() + "__" + tatmp.getId();
                    }
                } else {
                    s = s + actions[i].trim();
                }
            }
            if (i != actions.length-1) {
                s = s + ", ";
            }
        }

        s  = _input.substring(0, index0) + "(" + s + ")";

        // Managing output parameters
        index0 = s.indexOf("=");
        if (index0 != -1) {
            String param = s.substring(0, index0).trim();
            TAttribute ta = adp.getAvatarBDPanel().getAttribute(param, _blockName);
            if (ta == null) {
                TraceManager.addDev("-> -> NULL Param " + param + " in block " + _blockName);
                s = param + s.substring(index0, s.length());
            } else {
                if (ta.getType() == TAttribute.OTHER) {
                    String newparams = "";
                    boolean first = true;
                    for (TAttribute tatmp: adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther())) {
                        if (first)
                            first = false;
                        else
                            newparams = newparams + ", ";
                        newparams += param + "__" + tatmp.getId();
                    }
                    if (adp.getAvatarBDPanel().getAttributesOfDataType(ta.getTypeOther()).size() > 1)
                        newparams = "(" + newparams + ")";
                    s = newparams + s.substring(index0, s.length());
                } else {
                    s = param + s.substring(index0, s.length());
                }
            }
        }

        //TraceManager.addDev("-> -> Returning method call " + s);

        return s;
    }

    public boolean isAVariableAssignation (String _input) {
        int index = _input.indexOf('=');
        if (index == -1) {
            return false;
        }

        // Must check whether what follows the '=' is a function or not.
        String tmp = _input.substring(index+1, _input.length()).trim();

        index = tmp.indexOf('(');
        if (index == -1) {
            return true;
        }

        tmp = tmp.substring(0, index);

        //TraceManager.addDev("rest= >" + tmp + "<");
        int length = tmp.length();
        tmp = tmp.trim();
        if (tmp.length() != length) {
            TraceManager.addDev("pb of length");
            return true;
        }

        return !(TAttribute.isAValidId(tmp, false, false));
    }

    public void checkForAfterOnCompositeTransition() {

    }



}
