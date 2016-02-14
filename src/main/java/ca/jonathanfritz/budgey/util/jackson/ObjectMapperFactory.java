package ca.jonathanfritz.budgey.util.jackson;

import org.joda.money.Money;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * A factory that returns a properly configured {@link ObjectMapper} singleton
 */
public class ObjectMapperFactory {

	private static ObjectMapper objectMapper;

	private ObjectMapperFactory() {
	}

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();

			// joda time
			objectMapper.registerModule(new JodaModule());

			// joda money
			final SimpleModule moneyModule = new SimpleModule();
			moneyModule.addSerializer(Money.class, new JodaMoneySerializer());
			moneyModule.addDeserializer(Money.class, new JodaMoneyDeserializer());
			objectMapper.registerModule(moneyModule);
		}
		return objectMapper;
	}
}
