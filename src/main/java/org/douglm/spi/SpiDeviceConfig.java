/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.spi;

import org.bedework.base.ToString;

/**
 * User: mike Date: 4/2/25 Time: 23:13
 */
public class SpiDeviceConfig {
  private int spiAddress;
  private String spiName;

  public int getSpiAddress() {
    return spiAddress;
  }

  public void setSpiAddress(final int val) {
    spiAddress = val;
  }

  public String getSpiName() {
    return spiName;
  }

  public void setSpiName(final String val) {
    spiName = val;
  }

  public ToString toStringSegment(final ToString ts) {
    return ts.append("spiAddress", spiAddress)
             .append("spiName", spiName);
  }

  public String toString() {
    return toStringSegment(new ToString(this)).toString();
  }
}
