package parser.eibach;

import parser.eibach.entiities.EibPage;

import java.util.HashSet;
import java.util.Set;

public class EibachService {
    public static void savePageLinkToDB(String url) {
        EibPage page = new EibPage();
        page.setUrl(url);
        EibDAO.savePage(page);
    }

    public Set<String> getSavedPages() {

        return new HashSet<>(EibDAO.getSavedPages());
    }
}
