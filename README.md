# javaBay
Ebay clone using java spaces (University Assignment)

# Instructions for running
1. add VM argument: 

-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false  --module-path /local/javafx-sdk-11.0.2/lib --add-modules=javafx.controls,javafx.fxml


2. Add the following libraries:
  * jsk-lib.jar
  * outrigger.jar
  * reggie.jar
  * reggie-dl.jar
  * Java FX
  
  Note: If you are running the application it would be best to test the application and then run the test as the tests can modify the objects in the space and cause an error, if this error does occour you need to remove all the objects from the space and start fresh. this can be done by restarting the spacer or uncoment the line :
  
  stopAllSpaces(space);
  
  in Main.class
