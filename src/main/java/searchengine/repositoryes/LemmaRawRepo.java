package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaRaw;

@Repository
public interface LemmaRawRepo extends JpaRepository<LemmaRaw, Integer> {
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
