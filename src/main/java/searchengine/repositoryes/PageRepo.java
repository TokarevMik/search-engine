package searchengine.repositoryes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

import java.util.List;

@Repository
public interface PageRepo extends CrudRepository<Page,Integer> {
    List<Page> findDistinctByPath(String path);
    @Override
    void deleteAll();
}
