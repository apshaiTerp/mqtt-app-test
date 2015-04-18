package org.ac.bluemix;

import java.util.Properties;

/**
 * @author ac010168
 *
 */
public class SimpleApplication {

  public static int count = 0;
  public static int totalcount = 0;
  private MQTTHandler handler = null;

  /**
   * @param args
   */
  public static void main(String[] args) {
    new SimpleApplication().doSomeStuff();
  }
  
  public void doSomeStuff() {
    //Read properties from the conf file
    Properties props = MQTTUtil.readProperties("app.conf");
    
    String orgID      = props.getProperty("org");
    String appID      = props.getProperty("appid");
    String authMethod = props.getProperty("key");
    String authToken  = props.getProperty("token");
    //isSSL property
    String sslStr     = props.getProperty("isSSL");
    boolean isSSL = false;
    if (sslStr.equals("T")) {
      isSSL = true;
    }

    System.out.println("org:   " + orgID);
    System.out.println("appID: " + appID);
    System.out.println("key:   " + authMethod);
    System.out.println("token: " + authToken);
    System.out.println("isSSL: " + isSSL);
    
    String serverHost = orgID + MQTTUtil.SERVER_SUFFIX;
    String clientID   = "a:" + orgID + ":" + appID;
    
    handler           = new AppMQTTHandler();
    
    System.out.println ("serverHost: " + serverHost);
    System.out.println ("clientID:   " + clientID);
    
    handler.connect(serverHost, clientID, authMethod, authToken, isSSL);
    
    //Subscribe the Command events
    //iot-2/cmd/<cmd-type>/fmt/<format-id>
    handler.subscribe("iot-2/type/" + MQTTUtil.DEFAULT_DEVICE_TYPE + "/id/+/mon", 0);
    
    //Subscribe Device Events
    //iot-2/type/<type-id>/id/<device-id>/evt/<event-id>/fmt/<format-id>
    handler.subscribe("iot-2/type/" + MQTTUtil.DEFAULT_DEVICE_TYPE + "/id/+/evt/" + MQTTUtil.DEFAULT_EVENT_ID + "/fmt/json", 0);
  }
}
