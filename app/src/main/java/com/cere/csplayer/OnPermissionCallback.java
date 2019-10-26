package com.cere.csplayer;

import com.hjq.permissions.OnPermission;

import java.util.List;

/**
 * Created by CheRevir on 2019/10/26
 */
public abstract class OnPermissionCallback implements OnPermission {
    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        hasPermission(isAll);
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {

    }

    public abstract void hasPermission(boolean isAll);
}
