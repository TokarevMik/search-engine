package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;

@Repository
public interface LemmaRepo extends JpaRepository<Lemma, Integer> {
    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS lemma " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "lemma VARCHAR(255) NOT NULL, " +
            "frequency INT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableLemma();
}
