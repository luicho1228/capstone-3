package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping()
    public ShoppingCart getCart(Principal principal) {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();
        return shoppingCartService.getByUserId(userId);
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> addProductToCart(@PathVariable int productId, Principal principal) {
        String username = principal.getName();
        int userId = userService.getIdByUsername(username);

        ShoppingCart addedCart = shoppingCartService.addProduct(userId, productId);
        if (addedCart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(addedCart);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ShoppingCart> updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        String username = principal.getName();
        int userId = userService.getIdByUsername(username);
        ShoppingCart shoppingCart = shoppingCartService.updateShoppingCart(userId, productId, shoppingCartItem);
        if (shoppingCart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(shoppingCart);
    }

    @DeleteMapping()
    public ResponseEntity<ShoppingCart> clearCart(Principal principal) {
        String username = principal.getName();
        int userId = userService.getIdByUsername(username);
        ShoppingCart shoppingCart = shoppingCartService.deleteAllProducts(userId);
        return ResponseEntity.ok(shoppingCart);
    }

}
