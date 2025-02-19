package com.azs.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String invoiceNo;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = true)
    private String productPurchasedFrom;

    @Column(nullable = true)
    private Long productPurchasedFromContactNo;

    @Column(nullable = false)
    private String productPurchasedFromLocation;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "DATE")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;

    @Column(nullable = true)
    private String gst;

    @Column(nullable = false)
    private int productQtyOrdered;

    @Column(nullable = true)
    private String unit;

    @Column(nullable = false)
    private double productPrice;

    @Column(nullable = false)
    private double productTotalAmount;

    @PrePersist
    private void prePersist() {
        this.updatedAt = LocalDateTime.now();
        calculateProductTotalAmount();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateProductTotalAmount();
    }

    private void calculateProductTotalAmount() {
        if (this.productPrice > 0 && this.productQtyOrdered > 0) {
            this.productTotalAmount = this.productPrice * this.productQtyOrdered;
        } else {
            this.productTotalAmount = 0;
        }
    }
}
