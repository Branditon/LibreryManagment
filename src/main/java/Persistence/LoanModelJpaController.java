/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistence;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.UserModel;
import Model.BookModel;
import Model.LoanModel;
import Persistence.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import javax.persistence.Persistence;

/**
 *
 * @author brandonescudero
 */
public class LoanModelJpaController implements Serializable {

    public LoanModelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public LoanModelJpaController() {
        emf = Persistence.createEntityManagerFactory("PersistenceUnit");
    }

    public void create(LoanModel loanModel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserModel user = loanModel.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                loanModel.setUser(user);
            }
            BookModel book = loanModel.getBook();
            if (book != null) {
                book = em.getReference(book.getClass(), book.getId());
                loanModel.setBook(book);
            }
            em.persist(loanModel);
            if (user != null) {
                user.getLoan().add(loanModel);
                user = em.merge(user);
            }
            if (book != null) {
                book.getLoan().add(loanModel);
                book = em.merge(book);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LoanModel loanModel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LoanModel persistentLoanModel = em.find(LoanModel.class, loanModel.getId());
            UserModel userOld = persistentLoanModel.getUser();
            UserModel userNew = loanModel.getUser();
            BookModel bookOld = persistentLoanModel.getBook();
            BookModel bookNew = loanModel.getBook();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                loanModel.setUser(userNew);
            }
            if (bookNew != null) {
                bookNew = em.getReference(bookNew.getClass(), bookNew.getId());
                loanModel.setBook(bookNew);
            }
            loanModel = em.merge(loanModel);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getLoan().remove(loanModel);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getLoan().add(loanModel);
                userNew = em.merge(userNew);
            }
            if (bookOld != null && !bookOld.equals(bookNew)) {
                bookOld.getLoan().remove(loanModel);
                bookOld = em.merge(bookOld);
            }
            if (bookNew != null && !bookNew.equals(bookOld)) {
                bookNew.getLoan().add(loanModel);
                bookNew = em.merge(bookNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = loanModel.getId();
                if (findLoanModel(id) == null) {
                    throw new NonexistentEntityException("The loanModel with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LoanModel loanModel;
            try {
                loanModel = em.getReference(LoanModel.class, id);
                loanModel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The loanModel with id " + id + " no longer exists.", enfe);
            }
            UserModel user = loanModel.getUser();
            if (user != null) {
                user.getLoan().remove(loanModel);
                user = em.merge(user);
            }
            BookModel book = loanModel.getBook();
            if (book != null) {
                book.getLoan().remove(loanModel);
                book = em.merge(book);
            }
            em.remove(loanModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LoanModel> findLoanModelEntities() {
        return findLoanModelEntities(true, -1, -1);
    }

    public List<LoanModel> findLoanModelEntities(int maxResults, int firstResult) {
        return findLoanModelEntities(false, maxResults, firstResult);
    }

    private List<LoanModel> findLoanModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LoanModel.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public LoanModel findLoanModel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LoanModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getLoanModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LoanModel> rt = cq.from(LoanModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
