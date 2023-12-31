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

#include <AvAsyncSignal.h>
#include <AvTransition.h>
#include <AvCmd.h>

AvAsyncSignal::AvAsyncSignal(ID iID, std::string iName, unsigned int iSize): AvSignal(iID, iName), _size(iSize){
}

AvAsyncSignal::~AvAsyncSignal(){
}

void AvAsyncSignal::registerWriter(AvTransition* iWriter){
}

void AvAsyncSignal::registerReader(AvTransition* iReader){
}

void AvAsyncSignal::cancelWriter(AvTransition* iWriter){
}

void AvAsyncSignal::cancelReader(AvTransition* iReader){
}

Parameter* AvAsyncSignal::read(AvTransition* iReader){
	Parameter* iParamData=_paramQueue.front();
	_paramQueue.pop();
	return iParamData;
}

void AvAsyncSignal::write(AvTransition* iWriter, AvTransition* iReader, Parameter* iParamData){
	if (_paramQueue.size()<_size) _paramQueue.push(iParamData);
}

bool AvAsyncSignal::getSyncTransitionsWriter(AvBlock* iBlock, AvTransition* iWriter, EnabledTransList& iTransList){
	std::ostringstream aTransText;
	aTransText << iWriter->getOutgoingCmd()->toString() << " writes to channel " << toString();
	iTransList.push_back(SystemTransition(iBlock, iWriter, 0, aTransText.str()));
	return true;
}

bool AvAsyncSignal::getSyncTransitionsReader(AvBlock* iBlock, AvTransition* iReader, EnabledTransList& iTransList){
	if (!_paramQueue.empty()){
		std::ostringstream aTransText;
		aTransText << iReader->getOutgoingCmd()->toString() << " reads from channel " << toString();
		iTransList.push_back(SystemTransition(iBlock, iReader, 0, aTransText.str()));
		return true;
	}
	return false;
}
