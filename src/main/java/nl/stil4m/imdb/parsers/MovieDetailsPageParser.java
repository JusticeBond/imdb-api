package nl.stil4m.imdb.parsers;

import nl.stil4m.imdb.domain.MovieDetails;
import nl.stil4m.imdb.util.ElementUtil;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MovieDetailsPageParser implements Parser<MovieDetails> {

    private static final String IMAGE = "MovieDetailsPageParser.image";
    private static final String CATEGORIES = "MovieDetailsPageParser.categories";
    private static final String DESCRIPTION = "MovieDetailsPageParser.description";
    private static final String RATING = "MovieDetailsPageParser.rating";
    private static final String WRITERS = "MovieDetailsPageParser.writers";
    private static final String DIRECTORS = "MovieDetailsPageParser.directors";
    private static final String YEAR = "MovieDetailsPageParser.year";
    private static final String NAME_TITLE_HEADER = "MovieDetailsPageParser.nameTitleHeader";
    private static final String NAME_TITLE_EXTRA = "MovieDetailsPageParser.nameTitleExtra";
    private static final String NAME_TITLE_NORMAL = "MovieDetailsPageParser.nameTitleNormal";
    private static final String STARS = "MovieDetailsPageParser.stars";

    private final ElementUtil elementUtil;
    private final Properties properties;


    public MovieDetailsPageParser(ElementUtil elementUtil, Properties properties) {
        this.elementUtil = elementUtil;
        this.properties = properties;
    }

    @Override
    public MovieDetails parse(Element document) {
        String movieName = parseMovieName(document);
        Integer year = parseMovieYear(document);
        String description = parseDescription(document);
        Double rating = parseRating(document);
        List<String> directors = parseDirectors(document);
        List<String> writers = parseWriters(document);
        List<String> stars = parseStars(document);
        List<String> categories = parseCategories(document);
        String image = parseImage(document);
        return new MovieDetails(movieName, year, description, rating, directors, writers, stars, categories, image);
    }

    private String parseImage(Element document) {
        return document.select(properties.get(IMAGE).toString()).attr("src");
    }

    private List<String> parseCategories(Element document) {
        List<String> answer = new ArrayList<>();
        Collections.addAll(answer, document.select(properties.get(CATEGORIES).toString()).text().split(" "));
        return answer;
    }

    private String parseDescription(Element document) {
        return document.select(properties.get(DESCRIPTION).toString()).text();
    }

    private Double parseRating(Element document) {
        Elements elements = document.select(properties.get(RATING).toString());
        if (elements.text().trim().length() > 0) {
            return Double.parseDouble(elements.text());
        } else {
            return null;
        }
    }

    private List<String> parseWriters(Element document) {
        return elementUtil.allTextForElements(document.select(properties.get(WRITERS).toString()));
    }

    private List<String> parseDirectors(Element document) {
        return elementUtil.allTextForElements(document.select(properties.get(DIRECTORS).toString()));
    }

    private List<String> parseStars(Element document) {
        return elementUtil.allTextForElements(document.select(properties.get(STARS).toString()));
    }

    private String parseMovieName(Element document) {
        Elements header = document.select(properties.get(NAME_TITLE_HEADER).toString());
        if (header.select(properties.get(NAME_TITLE_EXTRA).toString()).size() > 0) {
            String text = header.select(properties.get(NAME_TITLE_EXTRA).toString()).get(0).ownText();
            if (text.startsWith("\"")) {
                text = text.substring(1, text.length() - 1);
            }
            return text;
        }
        return header.select(properties.get(NAME_TITLE_NORMAL).toString()).text();
    }

    private Integer parseMovieYear(Element document) {
        String yearString = document.select(properties.get(YEAR).toString()).text();
        yearString = yearString.substring(1, 5);
        return Integer.parseInt(yearString);
    }

}
