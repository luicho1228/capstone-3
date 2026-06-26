package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.shoppingCartService =shoppingCartService;
        this.profileService = profileService;
    }

    public ShoppingCart createOrder(int userId){
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
        double totalAmount = shoppingCart.getTotal();
        order.setShippingAmount(totalAmount);
        orderRepository.save(order);
        return shoppingCartService.getByUserId(userId);
    }

}
