package ca.jonathanfritz.budgey.util.jackson;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Jackson Serializer for {@link Money} types
 * See http://stackoverflow.com/a/23878172/591374
 */
@SuppressWarnings("serial")
public class JodaMoneySerializer extends StdSerializer<Money> {
	protected JodaMoneySerializer() {
		super(Money.class);
	}

	@Override
	public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.toString());
	}
}
