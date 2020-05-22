package io.knobb.polishr;

import java.io.File;

public class AudioPlayer {

    public static void main(String[] args)
    {
        AudioPlayer.playSound();
    }

    public static void playSound() {
        File file = new File("C:\\Users\\jeffr\\Downloads\\dancing_through_sunday.wav");
        try {
            WavFile wav = new WavFile(file);
            double[] audioData = new double[(int)wav.getFramesCount()];
            for (int i=0;i<wav.getFramesCount();i++) {
                int amplitude = wav.getSampleInt(i);
                // 65536‬ <- Max integer combinations (2^4)
                double amplitudeVal;
                if (amplitude == 0) {
                    amplitudeVal = 0;
                } else {
                    amplitudeVal = amplitude / 65536.0;
                }
                audioData[i] = amplitudeVal;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("DONE");
        }


    }
}
