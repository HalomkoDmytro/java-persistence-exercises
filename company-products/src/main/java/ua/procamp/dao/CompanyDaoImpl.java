package ua.procamp.dao;

import ua.procamp.exception.CompanyDaoException;
import ua.procamp.model.Company;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Objects;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        Objects.requireNonNull(id);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Company company = entityManager
                    .createQuery("select company from Company company " +
                            "left join fetch company.products where company.id = :id", Company.class)
                    .setParameter("id", id)
                    .getSingleResult();
            entityManager.getTransaction().commit();
            return company;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new CompanyDaoException(String.format("Exception during extraction by id, with id = %d", id), e);
        } finally {
            entityManager.close();
        }
    }

}
