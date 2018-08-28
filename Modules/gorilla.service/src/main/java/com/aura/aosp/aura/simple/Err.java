package com.aura.aosp.aura.simple;

import android.support.annotation.NonNull;

@SuppressWarnings({"WeakerAccess", "SameReturnValue", "CanBeFinal", "unused"})
public class Err
{
    //region Static stuff.

    private static Err lastErr;

    @NonNull
    public static Err getLastErr()
    {
        return lastErr;
    }

    @NonNull
    public static Err err()
    {
        return new Err("null pointer!");
    }

    @NonNull
    public static Err err(String error)
    {
        return new Err(error);
    }

    @NonNull
    public static Err err(String format, Object... args)
    {
        return new Err(String.format(format, args));
    }

    @NonNull
    public static Err err(Exception ex)
    {
        return new Err(ex, ex.toString());
    }

    @NonNull
    public static Err err(Exception ex, String error)
    {
        return new Err(ex, error);
    }

    @NonNull
    public static Err err(Exception ex, String format, Object... args)
    {
        return new Err(ex, String.format(format, args));
    }

    @NonNull
    public static Err errp()
    {
        Err err = err();
        Log.eerr(err.err);
        return err;
    }

    @NonNull
    public static Err errp(String error)
    {
        Err err = err(error);
        Log.eerr(err.err);
        return err;
    }

    @NonNull
    public static Err errp(String format, Object... args)
    {
        Err err = err(format, args);
        Log.eerr(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex, String error)
    {
        Err err = err(ex, error);
        Log.eerr(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex)
    {
        Err err = err(ex);
        Log.eerr(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex, String format, Object... args)
    {
        Err err = err(ex, format, args);
        Log.eerr(err.err);
        return err;
    }

    //endregion Static stuff.

    //region Instance stuff.

    public String err;
    public Exception ex;

    private Err(String err)
    {
        this.err = err;

        lastErr = this;
    }

    private Err(Exception ex, String err)
    {
        this.ex = ex;
        this.err = err;

        lastErr = this;
    }

    public String toString()
    {
        return err;
    }

    //endregion Instance stuff.
}
