package com.cafems.cafemanagementsystem.serviceImpl;

import com.cafems.cafemanagementsystem.dao.CategoryDao;
import com.cafems.cafemanagementsystem.jwt.JwtFilter;
import com.cafems.cafemanagementsystem.pojo.Category;
import com.cafems.cafemanagementsystem.service.CategoryService;
import com.cafems.cafemanagementsystem.util.CafeUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.cafems.cafemanagementsystem.constant.CafeConstant.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(validateCategoryMap(requestMap, false)){
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Category added successfully", HttpStatus.OK);
                }

            }else{
                return CafeUtils.getResponseEntity(UNAUTHORIZE, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") &&validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd){
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;

    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
           if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
               return new ResponseEntity<>(categoryDao.getAllCategory(), HttpStatus.OK);
           }
            return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
           if(jwtFilter.isAdmin()){
               if(validateCategoryMap(requestMap, true)){
                  Optional<Category> category = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                  if(!category.isEmpty()){
                      categoryDao.save(getCategoryFromMap(requestMap, true));
                      return CafeUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);
                  }else{
                      return CafeUtils.getResponseEntity("Category id does not exit", HttpStatus.OK);

                  }
               }
               return CafeUtils.getResponseEntity(INVALID_DATA, HttpStatus.BAD_REQUEST);
           }else{
               return CafeUtils.getResponseEntity(UNAUTHORIZE, HttpStatus.UNAUTHORIZED);
           }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}


