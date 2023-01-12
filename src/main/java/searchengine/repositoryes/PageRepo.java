package searchengine.repositoryes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.config.Page;

import java.util.List;

@Repository
public interface PageRepo extends CrudRepository<Page,Integer> {
    List<Page> findDistinctByPath(String path);
}
