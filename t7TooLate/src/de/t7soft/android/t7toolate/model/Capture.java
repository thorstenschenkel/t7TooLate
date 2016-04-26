package de.t7soft.android.t7toolate.model;

import java.util.Date;
import java.util.UUID;

public class Capture {

	private final String id;
	private Connection connection;
	private Date captureDateTime;
	private String comment;
	private boolean canceled;

	public Capture() {
		this(UUID.randomUUID().toString());
	}

	public Capture(final String id) {
		this.id = id;
		this.connection = new Connection();
		this.captureDateTime = new Date();
		this.comment = "";
		this.canceled = false;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(final Connection connection) {
		this.connection = connection;
	}

	public Date getCaptureDateTime() {
		return captureDateTime;
	}

	public void setCaptureDateTime(final Date captureDateTime) {
		this.captureDateTime = captureDateTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	public String getId() {
		return id;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
	}

}
