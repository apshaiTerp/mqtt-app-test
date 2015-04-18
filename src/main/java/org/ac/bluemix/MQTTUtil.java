package org.ac.bluemix;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author ac010168
 *
 */
public class MQTTUtil {
  //Default Mqtt Server Suffix
  public final static String SERVER_SUFFIX = ".messaging.internetofthings.ibmcloud.com";
  
  public final static String DEFAULT_EVENT_ID = "eid";
  public final static String DEFAULT_CMD_ID = "cid";
  public final static String DEFAULT_DEVICE_TYPE = "MQTTDevice";

  /**
   * This method reads the properties from the config file
   * @param filePath
   * @return
   */
  public static Properties readProperties(String filePath) {
    Properties props = new Properties();
    try {
      InputStream in = MQTTUtil.class.getResourceAsStream(filePath);
      props.load(in);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return props;
  }
}
