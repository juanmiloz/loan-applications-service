package co.com.pragma.model.shared.pagination;

public record PageQuery( int page, int size) {

    public static PageQuery of(int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, Math.min(size, 200));
        return new PageQuery(p, s);
    }

}