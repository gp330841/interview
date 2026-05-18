package springboot.interview.cache.components;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import springboot.interview.domain.entity.Product;
import springboot.interview.domain.repository.ProductRepository;

@Service
public class ProductCacheService {

    private final ProductRepository productRepository;

    public ProductCacheService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("--> Cache Miss: Fetching product from DB for ID: " + id);
        return productRepository.findById(id).orElse(null);
    }
}
