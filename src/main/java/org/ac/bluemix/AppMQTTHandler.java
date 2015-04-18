package org.ac.bluemix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 * @author ac010168
 *
 */
public class AppMQTTHandler extends MQTTHandler {

  //Pattern to check whether the events comes from a device for an event
  Pattern pattern = Pattern.compile("iot-2/type/" + MQTTUtil.DEFAULT_DEVICE_TYPE + "/id/(.+)/evt/" +
      MQTTUtil.DEFAULT_EVENT_ID + "/fmt/json");

  /**
   * Once a subscribed message is received
   */
  @Override
  public void messageArrived(String topic, MqttMessage mqttMessage)
      throws Exception {
    super.messageArrived(topic, mqttMessage);

    Matcher matcher = pattern.matcher(topic);
    if (matcher.matches()) {
      String deviceid = matcher.group(1);
      String payload = new String(mqttMessage.getPayload());
      
      //Parse the payload in Json Format
      JSONObject jsonObject = new JSONObject(payload);
      JSONObject contObj = jsonObject.getJSONObject("d");
      int count = contObj.getInt("count");
      System.out.println("Receive count " + count + " from device " + deviceid);

      //If count >=4, start a new thread to reset the count
      if (count >= 4) {
        new ResetCountThread(deviceid, 0, this).start();
      }
    }
  }
}
