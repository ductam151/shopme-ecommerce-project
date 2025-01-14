package dev.nhoxtam151.admin.controllers;

import dev.nhoxtam151.admin.services.CategoryService;
import dev.nhoxtam151.shopmecommon.models.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @ModelAttribute(name = "category")
    public Category category() {
        return new Category();
    }
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
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("parentCategories", categoryService.getCategoryFormDTO());
        return "categories/category_form";
    }


    @PostMapping("/save")
    public String saveCategory(Category category, @RequestParam(name = "image") MultipartFile image) {
        categoryService.save(category, image);
        return "redirect:/categories";
    }

}
