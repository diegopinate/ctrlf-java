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
	 * Saves a string to a text file
	 * @param filename Name of the file
	 * @param data Text
	 * @throws FileNotFoundException 
	 */
	public static void writeTextFile(String filename, String data) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(filename);
		out.println(data);
		out.close();
	}
	
	/**
	 * Start of the program
	 * @param args Filename of audio file and output name
	 */
	public static void main(String [] args) {
		String inputFilename = args[0];
		String outputFilename = args[1];
		String transcription = "";

		// Step 1: Convert file to RIFF WAV
		// TO-DO
		AudioConverter.convertToRIFF(inputFilename, outputFilename);
		
		System.exit(0);

		// Step 2: Transcribe audio
		try
		{
			transcription = AudioTranscriber.transcribe(outputFilename);
			System.out.println("Transcript: " + transcription);
		}
		catch (Exception e)
		{
			System.out.println("Exception caught while transcribing: " + e.getMessage());
			System.exit(1);
		}

		// Step 3: Save transcription
		try
		{
			writeTextFile("transcript.txt", transcription);
		}
		catch (Exception e)
		{
			System.out.println("Exception caught while saving transcript: " + e.getMessage());
			System.exit(1);
		}
	}
}
