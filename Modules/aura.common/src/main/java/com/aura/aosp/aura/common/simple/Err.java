/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;

/**
 * Exception safe, annotated and simplified
 * error methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "SameReturnValue", "CanBeFinal", "unused"})
public class Err
{
    //region Static stuff.

    /**
     * Last generated error.
     */
    private static Err lastErr = new Err("no error");

    /**
     * Return last generated error.
     */
    @NonNull
    public static Err getLastErr()
    {
        return lastErr;
    }

    /**
     * Create a null pointer error.
     *
     * @return error.
     */
    @NonNull
    public static Err err()
    {
        return new Err(new NullPointerException(), "null pointer!");
    }

    /**
     * Create a specific error.
     *
     * @param error error text.
     * @return error.
     */
    @NonNull
    public static Err err(String error)
    {
        return new Err(error);
    }

    /**
     * Create a formatted error.
     *
     * @param format string format.
     * @param args   arguments for string format.
     * @return error.
     */
    @NonNull
    public static Err err(String format, Object... args)
    {
        return new Err(String.format(format, args));
    }

    /**
     * Create an exception error.
     *
     * @param ex input exception.
     * @return error.
     */
    @NonNull
    public static Err err(Exception ex)
    {
        return new Err(ex, ex.toString());
    }

    /**
     * Create an exception error with specific text.
     *
     * @param ex    input exception.
     * @param error error text.
     * @return error.
     */
    @NonNull
    public static Err err(Exception ex, String error)
    {
        return new Err(ex, error);
    }

    /**
     * Create an exception error with formatted text.
     *
     * @param ex     input exception.
     * @param format string format.
     * @param args   arguments for string format.
     * @return error.
     */
    @NonNull
    public static Err err(Exception ex, String format, Object... args)
    {
        return new Err(ex, String.format(format, args));
    }

    /**
     * Create a null pointer error and print.
     *
     * @return error.
     */
    @NonNull
    public static Err errp()
    {
        Err err = err();
        Log.eerr(err.err);
        return err;
    }

    /**
     * Create a specific error and print.
     *
     * @param error error text.
     * @return error.
     */
    @NonNull
    public static Err errp(String error)
    {
        Err err = err(error);
        Log.eerr(err.err);
        return err;
    }

    /**
     * Create a formatted error and print.
     *
     * @param format string format.
     * @param args   arguments for string format.
     * @return error.
     */
    @NonNull
    public static Err errp(String format, Object... args)
    {
        Err err = err(format, args);
        Log.eerr(err.err);
        return err;
    }

    /**
     * Create an exception error.
     *
     * @param ex input exception.
     * @return error.
     */
    @NonNull
    public static Err errp(Exception ex)
    {
        Err err = err(ex);
        Log.eerr(err.err);
        return err;
    }

    /**
     * Create an exception error with specific text and print.
     *
     * @param ex    input exception.
     * @param error error text.
     * @return error.
     */
    @NonNull
    public static Err errp(Exception ex, String error)
    {
        Err err = err(ex, error);
        Log.eerr(err.err);
        return err;
    }

    /**
     * Create an exception error with formatted text and print.
     *
     * @param ex     input exception.
     * @param format string format.
     * @param args   arguments for string format.
     * @return error.
     */
    @NonNull
    public static Err errp(Exception ex, String format, Object... args)
    {
        Err err = err(ex, format, args);
        Log.eerr(err.err);
        return err;
    }

    /**
     * Deliberately print a stack trace.
     */
    public static void printStack()
    {
        new Exception().printStackTrace();
    }

    //endregion Static stuff.

    //region Instance stuff.

    /**
     * The formatted error text.
     */
    public String err;

    /**
     * The exception if available.
     */
    public Exception ex;

    /**
     * Create a new error from text message.
     *
     * @param err error text.
     */
    private Err(String err)
    {
        this.err = err;

        lastErr = this;
    }

    /**
     * Create a new error from exception and text message.
     *
     * @param ex  exception.
     * @param err error text.
     */
    private Err(Exception ex, String err)
    {
        this.ex = ex;
        this.err = err;

        lastErr = this;
    }

    /**
     * Convert error to string.
     *
     * @return error string.
     */
    @Override
    public String toString()
    {
        return err;
    }

    //endregion Instance stuff.
}
