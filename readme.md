# Making The Code Usable
To make the code usable, one has to resolve all the "todo" tasks of the project. The tasks are marked by "//todo" comments inside the code.

List of files with tasks:  
  
    Client:  
        MainController  
  
    Model:  
        Configuration  
        Game  
        Round  
        Validator  

To find the tasks easier, if you're using IntelliJ, you can:  
- CTRL+SHIFT+F and write "//todo" to see all the "//todo" tasks in all the files.
OR
- press SHIFT+SHIFT and then write the name of the files mentioned earlier.
- inside each file, press CTRL+F and write "//todo".

# Using The Project
The project has 3 start classes:
- StartServer starts the main server which communicates through sockets with its clients.
- StartClient starts a client which communicates through sockets with the server. One should first start the server before starting the client.
- StartRestServices starts the REST services. There is no client made for them. Because of this, one has to use Postman to communicate with it.
  
# REST API
In MyAppRestServices, in resources, there is a Postman collection which can be imported. It contains all the calls to the REST methods.
For "Create Configuration", the body of the request has to be modified to match the "Configuration" entity of the code.
