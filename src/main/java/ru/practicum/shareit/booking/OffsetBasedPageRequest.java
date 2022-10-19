package ru.practicum.shareit.booking;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Optional;


public class OffsetBasedPageRequest implements Pageable, Serializable {
    private int limit;
    private int offset;
    private final Sort sort;

    public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetBasedPageRequest(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    @Override
    public boolean isPaged() {
        return Pageable.super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return Pageable.super.isUnpaged();
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }

    @Override
    public @NonNull Sort getSortOr(@NonNull Sort sort) {
        return Pageable.super.getSortOr(sort);
    }

    @Override
    public @NonNull Pageable next() {
        return new OffsetBasedPageRequest((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public OffsetBasedPageRequest previous() {
        return hasPrevious() ? new OffsetBasedPageRequest((int) (getOffset() - getPageSize()), getPageSize(), getSort()) : this;
    }


    @Override
    public @NonNull Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public @NonNull Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public @NonNull Pageable withPage(int pageNumber) {
        return next();
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public @NonNull Optional<Pageable> toOptional() {
        return Pageable.super.toOptional();
    }
}