/**
 * 
 */
package de.xwic.cube.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.xwic.cube.IDimensionElement;

/**
 * Provides utility functions to work with the time dimension.
 * @author lippisch
 */
public class TimeUtil {

	public static final int Q1 = 0;
	public static final int Q2 = 1;
	public static final int Q3 = 2;
	public static final int Q4 = 3;
	
	private static final String[] MONTH_NAMES = {
		"Jan",
		"Feb",
		"Mar",
		"Apr",
		"May",
		"Jun",
		"Jul",
		"Aug",
		"Sep",
		"Oct",
		"Nov",
		"Dec"
	};
	private static Map<String, Integer> MONTH_INDEX = new HashMap<String, Integer>();
	static {
		for (int i = 0; i < MONTH_NAMES.length; i++) {
			MONTH_INDEX.put(MONTH_NAMES[i].toLowerCase(), i);
		}
	}
	
	private int year = 0;
	private int quarter = -1;
	private int month = -1;

	private int startMonth = 0;
	
	public TimeUtil() {
		
	}

	/**
	 * Constructs a new TimeUtil with the given start month.
	 * @param startMonth
	 */
	public TimeUtil(int startMonth) {
		this.startMonth = startMonth;
	}

	/**
	 * 
	 * @param elmTime
	 */
	public TimeUtil(IDimensionElement elmTime) {
		this(elmTime, 0);
	}
	
	/**
	 * 
	 * @param elmTime
	 * @param startMonth
	 */
	public TimeUtil(IDimensionElement elmTime, int startMonth) {
		
		this.startMonth = startMonth;
		IDimensionElement elm = elmTime;
		switch (elmTime.getDepth()) {
		case 0: // the dimension itself -> ALL
			year = -1;
			quarter = -1;
			month = -1;
			break;
		
		case 3: // month
			Integer mIdx = MONTH_INDEX.get(elm.getKey().toLowerCase());
			if (mIdx == null) {
				throw new IllegalArgumentException("Can not identify month: " + elm.getKey());
			}
			month = mIdx.intValue() - getStartMonth();
			if (month < 0) {
				month += 12;
			}
			elm = elm.getParent();
			// fall through...
		case 2: // quarter
			quarter = Integer.parseInt(elm.getKey().substring(1)) - 1;
			elm = elm.getParent();
			// fall through
		case 1: // the dimension is the year
			year = Integer.parseInt(elm.getKey());
		}
		
	}

	/**
	 * Construct a new TimeUtil from a Date.
	 * @param date
	 */
	public TimeUtil(Date date) {
		this(date, 0);
	}
	/**
	 * Construct a new TimeUtil from a Date.
	 * @param date
	 */
	public TimeUtil(Date date, int startMonth) {
		this.startMonth = startMonth;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH) - startMonth;
		if (month < 0) {
			month += 12;
			year--;
		}
		quarter = month / 3;
	}
	
	/**
	 * @param last
	 */
	public TimeUtil(TimeUtil last) {
		this.year = last.year;
		this.quarter = last.quarter;
		this.month = last.month;
		this.startMonth = last.startMonth;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the quarter
	 */
	public int getQuarter() {
		return quarter;
	}

	/**
	 * @param quarter the quarter to set
	 */
	public void setQuarter(int quarter) {
		this.quarter = quarter;
		if (month != -1) {
			if (month / 3 != quarter) {
				month = quarter * 3;
			}
		}
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
		// update quarter
		if (month != -1) {
			quarter = month / 3;
		}
	}

	/**
	 * Returns true if this time representation contains a year information.
	 * @return
	 */
	public boolean hasYear() {
		return year != -1;
	}
	
	/**
	 * Returns true if this time representation contains a quarter information.
	 * @return
	 */
	public boolean hasQuarter() {
		return year != -1 && quarter != -1;
	}
	
	/**
	 * Returns true if this time representation contains a quarter information.
	 * @return
	 */
	public boolean hasMonth() {
		return year != -1 && quarter != -1 && month != -1;
	}
	
	/**
	 * Returns true if the specified time is before the current time. 
	 * @param time
	 * @return
	 */
	public boolean isBefore(TimeUtil time) {
		if (hasYear() && time.hasYear()) {
			if (year < time.year) {
				return true;
			} else if (year > time.year) {
				return false;
			}
		}
		if (hasQuarter() && time.hasQuarter()) {
			if (quarter < time.quarter) {
				return true;
			} else if (quarter > time.quarter) {
				return false;
			}
		}
		if (hasMonth() && time.hasMonth()) {
			if (month < time.month) {
				return true;
			} else if (month > time.month) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Returns true if the specified time is after this time.
	 * @param time
	 * @return
	 */
	public boolean isAfter(TimeUtil time) {
		if (hasYear() && time.hasYear()) {
			if (year > time.year) {
				return true;
			} else if (year < time.year) {
				return false;
			}
			
		}
		if (hasQuarter() && time.hasQuarter()) {
			if (quarter > time.quarter) {
				return true;
			} else if (quarter < time.quarter) {
				return false; 
			}
		}
		if (hasMonth() && time.hasMonth()) {
			if (month > time.month) {
				return true;
			} else if (month < time.month) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Returns true if this time is within the specified time. Samples:
	 * 2009/Q3/Jan is within 2009
	 * 2009/Q2 is within 2009/Q2 
	 * @param time
	 * @return
	 */
	public boolean isWithin(TimeUtil time) {
		if (!time.hasYear()) {
			return true; 		// this time is always within the "all-time" 
		}
		if (hasYear() && year != time.year) {
			return false;
		}
		if (hasQuarter() && time.hasQuarter() && quarter != time.quarter) {
			return false;
		}
		if (hasMonth() && time.hasMonth() && month != time.month) {
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Build the path representation.
	 * @return
	 */
	public String toPath() {
		StringBuilder sb = new StringBuilder();
		if (year != -1) {
			sb.append(year);
			if (quarter != -1) {
				sb.append("/Q");
				sb.append(quarter + 1);
				if (month != -1) {
					sb.append("/");
					// re-shift month names
					int m = month + getStartMonth();
					if (m > 11) {
						m -= 12;
					}
					sb.append(MONTH_NAMES[m]);
				}
			}
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = toPath();
		if (s.length() == 0) {
			return "all-time";
		} 
		s = s + " (y=" + year + ", q=" + quarter + ", m=" + month + ")";
		return s;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + month;
		result = prime * result + quarter;
		result = prime * result + year;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeUtil other = (TimeUtil) obj;
		if (month != other.month)
			return false;
		if (quarter != other.quarter)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public String getMonthName() {
		int m = month + getStartMonth();
		if (m > 11) {
			m -= 12;
		}
		return MONTH_NAMES[m];
	}

	/**
	 * Add n month to the current time.
	 * @param i
	 */
	public void addMonth(int i) {
		
		year += (i / 12);
		month += i % 12;
		
		if (month >= 12) {
			month = month - 12;
			year++;
		}
		
		
		quarter = month / 3;
		
	}

	/**
	 * @param startMonth the startMonth to set
	 */
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	/**
	 * @return the startMonth
	 */
	public int getStartMonth() {
		return startMonth;
	}
	
}
