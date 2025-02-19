package com.azs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.azs.entity.DispatchedProductEntity;

@Repository
public interface DispatchedProductRepository extends JpaRepository<DispatchedProductEntity, Integer>{

	List<DispatchedProductEntity> findByWorkOrderNo(Long workOrderNo);
	
	List<DispatchedProductEntity> findByWorkOrderNoAndProductNameIgnoreCase(Long workOrderNo, String productName);
	
	List<DispatchedProductEntity> findAll();

}
