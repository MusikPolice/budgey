package ca.jonathanfritz.budgey.service;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.registry.AlgorithmRegistry;

public class EncryptionService {

	private String algorithm;

	public EncryptionService() {
		// this is the default algorithm that ships with jasypt
		// it'd be nice to upgrade to something stronger, but java's encryption situation is confusing at best
		this("PBEWithMD5AndDES");
	}

	public EncryptionService(String algorithm) {
		this.algorithm = algorithm;

		// the Legion of BouncyCastle provides an expanded set of encryption algorithms
		registerBouncyCastleProvider();
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@SuppressWarnings("unchecked")
	public String[] getAllSupportedAlgorithms() {
		return (String[]) AlgorithmRegistry.getAllPBEAlgorithms().toArray(new String[] {});
	}

	protected byte[] encrypt(byte[] plaintext, String password) {
		final StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setPassword(password);
		encryptor.initialize();
		return encryptor.encrypt(plaintext);
	}

	protected byte[] decrypt(byte[] ciphertext, String password) {
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
