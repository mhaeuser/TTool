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

#ifndef TMLWriteCommandH
#define TMLWriteCommandH

#include <definitions.h>
#include <TMLCommand.h>
#include <TMLChannel.h>

///This class models write operations on TML channels.
class TMLWriteCommand:public TMLCommand {
public:
	///Constructor
    	/**
      	\param iID ID of the command
	\param iTask Pointer to the task the command belongs to
	\param iLengthFunc Pointer to the function returning the length of the command
	\param iChannel Pointer to the channel to which is written
	\param iLiveVarList Bitmap of live variables
	\param iCheckpoint Checkpoint Flag
	\param iStatLength Static length of command if applicable
	*/
	TMLWriteCommand(ID iID, TMLTask* iTask, LengthFuncPointer iLengthFunc, TMLChannel* iChannel, const char* iLiveVarList, bool iCheckpoint, TMLLength iStatLength=1);
	///Destructor
	virtual ~TMLWriteCommand(){}
	void execute();
	inline TMLChannel* getChannel(unsigned int iIndex) const {return _channel;}
	inline unsigned int getNbOfChannels() const {return 1;}
	inline TMLTask* getDependentTask(unsigned int iIndex)const {return _channel->getBlockedReadTask();}
	std::string toString() const;
	std::string toString(TMLLength aLength, TMLLength aProgress) const;
	std::string toShortString() const;
	std::string toShortString(TMLLength aLength, TMLLength aProgress) const;
	inline std::string getCommandStr() const {return "wr";}
protected:
	///Pointer to the function returning the length of the command
	LengthFuncPointer _lengthFunc;
	///Channel to which is written
	TMLChannel* _channel;
	TMLCommand* prepareNextTransaction();
};

#endif
