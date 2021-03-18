package parser.summit;

import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.eibach.entiities.EibItem;
import parser.summit.entities.SumItem;
import parser.summit.entities.SumPage;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class SumDAO {


    public void savePageToDB(SumPage page, Session sumSession) {
        Transaction transaction = null;
        try {
            transaction = sumSession.getTransaction();
            transaction.begin();
            sumSession.save(page);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }



    public List<String> getAllPagesParts(Session sumSession) {
        List<String> result = new ArrayList<>();
        CriteriaBuilder builder = sumSession.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<SumPage> root = crQ.from(SumPage.class);
        crQ.select(root.get("part")).distinct(true);
        Query q = sumSession.createQuery(crQ);
        result = q.getResultList();

        return result;

    }

    public void saveItem(SumItem item, Session session) {
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
}
