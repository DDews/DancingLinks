import java.util.ArrayList;

public class DLX {
    protected DLY data;
    protected DLX left;
    protected DLX right;
    protected int size = 0;
    protected final int num;
    protected boolean alive = true;
    public DLX(int num) {
        left = this;
        right = this;
        this.num = num;
    }
    public DLX(int num, DLY data) {
        this.data = data;
        this.left = this;
        this.right = this;
        this.num = num;
    }
    public DLX(int num, DLY data, DLX left, DLX right) {
        this.data = data;
        this.left = left;
        this.right = right;
        this.num = num;
    }
    public void add(DLX other) {
        this.right = other;
        other.left = this;
    }
    public void add(DLY other) {
        if (data == null) data = other;
        data.addBottom(other);
        size++;
    }
    public void ghost() {
        if (!alive) throw new IllegalStateException(this + " is already dead!");
        alive = false;
        this.left.right = this.right;
        this.right.left = this.left;
       /* DLY current = data;
        ArrayList<DLY> out = new ArrayList<DLY>();
        do {
            DLY next = current.nextDown();
            current.ghost();
            out.add(current);
            current = next;
        } while (current != null && current != data);
        return out;*/
    }
    public ArrayList<DLY> ghost_rows() {
        this.left.right = this.right;
        this.right.left = this.left;
        DLY current = data.next();
        data.ghost();
        ArrayList<DLY> out = new ArrayList<DLY>();
        if (current != null) {
            do {
                DLY next = current.next();
                DLY row = current;
                if (row != null) {
                    do {
                        DLY next_right = row.next();
                        row.ghost();
                        out.add(row);
                        row = next_right;
                    } while (row != null && row != current);
                }
                current = next;
            } while (current != null && current != data);
        }
        return out;
    }
    public void resurrect() {
        /*if (num == 18) {
            System.err.println("ressurected!");
            new Throwable().printStackTrace();
        }*/
        if (alive) throw new IllegalStateException(this + " is already alive!");
        alive = true;
        this.right.left = this;
        this.left.right = this;
    }
    public void resurrect(ArrayList<DLY> in) {
        this.right = this;
        this.left = this;
        for (DLY node : in) {
            node.resurrect();
        }
    }
    public DLX next() {
        if (this.right == this) return null;
        return this.right;
    }
    public DLX prev() {
        if (this.left == this) return null;
        return this.left;
    }
    public boolean empty() {
        return this.data.bottom.top != this.data;
    }
    public boolean ded() {
        return this.left.right != this && this.right.left != this;
    }
    public void setData(DLY data) {
        this.data = data;
    }
    @Override
    public int hashCode() {
        return data.hashCode();
    }
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("(");
        out.append(this.num);
        out.append(",");
        if (data != null) {
            int max = 72;
            DLY first = data;
            DLY current = data;
            out.append(data.getList());
            do {
                current = current.nextDown();
                if (current != null) {
                    out.append(", ");
                    out.append(current.getList());
                }
                max--;
            } while (current != null && max >= 0 && current != first);
            out.append(data.getList());
        }
        else out.append("null");
        out.append(")");
        return out.toString();
    }
}
