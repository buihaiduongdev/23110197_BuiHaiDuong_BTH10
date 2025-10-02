package com.example.bth10.controller;

import com.example.bth10.entity.Product;
import com.example.bth10.entity.User;
import com.example.bth10.service.CategoryService;
import com.example.bth10.service.FileStorageService;
import com.example.bth10.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
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
            boolean isAdminOrManager = currentUser.getRole() == User.Role.admin || currentUser.getRole() == User.Role.manager;
            model.addAttribute("isAdminOrManager", isAdminOrManager);
        }
    }

    @GetMapping
    public String listProducts(Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("products", productService.findAll());
        addUserRolesToModel(session, model);
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        addUserRolesToModel(session, model);
        return "product/form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            addUserRolesToModel(session, model);
            return "product/form";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        product.setCreatedBy(currentUser);

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            product.setImage(fileName);
        }
        productService.save(product);
        return "redirect:/product";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        Product product = productService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        addUserRolesToModel(session, model);
        return "product/form";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            addUserRolesToModel(session, model);
            return "product/form";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        Product existingProduct = productService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        product.setCreatedBy(existingProduct.getCreatedBy());

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            product.setImage(fileName);
        } else {
            product.setImage(existingProduct.getImage());
        }
        product.setId(id);
        productService.save(product);
        return "redirect:/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        productService.deleteById(id);
        return "redirect:/product";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        if (!isUserAuthorized(session)) {
            return "redirect:/login";
        }
        model.addAttribute("products", productService.search(keyword));
        addUserRolesToModel(session, model);
        return "product/list";
    }
}
