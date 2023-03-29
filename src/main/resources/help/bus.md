# Bus

Bus nodes connect other nodes together.

## Main attributes

They have the following attributes.

- A name
- An arbitration policy capturing how simultaneous trafsre request are handled
- A data size in byte.
- A pipeline size expressing the number of cycles spent by one transfer in this bus
- A slice time, i.e. the maximum number of cycles a transfer can use before being preempted./ not taken into account, so ponly for documentation. Rather use the 'burst size"
- A burst size: this represents the maximum size of a transfer before it is preempted if another request is pending.
- A clock divider expresses the relation between the clock of the memory and the main clock of the architecture
- A bus privacy. A public bus can be spied at, i.e., an attacker can read messages, modify messages, and inject messages. In a private bus, an attacker can retrieve messages and reinject only the same messages. An attacker cannot read the content of messages in private channel, nor he/she can modify the content of these messages. Thus, confidentiality and integrity is guaranteed, but not uathenticity. To garantee authenticity, one had to use, for instance, nonces, to avoid message being replayed.
- A reference attack specifies which attack of attakc trees this bus relates to.


