package com.example.bth10.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Tên danh mục không được để trống")
	private String name;

	@NotBlank(message = "Mô tả không được để trống")
	private String description;

	private String image;

	@NotNull(message = "Trạng thái không được để trống")
	private boolean status;

	@NotNull(message = "Ngày tạo không được để trống")
	@PastOrPresent(message = "Ngày tạo không được là một ngày trong tương lai")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate creationDate;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity;

	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;
}
