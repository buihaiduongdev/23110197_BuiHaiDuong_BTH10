package com.example.bth10.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.bth10.entity.Category;
import com.example.bth10.entity.User;
import com.example.bth10.service.CategoryService;
import com.example.bth10.service.FileStorageService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FileStorageService fileStorageService;

	private boolean isUserAuthorized(HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");
		return currentUser != null;
	}

	private void addUserRolesToModel(HttpSession session, Model model) {
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser != null) {
			boolean isAdminOrManager = currentUser.getRole() == User.Role.admin
					|| currentUser.getRole() == User.Role.manager;
			model.addAttribute("isAdminOrManager", isAdminOrManager);
		}
	}

	@GetMapping
	public String listCategories(Model model, HttpSession session) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		model.addAttribute("categories", categoryService.findAll());
		addUserRolesToModel(session, model);
		return "category/list";
	}

	@GetMapping("/add")
	public String showAddForm(Model model, HttpSession session) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		Category category = new Category();
		category.setCreationDate(LocalDate.now()); // Set default creation date
		model.addAttribute("category", category);
		addUserRolesToModel(session, model);
		return "category/form";
	}

	@PostMapping("/add")
	public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult bindingResult,
			@RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		if (bindingResult.hasErrors()) {
			addUserRolesToModel(session, model);
			return "category/form";
		}

		User currentUser = (User) session.getAttribute("currentUser");
		category.setCreatedBy(currentUser);

		if (!imageFile.isEmpty()) {
			String fileName = fileStorageService.storeFile(imageFile);
			category.setImage(fileName);
		}
		categoryService.save(category);
		return "redirect:/category";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model, HttpSession session) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		Category category = categoryService.findById(id);
		model.addAttribute("category", category);
		addUserRolesToModel(session, model);
		return "category/form";
	}

	@PostMapping("/edit/{id}")
	public String editCategory(@PathVariable("id") Integer id, @Valid @ModelAttribute("category") Category category,
			BindingResult bindingResult, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session,
			Model model) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}

		if (bindingResult.hasErrors()) {
			addUserRolesToModel(session, model);
			return "category/form";
		}

		User currentUser = (User) session.getAttribute("currentUser");
		Category existingCategory = categoryService.findById(id);
		category.setCreatedBy(existingCategory.getCreatedBy());
		category.setCreatedBy(currentUser);

		if (!imageFile.isEmpty()) {
			String fileName = fileStorageService.storeFile(imageFile);
			category.setImage(fileName);
		} else {
			category.setImage(existingCategory.getImage());
		}
		category.setId(id);
		categoryService.save(category);
		return "redirect:/category";
	}

	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, HttpSession session) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		categoryService.deleteById(id);
		return "redirect:/category";
	}

	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = fileStorageService.loadFileAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@GetMapping("/search")
	public String searchCategory(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
		if (!isUserAuthorized(session)) {
			return "redirect:/login";
		}
		model.addAttribute("categories", categoryService.search(keyword));
		addUserRolesToModel(session, model);
		return "category/list";
	}
}
