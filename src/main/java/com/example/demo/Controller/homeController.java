package com.example.demo.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.Entity.*;
import com.example.demo.Validatoin.OrderDto;
import com.example.demo.service.adminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.homService;

import ch.qos.logback.core.model.Model;

@Controller
@RequestMapping("/")
public class homeController {

    private final homService service;

    private final adminService aservice;

    public homeController(homService service, adminService aservice) {
        this.service = service;
        this.aservice = aservice;
    }

    @GetMapping({"/home", "/index", "/"})
    public String home(org.springframework.ui.Model model, HttpSession session, @ModelAttribute("currentUrl") String currentUrl) {
        Pageable pageable = PageRequest.of(0, 8);
        List<product> newProducts = aservice.findTop8ByOrderByAddDateDesc(pageable);
        List<product> topProducts = aservice.findTop8ByOrderByReviewsCountDesc(pageable);
        List<category> categoryList = aservice.getCategories();
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("cat", categoryList != null ? categoryList : new ArrayList<>());
        getProductActivity(model,currentUrl);

        return getCurrentUser(model, session, "home");
    }

    //<editor-fold desc="zalupanomnepox">
    @PostMapping("/home/clearPlacedFlag")
    public String clearOrderPlacedFlag(HttpSession session) {
        session.removeAttribute("favPlaced");
        session.removeAttribute("cartPlaced");
        return "redirect:/home";
    }

    @RequestMapping("/cart")
    public String cart (HttpSession session){
        session.setAttribute("cartPlaced", true);
        return "redirect:/home";
    }

    @RequestMapping("/favorites")
    public String favorites (HttpSession session){
        session.setAttribute("favPlaced", true);
        return "redirect:/home";
    }


    @RequestMapping("/shop/cart")
    public String shopCart (HttpSession session){
        session.setAttribute("cartPlaced", true);
        return "redirect:/shop";
    }

    @RequestMapping("/shop/favorites")
    public String shopFavorites (HttpSession session){
        session.setAttribute("favPlaced", true);
        return "redirect:/shop";
    }

    @PostMapping("/shop/clearPlacedFlag")
    public String clearShopPlacedFlag(HttpSession session) {
        session.removeAttribute("favPlaced");
        session.removeAttribute("cartPlaced");
        return "redirect:/shop";
    }

    //</editor-fold>

    @GetMapping("/login")
    public String login(org.springframework.ui.Model model, HttpSession session, @ModelAttribute("currentUrl") String currentUrl) {
        session.removeAttribute("favPlaced");
        session.removeAttribute("cartPlaced");
        return getCurrentUser(model, session, "login");
    }

    @GetMapping("/signup")
    public String signup(org.springframework.ui.Model model, HttpSession session, @ModelAttribute("currentUrl") String currentUrl) {
        session.removeAttribute("favPlaced");
        session.removeAttribute("cartPlaced");
        return getCurrentUser(model, session, "register");
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User u, HttpSession session, @RequestParam("image") MultipartFile file) throws IllegalStateException, IOException {
        service.register(u, file);
        session.removeAttribute("favPlaced");
        session.removeAttribute("cartPlaced");
        return "login";
    }

    @GetMapping("/subCategories/{id}")
    public String subCategories(@PathVariable("id") String subId, org.springframework.ui.Model model, HttpSession session,
                                Principal principal, @ModelAttribute("currentUrl") String currentUrl) {
        model.addAttribute("products", aservice.findProductFromSubCategory(subId));
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("categories", session.getAttribute("categories"));
        model.addAttribute("curSubCat", aservice.findSubCategory(subId));
        model.addAttribute("curCat", aservice.findCategoryById(aservice.findCatFromSubCat(subId)));
        return getCurrentUser(model, session, "guest/subCategories");
    }

