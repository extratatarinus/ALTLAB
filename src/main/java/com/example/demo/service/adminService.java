package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Component
public class adminService {

    private final CartRepository cartrepo;

    private final OrderRepository orepo;

    private final OrderItemRepository oitemrepo;

    private final CartItemRepository cartitemrepo;

    private final PromocodeRepository pcrepo;

    private final favoriteRepository frepo;

    private final reviewsRepository rrepo;

    private final JavaMailSender jms;

    private final userRepository urepo;

    private final catRepository crepo;

    private final productRepository prepo;

    private final sunCatRepository srepo;

    private final brandRepository brepo;

    public adminService(CartRepository cartrepo,
                        PromocodeRepository pcrepo,
                        JavaMailSender jms,
                        userRepository urepo,
                        catRepository crepo,
                        productRepository prepo,
                        sunCatRepository srepo,
                        brandRepository brepo,
                        reviewsRepository rrepo,
                        favoriteRepository frepo,
                        CartItemRepository cartitemrepo,
                        OrderRepository orepo,
                        OrderItemRepository oitemrepo) {
        this.oitemrepo = oitemrepo;
        this.cartrepo = cartrepo;
        this.orepo = orepo;
        this.pcrepo = pcrepo;
        this.cartitemrepo = cartitemrepo;
        this.jms = jms;
        this.urepo = urepo;
        this.crepo = crepo;
        this.prepo = prepo;
        this.srepo = srepo;
        this.brepo = brepo;
        this.rrepo = rrepo;
        this.frepo = frepo;
    }

    public User findByEmail(String email) {
        return urepo.findByEmail(email);
    }

