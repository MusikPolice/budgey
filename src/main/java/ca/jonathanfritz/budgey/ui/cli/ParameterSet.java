package ca.jonathanfritz.budgey.ui.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterSet {

	private final Map<String, Parameter> types = new HashMap<>();
	private final Map<String, Object> values = new HashMap<>();

	public ParameterSet(Collection<Parameter> parameters) {
		for (final Parameter p : parameters) {
			types.put(p.name, p);
			values.put(p.name, null);
		}
	}

	public void setParameterValue(String name, String value) {
		setParameterValue(name, value, String.class);
	}

	public void setParameterValue(String name, Integer value) {
		setParameterValue(name, value, Integer.class);
	}

	private <T> void setParameterValue(String name, Object value, Class<T> type) {
		if (types.containsKey(name)) {
			if (types.get(name).type == type) {
				values.put(name, value);
				return;
			}
			throw new IllegalArgumentException("Parameter " + name + " is not a " + type.getName() + " parameter");
		}
		throw new IllegalArgumentException("Parameter " + name + " does not exist");
	}

	public List<Parameter> getParameters() {
		return new ArrayList<>(types.values());
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(String name, Class<T> type) {
		if (types.containsKey(name) && types.get(name).type == type) {
			if (values.containsKey(name)) {
				return (T) values.get(name);
			}
		}
		throw new IllegalArgumentException("Unknown " + type.getName() + " parameter " + name);
	}

	public static class Parameter {
		private final Class<?> type;
		private final String name;
		private final String description;

		public Parameter(String name, String description, Class<?> type) {
			this.name = name;
			this.description = description;
			this.type = type;
		}

		public Class<?> getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}
}