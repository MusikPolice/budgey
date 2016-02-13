package ca.jonathanfritz.budgey.importer;

import org.joda.time.DateTime;

public abstract class AbstractCSVParser {

	private DateTime lastDate = null;

	private int lastOrder = 0;

	protected int getOrderForDate(final DateTime date) {

		if(!date.isEqual(this.lastDate)) {
			this.lastOrder = 0;
		} else {
			this.lastOrder = this.lastOrder + 1;
		}

		this.lastDate = date;

		return this.lastOrder;

	}

}
