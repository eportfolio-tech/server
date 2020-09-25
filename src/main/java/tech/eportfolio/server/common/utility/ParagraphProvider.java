package tech.eportfolio.server.common.utility;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParagraphProvider {


    private ParagraphProvider() {

    }

    private static List<String> paragraphs() {
        List<String> paragraph = new ArrayList<>();
        paragraph.add(Faker.instance().buffy().quotes());
        paragraph.add(Faker.instance().backToTheFuture().quote());
        paragraph.add(Faker.instance().dune().quote());
        paragraph.add(Faker.instance().elderScrolls().quote());
        paragraph.add(Faker.instance().friends().quote());
        paragraph.add(Faker.instance().gameOfThrones().quote());
        paragraph.add(Faker.instance().harryPotter().quote());
        paragraph.add(Faker.instance().hitchhikersGuideToTheGalaxy().quote());
        paragraph.add(Faker.instance().hitchhikersGuideToTheGalaxy().marvinQuote());
        paragraph.add(Faker.instance().hobbit().quote());
        paragraph.add(Faker.instance().howIMetYourMother().quote());
        paragraph.add(Faker.instance().leagueOfLegends().quote());
        paragraph.add(Faker.instance().lebowski().quote());
        paragraph.add(Faker.instance().matz().quote());
        paragraph.add(Faker.instance().overwatch().quote());
        paragraph.add(Faker.instance().princessBride().quote());
        paragraph.add(Faker.instance().rickAndMorty().quote());
        paragraph.add(Faker.instance().robin().quote());
        paragraph.add(Faker.instance().shakespeare().hamletQuote());
        paragraph.add(Faker.instance().shakespeare().asYouLikeItQuote());
        paragraph.add(Faker.instance().shakespeare().kingRichardIIIQuote());
        paragraph.add(Faker.instance().shakespeare().romeoAndJulietQuote());
        paragraph.add(Faker.instance().twinPeaks().quote());
        paragraph.add(Faker.instance().witcher().quote());
        paragraph.add(Faker.instance().yoda().quote());
        return paragraph;
    }

    public static String paragraph() {
        return String.join(" ", paragraphs());
    }

    public static String sentence() {
        List<String> sentences = paragraphs();
        return sentences.get(new Random().nextInt(sentences.size()));
    }
}
