package org.okten.may2024;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class RelationsDemo {

    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProductDatabase")) {
            JpaHelper jpaHelper = new JpaHelper(emf);

            jpaHelper.doInJpa(entityManager -> {
                Address address = new Address();
                address.setCity("Kyiv");
                address.setStreet("Volodymyrska, 55");

                Person person = new Person();
                person.setName("John");
                person.setAddress(address);

//                entityManager.persist(address); // not needed as there is cascade = {CascadeType.ALL} on address field in Person class
                entityManager.persist(person);
            });
//
            jpaHelper.doInJpa(entityManager -> {
                Person person1 = entityManager.find(Person.class, 1L);

                Company intel = new Company();
                intel.setName("Intel");
                entityManager.persist(intel);

                person1.setCompany(intel);
            });

            // fetchType example
            jpaHelper.doInJpa(entityManager -> {
                Company company = entityManager.find(Company.class, 1L);
                System.out.println(company.getPersons().stream().map(person -> person.getName()).distinct().toList());
            });

            jpaHelper.doInJpa(entityManager -> {
                Product product = new Product();
                product.setName("test product");
                product.setCurrentDiscount(20D);

                Tag tag1 = new Tag();
                tag1.setName("розпродаж");
                Tag tag2 = new Tag();
                tag2.setName("акція");

                product.setTags(List.of(tag1, tag2));

                entityManager.persist(tag1);
                entityManager.persist(tag2);
                entityManager.persist(product);
            });
        }
    }
}
