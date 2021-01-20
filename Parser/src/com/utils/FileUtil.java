package com.utils;

import com.Constants;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class FileUtil {

    private static final Logger LOGGER = Loggers.getLogger(FileUtil.class.getName());

    public static void writeCategory(String category) {
        try (var categoryWriter = new FileWriter(Constants.LAST_CATEGORY_FILENAME)) {
            categoryWriter.write(category);
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void writePageNumber(int page) {
        try (var fileWriter = new FileWriter(Constants.LAST_PAGE_FILENAME)) {
            fileWriter.write(Integer.toString(page));
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void writeRecipesCount(String category, int totalRecipesCount) {
        try (var fileWriter = new FileWriter(Constants.RECIPES_COUNT_FILENAME, true)) {
            fileWriter.write(category + " " + totalRecipesCount + "\n");
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void writeToFile(Set<String> links, boolean append) {
        try (var fileWriter = new FileWriter(Constants.LINKS_FILENAME, append)) {
            for (var recipeLink : links) {
                fileWriter.write(recipeLink + '\n');
            }
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
        }
    }

    public static Set<String> readCategories() {
        try (var categoryReader = new Scanner(new File(Constants.LAST_CATEGORY_FILENAME));
             var linksReader = new BufferedReader(new FileReader(Constants.CATEGORIES_FILENAME))) {
            var categoryLink = categoryReader.hasNextLine() ? categoryReader.nextLine() : "";
            return linksReader.lines()
                    .filter(link -> link.compareTo(categoryLink) >= 0)
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
        return Set.of();
    }

    public static int readPageNumber() {
        try (var scanner = new Scanner(new File(Constants.LAST_PAGE_FILENAME))) {
            return scanner.hasNextInt() ? scanner.nextInt() : 0;
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
            return 0;
        }
    }

    public static String readFile(String filename, Charset charset) {
        var file = new File(filename);
        byte[] data = new byte[(int) file.length()];
        int totalBytesCount = 0;
        try (var fileInputStream = new FileInputStream(file)) {
            totalBytesCount = fileInputStream.read(data);
        } catch (FileNotFoundException e) {
            LOGGER.warning("FileNotFoundException: " + filename + "\n");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + ", filename: " + filename + "\n");
            e.printStackTrace();
        }
        if (totalBytesCount > 0) {
            return new String(data, 0, totalBytesCount, charset);
        }
        return "";
    }

    public static void writeFile(String data, String filename, Charset charset, boolean append) {
        byte[] dataBytes = data.getBytes(charset);
        try (var fileOutputStream = new FileOutputStream(filename, append)) {
            fileOutputStream.write(dataBytes);
        } catch (FileNotFoundException e) {
            LOGGER.warning("FileNotFoundException: " + filename + "\n");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + ", filename: " + filename + "\n");
            e.printStackTrace();
        }
    }

}
