package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.Principal;
import java.util.List;

import com.example.demo.Entity.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.adminService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ADMIN")
public class adminController {

	private final adminService aservice;

	public adminController(adminService aservice) {
		this.aservice = aservice;
	}

	@GetMapping("/home")
	public String home(Model model, Principal principal, HttpSession session) {
		String email = principal.getName();
		session.setAttribute("rcount", aservice.findByStatus("Unverified").size());
		model.addAttribute("rcount", aservice.findByStatus("Unverified").size());
		model.addAttribute("user", aservice.findByEmail(email));
		session.setAttribute("user", aservice.findByEmail(email));
		session.setAttribute("categories", aservice.getCategories());
		model.addAttribute("categories", aservice.getCategories());
		return "admin/admin_home";
	}

	@GetMapping("/add_admin_form")
	public String add_admin_form(HttpSession session, Model model,
			@RequestParam(name = "role", required = false) String role) {
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		session.setAttribute("role", role);
		return "admin/add_admin_form";
	}

	@PostMapping("/register_admin")
	public void register_admin(HttpServletResponse response, @ModelAttribute User user,
			@RequestParam("image") MultipartFile file, Model model, HttpSession session) throws IOException {
		System.out.println(file.getOriginalFilename());
		aservice.admin_register(user, file, (String) session.getAttribute("role"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/add_admin_form");
	}

	@GetMapping("/add_product_form")
	public String add_product_form(HttpSession session, Model model) {
		model.addAttribute("user", session.getAttribute("user"));
		List<com.example.demo.Entity.category> clist = aservice.getCategories();
		model.addAttribute("clist", clist);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		return "admin/add_product_form";
	}

	@GetMapping("/subCategories")
	@ResponseBody
	public List<subCategory> getsubCat(@RequestParam("categoryId") int cid, Model model) {
		return aservice.subCategories(cid);

	}

	@PostMapping("/add_product")
	public void add_product(Model model, HttpSession session, @ModelAttribute product p,
			@RequestParam("image") MultipartFile file, @RequestParam("subCategory") String subCategory,
			HttpServletResponse response) throws IllegalStateException, IOException {
		aservice.add_product(p, file, subCategory);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/add_product_form");
	}

	@GetMapping("/category")
	public String category(Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("user"));
		List<com.example.demo.Entity.category> list = aservice.getCategories();
		model.addAttribute("rcount", session.getAttribute("rcount"));
		model.addAttribute("categories", list);
		return "admin/category";

	}

	@PostMapping("/add_category")
	public void add_category(Model model, HttpSession session, @RequestParam("cname") String cname,
			@RequestParam("image") MultipartFile file, HttpServletResponse response) throws IOException {
		aservice.add_category(cname, file);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/category");

	}

	@GetMapping("/updateFormCategory/{id}")
	public String updateFormCategory(@PathVariable("id") int id, Model model, HttpSession session) {
		model.addAttribute("category", aservice.findCategoryById(id));
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		return "admin/updateCategory";
	}

	@PostMapping("updateCat/{id}")
	public void updateCat(@PathVariable("id") int id, @RequestParam("name") String cname,
			@RequestParam("image") MultipartFile file, Model model, HttpSession session, HttpServletResponse response)
			throws IOException {
		aservice.updateCategory(file, cname, id);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/category");
	}

	@GetMapping("/sub_category/{id}")
	public String sub_category(@PathVariable("id") int id, Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("user"));
		List<subCategory> slist = aservice.subCategories(id);
		model.addAttribute("subCategory", slist);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		session.setAttribute("cid", id);
		return "admin/sub_category";
	}

	@PostMapping("/add_subCategory")
	public void add_subCategory(Model model, HttpSession session, HttpServletResponse response,
			@RequestParam("cname") String subName, @RequestParam("image") MultipartFile file) throws IOException {
		if (aservice.findSubCategoryBySubName(subName) == null) {
			int cid = (int) session.getAttribute("cid");
			aservice.add_subCategory(subName, cid, file);
			model.addAttribute("rcount", session.getAttribute("rcount"));
			response.sendRedirect("/ADMIN/sub_category/" + cid);

		} else {

			throw new FileAlreadyExistsException(subName);
		}
	}

