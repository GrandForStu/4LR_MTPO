package org.example.mtpogr.domain.statemachine.repostory;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}