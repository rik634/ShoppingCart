package com.code.shopping_cart.repository;

import com.code.shopping_cart.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
