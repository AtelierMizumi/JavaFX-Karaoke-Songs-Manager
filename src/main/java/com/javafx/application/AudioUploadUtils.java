package com.javafx.application;

import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AudioUploadUtils {

    private TextField filePathTextField;

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Update the file path text field with the selected file path
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    public static byte[] convertAudioToBytes(String filePath) {
        byte[] audioBytes = null;
        try {
            File audioFile = new File(filePath);
            audioBytes = new byte[(int) audioFile.length()];
            FileInputStream fileInputStream = new FileInputStream(audioFile);
            fileInputStream.read(audioBytes);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioBytes;
    }

    public static String getAudioDuration(String audioFilePath) {
        Media media = new Media(new File(audioFilePath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        final String[] length = new String[1];
        final CountDownLatch latch = new CountDownLatch(1);
        mediaPlayer.setOnReady(() -> {
            Duration duration = media.getDuration();
            int durationInSeconds = (int) Math.floor(duration.toSeconds());
            int minutes = durationInSeconds / 60;
            int seconds = durationInSeconds % 60;
            length[0] = String.format("%d:%02d", minutes, seconds);
            latch.countDown();
        });
        try {
            latch.await(5, TimeUnit.SECONDS); // wait for mediaPlayer to be ready
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return length[0];
    }

    public static Blob convertAudioToBlob(String filePath) {
        Blob blob = null;
        try {
            File audioFile = new File(filePath);
            FileInputStream fis = new FileInputStream(audioFile);
            Connection conn = DatabaseHandler.getInstance().getConnection();
            blob = conn.createBlob();
            OutputStream os = blob.setBinaryStream(1);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blob;
    }
}
