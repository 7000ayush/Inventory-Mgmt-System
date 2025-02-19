package com.azs.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azs.constants.Endpoints;
import com.azs.dto.AddProductDto;
import com.azs.dto.BulkProductDispatchAllDetailsDto;
import com.azs.dto.DispatchedProductForSingleProductDto;
import com.azs.pojo.AddProductPojo;
import com.azs.pojo.BulkProductDispatchAllDetailsPojo;
import com.azs.pojo.DispatchedProductForSingleProductPojo;
import com.azs.service.AddProductService;
import com.azs.service.AllProductInfoService;
import com.azs.service.DispatchedProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(Endpoints.V1_AZS)
@Slf4j
public class HomeController {

	@Autowired
	private AddProductService addProductService;

	@Autowired
	private DispatchedProductService dispatchedProductService;

	@Autowired
	private AllProductInfoService allProductInfoService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping("/addProduct")
	public ResponseEntity<AddProductDto> addProduct(@Valid @RequestBody AddProductPojo addProductPojo) {
		log.info("Adding product: {}", addProductPojo.getProductName());

		AddProductDto addProductDto = modelMapper.map(addProductPojo, AddProductDto.class);
		AddProductDto savedProduct = modelMapper.map(addProductService.addProduct(addProductDto), AddProductDto.class);

		return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	}

	@PutMapping("/updateProduct/{id}")
	public ResponseEntity<AddProductDto> updateProduct(
			@Valid @PathVariable int id,
			@RequestBody AddProductPojo addProductPojo) {
		log.info("Updating product with ID: {}", id);

		AddProductDto addProductDto = modelMapper.map(addProductPojo, AddProductDto.class);
		AddProductDto updatedProduct = modelMapper.map(addProductService.updateProduct(id, addProductDto), AddProductDto.class);

		return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}


	@GetMapping("/findProductByName/{productName}")
	public ResponseEntity<List<AddProductDto>> getProductsByName(@PathVariable String productName) {
		log.info("Fetching products with name: {}", productName);

		List<AddProductDto> products = addProductService.getProductsByName(productName)
				.stream()
				.map(product -> modelMapper.map(product, AddProductDto.class))
				.collect(Collectors.toList());

		return ResponseEntity.ok(products);
	}



	@DeleteMapping("/deleteProduct/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable int id) {
		log.info("Deleting product with ID: {}", id);

		addProductService.deleteAddedProduct(id);

		return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
	}



	// ✅ **Create Dispatched Product**
	@PostMapping("/dispatchProduct")
	public ResponseEntity<DispatchedProductForSingleProductDto> createDispatchedProduct(@Valid @RequestBody DispatchedProductForSingleProductPojo dispatchedProductPojo) {
		log.info("Creating dispatched product: {}", dispatchedProductPojo);

		DispatchedProductForSingleProductDto dispatchedProductDto = modelMapper.map(dispatchedProductPojo, DispatchedProductForSingleProductDto.class);
		DispatchedProductForSingleProductDto savedDispatchedProduct = dispatchedProductService.createDispatchedProduct(dispatchedProductDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedDispatchedProduct);
	}


	//find all products

	@GetMapping("/info")
	public Map<String, Object> getProductsInfo() {

		log.info("Fetching all products info");

		return allProductInfoService.getProductsInfo();
	}




	@PostMapping("/bulk-dispatch")
	public ResponseEntity<BulkProductDispatchAllDetailsDto> createBulkDispatchedProducts(
			@Valid @RequestBody BulkProductDispatchAllDetailsPojo bulkDispatchPojo) {
		log.info("Processing bulk dispatch for work order: {}", bulkDispatchPojo.getWorkOrderInfo().getWorkOrderNo());

		// Map Pojo to Dto
		BulkProductDispatchAllDetailsDto bulkDispatchDto = modelMapper.map(bulkDispatchPojo, BulkProductDispatchAllDetailsDto.class);

		// Process bulk dispatch
		BulkProductDispatchAllDetailsDto responseDto = dispatchedProductService.createBulkDispatchedProducts(bulkDispatchDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}

	/**
	 * Endpoint to retrieve dispatched products by work order number.
	 *
	 * @param workOrderNo The work order number.
	 * @return The response entity containing the list of dispatched products.
	 */
	@GetMapping("/work-order/{workOrderNo}")
	public ResponseEntity<BulkProductDispatchAllDetailsDto> getDispatchedProductsByWorkOrder(
			@PathVariable Long workOrderNo) {
		log.info("Fetching dispatched products for work order number: {}", workOrderNo);

		// Retrieve dispatched products
		BulkProductDispatchAllDetailsDto responseDto = dispatchedProductService.getDispatchedProductsByWorkOrder(workOrderNo);

		return ResponseEntity.ok(responseDto);
	}


	@DeleteMapping("/work-order/{workOrderNo}/deleteDispatch/{productName}")
	public ResponseEntity<BulkProductDispatchAllDetailsDto> deleteDispatchedProductByProductName(
			@PathVariable Long workOrderNo,
			@PathVariable String productName) {
		log.info("Request to delete dispatched product with name {} for work order {}", productName, workOrderNo);

		BulkProductDispatchAllDetailsDto updatedBulkDispatch = 
				dispatchedProductService.deleteDispatchedProductByProductName(workOrderNo, productName);

		return ResponseEntity.ok(updatedBulkDispatch);
	}


}
