# Requirements
Requirements can be captured in SysML Requirement Diagrams.


## Requirement node


## Relations between requirements


### Composition
A composition ![syntax checking icon](file:../ui/util/reqcomp.gif) links a high-level requirements "r" to requirements that are supposed to compose "r".


### Refine
A refine relation ![syntax checking icon](file:../ui/util/reqref.gif) expresses a requirement which gives more details (e.g., more concrete values, or more concrete model elements) to a requirement.


### DeriveReqt
A DereiveReqt relation ![syntax checking icon](file:../ui/util/reqder.gif) builds a requirement from other requirements (i.e. it is derived from other requirements). This relation is particularly used when a requirement expresses required techincal aspects from non technical requirements.


## Security requirements

Security requirements in TTool can be made explicit, with e.g. "confidentiality", "integrity", etc. The following definitions make these terms more explicit.


### Privacy

Privacy is guaranteed if the **relation** between an **entity** and
a **set of information** is confidential. 


An example: *In a social network, for non administrator users, the user of
a message shall not be linkable to that message but two
messages sent by the same user shall be linkable to each
other*.


### Confidentiality

Confidentiality is satisfied when authorized **entities** are the
only ones that can know a given **quantum of information**.

Examples:
- *The content of Messages sent from A to B shall be known
only by A and B*
- *The state of a state machine shall be known only by its
execution engine*

###



