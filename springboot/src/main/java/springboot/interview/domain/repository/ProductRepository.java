package springboot.interview.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.interview.domain.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
