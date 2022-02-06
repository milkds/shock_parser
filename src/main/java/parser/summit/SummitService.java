package parser.summit;

import org.hibernate.Session;
import parser.summit.entities.SumItem;
import parser.summit.entities.SumPage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SummitService {
    static List<String> getParsedParts(Session sumSession) {
        List<String> result = new SumDAO().getAllPagesParts(sumSession);

        return result;
    }

    public static List<SumItem> skipSavedItems(List<SumItem> items, Session session) {
        List<String> partNos = SumDAO.getSavedItemsPartNos(session);
        Set<String> partsSet = new HashSet<>(partNos);
        List<SumItem> result = new ArrayList<>();
        items.forEach(item->{
            if (!partsSet.contains(item.getPartNo())){
                result.add(item);
            }
        });

        return result;
    }

    void savePageToDB(SumPage page, Session sumSession) {
        new SumDAO().savePageToDB(page, sumSession);
    }

    void saveItem(SumItem item, Session session) {
        new SumDAO().saveItem(item, session);
    }
}
