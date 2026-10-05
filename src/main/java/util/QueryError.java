package util;

/**
 * Every reason a statistics query can be rejected, with the message shown to the user.
 * Replaces the old numeric error codes (1-5), so the compiler checks that a code exists.
 *
 * @author Esmond Atiemo
 * @version 2026.10.01
 */
public enum QueryError {
	MISSING_YEAR("Start year or end year is missing. \nPlease check your inputted years."),
	START_AFTER_END("Start year cannot be greater than end year. \nPlease check your inputted years."),
	MISSING_AREA("Boundary coordinate(s) are missing. \nPlease select two distinct points on the map."),
	AREA_TOO_SMALL("Boundary area is too small. \nPlease select a larger boundary area."),
	NO_POLLUTANT("No pollutant selected. \nPlease select at least one pollutant.");

	private final String message;

	QueryError(String message) {
		this.message = message;
	}

	/**
	 * @return The explanation shown to the user in the alert.
	 */
	public String getMessage() {
		return message;
	}
}
