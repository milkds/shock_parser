package parser.fox.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.fox.entities.FoxItem;
import parser.fox.entities.FoxItemSpec;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class FoxItemDAO {

    public static FoxItem getItemByPartNo(Session session, String partNo) {
        FoxItem result = null;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<FoxItem> crQ = builder.createQuery(FoxItem.class);
        Root<FoxItem> root = crQ.from(FoxItem.class);
        crQ.where(builder.equal(root.get("partNo"), partNo));
        Query q = session.createQuery(crQ);
        result = (FoxItem)q.getSingleResult();

        return result;
    }

    public static FoxItemSpec getCheckedSpec(FoxItemSpec rawSpec, Session session) {
        FoxItemSpec result = null;
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<FoxItemSpec> crQ = builder.createQuery(FoxItemSpec.class);
        Root<FoxItemSpec> root = crQ.from(FoxItemSpec.class);
        crQ.where(builder.and(builder.equal(root.get("specName"), rawSpec.getSpecName()),
                              builder.equal(root.get("specVal"), rawSpec.getSpecVal())));
        Query q = session.createQuery(crQ);
        result = (FoxItemSpec)q.getSingleResult();

        return result;
    }

    public static void saveItem(FoxItem item, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.save(item);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public static void updateItem(FoxItem item, Session session) {
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.update(item);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
