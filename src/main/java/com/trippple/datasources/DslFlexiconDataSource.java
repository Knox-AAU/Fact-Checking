package com.trippple.datasources;

import com.trippple.models.FlexWord;
import com.trippple.models.Flexion;
import com.trippple.models.WordClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DslFlexiconDataSource implements IFlexionDataSource {
    private final File file;

    public DslFlexiconDataSource(File file) throws IOException {
        this.file = file;
        if (!file.canRead()) {
            throw new IOException("File '" + file.getAbsolutePath() + "' is read protected.");
        }
    }

    private WordClass wordClassAbbreviationConverter(String abbr) throws IOException {
        return switch (abbr) {
            case "S" -> WordClass.NOUN;
            case "A" -> WordClass.ADJECTIVE;
            case "V" -> WordClass.VERB;
            case "D" -> WordClass.ADVERB;
            case "F" -> WordClass.ABBREVIATION;
            case "K" -> WordClass.CONJUNCTION;
            case "L" -> WordClass.ONOMATOPOEICWORD;
            case "O" -> WordClass.PRONOUN;
            case "P" -> WordClass.PROPERNOUN;
            case "I" -> WordClass.PREFIX;
            case "Æ" -> WordClass.PREPOSITION;
            case "T" -> WordClass.NUMERAL;
            case "U" -> WordClass.INTERJECTION;
            case "X" -> WordClass.UNIDENTIFIED;
            default -> throw new IOException("Unknown word class abbreviation '" + abbr + "'");
        };
    }

    @Override
    public List<Flexion> getAll() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Flexion> flexions = new ArrayList<>();
        List<String> lineBuffer = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("*")) {
                if (lineBuffer.size() > 0) {
                    var parsedFlexion = parseFlexion(lineBuffer);
                    if (!(parsedFlexion.getWordClass() == WordClass.PREFIX || parsedFlexion.getWordClass() == WordClass.ABBREVIATION)) {
                        flexions.add(parsedFlexion);
                    }
                }
                lineBuffer.clear();
            } else {
                lineBuffer.add(line);
            }
        }
        var parsedFlexion = parseFlexion(lineBuffer);
        if (!(parsedFlexion.getWordClass() == WordClass.PREFIX || parsedFlexion.getWordClass() == WordClass.ABBREVIATION)) {
            flexions.add(parsedFlexion);
        }
        return flexions;
    }

    private Flexion parseFlexion(List<String> lines) {
        String word = lines.get(0);
        WordClass wc = null;
        try {
            wc = wordClassAbbreviationConverter(lines.get(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<FlexWord> words = new ArrayList<>();
        words.add(new FlexWord(word, 0));
        for (String line : lines.subList(2, lines.size())) {
            words.add(parseWordLine(line));
        }
        return new Flexion(words, wc);
    }

    private FlexWord parseWordLine(String line) {
        String[] split = line.split("\t");
        return new FlexWord(split[1], Integer.parseInt(split[0]));
    }
}
