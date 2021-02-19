package parser.summit;

import org.hibernate.Session;
import parser.summit.entities.SumPage;

import java.util.ArrayList;
import java.util.List;

public class SummitService {
    public static List<String> getParsedParts(Session sumSession) {
        List<String> result = new SumDAO().getAllPagesParts(sumSession);

        return result;
    }

    public void savePageToDB(SumPage page, Session sumSession) {
        new SumDAO().savePageToDB(page, sumSession);
    }
}
