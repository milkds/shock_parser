package parser.eibach;

import org.hibernate.Session;
import org.hibernate.Transaction;
import parser.eibach.entiities.EibPage;
import parser.utils.HibernateUtil;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class EibDAO {
    static List<String> getSavedPages() {
        Session session = HibernateUtil.getEibSessionFactory().openSession();
        List<String> result = new ArrayList<>();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> crQ = builder.createQuery(String.class);
        Root<EibPage> root = crQ.from(EibPage.class);
        crQ.select(root.get("url")).distinct(true);
        Query q = session.createQuery(crQ);
        result = q.getResultList();
        session.close();

        return result;
    }

    static void savePage(EibPage page) {
        Session session = HibernateUtil.getEibSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();
            transaction.begin();
            session.save(page);
            transaction.commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
