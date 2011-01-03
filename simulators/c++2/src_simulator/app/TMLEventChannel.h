/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
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
 *
 */

#ifndef TMLEventChannelH
#define TMLEventChannelH

#include <definitions.h>
#include <TMLStateChannel.h>
#include <Parameter.h>
#include <HashAlgo.h>

class Bus;

///This class represents the base class for all event channels.
class TMLEventChannel:public TMLStateChannel{
public:
	///Constructor
    	/**
	\param iID ID of channel
      	\param iName Name of the channel
	\param iNumberOfHops Number of buses on which the channel is mapped
	\param iMasters Pointers to the masters which the channel is connected to
	\param iSlaves Pointers to the slaves on which the channel is mapped
	\param iContent Initial content of the channel
    	*/
	TMLEventChannel(ID iID, std::string iName, unsigned int iNumberOfHops, BusMaster** iMasters, Slave** iSlaves, TMLLength iContent);
	///Destructor
	virtual ~TMLEventChannel();
	///Cancels a pending read operation 
	virtual void cancelReadTransaction()=0;
	///Returns a flag indicating if the channel is used by a request
    	/**
      	\return True if channel is used by a request, false otherwise
	*/
	virtual bool getRequestChannel()  const;
	virtual std::ostream& writeObject(std::ostream& s);
	virtual std::istream& readObject(std::istream& s);
	void print()  const;
	virtual void reset();
	virtual void streamStateXML(std::ostream& s) const;
	void getStateHash(HashAlgo* iHash) const;
protected:
	///Queue for parameters
	ParamQueue _paramQueue;
	///Temporary buffer for the parameters of the registered write transaction 
	Parameter<ParamType> _tmpParam;
	///Channel State Hash
	mutable HashAlgo _stateHash;
	///Flag indicating whether the current hash is up to date
	mutable bool _hashValid;
};

#endif
