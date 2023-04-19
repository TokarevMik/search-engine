package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {
    @Override
    public Map<String, Integer> LemmaMap(String s) throws IOException {
        Map<String, Integer> countMap = new HashMap<>();
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();
        String[] words = stringSplitter(s);
        List<String> wordBaseForms;
        for (String st : words) {
            if (st.length() > 2) {
                if (isCyrillic(st)) {
                    wordBaseForms = russianLuceneMorphology.getNormalForms(st);
                    for (String bf : wordBaseForms) {
                        if (isNotSpecial(bf)) {
                            Integer c = countMap.get(bf);
                            if (c == null) {
                                countMap.put(bf, 1);
                            } else {
                                countMap.put(bf, ++c);
                            }
                        }
                    }
                } else {
                    wordBaseForms = englishLuceneMorphology.getNormalForms(st);
                    for (String bf : wordBaseForms) {
                        Integer c = countMap.get(bf);
                        if (c == null) {
                            countMap.put(bf, 1);
                        } else {
                            countMap.put(bf, ++c);
                        }
                    }
                }
            }
        }
        return countMap;
    }
    public static boolean isNotSpecial(String s) throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorphology.getMorphInfo(s);
        for (String bf : wordBaseForms) {
            if (bf.contains("МЕЖД") || bf.contains("СОЮЗ") || bf.contains("ЧАСТ") || bf.contains("ПРЕДЛ")) {
                return false;
            }
        }
        return true;  //удаление служебных слов
    }

    public static String[] stringSplitter(String s) {
        if (!s.isEmpty()) {
            s = stringCleaner(s);
        }
        s = s.toLowerCase();
        return s.split(" ");
    }

    public static String stringCleaner(String s) {
        s = s.replaceAll("[a-zA-Z]+\\W[а-яА-Я]+", "");
        s = s.replaceAll("[a-zA-Z]+[а-яА-Я]+", "");
        s = s.replaceAll("&nbsp", " ");
        s = s.replaceAll("&copy;'", "");
        s = s.replaceAll("[\\s-\\s]", " ");
        s = s.replaceAll("[^А-Яа-яA-Za-z\\s]", ""); //удаление лишних символов
        s = s.replaceAll("\\s{2,}", " "); // удаление лишних пробелов
        return s;
    }

    static public boolean isCyrillic(String s) {
        char[] charArr = s.toCharArray();
        for (char ch : charArr) {
            if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.BASIC_LATIN)) {
                return false;
            }
        }
        return true;
    }
}
