package com.example.Xbot.MainBot.Image;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Getter
public class ImageGenerator {

    private String code;

    public String create() throws IOException {
        code = generateCode();
        System.out.println("Code: " + code);

        BufferedImage imageWithCode = createImageWithCode(code);

        String fileName = "code_" + code + ".png";

        File outputFile = new File("MyImages/" + fileName);

        outputFile.getParentFile().mkdirs();

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            ImageIO.write(imageWithCode, "png", outputStream);
            System.out.println("Image saved as " + outputFile.getAbsolutePath());
        }

        BufferedImage imageFromFile = ImageIO.read(outputFile);
        System.out.println("Image loaded from " + outputFile.getAbsolutePath());

        // Return unique file name
        return fileName;
    }

    private static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = (int) (Math.random() * 10);
            code.append(digit);
        }
        return code.toString();
    }

    private static BufferedImage createImageWithCode(String code) {
        int width = 300;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.setColor(Color.WHITE);
        g.drawString(code, (width - g.getFontMetrics().stringWidth(code)) / 2, height / 2 + g.getFontMetrics().getAscent() / 2);
        g.dispose();

        return image;
    }

    public void deleteAll() {
        String dirPath = "MyImages";
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean isDeleted = file.delete();
                        if (isDeleted) {
                            System.out.println("Deleted file: " + file);
                        } else {
                            System.err.println("Failed to delete file: " + file);
                        }
                    }
                }
            }
        } else {
            System.err.println("The specified path is not a directory");
        }
    }

    public void deleteFile(String fileName) {
        String dirPath = "MyImages";
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File file = new File(dir, fileName);
            if (file.isFile()) {
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    System.out.println("Deleted file: " + file);
                } else {
                    System.err.println("Failed to delete file: " + file);
                }
            } else {
                System.err.println("The specified file is not found");
            }
        } else {
            System.err.println("The specified path is not a directory");
        }
    }


}