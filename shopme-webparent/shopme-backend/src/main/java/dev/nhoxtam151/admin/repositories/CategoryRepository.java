package dev.nhoxtam151.admin.repositories;

import dev.nhoxtam151.shopmecommon.models.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    //    @Query("""
//            select c from Category c LEFT JOIN FETCH c.children
//            """)
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "children"
            })
    List<Category> findAll();
}
