package ru.gb.controllers;

//@RestController
//@RequestMapping("/api/home")
public class ProductRetrievalRestController {

//    @Autowired
//    private ProductRetrievalService productRetrievalService;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @GetMapping("/category/{categoryId}")
//    public ResponseEntity<?> getProductsByCategory(
//            @PathVariable Long categoryId,
//            @RequestParam(value = "sort", required = false) String sort
//    ) {
//        try {
//            List<Product> products;
//
//            if ("arzon".equals(sort)) {
//                products = productRetrievalService.findProductsByCategoryAndPriceAsc(categoryId);
//            } else if ("qimmat".equals(sort)) {
//                products = productRetrievalService.findProductsByCategoryAndPriceDesc(categoryId);
//            } else {
//                products = productRetrievalService.findProductsByCategory(categoryId);
//            }
//
//            Category category = categoryService.findById(categoryId);
//            List<Category> categories = categoryService.findAll();
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("products", products);
//            response.put("category", category);
//            response.put("sort", sort);
//            response.put("categories", categories);
//
//            return ResponseEntity.ok(response);
//
//        } catch (IllegalArgumentException e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Noto'g'ri kategoriya ID si.");
//            response.put("products", List.of());
//            response.put("category", null);
//            response.put("sort", null);
//            response.put("categories", categoryService.findAll());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/category")
//    public ResponseEntity<?> redirectToHome() {
//        return ResponseEntity.status(302).header("Location", "/api/home").build();
//    }
}