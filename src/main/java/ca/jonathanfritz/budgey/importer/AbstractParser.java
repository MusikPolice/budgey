package ca.jonathanfritz.budgey.importer;

import org.joda.time.DateTime;

public abstract class AbstractParser implements Parser {

	private DateTime lastDate = null;

	private int lastOrder = 0;

	protected int getOrderForDate(final DateTime date) {

		this.lastOrder = date.isEqual(this.lastDate) ? this.lastOrder + 1 : 0;
		this.lastDate = date;
		return this.lastOrder;

	}

}
