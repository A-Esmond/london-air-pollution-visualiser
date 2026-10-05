package util;

import java.util.Optional;

/**
 * Hold all input data for a query in the stats panel.
 * This is used to filter data accordingly.
 * There is a nested Builder class that is used to collect the
 * input data and validate it before a Query object is created.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public class Query {
	private final String startYear;
	private final String endYear;
	private final String pollutant;
	private final Tuple<Integer, Integer> firstAreaBound;
	private final Tuple<Integer, Integer> secondAreaBound;

	private Query(Builder builder) {
		this.startYear = builder.startYear;
		this.endYear = builder.endYear;
		this.pollutant = builder.pollutant;
		this.firstAreaBound = builder.firstCoords;
		this.secondAreaBound = builder.secondCoords;
	}

	public String getStartYear() {
		return startYear;
	}

	public String getEndYear() {
		return endYear;
	}

	public String getPollutant() {
		return pollutant;
	}

	public Tuple<Integer, Integer> getFirstAreaBound() {
		return firstAreaBound;
	}

	public Tuple<Integer, Integer> getSecondAreaBound() {
		return secondAreaBound;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("New query \n").append("Start year: " + startYear + "\n").append("End year: " + endYear + "\n").append("Pollutant: " + pollutant + "\n").append("Area boundaries: (" + firstAreaBound.val1() + ", " + firstAreaBound.val2()).append(") to (" + secondAreaBound.val1() + ", " + secondAreaBound.val2() + ")" + "\n");
		return stringBuilder.toString();
	}

	/**
	 * Nested class that collects the fields for a query and validates it.
	 *
	 * @author Esmond Atiemo
	 * @version 2026.03.12
	 */
	public static class Builder {
		private String startYear;
		private String endYear;
		private String pollutant;
		private Tuple<Integer, Integer> firstCoords;
		private Tuple<Integer, Integer> secondCoords;

		public Builder startYear(String startYear) {
			this.startYear = startYear;
			return this;
		}

		public Builder endYear(String endYear) {
			this.endYear = endYear;
			return this;
		}

		public Builder pollutant(String pollutant) {
			this.pollutant = pollutant;
			return this;
		}

		public Builder firstCoords(Tuple<Integer, Integer> coords) {
			this.firstCoords = coords;
			return this;
		}

		public Builder secondCoords(Tuple<Integer, Integer> coords) {
			this.secondCoords = coords;

			return this;
		}

		public Tuple<Integer, Integer> getFirstCoords() {
			return firstCoords;
		}

		public Tuple<Integer, Integer> getSecondCoords() {
			return secondCoords;
		}

		/**
		 * Checks the inputs collected so far.
		 *
		 * @return The first problem found, or empty if the query is valid.
		 */
		public Optional<QueryError> validate() {
			if (!isYear(startYear) || !isYear(endYear)) {
				return Optional.of(QueryError.MISSING_YEAR);
			}
			if (Integer.parseInt(startYear) > Integer.parseInt(endYear)) {
				return Optional.of(QueryError.START_AFTER_END);
			}
			if (firstCoords == null || secondCoords == null) {
				return Optional.of(QueryError.MISSING_AREA);
			}
			if (pollutant == null) {
				return Optional.of(QueryError.NO_POLLUTANT);
			}
			return Optional.empty();
		}

		/**
		 * @return true if the text is a whole number (the dropdowns hold
		 * placeholder text such as "Start year:" until a year is picked).
		 */
		private static boolean isYear(String text) {
			return text != null && text.matches("\\d+");
		}

		public Query build() {
			return new Query(this);
		}
	}
}
