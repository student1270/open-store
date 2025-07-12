package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.model.Category;
import ru.gb.service.CategoryService;

import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;



    @GetMapping({"/", "/home"})
    public String homePage(Model model) {
        model.addAttribute("title", "OpenStore - Bosh sahifa");
        model.addAttribute("categories", categoryService.findAll());
        return "home";
    }
}
/*
@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryService.findAll();
    }
}
*/
