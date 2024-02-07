package me.ringofsoul.ultralightoverlay.utils;

import com.labymedia.ultralight.UltralightJava;
import com.labymedia.ultralight.UltralightLoadException;
import com.labymedia.ultralight.gpu.UltralightGPUDriverNativeUtil;

import java.io.*;
import java.net.URISyntaxException;

public class ResourceManager {
    private ResourceManager(){}

    public static final File libDir = new File(".", "libraries");
    public static final File ultraLightDir = new File(libDir, "ultralight");
    public static final File binDir = new File(ultraLightDir, "bin");
    //public static final File resourceDir = new File(ultraLightDir, "resources");

    public static void loadUltralight() throws URISyntaxException, UltralightLoadException, IOException {
        UltralightJava.extractNativeLibrary(binDir.toPath());
        UltralightGPUDriverNativeUtil.extractNativeLibrary(binDir.toPath());

        System.load(new File(binDir.toPath().toFile(), "UltralightCore.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "glib-2.0-0.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "gobject-2.0-0.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "gmodule-2.0-0.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "gio-2.0-0.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "gstreamer-full-1.0.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "WebCore.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "Ultralight.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "ultralight-java-gpu.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "AppCore.dll").toPath().toAbsolutePath().toString());
        System.load(new File(binDir.toPath().toFile(), "ultralight-java.dll").toPath().toAbsolutePath().toString());
    }

}