/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.spi;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;

import static java.lang.String.format;

/**
 * User: mike Date: 3/17/25 Time: 18:27
 */
public class SpiDevice implements Logged, AutoCloseable {
  /* At some point making the device addresses configurable might be a
     good idea.

     Currently,
     * the analog board appears to be using GPIO07 as the CS
       corresponding to address 1.
     * the digital boards appear to be using GPIO17 as the CS
       corresponding to address 3.

     Each digital board takes a 2 bit address allowing 4 boards per CS.
   */
  private final Context pi4j;
  private final SpiDeviceConfig config;
  private Spi spi;

  public SpiDevice(final Context pi4j,
                   final SpiDeviceConfig config) {
    this.pi4j = pi4j;
    this.config = config;
  }

  public Context getContext() {
    return pi4j;
  }

  public int transfer(final byte[] data) {
    dumpBytes("before", data);
    final var res = getSpi().transfer(data, data.length);
    if (res >= 0) {
      dumpBytes("after (" + res + ")", data);
    } else {
      debug("Bad result " + res);
    }
    return res;
  }

  public Spi getSpi() {
    if (spi == null) {
      final var reg = pi4j.registry();
      if (reg.exists(config.getSpiName())) {
        spi = pi4j.registry().get(config.getSpiName());
      } else {
        createSpi();
      }
    }
    return spi;
  }

  public void close() {
    if (spi != null) {
      spi.close();
    }
  }

  protected void createSpi() {
    final var spiConfig = Spi.newConfigBuilder(pi4j)
                          .id(config.getSpiName())
//                          .address(address)
                          .channel(config.getSpiAddress())
                          .build();

    /*
    var config  = Spi.newConfigBuilder(pi4j)
                     .id("my-spi")
                     .name("My SPI")
                     .bus(SpiBus.BUS_1)                    //<----- CONFIGURE SPI BUS
     .chipSelect(SpiChipSelect.CS_0)       //<----- CONFIGURE SPI CS/ADDRESS/CHANNEL
            .mode(SpiMode.MODE_3)
            .build();
     */
    final var p = pi4j.spi();
    debug("Provider class {}", p.getClass().getName());

    spi = p.create(spiConfig);
  }

  protected void dumpBytes(final String s,final byte[] data) {
    final var sb = new StringBuilder(s).append(":");
    for (final byte b: data) {
      sb.append(format("%02x ", b));
    }
    debug(sb.toString());
  }

  /* ==============================================================
   *                   Logged methods
   * ============================================================== */

  private final BwLogger logger = new BwLogger();

  @Override
  public BwLogger getLogger() {
    if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
      logger.setLoggedClass(getClass());
    }

    return logger;
  }
}
