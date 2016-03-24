package de.t7soft.android.t7toolate.model;

import java.util.Date;

public class PeriodFilter extends AbstractFilter {

	private Date from;
	private Date to;

	public PeriodFilter() {
		super("PeriodFilter");
	}

	public Date getTo() {
		return to;
	}

	public void setTo(final Date to) {
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(final Date from) {
		this.from = from;
	}

}
