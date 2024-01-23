package com.javafx.application;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AudioUploadUtils {

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

    public static String uploadAudioToDatabase(String filePath) {
        try {
            // Compute SHA-256 hash of the file content
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] dataBytes = new byte[1024];
                int nread;
                while ((nread = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
            }
            byte[] digest = md.digest();

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            String sha256 = hexString.toString();

            // Get the file extension
            String extension = filePath.substring(filePath.lastIndexOf("."));

            // create the Songs directory if it doesn't exist
            Path path = Paths.get("Songs");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }

            // Check if the directory was created successfully
            if (Files.exists(path)) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Failed to create directory");
            }

            // Copy the file to the new directory with the name as SHA-256 hash
            Path targetPath = Paths.get("Songs/" + sha256 + extension);
            Files.copy(Paths.get(filePath), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Check if the file was copied successfully
            if (Files.exists(targetPath)) {
                System.out.println("File copied successfully");
            } else {
                System.out.println("Failed to copy file");
            }

            // Return the hashed name of the audio file
            return sha256 + extension;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
