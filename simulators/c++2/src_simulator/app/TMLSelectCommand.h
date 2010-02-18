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

#ifndef TMLSelectCommandH
#define TMLSelectCommandH

#include <definitions.h>
#include <TMLCommand.h>

class TMLEventChannel;

///This class models the waiting operation for one amongst multiple events.
class TMLSelectCommand:public TMLCommand{
public:
	///Constructor
    	/**
      	\param iID ID of the command
      	\param iTask Pointer to the task the command belongs to
	\param iChannel Pointer to an array of pointers to channels conveying the desired signals
	\param iNumbChannels Number of channels in the array
	\param iParamFunc Pointer to an array of parameter functions pointers
	*/
	TMLSelectCommand(ID iID, TMLTask* iTask,TMLEventChannel** iChannel, unsigned int iNumbChannels, ParamFuncPointer* iParamFunc);
	///Destructor
	~TMLSelectCommand();
	void execute();
	TMLChannel* getChannel(unsigned int iIndex) const;
	unsigned int getNbOfChannels() const;
	TMLTask* getDependentTask(unsigned int iIndex)const;
	ParamFuncPointer getParamFuncPointer() const;
	std::string toString() const;
	std::string toShortString() const;
	std::string getCommandStr() const;
#ifdef ADD_COMMENTS
	std::string getCommentString(Comment* iCom)  const;
#endif
protected:
	TMLCommand* prepareNextTransaction();
	TMLCommand* getNextCommand() const;
	///Pointer to an array of pointers to channels conveying the desired signals
	TMLEventChannel** _channel;
	///Pointer to an array of parameter function pointers
	ParamFuncPointer* _paramFuncs;
	/////Number of channels in the array
	//unsigned int _numbChannels;
	///Index of the next command within the _nextCommand array
	unsigned int _indexNextCommand;
	///Highest index of the channels on which the TMLChannel::testRead() method has been performed.
	unsigned int _maxChannelIndex;
};

#endif

