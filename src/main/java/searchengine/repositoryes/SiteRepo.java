package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.config.Site;
@Repository
public interface SiteRepo extends JpaRepository<Site,Integer> {
}
