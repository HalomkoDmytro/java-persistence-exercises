package ua.procamp.dao;

import ua.procamp.exception.DaoOperationException;
import ua.procamp.model.Photo;
import ua.procamp.model.PhotoComment;
import ua.procamp.util.EntityManagerUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Objects;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerFactory entityManagerFactory;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Photo photo) {
        Objects.requireNonNull(photo);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(photo);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new DaoOperationException("Exception during saving photo.", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Photo findById(long id) {
        Objects.requireNonNull(id);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Photo photo = entityManager.find(Photo.class, id);
            entityManager.getTransaction().commit();
            return photo;
        } catch (Exception e) {
            throw new DaoOperationException(String.format("Exception during searching by id. Where id = %d", id), e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Photo> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            List<Photo> resultList = entityManager.createQuery("select p from Photo p", Photo.class).getResultList();
            entityManager.getTransaction().commit();
            return resultList;
        } catch (Exception e) {
            throw new DaoOperationException("Exception during extracting all photos.", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void remove(Photo photo) {
        Objects.requireNonNull(photo);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Photo mergePhoto = entityManager.merge(photo);
            entityManager.remove(mergePhoto);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new DaoOperationException("Exception during deleting photo.", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void addComment(long photoId, String comment) {
        Objects.requireNonNull(photoId);
        Objects.requireNonNull(comment);

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Photo reference = entityManager.getReference(Photo.class, photoId);
            PhotoComment photoComment = new PhotoComment(comment, reference);
            entityManager.persist(photoComment);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new DaoOperationException("Exception during adding comment.", e);
        } finally {
            entityManager.close();
        }
    }
}
