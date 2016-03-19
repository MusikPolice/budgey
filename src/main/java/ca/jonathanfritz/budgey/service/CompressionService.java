package ca.jonathanfritz.budgey.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Convenience functions for compressing the contents of a byte array
 */
public class CompressionService {

	/**
	 * Compresses the specified data using the zip format
	 * @param uncompressed the data to be compressed
	 * @return the compressed data
	 * @throws IOException if something goes wrong
	 */
	public byte[] zip(byte[] uncompressed) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final ZipOutputStream zip = new ZipOutputStream(out)) {
			final ZipEntry entry = new ZipEntry("entry");
			zip.putNextEntry(entry);
			zip.write(uncompressed);
			zip.closeEntry();
		}
		return out.toByteArray();
	}

	/**
	 * Decompresses the specified data using the zip format
	 * @param compressed the data to be decompressed
	 * @return the decompressed data
	 * @throws IOException if something goes wrong
	 */
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
