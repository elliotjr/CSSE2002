package festival;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a method to read a festival line-up from a file.
 */
public class LineUpReader {

	/**
	 * <p>
	 * Reads a text file called fileName that describes the events in the
	 * line-up of a festival, and returns the line-up of events read from the
	 * file.
	 * </p>
	 * 
	 * <p>
	 * The file should contain zero or more lines, and each line of the file
	 * should specify exactly one event.
	 * </p>
	 * 
	 * <p>
	 * Each line describing an event should be of the form
	 * 
	 * "ACT: session SESSION at VENUE"
	 * 
	 * where the description of the act, ACT, may be any non-empty string
	 * containing word characters (digits or lower or upper-case ASCII letters)
	 * and single space characters (' '), and the session number SESSION is a
	 * positive integer, and the venue name VENUE is simply an unformatted
	 * non-empty string that doesn't contain any whitespace characters.
	 * </p>
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return the line-up that was read from the file.
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 * @throws FormatException
	 *             if there is an error with the input format.
	 */
	public static LineUp read(String fileName) throws IOException,
			FormatException {
		// scanner for reading the file
		Scanner in = new Scanner(new FileReader(fileName));
		LineUp lineUp = new LineUp();// the line-up to be returned
		int lineNumber = 0;// the number of the line being read

		try {
			// read in the events from the line-up, one per line
			while (in.hasNextLine()) {
				try {
					Event event = readEventString(++lineNumber, in.nextLine());
					lineUp.addEvent(event);
				} catch (InvalidLineUpException e) {
					throw new FormatException("Line " + lineNumber
							+ ": more than one event scheduled for"
							+ " the same time and session");
				}

			}
		} finally {
			in.close();
		}
		return lineUp;
	}

	/**
	 * <p>
	 * Reads and returns the event from the given line of the file.
	 * </p>
	 * 
	 * <p>
	 * The line describing the event should be of the form
	 * 
	 * "ACT: session SESSION at VENUE"
	 * 
	 * where the description of the act, ACT, may be any non-empty string
	 * containing word characters (digits or lower or upper-case ASCII letters)
	 * and single space characters (' '), and the session number SESSION is a
	 * positive integer, and the venue name VENUE is simply an unformatted
	 * non-empty string that doesn't contain any whitespace characters.
	 * </p>
	 * 
	 * @param line
	 *            the string containing the string representation of the event
	 * @param lineNumber
	 *            the line number that the line occurred on in the file (to be
	 *            used for error messages format exceptions).
	 * @return the event read from the line
	 * @throws FormatException
	 *             if the format of the event on the line is not valid (as
	 *             described above).
	 */
	private static Event readEventString(int lineNumber, String line)
			throws FormatException {
		Venue venue; // venue of the event on line
		int session; // session of the event on line
		String act; // act of the event on line

		// the pattern that the string representation of the event should
		// conform to, and the matcher for matching the string to the pattern
		Pattern pattern =
				Pattern.compile("([\\w ]+): session \\+?(\\d+) at (\\S+)");
		Matcher matcher = pattern.matcher(line);

		// retrieve venue, session and act if pattern matches the line
		if (matcher.matches()) {
			act = matcher.group(1);
			try {
				session = Integer.parseInt(matcher.group(2));
			} catch (NumberFormatException e) {
				throw new FormatException("Line " + lineNumber
						+ ": event incorrectly formatted. " + matcher.group(2)
						+ " is not an integer");
			}
			if (session <= 0) {
				throw new FormatException("Line " + lineNumber
						+ ": event incorrectly formatted. " + matcher.group(2)
						+ " is not a positive integer");
			}
			venue = new Venue(matcher.group(3));
		} else {
			throw new FormatException("Line " + lineNumber
					+ ": event incorrectly formatted");
		}

		// create and return event from retrieved values
		return new Event(venue, session, act);
	}

}
