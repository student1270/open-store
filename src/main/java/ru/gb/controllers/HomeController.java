

package ru.gb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ru.gb.service.CategoryService;



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
