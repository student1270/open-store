//package ru.gb.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.gb.model.Category;
//import ru.gb.service.CategoryService;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/home")
//public class HomeController {
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @GetMapping
//    public ResponseEntity<?> homePage() {
//        List<Category> categories = categoryService.findAll();
//        Map<String, Object> response = new HashMap<>();
//        response.put("title", "OpenStore - Bosh sahifa");
//        response.put("categories", categories);
//        return ResponseEntity.ok(response);
//    }
//}


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