    public void admin_register(User u, MultipartFile file, String role) throws IllegalStateException, IOException {
        User user = new User();
        if (file.isEmpty()) {
            user.setImgPath("profile.jpg");
        } else {
            String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
            String cpath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = cpath.indexOf(search);
            user.setImgPath(cpath.substring(i + search.length()));
            file.transferTo(new File(cpath));
        }
        user.setAddress(u.getAddress());
        user.setEmail(u.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
        user.setPhone(u.getPhone());
        user.setName(u.getName());
        user.setRole(role);
        user.setStatus("Verified");
        urepo.save(user);
    }

    public void add_category(String cname, MultipartFile file) throws IllegalStateException, IOException {
        String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
        category c = new category();
        if (file.isEmpty()) {
            c.setImgPath("");
        } else {
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            c.setImgPath(npath.substring(i + search.length()));
        }

        c.setCname(cname);
        crepo.save(c);
    }

    @Transactional
    public void updateCategory(MultipartFile file, String cname, int cid) throws IllegalStateException, IOException {
        String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";

        if (file.isEmpty()) {
            crepo.updateCategory(cname, "", cid);
        } else {
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            crepo.updateCategory(cname, npath.substring(i + search.length()), cid);
        }
    }

    @Transactional
    public void deleteCategory(int cid) {
        srepo.deleteByCid(cid);
        crepo.deleteById(cid);
    }

    public List<category> getCategories() {
        return crepo.findAll();
    }

    public void add_product(product product, MultipartFile file, String bid) throws IllegalStateException, IOException {
        String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
        String nPath = folderPath + file.getOriginalFilename();
        String search = "image\\";
        int i = nPath.indexOf(search);

        product p = new product();
        Brand b = brepo.findById(bid).get();

        if (file.isEmpty()) {
            p.setImgPath("");
        } else {
            file.transferTo(new File(nPath));
            p.setImgPath(nPath.substring(i + search.length()));
        }
        p.setPid(product.getPid());
        p.setBrand(b);
        p.setPname(product.getPname());
        p.setBrand(product.getBrand());
        p.setDescription(product.getDescription());
        p.setPrice(product.getPrice());
        p.setAddDate(LocalDate.now());
        p.setStatus("Unsold");
        prepo.save(p);
    }

    public List<subCategory> subCategories(int cid) {
        Optional<category> cop = crepo.findById(cid);
        category c = cop.get();
        return c.getSubCategory();
    }

    public void add_subCategory(String subName, int cid, MultipartFile file) throws IllegalStateException, IOException {
        subCategory s = new subCategory();
        category c = crepo.findById(cid).get();

        if (file.isEmpty()) {
            s.setImgPath("");
        } else {
            String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            s.setImgPath(npath.substring(i + search.length()));
        }

        if (subCategories(cid).isEmpty()) {
            String new_name = c.getCname().replace(" ", "");
            String Id = new_name + 1;
            s.setSubId(Id);
        } else {
            Optional<subCategory> os = srepo.findLatestSubCategoryByCategoryId(cid);
            String subID = os.get().getSubId();
            int l = c.getCname().length();
            int num = Integer.parseInt(subID.substring(l)) + 1;
            s.setSubId(c.getCname() + num);
        }
        s.setCreatedAt(LocalDateTime.now());
        s.setSubName(subName);
        s.setCategory(c);
        srepo.save(s);
    }

    public subCategory findSubCategory(String sid) {
        return srepo.findById(sid).get();
    }

    @Transactional
    public void updateSubCat(String sname, String id, MultipartFile file) throws IllegalStateException, IOException {
        if (file.isEmpty()) {
            srepo.updateSubCategory(sname, "", id);
        } else {
            String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            srepo.updateSubCategory(sname, (String) npath.substring(i + search.length()), id);
        }
    }

    public List<Brand> getBrands(String subId) {
        return brepo.getBrands(subId);
    }

    public void addBrand(String name, String subId, MultipartFile file) throws IllegalStateException, IOException {
        Brand b = new Brand();
        subCategory sc = srepo.findById(subId).get();
        if (file.isEmpty()) {
            b.setImgPath("");
        } else {
            String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            b.setImgPath(npath.substring(i + search.length()));
        }

        if (getBrands(subId).isEmpty()) {
            String sname = sc.getSubName().replace(" ", "");
            b.setBid(sname + 1);
        } else {
            Brand br = brepo.findLastAddedBrand(subId);
            String lid = br.getBid();
            int l = sc.getSubName().length();
            int num = Integer.parseInt(lid.substring(l)) + 1;
            b.setBid(sc.getSubName() + num);
        }
        b.setCreatedAt(LocalDateTime.now());
        b.setSubCategory(sc);
        b.setBname(name);
        brepo.save(b);
    }

    public Brand findBrandById(String id) {
        return brepo.findById(id).get();
    }

    @Transactional
    public void updateBrand(String bname, MultipartFile file, String bid) throws IllegalStateException, IOException {
        Brand br = findBrandById(bid);
        if (file.isEmpty()) {
            brepo.updateBrand(bname, br.getImgPath(), bid);
        } else {
            String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
            String npath = folderPath + file.getOriginalFilename();
            String search = "image\\";
            int i = npath.indexOf(search);
            file.transferTo(new File(npath));
            brepo.updateBrand(bname, npath.substring(i + search.length()), bid);
        }
    }

    public void deleteBrand(String bid) {
        brepo.deleteById(bid);
    }

    public int findCatFromSubCat(String sid) {
        subCategory s = srepo.findById(sid).get();
        return s.getCategory().getCid();
    }

    public List<product> findProductsByBid(String bid) {
        return prepo.findProductByBId(bid);
    }

    public void deleteSubById(String subId) {
        srepo.deleteById(subId);
    }

    public subCategory findSubCategoryBySubName(String sname) {
        return srepo.findSubCategoryBySubName(sname);
    }

    public category findCategoryById(int id) {
        return crepo.findById(id).get();
    }

    public List<User> findByStatus(String status) {
        return urepo.findByStatus(status);
    }

    public product findProductById(Long id) {
        return prepo.findById(id).get();
    }

    @Transactional
    public void updateProduct(String pname, String price, String description, MultipartFile file, Long pid) throws IllegalStateException, IOException {

        String folderPath = "C:\\ALTLAB\\src\\main\\resources\\static\\image\\";
        String npath = folderPath + file.getOriginalFilename();
        String search = "image\\";
        int i = npath.indexOf(search);
        if (file.isEmpty()) {
            prepo.updateProduct(pname, price, description, findProductById(pid).getImgPath(), pid);
        } else {
            prepo.updateProduct(pname, price, description, npath.substring(i + search.length()), pid);
            file.transferTo(new File(npath));
        }
    }

    public void deleteProduct(Long pid) {
        prepo.deleteById(pid);
    }

    public void mail(String to, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cg.gr@bk.ru");
        message.setTo(to);
        message.setText(body);
        message.setSubject(subject);
        jms.send(message);
    }

    @Transactional
    public void verify(String status, int id) {
        urepo.verify(status, id);
    }

    public User findById(int id) {
        return urepo.findById(id).get();
    }

    public void reject(int id) {
        urepo.deleteById(id);
    }

    public List<User> getAllUsers(String role) {
        return urepo.findByRole(role);
    }

    public User findUserById(int id) {
        return urepo.findById(id).get();
    }

    @Transactional
    public void updateUser(String name, String address, String email, String phone, int id) {
        urepo.updateUser(name, address, email, phone, id);
    }

    public void deleteUser(int id) {
        urepo.deleteById(id);
    }

    public List<product> findProductFromSubCategory(String subId) {
        return prepo.findProductBySubCategory(subId);
    }

    public List<product> getAllProduct() {
        return prepo.findAll();
    }

    public List<subCategory> getSubCategoriesByCategoryId(Integer categoryId) {
        return srepo.findByCategoryCid(categoryId);
    }

    public List<product> findProductsBySubCategoryIds(List<String> subCategoryIds) {
        return prepo.findByBrandSubCategorySubIdIn(subCategoryIds);
    }

    public List<product> findProductsByCategoryIds(List<Integer> categoryIds) {
        return prepo.findByBrandSubCategoryCategoryCidIn(categoryIds);
    }

    public int countProductsByCategory(int categoryId) {
        return prepo.countByBrandSubCategoryCategoryCid(categoryId);
    }

    public int countProductsBySubCategory(String subCategoryId) {
        return prepo.countByBrandSubCategorySubId(subCategoryId);
    }

    public category findCategoryByProductId(Long pid) {
        return prepo.findCategoryByProductId(pid);
    }

    public subCategory findSubCategoryByProductId(Long pid) {
        return prepo.findSubCategoryByProductId(pid);
    }

    public List<Reviews> getReviewsByProductId(Long productId) {
        return rrepo.findByProductId(productId);
    }

    public void addReviewToProduct(Long productId, Reviews review) {
        product product = prepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        review.setProduct(product);
        rrepo.save(review);
    }

    public Favorite getFavoriteList(int userId) {
        return frepo.findByUserId(userId);
    }

    public void addProductToFavorites(int userId, Long productId) {
        Favorite favoriteList = frepo.findByUserId(userId);
        if (favoriteList == null) {
            favoriteList = new Favorite();
            favoriteList.setUser(urepo.findById(userId).get());
            favoriteList.setProducts(new ArrayList<>());
        }
        product product = prepo.findById(productId).get();
        favoriteList.getProducts().add(product);
        frepo.save(favoriteList);
    }

    public void removeProductFromFavorites(int userId, Long productId) {
        Favorite favoriteList = frepo.findByUserId(userId);
        if (favoriteList != null) {
            product product = prepo.findById(productId).get();
            favoriteList.getProducts().remove(product);
            frepo.save(favoriteList);
        }
    }

    public subCategory findSubCategoryByBrandId(String brandId) {
        return brepo.findSubCategoryByBrandId(brandId);
    }

    public Cart getCartList(int userId) {
        return cartrepo.findByUserId(userId);
    }

    @Transactional
    public void addProductToCart(int userId, Long productId) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(urepo.findById(userId).orElseThrow());
            cartrepo.save(cart);
        }

