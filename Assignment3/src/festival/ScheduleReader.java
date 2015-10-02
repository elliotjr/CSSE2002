package festival;

import java.io.*;
import java.util.*;

/**
 * Provides a method to read a shuttle timetable from a file.
 */
public class ScheduleReader {

	// definition of an empty line
	private final static String EMPTY_LINE = "";

	/**
	 * <p>
	 * Reads a text file called fileName that describes the shuttle services
	 * available for a festival, and returns the shuttle timetable containing
	 * each of the services in the file.
	 * </p>
	 * 
	 * <p>
	 * The first line of the file contains a single positive integer, denoting
	 * the number of sessions in the festival. The rest of the file contains
	 * zero or more descriptions of venues and their services for the available
	 * sessions.
	 * </p>
	 * 
	 * <p>
	 * There may be leading or trailing whitespace on the first line of the file
	 * that contains the single positive integer denoting the number of sessions
	 * in the festival.
	 * </p>
	 * 
	 * <p>
	 * A description of a venue and its services consists of (1) a venue name on
	 * a line of its own, followed by (2) one line for each session in the
	 * festival that describes the services that depart the venue at the end of
	 * that session, followed by (3) an empty line. <br>
	 * <br>
	 * (For the purpose of this method) a venue name is simply an unformatted
	 * non-empty string that doesn't contain any whitespace characters. There
	 * may be leading and trailing whitespace on the line containing the venue
	 * name but no other information. <br>
	 * <br>
	 * For (2) the lines for each session in the festival should be ordered by
	 * session number: starting at 1 and ending at the number of sessions in the
	 * festival. Each such line should consist of the session number, followed
	 * by zero or more venue names separated by white spaces. There may be
	 * leading or trailing whitespace on each such line.
	 * 
	 * A venue may not have a shuttle service to itself, and there can be no
	 * duplicate services.
	 * </p>
	 * 
	 * <p>
	 * A venue shouldn't have more than one description of itself and its
	 * services, but a venue doesn't have to have a description of itself and
	 * its services if it doesn't have any.
	 * </p>
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return the shuttle timetable that was read from the file.
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 * @throws FormatException
	 *             if there is an error with the input format (e.g. a venue has
	 *             more than one description of itself and its services, or the
	 *             file format is not as specified above in any other way.) The
	 *             FormatExceptions thrown should have a meaningful message that
	 *             accurately describes the problem with the input file format.
	 */
	public static ShuttleTimetable read(String fileName) throws IOException,
			FormatException {
		// scanner for reading the file
		Scanner in = new Scanner(new FileReader(fileName));
		// the shuttle timetable to be returned
		ShuttleTimetable timetable = new ShuttleTimetable();
		Set<Venue> venues = new HashSet<>(); // source venues read so far
		int lineNumber = 0; // the number of the line being read

		// read number of sessions in festival from first line of in
		int numSessions = readNumberSessions(in, ++lineNumber);
		try {
			// read in the services for each source venue
			while (in.hasNextLine()) {
				// read source venue from next line of in
				Venue source =
						readSourceVenue(in.nextLine(), ++lineNumber, venues);
				for (int session = 1; session <= numSessions; session++) {
					// read services for source and session from next line of in
					readServices(in, ++lineNumber, source, session, timetable);
				}
				checkLineIsEmpty(in.hasNextLine() ? in.nextLine() : null,
						++lineNumber);
			}
		} finally {
			in.close();
		}
		return timetable;
	}

	/**
	 * @require in!=null && in is open for reading
	 * @ensure reads next line from scanner, and returns session number from
	 *         that line
	 * @throws FormatException
	 *             if there is no next line in the scanner, or the line does not
	 *             contain one positive integer denoting the session number.
	 */
	private static int readNumberSessions(Scanner in, int lineNumber)
			throws FormatException {
		// number of sessions to be read
		int numberOfSessions = 0;
		// scanner for parsing the line containing the number of sessions
		Scanner lineScanner = null;
		try {
			if (in.hasNextLine()) {
				lineScanner = new Scanner(in.nextLine());
				if (lineScanner.hasNextInt()) {
					numberOfSessions = lineScanner.nextInt();
				}
				if (numberOfSessions <= 0) {
					throw new FormatException("Line " + lineNumber
							+ ": invalid number of sessions");
				}
				if (lineScanner.hasNext()) {
					throw new FormatException("Line " + lineNumber
							+ ": extra information on line");
				}
			} else {
				throw new FormatException("Line " + lineNumber
						+ ": number of sessions not specified");
			}
		} finally {
			if (lineScanner != null) {
				lineScanner.close();
			}
		}
		return numberOfSessions;
	}