	@GetMapping("/viewSubCategory/{id}")
	public String viewSubCategory(@PathVariable("id") String id, Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("vsub", aservice.findSubCategory(id));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		return "admin/viewSubCategory";
	}

	@GetMapping("/update_subCat/{id}")
	public String updateCat(@PathVariable("id") String id, Model model, HttpSession session) {
		subCategory sc = aservice.findSubCategory(id);
		model.addAttribute("sub", sc);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		return "admin/updateCatForm";
	}

	@PostMapping("/updateSub/{id}")
	public void updateSub(@PathVariable("id") String id, @RequestParam("image") MultipartFile file,
			@RequestParam("name") String sname, Model model, HttpSession session, HttpServletResponse response)
			throws IllegalStateException, IOException {

		aservice.updateSubCat(sname, id, file);
		int cid = aservice.findCatFromSubCat(id);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		// model.addAttribute(", response)
		response.sendRedirect("/ADMIN/sub_category/" + cid);

	}

	@GetMapping("deleteCategory/{id}")
	public void deleteCategory(@PathVariable("id") int id, Model model, HttpSession session,
			HttpServletResponse response) throws IOException {
		aservice.deleteCategory(id);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/category");
	}

	@GetMapping("/delete_subCat/{id}")
	public void deleteSubCat(@PathVariable("id") String id, Model model, HttpSession session,
			HttpServletResponse response) throws IOException {
		int cid = aservice.findCatFromSubCat(id);
		aservice.deleteSubById(id);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/sub_category/" + cid);

	}

	@GetMapping("/request")
	public String request(Model model, HttpSession session) {
		List<User> rlist = aservice.findByStatus("Unverified");
		model.addAttribute("rlist", rlist);
		model.addAttribute("rcount", session.getAttribute("rcount"));
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		return "admin/request";
	}

	@GetMapping("/products/{id}")
	public String products(@PathVariable("id") String id, Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		return "admin/products";
	}
	
	@GetMapping("/update_product_form/{id}")
	public String update_product_form( @PathVariable("id")Long id  ,Model model,HttpSession session) {
		model.addAttribute("product", aservice.findProductById(id));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		return "admin/update_product_form";
	}

	
	@GetMapping("/verify/{id}")
	public void verify(Model model, HttpSession session, @PathVariable("id") int id, HttpServletResponse response)
			throws IOException {
		aservice.verify("Verified", id);
		User u = aservice.findById(id);
		model.addAttribute("user", session.getAttribute("user"));
		aservice.mail(u.getEmail(), "you are now Verified!", "Ecommerce");
		response.sendRedirect("/ADMIN/request");
	}

	@GetMapping("/reject/{id}")
	public void reject(Model model, @PathVariable("id") int id, HttpSession session, HttpServletResponse response)
			throws IOException {
		User u = aservice.findById(id);
		model.addAttribute("user", session.getAttribute("user"));
		aservice.mail(u.getEmail(), "rejected", "Ecommerce");
		aservice.reject(id);
		response.sendRedirect("/ADMIN/request");
	}

	@GetMapping("/users")
	public String users(Model model, HttpSession session) {
		List<User> users = aservice.getAllUsers("USER");
		model.addAttribute("users", users);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		return "admin/users";
	}

	@GetMapping("/userUpdateForm/{id}")
	public String userUpdateForm(@PathVariable("id") int id, Model model, HttpSession session) {
		User user = aservice.findUserById(id);
		model.addAttribute("updateUser", user);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		return "admin/userUpdateForm";
	}

	@PostMapping("/updateUser/{id}")
	public void updateUser(@PathVariable("id") int id, @RequestParam("name") String name,
			@RequestParam("address") String address, @RequestParam("email") String email,
			@RequestParam("phone") String phone, Model model, HttpSession session, HttpServletResponse response)
			throws IOException {
		aservice.updateUser(name, address, email, phone, id);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/users");
	}

	@GetMapping("/userDelete/{id}")
	public void delete(@PathVariable("id") int id, Model model, HttpSession session, HttpServletResponse response)
			throws IOException {
		aservice.deleteUser(id);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		model.addAttribute("rcount", session.getAttribute("rcount"));
		response.sendRedirect("/ADMIN/users");
	}
}