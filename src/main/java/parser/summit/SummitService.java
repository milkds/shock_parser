package parser.summit;

import org.hibernate.Session;
import parser.summit.entities.SumItem;
import parser.summit.entities.SumPage;
import java.util.List;

class SummitService {
    static List<String> getParsedParts(Session sumSession) {
        List<String> result = new SumDAO().getAllPagesParts(sumSession);

        return result;
    }

    void savePageToDB(SumPage page, Session sumSession) {
        new SumDAO().savePageToDB(page, sumSession);
    }

    void saveItem(SumItem item, Session session) {
        new SumDAO().saveItem(item, session);
    }
}
