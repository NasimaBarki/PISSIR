# SOFTWARE

- Intellij
- MQTTX
- DB BROWSER (SQLite)

# SETUP

- Scaricare le due cartelle "Client" e "Rest".
- Aprire le due cartelle in due istanze di intellij differenti.
- Il client utilizza come SDK "corretto-1.8 Amazon Corretto version 1.8.0_402"; la REST API utilizza invece "temurin-17 Eclipse Temurin version 17.0.7"
- Nel client far partire la classe "Client.java"
- Nella REST API far partire la classe "Service.java"
- MQTT si trova all'interno della REST API. In particolare, nella directory it.uniupo.pissir>mqtt>client. Per fare partire anche MQTT, eseguire la classe "MQTTClient.java"

# CLIENT

L'interfaccia è visualizzabile dall'indirizzo "localhost:5000". Questa interfaccia è visualizzabile da tutti gli utenti.

Il monitor all'ingresso è visualizzabile dall'indirizzo "localhost:5000/monitor".

Quando un utente si ritrova all'uscita del parcheggio scannerizza un QR Code che lo porta all'indirizzo "localhost:5000/exitPayment". Tuttavia, prima di scannerizzare, l'utente deve autenticarsi.

Il sistema accetta soltanto MasterCard, per semplificare la fase di testing è possibile utilizzare questo numero "5123 4567 8901 2345".

# UTENTI

### BASE

username: MarioRossi
password: Password1

### PREMIUM

username: LuigiVerdi
password: Password2

### ADMIN

username: AnnaBianchi
password: Password3
