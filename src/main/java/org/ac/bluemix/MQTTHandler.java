package org.ac.bluemix;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/**
 * @author ac010168
 *
 */
public class MQTTHandler implements MqttCallback {

  private final static String DEFAULT_TCP_PORT = "1883";
  private final static String DEFAULT_SSL_PORT = "8883";

  private MqttClient client = null;
  
  /**
   * Default Constructor
   */
  public MQTTHandler() {
  }

  /* (non-Javadoc)
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
   */
  public void connectionLost(Throwable throwable) {
    if (throwable != null)
      throwable.printStackTrace();
  }

  /* (non-Javadoc)
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
   */
  public void deliveryComplete(IMqttDeliveryToken arg0) {
    System.out.println ("deliveryComplete()!");

  }

  /* (non-Javadoc)
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
   */
  public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
    String payload = new String(mqttMessage.getPayload());
    System.out.println ("messageArrived() - Message received on topic " + topic + ": message is " + payload);
  }

  public void connect(String serverHost, String clientId, String authmethod, String authtoken, boolean isSSL) {
    // check if client is already connected
    if (!isMqttConnected()) {
      String connectionUri = null;
      
      //tcp://<org-id>.messaging.internetofthings.ibmcloud.com:1883
      //ssl://<org-id>.messaging.internetofthings.ibmcloud.com:8883
      if (isSSL) {
        connectionUri = "ssl://" + serverHost + ":" + DEFAULT_SSL_PORT;
      } else {
        connectionUri = "tcp://" + serverHost + ":" + DEFAULT_TCP_PORT;
      }

      if (client != null) {
        try {
          client.disconnect();
        } catch (MqttException e) {
          e.printStackTrace();
        }
        client = null;
      }

      try {
        //DEBUG
        System.out.println ("  ConnectionUri: " + connectionUri);
        System.out.println ("  ClientID:      " + clientId);
        
        
        client = new MqttClient(connectionUri, clientId);
      } catch (MqttException e) {
        e.printStackTrace();
      }

      client.setCallback(this);

      // create MqttConnectOptions and set the clean session flag
      MqttConnectOptions options = new MqttConnectOptions();
      options.setCleanSession(true);

      options.setUserName(authmethod);
      options.setPassword(authtoken.toCharArray());

      //If SSL is used, do not forget to use TLSv1.2
      if (isSSL) {
        java.util.Properties sslClientProps = new java.util.Properties();
        sslClientProps.setProperty("com.ibm.ssl.protocol", "TLSv1.2");
        options.setSSLProperties(sslClientProps);
      }

      try {
        // connect
        client.connect(options);
        System.out.println("Connected to " + connectionUri);
      } catch (MqttException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * Disconnect MqttClient from the MQTT server
   */
  public void disconnect() {
    // check if client is actually connected
    if (isMqttConnected()) {
      try {
        // disconnect
        client.disconnect();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Subscribe MqttClient to a topic
   * 
   * @param topic
   *            to subscribe to
   * @param qos
   *            to subscribe with
   */
  public void subscribe(String topic, int qos) {

    // check if client is connected
    if (isMqttConnected()) {
      try {
        client.subscribe(topic, qos);
        System.out.println("Subscribed: " + topic);

      } catch (MqttException e) {
        e.printStackTrace();
      }
    } else {
      connectionLost(null);
    }
  }

  /**
   * Unsubscribe MqttClient from a topic
   * 
   * @param topic
   *            to unsubscribe from
   */
  public void unsubscribe(String topic) {
    // check if client is connected
    if (isMqttConnected()) {
      try {

        client.unsubscribe(topic);
      } catch (MqttException e) {
        e.printStackTrace();
      }
    } else {
      connectionLost(null);
    }
  }

  /**
   * Publish message to a topic
   * 
   * @param topic
   *            to publish the message to
   * @param message
   *            JSON object representation as a string
   * @param retained
   *            true if retained flag is requred
   * @param qos
   *            quality of service (0, 1, 2)
   */
  public void publish(String topic, String message, boolean retained, int qos) {
    // check if client is connected
    if (isMqttConnected()) {
      // create a new MqttMessage from the message string
      MqttMessage mqttMsg = new MqttMessage(message.getBytes());
      // set retained flag
      mqttMsg.setRetained(retained);
      // set quality of service
      mqttMsg.setQos(qos);
      try {
        client.publish(topic, mqttMsg);
      } catch (MqttPersistenceException e) {
        e.printStackTrace();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    } else {
      connectionLost(null);
    }
  }

  /**
   * Checks if the MQTT client has an active connection
   * 
   * @return True if client is connected, false if not.
   */
  private boolean isMqttConnected() {
    boolean connected = false;
    try {
      if ((client != null) && (client.isConnected())) {
        connected = true;
      }
    } catch (Exception e) {
      // swallowing the exception as it means the client is not connected
    }
    return connected;
  }
}
