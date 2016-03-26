package ddsoft.ctrlf.main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Main start for the application
 * This is a command line application that takes in an audio file
 * and creates a timed transcription
 * 
 * @author diego
 *
 */
public class CtrlfMain {
	/**
	 * Start of the program
	 * @param args Filename of audio file and output name
	 */
	public static void main(String [] args) {
		String inputFilename = args[0];
		GeneralAudioTranscriber.transcribe(inputFilename, "transcript.txt");
	}
}
