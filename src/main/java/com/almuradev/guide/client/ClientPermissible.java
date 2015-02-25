package com.almuradev.guide.client;

import com.almuradev.almurasdk.permissions.Permissible;
import com.almuradev.almurasdk.permissions.PermissionsManager;
import com.almuradev.guide.Guide;

public class ClientPermissible implements Permissible {
    @Override
    public String getPermissibleModName() {
        return Guide.MOD_ID;
    }

    @Override
    public float getPermissibleModVersion() {
        return 1;
    }

    @Override
    public void registerPermissions(PermissionsManager permissionsManager) {
        permissionsManager.registerModPermission(this, "open");
    }

    @Override
    public void onPermissionsCleared(PermissionsManager manager) {

    }

    @Override
    public void onPermissionsChanged(PermissionsManager manager) {
        // TODO Re-populate Guide list if it is currently open
    }
}
