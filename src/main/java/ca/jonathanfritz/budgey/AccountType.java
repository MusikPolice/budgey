package ca.jonathanfritz.budgey;

public enum AccountType {
	CHECKING("Chequing"),
	VISA("Visa"),
	ROYAL_CREDIT_LINE("Royal Credit Line");

	private final String friendlyName;

	private AccountType(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public static AccountType fromString(String type) {
		for (final AccountType at : AccountType.values()) {
			if (type.equals(at.toString())) {
				return at;
			}
		}
		throw new IllegalArgumentException("No matching AccountType for string " + type);
	}
}
