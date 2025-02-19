package com.azs.pojo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductPojo {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Integer id;
	
	@NotBlank(message = "Invoice number cannot be empty")
	private String invoiceNo;
	
	@NotBlank(message = "Product name cannot be empty")
	private String productName;
	
	@NotNull(message = "Current Stock cannot be empty")
	private Integer currentStock;
	
	private String productPurchasedFrom;
	
	private Long productPurchasedFromContactNo;
	
	private String productPurchasedFromLocation;

	@NotNull(message = "Purchase Date cannot be empty")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate purchaseDate;

	private String gst;
	
	@NotNull(message = "Ordered Qty cannot be empty")
	private int productQtyOrdered;
	
	
	private String unit;
	
	@NotNull(message = "Product price cannot be empty")
	private double productPrice;
}
