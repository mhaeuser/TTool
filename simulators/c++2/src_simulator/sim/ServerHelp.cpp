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
#include<ServerHelp.h>
#include<unistd.h>

ServerHelp::ServerHelp() {
    parseHtml(STRING_HELP);
    //generate help server content
    helpServerContent = "\n************ Available Commands ************\n";
    std::map<std::string, CommandInfo>::iterator it;
    for (it = allCmds.begin(); it != allCmds.end(); it++) {
        helpServerContent += it->first + "/" + it->second.alias + "\n";
        aliasMapWithName[it->second.alias] = it->first;
    }

    helpServerContent += "********************************************\n";
}

void ServerHelp::printHelpSever(){
    std::cout << helpServerContent << std:: endl;
}

void ServerHelp::printHelpCommand(std::string cmd) {
    std::string result = "";
    std::map<std::string, CommandInfo>::iterator it;

    if (!cmd.empty()) {
        std::map<std::string, std::string>::iterator ite;
        ite = aliasMapWithName.find(cmd);
        // in case user using alias for help instead of real command name, we need to find the real name by alias
        if (ite != aliasMapWithName.end()) {
            cmd = ite->second;
        }

        it = allCmds.find(cmd);
        if (it != allCmds.end()) {
            result += "\nHelp on command: " + cmd + "\n";
            result += "-------------------------------------------------------------\n";
            result += cmd + " " + it->second.params + "\n";
            result += it->second.description + "\n";
            result += "alias: " + it->second.alias + "\n";
            result += "code: " + it->second.code + "\n";
            result += "-------------------------------------------------------------\n";
        } else {
            result += "Wrong command name!\n";
        }
    }

    std::cout << result << std::endl;
}

void ServerHelp::parseHtml(std::string filePath) {
    std::string line;
    std::string content = "";
    bool check = false;
    std::string searchStart1 = "<tr class=\"odd\">";
    std::string searchStart2 = "<tr class=\"even\">";
    std::string searchEnd = "</tr>";
    std::string delimiter = "</td>";
//    std::string breakLine = "<br />";

    std::istringstream data(filePath.c_str());

    while(std::getline(data, line)) {
        if (line.find(searchStart1, 0) != std::string::npos || line.find(searchStart2, 0) != std::string::npos) {
            check = true;
            continue;
//            replace(line,searchStart,"");
        }

        if (check) {
            if (line.find(searchEnd, 0) != std::string::npos) {
//                replace(line,searchEnd,"");
//                content += line;
                std::vector<std::string> tempData = splitData(content, delimiter);
                std::string tempParam = "";
                /*  the tempData have at least 5 values
                    tempData.at(0) = cmd Name;
                    tempData.at(1) = cmd alias
                    tempData.at(2) = cmd code
                    tempData.at(3) = cmd description
                    tempData.at(4) = cmd nb of params
                    if tempData.at(4) > 0 then these values which have index > 4 will be the param types and param description
                */
                int nbOfParams = 5;
                for (unsigned int i = 4; i < tempData.size(); i++) {
//                    std::cout << tempData.at(i);
                    if (tempData.at(i) == "-") {
                        nbOfParams--;
                    }
                }
//                std::cout << std::endl;

                for (int i = 0; i < nbOfParams; i++) {
                    if (tempData.at(4 + i).find("[Type: 0]", 0) != std::string::npos) {
                        //do nothing
                    } else if (tempData.at(4 + i).find("[Type: 1]", 0) != std::string::npos) {
                        std::size_t temp_pos = tempData.at(4 + i).find("[Type: 1]", 0);
                        std::string temp_param =  trim(tempData.at(4 + i).substr(temp_pos + 9));
                        tempParam += " <int: " + temp_param + ">";
                    } else if (tempData.at(4 + i).find("[Type: 2]", 0) != std::string::npos) {
                        std::size_t temp_pos = tempData.at(4 + i).find("[Type: 2]", 0);
                        std::string temp_param =  trim(tempData.at(4 + i).substr(temp_pos + 9));
                        tempParam += " <string: " + temp_param + ">";
                    } else if (tempData.at(4 + i).find("[Type: 3]", 0) != std::string::npos) {
                        std::size_t temp_pos = tempData.at(4 + i).find("[Type: 3]", 0);
                        std::string temp_param =  trim(tempData.at(4 + i).substr(temp_pos + 9));
                        tempParam += " [int: " + temp_param + "]";
                    } else if (tempData.at(4 + i).find("[Type: 4]", 0) != std::string::npos) {
                        std::size_t temp_pos = tempData.at(4 + i).find("[Type: 4]", 0);
                        std::string temp_param =  trim(tempData.at(4 + i).substr(temp_pos + 9));
                        tempParam += " [string: " + temp_param + "]";
                    } else if (tempData.at(4 + i).find("[Type: 6]", 0) != std::string::npos) {
                        std::size_t temp_pos = tempData.at(4 + i).find("[Type: 6]", 0);
                        std::string temp_param =  trim(tempData.at(4 + i).substr(temp_pos + 9));
                        tempParam += " [int between 0 and 100 (percentage): " + temp_param + "]";
                    } else {
                        tempParam += " <unknow param>";
                    }
                }

                CommandInfo tempCmdInfo(tempData.at(0), tempData.at(1), tempData.at(2), tempParam, tempData.at(3));
                allCmds.insert(std::pair<std::string, CommandInfo>(tempCmdInfo.fullCmd, tempCmdInfo));

                content = "";
                check = false;
            } else {
//                replace(line,breakLine,"");
                content += line + "\n";
            }
        }

    }
}

bool ServerHelp::replace(std::string& str, const std::string& from, const std::string& to) {
    size_t start_pos = str.find(from);
    if(start_pos == std::string::npos)
        return false;
    str.replace(start_pos, from.length(), to);
    return true;
}

std::vector<std::string> ServerHelp::splitData (std::string s, std::string delimiter) {
    std::size_t pos_start = 0, pos_end, pos, delim_len = delimiter.length();
    std::string token;
    std::vector<std::string> res;

    while ((pos_end = s.find (delimiter, pos_start)) != std::string::npos) {
        token = s.substr (pos_start, pos_end - pos_start);
        pos_start = pos_end + delim_len;
        pos = token.find("\">");
        if (pos != std::string::npos)
            token = trim(token.substr(pos + 2));
        res.push_back(token);
    }

    token = s.substr(pos_start);
    pos = token.find("\">");
    if (pos != std::string::npos)
        token = trim(token.substr(pos + 2));
    res.push_back(token);
    return res;
}