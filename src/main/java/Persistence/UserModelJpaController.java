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
import Model.RolModel;
import Model.LoanModel;
import Model.UserModel;
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
public class UserModelJpaController implements Serializable {

    public UserModelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public UserModelJpaController() {
        emf = Persistence.createEntityManagerFactory("PersistenceUnit");
    }


    public void create(UserModel userModel) {
        if (userModel.getLoan() == null) {
            userModel.setLoan(new ArrayList<LoanModel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolModel rol = userModel.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getId());
                userModel.setRol(rol);
            }
            List<LoanModel> attachedLoan = new ArrayList<LoanModel>();
            for (LoanModel loanLoanModelToAttach : userModel.getLoan()) {
                loanLoanModelToAttach = em.getReference(loanLoanModelToAttach.getClass(), loanLoanModelToAttach.getId());
                attachedLoan.add(loanLoanModelToAttach);
            }
            userModel.setLoan(attachedLoan);
            em.persist(userModel);
            if (rol != null) {
                rol.getUser().add(userModel);
                rol = em.merge(rol);
            }
            for (LoanModel loanLoanModel : userModel.getLoan()) {
                UserModel oldUserOfLoanLoanModel = loanLoanModel.getUser();
                loanLoanModel.setUser(userModel);
                loanLoanModel = em.merge(loanLoanModel);
                if (oldUserOfLoanLoanModel != null) {
                    oldUserOfLoanLoanModel.getLoan().remove(loanLoanModel);
                    oldUserOfLoanLoanModel = em.merge(oldUserOfLoanLoanModel);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserModel userModel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserModel persistentUserModel = em.find(UserModel.class, userModel.getId());
            RolModel rolOld = persistentUserModel.getRol();
            RolModel rolNew = userModel.getRol();
            List<LoanModel> loanOld = persistentUserModel.getLoan();
            List<LoanModel> loanNew = userModel.getLoan();
            if (rolNew != null) {
                rolNew = em.getReference(rolNew.getClass(), rolNew.getId());
                userModel.setRol(rolNew);
            }
            List<LoanModel> attachedLoanNew = new ArrayList<LoanModel>();
            for (LoanModel loanNewLoanModelToAttach : loanNew) {
                loanNewLoanModelToAttach = em.getReference(loanNewLoanModelToAttach.getClass(), loanNewLoanModelToAttach.getId());
                attachedLoanNew.add(loanNewLoanModelToAttach);
            }
            loanNew = attachedLoanNew;
            userModel.setLoan(loanNew);
            userModel = em.merge(userModel);
            if (rolOld != null && !rolOld.equals(rolNew)) {
                rolOld.getUser().remove(userModel);
                rolOld = em.merge(rolOld);
            }
            if (rolNew != null && !rolNew.equals(rolOld)) {
                rolNew.getUser().add(userModel);
                rolNew = em.merge(rolNew);
            }
            for (LoanModel loanOldLoanModel : loanOld) {
                if (!loanNew.contains(loanOldLoanModel)) {
                    loanOldLoanModel.setUser(null);
                    loanOldLoanModel = em.merge(loanOldLoanModel);
                }
            }
            for (LoanModel loanNewLoanModel : loanNew) {
                if (!loanOld.contains(loanNewLoanModel)) {
                    UserModel oldUserOfLoanNewLoanModel = loanNewLoanModel.getUser();
                    loanNewLoanModel.setUser(userModel);
                    loanNewLoanModel = em.merge(loanNewLoanModel);
                    if (oldUserOfLoanNewLoanModel != null && !oldUserOfLoanNewLoanModel.equals(userModel)) {
                        oldUserOfLoanNewLoanModel.getLoan().remove(loanNewLoanModel);
                        oldUserOfLoanNewLoanModel = em.merge(oldUserOfLoanNewLoanModel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = userModel.getId();
                if (findUserModel(id) == null) {
                    throw new NonexistentEntityException("The userModel with id " + id + " no longer exists.");
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
            UserModel userModel;
            try {
                userModel = em.getReference(UserModel.class, id);
                userModel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userModel with id " + id + " no longer exists.", enfe);
            }
            RolModel rol = userModel.getRol();
            if (rol != null) {
                rol.getUser().remove(userModel);
                rol = em.merge(rol);
            }
            List<LoanModel> loan = userModel.getLoan();
            for (LoanModel loanLoanModel : loan) {
                loanLoanModel.setUser(null);
                loanLoanModel = em.merge(loanLoanModel);
            }
            em.remove(userModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserModel> findUserModelEntities() {
        return findUserModelEntities(true, -1, -1);
    }

    public List<UserModel> findUserModelEntities(int maxResults, int firstResult) {
        return findUserModelEntities(false, maxResults, firstResult);
    }

    private List<UserModel> findUserModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserModel.class));
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

    public UserModel findUserModel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserModel> rt = cq.from(UserModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
