package com.github.audio.api.Interface;

import com.github.audio.api.JarPackage;

import java.io.File;
import java.io.IOException;

public class Demo {

    public static void main(String[] args) throws IOException {
        JarPackage jarPackage = new JarPackage("TestJar.zip");
        File sourceFile = new File("D:\\Desktop\\audio\\src\\main\\resources\\data");
        jarPackage.copy(sourceFile , "NewFile");
        jarPackage.flush();

    }
}
