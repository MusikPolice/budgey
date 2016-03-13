package ca.jonathanfritz.budgey.service;

import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class EncryptionServiceTest {

	@Test
	public void encryptionTest() {
		final EncryptionService encryptionService = new EncryptionService();

		final String password = UUID.randomUUID().toString();
		final byte[] data = UUID.randomUUID().toString().getBytes();
		final byte[] encrypted = encryptionService.encrypt(data, password);
		final byte[] decrypted = encryptionService.decrypt(encrypted, password);

		Assert.assertArrayEquals(data, decrypted);
	}

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
