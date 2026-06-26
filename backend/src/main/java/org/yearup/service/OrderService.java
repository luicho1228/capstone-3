package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDate;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ProfileService profileService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, OrderLineItemRepository orderLineItemRepository, ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    public Order createOrder(int userId) {
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty!");
        }

        Profile profile = profileService.getProfile(userId);

        if (profile == null) {
            return null;
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDate.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(shoppingCartService.getByUserId(userId).getTotal());

        Order savedOrder = orderRepository.save(order);

        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderLineItem orderLineItem = new OrderLineItem();

            orderLineItem.setOrderId(savedOrder.getOrderId());
            orderLineItem.setProductId(cartItem.getProduct().getProductId());
            orderLineItem.setSalesPrice(cartItem.getProduct().getPrice());
            orderLineItem.setQuantity(cartItem.getQuantity());
            orderLineItem.setDiscount(cartItem.getDiscountPercent());

            orderLineItemRepository.save(orderLineItem);
        }
        shoppingCartService.deleteAllProducts(userId);

        return savedOrder;
    }

}
