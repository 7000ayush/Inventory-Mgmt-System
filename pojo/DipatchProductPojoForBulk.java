package com.azs.pojo;

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
public class DipatchProductPojoForBulk {
	
	private Integer id;
    private Integer productId;

    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    @NotNull(message = "Dispatch Qty cannot be empty")
    private Integer dispatchedQty;

    @NotBlank(message = "Unit cannot be empty")
    private String unit;

    private String challanNo;
    
    private String productDispatchVehicleNo;

   
    private int currentStock;
    
    

}
