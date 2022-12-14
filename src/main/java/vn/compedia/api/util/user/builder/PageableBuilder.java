package vn.compedia.api.util.user.builder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.compedia.api.util.user.SortOrder;
import vn.compedia.api.util.user.filter.BaseFilter;

@Component
public class PageableBuilder {
    public static final String SUM = "sum";
    public static final String VALUE = "value";

    public Pageable build(BaseFilter filter) {
        int pageNumber = filter.getPageNumber() - 1;
        int pageSize = filter.getPageSize();
        //in dto at front this field calls sum
        String sortBy = filter.getSortBy();
        String sortField = sortBy != null && sortBy.equals(SUM) ? VALUE : sortBy;
        Pageable pageable;
        SortOrder sortOrder = filter.getOrderBy() == null ? SortOrder.EMPTY
                : SortOrder.valueOfIgnoreCase(filter.getOrderBy());
        switch (sortOrder) {
            case ASC:
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortField));
                break;
            case DESC:
                pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortField).descending());
                break;
            case EMPTY:
                pageable = PageRequest.of(pageNumber, pageSize);
                break;
            default:
                throw new IllegalArgumentException("Wrong sort order");
        }
        return pageable;
    }
}
