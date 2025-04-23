/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistence;

import Model.BookModel;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.LoanModel;
import Persistence.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Persistence;

/**
 *
 * @author brandonescudero
 */
public class BookModelJpaController implements Serializable {

    public BookModelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public BookModelJpaController() {
        emf = Persistence.createEntityManagerFactory("PersistenceUnit");
    }

    public void create(BookModel bookModel) {
        if (bookModel.getLoan() == null) {
            bookModel.setLoan(new ArrayList<LoanModel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<LoanModel> attachedLoan = new ArrayList<LoanModel>();
            for (LoanModel loanLoanModelToAttach : bookModel.getLoan()) {
                loanLoanModelToAttach = em.getReference(loanLoanModelToAttach.getClass(), loanLoanModelToAttach.getId());
                attachedLoan.add(loanLoanModelToAttach);
            }
            bookModel.setLoan(attachedLoan);
            em.persist(bookModel);
            for (LoanModel loanLoanModel : bookModel.getLoan()) {
                BookModel oldBookOfLoanLoanModel = loanLoanModel.getBook();
                loanLoanModel.setBook(bookModel);
                loanLoanModel = em.merge(loanLoanModel);
                if (oldBookOfLoanLoanModel != null) {
                    oldBookOfLoanLoanModel.getLoan().remove(loanLoanModel);
                    oldBookOfLoanLoanModel = em.merge(oldBookOfLoanLoanModel);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BookModel bookModel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BookModel persistentBookModel = em.find(BookModel.class, bookModel.getId());
            List<LoanModel> loanOld = persistentBookModel.getLoan();
            List<LoanModel> loanNew = bookModel.getLoan();
            List<LoanModel> attachedLoanNew = new ArrayList<LoanModel>();
            for (LoanModel loanNewLoanModelToAttach : loanNew) {
                loanNewLoanModelToAttach = em.getReference(loanNewLoanModelToAttach.getClass(), loanNewLoanModelToAttach.getId());
                attachedLoanNew.add(loanNewLoanModelToAttach);
            }
            loanNew = attachedLoanNew;
            bookModel.setLoan(loanNew);
            bookModel = em.merge(bookModel);
            for (LoanModel loanOldLoanModel : loanOld) {
                if (!loanNew.contains(loanOldLoanModel)) {
                    loanOldLoanModel.setBook(null);
                    loanOldLoanModel = em.merge(loanOldLoanModel);
                }
            }
            for (LoanModel loanNewLoanModel : loanNew) {
                if (!loanOld.contains(loanNewLoanModel)) {
                    BookModel oldBookOfLoanNewLoanModel = loanNewLoanModel.getBook();
                    loanNewLoanModel.setBook(bookModel);
                    loanNewLoanModel = em.merge(loanNewLoanModel);
                    if (oldBookOfLoanNewLoanModel != null && !oldBookOfLoanNewLoanModel.equals(bookModel)) {
                        oldBookOfLoanNewLoanModel.getLoan().remove(loanNewLoanModel);
                        oldBookOfLoanNewLoanModel = em.merge(oldBookOfLoanNewLoanModel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = bookModel.getId();
                if (findBookModel(id) == null) {
                    throw new NonexistentEntityException("The bookModel with id " + id + " no longer exists.");
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
            BookModel bookModel;
            try {
                bookModel = em.getReference(BookModel.class, id);
                bookModel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bookModel with id " + id + " no longer exists.", enfe);
            }
            List<LoanModel> loan = bookModel.getLoan();
            for (LoanModel loanLoanModel : loan) {
                loanLoanModel.setBook(null);
                loanLoanModel = em.merge(loanLoanModel);
            }
            em.remove(bookModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<BookModel> findBookModelEntities() {
        return findBookModelEntities(true, -1, -1);
    }

    public List<BookModel> findBookModelEntities(int maxResults, int firstResult) {
        return findBookModelEntities(false, maxResults, firstResult);
    }

    private List<BookModel> findBookModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BookModel.class));
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

    public BookModel findBookModel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BookModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getBookModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BookModel> rt = cq.from(BookModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
