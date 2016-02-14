package ca.jonathanfritz.budgey.util.jackson;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Jackson Deserializer for {@link Money} types
 * See http://stackoverflow.com/a/23878172/591374
 */
@SuppressWarnings("serial")
public class JodaMoneyDeserializer extends StdDeserializer<Money> {
	protected JodaMoneyDeserializer() {
		super(Money.class);
	}

	@Override
	public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		return Money.parse(jp.readValueAs(String.class));
	}
}