    //<editor-fold desc="shop">
    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) List<Integer> categoryIds,
                       @RequestParam(required = false) List<String> subCategoryIds,
                       @RequestParam(required = false) List<String> priceRanges,
                       org.springframework.ui.Model model, HttpSession session,
                       @ModelAttribute("currentUrl") String currentUrl) {
        List<category> categories = aservice.getCategories();
        model.addAttribute("categories", categories);

        List<subCategory> subCategories = new ArrayList<>();
        Map<Integer, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> subCategoryCounts = new HashMap<>();
        Map<String, Integer> priceCounts = new HashMap<>();

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer cid : categoryIds) {
                subCategories.addAll(aservice.getSubCategoriesByCategoryId(cid));
                categoryCounts.put(cid, aservice.countProductsByCategory(cid));
            }
            model.addAttribute("selectedCategoryIds", categoryIds);
        } else {
            for (category cat : categories) {
                categoryCounts.put(cat.getCid(), aservice.countProductsByCategory(cat.getCid()));
            }
            model.addAttribute("selectedCategoryIds", new ArrayList<>());
        }

        if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
            for (String subId : subCategoryIds) {
                subCategoryCounts.put(subId, aservice.countProductsBySubCategory(subId));
            }
            model.addAttribute("selectedSubCategoryIds", subCategoryIds);
        } else {
            for (subCategory subCat : subCategories) {
                subCategoryCounts.put(subCat.getSubId(), aservice.countProductsBySubCategory(subCat.getSubId()));
            }
            model.addAttribute("selectedSubCategoryIds", new ArrayList<>());
        }

        List<product> products;
        if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
            products = aservice.findProductsBySubCategoryIds(subCategoryIds);
        } else if (categoryIds != null && !categoryIds.isEmpty()) {
            products = aservice.findProductsByCategoryIds(categoryIds);
        } else {
            products = aservice.getAllProduct();
        }

        List<product> filteredProducts = new ArrayList<>(products);
        if (priceRanges != null && !priceRanges.isEmpty() && !priceRanges.contains("all")) {
            filteredProducts = filterProductsByPriceRanges(products, priceRanges);
        }
        for (String priceRange : getPriceRangeKeys()) {
            priceCounts.put(priceRange, countProductsByPriceRange(products, priceRange));
        }
        model.addAttribute("priceCounts", priceCounts);
        model.addAttribute("subCategories", subCategories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("subCategoryCounts", subCategoryCounts);
        model.addAttribute("products", filteredProducts);
        model.addAttribute("priceRanges", priceRanges);
        return getCurrentUser(model, session, "guest/shop");
    }

    private List<String> getPriceRangeKeys() {
        return Arrays.asList("all", "0-100", "100-200", "200-500", "500_plus");
    }

    private List<product> filterProductsByPriceRanges(List<product> products, List<String> priceRanges) {
        List<product> filteredProducts = new ArrayList<>();
        for (product p : products) {
            int price = Integer.parseInt(p.getPrice());
            for (String range : priceRanges) {
                if (range.equals("0-100") && price >= 0 && price <= 100) {
                    filteredProducts.add(p);
                } else if (range.equals("100-200") && price > 100 && price <= 200) {
                    filteredProducts.add(p);
                } else if (range.equals("200-500") && price > 200 && price <= 500) {
                    filteredProducts.add(p);
                } else if (range.equals("500_plus") && price > 500) {
                    filteredProducts.add(p);
                }
            }
        }
        return filteredProducts;
    }

    private int countProductsByPriceRange(List<product> products, String priceRange) {
        int count = 0;
        for (product p : products) {
            int price = Integer.parseInt(p.getPrice());
            if (priceRange.equals("0-100") && price >= 0 && price <= 100) {
                count++;
            } else if (priceRange.equals("100-200") && price > 100 && price <= 200) {
                count++;
            } else if (priceRange.equals("200-500") && price > 200 && price <= 500) {
                count++;
            } else if (priceRange.equals("500_plus") && price > 500) {
                count++;
            } else if (priceRange.equals("all")) {
                count++;
            }
        }
        return count;
    }
    //</editor-fold>


    @GetMapping("/contact")
    public String contact(org.springframework.ui.Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, "guest/contact");
    }

    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
    }

    @GetMapping("/detail/{id}")
    public String productDetails(@PathVariable("id") Long pId, org.springframework.ui.Model model,
                                 HttpSession session,
                                 @ModelAttribute("currentUrl") String currentUrl) {

        model.addAttribute("products", aservice.getAllProductShuffled());
        model.addAttribute("product", aservice.findProductById(pId));
        model.addAttribute("category", aservice.findCategoryByProductId(pId));
        model.addAttribute("subCategory", aservice.findSubCategoryByProductId(pId));
        model.addAttribute("reviews", aservice.getReviewsByProductId(pId));

        return getCurrentUser(model, session, "guest/detail");
    }

    @GetMapping("/search/{keyword}")
    public String fullSearchProducts(@PathVariable("keyword") String keyword,
                                     org.springframework.ui.Model model,
                                     HttpSession session,
                                     @ModelAttribute("currentUrl") String currentUrl) {
        List<product> products = aservice.findByPnameContaining(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("hasProducts", !products.isEmpty());
        return getCurrentUser(model, session, "guest/search_result");
    }


    @GetMapping("/category/{id}")
    public String category(@PathVariable("id") String categoryID, org.springframework.ui.Model model, HttpSession session,
                           @ModelAttribute("currentUrl") String currentUrl) {
        model.addAttribute("products", aservice.getProductsByCategory(categoryID));
        model.addAttribute("categories", session.getAttribute("categories"));
        model.addAttribute("curCat", aservice.findCategoryById(Integer.parseInt(categoryID)));
        return getCurrentUser(model, session, "guest/category_search");
    }

    private String getCurrentUser(org.springframework.ui.Model model, HttpSession session, String redirect) {
            model.addAttribute("categories", aservice.getCategories());
            session.setAttribute("categories", aservice.getCategories());
            return redirect;
    }

    public void getProductActivity(org.springframework.ui.Model model, @ModelAttribute("currentUrl") String currentUrl){
        model.addAttribute("currentUrl", currentUrl);
    }
}




