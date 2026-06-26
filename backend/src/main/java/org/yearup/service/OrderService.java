package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.OrderLineItem;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ProfileService profileService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService,OrderLineItemRepository orderLineItemRepository ,ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.shoppingCartService =shoppingCartService;
        this.profileService = profileService;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    public Order createOrder(int userId){
        // 1. Get the user's current cart
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        // 2. Do not allow checkout with an empty cart
        if (cart.getItems().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cart is empty!");
        }

        // 3. Get the user's profile for shipping address
        Profile profile = profileService.getProfile(userId);

        if (profile == null)
        {
            return null;
        }

        // 4. Create the order record
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDate.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(shoppingCartService.getByUserId(userId).getTotal());

        Order savedOrder = orderRepository.save(order);

        // 5. Create one order line item for each shopping cart item
        for (ShoppingCartItem cartItem : cart.getItems().values())
        {
            OrderLineItem orderLineItem = new OrderLineItem();

            orderLineItem.setOrderId(savedOrder.getOrderId());
            orderLineItem.setProductId(cartItem.getProduct().getProductId());
            orderLineItem.setSalesPrice(cartItem.getProduct().getPrice());
            orderLineItem.setQuantity(cartItem.getQuantity());
            orderLineItem.setDiscount(cartItem.getDiscountPercent());

            orderLineItemRepository.save(orderLineItem);
        }

        // 6. Clear the user's cart
        shoppingCartService.deleteAllProducts(userId);

        // 7. Return the updated cart, which should now be empty
        //return shoppingCartService.getByUserId(userId);
        return savedOrder;



        /*
        Profile profile = profileService.getProfile(userId);
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Profile not found");
        }
        ShoppingCart shoppingCart =shoppingCartService.getByUserId(userId);
        if (shoppingCart == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Shopping cart not found");
        }




        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setDate(LocalDate.now());
        double totalAmount = shoppingCart.getTotal();
        order.setShippingAmount(totalAmount);
        orderRepository.save(order);
        shoppingCartService.deleteAllProducts(userId);
        return shoppingCartService.getByUserId(userId);

        */
    }

}
