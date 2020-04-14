package parser.eibach.entiities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "eib_items")
public class EibItem {

    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "FIT_TITLE")
    private String fitTitle;

    @Column(name = "URL")
    private String url;

    @Column(name = "PART_NO")
    private String partNo;

    @Column(name = "RIDE_HEIGHT")
    private String rideHeight;

    @Column(name = "PROD_NOTE")
    private String prodNote;

    @Column(name = "STOCK")
    private String stock;

    @Column(name = "SHIPMENT")
    private String shipment;

    @Column(name = "IMG_LINK")
    private String imgLink;

    @Column(name = "DESC")
    private String desc;

    @Column(name = "OLD_PRICE")
    private BigDecimal oldPrice;

    @Column(name = "NEW_PRICE")
    private BigDecimal newPrice;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private EibCar car;

    @Override
    public String toString() {
        return "EibItem{" +
                "title='" + title + '\'' +
                ", fitTitle='" + fitTitle + '\'' +
                ", url='" + url + '\'' +
                ", partNo='" + partNo + '\'' +
                ", rideHeight='" + rideHeight + '\'' +
                ", prodNote='" + prodNote + '\'' +
                ", stock='" + stock + '\'' +
                ", shipment='" + shipment + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", desc='" + desc + '\'' +
                ", oldPrice=" + oldPrice +
                ", newPrice=" + newPrice +
                ", car=" + car +
                '}';
    }

    public String getProdNote() {
        return prodNote;
    }

    public void setProdNote(String prodNote) {
        this.prodNote = prodNote;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFitTitle() {
        return fitTitle;
    }

    public void setFitTitle(String fitTitle) {
        this.fitTitle = fitTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getRideHeight() {
        return rideHeight;
    }

    public void setRideHeight(String rideHeight) {
        this.rideHeight = rideHeight;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public EibCar getCar() {
        return car;
    }

    public void setCar(EibCar car) {
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
