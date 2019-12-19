package parser.fox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import parser.fox.dao.FoxCarDAO;
import parser.fox.dao.FoxFitDAO;
import parser.fox.dao.FoxItemDAO;
import parser.fox.entities.FoxCar;
import parser.fox.entities.FoxFit;
import parser.fox.entities.FoxItem;
import parser.fox.entities.FoxItemSpec;
import parser.utils.HibernateUtil;

import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.Set;

public class FoxDBService {
    private static final Logger logger = LogManager.getLogger(FoxDBService.class.getName());


    //returns null, if item doesn't exist
    public static FoxItem getExistingItem(Session session, String partNo) {
        FoxItem result = null;
        try {
            result = FoxItemDAO.getItemByPartNo(session, partNo);
        }
        catch (NoResultException ignored){}

        return result;
    }

    public static void saveItem(FoxItem item, Session session) {
        Set<FoxItemSpec> preparedSpecs = checkSpecs(item.getSpecs(), session);
        item.setSpecs(preparedSpecs);
        FoxItemDAO.saveItem(item, session);
    }

    private static Set<FoxItemSpec> checkSpecs(Set<FoxItemSpec> specs, Session session) {
        Set<FoxItemSpec> result = new HashSet<>();
        specs.forEach(rawSpec->{
            FoxItemSpec checkedSpec = null;
            try {
                checkedSpec = FoxItemDAO.getCheckedSpec(rawSpec, session);
            }
            catch (NoResultException e){
                checkedSpec = rawSpec;
            }
            result.add(checkedSpec);
        });

        return result;
    }

    public static void saveCars(Set<FoxCar> makeCars, Session session) {
        makeCars.forEach(foxCar -> {
            prepareFits(foxCar.getFits(), session);
            FoxCarDAO.saveCar(foxCar, session);
            foxCar.getFits().forEach(foxFit -> FoxFitDAO.saveFit(foxFit, session));
        });
    }

    private static void prepareFits(Set<FoxFit> fits, Session session) {
        fits.forEach(foxFit -> {
            FoxItem item = foxFit.getItem();
            if (item.getItemID()==0){
                try {
                    item = FoxItemDAO.getItemByPartNo(session, item.getPartNo());
                }
                catch (NoResultException e){
                    saveItem(item, session);
                }

            }
        });
    }

    public static StartPoint getStartPoint() {
        StartPoint result = new StartPoint();
        Session session = HibernateUtil.getFoxSessionFactory().openSession();
        int year = 0;
        try {
            year = FoxCarDAO.getEarliestYear(session);
        }
        catch (NoResultException|NullPointerException e){
            logger.error("No result error");
        }
        logger.debug(year);
        if (year>0){
            String make = FoxCarDAO.getLastMakeForYear(year, session);
            result.setMake(make);
        }
        result.setYear(year+"");
        session.close();

        return result;
    }
}
