package de.t7soft.android.t7toolate.model;

import java.util.Date;
import java.util.UUID;

public class Connection {

	private final String id;
	private String name;
	private String startStation;
	private Date startTime;
	private String endStation;
	private Date endTime;
	private boolean weekdays;
	private boolean saturday;
	private boolean sunday;

	public Connection() {
		this(UUID.randomUUID().toString());
	}

	public Connection(String id) {
		this.id = id;
		this.name = "";
		this.startTime = new Date();
		this.startStation = "";
		this.endTime = new Date();
		this.endStation = "";
		this.weekdays = true;
		this.saturday = true;
		this.sunday = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartStation() {
		return startStation;
	}

	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getEndStation() {
		return endStation;
	}

	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isWeekdays() {
		return weekdays;
	}

	public void setWeekdays(boolean weekdays) {
		this.weekdays = weekdays;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	public String getId() {
		return id;
	}

}
