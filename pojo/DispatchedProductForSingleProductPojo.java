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
public class DispatchedProductForSingleProductPojo {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id; // Unique identifier for dispatched product

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer productId; // ID of the associated product (reference to AddProductEntity)
	
	@NotBlank(message = "Product name cannot be empty")
	private String productName;
	
	@NotNull(message = "Work Order No. cannot be empty")
    private Long workOrderNo;
    
    
    @NotNull(message = "Work Order date cannot be empty")
    private LocalDate workOrderDate; 

    
    @NotBlank(message = "Plant Name cannot be empty eg- CSPGCL,CSPDCL etc")
    private String plantName;
    
	@NotNull(message = "Dispatch Qty cannot be empty")
    private Integer dispatchedQty; // Quantity of the product being dispatched

	@NotBlank(message = "Unit cannot be empty")
    private String unit; // Unit of measurement for the product

	@NotBlank(message = "Dispatch Location cannot be empty")
    private String productDispatchLocation; // Location from where the product is dispatched

	
    private String dispatchCity; // City of dispatch

	@NotBlank(message = "Dispatch State cannot be empty")
    private String dispatchState; // State of dispatch

	@NotBlank(message = "Cannot be empty")
    private String productDispatchedTo; // Entity or person to whom the product is dispatched

    private String challanNo; // Challan number for the dispatch

    private String productDispatchVehicleNo; // Vehicle number used for dispatch

    @NotNull(message = "Dispatch Date cannot be empty")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dispatchDate; // Date of dispatch

    @NotNull(message = "Date cannot be empty")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dispatchDateUpto; // Validity of the dispatch date

    
}
