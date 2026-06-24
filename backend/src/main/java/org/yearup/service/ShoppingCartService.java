package org.yearup.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {
        // load the user's cart rows, look up each product, and build the ShoppingCart
        ShoppingCart cart = new ShoppingCart();
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        for (CartItem cartItem : cartItems){
            Product product = productService.getById(cartItem.getProductId());
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setProduct(product);
            shoppingCartItem.setQuantity(cartItem.getQuantity());
            cart.add(shoppingCartItem);
        }
        return cart;
    }

    // add additional methods here

    public ShoppingCart addProduct(int userId, int productId)
    {
        Product product = productService.getById(productId);
        if (product == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

        for (CartItem cartItem: cartItems){
            if (cartItem.getProductId() == productId){
                cartItem.setQuantity(cartItem.getQuantity()+1);
                shoppingCartRepository.save(cartItem);
                return getByUserId(userId);
            }
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setUserId(userId);
        newCartItem.setProductId(productId);
        newCartItem.setQuantity(1);

        shoppingCartRepository.save(newCartItem);

        return getByUserId(userId);
    }


    public ShoppingCart updateShoppingCart(int userId, int productId, CartItem cartItem){
        CartItem updatedCartItem = shoppingCartRepository.findByUserIdAndProductId(userId,productId);
        if (updatedCartItem == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        updatedCartItem.setQuantity(cartItem.getQuantity());
        shoppingCartRepository.save(updatedCartItem);

        return getByUserId(userId);
    }

    @Transactional
    public void deleteAllProducts(int userId){
        shoppingCartRepository.deleteByUserId(userId);
    }

}
