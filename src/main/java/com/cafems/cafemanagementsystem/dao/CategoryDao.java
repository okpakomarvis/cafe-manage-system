package com.cafems.cafemanagementsystem.dao;

import com.cafems.cafemanagementsystem.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();
}
