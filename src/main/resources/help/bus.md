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
- A bus privacy. A public bus can be spied at, while a private bus cannot. On a public bus, an attacker can reive messages and inject messages.
- A reference attack specifies which attack of attakc trees this bus relates to.


