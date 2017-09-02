import java.util.HashSet;
import java.util.TreeSet;

public class DLY<Obj> implements Comparable {
    Obj data;
    protected DLY left;
    protected DLY top;
    protected DLY bottom;
    protected DLY right;
    protected final DLX header;
    protected boolean alive = true;
    protected boolean in_row = true;
    protected boolean in_col = true;
    protected TreeSet<DLY> control;
    public DLY(DLX header) {
        this.header = header;
    }
    public DLY(DLX header, Obj data) {
        this.data = data;
        this.top = this;
        this.bottom = this;
        this.left = this;
        this.right = this;
        this.header = header;
        this.header.add(this);
    }
    public DLY(DLX header, Obj data, DLY left, DLY right) {
        this.data = data;
        this.left = left;
        this.bottom = this;
        this.top = this;
        this.right = right;
        this.header = header;
    }
    public void addRight(DLY other) {
        other.right = this.right;
        other.left = this;
        this.right.left = other;
        this.right = other;
    }
    public void addLeft(DLY other) {
        other.left = this.left;
        other.right = this;
        this.left.right = other;
        this.left = other;
    }
    public void appendRight(DLY other) {
        this.right.left = other.left;
        other.left.right = this.right;
        other.left = this;
        this.right = other;
    }
    public void addBottom(DLY other) {
        other.bottom = this.bottom;
        other.top = this;
        this.bottom.top = other;
        this.bottom = other;
    }
    public void addTop(DLY other) {
        other.top = this.top;
        other.bottom = this;
        this.top.bottom = other;
        this.top = other;
    }
    public void ghost() {
        if (!alive) throw new IllegalStateException(this + " is already dead!");
        alive = false;
        this.left.right = this.right;
        this.right.left = this.left;
        this.bottom.top = this.top;
        this.top.bottom = this.bottom;
        if (this.header.data == this) {
            this.header.data = this.header.data.bottom;
            if (this.header.data == null) {
                if (this.header.size > 1) System.err.println(this.header.size);
            }
        }
        this.header.size--;
    }
    public void squeeze_horizontal() {
        if (!in_row) throw new IllegalStateException(this + " is already dead!");
        in_row = false;
        this.left.right = this.right;
        this.right.left = this.right;
    }
    public void squeeze_vertical() {
        if (!in_col) throw new IllegalStateException(this + " is already dead!");
        in_col = false;
        this.bottom.top = this.top;
        this.top.bottom = this.bottom;
        if (this.header.data == this) this.header.data = this.header.data.bottom;
        this.header.size--;
    }
    public void revive_vertical_links() {
        if (in_col) throw new IllegalStateException(this + " is already alive!");
        in_col = true;
        this.bottom.top = this;
        this.top.bottom = this;
        this.header.size++;
    }
    public void revive_horizontal_links() {
        if (in_row) throw new IllegalStateException(this + " is already alive!");
        in_row = true;
        this.right.left = this;
        this.left.right = this;
    }
    public boolean ded() {
        return this.right.left != this && this.bottom.top != this;
    }
    public void resurrect() {
        if (alive) throw new IllegalStateException(this + " is already alive!");
        alive = true;
        this.right.left = this;
        this.left.right = this;
        this.bottom.top = this;
        this.top.bottom = this;
        header.size++;
    }
    public DLY next() {
        if (this.right == this) return null;
        return this.right;
    }
    public DLY prev() {
        if (this.left == this) return null;
        return this.left;
    }
    public DLY nextDown() {
        if (this.bottom == this) return null;
        return this.bottom;
    }
    public String getList() {
        StringBuilder out = new StringBuilder("[");
        DLY first = this;
        DLY current = first;
        int max = 7;
        if (current != null) out.append(current);
        do {
            current = current.next();
            if (current != null) {
                out.append(", ");
                out.append(current);
            }
            max--;
        } while (max > 0 && current != first && current != null);
        if (max < 0) out.append("...");
        out.append("]");
        return out.toString();
    }
    public void setData(Obj data) {
        this.data = data;
    }
    @Override
    public int hashCode() {
        return header.num;
    }
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        out.append(this.header.num);
        out.append(",");
        out.append(data.hashCode());
        out.append(")");
        return out.toString();
    }

    @Override
    public int compareTo(Object o) {
        DLY other = (DLY)o;
        if (other == null) {
            return -1;
        }
        if (this.header.num > other.header.num) return 1;
        else if (this.header.num < other.header.num) return -1;
        return 0;
    }
}
