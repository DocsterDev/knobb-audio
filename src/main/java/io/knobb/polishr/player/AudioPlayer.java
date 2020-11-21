package io.knobb.polishr.player; /*************************************************************************
 *  Compilation:  javac StdAudio.java
 *  Execution:    java StdAudio
 *
 *  Simple library for reading, writing, and manipulating .wav files.

 *
 *  Limitations
 *  -----------
 *    - Does not seem to work properly when reading .wav files from a .jar file.
 *    - Assumes the audio is monaural, with sampling rate of 44,100.
 *
 *************************************************************************/

import javax.sound.sampled.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *  <i>Standard audio</i>. This class provides a basic capability for
 *  creating, reading, and saving audio. 
 *  <p>
 *  The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit, monaural.
 *
 *  <p>
 *  For additional documentation, see <a href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public final class AudioPlayer {

    /**
     *  The sample rate - 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 48000;

    private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;


    private static SourceDataLine line;   // to play the sound
    private static byte[] buffer;         // our internal buffer
    private static int bufferSize = 0;    // number of samples currently in internal buffer

    // not-instantiable
    private AudioPlayer() { }


    // static initializer
    static { init(); }

    // open up an audio stream
    private static void init() {
        try {
            // 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
            AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

            // the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
            // it gets divided because we can't expect the buffered data to line up exactly with when
            // the sound card decides to push out its samples.
            buffer = new byte[4];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // no sound gets made before this call
        line.start();
    }


    /**
     * Close standard audio.
     */
    public static void close() {
        line.drain();
        line.stop();
    }

    /**
     * Write one sample (between -1.0 and +1.0) to standard audio. If the sample
     * is outside the range, it will be clipped.
     */
    public static void play(double lin, double rin) {

        // clip if outside [-1, +1]
        if (lin < -1.0) lin = -1.0;
        if (lin > +1.0) lin = +1.0;

        if (rin < -1.0) rin = -1.0;
        if (rin > +1.0) rin = +1.0;

        // convert to bytes
        short l = (short) (MAX_16_BIT * lin);
        short r = (short) (MAX_16_BIT * rin);

        // Left Channel
        buffer[bufferSize++] = (byte) l;
        buffer[bufferSize++] = (byte) (l >> 8);   // little Endian

        // Right Channel
        buffer[bufferSize++] = (byte) r;
        buffer[bufferSize++] = (byte) (r >> 8);   // little Endian

        // send to sound card if buffer is full        
        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }

    /**
     * Write an array of samples (between -1.0 and +1.0) to standard audio. If a sample
     * is outside the range, it will be clipped.
     */
//    public static void play(double[] input) {
//        for (int i = 0; i < input.length; i++) {
//            play(input[i]);
//        }
//    }

    /**
     * Read audio samples from a file (in .wav or .au format) and return them as a double array
     * with values between -1.0 and +1.0.
     */
//    public static double[] read(String filename) {
//        byte[] data = readByte(filename);
//        int N = data.length;
//        double[] d = new double[N/2];
//        for (int i = 0; i < N/2; i++) {
//            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
//        }
//        return d;
//    }




    /**
     * Play a sound file (in .wav or .au format) in a background thread.
     */
    public static void play(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.play();
    }

    /**
     * Loop a sound file (in .wav or .au format) in a background thread.
     */
    public static void loop(String filename) {
        URL url = null;
        try {
            File file = new File(filename);
            if (file.canRead()) url = file.toURI().toURL();
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) throw new RuntimeException("audio " + filename + " not found");
        AudioClip clip = Applet.newAudioClip(url);
        clip.loop();
    }


    // return data as a byte array
    private static byte[] readByte(String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {

            // try to read from file
            File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }

            // try to read from URL
            else {
                URL url = AudioPlayer.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data = new byte[ais.available()];
                ais.read(data);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }

        return data;
    }





    /***********************************************************************
     * sample test client
     ***********************************************************************/

    // create a note (sine wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(double hz, double duration, double amplitude) {
        int N = (int) (AudioPlayer.SAMPLE_RATE * duration);
        double[] a = new double[N+1];
        for (int i = 0; i <= N; i++)
            a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / AudioPlayer.SAMPLE_RATE);
        return a;
    }

    /**
     * Test client - play an A major scale to standard audio.
     */
    public static void main(String[] args) {

        // 440 Hz for 1 sec
//        double freq = 440.0;
//        for (int i = 0; i <= AudioPlayer.SAMPLE_RATE; i++) {
//            AudioPlayer.play(0.5 * Math.sin(2*Math.PI * freq * i / AudioPlayer.SAMPLE_RATE));
//        }
//
//        // scale increments
//        int[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
//        for (int i = 0; i < steps.length; i++) {
//            double hz = 440.0 * Math.pow(2, steps[i] / 12.0);
//            AudioPlayer.play(note(hz, 1.0, 0.5));
//        }


        // need to call this in non-interactive stuff so the program doesn't terminate
        // until all the sound leaves the speaker.
        AudioPlayer.close();

        // need to terminate a Java program with sound
        System.exit(0);
    }
}