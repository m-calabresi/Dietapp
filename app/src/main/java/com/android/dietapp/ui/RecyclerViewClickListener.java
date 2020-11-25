package com.android.dietapp.ui;

/**
 * A simple interface allowing an entity to be notified when a {@code RecyclerView} item is clicked.
 * <p>
 * Although being an interface, the implementer can choose to override only one of the methods provided
 * as to give more freedom in which type of information is needed by the notified entity.
 */
public interface RecyclerViewClickListener {
    /**
     * Notifies the callee that an item in the {@code RecyclerView} has been clicked. This item
     * is located in the {@code RecyclerView} in {@code checkedItemPosition} position.
     *
     * @param clickedItemPosition the position inside the {@code RecyclerView}, where the clicked
     *                            element is located.
     */
    default void recyclerViewItemClicked(int clickedItemPosition) {
    }

    /**
     * Notifies the callee that an item in the {@code RecyclerView} has been clicked.
     */
    default void recyclerViewItemClicked() {
    }
}
