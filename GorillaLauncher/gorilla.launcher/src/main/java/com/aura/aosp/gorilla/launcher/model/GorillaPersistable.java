package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.gorilla.atoms.GorillaAtom;

interface GorillaPersistable {

    public GorillaAtom persist();

    public void readFromGorillaAtom(GorillaAtom atom);
}
