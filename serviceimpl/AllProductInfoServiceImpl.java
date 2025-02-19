package com.azs.serviceimpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azs.dto.DipatchProductDtoForBulk;
import com.azs.dto.DispatchedProductsWorkOrderDetailsDto;
import com.azs.dto.ProductStockDto;
import com.azs.entity.DispatchedProductEntity;
import com.azs.repository.DispatchedProductRepository;
import com.azs.repository.ProductRepository;
import com.azs.service.AllProductInfoService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AllProductInfoServiceImpl implements AllProductInfoService {
	
	@Autowired
    private DispatchedProductRepository dispatchedProductRepository;

    @Autowired
    private ProductRepository addProductRepository;

    @Override
    public Map<String, Object> getProductsInfo() {
        // Available products remain mapped as before
        List<ProductStockDto> availableProducts = addProductRepository.findByCurrentStockGreaterThan(0)
                .stream()
                .map(product -> new ProductStockDto(product.getProductName(), product.getCurrentStock()))
                .collect(Collectors.toList());

        // Fetch all dispatched products
        List<DispatchedProductEntity> dispatchedEntities = dispatchedProductRepository.findAll();

        // Group dispatched products by workOrderNo (each work order appears once)
        Map<Long, List<DispatchedProductEntity>> groupedByWorkOrder =
            dispatchedEntities.stream().collect(Collectors.groupingBy(DispatchedProductEntity::getWorkOrderNo));

        // Map each group to DispatchedProductsWorkOrderDetailsDto
        List<DispatchedProductsWorkOrderDetailsDto> dispatchedWorkOrderDetails = groupedByWorkOrder.entrySet()
            .stream()
            .map(entry -> {
                Long workOrderNo = entry.getKey();
                List<DispatchedProductEntity> group = entry.getValue();
                // Use the first entity to get common work order details
                DispatchedProductEntity first = group.get(0);
                
                // Map individual dispatched product details using your existing DipatchProductDtoForBulk
                List<DipatchProductDtoForBulk> dispatchedProducts = group.stream().map(entity -> {
                    DipatchProductDtoForBulk dto = new DipatchProductDtoForBulk();
                    dto.setId(entity.getId());
                    dto.setProductId(entity.getAddProductEntity() != null ? entity.getAddProductEntity().getId() : null);
                    // Use product name from the associated AddProductEntity to ensure consistency
                    dto.setProductName(entity.getAddProductEntity() != null 
                        ? entity.getAddProductEntity().getProductName() 
                        : entity.getProductName());
                    dto.setDispatchedQty(entity.getDispatchedQty());
                    dto.setUnit(entity.getUnit());
                    dto.setChallanNo(entity.getChallanNo());
                    dto.setProductDispatchVehicleNo(entity.getProductDispatchVehicleNo());
                    dto.setCurrentStock(entity.getAddProductEntity() != null 
                        ? entity.getAddProductEntity().getCurrentStock() 
                        : null);
                    return dto;
                }).collect(Collectors.toList());

                // Build the new grouped DTO for this work order
                DispatchedProductsWorkOrderDetailsDto groupDto = new DispatchedProductsWorkOrderDetailsDto();
                groupDto.setWorkOrderNo(first.getWorkOrderNo());
                groupDto.setWorkOrderDate(first.getWorkOrderDate());
                groupDto.setPlantName(first.getPlantName());
                groupDto.setDispatchLocation(first.getProductDispatchLocation()); // or use a dedicated field if available
                groupDto.setDispatchCity(first.getDispatchCity());
                groupDto.setDispatchState(first.getDispatchState());
                groupDto.setProductDispatchedTo(first.getProductDispatchedTo());
                groupDto.setDispatchDate(first.getDispatchDate());
                groupDto.setDispatchDateUpto(first.getDispatchDateUpto());
                groupDto.setDispatchedProducts(dispatchedProducts);
                return groupDto;
            })
            .collect(Collectors.toList());

        return Map.of(
                "availableProducts", availableProducts,
                "dispatchedProductsWorkorderDetails", dispatchedWorkOrderDetails
        );
    }
}