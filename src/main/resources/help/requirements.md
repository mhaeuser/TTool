# Requirements
Requirements can be captured in SysML Requirement Diagrams.


## Requirement node

A requirement node ![syntax checking icon](file:../ui/util/avatarrdreq.gif) defines a requirement with an textual identifier, a number unique id) and a description text, a risk, a type, a list of references and finally custom attributes defined as follows:

ID:Value

For instance: version:1.2


## Relations between requirements 


### Composition
A composition ![syntax checking icon](file:../ui/util/reqcomp.gif) links a high-level requirements "r" to requirements that are supposed to compose "r".


### Refine
A refine relation ![syntax checking icon](file:../ui/util/reqref.gif) expresses a requirement which gives more details (e.g., more concrete values, or more concrete model elements) to a requirement.


### DeriveReqt
A DeriveReqt relation ![syntax checking icon](file:../ui/util/reqder.gif) builds a requirement from other requirements (i.e. it is derived from other requirements). This relation is particularly used when a requirement expresses required technical aspects from non-technical requirements.


## Security requirements

Security requirements in TTool can be made explicit, with e.g. "confidentiality", "integrity", etc. The following definitions make these terms more explicit. There are other security requirements (e.g. eligibility, uniqueness, verifiability, etc.): TTool only support the most comon ones.


### Privacy

Privacy is guaranteed if the **relation** between an **entity** and
a **set of information** is confidential. 


An example:

- *In a social network, for non administrator users, the user of
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

### Integrity

Integrity is satisfied when a **quantum of information** has not been modified between **two observations**.

Integrity is also called "weak authenticity".

Examples:

- *The system shall ensure the integrity of  messages sent from A to B*

- *The  integrity of the instructions executed on the system processor shall be ensured*

### (Data origin) Authenticity

Data origin authenticity is satisfied when the  **data** (quantum of information) truly originates from the **author**

(authenticity  is also called "strong authenticity")

Example:

- *All information received from sensors by the main controller shall be authentic in terms of origin.*


### Non-Repudiation

The non-repudiation of an **action** is guaranteed if it is impossible for the **entity** that performed the action to claim that it did not perform this action

Example:

- *The payment system shall guarantee that neither the payer nor the billing system can deny a transaction once it has been performed*

### Controlled Access (a.k.a. Authorization)

Controlled access is guaranteed if  specified **entities** are the only entities that can perform the **actions** or **access the information**.

Examples:

- *Only explicitly authorized users shall be able to execute processes on the computer*

- *Controlled access to read data from a hard disk must be ensured*

### Freshness

Freshness is satisfied if a **quantum of information** received by an **entity** at the **given time** is not a copy of the same information received by the same or another entity in the past.

Freshness usually relates to replay attacks

Examples:

- *Freshness of all  messages sent from A to B must be ensured*

- *Execution of instruction in processor P must apply only to fresh instructions*

### Availability

Availability is satisfied when a **service** or a **physical device** is operational.

Availability is usually related to Denial of Service Attacks - DoS.

Examples:

- *The webserver must always respond in less than 1 second to requests*

- *The availability of the flight management system must be ensured*



