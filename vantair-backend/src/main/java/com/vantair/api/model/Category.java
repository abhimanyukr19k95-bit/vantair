package com.vantair.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    /** Slug used by the frontend, e.g. "perfumes". */
    @Id
    private String id;

    @Column(nullable = false)
    private String label;

    private String emoji;

    @Column(length = 500)
    private String description;

    private String color;
}
