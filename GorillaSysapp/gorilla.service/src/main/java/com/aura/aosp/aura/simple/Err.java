package com.aura.aosp.aura.simple;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings({"WeakerAccess", "SameReturnValue", "CanBeFinal", "unused"})
public class Err
{
    //region Static implementation.

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
        return new Err(ex, ex.getMessage());
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
        Log.e(err.err);
        return err;
    }

    @NonNull
    public static Err errp(String error)
    {
        Err err = err(error);
        Log.e(err.err);
        return err;
    }

    @NonNull
    public static Err errp(String format, Object... args)
    {
        Err err = err(format, args);
        Log.e(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex, String error)
    {
        Err err = err(ex, error);
        Log.e(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex)
    {
        Err err = err(ex);
        Log.e(err.err);
        return err;
    }

    @NonNull
    public static Err errp(Exception ex, String format, Object... args)
    {
        Err err = err(ex, format, args);
        Log.e(err.err);
        return err;
    }

    //endregion Static implementation.

    //region Instance implementation.

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

    //endregion Instance implementation.
}
