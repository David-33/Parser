package com;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;


public final class Constants {

    public static final Level LOGGING_LEVEL = Level.INFO;

    // Filenames
    public static final String LINKS_FILENAME = "recipes.txt";
    public static final String CATEGORIES_FILENAME = "categories.txt";
    public static final String LAST_CATEGORY_FILENAME = "last-category.txt";
    public static final String LAST_PAGE_FILENAME = "last-page.txt";
    public static final String RECIPES_COUNT_FILENAME = "recipes-count.txt";

    // URLs
    public static final String STORY_URL = "https://eda.ru/RecipeStory/Popup?recipeStoryId=%s&isPreviewMode=false";
    public static final String INGREDIENTS_URL = "https://eda.ru/Recipe/RecalculatePortions";

    // REGEX
    public static final String RECIPE_LINK_REGEX = "<a href=\"/recepty/([a-z-]+/[a-z0-9-]+-\\d+)\"";
    public static final String NAME_REGEX = "<h1.*?class=\"recipe__name g-h1\".*?>\s*(.+?)\s*</h1>";
    public static final String CATEGORIES_REGEX = "<li>" +
            "\s*<a.*?class=\"\".*?href=\"https://eda.ru/recepty/.*?\".*?>(.+?)</a>" +
            "\s*</li>";
    public static final String STORY_ID_REGEX = "data-recipe-story-id=\"(.+?)\"";
    public static final String STORY_REGEX = "<p.*?class=\"recipe__description layout__content-col _default-mod\".*?>\s*?(.+?)\s*?</p>";
    public static final String POPUP_STORY_REGEX = "<div.*?class=\"story__text\".*?>\s*?(.+?)\s*?</div>";
    public static final String ENERGY_VALUE_REGEX = "<p.*?class=\"nutrition__name\".*?>(.+?)</p>" +
            "\s*?<p.*?class=\"nutrition__weight\".*?>(.+?)</p>" +
            "\s*?<p.*?class=\"nutrition__percent\".*?>(.+?)</p>";
    public static final String TIME_REGEX = "<span.*?class=\"info-text\".*?>(.+?)</span>";
    public static final String IMG_REGEX = "<div.*?data-index=\"%d\">\s*?<img.*?src=\"(.+?)\">";
    public static final String IMG_DIMENSIONS_REGEX = "\\d+x\\d+";
    public static final String STEPS_IMG_REGEX = "<img class=\"g-print-visible\" width=\"\\d*\" src=\"(.+?)\"";
    public static final String INGREDIENT_REGEX = "\"id\".*?%s.*?\"name\".*?\"(.+?)\"";
    public static final String INGREDIENT_ID_REGEX = "\"(\\d+?)\":";
    public static final String INGREDIENT_VALUE_REGEX = ":\"(.+?)\"";
    public static final String INSTRUCTION_STEP_REGEX = "<a name=\"step%d\".*?</span>(.+?)\s*?</span>";

    public static final String IMG_DIMENSIONS = "544x370";
    public static final int IMAGES_MAX_COUNT = 5;
    public static final int PORTIONS_COUNT = 12;

    public static final Map<String, String> PROPERTIES_GET = Map.of(
            "upgrade-insecure-requests", "1",
            "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36",
            "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,hy;q=0.6"
    );

    public static final Map<String, String> PROPERTIES_POST = Collections.unmodifiableMap(new LinkedHashMap<>() {{
        putAll(PROPERTIES_GET);
        put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    }});

}
