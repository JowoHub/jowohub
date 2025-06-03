package c.plus.plan.common.entity;

/**
 * Created by fanwei on 1/26/22
 */
public class PageResult<T> {
    private T content;
    private int cursorId;
    private boolean last;
    private int totalCount;

    public T getContent() {
        return content;
    }

    public int getCursorId() {
        return cursorId;
    }

    public boolean isLast() {
        return last;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
