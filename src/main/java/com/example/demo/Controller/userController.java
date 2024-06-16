package com.example.demo.Controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.Entity.*;
import com.example.demo.Validatoin.OrderDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.adminService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/USER")
public class userController {

    private final adminService aservice;

    public userController(adminService aservice) {
        this.aservice = aservice;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session, Principal principal, @ModelAttribute("currentUrl") String currentUrl) {
        Pageable pageable = PageRequest.of(0, 8);
        List<product> newProducts = aservice.findTop8ByOrderByAddDateDesc(pageable);
        List<product> topProducts = aservice.findTop8ByOrderByReviewsCountDesc(pageable);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("newProducts", newProducts);
        getProductActivity(model,principal,currentUrl);
        return getCurrentUser(model, session, principal, "user_home");
    }

    @PostMapping("/home/clearOrderPlacedFlag")
    public String clearOrderPlacedFlag(HttpSession session) {
        session.removeAttribute("orderPlaced");
        return "redirect:/USER/home";
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

        model.addAttribute("products", aservice.getAllProductShuffled());
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

    @PostMapping("/cart/apply-promo")
    public String applyPromoCode(@RequestParam("promoCode") String promoCode, Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        aservice.applyPromoCode(currentUser.getId(), promoCode);
        return "redirect:/USER/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model,
                       HttpSession session,
                       Principal principal,
                       @ModelAttribute("currentUrl") String currentUrl,
                       @RequestParam(required = false) String promoCode) {
        User currentUser = aservice.findByEmail(principal.getName());
        Cart cart = aservice.getCartList(currentUser.getId());

        int totalSum = cart.getItems().stream().mapToInt(CartItem::getPrice).sum();
        int shippingCost = totalSum > 1000 || totalSum <= 0 ? 0 : 100;

        double discountPercentage = 0;
        if (cart.getPromoCode() != null) {
            discountPercentage = cart.getPromoCode().getDiscountPercentage();
        }

        double discountAmount = totalSum * discountPercentage / 100;
        double finalTotal = totalSum - discountAmount + shippingCost;

        Cart cartList = aservice.getCartList(currentUser.getId());

        model.addAttribute("cartList", cartList != null ? cartList : new Cart());
        model.addAttribute("user", currentUser);
        model.addAttribute("totalSum", totalSum);
        model.addAttribute("shippingCost", shippingCost);
        model.addAttribute("discountPercentage", discountPercentage);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalTotal", finalTotal);
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

    @PostMapping("/cart/update/increase/{userId}/{productId}")
    public String increaseCart(@PathVariable int userId, @PathVariable Long productId) {
        System.out.println("Action: increase");
        System.out.println("Product ID: " + productId);
        System.out.println("User ID: " + userId);

        aservice.increaseQuantity(userId, productId);
        return "redirect:/USER/cart";
    }

    @PostMapping("/cart/update/decrease/{userId}/{productId}")
    public String decreaseCart(@PathVariable int userId, @PathVariable Long productId) {
        System.out.println("Action: decrease");
        System.out.println("Product ID: " + productId);
        System.out.println("User ID: " + userId);

        aservice.decreaseQuantity(userId, productId);
        return "redirect:/USER/cart";
    }


    @GetMapping("/checkout")
    public String checkout(Model model,
                           HttpSession session,
                           Principal principal,
                           @ModelAttribute("currentUrl") String currentUrl,
                           @RequestParam(required = false) String promoCode) {
        User currentUser = aservice.findByEmail(principal.getName());
        Cart cart = aservice.getCartList(currentUser.getId());

        int totalSum = cart.getItems().stream().mapToInt(CartItem::getPrice).sum();
        int shippingCost = totalSum > 1000 ? 0 : 100;

        double discountPercentage = 0;
        if (cart.getPromoCode() != null) {
            discountPercentage = cart.getPromoCode().getDiscountPercentage();
        }

        double discountAmount = totalSum * discountPercentage / 100;
        double finalTotal = totalSum - discountAmount + shippingCost;

        Cart cartList = aservice.getCartList(currentUser.getId());

        model.addAttribute("cartList", cartList != null ? cartList : new Cart());
        model.addAttribute("user", currentUser);
        model.addAttribute("totalSum", totalSum);
        model.addAttribute("shippingCost", shippingCost);
        model.addAttribute("discountPercentage", discountPercentage);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("orderDto", new OrderDto());
        return getCurrentUser(model, session, principal, "checkout");
    }

    @PostMapping("/order/place")
    public String placeOrder(Principal principal,
                             @Valid @ModelAttribute("orderDto") OrderDto orderDto,
                             BindingResult bindingResult,
                             @RequestParam int totalSum,
                             @RequestParam int shippingCost,
                             @RequestParam double discountPercentage,
                             @RequestParam double discountAmount,
                             @RequestParam double finalTotal,
                             Model model,
                             HttpSession session) {

        if (bindingResult.hasErrors()) {
            User currentUser = aservice.findByEmail(principal.getName());
            Cart cart = aservice.getCartList(currentUser.getId());

            model.addAttribute("cartList", cart != null ? cart : new Cart());
            model.addAttribute("user", currentUser);
            model.addAttribute("totalSum", totalSum);
            model.addAttribute("shippingCost", shippingCost);
            model.addAttribute("discountPercentage", discountPercentage);
            model.addAttribute("discountAmount", discountAmount);
            model.addAttribute("finalTotal", finalTotal);

            return getCurrentUser(model, session, principal, "checkout");
        }

        User currentUser = aservice.findByEmail(principal.getName());
        Cart cart = aservice.getCartList(currentUser.getId());

        List<OrderItem> items = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        aservice.placeOrder(currentUser.getId(), orderDto.getFirstName(), orderDto.getLastName(), orderDto.getPhone(), orderDto.getAddress(), orderDto.getCity(), orderDto.getRegion(), orderDto.getZipCode(), orderDto.getPaymentMethod(), totalSum, shippingCost, discountPercentage, discountAmount, finalTotal, items);

        aservice.clearCart(currentUser.getId());

        session.setAttribute("orderPlaced", true);

        return "redirect:/USER/home";
    }

    @GetMapping("/order/confirmation")
    public String orderConfirmation(Model model, Principal principal) {
        User currentUser = aservice.findByEmail(principal.getName());
        model.addAttribute("user", currentUser);
        return "order_confirmation";
    }

    @GetMapping("/search/{keyword}")
    public String fullSearchProducts(@PathVariable("keyword") String keyword,
                                     Model model,
                                     Principal principal,
                                     HttpSession session,
                                     @ModelAttribute("currentUrl") String currentUrl) {
        List<product> products = aservice.findByPnameContaining(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("hasProducts", !products.isEmpty());
        getProductActivity(model, principal, currentUrl);
        return getCurrentUser(model, session, principal, "search_result");
    }


    @GetMapping("/category/{id}")
    public String category(@PathVariable("id") String categoryID, Model model, HttpSession session,
                                Principal principal, @ModelAttribute("currentUrl") String currentUrl) {
        model.addAttribute("products", aservice.getProductsByCategory(categoryID));
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("categories", session.getAttribute("categories"));
        model.addAttribute("curCat", aservice.findCategoryById(Integer.parseInt(categoryID)));
        getProductActivity(model, principal, currentUrl);
        return getCurrentUser(model, session, principal, "category_search");
    }

    public void getProductActivity(Model model, Principal principal,@ModelAttribute("currentUrl") String currentUrl){
        User currentUser = aservice.findByEmail(principal.getName());
        Favorite favoriteList = aservice.getFavoriteList(currentUser.getId());
        Cart cartList = aservice.getCartList(currentUser.getId());
        model.addAttribute("favoriteList", favoriteList != null ? favoriteList.getProducts() : new ArrayList<>());
        model.addAttribute("cartList", cartList != null ? cartList : new Cart());
        model.addAttribute("currentUrl", currentUrl);
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
            User currentUser = aservice.findByEmail(principal.getName());
            Favorite favoriteList = aservice.getFavoriteList(currentUser.getId());
            Cart cartList = aservice.getCartList(currentUser.getId());
            model.addAttribute("user", user);
            model.addAttribute("likes", favoriteList != null ? favoriteList.getProducts() : new ArrayList<>());
            model.addAttribute("corz", cartList != null ? cartList.getItems() : new ArrayList<>());
            session.setAttribute("user", user);
            model.addAttribute("categories", aservice.getCategories());
            session.setAttribute("categories", aservice.getCategories());
            return redirect;
        }
    }
}