        CartItem cartItem = cartitemrepo.findByCartIdAndProduct_Pid(cart.getId(), productId);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(prepo.findById(productId).orElseThrow());
            cartItem.setQuantity(1);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        cartItem.updatePrice();
        cartitemrepo.save(cartItem);
    }

    @Transactional
    public void removeProductFromCart(int userId, Long productId) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart != null) {
            CartItem cartItem = cartitemrepo.findByCartIdAndProduct_Pid(cart.getId(), productId);
            if (cartItem != null) {
                cartitemrepo.delete(cartItem);
            }
        }
    }

    @Transactional
    public void increaseQuantity(int userId, Long productId) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart != null) {
            CartItem cartItem = cartitemrepo.findByCartIdAndProduct_Pid(cart.getId(), productId);
            if (cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItem.updatePrice();
                cartitemrepo.save(cartItem);
            }
        }
    }

    @Transactional
    public void decreaseQuantity(int userId, Long productId) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart != null) {
            CartItem cartItem = cartitemrepo.findByCartIdAndProduct_Pid(cart.getId(), productId);
            if (cartItem != null && cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.updatePrice();
                cartitemrepo.save(cartItem);
            }
        }
    }

    @Transactional
    public void applyPromoCode(int userId, String code) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart != null) {
            PromoCode promoCode = pcrepo.findByCode(code);
            if (promoCode != null) {
                cart.setPromoCode(promoCode);
                cartrepo.save(cart);
            }
        }
    }

    public PromoCode getPromoCodeByCode(String code) {
        return pcrepo.findByCode(code);
    }

    public List<product> findByPnameContaining(String keyword){
        return prepo.findByPnameContaining(keyword);
    }

    public List<product> findTop8ByOrderByAddDateDesc(Pageable pageable) {
        return prepo.findTop8ByOrderByAddDateDesc(pageable);
    }

    public List<product> findTop8ByOrderByReviewsCountDesc(Pageable pageable) {
        return prepo.findTop8ByOrderByReviewsCountDesc(pageable);
    }

    public List<product> getAllProductShuffled() {
        List<product> products = prepo.findAll();
        Collections.shuffle(products);
        return products;
    }

    public List<product> getProductsByCategory(String categoryId) {
        return prepo.findProductsByCategoryId(categoryId);
    }

    public Order placeOrder(int userId, String firstName, String lastName, String phone, String address,
                            String city, String region, String zipCode, String paymentMethod,
                            int totalSum, int shippingCost, double discountPercentage, double discountAmount,
                            double finalTotal, List<OrderItem> items) {

        User user = urepo.findById(userId).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setCity(city);
        order.setRegion(region);
        order.setZipCode(zipCode);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Pending");
        order.setTotalSum(totalSum);
        order.setShippingCost(shippingCost);
        order.setDiscountPercentage(discountPercentage);
        order.setDiscountAmount(discountAmount);
        order.setFinalTotal(finalTotal);

        order = orepo.save(order);

        for (OrderItem item : items) {
            item.setOrder(order);
            oitemrepo.save(item);
        }

        return order;
    }

    @Transactional
    public void clearCart(int userId) {
        Cart cart = cartrepo.findByUserId(userId);
        if (cart != null) {
            cart.getItems().clear();
            cart.setPromoCode(null);
            cartrepo.save(cart);
        }
    }
}
