package com.azs.service;

import java.util.List;

import com.azs.dto.AddProductDto;
import com.azs.entity.AddProductEntity;

public interface AddProductService {

	public AddProductEntity addProduct(AddProductDto addProductDto);
	public AddProductEntity updateProduct(int id, AddProductDto addProductDto);

	//same method present in repository why here ans->
	//The service layer is responsible for business logic. 
	//It acts as the intermediary between the controller and the repository.
	//You might need to add logic to:
	//Validate the input.
	//Check if a product exists before fetching it.
	//Transform or enrich the data before returning it.
	//By having a method like getProductByName in the service layer, 
	//you ensure that any logic related to fetching a product is encapsulated there.

	//You can skip the service method and directly call the repository method in the controller, 
	//but it's not recommended in a well-structured application. Here’s why:

	//Violation of Separation of Concerns:

	//If you call the repository directly in the controller, 
	//the controller is now responsible for handling business logic, 
	//which goes against the principles of clean architecture.



	List<AddProductEntity> getProductsByName(String productName);
	
	public void deleteAddedProduct(int id);


}
