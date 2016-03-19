package ca.jonathanfritz.budgey.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CompressionService {
	private final static String JSON_FILE_NAME = "profile.json";

	public byte[] zip(byte[] uncompressed) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final ZipOutputStream zip = new ZipOutputStream(out)) {
			final ZipEntry entry = new ZipEntry(JSON_FILE_NAME);
			zip.putNextEntry(entry);
			zip.write(uncompressed);
			zip.closeEntry();
		}
		return out.toByteArray();
	}

	public byte[] unzip(byte[] compressed) throws IOException {
		final ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final ZipInputStream zip = new ZipInputStream(in)) {
			zip.getNextEntry();

			int count = 0;
			final byte[] buff = new byte[1024];
			while ((count = zip.read(buff, 0, buff.length)) != -1) {
				out.write(buff, 0, count);
			}
		}
		return out.toByteArray();
	}
}
