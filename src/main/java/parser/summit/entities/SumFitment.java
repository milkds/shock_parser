package parser.summit.entities;

import java.util.List;
import java.util.StringJoiner;

public class SumFitment {
    private List<SumFitAttribute> attributes;
    private SumItem item;

    public void setAttributes(List<SumFitAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<SumFitAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return new StringJoiner(System.lineSeparator(), SumFitment.class.getSimpleName() + "[", "]")
                .add("attributes=" + attributes)
                .toString();
    }

    public void setItem(SumItem item) {
        this.item = item;
    }

    public SumItem getItem() {
        return item;
    }
}
