package com.azs.service;

import com.azs.dto.BulkProductDispatchAllDetailsDto;
import com.azs.dto.DispatchedProductForSingleProductDto;

public interface DispatchedProductService {


	public DispatchedProductForSingleProductDto createDispatchedProduct(DispatchedProductForSingleProductDto dispatchedProductDto);

	public BulkProductDispatchAllDetailsDto createBulkDispatchedProducts(BulkProductDispatchAllDetailsDto bulkDispatchedProductDto);

	public BulkProductDispatchAllDetailsDto getDispatchedProductsByWorkOrder(Long workOrderNo);

	public BulkProductDispatchAllDetailsDto deleteDispatchedProductByProductName(Long workOrderNo, String productName);
}
