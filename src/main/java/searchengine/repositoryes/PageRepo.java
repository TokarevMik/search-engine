package searchengine.repositoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.config.Page;
import searchengine.config.Site;

import java.util.List;

@Repository
public interface PageRepo extends CrudRepository<Page,Integer> {
    List<Page> findDistinctByPath(String path);
}
