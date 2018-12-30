package nl.uscki.appcki.android.generated;

public class SingleValueWilsonItem<T> implements IWilsonBaseItem {

    private T value;
    private int id;

    public SingleValueWilsonItem(T value, int id) {
        this.value = value;
        this.id = id;
    }

    public T getValue() {return value;}
    public void setValue(T value) {this.value = value;}

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}
}
