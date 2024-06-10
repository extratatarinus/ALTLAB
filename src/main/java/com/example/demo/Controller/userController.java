package com.example.demo.Controller;

import java.security.Principal;
import java.util.*;

import com.example.demo.Entity.*;
import com.example.demo.Repository.userRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private final com.example.demo.Repository.userRepository userRepository;

    public userController(adminService aservice, userRepository userRepository) {
        this.aservice = aservice;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "user_home");
    }

    @GetMapping("/subCategories/{id}")
    public String subCategories(@PathVariable("id") String subId, Model model, HttpSession session,
                                Principal principal, @ModelAttribute("currentUrl") String currentUrl) {
        model.addAttribute("products", aservice.findProductFromSubCategory(subId));
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("categories", session.getAttribute("categories"));
        model.addAttribute("curSubCat", aservice.findSubCategory(subId));
        model.addAttribute("curCat", aservice.findCategoryById(aservice.findCatFromSubCat(subId)));
        getProductActivity(model, principal, currentUrl);
        return getCurrentUser(model, session, principal, "subCategories");
    }

    //<editor-fold desc="shop">
    @GetMapping("/shop")
    public String shop(@RequestParam(required = false) List<Integer> categoryIds,
                       @RequestParam(required = false) List<String> subCategoryIds,
                       @RequestParam(required = false) List<String> priceRanges,
                       Model model, HttpSession session, Principal principal,
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
        getProductActivity(model, principal, currentUrl);
        model.addAttribute("priceCounts", priceCounts);
        model.addAttribute("subCategories", subCategories);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("subCategoryCounts", subCategoryCounts);
        model.addAttribute("products", filteredProducts);
        model.addAttribute("priceRanges", priceRanges);
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
            User currentUser = aservice.findByEmail(principal.getName());
            Favorite favoriteList = aservice.getFavoriteList(currentUser.getId());
            Cart cartList = aservice.getCartList(currentUser.getId());
            model.addAttribute("user", user);
            model.addAttribute("likes", favoriteList != null ? favoriteList.getProducts() : new ArrayList<>());
            model.addAttribute("corz", cartList != null ? cartList.getProducts() : new ArrayList<>());
            session.setAttribute("user", user);
            model.addAttribute("categories", aservice.getCategories());
            session.setAttribute("categories", aservice.getCategories());
            return redirect;
        }
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "checkout");
    }

    @GetMapping("/contact")
    public String contact(Model model, HttpSession session, Principal principal) {
        return getCurrentUser(model, session, principal, "contact");
    }

    @GetMapping("/favorites")
    public String favorites(Model model, HttpSession session, Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        Favorite favoriteList = aservice.getFavoriteList(currentUser.getId());
        model.addAttribute("favoriteList", favoriteList != null ? favoriteList.getProducts() : new ArrayList<>());
        return getCurrentUser(model, session, principal, "favorites");
    }

    @PostMapping("/favorites/add/{productId}")
    public String addProductToFavorites(@PathVariable("productId") Long productId, Principal principal, @RequestParam String returnUrl) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.addProductToFavorites(currentUser.getId(), productId);
        return "redirect:" + returnUrl;
    }

    @ModelAttribute("currentUrl")
    public String getCurrentUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString != null ? "?" + queryString : "");
    }

    @PostMapping("/favorites/remove/{productId}")
    public String removeProductFromFavorites(@PathVariable("productId") Long productId, Principal principal, @RequestParam String returnUrl) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.removeProductFromFavorites(currentUser.getId(), productId);
        return "redirect:" + returnUrl;
    }


    @GetMapping("/detail/{id}")
    public String productDetails(@PathVariable("id") Long pId, Model model,
                                 Principal principal,
                                 HttpSession session,
                                 @ModelAttribute("currentUrl") String currentUrl) {

        model.addAttribute("products", aservice.getAllProduct());
        model.addAttribute("product", aservice.findProductById(pId));
        model.addAttribute("category", aservice.findCategoryByProductId(pId));
        model.addAttribute("subCategory", aservice.findSubCategoryByProductId(pId));
        model.addAttribute("reviews", aservice.getReviewsByProductId(pId));
        model.addAttribute("currentUser", aservice.findByEmail(principal.getName()));
        getProductActivity(model,principal,currentUrl);

        return getCurrentUser(model, session, principal, "detail");
    }

    @PostMapping("/detail/{id}/addReview")
    public String addReviewToProduct(@PathVariable("id") Long pId, @RequestParam String text, Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        Reviews review = new Reviews();
        review.setText(text);
        review.setAuthor(currentUser);

        aservice.addReviewToProduct(pId, review);
        return "redirect:/USER/detail/" + pId;
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session, Principal principal, HttpServletRequest request) {
        User currentUser = aservice.findByEmail(principal.getName());
        model.addAttribute("returnUrl", request.getRequestURI());
        Cart cartList = aservice.getCartList(currentUser.getId());
        model.addAttribute("cartList", cartList != null ? cartList.getProducts() : new ArrayList<>());
        return getCurrentUser(model, session, principal, "cart");
    }

    @PostMapping("/cart/add/{productId}")
    public String addProductToCart(@PathVariable("productId") Long productId, Principal principal, @RequestParam String returnUrl) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.addProductToCart(currentUser.getId(), productId);
        return "redirect:" + returnUrl;
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeProductFromCart(@PathVariable("productId") Long productId, Principal principal, @RequestParam String returnUrl) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.removeProductFromCart(currentUser.getId(), productId);
        return "redirect:" + returnUrl;
    }

    public void getProductActivity(Model model, Principal principal,@ModelAttribute("currentUrl") String currentUrl){
        User currentUser = aservice.findByEmail(principal.getName());
        Favorite favoriteList = aservice.getFavoriteList(currentUser.getId());
        Cart cartList = aservice.getCartList(currentUser.getId());
        model.addAttribute("favoriteList", favoriteList != null ? favoriteList.getProducts() : new ArrayList<>());
        model.addAttribute("cartList", cartList != null ? cartList.getProducts() : new ArrayList<>());
        model.addAttribute("currentUrl", currentUrl);
    }

    @RequestMapping("/cart/updateQuantity/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam String action,
                                 Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.updateProductQuantity(currentUser.getId(), productId, action);
        return "redirect:/USER/cart";
    }


}

