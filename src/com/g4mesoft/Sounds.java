package com.g4mesoft;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.LineUnavailableException;

import com.g4mesoft.sound.SoundManager;
import com.g4mesoft.sound.processor.AudioSource;

public class Sounds {

	public static int ENGINE_SOUND = -1;
	public static int LASER_SOUND = -1;
	
	public static void loadSounds() throws IOException {
		ENGINE_SOUND = loadSound("/engine.wav");
		LASER_SOUND = loadSound("/laser.wav");
	}
	
	private static int loadSound(String path) throws IOException {
		InputStream is = Sounds.class.getResourceAsStream(path);
		if (is == null)
			throw new IOException("Sound '" + path + "' not found.");
		return SoundManager.getInstance().loadSound(is);
	}
	
	public static AudioSource loopForever(int soundId, float pitch) {
		AudioSource audio = play(soundId, pitch);
		if (audio != null)
			audio.setLoopAmount(AudioSource.LOOP_CONTINOUSLY);
		return audio;
	}
	
	public static AudioSource play(int soundId, float pitch) {
		AudioSource audio = null;
		try {
			audio = SoundManager.getInstance().playSound(soundId);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		if (audio != null)
			audio.setPitch(pitch);
		return audio;
	}
}
