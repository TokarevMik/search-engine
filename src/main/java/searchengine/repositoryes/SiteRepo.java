package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;
import searchengine.model.Status;

import java.util.Date;
import java.util.List;

@Repository
public interface SiteRepo extends JpaRepository<Site,Integer> {
    @Override
    void deleteAll();
    @Modifying
    @Query("UPDATE Site s SET s.status = :status, s.status_time = :status_time  WHERE s.id = :id")
    void changeStatus(long id, Status status, Date status_time);
}
