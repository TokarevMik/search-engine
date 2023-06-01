package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;

@Repository
public interface IndexRepo extends JpaRepository<Index, Integer> {
    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS `index` " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "page_id INT NOT NULL, " +
            "lemma_id INT NOT NULL, " +
            "`rank` FLOAT NOT NULL, " +
            "PRIMARY KEY (id)" +
            ")"
            , nativeQuery = true)
    void createTableIndex();
}
