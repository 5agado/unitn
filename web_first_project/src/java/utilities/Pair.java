package utilities;

public class Pair<L,R> {
    private L first;
    private R second;
    
    public Pair() {}
    
    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }

    public L getFirst() {
        return first;
    }

    public void setFirst(L first) {
        this.first = first;
    }

    public R getSecond() {
        return second;
    }

    public void setSecond(R second) {
        this.second = second;
    }
    
    
    
}
