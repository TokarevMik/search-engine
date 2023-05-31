package searchengine.repositoryes;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

import java.util.List;

@Repository
public interface PageRepo extends CrudRepository<Page,Integer> {
    List<Page> findDistinctByPath(String path);
    @Override
    void deleteAll();
    @Modifying
    @Transactional
    @Query(value = "CREATE TABLE IF NOT EXISTS page " +
            "(" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "site_id INT NOT NULL, " +
            "path TEXT NOT NULL, " +
            "code INT NOT NULL, " +
            "content MEDIUMTEXT NOT NULL, " +
            "FULLTEXT KEY path (path), " +
            "PRIMARY KEY (id)" +
            ")",
            nativeQuery = true)
    void createTablePage();
}
