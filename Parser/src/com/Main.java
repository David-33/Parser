package com;

import com.parsers.HtmlParser;
import com.utils.Loggers;
import com.utils.NetUtil;

import java.util.List;
import java.util.logging.Logger;


public class Main {

    private static final Logger LOGGER = Loggers.getLogger(Main.class.getName());

    public static void main(String[] args) {
        NetUtil.disableVerification();
        List.of(
//                "https://eda.ru/recepty/vypechka-deserty/tvorozhnyy-chizkeyk-s-fruktami-139955",
//                "https://eda.ru/recepty/zavtraki/zolotye-galleony-126427",
//                "https://eda.ru/recepty/zakuski/kesadilya-s-kuricey-i-chederom-39152",
//                "https://eda.ru/recepty/supy/yaponskiy-buyabes-69467",
//                "https://eda.ru/recepty/supy/bujabes-47152",
                "https://eda.ru/recepty/vypechka-deserty/klassicheskaja-sharlotka-21916"
        ).forEach(url -> {
            LOGGER.info(url + "\n");
            HtmlParser.parsePage(url);
            LOGGER.info("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        });
    }

}
