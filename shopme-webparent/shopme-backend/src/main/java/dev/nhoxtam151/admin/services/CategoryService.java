package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.admin.models.CategoryFormDTO;
import dev.nhoxtam151.admin.models.SirvBody;
import dev.nhoxtam151.admin.models.SirvUrl;
import dev.nhoxtam151.admin.repositories.CategoryRepository;
import dev.nhoxtam151.admin.utils.ImageUtils;
import dev.nhoxtam151.shopmecommon.models.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService implements ApplicationContextAware {
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private final SirvBody sirvBody;
    private final SirvUrl sirvUrl;
    private final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final ImageUtils imageUtils;
    private ApplicationContext applicationContext;

    public CategoryService(CategoryRepository categoryRepository, RestTemplate restTemplate, SirvBody sirvBody, SirvUrl sirvUrl, @Qualifier("sirvImageUtils") ImageUtils imageUtils) {
        this.categoryRepository = categoryRepository;
        this.restTemplate = restTemplate;
        this.sirvBody = sirvBody;
        this.sirvUrl = sirvUrl;
        this.imageUtils = imageUtils;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<CategoryFormDTO> getCategoryFormDTO() {
        List<CategoryFormDTO> result = new ArrayList<>();
        List<Category> categoriesInDB = this.findAll();
        List<Category> rootCategories = categoriesInDB.stream().filter(category -> category.getParent() == null).toList();
        for (Category category : rootCategories) {
            //System.out.println(category.getName());
            result.add(new CategoryFormDTO(category.getId(), category.getName()));
            printChildrenCategory(result, category, 1);
        }
        return result;
    }

    private void printChildrenCategory(List<CategoryFormDTO> categoryFormDTOS, Category parent, int subLevel) {
        int level = subLevel + 1;
        Set<Category> children = parent.getChildren();
        for (Category category : children) {
            String name = "";
            for (int i = 0; i < level; i++) {
                //System.out.print("-");
                name += "-";
            }
            name += category.getName();
            categoryFormDTOS.add(new CategoryFormDTO(category.getId(), name));
            //System.out.println(category.getName());
            printChildrenCategory(categoryFormDTOS, category, level);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
