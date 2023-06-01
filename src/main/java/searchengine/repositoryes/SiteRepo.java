package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;
import searchengine.model.Status;

import java.util.Date;
import java.util.List;

@Repository
public interface SiteRepo extends JpaRepository<Site,Integer> {
    @Override
    void deleteAll();
    @Modifying
    @Query("UPDATE Site s SET s.status = :status, s.status_time = :status_time, last_error = ?4  WHERE s.url = :url")
    void changeStatus(String url, Status status, Date status_time, String last_error);
//@Query(value = "UPDATE site SET last_error = ?2 WHERE url = ?1", nativeQuery = true)


    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE site AUTO_INCREMENT = 1"
            , nativeQuery = true)
    int resetAutoIncrement();
    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS page, `index`, lemma, lemma_raw", nativeQuery = true)
    int dropTables();

}
