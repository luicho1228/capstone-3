package org.yearup.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    /**
     * Get the current shopping cart object saved in the database
     * @param userId
     * @return
     */
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setProduct(product);
            shoppingCartItem.setQuantity(cartItem.getQuantity());
            cart.add(shoppingCartItem);
        }
        return cart;
    }

    public ShoppingCart addProduct(int userId, int productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return null;
        }
        CartItem cartItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(1);
            shoppingCartRepository.save(newCartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            shoppingCartRepository.save(cartItem);
        }
        return getByUserId(userId);
    }

    /**
     * updates a specific item quantity in the cart
     * @param userId
     * @param productId
     * @param shoppingCartItem
     * @return
     */
    public ShoppingCart updateShoppingCart(int userId, int productId, ShoppingCartItem shoppingCartItem) {
        CartItem updatedCartItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        if (updatedCartItem == null) {
            return null;
        }
        updatedCartItem.setQuantity(shoppingCartItem.getQuantity());
        shoppingCartRepository.save(updatedCartItem);
        return getByUserId(userId);
    }

    @Transactional
    public ShoppingCart deleteAllProducts(int userId) {
        shoppingCartRepository.deleteByUserId(userId);
        return getByUserId(userId);
    }

}
