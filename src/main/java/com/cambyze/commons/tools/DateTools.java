package com.cambyze.commons.tools;


import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date tools
 * 
 * @author Thierry Nestelhut
 * @see <a href="https://github.com/cambyze">cambyze GitHub</a>
 */
public class DateTools {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateTools.class);

  /**
   * 
   * @param date date
   * @return
   */
  public static Date dateWithTimeAtStartOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    LOGGER.debug("new date = " + cal.getTime());
    return cal.getTime();
  }

}
