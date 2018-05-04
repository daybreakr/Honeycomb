package com.honeycomb.nav;

import android.content.Intent;
import android.os.Bundle;

public class NavItem {

    /**
     * Category in which the item should be placed.
     */
    public String category;

    /**
     * Used to control display order.
     */
    public int order;

    /**
     * The metaData from the activity that defines this item.
     */
    public Bundle metaData;

    /**
     * Optional intent to launch when the item is selected.
     */
    public Intent intent;

    /**
     * Title of the item that is shown to the user.
     */
    public CharSequence title;

    /**
     * Optional icon resource ID to show for this item.
     */
    public int iconRes;

    /**
     * Whether the icon can be tinted. This should be set to true for monochrome (single-color)
     * icons that can be tinted to match the design.
     */
    public boolean isIconTintable;
}
