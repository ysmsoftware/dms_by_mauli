package com.dms.app.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateFolder {
	
	public static void newFolder(String path, String folderName) {
        String specificPath = path+folderName; 
        try {
            Path path1 = Paths.get(specificPath);

            if (!Files.exists(path1) && !Files.isDirectory(path1)) {
            	 Files.createDirectories(path1);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}