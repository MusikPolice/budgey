package ca.jonathanfritz.budgey.service;

import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link EncryptionService}
 */
public class EncryptionServiceTest {

	/**
	 * Ensures that data can be encrypted and decrypted using the default algorithm
	 */
	@Test
	public void encryptionTest() {
		final EncryptionService encryptionService = new EncryptionService();

		final String password = UUID.randomUUID().toString();
		final byte[] data = UUID.randomUUID().toString().getBytes();
		final byte[] encrypted = encryptionService.encrypt(data, password);
		final byte[] decrypted = encryptionService.decrypt(encrypted, password);

		Assert.assertArrayEquals(data, decrypted);
	}

	/**
	 * Queries the list of available algorithms and attempts an encryption and decryption step with each. Those that
	 * function without issue are printed to the console.
	 */
	@Test
	public void availableAlgorithmsTest() {
		System.out.println("Available Encryption Algorithms:");
		final EncryptionService encryptionService = new EncryptionService();
		final byte[] data = "testdata".getBytes();
		final String password = "password";

		for (final String a : encryptionService.getAllSupportedAlgorithms()) {
			try {
				encryptionService.setAlgorithm(a);
				final byte[] encrypted = encryptionService.encrypt(data, password);
				final byte[] decrypted = encryptionService.decrypt(encrypted, password);
				Assert.assertThat(decrypted, IsEqual.equalTo(data));
			} catch (final Exception e) {
				continue;
			}
			System.out.println("\t" + a);
		}
	}
}
