package ddsoft.ctrlf.main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Does the complete transcription process
 * [audio conversion] -> [transcribe] -> [transcript]
 * 
 * @author diego
 *
 */
public class GeneralAudioTranscriber {
	
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
	 * Transcribes an input audio file and saves the transcription into a text file
	 * @param inputFilename Audio file
	 * @param transcriptFilename Transcript filename to be saved
	 * @return True if transcription and file write was successful
	 */
	public static boolean transcribe(String inputFilename, String transcriptFilename)
	{
		boolean result = false;
		
		String transcript = transcribe(inputFilename);
		
		try
		{
			writeTextFile(transcriptFilename, transcript);
			result = true;
		}
		catch(Exception e)
		{
			System.out.println("Could not write transcript file: " + e.getMessage());
		}
		
		return result;
	}

	/**
	 * Transcribes any audio format into a speech recognition transcript
	 * @param inputFilename Input filename
	 * @return Transcript
	 */
	public static String transcribe(String inputFilename)
	{
		String outputFilename = "rsc/converted.wav";
		String transcription = "";

		// Step 1: Convert file to RIFF WAV
		AudioConverter.convertToRIFF(inputFilename, outputFilename);
		
		// Step 2: Transcribe audio and clean-up converted
		try
		{
			// Transcribe
			transcription = AudioTranscriber.transcribe(outputFilename);
			// Clean-up temporary converted file
			Files.delete(Paths.get(outputFilename));
		}
		catch (Exception e)
		{
			System.out.println("Exception caught while transcribing: " + e.getMessage());
		}
		
		
		return transcription;
	}
}
