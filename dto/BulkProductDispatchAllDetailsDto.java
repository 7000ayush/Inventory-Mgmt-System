package com.azs.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkProductDispatchAllDetailsDto {


	@NotNull(message = "Work Order Information cannot be empty")
	private DispatchedProductsWorkOrderDetailsDto workOrderInfo;

	@NotNull(message = "Dispatched products list cannot be empty")
	private List<DipatchProductDtoForBulk> dispatchedProducts;
}
