package ca.jonathanfritz.budgey.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link CompressionService}
 */
public class CompressionServiceTest {

	@Test
	public void asciiDataTest() throws IOException {
		final CompressionService c = new CompressionService();
		final byte[] data = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
		final byte[] compressed = c.zip(data);
		final byte[] decompressed = c.unzip(compressed);
		Assert.assertArrayEquals(decompressed, data);
	}

	@Test
	public void binaryDataTest() throws IOException {
		final CompressionService c = new CompressionService();
		final byte[] data = new byte[256];
		final Random r = new Random();
		r.nextBytes(data);
		final byte[] compressed = c.zip(data);
		final byte[] decompressed = c.unzip(compressed);
		Assert.assertArrayEquals(decompressed, data);
	}
}
