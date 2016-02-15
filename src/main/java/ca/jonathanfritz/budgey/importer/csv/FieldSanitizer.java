package ca.jonathanfritz.budgey.importer.csv;

public class FieldSanitizer {

	public static String[] sanitizeFields(String[] fields) {
		final String[] sanitized = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			sanitized[i] = fields[i].replace("\"", "").trim();
		}
		return sanitized;
	}
}
