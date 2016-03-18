package ca.jonathanfritz.budgey;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {
	private final DateTime lastUpdatedUtc;
	private final Set<Account> accounts;

	/**
	 * Initializes an empty profile with no accounts
	 */
	public Profile() {
		lastUpdatedUtc = DateTime.now(DateTimeZone.UTC);
		accounts = new HashSet<>();
	}

	/**
	 * Initializes an existing profile.
	 * @param lastUpdatedUtc the time at which this profile was last changed
	 * @param accounts the accounts in this profile
	 */
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

	public void addAccount(Account account) {
		accounts.add(account);
	}
}
