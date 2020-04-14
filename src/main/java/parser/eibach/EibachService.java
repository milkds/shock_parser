package parser.eibach;

import parser.eibach.entiities.EibItem;
import parser.eibach.entiities.EibPage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EibachService {
    public static void savePageLinkToDB(String url) {
        EibPage page = new EibPage();
        page.setUrl(url);
        EibDAO.savePage(page);
    }

    public static Set<String> getProcessedItemsLinks() {
        List<String> itemLinks = EibDAO.getProcessedItemLinks();
        return new HashSet<>(itemLinks);
    }

    public static Set<String> getAllItemsLinks() {
        return new HashSet<>(EibDAO.getSavedPages());
    }

    public static void saveItemToDB(EibItem item) {
        EibDAO.saveItem(item);
    }

    public Set<String> getSavedPages() {

        return new HashSet<>(EibDAO.getSavedPages());
    }
}
