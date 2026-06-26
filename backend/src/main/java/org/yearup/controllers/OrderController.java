package org.yearup.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.models.ShoppingCart;
import org.yearup.service.OrderService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/Order")
@CrossOrigin
@PreAuthorize("hasRole('ROLE_USER')")
public class OrderController {

    private UserService userService;
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<ShoppingCart> createOrder(Principal principal){
        String username = principal.getName();
        int userId = userService.getIdByUsername(username);
        ShoppingCart shoppingCart = orderService.createOrder(userId);
        if (shoppingCart == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shoppingCart);
    }


}
