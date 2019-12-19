package parser.fox.entities;

import java.util.Objects;

public class TableEntry {

    private String webLink;
    private String fitNote;
    private String position;
    private String drive;
    private String lift;
    private String partNo;
    private String price;
    private String title;

    @Override
    public String toString() {
        return "TableEntry{" +
                "webLink='" + webLink + '\'' +
                ", fitNote='" + fitNote + '\'' +
                ", position='" + position + '\'' +
                ", drive='" + drive + '\'' +
                ", lift='" + lift + '\'' +
                ", partNo='" + partNo + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableEntry)) return false;
        TableEntry that = (TableEntry) o;
        return Objects.equals(getWebLink(), that.getWebLink()) &&
                Objects.equals(getFitNote(), that.getFitNote()) &&
                Objects.equals(getPosition(), that.getPosition()) &&
                Objects.equals(getDrive(), that.getDrive()) &&
                Objects.equals(getLift(), that.getLift()) &&
                Objects.equals(getPartNo(), that.getPartNo()) &&
                Objects.equals(getPrice(), that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWebLink());
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getFitNote() {
        return fitNote;
    }

    public void setFitNote(String fitNote) {
        this.fitNote = fitNote;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getLift() {
        return lift;
    }

    public void setLift(String lift) {
        this.lift = lift;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
