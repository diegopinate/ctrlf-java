package ddsoft.ctrlf.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Audio Converter
 * This class converts various audio formats into
 * RIFF WAV 16,000/8,000 Hz 16-bit
 * 
 * @author diego
 *
 */
public class AudioConverter {
	// Debug flag
	private static final boolean DEBUG = true;
	
	/**
	 * Creates a readable report for the audio file format
	 * This is for debugging purposes
	 * @param fmt Audio format
	 * @return Report of audio format
	 */
	public static String generateFormatReport(String filename) throws Exception
	{
		String report = "";
		AudioFileFormat inputFileFormat = AudioSystem.getAudioFileFormat(new File(filename));
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filename));
		AudioFormat audioFormat = ais.getFormat();

		report += "Filename: " + filename;
		report += "\nFile Format Type: "	+inputFileFormat.getType();
		report += "\nFile Format String: "+inputFileFormat.toString();
		report += "\nFile lenght: "		+inputFileFormat.getByteLength();
		report += "\nFrame length: "		+inputFileFormat.getFrameLength();
		report += "\nChannels: "			+audioFormat.getChannels();
		report += "\nEncoding: "			+audioFormat.getEncoding();
		report += "\nFrame Rate: "		+audioFormat.getFrameRate();
		report += "\nFrame Size: "		+audioFormat.getFrameSize();
		report += "\nSample Rate: "		+audioFormat.getSampleRate();
		report += "\nSample size (bits): "+audioFormat.getSampleSizeInBits();
		report += "\nBig endian: "		+audioFormat.isBigEndian();
		report += "\nAudio Format String: "+audioFormat.toString();
		report += "\n";
		
		ais.close();

		return report;
	}
	
	/**
	 * Returns true if the filename is a RIFF Wave file
	 * @param filename Filename of audio file
	 * @return True if RIFF format
	 */
	public static boolean isRIFFWav(String filename)
	{
		boolean result = false;

		try
		{
			byte[] buffer = new byte[4];
			FileInputStream fis = new FileInputStream(filename);
			fis.read(buffer);
			// Check first 4 bytes that should contain header
			result = (buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F');
			// Close input stream
			fis.close();
		}
		catch (Exception e)
		{
			System.out.println("Could not check RIFF format: " + e.getMessage());
		}

		return result;
	}
	
	/**
	 * Saves a byte array as a file
	 * @param data Byte array
	 * @param filename Filename to save byte array
	 * @return True if it worked
	 */
	public static boolean saveBytesToAudioFile(byte [] data, String filename, AudioFormat format)
	{
		boolean result = false;
		
		try
		{
			// Create an audio input stream from the source bytes
			InputStream bis = new ByteArrayInputStream(data);
			AudioInputStream stream = new AudioInputStream(bis, format, data.length);
			// Write the file into WAV format
			File file = new File(filename);
			AudioSystem.write(stream,  Type.WAVE, file);
			// Set flag
			result = true;
		}
		catch (Exception e)
		{
			System.out.println("Could not save byte array to file: " + e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Converts an audio file to a specific audio format
	 * @param filename Audio filename
	 * @param fmt Audio format
	 * @return Byte array of converted filename
	 */
	public static byte [] convertToFormat(String filename, AudioFormat fmt) throws Exception
	{
		// Read the input file as bytes
		byte [] inputData = Files.readAllBytes(Paths.get(filename));
		// Convert the audio file
		byte [] outputData = getAudioDataBytes(inputData, fmt);
		return outputData;
	}

	/**
	 * Converts an input file into RIFF format
	 * and saves it to the output filename path
	 * @param inputFilename File to be converted
	 * @param outputFilename Filename used to save
	 * @return True if conversion was possible, false otherwise
	 */
	public static boolean convertToRIFF(String inputFilename, String outputFilename)
	{
		boolean result = false;

		try
		{
			// Debug message: generate report of the input format
			if (DEBUG)
				System.out.println(generateFormatReport(inputFilename));

			// Output format for conversion
			AudioFormat outputFormat = new AudioFormat(16000, 16, 1, true, false);
			// Convert the audio file
			byte [] outputData = convertToFormat(inputFilename, outputFormat);
			// Save converted data
			saveBytesToAudioFile(outputData, outputFilename, outputFormat);
			// If we got here, everything went smoothly
			result = true;
		}
		catch (Exception e)
		{
			System.out.println("Problem converting to RIFF Wav: " + e.getMessage());
		}
		
		// Debug info
		if (DEBUG && isRIFFWav(outputFilename))
		{
			System.out.println("File converted to RIFF successfully");
		}

		return result;
	}

	/**
	 * @param sourceBytes
	 * @param audioFormat
	 * @return
	 * @throws UnsupportedAudioFileException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static byte [] getAudioDataBytes(byte [] sourceBytes, AudioFormat audioFormat) throws UnsupportedAudioFileException, IllegalArgumentException, Exception {
	    if(sourceBytes == null || sourceBytes.length == 0 || audioFormat == null){
	        throw new IllegalArgumentException("Illegal Argument passed to this method");
	    }

	    try (final ByteArrayInputStream bais = new ByteArrayInputStream(sourceBytes);
	         final AudioInputStream sourceAIS = AudioSystem.getAudioInputStream(bais)) {
	        AudioFormat sourceFormat = sourceAIS.getFormat();
	        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels()*2, sourceFormat.getSampleRate(), false);
	        try (final AudioInputStream convert1AIS = AudioSystem.getAudioInputStream(convertFormat, sourceAIS);
	             final AudioInputStream convert2AIS = AudioSystem.getAudioInputStream(audioFormat, convert1AIS);
	             final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
	            byte [] buffer = new byte[8192];
	            while(true){
	                int readCount = convert2AIS.read(buffer, 0, buffer.length);
	                if(readCount == -1){
	                    break;
	                }
	                baos.write(buffer, 0, readCount);
	            }
	            return baos.toByteArray();
	        }
	    }
	}
}
