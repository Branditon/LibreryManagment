/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistence;

import Model.PermissionModel;
import Persistence.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import javax.persistence.Persistence;

/**
 *
 * @author brandonescudero
 */
public class PermissionModelJpaController implements Serializable {

    public PermissionModelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public PermissionModelJpaController() {
        emf = Persistence.createEntityManagerFactory("PersistenceUnit");
    }


    public void create(PermissionModel permissionModel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(permissionModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PermissionModel permissionModel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            permissionModel = em.merge(permissionModel);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = permissionModel.getId();
                if (findPermissionModel(id) == null) {
                    throw new NonexistentEntityException("The permissionModel with id " + id + " no longer exists.");
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
            PermissionModel permissionModel;
            try {
                permissionModel = em.getReference(PermissionModel.class, id);
                permissionModel.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permissionModel with id " + id + " no longer exists.", enfe);
            }
            em.remove(permissionModel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PermissionModel> findPermissionModelEntities() {
        return findPermissionModelEntities(true, -1, -1);
    }

    public List<PermissionModel> findPermissionModelEntities(int maxResults, int firstResult) {
        return findPermissionModelEntities(false, maxResults, firstResult);
    }

    private List<PermissionModel> findPermissionModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PermissionModel.class));
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

    public PermissionModel findPermissionModel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PermissionModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermissionModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PermissionModel> rt = cq.from(PermissionModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
