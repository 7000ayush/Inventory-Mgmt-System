package com.azs.pojo;

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
public class BulkProductDispatchAllDetailsPojo {


	@NotNull(message = "Work Order Information cannot be empty")
	private WorkOrderInfoForBulkProductPojo workOrderInfo;

	@NotNull(message = "Dispatched products list cannot be empty")
	private List<DipatchProductPojoForBulk> dispatchedProducts;
}
