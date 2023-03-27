# Cryptographic configuration
A sending or receiving operator can specify a cryptographic configuration.
If no cryptographic configuration is used, it means that data sent over the channel are unmodified.
If a cryptographic configuration is used, then data sent over the channel are modified according to what is specified in the cryptographic configuration (e.g., data are encrypted, etc.).
In case an HSM is used, then data can be sent to the HSM encrypted or unencrypted.