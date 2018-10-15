package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.univid.Identity;

import java.util.List;

interface GorillaSharable {

    public void shareWith(Identity remoteIdentity);

    public void shareWith(List<Identity> remoteIdentities);

    public void unshareWith(Identity remoteIdentity);
}
