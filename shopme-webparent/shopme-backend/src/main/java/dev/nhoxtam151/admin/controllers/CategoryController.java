package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.services.CategoryService;
import dev.nhoxtam151.shopmecommon.models.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public String getCategoriesPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "categories/category";
    }

    @GetMapping("/new")
    public String createNewCategory(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("parentCategories", categoryService.getCategoryFormDTO());
        return "categories/category_form";
    }


    @PostMapping("/save")
    public String saveCategory(Category category) {
        return null;
    }

}
