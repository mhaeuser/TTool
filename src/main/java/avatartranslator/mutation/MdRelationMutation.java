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
 * Class MdRelationMutation
 * Creation: 29/06/2022
 *
 * @author Léon FRENOT
 * @version 1.0 29/06/2022
 */
public class MdRelationMutation extends RelationMutation implements MdMutation {
    
    private RelationMutation current;
    
    public MdRelationMutation(String _block1, String _block2) {
        super(_block1, _block2);
        current = new NoneRelationMutation(_block1, _block2);
    }

    public MdRelationMutation(String _relationString, int _relationType) {
        super(_relationString, _relationType);
        current = new NoneRelationMutation(_relationString, _relationType);
    }

    public void setCurrentBlocking(boolean b) {
        current.setBlocking(b);
    }

    public void setCurrentAsynchronous(boolean b) {
        current.setAsynchronous(b);
    }

    public void setCurrentAMS(boolean b) {
        current.setAMS(b);
    }

    public void setCurrentPrivate(boolean b) {
        current.setPrivate(b);
    }

    public void setCurrentBroadcast(boolean b) {
        current.setBroadcast(b);
    }

    public void setCurrentLossy(boolean b) {
        current.setLossy(b);
    }

    public void setCurrentSizeOfFIFO(String _sizeOfFIFO) {
        current.setSizeOfFIFO(_sizeOfFIFO);
    }

    public void setCurrentSizeOfFIFO(int _sizeOfFIFO) {
        current.setSizeOfFIFO(_sizeOfFIFO);
    }

    public void setCurrentId(int _id) {
        current.setId(_id);
    }

    public void apply(AvatarSpecification _avspec) {
        AvatarRelation relation = current.getElement(_avspec);

        if (this.blockingSet()) relation.setBlocking(this.isBlocking());

        if (this.asynchronousSet()) relation.setAsynchronous(this.isAsynchronous());

        if (this.AMSSet()) relation.setAMS(this.isAMS());

        if (this.privateSet()) relation.setPrivate(this.isPrivate());

        if (this.broadcastSet()) relation.setBroadcast(this.isBroadcast());

        if (this.lossySet()) relation.setLossy(this.isLossy());

        if (this.sizeOfFIFOSet()) relation.setSizeOfFIFO(this.getSizeOfFIFO());

        if (this.idSet()) relation.setId(this.getId());
    }

    public static MdRelationMutation createFromString(String toParse) {

        MdRelationMutation mutation = null;
        String[] tokens = MutationParser.tokenise(toParse);

        int index = MutationParser.indexOf(tokens, "BETWEEN");
        String _block1 = tokens[index + 1];
        String _block2 = tokens[index + 3];


        index = MutationParser.indexOf(tokens, "LINK");
        if (tokens.length <= index + 1 || MutationParser.isToken(tokens[index+1])) {
            mutation = new MdRelationMutation(_block1, _block2);
        } else {
            String _name = tokens[index + 1];
            int _nameType = MutationParser.UUIDType(_name);
            mutation = new MdRelationMutation(_name, _nameType);
        }

        int toIndex = MutationParser.indexOf(tokens, "TO");
        boolean b;

        index = MutationParser.indexOf(tokens, "PUBLIC");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(false);
                index = MutationParser.indexOf(index, tokens, "PUBLIC");
                if (index != -1) {
                    mutation.setPrivate(false);
                }
            } else {
                mutation.setPrivate(false);
            }
        }

        index = MutationParser.indexOf(tokens, "PRIVATE");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(true);
                index = MutationParser.indexOf(index, tokens, "PRIVATE");
                if (index != -1) {
                    mutation.setPrivate(true);
                }
            } else {
                mutation.setPrivate(true);
            }
        }

        index = MutationParser.indexOf(tokens, "SYNCHRONOUS");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(false);
                index = MutationParser.indexOf(index, tokens, "SYNCHRONOUS");
                if (index != -1) {
                    mutation.setPrivate(false);
                }
            } else {
                mutation.setPrivate(false);
            }
        } 

        index = MutationParser.indexOf(tokens, "SYNCH");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(false);
                index = MutationParser.indexOf(index, tokens, "SYNCH");
                if (index != -1) {
                    mutation.setPrivate(false);
                }
            } else {
                mutation.setPrivate(false);
            }
        } 

        index = MutationParser.indexOf(tokens, "ASYNCHRONOUS");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(true);
                index = MutationParser.indexOf(index, tokens, "ASYNCHRONOUS");
                if (index != -1) {
                    mutation.setPrivate(true);
                }
            } else {
                mutation.setPrivate(true);
            }
        } 

        index = MutationParser.indexOf(tokens, "ASYNCH");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentPrivate(true);
                index = MutationParser.indexOf(index, tokens, "ASYNCH");
                if (index != -1) {
                    mutation.setPrivate(true);
                }
            } else {
                mutation.setPrivate(true);
            }
        }

        index = MutationParser.indexOf(tokens, "AMS");
        if (index != -1) {
            b = !tokens[index - 1].toUpperCase().equals("NO");
            TraceManager.addDev("AMS : " + b + " " + (index < toIndex));
            if (index < toIndex) {
                mutation.setCurrentAMS(b);
                index = MutationParser.indexOf(index, tokens, "AMS");
                if (index != -1) {
                    b = !tokens[index - 1].toUpperCase().equals("NO");
                    mutation.setAMS(b);
                }
            } else {
                b = !tokens[index - 1].toUpperCase().equals("NO");
                TraceManager.addDev("setAMS " + b);
                mutation.setAMS(b);
            }
        }

        index = MutationParser.indexOf(tokens, "LOSSY");
        if (index != -1) {
            b = !tokens[index - 1].toUpperCase().equals("NO");
            if (index < toIndex) {
                mutation.setCurrentLossy(b);
                index = MutationParser.indexOf(index, tokens, "LOSSY");
                if (index != -1) {
                    b = !tokens[index - 1].toUpperCase().equals("NO");
                    mutation.setLossy(b);
                }
            } else {
                mutation.setLossy(b);
            }
        }

        index = MutationParser.indexOf(tokens, "BLOCKING");
        if (index != -1) {
            b = !tokens[index - 1].toUpperCase().equals("NO");
            if (index < toIndex) {
                mutation.setCurrentBlocking(b);
                index = MutationParser.indexOf(index, tokens, "BLOCKING");
                if (index != -1) {
                    b = !tokens[index - 1].toUpperCase().equals("NO");
                    mutation.setBlocking(b);
                }
            } else {
                mutation.setBlocking(b);
            }
        }

        index = MutationParser.indexOf(tokens, "BROADCAST");
        if (index != -1) {
            b = !tokens[index - 1].toUpperCase().equals("NO");
            if (index < toIndex) {
                mutation.setCurrentBroadcast(b);
                index = MutationParser.indexOf(index, tokens, "BROADCAST");
                if (index != -1) {
                    b = !tokens[index - 1].toUpperCase().equals("NO");
                    mutation.setBroadcast(b);
                }
            } else {
                mutation.setBroadcast(b);
            }
        }

        index = MutationParser.indexOf(tokens, "MAXFIFO");
        if (index != -1) {
            if (index < toIndex) {
                mutation.setCurrentSizeOfFIFO(tokens[index + 2]);
                index = MutationParser.indexOf(index, tokens, "MAXFIFO");
                if (index != -1) {
                    mutation.setSizeOfFIFO(tokens[index + 2]);
                }
            } else {
                mutation.setSizeOfFIFO(tokens[index + 2]);
            }
        }
        
        return mutation;
    }
    
}