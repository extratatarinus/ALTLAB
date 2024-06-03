package com.example.demo.Controller;

import java.security.Principal;
import java.util.*;

import com.example.demo.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.adminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/USER")
public class userController {

    private final adminService aservice;

    public userController(adminService aservice) {
        this.aservice = aservice;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "user_home");
    }

    @GetMapping("/subCategories/{id}")
    public String subCategories(@PathVariable("id") String subId, Model model, HttpSession session) {
        model.addAttribute("products", aservice.findProductFromSubCategory(subId));
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("categories", session.getAttribute("categories"));
        model.addAttribute("curSubCat", aservice.findSubCategory(subId));
        model.addAttribute("curCat", aservice.findCategoryById(aservice.findCatFromSubCat(subId)));
        return "subCategories";
    }

    //<editor-fold desc="shop">
    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) List<Integer> categoryIds,
                       @RequestParam(required = false) List<String> subCategoryIds,
                       @RequestParam(required = false) List<String> priceRanges,
                       Model model, HttpSession session, Principal principal) {
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
        model.addAttribute("priceRanges", priceRanges);  // Add this line
        return getCurrentUser(model, session, principal, "shop");
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

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "cart");
    }

    private String getCurrentUser(Model model, HttpSession session, Principal principal, String redirect) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = aservice.findByEmail(principal.getName());
        session.setAttribute("user", user);
        if ("Unverified".equals(user.getStatus())) {
            model.addAttribute("user", user);
            return "login";
        } else {
            model.addAttribute("user", user);
            session.setAttribute("user", user);
            model.addAttribute("categories", aservice.getCategories());
            session.setAttribute("categories", aservice.getCategories());
            return redirect;
        }
    }

    @GetMapping("/favorites")
    public String favorites(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "favorites");
    }

    @GetMapping("/detail/{id}")
    public String productDetails(@PathVariable("id") String pId, Model model, Principal principal, HttpSession session) {

        model.addAttribute("products", aservice.getAllProduct());
        model.addAttribute("product", aservice.findProductById(pId));
        model.addAttribute("category", aservice.findCategoryByProductId(pId));
        model.addAttribute("subCategory", aservice.findSubCategoryByProductId(pId));
        model.addAttribute("reviews", aservice.getReviewsByProductId(pId));
        model.addAttribute("currentUser", aservice.findByEmail(principal.getName()));

        return getCurrentUser(model, session, principal, "detail");
    }

    @PostMapping("/detail/{id}/addReview")
    public String addReviewToProduct(@PathVariable("id") String pId, @RequestParam String text, Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        Reviews review = new Reviews();
        review.setText(text);
        review.setAuthor(currentUser);

        aservice.addReviewToProduct(pId, review);
        return "redirect:/USER/detail/" + pId;
    }
}

