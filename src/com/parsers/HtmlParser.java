package com.parsers;

import com.Constants;
import com.utils.Loggers;
import com.utils.FileUtil;
import com.utils.NetUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HtmlParser {

    private static final Logger LOGGER = Loggers.getLogger(HtmlParser.class.getName());

    public static void parsePage(String url) {
        var html = NetUtil.doGet(url);
        if (html.isEmpty()) return;

        var recipeId = parseFirst(url, "\\d*$");
        var builder1 = new StringBuilder();
        var builder2 = new StringBuilder();
        var builder3 = new StringBuilder();
        var builder4 = new StringBuilder();
        var builder5 = new StringBuilder();

        parseAll(html, Constants.CATEGORIES_REGEX).forEach(category ->
                builder1.append("\t").append(category).append("\n"));
        parseImages(html).forEach(imageUrl ->
                builder2.append("\t").append(imageUrl).append("\n"));
        parseAll(html, Constants.STEPS_IMG_REGEX).forEach(imageUrl ->
                builder3.append("\t").append(imageUrl).append("\n"));
        parseInstructions(html).forEach(instruction ->
                builder4.append("\t").append(instruction).append("\n"));
        parseIngredients(html, recipeId).forEach((key, value) ->
                builder5.append("\t").append(key).append(" - ").append(value).append("\n"));

        LOGGER.info("Categories:\n" + builder1);
        LOGGER.info("Name: " + parseFirst(html, Constants.NAME_REGEX) + "\n");
        LOGGER.info("Time: " + parseFirst(html, Constants.TIME_REGEX) + "\n");
        LOGGER.info("Images:\n" + builder2);
        LOGGER.info("Story:\n\t" + parseStory(html) + "\n");
        LOGGER.info("Energy value: " + parseAll(html, Constants.ENERGY_VALUE_REGEX) + "\n");
        LOGGER.info("Steps images:\n" + builder3);
        LOGGER.info("Instructions:\n" + builder4);
        LOGGER.info("Ingredients:\n" + builder5);
    }

    public static void parseLinks() {
        var categories = FileUtil.readCategories();
        categories.forEach(category -> {
            FileUtil.writeCategory(category);
            parseCategory(category);
            FileUtil.writePageNumber(0);
        });
    }

    private static void parseCategory(String category) {
        var lastPage = FileUtil.readPageNumber();
        var categoryLink = category.substring(0, category.indexOf(' '));
        var expectedRecipesCount =
                Integer.parseInt(category.substring(category.lastIndexOf(' ') + 1));
        LOGGER.info(String.format("CATEGORY: %s\n", category));
        LOGGER.info(String.format("LAST PAGE: %d\n", lastPage));

        int totalRecipesCount = 0;
        for (int pageNumber = lastPage + 1; true; ++pageNumber) {
            var recipesLinks = parseRecipesLinks(categoryLink, pageNumber);
            if (recipesLinks.isEmpty()) break;
            FileUtil.writeToFile(recipesLinks, true);
            FileUtil.writePageNumber(pageNumber);

            int recipesCount = recipesLinks.size();
            totalRecipesCount += recipesCount;
            LOGGER.info(String.format("Page №%d parsed, recipes count = %d\n",
                    pageNumber, recipesCount));
            if (recipesCount != 14)
                LOGGER.warning("RECIPES COUNT = " + recipesCount + "\n");
        }
        if (totalRecipesCount != 0)
            FileUtil.writeRecipesCount(category, totalRecipesCount);
        LOGGER.info("TOTAL RECIPES COUNT = " + totalRecipesCount + "\n");
        LOGGER.info("EXPECTED RECIPES COUNT = " + expectedRecipesCount + "\n");
    }

    private static Set<String> parseRecipesLinks(String categoryLink, int pageNumber) {
        var html = NetUtil.doGet(String.format("%s?page=%d", categoryLink, pageNumber));
        if (html.isEmpty()) return Set.of();
        var recipesLinks = new TreeSet<String>();
        var matcher = Pattern.compile(Constants.RECIPE_LINK_REGEX).matcher(html);
        while (matcher.find()) {
            recipesLinks.add(matcher.group(1));
        }
        return recipesLinks;
    }

    public static void verifyParsedLinksCount() {
        try (var reader = new BufferedReader(new FileReader(Constants.RECIPES_COUNT_FILENAME))) {
            var expectedAndTotalCounts = reader.lines()
                    .map(link -> link.substring(link.lastIndexOf(' ', link.lastIndexOf(' ') - 1) + 1))
                    .reduce((link1, link2) -> {
                        var numbs1 = link1.split("[/ ]");
                        var numbs2 = link2.split("[/ ]");
                        return Integer.parseInt(numbs1[0])
                                + Integer.parseInt(numbs2[0])
                                + "/"
                                + (Integer.parseInt(numbs1[1])
                                + Integer.parseInt(numbs2[1]));
                    });
            LOGGER.info("Expected/Total: " + expectedAndTotalCounts.orElse("?/?"));
        } catch (FileNotFoundException e) {
            LOGGER.warning("FileNotFoundException: " + e.getMessage() + "\n");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public static void removeDuplicateLinks() {
        try (var reader = new BufferedReader(new FileReader(Constants.LINKS_FILENAME))) {
            var links = reader.lines().collect(Collectors.toCollection(TreeSet::new));
            FileUtil.writeToFile(links, false);
        } catch (FileNotFoundException e) {
            LOGGER.warning("FileNotFoundException: " + e.getMessage() + "\n");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }


    private static String parseStory(String html) {
        var storyId = parseFirst(html, Constants.STORY_ID_REGEX);
        String story;
        if (storyId == null || storyId.isEmpty()) {
            story = parseFirst(html, Constants.STORY_REGEX);
        } else {
            story = parseFirst(NetUtil.doGet(
                    String.format(Constants.STORY_URL, storyId)), Constants.POPUP_STORY_REGEX);
        }
        return story;
    }

    private static List<String> parseImages(String html) {
        var images = new ArrayList<String>(Constants.IMAGES_MAX_COUNT);
        String imgUrl;
        for (int i = 0; i < Constants.IMAGES_MAX_COUNT; ++i) {
            imgUrl = parseFirst(html, String.format(Constants.IMG_REGEX, i));
            if (imgUrl == null) break;
            images.add(imgUrl.replaceFirst(Constants.IMG_DIMENSIONS_REGEX,
                    Constants.IMG_DIMENSIONS));
        }
        return images;
    }

    private static Map<String, List<String>> parseIngredients(String html, String recipeId) {
        var ingredients = new LinkedHashMap<String, List<String>>();
        var arguments = new HashMap<String, String>() {{
            put("recipeID", recipeId);
            put("portionsCount", "1");
        }};
        var jsonString = clearString(NetUtil.doPost(Constants.INGREDIENTS_URL, arguments));
        var ingredientsNames = parseIngredientsNames(html,
                parseAll(jsonString, Constants.INGREDIENT_ID_REGEX));
        ingredientsNames.forEach(ingredientName ->
                ingredients.put(ingredientName, new ArrayList<>(Constants.PORTIONS_COUNT)));

        for (int i = 1; i <= Constants.PORTIONS_COUNT; ++i) {
            arguments.replace("portionsCount", Integer.toString(i));
            var jsonStr = clearString(NetUtil.doPost(Constants.INGREDIENTS_URL, arguments));
            var ingredientsValues = parseAll(jsonStr, Constants.INGREDIENT_VALUE_REGEX);
            var valuesIterator = ingredientsValues.iterator();
            ingredientsNames.forEach(ingredientName ->
                    ingredients.get(ingredientName).add(valuesIterator.next()));
        }
        return ingredients;
    }

    private static List<String> parseIngredientsNames(String html, List<String> ingredientsIds) {
        var names = new ArrayList<String>(ingredientsIds.size());
        ingredientsIds.forEach(id ->
                names.add(parseFirst(html, String.format(Constants.INGREDIENT_REGEX, id))));
        return names;
    }

    private static List<String> parseInstructions(String html) {
        var instructions = new ArrayList<String>();
        for (int i = 1; true; ++i) {
            var instruction = parseFirst(html,
                    String.format(Constants.INSTRUCTION_STEP_REGEX, i));
            if (instruction == null || instruction.isEmpty()) break;
            instructions.add(instruction);
        }
        return instructions;
    }

    private static String parseFirst(String source, String regex) {
        var list = parse(source, regex, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    private static List<String> parseAll(String source, String regex) {
        return parse(source, regex, Integer.MAX_VALUE);
    }

    private static List<String> parse(String source, String regex, int count) {
        var result = new ArrayList<String>();
        var matcher = Pattern.compile(regex).matcher(source);
        for (int index = 0; index < count && matcher.find(); ++index) {
            if (matcher.groupCount() == 0) {
                result.add(clearString(matcher.group()));
            }
            var joiner = new StringJoiner("|");
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                joiner.add(clearString(matcher.group(i)));
            }
            result.add(joiner.toString());
        }
        return result;
    }

    private static String clearString(String str) {
        return StringEscapeUtils
                .unescapeHtml4(StringEscapeUtils.unescapeJson(str).replace("&nbsp;", " "))
                .replaceAll("<.*?>", "")
                .trim();
    }

}
