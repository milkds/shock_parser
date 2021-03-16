package parser.summit.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class SumItem {
    private String title;
    private BigDecimal price;
    private String shortTitle;
    private String picUrls;
    private String videoUrls;
    private String brand;
    private String partNo;
    private String itemType;
    private List<SumItemAttribute> attributes = new ArrayList<>();
    private String description;
    private List<SumFitment> fitments;


    @Override
    public String toString() {
        return new StringJoiner(System.lineSeparator(), SumItem.class.getSimpleName() + "[", "]")
                .add("title='" + title + "'")
                .add("price=" + price)
                .add("shortTitle='" + shortTitle + "'")
                .add("picUrls='" + picUrls + "'")
                .add("videoUrls='" + videoUrls + "'")
                .add("brand='" + brand + "'")
                .add("partNo='" + partNo + "'")
                .add("itemType='" + itemType + "'")
                .add("attributes=" + attributes)
                .add("fitments=" + fitments)
                .add("description='" + description + "'")
                .toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setPicUrls(String picUrls) {
        this.picUrls = picUrls;
    }

    public String getPicUrls() {
        return picUrls;
    }

    public void setVideoUrls(String videoUrls) {
        this.videoUrls = videoUrls;
    }

    public String getVideoUrls() {
        return videoUrls;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemType() {
        return itemType;
    }

    public List<SumItemAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SumItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setFitments(List<SumFitment> fitments) {
        this.fitments = fitments;
    }

    public List<SumFitment> getFitments() {
        return fitments;
    }
}
