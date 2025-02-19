package com.azs.pojo;

import java.time.LocalDate;

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
public class WorkOrderInfoForBulkProductPojo {

	@NotNull(message = "Work Order No. cannot be empty")
	private Long workOrderNo;

	@NotNull(message = "Work Order date cannot be empty")
	private LocalDate workOrderDate;

	@NotBlank(message = "Plant Name cannot be empty")
	private String plantName;

	@NotBlank(message = "Dispatch Location cannot be empty")
	private String dispatchLocation;

	@NotBlank(message = "Dispatch City cannot be empty")
	private String dispatchCity;

	@NotBlank(message = "Dispatch State cannot be empty")
	private String dispatchState;

	@NotBlank(message = "Product Dispatched To cannot be empty")
	private String productDispatchedTo;

	@NotNull(message = "Dispatch Date cannot be empty")
	private LocalDate dispatchDate;

	@NotNull(message = "Date cannot be empty")
	private LocalDate dispatchDateUpto;

}
