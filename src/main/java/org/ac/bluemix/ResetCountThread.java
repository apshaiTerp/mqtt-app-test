package org.ac.bluemix;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ac010168
 *
 */
public class ResetCountThread extends Thread {

  private String deviceid = null;
  private int count = 0;
  private AppMQTTHandler handler;

  public ResetCountThread(String deviceid, int count, AppMQTTHandler handler) {
    this.deviceid = deviceid;
    this.count    = count;
    this.handler  = handler;
  }

  public void run() {
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.put("cmd", "reset");
      jsonObj.put("count", count);
      jsonObj.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          .format(new Date()));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    System.out.println("Reset count for device " + deviceid);
    
    //Publish command to one specific device
    //iot-2/type/<type-id>/id/<device-id>/cmd/<cmd-id>/fmt/<format-id>
    handler.publish("iot-2/type/" + MQTTUtil.DEFAULT_DEVICE_TYPE + "/id/" + deviceid + "/cmd/" + 
        MQTTUtil.DEFAULT_CMD_ID + "/fmt/json", jsonObj.toString(), false, 0);
  }
}
