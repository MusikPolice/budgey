package ca.jonathanfritz.budgey;

import java.util.Set;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {
	private final DateTime lastUpdatedUtc;
	private final Set<Account> accounts;

	@JsonCreator
	public Profile(@JsonProperty("lastUpdatedUtc") DateTime lastUpdatedUtc, @JsonProperty("accounts") Set<Account> accounts) {
		this.lastUpdatedUtc = lastUpdatedUtc;
		this.accounts = accounts;
	}

	@JsonGetter("lastUpdatedUtc")
	public DateTime getLastUpdatedUtc() {
		return lastUpdatedUtc;
	}

	@JsonGetter("accounts")
	public Set<Account> getAccounts() {
		return accounts;
	}
}
