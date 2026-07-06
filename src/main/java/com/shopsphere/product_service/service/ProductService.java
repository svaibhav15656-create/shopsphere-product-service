package com.shopsphere.product_service.service;
import java.util.*;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shopsphere.product_service.entity.Product;
import com.shopsphere.product_service.repository.ProductRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product){
        return productRepository.save(product);
    }
    @Cacheable(value ="products" , key = "#id")
    public Product getProductById(Long id){
       Optional<Product> product = productRepository.findById(id);
       if(product.isPresent()){
        return product.get();
       }
       return null;
    }
    public  Page<Product> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable);
    }
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product updatedProduct){
    Optional<Product> existingProductOpt = productRepository.findById(id);
    if(existingProductOpt.isPresent()){
        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        return productRepository.save(existingProduct);   
    }
    return null; 
}
    @CacheEvict(value = "products", key = "#id")
    public boolean deleteProduct(Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productRepository.deleteById(id);
            return true;
        }
        return false; 
    }
    public List<Product>  searchByCategory(String category){
        return productRepository.findByCategory(category);
    }
    public List<Product> searchByName(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    @Transactional
    public Product reduceStock(Long id, int quantity){
        Optional<Product> productOpt = productRepository.findById(id);
        if(productOpt.isEmpty()){
            throw new RuntimeException("Product not found");
        }
        Product product = productOpt.get();
        if(product.getStockQuantity() < quantity){
            throw new RuntimeException("Insufficient stock");
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
    
}