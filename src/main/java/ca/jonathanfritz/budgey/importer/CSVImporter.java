package ca.jonathanfritz.budgey.importer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.jonathanfritz.budgey.Transaction;

public class CSVImporter<T extends CSVParser> {

	private final T parser;

	public CSVImporter(T parser) {
		this.parser = parser;
	}

	public List<Transaction> importFile(String path) throws FileNotFoundException, IOException {
		final String absolutePath = path = path.replaceFirst("^~", System.getProperty("user.home"));

		final List<Transaction> transactions = new ArrayList<>();
		try (FileReader file = new FileReader(absolutePath)) {
			try (BufferedReader reader = new BufferedReader(file)) {
				String line = reader.readLine();
				while (StringUtils.isNotBlank(line)) {
					try {
						final String[] fields = FieldSanitizer.sanitizeFields(line.split(","));
						final Transaction transaction = parser.parse(fields);
						if (transaction != null) {
							transactions.add(transaction);
							continue;
						}
						System.out.println("Dropped line " + line);
					} catch (final Throwable t) {
						System.out.println("Dropped line " + line);
						t.printStackTrace();
					} finally {
						line = reader.readLine();
					}
				}
			}
		}
		return transactions;
	}

}