	/**
	 * @require venues != null && line != null
	 * @ensure creates a new venue with it's name specified on the input line,
	 *         and adds it to the set of venues, and returns it
	 * @throws FormatException
	 *             if there is no venue name on the line, or the venue read is
	 *             already in venues, or there is additional information on the
	 *             venue line.
	 */
	private static Venue readSourceVenue(String line, int lineNumber,
			Set<Venue> venues) throws FormatException {
		// scanner for parsing the line containing the source venue
		Scanner lineScanner = null;
		try {
			lineScanner = new Scanner(line);
			if (lineScanner.hasNext()) {
				Venue source = new Venue(lineScanner.next()); // source venue
				if (venues.contains(source)) {
					throw new FormatException("Line " + lineNumber
							+ ": duplicate source venue");
				}
				if (lineScanner.hasNext()) {
					throw new FormatException("Line " + lineNumber
							+ ": extra information on line");
				}
				venues.add(source);
				return source;
			} else {
				throw new FormatException("Line " + lineNumber
						+ ": no venue name given");
			}
		} finally {
			if (lineScanner != null) {
				lineScanner.close();
			}
		}
	}

	/**
	 * @require in != null && in is not closed && source != null && session > 0
	 *          && timetable != null
	 * @ensure reads services for source at session from the next line in the
	 *         scanner and adds them to the timetable
	 * @throws FormatException
	 *             if there is no next line on the scanner, or if the line is
	 *             not correctly formatted (i.e. it does not start with the
	 *             given session, or the services are not valid or contain
	 *             duplicates.)
	 */
	private static void readServices(Scanner in, int lineNumber, Venue source,
			int session, ShuttleTimetable timetable) throws FormatException {
		Scanner lineScanner = null; // scanner for the line

		// check that there is a line for session
		if (!in.hasNextLine()) {
			throw new FormatException("Line " + lineNumber + ": "
					+ "not enough sessions for" + source);
		}

		try {
			lineScanner = new Scanner(in.nextLine());
			// read session number and check that it equals the given session
			readSessionNumber(lineScanner, lineNumber, session);
			// create and add a service for each destination venue
			while (lineScanner.hasNext()) {
				Venue destination = new Venue(lineScanner.next());
				addService(lineNumber, timetable, source, destination, session);
			}
		} finally {
			if (lineScanner != null) {
				lineScanner.close();
			}
		}
	}

	/**
	 * Reads the session number as the first token from the line scanner and
	 * checks that it is the expected session number.
	 * 
	 * @require lineScanner!=null && lineScanner is open for reading
	 * @ensure reads the next integer token from the scanner lineScanner
	 * @throws FormatException
	 *             if lineScanner.hasNextInt() is false or the integer is not
	 *             equal to expectedSessionNumber
	 */
	private static void readSessionNumber(Scanner lineScanner, int lineNumber,
			int expectedSessionNumber) throws FormatException {
		int session = 0;
		if (lineScanner.hasNextInt()) {
			session = lineScanner.nextInt();
		} else {
			throw new FormatException("Line " + lineNumber
					+ ": missing session number " + expectedSessionNumber);
		}
		if (session != expectedSessionNumber) {
			throw new FormatException("Line " + lineNumber
					+ ": wrong session number. Expected "
					+ expectedSessionNumber + " but was " + session);
		}
	}

	/**
	 * @require timetable!=null && source !=null && destination !=null &&
	 *          session > 0
	 * @ensure creates a new service with given source, destination and session
	 *         and adds it to timetable
	 * @throws FormatException
	 *             if source.equals(destination) or timetable already contains
	 *             the service
	 */
	private static void addService(int lineNumber, ShuttleTimetable timetable,
			Venue source, Venue destination, int session)
			throws FormatException {
		if (source.equals(destination)) {
			throw new FormatException("Line " + lineNumber
					+ ": source and destination must be distinct for a service");
		}
		Service service = new Service(source, destination, session);
		if (timetable.hasService(service)) {
			throw new FormatException("Line " + lineNumber
					+ ": duplicate service detected");
		}
		timetable.addService(service);
	}

	/**
	 * Checks that the given line is empty.
	 * 
	 * @throws FormatException
	 *             if line is not equal to the empty string.
	 */
	private static void checkLineIsEmpty(String line, int lineNumber)
			throws FormatException {
		if (!EMPTY_LINE.equals(line)) {
			throw new FormatException("Empty line expected on line "
					+ lineNumber);
		}
	}

}
