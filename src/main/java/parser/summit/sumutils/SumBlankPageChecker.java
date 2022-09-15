package parser.summit.sumutils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.jsoup.helper.StringUtil;
import parser.summit.SumPageToDiscUtil;
import parser.summit.SummitService;
import parser.summit.entities.SumPage;
import parser.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SumBlankPageChecker {
    private Session session;
    public void clearPagesFromDB(String brand) {
        List<SumPage> parsedPages = SummitService.getAllPages(session);
        List<String> blankPagesParts = getBlankPagesParts(brand);
        Set<SumPage> pagesToDelete = getPagesToDelete(parsedPages, blankPagesParts);
        System.out.println(pagesToDelete.size());
        SummitService.deletePages(pagesToDelete, session);

        session.close();
        HibernateUtil.shutdown();
    }

    private Set<SumPage> getPagesToDelete(List<SumPage> parsedPages, List<String> blankPagesParts) {
        Set<SumPage> result = new HashSet<>();
        parsedPages.forEach(page->{
            if (blankPagesParts.contains(page.getPart())){
                result.add(page);
            }
        });

        return result;
    }

    private List<String> getBlankPagesParts(String brand) {
        List<String> result = new ArrayList<>();
        List<String>  fNames = new SumPageToDiscUtil(brand).getBlankPagesNames();
        fNames.forEach(fName->{
            String partNo = StringUtils.substringBefore(fName, ".txt");
            result.add(partNo);
        });

        return result;
    }


    public SumBlankPageChecker() {
       session = HibernateUtil.getSummitSessionFactory().openSession();
    }
}
