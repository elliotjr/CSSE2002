package festival;

/** An immutable class representing a venue at a music festival. **/
public class Venue {

	// the name of the venue
	private String name;

	/* invariant: name != null */

	/**
	 * Creates a new venue with the given name.
	 * 
	 * @param name
	 *            the name of the venue
	 * @throws NullPointerException
	 *             if the given name is null
	 */
	public Venue(String name) {
		if (name == null) {
			throw new NullPointerException("Venue names cannot be null");
		}
		this.name = name;
	}

	/**
	 * Returns the name of the venue.
	 * 
	 * @return the name of the venue
	 */
	public String getName() {
		return name;
	}

	/**
	 * Two venues are considered equal if their names are equivalent.
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Venue)) {
			return false;
		}
		Venue venue = (Venue) object; // venue to compare
		return this.name.equals(venue.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Determines whether this Venue is internally consistent (i.e. it satisfies
	 * its class invariant).
	 * 
	 * @return true if this Venue is internally consistent, and false otherwise.
	 */
	public boolean checkInv() {
		return name != null;
	}

}
