package com.example.appleStore.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProductFilterDTO {
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private String color;
    private String storage;
    private String sortBy;
    private String sortOrder;
    private String searchQuery;

}
