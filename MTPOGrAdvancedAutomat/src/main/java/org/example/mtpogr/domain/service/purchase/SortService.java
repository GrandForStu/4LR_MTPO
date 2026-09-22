package org.example.mtpogr.domain.service.purchase;

import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SortService {

    private List<Order> merge(List<Order> left, List<Order> right) {
        List<Order> res = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getId() <= right.get(j).getId()) {
                res.add(left.get(i++));
            } else {
                res.add(right.get(j++));
            }
        }

        while (i < left.size()) res.add(left.get(i++));
        while (j < right.size()) res.add(right.get(j++));

        return res;
    }

    public List<Order> sortById(List<Order> orders) {
        if (orders == null || orders.size() <= 1) return orders;
        int mid = orders.size() / 2;
        List<Order> left = sortById(new ArrayList<>(orders.subList(0, mid)));
        List<Order> right = sortById(new ArrayList<>(orders.subList(mid, orders.size())));
        return merge(left, right);
    }



    private List<Product> mergeP(List<Product> left, List<Product> right) {
        List<Product> res = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getId() <= right.get(j).getId()) {
                res.add(left.get(i++));
            } else {
                res.add(right.get(j++));
            }
        }

        while (i < left.size()) res.add(left.get(i++));
        while (j < right.size()) res.add(right.get(j++));

        return res;
    }

    public List<Product> sortByIdP(List<Product> orders) {
        if (orders == null || orders.size() <= 1) return orders;
        int mid = orders.size() / 2;
        List<Product> left = sortByIdP(new ArrayList<>(orders.subList(0, mid)));
        List<Product> right = sortByIdP(new ArrayList<>(orders.subList(mid, orders.size())));
        return mergeP(left, right);
    }
}