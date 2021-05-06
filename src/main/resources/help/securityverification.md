# Security verification

## Main principle

Security verification can be performed either from [DIPLODOCUS mapping diagrams](file://mapping.md) or from [AVATAR design diagrams](file://avatarsoftwaredesign.md).

The security verification consists in transforming a diagram and its security properties into a ProVerif specification, and then colling ProVerif to check for the satisfaction of security properties. while ProVerif runs, TTool indicates which properties could be proved and backtraces the results to diagrams, using locks next to perperties or elements involved in these properties. A green lock indicates a satisfied property, a greay lock indicates that the property could not be proved, and a red lock indicates that the property could be proved as violated.



## ProVerif installation and configuration

We advice to install the latest version of ProVerif on your computer. We usually install ProVerif using *opam*:

    $ opam install proverif


Once proverif has been installed, TTool must be configured. TTool relies on a .xml configuration file (by default: config.xml). Open this configuration file and configure:

The directory in which TTool generated ProVerif specifications. For instance:

    <ProVerifCodeDirectory data="/home/foo/TTool/proverif/" />


The path to the ProVerif executable. For instance:

    <ProVerifVerifierPath data="/home/foo/bin/proverif" />



## Security properties





## Investigating verification results


## Advanced concepts







