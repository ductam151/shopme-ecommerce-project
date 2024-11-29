package dev.nhoxtam151.admin;

import com.vladmihalcea.sql.SQLStatementCountValidator;
import dev.nhoxtam151.admin.repositories.CategoryRepository;
import dev.nhoxtam151.shopmecommon.models.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("create a new root category success")
    public void testCreateRootCategory() {
        Category category = new Category("Electronics", "Electronics");
        Category save = categoryRepository.save(category);
        assertThat(save.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
        Category parentCategory = new Category(8L);
        Category c1 = new Category("iPhone", "iPhone", parentCategory);
        Category saved = categoryRepository.save(c1);
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategoryAndChildren() {
        Category computers = categoryRepository.findById(1L).orElseThrow(() -> new RuntimeException("Can not find category with this id"));
        Set<Category> children = computers.getChildren();
        children.forEach(category -> System.out.println("category.getName() = " + category.getName()));
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<Category> rootCategories = categories.stream().filter(category -> category.getParent() == null).toList();
        for (Category category : rootCategories) {
            System.out.println(category.getName());
            printChildrenCategory(category, 1);
        }

    }

    private void printChildrenCategory(Category parent, int subLevel) {
        int level = subLevel + 1;
        Set<Category> children = parent.getChildren();
        for (Category category : children) {
            for (int i = 0; i < level; i++) {
                System.out.print("-");
            }
            System.out.println(category.getName());
            printChildrenCategory(category, level);
        }
    }
}
