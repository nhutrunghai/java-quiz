import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	public static void playSoundSuccess() {
		try {
			File soundFile = new File("effect/sound_effect_success.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playSoundFail() throws InterruptedException {
		try {
			File soundFile = new File("effect/sound_effect_fail.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start();
			long durationInMilliseconds = clip.getMicrosecondLength() / 1000;
			Thread.sleep(durationInMilliseconds);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static class SoundThread extends Thread {
		private String soundType;

		public SoundThread(String soundType) {
			this.soundType = soundType;
		}

		@Override
		public void run() {
			if (soundType.equals("success")) {
				playSoundSuccess();
			} else if (soundType.equals("fail")) {
				try {
					playSoundFail();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}
}
