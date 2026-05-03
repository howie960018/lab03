package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.CartItem;
import com.ctbc.assignment2.bean.CourseBean;
import com.ctbc.assignment2.bean.Order;
import com.ctbc.assignment2.controller.rest.dto.CheckoutRequest;
import com.ctbc.assignment2.controller.rest.dto.OrderDto;
import com.ctbc.assignment2.controller.rest.dto.OrderItemDto;
import com.ctbc.assignment2.service.CourseBeanService;
import com.ctbc.assignment2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CourseBeanService courseService;

    @GetMapping
    public List<OrderDto> getMyOrders(Principal principal) {
        return orderService.findByBuyer(principal.getName()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable Long id, Principal principal) {
        Order order = orderService.findById(id);
        if (!order.getBuyer().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDto(order);
    }

    @PostMapping("/checkout")
    public OrderDto checkout(@RequestBody CheckoutRequest request, Principal principal) {
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No courses selected");
        }
        List<CartItem> items = request.getCourseIds().stream().map(courseId -> {
            CourseBean course = courseService.findById(courseId);
            return new CartItem(course.getId(), course.getCourseName(), course.getPrice());
        }).collect(Collectors.toList());
        Order order = orderService.createOrder(principal.getName(), items);
        return toDto(order);
    }

    @PostMapping("/{id}/pay")
    public OrderDto pay(@PathVariable Long id, Principal principal) {
        return toDto(orderService.pay(id, principal.getName()));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, Principal principal) {
        orderService.cancel(id, principal.getName());
    }

    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setItems(order.getItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setId(item.getId());
            itemDto.setCourseId(item.getCourse().getId());
            itemDto.setCourseName(item.getCourse().getCourseName());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
