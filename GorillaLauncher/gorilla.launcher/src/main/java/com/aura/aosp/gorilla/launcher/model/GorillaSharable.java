package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.gorilla.launcher.model.user.User;

public interface GorillaSharable {

    public void shareWith(User remoteUser);

    public void unshareWith(User remoteUser);
}
