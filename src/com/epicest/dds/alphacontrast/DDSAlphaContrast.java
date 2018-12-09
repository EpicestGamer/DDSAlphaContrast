/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epicest.dds.alphacontrast;

import ddsutil.DDSUtil;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jayar
 */
public class DDSAlphaContrast {

 public static boolean verbose = false;
 public static float newAlphaContrast = 1.5f;
 public static File[] ddsFiles;

 public static File editedImages = new File("Edited Images");

 /**
  * @param args the command line arguments
  */
 public static void main(String[] args) {
  boolean customContrast = false;
  if (args.length != 0) {
   boolean helped = false;
   boolean onlyHelped = true;
   for (int i = 0; i < args.length; i++) {
    boolean help = false;
    switch (args[i]) {
     case "-v":
      verbose = true;
      onlyHelped = false;
      print("Displaying verbose output.");
      break;
     case "-c":
      try {
       customContrast = true;
       i++;
       newAlphaContrast = Float.parseFloat(args[i]);
       System.out.println("Using " + (newAlphaContrast * 100) + "% contrast");
      } catch (Exception e) {
       System.err.println("Invalid contrast input, defaulting to 150% contrast");
      }
      onlyHelped = false;
      break;
     case "-h":
      help = true;
     default:
      if (!helped) {
       if (!help) {
        System.out.println("Invalid argument \"" + args[i] + "\"\nDisplaying help\n");
       }
       System.out.println(
               "DDS Alpha Contrast, a program made by EpicestGamer https://github.com/EpicestGamer\n"
               + "Uses the DDSUtils library, by Dahie https://github.com/Dahie/DDS-Utils\n"
               + "\n"
               + "Options:\n"
               + "[-h] -- Displays this help message\n"
               + "[-v] -- Verbose output, may slightly slow down the program, useful if you want to see progress\n"
               + "[-c (contrast factor)] -- Uses a custom contrast, default is 1.5, or 150%");
      }
      helped = true;
      break;
    }
   }
   if (helped && onlyHelped) {
    System.exit(0);
   }
  }
  if (!customContrast) {
   System.out.println("No contrast input, defaulting to 150% contrast");
  }

  System.out.println("Working directory = \"" + System.getProperty("user.dir") + "\"");
  ddsFiles = getFlattenedDDSFiles();
  System.out.println("Done flattening dds files");
  print("Flattened DDS files result:");
  if (verbose) {
   for (File ddsFile : ddsFiles) {
    System.out.println("    \"" + ddsFile.getName() + "\"");
   }
  }
  RescaleOp ro = new RescaleOp(newAlphaContrast, 0.0f, null);
  editedImages.mkdir();
  System.out.println("Editing dds files");
  int successfulFiles = 0;
  for (File ddsFile : ddsFiles) {
   print("Editing \"" + ddsFile.getName() + "\"");
   try {
    //Load Image
    print("    Loading \"" + ddsFile.getName() + "\"");
    BufferedImage image = DDSUtil.decompressTexture(ddsFile);
    print("    Rescaling \"" + ddsFile.getName() + "\"");
    //Adjust Alpha Contrast
    BufferedImage alphaImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    alphaImage.setData(image.getAlphaRaster());
    alphaImage = ro.filter(alphaImage, null);
    image.getAlphaRaster().setRect(alphaImage.getData());
    //Save Image
    print("    Saving \"" + ddsFile.getName() + "\"");
    File output = new File(
            ddsFile.getPath().replace(
                    (CharSequence) System.getProperty("user.dir"),
                    "Edited Images"));
    output.getParentFile().mkdirs();
    DDSUtil.write(output, image, DDSUtil.getCompressionType(ddsFile), false);
    successfulFiles++;
    print("Done editing \"" + ddsFile.getName() + "\"");
   } catch (IOException ex) {
    ex.printStackTrace(System.err);
    print("Could not edit \"" + ddsFile.getName() + "\"");
   }
  }
  System.out.println("Done, edited " + successfulFiles + "/" + ddsFiles.length + " dds images");
 }

 public static File[] getFlattenedDDSFiles() {
  DDSTree ddsFiles = getDDSFiles();
  System.out.println("Flattening dds files");
  return flattenRecurse(ddsFiles, "").toArray(new File[0]);
 }

 private static ArrayList<File> flattenRecurse(DDSTree ddsFiles, String space) {
  print(space + "Flattening " + ddsFiles.name);
  ArrayList<File> files = new ArrayList<File>();
  for (File file : ddsFiles.ddsFiles) {
   files.add(file);
  }
  for (DDSTree directory : ddsFiles.children) {
   files.addAll(flattenRecurse(directory, space + "    "));
  }
  return files;
 }

 public static DDSTree getDDSFiles() {
  System.out.println("Scanning working directory for dds files");
  DDSTree ddsFiles = ddsRecurse(new File(System.getProperty("user.dir")), "");
  System.out.println("Done scanning for dds files");
  return ddsFiles;
 }

 private static DDSTree ddsRecurse(File curDirectory, String space) {
  DDSTree ddsTree = new DDSTree();
  ddsTree.name = curDirectory.getPath();
  print(space + "Scanning " + ddsTree.name);
  ArrayList<DDSTree> directories = new ArrayList<DDSTree>();
  ArrayList<File> ddsFileStuff = new ArrayList<File>();
  for (File file : curDirectory.listFiles()) {
   if (file.isDirectory() && !file.getAbsolutePath().equals(editedImages.getAbsolutePath())) {
    print(space + "    Found directory \"" + file.getName() + "\"");
    directories.add(ddsRecurse(file, space + "    "));
   } else if (file.getName().endsWith(".dds")) {
    print(space + "    Found dds image \"" + file.getName() + "\"");
    ddsFileStuff.add(file);
   }
  }
  ddsTree.children = directories.toArray(new DDSTree[0]);
  ddsTree.ddsFiles = ddsFileStuff.toArray(new File[0]);
  return ddsTree;
 }

 public static void print(Object obj) {
  if (verbose) {
   System.out.println(obj);
  }
 }
}

class DDSTree {

 public String name;

 public DDSTree[] children;

 public File[] ddsFiles;

}
