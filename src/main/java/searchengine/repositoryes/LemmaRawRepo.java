package searchengine.repositoryes;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LemmaRawRepo {
    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS lemma_raw " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "path TEXT NOT NULL, " +
            "lemma VARCHAR(255) NOT NULL, " +
            "ratio_rank FLOAT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableLemmaRaw();
}
