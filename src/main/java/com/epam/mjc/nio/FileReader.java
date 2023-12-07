package com.epam.mjc.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileReader {

    public Profile getDataFromFile(File file) {
        String text = getStringFromFile(file);
        return getProfileInfoFromText(text);
    }

    private String getStringFromFile(File file) {
        StringBuilder text = new StringBuilder();
        if (checkIfFileExists(file)) {
            try (RandomAccessFile aRndAccFile = new RandomAccessFile(file.getPath(), "r");
                 FileChannel inChannel = aRndAccFile.getChannel()
            ) {
                ByteBuffer buffer = ByteBuffer.allocate(256);
                while (inChannel.read(buffer) > 0) {
                    buffer.flip();
                    for (int i = 0; i < buffer.limit(); i++)
                        text.append((char) buffer.get());
                    buffer.clear();
                }
            } catch (IOException e) {
                throw new InputFileNotFoundException(e.toString());
            }
        }
        return text.toString();
    }

    private Profile getProfileInfoFromText(String text) {
        Profile profile = new Profile();
        if (text.length() > 1) {
            String[] data = text.split("[\\u0020\\n\\r]");
            for (int i = 0; i < data.length; i++) {
                if (data[i].isEmpty())
                    continue;
                if (data[i].toLowerCase().contains("name")) {
                    profile.setName(data[i + 1]);
                } else if (data[i].toLowerCase().contains("age")) {
                    profile.setAge(Integer.valueOf(data[i + 1]));
                } else if (data[i].toLowerCase().contains("email")) {
                    profile.setEmail(data[i + 1]);
                } else if (data[i].toLowerCase().contains("phone")) {
                    profile.setPhone(Long.valueOf(data[i + 1]));
                }
            }
        }
        return profile;
    }

    private boolean checkIfFileExists(File file) {
        Path path = Paths.get(file.getPath());
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }
}
