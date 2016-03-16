package generator.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	/**
	 * 获取当前系统时间
	 * @return
	 */
    public static String getStringToday() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(calender.getTime());
    }
}
