package com.cafems.cafemanagementsystem.service;

import com.cafems.cafemanagementsystem.pojo.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    ResponseEntity<String> update(Map<String, String> requestMap);
}
