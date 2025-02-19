package com.azs.repository; 

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.azs.entity.AddProductEntity;



//The repository interacts with the database and provides CRUD operations. Use Spring Data JPA to create this layer.

@Repository
public interface ProductRepository extends JpaRepository<AddProductEntity, Integer>{

	// Custom method to find product by name
	@Query("SELECT p FROM AddProductEntity p WHERE LOWER(p.productName) = LOWER(:productName)")
    List<AddProductEntity> findByProductNameIgnoreCase(@Param("productName") String productName);

	
	Optional<AddProductEntity> findByInvoiceNoAndProductName(String invoiceNo, String productName);
	
	 
	List<AddProductEntity> findByCurrentStockGreaterThan(int stock);
	
       
    //The repository is responsible only for interacting with the database.
   // It knows how to fetch, save, update, or delete data, but it doesn’t include any additional logic.
   //This method fetches data from the database based on the product name, nothing more.
}
