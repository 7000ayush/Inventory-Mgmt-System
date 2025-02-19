package com.azs.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchedProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private AddProductEntity addProductEntity; // Reference to AddProductEntity by id
	
	
	@Column(nullable = false)
	private String productName;
	
	@Column(nullable = false)
	private Long workOrderNo;

	@Column(nullable = false)
	private LocalDate workOrderDate;
	
	@Column(nullable = false)
	private String plantName;
	
	@Column(nullable = false)
	private Integer dispatchedQty;

	@Column(nullable = false)
	private String unit;

	@Column(nullable = false)
	private String productDispatchLocation;

	@Column(nullable = false)
	private String dispatchCity;

	@Column(nullable = false)
	private String dispatchState;

	@Column(nullable = false)
	private String productDispatchedTo;

	@Column(nullable = false)
	private String challanNo;

	@Column(nullable = false)
	private String productDispatchVehicleNo;

	@Column(nullable = false)
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dispatchDate;

	@Column(nullable = false)
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dispatchDateUpto;


}