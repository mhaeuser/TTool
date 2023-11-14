# AVATAR Security pragmas

AVATAR security pragmas are meant to enhance AVATAR models with two aspects:
- Global security behavior
- Security properties

## Global security behavior

These security pragmas are meant to describe cryptographic material that has been created and exchanged before the corresponding AVATAR model executes.

### Creating and sharing symmetric keys


When an AVATAR model is executed for proving security properties, the prover assumes that this model is in fact executed an infinite number of times. The pragma to create and share keys is meant to express whether these keys are created and shared once for all sessions, or for each session.


```
#InitialSystemKnowledge Block1.key1 Block2.key2
```
where Block1 and Block2 are blocks of the Avatar model, and key1 is an attribute of Block1 of type Key, and key2 is an attribute of Block2 of type Key. This pragma means that key1 and key2 are created once and are equal in all sessions, i.e., block1.key1 is always equal to Block2.key2, and their value is the same in all sessions.

```
#InitialSessionKnowledge Block1.key1 Block2.key2
```

where Block1 and Block2 are blocks of the Avatar model, and key1 is an attribute of Block1 of type Key, and key2 is an attribute of Block2 of type Key. This pragma means that key1 and key2 are different in each session, i.e., this pragma assumes that each time a session starts, a couple (key1, key2) is created and shared between Block1 and Block2.

### Public and Private keys

Couples of private and public keys can be defined as follows:

```
#PrivatePublicKeys ABlock privKeyAttribute pubKeyAttribute
```

This pragma means that Block named "ABlock" has two attributes of type Key: privKeyAttribute and pubKeyAttribute. This pragma also states that privKeyAttribute and pubKeyAttribute are a couple of associated private and public keys.


### Declaring the attacker access to attributes

A public attribute  is an attribute of a block that can be accessed by an attacker.

```
#Public  Block1.attribute1
```

Similarly, an attribute can be declared as public and constant. Contant means that its value does not change, that is the attacker knows its value when it accesses to its value one time.

```
#PublicConstant  Block1.attribute1
```

Oppositely, an attribute can be set as a private constant, to enforce the fact that the value will not be disclosed to the attacker. 

```
#PrivateConstant  Block1.attribute1
```

Note: attributes are assumed as private (but not constant) by default.


## Security properties

### Confidentiality

The following pragma specifies that we expect the attribute attribute1 of block Block1 to remain confidential with regards to the attacker.

```
#Confidentiality  Block1.attribute1
```

Using the keyword "Secret" is equivalent to "Confidentiality":

```
#Secret  Block1.attribute1
```

Also, an attribute can be assumed to be confidential. This is an **information** given to the prover to simplify the proof of other security properties.

```
#SecrecyAssumption  Block1.attribute1
```

### Integrity and Authenticity

In TTool, integrity is also called "Weak authenticity", and authenticity is called "String authenticity". Weak authenticity refers to the fact that a receiver receiving a message can detect that the message was modified by an attacker. Strong authenticity assumes weak authenticity and adds the fact that to each message received by the receiver corresponds exactly to one message sent by the sender.

The following pragma states that the attribute "secretMessage" in state "receiveMessage" of the state machine of block Server is authentic with regards to the attribute "secretMessage" in state "sendMessage" of the state machine of block Client.

```
#Authenticity Client.sendMessage.secretMessage Server.receiveMessage.secretMessage
```

### Property backtracing

Once Proverif has been invoked from TTool, proof results are backtraced to TTool

#### Confidentiality
A lock is drawn next to each attribute given in a "Confidentiality" pragma.
- Green means "confidentiality satisfied"
- Red means "confidentiality not satisfied"
- Grey means that the property could not be proved

#### Weak and strong authenticity
A lock is drawn next to each "Authenticity" pragma. the lock is divided into two parts. The upper right part refers to weak authenticity, and lower left part refers to the strong authenticity.

- Green means property satisfied
- Red means property not satisfied
- Grey means that the property could not be proved

For instance, the Figure below, taken from the "AliceAndBob" model illustrates an authenticity property after security proof: the lock shows that  weak authenticity is satisfied, but not strong authenticity.

<center>
![](file:../help/lockauthenticity_avatar.png)
</center>

