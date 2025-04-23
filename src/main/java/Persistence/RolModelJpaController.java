/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistence;

import Model.RolModel;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class RolModelJpaController implements Serializable {

    public RolModelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public RolModelJpaController() {
        emf = Persistence.createEntityManagerFactory("PersistenceUnit");
    }

    public void create(RolModel rolModel) {
        if (rolModel.getUser() == null) {
            rolModel.setUser(new ArrayList<UserModel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UserModel> attachedUser = new ArrayList<UserModel>();
            for (UserModel userUserModelToAttach : rolModel.getUser()) {
                userUserModelToAttach = em.getReference(userUserModelToAttach.getClass(), userUserModelToAttach.getId());
                attachedUser.add(userUserModelToAttach);
            }
            rolModel.setUser(attachedUser);
            em.persist(rolModel);
            for (UserModel userUserModel : rolModel.getUser()) {
                RolModel oldRolOfUserUserModel = userUserModel.getRol();
                userUserModel.setRol(rolModel);
                userUserModel = em.merge(userUserModel);
                if (oldRolOfUserUserModel != null) {
                    oldRolOfUserUserModel.getUser().remove(userUserModel);
                    oldRolOfUserUserModel = em.merge(oldRolOfUserUserModel);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RolModel rolModel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolModel persistentRolModel = em.find(RolModel.class, rolModel.getId());
            List<UserModel> userOld = persistentRolModel.getUser();
            List<UserModel> userNew = rolModel.getUser();
            List<UserModel> attachedUserNew = new ArrayList<UserModel>();
            for (UserModel userNewUserModelToAttach : userNew) {
                userNewUserModelToAttach = em.getReference(userNewUserModelToAttach.getClass(), userNewUserModelToAttach.getId());
                attachedUserNew.add(userNewUserModelToAttach);
            }
            userNew = attachedUserNew;
            rolModel.setUser(userNew);
            rolModel = em.merge(rolModel);
            for (UserModel userOldUserModel : userOld) {
                if (!userNew.contains(userOldUserModel)) {
                    userOldUserModel.setRol(null);
                    userOldUserModel = em.merge(userOldUserModel);
                }
            }
            for (UserModel userNewUserModel : userNew) {
                if (!userOld.contains(userNewUserModel)) {
                    RolModel oldRolOfUserNewUserModel = userNewUserModel.getRol();
                    userNewUserModel.setRol(rolModel);
                    userNewUserModel = em.merge(userNewUserModel);
                    if (oldRolOfUserNewUserModel != null && !oldRolOfUserNewUserModel.equals(rolModel)) {
                        oldRolOfUserNewUserModel.getUser().remove(userNewUserModel);
                        oldRolOfUserNewUserModel = em.merge(oldRolOfUserNewUserModel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = rolModel.getId();
                if (findRolModel(id) == null) {
                    throw new NonexistentEntityException("The rolModel with id " + id + " no longer exists.");
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
            RolModel rolModel;
            try {
                rolModel = em.getReference(RolModel.class, id);
                rolModel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolModel with id " + id + " no longer exists.", enfe);
            }
            List<UserModel> user = rolModel.getUser();
            for (UserModel userUserModel : user) {
                userUserModel.setRol(null);
                userUserModel = em.merge(userUserModel);
            }
            em.remove(rolModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolModel> findRolModelEntities() {
        return findRolModelEntities(true, -1, -1);
    }

    public List<RolModel> findRolModelEntities(int maxResults, int firstResult) {
        return findRolModelEntities(false, maxResults, firstResult);
    }

    private List<RolModel> findRolModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolModel.class));
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

    public RolModel findRolModel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolModel> rt = cq.from(RolModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
