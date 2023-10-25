# VanRoomies
A match-making housing app

## BACKEND 
### Development
#### Set up development key and cert
**Linux**
Change directory into backend.
```bash
$ cd backend/
```
Generate a certificate and key.
```bash
$ openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365
```
Use the passphrase `vanroomies`.
