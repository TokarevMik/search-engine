package searchengine.forTesting;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.services.LemmaService;
import searchengine.services.LemmaServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
public class Lemmitazer {
//    @Autowired


    public static void main(String[] args) throws IOException {
        String sg = "Повторное появление леопарда в Осетии позволяет предположить, " +
                "что леопард постоянно обитает в некоторых " +
                "районах Северного Кавказа.";

                printWords(sg);

    }
    static void printWords(String s) throws IOException {
        LemmaService lemmaService = new LemmaServiceImpl();
       Map<String,Double> sm =  lemmaService.LemmaMap(s);
       sm.forEach((s1, aDouble) -> System.out.println(s1 + " " + aDouble));
    }

}
