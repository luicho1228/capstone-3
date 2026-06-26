package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.repository.OrderRepository;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProfileService profileService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.profileService = profileService;
    }

    public ShoppingCart createOrder(int userId){
        Profile profile = profileService.getProfile();
        Order order = new Order();
    }

}
