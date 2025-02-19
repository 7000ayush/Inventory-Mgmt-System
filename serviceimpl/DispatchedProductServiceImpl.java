package com.azs.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.azs.constants.ErrorCodeAndErrorMsgEnum;
import com.azs.dto.BulkProductDispatchAllDetailsDto;
import com.azs.dto.DipatchProductDtoForBulk;
import com.azs.dto.DispatchedProductForSingleProductDto;
import com.azs.dto.DispatchedProductsWorkOrderDetailsDto;
import com.azs.entity.AddProductEntity;
import com.azs.entity.DispatchedProductEntity;
import com.azs.exception.CustomException;
import com.azs.repository.DispatchedProductRepository;
import com.azs.repository.ProductRepository;
import com.azs.service.DispatchedProductService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DispatchedProductServiceImpl implements DispatchedProductService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private DispatchedProductRepository dispatchedProductRepository;

	@Autowired
	private ProductRepository productRepository;


	@Override
    public DispatchedProductForSingleProductDto createDispatchedProduct(DispatchedProductForSingleProductDto dispatchedProductDto) {
        try {
            log.info("Creating a new dispatched product: {}", dispatchedProductDto);
            if (dispatchedProductDto == null || dispatchedProductDto.getWorkOrderNo() == null) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.INVALID_INPUT);
            }
            // Format plant name as needed (keep product name as is)
            dispatchedProductDto.setPlantName(formatStringUcase(dispatchedProductDto.getPlantName()));
            
            // Fetch product case-insensitively
            List<AddProductEntity> productEntities = productRepository
                    .findByProductNameIgnoreCase(dispatchedProductDto.getProductName().toLowerCase());
            if (productEntities.isEmpty()) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
            }
            AddProductEntity productEntity = productEntities.get(0);

            // Map DTO to entity using model mapper
            DispatchedProductEntity dispatchedProductEntity = modelMapper.map(dispatchedProductDto, DispatchedProductEntity.class);
            // Associate the product entity and override product name with the original value
            dispatchedProductEntity.setAddProductEntity(productEntity);
            dispatchedProductEntity.setProductName(productEntity.getProductName());

            // Check stock and update
            if (productEntity.getCurrentStock() < dispatchedProductEntity.getDispatchedQty()) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.INSUFFICIENT_STOCK);
            }
            productEntity.setCurrentStock(productEntity.getCurrentStock() - dispatchedProductEntity.getDispatchedQty());

            DispatchedProductEntity savedEntity = dispatchedProductRepository.save(dispatchedProductEntity);
            DispatchedProductForSingleProductDto savedDto = modelMapper.map(savedEntity, DispatchedProductForSingleProductDto.class);
            savedDto.setCurrentStock(productEntity.getCurrentStock());
            log.info("Dispatched product created successfully: {}", savedDto);
            return savedDto;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating dispatched product: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }

    @Override
    public BulkProductDispatchAllDetailsDto createBulkDispatchedProducts(BulkProductDispatchAllDetailsDto bulkDispatchedProductDto) {
        try {
            log.info("Processing bulk dispatch for work order: {}", bulkDispatchedProductDto.getWorkOrderInfo().getWorkOrderNo());
            // Extract shared work order information
            DispatchedProductsWorkOrderDetailsDto workOrderInfo = bulkDispatchedProductDto.getWorkOrderInfo();

            // Process each dispatched product
            List<DipatchProductDtoForBulk> processedProducts = bulkDispatchedProductDto.getDispatchedProducts()
                    .stream()
                    .map(productDto -> {
                        // Fetch product entity by product name (case-insensitive)
                        List<AddProductEntity> productEntities = productRepository
                                .findByProductNameIgnoreCase(productDto.getProductName().toLowerCase());
                        if (productEntities.isEmpty()) {
                            throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
                        }
                        AddProductEntity productEntity = productEntities.get(0);

                        // Check for sufficient stock
                        if (productEntity.getCurrentStock() < productDto.getDispatchedQty()) {
                            throw new CustomException(ErrorCodeAndErrorMsgEnum.INSUFFICIENT_STOCK);
                        }
                        // Deduct dispatched quantity from current stock
                        productEntity.setCurrentStock(productEntity.getCurrentStock() - productDto.getDispatchedQty());

                        // Map DTO to entity
                        DispatchedProductEntity dispatchedEntity = modelMapper.map(productDto, DispatchedProductEntity.class);
                        // Set shared work order details
                        dispatchedEntity.setWorkOrderNo(workOrderInfo.getWorkOrderNo());
                        dispatchedEntity.setWorkOrderDate(workOrderInfo.getWorkOrderDate());
                        dispatchedEntity.setPlantName(workOrderInfo.getPlantName().toUpperCase());
                        dispatchedEntity.setProductDispatchLocation(workOrderInfo.getDispatchLocation());
                        dispatchedEntity.setDispatchCity(workOrderInfo.getDispatchCity());
                        dispatchedEntity.setDispatchState(workOrderInfo.getDispatchState());
                        dispatchedEntity.setProductDispatchedTo(workOrderInfo.getProductDispatchedTo());
                        dispatchedEntity.setDispatchDate(workOrderInfo.getDispatchDate());
                        dispatchedEntity.setDispatchDateUpto(workOrderInfo.getDispatchDateUpto());
                        // Associate product and override product name
                        dispatchedEntity.setAddProductEntity(productEntity);
                        dispatchedEntity.setProductName(productEntity.getProductName());

                        // Save dispatched product entity
                        DispatchedProductEntity savedEntity = dispatchedProductRepository.save(dispatchedEntity);
                        // Update product stock in repository
                        productRepository.save(productEntity);

                        // Map back to DTO and update current stock
                        DipatchProductDtoForBulk savedDto = modelMapper.map(savedEntity, DipatchProductDtoForBulk.class);
                        savedDto.setCurrentStock(productEntity.getCurrentStock());
                        // Also ensure product name comes from productEntity
                        savedDto.setProductName(productEntity.getProductName());
                        return savedDto;
                    }).collect(Collectors.toList());

            // Return the response DTO with updated product details
            return BulkProductDispatchAllDetailsDto.builder()
                    .workOrderInfo(workOrderInfo)
                    .dispatchedProducts(processedProducts)
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during bulk dispatch processing: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }

    @Override
    public BulkProductDispatchAllDetailsDto getDispatchedProductsByWorkOrder(Long workOrderNo) {
        try {
            log.info("Fetching dispatched products for work order number: {}", workOrderNo);
            // Retrieve dispatched products based on work order number
            List<DispatchedProductEntity> dispatchedEntities = dispatchedProductRepository.findByWorkOrderNo(workOrderNo);
            if (dispatchedEntities.isEmpty()) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
            }
            // Map entities to DTOs
            List<DipatchProductDtoForBulk> dispatchedProducts = dispatchedEntities.stream().map(entity -> {
                DipatchProductDtoForBulk dto = modelMapper.map(entity, DipatchProductDtoForBulk.class);
                dto.setCurrentStock(entity.getAddProductEntity().getCurrentStock());
                // Ensure product name comes from the product entity
                dto.setProductName(entity.getAddProductEntity().getProductName());
                return dto;
            }).collect(Collectors.toList());
            // Extract shared work order information from the first entity
            DispatchedProductEntity firstEntity = dispatchedEntities.get(0);
            DispatchedProductsWorkOrderDetailsDto workOrderInfo = DispatchedProductsWorkOrderDetailsDto.builder()
                    .workOrderNo(firstEntity.getWorkOrderNo())
                    .workOrderDate(firstEntity.getWorkOrderDate())
                    .plantName(firstEntity.getPlantName())
                    .dispatchLocation(firstEntity.getProductDispatchLocation())
                    .dispatchCity(firstEntity.getDispatchCity())
                    .dispatchState(firstEntity.getDispatchState())
                    .productDispatchedTo(firstEntity.getProductDispatchedTo())
                    .dispatchDate(firstEntity.getDispatchDate())
                    .dispatchDateUpto(firstEntity.getDispatchDateUpto())
                    .build();
            // Return the response DTO
            return BulkProductDispatchAllDetailsDto.builder()
                    .workOrderInfo(workOrderInfo)
                    .dispatchedProducts(dispatchedProducts)
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching dispatched products: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }

    @Override
    public BulkProductDispatchAllDetailsDto deleteDispatchedProductByProductName(Long workOrderNo, String productName) {
        try {
            log.info("Deleting dispatched product with name: {} from work order: {}", productName, workOrderNo);
            // Retrieve the dispatched product(s) based on work order number and product name (ignoring case)
            List<DispatchedProductEntity> dispatchedEntities =
                    dispatchedProductRepository.findByWorkOrderNoAndProductNameIgnoreCase(workOrderNo, productName);
            if (dispatchedEntities.isEmpty()) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
            }
            // Reverse stock deduction and delete each dispatched product record
            for (DispatchedProductEntity entity : dispatchedEntities) {
                AddProductEntity productEntity = entity.getAddProductEntity();
                productEntity.setCurrentStock(productEntity.getCurrentStock() + entity.getDispatchedQty());
                productRepository.save(productEntity);
                dispatchedProductRepository.delete(entity);
            }
            // Return updated bulk dispatch details for the work order.
            return getDispatchedProductsByWorkOrder(workOrderNo);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting dispatched product by product name: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }

    private String formatStringUcase(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : "";
    }
}