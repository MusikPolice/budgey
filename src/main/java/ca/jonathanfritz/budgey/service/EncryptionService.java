package ca.jonathanfritz.budgey.service;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.registry.AlgorithmRegistry;

/**
 * Provides convenience methods for encrypting and decrypting byte arrays
 */
public class EncryptionService {

	private String algorithm;

	/**
	 * Initializes a new instance of the EncryptionService with the default algorithm
	 */
	public EncryptionService() {
		// this is the default algorithm that ships with jasypt. it'd be nice to upgrade to something stronger, but
		// java's encryption situation is confusing at best
		this("PBEWithMD5AndDES");
	}

	/**
	 * Initializes a new instance of the EncryptionService with the specified algorithm
	 * @param algorithm a recognized algorithm string. See {@link #getAllSupportedAlgorithms()} for details
	 */
	public EncryptionService(String algorithm) {
		this.algorithm = algorithm;

		// the Legion of BouncyCastle provides an expanded set of encryption algorithms
		registerBouncyCastleProvider();
	}

	/**
	 * @return the name of the algorithm that this instance of EncryptionService was intialized with.
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the algorithm that will be used for all subsequent encryption and decryption operations.
	 * @param algorithm a recognized algorithm string. See {@link #getAllSupportedAlgorithms()} for details
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Returns an array containing all available algorithm names. Note that only a subset of all available algorithms
	 * will be useable on any given machine. Which subset relies on the JRE and whether or not the JCE is installed.
	 */
	@SuppressWarnings("unchecked")
	public String[] getAllSupportedAlgorithms() {
		return (String[]) AlgorithmRegistry.getAllPBEAlgorithms().toArray(new String[] {});
	}

	/**
	 * Encrypts the specified plaintext with the specified password, using the previously activated algorithm
	 * @param plaintext the data to encrypt
	 * @param password the password to encrypt the data with
	 * @return encrypted data
	 */
	public byte[] encrypt(byte[] plaintext, String password) {
		final StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setPassword(password);
		encryptor.initialize();
		return encryptor.encrypt(plaintext);
	}

	/**
	 * Decrypts the specified ciphertext with the specified password, using the previously activated algorithm
	 * @param ciphertext the data to decrypt
	 * @param password the password to decrypt the data with
	 * @return decrypted data
	 */
	public byte[] decrypt(byte[] ciphertext, String password) {
		if (ciphertext == null || ciphertext.length == 0) {
			return new byte[] {};
		}

		final StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setPassword(password);
		encryptor.initialize();
		return encryptor.decrypt(ciphertext);
	}

	private void registerBouncyCastleProvider() {
		for (final Provider p : Security.getProviders()) {
			if (p instanceof BouncyCastleProvider) {
				return;
			}
		}
		Security.addProvider(new BouncyCastleProvider());
	}
}
