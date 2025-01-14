package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.admin.models.CategoryFormDTO;
import dev.nhoxtam151.admin.models.SirvBody;
import dev.nhoxtam151.admin.models.SirvUrl;
import dev.nhoxtam151.admin.repositories.CategoryRepository;
import dev.nhoxtam151.admin.utils.TokenImageUtils;
import dev.nhoxtam151.shopmecommon.models.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SirvUrl sirvUrl;
    private final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final TokenImageUtils imageUtils;

    public CategoryService(CategoryRepository categoryRepository, RestTemplate restTemplate, SirvBody sirvBody, SirvUrl sirvUrl, @Qualifier("sirvImageUtils") TokenImageUtils imageUtils) {
        this.categoryRepository = categoryRepository;
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
            printChildrenCategory(categoryFormDTOS, category, level);
        }
    }

    public Category save(Category category, MultipartFile categoryPhoto) {
        if (!categoryPhoto.isEmpty() && category.getPhoto() == null) {
            category.setPhoto(category.getAlias() + "_image");
            String url = sirvUrl.getUrl().get("post.category") + category.getPhoto();
            try {
                log.info("Uploaded category image: {}", category.getPhoto());
                imageUtils.uploadImage(url, categoryPhoto.getBytes());
            } catch (IOException e) {
                log.error("Failed to upload image to Sirv: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return categoryRepository.save(category);
    }

}
