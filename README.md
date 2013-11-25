# 1stPass-Server
=======

Java SSL/TLS Secure Server

### Features
* SSL Server with a generated certificate and Keystore
* Encryption class to crypt and decrypt files stored “PBKDF2WithHmacSHA1”
* Sever store password hashes and salt
* Client files are encrypted with the hash of the password, only the client can open them
* Multi user application, a Thread Monitor watches the active user (secure control of accesses)

### TO-DO
* Implement a "Lost Password" feature
* Store user files on Database