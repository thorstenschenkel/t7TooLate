package de.t7soft.android.t7toolate.model;

import java.util.Date;
import java.util.UUID;

public class Connection implements Cloneable {

	private final String id;
	private String name;
	private String startStation;
	private Date startTime;
	private String endStation;
	private Date endTime;
	private boolean weekdays;
	private boolean saturday;
	private boolean sunday;
	private int connectionType;

	public Connection() {
		this(UUID.randomUUID().toString());
	}

	public Connection(final String id) {
		this.id = id;
		this.name = "";
		this.startTime = new Date();
		this.startStation = "";
		this.endTime = new Date();
		this.endStation = "";
		this.weekdays = true;
		this.saturday = true;
		this.sunday = true;
		this.connectionType = ConnectionTypes.UNKOWN;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getStartStation() {
		return startStation;
	}

	public void setStartStation(final String startStation) {
		this.startStation = startStation;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(final Date startTime) {
		this.startTime = startTime;
	}

	public String getEndStation() {
		return endStation;
	}

	public void setEndStation(final String endStation) {
		this.endStation = endStation;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(final Date endTime) {
		this.endTime = endTime;
	}

	public boolean isWeekdays() {
		return weekdays;
	}

	public void setWeekdays(final boolean weekdays) {
		this.weekdays = weekdays;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(final boolean saturday) {
		this.saturday = saturday;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(final boolean sunday) {
		this.sunday = sunday;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return getName() + " (from " + getStartStation() + " to " + getEndStation() + ")";
	}

	public int getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(final int connectionType) {
		this.connectionType = connectionType;
		if ((this.connectionType < 0) && (this.connectionType > ConnectionTypes.PLANE)) {
			this.connectionType = ConnectionTypes.UNKOWN;
		}
	}

	@Override
	public Connection clone() throws CloneNotSupportedException {

		final Connection clone = new Connection(getId());
		clone.setName(getName());
		clone.setStartStation(getStartStation());
		clone.setStartTime(getStartTime());
		clone.setEndStation(getEndStation());
		clone.setEndTime(getEndTime());
		clone.setWeekdays(isWeekdays());
		clone.setSaturday(isSaturday());
		clone.setSunday(isSunday());
		clone.setConnectionType(getConnectionType());
		return clone;

	}

}
