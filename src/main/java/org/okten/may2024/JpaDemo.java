package org.okten.may2024;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

public class JpaDemo {

    // JPA - Java Persistence API
    public static void main(String[] args) {
        // Hibernate: SessionFactory creates Session - main object to work with Database
        // JPA: EntityManagerFactory creates EntityManager
         try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProductDatabase")) {
             JpaHelper jpaHelper = new JpaHelper(emf);

             jpaHelper.doInJpa(entityManager -> {
                 Product product = new Product();
                 product.setName("test product");
                 product.setCurrentDiscount(20D);
                 entityManager.persist(product);
             });

             jpaHelper.doInJpa(entityManager -> {
                 Product product = entityManager.find(Product.class, 1L);
                 product.setName("updated test product");
             });

             jpaHelper.doInJpa(entityManager -> {
                 Product product = entityManager.find(Product.class, 1L);
                 product.setName("new updated test product");
                 entityManager.detach(product); // ANY changes will not be applied to DB
                 product.setCurrentDiscount(32D);
             });

             Product product1 = jpaHelper.returnWithJpa(entityManager -> {
                 return entityManager.find(Product.class, 1L);
             });

             product1.setName("ABSOLUTELY NEW PRODUCT NAME");

             jpaHelper.doInJpa(entityManager -> {
                 entityManager.merge(product1);
             });
         }
    }
}
