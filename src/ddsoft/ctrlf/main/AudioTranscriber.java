package ddsoft.ctrlf.main;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This class is dedicated to manage using CMU's Sphinx4 library
 * to transcribe audio information
 * 
 * @author diego
 *
 */
public class AudioTranscriber {
	
	/**
	 * Transcribes an audio file
	 * Must be in RIFF WAV 16,000 Hz 16-bit Format
	 * 
	 * @param filename Input file name
	 * @return Returns the transcription of the audio file
	 */
	public static String Transcribe(String audioFilename) throws Exception
	{
        Configuration configuration = new Configuration();

        configuration
                .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration
                .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration
                .setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        
        configuration.setSampleRate(16000);
        
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(
                configuration);
        InputStream stream = new FileInputStream(new File(audioFilename));

        recognizer.startRecognition(stream);
        SpeechResult result;

        String transcription = "";

        while ((result = recognizer.getResult()) != null) {
        	String currentH = result.getHypothesis();
        	transcription += currentH + " ";
        }
        recognizer.stopRecognition();
        
        return transcription;
	}

}
