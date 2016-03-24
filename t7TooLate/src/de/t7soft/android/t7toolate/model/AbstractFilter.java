package de.t7soft.android.t7toolate.model;

public abstract class AbstractFilter {

	private boolean active;
	private final String name;

	public AbstractFilter(final String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

}
