package com.azs.serviceimpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azs.constants.ErrorCodeAndErrorMsgEnum;
import com.azs.dto.AddProductDto;
import com.azs.entity.AddProductEntity;
import com.azs.exception.CustomException;
import com.azs.repository.ProductRepository;
import com.azs.service.AddProductService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AddProductServiceImpl implements AddProductService {

	@Autowired
	private ProductRepository addProductRepository;

	@Override
	public AddProductEntity addProduct(AddProductDto addProductDto) {
		try {
			log.debug("Received purchaseDate: {}", addProductDto.getPurchaseDate());

			// Validate and parse the purchaseDate
			LocalDate purchaseDate;
			try {
				// Use strict parsing with DateTimeFormatter
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				purchaseDate = LocalDate.parse(addProductDto.getPurchaseDate().format(formatter), formatter);
			} catch (DateTimeParseException e) {
				throw new CustomException(ErrorCodeAndErrorMsgEnum.DATE_PARSE_ERROR, e);
			}
			// ✅ Validate input values
            if (addProductDto.getProductQtyOrdered() <= 0) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.INVALID_INPUT);
            }
            if (Objects.nonNull(addProductDto.getCurrentStock()) && addProductDto.getCurrentStock() < 0) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.INVALID_INPUT);
            }

            // ✅ Check if the product already exists with same invoiceNo and productName
            Optional<AddProductEntity> existingProductOptional = addProductRepository
                    .findByInvoiceNoAndProductName(addProductDto.getInvoiceNo(), addProductDto.getProductName());

            int updatedStock;

            if (existingProductOptional.isPresent()) {
                AddProductEntity existingProduct = existingProductOptional.get();
                
                // ✅ If product exists, add new quantity to the existing stock
                updatedStock = existingProduct.getCurrentStock() + addProductDto.getProductQtyOrdered();
                log.info("Existing product found. Previous stock: {}, New quantity: {}, Updated stock: {}", 
                          existingProduct.getCurrentStock(), addProductDto.getProductQtyOrdered(), updatedStock);
            } else {
                // ✅ If product does not exist, check if `currentStock` is present in JSON
                if (Objects.nonNull(addProductDto.getCurrentStock())) {
                    // Case 1: If `currentStock` is provided, add it to `productQtyOrdered`
                    updatedStock = addProductDto.getCurrentStock() + addProductDto.getProductQtyOrdered();
                    log.info("New product. Initial stock (from request): {}, Added quantity: {}, Final stock: {}", 
                              addProductDto.getCurrentStock(), addProductDto.getProductQtyOrdered(), updatedStock);
                } else {
                    // Case 2: If `currentStock` is missing, just set `updatedStock = productQtyOrdered`
                    updatedStock = addProductDto.getProductQtyOrdered();
                    log.info("New product. Initial stock set to ordered quantity: {}", updatedStock);
                }
            }

            // Build entity with updated stock
            AddProductEntity entity = AddProductEntity.builder()
                    .invoiceNo(addProductDto.getInvoiceNo())
                    .productName(addProductDto.getProductName())
                    .currentStock(updatedStock)  // ✅ Correctly updated
                    .productPurchasedFrom(addProductDto.getProductPurchasedFrom())
                    .productPurchasedFromContactNo(addProductDto.getProductPurchasedFromContactNo())
                    .productPurchasedFromLocation(addProductDto.getProductPurchasedFromLocation())
                    .purchaseDate(purchaseDate)
                    .gst(addProductDto.getGst())
                    .productQtyOrdered(addProductDto.getProductQtyOrdered())
                    .unit(addProductDto.getUnit())
                    .productPrice(addProductDto.getProductPrice())
                    .build();

            // ✅ Save the entity
            AddProductEntity savedEntity = addProductRepository.save(entity);
            log.info("Product saved successfully with ID: {} and updated stock: {}", savedEntity.getId(), savedEntity.getCurrentStock());

            return savedEntity;
        } catch (Exception e) {
            log.error("Error in addProduct: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }


	@Override
	public AddProductEntity updateProduct(int id, AddProductDto addProductDto) {
		try {
            log.debug("Received purchaseDate: {}", addProductDto.getPurchaseDate());

            // Validate purchase date
            LocalDate purchaseDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                purchaseDate = LocalDate.parse(addProductDto.getPurchaseDate().format(formatter), formatter);
            } catch (DateTimeParseException e) {
                throw new CustomException(ErrorCodeAndErrorMsgEnum.DATE_PARSE_ERROR, e);
            }

            // Find and update the existing entity
            AddProductEntity existingEntity = addProductRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND));

            // ✅ Add new quantity to the existing stock instead of replacing it
            int updatedStock = existingEntity.getCurrentStock() + addProductDto.getProductQtyOrdered();
            existingEntity.setCurrentStock(updatedStock);

            existingEntity.setInvoiceNo(addProductDto.getInvoiceNo());
            existingEntity.setProductName(addProductDto.getProductName());
            existingEntity.setProductPurchasedFrom(addProductDto.getProductPurchasedFrom());
            existingEntity.setProductPurchasedFromContactNo(addProductDto.getProductPurchasedFromContactNo());
            existingEntity.setProductPurchasedFromLocation(addProductDto.getProductPurchasedFromLocation());
            existingEntity.setPurchaseDate(purchaseDate);
            existingEntity.setGst(addProductDto.getGst());
            existingEntity.setProductQtyOrdered(addProductDto.getProductQtyOrdered());
            existingEntity.setUnit(addProductDto.getUnit());
            existingEntity.setProductPrice(addProductDto.getProductPrice());

            // Save and return updated entity
            return addProductRepository.save(existingEntity);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in updateProduct: ", e);
            throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
        }
    }


	@Override
	public List<AddProductEntity> getProductsByName(String productName) {
		try {
			log.info("Fetching products by name: {}", productName);

			// Fetch products from the database
			List<AddProductEntity> products = addProductRepository.findAll().stream()
					.filter(product -> product.getProductName().equalsIgnoreCase(productName))
					.collect(Collectors.toList());

			if (products.isEmpty()) {
				throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
			}

			return products;
		} catch (CustomException e) {
			log.error("Custom exception occurred: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error occurred while fetching products by name", e);
			throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
		}
	}



	@Override
	public void deleteAddedProduct(int id) {
		try {
			log.info("Deleting product with ID: {}", id);

			if (!addProductRepository.existsById(id)) {
				throw new CustomException(ErrorCodeAndErrorMsgEnum.PRODUCT_NOT_FOUND);
			}

			addProductRepository.deleteById(id);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error in deleteProduct: ", e);
			throw new CustomException(ErrorCodeAndErrorMsgEnum.DATABASE_ERROR, e);
		}
	}

}
