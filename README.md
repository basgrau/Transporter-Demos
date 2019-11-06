# Transporter-Demos
 Demos different ways to transport files through webservices.

# Use Case 1
 File is embedded in JSON Data. JSON Data ist always serialized and deserialized between the services.

# Use Case 2a
 File is embedded in JSON Data, from client to transport2002. 2002-Service writes to database an transmits the id instead of file data. Transport2005 only passes the id. Transport2008 access DB with id to extract file data. 
 * Requierements: 
    * transport2002 must be able to write to DB
    * transport2008 must be able to read from DB

* trade-offs:
    * transport2005 can't verify if message is "complete"
    * transport2008 can't verify if correct file is referenced


# Use Case 2b (optional|TODO)
Same as above, only that transport2002 and transport2005 share DB_Table_1 and transport2005 and transport 2008 share DB_Table_2. 
Transport2002 writes to DB_Table_1 and passes id, therefore transport2005 could verify data. Afterwards transport2005 writes to shared DB_Table_2 and now transport2008 can read and verify.


# Use Case 3
 File is embedded in JSON Data, from client to transport2002. 2002-Service writes to database an transmits the id instead of file data. Transport2005 receives id, & message-data and dumps both to "buffer". Async it accesses the file-Endpoint from transport2002 to receive file-data. Thereafter filedata is stored to db and messagedata is transfered to transport2008 with new id.  Transport2008 muss dump messagedata and id to "buffer". Async it access fileservice from transport2005, dumps filedata to db. And on it goes ...
 * Requierements: 
    * transport2002 must be able to write to db and read from DB. Must have its own db, because it could be done accross networks / datacenters.
    * transport2005 must be able to write to db and read from DB. Must have its own db, because it could be done accross networks / datacenters.
     *transport2008 must be able to write to db and read from DB. Must have its own db, because it could be done accross networks / datacenters.
* trade-offs:
    * more dbs, more "async" foo.
    * needs a lot more monitoring and checking