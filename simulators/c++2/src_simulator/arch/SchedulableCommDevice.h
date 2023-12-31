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

#ifndef SchedulableCommDeviceH
#define SchedulableCommDeviceH

#include <SchedulableDevice.h>

class TMLTransaction;

///Base class for communication devices which perform a scheduling
class SchedulableCommDevice: public SchedulableDevice{
public:
	///Constructor
	/**
	\param iID ID of the device
	\param iName Name of the device
	\param iScheduler Pointer to the scheduler object
	\param iChannelBasedPrio Flag indicating whether bus master based or channel based priorities are used to arbitrate the bus
	*/
	SchedulableCommDevice(ID iID, std::string iName, WorkloadSource* iScheduler,bool iChannelBasedPrio): SchedulableDevice(iID, iName, iScheduler), _channelBasedPrio(iChannelBasedPrio){}
	///Returns the size of an atomic bus transaction
	/**
	\return Size of an atomic bus transaction
	*/
	virtual TMLLength getBurstSize() const=0;
	/////Truncates a transaction so that it does not exceed the burst size of the communication device
	//virtual void truncateToBurst(TMLTransaction* iTrans) const=0;
	///Signals the component that a new transaction has become available and thus rescheduling is needed
	virtual void registerTransaction()=0;
	bool ChannelBasedPrio(){return _channelBasedPrio;}
	///Destructor
	virtual ~SchedulableCommDevice(){}
	virtual void calcStartTimeLength(TMLTime iTimeSlice) const=0;
	virtual void calcLength() const=0;
protected:
	///Flag indicating whether bus master based or channel based priorities are used to arbitrate the bus
	bool _channelBasedPrio;

};

#endif
